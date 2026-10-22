package impl.org.controlsfx.skin;

import impl.org.controlsfx.tools.rectangle.CoordinatePosition;
import impl.org.controlsfx.tools.rectangle.CoordinatePositions;
import impl.org.controlsfx.tools.rectangle.Rectangles2D;
import impl.org.controlsfx.tools.rectangle.change.MoveChangeStrategy;
import impl.org.controlsfx.tools.rectangle.change.NewChangeStrategy;
import impl.org.controlsfx.tools.rectangle.change.Rectangle2DChangeStrategy;
import impl.org.controlsfx.tools.rectangle.change.ToEastChangeStrategy;
import impl.org.controlsfx.tools.rectangle.change.ToNorthChangeStrategy;
import impl.org.controlsfx.tools.rectangle.change.ToNortheastChangeStrategy;
import impl.org.controlsfx.tools.rectangle.change.ToNorthwestChangeStrategy;
import impl.org.controlsfx.tools.rectangle.change.ToSouthChangeStrategy;
import impl.org.controlsfx.tools.rectangle.change.ToSoutheastChangeStrategy;
import impl.org.controlsfx.tools.rectangle.change.ToSouthwestChangeStrategy;
import impl.org.controlsfx.tools.rectangle.change.ToWestChangeStrategy;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.controlsfx.control.SnapshotView;

public class SnapshotViewSkin extends SkinBase<SnapshotView> {
   private static final double RELATIVE_EDGE_TOLERANCE = 0.015;
   private Node node;
   private final GridPane gridPane = createGridPane();
   private final Rectangle selectedArea = new Rectangle();
   private final Rectangle unselectedArea = new Rectangle();
   private final Node mouseNode = this.createMouseNode();
   private SnapshotViewSkin.SelectionChange selectionChange;
   private final Consumer<Boolean> setSelectionChanging = this.createSetSelectionChanging();

   public SnapshotViewSkin(SnapshotView snapshotView) {
      super(snapshotView);
      this.buildSceneGraph();
      this.initializeAreas();
      this.registerChangeListener(snapshotView.nodeProperty(), e -> this.updateNode());
      this.registerChangeListener(snapshotView.selectionProperty(), e -> this.updateSelection());
   }

   private static GridPane createGridPane() {
      GridPane pane = new GridPane();
      pane.setAlignment(Pos.CENTER);
      return pane;
   }

   private Node createMouseNode() {
      Rectangle mouseNode = new Rectangle();
      mouseNode.setFill(Color.TRANSPARENT);
      mouseNode.setManaged(false);
      mouseNode.widthProperty().bind(((SnapshotView)this.getSkinnable()).widthProperty());
      mouseNode.heightProperty().bind(((SnapshotView)this.getSkinnable()).heightProperty());
      mouseNode.addEventHandler(MouseEvent.ANY, this::handleMouseEvent);
      mouseNode.mouseTransparentProperty().bind(((SnapshotView)this.getSkinnable()).selectionMouseTransparentProperty());
      return mouseNode;
   }

   private Consumer<Boolean> createSetSelectionChanging() {
      return changing -> this.getNode().getProperties().put(SnapshotView.SELECTION_CHANGING_PROPERTY_KEY, changing);
   }

   private void buildSceneGraph() {
      this.getChildren().addAll(new Node[]{this.gridPane, this.unselectedArea, this.selectedArea, this.mouseNode});
      this.updateNode();
   }

   private void initializeAreas() {
      this.styleAreas();
      this.bindAreaCoordinatesTogether();
      this.bindAreaVisibilityToSelection();
   }

