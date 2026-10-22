package org.glassfish.jersey.client.innate.inject;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.DisposableSupplier;
import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.inject.PerThread;
import org.glassfish.jersey.internal.inject.ServiceHolder;
import org.glassfish.jersey.internal.inject.ServiceHolderImpl;
import org.glassfish.jersey.internal.inject.SupplierClassBinding;
import org.glassfish.jersey.internal.inject.SupplierInstanceBinding;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.process.internal.RequestScope;
import org.glassfish.jersey.process.internal.RequestScoped;

@ConstrainedTo(RuntimeType.CLIENT)
public final class NonInjectionManager implements InjectionManager {
   private static final Logger logger = Logger.getLogger(NonInjectionManager.class.getName());
   private final MultivaluedMap<Class<?>, InstanceBinding<?>> instanceBindings = new MultivaluedHashMap();
   private final MultivaluedMap<Class<?>, ClassBinding<?>> contractBindings = new MultivaluedHashMap();
   private final MultivaluedMap<Class<?>, SupplierInstanceBinding<?>> supplierInstanceBindings = new MultivaluedHashMap();
   private final MultivaluedMap<Class<?>, SupplierClassBinding<?>> supplierClassBindings = new MultivaluedHashMap();
   private final MultivaluedMap<Type, InstanceBinding<?>> instanceTypeBindings = new MultivaluedHashMap();
   private final MultivaluedMap<Type, ClassBinding<?>> contractTypeBindings = new MultivaluedHashMap();
   private final MultivaluedMap<Type, SupplierInstanceBinding<?>> supplierTypeInstanceBindings = new MultivaluedHashMap();
   private final MultivaluedMap<Type, SupplierClassBinding<?>> supplierTypeClassBindings = new MultivaluedHashMap();
   private final MultivaluedMap<DisposableSupplier, Object> disposableSupplierObjects = new MultivaluedHashMap();
   private final NonInjectionManager.Instances instances = new NonInjectionManager.Instances();
   private final NonInjectionManager.Types types = new NonInjectionManager.Types();
   private volatile boolean isRequestScope = false;
   private volatile boolean shutdown = false;

   public NonInjectionManager() {
   }

   public NonInjectionManager(boolean warning) {
      if (warning) {
         logger.warning(LocalizationMessages.NONINJECT_FALLBACK());
      } else {
         logger.log(Level.FINER, LocalizationMessages.NONINJECT_FALLBACK());
      }
   }

   public void completeRegistration() {
      this.instances._addSingleton(InjectionManager.class, this, new NonInjectionManager.InjectionManagerBinding(), null);
   }

   public void shutdown() {
      this.shutdown = true;
      this.disposableSupplierObjects.forEach((supplier, objects) -> objects.forEach(supplier::dispose));
      this.disposableSupplierObjects.clear();
      this.instances.dispose();
      this.types.dispose();
   }

   public boolean isShutdown() {
      return this.shutdown;
   }

   private void checkShutdown() {
      if (this.shutdown) {
         throw new IllegalStateException(LocalizationMessages.NONINJECT_SHUTDOWN());
      }
   }

   public void register(Binding binding) {
      this.checkShutdown();
      if (InstanceBinding.class.isInstance(binding)) {
         InstanceBinding instanceBinding = (InstanceBinding)binding;
         Class<?> mainType = binding.getImplementationType();
         if (!this.instanceBindings.containsKey(mainType)) {
            this.instanceBindings.add(mainType, (InstanceBinding)binding);
         }

         for (Type type : instanceBinding.getContracts()) {
            if (isClass(type)) {
               if (!mainType.equals(type)) {
                  this.instanceBindings.add((Class)type, instanceBinding);
               }
            } else {
               this.instanceTypeBindings.add(type, instanceBinding);
            }
         }
      } else if (ClassBinding.class.isInstance(binding)) {
         ClassBinding<?> contractBinding = (ClassBinding<?>)binding;
         Class<?> mainType = binding.getImplementationType();
         if (!this.contractBindings.containsKey(mainType)) {
            this.contractBindings.add(mainType, contractBinding);
         }

         for (Type typex : contractBinding.getContracts()) {
            if (isClass(typex)) {
               if (!mainType.equals(typex)) {
                  this.contractBindings.add((Class)typex, contractBinding);
               }
            } else {
               this.contractTypeBindings.add(typex, contractBinding);
            }
         }
      } else if (SupplierInstanceBinding.class.isInstance(binding)) {
         SupplierInstanceBinding<?> supplierBinding = (SupplierInstanceBinding<?>)binding;

         for (Type typexx : supplierBinding.getContracts()) {
            if (isClass(typexx)) {
               this.supplierInstanceBindings.add((Class)typexx, supplierBinding);
            } else {
               this.supplierTypeInstanceBindings.add(typexx, supplierBinding);
            }
         }
      } else if (SupplierClassBinding.class.isInstance(binding)) {
         SupplierClassBinding<?> supplierBinding = (SupplierClassBinding<?>)binding;

         for (Type typexxx : supplierBinding.getContracts()) {
            if (isClass(typexxx)) {
               this.supplierClassBindings.add((Class)typexxx, supplierBinding);
            } else {
               this.supplierTypeClassBindings.add(typexxx, supplierBinding);
            }
         }
      }
   }

