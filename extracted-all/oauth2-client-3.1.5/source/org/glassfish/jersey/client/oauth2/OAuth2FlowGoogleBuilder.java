package org.glassfish.jersey.client.oauth2;

public class OAuth2FlowGoogleBuilder extends AbstractAuthorizationCodeGrantBuilder<OAuth2FlowGoogleBuilder> {
   public static final String LOGIN_HINT = "login_hint";

   OAuth2FlowGoogleBuilder() {
      super(new AuthCodeGrantImpl.Builder());
   }

   public OAuth2FlowGoogleBuilder accessType(OAuth2FlowGoogleBuilder.AccessType accessType) {
      return (OAuth2FlowGoogleBuilder)this.property(OAuth2CodeGrantFlow.Phase.AUTHORIZATION, OAuth2FlowGoogleBuilder.AccessType.getKey(), accessType.getValue());
   }

   public OAuth2FlowGoogleBuilder prompt(OAuth2FlowGoogleBuilder.Prompt prompt) {
      return (OAuth2FlowGoogleBuilder)this.property(OAuth2CodeGrantFlow.Phase.AUTHORIZATION, OAuth2FlowGoogleBuilder.Prompt.getKey(), prompt.getValue());
   }

   public OAuth2FlowGoogleBuilder display(OAuth2FlowGoogleBuilder.Display display) {
      return (OAuth2FlowGoogleBuilder)this.property(OAuth2CodeGrantFlow.Phase.AUTHORIZATION, OAuth2FlowGoogleBuilder.Display.getKey(), display.getValue());
   }

   public OAuth2FlowGoogleBuilder loginHint(String loginHint) {
      return (OAuth2FlowGoogleBuilder)this.property(OAuth2CodeGrantFlow.Phase.AUTHORIZATION, OAuth2FlowGoogleBuilder.Display.getKey(), loginHint);
   }

   public static enum AccessType {
      ONLINE("online"),
      OFFLINE("offline");

      private final String propertyValue;

      private AccessType(String propertyValue) {
         this.propertyValue = propertyValue;
      }

      public String getValue() {
         return this.propertyValue;
      }

      public static String getKey() {
         return "access_type";
      }
   }

   public static enum Display {
      PAGE("page"),
      POPUP("popup"),
      TOUCH("touch"),
      WAP("wap");

      private final String value;

      private Display(String value) {
         this.value = value;
      }

      public String getValue() {
         return this.value;
      }

      public static String getKey() {
         return "display";
      }
   }

   public static enum Prompt {
      NONE("none"),
      CONSENT("consent"),
      SELECT_ACCOUNT("select_account");

      private final String value;

      private Prompt(String value) {
         this.value = value;
      }

      public String getValue() {
         return this.value;
      }

      public static String getKey() {
         return "prompt";
      }
   }
}
