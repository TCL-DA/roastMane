package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileGroup {
   private final Long id;
   private final String name;

   public ProfileGroup(@JsonProperty("id") Long id, @JsonProperty("name") String name) {
      this.id = id;
      this.name = name;
   }

   public Long getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }
}