   public void register(Iterable<Binding> descriptors) {
      this.checkShutdown();

      for (Binding binding : descriptors) {
         this.register(binding);
      }
   }

   public void register(Binder binder) {
      this.checkShutdown();
      binder.getBindings().stream().iterator().forEachRemaining(this::register);
   }

   public void register(Object provider) throws IllegalArgumentException {
      throw new UnsupportedOperationException("Register " + provider);
   }

   public boolean isRegistrable(Class<?> clazz) {
      return false;
   }

   public <T> List<ServiceHolder<T>> getAllServiceHolders(Class<T> contractOrImpl, Annotation... qualifiers) {
      this.checkShutdown();
      NonInjectionManager.ClassBindings<T> classBindings = this.classBindings(contractOrImpl, qualifiers);
      return classBindings.getAllServiceHolders(qualifiers);
   }

   public <T> T getInstance(Class<T> contractOrImpl, Annotation... qualifiers) {
      this.checkShutdown();
      NonInjectionManager.ClassBindings<T> classBindings = this.classBindings(contractOrImpl, qualifiers);
      classBindings.matchQualifiers(qualifiers);
      return classBindings.getInstance();
   }

   public <T> T getInstance(Class<T> contractOrImpl, String classAnalyzer) {
      throw new UnsupportedOperationException("getInstance(Class, String)");
   }

   public <T> T getInstance(Class<T> contractOrImpl) {
      this.checkShutdown();
      T instance = this.instances.getInstance(contractOrImpl, null);
      return instance != null ? instance : this.create(contractOrImpl);
   }

   public <T> T getInstance(Type contractOrImpl) {
      this.checkShutdown();
      if (ParameterizedType.class.isInstance(contractOrImpl)) {
         T instance = this.types.getInstance(contractOrImpl, null);
         if (instance != null) {
            return instance;
         } else {
            NonInjectionManager.TypeBindings<T> typeBindings = this.typeBindings(contractOrImpl);
            return typeBindings.getInstance();
         }
      } else if (isClass(contractOrImpl)) {
         return this.getInstance((Class<T>)contractOrImpl);
      } else {
         throw new IllegalStateException(LocalizationMessages.NONINJECT_UNSATISFIED(contractOrImpl));
      }
   }

   private static boolean isClass(Type type) {
      return Class.class.isAssignableFrom(type.getClass());
   }

   public Object getInstance(ForeignDescriptor foreignDescriptor) {
      throw new UnsupportedOperationException("getInstance(ForeignDescriptor foreignDescriptor) ");
   }

   public ForeignDescriptor createForeignDescriptor(Binding binding) {
      throw new UnsupportedOperationException("createForeignDescriptor(Binding binding) ");
   }

   public <T> List<T> getAllInstances(Type contractOrImpl) {
      this.checkShutdown();
      if (!isClass(contractOrImpl)) {
         NonInjectionManager.TypeBindings<T> typeBindings = this.typeBindings(contractOrImpl);
         return typeBindings.allInstances();
      } else {
         NonInjectionManager.ClassBindings<T> classBindings = this.classBindings((Class<T>)contractOrImpl);
         return classBindings.allInstances();
      }
   }

   public <T> T create(Class<T> createMe) {
      this.checkShutdown();
      if (InjectionManager.class.equals(createMe)) {
         return (T)this;
      } else if (RequestScope.class.equals(createMe)) {
         if (!this.isRequestScope) {
            this.isRequestScope = true;
            return (T)(new NonInjectionRequestScope());
         } else {
            throw new IllegalStateException(LocalizationMessages.NONINJECT_REQUESTSCOPE_CREATED());
         }
      } else {
         NonInjectionManager.ClassBindings<T> classBindings = this.classBindings(createMe);
         return classBindings.create(true);
      }
   }

