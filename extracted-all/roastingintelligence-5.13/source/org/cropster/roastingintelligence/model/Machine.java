package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Machine implements Identifiable {
   private final Long _id;
   private final String _processingStep;
   private final String _type;
   private final String _name;
   private boolean _forSamples;
   private final Group _group;
   private Location _location;
   private Measure _capacity;
   private String[] _channels;
   private String[] _comments;

   @JsonCreator
   public Machine(
      @JsonProperty("id") Long id,
      @JsonProperty("processingStep") String processingStep,
      @JsonProperty("type") String type,
      @JsonProperty("name") String name,
      @JsonProperty("group") Group group
   ) {
      this._id = id;
      this._processingStep = processingStep;
      this._type = type;
      this._name = name;
      this._group = group;
   }

   @Override
   public Long getId() {
      return this._id;
   }

   public String getProcessingStep() {
      return this._processingStep;
   }

   public String getType() {
      return this._type;
   }

   @Override
   public String getName() {
      return this._name;
   }

   public boolean isForSamples() {
      return this._forSamples;
   }

   public void setForSamples(boolean forSamples) {
      this._forSamples = forSamples;
   }

   public Group getGroup() {
      return this._group;
   }

   public Location getLocation() {
      return this._location;
   }

   public void setLocation(Location location) {
      this._location = location;
   }

   public Measure getCapacity() {
      return this._capacity;
   }

   public void setCapacity(Measure capacity) {
      this._capacity = capacity;
   }

   @JsonProperty("measures")
   public String[] getChannels() {
      return this._channels;
   }

   public void setChannels(String[] channels) {
      this._channels = channels;
   }

   public String[] getComments() {
      return this._comments;
   }

   public void setComments(String[] comments) {
      this._comments = comments;
   }

   @Override
   public String toString() {
      return String.format("[%s] %s", this._group.getCode(), this._name);
   }
}
