package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;

public class BlendProfile implements Identifiable {
   private final Long id;
   private final String name;
   private final Collection<BlendProfileComponent> blendProfileComponents;

   @JsonCreator
   public BlendProfile(
      @JsonProperty("id") Long id,
      @JsonProperty("name") String name,
      @JsonProperty("blendProfileComponents") Collection<BlendProfileComponent> blendProfileComponents
   ) {
      this.id = id;
      this.name = name;
      this.blendProfileComponents = blendProfileComponents;
   }

   @Override
   public Long getId() {
      return this.id;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public Collection<BlendProfileComponent> getBlendProfileComponents() {
      return this.blendProfileComponents;
   }
}
