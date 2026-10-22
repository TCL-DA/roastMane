package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import kotlin.coroutines.Continuation;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.QueryName;
import retrofit2.http.Tag;
import retrofit2.http.Url;

final class RequestFactory {
   private final Class<?> service;
   private final Method method;
   private final HttpUrl baseUrl;
   final String httpMethod;
   @Nullable
   private final String relativeUrl;
   @Nullable
   private final Headers headers;
   @Nullable
   private final MediaType contentType;
   private final boolean hasBody;
   private final boolean isFormEncoded;
   private final boolean isMultipart;
   private final ParameterHandler<?>[] parameterHandlers;
   final boolean isKotlinSuspendFunction;

   static RequestFactory parseAnnotations(Retrofit retrofit, Class<?> service, Method method) {
      return new RequestFactory.Builder(retrofit, service, method).build();
   }

   RequestFactory(RequestFactory.Builder builder) {
      this.service = builder.service;
      this.method = builder.method;
      this.baseUrl = builder.retrofit.baseUrl;
      this.httpMethod = builder.httpMethod;
      this.relativeUrl = builder.relativeUrl;
      this.headers = builder.headers;
      this.contentType = builder.contentType;
      this.hasBody = builder.hasBody;
      this.isFormEncoded = builder.isFormEncoded;
      this.isMultipart = builder.isMultipart;
      this.parameterHandlers = builder.parameterHandlers;
      this.isKotlinSuspendFunction = builder.isKotlinSuspendFunction;
   }

   Request create(@Nullable Object instance, Object[] args) throws IOException {
      ParameterHandler<Object>[] handlers = (ParameterHandler<Object>[])this.parameterHandlers;
      int argumentCount = args.length;
      if (argumentCount != handlers.length) {
         throw new IllegalArgumentException("Argument count (" + argumentCount + ") doesn't match expected count (" + handlers.length + ")");
      } else {
         RequestBuilder requestBuilder = new RequestBuilder(
            this.httpMethod, this.baseUrl, this.relativeUrl, this.headers, this.contentType, this.hasBody, this.isFormEncoded, this.isMultipart
         );
         if (this.isKotlinSuspendFunction) {
            argumentCount--;
         }

         List<Object> argumentList = new ArrayList<>(argumentCount);

         for (int p = 0; p < argumentCount; p++) {
            argumentList.add(args[p]);
            handlers[p].apply(requestBuilder, args[p]);
         }

         return requestBuilder.get().tag(Invocation.class, new Invocation(this.service, instance, this.method, argumentList)).build();
      }
   }

   static final class Builder {
      private static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
      private static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{([a-zA-Z][a-zA-Z0-9_-]*)\\}");
      private static final Pattern PARAM_NAME_REGEX = Pattern.compile("[a-zA-Z][a-zA-Z0-9_-]*");
      final Retrofit retrofit;
      final Class<?> service;
      final Method method;
      final Annotation[] methodAnnotations;
      final Annotation[][] parameterAnnotationsArray;
      final Type[] parameterTypes;
      boolean gotField;
      boolean gotPart;
      boolean gotBody;
      boolean gotPath;
      boolean gotQuery;
      boolean gotQueryName;
      boolean gotQueryMap;
      boolean gotUrl;
      @Nullable
      String httpMethod;
      boolean hasBody;
      boolean isFormEncoded;
      boolean isMultipart;
      @Nullable
      String relativeUrl;
      @Nullable
      Headers headers;
      @Nullable
      MediaType contentType;
      @Nullable
      Set<String> relativeUrlParamNames;
      @Nullable
      ParameterHandler<?>[] parameterHandlers;
      boolean isKotlinSuspendFunction;

      Builder(Retrofit retrofit, Class<?> service, Method method) {
         this.retrofit = retrofit;
         this.service = service;
         this.method = method;
         this.methodAnnotations = method.getAnnotations();
         this.parameterTypes = method.getGenericParameterTypes();
         this.parameterAnnotationsArray = method.getParameterAnnotations();
      }

