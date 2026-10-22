package org.glassfish.jersey.server.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.glassfish.jersey.Severity;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.model.internal.ModelErrors;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

public final class ComponentModelValidator {
   private final List<ResourceModelIssue> issueList = new LinkedList<>();
   private final List<ResourceModelVisitor> validators = new ArrayList<>();

   public ComponentModelValidator(Collection<ValueParamProvider> valueParamProviders, MessageBodyWorkers msgBodyWorkers) {
      this.validators.add(new ResourceValidator());
      this.validators.add(new RuntimeResourceModelValidator(msgBodyWorkers));
      this.validators.add(new ResourceMethodValidator(valueParamProviders));
      this.validators.add(new InvocableValidator());
   }

   public List<ResourceModelIssue> getIssueList() {
      return this.issueList;
   }

   public boolean fatalIssuesFound() {
      for (ResourceModelIssue issue : this.getIssueList()) {
         if (issue.getSeverity() == Severity.FATAL) {
            return true;
         }
      }

      return false;
   }

   public void cleanIssueList() {
      this.issueList.clear();
   }

   public void validate(final ResourceModelComponent component) {
      Errors.process(new Runnable() {
         @Override
         public void run() {
            Errors.mark();
            ComponentModelValidator.this.validateWithErrors(component);
            ComponentModelValidator.this.issueList.addAll(ModelErrors.getErrorsAsResourceModelIssues(true));
            Errors.unmark();
         }
      });
   }

   private void validateWithErrors(ResourceModelComponent component) {
      for (ResourceModelVisitor validator : this.validators) {
         component.accept(validator);
      }

      List<? extends ResourceModelComponent> componentList = component.getComponents();
      if (null != componentList) {
         for (ResourceModelComponent subComponent : componentList) {
            this.validateWithErrors(subComponent);
         }
      }
   }
}
