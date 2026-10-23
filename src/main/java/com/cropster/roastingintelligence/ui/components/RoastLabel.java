package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.Data;
import com.cropster.util.text.DurationFormat;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.util.MeasureFormat;

public class RoastLabel extends GridPane {
   private static final String BORDER_STYLE = "-fx-border-style: solid; -fx-border-width: 1px 0 0 0; -fx-border-color: -fx-color-neutral-40; ";
   private static final LocalDateTime SAMPLE_DATE_TIME = LocalDateTime.of(2020, 12, 31, 12, 23, 34);
   private static NumberFormat percentFormat = NumberFormat.getPercentInstance();
   private static DurationFormat durationFormat = DurationFormat.getDurationInstance();
   private static MeasureFormat measureFormat = MeasureFormat.getInstance();
   private static Map<String, RoastLabel.RoastLabelComponent> components = new LinkedHashMap<>();
   public static final RoastLabel.RoastLabelComponent IDTAG_NAME = new RoastLabel.RoastLabelComponent(
      "idTagName", "ID-Tag / Name", (d, r) -> String.format("%s %s", r.getLot().getIdTag(), r.getLot().getName())
   );
   public static final RoastLabel.RoastLabelComponent DATE = new RoastLabel.RoastLabelComponent(
      "date", org.cropster.roastingintelligence.util.Messages.m_("Date"), (d, r) -> Defaults.DATE_FORMAT.format(r.getStartDate())
   );
   public static final RoastLabel.RoastLabelComponent GREEN_LOTS = new RoastLabel.RoastLabelComponent(
      "greenLots",
      org.cropster.roastingintelligence.util.Messages.m_("Green lots"),
      org.cropster.roastingintelligence.util.Messages.m_("Green lots (ID-Tag / Name)"),
      RoastLabel.ComponentStyle.TEXT,
      (d, r) -> {
         String s = "";

         for (LotTransaction t : r.getLot().getSources()) {
            if (!s.isBlank()) {
               s = s + "\n";
            }

            if (d.getLots().containsKey(t.getLot())) {
               Lot l = d.getLots().get(t.getLot());
               s = s + l.getIdTag() + " " + l.getName();
            } else {
               s = s + "?";
            }

            s = s + String.format(" | %s (%s)", measureFormat.format(t.getWeight()), percentFormat.format(t.getWeight().divide(r.getStartWeight())));
         }

         return s;
      }
   );
   public static final RoastLabel.RoastLabelComponent MACHINE = new RoastLabel.RoastLabelComponent(
      "machine", org.cropster.roastingintelligence.util.Messages.m_("Machine"), (d, r) -> d.getMachines().get(r.getMachine()).getName()
   );
   public static final RoastLabel.RoastLabelComponent WORKER = new RoastLabel.RoastLabelComponent(
      "worker", org.cropster.roastingintelligence.util.Messages.m_("Roast technician"), (d, r) -> r.getWorker()
   );
   public static final RoastLabel.RoastLabelComponent PROFILE = new RoastLabel.RoastLabelComponent(
      "profile",
      org.cropster.roastingintelligence.util.Messages.m_("Profile"),
      (d, r) -> r.getProfile() != null && d.getProfiles().containsKey(r.getProfile()) ? d.getProfiles().get(r.getProfile()).getName() : null
   );
   public static final RoastLabel.RoastLabelComponent DURATION = new RoastLabel.RoastLabelComponent(
      "duration", org.cropster.roastingintelligence.util.Messages.m_("Duration"), (d, r) -> durationFormat.format(r.getDuration())
   );
   public static final RoastLabel.RoastLabelComponent WEIGHTS = new RoastLabel.RoastLabelComponent(
      "weights",
      org.cropster.roastingintelligence.util.Messages.m_("Weight (Start / End / Loss)"),
      (d, r) -> String.format(
            "%s / %s / %s", measureFormat.format(r.getStartWeight()), measureFormat.format(r.getEndWeight()), percentFormat.format(r.getWeightChange())
         )
   );
   public static final RoastLabel.RoastLabelComponent ROAST_VALUE_1 = new RoastLabel.RoastLabelComponent(
      "roastValue1",
      org.cropster.roastingintelligence.util.Messages.m_("Roast value 1"),
      (d, r) -> r.getMeasures().containsKey("roastValue") ? measureFormat.format(r.getMeasures().get("roastValue")) : null
   );
   public static final RoastLabel.RoastLabelComponent ROAST_VALUE_12 = new RoastLabel.RoastLabelComponent(
      "roastValue12",
      org.cropster.roastingintelligence.util.Messages.m_("Roast value 1 / 2"),
      (d, r) -> String.format(
            "%s / %s",
            r.getMeasures().containsKey("roastValue") ? measureFormat.format(r.getMeasures().get("roastValue")) : "-",
            r.getMeasures().containsKey("roastValue2") ? measureFormat.format(r.getMeasures().get("roastValue2")) : "-"
         )
   );
   public static final RoastLabel.RoastLabelComponent NOTES = new RoastLabel.RoastLabelComponent(
      "notes",
      org.cropster.roastingintelligence.util.Messages.m_("Roast notes"),
      org.cropster.roastingintelligence.util.Messages.m_("Roast notes"),
      RoastLabel.ComponentStyle.TEXT,
      (d, r) -> r.getNotes()
   );
   private final QrCodeRenderer qrCodeRenderer;

