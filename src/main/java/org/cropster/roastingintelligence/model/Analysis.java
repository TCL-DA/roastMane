package org.cropster.roastingintelligence.model;

import com.cropster.util.Collect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class Analysis {
   private Long _id;
   private String _idTag;
   private String _summary;
   private Set<Cupping> _cuppings = Collections.emptySet();
   private Set<Physical> _physicals = Collections.emptySet();

   public Long getId() {
      return this._id;
   }

   public void setId(Long id) {
      this._id = id;
   }

   public String getIdTag() {
      return this._idTag;
   }

   public void setIdTag(String idTag) {
      this._idTag = idTag;
   }

   @JsonProperty("resultSummary")
   public String getSummary() {
      return this._summary;
   }

   public void setSummary(String summary) {
      this._summary = summary;
   }

   public Set<Cupping> getCuppings() {
      return this._cuppings;
   }

   public void setCuppings(Set<Cupping> cuppings) {
      this._cuppings = cuppings;
   }

   public Set<Physical> getPhysicals() {
      return this._physicals;
   }

   public void setPhysicals(Set<Physical> physicals) {
      this._physicals = physicals;
   }

   @JsonIgnore
   public Physical getPhysical() {
      return this._physicals != null ? (Physical)Collect.first(this._physicals) : null;
   }

   @JsonIgnore
   public Float getScore() {
      String[] split = StringUtils.split(this._summary, ",");
      return split != null && split.length > 0 && split[0].matches("\\d+(?:\\.\\d+)") ? Float.parseFloat(split[0]) : null;
   }

   @JsonIgnore
   public Map<Descriptor, Integer> getDescriptorsCounted() {
      if (this._cuppings != null && !this._cuppings.isEmpty()) {
         Map<String, Integer> descriptorCount = new HashMap<>();
         Map<Descriptor, Integer> descriptors = new LinkedHashMap<>();
         this._cuppings
            .stream()
            .flatMap(c -> c.getItems().stream())
            .flatMap(i -> i.getDescriptors().stream())
            .filter(d -> {
               if (descriptorCount.containsKey(d.getName())) {
                  descriptorCount.put(d.getName(), descriptorCount.get(d.getName()) + 1);
                  return false;
               } else {
                  descriptorCount.put(d.getName(), 1);
                  return true;
               }
            })
            .sorted((l, r) -> l.getName().compareTo(r.getName()))
            .sorted((l, r) -> descriptorCount.get(r.getName()).compareTo(descriptorCount.get(l.getName())))
            .forEach(d -> descriptors.put(d, descriptorCount.get(d.getName())));
         return descriptors;
      } else {
         return Collections.emptyMap();
      }
   }
}
