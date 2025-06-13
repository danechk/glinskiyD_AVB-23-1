package com.example.demo2;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ProceduralClockApp extends Application {

    private static LocalTime currentTime; // Текущее время
    private static LocalTime alarmTime;   // Время будильника
    private static boolean isAlarmSet = false; // Флаг, установлен ли будильник
    private static boolean isAlarmMode = false; // Флаг, находится ли часы в режиме настройки будильника
    private static boolean isAlarmTriggered = false; // Флаг, сработал ли будильник
    private static Timeline timeline; // Таймер для обновления времени
    private static Label timeLabel;   // Метка для отображения времени

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Electronic Clock");

        // Инициализация элементов интерфейса
        timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 24px;");

        Button buttonH = new Button("H");
        Button buttonM = new Button("M");
        Button buttonA = new Button("A");

        // Горизонтальное расположение кнопок по центру
        HBox buttonBox = new HBox(10, buttonH, buttonM, buttonA);
        buttonBox.setAlignment(Pos.CENTER);

        // Вертикальное расположение метки времени и кнопок
        VBox layout = new VBox(20, timeLabel, buttonBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Инициализация часов
        initializeClock();

        // Обработчики кнопок
        buttonH.setOnAction(e -> adjustTime(true)); // true - часы, false - минуты
        buttonM.setOnAction(e -> adjustTime(false));
        buttonA.setOnAction(e -> toggleAlarmMode());
    }

    // Инициализация часов
    private static void initializeClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        currentTime = LocalTime.now();
        timeLabel.setText("Time: " + formatter.format(currentTime));

        // Таймер для обновления времени каждую секунду
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!isAlarmMode && !isAlarmTriggered) { // Если не в режиме настройки и будильник не сработал
                    currentTime = currentTime.plusMinutes(1); // Увеличиваем время на 1 минуту
                    timeLabel.setText("Time: " + formatter.format(currentTime));

                    // Проверка срабатывания будильника
                    if (isAlarmSet && currentTime.getHour() == alarmTime.getHour() && currentTime.getMinute() == alarmTime.getMinute()) {
                        triggerAlarm();
                    }
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Увеличение часов или минут
    private static void adjustTime(boolean isHours) {
        if (isAlarmMode) {
            // В режиме настройки будильника изменяем время будильника
            if (isHours) {
                alarmTime = alarmTime.plusHours(1);
            } else {
                alarmTime = alarmTime.plusMinutes(1);
            }
            // Ограничение по модулю 24 для часов и 60 для минут
            if (alarmTime.getHour() >= 24) {
                alarmTime = alarmTime.minusHours(24);
            }
            if (alarmTime.getMinute() >= 60) {
                alarmTime = alarmTime.minusMinutes(60);
            }
        } else {
            // В обычном режиме изменяем текущее время
            if (isHours) {
                currentTime = currentTime.plusHours(1);
            } else {
                currentTime = currentTime.plusMinutes(1);
            }
            // Ограничение по модулю 24 для часов и 60 для минут
            if (currentTime.getHour() >= 24) {
                currentTime = currentTime.minusHours(24);
            }
            if (currentTime.getMinute() >= 60) {
                currentTime = currentTime.minusMinutes(60);
            }
        }
        updateTimeLabel();
    }

    // Переключение режима будильника
    private static void toggleAlarmMode() {
        if (isAlarmTriggered) {
            // Если будильник сработал, выключаем его и возвращаемся в обычный режим
            isAlarmTriggered = false;
            isAlarmSet = false;
            timeline.play(); // Возобновляем обновление времени
            updateTimeLabel();
            System.out.println("Будильник выключен.");
        } else if (!isAlarmSet) {
            // Если будильник выключен, включаем его и переходим в режим настройки
            isAlarmSet = true;
            isAlarmMode = true;
            alarmTime = LocalTime.of(currentTime.getHour(), currentTime.getMinute()); // Устанавливаем время будильника на текущее время
            timeline.pause(); // Останавливаем обновление времени
            System.out.println("Режим настройки будильника. Установите время будильника.");
        } else if (isAlarmMode) {
            // Если находимся в режиме настройки будильника, возвращаемся в обычный режим
            isAlarmMode = false;
            timeline.play(); // Возобновляем обновление времени
            System.out.println("Будильник установлен на: " + alarmTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            // Если будильник включен и находимся в обычном режиме, выключаем будильник
            isAlarmSet = false;
            System.out.println("Будильник выключен.");
        }
        updateTimeLabel();
    }

    // Срабатывание будильника
    private static void triggerAlarm() {
        System.out.println("Будильник сработал!");
        isAlarmTriggered = true;
        timeline.pause(); // Останавливаем обновление времени
        timeLabel.setText("Будильник"); // Меняем текст Label на "Будильник"
    }

    // Обновление метки времени
    private static void updateTimeLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (isAlarmMode) {
            timeLabel.setText("Set Alarm: " + formatter.format(alarmTime));
        } else if (isAlarmTriggered) {
            timeLabel.setText("Будильник");
        } else {
            timeLabel.setText("Time: " + formatter.format(currentTime));
        }
    }
}