   public <T> T createAndInitialize(Class<T> createMe) {
      this.checkShutdown();
      if (InjectionManager.class.equals(createMe)) {
         return (T)this;
      } else if (RequestScope.class.equals(createMe)) {
         if (!this.isRequestScope) {
            this.isRequestScope = true;
            return (T)(new NonInjectionRequestScope());
         } else {
            throw new IllegalStateException(LocalizationMessages.NONINJECT_REQUESTSCOPE_CREATED());
         }
      } else {
         NonInjectionManager.ClassBindings<T> classBindings = this.classBindings(createMe);
         T t = classBindings.create(false);
         return t != null ? t : this.justCreate(createMe);
      }
   }

   public <T> T justCreate(Class<T> createMe) {
      T result = null;

      try {
         Constructor<T> mostArgConstructor = findConstructor(createMe);
         if (mostArgConstructor != null) {
            int argCount = mostArgConstructor.getParameterCount();
            if (argCount == 0) {
               ensureAccessible(mostArgConstructor);
               result = mostArgConstructor.newInstance();
            } else if (argCount > 0) {
               Object[] args = this.getArguments(mostArgConstructor, argCount);
               if (args != null) {
                  ensureAccessible(mostArgConstructor);
                  result = mostArgConstructor.newInstance(args);
               }
            }
         }

         if (result == null) {
            throw new IllegalStateException(LocalizationMessages.NONINJECT_NO_CONSTRUCTOR(createMe.getName()));
         }
      } catch (Exception var6) {
         throw new IllegalStateException(var6);
      }

      this.inject(result);
      return result;
   }

   private static <T> Constructor<T> findConstructor(Class<T> forClass) {
      Constructor<T>[] constructors = (Constructor<T>[])forClass.getDeclaredConstructors();
      Constructor<T> mostArgConstructor = null;
      int argCount = -1;

      for (Constructor<T> constructor : constructors) {
         if ((constructor.isAnnotationPresent(Inject.class) || constructor.getParameterCount() == 0) && constructor.getParameterCount() > argCount) {
            mostArgConstructor = constructor;
            argCount = constructor.getParameterCount();
         }
      }

      return mostArgConstructor;
   }

   private Object[] getArguments(Executable executable, int argCount) {
      if (executable == null) {
         return null;
      } else {
         Object[] args = new Object[argCount];

         for (int i = 0; i != argCount; i++) {
            Type type = executable.getAnnotatedParameterTypes()[i].getType();
            args[i] = isClass(type) ? this.getInstance((Class)type) : this.getInstance(type);
         }

         return args;
      }
   }

   private static void ensureAccessible(Executable executable) {
      try {
         if (!executable.isAccessible()) {
            executable.setAccessible(true);
         }
      } catch (Exception var2) {
      }
   }

   private void checkUnique(List<?> list) {
      if (list.size() != 1) {
         throw new IllegalStateException(LocalizationMessages.NONINJECT_AMBIGUOUS_SERVICES(list.get(0)));
      }
   }

   public void inject(Object injectMe) {
      Method postConstruct = getAnnotatedMethod(injectMe, PostConstruct.class);
      if (postConstruct != null) {
         ensureAccessible(postConstruct);

         try {
            postConstruct.invoke(injectMe);
         } catch (Exception var4) {
            throw new IllegalStateException(var4);
         }
      }
   }

   public void inject(Object injectMe, String classAnalyzer) {
      throw new UnsupportedOperationException("inject(Object injectMe, String classAnalyzer)");
   }

   public void preDestroy(Object preDestroyMe) {
      Method preDestroy = getAnnotatedMethod(preDestroyMe, PreDestroy.class);
      if (preDestroy != null) {
         ensureAccessible(preDestroy);

         try {
            preDestroy.invoke(preDestroyMe);
         } catch (Exception var4) {
            throw new IllegalStateException(var4);
         }
      }
   }

   private static Method getAnnotatedMethod(Object object, Class<? extends Annotation> annotation) {
      Class<?> clazz = object.getClass();

      for (Method method : clazz.getMethods()) {
         if (method.isAnnotationPresent(annotation) && method.getParameterCount() == 0) {
            return method;
         }
      }

      return null;
   }

