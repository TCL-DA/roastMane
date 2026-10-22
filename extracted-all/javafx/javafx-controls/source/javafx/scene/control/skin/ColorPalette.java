package javafx.scene.control.skin;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.control.CustomColorDialog;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import com.sun.javafx.util.Utils;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

class ColorPalette extends Region {
   private static final int SQUARE_SIZE = 15;
   ColorPalette.ColorPickerGrid colorPickerGrid;
   final Hyperlink customColorLink = new Hyperlink(Properties.getColorPickerString("customColorLink"));
   CustomColorDialog customColorDialog = null;
   private ColorPicker colorPicker;
   private final GridPane standardColorGrid = new GridPane();
   private final GridPane customColorGrid = new GridPane();
   private final Separator separator = new Separator();
   private final Label customColorLabel = new Label(Properties.getColorPickerString("customColorLabel"));
   private PopupControl popupControl;
   private ColorPalette.ColorSquare focusedSquare;
   private ContextMenu contextMenu = null;
   private Color mouseDragColor = null;
   private boolean dragDetected = false;
   private int customColorNumber = 0;
   private int customColorRows = 0;
   private int customColorLastRowLength = 0;
   private final ColorPalette.ColorSquare hoverSquare = new ColorPalette.ColorSquare();
   private static final int NUM_OF_COLUMNS = 12;
   private static double[] RAW_VALUES = new double[]{
      255.0,
      255.0,
      255.0,
      242.0,
      242.0,
      242.0,
      230.0,
      230.0,
      230.0,
      204.0,
      204.0,
      204.0,
      179.0,
      179.0,
      179.0,
      153.0,
      153.0,
      153.0,
      128.0,
      128.0,
      128.0,
      102.0,
      102.0,
      102.0,
      77.0,
      77.0,
      77.0,
      51.0,
      51.0,
      51.0,
      26.0,
      26.0,
      26.0,
      0.0,
      0.0,
      0.0,
      0.0,
      51.0,
      51.0,
      0.0,
      26.0,
      128.0,
      26.0,
      0.0,
      104.0,
      51.0,
      0.0,
      51.0,
      77.0,
      0.0,
      26.0,
      153.0,
      0.0,
      0.0,
      153.0,
      51.0,
      0.0,
      153.0,
      77.0,
      0.0,
      153.0,
      102.0,
      0.0,
      153.0,
      153.0,
      0.0,
      102.0,
      102.0,
      0.0,
      0.0,
      51.0,
      0.0,
      26.0,
      77.0,
      77.0,
      26.0,
      51.0,
      153.0,
      51.0,
      26.0,
      128.0,
      77.0,
      26.0,
      77.0,
      102.0,
      26.0,
      51.0,
      179.0,
      26.0,
      26.0,
      179.0,
      77.0,
      26.0,
      179.0,
      102.0,
      26.0,
      179.0,
      128.0,
      26.0,
      179.0,
      179.0,
      26.0,
      128.0,
      128.0,
      26.0,
      26.0,
      77.0,
      26.0,
      51.0,
      102.0,
      102.0,
      51.0,
      77.0,
      179.0,
      77.0,
      51.0,
      153.0,
      102.0,
      51.0,
      102.0,
      128.0,
      51.0,
      77.0,
      204.0,
      51.0,
      51.0,
      204.0,
      102.0,
      51.0,
      204.0,
      128.0,
      51.0,
      204.0,
      153.0,
      51.0,
      204.0,
      204.0,
      51.0,
      153.0,
      153.0,
      51.0,
      51.0,
      102.0,
      51.0,
      77.0,
      128.0,
      128.0,
      77.0,
      102.0,
      204.0,
      102.0,
      77.0,
      179.0,
      128.0,
      77.0,
      128.0,
      153.0,
      77.0,
      102.0,
      230.0,
      77.0,
      77.0,
      230.0,
      128.0,
      77.0,
      230.0,
      153.0,
      77.0,
      230.0,
      179.0,
      77.0,
      230.0,
      230.0,
      77.0,
      179.0,
      179.0,
      77.0,
      77.0,
      128.0,
      77.0,
      102.0,
      153.0,
      153.0,
      102.0,
      128.0,
      230.0,
      128.0,
      102.0,
      204.0,
      153.0,
      102.0,
      153.0,
      179.0,
      102.0,
      128.0,
      255.0,
      102.0,
      102.0,
      255.0,
      153.0,
      102.0,
      255.0,
      179.0,
      102.0,
      255.0,
      204.0,
      102.0,
      255.0,
      255.0,
      77.0,
      204.0,
      204.0,
      102.0,
      102.0,
      153.0,
      102.0,
      128.0,
      179.0,
      179.0,
      128.0,
      153.0,
      255.0,
      153.0,
      128.0,
      230.0,
      179.0,
      128.0,
      179.0,
      204.0,
      128.0,
      153.0,
      255.0,
      128.0,
      128.0,
      255.0,
      153.0,
      128.0,
      255.0,
      204.0,
      128.0,
      255.0,
      230.0,
      102.0,
      255.0,
      255.0,
      102.0,
      230.0,
      230.0,
      128.0,
      128.0,
      179.0,
      128.0,
      153.0,
      204.0,
      204.0,
      153.0,
      179.0,
      255.0,
      179.0,
      153.0,
      255.0,
      204.0,
      153.0,
      204.0,
      230.0,
      153.0,
      179.0,
      255.0,
      153.0,
      153.0,
      255.0,
      179.0,
      128.0,
      255.0,
      204.0,
      153.0,
      255.0,
      230.0,
      128.0,
      255.0,
      255.0,
      128.0,
      230.0,
      230.0,
      153.0,
      153.0,
      204.0,
      153.0,
      179.0,
      230.0,
      230.0,
      179.0,
      204.0,
      255.0,
      204.0,
      179.0,
      255.0,
      230.0,
      179.0,
      230.0,
      230.0,
      179.0,
      204.0,
      255.0,
      179.0,
      179.0,
      255.0,
      179.0,
      153.0,
      255.0,
      230.0,
      179.0,
      255.0,
      230.0,
      153.0,
      255.0,
      255.0,
      153.0,
      230.0,
      230.0,
      179.0,
      179.0,
      230.0,
      179.0,
      204.0,
      255.0,
      255.0,
      204.0,
      230.0,
      255.0,
      230.0,
      204.0,
      255.0,
      255.0,
      204.0,
      255.0,
      255.0,
      204.0,
      230.0,
      255.0,
      204.0,
      204.0,
      255.0,
      204.0,
      179.0,
      255.0,
      230.0,
      204.0,
      255.0,
      255.0,
      179.0,
      255.0,
      255.0,
      204.0,
      230.0,
      230.0,
      204.0,
      204.0,
      255.0,
      204.0
   };
   private static final int NUM_OF_COLORS = RAW_VALUES.length / 3;
   private static final int NUM_OF_ROWS = NUM_OF_COLORS / 12;