      RequestFactory build() {
         for (Annotation annotation : this.methodAnnotations) {
            this.parseMethodAnnotation(annotation);
         }

         if (this.httpMethod == null) {
            throw Utils.methodError(this.method, "HTTP method annotation is required (e.g., @GET, @POST, etc.).");
         } else {
            if (!this.hasBody) {
               if (this.isMultipart) {
                  throw Utils.methodError(this.method, "Multipart can only be specified on HTTP methods with request body (e.g., @POST).");
               }

               if (this.isFormEncoded) {
                  throw Utils.methodError(this.method, "FormUrlEncoded can only be specified on HTTP methods with request body (e.g., @POST).");
               }
            }

            int parameterCount = this.parameterAnnotationsArray.length;
            this.parameterHandlers = new ParameterHandler[parameterCount];
            int p = 0;

            for (int lastParameter = parameterCount - 1; p < parameterCount; p++) {
               this.parameterHandlers[p] = this.parseParameter(p, this.parameterTypes[p], this.parameterAnnotationsArray[p], p == lastParameter);
            }

            if (this.relativeUrl == null && !this.gotUrl) {
               throw Utils.methodError(this.method, "Missing either @%s URL or @Url parameter.", this.httpMethod);
            } else if (!this.isFormEncoded && !this.isMultipart && !this.hasBody && this.gotBody) {
               throw Utils.methodError(this.method, "Non-body HTTP method cannot contain @Body.");
            } else if (this.isFormEncoded && !this.gotField) {
               throw Utils.methodError(this.method, "Form-encoded method must contain at least one @Field.");
            } else if (this.isMultipart && !this.gotPart) {
               throw Utils.methodError(this.method, "Multipart method must contain at least one @Part.");
            } else {
               return new RequestFactory(this);
            }
         }
      }

      private void parseMethodAnnotation(Annotation annotation) {
         if (annotation instanceof DELETE) {
            this.parseHttpMethodAndPath("DELETE", ((DELETE)annotation).value(), false);
         } else if (annotation instanceof GET) {
            this.parseHttpMethodAndPath("GET", ((GET)annotation).value(), false);
         } else if (annotation instanceof HEAD) {
            this.parseHttpMethodAndPath("HEAD", ((HEAD)annotation).value(), false);
         } else if (annotation instanceof PATCH) {
            this.parseHttpMethodAndPath("PATCH", ((PATCH)annotation).value(), true);
         } else if (annotation instanceof POST) {
            this.parseHttpMethodAndPath("POST", ((POST)annotation).value(), true);
         } else if (annotation instanceof PUT) {
            this.parseHttpMethodAndPath("PUT", ((PUT)annotation).value(), true);
         } else if (annotation instanceof OPTIONS) {
            this.parseHttpMethodAndPath("OPTIONS", ((OPTIONS)annotation).value(), false);
         } else if (annotation instanceof HTTP) {
            HTTP http = (HTTP)annotation;
            this.parseHttpMethodAndPath(http.method(), http.path(), http.hasBody());
         } else if (annotation instanceof retrofit2.http.Headers) {
            retrofit2.http.Headers headers = (retrofit2.http.Headers)annotation;
            String[] headersToParse = headers.value();
            if (headersToParse.length == 0) {
               throw Utils.methodError(this.method, "@Headers annotation is empty.");
            }

            this.headers = this.parseHeaders(headersToParse, headers.allowUnsafeNonAsciiValues());
         } else if (annotation instanceof Multipart) {
            if (this.isFormEncoded) {
               throw Utils.methodError(this.method, "Only one encoding annotation is allowed.");
            }

            this.isMultipart = true;
         } else if (annotation instanceof FormUrlEncoded) {
            if (this.isMultipart) {
               throw Utils.methodError(this.method, "Only one encoding annotation is allowed.");
            }

            this.isFormEncoded = true;
         }
      }

