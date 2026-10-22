module javafx.base {
   requires java.desktop;
   requires static jdk.jfr;

   exports javafx.beans;
   exports javafx.beans.binding;
   exports javafx.beans.property;
   exports javafx.beans.property.adapter;
   exports javafx.beans.value;
   exports javafx.collections;
   exports javafx.collections.transformation;
   exports javafx.event;
   exports javafx.util;
   exports javafx.util.converter;
   exports com.sun.javafx to
      javafx.controls,
      javafx.graphics,
      javafx.fxml,
      javafx.media,
      javafx.swing;
   exports com.sun.javafx.beans to
      javafx.controls,
      javafx.fxml,
      javafx.graphics;
   exports com.sun.javafx.binding to
      javafx.controls,
      javafx.graphics;
   exports com.sun.javafx.collections to
      javafx.controls,
      javafx.graphics,
      javafx.media,
      javafx.swing;
   exports com.sun.javafx.event to
      javafx.controls,
      javafx.graphics;
   exports com.sun.javafx.logging to
      javafx.controls,
      javafx.graphics,
      javafx.fxml,
      javafx.swing,
      javafx.web;
   exports com.sun.javafx.property to
      javafx.controls;
   exports com.sun.javafx.reflect to
      javafx.fxml,
      javafx.web;
   exports com.sun.javafx.runtime to
      javafx.graphics;
}
