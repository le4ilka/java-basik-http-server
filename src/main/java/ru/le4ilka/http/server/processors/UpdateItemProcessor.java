package ru.le4ilka.http.server.processors;

import com.google.gson.Gson;
import ru.le4ilka.http.server.BadRequestException;
import ru.le4ilka.http.server.DatabaseProvaider;
import ru.le4ilka.http.server.HttpRequest;
import ru.le4ilka.http.server.app.Item;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class UpdateItemProcessor implements RequestProcessor {
    private DatabaseProvaider databaseProvaider;

    public UpdateItemProcessor(DatabaseProvaider databaseProvaider) {
        this.databaseProvaider = databaseProvaider;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        Gson gson = new Gson();
        Item item = gson.fromJson(request.getBody(), Item.class);
        if (item.getId() == null){
            throw new BadRequestException("Parameter 'id' is missing");
        }
        if (!(databaseProvaider.getAllId().contains(item.getId()))){
            throw new BadRequestException("No such item");
        }
        if (item.getTitle() == null){
            databaseProvaider.updatePrice(item);
        }
        if (item.getPrice() == null){
            databaseProvaider.updateTitle(item);
        }
        if (item.getTitle() != null && item.getPrice() != null){
            databaseProvaider.updateItem(item);
        }

        String response = "" +
                "HTTP/1.1 201 Created\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                gson.toJson(item);
        output.write(response.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }
}

