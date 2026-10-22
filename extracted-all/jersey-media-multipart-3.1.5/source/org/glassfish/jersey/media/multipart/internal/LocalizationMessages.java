package org.glassfish.jersey.media.multipart.internal;

import java.util.Locale;
import java.util.ResourceBundle;
import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory.ResourceBundleSupplier;

public final class LocalizationMessages {
   private static final String BUNDLE_NAME = "org.glassfish.jersey.media.multipart.internal.localization";
   private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory(
      "org.glassfish.jersey.media.multipart.internal.localization", new LocalizationMessages.BundleSupplier()
   );
   private static final Localizer LOCALIZER = new Localizer();

   private LocalizationMessages() {
   }

   public static Localizable localizableERROR_PARSING_CONTENT_DISPOSITION(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.parsing.content.disposition", new Object[]{arg0});
   }

   public static String ERROR_PARSING_CONTENT_DISPOSITION(Object arg0) {
      return LOCALIZER.localize(localizableERROR_PARSING_CONTENT_DISPOSITION(arg0));
   }

   public static Localizable localizableENTITY_HAS_WRONG_TYPE() {
      return MESSAGE_FACTORY.getMessage("entity.has.wrong.type", new Object[0]);
   }

   public static String ENTITY_HAS_WRONG_TYPE() {
      return LOCALIZER.localize(localizableENTITY_HAS_WRONG_TYPE());
   }

   public static Localizable localizableCANNOT_INJECT_FILE() {
      return MESSAGE_FACTORY.getMessage("cannot.inject.file", new Object[0]);
   }

   public static String CANNOT_INJECT_FILE() {
      return LOCALIZER.localize(localizableCANNOT_INJECT_FILE());
   }

   public static Localizable localizableMISSING_ENTITY_OF_BODY_PART(Object arg0) {
      return MESSAGE_FACTORY.getMessage("missing.entity.of.body.part", new Object[]{arg0});
   }

   public static String MISSING_ENTITY_OF_BODY_PART(Object arg0) {
      return LOCALIZER.localize(localizableMISSING_ENTITY_OF_BODY_PART(arg0));
   }

   public static Localizable localizableMISSING_MEDIA_TYPE_OF_BODY_PART() {
      return MESSAGE_FACTORY.getMessage("missing.media.type.of.body.part", new Object[0]);
   }

   public static String MISSING_MEDIA_TYPE_OF_BODY_PART() {
      return LOCALIZER.localize(localizableMISSING_MEDIA_TYPE_OF_BODY_PART());
   }

   public static Localizable localizableNO_AVAILABLE_MBW(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("no.available.mbw", new Object[]{arg0, arg1});
   }