   private void styleAreas() {
      this.selectedArea.fillProperty().bind(((SnapshotView)this.getSkinnable()).selectionAreaFillProperty());
      this.selectedArea.strokeProperty().bind(((SnapshotView)this.getSkinnable()).selectionBorderPaintProperty());
      this.selectedArea.strokeWidthProperty().bind(((SnapshotView)this.getSkinnable()).selectionBorderWidthProperty());
      this.selectedArea.setStrokeType(StrokeType.OUTSIDE);
      this.selectedArea.setManaged(false);
      this.selectedArea.setMouseTransparent(true);
      this.unselectedArea.setFill(Color.TRANSPARENT);
      this.unselectedArea.strokeProperty().bind(((SnapshotView)this.getSkinnable()).unselectedAreaFillProperty());
      this.unselectedArea
         .strokeWidthProperty()
         .bind(Bindings.max(((SnapshotView)this.getSkinnable()).widthProperty(), ((SnapshotView)this.getSkinnable()).heightProperty()));
      this.unselectedArea.setStrokeType(StrokeType.OUTSIDE);
      this.unselectedArea.setManaged(false);
      this.unselectedArea.setMouseTransparent(true);
   }

   private void bindAreaCoordinatesTogether() {
      this.unselectedArea.xProperty().bind(this.selectedArea.xProperty());
      this.unselectedArea.yProperty().bind(this.selectedArea.yProperty());
      this.unselectedArea.widthProperty().bind(this.selectedArea.widthProperty());
      this.unselectedArea.heightProperty().bind(this.selectedArea.heightProperty());
   }

   private void bindAreaVisibilityToSelection() {
      ReadOnlyBooleanProperty selectionExists = ((SnapshotView)this.getSkinnable()).hasSelectionProperty();
      ReadOnlyBooleanProperty selectionActive = ((SnapshotView)this.getSkinnable()).selectionActiveProperty();
      BooleanBinding existsAndActive = Bindings.and(selectionExists, selectionActive);
      this.selectedArea.visibleProperty().bind(existsAndActive);
      this.unselectedArea.visibleProperty().bind(existsAndActive);
      new SnapshotViewSkin.Clipper((SnapshotView)this.getSkinnable(), this.unselectedArea, () -> this.unselectedArea.visibleProperty().bind(existsAndActive));
   }

   private void updateNode() {
      if (this.node != null) {
         this.gridPane.getChildren().remove(this.node);
      }

      this.node = ((SnapshotView)this.getSkinnable()).getNode();
      if (this.node != null) {
         this.gridPane.getChildren().add(0, this.node);
      }
   }

   private void updateSelection() {
      boolean showSelection = ((SnapshotView)this.getSkinnable()).hasSelection() && ((SnapshotView)this.getSkinnable()).isSelectionActive();
      if (showSelection) {
         Rectangle2D selection = ((SnapshotView)this.getSkinnable()).getSelection();
         this.setSelection(selection.getMinX(), selection.getMinY(), selection.getWidth(), selection.getHeight());
      } else {
         this.setSelection(0.0, 0.0, 0.0, 0.0);
      }
   }

   private void setSelection(double x, double y, double width, double height) {
      this.selectedArea.setX(x);
      this.selectedArea.setY(y);
      this.selectedArea.setWidth(width);
      this.selectedArea.setHeight(height);
   }

   private void handleMouseEvent(MouseEvent event) {
      Cursor newCursor = this.handleMouseEventImpl(event);
      this.mouseNode.setCursor(newCursor);
   }

   private Cursor handleMouseEventImpl(MouseEvent mouseEvent) {
      Objects.requireNonNull(mouseEvent, "The argument 'mouseEvent' must not be null.");
      EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();
      SnapshotViewSkin.SelectionEvent selectionEvent = this.createSelectionEvent(mouseEvent);
      if (eventType == MouseEvent.MOUSE_MOVED) {
         return this.getCursor(selectionEvent);
      } else if (eventType == MouseEvent.MOUSE_PRESSED) {
         return this.handleMousePressedEvent(selectionEvent);
      } else if (eventType == MouseEvent.MOUSE_DRAGGED) {
         return this.handleMouseDraggedEvent(selectionEvent);
      } else {
         return eventType == MouseEvent.MOUSE_RELEASED ? this.handleMouseReleasedEvent(selectionEvent) : Cursor.DEFAULT;
      }
   }

