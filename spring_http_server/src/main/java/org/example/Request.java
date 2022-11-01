package org.example;

public class Request {
    private String method;
    private String pathRequest;
    private String body;

    public Request(String method, String path, String body) {
        this.method = method;
        this.pathRequest = path;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPathRequest() {
        return pathRequest;
    }

    public void setPathRequest(String pathRequest) {
        this.pathRequest = pathRequest;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Запрос: [ метод - " + method + ", путь запроса - " + pathRequest + ",тело запроса - " + body + "]";
    }
}
