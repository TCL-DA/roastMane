package jakarta.ws.rs.sse;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import java.lang.reflect.Type;

public interface OutboundSseEvent extends SseEvent {
   Class<?> getType();

   Type getGenericType();

   MediaType getMediaType();

   Object getData();

   public interface Builder {
      OutboundSseEvent.Builder id(String var1);

      OutboundSseEvent.Builder name(String var1);

      OutboundSseEvent.Builder reconnectDelay(long var1);

      OutboundSseEvent.Builder mediaType(MediaType var1);

      OutboundSseEvent.Builder comment(String var1);

      OutboundSseEvent.Builder data(Class var1, Object var2);

      OutboundSseEvent.Builder data(GenericType var1, Object var2);

      OutboundSseEvent.Builder data(Object var1);

      OutboundSseEvent build();
   }
}
