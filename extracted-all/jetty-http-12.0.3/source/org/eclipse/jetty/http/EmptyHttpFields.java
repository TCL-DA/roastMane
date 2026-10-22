package org.eclipse.jetty.http;

import java.util.Collections;
import java.util.ListIterator;

class EmptyHttpFields implements HttpFields {
   @Override
   public ListIterator<HttpField> listIterator(int index) {
      return Collections.emptyListIterator();
   }
}
