package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigOverrides implements Serializable {
   private static final long serialVersionUID = 1L;
   protected Map<Class<?>, MutableConfigOverride> _overrides;
   protected Value _defaultInclusion;
   protected com.fasterxml.jackson.annotation.JsonSetter.Value _defaultSetterInfo;
   protected VisibilityChecker<?> _visibilityChecker;
   protected Boolean _defaultMergeable;
   protected Boolean _defaultLeniency;

   public ConfigOverrides() {
      this(null, Value.empty(), com.fasterxml.jackson.annotation.JsonSetter.Value.empty(), VisibilityChecker.Std.defaultInstance(), null, null);
   }

   protected ConfigOverrides(
      Map<Class<?>, MutableConfigOverride> overrides,
      Value defIncl,
      com.fasterxml.jackson.annotation.JsonSetter.Value defSetter,
      VisibilityChecker<?> defVisibility,
      Boolean defMergeable,
      Boolean defLeniency
   ) {
      this._overrides = overrides;
      this._defaultInclusion = defIncl;
      this._defaultSetterInfo = defSetter;
      this._visibilityChecker = defVisibility;
      this._defaultMergeable = defMergeable;
      this._defaultLeniency = defLeniency;
   }

   @Deprecated
   protected ConfigOverrides(
      Map<Class<?>, MutableConfigOverride> overrides,
      Value defIncl,
      com.fasterxml.jackson.annotation.JsonSetter.Value defSetter,
      VisibilityChecker<?> defVisibility,
      Boolean defMergeable
   ) {
      this(overrides, defIncl, defSetter, defVisibility, defMergeable, null);
   }

   public ConfigOverrides copy() {
      Map<Class<?>, MutableConfigOverride> newOverrides;
      if (this._overrides == null) {
         newOverrides = null;
      } else {
         newOverrides = this._newMap();

         for (Entry<Class<?>, MutableConfigOverride> entry : this._overrides.entrySet()) {
            newOverrides.put(entry.getKey(), entry.getValue().copy());
         }
      }

      return new ConfigOverrides(
         newOverrides, this._defaultInclusion, this._defaultSetterInfo, this._visibilityChecker, this._defaultMergeable, this._defaultLeniency
      );
   }

   public ConfigOverride findOverride(Class<?> type) {
      return this._overrides == null ? null : this._overrides.get(type);
   }

   public MutableConfigOverride findOrCreateOverride(Class<?> type) {
      if (this._overrides == null) {
         this._overrides = this._newMap();
      }

      MutableConfigOverride override = this._overrides.get(type);
      if (override == null) {
         override = new MutableConfigOverride();
         this._overrides.put(type, override);
      }

      return override;
   }

   public com.fasterxml.jackson.annotation.JsonFormat.Value findFormatDefaults(Class<?> type) {
      if (this._overrides != null) {
         ConfigOverride override = this._overrides.get(type);
         if (override != null) {
            com.fasterxml.jackson.annotation.JsonFormat.Value format = override.getFormat();
            if (format != null) {
               if (!format.hasLenient()) {
                  return format.withLenient(this._defaultLeniency);
               }

               return format;
            }
         }
      }

      return this._defaultLeniency == null
         ? com.fasterxml.jackson.annotation.JsonFormat.Value.empty()
         : com.fasterxml.jackson.annotation.JsonFormat.Value.forLeniency(this._defaultLeniency);
   }

   public Value getDefaultInclusion() {
      return this._defaultInclusion;
   }

   public com.fasterxml.jackson.annotation.JsonSetter.Value getDefaultSetterInfo() {
      return this._defaultSetterInfo;
   }

   public Boolean getDefaultMergeable() {
      return this._defaultMergeable;
   }

   public Boolean getDefaultLeniency() {
      return this._defaultLeniency;
   }

   public VisibilityChecker<?> getDefaultVisibility() {
      return this._visibilityChecker;
   }

   public void setDefaultInclusion(Value v) {
      this._defaultInclusion = v;
   }

   public void setDefaultSetterInfo(com.fasterxml.jackson.annotation.JsonSetter.Value v) {
      this._defaultSetterInfo = v;
   }

   public void setDefaultMergeable(Boolean v) {
      this._defaultMergeable = v;
   }

   public void setDefaultLeniency(Boolean v) {
      this._defaultLeniency = v;
   }

   public void setDefaultVisibility(VisibilityChecker<?> v) {
      this._visibilityChecker = v;
   }

   protected Map<Class<?>, MutableConfigOverride> _newMap() {
      return new HashMap<>();
   }
}
