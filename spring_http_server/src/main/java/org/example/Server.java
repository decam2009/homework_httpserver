package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    private final ExecutorService poolExecutor;

    public Server() {
        final int MAX_THREADS = 64;
        this.poolExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    }

    public void listen(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                poolExecutor.execute(new Connection(serverSocket));
            }
        } catch (IOException e) {
            System.out.printf("Exception %s", e.getMessage());
        } finally {
            poolExecutor.shutdownNow();
        }
    }
}
