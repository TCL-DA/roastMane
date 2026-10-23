package com.cropster.roastingintelligence.ui.util;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.util.Locale;
import org.girod.javafx.svgimage.GlobalConfig;
import org.girod.javafx.svgimage.SVGImage;

public enum Icon {
   ACTIVE,
   ADD,
   AIRFLOW,
   AIRFLOW_CONTROL_REPLAY,
   ALERT,
   ARROW_DOWN,
   ARROW_LEFT,
   ARROW_RIGHT,
   ARROW_UP,
   CHECK_MARK,
   CHEVRON_DOWN,
   CHEVRON_LEFT,
   CHEVRON_RIGHT,
   CHEVRON_UP,
   CLEAN,
   CLOSE,
   COFFEE_BEAN,
   COMMENT,
   CONTROL_SLIDER,
   CONTROL_SLIDER_AND_SWITCH,
   CONTROL_SWITCH,
   CUSTOMIZE,
   DELETE,
   DENSITY,
   DESCRIPTORS,
   DOWNLOAD,
   DRAG_AND_DROP,
   DRUM_CONTROL_REPLAY,
   DRUM_SPEED,
   EDIT,
   EDIT_INLINE,
   EMPTY,
   ERROR,
   EVALUATOR,
   EXPAND,
   FAVOURITE,
   FEEDBACK,
   FILE,
   FORCE_START,
   GAS,
   GAS_CONTROL_REPLAY,
   GOAL,
   HELP,
   HIDE,
   INACTIVE,
   INFO,
   LAYOUT,
   LIST,
   LIVE,
   LOADER,
   LOGIN,
   LOGO_CROPSTER,
   LOGO_CROPSTER_COLOR,
   LOGOUT,
   MERGE,
   MOISTURE,
   NO_VISUAL_FEEDBACK,
   OFFLINE,
   ONLINE,
   OPEN_OUT,
   POWER,
   PRINT,
   PROFILE,
   QR_CODE,
   REDO,
   REMOVE,
   REORDER,
   REPLAY,
   RESET,
   ROAST,
   SCHEDULE,
   SCHEDULE_COMPLETE,
   SEE_MORE,
   SETTINGS,
   SHOW,
   SIDEBAR,
   SIDEBAR_DETACHED,
   SPLIT,
   START,
   STATISTICS,
   STOP,
   SUCCESS,
   SWITCH,
   SYNC,
   TARGET,
   TEMPERATURE,
   TIME,
   TIP,
   TRACK,
   UNDO,
   UNFAVOURITE,
   UPLOAD,
   USER,
   VOLUME_HIGH,
   WARNING,
   WATER_ACTIVITY;

   public SVGImage load(String styleClass, double scale) {
      return this.setIconStyles(JavaFxUtil.loadSvg(this.toUrl(), styleClass, scale));
   }

   public SVGImage load(String styleClass) {
      return this.setIconStyles(JavaFxUtil.loadSvg(this.toUrl(), styleClass));
   }

   public SVGImage load(double scale) {
      return this.setIconStyles(JavaFxUtil.loadSvg(this.toUrl(), scale));
   }

   public SVGImage load() {
      return this.setIconStyles(JavaFxUtil.loadSvg(this.toUrl()));
   }

   URL toUrl() {
      String path = String.format("/icons/%s.svg", this.lowerCaseName());
      return Icon.class.getResource(path);
   }

   public String lowerCaseName() {
      return this.name().toLowerCase(Locale.US).replace("_", "-");
   }

   public String iconStyle() {
      return "icon-" + this.lowerCaseName();
   }

   private SVGImage setIconStyles(SVGImage svgImage) {
      svgImage.getStyleClass().add("icon");
      svgImage.getStyleClass().add(this.iconStyle());
      return svgImage;
   }

   static {
      GlobalConfig.getInstance().setExceptionsHandling((short)3);

      for (Icon icon : values()) {
         try {
            icon.load();
         } catch (Exception var5) {
            throw new Error("Error loading icon " + icon, var5);
         }
      }
   }
}
