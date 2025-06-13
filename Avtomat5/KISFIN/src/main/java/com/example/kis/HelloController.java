package com.example.kis;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HelloController {

    static String Sdate;
    static String Fdate;
    public static String selectedCurrencyCharCode;
    public static String selectedDataType;
    public static String selectedRequestType;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    String patternR = "dd/MM/yyyy";
    DateTimeFormatter dateFormatterR = DateTimeFormatter.ofPattern(patternR);
    @FXML
    private Button ButToday;

    @FXML
    private DatePicker Finish;

    @FXML
    private Button RisGraph;

    @FXML
    private ComboBox<Map<String, String>> ValType;

    @FXML
    private ComboBox<String> dataT;

    @FXML
    private ComboBox<String> reqType;

    @FXML
    private DatePicker Start;

    @FXML
    private Label Today;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private LineChart<Number, Double> graph;

    String pattern = "yyyy/MM/dd";
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
    DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd.MM");

    @FXML
    void initialize() {

        ObservableList<Map<String, String>> currencies = FXCollections.observableArrayList(
                Map.of("name", "Доллар США", "code", "USD", "charCode", "USD"),
                Map.of("name", "Евро", "code", "EUR", "charCode", "EUR"),
                Map.of("name", "Фунт стерлингов", "code", "GBP", "charCode", "GBP"),
                Map.of("name", "Японская йена", "code", "JPY", "charCode", "JPY"),
                Map.of("name", "Китайский юань", "code", "CNY", "charCode", "CNY")
        );
        ObservableList<String> dataTypes = FXCollections.observableArrayList(
                "XML",
                "json"
        );

        dataT.setItems(dataTypes);



        // Обработчик выбора типа данных
        dataT.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedDataType = newVal;
                System.out.println("Выбран тип данных: " + selectedDataType);
            }
        });

        // Установка значения по умолчанию
        dataT.getSelectionModel().selectFirst();




// Устанавливаем список в ComboBox
        ValType.setItems(currencies);
        ValType.setCellFactory(lv -> new ListCell<Map<String, String>>() {
            @Override
            protected void updateItem(Map<String, String> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.get("name") + " (" + item.get("charCode") + ")");
            }
        });