   private SnapshotViewSkin.SelectionEvent createSelectionEvent(MouseEvent mouseEvent) {
      Point2D point = new Point2D(mouseEvent.getX(), mouseEvent.getY());
      Rectangle2D selectionBounds = this.createBoundsForCurrentBoundary();
      CoordinatePosition position = this.computePosition(point);
      return new SnapshotViewSkin.SelectionEvent(mouseEvent, point, selectionBounds, position);
   }

   private Rectangle2D createBoundsForCurrentBoundary() {
      SnapshotView.Boundary boundary = ((SnapshotView)this.getSkinnable()).getSelectionAreaBoundary();
      switch (boundary) {
         case CONTROL:
            return new Rectangle2D(0.0, 0.0, this.getControlWidth(), this.getControlHeight());
         case NODE:
            boolean nodeExists = this.getSnapshotNode() != null;
            if (nodeExists) {
               Bounds nodeBounds = this.getSnapshotNode().getBoundsInParent();
               return Rectangles2D.fromBounds(nodeBounds);
            }

            return Rectangle2D.EMPTY;
         default:
            throw new IllegalArgumentException("The boundary " + boundary + " is not fully implemented.");
      }
   }

   private CoordinatePosition computePosition(Point2D point) {
      boolean noSelection = !((SnapshotView)this.getSkinnable()).hasSelection() || !((SnapshotView)this.getSkinnable()).isSelectionActive();
      boolean controlHasNoSpace = this.getControlWidth() == 0.0 || this.getControlHeight() == 0.0;
      if (!noSelection && !controlHasNoSpace) {
         double tolerance = this.computeTolerance();
         return computePosition(this.getSelection(), point, tolerance);
      } else {
         return CoordinatePosition.OUT_OF_RECTANGLE;
      }
   }

   private double computeTolerance() {
      double controlMeanLength = Math.sqrt(this.getControlWidth() * this.getControlHeight());
      return 0.015 * controlMeanLength;
   }

   private static CoordinatePosition computePosition(Rectangle2D selection, Point2D point, double tolerance) {
      CoordinatePosition onEdge = CoordinatePositions.onEdges(selection, point, tolerance);
      return onEdge != null ? onEdge : CoordinatePositions.inRectangle(selection, point);
   }

   private Cursor handleMousePressedEvent(SnapshotViewSkin.SelectionEvent selectionEvent) {
      if (selectionEvent.isPointInSelectionBounds()) {
         Cursor cursor = this.getCursor(selectionEvent);
         Rectangle2DChangeStrategy selectionChangeStrategy = this.getChangeStrategy(selectionEvent);
         boolean deactivateSelectionIfClick = willDeactivateSelectionIfClick(selectionEvent);
         this.selectionChange = new SnapshotViewSkin.SelectionChangeByStrategy(
            (SnapshotView)this.getSkinnable(), this.setSelectionChanging, selectionChangeStrategy, cursor, deactivateSelectionIfClick
         );
         this.selectionChange.beginSelectionChange(selectionEvent.getPoint());
      } else {
         this.selectionChange = SnapshotViewSkin.NoSelectionChange.INSTANCE;
      }

      return this.selectionChange.getCursor();
   }

   private Cursor handleMouseDraggedEvent(SnapshotViewSkin.SelectionEvent selectionEvent) {
      this.selectionChange.continueSelectionChange(selectionEvent.getPoint());
      return this.selectionChange.getCursor();
   }

   private Cursor handleMouseReleasedEvent(SnapshotViewSkin.SelectionEvent selectionEvent) {
      this.selectionChange.endSelectionChange(selectionEvent.getPoint());
      this.selectionChange = null;
      return this.getCursor(selectionEvent);
   }