   public ColorPalette(final ColorPicker var1) {
      this.getStyleClass().add("color-palette-region");
      this.colorPicker = var1;
      this.colorPickerGrid = new ColorPalette.ColorPickerGrid();
      ((Node)this.colorPickerGrid.getChildren().get(0)).requestFocus();
      this.customColorLabel.setAlignment(Pos.CENTER_LEFT);
      this.customColorLink.setPrefWidth(this.colorPickerGrid.prefWidth(-1.0));
      this.customColorLink.setAlignment(Pos.CENTER);
      this.customColorLink.setFocusTraversable(true);
      this.customColorLink.setVisited(true);
      this.customColorLink
         .setOnAction(
            new EventHandler<ActionEvent>() {
               public void handle(ActionEvent var1x) {
                  if (ColorPalette.this.customColorDialog == null) {
                     ColorPalette.this.customColorDialog = new CustomColorDialog(ColorPalette.this.popupControl);
                     ColorPalette.this.customColorDialog
                        .customColorProperty()
                        .addListener((var2, var3, var4) -> var1.setValue((Color)ColorPalette.this.customColorDialog.customColorProperty().get()));
                     ColorPalette.this.customColorDialog.setOnSave(() -> {
                        Color var2 = (Color)ColorPalette.this.customColorDialog.customColorProperty().get();
                        ColorPalette.this.buildCustomColors();
                        var1.getCustomColors().add(var2);
                        ColorPalette.this.updateSelection(var2);
                        Event.fireEvent(var1, new ActionEvent());
                        var1.hide();
                     });
                     ColorPalette.this.customColorDialog.setOnUse(() -> {
                        Event.fireEvent(var1, new ActionEvent());
                        var1.hide();
                     });
                  }

                  ColorPalette.this.customColorDialog.setCurrentColor((Color)var1.valueProperty().get());
                  if (ColorPalette.this.popupControl != null) {
                     ColorPalette.this.popupControl.setAutoHide(false);
                  }

                  ColorPalette.this.customColorDialog.show();
                  ColorPalette.this.customColorDialog.setOnHidden(var1xxx -> {
                     if (ColorPalette.this.popupControl != null) {
                        ColorPalette.this.popupControl.setAutoHide(true);
                     }
                  });
               }
            }
         );
      this.initNavigation();
      this.buildStandardColors();
      this.standardColorGrid.getStyleClass().add("color-picker-grid");
      this.standardColorGrid.setVisible(true);
      this.customColorGrid.getStyleClass().add("color-picker-grid");
      this.customColorGrid.setVisible(false);
      this.buildCustomColors();
      var1.getCustomColors().addListener(new ListChangeListener<Color>() {
         public void onChanged(Change<? extends Color> var1) {
            ColorPalette.this.buildCustomColors();
         }
      });
      VBox var2 = new VBox();
      var2.getStyleClass().add("color-palette");
      var2.getChildren()
         .addAll(new Node[]{this.standardColorGrid, this.colorPickerGrid, this.customColorLabel, this.customColorGrid, this.separator, this.customColorLink});
      this.hoverSquare.setMouseTransparent(true);
      this.hoverSquare.getStyleClass().addAll(new String[]{"hover-square"});
      this.setFocusedSquare(null);
      this.getChildren().addAll(new Node[]{var2, this.hoverSquare});
   }