   public static Collection<RoastLabel.RoastLabelComponent> getComponents() {
      return Collections.unmodifiableCollection(components.values());
   }

   public static RoastLabel.RoastLabelComponent getComponent(String name) {
      return components.get(name);
   }

   public static Map<String, RoastLabel.RoastLabelData> toData(AppModel model, Roast roast, RoastLabel.DateFormatter dateFormatter) {
      Map<String, String> labelMapping = model.getPreferences().getPrintLabelMapping();
      Map<String, RoastLabel.RoastLabelData> data = new LinkedHashMap<>();
      String url = model.getUri().resolve(String.format("/r/roast/%s", roast.getLot().getId())).toString();
      if (RoastLabel.QrCodePosition.TOP.equals(model.getPreferences().getPrintQrCodePosition())) {
         data.put("QR.TOP_LEFT", new RoastLabel.RoastLabelData(url));
      } else if (RoastLabel.QrCodePosition.BOTTOM.equals(model.getPreferences().getPrintQrCodePosition())) {
         data.put("QR.BOTTOM_RIGHT", new RoastLabel.RoastLabelData(url));
      }

      for (Entry<String, String> e : labelMapping.entrySet()) {
         RoastLabel.RoastLabelComponent c = getComponent(e.getValue());
         if (Objects.equals(e.getValue(), DATE.getId())) {
            c.setFormatter((d, r) -> dateFormatter.getFormatter().format(r.getStartDate()));
         }

         data.put(e.getKey(), c.forValue(model.getData(), roast));
      }

      return data;
   }

   public RoastLabel(Map<String, RoastLabel.RoastLabelData> data, QrCodeRenderer qrCodeRenderer, boolean printTitleVisible) {
      this.qrCodeRenderer = qrCodeRenderer;
      this.init(data, printTitleVisible);
   }

   private AbstractTitled createTitled(RoastLabel.RoastLabelData data, boolean titleVisible) {
      AbstractTitled titled;
      if (RoastLabel.ComponentStyle.TEXT.equals(data.getComponent().getStyle())) {
         TitledText titledText = new TitledText();
         titledText.getTextFlow().getChildren().add(new Text(StringUtils.isNotBlank(data.getValue()) ? data.getValue() : "-"));
         titled = titledText;
      } else {
         TitledLabel titledLabel = new TitledLabel();
         titledLabel.setText(data.getValue() != null ? data.toString() : "-");
         titled = titledLabel;
      }

      titled.setTitle(data.getComponent().getTitle());
      titled.setTitleVisible(titleVisible);
      return titled;
   }