   private Cursor getCursor(SnapshotViewSkin.SelectionEvent selectionEvent) {
      if (!selectionEvent.isPointInSelectionBounds()) {
         return this.getRegularCursor();
      } else {
         switch (selectionEvent.getPosition()) {
            case IN_RECTANGLE:
               return Cursor.MOVE;
            case OUT_OF_RECTANGLE:
               return this.getRegularCursor();
            case NORTH_EDGE:
               return Cursor.N_RESIZE;
            case NORTHEAST_EDGE:
               return Cursor.NE_RESIZE;
            case EAST_EDGE:
               return Cursor.E_RESIZE;
            case SOUTHEAST_EDGE:
               return Cursor.SE_RESIZE;
            case SOUTH_EDGE:
               return Cursor.S_RESIZE;
            case SOUTHWEST_EDGE:
               return Cursor.SW_RESIZE;
            case WEST_EDGE:
               return Cursor.W_RESIZE;
            case NORTHWEST_EDGE:
               return Cursor.NW_RESIZE;
            default:
               throw new IllegalArgumentException("The position " + selectionEvent.getPosition() + " is not fully implemented.");
         }
      }
   }

   private Cursor getRegularCursor() {
      return ((SnapshotView)this.getSkinnable()).getCursor();
   }

   private Rectangle2DChangeStrategy getChangeStrategy(SnapshotViewSkin.SelectionEvent selectionEvent) {
      boolean mousePressed = selectionEvent.getMouseEvent().getEventType() == MouseEvent.MOUSE_PRESSED;
      if (!mousePressed) {
         throw new IllegalArgumentException();
      } else {
         Rectangle2D selectionBounds = selectionEvent.getSelectionBounds();
         switch (selectionEvent.getPosition()) {
            case IN_RECTANGLE:
               return new MoveChangeStrategy(this.getSelection(), selectionBounds);
            case OUT_OF_RECTANGLE:
               return new NewChangeStrategy(this.isSelectionRatioFixed(), this.getSelectionRatio(), selectionBounds);
            case NORTH_EDGE:
               return new ToNorthChangeStrategy(this.getSelection(), this.isSelectionRatioFixed(), this.getSelectionRatio(), selectionBounds);
            case NORTHEAST_EDGE:
               return new ToNortheastChangeStrategy(this.getSelection(), this.isSelectionRatioFixed(), this.getSelectionRatio(), selectionBounds);
            case EAST_EDGE:
               return new ToEastChangeStrategy(this.getSelection(), this.isSelectionRatioFixed(), this.getSelectionRatio(), selectionBounds);
            case SOUTHEAST_EDGE:
               return new ToSoutheastChangeStrategy(this.getSelection(), this.isSelectionRatioFixed(), this.getSelectionRatio(), selectionBounds);
            case SOUTH_EDGE:
               return new ToSouthChangeStrategy(this.getSelection(), this.isSelectionRatioFixed(), this.getSelectionRatio(), selectionBounds);
            case SOUTHWEST_EDGE:
               return new ToSouthwestChangeStrategy(this.getSelection(), this.isSelectionRatioFixed(), this.getSelectionRatio(), selectionBounds);
            case WEST_EDGE:
               return new ToWestChangeStrategy(this.getSelection(), this.isSelectionRatioFixed(), this.getSelectionRatio(), selectionBounds);
            case NORTHWEST_EDGE:
               return new ToNorthwestChangeStrategy(this.getSelection(), this.isSelectionRatioFixed(), this.getSelectionRatio(), selectionBounds);
            default:
               throw new IllegalArgumentException("The position " + selectionEvent.getPosition() + " is not fully implemented.");
         }
      }
   }

   private static boolean willDeactivateSelectionIfClick(SnapshotViewSkin.SelectionEvent selectionEvent) {
      boolean rightClick = selectionEvent.getMouseEvent().getButton() == MouseButton.SECONDARY;
      boolean outOfAreaClick = selectionEvent.getPosition() == CoordinatePosition.OUT_OF_RECTANGLE;
      return rightClick || outOfAreaClick;
   }

   private double getControlWidth() {
      return ((SnapshotView)this.getSkinnable()).getWidth();
   }

   private double getControlHeight() {
      return ((SnapshotView)this.getSkinnable()).getHeight();
   }

   private Node getSnapshotNode() {
      return ((SnapshotView)this.getSkinnable()).getNode();
   }

   private Rectangle2D getSelection() {
      return ((SnapshotView)this.getSkinnable()).getSelection();
   }

