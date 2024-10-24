package ru.le4ilka.http.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseProvaider {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/items";
    private static Connection connection;

    public DatabaseProvaider(){

        try {
            connection = DriverManager.getConnection(DATABASE_URL, "postgres", "111111");
        } catch (Exception e) {
            System.out.println("Что-то поймали");
            e.printStackTrace();
        }

        System.out.println("Сервис запущен: DB режим");
        System.out.println("Это список продуктов: ");
        this.getItems(GET_ITEMS_QUERY);
    }

    private static final String GET_ITEMS_QUERY = "select id, title, price from item";

    private void getItems(String query){
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.print(resultSet.getString(1) + " ");
                    System.out.print(resultSet.getString(2) + " ");
                    System.out.println(resultSet.getString(3));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void close() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

