package com.cropster.roastingintelligence.ui;

import com.cropster.roastingintelligence.ui.util.WindowUtil;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;

public class ComponentManager {
   public static final double MINIMUM_WINDOW_HEIGHT = 590.0;
   public static final double MINIMUM_WINDOW_WIDTH = 700.0;
   private static final String PREF_EXPANDED = "expanded";
   private static final String PREF_FULL_SCREEN = "fullScreen";
   private static final String PREF_MAXIMIZED = "maximized";
   private static final String PREF_POSITION = "position";
   private static final String PREF_X = "x";
   private static final String PREF_Y = "y";
   private static final String PREF_HEIGHT = "height";
   private static final String PREF_WIDTH = "width";
   private final Preferences preferences;

   public ComponentManager(Preferences preferences) {
      this.preferences = preferences;
   }

   private String getPropertyName(String key, String property) {
      return key + "." + property;
   }

   private String getIndexedPropertyName(String key, String qualifier, String property, int index) {
      return key + "." + qualifier + Integer.toString(index) + "." + property;
   }

   private String getBoundsPropertyName(String key, String property) {
      return key + ".bounds." + property;
   }

   private void restoreBoundsAndExtendedState(Stage stage, String stageName) {
      int x = this.preferences.getInt(this.getBoundsPropertyName(stageName, "x"), -1);
      int y = this.preferences.getInt(this.getBoundsPropertyName(stageName, "y"), -1);
      int width = this.preferences.getInt(this.getBoundsPropertyName(stageName, "width"), -1);
      int height = this.preferences.getInt(this.getBoundsPropertyName(stageName, "height"), -1);
      if (x == -1 && y == -1 && width == -1 && height == -1) {
         stage.sizeToScene();
         Point2D position = WindowUtil.getScreenPosition(stage, null);
         stage.setX(position.getX());
         stage.setY(position.getY());
         x = (int)stage.getX();
         y = (int)stage.getY();
         width = (int)stage.getWidth();
         height = (int)stage.getHeight();
      }

      Screen screen = Screen.getPrimary();
      if (width > 0 && height > 0) {
         double centerX = (double)x + (double)width / 2.0;
         double centerY = (double)y + (double)height / 2.0;
         screen = Screen.getScreens().stream().filter(s -> s.getBounds().contains(centerX, centerY)).findFirst().orElse(screen);
      }

      Rectangle2D visualBounds = screen.getVisualBounds();
      if ((double)x < visualBounds.getMinX() || (double)(x + width) > visualBounds.getMaxX()) {
         width = Math.max((int)visualBounds.getWidth() - 200, 700);
         x = (int)(visualBounds.getMinX() + (visualBounds.getWidth() - (double)width) / 2.0);
      }

      if ((double)y < visualBounds.getMinY() || (double)(y + height) > visualBounds.getMaxY()) {
         double ratio = visualBounds.getHeight() / visualBounds.getWidth();
         height = Math.max((int)(visualBounds.getHeight() - 200.0 * ratio), 590);
         y = (int)(visualBounds.getMinY() + (visualBounds.getHeight() - (double)height) / 2.0);
      }

      stage.setX((double)x);
      stage.setY((double)y);
      stage.setWidth((double)width);
      stage.setHeight((double)height);
      Platform.runLater(() -> {
         stage.setFullScreen(this.preferences.getBoolean(this.getBoundsPropertyName(stageName, "fullScreen"), false));
         if (!SystemUtils.IS_OS_MAC_OSX) {
            stage.setMaximized(this.preferences.getBoolean(this.getBoundsPropertyName(stageName, "maximized"), false));
         }
      });
   }

   public void store(Stage stage, String stageName) {
      this.preferences.putBoolean(this.getBoundsPropertyName(stageName, "fullScreen"), stage.isFullScreen());
      this.preferences.putBoolean(this.getBoundsPropertyName(stageName, "maximized"), stage.isMaximized());
      this.preferences.putInt(this.getBoundsPropertyName(stageName, "x"), (int)stage.getX());
      this.preferences.putInt(this.getBoundsPropertyName(stageName, "y"), (int)stage.getY());
      this.preferences.putInt(this.getBoundsPropertyName(stageName, "width"), (int)stage.getWidth());
      this.preferences.putInt(this.getBoundsPropertyName(stageName, "height"), (int)stage.getHeight());
   }

