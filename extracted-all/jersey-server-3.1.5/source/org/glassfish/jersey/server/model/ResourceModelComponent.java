package org.glassfish.jersey.server.model;

import java.util.List;

public interface ResourceModelComponent {
   void accept(ResourceModelVisitor var1);

   List<? extends ResourceModelComponent> getComponents();
}
