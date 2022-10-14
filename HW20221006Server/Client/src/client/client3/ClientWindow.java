package client.client3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientWindow extends JFrame {
    private static final String SERVER_HOST = "localhost"; // адреса сервера

    private static final int SERVER_PORT = 3443; // порт

    private Socket clientSocket; // сокет клієнта

    private Scanner inMessage; // вхідне повідомлення

    private PrintWriter outMessage; // вихідне повідомлення

    private List<Place> places = new ArrayList<>(); // колекція зайнятих місць, отримана від сервера
    private List<Place> placesSelectedByClient = new ArrayList<>(); // колекція місць, обраних клієнтом

    private String messageToServer; // серіалізована колекція, підготовлена для відправлення на сервер

    // поля - елементи форми
    List<JButton> buttonList = new ArrayList<>(); // колекція кнопок - місць
    JButton jButtonOk; // кнопка передачі данних
    JTextField jTextField; // текстове поле
    String tempString = ""; // тимчасове збереження вмісту текстового поля при отриманні/втраті фокуса

    // конструктор
    public ClientWindow() {
        // підключення до сервера
        serverConnection();

        // налаштування елементів форми
        setFrame();
        formSettings();
        addListeners();

        // у окремому потоці відбувається робота з сервером
        serverThread();
    }

    // підключення до сервера
    public void serverConnection() {
        try {
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFrame() {
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Cinema");
    }

    public void formSettings() {
        // налаштування елементів форми
        JPanel jPanel = new JPanel(new BorderLayout(5, 5)); // основна панель

        JPanel jPanelForButtons = new JPanel(new GridLayout(3, 3)); // другорядна панель - панель для кнопок - місць
        JPanel jPanelForTextFieldAndSubmitButton = new JPanel(new GridLayout(1, 2)); // другорядна панель - панель для текстового поля та кнопки ОК

        // генерування кнопок-мість та їх додавання до масиву кнопок
        for (int i = 0; i < 9; i++) {
            buttonList.add(new JButton("" + (i + 1)));
        }

        // додавання кнопок-місць з масиву кнопок до панелі кнопок
        for (JButton jButton : buttonList) {
            jButton.setBackground(ColorRGB.GREY.getColor()); // встановлення дефолтного кольору

            // додавання до панелі
            jPanelForButtons.add(jButton);
        }

        // ініціалізація текстового поля та кнопки ОК, а також їх приєднання до панелі
        jButtonOk = new JButton("OK");
        //jButtonOk.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        jTextField = new JTextField("Enter phone number");
        jPanelForTextFieldAndSubmitButton.add(jTextField);
        jPanelForTextFieldAndSubmitButton.add(jButtonOk);

        // приєднання другорядних панелей до основної
        jPanel.add(jPanelForButtons, BorderLayout.CENTER);
        jPanel.add(jPanelForTextFieldAndSubmitButton, BorderLayout.SOUTH);

        // приєднання основної панелі до вікна, візуалізація
        add(jPanel);
        setVisible(true);
    }

    // додавання подій до об'єктів форми
    public void addListeners() {
        // подія -- натискання кнопки місця - зміна кольору сірий-жовтий-сірий
        for (JButton jButton : buttonList) {
            jButton.addActionListener(e -> {
                if (!Objects.equals(jButton.getBackground(), ColorRGB.RED.getColor()) &&
                        !Objects.equals(jButton.getBackground(), ColorRGB.GREEN.getColor())) {
                    if (Objects.equals(jButton.getBackground(), ColorRGB.YELLOW.getColor())) {
                        jButton.setBackground(ColorRGB.GREY.getColor());
                    } else {
                        jButton.setBackground(ColorRGB.YELLOW.getColor());
                    }
                    //System.out.println(jButton.getBackground().toString());
                }
            });
        }

        // подія - натискання кнопки ОК
        jButtonOk.addActionListener(e -> {
            if (checkTextField()) {
                List<JButton> buttonsPressed = buttonList
                        .stream()
                        .filter(b -> Objects.equals(b.getBackground(), ColorRGB.YELLOW.getColor()))
                        .collect(Collectors.toList());
                for (JButton jButton : buttonsPressed) {
                    Place p = new Place(jButton.getText(), jTextField.getText());
                    //Db.insertPlace(p1);
                    placesSelectedByClient.add(p); // додавання обраних місць до колекції
                    jButton.setBackground(ColorRGB.GREEN.getColor());
                }

                // відправлення повідомлення на сервер у разі, якщо натиснута принаймі 1 кнопка
                if (placesSelectedByClient.size() != 0) {

                    // серіалізація
                    messageToServer = ProcessorGson.serializeListToString(placesSelectedByClient);
                    placesSelectedByClient.clear(); // очищення колекції обраних клієнтом кнопок

                    // відправлення на сервер
                    sendMsg();
                }
                //------------------------------------------------------------------------------------------------------
                jTextField.setText("Enter phone number");
            }
        });

        // подія - якщо текстове поле знаходиться у фокусі -
        // текст очищується/залишається введений рніше рядок,
        // що зберігається у змінній екземпляра tempString
        jTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                //super.focusGained(e);
                jTextField.setText(tempString);
            }
        });

        // подія - втрата фокуса на текстовому полі -
        // збереження тексту, що до цього введений у текстовому полі
        // до змінної екземпляра tempString
        jTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                //super.focusLost(e);
                tempString = jTextField.getText();
            }
        });
    }

    // перевірка коректності тексту у текстовому полі
    public boolean checkTextField() {
        String text = jTextField.getText();
        if (text.equals("Enter phone number")) {
            return false;
        } else return !text.isEmpty();
    }

    // поток, що чекатиме на повідомлення від сервера
    public void serverThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // нескінченний цикл
                    while (true) {
                        // якщо є вхідне повідомлення
                        if (inMessage.hasNext()) {
                            // читаємо нове повідомлення
                            String inMes = inMessage.nextLine();
                            // тестове повідомлення в консоль клієнта - отримане від сервера повідомлення
                            System.out.println("Отримано повідомлення від сервера - ");
                            System.out.println(inMes);

                            // десеріалізувати повідомлення
                            places = ProcessorGson.deserializeStringToList(inMes);

                            // тестове повідомлення в консоль клієнта - отримана від сервера колекція
                            System.out.println("Отримана колекція - ");
                            places.forEach(System.out::println);

                            // оновити вигляд клієнтського вікна
                            loudOllPlaces(places);
                            //--------------------------------------------------------------------------------------
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // відправлення повідомлення
    public void sendMsg() {
        // відправлення повідомлення
        outMessage.println(messageToServer);
        outMessage.flush();
    }

    // завантаження інформації (фарбування кнопок), що міститься
    // у отриманому з сервера повідомленні (десеріалізований колекції)
    public void loudOllPlaces(List<Place> places) {
        for (JButton jButton : buttonList) {
            for (Place place : places) {
                paintTheButton(jButton, place);
            }
        }
    }

    // фарбування кнопок - при співпадінні номера кнопки форми та місця з колекції, отриманої від сервера,
    // а також при відсутності попереднього фарбування у зелений колір (фарбується по натисканню кнопки ОК)
    public void paintTheButton(JButton jButton, Place place) {
        if (jButton.getText().equals(place.getNum()) &&
                !Objects.equals(jButton.getBackground(), ColorRGB.GREEN.getColor())) {
            jButton.setBackground(ColorRGB.RED.getColor());
        }
    }

}
