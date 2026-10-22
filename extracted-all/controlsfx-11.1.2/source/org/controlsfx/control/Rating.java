package org.controlsfx.control;

import impl.org.controlsfx.skin.RatingSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Skin;

public class Rating extends ControlsFXControl {
   private DoubleProperty rating = new SimpleDoubleProperty(this, "rating", 3.0);
   private IntegerProperty max = new SimpleIntegerProperty(this, "max", 5);
   private ObjectProperty<Orientation> orientation;
   private BooleanProperty partialRating = new SimpleBooleanProperty(this, "partialRating", false);
   private BooleanProperty updateOnHover = new SimpleBooleanProperty(this, "updateOnHover", false);

   public Rating() {
      this(5);
   }

   public Rating(int max) {
      this(max, -1);
   }

   public Rating(int max, int rating) {
      this.getStyleClass().setAll(new String[]{"rating"});
      this.setMax(max);
      this.setRating(rating == -1 ? (double)((int)Math.floor((double)max / 2.0)) : (double)rating);
   }

   protected Skin<?> createDefaultSkin() {
      return new RatingSkin(this);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(Rating.class, "rating.css");
   }

   public final DoubleProperty ratingProperty() {
      return this.rating;
   }

   public final void setRating(double value) {
      this.ratingProperty().set(value);
   }

   public final double getRating() {
      return this.rating == null ? 3.0 : this.rating.get();
   }

   public final IntegerProperty maxProperty() {
      return this.max;
   }

   public final void setMax(int value) {
      this.maxProperty().set(value);
   }

   public final int getMax() {
      return this.max == null ? 5 : this.max.get();
   }

   public final ObjectProperty<Orientation> orientationProperty() {
      if (this.orientation == null) {
         this.orientation = new SimpleObjectProperty(this, "orientation", Orientation.HORIZONTAL);
      }

      return this.orientation;
   }

   public final void setOrientation(Orientation value) {
      this.orientationProperty().set(value);
   }

   public final Orientation getOrientation() {
      return this.orientation == null ? Orientation.HORIZONTAL : (Orientation)this.orientation.get();
   }

   public final BooleanProperty partialRatingProperty() {
      return this.partialRating;
   }

   public final void setPartialRating(boolean value) {
      this.partialRatingProperty().set(value);
   }

   public final boolean isPartialRating() {
      return this.partialRating == null ? false : this.partialRating.get();
   }

   public final BooleanProperty updateOnHoverProperty() {
      return this.updateOnHover;
   }

   public final void setUpdateOnHover(boolean value) {
      this.updateOnHoverProperty().set(value);
   }

   public final boolean isUpdateOnHover() {
      return this.updateOnHover == null ? false : this.updateOnHover.get();
   }
}