      private void parseHttpMethodAndPath(String httpMethod, String value, boolean hasBody) {
         if (this.httpMethod != null) {
            throw Utils.methodError(this.method, "Only one HTTP method is allowed. Found: %s and %s.", this.httpMethod, httpMethod);
         } else {
            this.httpMethod = httpMethod;
            this.hasBody = hasBody;
            if (!value.isEmpty()) {
               int question = value.indexOf(63);
               if (question != -1 && question < value.length() - 1) {
                  String queryParams = value.substring(question + 1);
                  Matcher queryParamMatcher = PARAM_URL_REGEX.matcher(queryParams);
                  if (queryParamMatcher.find()) {
                     throw Utils.methodError(
                        this.method, "URL query string \"%s\" must not have replace block. For dynamic query parameters use @Query.", queryParams
                     );
                  }
               }

               this.relativeUrl = value;
               this.relativeUrlParamNames = parsePathParameters(value);
            }
         }
      }

      private Headers parseHeaders(String[] headers, boolean allowUnsafeNonAsciiValues) {
         okhttp3.Headers.Builder builder = new okhttp3.Headers.Builder();

         for (String header : headers) {
            int colon = header.indexOf(58);
            if (colon == -1 || colon == 0 || colon == header.length() - 1) {
               throw Utils.methodError(this.method, "@Headers value must be in the form \"Name: Value\". Found: \"%s\"", header);
            }

            String headerName = header.substring(0, colon);
            String headerValue = header.substring(colon + 1).trim();
            if ("Content-Type".equalsIgnoreCase(headerName)) {
               try {
                  this.contentType = MediaType.get(headerValue);
               } catch (IllegalArgumentException var12) {
                  throw Utils.methodError(this.method, var12, "Malformed content type: %s", headerValue);
               }
            } else if (allowUnsafeNonAsciiValues) {
               builder.addUnsafeNonAscii(headerName, headerValue);
            } else {
               builder.add(headerName, headerValue);
            }
         }

         return builder.build();
      }

      @Nullable
      private ParameterHandler<?> parseParameter(int p, Type parameterType, @Nullable Annotation[] annotations, boolean allowContinuation) {
         ParameterHandler<?> result = null;
         if (annotations != null) {
            for (Annotation annotation : annotations) {
               ParameterHandler<?> annotationAction = this.parseParameterAnnotation(p, parameterType, annotations, annotation);
               if (annotationAction != null) {
                  if (result != null) {
                     throw Utils.parameterError(this.method, p, "Multiple Retrofit annotations found, only one allowed.");
                  }

                  result = annotationAction;
               }
            }
         }

         if (result == null) {
            if (allowContinuation) {
               try {
                  if (Utils.getRawType(parameterType) == Continuation.class) {
                     this.isKotlinSuspendFunction = true;
                     return null;
                  }
               } catch (NoClassDefFoundError var11) {
               }
            }

            throw Utils.parameterError(this.method, p, "No Retrofit annotation found.");
         } else {
            return result;
         }
      }

