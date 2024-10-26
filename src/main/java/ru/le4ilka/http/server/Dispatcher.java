package ru.le4ilka.http.server;

import ru.le4ilka.http.server.app.ItemsRepository;
import ru.le4ilka.http.server.processors.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Dispatcher {
    private Map<String, RequestProcessor> processors;
    private Set<String> possibleUrls;
    private RequestProcessor defaultNotFoundProcessor;
    private RequestProcessor defaultInternalServerErrorProcessor;
    private RequestProcessor defaultBadRequestProcessor;
    private RequestProcessor defaultMethodNotAllowedProcessor;
    private ItemsRepository itemsRepository;
    private DatabaseProvaider databaseProvaider;

    public Dispatcher() {
        this.databaseProvaider = new DatabaseProvaider();
        this.itemsRepository = new ItemsRepository();
        this.processors = new HashMap<>();
        this.processors.put("GET /", new HelloWorldProcessor());
        this.processors.put("GET /calculator", new CalculatorProcessor());
        this.processors.put("GET /items", new GetAllItemsProcessor(databaseProvaider));
        this.processors.put("POST /items", new CreateNewItemsProcessor(databaseProvaider));
        this.possibleUrls = new HashSet<>();
        this.possibleUrls.add("POST /calculator");
        this.possibleUrls.add("POST /calculator/");
        this.possibleUrls.add("GET /calculator/");
        this.possibleUrls.add("GET /items/");
        this.defaultNotFoundProcessor = new DefaultNotFoundProcessor();
        this.defaultInternalServerErrorProcessor = new DefaultInternalServerErrorProcessor();
        this.defaultBadRequestProcessor = new DefaultBadRequestProcessor();
        this.defaultMethodNotAllowedProcessor = new DefaultMethodNotAllowedProcessor();
    }

    public void execute(HttpRequest request, OutputStream out) throws IOException {
        try {

            if (possibleUrls.contains(request.getRoutingKey())) {
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