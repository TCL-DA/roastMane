package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.VeilSupport;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.controlsfx.control.PopOver.ArrowLocation;

public class PopOverTour {
   public static final String ID_PROPERTY = "tour-identifier";
   public static final String PAGE_PROPERTY = "page-identifier";
   public static final String NEXT_BUTTON_ID = "next-button";
   public static final String PREVIOUS_BUTTON_ID = "previous-button";
   public static final String RESTART_BUTTON_ID = "restart-button";
   private final String id;
   private final int maxWidth;
   private final int minWidth;
   private final List<PopOverTour.TourPage> pages = new ArrayList<>();
   private final VeilSupport veilSupport = new VeilSupport(true);

   public static String getId(Node node) {
      return (String)Objects.requireNonNull(node, "Null 'node' parameter").getProperties().get("tour-identifier");
   }

   public static String getPage(Node node) {
      return (String)Objects.requireNonNull(node, "Null 'node' parameter").getProperties().get("page-identifier");
   }

   public static void setId(Node node, String identifier) {
      Objects.requireNonNull(node, "Null 'node' parameter").getProperties().put("tour-identifier", StringUtils.defaultIfBlank(identifier, null));
   }

   public static void setPage(Node node, String identifier) {
      Objects.requireNonNull(node, "Null 'node' parameter").getProperties().put("page-identifier", StringUtils.defaultIfBlank(identifier, null));
   }

   private static String checkID(String id) {
      return (String)Validate.notBlank(id);
   }

   public PopOverTour() {
      this(80, 240);
   }

   public PopOverTour(int maxWidth) {
      this(80, maxWidth);
   }

   public PopOverTour(int minWidth, int maxWidth) {
      this.id = null;
      this.maxWidth = maxWidth;
      this.minWidth = minWidth;
   }

   public PopOverTour(String id) {
      this(checkID(id), 80, 240);
   }

   public PopOverTour(String id, int maxWidth) {
      this(checkID(id), 80, maxWidth);
   }

   public PopOverTour(String id, int minWidth, int maxWidth) {
      this.id = checkID(id);
      this.maxWidth = maxWidth;
      this.minWidth = minWidth;
   }

   public PopOverTour addCustomPage(String title, Node content) {
      return this.addCustomPage(title, (Runnable)null, content);
   }

   public PopOverTour addCustomPage(String title, Runnable beforePage, Node content) {
      this.pages.add(new PopOverTour.NodeTourPage(null, null, ArrowLocation.TOP_CENTER, false, title, beforePage, content));
      return this;
   }

   public PopOverTour addCustomPage(ArrowLocation arrowLocation, String title, Node content) {
      return this.addCustomPage(arrowLocation, title, (Runnable)null, content);
   }

   public PopOverTour addCustomPage(ArrowLocation arrowLocation, String title, Runnable beforePage, Node content) {
      this.pages.add(new PopOverTour.NodeTourPage(null, null, arrowLocation, false, title, beforePage, content));
      return this;
   }

   public PopOverTour addCustomPage(String id, String title, Node content) {
      return this.addCustomPage(id, title, (Runnable)null, content);
   }

   public PopOverTour addCustomPage(String id, String title, Runnable beforePage, Node content) {
      return this.addCustomPage(id, title, beforePage, null, content);
   }

