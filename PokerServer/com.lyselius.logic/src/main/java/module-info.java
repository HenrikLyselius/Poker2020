module com.lyselius.logic {
    requires com.lyselius.connection;
    requires java.persistence;
    opens com.lyselius.logic to org.hibernate.orm.core;
    exports com.lyselius.logic;
}