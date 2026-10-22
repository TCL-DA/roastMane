package nonapi.io.github.classgraph.json;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.types.ParseException;

public class JSONDeserializer {
   private JSONDeserializer() {
   }

   private static Object jsonBasicValueToObject(Object jsonVal, Type expectedType, boolean convertStringToNumber) {
      if (jsonVal == null) {
         return null;
      } else if (jsonVal instanceof JSONArray || jsonVal instanceof JSONObject) {
         throw new RuntimeException("Expected a basic value type");
      } else if (expectedType instanceof ParameterizedType) {
         if (((ParameterizedType)expectedType).getRawType().getClass() == Class.class) {
            String str = jsonVal.toString();
            int idx = str.indexOf(60);
            String className = str.substring(0, idx < 0 ? str.length() : idx);

            try {
               return Class.forName(className);
            } catch (ClassNotFoundException var7) {
               throw new IllegalArgumentException("Could not deserialize class reference " + jsonVal, var7);
            }
         } else {
            throw new IllegalArgumentException("Got illegal ParameterizedType: " + expectedType);
         }
      } else if (!(expectedType instanceof Class)) {
         throw new IllegalArgumentException("Got illegal basic value type: " + expectedType);
      } else {
         Class<?> rawType = (Class<?>)expectedType;
         if (rawType == String.class) {
            if (!(jsonVal instanceof CharSequence)) {
               throw new IllegalArgumentException("Expected string; got " + jsonVal.getClass().getName());
            } else {
               return jsonVal.toString();
            }
         } else if (rawType == CharSequence.class) {
            if (!(jsonVal instanceof CharSequence)) {
               throw new IllegalArgumentException("Expected CharSequence; got " + jsonVal.getClass().getName());
            } else {
               return jsonVal;
            }
         } else if (rawType != Integer.class && rawType != int.class) {
            if (rawType == Long.class || rawType == long.class) {
               boolean isLong = jsonVal instanceof Long;
               boolean isInteger = jsonVal instanceof Integer;
               if (convertStringToNumber && jsonVal instanceof CharSequence) {
                  return isLong ? Long.parseLong(jsonVal.toString()) : (long)Integer.parseInt(jsonVal.toString());
               } else if (!isLong && !isInteger) {
                  throw new IllegalArgumentException("Expected long; got " + jsonVal.getClass().getName());
               } else {
                  return isLong ? jsonVal : (long)((Integer)jsonVal).intValue();
               }
            } else if (rawType != Short.class && rawType != short.class) {
               if (rawType != Float.class && rawType != float.class) {
                  if (rawType != Double.class && rawType != double.class) {
                     if (rawType != Byte.class && rawType != byte.class) {
                        if (rawType != Character.class && rawType != char.class) {
                           if (rawType != Boolean.class && rawType != boolean.class) {
                              if (!Enum.class.isAssignableFrom(rawType)) {
                                 if (JSONUtils.getRawType(expectedType).isAssignableFrom(jsonVal.getClass())) {
                                    return jsonVal;
                                 } else {
                                    throw new IllegalArgumentException("Got type " + jsonVal.getClass() + "; expected " + expectedType);
                                 }
                              } else if (!(jsonVal instanceof CharSequence)) {
                                 throw new IllegalArgumentException("Expected string for enum value; got " + jsonVal.getClass().getName());
                              } else {
                                 return Enum.valueOf(rawType, jsonVal.toString());
                              }
                           } else if (convertStringToNumber && jsonVal instanceof CharSequence) {
                              return Boolean.parseBoolean(jsonVal.toString());
                           } else if (!(jsonVal instanceof Boolean)) {
                              throw new IllegalArgumentException("Expected boolean; got " + jsonVal.getClass().getName());
                           } else {
                              return jsonVal;
                           }
                        } else if (!(jsonVal instanceof CharSequence)) {
                           throw new IllegalArgumentException("Expected character; got " + jsonVal.getClass().getName());
                        } else {
                           CharSequence charSequence = (CharSequence)jsonVal;
                           if (charSequence.length() != 1) {
                              throw new IllegalArgumentException("Expected single character; got string");
                           } else {
                              return charSequence.charAt(0);
                           }
                        }
                     } else if (convertStringToNumber && jsonVal instanceof CharSequence) {
                        return Byte.parseByte(jsonVal.toString());
                     } else if (!(jsonVal instanceof Integer)) {
                        throw new IllegalArgumentException("Expected byte; got " + jsonVal.getClass().getName());
                     } else {
                        int intValue = (Integer)jsonVal;
                        if (intValue >= -128 && intValue <= 127) {
                           return (byte)intValue;
                        } else {
                           throw new IllegalArgumentException("Expected byte; got out-of-range value " + intValue);
                        }
                     }
                  } else if (convertStringToNumber && jsonVal instanceof CharSequence) {
                     return Double.parseDouble(jsonVal.toString());
                  } else if (!(jsonVal instanceof Double)) {
                     throw new IllegalArgumentException("Expected double; got " + jsonVal.getClass().getName());
                  } else {
                     return jsonVal;
                  }
               } else if (convertStringToNumber && jsonVal instanceof CharSequence) {
                  return Float.parseFloat(jsonVal.toString());
               } else if (!(jsonVal instanceof Double)) {
                  throw new IllegalArgumentException("Expected float; got " + jsonVal.getClass().getName());
               } else {
                  double doubleValue = (Double)jsonVal;
                  if (!(doubleValue < -Float.MAX_VALUE) && !(doubleValue > Float.MAX_VALUE)) {
                     return (float)doubleValue;
                  } else {
                     throw new IllegalArgumentException("Expected float; got out-of-range value " + doubleValue);
                  }
               }
            } else if (convertStringToNumber && jsonVal instanceof CharSequence) {
               return Short.parseShort(jsonVal.toString());
            } else if (!(jsonVal instanceof Integer)) {
               throw new IllegalArgumentException("Expected short; got " + jsonVal.getClass().getName());
            } else {
               int intValue = (Integer)jsonVal;
               if (intValue >= -32768 && intValue <= 32767) {
                  return (short)intValue;
               } else {
                  throw new IllegalArgumentException("Expected short; got out-of-range value " + intValue);
               }
            }
         } else if (convertStringToNumber && jsonVal instanceof CharSequence) {
            return Integer.parseInt(jsonVal.toString());
         } else if (!(jsonVal instanceof Integer)) {
            throw new IllegalArgumentException("Expected integer; got " + jsonVal.getClass().getName());
         } else {
            return jsonVal;
         }
      }
   }

