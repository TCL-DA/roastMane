package com.fasterxml.jackson.core.filter;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserDelegate;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class FilteringParserDelegate extends JsonParserDelegate {
   protected TokenFilter rootFilter;
   protected boolean _allowMultipleMatches;
   protected TokenFilter.Inclusion _inclusion;
   protected JsonToken _currToken;
   protected JsonToken _lastClearedToken;
   protected TokenFilterContext _headContext;
   protected TokenFilterContext _exposedContext;
   protected TokenFilter _itemFilter;
   protected int _matchCount;

   @Deprecated
   public FilteringParserDelegate(JsonParser p, TokenFilter f, boolean includePath, boolean allowMultipleMatches) {
      this(p, f, includePath ? TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH : TokenFilter.Inclusion.ONLY_INCLUDE_ALL, allowMultipleMatches);
   }

   public FilteringParserDelegate(JsonParser p, TokenFilter f, TokenFilter.Inclusion inclusion, boolean allowMultipleMatches) {
      super(p);
      this.rootFilter = f;
      this._itemFilter = f;
      this._headContext = TokenFilterContext.createRootContext(f);
      this._inclusion = inclusion;
      this._allowMultipleMatches = allowMultipleMatches;
   }

   public TokenFilter getFilter() {
      return this.rootFilter;
   }

   public int getMatchCount() {
      return this._matchCount;
   }

   @Override
   public JsonToken getCurrentToken() {
      return this._currToken;
   }

   @Override
   public JsonToken currentToken() {
      return this._currToken;
   }

   @Deprecated
   @Override
   public final int getCurrentTokenId() {
      return this.currentTokenId();
   }

   @Override
   public final int currentTokenId() {
      JsonToken t = this._currToken;
      return t == null ? 0 : t.id();
   }

   @Override
   public boolean hasCurrentToken() {
      return this._currToken != null;
   }

   @Override
   public boolean hasTokenId(int id) {
      JsonToken t = this._currToken;
      return t == null ? 0 == id : t.id() == id;
   }

   @Override
   public final boolean hasToken(JsonToken t) {
      return this._currToken == t;
   }

   @Override
   public boolean isExpectedStartArrayToken() {
      return this._currToken == JsonToken.START_ARRAY;
   }

   @Override
   public boolean isExpectedStartObjectToken() {
      return this._currToken == JsonToken.START_OBJECT;
   }

   @Override
   public JsonLocation getCurrentLocation() {
      return this.delegate.getCurrentLocation();
   }

   @Override
   public JsonStreamContext getParsingContext() {
      return this._filterContext();
   }

   @Override
   public String getCurrentName() throws IOException {
      JsonStreamContext ctxt = this._filterContext();
      if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY) {
         return ctxt.getCurrentName();
      } else {
         JsonStreamContext parent = ctxt.getParent();
         return parent == null ? null : parent.getCurrentName();
      }
   }

   @Override
   public String currentName() throws IOException {
      JsonStreamContext ctxt = this._filterContext();
      if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY) {
         return ctxt.getCurrentName();
      } else {
         JsonStreamContext parent = ctxt.getParent();
         return parent == null ? null : parent.getCurrentName();
      }
   }

   @Override
   public void clearCurrentToken() {
      if (this._currToken != null) {
         this._lastClearedToken = this._currToken;
         this._currToken = null;
      }
   }

   @Override
   public JsonToken getLastClearedToken() {
      return this._lastClearedToken;
   }

   @Override
   public void overrideCurrentName(String name) {
      throw new UnsupportedOperationException("Can not currently override name during filtering read");
   }

   @Override
   public JsonToken nextToken() throws IOException {
      if (!this._allowMultipleMatches
         && this._currToken != null
         && this._exposedContext == null
         && this._currToken.isScalarValue()
         && !this._headContext.isStartHandled()
         && this._inclusion == TokenFilter.Inclusion.ONLY_INCLUDE_ALL
         && this._itemFilter == TokenFilter.INCLUDE_ALL) {
         return this._currToken = null;
      } else {
         TokenFilterContext ctxt = this._exposedContext;
         if (ctxt != null) {
            while (true) {
               JsonToken t = ctxt.nextTokenToRead();
               if (t != null) {
                  this._currToken = t;
                  return t;
               }

               if (ctxt == this._headContext) {
                  this._exposedContext = null;
                  if (ctxt.inArray()) {
                     t = this.delegate.getCurrentToken();
                     this._currToken = t;
                     if (this._currToken == JsonToken.END_ARRAY) {
                        this._headContext = this._headContext.getParent();
                        this._itemFilter = this._headContext.getFilter();
                     }

                     return t;
                  }

                  t = this.delegate.currentToken();
                  if (t == JsonToken.END_OBJECT) {
                     this._headContext = this._headContext.getParent();
                     this._itemFilter = this._headContext.getFilter();
                  }

                  if (t != JsonToken.FIELD_NAME) {
                     this._currToken = t;
                     return t;
                  }
                  break;
               }

               ctxt = this._headContext.findChildOf(ctxt);
               this._exposedContext = ctxt;
               if (ctxt == null) {
                  throw this._constructError("Unexpected problem: chain of filtered context broken");
               }
            }
         }

         JsonToken tx = this.delegate.nextToken();
         if (tx == null) {
            this._currToken = tx;
            return tx;
         } else {
            switch (tx.id()) {
               case 1:
                  TokenFilter fxx = this._itemFilter;
                  if (fxx == TokenFilter.INCLUDE_ALL) {
                     this._headContext = this._headContext.createChildObjectContext(fxx, true);
                     return this._currToken = tx;
                  }

                  if (fxx == null) {
                     this.delegate.skipChildren();
                  } else {
                     fxx = this._headContext.checkValue(fxx);
                     if (fxx == null) {
                        this.delegate.skipChildren();
                     } else {
                        if (fxx != TokenFilter.INCLUDE_ALL) {
                           fxx = fxx.filterStartObject();
                        }

                        this._itemFilter = fxx;
                        if (fxx == TokenFilter.INCLUDE_ALL) {
                           this._headContext = this._headContext.createChildObjectContext(fxx, true);
                           return this._currToken = tx;
                        }

                        if (fxx != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                           this._headContext = this._headContext.createChildObjectContext(fxx, true);
                           return this._currToken = tx;
                        }

                        this._headContext = this._headContext.createChildObjectContext(fxx, false);
                        if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
                           tx = this._nextTokenWithBuffering(this._headContext);
                           if (tx != null) {
                              this._currToken = tx;
                              return tx;
                           }
                        }
                     }
                  }
                  break;
               case 2:
               case 4:
                  boolean returnEnd = this._headContext.isStartHandled();
                  TokenFilter fx = this._headContext.getFilter();
                  if (fx != null && fx != TokenFilter.INCLUDE_ALL) {
                     fx.filterFinishArray();
                  }

                  this._headContext = this._headContext.getParent();
                  this._itemFilter = this._headContext.getFilter();
                  if (returnEnd) {
                     return this._currToken = tx;
                  }
                  break;
               case 3:
                  TokenFilter fxxx = this._itemFilter;
                  if (fxxx == TokenFilter.INCLUDE_ALL) {
                     this._headContext = this._headContext.createChildArrayContext(fxxx, true);
                     return this._currToken = tx;
                  }

                  if (fxxx == null) {
                     this.delegate.skipChildren();
                  } else {
                     fxxx = this._headContext.checkValue(fxxx);
                     if (fxxx == null) {
                        this.delegate.skipChildren();
                     } else {
                        if (fxxx != TokenFilter.INCLUDE_ALL) {
                           fxxx = fxxx.filterStartArray();
                        }

                        this._itemFilter = fxxx;
                        if (fxxx == TokenFilter.INCLUDE_ALL) {
                           this._headContext = this._headContext.createChildArrayContext(fxxx, true);
                           return this._currToken = tx;
                        }

                        if (fxxx != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                           this._headContext = this._headContext.createChildArrayContext(fxxx, true);
                           return this._currToken = tx;
                        }

                        this._headContext = this._headContext.createChildArrayContext(fxxx, false);
                        if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
                           tx = this._nextTokenWithBuffering(this._headContext);
                           if (tx != null) {
                              this._currToken = tx;
                              return tx;
                           }
                        }
                     }
                  }
                  break;
               case 5:
                  String name = this.delegate.getCurrentName();
                  TokenFilter f = this._headContext.setFieldName(name);
                  if (f == TokenFilter.INCLUDE_ALL) {
                     this._itemFilter = f;
                     return this._currToken = tx;
                  }

                  if (f == null) {
                     this.delegate.nextToken();
                     this.delegate.skipChildren();
                  } else {
                     f = f.includeProperty(name);
                     if (f == null) {
                        this.delegate.nextToken();
                        this.delegate.skipChildren();
                     } else {
                        this._itemFilter = f;
                        if (f == TokenFilter.INCLUDE_ALL) {
                           if (this._verifyAllowedMatches()) {
                              if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
                                 return this._currToken = tx;
                              }
                           } else {
                              this.delegate.nextToken();
                              this.delegate.skipChildren();
                           }
                        }

                        if (this._inclusion != TokenFilter.Inclusion.ONLY_INCLUDE_ALL) {
                           tx = this._nextTokenWithBuffering(this._headContext);
                           if (tx != null) {
                              this._currToken = tx;
                              return tx;
                           }
                        }
                     }
                  }
                  break;
               default:
                  TokenFilter fxxxx = this._itemFilter;
                  if (fxxxx == TokenFilter.INCLUDE_ALL) {
                     return this._currToken = tx;
                  }

                  if (fxxxx != null) {
                     fxxxx = this._headContext.checkValue(fxxxx);
                     if ((fxxxx == TokenFilter.INCLUDE_ALL || fxxxx != null && fxxxx.includeValue(this.delegate)) && this._verifyAllowedMatches()) {
                        return this._currToken = tx;
                     }
                  }
            }

            return this._nextToken2();
         }
      }
   }

   protected final JsonToken _nextToken2() throws IOException {
      while (true) {
         JsonToken t = this.delegate.nextToken();
         if (t == null) {
            this._currToken = t;
            return t;
         }

         switch (t.id()) {
            case 1:
               TokenFilter fxxxx = this._itemFilter;
               if (fxxxx == TokenFilter.INCLUDE_ALL) {
                  this._headContext = this._headContext.createChildObjectContext(fxxxx, true);
                  return this._currToken = t;
               }

               if (fxxxx == null) {
                  this.delegate.skipChildren();
               } else {
                  fxxxx = this._headContext.checkValue(fxxxx);
                  if (fxxxx == null) {
                     this.delegate.skipChildren();
                  } else {
                     if (fxxxx != TokenFilter.INCLUDE_ALL) {
                        fxxxx = fxxxx.filterStartObject();
                     }

                     this._itemFilter = fxxxx;
                     if (fxxxx == TokenFilter.INCLUDE_ALL) {
                        this._headContext = this._headContext.createChildObjectContext(fxxxx, true);
                        return this._currToken = t;
                     }

                     if (fxxxx != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                        this._headContext = this._headContext.createChildObjectContext(fxxxx, true);
                        return this._currToken = t;
                     }

                     this._headContext = this._headContext.createChildObjectContext(fxxxx, false);
                     if (this._inclusion != TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
                        continue;
                     }

                     t = this._nextTokenWithBuffering(this._headContext);
                     if (t == null) {
                        continue;
                     }

                     this._currToken = t;
                     return t;
                  }
               }
               break;
            case 2:
               boolean returnEndx = this._headContext.isStartHandled();
               TokenFilter fxxx = this._headContext.getFilter();
               if (fxxx != null && fxxx != TokenFilter.INCLUDE_ALL) {
                  boolean includeEmpty = fxxx.includeEmptyArray(this._headContext.hasCurrentName());
                  fxxx.filterFinishObject();
                  if (includeEmpty) {
                     return this._nextBuffered(this._headContext);
                  }
               }

               this._headContext = this._headContext.getParent();
               this._itemFilter = this._headContext.getFilter();
               if (!returnEndx) {
                  break;
               }

               return this._currToken = t;
            case 3:
               TokenFilter fxx = this._itemFilter;
               if (fxx == TokenFilter.INCLUDE_ALL) {
                  this._headContext = this._headContext.createChildArrayContext(fxx, true);
                  return this._currToken = t;
               }

               if (fxx == null) {
                  this.delegate.skipChildren();
               } else {
                  fxx = this._headContext.checkValue(fxx);
                  if (fxx == null) {
                     this.delegate.skipChildren();
                  } else {
                     if (fxx != TokenFilter.INCLUDE_ALL) {
                        fxx = fxx.filterStartArray();
                     }

                     this._itemFilter = fxx;
                     if (fxx == TokenFilter.INCLUDE_ALL) {
                        this._headContext = this._headContext.createChildArrayContext(fxx, true);
                        return this._currToken = t;
                     }

                     if (fxx != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                        this._headContext = this._headContext.createChildArrayContext(fxx, true);
                        return this._currToken = t;
                     }

                     this._headContext = this._headContext.createChildArrayContext(fxx, false);
                     if (this._inclusion != TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
                        continue;
                     }

                     t = this._nextTokenWithBuffering(this._headContext);
                     if (t == null) {
                        continue;
                     }

                     this._currToken = t;
                     return t;
                  }
               }
               break;
            case 4:
               boolean returnEnd = this._headContext.isStartHandled();
               TokenFilter fx = this._headContext.getFilter();
               if (fx != null && fx != TokenFilter.INCLUDE_ALL) {
                  boolean includeEmpty = fx.includeEmptyArray(this._headContext.hasCurrentIndex());
                  fx.filterFinishArray();
                  if (includeEmpty) {
                     return this._nextBuffered(this._headContext);
                  }
               }

               this._headContext = this._headContext.getParent();
               this._itemFilter = this._headContext.getFilter();
               if (!returnEnd) {
                  break;
               }

               return this._currToken = t;
            case 5:
               String name = this.delegate.getCurrentName();
               TokenFilter f = this._headContext.setFieldName(name);
               if (f == TokenFilter.INCLUDE_ALL) {
                  this._itemFilter = f;
                  return this._currToken = t;
               }

               if (f == null) {
                  this.delegate.nextToken();
                  this.delegate.skipChildren();
               } else {
                  f = f.includeProperty(name);
                  if (f == null) {
                     this.delegate.nextToken();
                     this.delegate.skipChildren();
                  } else {
                     this._itemFilter = f;
                     if (f == TokenFilter.INCLUDE_ALL) {
                        if (this._verifyAllowedMatches()) {
                           if (this._inclusion != TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
                              continue;
                           }

                           return this._currToken = t;
                        }

                        this.delegate.nextToken();
                        this.delegate.skipChildren();
                     } else {
                        if (this._inclusion == TokenFilter.Inclusion.ONLY_INCLUDE_ALL) {
                           continue;
                        }

                        t = this._nextTokenWithBuffering(this._headContext);
                        if (t == null) {
                           continue;
                        }

                        this._currToken = t;
                        return t;
                     }
                  }
               }
               break;
            default:
               TokenFilter fxxxxx = this._itemFilter;
               if (fxxxxx == TokenFilter.INCLUDE_ALL) {
                  return this._currToken = t;
               }

               if (fxxxxx != null) {
                  fxxxxx = this._headContext.checkValue(fxxxxx);
                  if ((fxxxxx == TokenFilter.INCLUDE_ALL || fxxxxx != null && fxxxxx.includeValue(this.delegate)) && this._verifyAllowedMatches()) {
                     return this._currToken = t;
                  }
               }
         }
      }
   }

   protected final JsonToken _nextTokenWithBuffering(TokenFilterContext buffRoot) throws IOException {
      while (true) {
         JsonToken t = this.delegate.nextToken();
         if (t == null) {
            return t;
         }

         switch (t.id()) {
            case 1:
               TokenFilter fxx = this._itemFilter;
               if (fxx == TokenFilter.INCLUDE_ALL) {
                  this._headContext = this._headContext.createChildObjectContext(fxx, true);
                  return t;
               }

               if (fxx == null) {
                  this.delegate.skipChildren();
               } else {
                  fxx = this._headContext.checkValue(fxx);
                  if (fxx == null) {
                     this.delegate.skipChildren();
                     break;
                  }

                  if (fxx != TokenFilter.INCLUDE_ALL) {
                     fxx = fxx.filterStartObject();
                  }

                  this._itemFilter = fxx;
                  if (fxx == TokenFilter.INCLUDE_ALL) {
                     this._headContext = this._headContext.createChildObjectContext(fxx, true);
                     return this._nextBuffered(buffRoot);
                  }

                  if (fxx != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                     this._headContext = this._headContext.createChildArrayContext(fxx, true);
                     return this._nextBuffered(buffRoot);
                  }

                  this._headContext = this._headContext.createChildObjectContext(fxx, false);
               }
               break;
            case 2:
               TokenFilter fxxxxx = this._headContext.getFilter();
               if (fxxxxx != null && fxxxxx != TokenFilter.INCLUDE_ALL) {
                  boolean includeEmpty = fxxxxx.includeEmptyObject(this._headContext.hasCurrentName());
                  fxxxxx.filterFinishObject();
                  if (includeEmpty) {
                     this._headContext._currentName = this._headContext._parent == null ? null : this._headContext._parent._currentName;
                     this._headContext._needToHandleName = false;
                     return this._nextBuffered(buffRoot);
                  }
               }

               boolean gotEndx = this._headContext == buffRoot;
               boolean returnEndx = gotEndx && this._headContext.isStartHandled();
               this._headContext = this._headContext.getParent();
               this._itemFilter = this._headContext.getFilter();
               if (returnEndx) {
                  return t;
               }

               if (gotEndx) {
                  return null;
               }
               break;
            case 3:
               TokenFilter fx = this._headContext.checkValue(this._itemFilter);
               if (fx == null) {
                  this.delegate.skipChildren();
                  break;
               }

               if (fx != TokenFilter.INCLUDE_ALL) {
                  fx = fx.filterStartArray();
               }

               this._itemFilter = fx;
               if (fx == TokenFilter.INCLUDE_ALL) {
                  this._headContext = this._headContext.createChildArrayContext(fx, true);
                  return this._nextBuffered(buffRoot);
               }

               if (fx != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                  this._headContext = this._headContext.createChildArrayContext(fx, true);
                  return this._nextBuffered(buffRoot);
               }

               this._headContext = this._headContext.createChildArrayContext(fx, false);
               break;
            case 4:
               TokenFilter fxxxx = this._headContext.getFilter();
               if (fxxxx != null && fxxxx != TokenFilter.INCLUDE_ALL) {
                  boolean includeEmpty = fxxxx.includeEmptyArray(this._headContext.hasCurrentIndex());
                  fxxxx.filterFinishArray();
                  if (includeEmpty) {
                     return this._nextBuffered(buffRoot);
                  }
               }

               boolean gotEnd = this._headContext == buffRoot;
               boolean returnEnd = gotEnd && this._headContext.isStartHandled();
               this._headContext = this._headContext.getParent();
               this._itemFilter = this._headContext.getFilter();
               if (returnEnd) {
                  return t;
               }

               if (!gotEnd) {
                  break;
               }

               return null;
            case 5:
               String name = this.delegate.getCurrentName();
               TokenFilter f = this._headContext.setFieldName(name);
               if (f == TokenFilter.INCLUDE_ALL) {
                  this._itemFilter = f;
                  return this._nextBuffered(buffRoot);
               }

               if (f == null) {
                  this.delegate.nextToken();
                  this.delegate.skipChildren();
               } else {
                  f = f.includeProperty(name);
                  if (f == null) {
                     this.delegate.nextToken();
                     this.delegate.skipChildren();
                  } else {
                     this._itemFilter = f;
                     if (f != TokenFilter.INCLUDE_ALL) {
                        break;
                     }

                     if (this._verifyAllowedMatches()) {
                        return this._nextBuffered(buffRoot);
                     }

                     this._itemFilter = this._headContext.setFieldName(name);
                  }
               }
               break;
            default:
               TokenFilter fxxx = this._itemFilter;
               if (fxxx == TokenFilter.INCLUDE_ALL) {
                  return this._nextBuffered(buffRoot);
               }

               if (fxxx != null) {
                  fxxx = this._headContext.checkValue(fxxx);
                  if ((fxxx == TokenFilter.INCLUDE_ALL || fxxx != null && fxxx.includeValue(this.delegate)) && this._verifyAllowedMatches()) {
                     return this._nextBuffered(buffRoot);
                  }
               }
         }
      }
   }

   private JsonToken _nextBuffered(TokenFilterContext buffRoot) throws IOException {
      this._exposedContext = buffRoot;
      TokenFilterContext ctxt = buffRoot;
      JsonToken t = buffRoot.nextTokenToRead();
      if (t != null) {
         return t;
      } else {
         while (ctxt != this._headContext) {
            ctxt = this._exposedContext.findChildOf(ctxt);
            this._exposedContext = ctxt;
            if (ctxt == null) {
               throw this._constructError("Unexpected problem: chain of filtered context broken");
            }

            t = this._exposedContext.nextTokenToRead();
            if (t != null) {
               return t;
            }
         }

         throw this._constructError("Internal error: failed to locate expected buffered tokens");
      }
   }

   private final boolean _verifyAllowedMatches() throws IOException {
      if (this._matchCount != 0 && !this._allowMultipleMatches) {
         return false;
      } else {
         this._matchCount++;
         return true;
      }
   }

   @Override
   public JsonToken nextValue() throws IOException {
      JsonToken t = this.nextToken();
      if (t == JsonToken.FIELD_NAME) {
         t = this.nextToken();
      }

      return t;
   }

   @Override
   public JsonParser skipChildren() throws IOException {
      if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY) {
         return this;
      } else {
         int open = 1;

         while (true) {
            JsonToken t = this.nextToken();
            if (t == null) {
               return this;
            }

            if (t.isStructStart()) {
               open++;
            } else if (t.isStructEnd()) {
               if (--open == 0) {
                  return this;
               }
            }
         }
      }
   }

   @Override
   public String getText() throws IOException {
      return this._currToken == JsonToken.FIELD_NAME ? this.currentName() : this.delegate.getText();
   }

   @Override
   public boolean hasTextCharacters() {
      return this._currToken == JsonToken.FIELD_NAME ? false : this.delegate.hasTextCharacters();
   }

   @Override
   public char[] getTextCharacters() throws IOException {
      return this._currToken == JsonToken.FIELD_NAME ? this.currentName().toCharArray() : this.delegate.getTextCharacters();
   }

   @Override
   public int getTextLength() throws IOException {
      return this._currToken == JsonToken.FIELD_NAME ? this.currentName().length() : this.delegate.getTextLength();
   }

   @Override
   public int getTextOffset() throws IOException {
      return this._currToken == JsonToken.FIELD_NAME ? 0 : this.delegate.getTextOffset();
   }

   @Override
   public BigInteger getBigIntegerValue() throws IOException {
      return this.delegate.getBigIntegerValue();
   }

   @Override
   public boolean getBooleanValue() throws IOException {
      return this.delegate.getBooleanValue();
   }

   @Override
   public byte getByteValue() throws IOException {
      return this.delegate.getByteValue();
   }

   @Override
   public short getShortValue() throws IOException {
      return this.delegate.getShortValue();
   }

   @Override
   public BigDecimal getDecimalValue() throws IOException {
      return this.delegate.getDecimalValue();
   }

   @Override
   public double getDoubleValue() throws IOException {
      return this.delegate.getDoubleValue();
   }

   @Override
   public float getFloatValue() throws IOException {
      return this.delegate.getFloatValue();
   }

   @Override
   public int getIntValue() throws IOException {
      return this.delegate.getIntValue();
   }

   @Override
   public long getLongValue() throws IOException {
      return this.delegate.getLongValue();
   }

   @Override
   public JsonParser.NumberType getNumberType() throws IOException {
      return this.delegate.getNumberType();
   }

   @Override
   public Number getNumberValue() throws IOException {
      return this.delegate.getNumberValue();
   }

   @Override
   public int getValueAsInt() throws IOException {
      return this.delegate.getValueAsInt();
   }

   @Override
   public int getValueAsInt(int defaultValue) throws IOException {
      return this.delegate.getValueAsInt(defaultValue);
   }

   @Override
   public long getValueAsLong() throws IOException {
      return this.delegate.getValueAsLong();
   }

   @Override
   public long getValueAsLong(long defaultValue) throws IOException {
      return this.delegate.getValueAsLong(defaultValue);
   }

   @Override
   public double getValueAsDouble() throws IOException {
      return this.delegate.getValueAsDouble();
   }

   @Override
   public double getValueAsDouble(double defaultValue) throws IOException {
      return this.delegate.getValueAsDouble(defaultValue);
   }

   @Override
   public boolean getValueAsBoolean() throws IOException {
      return this.delegate.getValueAsBoolean();
   }

   @Override
   public boolean getValueAsBoolean(boolean defaultValue) throws IOException {
      return this.delegate.getValueAsBoolean(defaultValue);
   }

   @Override
   public String getValueAsString() throws IOException {
      return this._currToken == JsonToken.FIELD_NAME ? this.currentName() : this.delegate.getValueAsString();
   }

   @Override
   public String getValueAsString(String defaultValue) throws IOException {
      return this._currToken == JsonToken.FIELD_NAME ? this.currentName() : this.delegate.getValueAsString(defaultValue);
   }

   @Override
   public Object getEmbeddedObject() throws IOException {
      return this.delegate.getEmbeddedObject();
   }

   @Override
   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
      return this.delegate.getBinaryValue(b64variant);
   }

   @Override
   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
      return this.delegate.readBinaryValue(b64variant, out);
   }

   @Override
   public JsonLocation getTokenLocation() {
      return this.delegate.getTokenLocation();
   }

   protected JsonStreamContext _filterContext() {
      return this._exposedContext != null ? this._exposedContext : this._headContext;
   }
}
