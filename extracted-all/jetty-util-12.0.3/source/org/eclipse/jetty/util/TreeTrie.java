package org.eclipse.jetty.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TreeTrie<V> extends AbstractTrie<V> {
   private static final int[] LOOKUP_INSENSITIVE = new int[]{
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      31,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      26,
      -1,
      27,
      30,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      28,
      29,
      -1,
      -1,
      -1,
      -1,
      -1,
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      -1,
      -1,
      -1,
      -1,
      -1
   };
   private static final int[] LOOKUP_SENSITIVE = new int[]{
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      31,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      26,
      -1,
      27,
      30,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      28,
      29,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      -1,
      -1,
      -1,
      -1,
      -1
   };
   private static final int INDEX = 32;
   private final int[] _lookup;
   private final TreeTrie.Node<V> _root;

   public static <V> AbstractTrie<V> from(boolean caseSensitive, Map<String, V> contents) {
      TreeTrie<V> trie = new TreeTrie<>(caseSensitive);
      return contents != null && !trie.putAll(contents) ? null : trie;
   }

   TreeTrie() {
      this(false);
   }

   TreeTrie(boolean caseSensitive) {
      super(caseSensitive);
      this._lookup = caseSensitive ? LOOKUP_SENSITIVE : LOOKUP_INSENSITIVE;
      this._root = new TreeTrie.Node<>('\u0000');
   }

   @Override
   public void clear() {
      Arrays.fill(this._root._nextIndex, null);
      this._root._nextOther.clear();
      this._root._key = null;
      this._root._value = null;
   }

   @Override
   public boolean put(String s, V v) {
      TreeTrie.Node<V> t = this._root;
      int limit = s.length();

      for (int k = 0; k < limit; k++) {
         char c = s.charAt(k);
         int index = c < 127 ? this._lookup[c] : -1;
         if (index >= 0) {
            if (t._nextIndex[index] == null) {
               t._nextIndex[index] = new TreeTrie.Node<>(c);
            }

            t = t._nextIndex[index];
         } else {
            TreeTrie.Node<V> n = null;

            for (int i = t._nextOther.size(); i-- > 0; n = null) {
               n = t._nextOther.get(i);
               if (n._c == c) {
                  break;
               }
            }

            if (n == null) {
               n = new TreeTrie.Node<>(c);
               t._nextOther.add(n);
            }

            t = n;
         }
      }

      t._key = v == null ? null : s;
      t._value = v;
      return true;
   }

   @Override
   public V get(String s, int offset, int len) {
      TreeTrie.Node<V> t = this._root;

      for (int i = 0; i < len; i++) {
         char c = s.charAt(offset + i);
         int index = c < 127 ? this._lookup[c] : -1;
         if (index >= 0) {
            if (t._nextIndex[index] == null) {
               return null;
            }

            t = t._nextIndex[index];
         } else {
            TreeTrie.Node<V> n = null;

            for (int j = t._nextOther.size(); j-- > 0; n = null) {
               n = t._nextOther.get(j);
               if (n._c == c) {
                  break;
               }
            }

            if (n == null) {
               return null;
            }

            t = n;
         }
      }

      return t._value;
   }

   @Override
   public V get(ByteBuffer b, int offset, int len) {
      TreeTrie.Node<V> t = this._root;

      for (int i = 0; i < len; i++) {
         byte c = b.get(offset + i);
         int index = c >= 0 && c < 127 ? this._lookup[c] : -1;
         if (index >= 0) {
            if (t._nextIndex[index] == null) {
               return null;
            }

            t = t._nextIndex[index];
         } else {
            TreeTrie.Node<V> n = null;

            for (int j = t._nextOther.size(); j-- > 0; n = null) {
               n = t._nextOther.get(j);
               if (n._c == c) {
                  break;
               }
            }

            if (n == null) {
               return null;
            }

            t = n;
         }
      }

      return t._value;
   }

   @Override
   public V getBest(byte[] b, int offset, int len) {
      return this.getBest(this._root, b, offset, len);
   }

   private V getBest(TreeTrie.Node<V> node, byte[] b, int offset, int len) {
      for (int i = 0; i < len; i++) {
         byte c = b[offset + i];
         int index = c >= 0 && c < 127 ? this._lookup[c] : -1;
         TreeTrie.Node<V> next;
         if (index >= 0) {
            if (node._nextIndex[index] == null) {
               break;
            }

            next = node._nextIndex[index];
         } else {
            TreeTrie.Node<V> n = null;

            for (int j = node._nextOther.size(); j-- > 0; n = null) {
               n = node._nextOther.get(j);
               if (n._c == c) {
                  break;
               }
            }

            if (n == null) {
               break;
            }

            next = n;
         }

         if (node._key != null) {
            V best = this.getBest(next, b, offset + i + 1, len - i - 1);
            if (best != null) {
               return best;
            }
            break;
         }

         node = next;
      }

      return node._value;
   }

   @Override
   public boolean isEmpty() {
      return this.isEmpty(this._root);
   }

   private boolean isEmpty(TreeTrie.Node<V> t) {
      if (t != null) {
         if (t._key != null) {
            return false;
         }

         for (int i = 0; i < 32; i++) {
            if (t._nextIndex[i] != null && !this.isEmpty(t._nextIndex[i])) {
               return false;
            }
         }

         int ix = t._nextOther.size();

         while (ix-- > 0) {
            if (!this.isEmpty(t._nextOther.get(ix))) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public int size() {
      return this.keySet().size();
   }

   @Override
   public V getBest(String s, int offset, int len) {
      return this.getBest(this._root, s, offset, len);
   }

   private V getBest(TreeTrie.Node<V> node, String s, int offset, int len) {
      for (int i = 0; i < len; i++) {
         char c = s.charAt(offset + i);
         int index = c < 127 ? this._lookup[c] : -1;
         TreeTrie.Node<V> next;
         if (index >= 0) {
            if (node._nextIndex[index] == null) {
               break;
            }

            next = node._nextIndex[index];
         } else {
            TreeTrie.Node<V> n = null;

            for (int j = node._nextOther.size(); j-- > 0; n = null) {
               n = node._nextOther.get(j);
               if (n._c == c) {
                  break;
               }
            }

            if (n == null) {
               break;
            }

            next = n;
         }

         if (node._key != null) {
            V best = this.getBest(next, s, offset + i + 1, len - i - 1);
            if (best != null) {
               return best;
            }
            break;
         }

         node = next;
      }

      return node._value;
   }

   @Override
   public V getBest(ByteBuffer b, int offset, int len) {
      return b.hasArray() ? this.getBest(b.array(), b.arrayOffset() + b.position() + offset, len) : this.getBest(this._root, b, offset, len);
   }

   private V getBest(TreeTrie.Node<V> node, ByteBuffer b, int offset, int len) {
      int pos = b.position() + offset;

      for (int i = 0; i < len; i++) {
         byte c = b.get(pos++);
         int index = c >= 0 && c < 127 ? this._lookup[c] : -1;
         TreeTrie.Node<V> next;
         if (index >= 0) {
            if (node._nextIndex[index] == null) {
               break;
            }

            next = node._nextIndex[index];
         } else {
            TreeTrie.Node<V> n = null;

            for (int j = node._nextOther.size(); j-- > 0; n = null) {
               n = node._nextOther.get(j);
               if (n._c == c) {
                  break;
               }
            }

            if (n == null) {
               break;
            }

            next = n;
         }

         if (node._key != null) {
            V best = this.getBest(next, b, offset + i + 1, len - i - 1);
            if (best != null) {
               return best;
            }
            break;
         }

         node = next;
      }

      return node._value;
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("TT@").append(Integer.toHexString(this.hashCode())).append('{');
      buf.append("ci=").append(this.isCaseInsensitive()).append(';');
      toString(buf, this._root, "");
      buf.append('}');
      return buf.toString();
   }

   private static <V> void toString(Appendable out, TreeTrie.Node<V> t, String separator) {
      label48:
      while (t != null) {
         if (t._value != null) {
            try {
               out.append(separator);
               separator = ",";
               out.append(t._key);
               out.append('=');
               out.append(t._value.toString());
            } catch (IOException var5) {
               throw new RuntimeException(var5);
            }
         }

         int i = 0;

         while (i < 32) {
            TreeTrie.Node<V> n = t._nextIndex[i++];
            if (n != null) {
               if (i == 32 && t._nextOther.size() == 0) {
                  t = n;
                  continue label48;
               }

               toString(out, n, separator);
            }
         }

         i = t._nextOther.size();

         while (i-- > 0) {
            if (i == 0) {
               t = t._nextOther.get(i);
               continue label48;
            }

            toString(out, t._nextOther.get(i), separator);
         }
         break;
      }
   }

   @Override
   public Set<String> keySet() {
      Set<String> keys = new HashSet<>();
      keySet(keys, this._root);
      return keys;
   }

   private static <V> void keySet(Set<String> set, TreeTrie.Node<V> t) {
      if (t != null) {
         if (t._key != null) {
            set.add(t._key);
         }

         for (int i = 0; i < 32; i++) {
            if (t._nextIndex[i] != null) {
               keySet(set, t._nextIndex[i]);
            }
         }

         int ix = t._nextOther.size();

         while (ix-- > 0) {
            keySet(set, t._nextOther.get(ix));
         }
      }
   }

   private static class Node<V> {
      private final TreeTrie.Node<V>[] _nextIndex;
      private final List<TreeTrie.Node<V>> _nextOther = new ArrayList<>();
      private final char _c;
      private String _key;
      private V _value;

      private Node(char c) {
         this._nextIndex = new TreeTrie.Node[32];
         this._c = c;
      }
   }
}
