package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.glassfish.jersey.media.multipart.internal.LocalizationMessages;
import org.glassfish.jersey.message.internal.MediaTypes;

public class FormDataMultiPart extends MultiPart {
   public FormDataMultiPart() {
      super(MediaType.MULTIPART_FORM_DATA_TYPE);
   }

   public FormDataMultiPart field(String name, String value) {
      this.getBodyParts().add(new FormDataBodyPart(name, value));
      return this;
   }

   public FormDataMultiPart field(String name, Object entity, MediaType mediaType) {
      this.getBodyParts().add(new FormDataBodyPart(name, entity, mediaType));
      return this;
   }

   public FormDataBodyPart getField(String name) {
      FormDataBodyPart result = null;

      for (BodyPart bodyPart : this.getBodyParts()) {
         if (bodyPart instanceof FormDataBodyPart && name.equals(((FormDataBodyPart)bodyPart).getName())) {
            result = (FormDataBodyPart)bodyPart;
            break;
         }
      }

      return result;
   }

   public List<FormDataBodyPart> getFields(String name) {
      List<FormDataBodyPart> result = null;

      for (BodyPart bodyPart : this.getBodyParts()) {
         if (bodyPart instanceof FormDataBodyPart && name.equals(((FormDataBodyPart)bodyPart).getName())) {
            if (result == null) {
               result = new ArrayList<>(1);
            }

            result.add((FormDataBodyPart)bodyPart);
         }
      }

      return result;
   }

   public Map<String, List<FormDataBodyPart>> getFields() {
      Map<String, List<FormDataBodyPart>> map = new HashMap<>();

      for (BodyPart bodyPart : this.getBodyParts()) {
         if (bodyPart instanceof FormDataBodyPart) {
            FormDataBodyPart p = (FormDataBodyPart)bodyPart;
            List<FormDataBodyPart> l = map.get(p.getName());
            if (l == null) {
               l = new ArrayList<>(1);
               map.put(p.getName(), l);
            }

            l.add(p);
         }
      }

      return map;
   }

   @Override
   public void setMediaType(MediaType mediaType) {
      if (!MediaTypes.typeEqual(mediaType, MediaType.MULTIPART_FORM_DATA_TYPE)) {
         throw new IllegalArgumentException(LocalizationMessages.FORM_DATA_MULTIPART_CANNOT_CHANGE_MEDIATYPE());
      } else {
         super.setMediaType(mediaType);
      }
   }
}
