package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.media.multipart.internal.EntityPartReader;
import org.glassfish.jersey.media.multipart.internal.EntityPartWriter;
import org.glassfish.jersey.media.multipart.internal.FormDataParamInjectionFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartReaderClientSide;
import org.glassfish.jersey.media.multipart.internal.MultiPartReaderServerSide;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.glassfish.jersey.media.multipart.internal.SingleEntityPartReader;
import org.glassfish.jersey.media.multipart.internal.SingleEntityPartWriter;

public class MultiPartFeature implements Feature {
   public boolean configure(FeatureContext context) {
      RuntimeType runtime = context.getConfiguration().getRuntimeType();
      if (RuntimeType.SERVER.equals(runtime)) {
         context.register(FormDataParamInjectionFeature.class);
         context.register(MultiPartReaderServerSide.class);
      } else {
         context.register(MultiPartReaderClientSide.class);
      }

      context.register(MultiPartWriter.class);
      context.register(EntityPartReader.class);
      context.register(EntityPartWriter.class);
      context.register(SingleEntityPartReader.class);
      context.register(SingleEntityPartWriter.class);
      return true;
   }
}
