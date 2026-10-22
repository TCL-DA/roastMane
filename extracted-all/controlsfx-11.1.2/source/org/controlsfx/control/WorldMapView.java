package org.controlsfx.control;

import impl.org.controlsfx.worldmap.WorldMapViewSkin;
import java.util.Locale;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.util.Callback;

public class WorldMapView extends ControlsFXControl {
   private static final String DEFAULT_STYLE_CLASS = "world-map";
   private Tooltip tooltip = new Tooltip();
   private final ObjectProperty<WorldMapView.SelectionMode> countrySelectionMode = new SimpleObjectProperty(
      this, "countrySelectionMode", WorldMapView.SelectionMode.MULTIPLE
   );
   private final ObjectProperty<WorldMapView.SelectionMode> locationSelectionMode = new SimpleObjectProperty(
      this, "locationSelectionMode", WorldMapView.SelectionMode.MULTIPLE
   );
   private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, "zoomFactor", 1.0) {
      public void set(double newValue) {
         super.set(Math.max(1.0, Math.min(10.0, newValue)));
      }
   };
   private final ListProperty<WorldMapView.Country> selectedCountries = new SimpleListProperty(this, "selectedCountries", FXCollections.observableArrayList());
   private final ListProperty<WorldMapView.Location> selectedLocations = new SimpleListProperty(this, "selectedLocations", FXCollections.observableArrayList());
   private final ListProperty<WorldMapView.Country> countries = new SimpleListProperty(this, "countries", FXCollections.observableArrayList());
   private final ListProperty<WorldMapView.Location> locations = new SimpleListProperty(this, "locations", FXCollections.observableArrayList());
   private final BooleanProperty showLocations = new SimpleBooleanProperty(this, "showLocations", true);
   private final ObjectProperty<Callback<WorldMapView.Location, Node>> locationViewFactory = new SimpleObjectProperty(this, "locationViewFactory");
   private final ObjectProperty<Callback<WorldMapView.Country, WorldMapView.CountryView>> countryViewFactory = new SimpleObjectProperty(
      this, "countryViewFactory"
   );

   public WorldMapView() {
      this.getStyleClass().add("world-map");
      this.setCountryViewFactory(country -> {
         WorldMapView.CountryView view = new WorldMapView.CountryView(country);
         view.setOnMouseEntered(evt -> this.tooltip.setText(country.getLocale().getDisplayCountry()));
         Tooltip.install(view, this.tooltip);
         return view;
      });
      this.setLocationViewFactory(location -> {
         Circle circle = new Circle();
         circle.setRadius(4.0);
         circle.setTranslateX(-4.0);
         circle.setTranslateY(-4.0);
         circle.setOnMouseEntered(evt -> this.tooltip.setText(location.getName()));
         Tooltip.install(circle, this.tooltip);
         return circle;
      });
   }

   protected Skin<?> createDefaultSkin() {
      return new WorldMapViewSkin(this);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(WorldMapView.class, "world.css");
   }

   public final ObjectProperty<WorldMapView.SelectionMode> countrySelectionModeProperty() {
      return this.countrySelectionMode;
   }

   public final WorldMapView.SelectionMode getCountrySelectionMode() {
      return (WorldMapView.SelectionMode)this.countrySelectionMode.get();
   }

   public final void setCountrySelectionMode(WorldMapView.SelectionMode mode) {
      this.countrySelectionMode.set(mode);
   }

   public final ObjectProperty<WorldMapView.SelectionMode> locationSelectionModeProperty() {
      return this.locationSelectionMode;
   }

   public final WorldMapView.SelectionMode getLocationSelectionMode() {
      return (WorldMapView.SelectionMode)this.locationSelectionMode.get();
   }

   public final void setLocationSelectionMode(WorldMapView.SelectionMode mode) {
      this.locationSelectionMode.set(mode);
   }

   public final DoubleProperty zoomFactorProperty() {
      return this.zoomFactor;
   }

   public final double getZoomFactor() {
      return this.zoomFactor.get();
   }

   public final void setZoomFactor(double factor) {
      this.zoomFactor.set(factor);
   }

   public final ListProperty<WorldMapView.Country> selectedCountriesProperty() {
      return this.selectedCountries;
   }

   public final ObservableList<WorldMapView.Country> getSelectedCountries() {
      return (ObservableList<WorldMapView.Country>)this.selectedCountries.get();
   }

   public final void setSelectedCountries(ObservableList<WorldMapView.Country> countries) {
      this.selectedCountries.set(countries);
   }

   public final ListProperty<WorldMapView.Location> selectedLocationsProperty() {
      return this.selectedLocations;
   }

   public final ObservableList<WorldMapView.Location> getSelectedLocations() {
      return (ObservableList<WorldMapView.Location>)this.selectedLocations.get();
   }

   public final void setSelectedLocations(ObservableList<WorldMapView.Location> locations) {
      this.selectedLocations.set(locations);
   }

   public final ListProperty<WorldMapView.Country> countriesProperty() {
      return this.countries;
   }

   public final ObservableList<WorldMapView.Country> getCountries() {
      return (ObservableList<WorldMapView.Country>)this.countries.get();
   }

   public final void setCountries(ObservableList<WorldMapView.Country> countries) {
      this.countries.set(countries);
   }

   public final ListProperty<WorldMapView.Location> locationsProperty() {
      return this.locations;
   }

   public final ObservableList<WorldMapView.Location> getLocations() {
      return (ObservableList<WorldMapView.Location>)this.locations.get();
   }

   public final void setLocations(ObservableList<WorldMapView.Location> locations) {
      this.locations.set(locations);
   }

   public final BooleanProperty showLocationsProperty() {
      return this.showLocations;
   }

   public final boolean isShowLocations() {
      return this.showLocations.get();
   }

   public final void setShowLocations(boolean show) {
      this.showLocations.set(show);
   }

   public final ObjectProperty<Callback<WorldMapView.Location, Node>> locationViewFactoryProperty() {
      return this.locationViewFactory;
   }

   public final Callback<WorldMapView.Location, Node> getLocationViewFactory() {
      return (Callback<WorldMapView.Location, Node>)this.locationViewFactory.get();
   }

   public final void setLocationViewFactory(Callback<WorldMapView.Location, Node> factory) {
      this.locationViewFactory.set(factory);
   }

   public final ObjectProperty<Callback<WorldMapView.Country, WorldMapView.CountryView>> countryViewFactoryProperty() {
      return this.countryViewFactory;
   }

   public final Callback<WorldMapView.Country, WorldMapView.CountryView> getCountryViewFactory() {
      return (Callback<WorldMapView.Country, WorldMapView.CountryView>)this.countryViewFactory.get();
   }

   public final void setCountryViewFactory(Callback<WorldMapView.Country, WorldMapView.CountryView> factory) {
      this.countryViewFactory.set(factory);
   }

   public static enum Country {
      AE(),
      AO(),
      AR(),
      AT(),
      AU(),
      AZ(),
      BA(),
      BD(),
      BE(),
      BF(),
      BG(),
      BI(),
      BJ(),
      BN(),
      BO(),
      BR(),
      BS(),
      BT(),
      BW(),
      BY(),
      BZ(),
      CA(),
      CD(),
      CF(),
      CG(),
      CH(),
      CI(),
      CL(),
      CM(),
      CN(),
      CO(),
      CR(),
      CU(),
      CY(),
      CZ(),
      DE(),
      DJ(),
      DK(),
      DO(),
      DZ(),
      EC(),
      EE(),
      EG(),
      EH(),
      ER(),
      ES(),
      ET(),
      FK(),
      FI(),
      FJ(),
      FR(),
      GA(),
      GB(),
      GE(),
      GF(),
      GH(),
      GL(),
      GM(),
      GN(),
      GQ(),
      GR(),
      GT(),
      GW(),
      GY(),
      HN(),
      HR(),
      HT(),
      HU(),
      ID(),
      IE(),
      IL(),
      IN(),
      IQ(),
      IR(),
      IS(),
      IT(),
      JM(),
      JO(),
      JP(),
      KE(),
      KG(),
      KH(),
      KP(),
      KR(),
      XK(),
      KW(),
      KZ(),
      LA(),
      LB(),
      LK(),
      LR(),
      LS(),
      LT(),
      LU(),
      LV(),
      LY(),
      MA(),
      MD(),
      ME(),
      MG(),
      MK(),
      ML(),
      MM(),
      MN(),
      MR(),
      MW(),
      MX(),
      MY(),
      MZ(),
      NA(),
      NC(),
      NE(),
      NG(),
      NI(),
      NL(),
      NO(),
      NP(),
      NZ(),
      OM(),
      PA(),
      PE(),
      PG(),
      PH(),
      PL(),
      PK(),
      PR(),
      PS(),
      PT(),
      PY(),
      QA(),
      RO(),
      RS(),
      RU(),
      RW(),
      SA(),
      SB(),
      SD(),
      SE(),
      SI(),
      SJ(),
      SK(),
      SL(),
      SN(),
      SO(),
      SR(),
      SS(),
      SV(),
      SY(),
      SZ(),
      TD(),
      TF(),
      TG(),
      TH(),
      TJ(),
      TL(),
      TM(),
      TN(),
      TR(),
      TT(),
      TW(),
      TZ(),
      UA(),
      UG(),
      US(),
      UY(),
      UZ(),
      VE(),
      VN(),
      VU(),
      YE(),
      ZA(),
      ZM(),
      ZW();

      private final Locale locale = new Locale("", this.name());

      private Country(String... p) {
      }

      public Locale getLocale() {
         return this.locale;
      }
   }

   public static class CountryView extends SVGPath {
      private final WorldMapView.Country country;

      public CountryView(WorldMapView.Country country) {
         this.country = Objects.requireNonNull(country);
      }

      public final WorldMapView.Country getCountry() {
         return this.country;
      }

      public String getName() {
         return this.country.name();
      }
   }

   public static class Location {
      private String name;
      private double latitude;
      private double longitude;

      public Location(double latitude, double longitude) {
         this("", latitude, longitude);
      }

      public Location(String name, double latitude, double longitude) {
         this.name = name;
         this.latitude = latitude;
         this.longitude = longitude;
      }

      public final String getName() {
         return this.name;
      }

      public final double getLatitude() {
         return this.latitude;
      }

      public final double getLongitude() {
         return this.longitude;
      }
   }

   public static enum SelectionMode {
      SINGLE,
      MULTIPLE;
   }
}