// Настраиваем отображение выбранного элемента
        ValType.setConverter(new StringConverter<Map<String, String>>() {
            @Override
            public String toString(Map<String, String> item) {
                return item == null ? null : item.get("name") + " (" + item.get("charCode") + ")";
            }

            @Override
            public Map<String, String> fromString(String string) {
                return null; // Не требуется для простого отображения
            }
        });

        ValType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedCurrencyCharCode = newVal.get("charCode");
                System.out.println("Выбрана валюта: " + selectedCurrencyCharCode);
            }
        });

        // Выбираем первую валюту по умолчанию
        ValType.getSelectionModel().selectFirst();


        ObservableList<String> requestTypes = FXCollections.observableArrayList(
                "REST",
                "SOAP"
        );

        reqType.setItems(requestTypes);

        // Обработчик выбора типа запроса
        reqType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedRequestType = newVal; // Сохраняем выбранный тип
                System.out.println("Выбрана валюта: " + selectedRequestType);
                if ("SOAP".equals(newVal)) {
                    // Оставляем только XML для SOAP
                    dataT.setItems(FXCollections.observableArrayList("XML"));
                    dataT.getSelectionModel().selectFirst(); // Автоматически выбираем XML
                } else {
                    // Для REST возвращаем оба варианта
                    dataT.setItems(FXCollections.observableArrayList("XML", "JSON"));
                }
            }
        });




        // Установка значения по умолчанию (REST)
        reqType.getSelectionModel().selectFirst();


        assert ButToday != null : "fx:id=\"ButToday\" ";
        assert Finish != null : "fx:id=\"Finish\" ";
        assert RisGraph != null : "fx:id=\"RisGraph\" ";
        assert Start != null : "fx:id=\"Start\" ";
        assert Today != null : "fx:id=\"Today\" ";
        assert graph != null : "fx:id=\"graph\" ";
        assert xAxis != null : "fx:id=\"xAxis\" ";


        Finish.setValue(LocalDate.now());
        Start.setValue(LocalDate.now().minusDays(10));
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                LocalDate date = LocalDate.ofEpochDay(object.longValue());
                return date.format(displayFormatter);
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        Start.setConverter(new javafx.util.converter.LocalDateStringConverter(dateFormatter, DateTimeFormatter.ISO_LOCAL_DATE));
        Finish.setConverter(new javafx.util.converter.LocalDateStringConverter(dateFormatter, DateTimeFormatter.ISO_LOCAL_DATE));

        Start.setOnAction(event -> {
            String formattedStartDate = Start.getValue().format(dateFormatter);
            System.out.println("Start Date: " + formattedStartDate);
        });

        Finish.setOnAction(event -> {
            String formattedFinishDate = Finish.getValue().format(dateFormatter);
            System.out.println("Finish Date: " + formattedFinishDate);
        });

        Start.setPromptText(pattern);
        Finish.setPromptText(pattern);

        ButToday.setOnAction(actionEvent -> {
            if (HelloController.selectedDataType.equals("XML") && HelloController.selectedRequestType.equals("REST")){
                SimpleDateFormat formattt = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date date = java.sql.Date.valueOf(LocalDate.now());
                try {
                    double text = HelloApplication.reqest(formattt.format(date));
                    Today.setText("Курс Валюты на сегодня = " + String.valueOf(text));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            else{
                SimpleDateFormat formattt = new SimpleDateFormat("yyyy/MM/dd");
                java.util.Date date = java.sql.Date.valueOf(LocalDate.now());
                try {
                    double text = HelloApplication.reqest(formattt.format(date));
                    Today.setText("Курс Азезбайджанского маната за сегодня = " + String.valueOf(text));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }



        });

        RisGraph.setOnAction(actionEvent -> {
            if (HelloController.selectedDataType.equals("XML") && HelloController.selectedRequestType.equals("REST")) {

                System.err.println(Start.getValue());
                Ris3(Finish.getValue().toString(), Start.getValue().toString());

            }
            else {
                Ris2(Finish.getValue().toString(), Start.getValue().toString());


            }





        });
    }

    void Ris1() {
        ObservableList<XYChart.Data<Number, Double>> data = FXCollections.observableArrayList();

        LocalDate startDate = LocalDate.now().minusDays(6);
        LocalDate endDate = LocalDate.now();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

            if (HelloController.selectedDataType.equals("XML") && HelloController.selectedRequestType.equals("REST")){
                try {
                    String requestDate = date.format(dateFormatterR);
                    double value = HelloApplication.reqest(requestDate);
                    if (value == 0.0) {
                        continue;

                    }

                    data.add(new XYChart.Data<>(date.toEpochDay(), value));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else {

                try {
                    String requestDate = date.format(dateFormatter);
                    double value = HelloApplication.reqest(requestDate);
                    if (value == 0.0) {
                        continue;

                    }

                    data.add(new XYChart.Data<>(date.toEpochDay(), value));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        XYChart.Series<Number, Double> series = new XYChart.Series<>();
        series.setName("Азербайджанский манат");
        series.setData(data);

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(startDate.toEpochDay());
        xAxis.setUpperBound(endDate.toEpochDay());
        xAxis.setTickUnit(1);
        xAxis.setTickLabelRotation(45);

        graph.getData().clear();
        graph.getData().add(series);
    }




    void Ris2(String finDate, String stDate) {
        ObservableList<XYChart.Data<Number, Double>> data = FXCollections.observableArrayList();

        LocalDate startDate = LocalDate.parse(stDate);
        LocalDate endDate = LocalDate.parse(finDate);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            try {
                String requestDate = date.format(dateFormatter);
                double value = HelloApplication.reqest(requestDate);
                if (value == 0.0){
                    continue;
                }
                if (selectedCurrencyCharCode.equals("JPY")){
                    value /= 100;
                }
                data.add(new XYChart.Data<>(date.toEpochDay(), value));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        XYChart.Series<Number, Double> series = new XYChart.Series<>();
        series.setName("Курс валют");
        series.setData(data);

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(startDate.toEpochDay());
        xAxis.setUpperBound(endDate.toEpochDay());
        xAxis.setTickUnit(1);
        xAxis.setTickLabelRotation(45);

        graph.getData().clear();
        graph.getData().add(series);
    }

    void Ris3(String finDate, String stDate) {
        ObservableList<XYChart.Data<Number, Double>> data = FXCollections.observableArrayList();

        LocalDate startDate = LocalDate.parse(stDate);
        LocalDate endDate = LocalDate.parse(finDate);

        try {

            DateTimeFormatter requestFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String startDateStr = startDate.format(requestFormatter);
            String endDateStr = endDate.format(requestFormatter);

            Map<String, List<String>> result = HelloApplication.request2(startDateStr, endDateStr);
            List<String> dates = result.get("dates");
            List<String> values = result.get("values");

            for (int i = 0; i < dates.size(); i++) {
                String dateStr = dates.get(i);
                String valueStr = values.get(i);


                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                double rate = Double.parseDouble(valueStr.replace(",", "."));
                if (selectedCurrencyCharCode.equals("JPY")){
                    rate /= 100;
                }

                data.add(new XYChart.Data<>(date.toEpochDay(), rate));
            }

            XYChart.Series<Number, Double> series = new XYChart.Series<>();
            series.setName("График курса валют");
            series.setData(data);

            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(startDate.toEpochDay());
            xAxis.setUpperBound(endDate.toEpochDay());
            xAxis.setTickUnit(1);
            xAxis.setTickLabelRotation(45);

            graph.getData().clear();
            graph.getData().add(series);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static class PeriodHandler extends DefaultHandler {
        private List<String> dates = new ArrayList<>();
        private List<String> values = new ArrayList<>();
        private StringBuilder currentValue;
        private boolean isDate = false;
        private boolean isValue = false;

        public List<String> getDates() {
            return dates;
        }

        public List<String> getValues() {
            return values;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("Record".equals(qName)) {
                String date = attributes.getValue("Date");
                if (date != null) {
                    dates.add(date);
                }
            }
            if ("Value".equals(qName)) {
                currentValue = new StringBuilder();
                isValue = true;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (isValue && currentValue != null) {
                currentValue.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("Value".equals(qName)) {
                values.add(currentValue.toString().trim());
                currentValue = null;
                isValue = false;
            }
        }
    }
}
