package impl.org.controlsfx.worldmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.WeakListChangeListener;
import javafx.css.PseudoClass;
import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import org.controlsfx.control.WorldMapView;

public class WorldMapViewSkin extends SkinBase<WorldMapView> {
   private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
   private static final String DEFAULT_STYLE_LOCATION = "location";
   private static final String DEFAULT_STYLE_COUNTRY = "country";
   private static final double PREFERRED_WIDTH = 1009.0;
   private static final double PREFERRED_HEIGHT = 665.0;
   private static double MAP_OFFSET_X = -28.756500000000003;
   private static double MAP_OFFSET_Y = 129.675;
   private final Map<WorldMapView.Country, List<? extends String>> countryPathMap = new HashMap<>();
   private final Map<WorldMapView.Country, List<? extends WorldMapView.CountryView>> countryViewMap = new HashMap<>();
   private Pane countryPane;
   private Group group;
   private Group locationsGroup;
   protected ObservableMap<WorldMapView.Location, Node> locationMap;
   private double dragX;
   private double dragY;
   private final ListChangeListener<? super WorldMapView.Location> locationsListener = change -> {
      while (change.next()) {
         if (change.wasAdded()) {
            change.getAddedSubList().forEach(location -> this.addLocation(location));
         } else if (change.wasRemoved()) {
            change.getRemoved().forEach(location -> this.removeLocation(location));
         }
      }
   };
   private final WeakListChangeListener weakLocationsListener = new WeakListChangeListener(this.locationsListener);
   private final ListChangeListener<? super WorldMapView.Country> countrySelectionListener = change -> {
      while (change.next()) {
         if (change.wasAdded()) {
            change.getAddedSubList()
               .forEach(countryx -> this.countryViewMap.get(countryx).forEach(path -> path.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true)));
         } else if (change.wasRemoved()) {
            change.getRemoved()
               .forEach(countryx -> this.countryViewMap.get(countryx).forEach(path -> path.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false)));
         }
      }
   };
   private final WeakListChangeListener weakCountrySelectionListener = new WeakListChangeListener(this.countrySelectionListener);
   private final ListChangeListener<? super WorldMapView.Location> locationSelectionListener = change -> {
      while (change.next()) {
         if (change.wasAdded()) {
            change.getAddedSubList().forEach(location -> ((Node)this.locationMap.get(location)).pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true));
         } else if (change.wasRemoved()) {
            change.getRemoved().forEach(location -> ((Node)this.locationMap.get(location)).pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false));
         }
      }
   };
   private final WeakListChangeListener weakLocationSelectionListener = new WeakListChangeListener(this.locationSelectionListener);

   public WorldMapViewSkin(WorldMapView view) {
      super(view);
      this.locationMap = FXCollections.observableHashMap();
      this.group = new Group();
      this.group.setManaged(false);
      this.group.setAutoSizeChildren(false);
      this.locationsGroup = new Group();
      this.locationsGroup.setManaged(false);
      this.locationsGroup.visibleProperty().bind(view.showLocationsProperty());
      this.locationsGroup.setAutoSizeChildren(false);
      this.countryPane = new Pane();
      this.countryPane.getChildren().add(this.group);
      view.getLocations().addListener(this.locationsListener);
      ListChangeListener<? super WorldMapView.Country> countriesListener = change -> this.buildView();
      view.getCountries().addListener(countriesListener);
      this.locationMap.addListener(change -> {
         if (change.wasRemoved()) {
            this.locationsGroup.getChildren().remove(change.getValueRemoved());
         }
      });
      BorderPane borderPane = new BorderPane();
      borderPane.setCenter(this.countryPane);
      this.getChildren().add(borderPane);
      view.zoomFactorProperty().addListener(it -> view.requestLayout());
      Properties mapData = this.loadData();

      for (WorldMapView.Country country : WorldMapView.Country.values()) {
         String countryData = (String)mapData.get(country.name());
         if (countryData == null) {
            System.out.println("Missing SVG path for country " + country.getLocale().getDisplayCountry() + " (" + country + ")");
         } else {
            StringTokenizer st = new StringTokenizer(countryData, ";");
            List<String> paths = new ArrayList<>();

            while (st.hasMoreTokens()) {
               paths.add(st.nextToken());
            }

            this.countryPathMap.put(country, paths);
         }
      }

      this.buildView();
      view.getSelectedCountries().addListener(this.weakCountrySelectionListener);
      view.selectedCountriesProperty().addListener(it -> view.getSelectedCountries().addListener(this.weakCountrySelectionListener));
      view.getSelectedLocations().addListener(this.weakLocationSelectionListener);
      view.selectedLocationsProperty().addListener(it -> view.getSelectedLocations().addListener(this.weakLocationSelectionListener));
      view.getLocations().addListener(this.weakLocationsListener);
      view.locationsProperty().addListener(it -> view.getLocations().addListener(this.weakLocationsListener));
      view.getLocations().forEach(location -> this.addLocation(location));
      view.addEventHandler(ScrollEvent.SCROLL, evt -> evt.consume());
      view.addEventHandler(ZoomEvent.ZOOM, evt -> {
         double factor = evt.getZoomFactor();
         view.setZoomFactor(view.getZoomFactor() * factor);
         evt.consume();
      });
      view.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
         this.dragX = evt.getX();
         this.dragY = evt.getY();
      });
      view.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
         double deltaX = evt.getX() - this.dragX;
         double deltaY = evt.getY() - this.dragY;
         this.group.setTranslateX(this.group.getTranslateX() + deltaX);
         this.group.setTranslateY(this.group.getTranslateY() + deltaY);
         this.dragX = evt.getX();
         this.dragY = evt.getY();
      });
      view.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
         if (evt.getClickCount() == 2) {
            view.setZoomFactor(1.0);
            this.group.setTranslateX(0.0);
            this.group.setTranslateY(0.0);
         } else if (evt.getButton().equals(MouseButton.PRIMARY)) {
            EventTarget target = evt.getTarget();
            if (target instanceof WorldMapView.CountryView) {
               WorldMapView.CountryView path = (WorldMapView.CountryView)target;
               WorldMapView.Country countryx = path.getCountry();
               boolean wasSelected = view.getSelectedCountries().contains(countryx);
               if (view.getCountrySelectionMode().equals(WorldMapView.SelectionMode.SINGLE) || !evt.isShortcutDown() && !evt.isShiftDown()) {
                  view.getSelectedCountries().clear();
               }

               if (wasSelected) {
                  view.getSelectedCountries().remove(countryx);
               } else {
                  view.getSelectedCountries().add(countryx);
               }
            } else if (target.equals(this.countryPane)) {
               view.getSelectedCountries().clear();
            } else {
               for (WorldMapView.Location location : this.locationMap.keySet()) {
                  Node node = (Node)this.locationMap.get(location);
                  if (target.equals(node)) {
                     boolean wasSelectedx = view.getSelectedLocations().contains(location);
                     if (view.getLocationSelectionMode().equals(WorldMapView.SelectionMode.SINGLE) || !evt.isShortcutDown() && !evt.isShiftDown()) {
                        view.getSelectedLocations().clear();
                     }

                     if (wasSelectedx) {
                        view.getSelectedLocations().remove(location);
                     } else {
                        view.getSelectedLocations().add(location);
                     }
                     break;
                  }
               }
            }
         }
      });
      Rectangle clip = new Rectangle();
      clip.widthProperty().bind(view.widthProperty());
      clip.heightProperty().bind(view.heightProperty());
      view.setClip(clip);
      view.countryViewFactoryProperty().addListener(it -> this.buildView());
      view.locationViewFactoryProperty().addListener(it -> this.buildView());
   }

   private Point2D getLocationCoordinates(WorldMapView.Location location) {
      double x = (location.getLongitude() + 180.0) * 2.8027777777777776 + MAP_OFFSET_X;
      double y = 332.5 - 1009.0 * Math.log(Math.tan((Math.PI / 4) + Math.toRadians(location.getLatitude()) / 2.0)) / (Math.PI * 2) + MAP_OFFSET_Y;
      return new Point2D(x, y);
   }

   private void addLocation(WorldMapView.Location location) {
      Point2D coordinates = this.getLocationCoordinates(location);
      Callback<WorldMapView.Location, Node> locationViewFactory = ((WorldMapView)this.getSkinnable()).getLocationViewFactory();
      Node view = (Node)locationViewFactory.call(location);
      if (view == null) {
         throw new IllegalArgumentException("location view factory returned NULL");
      } else {
         view.getStyleClass().add("location");
         view.setManaged(false);
         this.locationsGroup.getChildren().add(view);
         view.applyCss();
         view.resizeRelocate(coordinates.getX(), coordinates.getY(), view.prefWidth(-1.0), view.prefHeight(-1.0));
         this.locationMap.put(location, view);
      }
   }

   private void removeLocation(WorldMapView.Location location) {
      this.locationMap.remove(location);
   }

   private void buildView() {
      this.group.getChildren().clear();
      this.locationsGroup.getChildren().clear();
      if (Double.compare(((WorldMapView)this.getSkinnable()).getPrefWidth(), 0.0) <= 0
         || Double.compare(((WorldMapView)this.getSkinnable()).getPrefHeight(), 0.0) <= 0
         || Double.compare(((WorldMapView)this.getSkinnable()).getWidth(), 0.0) <= 0
         || Double.compare(((WorldMapView)this.getSkinnable()).getHeight(), 0.0) <= 0) {
         if (((WorldMapView)this.getSkinnable()).getPrefWidth() > 0.0 && ((WorldMapView)this.getSkinnable()).getPrefHeight() > 0.0) {
            ((WorldMapView)this.getSkinnable())
               .setPrefSize(((WorldMapView)this.getSkinnable()).getPrefWidth(), ((WorldMapView)this.getSkinnable()).getPrefHeight());
         } else {
            ((WorldMapView)this.getSkinnable()).setPrefSize(1009.0, 665.0);
         }
      }

      Callback<WorldMapView.Country, WorldMapView.CountryView> factory = ((WorldMapView)this.getSkinnable()).getCountryViewFactory();

      for (WorldMapView.Country country : WorldMapView.Country.values()) {
         if (((WorldMapView)this.getSkinnable()).getCountries().isEmpty() || ((WorldMapView)this.getSkinnable()).getCountries().contains(country)) {
            List<WorldMapView.CountryView> countryViews = new ArrayList<>();

            for (String svgPath : this.countryPathMap.get(country)) {
               WorldMapView.CountryView view = (WorldMapView.CountryView)factory.call(country);
               if (view != null) {
                  view.setContent(svgPath);
                  view.getStyleClass().add(0, "country");
                  this.group.getChildren().addAll(new Node[]{view});
                  countryViews.add(view);
               }
            }

            this.countryViewMap.put(country, countryViews);
         }
      }

      for (WorldMapView.Location location : this.locationMap.keySet()) {
         Point2D coordinates = this.getLocationCoordinates(location);
         if (this.group.getLayoutBounds().contains(coordinates)) {
            this.locationsGroup.getChildren().add((Node)this.locationMap.get(location));
         }
      }

      this.group.getChildren().add(this.locationsGroup);
      ((WorldMapView)this.getSkinnable()).requestLayout();
   }

   protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
      super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
      double prefWidth = this.group.prefWidth(-1.0);
      double prefHeight = this.group.prefHeight(-1.0);
      double scaleX = contentWidth / prefWidth;
      double scaleY = contentHeight / prefHeight;
      double scale = Math.min(scaleX, scaleY) * ((WorldMapView)this.getSkinnable()).getZoomFactor();
      this.group.setTranslateX(-this.group.getLayoutBounds().getMinX());
      this.group.setTranslateY(-this.group.getLayoutBounds().getMinY());
      this.group.setScaleX(scale);
      this.group.setScaleY(scale);
      this.group.setLayoutX((contentWidth - prefWidth) / 2.0);
      this.group.setLayoutY((contentHeight - prefHeight) / 2.0);
   }

   protected Properties loadData() {
      Properties mapData = new Properties();

      try {
         mapData.load(WorldMapView.class.getResourceAsStream("worldmap-small.properties"));
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return mapData;
   }
}
