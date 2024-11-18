package ru.le4ilka.http.server.processors;

import ru.le4ilka.http.server.HttpRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PrintToScreenFileProcessor implements RequestProcessor {
    private String fileDir;
    public PrintToScreenFileProcessor(String fileDir){
        this.fileDir = fileDir;
    }
    public StringBuilder printFileToScreen(String fileName) {
        StringBuilder str = new StringBuilder();
        try (InputStreamReader in = new InputStreamReader(new BufferedInputStream(new FileInputStream(this.fileDir + fileName)))) {
            int n = in.read();
            while (n != -1) {
                str.append((char) n);
                //System.out.print((char) n);
                n = in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return str;
        }
        return str;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {

        String fileName = request.getUri().substring(1);
        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: */*\r\n" +
                "\r\n" +
                "" + printFileToScreen(fileName).toString() + "";
        output.write(response.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }
}
