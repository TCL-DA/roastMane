package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import java.io.Serializable;

public class MutableConfigOverride extends ConfigOverride implements Serializable {
   private static final long serialVersionUID = 1L;

   public MutableConfigOverride() {
   }

   protected MutableConfigOverride(MutableConfigOverride src) {
      super(src);
   }

   public MutableConfigOverride copy() {
      return new MutableConfigOverride(this);
   }

   public MutableConfigOverride setFormat(Value v) {
      this._format = v;
      return this;
   }

   public MutableConfigOverride setInclude(com.fasterxml.jackson.annotation.JsonInclude.Value v) {
      this._include = v;
      return this;
   }

   public MutableConfigOverride setIncludeAsProperty(com.fasterxml.jackson.annotation.JsonInclude.Value v) {
      this._includeAsProperty = v;
      return this;
   }

   public MutableConfigOverride setIgnorals(com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value v) {
      this._ignorals = v;
      return this;
   }

   public MutableConfigOverride setIsIgnoredType(Boolean v) {
      this._isIgnoredType = v;
      return this;
   }

   public MutableConfigOverride setSetterInfo(com.fasterxml.jackson.annotation.JsonSetter.Value v) {
      this._setterInfo = v;
      return this;
   }

   public MutableConfigOverride setVisibility(com.fasterxml.jackson.annotation.JsonAutoDetect.Value v) {
      this._visibility = v;
      return this;
   }

   public MutableConfigOverride setMergeable(Boolean v) {
      this._mergeable = v;
      return this;
   }
}