   private static void populateObjectFromJsonObject(
      Object objectInstance,
      Type objectResolvedType,
      Object jsonVal,
      ClassFieldCache classFieldCache,
      Map<CharSequence, Object> idToObjectInstance,
      List<Runnable> collectionElementAdders
   ) {
      if (jsonVal != null) {
         boolean isJsonObject = jsonVal instanceof JSONObject;
         boolean isJsonArray = jsonVal instanceof JSONArray;
         if (!isJsonArray && !isJsonObject) {
            throw new IllegalArgumentException("Expected JSONObject or JSONArray, got " + jsonVal.getClass().getSimpleName());
         } else {
            JSONObject jsonObject = isJsonObject ? (JSONObject)jsonVal : null;
            JSONArray jsonArray = isJsonArray ? (JSONArray)jsonVal : null;
            Class<?> rawType = objectInstance.getClass();
            boolean isMap = Map.class.isAssignableFrom(rawType);
            Map<Object, Object> mapInstance = isMap ? (Map)objectInstance : null;
            boolean isCollection = Collection.class.isAssignableFrom(rawType);
            final Collection<Object> collectionInstance = isCollection ? (Collection)objectInstance : null;
            boolean isArray = rawType.isArray();
            boolean isObj = !isMap && !isCollection && !isArray;
            if ((isMap || isObj) == isJsonObject && (isCollection || isArray) == isJsonArray) {
               Type objectResolvedTypeGeneric = objectResolvedType;
               if (objectResolvedType instanceof Class) {
                  Class<?> objectResolvedCls = (Class<?>)objectResolvedType;
                  if (Map.class.isAssignableFrom(objectResolvedCls)) {
                     if (!isMap) {
                        throw new IllegalArgumentException("Got an unexpected map type");
                     }

                     objectResolvedTypeGeneric = objectResolvedCls.getGenericSuperclass();
                  } else if (Collection.class.isAssignableFrom(objectResolvedCls)) {
                     if (!isCollection) {
                        throw new IllegalArgumentException("Got an unexpected map type");
                     }

                     objectResolvedTypeGeneric = objectResolvedCls.getGenericSuperclass();
                  }
               }

               Type mapKeyType;
               Type commonResolvedValueType;
               Class<?> arrayComponentType;
               boolean is1DArray;
               TypeResolutions typeResolutions;
               if (objectResolvedTypeGeneric instanceof Class) {
                  typeResolutions = null;
                  mapKeyType = null;
                  Class<?> objectResolvedCls = (Class<?>)objectResolvedTypeGeneric;
                  if (isArray) {
                     arrayComponentType = objectResolvedCls.getComponentType();
                     is1DArray = !arrayComponentType.isArray();
                  } else {
                     arrayComponentType = null;
                     is1DArray = false;
                  }

                  commonResolvedValueType = null;
               } else {
                  if (!(objectResolvedTypeGeneric instanceof ParameterizedType)) {
                     throw new IllegalArgumentException("Got illegal type: " + objectResolvedTypeGeneric);
                  }

                  ParameterizedType parameterizedResolvedType = (ParameterizedType)objectResolvedTypeGeneric;
                  typeResolutions = new TypeResolutions(parameterizedResolvedType);
                  int numTypeArgs = typeResolutions.resolvedTypeArguments.length;
                  if (isMap && numTypeArgs != 2) {
                     throw new IllegalArgumentException("Wrong number of type arguments for Map: got " + numTypeArgs + "; expected 2");
                  }

                  if (isCollection && numTypeArgs != 1) {
                     throw new IllegalArgumentException("Wrong number of type arguments for Collection: got " + numTypeArgs + "; expected 1");
                  }

                  mapKeyType = isMap ? typeResolutions.resolvedTypeArguments[0] : null;
                  commonResolvedValueType = isMap ? typeResolutions.resolvedTypeArguments[1] : (isCollection ? typeResolutions.resolvedTypeArguments[0] : null);
                  is1DArray = false;
                  arrayComponentType = null;
               }

               Class<?> commonValueRawType = commonResolvedValueType == null ? null : JSONUtils.getRawType(commonResolvedValueType);
               Constructor<?> commonValueDefaultConstructor;
               Constructor<?> commonValueConstructorWithSizeHint;
               if (isMap || isCollection || is1DArray && !JSONUtils.isBasicValueType(arrayComponentType)) {
                  commonValueConstructorWithSizeHint = classFieldCache.getConstructorWithSizeHintForConcreteTypeOf(
                     is1DArray ? arrayComponentType : commonValueRawType
                  );
                  if (commonValueConstructorWithSizeHint != null) {
                     commonValueDefaultConstructor = null;
                  } else {
                     commonValueDefaultConstructor = classFieldCache.getDefaultConstructorForConcreteTypeOf(is1DArray ? arrayComponentType : commonValueRawType);
                  }
               } else {
                  commonValueConstructorWithSizeHint = null;
                  commonValueDefaultConstructor = null;
               }

               ClassFields classFields = isObj ? classFieldCache.get(rawType) : null;
               ArrayList<JSONDeserializer.ObjectInstantiation> itemsToRecurseToInPass2 = null;
               int numItems = jsonObject != null ? jsonObject.items.size() : (jsonArray != null ? jsonArray.items.size() : 0);

               for (int i = 0; i < numItems; i++) {
                  Object itemJsonValue;
                  String itemJsonKey;
                  if (jsonObject != null) {
                     Entry<String, Object> jsonObjectItem = jsonObject.items.get(i);
                     itemJsonKey = jsonObjectItem.getKey();
                     itemJsonValue = jsonObjectItem.getValue();
                  } else {
                     if (jsonArray == null) {
                        throw new RuntimeException("This exception should not be thrown");
                     }

                     itemJsonKey = null;
                     itemJsonValue = jsonArray.items.get(i);
                  }

                  boolean itemJsonValueIsJsonObject = itemJsonValue instanceof JSONObject;
                  boolean itemJsonValueIsJsonArray = itemJsonValue instanceof JSONArray;
                  JSONObject itemJsonValueJsonObject = itemJsonValueIsJsonObject ? (JSONObject)itemJsonValue : null;
                  JSONArray itemJsonValueJsonArray = itemJsonValueIsJsonArray ? (JSONArray)itemJsonValue : null;
                  FieldTypeInfo fieldTypeInfo;
                  if (classFields != null) {
                     fieldTypeInfo = classFields.fieldNameToFieldTypeInfo.get(itemJsonKey);
                     if (fieldTypeInfo == null) {
                        throw new IllegalArgumentException(
                           "Field " + rawType.getName() + "." + itemJsonKey + " does not exist or is not accessible, non-final, and non-transient"
                        );
                     }
                  } else {
                     fieldTypeInfo = null;
                  }

                  Type resolvedItemValueType = (Type)(fieldTypeInfo != null
                     ? fieldTypeInfo.getFullyResolvedFieldType(typeResolutions)
                     : (isArray ? arrayComponentType : commonResolvedValueType));
                  final Object instantiatedItemObject;
                  if (itemJsonValue == null) {
                     instantiatedItemObject = null;
                  } else if (resolvedItemValueType == Object.class) {
                     if (itemJsonValueIsJsonObject) {
                        instantiatedItemObject = new HashMap();
                        if (itemsToRecurseToInPass2 == null) {
                           itemsToRecurseToInPass2 = new ArrayList<>();
                        }

                        itemsToRecurseToInPass2.add(
                           new JSONDeserializer.ObjectInstantiation(instantiatedItemObject, ParameterizedTypeImpl.MAP_OF_UNKNOWN_TYPE, itemJsonValue)
                        );
                     } else if (itemJsonValueIsJsonArray) {
                        instantiatedItemObject = new ArrayList();
                        if (itemsToRecurseToInPass2 == null) {
                           itemsToRecurseToInPass2 = new ArrayList<>();
                        }

                        itemsToRecurseToInPass2.add(
                           new JSONDeserializer.ObjectInstantiation(instantiatedItemObject, ParameterizedTypeImpl.LIST_OF_UNKNOWN_TYPE, itemJsonValue)
                        );
                     } else {
                        instantiatedItemObject = jsonBasicValueToObject(itemJsonValue, resolvedItemValueType, false);
                     }
                  } else if (JSONUtils.isBasicValueType(resolvedItemValueType)) {
                     if (itemJsonValueIsJsonObject || itemJsonValueIsJsonArray) {
                        throw new IllegalArgumentException("Got JSONObject or JSONArray type when expecting a simple value type");
                     }

                     instantiatedItemObject = jsonBasicValueToObject(itemJsonValue, resolvedItemValueType, false);
                  } else if (CharSequence.class.isAssignableFrom(itemJsonValue.getClass())) {
                     Object linkedObject = idToObjectInstance.get(itemJsonValue);
                     if (linkedObject == null) {
                        throw new IllegalArgumentException("Object id not found: " + itemJsonValue);
                     }

                     instantiatedItemObject = linkedObject;
                  } else {
                     if (!itemJsonValueIsJsonObject && !itemJsonValueIsJsonArray) {
                        throw new IllegalArgumentException("Got simple value type when expecting a JSON object or JSON array");
                     }

                     try {
                        int numSubItems = itemJsonValueJsonObject != null
                           ? itemJsonValueJsonObject.items.size()
                           : (itemJsonValueJsonArray != null ? itemJsonValueJsonArray.items.size() : 0);
                        if (resolvedItemValueType instanceof Class && ((Class)resolvedItemValueType).isArray()) {
                           if (!itemJsonValueIsJsonArray) {
                              throw new IllegalArgumentException("Expected JSONArray, got " + itemJsonValue.getClass().getName());
                           }

                           instantiatedItemObject = Array.newInstance(((Class)resolvedItemValueType).getComponentType(), numSubItems);
                        } else if (!isCollection && !isMap && !is1DArray) {
                           if (fieldTypeInfo != null) {
                              Constructor<?> valueConstructorWithSizeHint = fieldTypeInfo.getConstructorForFieldTypeWithSizeHint(
                                 resolvedItemValueType, classFieldCache
                              );
                              if (valueConstructorWithSizeHint != null) {
                                 instantiatedItemObject = valueConstructorWithSizeHint.newInstance(numSubItems);
                              } else {
                                 instantiatedItemObject = fieldTypeInfo.getDefaultConstructorForFieldType(resolvedItemValueType, classFieldCache).newInstance();
                              }
                           } else {
                              if (!isArray || is1DArray) {
                                 throw new IllegalArgumentException("Got illegal type");
                              }

                              instantiatedItemObject = Array.newInstance(rawType.getComponentType(), numSubItems);
                           }
                        } else {
                           instantiatedItemObject = commonValueConstructorWithSizeHint != null
                              ? commonValueConstructorWithSizeHint.newInstance(numSubItems)
                              : (commonValueDefaultConstructor != null ? commonValueDefaultConstructor.newInstance() : null);
                        }
                     } catch (SecurityException | ReflectiveOperationException var41) {
                        throw new IllegalArgumentException("Could not instantiate type " + resolvedItemValueType, var41);
                     }

                     if (itemJsonValue instanceof JSONObject) {
                        JSONObject itemJsonObject = (JSONObject)itemJsonValue;
                        if (itemJsonObject.objectId != null) {
                           idToObjectInstance.put(itemJsonObject.objectId, instantiatedItemObject);
                        }
                     }

                     if (itemsToRecurseToInPass2 == null) {
                        itemsToRecurseToInPass2 = new ArrayList<>();
                     }

                     itemsToRecurseToInPass2.add(new JSONDeserializer.ObjectInstantiation(instantiatedItemObject, resolvedItemValueType, itemJsonValue));
                  }

                  if (fieldTypeInfo != null) {
                     fieldTypeInfo.setFieldValue(objectInstance, instantiatedItemObject);
                  } else if (mapInstance != null) {
                     Object mapKey = jsonBasicValueToObject(itemJsonKey, mapKeyType, true);
                     mapInstance.put(mapKey, instantiatedItemObject);
                  } else if (isArray) {
                     Array.set(objectInstance, i, instantiatedItemObject);
                  } else if (collectionInstance != null) {
                     collectionElementAdders.add(new Runnable() {
                        @Override
                        public void run() {
                           collectionInstance.add(instantiatedItemObject);
                        }
                     });
                  }
               }

               if (itemsToRecurseToInPass2 != null) {
                  for (JSONDeserializer.ObjectInstantiation i : itemsToRecurseToInPass2) {
                     populateObjectFromJsonObject(i.objectInstance, i.type, i.jsonVal, classFieldCache, idToObjectInstance, collectionElementAdders);
                  }
               }
            } else {
               throw new IllegalArgumentException("Wrong JSON type for class " + objectInstance.getClass().getName());
            }
         }
      }
   }

