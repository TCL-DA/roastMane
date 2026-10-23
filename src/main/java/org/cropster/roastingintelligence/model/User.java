package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
   private final String _username;
   private final String _nameEmail;
   private final String _lastName;
   private final String _firstName;

   @JsonCreator
   public User(
      @JsonProperty("username") String username,
      @JsonProperty("nameEmail") String nameEmail,
      @JsonProperty("lastName") String lastName,
      @JsonProperty("firstName") String firstName
   ) {
      this._username = username;
      this._nameEmail = nameEmail;
      this._lastName = lastName;
      this._firstName = firstName;
   }

   public String getUsername() {
      return this._username;
   }

   public String getNameEmail() {
      return this._nameEmail;
   }

   public String getLastName() {
      return this._lastName;
   }

   public String getFirstName() {
      return this._firstName;
   }

   public String getFullName() {
      if (this._firstName == null && this._lastName == null) {
         return null;
      } else if (this._firstName == null) {
         return this._lastName;
      } else {
         return this._lastName == null ? this._firstName : this._firstName + " " + this._lastName;
      }
   }
}
