package org.glassfish.jersey.internal.routing;

public final class RequestSpecificConsumesProducesAcceptor<MethodRouting> implements Comparable {
   private final CombinedMediaType consumes;
   private final CombinedMediaType produces;
   private final MethodRouting methodRouting;
   private final boolean producesFromProviders;

   public RequestSpecificConsumesProducesAcceptor(
      CombinedMediaType consumes, CombinedMediaType produces, boolean producesFromProviders, MethodRouting methodRouting
   ) {
      this.methodRouting = methodRouting;
      this.consumes = consumes;
      this.produces = produces;
      this.producesFromProviders = producesFromProviders;
   }

   @Override
   public int compareTo(Object o) {
      if (o == null) {
         return -1;
      } else if (!(o instanceof RequestSpecificConsumesProducesAcceptor)) {
         return -1;
      } else {
         RequestSpecificConsumesProducesAcceptor other = (RequestSpecificConsumesProducesAcceptor)o;
         int consumedComparison = CombinedMediaType.COMPARATOR.compare(this.consumes, other.consumes);
         return consumedComparison != 0 ? consumedComparison : CombinedMediaType.COMPARATOR.compare(this.produces, other.produces);
      }
   }

   public CombinedMediaType getConsumes() {
      return this.consumes;
   }

   public MethodRouting getMethodRouting() {
      return this.methodRouting;
   }

   public CombinedMediaType getProduces() {
      return this.produces;
   }

   public boolean producesFromProviders() {
      return this.producesFromProviders;
   }

   @Override
   public String toString() {
      return String.format("%s->%s:%s", this.consumes, this.produces, this.methodRouting);
   }
}
