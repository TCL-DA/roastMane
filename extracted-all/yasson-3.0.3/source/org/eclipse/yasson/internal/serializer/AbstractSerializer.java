package org.eclipse.yasson.internal.serializer;

abstract class AbstractSerializer implements ModelSerializer {
   final ModelSerializer delegate;

   AbstractSerializer(ModelSerializer delegate) {
      this.delegate = delegate;
   }
}
