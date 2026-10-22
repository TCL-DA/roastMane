package kotlin.coroutines

import java.io.Serializable
import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.CoroutineContext.Key
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Ref
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SinceKotlin(version = "1.3")
@SourceDebugExtension(["SMAP\nCoroutineContextImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineContextImpl.kt\nkotlin/coroutines/CombinedContext\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,196:1\n1#2:197\n*E\n"])
internal class CombinedContext(left: CoroutineContext, element: Element) : CoroutineContext, Serializable {
   private final val element: Element
   private final val left: CoroutineContext

   init {
      this.left = left;
      this.element = element;
   }

   public override operator fun <E : Element> get(key: Key<E>): E? {
      var cur: CombinedContext = this;

      while (true) {
         var next: CoroutineContext.Element = cur.element.get(key);
         if (next != null) {
            return (E)next;
         }

         if (cur.left !is CombinedContext) {
            return (E)cur.left.get(key);
         }

         cur = cur.left as CombinedContext;
      }
   }

   public override fun <R> fold(initial: R, operation: (R, Element) -> R): R {
      return (R)operation.invoke(this.left.fold(initial, operation), this.element);
   }

   public override fun minusKey(key: Key<*>): CoroutineContext {
      if (this.element.get(key) != null) {
         return this.left;
      } else {
         var newLeft: CoroutineContext = this.left.minusKey(key);
         return if (newLeft === this.left)
            this
            else
            (if (newLeft === EmptyCoroutineContext.INSTANCE) this.element else new CombinedContext(newLeft, this.element));
      }
   }

   private fun size(): Int {
      var cur: CombinedContext = this;
      var size: Int = 2;

      while (true) {
         var var3: CoroutineContext = cur.left;
         var var10000: CombinedContext = cur.left as? CombinedContext;
         if ((cur.left as? CombinedContext) == null) {
            return size;
         }

         cur = var10000;
         size++;
      }
   }

   private fun contains(element: Element): Boolean {
      return this.get(element.getKey()) == element;
   }

   private fun containsAll(context: CombinedContext): Boolean {
      var cur: CombinedContext = context;

      while (this.contains(cur.element)) {
         var next: CoroutineContext = cur.left;
         if (cur.left !is CombinedContext) {
            return this.contains(next as CoroutineContext.Element);
         }

         cur = cur.left as CombinedContext;
      }

      return false;
   }

   public override operator fun equals(other: Any?): Boolean {
      return this === other || other is CombinedContext && (other as CombinedContext).size() == this.size() && (other as CombinedContext).containsAll(this);
   }

   public override fun hashCode(): Int {
      return this.left.hashCode() + this.element.hashCode();
   }

   public override fun toString(): String {
      return '[' + this.fold("", <unrepresentable>.INSTANCE) + ']';
   }

   private fun writeReplace(): Any {
      var n: Int = this.size();
      var elements: Array<CoroutineContext> = new CoroutineContext[n];
      var index: Ref.IntRef = new Ref.IntRef();
      this.fold(Unit.INSTANCE, (new Function2<Unit, CoroutineContext.Element, Unit>(elements, index) {
         {
            super(2);
            this.$elements = `$elements`;
            this.$index = `$index`;
         }

         public final void invoke(@NotNull Unit var1, @NotNull CoroutineContext.Element element) {
            this.$elements[this.$index.element++] = element;
         }
      }) as Function2<? super Unit, ? super CoroutineContext.Element, ? extends Unit>);
      if (index.element != n) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         return new CombinedContext.Serialized(elements);
      }
   }

   override fun plus(context: CoroutineContext): CoroutineContext {
      return CoroutineContext.DefaultImpls.plus(this, context);
   }

   @SourceDebugExtension(["SMAP\nCoroutineContextImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineContextImpl.kt\nkotlin/coroutines/CombinedContext$Serialized\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,196:1\n12720#2,3:197\n*S KotlinDebug\n*F\n+ 1 CoroutineContextImpl.kt\nkotlin/coroutines/CombinedContext$Serialized\n*L\n193#1:197,3\n*E\n"])
   private class Serialized(vararg elements: Any) : Serializable {
      @JvmStatic
      public CombinedContext.Serialized.Companion Companion = new CombinedContext.Serialized.Companion(null);
      private CoroutineContext[] elements;
      @JvmStatic
      private long serialVersionUID = 0L;

      public final val elements: Array<CoroutineContext>

      init {
         this.elements = elements;
      }

      private fun readResolve(): Any {
         var `accumulator$iv`: Any = EmptyCoroutineContext.INSTANCE;

         var `$this$fold$iv`: Array<CoroutineContext>;
         for (Object element$iv : $this$fold$iv) {
            `accumulator$iv` = (`accumulator$iv` as CoroutineContext).plus((CoroutineContext)`element$iv`);
         }

         return `accumulator$iv`;
      }

      companion object Companion private constructor() {
         private const val serialVersionUID: Long

      }
   }
}
