package org.jvnet.hk2.internal;

import java.util.LinkedList;
import java.util.List;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;

public class NarrowResults {
   private List<ActiveDescriptor<?>> unnarrowedResults;
   private final List<ActiveDescriptor<?>> goodResults = new LinkedList<>();
   private final List<ErrorResults> errors = new LinkedList<>();

   void addGoodResult(ActiveDescriptor<?> result) {
      this.goodResults.add(result);
   }

   void addError(ActiveDescriptor<?> fail, Injectee injectee, MultiException me) {
      this.errors.add(new ErrorResults(fail, injectee, me));
   }

   List<ActiveDescriptor<?>> getResults() {
      return this.goodResults;
   }

   List<ErrorResults> getErrors() {
      return this.errors;
   }

   void setUnnarrowedResults(List<ActiveDescriptor<?>> unnarrowed) {
      this.unnarrowedResults = unnarrowed;
   }

   ActiveDescriptor<?> removeUnnarrowedResult() {
      return this.unnarrowedResults != null && !this.unnarrowedResults.isEmpty() ? this.unnarrowedResults.remove(0) : null;
   }

   @Override
   public String toString() {
      return "NarrowResults(goodResultsSize=" + this.goodResults.size() + ",errorsSize=" + this.errors.size() + "," + System.identityHashCode(this) + ")";
   }
}
