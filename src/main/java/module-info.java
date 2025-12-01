module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires com.almasb.fxgl.all;
    requires java.rmi;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.dao to javafx.fxml;
    opens com.example.demo.service to javafx.fxml;
    opens com.example.demo.database to javafx.fxml;
    opens com.example.demo.server to javafx.fxml;
    opens com.example.demo.client to javafx.fxml;
    opens com.example.demo.test to javafx.fxml;
    opens com.example.demo.util to javafx.fxml;
    opens Entite to javafx.fxml;
    opens CategorieEnum to javafx.fxml;

    exports com.example.demo;
    exports com.example.demo.dao;
    exports com.example.demo.service;
    exports com.example.demo.database;
    exports com.example.demo.server;
    exports com.example.demo.client;
    exports com.example.demo.test;
    exports com.example.demo.util;
    exports Entite;
    exports CategorieEnum;
}