      @Nullable
      private ParameterHandler<?> parseParameterAnnotation(int p, Type type, Annotation[] annotations, Annotation annotation) {
         if (!(annotation instanceof Url)) {
            if (annotation instanceof Path) {
               this.validateResolvableType(p, type);
               if (this.gotQuery) {
                  throw Utils.parameterError(this.method, p, "A @Path parameter must not come after a @Query.");
               } else if (this.gotQueryName) {
                  throw Utils.parameterError(this.method, p, "A @Path parameter must not come after a @QueryName.");
               } else if (this.gotQueryMap) {
                  throw Utils.parameterError(this.method, p, "A @Path parameter must not come after a @QueryMap.");
               } else if (this.gotUrl) {
                  throw Utils.parameterError(this.method, p, "@Path parameters may not be used with @Url.");
               } else if (this.relativeUrl == null) {
                  throw Utils.parameterError(this.method, p, "@Path can only be used with relative url on @%s", this.httpMethod);
               } else {
                  this.gotPath = true;
                  Path path = (Path)annotation;
                  String name = path.value();
                  this.validatePathName(p, name);
                  Converter<?, String> converter = this.retrofit.stringConverter(type, annotations);
                  return new ParameterHandler.Path<>(this.method, p, name, converter, path.encoded());
               }
            } else if (annotation instanceof Query) {
               this.validateResolvableType(p, type);
               Query query = (Query)annotation;
               String name = query.value();
               boolean encoded = query.encoded();
               Class<?> rawParameterType = Utils.getRawType(type);
               this.gotQuery = true;
               if (Iterable.class.isAssignableFrom(rawParameterType)) {
                  if (!(type instanceof ParameterizedType)) {
                     throw Utils.parameterError(
                        this.method,
                        p,
                        rawParameterType.getSimpleName() + " must include generic type (e.g., " + rawParameterType.getSimpleName() + "<String>)"
                     );
                  } else {
                     ParameterizedType parameterizedType = (ParameterizedType)type;
                     Type iterableType = Utils.getParameterUpperBound(0, parameterizedType);
                     Converter<?, String> converter = this.retrofit.stringConverter(iterableType, annotations);
                     return new ParameterHandler.Query<>(name, converter, encoded).iterable();
                  }
               } else if (rawParameterType.isArray()) {
                  Class<?> arrayComponentType = boxIfPrimitive(rawParameterType.getComponentType());
                  Converter<?, String> converter = this.retrofit.stringConverter(arrayComponentType, annotations);
                  return new ParameterHandler.Query<>(name, converter, encoded).array();
               } else {
                  Converter<?, String> converter = this.retrofit.stringConverter(type, annotations);
                  return new ParameterHandler.Query<>(name, converter, encoded);
               }
            } else if (annotation instanceof QueryName) {
               this.validateResolvableType(p, type);
               QueryName query = (QueryName)annotation;
               boolean encoded = query.encoded();
               Class<?> rawParameterType = Utils.getRawType(type);
               this.gotQueryName = true;
               if (Iterable.class.isAssignableFrom(rawParameterType)) {
                  if (!(type instanceof ParameterizedType)) {
                     throw Utils.parameterError(
                        this.method,
                        p,
                        rawParameterType.getSimpleName() + " must include generic type (e.g., " + rawParameterType.getSimpleName() + "<String>)"
                     );
                  } else {
                     ParameterizedType parameterizedType = (ParameterizedType)type;
                     Type iterableType = Utils.getParameterUpperBound(0, parameterizedType);
                     Converter<?, String> converter = this.retrofit.stringConverter(iterableType, annotations);
                     return new ParameterHandler.QueryName<>(converter, encoded).iterable();
                  }
               } else if (rawParameterType.isArray()) {
                  Class<?> arrayComponentType = boxIfPrimitive(rawParameterType.getComponentType());
                  Converter<?, String> converter = this.retrofit.stringConverter(arrayComponentType, annotations);
                  return new ParameterHandler.QueryName<>(converter, encoded).array();
               } else {
                  Converter<?, String> converter = this.retrofit.stringConverter(type, annotations);
                  return new ParameterHandler.QueryName<>(converter, encoded);
               }
            } else if (annotation instanceof QueryMap) {
               this.validateResolvableType(p, type);
               Class<?> rawParameterType = Utils.getRawType(type);
               this.gotQueryMap = true;
               if (!Map.class.isAssignableFrom(rawParameterType)) {
                  throw Utils.parameterError(this.method, p, "@QueryMap parameter type must be Map.");
               } else {
                  Type mapType = Utils.getSupertype(type, rawParameterType, Map.class);
                  if (!(mapType instanceof ParameterizedType)) {
                     throw Utils.parameterError(this.method, p, "Map must include generic types (e.g., Map<String, String>)");
                  } else {
                     ParameterizedType parameterizedType = (ParameterizedType)mapType;
                     Type keyType = Utils.getParameterUpperBound(0, parameterizedType);
                     if (String.class != keyType) {
                        throw Utils.parameterError(this.method, p, "@QueryMap keys must be of type String: " + keyType);
                     } else {
                        Type valueType = Utils.getParameterUpperBound(1, parameterizedType);
                        Converter<?, String> valueConverter = this.retrofit.stringConverter(valueType, annotations);
                        return new ParameterHandler.QueryMap<>(this.method, p, valueConverter, ((QueryMap)annotation).encoded());
                     }
                  }
               }
            } else if (annotation instanceof Header) {
               this.validateResolvableType(p, type);
               Header header = (Header)annotation;
               String name = header.value();
               Class<?> rawParameterType = Utils.getRawType(type);
               if (Iterable.class.isAssignableFrom(rawParameterType)) {
                  if (!(type instanceof ParameterizedType)) {
                     throw Utils.parameterError(
                        this.method,
                        p,
                        rawParameterType.getSimpleName() + " must include generic type (e.g., " + rawParameterType.getSimpleName() + "<String>)"
                     );
                  } else {
                     ParameterizedType parameterizedType = (ParameterizedType)type;
                     Type iterableType = Utils.getParameterUpperBound(0, parameterizedType);
                     Converter<?, String> converter = this.retrofit.stringConverter(iterableType, annotations);
                     return new ParameterHandler.Header<>(name, converter, header.allowUnsafeNonAsciiValues()).iterable();
                  }
               } else if (rawParameterType.isArray()) {
                  Class<?> arrayComponentType = boxIfPrimitive(rawParameterType.getComponentType());
                  Converter<?, String> converter = this.retrofit.stringConverter(arrayComponentType, annotations);
                  return new ParameterHandler.Header<>(name, converter, header.allowUnsafeNonAsciiValues()).array();
               } else {
                  Converter<?, String> converter = this.retrofit.stringConverter(type, annotations);
                  return new ParameterHandler.Header<>(name, converter, header.allowUnsafeNonAsciiValues());
               }
            } else if (annotation instanceof HeaderMap) {
               if (type == Headers.class) {
                  return new ParameterHandler.Headers(this.method, p);
               } else {
                  this.validateResolvableType(p, type);
                  Class<?> rawParameterType = Utils.getRawType(type);
                  if (!Map.class.isAssignableFrom(rawParameterType)) {
                     throw Utils.parameterError(this.method, p, "@HeaderMap parameter type must be Map or Headers.");
                  } else {
                     Type mapType = Utils.getSupertype(type, rawParameterType, Map.class);
                     if (!(mapType instanceof ParameterizedType)) {
                        throw Utils.parameterError(this.method, p, "Map must include generic types (e.g., Map<String, String>)");
                     } else {
                        ParameterizedType parameterizedType = (ParameterizedType)mapType;
                        Type keyType = Utils.getParameterUpperBound(0, parameterizedType);
                        if (String.class != keyType) {
                           throw Utils.parameterError(this.method, p, "@HeaderMap keys must be of type String: " + keyType);
                        } else {
                           Type valueType = Utils.getParameterUpperBound(1, parameterizedType);
                           Converter<?, String> valueConverter = this.retrofit.stringConverter(valueType, annotations);
                           return new ParameterHandler.HeaderMap<>(this.method, p, valueConverter, ((HeaderMap)annotation).allowUnsafeNonAsciiValues());
                        }
                     }
                  }
               }
            } else if (annotation instanceof Field) {
               this.validateResolvableType(p, type);
               if (!this.isFormEncoded) {
                  throw Utils.parameterError(this.method, p, "@Field parameters can only be used with form encoding.");
               } else {
                  Field field = (Field)annotation;
                  String name = field.value();
                  boolean encoded = field.encoded();
                  this.gotField = true;
                  Class<?> rawParameterType = Utils.getRawType(type);
                  if (Iterable.class.isAssignableFrom(rawParameterType)) {
                     if (!(type instanceof ParameterizedType)) {
                        throw Utils.parameterError(
                           this.method,
                           p,
                           rawParameterType.getSimpleName() + " must include generic type (e.g., " + rawParameterType.getSimpleName() + "<String>)"
                        );
                     } else {
                        ParameterizedType parameterizedType = (ParameterizedType)type;
                        Type iterableType = Utils.getParameterUpperBound(0, parameterizedType);
                        Converter<?, String> converter = this.retrofit.stringConverter(iterableType, annotations);
                        return new ParameterHandler.Field<>(name, converter, encoded).iterable();
                     }
                  } else if (rawParameterType.isArray()) {
                     Class<?> arrayComponentType = boxIfPrimitive(rawParameterType.getComponentType());
                     Converter<?, String> converter = this.retrofit.stringConverter(arrayComponentType, annotations);
                     return new ParameterHandler.Field<>(name, converter, encoded).array();
                  } else {
                     Converter<?, String> converter = this.retrofit.stringConverter(type, annotations);
                     return new ParameterHandler.Field<>(name, converter, encoded);
                  }
               }
            } else if (annotation instanceof FieldMap) {
               this.validateResolvableType(p, type);
               if (!this.isFormEncoded) {
                  throw Utils.parameterError(this.method, p, "@FieldMap parameters can only be used with form encoding.");
               } else {
                  Class<?> rawParameterType = Utils.getRawType(type);
                  if (!Map.class.isAssignableFrom(rawParameterType)) {
                     throw Utils.parameterError(this.method, p, "@FieldMap parameter type must be Map.");
                  } else {
                     Type mapType = Utils.getSupertype(type, rawParameterType, Map.class);
                     if (!(mapType instanceof ParameterizedType)) {
                        throw Utils.parameterError(this.method, p, "Map must include generic types (e.g., Map<String, String>)");
                     } else {
                        ParameterizedType parameterizedType = (ParameterizedType)mapType;
                        Type keyType = Utils.getParameterUpperBound(0, parameterizedType);
                        if (String.class != keyType) {
                           throw Utils.parameterError(this.method, p, "@FieldMap keys must be of type String: " + keyType);
                        } else {
                           Type valueType = Utils.getParameterUpperBound(1, parameterizedType);
                           Converter<?, String> valueConverter = this.retrofit.stringConverter(valueType, annotations);
                           this.gotField = true;
                           return new ParameterHandler.FieldMap<>(this.method, p, valueConverter, ((FieldMap)annotation).encoded());
                        }
                     }
                  }
               }
            } else if (annotation instanceof Part) {
               this.validateResolvableType(p, type);
               if (!this.isMultipart) {
                  throw Utils.parameterError(this.method, p, "@Part parameters can only be used with multipart encoding.");
               } else {
                  Part part = (Part)annotation;
                  this.gotPart = true;
                  String partName = part.value();
                  Class<?> rawParameterType = Utils.getRawType(type);
                  if (partName.isEmpty()) {
                     if (Iterable.class.isAssignableFrom(rawParameterType)) {
                        if (!(type instanceof ParameterizedType)) {
                           throw Utils.parameterError(
                              this.method,
                              p,
                              rawParameterType.getSimpleName() + " must include generic type (e.g., " + rawParameterType.getSimpleName() + "<String>)"
                           );
                        } else {
                           ParameterizedType parameterizedType = (ParameterizedType)type;
                           Type iterableType = Utils.getParameterUpperBound(0, parameterizedType);
                           if (!okhttp3.MultipartBody.Part.class.isAssignableFrom(Utils.getRawType(iterableType))) {
                              throw Utils.parameterError(this.method, p, "@Part annotation must supply a name or use MultipartBody.Part parameter type.");
                           } else {
                              return ParameterHandler.RawPart.INSTANCE.iterable();
                           }
                        }
                     } else if (rawParameterType.isArray()) {
                        Class<?> arrayComponentType = rawParameterType.getComponentType();
                        if (!okhttp3.MultipartBody.Part.class.isAssignableFrom(arrayComponentType)) {
                           throw Utils.parameterError(this.method, p, "@Part annotation must supply a name or use MultipartBody.Part parameter type.");
                        } else {
                           return ParameterHandler.RawPart.INSTANCE.array();
                        }
                     } else if (okhttp3.MultipartBody.Part.class.isAssignableFrom(rawParameterType)) {
                        return ParameterHandler.RawPart.INSTANCE;
                     } else {
                        throw Utils.parameterError(this.method, p, "@Part annotation must supply a name or use MultipartBody.Part parameter type.");
                     }
                  } else {
                     Headers headers = Headers.of(
                        new String[]{"Content-Disposition", "form-data; name=\"" + partName + "\"", "Content-Transfer-Encoding", part.encoding()}
                     );
                     if (Iterable.class.isAssignableFrom(rawParameterType)) {
                        if (!(type instanceof ParameterizedType)) {
                           throw Utils.parameterError(
                              this.method,
                              p,
                              rawParameterType.getSimpleName() + " must include generic type (e.g., " + rawParameterType.getSimpleName() + "<String>)"
                           );
                        } else {
                           ParameterizedType parameterizedType = (ParameterizedType)type;
                           Type iterableType = Utils.getParameterUpperBound(0, parameterizedType);
                           if (okhttp3.MultipartBody.Part.class.isAssignableFrom(Utils.getRawType(iterableType))) {
                              throw Utils.parameterError(
                                 this.method, p, "@Part parameters using the MultipartBody.Part must not include a part name in the annotation."
                              );
                           } else {
                              Converter<?, RequestBody> converter = this.retrofit.requestBodyConverter(iterableType, annotations, this.methodAnnotations);
                              return new ParameterHandler.Part<>(this.method, p, headers, converter).iterable();
                           }
                        }
                     } else if (rawParameterType.isArray()) {
                        Class<?> arrayComponentType = boxIfPrimitive(rawParameterType.getComponentType());
                        if (okhttp3.MultipartBody.Part.class.isAssignableFrom(arrayComponentType)) {
                           throw Utils.parameterError(
                              this.method, p, "@Part parameters using the MultipartBody.Part must not include a part name in the annotation."
                           );
                        } else {
                           Converter<?, RequestBody> converter = this.retrofit.requestBodyConverter(arrayComponentType, annotations, this.methodAnnotations);
                           return new ParameterHandler.Part<>(this.method, p, headers, converter).array();
                        }
                     } else if (okhttp3.MultipartBody.Part.class.isAssignableFrom(rawParameterType)) {
                        throw Utils.parameterError(
                           this.method, p, "@Part parameters using the MultipartBody.Part must not include a part name in the annotation."
                        );
                     } else {
                        Converter<?, RequestBody> converter = this.retrofit.requestBodyConverter(type, annotations, this.methodAnnotations);
                        return new ParameterHandler.Part<>(this.method, p, headers, converter);
                     }
                  }
               }
            } else if (annotation instanceof PartMap) {
               this.validateResolvableType(p, type);
               if (!this.isMultipart) {
                  throw Utils.parameterError(this.method, p, "@PartMap parameters can only be used with multipart encoding.");
               } else {
                  this.gotPart = true;
                  Class<?> rawParameterType = Utils.getRawType(type);
                  if (!Map.class.isAssignableFrom(rawParameterType)) {
                     throw Utils.parameterError(this.method, p, "@PartMap parameter type must be Map.");
                  } else {
                     Type mapType = Utils.getSupertype(type, rawParameterType, Map.class);
                     if (!(mapType instanceof ParameterizedType)) {
                        throw Utils.parameterError(this.method, p, "Map must include generic types (e.g., Map<String, String>)");
                     } else {
                        ParameterizedType parameterizedType = (ParameterizedType)mapType;
                        Type keyType = Utils.getParameterUpperBound(0, parameterizedType);
                        if (String.class != keyType) {
                           throw Utils.parameterError(this.method, p, "@PartMap keys must be of type String: " + keyType);
                        } else {
                           Type valueType = Utils.getParameterUpperBound(1, parameterizedType);
                           if (okhttp3.MultipartBody.Part.class.isAssignableFrom(Utils.getRawType(valueType))) {
                              throw Utils.parameterError(
                                 this.method, p, "@PartMap values cannot be MultipartBody.Part. Use @Part List<Part> or a different value type instead."
                              );
                           } else {
                              Converter<?, RequestBody> valueConverter = this.retrofit.requestBodyConverter(valueType, annotations, this.methodAnnotations);
                              PartMap partMap = (PartMap)annotation;
                              return new ParameterHandler.PartMap<>(this.method, p, valueConverter, partMap.encoding());
                           }
                        }
                     }
                  }
               }
            } else if (annotation instanceof Body) {
               this.validateResolvableType(p, type);
               if (this.isFormEncoded || this.isMultipart) {
                  throw Utils.parameterError(this.method, p, "@Body parameters cannot be used with form or multi-part encoding.");
               } else if (this.gotBody) {
                  throw Utils.parameterError(this.method, p, "Multiple @Body method annotations found.");
               } else {
                  Converter<?, RequestBody> converter;
                  try {
                     converter = this.retrofit.requestBodyConverter(type, annotations, this.methodAnnotations);
                  } catch (RuntimeException var12) {
                     throw Utils.parameterError(this.method, var12, p, "Unable to create @Body converter for %s", type);
                  }

                  this.gotBody = true;
                  return new ParameterHandler.Body<>(this.method, p, converter);
               }
            } else if (annotation instanceof Tag) {
               this.validateResolvableType(p, type);
               Class<?> tagType = Utils.getRawType(type);

               for (int i = p - 1; i >= 0; i--) {
                  ParameterHandler<?> otherHandler = this.parameterHandlers[i];
                  if (otherHandler instanceof ParameterHandler.Tag && ((ParameterHandler.Tag)otherHandler).cls.equals(tagType)) {
                     throw Utils.parameterError(
                        this.method,
                        p,
                        "@Tag type "
                           + tagType.getName()
                           + " is duplicate of "
                           + Platform.reflection.describeMethodParameter(this.method, i)
                           + " and would always overwrite its value."
                     );
                  }
               }

               return new ParameterHandler.Tag<>(tagType);
            } else {
               return null;
            }
         } else {
            this.validateResolvableType(p, type);
            if (this.gotUrl) {
               throw Utils.parameterError(this.method, p, "Multiple @Url method annotations found.");
            } else if (this.gotPath) {
               throw Utils.parameterError(this.method, p, "@Path parameters may not be used with @Url.");
            } else if (this.gotQuery) {
               throw Utils.parameterError(this.method, p, "A @Url parameter must not come after a @Query.");
            } else if (this.gotQueryName) {
               throw Utils.parameterError(this.method, p, "A @Url parameter must not come after a @QueryName.");
            } else if (this.gotQueryMap) {
               throw Utils.parameterError(this.method, p, "A @Url parameter must not come after a @QueryMap.");
            } else if (this.relativeUrl != null) {
               throw Utils.parameterError(this.method, p, "@Url cannot be used with @%s URL", this.httpMethod);
            } else {
               this.gotUrl = true;
               if (type != HttpUrl.class
                  && type != String.class
                  && type != URI.class
                  && (!(type instanceof Class) || !"android.net.Uri".equals(((Class)type).getName()))) {
                  throw Utils.parameterError(this.method, p, "@Url must be okhttp3.HttpUrl, String, java.net.URI, or android.net.Uri type.");
               } else {
                  return new ParameterHandler.RelativeUrl(this.method, p);
               }
            }
         }
      }

