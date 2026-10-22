module javafx.swing {
   requires java.datatransfer;
   requires javafx.base;
   requires jdk.unsupported.desktop;
   requires transitive java.desktop;
   requires transitive javafx.graphics;

   exports javafx.embed.swing;
   exports com.sun.javafx.embed.swing to
      javafx.graphics;
}