   private void setFocusedSquare(ColorPalette.ColorSquare var1) {
      if (var1 != this.focusedSquare) {
         this.focusedSquare = var1;
         this.hoverSquare.setVisible(this.focusedSquare != null);
         if (this.focusedSquare != null) {
            if (!this.focusedSquare.isFocused()) {
               this.focusedSquare.requestFocus();
            }

            this.hoverSquare.rectangle.setFill(this.focusedSquare.rectangle.getFill());
            Bounds var2 = var1.localToScene(var1.getLayoutBounds());
            double var3 = var2.getMinX();
            double var5 = var2.getMinY();
            double var9 = this.hoverSquare.getScaleX() == 1.0 ? 0.0 : this.hoverSquare.getWidth() / 4.0;
            double var7;
            if (this.colorPicker.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
               var3 = this.focusedSquare.getLayoutX();
               var7 = -this.focusedSquare.getWidth() + var9;
            } else {
               var7 = this.focusedSquare.getWidth() / 2.0 + var9;
            }

            this.hoverSquare.setLayoutX(this.snapPositionX(var3) - var7);
            this.hoverSquare
               .setLayoutY(
                  this.snapPositionY(var5)
                     - this.focusedSquare.getHeight() / 2.0
                     + (this.hoverSquare.getScaleY() == 1.0 ? 0.0 : this.focusedSquare.getHeight() / 4.0)
               );
         }
      }
   }

