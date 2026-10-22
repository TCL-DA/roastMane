module javafx.controls {
   requires transitive javafx.base;
   requires transitive javafx.graphics;

   exports javafx.scene.chart;
   exports javafx.scene.control;
   exports javafx.scene.control.cell;
   exports javafx.scene.control.skin;
   exports com.sun.javafx.scene.control to
      javafx.web;
   exports com.sun.javafx.scene.control.behavior to
      javafx.web;
   exports com.sun.javafx.scene.control.inputmap to
      javafx.web;
   exports com.sun.javafx.scene.control.skin to
      javafx.graphics,
      javafx.web;
}
