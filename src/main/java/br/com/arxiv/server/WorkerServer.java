package br.com.arxiv.server;

import br.com.arxiv.common.model.SearchRequest;
import br.com.arxiv.common.model.SearchResponse;
import br.com.arxiv.common.utils.JsonUtils;
import br.com.arxiv.server.search.SearchService;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerServer {
    private final int port;
    private final SearchService searchService;
    private final ExecutorService threadPool;

    public WorkerServer(int port, SearchService searchService) {
        this.port = port;
        this.searchService = searchService;
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Servidor Worker escutando na porta %d...\n", port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> request(clientSocket));
            }
        } catch (IOException e) {
            System.err.printf("Erro ao iniciar o servidor na porta %d: %s\n", port, e.getMessage());
        }
    }

    private void request(Socket socket) {
        try (socket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String requestLine = reader.readLine();
            SearchRequest request = JsonUtils.fromJson(requestLine, SearchRequest.class);

            var results = searchService.search(request.query());

            SearchResponse response = new SearchResponse(results);
            writer.println(JsonUtils.toJson(response));

        } catch (IOException e) {
            System.err.println("Erro na comunicação com o cliente: " + e.getMessage());
        }
    }
}