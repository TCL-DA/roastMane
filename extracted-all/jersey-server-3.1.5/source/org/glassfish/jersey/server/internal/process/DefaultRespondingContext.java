package org.glassfish.jersey.server.internal.process;

import java.util.function.Function;
import org.glassfish.jersey.process.internal.ChainableStage;
import org.glassfish.jersey.process.internal.Stage;
import org.glassfish.jersey.process.internal.Stages.LinkedStage;
import org.glassfish.jersey.server.ContainerResponse;

class DefaultRespondingContext implements RespondingContext {
   private Stage<ContainerResponse> rootStage;

   @Override
   public void push(Function<ContainerResponse, ContainerResponse> responseTransformation) {
      this.rootStage = this.rootStage == null ? new LinkedStage(responseTransformation) : new LinkedStage(responseTransformation, this.rootStage);
   }

   @Override
   public void push(ChainableStage<ContainerResponse> stage) {
      if (this.rootStage != null) {
         stage.setDefaultNext(this.rootStage);
      }

      this.rootStage = stage;
   }

   @Override
   public Stage<ContainerResponse> createRespondingRoot() {
      return this.rootStage;
   }
}
