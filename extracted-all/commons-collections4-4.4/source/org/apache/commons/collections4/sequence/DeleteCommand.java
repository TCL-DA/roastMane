package org.apache.commons.collections4.sequence;

public class DeleteCommand<T> extends EditCommand<T> {
   public DeleteCommand(T object) {
      super(object);
   }

   @Override
   public void accept(CommandVisitor<T> visitor) {
      visitor.visitDeleteCommand(this.getObject());
   }
}
