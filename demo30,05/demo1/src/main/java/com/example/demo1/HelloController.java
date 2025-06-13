package com.example.demo1;

import java.net.URL;

import javafx.scene.control.ButtonBar;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.example.demo1.DBLogger;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HelloController {



    private LocalTime timeOnWatch;
    private LocalTime timeOfBudilnick;
    private boolean bodilnick = false;
    private boolean zvonok = false;
    private short kostul = 0;
    private LocalTime save;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button but1;

    @FXML
    private Button but2;

    @FXML
    private Button but3;

    @FXML
    private Label time;

    @FXML
    private Timeline timeline;

    @FXML
    private Timeline budTimeline;

    @FXML
    private VBox pan1;

    @FXML
    private ButtonBar pan2;

    public void Zvonok(){
        pan1.setRotate(pan1.getRotate()+10);
        pan2.setRotate(pan1.getRotate()+10);
        if (!bodilnick){
            budTimeline.pause();
            timeline.play();
        }
    }

    @FXML
    void initialize() {
        DBLogger.log("EVENT", "часы запущены.");
        assert but1 != null : "fx:id=\"but1\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert but2 != null : "fx:id=\"but2\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert but3 != null : "fx:id=\"but3\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert time != null : "fx:id=\"time\" was not injected: check your FXML file 'hello-view.fxml'.";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        this.timeOnWatch = LocalTime.now();
        time.setText(formatter.format(timeOnWatch));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (timeOfBudilnick != null && formatter.format(timeOnWatch.plusMinutes(1)).equals(formatter.format(timeOfBudilnick))){
                    bodilnick = true;
                    timeline.pause();
                    budTimeline.play();
                }
                timeOnWatch = timeOnWatch.plusMinutes(1);
                time.setText(formatter.format(timeOnWatch));
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        budTimeline = new Timeline(new KeyFrame(Duration.seconds(0.2), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Zvonok();
            }
        }));
        budTimeline.setCycleCount(Timeline.INDEFINITE);

        but1.setOnAction(actionEvent -> {
            if (bodilnick) {
                DBLogger.log("STOP", "Будильник отключён пользователем");
                bodilnick = false;
                timeOfBudilnick = null;
                budTimeline.pause(); // Останавливаем будильник
                pan1.setRotate(0);
                pan2.setRotate(0);
                timeline.play(); // Запускаем основной таймер
                System.err.println("Будильник выключен.");
                kostul = 2;
            }

            if (kostul == 1) {
                DBLogger.log("CLOCK", "Будильник переключен обычный режим работы.");
                kostul = 0;
                timeOfBudilnick = timeOnWatch;
                timeOnWatch = save;
                System.err.println("Будильник установлен на: " + formatter.format(timeOfBudilnick));
                time.setText(formatter.format(timeOnWatch));
                timeline.play(); // Запускаем основной таймер
            } else if (!bodilnick && kostul == 0) {
                DBLogger.log("CLOCK", "Будильник переключен в режим установки времени.");
                kostul = 1;
                timeline.pause(); // Останавливаем основной таймер для установки будильника
                save = timeOnWatch;
            }

            if (kostul == 2) {
                kostul = 0;
            }

        });

        but2.setOnAction(actionEvent -> {
            DBLogger.log("EVENT", "Добавлен час.");
            timeOnWatch = timeOnWatch.plusHours(1);
            time.setText(formatter.format(timeOnWatch));
        });

        but3.setOnAction(actionEvent -> {
            DBLogger.log("EVENT", "Добавлена минута.");
            timeOnWatch = timeOnWatch.plusMinutes(1);
            time.setText(formatter.format(timeOnWatch));
        });





    }

}
