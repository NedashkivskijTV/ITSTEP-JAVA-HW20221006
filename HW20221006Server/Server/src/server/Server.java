package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    static final int PORT = 3443; // порт, що буде прослуховуватись сервером

    private ArrayList<ClientHandler> clients = new ArrayList<>(); // список клієнтів, які будуть підключатись до сервера

    Socket clientSocket = null; // сокет клієнта - поток, що підключатиметься до сервера по адресі(хосту) та порту

    ServerSocket serverSocket = null; // сокет сервера

    // конструктор сервера
    public Server() {
        // очищення БД (за бажанням користувача)
        clearDb();

        // створення серверного сокета по визначеному порту
        serverCreation();
    }

    // очищення БД (за бажанням користувача)
    public void clearDb(){
        System.out.println("Очищення бази даних");
        System.out.println("Для очищення наберіть YES та натисніть Enter");
        System.out.print("Інше - запуск сервера без очищення - ");
        Scanner scanner = new Scanner(System.in);
        String choiceLine = scanner.nextLine();
        if(choiceLine.equals("YES")){
            Db.deleteOllPlaces(1, 9);
            System.out.println("Базу данних очищено");
        }
        else{
            System.out.println("Очищення бази данних відхилено");
        }
    }

    // створення серверного сокета по визначеному порту
    public void serverCreation() {
        try {
            // створення серверного сокета по визначеному порту
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущено!");

            // запуск нескінченного циклу - очікуватиме підключення клієнта
            while (true) {
                // очікування підключень від сервера
                clientSocket = serverSocket.accept();

                // створення обробника для клієнта, що підключиться до сервера (у параметрах this - наш сервер)
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);

                // кожне підключення клієнта обробляється у новому потоці
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // процедура закриття (припинення роботи) сервера
                clientSocket.close();
                System.out.println("Сервер зупинено");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // відправлення повідомлення усім клієнтам
    public void sendMessageToOllClients(String message) {
        for (ClientHandler client : clients) {
            client.sendMsg(message);
        }
    }

    // видалення клієнта з колекції при його виході з чату
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

}
