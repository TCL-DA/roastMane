module javafx.media {
   requires transitive javafx.base;
   requires transitive javafx.graphics;

   exports javafx.scene.media;
   exports com.sun.javafx.media to
      javafx.web;
   exports com.sun.media.jfxmedia to
      javafx.web;
   exports com.sun.media.jfxmedia.control to
      javafx.web;
   exports com.sun.media.jfxmedia.events to
      javafx.web;
   exports com.sun.media.jfxmedia.locator to
      javafx.web;
   exports com.sun.media.jfxmedia.track to
      javafx.web;
}