   private <T> T createSupplierProxyIfNeeded(Boolean createProxy, Class<T> iface, final Supplier<T> supplier) {
      return (T)(createProxy != null && createProxy && iface.isInterface()
         ? Proxy.newProxyInstance(iface.getClassLoader(), new Class[]{iface}, new InvocationHandler() {
            final NonInjectionManager.SingleRegisterSupplier<T> singleSupplierRegister = NonInjectionManager.this.new SingleRegisterSupplier<>(supplier);

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
               T t = this.singleSupplierRegister.get();
               return method.invoke(t, args);
            }
         })
         : this.registerDisposableSupplierAndGet(supplier));
   }

   private <T> T registerDisposableSupplierAndGet(Supplier<T> supplier) {
      T instance = supplier.get();
      if (DisposableSupplier.class.isInstance(supplier)) {
         this.disposableSupplierObjects.add(supplier, instance);
      }

      return instance;
   }

   private <T> NonInjectionManager.ClassBindings<T> classBindings(Class<T> clazz, Annotation... instancesQualifiers) {
      NonInjectionManager.ClassBindings<T> classBindings = new NonInjectionManager.ClassBindings<>(clazz, instancesQualifiers);
      List<InstanceBinding<?>> ib = (List<InstanceBinding<?>>)this.instanceBindings.get(clazz);
      if (ib != null) {
         ib.forEach(binding -> classBindings.instanceBindings.add((InstanceBinding<T>)binding));
      }

      List<SupplierInstanceBinding<?>> sib = (List<SupplierInstanceBinding<?>>)this.supplierInstanceBindings.get(clazz);
      if (sib != null) {
         sib.forEach(binding -> classBindings.supplierInstanceBindings.add((SupplierInstanceBinding<T>)binding));
      }

      List<ClassBinding<?>> cb = (List<ClassBinding<?>>)this.contractBindings.get(clazz);
      if (cb != null) {
         cb.forEach(binding -> classBindings.classBindings.add((ClassBinding<T>)binding));
      }

      List<SupplierClassBinding<?>> scb = (List<SupplierClassBinding<?>>)this.supplierClassBindings.get(clazz);
      if (scb != null) {
         scb.forEach(binding -> classBindings.supplierClassBindings.add((SupplierClassBinding<T>)binding));
      }

      return classBindings;
   }

   private <T> NonInjectionManager.TypeBindings<T> typeBindings(Type type) {
      NonInjectionManager.TypeBindings<T> typeBindings = new NonInjectionManager.TypeBindings<>(type);
      List<InstanceBinding<?>> ib = (List<InstanceBinding<?>>)this.instanceTypeBindings.get(type);
      if (ib != null) {
         ib.forEach(binding -> typeBindings.instanceBindings.add((InstanceBinding<T>)binding));
      }

      List<SupplierInstanceBinding<?>> sib = (List<SupplierInstanceBinding<?>>)this.supplierTypeInstanceBindings.get(type);
      if (sib != null) {
         sib.forEach(binding -> typeBindings.supplierInstanceBindings.add((SupplierInstanceBinding<T>)binding));
      }

      List<ClassBinding<?>> cb = (List<ClassBinding<?>>)this.contractTypeBindings.get(type);
      if (cb != null) {
         cb.forEach(binding -> typeBindings.classBindings.add((ClassBinding<T>)binding));
      }

      List<SupplierClassBinding<?>> scb = (List<SupplierClassBinding<?>>)this.supplierTypeClassBindings.get(type);
      if (scb != null) {
         scb.forEach(binding -> typeBindings.supplierClassBindings.add((SupplierClassBinding<T>)binding));
      }

      return typeBindings;
   }

   private class ClassBindings<T> extends NonInjectionManager.XBindings<T, Class<?>> {
      private ClassBindings(Class<T> clazz, Annotation[] instancesQualifiers) {
         super(clazz, instancesQualifiers, NonInjectionManager.this.instances);
      }

      List<ServiceHolder<T>> getAllServiceHolders(Annotation... qualifiers) {
         this.matchQualifiers(qualifiers);
         List<ServiceHolder<T>> holders = new LinkedList<>();
         List<NonInjectionManager.InstanceContext<?>> instanceContextList = this.instances.getContexts(this.type, qualifiers);
         if (instanceContextList != null) {
            instanceContextList.forEach(instanceContext -> this.filterBinding(instanceContext.getBinding()));
            instanceContextList.forEach(
               instanceContext -> holders.add(
                     new ServiceHolderImpl(
                        instanceContext.getInstance(),
                        instanceContext.getInstance().getClass(),
                        instanceContext.getBinding().getContracts(),
                        instanceContext.getBinding().getRank() == null ? 0 : instanceContext.getBinding().getRank()
                     )
                  )
            );
         }

         List<ServiceHolder<T>> instanceBindingHolders = this.instanceBindings.stream().map(this::_serviceHolder).collect(Collectors.toList());
         holders.addAll(instanceBindingHolders);
         List<ServiceHolder<T>> classBindingHolders = this.classBindings
            .stream()
            .filter(binding -> NonInjectionManager.findConstructor(binding.getService()) != null)
            .map(this::_serviceHolder)
            .collect(Collectors.toList());
         holders.addAll(classBindingHolders);
         return holders;
      }

      private <T> ServiceHolderImpl<T> _serviceHolder(InstanceBinding<T> binding) {
         return new ServiceHolderImpl(
            binding.getService(), binding.getImplementationType(), binding.getContracts(), binding.getRank() == null ? 0 : binding.getRank()
         );
      }

      private <T> ServiceHolderImpl<T> _serviceHolder(ClassBinding<T> binding) {
         return new ServiceHolderImpl(
            NonInjectionManager.this.create(binding.getService()),
            binding.getImplementationType(),
            binding.getContracts(),
            binding.getRank() == null ? 0 : binding.getRank()
         );
      }

      @Override
      protected T _create(SupplierClassBinding<T> binding) {
         Supplier<T> supplier = this.instances.getInstance(binding.getSupplierClass(), null);
         if (supplier == null) {
            supplier = NonInjectionManager.this.justCreate(binding.getSupplierClass());
            if (Singleton.class.equals(binding.getSupplierScope())) {
               supplier = this.instances.addSingleton(binding.getSupplierClass(), supplier, binding, null);
            } else if (this._isPerThread(binding.getSupplierScope())) {
               this.instances.addThreadInstance(binding.getSupplierClass(), supplier, binding, null);
            }
         }

         T t = NonInjectionManager.this.createSupplierProxyIfNeeded(binding.isProxiable(), (Class<T>)this.type, supplier);
         if (Singleton.class.equals(binding.getScope())) {
            t = this._addInstance(this.type, t, binding);
         } else if (this._isPerThread(binding.getScope())) {
            this._addThreadInstance(this.type, t, binding);
         }

         return t;
      }

      @Override
      protected T _createAndStore(ClassBinding<T> binding) {
         T result = NonInjectionManager.this.justCreate(binding.getService());
         return this._addInstance(binding.getService(), result, binding);
      }
   }

   private static final class InjectionManagerBinding extends Binding<InjectionManager, Binding<?, ?>> {
      private InjectionManagerBinding() {
      }
   }

   private static class InstanceContext<T> {
      private final T instance;
      private final Binding<?, ?> binding;
      private final Annotation[] createdWithQualifiers;

      private InstanceContext(T instance, Binding<?, ?> binding, Annotation[] qualifiers) {
         this.instance = instance;
         this.binding = binding;
         this.createdWithQualifiers = qualifiers;
      }

      public Binding<?, ?> getBinding() {
         return this.binding;
      }

      public T getInstance() {
         return this.instance;
      }

      static <T> List<T> toInstances(List<NonInjectionManager.InstanceContext<?>> instances, Annotation[] qualifiers) {
         return instances != null
            ? instances.stream().filter(instance -> instance.hasQualifiers(qualifiers)).map(pair -> (T)pair.getInstance()).collect(Collectors.toList())
            : null;
      }

      private static List<NonInjectionManager.InstanceContext<?>> filterInstances(
         List<NonInjectionManager.InstanceContext<?>> instances, Annotation... qualifiers
      ) {
         return instances != null ? instances.stream().filter(instance -> instance.hasQualifiers(qualifiers)).collect(Collectors.toList()) : null;
      }

      private boolean hasQualifiers(Annotation[] requested) {
         if (requested != null) {
            label29:
            for (Annotation req : requested) {
               if (this.createdWithQualifiers != null) {
                  for (Annotation cur : this.createdWithQualifiers) {
                     if (cur.annotationType().isInstance(req)) {
                        continue label29;
                     }
                  }

                  return false;
               }
            }
         }

         return true;
      }
   }

   private class Instances extends NonInjectionManager.TypedInstances<Class<?>> {
      private Instances() {
      }
   }

   private class SingleRegisterSupplier<T> {
      private final LazyValue<T> once;

      private SingleRegisterSupplier(Supplier<T> supplier) {
         this.once = Values.lazy(() -> NonInjectionManager.this.registerDisposableSupplierAndGet(supplier));
      }

      T get() {
         return (T)this.once.get();
      }
   }

   private class TypeBindings<T> extends NonInjectionManager.XBindings<T, Type> {
      private TypeBindings(Type type) {
         super(type, null, NonInjectionManager.this.types);
      }

      @Override
      protected T _create(SupplierClassBinding<T> binding) {
         Supplier<T> supplier = NonInjectionManager.this.justCreate(binding.getSupplierClass());
         T t = NonInjectionManager.this.registerDisposableSupplierAndGet(supplier);
         if (Singleton.class.equals(binding.getScope())) {
            t = this._addInstance(this.type, t, binding);
         } else if (this._isPerThread(binding.getScope())) {
            this._addThreadInstance(this.type, t, binding);
         }

         return t;
      }

      @Override
      protected T _createAndStore(ClassBinding<T> binding) {
         T result = NonInjectionManager.this.justCreate(binding.getService());
         return this._addInstance(this.type, result, binding);
      }

      @Override
      T create(boolean throwWhenNoBinding) {
         if (ParameterizedType.class.isInstance(this.type)) {
            final ParameterizedType pt = (ParameterizedType)this.type;
            if (Provider.class.equals(pt.getRawType())) {
               return (T)(new Provider<Object>() {
                  final NonInjectionManager.SingleRegisterSupplier<Object> supplier = NonInjectionManager.this.new SingleRegisterSupplier<>(
                     new Supplier<Object>() {
                        @Override
                        public Object get() {
                           Type actualTypeArgument = pt.getActualTypeArguments()[0];
                           return NonInjectionManager.isClass(actualTypeArgument)
                              ? NonInjectionManager.this.getInstance((Class<T>)actualTypeArgument)
                              : NonInjectionManager.this.getInstance(actualTypeArgument);
                        }
                     }
                  );

                  public Object get() {
                     return this.supplier.get();
                  }
               });
            }
         }

         return (T)super.create(throwWhenNoBinding);
      }
   }

   private class TypedInstances<TYPE> {
      private final MultivaluedMap<TYPE, NonInjectionManager.InstanceContext<?>> singletonInstances = new MultivaluedHashMap();
      private final ThreadLocal<MultivaluedMap<TYPE, NonInjectionManager.InstanceContext<?>>> threadInstances = new ThreadLocal<>();
      private final List<Object> threadPredestroyables = Collections.synchronizedList(new LinkedList<>());

      private TypedInstances() {
      }

      private <T> List<NonInjectionManager.InstanceContext<?>> _getSingletons(TYPE clazz) {
         synchronized (this.singletonInstances) {
            return (List<NonInjectionManager.InstanceContext<?>>)this.singletonInstances.get(clazz);
         }
      }

      <T> T _addSingleton(TYPE clazz, T instance, Binding<?, ?> binding, Annotation[] qualifiers) {
         synchronized (this.singletonInstances) {
            List<NonInjectionManager.InstanceContext<?>> values = (List<NonInjectionManager.InstanceContext<?>>)this.singletonInstances.get(clazz);
            if (values != null) {
               List<NonInjectionManager.InstanceContext<?>> qualified = values.stream()
                  .filter(ctx -> ctx.hasQualifiers(qualifiers))
                  .collect(Collectors.toList());
               if (!qualified.isEmpty()) {
                  return qualified.get(0).instance;
               }
            }

            this.singletonInstances.add(clazz, new NonInjectionManager.InstanceContext(instance, binding, qualifiers));
            this.threadPredestroyables.add(instance);
            return instance;
         }
      }

      <T> T addSingleton(TYPE clazz, T t, Binding<?, ?> binding, Annotation[] instanceQualifiers) {
         T t2 = this._addSingleton(clazz, t, binding, instanceQualifiers);
         if (t2 == t) {
            for (Type contract : binding.getContracts()) {
               if (!clazz.equals(contract) && NonInjectionManager.isClass(contract)) {
                  this._addSingleton((TYPE)contract, t, binding, instanceQualifiers);
               }
            }
         }

         return t2;
      }

      private List<NonInjectionManager.InstanceContext<?>> _getThreadInstances(TYPE clazz) {
         MultivaluedMap<TYPE, NonInjectionManager.InstanceContext<?>> ti = this.threadInstances.get();
         List<NonInjectionManager.InstanceContext<?>> list = ti == null ? null : new LinkedList<>();
         return ti != null ? (List)ti.get(clazz) : list;
      }

      private <T> void _addThreadInstance(TYPE clazz, T instance, Binding<T, ?> binding, Annotation[] qualifiers) {
         MultivaluedMap<TYPE, NonInjectionManager.InstanceContext<?>> map = this.threadInstances.get();
         if (map == null) {
            map = new MultivaluedHashMap();
            this.threadInstances.set(map);
         }

         map.add(clazz, new NonInjectionManager.InstanceContext(instance, binding, qualifiers));
         this.threadPredestroyables.add(instance);
      }

      <T> void addThreadInstance(TYPE clazz, T t, Binding<T, ?> binding, Annotation[] instanceQualifiers) {
         this._addThreadInstance(clazz, t, binding, instanceQualifiers);

         for (Type contract : binding.getContracts()) {
            if (!clazz.equals(contract) && NonInjectionManager.isClass(contract)) {
               this._addThreadInstance((TYPE)contract, t, binding, instanceQualifiers);
            }
         }
      }

      private <T> List<T> getInstances(TYPE clazz, Annotation[] annotations) {
         List<NonInjectionManager.InstanceContext<?>> i = this._getContexts(clazz);
         return NonInjectionManager.InstanceContext.toInstances(i, annotations);
      }

      <T> List<NonInjectionManager.InstanceContext<?>> getContexts(TYPE clazz, Annotation[] annotations) {
         List<NonInjectionManager.InstanceContext<?>> i = this._getContexts(clazz);
         return NonInjectionManager.InstanceContext.filterInstances(i, annotations);
      }

      private <T> List<NonInjectionManager.InstanceContext<?>> _getContexts(TYPE clazz) {
         List<NonInjectionManager.InstanceContext<?>> si = this._getSingletons(clazz);
         List<NonInjectionManager.InstanceContext<?>> ti = this._getThreadInstances(clazz);
         if (si == null && ti != null) {
            si = ti;
         } else if (ti != null) {
            si.addAll(ti);
         }

         return si;
      }

      <T> T getInstance(TYPE clazz, Annotation[] annotations) {
         List<T> i = this.getInstances(clazz, annotations);
         if (i != null) {
            NonInjectionManager.this.checkUnique(i);
            return i.get(0);
         } else {
            return null;
         }
      }

      void dispose() {
         this.singletonInstances.forEach((clazz, instances) -> instances.forEach(instance -> NonInjectionManager.this.preDestroy(instance.getInstance())));
         this.threadPredestroyables.forEach(NonInjectionManager.this::preDestroy);
      }
   }

   private class Types extends NonInjectionManager.TypedInstances<Type> {
      private Types() {
      }
   }

   private abstract class XBindings<X, TYPE> {
      protected final List<InstanceBinding<X>> instanceBindings = new LinkedList<>();
      protected final List<SupplierInstanceBinding<X>> supplierInstanceBindings = new LinkedList<>();
      protected final List<ClassBinding<X>> classBindings = new LinkedList<>();
      protected final List<SupplierClassBinding<X>> supplierClassBindings = new LinkedList<>();
      protected final TYPE type;
      protected final Annotation[] instancesQualifiers;
      protected final NonInjectionManager.TypedInstances<TYPE> instances;

      protected XBindings(TYPE type, Annotation[] instancesQualifiers, NonInjectionManager.TypedInstances<TYPE> instances) {
         this.type = type;
         this.instancesQualifiers = instancesQualifiers;
         this.instances = instances;
      }

      int size() {
         return this.instanceBindings.size() + this.supplierInstanceBindings.size() + this.classBindings.size() + this.supplierClassBindings.size();
      }

      private void _checkUnique() {
         if (this.size() > 1) {
            throw new IllegalStateException(LocalizationMessages.NONINJECT_AMBIGUOUS_SERVICES(this.type));
         }
      }

      void filterBinding(Binding binding) {
         if (InstanceBinding.class.isInstance(binding)) {
            this.instanceBindings.remove(binding);
         } else if (ClassBinding.class.isInstance(binding)) {
            this.classBindings.remove(binding);
         } else if (SupplierInstanceBinding.class.isInstance(binding)) {
            this.supplierInstanceBindings.remove(binding);
         } else if (SupplierClassBinding.class.isInstance(binding)) {
            this.supplierClassBindings.remove(binding);
         }
      }

      void matchQualifiers(Annotation... bindingQualifiers) {
         if (bindingQualifiers != null) {
            this._filterRequested(this.instanceBindings, bindingQualifiers);
            this._filterRequested(this.classBindings, bindingQualifiers);
            this._filterRequested(this.supplierInstanceBindings, bindingQualifiers);
            this._filterRequested(this.supplierClassBindings, bindingQualifiers);
         }
      }

      private void _filterRequested(List<? extends Binding<?, ?>> bindingList, Annotation... requestedQualifiers) {
         Iterator<? extends Binding> bindingIterator = bindingList.iterator();

         while (bindingIterator.hasNext()) {
            Binding<X, ?> binding = (Binding<X, ?>)bindingIterator.next();

            label29:
            for (Annotation requestedQualifier : requestedQualifiers) {
               for (Annotation bindingQualifier : binding.getQualifiers()) {
                  if (requestedQualifier.annotationType().isInstance(bindingQualifier)) {
                     continue label29;
                  }
               }

               bindingIterator.remove();
            }
         }
      }

      protected boolean _isPerThread(Class<? extends Annotation> scope) {
         return RequestScoped.class.equals(scope) || PerThread.class.equals(scope);
      }

      private X _getInstance(InstanceBinding<X> instanceBinding) {
         return (X)instanceBinding.getService();
      }

      private X _create(SupplierInstanceBinding<X> binding) {
         Supplier<X> supplier = binding.getSupplier();
         X t = NonInjectionManager.this.registerDisposableSupplierAndGet(supplier);
         if (Singleton.class.equals(binding.getScope())) {
            this._addInstance(t, binding);
         } else if (this._isPerThread(binding.getScope())) {
            this._addThreadInstance(t, binding);
         }

         return t;
      }

      X create(boolean throwWhenNoBinding) {
         this._checkUnique();
         if (!this.instanceBindings.isEmpty()) {
            return this._getInstance(this.instanceBindings.get(0));
         } else if (!this.supplierInstanceBindings.isEmpty()) {
            return this._create(this.supplierInstanceBindings.get(0));
         } else if (!this.classBindings.isEmpty()) {
            return this._createAndStore(this.classBindings.get(0));
         } else if (!this.supplierClassBindings.isEmpty()) {
            return this._create(this.supplierClassBindings.get(0));
         } else if (throwWhenNoBinding) {
            throw new IllegalStateException(LocalizationMessages.NONINJECT_NO_BINDING(this.type));
         } else {
            return null;
         }
      }

      protected X getInstance() {
         X instance = this.instances.getInstance(this.type, this.instancesQualifiers);
         return instance != null ? instance : this.create(true);
      }

      List<X> allInstances() {
         List<X> list = new LinkedList<>();
         List<NonInjectionManager.InstanceContext<?>> instanceContextList = this.instances.getContexts(this.type, this.instancesQualifiers);
         if (instanceContextList != null) {
            instanceContextList.forEach(instanceContext -> this.filterBinding(instanceContext.getBinding()));
            instanceContextList.forEach(instanceContext -> list.add((X)instanceContext.getInstance()));
         }

         list.addAll(this.instanceBindings.stream().map(this::_getInstance).collect(Collectors.toList()));
         list.addAll(this.classBindings.stream().map(this::_createAndStore).collect(Collectors.toList()));
         list.addAll(this.supplierInstanceBindings.stream().map(this::_create).collect(Collectors.toList()));
         list.addAll(this.supplierClassBindings.stream().map(this::_create).collect(Collectors.toList()));
         return list;
      }

      protected abstract X _create(SupplierClassBinding<X> var1);

      protected abstract X _createAndStore(ClassBinding<X> var1);

      protected <T> T _addInstance(TYPE type, T instance, Binding<?, ?> binding) {
         return this.instances.addSingleton(type, instance, binding, this.instancesQualifiers);
      }

      protected void _addThreadInstance(TYPE type, Object instance, Binding binding) {
         this.instances.addThreadInstance(type, (Binding)instance, binding, this.instancesQualifiers);
      }

      protected <T> T _addInstance(T instance, Binding<?, ?> binding) {
         return this.instances.addSingleton(this.type, instance, binding, this.instancesQualifiers);
      }

      protected void _addThreadInstance(Object instance, Binding binding) {
         this.instances.addThreadInstance(this.type, (Binding)instance, binding, this.instancesQualifiers);
      }
   }
}
