package jakarta.activation;

public class ActivationDataFlavor {
   private String mimeType = null;
   private MimeType mimeObject = null;
   private String humanPresentableName = null;
   private Class<?> representationClass = null;

   public ActivationDataFlavor(Class<?> representationClass, String mimeType, String humanPresentableName) {
      this.mimeType = mimeType;
      this.humanPresentableName = humanPresentableName;
      this.representationClass = representationClass;
   }

   public ActivationDataFlavor(Class<?> representationClass, String humanPresentableName) {
      this.mimeType = "application/x-java-serialized-object";
      this.representationClass = representationClass;
      this.humanPresentableName = humanPresentableName;
   }

   public ActivationDataFlavor(String mimeType, String humanPresentableName) {
      this.mimeType = mimeType;

      try {
         this.representationClass = Class.forName("java.io.InputStream");
      } catch (ClassNotFoundException var4) {
      }

      this.humanPresentableName = humanPresentableName;
   }

   public String getMimeType() {
      return this.mimeType;
   }

   public Class<?> getRepresentationClass() {
      return this.representationClass;
   }

   public String getHumanPresentableName() {
      return this.humanPresentableName;
   }

   public void setHumanPresentableName(String humanPresentableName) {
      this.humanPresentableName = humanPresentableName;
   }

   public boolean equals(ActivationDataFlavor dataFlavor) {
      return this.isMimeTypeEqual(dataFlavor.mimeType) && dataFlavor.getRepresentationClass() == this.representationClass;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof ActivationDataFlavor && this.equals((ActivationDataFlavor)o);
   }

   @Deprecated
   public boolean equals(String s) {
      return s != null && this.mimeType != null ? this.isMimeTypeEqual(s) : false;
   }

   @Override
   public int hashCode() {
      int total = 0;
      if (this.representationClass != null) {
         total += this.representationClass.hashCode();
      }

      return total;
   }

   public boolean isMimeTypeEqual(String mimeType) {
      MimeType mt = null;

      try {
         if (this.mimeObject == null) {
            this.mimeObject = new MimeType(this.mimeType);
         }

         mt = new MimeType(mimeType);
      } catch (MimeTypeParseException var4) {
         return this.mimeType.equalsIgnoreCase(mimeType);
      }

      return this.mimeObject.match(mt);
   }

   @Deprecated
   protected String normalizeMimeTypeParameter(String parameterName, String parameterValue) {
      return parameterValue;
   }

   @Deprecated
   protected String normalizeMimeType(String mimeType) {
      return mimeType;
   }
}