      private void validateResolvableType(int p, Type type) {
         if (Utils.hasUnresolvableType(type)) {
            throw Utils.parameterError(this.method, p, "Parameter type must not include a type variable or wildcard: %s", type);
         }
      }

      private void validatePathName(int p, String name) {
         if (!PARAM_NAME_REGEX.matcher(name).matches()) {
            throw Utils.parameterError(this.method, p, "@Path parameter name must match %s. Found: %s", PARAM_URL_REGEX.pattern(), name);
         } else if (!this.relativeUrlParamNames.contains(name)) {
            throw Utils.parameterError(this.method, p, "URL \"%s\" does not contain \"{%s}\".", this.relativeUrl, name);
         }
      }

      static Set<String> parsePathParameters(String path) {
         Matcher m = PARAM_URL_REGEX.matcher(path);
         Set<String> patterns = new LinkedHashSet<>();

         while (m.find()) {
            patterns.add(m.group(1));
         }

         return patterns;
      }

      private static Class<?> boxIfPrimitive(Class<?> type) {
         if (boolean.class == type) {
            return Boolean.class;
         } else if (byte.class == type) {
            return Byte.class;
         } else if (char.class == type) {
            return Character.class;
         } else if (double.class == type) {
            return Double.class;
         } else if (float.class == type) {
            return Float.class;
         } else if (int.class == type) {
            return Integer.class;
         } else if (long.class == type) {
            return Long.class;
         } else {
            return short.class == type ? Short.class : type;
         }
      }
   }
}