   private static Map<CharSequence, Object> getInitialIdToObjectMap(Object objectInstance, Object parsedJSON) {
      Map<CharSequence, Object> idToObjectInstance = new HashMap<>();
      if (parsedJSON instanceof JSONObject) {
         JSONObject itemJsonObject = (JSONObject)parsedJSON;
         if (!itemJsonObject.items.isEmpty()) {
            Entry<String, Object> firstItem = itemJsonObject.items.get(0);
            if (firstItem.getKey().equals("__ID")) {
               Object firstItemValue = firstItem.getValue();
               if (firstItemValue == null || !CharSequence.class.isAssignableFrom(firstItemValue.getClass())) {
                  idToObjectInstance.put((CharSequence)firstItemValue, objectInstance);
               }
            }
         }
      }

      return idToObjectInstance;
   }

   private static <T> T deserializeObject(Class<T> expectedType, String json, ClassFieldCache classFieldCache) throws IllegalArgumentException {
      Object parsedJSON;
      try {
         parsedJSON = JSONParser.parseJSON(json);
      } catch (ParseException var9) {
         throw new IllegalArgumentException("Could not parse JSON", var9);
      }

      T objectInstance;
      try {
         Constructor<?> constructor = classFieldCache.getDefaultConstructorForConcreteTypeOf(expectedType);
         T newInstance = (T)constructor.newInstance();
         objectInstance = newInstance;
      } catch (SecurityException | ReflectiveOperationException var8) {
         throw new IllegalArgumentException("Could not construct object of type " + expectedType.getName(), var8);
      }

      List<Runnable> collectionElementAdders = new ArrayList<>();
      populateObjectFromJsonObject(
         objectInstance, expectedType, parsedJSON, classFieldCache, getInitialIdToObjectMap(objectInstance, parsedJSON), collectionElementAdders
      );

      for (Runnable runnable : collectionElementAdders) {
         runnable.run();
      }

      return objectInstance;
   }

