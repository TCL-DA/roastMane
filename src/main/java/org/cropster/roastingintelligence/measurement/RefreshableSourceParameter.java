package org.cropster.roastingintelligence.measurement;

import java.util.Collection;

public abstract class RefreshableSourceParameter extends SourceParameter {
   public RefreshableSourceParameter(String name, Class<?> type, String value) {
      super(name, type, value);
   }

   public abstract String[] refresh(Collection<SourceParameter> var1);
}