   public void restore(Stage stage, String stageName) {
      this.restoreBoundsAndExtendedState(stage, stageName);
   }

   public void store(TitledPane pane, String paneName) {
      this.preferences.putBoolean(this.getPropertyName(paneName, "expanded"), pane.isExpanded());
   }

   public void restore(TitledPane pane, String paneName) {
      pane.setExpanded(this.preferences.getBoolean(this.getPropertyName(paneName, "expanded"), true));
   }

   private String getDividerPropertyName(String key, String property, int index) {
      return this.getIndexedPropertyName(key, "divider", property, index);
   }

   public void store(SplitPane pane, String paneName) {
      this.store(pane, paneName, 0);
   }

   public void store(SplitPane pane, String paneName, int dividerIndex) {
      this.store(pane, paneName, new int[]{dividerIndex});
   }

   public void store(SplitPane pane, String paneName, int[] dividersIndexes) {
      for (int index : dividersIndexes) {
         this.preferences.putDouble(this.getDividerPropertyName(paneName, "position", index), ((Divider)pane.getDividers().get(index)).getPosition());
      }
   }

   public void restore(SplitPane pane, String paneName) {
      this.restore(pane, paneName, 0, 0.5);
   }

   public void restore(SplitPane pane, String paneName, double defaultValue) {
      this.restore(pane, paneName, 0, defaultValue);
   }

   public void restore(SplitPane pane, String paneName, int dividerIndex) {
      double defaultValue = 0.5;
      if (pane.getDividers().size() > 1) {
         if (dividerIndex == 0) {
            defaultValue = ((Divider)pane.getDividers().get(1)).getPosition() / 2.0;
         } else if (dividerIndex == pane.getDividers().size() - 1) {
            defaultValue = (1.0 + ((Divider)pane.getDividers().get(0)).getPosition()) / 2.0;
         } else {
            defaultValue = (
                  ((Divider)pane.getDividers().get(dividerIndex - 1)).getPosition() + ((Divider)pane.getDividers().get(dividerIndex + 1)).getPosition()
               )
               / 2.0;
         }
      }

      this.restore(pane, paneName, dividerIndex, defaultValue);
   }

   public void restore(SplitPane pane, String paneName, int dividerIndex, double defaultValue) {
      this.restore(pane, paneName, new int[]{dividerIndex}, new double[]{defaultValue});
   }

   public void restore(SplitPane pane, String paneName, int[] dividersIndexes) {
      double segmentSize = 1.0 / (double)(1 + pane.getDividers().size());
      double[] defaultValues = new double[dividersIndexes.length];

      for (int i = 0; i < dividersIndexes.length; i++) {
         defaultValues[i] = (double)(i + 1) * segmentSize;
      }

      this.restore(pane, paneName, dividersIndexes, defaultValues);
   }

   public void restore(SplitPane pane, String paneName, int[] dividersIndexes, double[] defaultValues) {
      ObservableList<Divider> dividers = pane.getDividers();
      if (!dividers.isEmpty()) {
         for (int i = 0; i < dividersIndexes.length; i++) {
            int index = dividersIndexes[i];
            double defaultValue = defaultValues[i];
            if (index >= 0 && index < dividers.size()) {
               ((Divider)dividers.get(index)).setPosition(this.preferences.getDouble(this.getDividerPropertyName(paneName, "position", index), defaultValue));
            }
         }
      }
   }

   public void store(BooleanProperty property, String propertyName) {
      this.preferences.putBoolean(propertyName, property.get());
   }

   public void restore(BooleanProperty property, String propertyName, boolean defaultValue) {
      property.set(this.preferences.getBoolean(propertyName, defaultValue));
   }

   public void store(IntegerProperty property, String propertyName) {
      this.preferences.putInt(propertyName, property.get());
   }

   public void restore(IntegerProperty property, String propertyName, int defaultValue) {
      property.set(this.preferences.getInt(propertyName, defaultValue));
   }
}
