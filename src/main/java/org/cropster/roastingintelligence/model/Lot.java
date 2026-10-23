package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@JsonPropertyOrder({"id", "idTag", "creationDate", "name", "project", "location", "weight", "sources", "notes", "blendProfile"})
public class Lot implements Identifiable {
   private Long _id;
   private String _idTag;
   private Date _creationDate;
   private String _name;
   private Project _project;
   private Measure _weight;
   private Location _location;
   private final Set<LotTransaction> _sources = new LinkedHashSet<>();
   private String _notes;
   private BlendProfile _blendProfile;

   public Lot() {
   }

   public Lot(Long id, String name) {
      this();
      this._id = id;
      this._name = name;
   }

   @Override
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

   public Date getCreationDate() {
      return this._creationDate;
   }

   public void setCreationDate(Date creationDate) {
      this._creationDate = creationDate;
   }

   @Override
   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public Project getProject() {
      return this._project;
   }

   public void setProject(Project project) {
      this._project = project;
   }

   public Measure getWeight() {
      return this._weight;
   }

   public void setWeight(Measure weight) {
      this._weight = weight;
   }

   public Location getLocation() {
      return this._location;
   }

   public void setLocation(Location location) {
      this._location = location;
   }

   @JsonInclude(Include.NON_EMPTY)
   public Set<LotTransaction> getSources() {
      return this._sources;
   }

   public String getNotes() {
      return this._notes;
   }

   public void setNotes(String notes) {
      this._notes = notes;
   }

   public BlendProfile getBlendProfile() {
      return this._blendProfile;
   }

   public void setBlendProfile(BlendProfile blendProfile) {
      this._blendProfile = blendProfile;
   }

   @Override
   public String toString() {
      return String.format("[%s]%s %s", this._idTag, this._name != null ? " " + this._name : "", this._weight);
   }
}
