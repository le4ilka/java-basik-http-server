package ru.le4ilka.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.le4ilka.http.server.app.Item;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProvaider implements AutoCloseable {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/items";
    private static Connection connection;
    private static final Logger LOGGER = LogManager.getLogger(DatabaseProvaider.class);

    public DatabaseProvaider() {

        try {
            connection = DriverManager.getConnection(DATABASE_URL, "postgres", "111111");
        } catch (Exception e) {
            LOGGER.info("Что-то поймали");
            e.printStackTrace();
        }

        LOGGER.info("Сервис запущен: DB режим");
        this.getItems();
       // this.insertItem("box12", BigDecimal.valueOf(100));
    }

    private static final String GET_ITEMS_QUERY = "select id, title, price from item";

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(GET_ITEMS_QUERY)) {
                while (resultSet.next()) {
                    Item item = new Item();
                    item.setId(resultSet.getLong(1));
                    item.setTitle(resultSet.getString(2));
                    item.setPrice(resultSet.getBigDecimal(3));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Список продуктов: {}", items.toString());
        return items;
    }


    private static final String GET_ITEM_QUERY = "select id, title, price from item where id = ?";

    public Item getItem(Long id) {
        Item item = new Item();
        try (PreparedStatement statement = connection.prepareStatement(GET_ITEM_QUERY)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    item.setId(resultSet.getLong(1));
                    item.setTitle(resultSet.getString(2));
                    item.setPrice(resultSet.getBigDecimal(3));
                    LOGGER.info("{} {} {}", resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }


    private static final String INSERT_ITEM_QUERY = "insert into item (title, price) values (?, ?)";

    public boolean insertItem(Item item) {
        String title = item.getTitle();
        BigDecimal price = item.getPrice();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_ITEM_QUERY)) {
            statement.setString(1, title);
            statement.setBigDecimal(2, price);
            statement.executeUpdate();
            LOGGER.info("Продукт {} - {} добавлен", title, price);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

