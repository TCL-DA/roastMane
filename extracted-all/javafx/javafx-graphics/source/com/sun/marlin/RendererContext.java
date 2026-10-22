package com.sun.marlin;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Rectangle;
import com.sun.util.reentrant.ReentrantContext;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

public final class RendererContext extends ReentrantContext implements MarlinConst {
   private static final AtomicInteger CTX_COUNT = new AtomicInteger(1);
   private final Object cleanerObj;
   public boolean dirty = false;
   public final float[] float6 = new float[6];
   final Curve curve = new Curve();
   public final TransformingPathConsumer2D transformerPC2D;
   private WeakReference<Path2D> refPath2D = null;
   public final Renderer renderer;
   public final Stroker stroker;
   public final CollinearSimplifier simplifier = new CollinearSimplifier();
   public final PathSimplifier pathSimplifier = new PathSimplifier();
   public final Dasher dasher;
   int stroking = 0;
   public boolean doClip = false;
   boolean closedPath = false;
   public final double[] clipRect = new double[4];
   public double clipInvScale = 0.0;
   public final TransformingPathConsumer2D.CurveBasicMonotonizer monotonizer;
   final TransformingPathConsumer2D.CurveClipSplitter curveClipSplitter;
   final RendererContext.RendererSharedMemory rdrMem;
   private RendererNoAA rendererNoAA = null;
   public final Rectangle clip = new Rectangle();
   public MaskMarlinAlphaConsumer consumer = null;
   private final IntArrayCache cleanIntCache = new IntArrayCache(true, 5);
   private final IntArrayCache dirtyIntCache = new IntArrayCache(false, 5);
   private final DoubleArrayCache dirtyDoubleCache = new DoubleArrayCache(false, 4);
   private final ByteArrayCache dirtyByteCache = new ByteArrayCache(false, 2);
   final RendererStats stats;

   public static RendererContext createContext() {
      return new RendererContext("ctx" + Integer.toString(CTX_COUNT.getAndIncrement()));
   }

   RendererContext(String var1) {
      if (LOG_CREATE_CONTEXT) {
         MarlinUtils.logInfo("new RendererContext = " + var1);
      }

      this.cleanerObj = new Object();
      if (!DO_STATS) {
         this.stats = null;
      } else {
         this.stats = RendererStats.createInstance(this.cleanerObj, var1);
         this.stats.cacheStats = new ArrayCacheConst.CacheStats[]{
            this.cleanIntCache.stats, this.dirtyIntCache.stats, this.dirtyDoubleCache.stats, this.dirtyByteCache.stats
         };
      }

      this.monotonizer = new TransformingPathConsumer2D.CurveBasicMonotonizer(this);
      this.curveClipSplitter = new TransformingPathConsumer2D.CurveClipSplitter(this);
      this.transformerPC2D = new TransformingPathConsumer2D(this);
      this.rdrMem = new RendererContext.RendererSharedMemory(this);
      this.renderer = new Renderer(this);
      this.stroker = new Stroker(this);
      this.dasher = new Dasher(this);
   }

   public void dispose() {
      if (DO_STATS) {
         if (this.stats.totalOffHeap > this.stats.totalOffHeapMax) {
            this.stats.totalOffHeapMax = this.stats.totalOffHeap;
         }

         this.stats.totalOffHeap = 0L;
      }

      this.stroking = 0;
      this.doClip = false;
      this.closedPath = false;
      this.clipInvScale = 0.0;
      if (this.dirty) {
         this.dasher.dispose();
         this.stroker.dispose();
         this.dirty = false;
      }
   }

   public Path2D getPath2D() {
      Path2D var1 = this.refPath2D != null ? this.refPath2D.get() : null;
      if (var1 == null) {
         var1 = new Path2D(1, INITIAL_EDGES_COUNT);
         this.refPath2D = new WeakReference<>(var1);
      }

      var1.reset();
      return var1;
   }

   public RendererNoAA getRendererNoAA() {
      if (this.rendererNoAA == null) {
         this.rendererNoAA = new RendererNoAA(this);
      }

      return this.rendererNoAA;
   }

   OffHeapArray newOffHeapArray(long var1) {
      if (DO_STATS) {
         this.stats.totalOffHeapInitial += var1;
      }

      return new OffHeapArray(this.cleanerObj, var1);
   }

   IntArrayCache.Reference newCleanIntArrayRef(int var1) {
      return this.cleanIntCache.createRef(var1);
   }

   IntArrayCache.Reference newDirtyIntArrayRef(int var1) {
      return this.dirtyIntCache.createRef(var1);
   }

   DoubleArrayCache.Reference newDirtyDoubleArrayRef(int var1) {
      return this.dirtyDoubleCache.createRef(var1);
   }

   ByteArrayCache.Reference newDirtyByteArrayRef(int var1) {
      return this.dirtyByteCache.createRef(var1);
   }

   static final class RendererSharedMemory {
      final OffHeapArray edges;
      final IntArrayCache.Reference edgeBuckets_ref;
      final IntArrayCache.Reference edgeBucketCounts_ref;
      final IntArrayCache.Reference alphaLine_ref;
      final IntArrayCache.Reference crossings_ref;
      final IntArrayCache.Reference edgePtrs_ref;
      final IntArrayCache.Reference aux_crossings_ref;
      final IntArrayCache.Reference aux_edgePtrs_ref;
      final IntArrayCache.Reference blkFlags_ref;

      RendererSharedMemory(RendererContext var1) {
         this.edges = var1.newOffHeapArray((long)MarlinConst.INITIAL_EDGES_CAPACITY);
         this.edgeBuckets_ref = var1.newCleanIntArrayRef(MarlinConst.INITIAL_BUCKET_ARRAY);
         this.edgeBucketCounts_ref = var1.newCleanIntArrayRef(MarlinConst.INITIAL_BUCKET_ARRAY);
         this.alphaLine_ref = var1.newCleanIntArrayRef(MarlinConst.INITIAL_AA_ARRAY);
         this.crossings_ref = var1.newDirtyIntArrayRef(MarlinConst.INITIAL_CROSSING_COUNT);
         this.aux_crossings_ref = var1.newDirtyIntArrayRef(MarlinConst.INITIAL_CROSSING_COUNT);
         this.edgePtrs_ref = var1.newDirtyIntArrayRef(MarlinConst.INITIAL_CROSSING_COUNT);
         this.aux_edgePtrs_ref = var1.newDirtyIntArrayRef(MarlinConst.INITIAL_CROSSING_COUNT);
         this.blkFlags_ref = var1.newCleanIntArrayRef(256);
      }
   }
}
