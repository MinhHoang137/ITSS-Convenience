module itss.convenience {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;

    opens itss.convenience to javafx.fxml;

    exports itss.convenience;

    opens controller to javafx.fxml;
    exports controller;

    opens controller.meal to javafx.fxml;

    exports controller.meal;

    opens controller.fridge to javafx.fxml;

    exports controller.fridge;
    opens model.entity to javafx.base, javafx.fxml;
    exports model.entity;
    exports controller.group;
    opens controller.group to javafx.fxml;
    exports controller.shopping;
    opens controller.shopping to javafx.fxml;
    exports controller.auth;
    opens controller.auth to javafx.fxml;
}