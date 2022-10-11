package edu.itstep.hw20221006;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyFrame extends JFrame {

    List<JButton> buttonList = new ArrayList<>(); // колекція кнопок - місць
    JButton jButtonOk; // кнопка передачі данних
    JTextField jTextField; // текстове поле
    String tempString = ""; // тимчасове збереження вмісту текстового поля при отриманні/втраті фокуса

    public MyFrame() {
        Db.createPlacesTable();
        setFrame();
        init();
        addListeners();
    }

    public void setFrame() {
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Cinema");
    }

    public void init() {

        JPanel jPanel = new JPanel(new BorderLayout(5, 5)); // основна панель

        JPanel jPanelForButtons = new JPanel(new GridLayout(3, 3)); // другорядна панель - панель для кнопок - місць
        JPanel jPanelForTextFieldAndSubmitButton = new JPanel(new GridLayout(1, 2)); // другорядна панель - панель для текстового поля та кнопки ОК

        // генерування кнопок-мість та їх додавання до масиву кнопок
        for (int i = 0; i < 9; i++) {
            buttonList.add(new JButton("" + (i + 1)));
        }

        // додавання кновок-місць з масиву кнопок до панелі кнопок
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

        // отримання даних з БД
        downloadDB();

        // приєднання основної панелі до вікна, візуалізація
        add(jPanel);
        setVisible(true);
    }

    // подія - натискання кнопки - місця
    public void addListeners() {
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
                    Place p1 = new Place(jButton.getText(), jTextField.getText());
                    Db.insertPlace(p1);
                    jButton.setBackground(ColorRGB.GREEN.getColor());
                }

                // первинний варіант вирішення
//                for (JButton jButton : buttonList) {
//                    if (Objects.equals(jButton.getBackground(), ColorRGB.YELLOW.getColor())) {
//                        Place p1 = new Place(jButton.getText(), jTextField.getText());
//                        Db.insertPlace(p1);
//                        jButton.setBackground(ColorRGB.GREEN.getColor());
//                    }
//                }
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

    // первинне завантаження інф з БД
    public void downloadDB() {
        List<Place> places = Db.getAllPlaces();
        places.forEach(System.out::println); // тестове виведення в консоль отриманого з БД масиву місць

        for (JButton jButton : buttonList) {
            for (Place place : places) {
                if (jButton.getText().equals(place.getNum())) {
                    jButton.setBackground(ColorRGB.RED.getColor());
                }
            }
        }
    }

    // перевірка коректності тексту у текстовому полі
    public boolean checkTextField() {
        String text = jTextField.getText();
        if (text.equals("Enter phone number")) {
            return false;
        } else return !text.isEmpty();
    }

}

