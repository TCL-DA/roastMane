package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Group {
   private final String _code;
   private final String _name;

   @JsonCreator
   public Group(@JsonProperty("code") String code, @JsonProperty("name") String name) {
      this._code = code;
      this._name = name;
   }

   public String getCode() {
      return this._code;
   }

   public String getName() {
      return this._name;
   }
}