   private void init(Map<String, RoastLabel.RoastLabelData> data, boolean printTitleVisible) {
      this.getStyleClass().add("roast-label");
      RoastLabel.RoastLabelData qrTop = data.get("QR.TOP_LEFT");
      RoastLabel.RoastLabelData qrBottom = data.get("QR.BOTTOM_RIGHT");
      RoastLabel.RoastLabelData dataA = data.get("A");
      RoastLabel.RoastLabelData dataB1 = data.get("B.1");
      RoastLabel.RoastLabelData dataB2 = data.get("B.2");
      RoastLabel.RoastLabelData dataB3 = data.get("B.3");
      RoastLabel.RoastLabelData dataC = data.get("C");
      RoastLabel.RoastLabelData dataD1 = data.get("D.1");
      RoastLabel.RoastLabelData dataD2 = data.get("D.2");
      RoastLabel.RoastLabelData dataD3 = data.get("D.3");
      RoastLabel.RoastLabelData dataE1 = data.get("E.1");
      RoastLabel.RoastLabelData dataE2 = data.get("E.2");
      RoastLabel.RoastLabelData dataE3 = data.get("E.3");
      RoastLabel.RoastLabelData dataF = data.get("F");
      ColumnConstraints cc = new ColumnConstraints();
      cc.setFillWidth(true);
      cc.setHgrow(Priority.ALWAYS);
      this.getColumnConstraints().add(cc);
      int y = 0;
      HBox hboxA = new HBox();
      hboxA.getStyleClass().add("roast-label--padding");
      if (dataA != null) {
         Region titleLabelA = this.createTitled(dataA, printTitleVisible);
         titleLabelA.setMaxWidth(Double.POSITIVE_INFINITY);
         titleLabelA.getStyleClass().add("titled-label--h1");
         HBox.setHgrow(titleLabelA, Priority.ALWAYS);
         hboxA.getChildren().add(titleLabelA);
      }

      if (qrTop != null && StringUtils.isNotBlank(qrTop.getValue())) {
         hboxA.getChildren().add(this.qrCodeRenderer.renderImageView(qrTop.getValue()));
      }

      if (!hboxA.getChildren().isEmpty()) {
         GridPane.setRowIndex(hboxA, y++);
         GridPane.setFillWidth(hboxA, true);
         this.getChildren().add(hboxA);
      }

      GridPane paneB = new GridPane();
      paneB.getStyleClass().add("roast-label--padding");
      if (dataB1 != null) {
         AbstractTitled propertyLabelB1 = this.createTitled(dataB1, printTitleVisible);
         HBox.setHgrow(propertyLabelB1, Priority.ALWAYS);
         paneB.getChildren().add(propertyLabelB1);
      }

      if (dataB2 != null) {
         AbstractTitled propertyLabelB2 = this.createTitled(dataB2, printTitleVisible);
         HBox.setHgrow(propertyLabelB2, Priority.ALWAYS);
         paneB.getChildren().add(propertyLabelB2);
      }

      if (dataB3 != null) {
         AbstractTitled propertyLabelB3 = this.createTitled(dataB3, printTitleVisible);
         HBox.setHgrow(propertyLabelB3, Priority.ALWAYS);
         paneB.getChildren().add(propertyLabelB3);
      }

      if (!paneB.getChildren().isEmpty()) {
         for (int i = 0; i < paneB.getChildren().size(); i++) {
            GridPane.setColumnIndex((Node)paneB.getChildren().get(i), i);
            ColumnConstraints cc1 = new ColumnConstraints();
            cc1.setPercentWidth(100.0 / (double)paneB.getChildren().size());
            paneB.getColumnConstraints().add(cc1);
         }

         GridPane.setRowIndex(paneB, y++);
         GridPane.setFillWidth(paneB, true);
         this.getChildren().add(paneB);
      }

      if (dataC != null && dataC.getValue() != null) {
         AbstractTitled titleLabelC = this.createTitled(dataC, printTitleVisible);
         titleLabelC.setMaxWidth(Double.POSITIVE_INFINITY);
         titleLabelC.getStyleClass().add("titled-label--h2");
         titleLabelC.getStyleClass().add("roast-label--padding");
         GridPane.setRowIndex(titleLabelC, y++);
         this.getChildren().add(titleLabelC);
      }

      GridPane paneDE = new GridPane();
      paneDE.getStyleClass().add("roast-label--padding");
      VBox vboxD = new VBox();
      vboxD.getStyleClass().add("roast-label--spacing");
      if (dataD1 != null && dataD1.getValue() != null) {
         AbstractTitled propertyLabelD1 = this.createTitled(dataD1, printTitleVisible);
         VBox.setVgrow(propertyLabelD1, Priority.ALWAYS);
         vboxD.getChildren().add(propertyLabelD1);
      }

      if (dataD2 != null) {
         AbstractTitled propertyLabelD2 = this.createTitled(dataD2, printTitleVisible);
         VBox.setVgrow(propertyLabelD2, Priority.ALWAYS);
         vboxD.getChildren().add(propertyLabelD2);
      }

      if (dataD3 != null) {
         AbstractTitled propertyLabelD3 = this.createTitled(dataD3, printTitleVisible);
         VBox.setVgrow(propertyLabelD3, Priority.ALWAYS);
         vboxD.getChildren().add(propertyLabelD3);
      }

      if (!vboxD.getChildren().isEmpty()) {
         GridPane.setHgrow(vboxD, Priority.ALWAYS);
         paneDE.getChildren().add(vboxD);
      }

      VBox vboxE = new VBox();
      vboxE.getStyleClass().add("roast-label--spacing");
      if (dataE1 != null) {
         AbstractTitled propertyLabelE1 = this.createTitled(dataE1, printTitleVisible);
         VBox.setVgrow(propertyLabelE1, Priority.ALWAYS);
         vboxE.getChildren().add(propertyLabelE1);
      }

      if (dataE2 != null) {
         AbstractTitled propertyLabelE2 = this.createTitled(dataE2, printTitleVisible);
         VBox.setVgrow(propertyLabelE2, Priority.ALWAYS);
         vboxE.getChildren().add(propertyLabelE2);
      }

      if (dataE3 != null) {
         AbstractTitled propertyLabelE3 = this.createTitled(dataE3, printTitleVisible);
         VBox.setVgrow(propertyLabelE3, Priority.ALWAYS);
         vboxE.getChildren().add(propertyLabelE3);
      }

      if (!vboxE.getChildren().isEmpty()) {
         if (!vboxD.getChildren().isEmpty()) {
            GridPane.setColumnIndex(vboxE, 1);
            ColumnConstraints cc1 = new ColumnConstraints();
            cc1.setPercentWidth(50.0);
            paneDE.getColumnConstraints().add(cc1);
            ColumnConstraints cc2 = new ColumnConstraints();
            cc2.setPercentWidth(50.0);
            paneDE.getColumnConstraints().add(cc2);
         }

         GridPane.setHgrow(vboxE, Priority.ALWAYS);
         paneDE.getChildren().add(vboxE);
      }

      if (!paneDE.getChildren().isEmpty()) {
         GridPane.setRowIndex(paneDE, y++);
         this.getChildren().add(paneDE);
      }

      HBox hboxF = new HBox();
      hboxF.getStyleClass().add("roast-label--padding");
      if (dataF != null) {
         Region propertyLabelF = this.createTitled(dataF, printTitleVisible);
         propertyLabelF.setMaxWidth(Double.MAX_VALUE);
         HBox.setHgrow(propertyLabelF, Priority.ALWAYS);
         hboxF.getChildren().add(propertyLabelF);
      }

      if (qrBottom != null && StringUtils.isNotBlank(qrBottom.getValue())) {
         hboxF.getChildren().add(this.qrCodeRenderer.renderImageView(qrBottom.getValue()));
      }

      if (!hboxF.getChildren().isEmpty()) {
         GridPane.setRowIndex(hboxF, y++);
         GridPane.setFillWidth(hboxF, true);
         this.getChildren().add(hboxF);
      }
   }

