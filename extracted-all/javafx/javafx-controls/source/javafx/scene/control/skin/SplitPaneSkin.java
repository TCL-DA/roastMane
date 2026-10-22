package javafx.scene.control.skin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class SplitPaneSkin extends SkinBase<SplitPane> {
   private ObservableList<SplitPaneSkin.Content> contentRegions;
   private ObservableList<SplitPaneSkin.ContentDivider> contentDividers;
   private boolean horizontal;
   private double previousSize = -1.0;
   private int lastDividerUpdate = 0;
   private boolean resize = false;
   private boolean checkDividerPos = true;

   public SplitPaneSkin(SplitPane var1) {
      super(var1);
      this.horizontal = this.getSkinnable().getOrientation() == Orientation.HORIZONTAL;
      this.contentRegions = FXCollections.observableArrayList();
      this.contentDividers = FXCollections.observableArrayList();
      int var2 = 0;

      for (Node var4 : this.getSkinnable().getItems()) {
         this.addContent(var2++, var4);
      }

      this.initializeContentListener();

      for (SplitPane.Divider var6 : this.getSkinnable().getDividers()) {
         this.addDivider(var6);
      }

      this.registerChangeListener(var1.orientationProperty(), var1x -> {
         this.horizontal = this.getSkinnable().getOrientation() == Orientation.HORIZONTAL;
         this.previousSize = -1.0;

         for (SplitPaneSkin.ContentDivider var3 : this.contentDividers) {
            var3.setGrabberStyle(this.horizontal);
         }

         this.getSkinnable().requestLayout();
      });
      this.registerChangeListener(var1.widthProperty(), var1x -> this.getSkinnable().requestLayout());
      this.registerChangeListener(var1.heightProperty(), var1x -> this.getSkinnable().requestLayout());
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      SplitPane var9 = this.getSkinnable();
      double var10 = var9.getWidth();
      double var12 = var9.getHeight();
      if ((this.horizontal ? var10 != 0.0 : var12 != 0.0) && !this.contentRegions.isEmpty()) {
         double var14 = this.contentDividers.isEmpty() ? 0.0 : ((SplitPaneSkin.ContentDivider)this.contentDividers.get(0)).prefWidth(-1.0);
         if (this.contentDividers.size() > 0 && this.previousSize != -1.0 && this.previousSize != (this.horizontal ? var10 : var12)) {
            ArrayList var16 = new ArrayList();

            for (SplitPaneSkin.Content var18 : this.contentRegions) {
               if (var18.isResizableWithParent()) {
                  var16.add(var18);
               }
            }

            double var38 = (this.horizontal ? var9.getWidth() : var9.getHeight()) - this.previousSize;
            boolean var19 = var38 > 0.0;
            var38 = Math.abs(var38);
            if (var38 != 0.0 && !var16.isEmpty()) {
               int var20 = (int)var38 / var16.size();
               int var21 = (int)var38 % var16.size();
               int var22 = 0;
               if (var20 == 0) {
                  var20 = var21;
                  var22 = var21;
                  var21 = 0;
               } else {
                  var22 = var20 * var16.size();
               }

               while (var22 > 0 && !var16.isEmpty()) {
                  if (var19) {
                     this.lastDividerUpdate++;
                  } else {
                     this.lastDividerUpdate--;
                     if (this.lastDividerUpdate < 0) {
                        this.lastDividerUpdate = this.contentRegions.size() - 1;
                     }
                  }

                  int var23 = this.lastDividerUpdate % this.contentRegions.size();
                  SplitPaneSkin.Content var24 = (SplitPaneSkin.Content)this.contentRegions.get(var23);
                  if (var24.isResizableWithParent() && var16.contains(var24)) {
                     double var25 = var24.getArea();
                     if (var19) {
                        double var27 = this.horizontal ? var24.maxWidth(-1.0) : var24.maxHeight(-1.0);
                        if (!(var25 + (double)var20 <= var27)) {
                           var16.remove(var24);
                           continue;
                        }

                        var25 += (double)var20;
                     } else {
                        double var64 = this.horizontal ? var24.minWidth(-1.0) : var24.minHeight(-1.0);
                        if (!(var25 - (double)var20 >= var64)) {
                           var16.remove(var24);
                           continue;
                        }

                        var25 -= (double)var20;
                     }

                     var24.setArea(var25);
                     var22 -= var20;
                     if (var22 == 0 && var21 != 0) {
                        var20 = var21;
                        var22 = var21;
                        var21 = 0;
                     } else if (var22 == 0) {
                        break;
                     }
                  }
               }

               for (SplitPaneSkin.Content var58 : this.contentRegions) {
                  var58.setResizableWithParentArea(var58.getArea());
                  var58.setAvailable(0.0);
               }

               this.resize = true;
            }

            this.previousSize = this.horizontal ? var10 : var12;
         } else {
            this.previousSize = this.horizontal ? var10 : var12;
         }

         double var37 = this.totalMinSize();
         if (var37 > (this.horizontal ? var5 : var7)) {
            double var41 = 0.0;

            for (int var45 = 0; var45 < this.contentRegions.size(); var45++) {
               SplitPaneSkin.Content var51 = (SplitPaneSkin.Content)this.contentRegions.get(var45);
               double var54 = this.horizontal ? var51.minWidth(-1.0) : var51.minHeight(-1.0);
               var41 = var54 / var37;
               if (this.horizontal) {
                  var51.setArea(this.snapSpaceX(var41 * var5));
               } else {
                  var51.setArea(this.snapSpaceY(var41 * var7));
               }

               var51.setAvailable(0.0);
            }

            this.setupContentAndDividerForLayout();
            this.layoutDividersAndContent(var5, var7);
            this.resize = false;
         } else {
            for (int var40 = 0; var40 < 10; var40++) {
               SplitPaneSkin.ContentDivider var43 = null;
               SplitPaneSkin.ContentDivider var44 = null;

               for (int var46 = 0; var46 < this.contentRegions.size(); var46++) {
                  double var53 = 0.0;
                  if (var46 < this.contentDividers.size()) {
                     var44 = (SplitPaneSkin.ContentDivider)this.contentDividers.get(var46);
                     if (var44.posExplicit) {
                        this.checkDividerPosition(var44, this.posToDividerPos(var44, var44.d.getPosition()), var44.getDividerPos());
                     }

                     if (var46 == 0) {
                        var53 = this.getAbsoluteDividerPos(var44);
                     } else {
                        double var59 = this.getAbsoluteDividerPos(var43) + var14;
                        if (this.getAbsoluteDividerPos(var44) <= this.getAbsoluteDividerPos(var43)) {
                           this.setAndCheckAbsoluteDividerPos(var44, var59);
                        }

                        var53 = this.getAbsoluteDividerPos(var44) - var59;
                     }
                  } else if (var46 == this.contentDividers.size()) {
                     var53 = (this.horizontal ? var5 : var7) - (var43 != null ? this.getAbsoluteDividerPos(var43) + var14 : 0.0);
                  }

                  if (!this.resize || var44.posExplicit) {
                     ((SplitPaneSkin.Content)this.contentRegions.get(var46)).setArea(var53);
                  }

                  var43 = var44;
               }

               double var47 = 0.0;
               double var56 = 0.0;

               for (SplitPaneSkin.Content var26 : this.contentRegions) {
                  if (var26 != null) {
                     double var65 = this.horizontal ? var26.maxWidth(-1.0) : var26.maxHeight(-1.0);
                     double var29 = this.horizontal ? var26.minWidth(-1.0) : var26.minHeight(-1.0);
                     if (var26.getArea() >= var65) {
                        var56 += var26.getArea() - var65;
                        var26.setArea(var65);
                     }

                     var26.setAvailable(var26.getArea() - var29);
                     if (var26.getAvailable() < 0.0) {
                        var47 += var26.getAvailable();
                     }
                  }
               }

               var47 = Math.abs(var47);
               ArrayList var62 = new ArrayList();
               ArrayList var63 = new ArrayList();
               ArrayList var66 = new ArrayList();
               double var28 = 0.0;

               for (SplitPaneSkin.Content var31 : this.contentRegions) {
                  if (var31.getAvailable() >= 0.0) {
                     var28 += var31.getAvailable();
                     var62.add(var31);
                  }

                  if (this.resize && !var31.isResizableWithParent()) {
                     if (var31.getArea() >= var31.getResizableWithParentArea()) {
                        var56 += var31.getArea() - var31.getResizableWithParentArea();
                     } else {
                        var47 += var31.getResizableWithParentArea() - var31.getArea();
                     }

                     var31.setAvailable(0.0);
                  }

                  if (this.resize) {
                     if (var31.isResizableWithParent()) {
                        var63.add(var31);
                     }
                  } else {
                     var63.add(var31);
                  }

                  if (var31.getAvailable() < 0.0) {
                     var66.add(var31);
                  }
               }

               if (var56 > 0.0) {
                  var56 = this.distributeTo(var63, var56);
                  var47 = 0.0;
                  var66.clear();
                  var28 = 0.0;
                  var62.clear();

                  for (SplitPaneSkin.Content var72 : this.contentRegions) {
                     if (var72.getAvailable() < 0.0) {
                        var47 += var72.getAvailable();
                        var66.add(var72);
                     } else {
                        var28 += var72.getAvailable();
                        var62.add(var72);
                     }
                  }

                  var47 = Math.abs(var47);
               }

               if (var28 >= var47) {
                  for (SplitPaneSkin.Content var73 : var66) {
                     double var32 = this.horizontal ? var73.minWidth(-1.0) : var73.minHeight(-1.0);
                     var73.setArea(var32);
                     var73.setAvailable(0.0);
                  }

                  if (var47 > 0.0 && !var66.isEmpty()) {
                     this.distributeFrom(var47, var62);
                  }

                  if (this.resize) {
                     double var69 = 0.0;

                     for (SplitPaneSkin.Content var33 : this.contentRegions) {
                        if (var33.isResizableWithParent()) {
                           var69 += var33.getArea();
                        } else {
                           var69 += var33.getResizableWithParentArea();
                        }
                     }

                     var69 += var14 * (double)this.contentDividers.size();
                     if (var69 < (this.horizontal ? var5 : var7)) {
                        var56 += (this.horizontal ? var5 : var7) - var69;
                        this.distributeTo(var63, var56);
                     } else {
                        var47 += var69 - (this.horizontal ? var5 : var7);
                        this.distributeFrom(var47, var63);
                     }
                  }
               }

               this.setupContentAndDividerForLayout();
               boolean var71 = true;

               for (SplitPaneSkin.Content var76 : this.contentRegions) {
                  double var77 = this.horizontal ? var76.maxWidth(-1.0) : var76.maxHeight(-1.0);
                  double var35 = this.horizontal ? var76.minWidth(-1.0) : var76.minHeight(-1.0);
                  if (var76.getArea() < var35 || var76.getArea() > var77) {
                     var71 = false;
                     break;
                  }
               }

               if (var71) {
                  break;
               }
            }

            this.layoutDividersAndContent(var5, var7);
            this.resize = false;
         }
      }
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      double var13 = 0.0;

      for (SplitPaneSkin.Content var16 : this.contentRegions) {
         var11 += var16.minWidth(-1.0);
         var13 = Math.max(var13, var16.minWidth(-1.0));
      }

      for (SplitPaneSkin.ContentDivider var18 : this.contentDividers) {
         var11 += var18.prefWidth(-1.0);
      }

      return this.horizontal ? var11 + var9 + var5 : var13 + var9 + var5;
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      double var13 = 0.0;

      for (SplitPaneSkin.Content var16 : this.contentRegions) {
         var11 += var16.minHeight(-1.0);
         var13 = Math.max(var13, var16.minHeight(-1.0));
      }

      for (SplitPaneSkin.ContentDivider var18 : this.contentDividers) {
         var11 += var18.prefWidth(-1.0);
      }

      return this.horizontal ? var13 + var3 + var7 : var11 + var3 + var7;
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      double var13 = 0.0;

      for (SplitPaneSkin.Content var16 : this.contentRegions) {
         var11 += var16.prefWidth(-1.0);
         var13 = Math.max(var13, var16.prefWidth(-1.0));
      }

      for (SplitPaneSkin.ContentDivider var18 : this.contentDividers) {
         var11 += var18.prefWidth(-1.0);
      }

      return this.horizontal ? var11 + var9 + var5 : var13 + var9 + var5;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      double var13 = 0.0;

      for (SplitPaneSkin.Content var16 : this.contentRegions) {
         var11 += var16.prefHeight(-1.0);
         var13 = Math.max(var13, var16.prefHeight(-1.0));
      }

      for (SplitPaneSkin.ContentDivider var18 : this.contentDividers) {
         var11 += var18.prefWidth(-1.0);
      }

      return this.horizontal ? var13 + var3 + var7 : var11 + var3 + var7;
   }

   private void addContent(int var1, Node var2) {
      SplitPaneSkin.Content var3 = new SplitPaneSkin.Content(var2);
      this.contentRegions.add(var1, var3);
      this.getChildren().add(var1, var3);
   }

   private void removeContent(Node var1) {
      for (SplitPaneSkin.Content var3 : this.contentRegions) {
         if (var3.getContent().equals(var1)) {
            var3.dispose();
            this.getChildren().remove(var3);
            this.contentRegions.remove(var3);
            break;
         }
      }
   }

   private void initializeContentListener() {
      this.getSkinnable().getItems().addListener(var1 -> {
         while (var1.next()) {
            if (!var1.wasPermutated() && !var1.wasUpdated()) {
               for (Node var9 : var1.getRemoved()) {
                  this.removeContent(var9);
               }

               int var7 = var1.getFrom();

               for (Node var11 : var1.getAddedSubList()) {
                  this.addContent(var7++, var11);
               }
            } else {
               this.getChildren().clear();
               this.contentRegions.clear();
               int var5 = 0;

               for (Node var4 : var1.getList()) {
                  this.addContent(var5++, var4);
               }
            }
         }

         this.removeAllDividers();

         for (SplitPane.Divider var3 : this.getSkinnable().getDividers()) {
            this.addDivider(var3);
         }
      });
   }

   private void checkDividerPosition(SplitPaneSkin.ContentDivider var1, double var2, double var4) {
      double var6 = var1.prefWidth(-1.0);
      SplitPaneSkin.Content var8 = this.getLeft(var1);
      SplitPaneSkin.Content var9 = this.getRight(var1);
      double var10 = var8 == null ? 0.0 : (this.horizontal ? var8.minWidth(-1.0) : var8.minHeight(-1.0));
      double var12 = var9 == null ? 0.0 : (this.horizontal ? var9.minWidth(-1.0) : var9.minHeight(-1.0));
      double var14 = var8 == null
         ? 0.0
         : (var8.getContent() != null ? (this.horizontal ? var8.getContent().maxWidth(-1.0) : var8.getContent().maxHeight(-1.0)) : 0.0);
      double var16 = var9 == null
         ? 0.0
         : (var9.getContent() != null ? (this.horizontal ? var9.getContent().maxWidth(-1.0) : var9.getContent().maxHeight(-1.0)) : 0.0);
      double var18 = 0.0;
      double var20 = this.getSize();
      int var22 = this.contentDividers.indexOf(var1);
      if (var22 - 1 >= 0) {
         var18 = ((SplitPaneSkin.ContentDivider)this.contentDividers.get(var22 - 1)).getDividerPos();
         if (var18 == -1.0) {
            var18 = this.getAbsoluteDividerPos((SplitPaneSkin.ContentDivider)this.contentDividers.get(var22 - 1));
         }
      }

      if (var22 + 1 < this.contentDividers.size()) {
         var20 = ((SplitPaneSkin.ContentDivider)this.contentDividers.get(var22 + 1)).getDividerPos();
         if (var20 == -1.0) {
            var20 = this.getAbsoluteDividerPos((SplitPaneSkin.ContentDivider)this.contentDividers.get(var22 + 1));
         }
      }

      this.checkDividerPos = false;
      if (var2 > var4) {
         double var23 = var18 == 0.0 ? var14 : var18 + var6 + var14;
         double var25 = var20 - var12 - var6;
         double var27 = Math.min(var23, var25);
         if (var2 >= var27) {
            this.setAbsoluteDividerPos(var1, var27);
         } else {
            double var29 = var20 - var16 - var6;
            if (var2 <= var29) {
               this.setAbsoluteDividerPos(var1, var29);
            } else {
               this.setAbsoluteDividerPos(var1, var2);
            }
         }
      } else {
         double var31 = var20 - var16 - var6;
         double var32 = var18 == 0.0 ? var10 : var18 + var10 + var6;
         double var33 = Math.max(var31, var32);
         if (var2 <= var33) {
            this.setAbsoluteDividerPos(var1, var33);
         } else {
            double var34 = var18 + var14 + var6;
            if (var2 >= var34) {
               this.setAbsoluteDividerPos(var1, var34);
            } else {
               this.setAbsoluteDividerPos(var1, var2);
            }
         }
      }

      this.checkDividerPos = true;
   }

   private void addDivider(SplitPane.Divider var1) {
      SplitPaneSkin.ContentDivider var2 = new SplitPaneSkin.ContentDivider(var1);
      var2.setInitialPos(var1.getPosition());
      var2.setDividerPos(-1.0);
      SplitPaneSkin.PosPropertyListener var3 = new SplitPaneSkin.PosPropertyListener(var2);
      var2.setPosPropertyListener(var3);
      var1.positionProperty().addListener(var3);
      this.initializeDivderEventHandlers(var2);
      this.contentDividers.add(var2);
      this.getChildren().add(var2);
   }

   private void removeAllDividers() {
      ListIterator var1 = this.contentDividers.listIterator();

      while (var1.hasNext()) {
         SplitPaneSkin.ContentDivider var2 = (SplitPaneSkin.ContentDivider)var1.next();
         this.getChildren().remove(var2);
         var2.getDivider().positionProperty().removeListener(var2.getPosPropertyListener());
         var1.remove();
      }

      this.lastDividerUpdate = 0;
   }

   private void initializeDivderEventHandlers(SplitPaneSkin.ContentDivider var1) {
      var1.addEventHandler(MouseEvent.ANY, var0 -> var0.consume());
      var1.setOnMousePressed(
         var2 -> {
            if (this.horizontal) {
               var1.setInitialPos(var1.getDividerPos());
               var1.setPressPos(var2.getSceneX());
               var1.setPressPos(
                  this.getSkinnable().getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT
                     ? this.getSkinnable().getWidth() - var2.getSceneX()
                     : var2.getSceneX()
               );
            } else {
               var1.setInitialPos(var1.getDividerPos());
               var1.setPressPos(var2.getSceneY());
            }

            var2.consume();
         }
      );
      var1.setOnMouseDragged(
         var2 -> {
            double var3 = 0.0;
            if (this.horizontal) {
               var3 = this.getSkinnable().getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT
                  ? this.getSkinnable().getWidth() - var2.getSceneX()
                  : var2.getSceneX();
            } else {
               var3 = var2.getSceneY();
            }

            var3 -= var1.getPressPos();
            this.setAndCheckAbsoluteDividerPos(var1, Math.ceil(var1.getInitialPos() + var3));
            var2.consume();
         }
      );
   }

   private SplitPaneSkin.Content getLeft(SplitPaneSkin.ContentDivider var1) {
      int var2 = this.contentDividers.indexOf(var1);
      return var2 != -1 ? (SplitPaneSkin.Content)this.contentRegions.get(var2) : null;
   }

   private SplitPaneSkin.Content getRight(SplitPaneSkin.ContentDivider var1) {
      int var2 = this.contentDividers.indexOf(var1);
      return var2 != -1 ? (SplitPaneSkin.Content)this.contentRegions.get(var2 + 1) : null;
   }

   private void setAbsoluteDividerPos(SplitPaneSkin.ContentDivider var1, double var2) {
      if (this.getSkinnable().getWidth() > 0.0 && this.getSkinnable().getHeight() > 0.0 && var1 != null) {
         SplitPane.Divider var4 = var1.getDivider();
         var1.setDividerPos(var2);
         double var5 = this.getSize();
         if (var5 != 0.0) {
            double var7 = var2 + var1.prefWidth(-1.0) / 2.0;
            var4.setPosition(var7 / var5);
         } else {
            var4.setPosition(0.0);
         }
      }
   }

   private double getAbsoluteDividerPos(SplitPaneSkin.ContentDivider var1) {
      if (this.getSkinnable().getWidth() > 0.0 && this.getSkinnable().getHeight() > 0.0 && var1 != null) {
         SplitPane.Divider var2 = var1.getDivider();
         double var3 = this.posToDividerPos(var1, var2.getPosition());
         var1.setDividerPos(var3);
         return var3;
      } else {
         return 0.0;
      }
   }

   private double posToDividerPos(SplitPaneSkin.ContentDivider var1, double var2) {
      double var4 = this.getSize() * var2;
      if (var2 == 1.0) {
         var4 -= var1.prefWidth(-1.0);
      } else {
         var4 -= var1.prefWidth(-1.0) / 2.0;
      }

      return (double)Math.round(var4);
   }

   private double totalMinSize() {
      double var1 = !this.contentDividers.isEmpty()
         ? (double)this.contentDividers.size() * ((SplitPaneSkin.ContentDivider)this.contentDividers.get(0)).prefWidth(-1.0)
         : 0.0;
      double var3 = 0.0;

      for (SplitPaneSkin.Content var6 : this.contentRegions) {
         if (this.horizontal) {
            var3 += var6.minWidth(-1.0);
         } else {
            var3 += var6.minHeight(-1.0);
         }
      }

      return var3 + var1;
   }

   private double getSize() {
      SplitPane var1 = this.getSkinnable();
      double var2 = this.totalMinSize();
      if (this.horizontal) {
         if (var1.getWidth() > var2) {
            var2 = var1.getWidth() - this.snappedLeftInset() - this.snappedRightInset();
         }
      } else if (var1.getHeight() > var2) {
         var2 = var1.getHeight() - this.snappedTopInset() - this.snappedBottomInset();
      }

      return var2;
   }

   private double distributeTo(List<SplitPaneSkin.Content> var1, double var2) {
      if (var1.isEmpty()) {
         return var2;
      } else {
         var2 = this.horizontal ? this.snapSizeX(var2) : this.snapSizeY(var2);
         int var4 = (int)var2 / var1.size();

         while (var2 > 0.0 && !var1.isEmpty()) {
            Iterator var6 = var1.iterator();

            while (var6.hasNext()) {
               SplitPaneSkin.Content var7 = (SplitPaneSkin.Content)var6.next();
               double var8 = Math.min(this.horizontal ? var7.maxWidth(-1.0) : var7.maxHeight(-1.0), Double.MAX_VALUE);
               double var10 = this.horizontal ? var7.minWidth(-1.0) : var7.minHeight(-1.0);
               if (var7.getArea() >= var8) {
                  var7.setAvailable(var7.getArea() - var10);
                  var6.remove();
               } else {
                  if ((double)var4 >= var8 - var7.getArea()) {
                     var2 -= var8 - var7.getArea();
                     var7.setArea(var8);
                     var7.setAvailable(var8 - var10);
                     var6.remove();
                  } else {
                     var7.setArea(var7.getArea() + (double)var4);
                     var7.setAvailable(var7.getArea() - var10);
                     var2 -= (double)var4;
                  }

                  if ((int)var2 == 0) {
                     return var2;
                  }
               }
            }

            if (var1.isEmpty()) {
               return var2;
            }

            var4 = (int)var2 / var1.size();
            int var5 = (int)var2 % var1.size();
            if (var4 == 0 && var5 != 0) {
               var4 = var5;
               boolean var13 = false;
            }
         }

         return var2;
      }
   }

   private double distributeFrom(double var1, List<SplitPaneSkin.Content> var3) {
      if (var3.isEmpty()) {
         return var1;
      } else {
         var1 = this.horizontal ? this.snapSizeX(var1) : this.snapSizeY(var1);
         int var4 = (int)var1 / var3.size();

         while (var1 > 0.0 && !var3.isEmpty()) {
            Iterator var6 = var3.iterator();

            while (var6.hasNext()) {
               SplitPaneSkin.Content var7 = (SplitPaneSkin.Content)var6.next();
               if ((double)var4 >= var7.getAvailable()) {
                  var7.setArea(var7.getArea() - var7.getAvailable());
                  var1 -= var7.getAvailable();
                  var7.setAvailable(0.0);
                  var6.remove();
               } else {
                  var7.setArea(var7.getArea() - (double)var4);
                  var7.setAvailable(var7.getAvailable() - (double)var4);
                  var1 -= (double)var4;
               }

               if ((int)var1 == 0) {
                  return var1;
               }
            }

            if (var3.isEmpty()) {
               return var1;
            }

            var4 = (int)var1 / var3.size();
            int var5 = (int)var1 % var3.size();
            if (var4 == 0 && var5 != 0) {
               var4 = var5;
               boolean var9 = false;
            }
         }

         return var1;
      }
   }

   private void setupContentAndDividerForLayout() {
      double var1 = this.contentDividers.isEmpty() ? 0.0 : ((SplitPaneSkin.ContentDivider)this.contentDividers.get(0)).prefWidth(-1.0);
      double var3 = 0.0;
      double var5 = 0.0;

      for (SplitPaneSkin.Content var8 : this.contentRegions) {
         if (this.resize && !var8.isResizableWithParent()) {
            var8.setArea(var8.getResizableWithParentArea());
         }

         var8.setX(var3);
         var8.setY(var5);
         if (this.horizontal) {
            var3 += var8.getArea() + var1;
         } else {
            var5 += var8.getArea() + var1;
         }
      }

      var3 = 0.0;
      var5 = 0.0;
      this.checkDividerPos = false;

      for (int var11 = 0; var11 < this.contentDividers.size(); var11++) {
         SplitPaneSkin.ContentDivider var12 = (SplitPaneSkin.ContentDivider)this.contentDividers.get(var11);
         if (this.horizontal) {
            var3 += this.getLeft(var12).getArea() + (var11 == 0 ? 0.0 : var1);
         } else {
            var5 += this.getLeft(var12).getArea() + (var11 == 0 ? 0.0 : var1);
         }

         var12.setX(var3);
         var12.setY(var5);
         this.setAbsoluteDividerPos(var12, this.horizontal ? var12.getX() : var12.getY());
         var12.posExplicit = false;
      }

      this.checkDividerPos = true;
   }

   private void layoutDividersAndContent(double var1, double var3) {
      double var5 = this.snappedLeftInset();
      double var7 = this.snappedTopInset();
      double var9 = this.contentDividers.isEmpty() ? 0.0 : ((SplitPaneSkin.ContentDivider)this.contentDividers.get(0)).prefWidth(-1.0);

      for (SplitPaneSkin.Content var12 : this.contentRegions) {
         if (this.horizontal) {
            var12.setClipSize(var12.getArea(), var3);
            this.layoutInArea(var12, var12.getX() + var5, var12.getY() + var7, var12.getArea(), var3, 0.0, HPos.CENTER, VPos.CENTER);
         } else {
            var12.setClipSize(var1, var12.getArea());
            this.layoutInArea(var12, var12.getX() + var5, var12.getY() + var7, var1, var12.getArea(), 0.0, HPos.CENTER, VPos.CENTER);
         }
      }

      for (SplitPaneSkin.ContentDivider var14 : this.contentDividers) {
         if (this.horizontal) {
            var14.resize(var9, var3);
            this.positionInArea(var14, var14.getX() + var5, var14.getY() + var7, var9, var3, 0.0, HPos.CENTER, VPos.CENTER);
         } else {
            var14.resize(var1, var9);
            this.positionInArea(var14, var14.getX() + var5, var14.getY() + var7, var1, var9, 0.0, HPos.CENTER, VPos.CENTER);
         }
      }
   }

   private void setAndCheckAbsoluteDividerPos(SplitPaneSkin.ContentDivider var1, double var2) {
      double var4 = var1.getDividerPos();
      this.setAbsoluteDividerPos(var1, var2);
      this.checkDividerPosition(var1, var2, var4);
   }

   static class Content extends StackPane {
      private Node content;
      private Rectangle clipRect = new Rectangle();
      private double x;
      private double y;
      private double area;
      private double resizableWithParentArea;
      private double available;

      public Content(Node var1) {
         this.setClip(this.clipRect);
         this.content = var1;
         if (var1 != null) {
            this.getChildren().add(var1);
         }

         this.x = 0.0;
         this.y = 0.0;
      }

      public Node getContent() {
         return this.content;
      }

      public double getX() {
         return this.x;
      }

      public void setX(double var1) {
         this.x = var1;
      }

      public double getY() {
         return this.y;
      }

      public void setY(double var1) {
         this.y = var1;
      }

      public double getArea() {
         return this.area;
      }

      public void setArea(double var1) {
         this.area = var1;
      }

      public double getAvailable() {
         return this.available;
      }

      public void setAvailable(double var1) {
         this.available = var1;
      }

      public boolean isResizableWithParent() {
         return SplitPane.isResizableWithParent(this.content);
      }

      public double getResizableWithParentArea() {
         return this.resizableWithParentArea;
      }

      public void setResizableWithParentArea(double var1) {
         if (!this.isResizableWithParent()) {
            this.resizableWithParentArea = var1;
         } else {
            this.resizableWithParentArea = 0.0;
         }
      }

      protected void setClipSize(double var1, double var3) {
         this.clipRect.setWidth(var1);
         this.clipRect.setHeight(var3);
      }

      private void dispose() {
         this.getChildren().remove(this.content);
      }

      protected double computeMaxWidth(double var1) {
         return this.snapSizeX(this.content.maxWidth(var1));
      }

      protected double computeMaxHeight(double var1) {
         return this.snapSizeY(this.content.maxHeight(var1));
      }
   }

   class ContentDivider extends StackPane {
      private double initialPos;
      private double dividerPos;
      private double pressPos;
      private SplitPane.Divider d;
      private StackPane grabber;
      private double x;
      private double y;
      private boolean posExplicit;
      private ChangeListener<Number> listener;

      public ContentDivider(SplitPane.Divider var2) {
         this.getStyleClass().setAll(new String[]{"split-pane-divider"});
         this.d = var2;
         this.initialPos = 0.0;
         this.dividerPos = 0.0;
         this.pressPos = 0.0;
         this.grabber = new StackPane() {
            protected double computeMinWidth(double var1) {
               return 0.0;
            }

            protected double computeMinHeight(double var1) {
               return 0.0;
            }

            protected double computePrefWidth(double var1) {
               return this.snappedLeftInset() + this.snappedRightInset();
            }

            protected double computePrefHeight(double var1) {
               return this.snappedTopInset() + this.snappedBottomInset();
            }

            protected double computeMaxWidth(double var1) {
               return this.computePrefWidth(-1.0);
            }

            protected double computeMaxHeight(double var1) {
               return this.computePrefHeight(-1.0);
            }
         };
         this.setGrabberStyle(SplitPaneSkin.this.horizontal);
         this.getChildren().add(this.grabber);
      }

      public SplitPane.Divider getDivider() {
         return this.d;
      }

      public final void setGrabberStyle(boolean var1) {
         this.grabber.getStyleClass().clear();
         this.grabber.getStyleClass().setAll(new String[]{"vertical-grabber"});
         this.setCursor(Cursor.V_RESIZE);
         if (var1) {
            this.grabber.getStyleClass().setAll(new String[]{"horizontal-grabber"});
            this.setCursor(Cursor.H_RESIZE);
         }
      }

      public double getInitialPos() {
         return this.initialPos;
      }

      public void setInitialPos(double var1) {
         this.initialPos = var1;
      }

      public double getDividerPos() {
         return this.dividerPos;
      }

      public void setDividerPos(double var1) {
         this.dividerPos = var1;
      }

      public double getPressPos() {
         return this.pressPos;
      }

      public void setPressPos(double var1) {
         this.pressPos = var1;
      }

      public double getX() {
         return this.x;
      }

      public void setX(double var1) {
         this.x = var1;
      }

      public double getY() {
         return this.y;
      }

      public void setY(double var1) {
         this.y = var1;
      }

      public ChangeListener<Number> getPosPropertyListener() {
         return this.listener;
      }

      public void setPosPropertyListener(ChangeListener<Number> var1) {
         this.listener = var1;
      }

      protected double computeMinWidth(double var1) {
         return this.computePrefWidth(var1);
      }

      protected double computeMinHeight(double var1) {
         return this.computePrefHeight(var1);
      }

      protected double computePrefWidth(double var1) {
         return this.snappedLeftInset() + this.snappedRightInset();
      }

      protected double computePrefHeight(double var1) {
         return this.snappedTopInset() + this.snappedBottomInset();
      }

      protected double computeMaxWidth(double var1) {
         return this.computePrefWidth(var1);
      }

      protected double computeMaxHeight(double var1) {
         return this.computePrefHeight(var1);
      }

      protected void layoutChildren() {
         double var1 = this.grabber.prefWidth(-1.0);
         double var3 = this.grabber.prefHeight(-1.0);
         double var5 = (this.getWidth() - var1) / 2.0;
         double var7 = (this.getHeight() - var3) / 2.0;
         this.grabber.resize(var1, var3);
         this.positionInArea(this.grabber, var5, var7, var1, var3, 0.0, HPos.CENTER, VPos.CENTER);
      }
   }

   class PosPropertyListener implements ChangeListener<Number> {
      SplitPaneSkin.ContentDivider divider;

      public PosPropertyListener(SplitPaneSkin.ContentDivider var2) {
         this.divider = var2;
      }

      public void changed(ObservableValue<? extends Number> var1, Number var2, Number var3) {
         if (SplitPaneSkin.this.checkDividerPos) {
            this.divider.posExplicit = true;
         }

         SplitPaneSkin.this.getSkinnable().requestLayout();
      }
   }
}
