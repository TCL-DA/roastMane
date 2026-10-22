package com.fasterxml.jackson.databind.jsontype;

import java.io.Serializable;
import java.util.Objects;

public final class NamedType implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final Class<?> _class;
   protected final int _hashCode;
   protected String _name;

   public NamedType(Class<?> c) {
      this(c, null);
   }

   public NamedType(Class<?> c, String name) {
      this._class = c;
      this._hashCode = c.getName().hashCode() + (name == null ? 0 : name.hashCode());
      this.setName(name);
   }

   public Class<?> getType() {
      return this._class;
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name != null && !name.isEmpty() ? name : null;
   }

   public boolean hasName() {
      return this._name != null;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o.getClass() != this.getClass()) {
         return false;
      } else {
         NamedType other = (NamedType)o;
         return this._class == other._class && Objects.equals(this._name, other._name);
      }
   }

   @Override
   public int hashCode() {
      return this._hashCode;
   }

   @Override
   public String toString() {
      return "[NamedType, class " + this._class.getName() + ", name: " + (this._name == null ? "null" : "'" + this._name + "'") + "]";
   }
}
