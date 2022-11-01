package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ExecutorService poolExecutor;
    public static final ConcurrentHashMap<String, HashMap<String, Handler>> handlers = new ConcurrentHashMap<>();

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

    public void addHandler(String method, String path, Handler handler) {
        handlers.put(method, new HashMap<>() {{
            put(path, handler);
        }});
    }
}
