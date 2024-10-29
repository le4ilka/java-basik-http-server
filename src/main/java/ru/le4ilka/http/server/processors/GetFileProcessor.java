package ru.le4ilka.http.server.processors;

import ru.le4ilka.http.server.HttpRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GetFileProcessor implements RequestProcessor {

    public static StringBuilder printFileToScreen(String fileName) {
        StringBuilder str = new StringBuilder();
        try (InputStreamReader in = new InputStreamReader(new BufferedInputStream(new FileInputStream(fileName)))) {
            int n = in.read();
            while (n != -1) {
                str.append((char) n);
                System.out.print((char) n);
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
        File dir = new File("./");
        File[] fileList = dir.listFiles();
        for (File file : fileList) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
        }
        System.out.println("Укажите имя файла, который требуется вывести на экран:");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        System.out.println("Вы выбрали " + fileName + " файл содержит:");

        //printFileToScreen(fileName);

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
