package javafx.scene.web;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.control.skin.FXVK;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import com.sun.javafx.scene.web.behavior.HTMLEditorBehavior;
import com.sun.javafx.webkit.Accessor;
import com.sun.webkit.WebPage;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.css.StyleableProperty;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

public class HTMLEditorSkin extends SkinBase<HTMLEditor> {
   private GridPane gridPane;
   private ToolBar toolbar1;
   private ToolBar toolbar2;
   private Button cutButton;
   private Button copyButton;
   private Button pasteButton;
   private Button insertHorizontalRuleButton;
   private ToggleGroup alignmentToggleGroup;
   private ToggleButton alignLeftButton;
   private ToggleButton alignCenterButton;
   private ToggleButton alignRightButton;
   private ToggleButton alignJustifyButton;
   private ToggleButton bulletsButton;
   private ToggleButton numbersButton;
   private Button indentButton;
   private Button outdentButton;
   private ComboBox<String> formatComboBox;
   private Map<String, String> formatStyleMap;
   private Map<String, String> styleFormatMap;
   private ComboBox<String> fontFamilyComboBox;
   private ComboBox<String> fontSizeComboBox;
   private Map<String, String> fontSizeMap;
   private Map<String, String> sizeFontMap;
   private ToggleButton boldButton;
   private ToggleButton italicButton;
   private ToggleButton underlineButton;
   private ToggleButton strikethroughButton;
   private ColorPicker fgColorButton;
   private ColorPicker bgColorButton;
   private WebView webView;
   private WebPage webPage;
   private ParentTraversalEngine engine;
   private boolean resetToolbarState = false;
   private String cachedHTMLText = "<html><head></head><body contenteditable=\"true\"></body></html>";
   private ResourceBundle resources;
   private boolean enableAtomicityCheck = false;
   private int atomicityCount = 0;
   private boolean isFirstRun = true;
   private static final int FONT_FAMILY_MENUBUTTON_WIDTH = 150;
   private static final int FONT_FAMILY_MENU_WIDTH = 100;
   private static final int FONT_SIZE_MENUBUTTON_WIDTH = 80;
   private static final Color DEFAULT_BG_COLOR = Color.WHITE;
   private static final Color DEFAULT_FG_COLOR = Color.BLACK;
   private static final String FORMAT_PARAGRAPH = "<p>";
   private static final String FORMAT_HEADING_1 = "<h1>";
   private static final String FORMAT_HEADING_2 = "<h2>";
   private static final String FORMAT_HEADING_3 = "<h3>";
   private static final String FORMAT_HEADING_4 = "<h4>";
   private static final String FORMAT_HEADING_5 = "<h5>";
   private static final String FORMAT_HEADING_6 = "<h6>";
   private static final String SIZE_XX_SMALL = "1";
   private static final String SIZE_X_SMALL = "2";
   private static final String SIZE_SMALL = "3";
   private static final String SIZE_MEDIUM = "4";
   private static final String SIZE_LARGE = "5";
   private static final String SIZE_X_LARGE = "6";
   private static final String SIZE_XX_LARGE = "7";
   private static final String[][] DEFAULT_FORMAT_MAPPINGS = new String[][]{
      {"<p>", "", "3"},
      {"<h1>", HTMLEditorSkin.Command.BOLD.getCommand(), "6"},
      {"<h2>", HTMLEditorSkin.Command.BOLD.getCommand(), "5"},
      {"<h3>", HTMLEditorSkin.Command.BOLD.getCommand(), "4"},
      {"<h4>", HTMLEditorSkin.Command.BOLD.getCommand(), "3"},
      {"<h5>", HTMLEditorSkin.Command.BOLD.getCommand(), "2"},
      {"<h6>", HTMLEditorSkin.Command.BOLD.getCommand(), "1"}
   };
   private static PseudoClass CONTAINS_FOCUS_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("contains-focus");
   private ListChangeListener<Node> itemsListener = var1x -> {
      while (var1x.next()) {
         if (var1x.getRemovedSize() > 0) {
            for (Node var3x : var1x.getList()) {
               if (var3x instanceof WebView) {
                  this.webPage.dispose();
               }
            }
         }
      }
   };

