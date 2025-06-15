package br.com.arxiv.server;

import br.com.arxiv.common.model.Article;
import br.com.arxiv.common.model.SearchRequest;
import br.com.arxiv.common.model.SearchResponse;
import br.com.arxiv.common.utils.JsonUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class ServerA {
    private final int port;
    private final ExecutorService clientHandlerPool;
    private final ExecutorService workerRequestPool;
    private final List<WorkerNode> workerNodes;

    private record WorkerNode(String host, int port) { }

    public ServerA(int port) {
        this.port = port;
        this.workerNodes = loadWorkerNodes();
        this.clientHandlerPool = Executors.newCachedThreadPool();
        this.workerRequestPool = Executors.newFixedThreadPool(Math.max(1, workerNodes.size()));
    }

    private List<WorkerNode> loadWorkerNodes() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("workers.properties")) {
            if (input == null) {
                System.err.println("Arquivo 'workers.properties' não encontrado. Nenhum worker será contatado.");
                return Collections.emptyList();
            }

            Properties prop = new Properties();
            prop.load(input);
            String workersProperty = prop.getProperty("workers");

            return Stream.of(workersProperty.split(","))
                    .map(String::trim)
                    .map(workerString -> {
                        String[] parts = workerString.split(":");
                        return new WorkerNode(parts[0], Integer.parseInt(parts[1]));
                    })
                    .toList();

        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar ou processar 'workers.properties': " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Servidor A (Principal) escutando na porta %d...\n", port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientHandlerPool.submit(() -> clientRequest(clientSocket));
            }
        } catch (IOException e) {
            System.err.printf("Erro ao iniciar o Servidor A: %s\n", e.getMessage());
        }
    }

    private void clientRequest(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
             clientSocket) {

            String requestLine = reader.readLine();
            SearchRequest request = JsonUtils.fromJson(requestLine, SearchRequest.class);
            System.out.printf("Recebida busca por: '%s'\n", request.query());

            List<Future<SearchResponse>> futures = this.workerNodes.stream()
                    .map(node -> workerRequestPool.submit(() -> forwardToWorker(node.host(), node.port(), request)))
                    .toList();

            List<Article> allResults = futures.stream()
                    .flatMap(future -> {
                        try {
                            return future.get().results().stream();
                        } catch (InterruptedException | ExecutionException e) {
                            System.err.println("Falha ao obter resultado de um worker: " + e.getMessage());
                            return Stream.empty();
                        }
                    }).toList();

            writer.println(JsonUtils.toJson(new SearchResponse(allResults)));
            System.out.printf("Busca por '%s' finalizada. %d resultados enviados.\n", request.query(), allResults.size());

        } catch (IOException e) {
            System.err.println("Falha ao processar requisição do cliente: " + e.getMessage());
        }
    }

    private SearchResponse forwardToWorker(String host, int workerPort, SearchRequest request) {
        try (Socket socket = new Socket(host, workerPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(JsonUtils.toJson(request));
            String responseJson = in.readLine();
            return JsonUtils.fromJson(responseJson, SearchResponse.class);
        } catch (IOException e) {
            System.err.printf("Não foi possível conectar ao worker %s:%d. Motivo: %s\n", host, workerPort, e.getMessage());
            return new SearchResponse(new ArrayList<>());
        }
    }
}