   static {
      percentFormat.setMinimumFractionDigits(0);
      percentFormat.setMaximumFractionDigits(3);
      percentFormat.setRoundingMode(RoundingMode.HALF_EVEN);
   }

   public static enum ComponentStyle {
      LABEL,
      TEXT;
   }

   public static enum DateFormatter {
      ISO_LOCAL_DATE_TIME(new SimpleDateFormat("yyyy-MM-dd HH:mm")),
      MMDDYYYY_DATE_TIME(new SimpleDateFormat("MM-dd-yyyy HH:mm")),
      DDMMYYYY_DATE_TIME(new SimpleDateFormat("dd-MM-yyyy HH:mm")),
      DAY_YYYYMMDD_DATE_TIME(
         new SimpleDateFormat("EEE, yyyy-MM-dd HH:mm"), "-fx-border-style: solid; -fx-border-width: 1px 0 0 0; -fx-border-color: -fx-color-neutral-40; "
      ),
      DAY_MMDDYYYY_DATE_TIME(new SimpleDateFormat("EEE, MM-dd-yyyy HH:mm")),
      DAY_DDMMYYYY_DATE_TIME(new SimpleDateFormat("EEE, dd-MM-yyyy HH:mm")),
      MONTH_DAY_YEAR_DATE_TIME(
         new SimpleDateFormat("MMM d, yyyy HH:mm"), "-fx-border-style: solid; -fx-border-width: 1px 0 0 0; -fx-border-color: -fx-color-neutral-40; "
      ),
      DAY_MONTH_YEAR_DATE_TIME(new SimpleDateFormat("d MMM, yyyy HH:mm"));