   private void buildStandardColors() {
      Color[] var1 = new Color[]{
         Color.AQUA,
         Color.TEAL,
         Color.BLUE,
         Color.NAVY,
         Color.FUCHSIA,
         Color.PURPLE,
         Color.RED,
         Color.MAROON,
         Color.YELLOW,
         Color.OLIVE,
         Color.GREEN,
         Color.LIME
      };
      this.standardColorGrid.getChildren().clear();

      for (int var2 = 0; var2 < 12; var2++) {
         this.standardColorGrid.add(new ColorPalette.ColorSquare(var1[var2], var2, ColorPalette.ColorType.STANDARD), var2, 0);
      }
   }

   private void buildCustomColors() {
      ObservableList var1 = this.colorPicker.getCustomColors();
      this.customColorNumber = var1.size();
      this.customColorGrid.getChildren().clear();
      if (var1.isEmpty()) {
         this.customColorLabel.setVisible(false);
         this.customColorLabel.setManaged(false);
         this.customColorGrid.setVisible(false);
         this.customColorGrid.setManaged(false);
      } else {
         this.customColorLabel.setVisible(true);
         this.customColorLabel.setManaged(true);
         this.customColorGrid.setVisible(true);
         this.customColorGrid.setManaged(true);
         if (this.contextMenu == null) {
            MenuItem var2 = new MenuItem(Properties.getColorPickerString("removeColor"));
            var2.setOnAction(var2x -> {
               ColorPalette.ColorSquare var3x = (ColorPalette.ColorSquare)this.contextMenu.getOwnerNode();
               var1.remove(var3x.rectangle.getFill());
               this.buildCustomColors();
            });
            this.contextMenu = new ContextMenu(var2);
         }

         int var9 = 0;
         int var3 = 0;
         int var4 = var1.size() % 12;
         int var5 = var4 == 0 ? 0 : 12 - var4;
         this.customColorLastRowLength = var4 == 0 ? 12 : var4;

         for (int var6 = 0; var6 < var1.size(); var6++) {
            Color var7 = (Color)var1.get(var6);
            ColorPalette.ColorSquare var8 = new ColorPalette.ColorSquare(var7, var6, ColorPalette.ColorType.CUSTOM);
            var8.addEventHandler(KeyEvent.KEY_PRESSED, var3x -> {
               if (var3x.getCode() == KeyCode.DELETE) {
                  var1.remove(var8.rectangle.getFill());
                  this.buildCustomColors();
               }
            });
            this.customColorGrid.add(var8, var9, var3);
            if (++var9 == 12) {
               var9 = 0;
               var3++;
            }
         }

         for (int var10 = 0; var10 < var5; var10++) {
            ColorPalette.ColorSquare var11 = new ColorPalette.ColorSquare();
            var11.setDisable(true);
            this.customColorGrid.add(var11, var9, var3);
            var9++;
         }

         this.customColorRows = var3 + 1;
         this.requestLayout();
      }
   }

