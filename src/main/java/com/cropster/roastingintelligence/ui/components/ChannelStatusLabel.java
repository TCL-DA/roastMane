package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.model.Measure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelStatusLabel extends GridPane implements Initializable {
   private static final Logger LOGGER = LoggerFactory.getLogger(ChannelStatusLabel.class);
   private static final Font NORMAL_FONT3EM = loadFont(Font.getDefault().getSize() * 3.0);
   private static final Font NORMAL_FONT2EM = loadFont(Font.getDefault().getSize() * 2.0);
   private static final Font CONDENSED_FONT3EM = loadFont(Font.getDefault().getSize() * 3.0 * 0.75);
   private static final Font CONDENSED_FONT2EM = loadFont(Font.getDefault().getSize() * 2.0 * 0.75);
   private static final NumberFormat FORMAT = NumberFormat.getInstance();
   private static final String NULL_STRING = "-";
   private final boolean condensedChannelStatus;
   @FXML
   private VBox valueContainer;
   @FXML
   private Label nameLabel;
   @FXML
   private Label valueLabel;
   @FXML
   private Label unitLabel;
   @FXML
   private Label rorValueLabel;
   @FXML
   private StackPane rorContainer;
   private Measure value;
   private Measure secondaryValue;
   private Color valueCcolor;
   private Color rorColor;
   private boolean autoMode = false;

   private static Font loadFont(double size) {
      try {
         return Defaults.LOCALE_ARABIC.getLanguage().equals(Locale.getDefault().getLanguage())
            ? Font.font(size)
            : Font.loadFont(JavaFxUtil.FONT_MONOSPACE_URL.openStream(), size);
      } catch (Exception var3) {
         LOGGER.error("Error loading font {}", JavaFxUtil.FONT_MONOSPACE_URL, var3);
         return null;
      }
   }

   public ChannelStatusLabel() {
      this(false);
   }

   public ChannelStatusLabel(boolean condensedChannelStatus) {
      JavaFxUtil.loadComponent(this);
      this.condensedChannelStatus = condensedChannelStatus;
      this.rorValueLabel.setFont(this.getFont2Em());
      this.valueLabel.setFont(this.getFont3Em());
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.setName("?");
      this.setValue(null);
      this.setRorVisible(false);
   }

   public boolean isRorVisible() {
      return this.rorContainer.isVisible();
   }

   public void setRorVisible(boolean visible) {
      this.rorContainer.setVisible(visible);
   }

   public boolean isPrimaryValue() {
      return this.valueLabel.getFont() == this.getFont2Em();
   }

   public void setPrimaryValue(boolean primaryValue) {
      this.valueLabel.setFont(primaryValue ? this.getFont3Em() : this.getFont2Em());
   }

   public String getName() {
      return this.nameLabel.getText();
   }

   public void setName(String name) {
      this.nameLabel.setText(name);
   }

   public Measure getValue() {
      return this.value;
   }

   public void setAutoMode(boolean autoMode) {
      this.autoMode = autoMode;
      if (autoMode) {
         this.valueLabel.setText(org.cropster.roastingintelligence.util.Messages.m_("Auto"));
         this.unitLabel.setVisible(false);
      } else {
         this.setValue(this.value);
      }
   }

   public void setValue(Measure value) {
      this.value = value;
      if (!this.autoMode) {
         if (value != null) {
            String text = FORMAT.format(value.getAmount());
            this.valueLabel.setText(text);
            this.unitLabel.setText(org.cropster.roastingintelligence.util.Messages.m_(value.getUnit()));
            this.unitLabel.setVisible(true);
         } else {
            this.valueLabel.setText("-");
            this.unitLabel.setVisible(false);
         }
      }
   }

   public Measure getRorValue() {
      return this.secondaryValue;
   }

   public void setRorValue(Measure value) {
      if (!this.rorContainer.isVisible()) {
         this.rorContainer.setVisible(true);
      }

      this.secondaryValue = value;
      this.rorValueLabel.setText(value != null ? FORMAT.format(value.getAmount()) : "-");
   }

   public Color getValueColor() {
      return this.valueCcolor;
   }

   public void setValueColor(Color valueColor) {
      this.valueCcolor = valueColor;
      JavaFxUtil.setTextColor(this.nameLabel, valueColor);
      JavaFxUtil.setTextColor(this.valueLabel, valueColor);
      JavaFxUtil.setTextColor(this.unitLabel, valueColor);
   }

   public Color getRorColor() {
      return this.rorColor;
   }

   public void setRorColor(Color rorColor) {
      this.rorColor = rorColor;
      JavaFxUtil.setTextColor(this.rorValueLabel, rorColor);
   }

   public void makeValueSpanColumns() {
      GridPane.setColumnIndex(this.valueContainer, 0);
      GridPane.setColumnSpan(this.valueContainer, 2);
   }

   private Font getFont3Em() {
      return this.condensedChannelStatus ? CONDENSED_FONT3EM : NORMAL_FONT3EM;
   }

   private Font getFont2Em() {
      return this.condensedChannelStatus ? CONDENSED_FONT2EM : NORMAL_FONT2EM;
   }

   static {
      FORMAT.setMinimumFractionDigits(1);
      FORMAT.setMaximumFractionDigits(1);
      FORMAT.setGroupingUsed(false);
   }
}
