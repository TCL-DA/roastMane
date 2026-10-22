package impl.org.controlsfx.skin;

import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.controlsfx.control.MasterDetailPane;

public class MasterDetailPaneSkin extends SkinBase<MasterDetailPane> {
   private boolean changing = false;
   private SplitPane splitPane;
   private final Timeline timeline = new Timeline();
   private BooleanProperty showDetailForTimeline = new SimpleBooleanProperty();
   private InvalidationListener listenersDivider = new InvalidationListener() {
      public void invalidated(Observable arg0) {
         MasterDetailPaneSkin.this.changing = true;
         MasterDetailPaneSkin.this.splitPane.setDividerPosition(0, ((MasterDetailPane)MasterDetailPaneSkin.this.getSkinnable()).getDividerPosition());
         MasterDetailPaneSkin.this.changing = false;
      }
   };
   private ChangeListener<Number> updateDividerPositionListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
         if (!MasterDetailPaneSkin.this.changing) {
            ((MasterDetailPane)MasterDetailPaneSkin.this.getSkinnable()).setDividerPosition(t1.doubleValue());
         }
      }
   };

   public MasterDetailPaneSkin(MasterDetailPane pane) {
      super(pane);
      this.splitPane = new SplitPane();
      this.splitPane.setDividerPosition(0, pane.getDividerPosition());
      this.splitPane.getDividers().addListener(change -> {
         while (change.next()) {
            if (change.wasAdded()) {
               ((Divider)change.getAddedSubList().get(0)).positionProperty().addListener(this.updateDividerPositionListener);
            } else if (change.wasRemoved()) {
               ((Divider)change.getRemoved().get(0)).positionProperty().removeListener(this.updateDividerPositionListener);
            }
         }
      });
      SplitPane.setResizableWithParent(((MasterDetailPane)this.getSkinnable()).getDetailNode(), false);
      switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
         case BOTTOM:
         case TOP:
            this.splitPane.setOrientation(Orientation.VERTICAL);
            break;
         case LEFT:
         case RIGHT:
            this.splitPane.setOrientation(Orientation.HORIZONTAL);
      }

      ((MasterDetailPane)this.getSkinnable())
         .masterNodeProperty()
         .addListener(
            (observable, oldNode, newNode) -> {
               if (oldNode != null) {
                  this.splitPane.getItems().remove(oldNode);
               }

               if (newNode != null) {
                  int masterIndex;
                  this.updateMinAndMaxSizes();
                  masterIndex = 0;
                  label23:
                  switch (this.splitPane.getOrientation()) {
                     case HORIZONTAL:
                        switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
                           case LEFT:
                              masterIndex = 1;
                              break label23;
                           case RIGHT:
                              masterIndex = 0;
                              break label23;
                           default:
                              throw new IllegalArgumentException(
                                 "illegal details position "
                                    + ((MasterDetailPane)this.getSkinnable()).getDetailSide()
                                    + " for orientation "
                                    + this.splitPane.getOrientation()
                              );
                        }
                     case VERTICAL:
                        switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
                           case BOTTOM:
                              masterIndex = 0;
                              break;
                           case TOP:
                              masterIndex = 1;
                              break;
                           default:
                              throw new IllegalArgumentException(
                                 "illegal details position "
                                    + ((MasterDetailPane)this.getSkinnable()).getDetailSide()
                                    + " for orientation "
                                    + this.splitPane.getOrientation()
                              );
                        }
                  }

                  List<Node> items = this.splitPane.getItems();
                  if (items.isEmpty()) {
                     items.add(newNode);
                  } else {
                     items.add(masterIndex, newNode);
                  }
               }
            }
         );
      ((MasterDetailPane)this.getSkinnable())
         .detailNodeProperty()
         .addListener(
            (observable, oldNode, newNode) -> {
               if (oldNode != null) {
                  this.splitPane.getItems().remove(oldNode);
               }

               if (newNode != null && ((MasterDetailPane)this.getSkinnable()).isShowDetailNode()) {
                  int detailsIndex;
                  this.splitPane.setDividerPositions(new double[]{((MasterDetailPane)this.getSkinnable()).getDividerPosition()});
                  this.updateMinAndMaxSizes();
                  SplitPane.setResizableWithParent(newNode, false);
                  detailsIndex = 0;
                  label24:
                  switch (this.splitPane.getOrientation()) {
                     case HORIZONTAL:
                        switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
                           case LEFT:
                              detailsIndex = 0;
                              break label24;
                           case RIGHT:
                              detailsIndex = 1;
                              break label24;
                           default:
                              throw new IllegalArgumentException(
                                 "illegal details position "
                                    + ((MasterDetailPane)this.getSkinnable()).getDetailSide()
                                    + " for orientation "
                                    + this.splitPane.getOrientation()
                              );
                        }
                     case VERTICAL:
                        switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
                           case BOTTOM:
                              detailsIndex = 1;
                              break;
                           case TOP:
                              detailsIndex = 0;
                              break;
                           default:
                              throw new IllegalArgumentException(
                                 "illegal details position "
                                    + ((MasterDetailPane)this.getSkinnable()).getDetailSide()
                                    + " for orientation "
                                    + this.splitPane.getOrientation()
                              );
                        }
                  }

                  List<Node> items = this.splitPane.getItems();
                  if (items.isEmpty()) {
                     items.add(newNode);
                  } else {
                     items.add(detailsIndex, newNode);
                  }
               }
            }
         );
      ((MasterDetailPane)this.getSkinnable()).showDetailNodeProperty().addListener((observable, oldShow, newShow) -> {
         if (((MasterDetailPane)this.getSkinnable()).isAnimated() && this.timeline.getStatus() == Status.RUNNING) {
            this.timeline.jumpTo("endAnimation");
            this.timeline.getOnFinished().handle(null);
         }

         if (newShow) {
            this.open();
         } else {
            this.close();
         }
      });
      ((MasterDetailPane)this.getSkinnable()).detailSideProperty().addListener((observable, oldPos, newPos) -> {
         Node detailNode = ((MasterDetailPane)this.getSkinnable()).getDetailNode();
         Node masterNode = ((MasterDetailPane)this.getSkinnable()).getMasterNode();
         boolean showDetailNode = ((MasterDetailPane)this.getSkinnable()).isShowDetailNode() && detailNode != null;
         if (showDetailNode) {
            this.splitPane.getItems().clear();
         }

         switch (newPos) {
            case BOTTOM:
            case TOP:
               this.splitPane.setOrientation(Orientation.VERTICAL);
               break;
            case LEFT:
            case RIGHT:
               this.splitPane.setOrientation(Orientation.HORIZONTAL);
         }

         label36:
         switch (newPos) {
            case BOTTOM:
            case RIGHT:
               if (showDetailNode) {
                  this.splitPane.getItems().add(masterNode);
                  this.splitPane.getItems().add(detailNode);
               }

               switch (oldPos) {
                  case TOP:
                  case LEFT:
                     ((MasterDetailPane)this.getSkinnable()).setDividerPosition(1.0 - ((MasterDetailPane)this.getSkinnable()).getDividerPosition());
                  default:
                     break label36;
               }
            case TOP:
            case LEFT:
               if (showDetailNode) {
                  this.splitPane.getItems().add(detailNode);
                  this.splitPane.getItems().add(masterNode);
               }

               switch (oldPos) {
                  case BOTTOM:
                  case RIGHT:
                     ((MasterDetailPane)this.getSkinnable()).setDividerPosition(1.0 - ((MasterDetailPane)this.getSkinnable()).getDividerPosition());
               }
         }

         if (showDetailNode) {
            this.splitPane.setDividerPositions(new double[]{((MasterDetailPane)this.getSkinnable()).getDividerPosition()});
         }
      });
      this.updateMinAndMaxSizes();
      this.getChildren().add(this.splitPane);
      this.splitPane.getItems().add(((MasterDetailPane)this.getSkinnable()).getMasterNode());
      if (((MasterDetailPane)this.getSkinnable()).isShowDetailNode()) {
         switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
            case BOTTOM:
            case RIGHT:
               this.splitPane.getItems().add(((MasterDetailPane)this.getSkinnable()).getDetailNode());
               break;
            case TOP:
            case LEFT:
               this.splitPane.getItems().add(0, ((MasterDetailPane)this.getSkinnable()).getDetailNode());
         }

         this.bindDividerPosition();
      }

      this.timeline.setOnFinished(evt -> {
         if (!this.showDetailForTimeline.get()) {
            this.unbindDividerPosition();
            this.splitPane.getItems().remove(((MasterDetailPane)this.getSkinnable()).getDetailNode());
            ((MasterDetailPane)this.getSkinnable()).getDetailNode().setOpacity(1.0);
         }

         this.changing = false;
      });
   }

   private void bindDividerPosition() {
      ((MasterDetailPane)this.getSkinnable()).dividerPositionProperty().addListener(this.listenersDivider);
   }

   private void unbindDividerPosition() {
      ((MasterDetailPane)this.getSkinnable()).dividerPositionProperty().removeListener(this.listenersDivider);
   }

   private void updateMinAndMaxSizes() {
      if (((MasterDetailPane)this.getSkinnable()).getMasterNode() instanceof Region) {
         ((Region)((MasterDetailPane)this.getSkinnable()).getMasterNode()).setMinSize(0.0, 0.0);
         ((Region)((MasterDetailPane)this.getSkinnable()).getMasterNode()).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      }

      if (((MasterDetailPane)this.getSkinnable()).getDetailNode() instanceof Region) {
         ((Region)((MasterDetailPane)this.getSkinnable()).getDetailNode()).setMinSize(0.0, 0.0);
         ((Region)((MasterDetailPane)this.getSkinnable()).getDetailNode()).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      }
   }

   private void open() {
      this.changing = true;
      Node node = ((MasterDetailPane)this.getSkinnable()).getDetailNode();
      if (node != null) {
         switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
            case BOTTOM:
            case RIGHT:
               this.splitPane.getItems().add(node);
               this.splitPane.setDividerPositions(new double[]{1.0});
               break;
            case TOP:
            case LEFT:
               this.splitPane.getItems().add(0, node);
               this.splitPane.setDividerPositions(new double[]{0.0});
         }

         this.updateMinAndMaxSizes();
         this.maybeAnimatePositionChange(((MasterDetailPane)this.getSkinnable()).getDividerPosition(), true);
      }
   }

   private void close() {
      this.changing = true;
      if (!this.splitPane.getDividers().isEmpty()) {
         double targetLocation = 0.0;
         switch (((MasterDetailPane)this.getSkinnable()).getDetailSide()) {
            case BOTTOM:
            case RIGHT:
               targetLocation = 1.0;
            default:
               this.maybeAnimatePositionChange(targetLocation, false);
         }
      }
   }

   private void maybeAnimatePositionChange(double position, boolean showDetail) {
      Node detailNode = ((MasterDetailPane)this.getSkinnable()).getDetailNode();
      if (detailNode != null) {
         this.showDetailForTimeline.set(showDetail);
         Divider divider = (Divider)this.splitPane.getDividers().get(0);
         if (this.showDetailForTimeline.get()) {
            this.unbindDividerPosition();
            this.bindDividerPosition();
         }

         if (((MasterDetailPane)this.getSkinnable()).isAnimated() && detailNode != null) {
            KeyValue positionKeyValue = new KeyValue(divider.positionProperty(), position);
            KeyValue opacityKeyValue = new KeyValue(detailNode.opacityProperty(), this.showDetailForTimeline.get() ? 1 : 0);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.1), "endAnimation", new KeyValue[]{positionKeyValue, opacityKeyValue});
            this.timeline.getKeyFrames().clear();
            this.timeline.getKeyFrames().add(keyFrame);
            this.timeline.playFromStart();
         } else {
            detailNode.setOpacity(1.0);
            divider.setPosition(position);
            if (!this.showDetailForTimeline.get()) {
               this.unbindDividerPosition();
               this.splitPane.getItems().remove(((MasterDetailPane)this.getSkinnable()).getDetailNode());
            }

            this.changing = false;
         }
      }
   }
}