   private void initNavigation() {
      this.setOnKeyPressed(var1 -> {
         switch (var1.getCode()) {
            case SPACE:
            case ENTER:
               this.processSelectKey(var1);
               var1.consume();
         }
      });
      ParentHelper.setTraversalEngine(
         this,
         new ParentTraversalEngine(
            this,
            new Algorithm() {
               public Node select(Node var1, Direction var2, TraversalContext var3) {
                  Node var4 = var3.selectInSubtree(var3.getRoot(), var1, var2);
                  switch (var2) {
                     case NEXT:
                     case NEXT_IN_LINE:
                     case PREVIOUS:
                        return var4;
                     case LEFT:
                     case RIGHT:
                     case UP:
                     case DOWN:
                        if (var1 instanceof ColorPalette.ColorSquare) {
                           Node var5 = this.processArrow((ColorPalette.ColorSquare)var1, var2);
                           return var5 != null ? var5 : var4;
                        }

                        return var4;
                     default:
                        return null;
                  }
               }

               private Node processArrow(ColorPalette.ColorSquare var1, Direction var2) {
                  int var3 = 0;
                  int var4 = 0;
                  if (var1.colorType == ColorPalette.ColorType.STANDARD) {
                     var3 = 0;
                     var4 = var1.index;
                  } else {
                     var3 = var1.index / 12;
                     var4 = var1.index % 12;
                  }

                  var2 = var2.getDirectionForNodeOrientation(ColorPalette.this.colorPicker.getEffectiveNodeOrientation());
                  if (this.isAtBorder(var2, var3, var4, var1.colorType == ColorPalette.ColorType.CUSTOM)) {
                     int var5 = var3;
                     int var6 = var4;
                     boolean var7 = var1.colorType == ColorPalette.ColorType.CUSTOM;
                     boolean var8 = var1.colorType == ColorPalette.ColorType.STANDARD;
                     switch (var2) {
                        case LEFT:
                        case RIGHT:
                           if (var1.colorType == ColorPalette.ColorType.STANDARD) {
                              var5 = 0;
                              var6 = var2 == Direction.LEFT ? 11 : 0;
                           } else if (var1.colorType == ColorPalette.ColorType.CUSTOM) {
                              var5 = Math.floorMod(var2 == Direction.LEFT ? var3 - 1 : var3 + 1, ColorPalette.this.customColorRows);
                              var6 = var2 == Direction.LEFT
                                 ? (var5 == ColorPalette.this.customColorRows - 1 ? ColorPalette.this.customColorLastRowLength - 1 : 11)
                                 : 0;
                           } else {
                              var5 = Math.floorMod(var2 == Direction.LEFT ? var3 - 1 : var3 + 1, ColorPalette.NUM_OF_ROWS);
                              var6 = var2 == Direction.LEFT ? 11 : 0;
                           }
                           break;
                        case UP:
                           if (var1.colorType == ColorPalette.ColorType.NORMAL && var3 == 0) {
                              var8 = true;
                           }
                           break;
                        case DOWN:
                           if (ColorPalette.this.customColorNumber <= 0) {
                              return null;
                           }

                           var7 = true;
                           var5 = 0;
                           var6 = ColorPalette.this.customColorRows > 1 ? var4 : Math.min(ColorPalette.this.customColorLastRowLength - 1, var4);
                     }

                     if (var7) {
                        return (Node)ColorPalette.this.customColorGrid.getChildren().get(var5 * 12 + var6);
                     } else {
                        return var8
                           ? (Node)ColorPalette.this.standardColorGrid.getChildren().get(var6)
                           : (Node)ColorPalette.this.colorPickerGrid.getChildren().get(var5 * 12 + var6);
                     }
                  } else {
                     return null;
                  }
               }

               private boolean isAtBorder(Direction var1, int var2, int var3, boolean var4) {
                  switch (var1) {
                     case LEFT:
                        return var3 == 0;
                     case RIGHT:
                        return var4 && var2 == ColorPalette.this.customColorRows - 1 ? var3 == ColorPalette.this.customColorLastRowLength - 1 : var3 == 11;
                     case UP:
                        return !var4 && var2 == 0;
                     case DOWN:
                        return !var4 && var2 == ColorPalette.NUM_OF_ROWS - 1;
                     default:
                        return false;
                  }
               }

               public Node selectFirst(TraversalContext var1) {
                  return (Node)ColorPalette.this.standardColorGrid.getChildren().get(0);
               }

               public Node selectLast(TraversalContext var1) {
                  return ColorPalette.this.customColorLink;
               }
            }
         )
      );
   }

   private void processSelectKey(KeyEvent var1) {
      if (this.focusedSquare != null) {
         this.focusedSquare.selectColor(var1);
      }
   }