   private boolean isSelectionRatioFixed() {
      return ((SnapshotView)this.getSkinnable()).isSelectionRatioFixed();
   }

   private double getSelectionRatio() {
      return ((SnapshotView)this.getSkinnable()).getFixedSelectionRatio();
   }

   private static class Clipper {
      private final SnapshotView snapshotView;
      private final Node clippedNode;
      private final Runnable rebindClippedNodeVisibility;
      private final Rectangle controlClip;
      private final Rectangle nodeClip;
      private final ChangeListener<Bounds> updateControlClipToNewBoundsListener;
      private final ChangeListener<Bounds> updateNodeClipToNewBoundsListener;

      public Clipper(SnapshotView snapshotView, Node clippedNode, Runnable rebindClippedNodeVisibility) {
         this.snapshotView = snapshotView;
         this.clippedNode = clippedNode;
         this.rebindClippedNodeVisibility = rebindClippedNodeVisibility;
         this.controlClip = new Rectangle();
         this.updateControlClipToNewBoundsListener = (o, oldBounds, newBounds) -> resizeRectangleToBounds(this.controlClip, newBounds);
         this.nodeClip = new Rectangle();
         this.updateNodeClipToNewBoundsListener = (o, oldBounds, newBounds) -> resizeRectangleToBounds(this.nodeClip, newBounds);
         this.setClipping();
         snapshotView.unselectedAreaBoundaryProperty().addListener((o, oldBoundary, newBoundary) -> this.setClipping());
      }

      private void setClipping() {
         SnapshotView.Boundary boundary = this.snapshotView.getUnselectedAreaBoundary();
         switch (boundary) {
            case CONTROL:
               this.clipToControl();
               break;
            case NODE:
               this.clipToNode();
               break;
            default:
               throw new IllegalArgumentException("The boundary " + boundary + " is not fully implemented.");
         }
      }

      private void clipToControl() {
         this.updateNodeClipToChangingNode(this.snapshotView.nodeProperty(), this.snapshotView.getNode(), null);
         resizeRectangleToBounds(this.controlClip, this.snapshotView.getBoundsInLocal());
         this.snapshotView.boundsInLocalProperty().addListener(this.updateControlClipToNewBoundsListener);
         this.setClip(this.controlClip);
      }

      private void clipToNode() {
         this.updateNodeClipToChangingNode(this.snapshotView.nodeProperty(), null, this.snapshotView.getNode());
         this.snapshotView.nodeProperty().addListener(this::updateNodeClipToChangingNode);
         this.setClip(this.nodeClip);
      }

      private void updateNodeClipToChangingNode(ObservableValue<? extends Node> o, Node oldNode, Node newNode) {
         resizeRectangleToNodeBounds(this.nodeClip, newNode);
         if (oldNode != null) {
            oldNode.boundsInParentProperty().removeListener(this.updateNodeClipToNewBoundsListener);
         }

         if (newNode != null) {
            newNode.boundsInParentProperty().addListener(this.updateNodeClipToNewBoundsListener);
         }
      }

      private static void resizeRectangleToNodeBounds(Rectangle rectangle, Node node) {
         if (node == null) {
            resizeRectangleToZero(rectangle);
         } else {
            resizeRectangleToBounds(rectangle, node.getBoundsInParent());
         }
      }

      private static void resizeRectangleToZero(Rectangle rectangle) {
         rectangle.setX(0.0);
         rectangle.setY(0.0);
         rectangle.setWidth(0.0);
         rectangle.setHeight(0.0);
      }

      private static void resizeRectangleToBounds(Rectangle rectangle, Bounds bounds) {
         rectangle.setX(bounds.getMinX());
         rectangle.setY(bounds.getMinY());
         rectangle.setWidth(bounds.getWidth());
         rectangle.setHeight(bounds.getHeight());
      }

      private void setClip(Node clip) {
         boolean workAroundVisibilityProblem = !this.clippedNode.isVisible();
         if (workAroundVisibilityProblem) {
            this.clippedNode.visibleProperty().unbind();
            this.clippedNode.setVisible(true);
         }

         this.clippedNode.setClip(clip);
         if (workAroundVisibilityProblem) {
            this.rebindClippedNodeVisibility.run();
         }
      }
   }

