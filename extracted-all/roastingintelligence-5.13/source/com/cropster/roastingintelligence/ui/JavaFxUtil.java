package com.cropster.roastingintelligence.ui;

import com.cropster.roastingintelligence.ui.components.HyperlinkText;
import com.cropster.roastingintelligence.ui.util.DelegatingBuilderFactory;
import com.cropster.util.model.Unit;
import com.sun.javafx.print.Units;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.ScanResult;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.BuilderFactory;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.Validate;
import org.girod.javafx.svgimage.LoaderParameters;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaFxUtil {
   public static final IntegerStringConverter INTEGER_STRING_CONVERTER = new IntegerStringConverter();
   private static final Pattern TAG_PATTERN = Pattern.compile("<(/?)(\\p{Alpha}+)([^>]*)>");
   private static final Pattern STYLE_PATTERN = Pattern.compile("style=\"([^\"]*)\"");
   private static final Pattern HREF_PATTERN = Pattern.compile("href=\"([^\"]*)\"");
   private static final String LINE_SPACING_CHILDREN_KEY = "lineSpacingChildren";
   private static final String LENGTH_LIMIT_IME_COMPOSITION_ACTIVE_PROPERTY = JavaFxUtil.class.getName() + ".lengthLimitImeCompositionActive";
   private static final String LENGTH_LIMIT_IME_HANDLER_INSTALLED_PROPERTY = JavaFxUtil.class.getName() + ".lengthLimitImeHandlerInstalled";
   private static final Logger LOGGER = LoggerFactory.getLogger(JavaFxUtil.class);
   private static volatile boolean touchScreenDetected;
   public static final URL FONT_MONOSPACE_URL = JavaFxUtil.class.getResource("/fonts/NotoMono-Regular.ttf");
   private static final int[] ICON_RESOLUTIONS = new int[]{16, 32, 48, 64, 96, 128, 256, 512, 1024};
   private static final String CONTENT_DIVIDER_CLASS_NAME = "javafx.scene.control.skin.SplitPaneSkin$ContentDivider";
   public static final Color BLUE = Color.rgb(51, 102, 204);
   public static final Color RED = Color.rgb(220, 57, 18);
   public static final Color ORANGE = Color.rgb(255, 153, 0);
   public static final Color GREEN = Color.rgb(16, 150, 24);
   public static final Color PURPLE = Color.rgb(153, 0, 153);
   public static final Color[] COLORS = new Color[]{
      BLUE, RED, ORANGE, GREEN, PURPLE, Color.rgb(0, 153, 198), Color.rgb(221, 68, 119), Color.rgb(102, 170, 0), Color.rgb(184, 46, 46), Color.rgb(49, 99, 149)
   };
   public static final Color WARNING = Color.rgb(145, 113, 63);
   public static final Color ERROR = Color.rgb(145, 63, 69);
   public static final double LINE_SPACING_FACTOR = 0.175;
   private static final ButtonType[] BUTTON_TYPES_POSITIVE = new ButtonType[]{ButtonType.APPLY, ButtonType.OK, ButtonType.YES, ButtonType.FINISH};
   private static final ButtonType[] BUTTON_TYPES_NEGATIVE = new ButtonType[]{ButtonType.CANCEL, ButtonType.CLOSE, ButtonType.NO};
   private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(3, 3);
   private static DateFormat dateFormat = DateFormat.getDateInstance(3);
   private static DateFormat timeFormat = DateFormat.getTimeInstance(3);
   private static BuilderFactory builderFactory = new DelegatingBuilderFactory();

   public static NodeOrientation getNodeOrientation() {
      return NodeOrientation.LEFT_TO_RIGHT;
   }

   public static boolean isTouchScreenDetected() {
      return touchScreenDetected;
   }

   public static void setTouchScreenDetected(boolean touchScreenDetected) {
      JavaFxUtil.touchScreenDetected = touchScreenDetected;
   }

   public static ColumnConstraints newColumnConstraints(Priority priority) {
      ColumnConstraints cc = new ColumnConstraints();
      cc.setHgrow(priority);
      return cc;
   }

   public static boolean isAncestor(Node ancestorCandidate, Node node) {
      if (ancestorCandidate == null) {
         return false;
      } else {
         Node n = node;

         while (n != null) {
            n = n.getParent();
            if (n == ancestorCandidate) {
               return true;
            }
         }

         return false;
      }
   }

   public static EventHandler<MouseEvent> addDividerDoubleClickHandler(SplitPane splitPane, int dividerIndex, Runnable handler) {
      Objects.requireNonNull(splitPane, "Null 'splitPane' parameter");
      Objects.requireNonNull(handler, "Null 'handler' parameter");
      Validate.isTrue(dividerIndex >= 0, "Negative 'dividerIndex' parameter", new Object[0]);
      EventHandler<MouseEvent> eventHandler = e -> {
         if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && e.getTarget() instanceof Node && dividerIndex < splitPane.getDividers().size()) {
            Node target = (Node)e.getTarget();
            String skinDividerClassName = "javafx.scene.control.skin.SplitPaneSkin$ContentDivider";
            Node targetParent = (Node)(skinDividerClassName.equals(target.getClass().getName()) ? target : target.getParent());
            if (targetParent != null) {
               String className = targetParent.getClass().getName();

               try {
                  ScanResult scanResult = new ClassGraph()
                     .enableClassInfo()
                     .enableMethodInfo()
                     .enableSystemJarsAndModules()
                     .ignoreClassVisibility()
                     .ignoreMethodVisibility()
                     .acceptClasses(new String[]{className})
                     .scan();

                  try {
                     ClassInfo classInfo = scanResult.getClassInfo(className);
                     MethodInfoList methodInfoList = classInfo.getMethodInfo("getDivider");
                     if (!methodInfoList.isEmpty()) {
                        Method method = ((MethodInfo)methodInfoList.get(0)).loadClassAndGetMethod();
                        method.setAccessible(true);
                        Divider clickedDivider = (Divider)method.invoke(targetParent);
                        if (clickedDivider == splitPane.getDividers().get(dividerIndex)) {
                           handler.run();
                        }
                     }
                  } catch (Throwable var14) {
                     if (scanResult != null) {
                        try {
                           scanResult.close();
                        } catch (Throwable var13) {
                           var14.addSuppressed(var13);
                        }
                     }

                     throw var14;
                  }

                  if (scanResult != null) {
                     scanResult.close();
                  }
               } catch (Exception var15) {
                  LOGGER.warn("Error invoking method 'getDivider' on {}: {}", className, var15.getMessage());
               }
            }
         }
      };
      splitPane.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
      return eventHandler;
   }

   public static Color colorFromAwt(java.awt.Color awtColor) {
      return Color.rgb(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), (double)awtColor.getAlpha() / 255.0);
   }

   public static java.awt.Color colorToAwt(Color jfxColor) {
      return new java.awt.Color((float)jfxColor.getRed(), (float)jfxColor.getGreen(), (float)jfxColor.getBlue(), (float)jfxColor.getOpacity());
   }

   public static String colorToHexString(Color color) {
      return StringUtils.leftPad(Integer.toHexString(color.hashCode() >>> 8), 6, "0");
   }

   public static String formatDateTime(Date date) {
      return dateTimeFormat.format(date);
   }

   public static String formatDate(Date date) {
      return dateFormat.format(date);
   }

   public static String formatTime(Date date) {
      return timeFormat.format(date);
   }

   public static void loadComponent(Object component) {
      Class<? extends Object> componentClass = (Class<? extends Object>)component.getClass();
      URL fxmlResource = null;

      do {
         String template = componentClass.getSimpleName() + ".fxml";
         fxmlResource = componentClass.getResource(template);
         componentClass = (Class<? extends Object>)componentClass.getSuperclass();
      } while (fxmlResource == null);

      FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource, new JavaFxUtil.FailSafeResourceBundle(org.cropster.roastingintelligence.util.Messages.getBundle()));
      fxmlLoader.setBuilderFactory(builderFactory);
      fxmlLoader.setRoot(component);
      fxmlLoader.setController(component);

      try {
         fxmlLoader.load();
      } catch (IOException var5) {
         throw new Error("Error loading " + component.getClass().getSimpleName(), var5);
      }
   }

   public static <T extends Parent> T findParent(Node leaf, Class<T> parentType) {
      for (Parent parent = leaf.getParent(); parent != null; parent = parent.getParent()) {
         if (parentType.isAssignableFrom(parent.getClass())) {
            return (T)parent;
         }
      }

      return null;
   }

   public static <T extends Node> Stream<T> newRecursiveChildrenStream(Parent rootNode, Class<T> nodeType) {
      ObservableList<Node> children = rootNode instanceof ScrollPane
         ? FXCollections.singletonObservableList(((ScrollPane)rootNode).getContent())
         : rootNode.getChildrenUnmodifiable();
      return children.stream().flatMap(c -> {
         if (c instanceof Parent) {
            Stream<T> childrenStream = newRecursiveChildrenStream((Parent)c, nodeType);
            return nodeType != null && !nodeType.isAssignableFrom(c.getClass()) ? childrenStream : Stream.concat(Stream.of((T)c), childrenStream);
         } else {
            return nodeType != null && !nodeType.isAssignableFrom(c.getClass()) ? Stream.empty() : Stream.of((T)c);
         }
      });
   }

   public static Stream<Node> newRecursiveChildrenStream(Parent rootNode) {
      return newRecursiveChildrenStream(rootNode, null);
   }

   public static void reverseButtonBars(Parent rootNode) {
      if (SystemUtils.IS_OS_WINDOWS) {
         rootNode.lookupAll(".button-bar--reversable").forEach(n -> {
            Pane buttonBar = (Pane)n;
            ArrayList<Node> buttons = new ArrayList<>(buttonBar.getChildren());
            Collections.reverse(buttons);
            buttonBar.getChildren().setAll(buttons);
         });
      }
   }

   public static void removeRow(GridPane gridPane, int rowIndex) {
      Iterator<Node> it = gridPane.getChildren().iterator();

      while (it.hasNext()) {
         Node node = it.next();
         Integer r = GridPane.getRowIndex(node);
         r = r == null ? 0 : r;
         GridPane.setRowIndex(node, r);
         if (r == rowIndex) {
            it.remove();
         }
      }

      for (Node node : gridPane.getChildren()) {
         Integer r = GridPane.getRowIndex(node);
         if (r >= rowIndex) {
            GridPane.setRowIndex(node, r - 1);
         }
      }
   }

   public static void copyToClipboard(String string) {
      Clipboard clipboard = Clipboard.getSystemClipboard();
      ClipboardContent content = new ClipboardContent();
      content.putString(string);
      clipboard.setContent(content);
   }

   public static void applyButtonStyles(Alert alert, String positiveButtonStyle) {
      for (ButtonType buttonType : BUTTON_TYPES_POSITIVE) {
         Node button = alert.getDialogPane().lookupButton(buttonType);
         if (button != null) {
            button.getStyleClass().add(positiveButtonStyle);
         }
      }

      for (ButtonType buttonTypex : BUTTON_TYPES_NEGATIVE) {
         Node button = alert.getDialogPane().lookupButton(buttonTypex);
         if (button != null) {
            button.getStyleClass().add("button--plain");
         }
      }
   }

   public static void applyButtonStyles(Alert alert, ButtonType positiveType, String positiveButtonStyle) {
      Node button = alert.getDialogPane().lookupButton(positiveType);
      if (button != null) {
         button.getStyleClass().add(positiveButtonStyle);
      }

      for (ButtonType buttonType : BUTTON_TYPES_NEGATIVE) {
         button = alert.getDialogPane().lookupButton(buttonType);
         if (button != null) {
            button.getStyleClass().add("button--plain");
         }
      }
   }

   public static void applyButtonStyles(Alert alert) {
      applyButtonStyles(alert, "button--primary");
   }

   public static void setIcons(Stage stage) {
      for (int res : ICON_RESOLUTIONS) {
         String iconPath = String.format("/images/roastmane%sx%s.png", res, res);

         try {
            stage.getIcons().add(new Image(JavaFxUtil.class.getResourceAsStream(iconPath)));
         } catch (Exception var7) {
            LOGGER.error("Error loading icon {}", iconPath, var7);
         }
      }
   }

   public static void setTextColor(Labeled node, Color color) {
      String colorString = "#" + color.toString().substring(2, 8);
      node.setStyle("-fx-text-fill: " + colorString);
   }

   public static void setTextColor(Text node, Color color) {
      String colorString = "#" + color.toString().substring(2, 8);
      node.setStyle("-fx-fill: " + colorString);
   }

   public static SVGImage loadSvg(URL url, String styleClass, double scale) {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = JavaFxController.STYLE_SHEET;
      params.scale = scale;
      params.scaleLineWidth = true;
      SVGImage svgImage = SVGLoader.load(url, params);
      svgImage.getStyleClass().add("svg-image");
      if (styleClass != null) {
         svgImage.getStyleClass().add(styleClass);
      }

      if (scale > 0.0) {
         svgImage.setScaleX(scale);
         svgImage.setScaleY(scale);
      }

      return svgImage;
   }

   public static SVGImage loadSvg(URL url, String styleClass) {
      return loadSvg(url, styleClass, -1.0);
   }

   public static SVGImage loadSvg(URL url, double scale) {
      return loadSvg(url, null, scale);
   }

   public static SVGImage loadSvg(URL url) {
      return loadSvg(url, null);
   }

   public static Parent squareImage(SVGImage svgImage) {
      double width = svgImage.getWidth();
      double height = svgImage.getHeight();
      if (width < height) {
         return squareImage(svgImage, height);
      } else {
         return (Parent)(width > height ? squareImage(svgImage, width) : svgImage);
      }
   }

   public static Parent squareImage(SVGImage icon, double size) {
      Validate.isTrue(size > 0.0, "Size must be greater than 0", new Object[0]);
      if (icon == null) {
         return null;
      } else if (icon.getWidth() == size && icon.getHeight() == size) {
         return icon;
      } else {
         HBox graphics = new HBox(new Node[]{icon});
         graphics.setMinWidth(size);
         graphics.setMinHeight(size);
         graphics.setMaxWidth(size);
         graphics.setMaxHeight(size);
         graphics.setAlignment(Pos.CENTER);
         return graphics;
      }
   }

   public static ContextMenu createContextMenu(Node owner) {
      ContextMenu contextMenu = new ContextMenu();
      contextMenu.setAutoHide(true);
      if (owner.getProperties().containsKey(ContextMenu.class)) {
         ((ContextMenu)owner.getProperties().get(ContextMenu.class)).hide();
      }

      contextMenu.setOnShown(e -> owner.getProperties().put(ContextMenu.class, contextMenu));
      contextMenu.setOnHidden(e -> owner.getProperties().remove(ContextMenu.class));
      return contextMenu;
   }

   public static void addStyleIfNotPresent(ObservableList<String> styleClass, String style) {
      Objects.requireNonNull(styleClass, "Null `styleClass` parameter");
      Validate.notBlank(style, "Blank `style` parameter", new Object[0]);
      if (!styleClass.contains(style)) {
         styleClass.add(style);
      }
   }

   public static void replaceStyles(ObservableList<String> styleClass, String replacement, String... styles) {
      Objects.requireNonNull(styleClass, "Null `styleClass` parameter");
      Validate.notBlank(replacement, "Blank `replacement` parameter", new Object[0]);
      Validate.notEmpty(styles, "Empty `styles` parameter", new Object[0]);
      styleClass.removeAll(styles);
      addStyleIfNotPresent(styleClass, replacement);
   }

   public static void setStyleRecursively(Node node, String style) {
      node.setStyle(style);
      if (node instanceof Parent parent) {
         for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof Parent pChild) {
               setStyleRecursively(pChild, style);
            } else {
               child.setStyle(style);
            }
         }
      }
   }

   public static void bindButtonToBoolean(Button button, BooleanProperty property, Node image, String text, EventHandler<ActionEvent> action, Tooltip tooltip) {
      bindButtonToBoolean(button, property, image, text, action, tooltip, button.getGraphic(), button.getText(), button.getOnAction(), button.getTooltip());
      button.visibleProperty().bind(property);
   }

   public static void bindButtonToBoolean(
      Button button,
      BooleanProperty property,
      Image image1,
      String text1,
      EventHandler<ActionEvent> action1,
      Tooltip tooltip1,
      Image image2,
      String text2,
      EventHandler<ActionEvent> action2,
      Tooltip tooltip2
   ) {
      ImageView graphic1 = new ImageView(image1);
      ImageView graphic2 = new ImageView(image2);
      bindButtonToBoolean(button, property, graphic1, text1, action1, tooltip1, graphic2, text2, action2, tooltip2);
   }

   public static void bindButtonToBoolean(
      Button button,
      BooleanProperty property,
      Node graphic1,
      String text1,
      EventHandler<ActionEvent> action1,
      Tooltip tooltip1,
      Node graphic2,
      String text2,
      EventHandler<ActionEvent> action2,
      Tooltip tooltip2
   ) {
      button.graphicProperty().bind(Bindings.createObjectBinding(() -> property.get() ? graphic1 : graphic2, new Observable[]{property}));
      button.textProperty().bind(Bindings.createStringBinding(() -> property.get() ? text1 : text2, new Observable[]{property}));
      button.tooltipProperty().bind(Bindings.createObjectBinding(() -> property.get() ? tooltip1 : tooltip2, new Observable[]{property}));
      button.onActionProperty().bind(Bindings.createObjectBinding(() -> property.get() ? action1 : action2, new Observable[]{property}));
   }

   public static void bindStyleToColor(String style, StringProperty styleProperty, ObjectProperty<Color> colorProperty) {
      styleProperty.bind(
         Bindings.createStringBinding(() -> String.format("%s: #%s", style, colorToHexString((Color)colorProperty.get())), new Observable[]{colorProperty})
      );
   }

   public static void selectToggle(ToggleGroup toggleGroup, Object userData) {
      for (Toggle toggle : toggleGroup.getToggles()) {
         if (userData.equals(toggle.getUserData())) {
            toggleGroup.selectToggle(toggle);
            break;
         }
      }
   }

   public static void configureTooltips(Parent parent) {
      newRecursiveChildrenStream(parent, Control.class).filter(c -> c.getTooltip() != null).forEach(c -> configureTooltip(c.getTooltip()));
   }

   public static Tooltip configureTooltip(Tooltip tooltip) {
      tooltip.setShowDelay(Duration.millis(400.0));
      return tooltip;
   }

   public static Units toPrintUnit(Unit unit) {
      if (Unit.MM.equals(unit)) {
         return Units.MM;
      } else if (Unit.IN.equals(unit)) {
         return Units.INCH;
      } else {
         throw new IllegalArgumentException("Not a valid print unit: " + unit);
      }
   }

   public static BooleanProperty createLengthOverrunProperty(int maxLength, TextInputControl textInput, Label availableChars) {
      Validate.isTrue(maxLength > 0, "`maxLength` parameter must be greater than 0", new Object[0]);
      Objects.requireNonNull(textInput, "Null 'textInput' parameter");
      BooleanProperty lengthOverrun = new SimpleBooleanProperty();
      textInput.lengthProperty().addListener((s, o, n) -> {
         int chars = maxLength - (n != null ? n.intValue() : 0);
         lengthOverrun.set(chars < 0);
         if (availableChars != null) {
            availableChars.setText(INTEGER_STRING_CONVERTER.toString(chars));
            availableChars.getStyleClass().removeAll(new String[]{"comment-under-limit", "comment-over-limit"});
            if (lengthOverrun.get()) {
               availableChars.getStyleClass().add("comment-over-limit");
            } else {
               availableChars.getStyleClass().add("comment-under-limit");
            }
         }
      });
      return lengthOverrun;
   }

   public static String clamp(String text, int maxLength) {
      Validate.isTrue(maxLength > 0, "`maxLength` parameter must be greater than 0", new Object[0]);
      return !StringUtils.isEmpty(text) && text.length() > maxLength ? text.substring(0, maxLength) : text;
   }

   public static TextFormatter<String> createLengthLimitingTextFormatter(int maxLength) {
      Validate.isTrue(maxLength > 0, "`maxLength` parameter must be greater than 0", new Object[0]);
      return new TextFormatter(c -> {
         if (c.isContentChange()) {
            if (!(c.getControl() instanceof TextInputControl control)) {
               return c;
            }

            installLengthLimitImeTracking(control);
            if (!control.isFocused() || Boolean.TRUE.equals(control.getProperties().get(LENGTH_LIMIT_IME_COMPOSITION_ACTIVE_PROPERTY))) {
               return c;
            }

            if (c.getControlNewText().length() <= maxLength) {
               return c;
            }

            int beforeChangeLength = c.getRangeStart();
            int afterChangeLength = c.getControlText().length() - c.getRangeEnd();
            int availableSpace = maxLength - beforeChangeLength - afterChangeLength;
            if (availableSpace <= 0) {
               c.setText("");
            } else {
               int acceptableInsertLength = Math.min(c.getText().length(), availableSpace);
               c.setText(c.getText().substring(0, acceptableInsertLength));
            }
         }

         return c;
      });
   }

   private static void installLengthLimitImeTracking(TextInputControl control) {
      MacOsFxUtil.installImeCompositionResetWorkaround(control);
      if (control != null && !Boolean.TRUE.equals(control.getProperties().get(LENGTH_LIMIT_IME_HANDLER_INSTALLED_PROPERTY))) {
         control.getProperties().put(LENGTH_LIMIT_IME_HANDLER_INSTALLED_PROPERTY, Boolean.TRUE);
         control.addEventHandler(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, event -> {
            control.getProperties().put(LENGTH_LIMIT_IME_COMPOSITION_ACTIVE_PROPERTY, Boolean.TRUE);
            Platform.runLater(() -> control.getProperties().put(LENGTH_LIMIT_IME_COMPOSITION_ACTIVE_PROPERTY, Boolean.FALSE));
         });
      }
   }

   public static String toString(KeyEvent e) {
      StringBuilder sb = new StringBuilder("KeyEvent [");
      sb.append("eventType = ").append(e.getEventType());
      sb.append(", character = ").append(e.getCharacter());
      sb.append(", text = ").append(e.getText());
      sb.append(", code = ").append(e.getCode());
      sb.append(" (");
      if (e.getCode().isArrowKey()) {
         sb.append("arrow key");
      }

      if (e.getCode().isDigitKey()) {
         sb.append("digit key");
      }

      if (e.getCode().isFunctionKey()) {
         sb.append("function key");
      }

      if (e.getCode().isKeypadKey()) {
         sb.append("keypad key");
      }

      if (e.getCode().isLetterKey()) {
         sb.append("letter key");
      }

      if (e.getCode().isMediaKey()) {
         sb.append("media key");
      }

      if (e.getCode().isModifierKey()) {
         sb.append("modifier key");
      }

      if (e.getCode().isNavigationKey()) {
         sb.append("navigation key");
      }

      if (e.getCode().isWhitespaceKey()) {
         sb.append("whitespace key");
      }

      sb.append(")");
      if (e.isShiftDown()) {
         sb.append(", shift down");
      }

      if (e.isControlDown()) {
         sb.append(", control down");
      }

      if (e.isAltDown()) {
         sb.append(", alt down");
      }

      if (e.isMetaDown()) {
         sb.append(", meta down");
      }

      if (e.isShortcutDown()) {
         sb.append(", shortcut down");
      }

      return sb.append("]").toString();
   }

   public static <T> ListCell<T> getListCell(ListView<T> listView, int index) {
      if (listView != null && index >= 0 && index < listView.getItems().size()) {
         if (listView.getSkin() instanceof ListViewSkin skin) {
            VirtualFlow<?> virtualFlow = (VirtualFlow<?>)skin.getChildren().get(0);
            return (ListCell<T>)virtualFlow.getCell(index);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static <T> void selectItemExclusively(ListView<T> listView, T item) {
      Objects.requireNonNull(listView, "Null 'listView' parameter");
      Objects.requireNonNull(item, "Null 'item' parameter");
      Validate.isTrue(listView.getItems().contains(item), "The item to select must be part of the ListView items", new Object[0]);
      MultipleSelectionModel<T> selectionModel = listView.getSelectionModel();
      selectionModel.select(item);
      List<Integer> selectedIndices = new ArrayList<>(selectionModel.getSelectedIndices());
      if (selectedIndices.size() > 1) {
         Collections.reverse(selectedIndices);
         selectedIndices.stream().filter(i -> !Objects.equals(listView.getItems().get(i), item)).forEach(selectionModel::clearSelection);
      }
   }

   public static List<Text> parseHtmlToTextNodes(String html) {
      if (StringUtils.isEmpty(html)) {
         return List.of(new Text());
      } else {
         List<Text> nodes = new ArrayList<>();
         StringBuilder style = new StringBuilder();
         Matcher matcher = TAG_PATTERN.matcher(html);
         Deque<String> spanStyleStack = new ArrayDeque<>();
         int lastIndex = 0;

         while (matcher.find()) {
            String tag = matcher.group(2);
            String tagContent = matcher.group(3);
            boolean isClosingTag = "/".equals(matcher.group(1));
            if (matcher.start() > lastIndex) {
               addTextNode(html, lastIndex, matcher.start(), style, nodes);
            }

            switch (tag) {
               case "b":
               case "strong":
                  style = handleSimpleTag(isClosingTag, style, "-fx-font-weight: bold; ");
                  break;
               case "i":
                  style = handleSimpleTag(isClosingTag, style, "-fx-font-style: italic; ");
                  break;
               case "span":
                  style = handleSpanTag(isClosingTag, spanStyleStack, style, tagContent);
                  break;
               case "a":
                  if (!isClosingTag) {
                     Matcher hrefMatcher = HREF_PATTERN.matcher(tagContent);
                     if (hrefMatcher.find()) {
                        String href = hrefMatcher.group(1);
                        int nextTagIndex = html.indexOf("</a>", matcher.end());
                        if (StringUtils.isNotBlank(href) && nextTagIndex != -1) {
                           String linkText = html.substring(matcher.end(), nextTagIndex);
                           handleLinkText(linkText, nodes, href);
                           lastIndex = nextTagIndex + 4;
                           matcher.region(lastIndex, html.length());
                           continue;
                        }
                     }
                  }
                  break;
               default:
                  throw new IllegalArgumentException("Unsupported HTML tag: " + tag);
            }

            lastIndex = matcher.end();
         }

         if (lastIndex < html.length()) {
            addTextNode(html, lastIndex, html.length(), style, nodes);
         }

         return nodes;
      }
   }

   private static void handleLinkText(String linkText, List<Text> nodes, String href) {
      if (!StringUtils.isEmpty(linkText)) {
         Matcher unsupportedMatcher = TAG_PATTERN.matcher(linkText);
         if (unsupportedMatcher.find()) {
            LOGGER.warn("Unsupported HTML tags found in link text will be removed: {}", linkText);
            linkText = unsupportedMatcher.replaceAll("");
         }

         nodes.add(new HyperlinkText(linkText, href));
      }
   }

   private static StringBuilder handleSpanTag(boolean isClosingTag, Deque<String> spanStyleStack, StringBuilder builder, String tagContent) {
      if (isClosingTag) {
         String spanStyle = spanStyleStack.pollFirst();
         if (spanStyle != null) {
            builder = new StringBuilder(builder.toString().replace(spanStyle, ""));
         }
      } else {
         Matcher styleMatcher = STYLE_PATTERN.matcher(tagContent);
         if (styleMatcher.find()) {
            String spanStyle = styleMatcher.group(1);
            if (StringUtils.isNotEmpty(spanStyle)) {
               spanStyle = spanStyle + "; ";
               spanStyleStack.offerFirst(spanStyle);
               builder.append(spanStyle);
            }
         }
      }

      return builder;
   }

   private static StringBuilder handleSimpleTag(boolean isClosingTag, StringBuilder builder, String style) {
      if (isClosingTag) {
         builder = new StringBuilder(builder.toString().replace(style, ""));
      } else {
         builder.append(style);
      }

      return builder;
   }

   private static void addTextNode(String html, int beginIndex, int endIndex, StringBuilder style, List<Text> nodes) {
      String textContent = html.substring(beginIndex, endIndex);
      if (StringUtils.isNotEmpty(textContent)) {
         Text textNode = new Text(textContent);
         if (StringUtils.isNotBlank(style.toString())) {
            textNode.setStyle(style.toString());
         }

         nodes.add(textNode);
      }
   }

   public static void preventToggleGroupBeingUnselected(ToggleGroup toggleGroup) {
      toggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
         if (newVal == null) {
            oldVal.setSelected(true);
         }
      });
   }

   public static void setDefaultLineSpacing(Node node) {
      setLineSpacing(0.175, node);
   }

   public static void setLineSpacing(double lineSpacing, Node node) {
      if (node != null) {
         if (node instanceof Labeled labeled) {
            if (!labeled.lineSpacingProperty().isBound()) {
               labeled.setLineSpacing(lineSpacing * labeled.getFont().getSize());
            }
         } else if (node instanceof Text text) {
            if (!text.lineSpacingProperty().isBound()) {
               text.setLineSpacing(lineSpacing * text.getFont().getSize());
            }
         } else if (node instanceof TextFlow) {
            updateLineSpacing((TextFlow)node, lineSpacing);
         } else if (node instanceof Parent) {
            newRecursiveChildrenStream((Parent)node).forEach(n -> {
               if (n instanceof Labeled labeledx) {
                  if (!labeledx.lineSpacingProperty().isBound()) {
                     labeledx.setLineSpacing(lineSpacing * labeledx.getFont().getSize());
                  }
               } else if (n instanceof Text textx) {
                  if (!textx.lineSpacingProperty().isBound()) {
                     textx.setLineSpacing(lineSpacing * textx.getFont().getSize());
                  }
               } else if (n instanceof TextFlow) {
                  updateLineSpacing((TextFlow)n, lineSpacing);
               }
            });
         }
      }
   }

   private static void setLineSpacing(TextFlow flow, double lineSpacing) {
      if (!flow.lineSpacingProperty().isBound()) {
         flow.getChildren().stream().filter(n -> n instanceof Labeled || n instanceof Text).findFirst().ifPresent(n -> {
            if (n instanceof Labeled labeled) {
               flow.setLineSpacing(lineSpacing * labeled.getFont().getSize());
            } else if (n instanceof Text text) {
               flow.setLineSpacing(lineSpacing * text.getFont().getSize());
            }
         });
      }
   }

   private static void updateLineSpacing(TextFlow flow, double lineSpacing) {
      if (flow.getProperties().containsKey("lineSpacingChildren")) {
         flow.getChildren().removeListener((ListChangeListener)flow.getProperties().get("lineSpacingChildren"));
      }

      ListChangeListener<? super Node> childrenListener = c -> setLineSpacing(flow, lineSpacing);
      flow.getChildren().addListener(childrenListener);
      flow.getProperties().put("lineSpacingChildren", childrenListener);
      setLineSpacing(flow, lineSpacing);
   }

   public static <T> void executeOnceWhenPropertyIsNonNull(final ObservableValue<T> property, final Consumer<T> consumer) {
      if (property != null) {
         T value = (T)property.getValue();
         if (value != null) {
            consumer.accept(value);
         } else {
            InvalidationListener listener = new InvalidationListener() {
               public void invalidated(Observable observable) {
                  T value = (T)property.getValue();
                  if (value != null) {
                     property.removeListener(this);
                     consumer.accept(value);
                  }
               }
            };
            property.addListener(listener);
         }
      }
   }

   private JavaFxUtil() {
   }

   public static class FailSafeResourceBundle extends ResourceBundle {
      public FailSafeResourceBundle(ResourceBundle parent) {
         this.setParent(parent);
      }

      @Override
      protected Object handleGetObject(String key) {
         if (this.parent.containsKey(key)) {
            return this.parent.getObject(key);
         } else {
            if (JavaFxUtil.LOGGER.isTraceEnabled()) {
               JavaFxUtil.LOGGER.trace("Missing translation for key \"{}\"", key);
            }

            return key;
         }
      }

      @Override
      public Enumeration<String> getKeys() {
         return this.parent.getKeys();
      }

      @Override
      public boolean containsKey(String key) {
         return true;
      }
   }
}
