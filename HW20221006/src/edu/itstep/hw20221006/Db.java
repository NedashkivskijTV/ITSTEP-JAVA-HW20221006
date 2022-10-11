package edu.itstep.hw20221006;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Db {
    private final static String URL = "jdbc:postgresql://localhost/cinema";
    private final static String USER_DB = "postgres";
    private final static String PASSWORD = "7777";

    public static void createPlacesTable() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(URL, USER_DB, PASSWORD);
            statement = connection.createStatement();
            statement.execute("CREATE TABLE if not exists places(id BIGSERIAL PRIMARY KEY NOT NULL, num VARCHAR(25) NOT NULL, phone VARCHAR(25) NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void insertPlace(Place place) {
        try (Connection connection = DriverManager.getConnection(URL, USER_DB, PASSWORD);
             Statement statement = connection.createStatement()) {
            //statement.execute("INSERT INTO ");
            String sql = String.format("INSERT INTO places(num, phone) VALUES('%s','%s')",
                    place.getNum(),
                    place.getPhone());
            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Place> getAllPlaces() {
        List<Place> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER_DB, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM places")) {
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String num = resultSet.getString("num");
                String phone = resultSet.getString("phone");
                users.add(new Place(id, num, phone));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void deletePlaceById(long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER_DB, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE from places WHERE id = " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteOllPlaces(int startPlace, int endPlace) {
        try (Connection connection = DriverManager.getConnection(URL, USER_DB, PASSWORD);
             Statement statement = connection.createStatement()) {
            for (int i = startPlace; i <= endPlace; i++) {
                //System.out.println("i = " + i);
                statement.execute("DELETE from places WHERE num = '" + i + "'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlaces(Place place) {
        try (Connection connection = DriverManager.getConnection(URL, USER_DB, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute("UPDATE places SET num = '" + place.getNum() + "', phone = '" + place.getPhone() + "' WHERE id = " + place.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