   private static class NoSelectionChange implements SnapshotViewSkin.SelectionChange {
      public static final SnapshotViewSkin.NoSelectionChange INSTANCE = new SnapshotViewSkin.NoSelectionChange();

      @Override
      public void beginSelectionChange(Point2D point) {
      }

      @Override
      public void continueSelectionChange(Point2D point) {
      }

      @Override
      public void endSelectionChange(Point2D point) {
      }

      @Override
      public Cursor getCursor() {
         return Cursor.DEFAULT;
      }
   }

   private interface SelectionChange {
      void beginSelectionChange(Point2D var1);

      void continueSelectionChange(Point2D var1);

      void endSelectionChange(Point2D var1);

      Cursor getCursor();
   }

   private static class SelectionChangeByStrategy implements SnapshotViewSkin.SelectionChange {
      private final SnapshotView snapshotView;
      private final Consumer<Boolean> setSelectionChanging;
      private final Rectangle2DChangeStrategy selectionChangeStrategy;
      private final Cursor cursor;
      private final boolean deactivateSelectionIfClick;
      private Point2D startingPoint;
      private boolean mouseMoved;

      public SelectionChangeByStrategy(
         SnapshotView snapshotView,
         Consumer<Boolean> setSelectionChanging,
         Rectangle2DChangeStrategy selectionChangeStrategy,
         Cursor cursor,
         boolean deactivateSelectionIfClick
      ) {
         this.snapshotView = snapshotView;
         this.setSelectionChanging = setSelectionChanging;
         this.selectionChangeStrategy = selectionChangeStrategy;
         this.cursor = cursor;
         this.deactivateSelectionIfClick = deactivateSelectionIfClick;
      }

      @Override
      public void beginSelectionChange(Point2D point) {
         this.startingPoint = point;
         this.setSelectionChanging.accept(true);
         Rectangle2D newSelection = this.selectionChangeStrategy.beginChange(point);
         this.snapshotView.setSelection(newSelection);
      }

      @Override
      public void continueSelectionChange(Point2D point) {
         this.updateMouseMoved(point);
         Rectangle2D newSelection = this.selectionChangeStrategy.continueChange(point);
         this.snapshotView.setSelection(newSelection);
      }

      @Override
      public void endSelectionChange(Point2D point) {
         this.updateMouseMoved(point);
         Rectangle2D newSelection = this.selectionChangeStrategy.endChange(point);
         this.snapshotView.setSelection(newSelection);
         boolean deactivateSelection = this.deactivateSelectionIfClick && !this.mouseMoved;
         if (deactivateSelection) {
            this.snapshotView.setSelection(null);
         }

         this.setSelectionChanging.accept(false);
      }

      private void updateMouseMoved(Point2D point) {
         if (!this.mouseMoved) {
            boolean mouseMovedNow = !this.startingPoint.equals(point);
            this.mouseMoved = mouseMovedNow;
         }
      }

      @Override
      public Cursor getCursor() {
         return this.cursor;
      }
   }

   private static class SelectionEvent {
      private final MouseEvent mouseEvent;
      private final Point2D point;
      private final Rectangle2D selectionBounds;
      private final CoordinatePosition position;

      public SelectionEvent(MouseEvent mouseEvent, Point2D point, Rectangle2D selectionBounds, CoordinatePosition position) {
         this.mouseEvent = mouseEvent;
         this.point = point;
         this.selectionBounds = selectionBounds;
         this.position = position;
      }

      public MouseEvent getMouseEvent() {
         return this.mouseEvent;
      }

      public Point2D getPoint() {
         return this.point;
      }

      public Rectangle2D getSelectionBounds() {
         return this.selectionBounds;
      }

      public boolean isPointInSelectionBounds() {
         return this.selectionBounds.contains(this.point);
      }

      public CoordinatePosition getPosition() {
         return this.position;
      }
   }
}
