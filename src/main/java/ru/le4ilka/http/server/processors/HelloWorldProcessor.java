package ru.le4ilka.http.server.processors;

import ru.le4ilka.http.server.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class HelloWorldProcessor implements RequestProcessor {

    Set<String> possibleUrls;

    public HelloWorldProcessor(Set<String> possibleUrls) {
        this.possibleUrls = possibleUrls;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        StringBuilder links = new StringBuilder();
        for (String link : possibleUrls) {
            links.append("<a href = http://localhost:");
            links.append(request.getPort());
            links.append(link);
            links.append("> http://localhost:");
            links.append(request.getPort());
            links.append(link);
            links.append("</a><br>");
        }

        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h1>Hello World!!!</h1><table><tr><td></td><td>" + links + "</td></tr></table></body></html>";
        output.write(response.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }
}
