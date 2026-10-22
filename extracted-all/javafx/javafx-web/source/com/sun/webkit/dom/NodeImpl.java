package com.sun.webkit.dom;

import com.sun.webkit.Disposer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public class NodeImpl extends JSObject implements Node, EventTarget {
   private static NodeImpl.SelfDisposer[] hashTable = new NodeImpl.SelfDisposer[64];
   private static int hashCount;
   public static final int ELEMENT_NODE = 1;
   public static final int ATTRIBUTE_NODE = 2;
   public static final int TEXT_NODE = 3;
   public static final int CDATA_SECTION_NODE = 4;
   public static final int ENTITY_REFERENCE_NODE = 5;
   public static final int ENTITY_NODE = 6;
   public static final int PROCESSING_INSTRUCTION_NODE = 7;
   public static final int COMMENT_NODE = 8;
   public static final int DOCUMENT_NODE = 9;
   public static final int DOCUMENT_TYPE_NODE = 10;
   public static final int DOCUMENT_FRAGMENT_NODE = 11;
   public static final int NOTATION_NODE = 12;
   public static final int DOCUMENT_POSITION_DISCONNECTED = 1;
   public static final int DOCUMENT_POSITION_PRECEDING = 2;
   public static final int DOCUMENT_POSITION_FOLLOWING = 4;
   public static final int DOCUMENT_POSITION_CONTAINS = 8;
   public static final int DOCUMENT_POSITION_CONTAINED_BY = 16;
   public static final int DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 32;

   private static int hashPeer(long var0) {
      return (int)(~var0 ^ var0 >> 7) & hashTable.length - 1;
   }

   private static Node getCachedImpl(long var0) {
      if (var0 == 0L) {
         return null;
      } else {
         int var2 = hashPeer(var0);
         NodeImpl.SelfDisposer var3 = hashTable[var2];
         NodeImpl.SelfDisposer var4 = null;
         NodeImpl.SelfDisposer var5 = var3;

         while (var5 != null) {
            NodeImpl.SelfDisposer var6 = var5.next;
            if (var5.peer == var0) {
               NodeImpl var7 = (NodeImpl)var5.get();
               if (var7 != null) {
                  dispose(var0);
                  return var7;
               }

               if (var4 != null) {
                  var4.next = var6;
               } else {
                  hashTable[var2] = var6;
               }
               break;
            }

            var4 = var5;
            var5 = var6;
         }

         NodeImpl var8 = (NodeImpl)createInterface(var0);
         NodeImpl.SelfDisposer var9 = new NodeImpl.SelfDisposer(var8, var0);
         Disposer.addRecord(var9);
         var9.next = var3;
         hashTable[var2] = var9;
         if (3 * hashCount >= 2 * hashTable.length) {
            rehash();
         }

         hashCount++;
         return var8;
      }
   }

   static int test_getHashCount() {
      return hashCount;
   }

   private static void rehash() {
      NodeImpl.SelfDisposer[] var0 = hashTable;
      int var1 = var0.length;
      NodeImpl.SelfDisposer[] var2 = new NodeImpl.SelfDisposer[2 * var1];
      hashTable = var2;
      int var3 = var1;

      while (--var3 >= 0) {
         NodeImpl.SelfDisposer var4 = var0[var3];

         while (var4 != null) {
            NodeImpl.SelfDisposer var5 = var4.next;
            int var6 = hashPeer(var4.peer);
            var4.next = var2[var6];
            var2[var6] = var4;
            var4 = var5;
         }
      }
   }

   NodeImpl(long var1) {
      super(var1, 1);
   }

   static Node createInterface(long var0) {
      if (var0 == 0L) {
         return null;
      } else {
         switch (getNodeTypeImpl(var0)) {
            case 1:
               if (!ElementImpl.isHTMLElementImpl(var0)) {
                  return new ElementImpl(var0);
               } else {
                  String var2 = ElementImpl.getTagNameImpl(var0).toUpperCase();
                  if ("A".equals(var2)) {
                     return new HTMLAnchorElementImpl(var0);
                  } else if ("APPLET".equals(var2)) {
                     return new HTMLAppletElementImpl(var0);
                  } else if ("AREA".equals(var2)) {
                     return new HTMLAreaElementImpl(var0);
                  } else if ("BASE".equals(var2)) {
                     return new HTMLBaseElementImpl(var0);
                  } else if ("BASEFONT".equals(var2)) {
                     return new HTMLBaseFontElementImpl(var0);
                  } else if ("BODY".equals(var2)) {
                     return new HTMLBodyElementImpl(var0);
                  } else if ("BR".equals(var2)) {
                     return new HTMLBRElementImpl(var0);
                  } else if ("BUTTON".equals(var2)) {
                     return new HTMLButtonElementImpl(var0);
                  } else if ("DIR".equals(var2)) {
                     return new HTMLDirectoryElementImpl(var0);
                  } else if ("DIV".equals(var2)) {
                     return new HTMLDivElementImpl(var0);
                  } else if ("DL".equals(var2)) {
                     return new HTMLDListElementImpl(var0);
                  } else if ("FIELDSET".equals(var2)) {
                     return new HTMLFieldSetElementImpl(var0);
                  } else if ("FONT".equals(var2)) {
                     return new HTMLFontElementImpl(var0);
                  } else if ("FORM".equals(var2)) {
                     return new HTMLFormElementImpl(var0);
                  } else if ("FRAME".equals(var2)) {
                     return new HTMLFrameElementImpl(var0);
                  } else if ("FRAMESET".equals(var2)) {
                     return new HTMLFrameSetElementImpl(var0);
                  } else if ("HEAD".equals(var2)) {
                     return new HTMLHeadElementImpl(var0);
                  } else if (var2.length() == 2 && var2.charAt(0) == 'H' && var2.charAt(1) >= '1' && var2.charAt(1) <= '6') {
                     return new HTMLHeadingElementImpl(var0);
                  } else if ("HR".equals(var2)) {
                     return new HTMLHRElementImpl(var0);
                  } else if ("IFRAME".equals(var2)) {
                     return new HTMLIFrameElementImpl(var0);
                  } else if ("IMG".equals(var2)) {
                     return new HTMLImageElementImpl(var0);
                  } else if ("INPUT".equals(var2)) {
                     return new HTMLInputElementImpl(var0);
                  } else if ("LABEL".equals(var2)) {
                     return new HTMLLabelElementImpl(var0);
                  } else if ("LEGEND".equals(var2)) {
                     return new HTMLLegendElementImpl(var0);
                  } else if ("LI".equals(var2)) {
                     return new HTMLLIElementImpl(var0);
                  } else if ("LINK".equals(var2)) {
                     return new HTMLLinkElementImpl(var0);
                  } else if ("MAP".equals(var2)) {
                     return new HTMLMapElementImpl(var0);
                  } else if ("MENU".equals(var2)) {
                     return new HTMLMenuElementImpl(var0);
                  } else if ("META".equals(var2)) {
                     return new HTMLMetaElementImpl(var0);
                  } else {
                     if (!"INS".equals(var2) && !"DEL".equals(var2)) {
                        if ("OBJECT".equals(var2)) {
                           return new HTMLObjectElementImpl(var0);
                        }

                        if ("OL".equals(var2)) {
                           return new HTMLOListElementImpl(var0);
                        }

                        if ("OPTGROUP".equals(var2)) {
                           return new HTMLOptGroupElementImpl(var0);
                        }

                        if ("OPTION".equals(var2)) {
                           return new HTMLOptionElementImpl(var0);
                        }

                        if ("P".equals(var2)) {
                           return new HTMLParagraphElementImpl(var0);
                        }

                        if ("PARAM".equals(var2)) {
                           return new HTMLParamElementImpl(var0);
                        }

                        if ("PRE".equals(var2)) {
                           return new HTMLPreElementImpl(var0);
                        }

                        if ("Q".equals(var2)) {
                           return new HTMLQuoteElementImpl(var0);
                        }

                        if ("SCRIPT".equals(var2)) {
                           return new HTMLScriptElementImpl(var0);
                        }

                        if ("SELECT".equals(var2)) {
                           return new HTMLSelectElementImpl(var0);
                        }

                        if ("STYLE".equals(var2)) {
                           return new HTMLStyleElementImpl(var0);
                        }

                        if ("CAPTION".equals(var2)) {
                           return new HTMLTableCaptionElementImpl(var0);
                        }

                        if ("TD".equals(var2)) {
                           return new HTMLTableCellElementImpl(var0);
                        }

                        if ("COL".equals(var2)) {
                           return new HTMLTableColElementImpl(var0);
                        }

                        if ("TABLE".equals(var2)) {
                           return new HTMLTableElementImpl(var0);
                        }

                        if ("TR".equals(var2)) {
                           return new HTMLTableRowElementImpl(var0);
                        }

                        if (!"THEAD".equals(var2) && !"TFOOT".equals(var2) && !"TBODY".equals(var2)) {
                           if ("TEXTAREA".equals(var2)) {
                              return new HTMLTextAreaElementImpl(var0);
                           }

                           if ("TITLE".equals(var2)) {
                              return new HTMLTitleElementImpl(var0);
                           }

                           if ("UL".equals(var2)) {
                              return new HTMLUListElementImpl(var0);
                           }

                           return new HTMLElementImpl(var0);
                        }

                        return new HTMLTableSectionElementImpl(var0);
                     }

                     return new HTMLModElementImpl(var0);
                  }
               }
            case 2:
               return new AttrImpl(var0);
            case 3:
               return new TextImpl(var0);
            case 4:
               return new CDATASectionImpl(var0);
            case 5:
               return new EntityReferenceImpl(var0);
            case 6:
               return new EntityImpl(var0);
            case 7:
               return new ProcessingInstructionImpl(var0);
            case 8:
               return new CommentImpl(var0);
            case 9:
               if (DocumentImpl.isHTMLDocumentImpl(var0)) {
                  return new HTMLDocumentImpl(var0);
               }

               return new DocumentImpl(var0);
            case 10:
               return new DocumentTypeImpl(var0);
            case 11:
               return new DocumentFragmentImpl(var0);
            default:
               return new NodeImpl(var0);
         }
      }
   }

   static Node create(long var0) {
      return getCachedImpl(var0);
   }

   static long getPeer(Node var0) {
      return var0 == null ? 0L : ((NodeImpl)var0).getPeer();
   }

   private static native void dispose(long var0);

   static Node getImpl(long var0) {
      return create(var0);
   }

   @Override
   public String getNodeName() {
      return getNodeNameImpl(this.getPeer());
   }

   static native String getNodeNameImpl(long var0);

   @Override
   public String getNodeValue() {
      return getNodeValueImpl(this.getPeer());
   }

   static native String getNodeValueImpl(long var0);

   @Override
   public void setNodeValue(String var1) throws DOMException {
      setNodeValueImpl(this.getPeer(), var1);
   }

   static native void setNodeValueImpl(long var0, String var2);

   @Override
   public short getNodeType() {
      return getNodeTypeImpl(this.getPeer());
   }

   static native short getNodeTypeImpl(long var0);

   @Override
   public Node getParentNode() {
      return getImpl(getParentNodeImpl(this.getPeer()));
   }

   static native long getParentNodeImpl(long var0);

   @Override
   public NodeList getChildNodes() {
      return NodeListImpl.getImpl(getChildNodesImpl(this.getPeer()));
   }

   static native long getChildNodesImpl(long var0);

   @Override
   public Node getFirstChild() {
      return getImpl(getFirstChildImpl(this.getPeer()));
   }

   static native long getFirstChildImpl(long var0);

   @Override
   public Node getLastChild() {
      return getImpl(getLastChildImpl(this.getPeer()));
   }

   static native long getLastChildImpl(long var0);

   @Override
   public Node getPreviousSibling() {
      return getImpl(getPreviousSiblingImpl(this.getPeer()));
   }

   static native long getPreviousSiblingImpl(long var0);

   @Override
   public Node getNextSibling() {
      return getImpl(getNextSiblingImpl(this.getPeer()));
   }

   static native long getNextSiblingImpl(long var0);

   @Override
   public Document getOwnerDocument() {
      return DocumentImpl.getImpl(getOwnerDocumentImpl(this.getPeer()));
   }

   static native long getOwnerDocumentImpl(long var0);

   @Override
   public String getNamespaceURI() {
      return getNamespaceURIImpl(this.getPeer());
   }

   static native String getNamespaceURIImpl(long var0);

   @Override
   public String getPrefix() {
      return getPrefixImpl(this.getPeer());
   }

   static native String getPrefixImpl(long var0);

   @Override
   public void setPrefix(String var1) throws DOMException {
      setPrefixImpl(this.getPeer(), var1);
   }

   static native void setPrefixImpl(long var0, String var2);

   @Override
   public String getLocalName() {
      return getLocalNameImpl(this.getPeer());
   }

   static native String getLocalNameImpl(long var0);

   @Override
   public NamedNodeMap getAttributes() {
      return NamedNodeMapImpl.getImpl(getAttributesImpl(this.getPeer()));
   }

   static native long getAttributesImpl(long var0);

   @Override
   public String getBaseURI() {
      return getBaseURIImpl(this.getPeer());
   }

   static native String getBaseURIImpl(long var0);

   @Override
   public String getTextContent() {
      return getTextContentImpl(this.getPeer());
   }

   static native String getTextContentImpl(long var0);

   @Override
   public void setTextContent(String var1) throws DOMException {
      setTextContentImpl(this.getPeer(), var1);
   }

   static native void setTextContentImpl(long var0, String var2);

   public Element getParentElement() {
      return ElementImpl.getImpl(getParentElementImpl(this.getPeer()));
   }

   static native long getParentElementImpl(long var0);

   @Override
   public Node insertBefore(Node var1, Node var2) throws DOMException {
      return getImpl(insertBeforeImpl(this.getPeer(), getPeer(var1), getPeer(var2)));
   }

   static native long insertBeforeImpl(long var0, long var2, long var4);

   @Override
   public Node replaceChild(Node var1, Node var2) throws DOMException {
      return getImpl(replaceChildImpl(this.getPeer(), getPeer(var1), getPeer(var2)));
   }

   static native long replaceChildImpl(long var0, long var2, long var4);

   @Override
   public Node removeChild(Node var1) throws DOMException {
      return getImpl(removeChildImpl(this.getPeer(), getPeer(var1)));
   }

   static native long removeChildImpl(long var0, long var2);

   @Override
   public Node appendChild(Node var1) throws DOMException {
      return getImpl(appendChildImpl(this.getPeer(), getPeer(var1)));
   }

   static native long appendChildImpl(long var0, long var2);

   @Override
   public boolean hasChildNodes() {
      return hasChildNodesImpl(this.getPeer());
   }

   static native boolean hasChildNodesImpl(long var0);

   @Override
   public Node cloneNode(boolean var1) throws DOMException {
      return getImpl(cloneNodeImpl(this.getPeer(), var1));
   }

   static native long cloneNodeImpl(long var0, boolean var2);

   @Override
   public void normalize() {
      normalizeImpl(this.getPeer());
   }

   static native void normalizeImpl(long var0);

   @Override
   public boolean isSupported(String var1, String var2) {
      return isSupportedImpl(this.getPeer(), var1, var2);
   }

   static native boolean isSupportedImpl(long var0, String var2, String var3);

   @Override
   public boolean hasAttributes() {
      return hasAttributesImpl(this.getPeer());
   }

   static native boolean hasAttributesImpl(long var0);

   @Override
   public boolean isSameNode(Node var1) {
      return isSameNodeImpl(this.getPeer(), getPeer(var1));
   }

   static native boolean isSameNodeImpl(long var0, long var2);

   @Override
   public boolean isEqualNode(Node var1) {
      return isEqualNodeImpl(this.getPeer(), getPeer(var1));
   }

   static native boolean isEqualNodeImpl(long var0, long var2);

   @Override
   public String lookupPrefix(String var1) {
      return lookupPrefixImpl(this.getPeer(), var1);
   }

   static native String lookupPrefixImpl(long var0, String var2);

   @Override
   public boolean isDefaultNamespace(String var1) {
      return isDefaultNamespaceImpl(this.getPeer(), var1);
   }

   static native boolean isDefaultNamespaceImpl(long var0, String var2);

   @Override
   public String lookupNamespaceURI(String var1) {
      return lookupNamespaceURIImpl(this.getPeer(), var1);
   }

   static native String lookupNamespaceURIImpl(long var0, String var2);

   @Override
   public short compareDocumentPosition(Node var1) {
      return compareDocumentPositionImpl(this.getPeer(), getPeer(var1));
   }

   static native short compareDocumentPositionImpl(long var0, long var2);

   public boolean contains(Node var1) {
      return containsImpl(this.getPeer(), getPeer(var1));
   }

   static native boolean containsImpl(long var0, long var2);

   @Override
   public void addEventListener(String var1, EventListener var2, boolean var3) {
      addEventListenerImpl(this.getPeer(), var1, EventListenerImpl.getPeer(var2), var3);
   }

   static native void addEventListenerImpl(long var0, String var2, long var3, boolean var5);

   @Override
   public void removeEventListener(String var1, EventListener var2, boolean var3) {
      removeEventListenerImpl(this.getPeer(), var1, EventListenerImpl.getPeer(var2), var3);
   }

   static native void removeEventListenerImpl(long var0, String var2, long var3, boolean var5);

   @Override
   public boolean dispatchEvent(Event var1) throws DOMException {
      return dispatchEventImpl(this.getPeer(), EventImpl.getPeer(var1));
   }

   static native boolean dispatchEventImpl(long var0, long var2);

   @Override
   public Object getUserData(String var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Object setUserData(String var1, Object var2, UserDataHandler var3) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Object getFeature(String var1, String var2) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   private static final class SelfDisposer extends Disposer.WeakDisposerRecord {
      private final long peer;
      NodeImpl.SelfDisposer next;

      SelfDisposer(Object var1, long var2) {
         super(var1);
         this.peer = var2;
      }

      @Override
      public void dispose() {
         int var1 = NodeImpl.hashPeer(this.peer);
         NodeImpl.SelfDisposer var2 = NodeImpl.hashTable[var1];
         NodeImpl.SelfDisposer var3 = null;
         NodeImpl.SelfDisposer var4 = var2;

         while (var4 != null) {
            NodeImpl.SelfDisposer var5 = var4.next;
            if (var4.peer == this.peer) {
               var4.clear();
               if (var3 != null) {
                  var3.next = var5;
               } else {
                  NodeImpl.hashTable[var1] = var5;
               }

               NodeImpl.hashCount--;
               break;
            }

            var3 = var4;
            var4 = var5;
         }

         NodeImpl.dispose(this.peer);
      }
   }
}
