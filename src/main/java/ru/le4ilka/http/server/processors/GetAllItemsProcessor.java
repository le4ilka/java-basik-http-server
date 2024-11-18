package ru.le4ilka.http.server.processors;

import com.google.gson.Gson;
import ru.le4ilka.http.server.BadRequestException;
import ru.le4ilka.http.server.DatabaseProvaider;
import ru.le4ilka.http.server.HttpRequest;
import ru.le4ilka.http.server.app.Item;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetAllItemsProcessor implements RequestProcessor {
    DatabaseProvaider databaseProvaider;

    public GetAllItemsProcessor(DatabaseProvaider databaseProvaider) {
        this.databaseProvaider = databaseProvaider;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        if (request.containsParameter("id")) {

            if (request.getParameter("id") == null) {
                throw new BadRequestException("Parameter 'id' is missing");
            }
            String id = request.getParameter("id");
            Item item = databaseProvaider.getItem(Long.valueOf(id));
            if (item.getId() == null && item.getTitle() == null) {
                throw new BadRequestException("No such item");
            }
            Gson gson = new Gson();
            String itemJson = gson.toJson(item);

            String response = "" +
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "\r\n" +
                    itemJson;
            output.write(response.getBytes(StandardCharsets.UTF_8));
            output.flush();
            output.close();
            return;
        }

        List<Item> items = databaseProvaider.getItems();
        Gson gson = new Gson();
        String itemsJson = gson.toJson(items);

        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                itemsJson;
        output.write(response.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }
}