   public PopOverTour addCustomPage(String id, String title, Runnable beforePage, Function<Node, Node> nodeMapper, Node content) {
      PopOverTour.NodeTourPage page = new PopOverTour.NodeTourPage(checkID(id), null, ArrowLocation.TOP_CENTER, false, title, beforePage, content);
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public PopOverTour addCustomPage(String id, ArrowLocation arrowLocation, String title, Node content) {
      return this.addCustomPage(id, arrowLocation, title, (Runnable)null, content);
   }

   public PopOverTour addCustomPage(String id, ArrowLocation arrowLocation, String title, Runnable beforePage, Node content) {
      return this.addCustomPage(id, arrowLocation, title, beforePage, null, content);
   }

   public PopOverTour addCustomPage(String id, ArrowLocation arrowLocation, String title, Runnable beforePage, Function<Node, Node> nodeMapper, Node content) {
      PopOverTour.NodeTourPage page = new PopOverTour.NodeTourPage(checkID(id), null, arrowLocation, false, title, beforePage, content);
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public PopOverTour addCustomPage(Node owner, String title, Node content) {
      return this.addCustomPage(owner, title, (Runnable)null, content);
   }

   public PopOverTour addCustomPage(Node owner, String title, Runnable beforePage, Node content) {
      return this.addCustomPage(owner, title, beforePage, null, content);
   }

   public PopOverTour addCustomPage(Node owner, String title, Runnable beforePage, Function<Node, Node> nodeMapper, Node content) {
      PopOverTour.NodeTourPage page = new PopOverTour.NodeTourPage(
         null, Objects.requireNonNull(owner, "Parameter 'owner' cannot be null."), ArrowLocation.TOP_CENTER, false, title, beforePage, content
      );
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public PopOverTour addCustomPage(Node owner, ArrowLocation arrowLocation, String title, Node content) {
      return this.addCustomPage(owner, arrowLocation, title, (Runnable)null, content);
   }

   public PopOverTour addCustomPage(Node owner, ArrowLocation arrowLocation, String title, Runnable beforePage, Node content) {
      return this.addCustomPage(owner, arrowLocation, title, beforePage, null, content);
   }

   public PopOverTour addCustomPage(Node owner, ArrowLocation arrowLocation, String title, Runnable beforePage, Function<Node, Node> nodeMapper, Node content) {
      PopOverTour.NodeTourPage page = new PopOverTour.NodeTourPage(
         null, Objects.requireNonNull(owner, "Parameter 'owner' cannot be null."), arrowLocation, false, title, beforePage, content
      );
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public PopOverTour addDetachedCustomPage(String title, Node content) {
      return this.addDetachedCustomPage(title, (Runnable)null, content);
   }

   public PopOverTour addDetachedCustomPage(String title, Runnable beforePage, Node content) {
      this.pages.add(new PopOverTour.NodeTourPage(null, null, ArrowLocation.TOP_CENTER, true, title, beforePage, content));
      return this;
   }

   public PopOverTour addDetachedCustomPage(Node owner, String title, Node content) {
      return this.addDetachedCustomPage(owner, title, (Runnable)null, content);
   }

   public PopOverTour addDetachedCustomPage(Node owner, String title, Runnable beforePage, Node content) {
      return this.addDetachedCustomPage(owner, title, beforePage, null, content);
   }

   public PopOverTour addDetachedCustomPage(Node owner, String title, Runnable beforePage, Function<Node, Node> nodeMapper, Node content) {
      PopOverTour.NodeTourPage page = new PopOverTour.NodeTourPage(
         null, Objects.requireNonNull(owner, "Parameter 'owner' cannot be null."), ArrowLocation.TOP_CENTER, true, title, beforePage, content
      );
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public PopOverTour addDetachedCustomPage(String id, String title, Node content) {
      return this.addDetachedCustomPage(id, title, (Runnable)null, content);
   }

   public PopOverTour addDetachedCustomPage(String id, String title, Runnable beforePage, Node content) {
      return this.addDetachedCustomPage(id, title, beforePage, null, content);
   }

   public PopOverTour addDetachedCustomPage(String id, String title, Runnable beforePage, Function<Node, Node> nodeMapper, Node content) {
      PopOverTour.NodeTourPage page = new PopOverTour.NodeTourPage(checkID(id), null, ArrowLocation.TOP_CENTER, true, title, beforePage, content);
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public PopOverTour addDetachedPage(String title, String[] content) {
      return this.addDetachedPage(title, (Runnable)null, content);
   }

   public PopOverTour addDetachedPage(String title, Runnable beforePage, String... content) {
      this.pages.add(new PopOverTour.TextTourPage(null, null, ArrowLocation.TOP_CENTER, true, title, beforePage, content));
      return this;
   }

   public PopOverTour addDetachedPage(Node owner, String title, String... content) {
      return this.addDetachedPage(owner, title, (Runnable)null, content);
   }

   public PopOverTour addDetachedPage(Node owner, String title, Runnable beforePage, String... content) {
      return this.addDetachedPage(owner, title, beforePage, null, content);
   }

   public PopOverTour addDetachedPage(Node owner, String title, Runnable beforePage, Function<Node, Node> nodeMapper, String... content) {
      PopOverTour.TextTourPage page = new PopOverTour.TextTourPage(
         null, Objects.requireNonNull(owner, "Parameter 'owner' cannot be null."), ArrowLocation.TOP_CENTER, true, title, beforePage, content
      );
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public PopOverTour addDetachedPage(String id, String title, String... content) {
      return this.addDetachedPage(id, title, (Runnable)null, content);
   }

   public PopOverTour addDetachedPage(String id, String title, Runnable beforePage, String... content) {
      return this.addDetachedPage(id, title, beforePage, null, content);
   }

   public PopOverTour addDetachedPage(String id, String title, Runnable beforePage, Function<Node, Node> nodeMapper, String... content) {
      PopOverTour.TextTourPage page = new PopOverTour.TextTourPage(checkID(id), null, ArrowLocation.TOP_CENTER, true, title, beforePage, content);
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public PopOverTour addPage(String title, String[] content) {
      return this.addPage(title, (Runnable)null, content);
   }

   public PopOverTour addPage(String title, Runnable beforePage, String... content) {
      this.pages.add(new PopOverTour.TextTourPage(null, null, ArrowLocation.TOP_CENTER, false, title, beforePage, content));
      return this;
   }

   public PopOverTour addPage(ArrowLocation arrowLocation, String title, String... content) {
      return this.addPage(arrowLocation, title, (Runnable)null, content);
   }

   public PopOverTour addPage(ArrowLocation arrowLocation, String title, Runnable beforePage, String... content) {
      this.pages.add(new PopOverTour.TextTourPage(null, null, arrowLocation, false, title, beforePage, content));
      return this;
   }

   public PopOverTour addPage(String id, String title, String... content) {
      return this.addPage(id, title, (Runnable)null, content);
   }

   public PopOverTour addPage(String id, String title, Runnable beforePage, String... content) {
      return this.addPage(id, title, beforePage, null, content);
   }

   public PopOverTour addPage(String id, String title, Runnable beforePage, Function<Node, Node> nodeMapper, String... content) {
      PopOverTour.TextTourPage page = new PopOverTour.TextTourPage(checkID(id), null, ArrowLocation.TOP_CENTER, false, title, beforePage, content);
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public PopOverTour addPage(String id, ArrowLocation arrowLocation, String title, String... content) {
      return this.addPage(id, arrowLocation, title, (Runnable)null, content);
   }

   public PopOverTour addPage(String id, ArrowLocation arrowLocation, String title, Runnable beforePage, String... content) {
      return this.addPage(id, arrowLocation, title, beforePage, null, content);
   }

   public PopOverTour addPage(String id, ArrowLocation arrowLocation, String title, Runnable beforePage, Function<Node, Node> nodeMapper, String... content) {
      PopOverTour.TextTourPage page = new PopOverTour.TextTourPage(checkID(id), null, arrowLocation, false, title, beforePage, content);
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public PopOverTour addPage(Node owner, String title, String... content) {
      return this.addPage(owner, title, (Runnable)null, content);
   }

   public PopOverTour addPage(Node owner, String title, Runnable beforePage, String... content) {
      return this.addPage(owner, title, beforePage, null, content);
   }

   public PopOverTour addPage(Node owner, String title, Runnable beforePage, Function<Node, Node> nodeMapper, String... content) {
      PopOverTour.TextTourPage page = new PopOverTour.TextTourPage(
         null, Objects.requireNonNull(owner, "Parameter 'owner' cannot be null."), ArrowLocation.TOP_CENTER, false, title, beforePage, content
      );
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public PopOverTour addPage(Node owner, ArrowLocation arrowLocation, String title, String... content) {
      return this.addPage(owner, arrowLocation, title, (Runnable)null, content);
   }

   public PopOverTour addPage(Node owner, ArrowLocation arrowLocation, String title, Runnable beforePage, String... content) {
      return this.addPage(owner, arrowLocation, title, beforePage, null, content);
   }

   public PopOverTour addPage(Node owner, ArrowLocation arrowLocation, String title, Runnable beforePage, Function<Node, Node> nodeMapper, String... content) {
      PopOverTour.TextTourPage page = new PopOverTour.TextTourPage(
         null, Objects.requireNonNull(owner, "Parameter 'owner' cannot be null."), arrowLocation, false, title, beforePage, content
      );
      if (nodeMapper != null) {
         page.setNodeMapper(nodeMapper);
      }

      this.pages.add(page);
      return this;
   }

   public void show() throws IllegalStateException {
      if (this.pages.isEmpty()) {
         throw new IllegalStateException("No pages added.");
      } else if (this.pages.stream().filter(p -> p.getOwner() != null).count() == 0L) {
         throw new IllegalStateException("All pages without owner.");
      } else {
         Platform.runLater(() -> this.showPage(0, null));
      }
   }

   public void show(Parent root) throws IllegalStateException {
      Objects.requireNonNull(root, "Null 'root' parameter.");
      if (StringUtils.isBlank(this.id)) {
         throw new IllegalStateException("This tour doesn't have a defined identifier.");
      } else {
         Map<String, PopOverTour.TourPage> idToPageMap = this.pages
            .stream()
            .filter(p -> StringUtils.isNotBlank(p.getId()) && Objects.isNull(p.getOwner()))
            .collect(Collectors.toMap(p -> p.getId(), Function.identity()));
         JavaFxUtil.newRecursiveChildrenStream(root)
            .filter(n -> this.id.equals(getId(n)))
            .filter(n -> StringUtils.isNotBlank(getPage(n)))
            .flatMap(n -> Stream.of(StringUtils.split(getPage(n), ',')).map(p -> Pair.of(n, p.trim())))
            .filter(pair -> idToPageMap.containsKey(pair.getRight()))
            .forEach(pair -> {
               PopOverTour.TourPage page = idToPageMap.get(pair.getRight());
               page.setOwner(page.getNodeMapper().apply((Node)pair.getLeft()));
            });
         this.pages
            .stream()
            .filter(p -> StringUtils.isBlank(p.getId()) || Objects.isNull(p.getOwner()))
            .forEach(p -> p.setOwner(p.getNodeMapper().apply(root)));
         this.show();
      }
   }

   private void showPage(int pageIndex, PopOverBubble previousPopover) {
      if (previousPopover != null) {
         this.veilSupport.uninstallHandler(previousPopover);
         previousPopover.hide();
         this.showPage(pageIndex, null);
      } else {
         PopOverTour.TourPage page = this.pages.get(pageIndex);
         if (page.getBeforePage() != null) {
            Platform.runLater(() -> {
               page.getBeforePage().run();
               this.showPage(page, pageIndex);
            });
         } else {
            this.showPage(page, pageIndex);
         }
      }
   }

   private void showPage(PopOverTour.TourPage page, int pageIndex) {
      Node pageOwner = page.getOwner();
      this.veilSupport.setVeiledScene(pageOwner, !page.isDetached() || pageOwner != null && pageOwner != pageOwner.getScene().getRoot());
      PopOverBubble[] popover = new PopOverBubble[1];
      PopOverBubble.Builder builder = PopOverBubble.builder()
         .closable(true)
         .withDetachedCentered()
         .maximumWidth((double)this.maxWidth)
         .minimumWidth((double)this.minWidth)
         .withHeader(
            MessageFormat.format("{0,number,#0} of {1,number,#0} - {2}", 1 + pageIndex, this.pages.size(), page.getTitle()), Icon.TIP.load("icon--muted")
         )
         .withFooter(
            List.of(
               new ActionBuilder(org.cropster.roastingintelligence.util.Messages.m_("Restart tour"), e -> this.showPage(0, popover[0]))
                  .disabled(pageIndex == 0)
                  .id("restart-button")
                  .styleClass("button--plain")
                  .build()
            ),
            true,
            List.of(
               new ActionBuilder(e -> this.showPage(pageIndex + 1, popover[0]))
                  .disabled(pageIndex == this.pages.size() - 1)
                  .graphics(Icon.CHEVRON_RIGHT.load())
                  .id("next-button")
                  .build(),
               new ActionBuilder(e -> this.showPage(pageIndex - 1, popover[0]))
                  .disabled(pageIndex == 0)
                  .graphics(Icon.CHEVRON_LEFT.load())
                  .id("previous-button")
                  .build()
            ),
            false,
            false
         );
      if (page.isDetached()) {
         builder.withDetached();
      }

      if (page instanceof PopOverTour.TextTourPage) {
         builder.withContent(((PopOverTour.TextTourPage)page).getTextContent());
      } else if (page instanceof PopOverTour.NodeTourPage) {
         builder.withContent(((PopOverTour.NodeTourPage)page).getNodeContent());
      }

      popover[0] = builder.build();
      popover[0].setAutoHide(false);
      popover[0].showAt(page.getOwner(), page.getArrowLocation());
      this.veilSupport.installHandler(popover[0]);
   }

   private static class NodeTourPage extends PopOverTour.TourPage {
      private final Node content;

      NodeTourPage(String id, Node owner, ArrowLocation arrowLocation, boolean detached, String title, Runnable beforePage, Node content) {
         super(id, owner, arrowLocation, detached, title, beforePage);
         this.content = Objects.requireNonNull(content, "Null 'content' parameter.");
      }

      public Node getNodeContent() {
         return this.content;
      }
   }

   private static class TextTourPage extends PopOverTour.TourPage {
      private final String[] content;

      TextTourPage(String id, Node owner, ArrowLocation arrowLocation, boolean detached, String title, Runnable beforePage, String... content) {
         super(id, owner, arrowLocation, detached, title, beforePage);
         this.content = (String[])Validate.notEmpty(content);
      }

      public String[] getTextContent() {
         return this.content;
      }
   }

   private static class TourPage {
      private final ArrowLocation arrowLocation;
      private final Runnable beforePage;
      private final boolean detached;
      private final String id;
      private final String title;
      private Function<Node, Node> nodeMapper;
      private Node owner = null;

      TourPage(String id, Node owner, ArrowLocation arrowLocation, boolean detached, String title, Runnable beforePage) {
         this.id = id;
         this.title = (String)Validate.notBlank(title);
         this.owner = owner;
         this.arrowLocation = arrowLocation;
         this.detached = detached;
         this.beforePage = beforePage;
         this.nodeMapper = Function.identity();
      }

      public ArrowLocation getArrowLocation() {
         return this.arrowLocation;
      }

      public Runnable getBeforePage() {
         return this.beforePage;
      }

      public String getId() {
         return this.id;
      }

      public String getTitle() {
         return this.title;
      }

      public boolean isDetached() {
         return this.detached;
      }

      public Function<Node, Node> getNodeMapper() {
         return this.nodeMapper;
      }

      public void setNodeMapper(Function<Node, Node> nodeMapper) {
         this.nodeMapper = nodeMapper;
      }

      public Node getOwner() {
         return this.owner;
      }

      void setOwner(Node owner) {
         this.owner = owner;
      }
   }
}
