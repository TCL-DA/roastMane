package com.cropster.util.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum State {
   US_AL("Alabama"),
   US_AK("Alaska"),
   US_AZ("Arizona"),
   US_AR("Arkansas"),
   US_CA("California"),
   US_CO("Colorado"),
   US_CT("Connecticut"),
   US_DE("Delaware"),
   US_FL("Florida"),
   US_GA("Georgia"),
   US_HI("Hawaii"),
   US_ID("Idaho"),
   US_IL("Illinois"),
   US_IN("Indiana"),
   US_IA("Iowa"),
   US_KS("Kansas"),
   US_KY("Kentucky"),
   US_LA("Louisiana"),
   US_ME("Maine"),
   US_MD("Maryland"),
   US_MA("Massachusetts"),
   US_MI("Michigan"),
   US_MN("Minnesota"),
   US_MS("Mississippi"),
   US_MO("Missouri"),
   US_MT("Montana"),
   US_NE("Nebraska"),
   US_NV("Nevada"),
   US_NH("New Hampshire"),
   US_NJ("New Jersey"),
   US_NM("New Mexico"),
   US_NY("New York"),
   US_NC("North Carolina"),
   US_ND("North Dakota"),
   US_OH("Ohio"),
   US_OK("Oklahoma"),
   US_OR("Oregon"),
   US_PA("Pennsylvania"),
   US_RI("Rhode Island"),
   US_SC("South Carolina"),
   US_SD("South Dakota"),
   US_TN("Tennessee"),
   US_TX("Texas"),
   US_UT("Utah"),
   US_VT("Vermont"),
   US_VA("Virginia"),
   US_WA("Washington"),
   US_WV("West Virginia"),
   US_WI("Wisconsin"),
   US_WY("Wyoming"),
   US_DC("District of Columbia"),
   US_AS("American Samoa"),
   US_GU("Guam"),
   US_MP("Northern Mariana Islands"),
   US_PR("Puerto Rico"),
   US_UM("United States Minor Outlying Islands"),
   US_VI("Virgin Islands, U.S."),
   CA_AB("Alberta"),
   CA_BC("British Columbia"),
   CA_MB("Manitoba"),
   CA_NB("New Brunswick"),
   CA_NL("Newfoundland and Labrador"),
   CA_NS("Nova Scotia"),
   CA_ON("Ontario"),
   CA_PE("Prince Edward Island"),
   CA_QC("Quebec"),
   CA_SK("Saskatchewan"),
   CA_NT("Northwest Territories"),
   CA_NU("Nunavut"),
   CA_YT("Yukon");

   private String stateName;
   private String abbreviation;
   private Country country;

   private State(String name) {
      this.stateName = name;
      String[] codes = this.name().split("_");
      this.country = Country.forIso2(codes[0]);
      this.abbreviation = codes[1];
   }

   public static List<State> byCountry(Country country) {
      return Arrays.stream(values()).filter(s -> s.country == country).collect(Collectors.toList());
   }

   public String getStateName() {
      return this.stateName;
   }

   public Country getCountry() {
      return this.country;
   }

   public String getAbbreviation() {
      return this.abbreviation;
   }

   @Override
   public String toString() {
      return this.name().replace('_', '-') + " " + this.stateName;
   }
}
