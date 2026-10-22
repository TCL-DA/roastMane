package jakarta.ws.rs.container;

import java.lang.reflect.Method;

public interface ResourceInfo {
   Method getResourceMethod();

   Class<?> getResourceClass();
}
