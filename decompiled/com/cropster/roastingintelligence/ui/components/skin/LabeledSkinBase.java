package com.cropster.roastingintelligence.ui.components.skin;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.LabeledText;
import com.sun.javafx.scene.control.behavior.TextBinding;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.geometry.HPos;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.SkinBase;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public abstract class LabeledSkinBase<C extends Labeled> extends SkinBase<C> {
   LabeledText text;
   boolean invalidText = true;
   Node graphic;
   double textWidth = Double.NEGATIVE_INFINITY;
   double ellipsisWidth = Double.NEGATIVE_INFINITY;
   final InvalidationListener graphicPropertyChangedListener = valueModel -> {
      this.invalidText = true;
      ((Labeled)this.getSkinnable()).requestLayout();
   };
   private Rectangle textClip;
   private double wrapWidth;
   private double wrapHeight;
   private TextBinding bindings;
   private Line mnemonicUnderscore;
   private boolean containsMnemonic = false;
   private Scene mnemonicScene = null;
   private KeyCombination mnemonicCode;
   private Node labeledNode = null;

   public LabeledSkinBase(C labeled) {
      super(labeled);
      this.text = new LabeledText(labeled);
      this.updateChildren();
      this.registerChangeListener(labeled.ellipsisStringProperty(), o -> {
         this.textMetricsChanged();
         this.invalidateWidths();
         this.ellipsisWidth = Double.NEGATIVE_INFINITY;
      });
      this.registerChangeListener(labeled.widthProperty(), o -> {
         this.updateWrappingWidth();
         this.invalidText = true;
      });
      this.registerChangeListener(labeled.heightProperty(), o -> this.invalidText = true);
      this.registerChangeListener(labeled.fontProperty(), o -> {
         this.textMetricsChanged();
         this.invalidateWidths();
         this.ellipsisWidth = Double.NEGATIVE_INFINITY;
      });
      this.registerChangeListener(labeled.graphicProperty(), o -> {
         this.updateChildren();
         this.textMetricsChanged();
      });
      this.registerChangeListener(labeled.contentDisplayProperty(), o -> {
         this.updateChildren();
         this.textMetricsChanged();
      });
      this.registerChangeListener(labeled.labelPaddingProperty(), o -> this.textMetricsChanged());
      this.registerChangeListener(labeled.graphicTextGapProperty(), o -> this.textMetricsChanged());
      this.registerChangeListener(labeled.alignmentProperty(), o -> ((Labeled)this.getSkinnable()).requestLayout());
      this.registerChangeListener(labeled.mnemonicParsingProperty(), o -> {
         this.containsMnemonic = false;
         this.textMetricsChanged();
      });
      this.registerChangeListener(labeled.textProperty(), o -> {
         this.updateChildren();
         this.textMetricsChanged();
         this.invalidateWidths();
      });
      this.registerChangeListener(labeled.textAlignmentProperty(), o -> {
      });
      this.registerChangeListener(labeled.textOverrunProperty(), o -> this.textMetricsChanged());
      this.registerChangeListener(labeled.wrapTextProperty(), o -> {
         this.updateWrappingWidth();
         this.textMetricsChanged();
      });
      this.registerChangeListener(labeled.underlineProperty(), o -> this.textMetricsChanged());
      this.registerChangeListener(labeled.lineSpacingProperty(), o -> this.textMetricsChanged());
      this.registerChangeListener(labeled.sceneProperty(), o -> this.sceneChanged());
   }

   public void dispose() {
      if (this.graphic != null) {
         this.graphic.layoutBoundsProperty().removeListener(this.graphicPropertyChangedListener);
         this.graphic = null;
      }

      super.dispose();
   }

   protected void updateChildren() {
      Labeled labeled = (Labeled)this.getSkinnable();
      if (this.graphic != null) {
         this.graphic.layoutBoundsProperty().removeListener(this.graphicPropertyChangedListener);
      }

      this.graphic = labeled.getGraphic();
      if (this.graphic instanceof ImageView) {
         this.graphic.setMouseTransparent(true);
      }

      if (this.isIgnoreGraphic()) {
         if (labeled.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) {
            this.getChildren().clear();
         } else {
            this.getChildren().setAll(new Node[]{this.text});
         }
      } else {
         this.graphic.layoutBoundsProperty().addListener(this.graphicPropertyChangedListener);
         if (this.isIgnoreText()) {
            this.getChildren().setAll(new Node[]{this.graphic});
         } else {
            this.getChildren().setAll(new Node[]{this.graphic, this.text});
         }
      }
   }

   protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.computeMinLabeledPartWidth(height, topInset, rightInset, bottomInset, leftInset);
   }

   protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.computeMinLabeledPartHeight(width, topInset, rightInset, bottomInset, leftInset);
   }

   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      Labeled labeled = (Labeled)this.getSkinnable();
      Font font = this.text.getFont();
      String cleanText = this.getCleanText();
      boolean emptyText = cleanText == null || cleanText.isEmpty();
      double widthPadding = leftInset + rightInset;
      if (!this.isIgnoreText()) {
         widthPadding += this.leftLabelPadding() + this.rightLabelPadding();
      }

      double textWidth = 0.0;
      if (!emptyText) {
         textWidth = Utils.computeTextWidth(font, cleanText, 0.0);
      }

      double graphicWidth = this.graphic == null
         ? 0.0
         : Utils.boundedSize(this.graphic.prefWidth(-1.0), this.graphic.minWidth(-1.0), this.graphic.maxWidth(-1.0));
      if (this.isIgnoreGraphic()) {
         return textWidth + widthPadding;
      } else if (this.isIgnoreText()) {
         return graphicWidth + widthPadding;
      } else {
         return labeled.getContentDisplay() != ContentDisplay.LEFT && labeled.getContentDisplay() != ContentDisplay.RIGHT
            ? Math.max(textWidth, graphicWidth) + widthPadding
            : textWidth + labeled.getGraphicTextGap() + graphicWidth + widthPadding;
      }
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      Labeled labeled = (Labeled)this.getSkinnable();
      Font font = this.text.getFont();
      ContentDisplay contentDisplay = labeled.getContentDisplay();
      double gap = labeled.getGraphicTextGap();
      width -= leftInset + rightInset;
      if (!this.isIgnoreText()) {
         width -= this.leftLabelPadding() + this.rightLabelPadding();
      }

      String cleanText = this.getCleanText();
      if (cleanText != null && cleanText.endsWith("\n")) {
         cleanText = cleanText.substring(0, cleanText.length() - 1);
      }

      double textWidth = width;
      if (!this.isIgnoreGraphic() && (contentDisplay == ContentDisplay.LEFT || contentDisplay == ContentDisplay.RIGHT)) {
         textWidth = width - (this.graphic.prefWidth(-1.0) + gap);
      }

      double textHeight = Utils.computeTextHeight(font, cleanText, labeled.isWrapText() ? textWidth : 0.0, labeled.getLineSpacing(), this.text.getBoundsType());
      double h = textHeight;
      if (!this.isIgnoreGraphic()) {
         Node graphic = labeled.getGraphic();
         if (contentDisplay != ContentDisplay.TOP && contentDisplay != ContentDisplay.BOTTOM) {
            h = Math.max(textHeight, graphic.prefHeight(width));
         } else {
            h = graphic.prefHeight(width) + gap + textHeight;
         }
      }

      double padding = topInset + bottomInset;
      if (!this.isIgnoreText()) {
         padding += this.topLabelPadding() + this.bottomLabelPadding();
      }

      return h + padding;
   }

   protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((Labeled)this.getSkinnable()).prefWidth(height);
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((Labeled)this.getSkinnable()).prefHeight(width);
   }

   public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
      double textBaselineOffset = this.text.getBaselineOffset();
      double h = textBaselineOffset;
      Labeled labeled = (Labeled)this.getSkinnable();
      Node g = labeled.getGraphic();
      if (!this.isIgnoreGraphic()) {
         ContentDisplay contentDisplay = labeled.getContentDisplay();
         if (contentDisplay == ContentDisplay.TOP) {
            h = g.prefHeight(-1.0) + labeled.getGraphicTextGap() + textBaselineOffset;
         } else if (contentDisplay == ContentDisplay.LEFT || contentDisplay == ContentDisplay.RIGHT) {
            h = textBaselineOffset + (g.prefHeight(-1.0) - this.text.prefHeight(-1.0)) / 2.0;
         }
      }

      double offset = topInset + h;
      if (!this.isIgnoreText()) {
         offset += this.topLabelPadding();
      }

      return offset;
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      this.layoutLabelInArea(x, y, w, h);
   }

   protected void layoutLabelInArea(double x, double y, double w, double h) {
      this.layoutLabelInArea(x, y, w, h, null);
   }

   protected void layoutLabelInArea(double x, double y, double w, double h, Pos alignment) {
      Labeled labeled = (Labeled)this.getSkinnable();
      ContentDisplay contentDisplay = labeled.getContentDisplay();
      if (alignment == null) {
         alignment = labeled.getAlignment();
      }

      HPos hpos = alignment == null ? HPos.LEFT : alignment.getHpos();
      VPos vpos = alignment == null ? VPos.CENTER : alignment.getVpos();
      boolean ignoreGraphic = this.isIgnoreGraphic();
      boolean ignoreText = this.isIgnoreText();
      if (!ignoreText) {
         x += this.leftLabelPadding();
         y += this.topLabelPadding();
         w -= this.leftLabelPadding() + this.rightLabelPadding();
         h -= this.topLabelPadding() + this.bottomLabelPadding();
      }

      double graphicWidth;
      double graphicHeight;
      if (ignoreGraphic) {
         graphicWidth = 0.0;
         graphicHeight = 0.0;
      } else if (ignoreText) {
         if (this.graphic.isResizable()) {
            Orientation contentBias = this.graphic.getContentBias();
            if (contentBias == Orientation.HORIZONTAL) {
               graphicWidth = Utils.boundedSize(w, this.graphic.minWidth(-1.0), this.graphic.maxWidth(-1.0));
               graphicHeight = Utils.boundedSize(h, this.graphic.minHeight(graphicWidth), this.graphic.maxHeight(graphicWidth));
            } else if (contentBias == Orientation.VERTICAL) {
               graphicHeight = Utils.boundedSize(h, this.graphic.minHeight(-1.0), this.graphic.maxHeight(-1.0));
               graphicWidth = Utils.boundedSize(w, this.graphic.minWidth(graphicHeight), this.graphic.maxWidth(graphicHeight));
            } else {
               graphicWidth = Utils.boundedSize(w, this.graphic.minWidth(-1.0), this.graphic.maxWidth(-1.0));
               graphicHeight = Utils.boundedSize(h, this.graphic.minHeight(-1.0), this.graphic.maxHeight(-1.0));
            }

            this.graphic.resize(graphicWidth, graphicHeight);
         } else {
            graphicWidth = this.graphic.getLayoutBounds().getWidth();
            graphicHeight = this.graphic.getLayoutBounds().getHeight();
         }
      } else {
         this.graphic.autosize();
         graphicWidth = this.graphic.getLayoutBounds().getWidth();
         graphicHeight = this.graphic.getLayoutBounds().getHeight();
      }

      double textWidth;
      double textHeight;
      if (ignoreText) {
         textWidth = 0.0;
         textHeight = 0.0;
         this.text.setText("");
      } else {
         this.updateDisplayedText(w, h);
         textWidth = this.snapSizeX(Math.min(this.text.getLayoutBounds().getWidth(), this.wrapWidth));
         textHeight = this.snapSizeY(Math.min(this.text.getLayoutBounds().getHeight(), this.wrapHeight));
      }

      double gap = !ignoreText && !ignoreGraphic ? Math.max(labeled.getGraphicTextGap(), w - graphicWidth - textWidth) : 0.0;
      double contentWidth = Math.max(graphicWidth, textWidth);
      double contentHeight = Math.max(graphicHeight, textHeight);
      if (contentDisplay == ContentDisplay.TOP || contentDisplay == ContentDisplay.BOTTOM) {
         contentHeight = graphicHeight + gap + textHeight;
      } else if (contentDisplay == ContentDisplay.LEFT || contentDisplay == ContentDisplay.RIGHT) {
         contentWidth = graphicWidth + gap + textWidth;
      }

      double contentX;
      if (hpos == HPos.LEFT) {
         contentX = x;
      } else if (hpos == HPos.RIGHT) {
         contentX = x + (w - contentWidth);
      } else {
         contentX = x + (w - contentWidth) / 2.0;
      }

      double contentY;
      if (vpos == VPos.TOP) {
         contentY = y;
      } else if (vpos == VPos.BOTTOM) {
         contentY = y + (h - contentHeight);
      } else {
         contentY = y + (h - contentHeight) / 2.0;
      }

      Point2D mnemonicPos = null;
      double mnemonicWidth = 0.0;
      double mnemonicHeight = 0.0;
      if (this.containsMnemonic) {
         Font font = this.text.getFont();
         String preSt = this.bindings.getText();
         boolean isRTL = this.labeledNode.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
         mnemonicPos = Utils.computeMnemonicPosition(font, preSt, this.bindings.getMnemonicIndex(), this.wrapWidth, labeled.getLineSpacing(), isRTL);
         mnemonicWidth = Utils.computeTextWidth(font, preSt.substring(this.bindings.getMnemonicIndex(), this.bindings.getMnemonicIndex() + 1), 0.0);
         mnemonicHeight = Utils.computeTextHeight(font, "_", 0.0, this.text.getBoundsType());
      }

      if ((!ignoreGraphic || !ignoreText) && !this.text.isManaged()) {
         this.text.setManaged(true);
      }

      if (ignoreGraphic && ignoreText) {
         if (this.text.isManaged()) {
            this.text.setManaged(false);
         }

         this.text.relocate(this.snapPositionX(contentX), this.snapPositionY(contentY));
      } else if (ignoreGraphic) {
         this.text.relocate(this.snapPositionX(contentX), this.snapPositionY(contentY));
         if (this.containsMnemonic && mnemonicPos != null) {
            this.mnemonicUnderscore.setEndX(mnemonicWidth - 2.0);
            this.mnemonicUnderscore.relocate(this.snapPositionX(contentX + mnemonicPos.getX()), this.snapPositionY(contentY + mnemonicPos.getY()));
         }
      } else if (ignoreText) {
         this.text.relocate(this.snapPositionX(contentX), this.snapPositionY(contentY));
         this.graphic.relocate(this.snapPositionX(contentX), this.snapPositionY(contentY));
         if (this.containsMnemonic && mnemonicPos != null) {
            this.mnemonicUnderscore.setEndX(mnemonicWidth);
            this.mnemonicUnderscore.setStrokeWidth(mnemonicHeight / 10.0);
            this.mnemonicUnderscore.relocate(this.snapPositionX(contentX + mnemonicPos.getX()), this.snapPositionY(contentY + mnemonicPos.getY()));
         }
      } else {
         double graphicX = 0.0;
         double graphicY = 0.0;
         double textX = 0.0;
         double textY = 0.0;
         if (contentDisplay == ContentDisplay.TOP) {
            graphicX = contentX + (contentWidth - graphicWidth) / 2.0;
            textX = contentX + (contentWidth - textWidth) / 2.0;
            graphicY = contentY;
            textY = contentY + graphicHeight + gap;
         } else if (contentDisplay == ContentDisplay.RIGHT) {
            textX = contentX;
            graphicX = contentX + textWidth + gap;
            graphicY = contentY + (contentHeight - graphicHeight) / 2.0;
            textY = contentY + (contentHeight - textHeight) / 2.0;
         } else if (contentDisplay == ContentDisplay.BOTTOM) {
            graphicX = contentX + (contentWidth - graphicWidth) / 2.0;
            textX = contentX + (contentWidth - textWidth) / 2.0;
            textY = contentY;
            graphicY = contentY + textHeight + gap;
         } else if (contentDisplay == ContentDisplay.LEFT) {
            graphicX = contentX;
            textX = contentX + graphicWidth + gap;
            graphicY = contentY + (contentHeight - graphicHeight) / 2.0;
            textY = contentY + (contentHeight - textHeight) / 2.0;
         } else if (contentDisplay == ContentDisplay.CENTER) {
            graphicX = contentX + (contentWidth - graphicWidth) / 2.0;
            textX = contentX + (contentWidth - textWidth) / 2.0;
            graphicY = contentY + (contentHeight - graphicHeight) / 2.0;
            textY = contentY + (contentHeight - textHeight) / 2.0;
         }

         this.text.relocate(this.snapPositionX(textX), this.snapPositionY(textY));
         if (this.containsMnemonic && mnemonicPos != null) {
            this.mnemonicUnderscore.setEndX(mnemonicWidth);
            this.mnemonicUnderscore.setStrokeWidth(mnemonicHeight / 10.0);
            this.mnemonicUnderscore.relocate(this.snapPositionX(textX + mnemonicPos.getX()), this.snapPositionY(textY + mnemonicPos.getY()));
         }

         this.graphic.relocate(this.snapPositionX(graphicX), this.snapPositionY(graphicY));
      }

      if (this.text != null && (this.text.getLayoutBounds().getHeight() > this.wrapHeight || this.text.getLayoutBounds().getWidth() > this.wrapWidth)) {
         if (this.textClip == null) {
            this.textClip = new Rectangle();
         }

         if (labeled.getEffectiveNodeOrientation() == NodeOrientation.LEFT_TO_RIGHT) {
            this.textClip.setX(this.text.getLayoutBounds().getMinX());
         } else {
            this.textClip.setX(this.text.getLayoutBounds().getMaxX() - this.wrapWidth);
         }

         this.textClip.setY(this.text.getLayoutBounds().getMinY());
         this.textClip.setWidth(this.wrapWidth);
         this.textClip.setHeight(this.wrapHeight);
         if (this.text.getClip() == null) {
            this.text.setClip(this.textClip);
         }
      } else if (this.text.getClip() != null) {
         this.text.setClip(null);
      }
   }

   protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
      switch (attribute) {
         case TEXT:
            Labeled labeled = (Labeled)this.getSkinnable();
            String accText = labeled.getAccessibleText();
            if (accText != null && !accText.isEmpty()) {
               return accText;
            } else {
               String cleanText = this.getCleanText();
               if (cleanText != null && !cleanText.isEmpty()) {
                  return cleanText;
               } else {
                  if (this.graphic != null) {
                     Object result = this.graphic.queryAccessibleAttribute(AccessibleAttribute.TEXT, new Object[0]);
                     if (result != null) {
                        return result;
                     }
                  }

                  return null;
               }
            }
         case MNEMONIC:
            if (this.bindings != null) {
               return this.bindings.getMnemonic();
            }

            return null;
         default:
            return super.queryAccessibleAttribute(attribute, parameters);
      }
   }

   private double computeMinLabeledPartWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      Labeled labeled = (Labeled)this.getSkinnable();
      ContentDisplay contentDisplay = labeled.getContentDisplay();
      double gap = labeled.getGraphicTextGap();
      double minTextWidth = 0.0;
      Font font = this.text.getFont();
      OverrunStyle truncationStyle = labeled.getTextOverrun();
      String ellipsisString = labeled.getEllipsisString();
      String cleanText = this.getCleanText();
      boolean emptyText = cleanText == null || cleanText.isEmpty();
      if (!emptyText) {
         if (truncationStyle == OverrunStyle.CLIP) {
            if (this.textWidth == Double.NEGATIVE_INFINITY) {
               this.textWidth = Utils.computeTextWidth(font, cleanText.substring(0, 1), 0.0);
            }

            minTextWidth = this.textWidth;
         } else {
            if (this.textWidth == Double.NEGATIVE_INFINITY) {
               this.textWidth = Utils.computeTextWidth(font, cleanText, 0.0);
            }

            if (this.ellipsisWidth == Double.NEGATIVE_INFINITY) {
               this.ellipsisWidth = Utils.computeTextWidth(font, ellipsisString, 0.0);
            }

            minTextWidth = Math.min(this.textWidth, this.ellipsisWidth);
         }
      }

      Node graphic = labeled.getGraphic();
      double width;
      if (this.isIgnoreGraphic()) {
         width = minTextWidth;
      } else if (this.isIgnoreText()) {
         width = graphic.minWidth(-1.0);
      } else if (contentDisplay != ContentDisplay.LEFT && contentDisplay != ContentDisplay.RIGHT) {
         width = Math.max(minTextWidth, graphic.minWidth(-1.0));
      } else {
         width = minTextWidth + graphic.minWidth(-1.0) + gap;
      }

      double padding = leftInset + rightInset;
      if (!this.isIgnoreText()) {
         padding += this.leftLabelPadding() + this.rightLabelPadding();
      }

      return width + padding;
   }

   private double computeMinLabeledPartHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      Labeled labeled = (Labeled)this.getSkinnable();
      Font font = this.text.getFont();
      String cleanText = this.getCleanText();
      if (cleanText != null && !cleanText.isEmpty()) {
         int newlineIndex = cleanText.indexOf(10);
         if (newlineIndex >= 0) {
            cleanText = cleanText.substring(0, newlineIndex);
         }
      }

      double s = labeled.getLineSpacing();
      double textHeight = Utils.computeTextHeight(font, cleanText, 0.0, s, this.text.getBoundsType());
      double h = textHeight;
      if (!this.isIgnoreGraphic()) {
         Node graphic = labeled.getGraphic();
         if (labeled.getContentDisplay() != ContentDisplay.TOP && labeled.getContentDisplay() != ContentDisplay.BOTTOM) {
            h = Math.max(textHeight, graphic.minHeight(width));
         } else {
            h = graphic.minHeight(width) + labeled.getGraphicTextGap() + textHeight;
         }
      }

      double padding = topInset + bottomInset;
      if (!this.isIgnoreText()) {
         padding += this.topLabelPadding() - this.bottomLabelPadding();
      }

      return h + padding;
   }

   double topLabelPadding() {
      return this.snapSizeY(((Labeled)this.getSkinnable()).getLabelPadding().getTop());
   }

   double bottomLabelPadding() {
      return this.snapSizeY(((Labeled)this.getSkinnable()).getLabelPadding().getBottom());
   }

   double leftLabelPadding() {
      return this.snapSizeX(((Labeled)this.getSkinnable()).getLabelPadding().getLeft());
   }

   double rightLabelPadding() {
      return this.snapSizeX(((Labeled)this.getSkinnable()).getLabelPadding().getRight());
   }

   private void textMetricsChanged() {
      this.invalidText = true;
      ((Labeled)this.getSkinnable()).requestLayout();
   }

   void mnemonicTargetChanged() {
      if (this.containsMnemonic) {
         this.removeMnemonic();
         Control control = this.getSkinnable();
         if (control instanceof Label) {
            this.labeledNode = ((Label)control).getLabelFor();
            this.addMnemonic();
         } else {
            this.labeledNode = null;
         }
      }
   }

   private void sceneChanged() {
      Labeled labeled = (Labeled)this.getSkinnable();
      Scene scene = labeled.getScene();
      if (scene != null && this.containsMnemonic) {
         this.addMnemonic();
      }
   }

   private void invalidateWidths() {
      this.textWidth = Double.NEGATIVE_INFINITY;
   }

   void updateDisplayedText() {
      this.updateDisplayedText(-1.0, -1.0);
   }

   private void updateDisplayedText(double w, double h) {
      if (this.invalidText) {
         Labeled labeled = (Labeled)this.getSkinnable();
         String cleanText = this.getCleanText();
         int mnemonicIndex = -1;
         if (cleanText != null && !cleanText.isEmpty() && this.bindings != null && !PlatformUtil.isMac() && ((Labeled)this.getSkinnable()).isMnemonicParsing()) {
            if (labeled instanceof Label) {
               this.labeledNode = ((Label)labeled).getLabelFor();
            } else {
               this.labeledNode = labeled;
            }

            if (this.labeledNode == null) {
               this.labeledNode = labeled;
            }

            mnemonicIndex = this.bindings.getMnemonicIndex();
         }

         if (this.containsMnemonic) {
            if (this.mnemonicScene != null
               && (mnemonicIndex == -1 || this.bindings != null && !this.bindings.getMnemonicKeyCombination().equals(this.mnemonicCode))) {
               this.removeMnemonic();
               this.containsMnemonic = false;
            }
         } else {
            this.removeMnemonic();
         }

         if (cleanText != null && !cleanText.isEmpty() && mnemonicIndex >= 0 && !this.containsMnemonic) {
            this.containsMnemonic = true;
            this.mnemonicCode = this.bindings.getMnemonicKeyCombination();
            this.addMnemonic();
         }

         if (this.containsMnemonic) {
            if (this.mnemonicUnderscore == null) {
               this.mnemonicUnderscore = new Line();
               this.mnemonicUnderscore.setStartX(0.0);
               this.mnemonicUnderscore.setStartY(0.0);
               this.mnemonicUnderscore.setEndY(0.0);
               this.mnemonicUnderscore.getStyleClass().clear();
               this.mnemonicUnderscore.getStyleClass().setAll(new String[]{"mnemonic-underline"});
            }

            if (!this.getChildren().contains(this.mnemonicUnderscore)) {
               this.getChildren().add(this.mnemonicUnderscore);
            }
         } else if (this.mnemonicUnderscore != null && this.getChildren().contains(this.mnemonicUnderscore)) {
            Platform.runLater(() -> {
               this.getChildren().remove(this.mnemonicUnderscore);
               this.mnemonicUnderscore = null;
            });
         }

         int len = cleanText != null ? cleanText.length() : 0;
         boolean multiline = false;
         if (cleanText != null && len > 0) {
            int i = cleanText.indexOf(10);
            if (i > -1 && i < len - 1) {
               multiline = true;
            }
         }

         boolean horizontalPosition = labeled.getContentDisplay() == ContentDisplay.LEFT || labeled.getContentDisplay() == ContentDisplay.RIGHT;
         double availableWidth = labeled.getWidth() - this.snappedLeftInset() - this.snappedRightInset();
         if (!this.isIgnoreText()) {
            availableWidth -= this.leftLabelPadding() + this.rightLabelPadding();
         }

         availableWidth = Math.max(availableWidth, 0.0);
         if (w == -1.0) {
            w = availableWidth;
         }

         double minW = Math.min(
            this.computeMinLabeledPartWidth(-1.0, this.snappedTopInset(), this.snappedRightInset(), this.snappedBottomInset(), this.snappedLeftInset()),
            availableWidth
         );
         if (horizontalPosition && !this.isIgnoreGraphic()) {
            double graphicW = labeled.getGraphic().getLayoutBounds().getWidth() + labeled.getGraphicTextGap();
            w -= graphicW;
            minW -= graphicW;
         }

         this.wrapWidth = Math.max(minW, w);
         boolean verticalPosition = labeled.getContentDisplay() == ContentDisplay.TOP || labeled.getContentDisplay() == ContentDisplay.BOTTOM;
         double availableHeight = labeled.getHeight() - this.snappedTopInset() - this.snappedBottomInset();
         if (!this.isIgnoreText()) {
            availableHeight -= this.topLabelPadding() + this.bottomLabelPadding();
         }

         availableHeight = Math.max(availableHeight, 0.0);
         if (h == -1.0) {
            h = availableHeight;
         }

         double minH = Math.min(
            this.computeMinLabeledPartHeight(
               this.wrapWidth, this.snappedTopInset(), this.snappedRightInset(), this.snappedBottomInset(), this.snappedLeftInset()
            ),
            availableHeight
         );
         if (verticalPosition && labeled.getGraphic() != null) {
            double graphicH = labeled.getGraphic().getLayoutBounds().getHeight() + labeled.getGraphicTextGap();
            h -= graphicH;
            minH -= graphicH;
         }

         this.wrapHeight = Math.max(minH, h);
         this.updateWrappingWidth();
         Font font = this.text.getFont();
         OverrunStyle truncationStyle = labeled.getTextOverrun();
         String ellipsisString = labeled.getEllipsisString();
         String result;
         if (labeled.isWrapText()) {
            result = Utils.computeClippedWrappedText(
               font, cleanText, this.wrapWidth, this.wrapHeight, labeled.getLineSpacing(), truncationStyle, ellipsisString, this.text.getBoundsType()
            );
         } else if (!multiline) {
            result = Utils.computeClippedText(font, cleanText, this.wrapWidth, truncationStyle, ellipsisString);
         } else {
            StringBuilder sb = new StringBuilder();
            String[] splits = cleanText.split("\n");

            for (int i = 0; i < splits.length; i++) {
               sb.append(Utils.computeClippedText(font, splits[i], this.wrapWidth, truncationStyle, ellipsisString));
               if (i < splits.length - 1) {
                  sb.append('\n');
               }
            }

            result = sb.toString();
         }

         if (result != null && result.endsWith("\n")) {
            result = result.substring(0, result.length() - 1);
         }

         this.text.setText(result);
         this.updateWrappingWidth();
         this.invalidText = false;
      }
   }

   private String getCleanText() {
      Labeled labeled = (Labeled)this.getSkinnable();
      String sourceText = labeled.getText();
      if (sourceText != null && labeled.isMnemonicParsing()) {
         if (this.bindings == null) {
            this.bindings = new TextBinding(sourceText);
         } else {
            this.bindings.update(sourceText);
         }

         return this.bindings.getText();
      } else {
         return sourceText;
      }
   }

   private void addMnemonic() {
      if (this.labeledNode != null) {
         this.mnemonicScene = this.labeledNode.getScene();
         if (this.mnemonicScene != null) {
            this.mnemonicScene.addMnemonic(new Mnemonic(this.labeledNode, this.mnemonicCode));
         }
      }
   }

   private void removeMnemonic() {
      if (this.mnemonicScene != null && this.labeledNode != null) {
         this.mnemonicScene.removeMnemonic(new Mnemonic(this.labeledNode, this.mnemonicCode));
         this.mnemonicScene = null;
      }
   }

   private void updateWrappingWidth() {
      Labeled labeled = (Labeled)this.getSkinnable();
      this.text.setWrappingWidth(0.0);
      if (labeled.isWrapText()) {
         double w = Math.min(this.text.prefWidth(-1.0), this.wrapWidth);
         this.text.setWrappingWidth(w);
      }
   }

   boolean isIgnoreGraphic() {
      return this.graphic == null || !this.graphic.isManaged() || ((Labeled)this.getSkinnable()).getContentDisplay() == ContentDisplay.TEXT_ONLY;
   }

   boolean isIgnoreText() {
      Labeled labeled = (Labeled)this.getSkinnable();
      String txt = this.getCleanText();
      return txt == null || txt.isEmpty() || labeled.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY;
   }
}
