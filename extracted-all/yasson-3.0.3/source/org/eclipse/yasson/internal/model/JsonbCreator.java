package org.eclipse.yasson.internal.model;

import jakarta.json.bind.JsonbException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class JsonbCreator {
   private final Executable executable;
   private final CreatorModel[] params;

   public JsonbCreator(Executable executable, CreatorModel[] creatorModels) {
      this.executable = executable;
      this.params = creatorModels;
   }

   public <T> T call(Object[] params, Class<T> on) {
      try {
         return (T)(this.executable instanceof Constructor ? ((Constructor)this.executable).newInstance(params) : ((Method)this.executable).invoke(on, params));
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var4) {
         throw new JsonbException(Messages.getMessage(MessageKeys.ERROR_CALLING_JSONB_CREATOR, on), var4);
      }
   }

   public boolean contains(String paramName) {
      return this.findByName(paramName) != null;
   }

   public CreatorModel findByName(String paramName) {
      for (CreatorModel param : this.params) {
         if (param.getName().equals(paramName)) {
            return param;
         }
      }

      return null;
   }

   public CreatorModel[] getParams() {
      return this.params;
   }
}
