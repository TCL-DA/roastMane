package com.fasterxml.jackson.module.jakarta.xmlbind;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.Module.SetupContext;

public class JakartaXmlBindAnnotationModule extends Module {
   protected JakartaXmlBindAnnotationModule.Priority _priority = JakartaXmlBindAnnotationModule.Priority.PRIMARY;
   protected JakartaXmlBindAnnotationIntrospector _introspector;
   protected Include _nonNillableInclusion;
   protected String _nameUsedForXmlValue = "value";

   public JakartaXmlBindAnnotationModule() {
   }

   public JakartaXmlBindAnnotationModule(JakartaXmlBindAnnotationIntrospector intr) {
      this._introspector = intr;
   }

   public String getModuleName() {
      return this.getClass().getSimpleName();
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   public void setupModule(SetupContext context) {
      JakartaXmlBindAnnotationIntrospector intr = this._introspector;
      if (intr == null) {
         intr = new JakartaXmlBindAnnotationIntrospector(context.getTypeFactory());
         if (this._nonNillableInclusion != null) {
            intr.setNonNillableInclusion(this._nonNillableInclusion);
         }

         intr.setNameUsedForXmlValue(this._nameUsedForXmlValue);
      }

      switch (this._priority) {
         case PRIMARY:
            context.insertAnnotationIntrospector(intr);
            break;
         case SECONDARY:
            context.appendAnnotationIntrospector(intr);
      }
   }

   public JakartaXmlBindAnnotationModule setPriority(JakartaXmlBindAnnotationModule.Priority p) {
      this._priority = p;
      return this;
   }

   public JakartaXmlBindAnnotationModule.Priority getPriority() {
      return this._priority;
   }

   public JakartaXmlBindAnnotationModule setNonNillableInclusion(Include incl) {
      this._nonNillableInclusion = incl;
      if (this._introspector != null) {
         this._introspector.setNonNillableInclusion(incl);
      }

      return this;
   }

   public Include getNonNillableInclusion() {
      return this._nonNillableInclusion;
   }

   public JakartaXmlBindAnnotationModule setNameUsedForXmlValue(String name) {
      this._nameUsedForXmlValue = name;
      return this;
   }

   public String getNameUsedForXmlValue() {
      return this._nameUsedForXmlValue;
   }

   public static enum Priority {
      PRIMARY,
      SECONDARY;
   }
}
