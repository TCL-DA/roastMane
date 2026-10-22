package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.awt.Color;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Locale;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.util.json.LenientClassDeserialzer;

@JsonAutoDetect(
   fieldVisibility = com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE,
   getterVisibility = com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE,
   isGetterVisibility = com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE,
   setterVisibility = com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder({"name", "sourceFactory", "index", "type", "visibility", "offset", "ror", "color", "primary"})
public class Channel implements Cloneable, Serializable {
   private static final long serialVersionUID = 6463652454129968693L;
   private final String _name;
   private volatile Class<? extends MeasurementSourceFactory> _sourceFactory;
   private volatile int _index;
   private volatile String _probeType;
   private volatile EnumSet<Channel.Visibility> _visibility;
   private volatile double _offset;
   private volatile boolean _rorEnabled;
   private volatile Color _color = Color.LIGHT_GRAY;
   private volatile boolean _primary = false;

   @JsonCreator
   public Channel(@JsonProperty("name") String name) {
      this._name = name;
      this._visibility = EnumSet.allOf(Channel.Visibility.class);
   }

   @JsonProperty
   public String getName() {
      return this._name;
   }

   @JsonIgnore
   public boolean isControlChannel() {
      return this._name.matches(".+[cC]ontrol");
   }

   @JsonIgnore
   public boolean isTemperatureChannel() {
      return this._name.matches(".+Temperature");
   }

   @JsonIgnore
   public boolean isRorSupported() {
      return this.isTemperatureChannel() && !this.isControlChannel();
   }

   @JsonProperty
   public Class<? extends MeasurementSourceFactory> getSourceFactory() {
      return this._sourceFactory;
   }

   @JsonDeserialize(
      using = LenientClassDeserialzer.class
   )
   public void setSourceFactory(Class<? extends MeasurementSourceFactory> sourceFactory) {
      this._sourceFactory = sourceFactory;
   }

   @JsonProperty
   public int getIndex() {
      return this._index;
   }

   public void setIndex(int index) {
      this._index = index;
   }

   public boolean isActive() {
      return this._sourceFactory != null && this._probeType != null;
   }

   @JsonProperty("type")
   public String getProbeType() {
      return this._probeType;
   }

   public void setProbeType(String probeType) {
      this._probeType = probeType;
   }

   @JsonProperty
   public EnumSet<Channel.Visibility> getVisibility() {
      return this._visibility;
   }

   public void setVisibility(EnumSet<Channel.Visibility> visibility) {
      this._visibility = visibility;
   }

   public boolean isVisible(Channel.Visibility visibility) {
      return this._visibility.contains(visibility);
   }

   @JsonProperty
   public double getOffset() {
      return this._offset;
   }

   public void setOffset(double offset) {
      this._offset = offset;
   }

   @JsonProperty("ror")
   public boolean isRorEnabled() {
      return this._rorEnabled;
   }

   public void setRorEnabled(boolean rorEnabled) {
      this._rorEnabled = rorEnabled;
   }

   @JsonProperty
   public Color getColor() {
      return this._color;
   }

   public void setColor(Color color) {
      this._color = color;
   }

   @JsonProperty
   public boolean isPrimary() {
      return this._primary;
   }

   public void setPrimary(boolean primary) {
      this._primary = primary;
   }

   @Override
   public String toString() {
      return String.format(Locale.ENGLISH, "Channel %d type %s (%s %+.1f, RoR %s)", this._index, this._probeType, this._name, this._offset, this._rorEnabled);
   }

   public Channel clone() {
      try {
         return (Channel)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static enum Visibility {
      STATUS,
      CHART;
   }
}