   public void setPopupControl(PopupControl var1) {
      this.popupControl = var1;
   }

   public ColorPalette.ColorPickerGrid getColorGrid() {
      return this.colorPickerGrid;
   }

   public boolean isCustomColorDialogShowing() {
      return this.customColorDialog != null ? this.customColorDialog.isVisible() : false;
   }

   public void updateSelection(Color var1) {
      this.setFocusedSquare(null);

      for (GridPane var4 : List.of(this.standardColorGrid, this.colorPickerGrid, this.customColorGrid)) {
         ColorPalette.ColorSquare var5 = this.findColorSquare(var4, var1);
         if (var5 != null) {
            this.setFocusedSquare(var5);
            return;
         }
      }
   }

   private ColorPalette.ColorSquare findColorSquare(GridPane var1, Color var2) {
      for (Node var4 : var1.getChildren()) {
         ColorPalette.ColorSquare var5 = (ColorPalette.ColorSquare)var4;
         if (var5.rectangle.getFill().equals(var2)) {
            return var5;
         }
      }

      return null;
   }

   class ColorPickerGrid extends GridPane {
      private final List<ColorPalette.ColorSquare> squares;

      public ColorPickerGrid() {
         this.getStyleClass().add("color-picker-grid");
         this.setId("ColorCustomizerColorGrid");
         int var2 = 0;
         int var3 = 0;
         this.squares = FXCollections.observableArrayList();
         int var4 = ColorPalette.RAW_VALUES.length / 3;
         Color[] var5 = new Color[var4];

         for (int var6 = 0; var6 < var4; var6++) {
            var5[var6] = new Color(
               ColorPalette.RAW_VALUES[var6 * 3] / 255.0, ColorPalette.RAW_VALUES[var6 * 3 + 1] / 255.0, ColorPalette.RAW_VALUES[var6 * 3 + 2] / 255.0, 1.0
            );
            ColorPalette.ColorSquare var7 = ColorPalette.this.new ColorSquare(var5[var6], var6);
            this.squares.add(var7);
         }

         for (ColorPalette.ColorSquare var9 : this.squares) {
            this.add(var9, var2, var3);
            if (++var2 == 12) {
               var2 = 0;
               var3++;
            }
         }

         this.setOnMouseDragged(var1x -> {
            if (!ColorPalette.this.dragDetected) {
               ColorPalette.this.dragDetected = true;
               ColorPalette.this.mouseDragColor = ColorPalette.this.colorPicker.getValue();
            }

            int var2x = Utils.clamp(0, (int)var1x.getX() / 16, 11);
            int var3x = Utils.clamp(0, (int)var1x.getY() / 16, ColorPalette.NUM_OF_ROWS - 1);
            int var4x = var2x + var3x * 12;
            ColorPalette.this.colorPicker.setValue((Color)this.squares.get(var4x).rectangle.getFill());
            ColorPalette.this.updateSelection(ColorPalette.this.colorPicker.getValue());
         });
         this.addEventHandler(MouseEvent.MOUSE_RELEASED, var1x -> {
            if (ColorPalette.this.colorPickerGrid.getBoundsInLocal().contains(var1x.getX(), var1x.getY())) {
               ColorPalette.this.updateSelection(ColorPalette.this.colorPicker.getValue());
               ColorPalette.this.colorPicker.fireEvent(new ActionEvent());
               ColorPalette.this.colorPicker.hide();
            } else if (ColorPalette.this.mouseDragColor != null) {
               ColorPalette.this.colorPicker.setValue(ColorPalette.this.mouseDragColor);
               ColorPalette.this.updateSelection(ColorPalette.this.mouseDragColor);
            }

            ColorPalette.this.dragDetected = false;
         });
      }

      public List<ColorPalette.ColorSquare> getSquares() {
         return this.squares;
      }

      protected double computePrefWidth(double var1) {
         return 192.0;
      }

