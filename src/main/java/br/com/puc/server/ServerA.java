package br.com.puc.server;

import br.com.puc.common.model.Article;
import br.com.puc.common.model.SearchRequest;
import br.com.puc.common.model.SearchResponse;
import br.com.puc.common.utils.JsonUtils;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ServerA {

    private final int port;
    private final ExecutorService clientHandlerPool;
    private final ExecutorService workerRequestPool;

    public ServerA(int port) {
        this.port = port;
        this.clientHandlerPool = Executors.newCachedThreadPool();
        this.workerRequestPool = Executors.newFixedThreadPool(2);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Servidor A (Principal) escutando na porta %d...\n", port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientHandlerPool.submit(() -> handleClientRequest(clientSocket));
            }
        } catch (IOException e) {
            System.err.printf("Erro ao iniciar o Servidor A: %s\n", e.getMessage());
        }
    }

    private void handleClientRequest(Socket clientSocket) {
        try (clientSocket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String requestLine = reader.readLine();
            SearchRequest request = JsonUtils.fromJson(requestLine, SearchRequest.class);
            System.out.printf("Recebida busca por: '%s'\n", request.query());

            Future<SearchResponse> futureB = workerRequestPool.submit(() -> forwardToWorker("localhost", 8082, request));
            Future<SearchResponse> futureC = workerRequestPool.submit(() -> forwardToWorker("localhost", 8083, request));

            List<Article> allResults = new ArrayList<>();
            allResults.addAll(futureB.get().results());
            allResults.addAll(futureC.get().results());

            writer.println(JsonUtils.toJson(new SearchResponse(allResults)));
            System.out.printf("Busca por '%s' finalizada. %d resultados enviados.\n", request.query(), allResults.size());

        } catch (IOException | InterruptedException | ExecutionException e) {
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
            return new SearchResponse(new ArrayList<>()); // Tolerância a falhas
        }
    }
}