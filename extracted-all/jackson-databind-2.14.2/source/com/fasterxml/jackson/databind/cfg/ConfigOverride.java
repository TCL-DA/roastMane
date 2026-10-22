package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonFormat.Value;

public abstract class ConfigOverride {
   protected Value _format;
   protected com.fasterxml.jackson.annotation.JsonInclude.Value _include;
   protected com.fasterxml.jackson.annotation.JsonInclude.Value _includeAsProperty;
   protected com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value _ignorals;
   protected com.fasterxml.jackson.annotation.JsonSetter.Value _setterInfo;
   protected com.fasterxml.jackson.annotation.JsonAutoDetect.Value _visibility;
   protected Boolean _isIgnoredType;
   protected Boolean _mergeable;

   protected ConfigOverride() {
   }

   protected ConfigOverride(ConfigOverride src) {
      this._format = src._format;
      this._include = src._include;
      this._includeAsProperty = src._includeAsProperty;
      this._ignorals = src._ignorals;
      this._setterInfo = src._setterInfo;
      this._visibility = src._visibility;
      this._isIgnoredType = src._isIgnoredType;
      this._mergeable = src._mergeable;
   }

   public static ConfigOverride empty() {
      return ConfigOverride.Empty.INSTANCE;
   }

   public Value getFormat() {
      return this._format;
   }

   public com.fasterxml.jackson.annotation.JsonInclude.Value getInclude() {
      return this._include;
   }

   public com.fasterxml.jackson.annotation.JsonInclude.Value getIncludeAsProperty() {
      return this._includeAsProperty;
   }

   public com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value getIgnorals() {
      return this._ignorals;
   }

   public Boolean getIsIgnoredType() {
      return this._isIgnoredType;
   }

   public com.fasterxml.jackson.annotation.JsonSetter.Value getSetterInfo() {
      return this._setterInfo;
   }

   public com.fasterxml.jackson.annotation.JsonAutoDetect.Value getVisibility() {
      return this._visibility;
   }

   public Boolean getMergeable() {
      return this._mergeable;
   }

   static final class Empty extends ConfigOverride {
      static final ConfigOverride.Empty INSTANCE = new ConfigOverride.Empty();

      private Empty() {
      }
   }
}