   public static String NO_AVAILABLE_MBW(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableNO_AVAILABLE_MBW(arg0, arg1));
   }

   public static Localizable localizableTEMP_FILE_NOT_DELETED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("temp.file.not.deleted", new Object[]{arg0});
   }

   public static String TEMP_FILE_NOT_DELETED(Object arg0) {
      return LOCALIZER.localize(localizableTEMP_FILE_NOT_DELETED(arg0));
   }

   public static Localizable localizableERROR_CHARSET_UNSUPPORTED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.charset.unsupported", new Object[]{arg0});
   }

   public static String ERROR_CHARSET_UNSUPPORTED(Object arg0) {
      return LOCALIZER.localize(localizableERROR_CHARSET_UNSUPPORTED(arg0));
   }

   public static Localizable localizableENTITY_IS_EMPTY() {
      return MESSAGE_FACTORY.getMessage("entity.is.empty", new Object[0]);
   }

   public static String ENTITY_IS_EMPTY() {
      return LOCALIZER.localize(localizableENTITY_IS_EMPTY());
   }

   public static Localizable localizableMEDIA_TYPE_NOT_TEXT_PLAIN() {
      return MESSAGE_FACTORY.getMessage("media.type.not.text.plain", new Object[0]);
   }

   public static String MEDIA_TYPE_NOT_TEXT_PLAIN() {
      return LOCALIZER.localize(localizableMEDIA_TYPE_NOT_TEXT_PLAIN());
   }

   public static Localizable localizableMUST_SPECIFY_BODY_PART() {
      return MESSAGE_FACTORY.getMessage("must.specify.body.part", new Object[0]);
   }

   public static String MUST_SPECIFY_BODY_PART() {
      return LOCALIZER.localize(localizableMUST_SPECIFY_BODY_PART());
   }

   public static Localizable localizableNO_AVAILABLE_MBR(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("no.available.mbr", new Object[]{arg0, arg1});
   }

   public static String NO_AVAILABLE_MBR(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableNO_AVAILABLE_MBR(arg0, arg1));
   }

   public static Localizable localizableCONTROL_NAME_CANNOT_BE_NULL() {
      return MESSAGE_FACTORY.getMessage("control.name.cannot.be.null", new Object[0]);
   }

   public static String CONTROL_NAME_CANNOT_BE_NULL() {
      return LOCALIZER.localize(localizableCONTROL_NAME_CANNOT_BE_NULL());
   }

   public static Localizable localizableENTITY_CONTENT_NOT_SET() {
      return MESSAGE_FACTORY.getMessage("entity.content.not.set", new Object[0]);
   }

   public static String ENTITY_CONTENT_NOT_SET() {
      return LOCALIZER.localize(localizableENTITY_CONTENT_NOT_SET());
   }

   public static Localizable localizableERROR_FILENAME_UNSUPPORTED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.filename.unsupported", new Object[]{arg0});
   }

   public static String ERROR_FILENAME_UNSUPPORTED(Object arg0) {
      return LOCALIZER.localize(localizableERROR_FILENAME_UNSUPPORTED(arg0));
   }

   public static Localizable localizableCONTENT_HAS_BEEN_ALREADY_READ() {
      return MESSAGE_FACTORY.getMessage("content.has.been.already.read", new Object[0]);
   }

   public static String CONTENT_HAS_BEEN_ALREADY_READ() {
      return LOCALIZER.localize(localizableCONTENT_HAS_BEEN_ALREADY_READ());
   }

   public static Localizable localizablePARSING_ERROR() {
      return MESSAGE_FACTORY.getMessage("parsing.error", new Object[0]);
   }

   public static String PARSING_ERROR() {
      return LOCALIZER.localize(localizablePARSING_ERROR());
   }

   public static Localizable localizableERROR_READING_ENTITY(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.reading.entity", new Object[]{arg0});
   }

   public static String ERROR_READING_ENTITY(Object arg0) {
      return LOCALIZER.localize(localizableERROR_READING_ENTITY(arg0));
   }

   public static Localizable localizableFORM_DATA_MULTIPART_CANNOT_CHANGE_MEDIATYPE() {
      return MESSAGE_FACTORY.getMessage("form.data.multipart.cannot.change.mediatype", new Object[0]);
   }

   public static String FORM_DATA_MULTIPART_CANNOT_CHANGE_MEDIATYPE() {
      return LOCALIZER.localize(localizableFORM_DATA_MULTIPART_CANNOT_CHANGE_MEDIATYPE());
   }

   public static Localizable localizableTEMP_FILE_CANNOT_BE_CREATED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("temp.file.cannot.be.created", new Object[]{arg0});
   }

   public static String TEMP_FILE_CANNOT_BE_CREATED(Object arg0) {
      return LOCALIZER.localize(localizableTEMP_FILE_CANNOT_BE_CREATED(arg0));
   }

   private static class BundleSupplier implements ResourceBundleSupplier {
      private BundleSupplier() {
      }

      public ResourceBundle getResourceBundle(Locale locale) {
         return ResourceBundle.getBundle("org.glassfish.jersey.media.multipart.internal.localization", locale);
      }
   }
}
