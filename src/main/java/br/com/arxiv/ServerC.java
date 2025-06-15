package br.com.arxiv;

import br.com.arxiv.server.WorkerServer;
import br.com.arxiv.server.search.FileSearchService;

public class ServerC {
    public static void main(String[] args) {
        new WorkerServer(8083, new FileSearchService("data/dados_servidor_c.json")).start();
    }
}