   public static <T> T deserializeObject(Class<T> expectedType, String json, ReflectionUtils reflectionUtils) throws IllegalArgumentException {
      ClassFieldCache classFieldCache = new ClassFieldCache(true, false, reflectionUtils);
      return deserializeObject(expectedType, json, classFieldCache);
   }

   public static <T> T deserializeObject(Class<T> expectedType, String json) throws IllegalArgumentException {
      return deserializeObject(expectedType, json, new ReflectionUtils());
   }

   public static void deserializeToField(Object containingObject, String fieldName, String json, ClassFieldCache classFieldCache) throws IllegalArgumentException {
      if (containingObject == null) {
         throw new IllegalArgumentException("Cannot deserialize to a field of a null object");
      } else {
         Object parsedJSON;
         try {
            parsedJSON = JSONParser.parseJSON(json);
         } catch (ParseException var9) {
            throw new IllegalArgumentException("Could not parse JSON", var9);
         }

         JSONObject wrapperJsonObj = new JSONObject(1);
         wrapperJsonObj.items.add(new SimpleEntry<>(fieldName, parsedJSON));
         List<Runnable> collectionElementAdders = new ArrayList<>();
         populateObjectFromJsonObject(containingObject, containingObject.getClass(), wrapperJsonObj, classFieldCache, new HashMap<>(), collectionElementAdders);

         for (Runnable runnable : collectionElementAdders) {
            runnable.run();
         }
      }
   }

   public static void deserializeToField(Object containingObject, String fieldName, String json, ReflectionUtils reflectionUtils) throws IllegalArgumentException {
      ClassFieldCache typeCache = new ClassFieldCache(true, false, reflectionUtils);
      deserializeToField(containingObject, fieldName, json, typeCache);
   }

   public static void deserializeToField(Object containingObject, String fieldName, String json) throws IllegalArgumentException {
      deserializeToField(containingObject, fieldName, json, new ReflectionUtils());
   }

   private static class ObjectInstantiation {
      Object jsonVal;
      Object objectInstance;
      Type type;

      public ObjectInstantiation(Object objectInstance, Type type, Object jsonVal) {
         this.jsonVal = jsonVal;
         this.objectInstance = objectInstance;
         this.type = type;
      }
   }
}
