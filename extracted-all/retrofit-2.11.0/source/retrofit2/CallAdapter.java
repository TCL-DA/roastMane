package retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

public interface CallAdapter<R, T> {
   Type responseType();

   T adapt(Call<R> var1);

   public abstract static class Factory {
      @Nullable
      public abstract CallAdapter<?, ?> get(Type var1, Annotation[] var2, Retrofit var3);

      protected static Type getParameterUpperBound(int index, ParameterizedType type) {
         return Utils.getParameterUpperBound(index, type);
      }

      protected static Class<?> getRawType(Type type) {
         return Utils.getRawType(type);
      }
   }
}
