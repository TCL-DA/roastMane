module javafx.web {
   requires java.desktop;
   requires java.net.http;
   requires javafx.media;
   requires jdk.jsobject;
   requires jdk.xml.dom;
   requires transitive java.xml;
   requires transitive javafx.base;
   requires transitive javafx.controls;
   requires transitive javafx.graphics;

   exports javafx.scene.web;
   exports com.sun.javafx.fxml.builder.web to
      javafx.fxml;
}