      private final DateFormat formatter;
      private final String example;
      private final String style;

      private DateFormatter(DateFormat formatter) {
         this(formatter, null);
      }

      private DateFormatter(DateFormat formatter, String style) {
         this.formatter = formatter;
         this.style = style;
         this.example = formatter.format(Date.from(RoastLabel.SAMPLE_DATE_TIME.toInstant(ZoneOffset.UTC)));
      }

      public DateFormat getFormatter() {
         return this.formatter;
      }

      public String getExample() {
         return this.example;
      }

      public String getStyle() {
         return this.style;
      }
   }

   public static enum QrCodePosition {
      TOP,
      BOTTOM,
      HIDDEN;
   }

   public static final class RoastLabelComponent {
      private final String id;
      private final String title;
      private final String label;
      private final RoastLabel.ComponentStyle style;
      private BiFunction<Data, Roast, String> formatter;

      public RoastLabelComponent(String id, String title, String label, RoastLabel.ComponentStyle style, BiFunction<Data, Roast, String> formatter) {
         this.id = id;
         this.title = title;
         this.label = label;
         this.style = style;
         this.formatter = formatter;
         RoastLabel.components.put(id, this);
      }

      public RoastLabelComponent(String id, String title, BiFunction<Data, Roast, String> formatter) {
         this(id, title, title, RoastLabel.ComponentStyle.LABEL, formatter);
      }

      public String getId() {
         return this.id;
      }

      public String getTitle() {
         return this.title;
      }

      public String getLabel() {
         return this.label;
      }

      public RoastLabel.ComponentStyle getStyle() {
         return this.style;
      }

      public String format(Data data, Roast value) {
         return this.formatter != null ? this.formatter.apply(data, value) : "";
      }

      public RoastLabel.RoastLabelData forValue(Data data, Roast value) {
         return new RoastLabel.RoastLabelData(this, value != null ? this.format(data, value) : null);
      }

      public void setFormatter(BiFunction<Data, Roast, String> formatter) {
         this.formatter = formatter;
      }
   }

   public static final class RoastLabelData {
      private final RoastLabel.RoastLabelComponent component;
      private final String value;

      private RoastLabelData(RoastLabel.RoastLabelComponent component, String value) {
         this.component = component;
         this.value = value;
      }

      public RoastLabelData(String value) {
         this.component = null;
         this.value = value;
      }

      public RoastLabel.RoastLabelComponent getComponent() {
         return this.component;
      }

      public String getValue() {
         return this.value;
      }

      @Override
      public String toString() {
         return this.value != null ? this.value : "";
      }
   }

   public static enum RoastLabelStyle {
      DEFAULT,
      CARD;
   }
}
