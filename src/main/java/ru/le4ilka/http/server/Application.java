package ru.le4ilka.http.server;

public class Application {

    public static void main(String[] args) {
        new HttpServer(Integer.parseInt(args[0])).start();
    }
}
