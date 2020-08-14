module com.lyselius.GUI {
    requires com.lyselius.connection;
    requires javafx.controls;
    opens com.lyselius.GUI to javafx.graphics;
}