package br.com.puc;

import br.com.puc.server.WorkerServer;
import br.com.puc.server.search.FileSearchService;

public class ServerB {
    public static void main(String[] args) {
        new WorkerServer(8082, new FileSearchService("data/dados_servidor_b.json")).start();
    }
}