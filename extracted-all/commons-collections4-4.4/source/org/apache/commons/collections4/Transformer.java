package org.apache.commons.collections4;

@FunctionalInterface
public interface Transformer<I, O> {
   O transform(I var1);
}
