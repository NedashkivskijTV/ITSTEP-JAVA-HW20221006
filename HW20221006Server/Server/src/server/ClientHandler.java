package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Server server; // екземпляр сервера

    private PrintWriter outMessage; // вихідне повідомлення

    private Scanner inMessage; // вхідне повідомлення

    // вузол та порт для відправки повідомлень
    private static final String HOST = "localhost";
    private static final int PORT = 3443;

    private Socket clientSocket = null; // сокет клієнта


    // конструктор обробника для клієнта, що приймає сокет клієнта та сервер
    public ClientHandler(Socket socket, Server server) {
        try {
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Перевизначення методу run(), що викликається при створенні нового потоку (new Thread(client).start();)
    @Override
    public void run() {
        try {
            while (true) {
                // отримання даних з БД - колекція
                List<Place> places = Db.getAllPlaces();

                // серіалізація колекції
                String jsonListPlaces = ProcessorGson.serializeListToString(places);

                // відправка рядка усім
                server.sendMessageToOllClients(jsonListPlaces);
                break;
            }

            while (true) {
                // якщо від клієнта прийшло повідомлення
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                        // тестове повідомлення - виведення повідомлення в консоль сервера
                        System.out.println("Отримано повідомлення від клієнта:");
                        System.out.println(clientMessage);

                        // десеріалізація повідомлення
                        List<Place> placesFromClient = ProcessorGson.deserializeStringToList(clientMessage);

                        // тестове повідомлення в консоль сервера
                        System.out.println("Десеріалізоване повідомлення від клієнта:");
                        placesFromClient.forEach(System.out::println);

                        // завантаження даних до БД
                        for (Place place : placesFromClient) {
                            Db.insertPlace(place);
                        }

                        // отримання повного вмісту БД в колекції (з урахування щойно вневених змін)
                        List<Place> places = Db.getAllPlaces();

                        // серіалізація колекції - (отримується рядок для відправлення клієнтам)
                        String jsonListPlaces = ProcessorGson.serializeListToString(places);
                        //----------------------------------------------------------------------------------------------

                        // відправлення повідомлення усім клієнтам
                        server.sendMessageToOllClients(jsonListPlaces);
                }
                // пауза в роботі потоку на 100 мс
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    // відправлення повідомлення
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // вихід клієнта з чату - не працює коли робота клієнта завершується не натисканням Х на вікні !!!
    public void close() {
        // видалення клієнта зі списку
        server.removeClient(this);
    }
}
