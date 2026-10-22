package impl.org.controlsfx.skin;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.text.TextFlow;
import org.controlsfx.control.HyperlinkLabel;

public class HyperlinkLabelSkin extends SkinBase<HyperlinkLabel> {
   private static final String HYPERLINK_START = "[";
   private static final String HYPERLINK_END = "]";
   private final TextFlow textFlow;
   private final EventHandler<ActionEvent> eventHandler = event -> {
      EventHandler<ActionEvent> onActionHandler = ((HyperlinkLabel)this.getSkinnable()).getOnAction();
      if (onActionHandler != null) {
         onActionHandler.handle(event);
      }
   };

   public HyperlinkLabelSkin(HyperlinkLabel control) {
      super(control);
      this.textFlow = new TextFlow();
      this.getChildren().add(this.textFlow);
      this.updateText();
      this.updateFocusTraversable(((HyperlinkLabel)this.getSkinnable()).isFocusTraversable());
      this.registerChangeListener(control.focusedProperty(), e -> {
         if (((HyperlinkLabel)this.getSkinnable()).isFocused()) {
            this.textFlow.getChildren().stream().filter(Node::isFocusTraversable).findFirst().ifPresent(Node::requestFocus);
         }
      });
      this.registerChangeListener(
         control.focusTraversableProperty(), e -> this.updateFocusTraversable(((HyperlinkLabel)this.getSkinnable()).isFocusTraversable())
      );
      this.registerChangeListener(control.textProperty(), e -> this.updateText());
   }

   private void updateFocusTraversable(Boolean nv) {
      this.textFlow.getChildren().stream().filter(node -> node instanceof Hyperlink).forEach(node -> node.setFocusTraversable(nv));
   }

   private void updateText() {
      String text = ((HyperlinkLabel)this.getSkinnable()).getText();
      if (text != null && !text.isEmpty()) {
         List<Node> nodes = new ArrayList<>();
         int start = 0;
         int textLength = text.length();

         while (start != -1 && start < textLength) {
            int startPos = text.indexOf("[", start);
            int endPos = text.indexOf("]", startPos);
            if ((startPos == -1 || endPos == -1) && textLength > start) {
               Label label = new Label(text.substring(start));
               nodes.add(label);
               break;
            }

            Label label = new Label(text.substring(start, startPos));
            nodes.add(label);
            Hyperlink hyperlink = new Hyperlink(text.substring(startPos + 1, endPos));
            hyperlink.setPadding(new Insets(0.0, 0.0, 0.0, 0.0));
            hyperlink.setOnAction(this.eventHandler);
            nodes.add(hyperlink);
            start = endPos + 1;
         }

         this.textFlow.getChildren().setAll(nodes);
      } else {
         this.textFlow.getChildren().clear();
      }
   }
}
