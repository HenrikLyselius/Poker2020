module com.lyselius.database {
    requires org.hibernate.orm.core;
    requires com.fasterxml.classmate;
    requires net.bytebuddy;
    requires org.hibernate.commons.annotations;
    requires com.sun.xml.bind;
    requires java.xml.bind;
    requires java.sql;
    requires mysql.connector.java;
    requires com.lyselius.logic;
    requires java.xml;
    requires java.naming;
    exports com.lyselius.database;
}