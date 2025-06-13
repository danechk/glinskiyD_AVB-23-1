package com.example.kis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class HelloApplication extends Application {

    static double lastValue = 0.0;

    static Map<String,String> k = new HashMap<String,String>();


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Курсы валют ЦБ РФ");
        stage.setScene(scene);
        stage.show();
    }

    static double reqest(String date) throws IOException {
        k.put("USD", "R01235");
        k.put("EUR", "R01239");
        k.put("GBP", "R01035");
        k.put("JPY", "R01820");
        k.put("CNY", "R01375");
       // System.out.println("Selected data type: " + HelloController.selectedDataType);
        //System.out.println("Selected request type: " + HelloController.selectedRequestType);
       // System.out.println("Selected currency: " + HelloController.selectedCurrencyCharCode);
        double valueA = 0;
        if (HelloController.selectedDataType.equals("XML") && HelloController.selectedRequestType.equals("REST")) {
            String urlString = "https://www.cbr.ru/scripts/XML_daily.asp?date_req=" + date;
            System.err.println(urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();

            StringBuilder value = new StringBuilder();
            try {

                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                SAXParser saxParser = parserFactory.newSAXParser();
                MyHandler handler = new MyHandler(k.get(HelloController.selectedCurrencyCharCode));

                saxParser.parse(inputStream, handler);

                value = handler.getElementValue();
                String value2 = String.valueOf(value);
                return Double.parseDouble(String.valueOf(value2.replace(",", ".")));

            } catch (Exception e) {
                e.printStackTrace();
            }



        } else {
            // JSON запрос (оставлен без изменений)
            String url = "https://www.cbr-xml-daily.ru/archive/" + date + "/daily_json.js";
            System.out.println("Making JSON request to: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(response.body());
                    JsonNode valuteNode = rootNode.path("Valute");
                    return valuteNode.path(HelloController.selectedCurrencyCharCode).path("Value").asDouble();
                }
            } catch (Exception e) {
                System.err.println("Error processing JSON request:");
                e.printStackTrace();
            }
            return lastValue;
        }
        return valueA;
    }


    static Map<String, List<String>> request2(String date1, String date2) throws IOException {
        k.put("USD", "R01235");
        k.put("EUR", "R01239");
        k.put("GBP", "R01035");
        k.put("JPY", "R01820");
        k.put("CNY", "R01375");
        String urlString = "http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=" + date1 + "&date_req2=" + date2 + "&VAL_NM_RQ=" + k.get(HelloController.selectedCurrencyCharCode);
        //System.err.println(urlString);
        Map<String, List<String>> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<String> values = new ArrayList<>();

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        InputStream inputStream = connection.getInputStream();

        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = parserFactory.newSAXParser();

            HelloController.PeriodHandler handler = new HelloController.PeriodHandler();
            saxParser.parse(inputStream, handler);

            dates = handler.getDates();
            values = handler.getValues();

        } catch (Exception e) {
            e.printStackTrace();
        }

        result.put("dates", dates);

        result.put("values", values);
        return result;
    }


    public static void main(String[] args) {
        launch();


    }




    public static class MyHandler extends DefaultHandler {

        private String targetId;
        private boolean isTarget = false;
        private StringBuilder elementValue;

        public MyHandler(String targetId) {
            this.targetId = targetId;
        }

        public StringBuilder getElementValue() {
            return elementValue;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("Valute".equals(qName) && targetId.equals(attributes.getValue("ID"))) {
                isTarget = true;
            }
            if (isTarget && "Value".equals(qName)) {
                elementValue = new StringBuilder();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (isTarget && elementValue != null) {
                elementValue.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (isTarget && "Value".equals(qName)) {
                //System.out.println("Value: " + elementValue.toString().trim());
                elementValue.setLength(0);
            }
            if ("Valute".equals(qName)) {
                isTarget = false;

            }
        }
    }





}