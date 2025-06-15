package br.com.arxiv;

import br.com.arxiv.server.WorkerServer;
import br.com.arxiv.server.search.FileSearchService;

public class ServerB {
    public static void main(String[] args) {
        new WorkerServer(8082, new FileSearchService("data/dados_servidor_b.json")).start();
    }
}