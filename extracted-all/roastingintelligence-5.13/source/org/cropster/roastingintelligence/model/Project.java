package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Project {
   private final Long _id;
   private final String _name;

   @JsonCreator
   public Project(@JsonProperty("id") Long id, @JsonProperty("name") String name) {
      this._id = id;
      this._name = name;
   }

   public Long getId() {
      return this._id;
   }

   public String getName() {
      return this._name;
   }
}
