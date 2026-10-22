package org.aopalliance.intercept;

import java.lang.reflect.Field;

public interface FieldAccess extends Joinpoint {
   int READ = 0;
   int WRITE = 1;

   Field getField();

   Object getValueToSet();

   int getAccessType();
}
