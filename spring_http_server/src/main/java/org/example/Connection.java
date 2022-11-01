package org.example;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Connection implements Runnable {
    private final Socket client;
    private final BufferedReader in;
    private final BufferedOutputStream out;

    private static final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html",
            "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public Connection(ServerSocket serverSocket) throws IOException {
        this.client = serverSocket.accept();
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new BufferedOutputStream(client.getOutputStream());
    }

    @Override
    public void run() {
        try {
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                client.close();
            }

            Request request = new Request(parts[0], parts[1], parts[0].equals("GET") ? null : requestLine);

            if (request.getMethod().equals("GET")) {
                Server.handlers.get(request.getMethod()).get(request.getPathRequest()).handle(request, out);
            }

            if (request.getMethod().equals("POST")) {
                Server.handlers.get(request.getMethod()).get(request.getPathRequest()).handle(request, out);
            }

            final var path = parts[1];
            if (!validPaths.contains(path)) {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                client.close();
            }
            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);

            // special case for classic
            if (path.equals("/classic.html")) {
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
                client.close();
                return;
            }
            final var length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
            client.close();
        } catch (IOException e) {
            System.out.printf(" Exception %s \n", e.getMessage());
        }
    }
}
