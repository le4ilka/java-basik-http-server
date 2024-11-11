package ru.le4ilka.http.server;

import ru.le4ilka.http.server.app.ItemsRepository;
import ru.le4ilka.http.server.processors.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Dispatcher {
    private Map<String, RequestProcessor> processors;
    private Set<String> possibleUrls = new HashSet<>();
    private RequestProcessor defaultNotFoundProcessor;
    private RequestProcessor defaultInternalServerErrorProcessor;
    private RequestProcessor defaultBadRequestProcessor;
    private RequestProcessor defaultMethodNotAllowedProcessor;
    private ItemsRepository itemsRepository;
    private DatabaseProvaider databaseProvaider;
    private String fileDir = "./static/";

    public Dispatcher() {
        this.databaseProvaider = new DatabaseProvaider();
        //this.itemsRepository = new ItemsRepository();
        this.processors = new HashMap<>();
        this.processors.put("GET /", new HelloWorldProcessor());
        this.processors.put("GET /calculator", new CalculatorProcessor());
        this.processors.put("GET /items", new GetAllItemsProcessor(databaseProvaider));
        this.processors.put("POST /items", new CreateNewItemsProcessor(databaseProvaider));
        this.processors.put("DELETE /items", new DeleteItemProcessor(databaseProvaider));
        this.processors.put("PUT /items", new UpdateItemProcessor(databaseProvaider));
        putProcessorsForPrintToScreenFile();
        this.defaultNotFoundProcessor = new DefaultNotFoundProcessor();
        this.defaultInternalServerErrorProcessor = new DefaultInternalServerErrorProcessor();
        this.defaultBadRequestProcessor = new DefaultBadRequestProcessor();
        this.defaultMethodNotAllowedProcessor = new DefaultMethodNotAllowedProcessor();
        for (String url : processors.keySet()) {
            int startIndex = url.indexOf(' ');
            this.possibleUrls.add(url.substring(startIndex + 1));
        }
    }

    private void putProcessorsForPrintToScreenFile() {
        File dir = new File(fileDir);
        File[] fileList = dir.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isFile()) {
                this.processors.put("GET /" + file.getName(), new PrintToScreenFileProcessor(fileDir));
            }
        }
    }

    public void execute(HttpRequest request, OutputStream out) throws IOException {
        try {
            if (possibleUrls.contains(request.getUri()) && !(processors.containsKey(request.getRoutingKey()))) {
                defaultMethodNotAllowedProcessor.execute(request, out);
                return;
            }

            if (!processors.containsKey(request.getRoutingKey())) {
                defaultNotFoundProcessor.execute(request, out);
                return;
            }

            processors.get(request.getRoutingKey()).execute(request, out);
        } catch (BadRequestException e) {
            request.setException(e);
            defaultBadRequestProcessor.execute(request, out);
        } catch (Exception e) {
            e.printStackTrace();
            defaultInternalServerErrorProcessor.execute(request, out);
        }
    }
}