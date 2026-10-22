package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;

public class Profile implements Identifiable {
   private Long _id;
   private String _name;
   private Measure _batchSize;
   private BigDecimal _calculatedWeightChange;
   private BigDecimal _weightChange;
   private String _notes;
   private LinkedHashSet<BlendComponent> _components = new LinkedHashSet<>();
   private Reference _reference;
   private Date _referenceLastModified;
   private ProfileGroup _profileGroup;

   public Profile() {
   }

   public Profile(Long id, String name) {
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

   @Override
   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public Measure getBatchSize() {
      return this._batchSize;
   }

   public void setBatchSize(Measure batchSize) {
      this._batchSize = batchSize;
   }

   public BigDecimal getCalculatedWeightChange() {
      return this._calculatedWeightChange;
   }

   public void setCalculatedWeightChange(BigDecimal calculatedWeightChange) {
      this._calculatedWeightChange = calculatedWeightChange;
   }

   public BigDecimal getWeightChange() {
      return this._weightChange;
   }

   public void setWeightChange(BigDecimal weightChange) {
      this._weightChange = weightChange;
   }

   @JsonInclude(Include.NON_EMPTY)
   public String getNotes() {
      return this._notes;
   }

   public void setNotes(String notes) {
      this._notes = notes;
   }

   @JsonInclude(Include.NON_EMPTY)
   public LinkedHashSet<BlendComponent> getComponents() {
      return this._components;
   }

   public void setComponents(LinkedHashSet<BlendComponent> components) {
      this._components = components;
   }

   public Reference getReference() {
      return this._reference;
   }

   public void setReference(Reference reference) {
      this._reference = reference;
   }

   @JsonIgnore
   public Long getReferenceId() {
      return this._reference != null ? this._reference.getLotId() : null;
   }

   public Date getReferenceLastModified() {
      return this._referenceLastModified;
   }

   public void setReferenceLastModified(Date referenceLastModified) {
      this._referenceLastModified = referenceLastModified;
   }

   public ProfileGroup getProfileGroup() {
      return this._profileGroup;
   }

   public void setProfileGroup(ProfileGroup profileGroup) {
      this._profileGroup = profileGroup;
   }

   @Override
   public String toString() {
      return this._name;
   }
}