   public HTMLEditorSkin(HTMLEditor var1) {
      super(var1);
      new HTMLEditorBehavior(var1);
      this.getChildren().clear();
      this.gridPane = new GridPane();
      this.gridPane.getStyleClass().add("grid");
      this.getChildren().addAll(new Node[]{this.gridPane});
      this.toolbar1 = new ToolBar();
      this.toolbar1.getStyleClass().add("top-toolbar");
      this.gridPane.add(this.toolbar1, 0, 0);
      this.toolbar2 = new ToolBar();
      this.toolbar2.getStyleClass().add("bottom-toolbar");
      this.gridPane.add(this.toolbar2, 0, 1);
      this.webView = new WebView();
      this.gridPane.add(this.webView, 0, 2);
      ColumnConstraints var3 = new ColumnConstraints();
      var3.setHgrow(Priority.ALWAYS);
      this.gridPane.getColumnConstraints().add(var3);
      this.webPage = Accessor.getPageFor(this.webView.getEngine());
      this.webView.addEventHandler(MouseEvent.MOUSE_RELEASED, var1x -> Platform.runLater(new Runnable() {
            @Override
            public void run() {
               HTMLEditorSkin.this.enableAtomicityCheck = true;
               HTMLEditorSkin.this.updateToolbarState(true);
               HTMLEditorSkin.this.enableAtomicityCheck = false;
            }
         }));
      this.webView
         .addEventHandler(
            KeyEvent.KEY_PRESSED,
            var1x -> {
               this.applyTextFormatting();
               if (var1x.getCode() != KeyCode.CONTROL && var1x.getCode() != KeyCode.META) {
                  if (var1x.getCode() == KeyCode.TAB && !var1x.isControlDown()) {
                     if (!var1x.isShiftDown()) {
                        if (!this.getCommandState(HTMLEditorSkin.Command.BULLETS.getCommand())
                           && !this.getCommandState(HTMLEditorSkin.Command.NUMBERS.getCommand())) {
                           this.executeCommand(HTMLEditorSkin.Command.INSERT_TAB.getCommand(), null);
                        } else {
                           this.executeCommand(HTMLEditorSkin.Command.INDENT.getCommand(), null);
                        }
                     } else if (this.getCommandState(HTMLEditorSkin.Command.BULLETS.getCommand())
                        || this.getCommandState(HTMLEditorSkin.Command.NUMBERS.getCommand())) {
                        this.executeCommand(HTMLEditorSkin.Command.OUTDENT.getCommand(), null);
                     }
                  } else if ((this.fgColorButton == null || !this.fgColorButton.isShowing()) && (this.bgColorButton == null || !this.bgColorButton.isShowing())
                     )
                   {
                     Platform.runLater(
                        () -> {
                           if (this.webPage.getClientSelectedText().isEmpty()) {
                              if (var1x.getCode() == KeyCode.UP
                                 || var1x.getCode() == KeyCode.DOWN
                                 || var1x.getCode() == KeyCode.LEFT
                                 || var1x.getCode() == KeyCode.RIGHT
                                 || var1x.getCode() == KeyCode.HOME
                                 || var1x.getCode() == KeyCode.END) {
                                 this.updateToolbarState(true);
                              } else if (!var1x.isControlDown() && !var1x.isMetaDown()) {
                                 this.resetToolbarState = var1x.getCode() == KeyCode.ENTER;
                                 if (this.resetToolbarState
                                    && this.getCommandState(HTMLEditorSkin.Command.BOLD.getCommand()) != this.boldButton.selectedProperty().getValue()) {
                                    this.executeCommand(HTMLEditorSkin.Command.BOLD.getCommand(), this.boldButton.selectedProperty().getValue().toString());
                                 }

                                 this.updateToolbarState(false);
                              } else {
                                 if (var1x.getCode() == KeyCode.B) {
                                    this.performCommand(HTMLEditorSkin.Command.BOLD);
                                 } else if (var1x.getCode() == KeyCode.I) {
                                    this.performCommand(HTMLEditorSkin.Command.ITALIC);
                                 } else if (var1x.getCode() == KeyCode.U) {
                                    this.performCommand(HTMLEditorSkin.Command.UNDERLINE);
                                 }

                                 this.updateToolbarState(true);
                              }

                              this.resetToolbarState = false;
                           } else if (!var1x.isShiftDown()
                              || var1x.getCode() != KeyCode.UP
                                 && var1x.getCode() != KeyCode.DOWN
                                 && var1x.getCode() != KeyCode.LEFT
                                 && var1x.getCode() != KeyCode.RIGHT
                                 && var1x.getCode() != KeyCode.HOME
                                 && var1x.getCode() != KeyCode.END) {
                              if ((var1x.isControlDown() || var1x.isMetaDown()) && var1x.getCode() == KeyCode.A) {
                                 this.enableAtomicityCheck = true;
                                 this.updateToolbarState(true);
                                 this.enableAtomicityCheck = false;
                              }
                           } else {
                              this.enableAtomicityCheck = true;
                              this.updateToolbarState(true);
                              this.enableAtomicityCheck = false;
                           }
                        }
                     );
                  }
               }
            }
         );
      this.webView
         .addEventHandler(
            KeyEvent.KEY_RELEASED,
            var1x -> {
               if (var1x.getCode() != KeyCode.CONTROL && var1x.getCode() != KeyCode.META) {
                  if ((this.fgColorButton == null || !this.fgColorButton.isShowing()) && (this.bgColorButton == null || !this.bgColorButton.isShowing())) {
                     Platform.runLater(
                        () -> {
                           if (this.webPage.getClientSelectedText().isEmpty()) {
                              if (var1x.getCode() == KeyCode.UP
                                 || var1x.getCode() == KeyCode.DOWN
                                 || var1x.getCode() == KeyCode.LEFT
                                 || var1x.getCode() == KeyCode.RIGHT
                                 || var1x.getCode() == KeyCode.HOME
                                 || var1x.getCode() == KeyCode.END) {
                                 this.updateToolbarState(true);
                              } else if (!var1x.isControlDown() && !var1x.isMetaDown()) {
                                 this.resetToolbarState = var1x.getCode() == KeyCode.ENTER;
                                 if (!this.resetToolbarState) {
                                    this.updateToolbarState(false);
                                 }
                              } else {
                                 if (var1x.getCode() == KeyCode.B) {
                                    this.performCommand(HTMLEditorSkin.Command.BOLD);
                                 } else if (var1x.getCode() == KeyCode.I) {
                                    this.performCommand(HTMLEditorSkin.Command.ITALIC);
                                 } else if (var1x.getCode() == KeyCode.U) {
                                    this.performCommand(HTMLEditorSkin.Command.UNDERLINE);
                                 }

                                 this.updateToolbarState(true);
                              }

                              this.resetToolbarState = false;
                           }
                        }
                     );
                  }
               }
            }
         );
      ((HTMLEditor)this.getSkinnable()).focusedProperty().addListener((var1x, var2, var3x) -> Platform.runLater(new Runnable() {
            @Override
            public void run() {
               if (var3x) {
                  HTMLEditorSkin.this.webView.requestFocus();
               }
            }
         }));
      this.webView.focusedProperty().addListener((var1x, var2, var3x) -> {
         this.pseudoClassStateChanged(CONTAINS_FOCUS_PSEUDOCLASS_STATE, var3x);
         Platform.runLater(new Runnable() {
            @Override
            public void run() {
               HTMLEditorSkin.this.updateToolbarState(true);
               if (PlatformImpl.isSupported(ConditionalFeature.VIRTUAL_KEYBOARD)) {
                  Scene var1 = ((HTMLEditor)HTMLEditorSkin.this.getSkinnable()).getScene();
                  if (var3x) {
                     FXVK.attach(HTMLEditorSkin.this.webView);
                  } else if (var1 == null || var1.getWindow() == null || !var1.getWindow().isFocused() || !(var1.getFocusOwner() instanceof TextInputControl)) {
                     FXVK.detach();
                  }
               }
            }
         });
      });
      this.webView.getEngine().getLoadWorker().workDoneProperty().addListener((var1x, var2, var3x) -> {
         Platform.runLater(() -> this.webView.requestLayout());
         double var4 = this.webView.getEngine().getLoadWorker().getTotalWork();
         if (var3x.doubleValue() == var4) {
            this.cachedHTMLText = null;
            Platform.runLater(() -> {
               this.setContentEditable(true);
               this.updateToolbarState(true);
               this.updateNodeOrientation();
               this.executeCommand(HTMLEditorSkin.Command.STYLEWITHCSS.getCommand(), "true");
            });
         }
      });
      this.enableToolbar(true);
      this.setHTMLText(this.cachedHTMLText);
      this.engine = new ParentTraversalEngine(this.getSkinnable(), new Algorithm() {
         public Node select(Node var1, Direction var2, TraversalContext var3) {
            return HTMLEditorSkin.this.cutButton;
         }

         public Node selectFirst(TraversalContext var1) {
            return HTMLEditorSkin.this.cutButton;
         }

         public Node selectLast(TraversalContext var1) {
            return HTMLEditorSkin.this.cutButton;
         }
      });
      ParentHelper.setTraversalEngine(this.getSkinnable(), this.engine);
      this.webView.setFocusTraversable(true);
      this.gridPane.getChildren().addListener(this.itemsListener);
   }

