module com.senac.projetointegradorcomeco {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    exports Aplication to javafx.graphics;
    exports controller to javafx.fxml;
    opens controller to javafx.fxml;
    opens model.entities to javafx.base;

}