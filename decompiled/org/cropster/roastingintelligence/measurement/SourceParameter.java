package org.cropster.roastingintelligence.measurement;

import java.util.HashMap;
import java.util.Map;

public class SourceParameter {
   private final String _name;
   private String _displayName = null;
   private final Class<?> _type;
   private String[] _options;
   private final Map<String, String> _optionDisplayNames = new HashMap<>();
   private String _value;
   private boolean _hidden;

   public SourceParameter(String name, Class<?> type, String value) {
      this._name = name;
      this._type = type;
      this._value = value;
      this._hidden = false;
   }

   public String getName() {
      return this._name;
   }

   public String getDisplayName() {
      return this._displayName;
   }

   public void setDisplayName(String displayName) {
      this._displayName = displayName;
   }

   public Class<?> getType() {
      return this._type;
   }

   public String[] getOptions() {
      return this._options;
   }

   public void setOptions(String[] options) {
      this._options = options;
   }

   public String getOptionDisplayName(String option) {
      return this._optionDisplayNames.containsKey(option) ? this._optionDisplayNames.get(option) : null;
   }

   public void setOptionDisplayName(String option, String displayName) {
      this._optionDisplayNames.put(option, displayName);
   }

   public String getValue() {
      return this._value;
   }

   public void setValue(String value) {
      this._value = value;
   }

   public void setHidden(boolean hidden) {
      this._hidden = hidden;
   }

   public boolean isHidden() {
      return this._hidden;
   }

   @Override
   public String toString() {
      return this._name + " (" + this._type.getSimpleName() + ") = " + this._value;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this._name == null ? 0 : this._name.hashCode());
      return 31 * result + (this._value == null ? 0 : this._value.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         SourceParameter other = (SourceParameter)obj;
         if (this._name == null) {
            if (other._name != null) {
               return false;
            }
         } else if (!this._name.equals(other._name)) {
            return false;
         }

         if (this._value == null) {
            if (other._value != null) {
               return false;
            }
         } else if (!this._value.equals(other._value)) {
            return false;
         }

         return true;
      }
   }
}