   public void performCommand(HTMLEditorSkin.Command var1) {
      switch (var1) {
         case BOLD:
            this.boldButton.fire();
            break;
         case ITALIC:
            this.italicButton.setSelected(!this.italicButton.isSelected());
            break;
         case UNDERLINE:
            this.underlineButton.setSelected(!this.underlineButton.isSelected());
      }
   }

   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      if (this.isFirstRun) {
         this.populateToolbars();
         this.isFirstRun = false;
      }

      super.layoutChildren(var1, var3, var5, var7);
      double var9 = Math.max(this.toolbar1.prefWidth(-1.0), this.toolbar2.prefWidth(-1.0));
      this.toolbar1.setMinWidth(var9);
      this.toolbar1.setPrefWidth(var9);
      this.toolbar2.setMinWidth(var9);
      this.toolbar2.setPrefWidth(var9);
   }

   final String getHTMLText() {
      return this.cachedHTMLText != null ? this.cachedHTMLText : this.webPage.getHtml(this.webPage.getMainFrame());
   }

   final void setHTMLText(String var1) {
      this.cachedHTMLText = var1;
      this.webPage.load(this.webPage.getMainFrame(), var1, "text/html");
      Platform.runLater(() -> this.updateToolbarState(true));
   }

   private void populateToolbars() {
      this.resources = ResourceBundle.getBundle(HTMLEditorSkin.class.getName());
      this.cutButton = this.addButton(
         this.toolbar1, this.resources.getString("cutIcon"), this.resources.getString("cut"), HTMLEditorSkin.Command.CUT.getCommand(), "html-editor-cut"
      );
      this.copyButton = this.addButton(
         this.toolbar1, this.resources.getString("copyIcon"), this.resources.getString("copy"), HTMLEditorSkin.Command.COPY.getCommand(), "html-editor-copy"
      );
      this.pasteButton = this.addButton(
         this.toolbar1,
         this.resources.getString("pasteIcon"),
         this.resources.getString("paste"),
         HTMLEditorSkin.Command.PASTE.getCommand(),
         "html-editor-paste"
      );
      this.toolbar1.getItems().add(new Separator(Orientation.VERTICAL));
      this.alignmentToggleGroup = new ToggleGroup();
      this.alignLeftButton = this.addToggleButton(
         this.toolbar1,
         this.alignmentToggleGroup,
         this.resources.getString("alignLeftIcon"),
         this.resources.getString("alignLeft"),
         HTMLEditorSkin.Command.ALIGN_LEFT.getCommand(),
         "html-editor-align-left"
      );
      this.alignCenterButton = this.addToggleButton(
         this.toolbar1,
         this.alignmentToggleGroup,
         this.resources.getString("alignCenterIcon"),
         this.resources.getString("alignCenter"),
         HTMLEditorSkin.Command.ALIGN_CENTER.getCommand(),
         "html-editor-align-center"
      );
      this.alignRightButton = this.addToggleButton(
         this.toolbar1,
         this.alignmentToggleGroup,
         this.resources.getString("alignRightIcon"),
         this.resources.getString("alignRight"),
         HTMLEditorSkin.Command.ALIGN_RIGHT.getCommand(),
         "html-editor-align-right"
      );
      this.alignJustifyButton = this.addToggleButton(
         this.toolbar1,
         this.alignmentToggleGroup,
         this.resources.getString("alignJustifyIcon"),
         this.resources.getString("alignJustify"),
         HTMLEditorSkin.Command.ALIGN_JUSTIFY.getCommand(),
         "html-editor-align-justify"
      );
      this.toolbar1.getItems().add(new Separator(Orientation.VERTICAL));
      this.outdentButton = this.addButton(
         this.toolbar1,
         this.resources.getString("outdentIcon"),
         this.resources.getString("outdent"),
         HTMLEditorSkin.Command.OUTDENT.getCommand(),
         "html-editor-outdent"
      );
      if (this.outdentButton.getGraphic() != null) {
         this.outdentButton.getGraphic().setNodeOrientation(NodeOrientation.INHERIT);
      }

      this.indentButton = this.addButton(
         this.toolbar1,
         this.resources.getString("indentIcon"),
         this.resources.getString("indent"),
         HTMLEditorSkin.Command.INDENT.getCommand(),
         "html-editor-indent"
      );
      if (this.indentButton.getGraphic() != null) {
         this.indentButton.getGraphic().setNodeOrientation(NodeOrientation.INHERIT);
      }

      this.toolbar1.getItems().add(new Separator(Orientation.VERTICAL));
      ToggleGroup var1 = new ToggleGroup();
      this.bulletsButton = this.addToggleButton(
         this.toolbar1,
         var1,
         this.resources.getString("bulletsIcon"),
         this.resources.getString("bullets"),
         HTMLEditorSkin.Command.BULLETS.getCommand(),
         "html-editor-bullets"
      );
      if (this.bulletsButton.getGraphic() != null) {
         this.bulletsButton.getGraphic().setNodeOrientation(NodeOrientation.INHERIT);
      }

      this.numbersButton = this.addToggleButton(
         this.toolbar1,
         var1,
         this.resources.getString("numbersIcon"),
         this.resources.getString("numbers"),
         HTMLEditorSkin.Command.NUMBERS.getCommand(),
         "html-editor-numbers"
      );
      this.toolbar1.getItems().add(new Separator(Orientation.VERTICAL));
      this.formatComboBox = new ComboBox();
      this.formatComboBox.getStyleClass().add("font-menu-button");
      this.formatComboBox.setFocusTraversable(false);
      this.formatComboBox.setMinWidth(Double.NEGATIVE_INFINITY);
      this.toolbar2.getItems().add(this.formatComboBox);
      this.formatStyleMap = new HashMap<>();
      this.styleFormatMap = new HashMap<>();
      this.createFormatMenuItem("<p>", this.resources.getString("paragraph"));
      Platform.runLater(() -> this.formatComboBox.setValue(this.resources.getString("paragraph")));
      this.createFormatMenuItem("<h1>", this.resources.getString("heading1"));
      this.createFormatMenuItem("<h2>", this.resources.getString("heading2"));
      this.createFormatMenuItem("<h3>", this.resources.getString("heading3"));
      this.createFormatMenuItem("<h4>", this.resources.getString("heading4"));
      this.createFormatMenuItem("<h5>", this.resources.getString("heading5"));
      this.createFormatMenuItem("<h6>", this.resources.getString("heading6"));
      this.formatComboBox.setTooltip(new Tooltip(this.resources.getString("format")));
      this.formatComboBox.valueProperty().addListener((var1x, var2, var3) -> {
         if (var3 == null) {
            this.formatComboBox.setValue(null);
         } else {
            String var4 = this.formatStyleMap.get(var3);
            this.executeCommand(HTMLEditorSkin.Command.FORMAT.getCommand(), var4);
            this.updateToolbarState(false);

            for (int var5 = 0; var5 < DEFAULT_FORMAT_MAPPINGS.length; var5++) {
               String[] var6 = DEFAULT_FORMAT_MAPPINGS[var5];
               if (var6[0].equalsIgnoreCase(var4)) {
                  this.executeCommand(HTMLEditorSkin.Command.FONT_SIZE.getCommand(), var6[2]);
                  this.updateToolbarState(false);
                  break;
               }
            }
         }
      });
      this.fontFamilyComboBox = new ComboBox();
      this.fontFamilyComboBox.getStyleClass().add("font-menu-button");
      this.fontFamilyComboBox.setMinWidth(150.0);
      this.fontFamilyComboBox.setPrefWidth(150.0);
      this.fontFamilyComboBox.setMaxWidth(150.0);
      this.fontFamilyComboBox.setFocusTraversable(false);
      this.fontFamilyComboBox.setTooltip(new Tooltip(this.resources.getString("fontFamily")));
      this.toolbar2.getItems().add(this.fontFamilyComboBox);
      this.fontFamilyComboBox.getProperties().put("comboBoxRowsToMeasureWidth", 0);
      this.fontFamilyComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
         public ListCell<String> call(ListView<String> var1) {
            ListCell var2 = new ListCell<String>() {
               public void updateItem(String var1, boolean var2) {
                  super.updateItem(var1, var2);
                  if (var1 != null) {
                     this.setText(var1);
                     this.setFont(new Font(var1, 12.0));
                  }
               }
            };
            var2.setMinWidth(100.0);
            var2.setPrefWidth(100.0);
            var2.setMaxWidth(100.0);
            return var2;
         }
      });
      Platform.runLater(() -> {
         ObservableList var1x = FXCollections.observableArrayList(Font.getFamilies());
         var1x.add(0, "");

         for (String var3 : var1x) {
            this.fontFamilyComboBox.setValue("");
            this.fontFamilyComboBox.setItems(var1x);
         }
      });
      this.fontFamilyComboBox
         .valueProperty()
         .addListener((var1x, var2, var3) -> this.executeCommand(HTMLEditorSkin.Command.FONT_FAMILY.getCommand(), "'" + var3 + "'"));
      this.fontSizeComboBox = new ComboBox();
      this.fontSizeComboBox.getStyleClass().add("font-menu-button");
      this.fontSizeComboBox.setFocusTraversable(false);
      this.toolbar2.getItems().add(this.fontSizeComboBox);
      this.fontSizeMap = new HashMap<>();
      this.sizeFontMap = new HashMap<>();
      this.createFontSizeMenuItem("1", this.resources.getString("extraExtraSmall"));
      this.createFontSizeMenuItem("2", this.resources.getString("extraSmall"));
      this.createFontSizeMenuItem("3", this.resources.getString("small"));
      Platform.runLater(() -> this.fontSizeComboBox.setValue(this.resources.getString("small")));
      this.createFontSizeMenuItem("4", this.resources.getString("medium"));
      this.createFontSizeMenuItem("5", this.resources.getString("large"));
      this.createFontSizeMenuItem("6", this.resources.getString("extraLarge"));
      this.createFontSizeMenuItem("7", this.resources.getString("extraExtraLarge"));
      this.fontSizeComboBox.setTooltip(new Tooltip(this.resources.getString("fontSize")));
      this.fontSizeComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
         public ListCell<String> call(ListView<String> var1) {
            return new ListCell<String>() {
               public void updateItem(String var1, boolean var2) {
                  super.updateItem(var1, var2);
                  if (var1 != null) {
                     this.setText(var1);
                     String var3 = var1.replaceFirst("[^0-9.].*$", "");
                     this.setFont(new Font((String)HTMLEditorSkin.this.fontFamilyComboBox.getValue(), Double.valueOf(var3)));
                  }
               }
            };
         }
      });
      this.fontSizeComboBox.valueProperty().addListener((var1x, var2, var3) -> {
         String var4 = this.getCommandValue(HTMLEditorSkin.Command.FONT_SIZE.getCommand());
         if (!var3.equals(var4)) {
            this.executeCommand(HTMLEditorSkin.Command.FONT_SIZE.getCommand(), this.fontSizeMap.get(var3));
         }
      });
      this.toolbar2.getItems().add(new Separator(Orientation.VERTICAL));
      this.boldButton = this.addToggleButton(
         this.toolbar2,
         null,
         this.resources.getString("boldIcon"),
         this.resources.getString("bold"),
         HTMLEditorSkin.Command.BOLD.getCommand(),
         "html-editor-bold"
      );
      this.boldButton.setOnAction(var1x -> {
         if ("<p>".equals(this.formatStyleMap.get(this.formatComboBox.getValue()))) {
            this.executeCommand(HTMLEditorSkin.Command.BOLD.getCommand(), this.boldButton.selectedProperty().getValue().toString());
         }
      });
      this.italicButton = this.addToggleButton(
         this.toolbar2,
         null,
         this.resources.getString("italicIcon"),
         this.resources.getString("italic"),
         HTMLEditorSkin.Command.ITALIC.getCommand(),
         "html-editor-italic"
      );
      this.underlineButton = this.addToggleButton(
         this.toolbar2,
         null,
         this.resources.getString("underlineIcon"),
         this.resources.getString("underline"),
         HTMLEditorSkin.Command.UNDERLINE.getCommand(),
         "html-editor-underline"
      );
      this.strikethroughButton = this.addToggleButton(
         this.toolbar2,
         null,
         this.resources.getString("strikethroughIcon"),
         this.resources.getString("strikethrough"),
         HTMLEditorSkin.Command.STRIKETHROUGH.getCommand(),
         "html-editor-strike"
      );
      this.toolbar2.getItems().add(new Separator(Orientation.VERTICAL));
      this.insertHorizontalRuleButton = this.addButton(
         this.toolbar2,
         this.resources.getString("insertHorizontalRuleIcon"),
         this.resources.getString("insertHorizontalRule"),
         HTMLEditorSkin.Command.INSERT_HORIZONTAL_RULE.getCommand(),
         "html-editor-hr"
      );
      this.insertHorizontalRuleButton.setOnAction(var1x -> {
         this.executeCommand(HTMLEditorSkin.Command.INSERT_NEW_LINE.getCommand(), null);
         this.executeCommand(HTMLEditorSkin.Command.INSERT_HORIZONTAL_RULE.getCommand(), null);
         this.updateToolbarState(false);
      });
      this.fgColorButton = new ColorPicker();
      this.fgColorButton.getStyleClass().add("html-editor-foreground");
      this.fgColorButton.setFocusTraversable(false);
      this.toolbar1.getItems().add(this.fgColorButton);
      this.fgColorButton.setValue(DEFAULT_FG_COLOR);
      this.fgColorButton.setTooltip(new Tooltip(this.resources.getString("foregroundColor")));
      this.fgColorButton.setOnAction(var1x -> {
         Color var2 = (Color)this.fgColorButton.getValue();
         if (var2 != null) {
            this.executeCommand(HTMLEditorSkin.Command.FOREGROUND_COLOR.getCommand(), this.colorValueToRGBA(var2));
            this.fgColorButton.hide();
         }
      });
      this.bgColorButton = new ColorPicker();
      this.bgColorButton.getStyleClass().add("html-editor-background");
      this.bgColorButton.setFocusTraversable(false);
      this.toolbar1.getItems().add(this.bgColorButton);
      this.bgColorButton.setValue(DEFAULT_BG_COLOR);
      this.bgColorButton.setTooltip(new Tooltip(this.resources.getString("backgroundColor")));
      this.bgColorButton.setOnAction(var1x -> {
         Color var2 = (Color)this.bgColorButton.getValue();
         if (var2 != null) {
            this.executeCommand(HTMLEditorSkin.Command.BACKGROUND_COLOR.getCommand(), this.colorValueToRGBA(var2));
            this.bgColorButton.hide();
         }
      });
   }

   private String colorValueToRGBA(Color var1) {
      return String.format(
         (Locale)null,
         "rgba(%d, %d, %d, %.5f)",
         Math.round(var1.getRed() * 255.0),
         Math.round(var1.getGreen() * 255.0),
         Math.round(var1.getBlue() * 255.0),
         var1.getOpacity()
      );
   }

   private Button addButton(ToolBar var1, String var2, String var3, String var4, String var5) {
      Button var6 = new Button();
      var6.setFocusTraversable(false);
      var6.getStyleClass().add(var5);
      var1.getItems().add(var6);
      Image var7 = AccessController.doPrivileged((PrivilegedAction<Image>)(() -> new Image(HTMLEditorSkin.class.getResource(var2).toString())));
      ((StyleableProperty)var6.graphicProperty()).applyStyle(null, new ImageView(var7));
      var6.setTooltip(new Tooltip(var3));
      var6.setOnAction(var2x -> {
         this.executeCommand(var4, null);
         this.updateToolbarState(false);
      });
      return var6;
   }

   private ToggleButton addToggleButton(ToolBar var1, ToggleGroup var2, String var3, String var4, String var5, String var6) {
      ToggleButton var7 = new ToggleButton();
      var7.setUserData(var5);
      var7.setFocusTraversable(false);
      var7.getStyleClass().add(var6);
      var1.getItems().add(var7);
      if (var2 != null) {
         var7.setToggleGroup(var2);
      }

      Image var8 = AccessController.doPrivileged((PrivilegedAction<Image>)(() -> new Image(HTMLEditorSkin.class.getResource(var3).toString())));
      ((StyleableProperty)var7.graphicProperty()).applyStyle(null, new ImageView(var8));
      var7.setTooltip(new Tooltip(var4));
      if (!HTMLEditorSkin.Command.BOLD.getCommand().equals(var5)) {
         var7.selectedProperty().addListener((var2x, var3x, var4x) -> {
            if (this.getCommandState(var5) != var4x) {
               this.executeCommand(var5, null);
            }
         });
      }

      return var7;
   }

   private void createFormatMenuItem(String var1, String var2) {
      this.formatComboBox.getItems().add(var2);
      this.formatStyleMap.put(var2, var1);
      this.styleFormatMap.put(var1, var2);
   }

   private void createFontSizeMenuItem(String var1, String var2) {
      this.fontSizeComboBox.getItems().add(var2);
      this.fontSizeMap.put(var2, var1);
      this.sizeFontMap.put(var1, var2);
   }

   private void updateNodeOrientation() {
      NodeOrientation var1 = ((HTMLEditor)this.getSkinnable()).getEffectiveNodeOrientation();
      HTMLDocument var2 = (HTMLDocument)this.webPage.getDocument(this.webPage.getMainFrame());
      HTMLElement var3 = (HTMLElement)var2.getDocumentElement();
      if (var3.getAttribute("dir") == null) {
         var3.setAttribute("dir", var1 == NodeOrientation.RIGHT_TO_LEFT ? "rtl" : "ltr");
      }
   }

   private void updateToolbarState(boolean var1) {
      if (this.webView.isFocused()) {
         this.atomicityCount++;
         this.copyButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.CUT.getCommand()));
         this.cutButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.COPY.getCommand()));
         this.pasteButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.PASTE.getCommand()));
         this.insertHorizontalRuleButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.INSERT_HORIZONTAL_RULE.getCommand()));
         if (var1) {
            this.alignLeftButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.ALIGN_LEFT.getCommand()));
            this.alignLeftButton.setSelected(this.getCommandState(HTMLEditorSkin.Command.ALIGN_LEFT.getCommand()));
            this.alignCenterButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.ALIGN_CENTER.getCommand()));
            this.alignCenterButton.setSelected(this.getCommandState(HTMLEditorSkin.Command.ALIGN_CENTER.getCommand()));
            this.alignRightButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.ALIGN_RIGHT.getCommand()));
            this.alignRightButton.setSelected(this.getCommandState(HTMLEditorSkin.Command.ALIGN_RIGHT.getCommand()));
            this.alignJustifyButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.ALIGN_JUSTIFY.getCommand()));
            this.alignJustifyButton.setSelected(this.getCommandState(HTMLEditorSkin.Command.ALIGN_JUSTIFY.getCommand()));
         } else if (this.alignmentToggleGroup.getSelectedToggle() != null) {
            String var2 = this.alignmentToggleGroup.getSelectedToggle().getUserData().toString();
            if (this.isCommandEnabled(var2) && !this.getCommandState(var2)) {
               this.executeCommand(var2, null);
            }
         }

         if (this.alignmentToggleGroup.getSelectedToggle() == null && this.webPage.getClientSelectedText().isEmpty()) {
            this.alignmentToggleGroup.selectToggle(this.alignLeftButton);
         }

         this.bulletsButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.BULLETS.getCommand()));
         this.bulletsButton.setSelected(this.getCommandState(HTMLEditorSkin.Command.BULLETS.getCommand()));
         this.numbersButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.NUMBERS.getCommand()));
         this.numbersButton.setSelected(this.getCommandState(HTMLEditorSkin.Command.NUMBERS.getCommand()));
         this.indentButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.INDENT.getCommand()));
         this.outdentButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.OUTDENT.getCommand()));
         this.formatComboBox.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.FORMAT.getCommand()));
         String var10 = this.getCommandValue(HTMLEditorSkin.Command.FORMAT.getCommand());
         if (var10 != null) {
            String var3 = "<" + var10 + ">";
            String var4 = this.styleFormatMap.get(var3);
            String var5 = (String)this.formatComboBox.getValue();
            if (this.resetToolbarState || var3.equals("<>") || var3.equalsIgnoreCase("<div>") || var3.equalsIgnoreCase("<blockquote>")) {
               this.formatComboBox.setValue(this.resources.getString("paragraph"));
            } else if (var5 != null && !var5.equalsIgnoreCase(var4)) {
               this.formatComboBox.setValue(var4);
            }
         }

         this.fontFamilyComboBox.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.FONT_FAMILY.getCommand()));
         String var11 = this.getCommandValue(HTMLEditorSkin.Command.FONT_FAMILY.getCommand());
         if (var11 != null) {
            String var12 = var11;
            if (var11.startsWith("'")) {
               var12 = var11.substring(1);
            }

            if (var12.endsWith("'")) {
               var12 = var12.substring(0, var12.length() - 1);
            }

            Object var14 = this.fontFamilyComboBox.getValue();
            if (var14 instanceof String && !var14.equals(var12)) {
               ObservableList var6 = this.fontFamilyComboBox.getItems();
               String var7 = null;

               for (String var9 : var6) {
                  if (var9.equals(var12)) {
                     var7 = var9;
                     break;
                  }

                  if (var9.equals("") && var12.equals("Dialog")) {
                     var7 = var9;
                     break;
                  }
               }

               if (var7 != null) {
                  this.fontFamilyComboBox.setValue(var7);
               }
            }
         }

         this.fontSizeComboBox.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.FONT_SIZE.getCommand()));
         String var13 = this.getCommandValue(HTMLEditorSkin.Command.FONT_SIZE.getCommand());
         if (this.resetToolbarState && var13 == null) {
            this.fontSizeComboBox.setValue(this.sizeFontMap.get("3"));
         } else if (var13 != null) {
            if (!((String)this.fontSizeComboBox.getValue()).equals(this.sizeFontMap.get(var13))) {
               this.fontSizeComboBox.setValue(this.sizeFontMap.get(var13));
            }
         } else if (this.fontSizeComboBox.getValue() == null || !((String)this.fontSizeComboBox.getValue()).equals(this.sizeFontMap.get("3"))) {
            this.fontSizeComboBox.setValue(this.sizeFontMap.get("3"));
         }

         this.boldButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.BOLD.getCommand()));
         this.boldButton.setSelected(this.getCommandState(HTMLEditorSkin.Command.BOLD.getCommand()));
         this.italicButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.ITALIC.getCommand()));
         this.italicButton.setSelected(this.getCommandState(HTMLEditorSkin.Command.ITALIC.getCommand()));
         this.underlineButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.UNDERLINE.getCommand()));
         this.underlineButton.setSelected(this.getCommandState(HTMLEditorSkin.Command.UNDERLINE.getCommand()));
         this.strikethroughButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.STRIKETHROUGH.getCommand()));
         this.strikethroughButton.setSelected(this.getCommandState(HTMLEditorSkin.Command.STRIKETHROUGH.getCommand()));
         this.fgColorButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.FOREGROUND_COLOR.getCommand()));
         String var15 = this.getCommandValue(HTMLEditorSkin.Command.FOREGROUND_COLOR.getCommand());
         if (var15 != null) {
            this.fgColorButton.setValue(this.getColor(var15));
         }

         this.bgColorButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.BACKGROUND_COLOR.getCommand()));
         String var16 = this.getCommandValue(HTMLEditorSkin.Command.BACKGROUND_COLOR.getCommand());
         if (var16 != null) {
            this.bgColorButton.setValue(this.getColor(var16));
         }

         this.atomicityCount = this.atomicityCount == 0 ? 0 : --this.atomicityCount;
      }
   }

   private void enableToolbar(boolean var1) {
      Platform.runLater(() -> {
         if (this.copyButton != null) {
            if (var1) {
               this.copyButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.COPY.getCommand()));
               this.cutButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.CUT.getCommand()));
               this.pasteButton.setDisable(!this.isCommandEnabled(HTMLEditorSkin.Command.PASTE.getCommand()));
            } else {
               this.copyButton.setDisable(true);
               this.cutButton.setDisable(true);
               this.pasteButton.setDisable(true);
            }

            this.insertHorizontalRuleButton.setDisable(!var1);
            this.alignLeftButton.setDisable(!var1);
            this.alignCenterButton.setDisable(!var1);
            this.alignRightButton.setDisable(!var1);
            this.alignJustifyButton.setDisable(!var1);
            this.bulletsButton.setDisable(!var1);
            this.numbersButton.setDisable(!var1);
            this.indentButton.setDisable(!var1);
            this.outdentButton.setDisable(!var1);
            this.formatComboBox.setDisable(!var1);
            this.fontFamilyComboBox.setDisable(!var1);
            this.fontSizeComboBox.setDisable(!var1);
            this.boldButton.setDisable(!var1);
            this.italicButton.setDisable(!var1);
            this.underlineButton.setDisable(!var1);
            this.strikethroughButton.setDisable(!var1);
            this.fgColorButton.setDisable(!var1);
            this.bgColorButton.setDisable(!var1);
         }
      });
   }

   private boolean executeCommand(String var1, String var2) {
      return this.enableAtomicityCheck && (!this.enableAtomicityCheck || this.atomicityCount != 0) ? false : this.webPage.executeCommand(var1, var2);
   }

   private boolean isCommandEnabled(String var1) {
      return this.webPage.queryCommandEnabled(var1);
   }

   private void setContentEditable(boolean var1) {
      HTMLDocument var2 = (HTMLDocument)this.webPage.getDocument(this.webPage.getMainFrame());
      HTMLElement var3 = (HTMLElement)var2.getDocumentElement();
      HTMLElement var4 = (HTMLElement)var3.getElementsByTagName("body").item(0);
      var4.setAttribute("contenteditable", Boolean.toString(var1));
   }

   private boolean getCommandState(String var1) {
      return this.webPage.queryCommandState(var1);
   }

   private String getCommandValue(String var1) {
      return this.webPage.queryCommandValue(var1);
   }

   private Color getColor(String var1) {
      Color var2 = Color.web(var1);
      if (var2.equals(Color.TRANSPARENT)) {
         var2 = Color.WHITE;
      }

      return var2;
   }

   private void applyTextFormatting() {
      if (!this.getCommandState(HTMLEditorSkin.Command.BULLETS.getCommand()) && !this.getCommandState(HTMLEditorSkin.Command.NUMBERS.getCommand())) {
         if (this.webPage.getClientCommittedTextLength() == 0) {
            String var1 = this.formatStyleMap.get(this.formatComboBox.getValue());
            String var2 = ((String)this.fontFamilyComboBox.getValue()).toString();
            this.executeCommand(HTMLEditorSkin.Command.FORMAT.getCommand(), var1);
            this.executeCommand(HTMLEditorSkin.Command.FONT_FAMILY.getCommand(), "'" + var2 + "'");
         }
      }
   }

   void print(PrinterJob var1) {
      this.webView.getEngine().print(var1);
   }

   public static enum Command {
      CUT("cut"),
      COPY("copy"),
      PASTE("paste"),
      UNDO("undo"),
      REDO("redo"),
      INSERT_HORIZONTAL_RULE("inserthorizontalrule"),
      ALIGN_LEFT("justifyleft"),
      ALIGN_CENTER("justifycenter"),
      ALIGN_RIGHT("justifyright"),
      ALIGN_JUSTIFY("justifyfull"),
      BULLETS("insertUnorderedList"),
      NUMBERS("insertOrderedList"),
      INDENT("indent"),
      OUTDENT("outdent"),
      FORMAT("formatblock"),
      FONT_FAMILY("fontname"),
      FONT_SIZE("fontsize"),
      BOLD("bold"),
      ITALIC("italic"),
      UNDERLINE("underline"),
      STRIKETHROUGH("strikethrough"),
      FOREGROUND_COLOR("forecolor"),
      BACKGROUND_COLOR("backcolor"),
      STYLEWITHCSS("styleWithCSS"),
      INSERT_NEW_LINE("insertnewline"),
      INSERT_TAB("inserttab");

      private final String command;

      private Command(String var3) {
         this.command = var3;
      }

      public String getCommand() {
         return this.command;
      }
   }
}