      protected double computePrefHeight(double var1) {
         return (double)(16 * ColorPalette.NUM_OF_ROWS);
      }
   }

   class ColorSquare extends StackPane {
      Rectangle rectangle;
      int index;
      boolean isEmpty;
      ColorPalette.ColorType colorType = ColorPalette.ColorType.NORMAL;

      public ColorSquare() {
         this(null, -1, ColorPalette.ColorType.NORMAL);
      }

      public ColorSquare(Color var2, int var3) {
         this(var2, var3, ColorPalette.ColorType.NORMAL);
      }

      public ColorSquare(Color var2, int var3, ColorPalette.ColorType var4) {
         this.getStyleClass().add("color-square");
         if (var2 != null) {
            this.setFocusTraversable(true);
            this.focusedProperty().addListener((var1x, var2x, var3x) -> ColorPalette.this.setFocusedSquare(var3x ? this : null));
            this.addEventHandler(MouseEvent.MOUSE_ENTERED, var1x -> ColorPalette.this.setFocusedSquare(this));
            this.addEventHandler(MouseEvent.MOUSE_EXITED, var1x -> ColorPalette.this.setFocusedSquare(null));
            this.addEventHandler(
               MouseEvent.MOUSE_RELEASED,
               var1x -> {
                  if (!ColorPalette.this.dragDetected && var1x.getButton() == MouseButton.PRIMARY && var1x.getClickCount() == 1) {
                     if (!this.isEmpty) {
                        Color var2x = (Color)this.rectangle.getFill();
                        ColorPalette.this.colorPicker.setValue(var2x);
                        ColorPalette.this.colorPicker.fireEvent(new ActionEvent());
                        ColorPalette.this.updateSelection(var2x);
                        var1x.consume();
                     }

                     ColorPalette.this.colorPicker.hide();
                  } else if ((var1x.getButton() == MouseButton.SECONDARY || var1x.getButton() == MouseButton.MIDDLE)
                     && this.colorType == ColorPalette.ColorType.CUSTOM
                     && ColorPalette.this.contextMenu != null) {
                     if (!ColorPalette.this.contextMenu.isShowing()) {
                        ColorPalette.this.contextMenu.show(this, Side.RIGHT, 0.0, 0.0);
                        com.sun.javafx.scene.control.skin.Utils.addMnemonics(
                           ColorPalette.this.contextMenu, this.getScene(), NodeHelper.isShowMnemonics(ColorPalette.this.colorPicker)
                        );
                     } else {
                        ColorPalette.this.contextMenu.hide();
                        com.sun.javafx.scene.control.skin.Utils.removeMnemonics(ColorPalette.this.contextMenu, this.getScene());
                     }
                  }
               }
            );
         }

         this.index = var3;
         this.colorType = var4;
         this.rectangle = new Rectangle(15.0, 15.0);
         if (var2 == null) {
            this.rectangle.setFill(Color.WHITE);
            this.isEmpty = true;
         } else {
            this.rectangle.setFill(var2);
         }

         this.rectangle.setStrokeType(StrokeType.INSIDE);
         String var5 = ColorPickerSkin.tooltipString(var2);
         Tooltip.install(this, new Tooltip(var5 == null ? "" : var5));
         this.rectangle.getStyleClass().add("color-rect");
         this.getChildren().add(this.rectangle);
      }

      public void selectColor(KeyEvent var1) {
         if (this.rectangle.getFill() != null) {
            if (this.rectangle.getFill() instanceof Color) {
               ColorPalette.this.colorPicker.setValue((Color)this.rectangle.getFill());
               ColorPalette.this.colorPicker.fireEvent(new ActionEvent());
            }

            var1.consume();
         }

         ColorPalette.this.colorPicker.hide();
      }
   }

   static enum ColorType {
      NORMAL,
      STANDARD,
      CUSTOM;
   }
}
