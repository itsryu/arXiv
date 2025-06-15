package br.com.arxiv.client;

import br.com.arxiv.common.model.SearchRequest;
import br.com.arxiv.common.model.SearchResponse;
import br.com.arxiv.common.utils.JsonUtils;
import java.io.*;
import java.net.Socket;

public class SearchClient {
    private final String serverHost;
    private final int serverPort;

    public SearchClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public SearchResponse search(String query) {
        try (Socket socket = new Socket(serverHost, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            SearchRequest request = new SearchRequest(query);
            out.println(JsonUtils.toJson(request));

            String responseJson = in.readLine();
            return JsonUtils.fromJson(responseJson, SearchResponse.class);
        } catch (IOException e) {
            System.err.println("Erro de comunicação com o servidor: " + e.getMessage());
            return null;
        }
    }
}