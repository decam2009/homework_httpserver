package org.example;

public class Main {

    private static final int PORT = 9090;

    public static void main(String[] args) {
        Server server = new Server();

        server.listen(PORT);
    }
}