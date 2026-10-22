package com.sun.marlin;

import sun.misc.Unsafe;

public final class Renderer implements MarlinRenderer, MarlinConst {
   static final boolean DISABLE_RENDER = false;
   private static final int ALL_BUT_LSB = -2;
   private static final int ERR_STEP_MAX = Integer.MAX_VALUE;
   private static final double POWER_2_TO_32 = 4.2949673E9F;
   static final double SUBPIXEL_SCALE_X = (double)SUBPIXEL_POSITIONS_X;
   static final double SUBPIXEL_SCALE_Y = (double)SUBPIXEL_POSITIONS_Y;
   static final int SUBPIXEL_MASK_X = SUBPIXEL_POSITIONS_X - 1;
   static final int SUBPIXEL_MASK_Y = SUBPIXEL_POSITIONS_Y - 1;
   private static final double RDR_OFFSET_X = 0.5 / SUBPIXEL_SCALE_X;
   private static final double RDR_OFFSET_Y = 0.5 / SUBPIXEL_SCALE_Y;
   public static final long OFF_CURX_OR = 0L;
   public static final long OFF_ERROR = 0L + (long)OffHeapArray.SIZE_INT;
   public static final long OFF_BUMP_X = OFF_ERROR + (long)OffHeapArray.SIZE_INT;
   public static final long OFF_BUMP_ERR = OFF_BUMP_X + (long)OffHeapArray.SIZE_INT;
   public static final long OFF_NEXT = OFF_BUMP_ERR + (long)OffHeapArray.SIZE_INT;
   public static final long OFF_YMAX = OFF_NEXT + (long)OffHeapArray.SIZE_INT;
   public static final int SIZEOF_EDGE_BYTES = (int)(OFF_YMAX + (long)OffHeapArray.SIZE_INT);
   private static final double CUB_DEC_ERR_SUBPIX = (double)MarlinProperties.getCubicDecD2() * ((double)SUBPIXEL_POSITIONS_X / 8.0);
   private static final double CUB_INC_ERR_SUBPIX = (double)MarlinProperties.getCubicIncD1() * ((double)SUBPIXEL_POSITIONS_X / 8.0);
   public static final double SCALE_DY = (double)SUBPIXEL_POSITIONS_X / (double)SUBPIXEL_POSITIONS_Y;
   public static final double CUB_DEC_BND = 8.0 * CUB_DEC_ERR_SUBPIX;
   public static final double CUB_INC_BND = 8.0 * CUB_INC_ERR_SUBPIX;
   public static final int CUB_COUNT_LG = 2;
   private static final int CUB_COUNT = 4;
   private static final int CUB_COUNT_2 = 16;
   private static final int CUB_COUNT_3 = 64;
   private static final double CUB_INV_COUNT = 0.25;
   private static final double CUB_INV_COUNT_2 = 0.0625;
   private static final double CUB_INV_COUNT_3 = 0.015625;
   private static final double QUAD_DEC_ERR_SUBPIX = (double)MarlinProperties.getQuadDecD2() * ((double)SUBPIXEL_POSITIONS_X / 8.0);
   public static final double QUAD_DEC_BND = 8.0 * QUAD_DEC_ERR_SUBPIX;
   private int[] crossings;
   private int[] aux_crossings;
   private int edgeCount;
   private int[] edgePtrs;
   private int[] aux_edgePtrs;
   private int activeEdgeMaxUsed;
   private final IntArrayCache.Reference crossings_ref;
   private final IntArrayCache.Reference edgePtrs_ref;
   private final IntArrayCache.Reference aux_crossings_ref;
   private final IntArrayCache.Reference aux_edgePtrs_ref;
   private int edgeMinY = Integer.MAX_VALUE;
   private int edgeMaxY = Integer.MIN_VALUE;
   private double edgeMinX = Double.POSITIVE_INFINITY;
   private double edgeMaxX = Double.NEGATIVE_INFINITY;
   private final OffHeapArray edges;
   private int[] edgeBuckets;
   private int[] edgeBucketCounts;
   private int buckets_minY;
   private int buckets_maxY;
   private final IntArrayCache.Reference edgeBuckets_ref;
   private final IntArrayCache.Reference edgeBucketCounts_ref;
   boolean useRLE = false;
   private int boundsMinX;
   private int boundsMinY;
   private int boundsMaxX;
   private int boundsMaxY;
   private int windingRule;
   private double x0;
   private double y0;
   private double sx0;
   private double sy0;
   final RendererContext rdrCtx;
   private final Curve curve;
   private int[] alphaLine;
   private final IntArrayCache.Reference alphaLine_ref;
   private boolean enableBlkFlags = false;
   private boolean prevUseBlkFlags = false;
   private int[] blkFlags;
   private final IntArrayCache.Reference blkFlags_ref;
   private int bbox_spminX;
   private int bbox_spmaxX;
   private int bbox_spminY;
   private int bbox_spmaxY;
   int bboxX0;
   int bboxX1;
   int bboxY0;
   int bboxY1;

   private void quadBreakIntoLinesAndAdd(double var1, double var3, Curve var5, double var6, double var8) {
      int var10 = 1;
      double var11 = Math.abs(var5.dbx) + Math.abs(var5.dby) * SCALE_DY;
      double var13 = QUAD_DEC_BND;

      while (var11 >= var13) {
         var11 /= 4.0;
         var10 <<= 1;
         if (DO_STATS) {
            this.rdrCtx.stats.stat_rdr_quadBreak_dec.add(var10);
         }
      }

      int var15 = var10;
      if (var10 > 1) {
         double var16 = 1.0 / (double)var10;
         double var18 = var16 * var16;
         double var20 = var5.dbx * var18;
         double var22 = var5.dby * var18;
         double var24 = var5.bx * var18 + var5.cx * var16;
         double var26 = var5.by * var18 + var5.cy * var16;
         double var28 = var1;

         for (double var30 = var3; --var10 > 0; var26 += var22) {
            var28 += var24;
            var30 += var26;
            this.addLine(var1, var3, var28, var30);
            var1 = var28;
            var3 = var30;
            var24 += var20;
         }
      }

      this.addLine(var1, var3, var6, var8);
      if (DO_STATS) {
         this.rdrCtx.stats.stat_rdr_quadBreak.add(var15);
      }
   }

   private void curveBreakIntoLinesAndAdd(double var1, double var3, Curve var5, double var6, double var8) {
      int var10 = 4;
      double var11 = 2.0 * var5.dax * 0.015625;
      double var13 = 2.0 * var5.day * 0.015625;
      double var15 = var11 + var5.dbx * 0.0625;
      double var17 = var13 + var5.dby * 0.0625;
      double var19 = var5.ax * 0.015625 + var5.bx * 0.0625 + var5.cx * 0.25;
      double var21 = var5.ay * 0.015625 + var5.by * 0.0625 + var5.cy * 0.25;
      byte var23 = 0;
      double var24 = CUB_DEC_BND;
      double var26 = CUB_INC_BND;
      double var28 = SCALE_DY;
      double var30 = var1;

      for (double var32 = var3; var10 > 0; var3 = var32) {
         while (var10 % 2 == 0 && Math.abs(var15) + Math.abs(var17) * var28 <= var26) {
            var19 = 2.0 * var19 + var15;
            var21 = 2.0 * var21 + var17;
            var15 = 4.0 * (var15 + var11);
            var17 = 4.0 * (var17 + var13);
            var11 *= 8.0;
            var13 *= 8.0;
            var10 >>= 1;
            if (DO_STATS) {
               this.rdrCtx.stats.stat_rdr_curveBreak_inc.add(var10);
            }
         }

         while (Math.abs(var15) + Math.abs(var17) * var28 >= var24) {
            var11 /= 8.0;
            var13 /= 8.0;
            var15 = var15 / 4.0 - var11;
            var17 = var17 / 4.0 - var13;
            var19 = (var19 - var15) / 2.0;
            var21 = (var21 - var17) / 2.0;
            var10 <<= 1;
            if (DO_STATS) {
               this.rdrCtx.stats.stat_rdr_curveBreak_dec.add(var10);
            }
         }

         if (--var10 == 0) {
            break;
         }

         var30 += var19;
         var32 += var21;
         var19 += var15;
         var21 += var17;
         var15 += var11;
         var17 += var13;
         this.addLine(var1, var3, var30, var32);
         var1 = var30;
      }

      this.addLine(var1, var3, var6, var8);
      if (DO_STATS) {
         this.rdrCtx.stats.stat_rdr_curveBreak.add(var23 + 1);
      }
   }

   private void addLine(double var1, double var3, double var5, double var7) {
      if (DO_STATS) {
         this.rdrCtx.stats.stat_rdr_addLine.add(1);
      }

      byte var9 = 1;
      if (var7 < var3) {
         var9 = 0;
         double var10 = var7;
         var7 = var3;
         var3 = var10;
         var10 = var5;
         var5 = var1;
         var1 = var10;
      }

      int var31 = FloatMath.max(FloatMath.ceil_int(var3), this.boundsMinY);
      int var11 = FloatMath.min(FloatMath.ceil_int(var7), this.boundsMaxY);
      if (var31 >= var11) {
         if (DO_STATS) {
            this.rdrCtx.stats.stat_rdr_addLine_skip.add(1);
         }
      } else {
         if (var31 < this.edgeMinY) {
            this.edgeMinY = var31;
         }

         if (var11 > this.edgeMaxY) {
            this.edgeMaxY = var11;
         }

         double var12 = (var1 - var5) / (var3 - var7);
         if (var12 >= 0.0) {
            if (var1 < this.edgeMinX) {
               this.edgeMinX = var1;
            }

            if (var5 > this.edgeMaxX) {
               this.edgeMaxX = var5;
            }
         } else {
            if (var5 < this.edgeMinX) {
               this.edgeMinX = var5;
            }

            if (var1 > this.edgeMaxX) {
               this.edgeMaxX = var1;
            }
         }

         int var14 = SIZEOF_EDGE_BYTES;
         OffHeapArray var15 = this.edges;
         int var16 = var15.used;
         if (var15.length - (long)var16 < (long)var14) {
            long var17 = ArrayCacheConst.getNewLargeSize(var15.length, (long)(var16 + var14));
            if (DO_STATS) {
               this.rdrCtx.stats.stat_rdr_edges_resizes.add(var17);
            }

            var15.resize(var17);
         }

         Unsafe var32 = OffHeapArray.UNSAFE;
         long var18 = var15.address + (long)var16;
         double var20 = var1 + ((double)var31 - var3) * var12;
         long var22 = (long)(4.2949673E9F * var20) + 2147483647L;
         var32.putInt(var18, (int)(var22 >> 31) & -2 | var9);
         var18 += 4L;
         var32.putInt(var18, (int)var22 >>> 1);
         var18 += 4L;
         long var24 = (long)(4.2949673E9F * var12);
         var32.putInt(var18, (int)(var24 >> 31) & -2);
         var18 += 4L;
         var32.putInt(var18, (int)var24 >>> 1);
         var18 += 4L;
         int[] var26 = this.edgeBuckets;
         int[] var27 = this.edgeBucketCounts;
         int var28 = this.boundsMinY;
         int var29 = var31 - var28;
         var32.putInt(var18, var26[var29]);
         var18 += 4L;
         var32.putInt(var18, var11);
         var26[var29] = var16;
         var27[var29] += 2;
         var27[var11 - var28] = var27[var11 - var28] | 1;
         var15.used += var14;
      }
   }

   Renderer(RendererContext var1) {
      this.rdrCtx = var1;
      this.curve = var1.curve;
      this.edges = var1.rdrMem.edges;
      this.edgeBuckets_ref = var1.rdrMem.edgeBuckets_ref;
      this.edgeBucketCounts_ref = var1.rdrMem.edgeBucketCounts_ref;
      this.edgeBuckets = this.edgeBuckets_ref.initial;
      this.edgeBucketCounts = this.edgeBucketCounts_ref.initial;
      this.alphaLine_ref = var1.rdrMem.alphaLine_ref;
      this.alphaLine = this.alphaLine_ref.initial;
      this.crossings_ref = var1.rdrMem.crossings_ref;
      this.aux_crossings_ref = var1.rdrMem.aux_crossings_ref;
      this.edgePtrs_ref = var1.rdrMem.edgePtrs_ref;
      this.aux_edgePtrs_ref = var1.rdrMem.aux_edgePtrs_ref;
      this.crossings = this.crossings_ref.initial;
      this.aux_crossings = this.aux_crossings_ref.initial;
      this.edgePtrs = this.edgePtrs_ref.initial;
      this.aux_edgePtrs = this.aux_edgePtrs_ref.initial;
      this.blkFlags_ref = var1.rdrMem.blkFlags_ref;
      this.blkFlags = this.blkFlags_ref.initial;
   }

   public Renderer init(int var1, int var2, int var3, int var4, int var5) {
      this.windingRule = var5;
      this.boundsMinX = var1 << SUBPIXEL_LG_POSITIONS_X;
      this.boundsMaxX = var1 + var3 << SUBPIXEL_LG_POSITIONS_X;
      this.boundsMinY = var2 << SUBPIXEL_LG_POSITIONS_Y;
      this.boundsMaxY = var2 + var4 << SUBPIXEL_LG_POSITIONS_Y;
      if (DO_LOG_BOUNDS) {
         MarlinUtils.logInfo("boundsXY = [" + this.boundsMinX + " ... " + this.boundsMaxX + "[ [" + this.boundsMinY + " ... " + this.boundsMaxY + "[");
      }

      int var6 = this.boundsMaxY - this.boundsMinY + 1;
      if (var6 > INITIAL_BUCKET_ARRAY) {
         if (DO_STATS) {
            this.rdrCtx.stats.stat_array_renderer_edgeBuckets.add(var6);
            this.rdrCtx.stats.stat_array_renderer_edgeBucketCounts.add(var6);
         }

         this.edgeBuckets = this.edgeBuckets_ref.getArray(var6);
         this.edgeBucketCounts = this.edgeBucketCounts_ref.getArray(var6);
      }

      this.edgeMinY = Integer.MAX_VALUE;
      this.edgeMaxY = Integer.MIN_VALUE;
      this.edgeMinX = Double.POSITIVE_INFINITY;
      this.edgeMaxX = Double.NEGATIVE_INFINITY;
      this.edgeCount = 0;
      this.activeEdgeMaxUsed = 0;
      this.edges.used = 0;
      this.bboxX0 = 0;
      this.bboxX1 = 0;
      return this;
   }

   @Override
   public void dispose() {
      if (DO_STATS) {
         this.rdrCtx.stats.stat_rdr_activeEdges.add(this.activeEdgeMaxUsed);
         this.rdrCtx.stats.stat_rdr_edges.add(this.edges.used);
         this.rdrCtx.stats.stat_rdr_edges_count.add(this.edges.used / SIZEOF_EDGE_BYTES);
         this.rdrCtx.stats.hist_rdr_edges_count.add(this.edges.used / SIZEOF_EDGE_BYTES);
         this.rdrCtx.stats.totalOffHeap = this.rdrCtx.stats.totalOffHeap + this.edges.length;
      }

      this.crossings = this.crossings_ref.putArray(this.crossings);
      this.aux_crossings = this.aux_crossings_ref.putArray(this.aux_crossings);
      this.edgePtrs = this.edgePtrs_ref.putArray(this.edgePtrs);
      this.aux_edgePtrs = this.aux_edgePtrs_ref.putArray(this.aux_edgePtrs);
      this.alphaLine = this.alphaLine_ref.putArray(this.alphaLine, 0, 0);
      this.blkFlags = this.blkFlags_ref.putArray(this.blkFlags, 0, 0);
      if (this.edgeMinY != Integer.MAX_VALUE) {
         if (this.rdrCtx.dirty) {
            this.buckets_minY = 0;
            this.buckets_maxY = this.boundsMaxY - this.boundsMinY;
         }

         this.edgeBuckets = this.edgeBuckets_ref.putArray(this.edgeBuckets, this.buckets_minY, this.buckets_maxY);
         this.edgeBucketCounts = this.edgeBucketCounts_ref.putArray(this.edgeBucketCounts, this.buckets_minY, this.buckets_maxY + 1);
      } else {
         this.edgeBuckets = this.edgeBuckets_ref.putArray(this.edgeBuckets, 0, 0);
         this.edgeBucketCounts = this.edgeBucketCounts_ref.putArray(this.edgeBucketCounts, 0, 0);
      }

      if (this.edges.length != (long)INITIAL_EDGES_CAPACITY) {
         this.edges.resize((long)INITIAL_EDGES_CAPACITY);
      }
   }

   private static double tosubpixx(double var0) {
      return SUBPIXEL_SCALE_X * var0;
   }

   private static double tosubpixy(double var0) {
      return SUBPIXEL_SCALE_Y * var0 - 0.5;
   }

   @Override
   public void moveTo(double var1, double var3) {
      this.closePath();
      double var5 = tosubpixx(var1);
      double var7 = tosubpixy(var3);
      this.sx0 = var5;
      this.sy0 = var7;
      this.x0 = var5;
      this.y0 = var7;
   }

   @Override
   public void lineTo(double var1, double var3) {
      double var5 = tosubpixx(var1);
      double var7 = tosubpixy(var3);
      this.addLine(this.x0, this.y0, var5, var7);
      this.x0 = var5;
      this.y0 = var7;
   }

   @Override
   public void curveTo(double var1, double var3, double var5, double var7, double var9, double var11) {
      double var13 = tosubpixx(var9);
      double var15 = tosubpixy(var11);
      this.curve.set(this.x0, this.y0, tosubpixx(var1), tosubpixy(var3), tosubpixx(var5), tosubpixy(var7), var13, var15);
      this.curveBreakIntoLinesAndAdd(this.x0, this.y0, this.curve, var13, var15);
      this.x0 = var13;
      this.y0 = var15;
   }

   @Override
   public void quadTo(double var1, double var3, double var5, double var7) {
      double var9 = tosubpixx(var5);
      double var11 = tosubpixy(var7);
      this.curve.set(this.x0, this.y0, tosubpixx(var1), tosubpixy(var3), var9, var11);
      this.quadBreakIntoLinesAndAdd(this.x0, this.y0, this.curve, var9, var11);
      this.x0 = var9;
      this.y0 = var11;
   }

   @Override
   public void closePath() {
      if (this.x0 != this.sx0 || this.y0 != this.sy0) {
         this.addLine(this.x0, this.y0, this.sx0, this.sy0);
         this.x0 = this.sx0;
         this.y0 = this.sy0;
      }
   }

   @Override
   public void pathDone() {
      this.closePath();
      this.endRendering();
   }

   private void _endRendering(int var1, int var2, MarlinAlphaConsumer var3) {
      int var4 = this.bbox_spminX;
      int var5 = this.bbox_spmaxX;
      boolean var6 = this.windingRule == 0;
      int[] var7 = this.alphaLine;
      OffHeapArray var8 = this.edges;
      int[] var9 = this.edgeBuckets;
      int[] var10 = this.edgeBucketCounts;
      int[] var11 = this.crossings;
      int[] var12 = this.edgePtrs;
      int[] var13 = this.aux_crossings;
      int[] var14 = this.aux_edgePtrs;
      long var15 = OFF_ERROR;
      long var17 = OFF_BUMP_X;
      long var19 = OFF_BUMP_ERR;
      long var21 = OFF_NEXT;
      long var23 = OFF_YMAX;
      Unsafe var25 = OffHeapArray.UNSAFE;
      long var26 = var8.address;
      int var30 = SUBPIXEL_LG_POSITIONS_X;
      int var31 = SUBPIXEL_LG_POSITIONS_Y;
      int var32 = SUBPIXEL_MASK_X;
      int var33 = SUBPIXEL_MASK_Y;
      int var34 = SUBPIXEL_POSITIONS_X;
      int var35 = Integer.MAX_VALUE;
      int var36 = Integer.MIN_VALUE;
      int var37 = var1;
      int var38 = var1 - this.boundsMinY;
      int var39 = this.edgeCount;
      int var40 = var12.length;
      int var41 = var11.length;
      int var42 = this.activeEdgeMaxUsed;
      int var43 = 0;
      int[] var69 = this.blkFlags;
      int var70 = BLOCK_SIZE_LG;
      int var71 = BLOCK_SIZE;
      boolean var72 = ENABLE_BLOCK_FLAGS_HEURISTICS && this.enableBlkFlags;
      boolean var73 = this.prevUseBlkFlags;
      int var74 = this.rdrCtx.stroking;

      int var75;
      for (var75 = -1; var37 < var2; var38++) {
         int var46 = var10[var38];
         int var67 = var39;
         if (var46 != 0) {
            if (DO_STATS) {
               this.rdrCtx.stats.stat_rdr_activeEdges_updates.add(var39);
            }

            if ((var46 & 1) != 0) {
               long var28 = var26 + var23;
               int var47 = 0;

               int var44;
               for (var44 = 0; var47 < var39; var47++) {
                  int var49 = var12[var47];
                  if (var25.getInt(var28 + (long)var49) > var37) {
                     var12[var44++] = var49;
                  }
               }

               var39 = var44;
               var67 = var44;
            }

            var43 = var46 >> 1;
            if (var43 != 0) {
               if (DO_STATS) {
                  this.rdrCtx.stats.stat_rdr_activeEdges_adds.add(var43);
                  if (var43 > 10) {
                     this.rdrCtx.stats.stat_rdr_activeEdges_adds_high.add(var43);
                  }
               }

               int var45 = var39 + var43;
               if (var40 < var45) {
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_array_renderer_edgePtrs.add(var45);
                  }

                  this.edgePtrs = var12 = this.edgePtrs_ref.widenArray(var12, var39, var45);
                  var40 = var12.length;
                  this.aux_edgePtrs_ref.putArray(var14);
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_array_renderer_aux_edgePtrs.add(var45);
                  }

                  this.aux_edgePtrs = var14 = this.aux_edgePtrs_ref.getArray(ArrayCacheConst.getNewSize(var39, var45));
               }

               long var76 = var26 + var21;

               for (int var92 = var9[var38]; var39 < var45; var39++) {
                  var12[var39] = var92;
                  var92 = var25.getInt(var76 + (long)var92);
               }

               if (var41 < var39) {
                  this.crossings_ref.putArray(var11);
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_array_renderer_crossings.add(var39);
                  }

                  this.crossings = var11 = this.crossings_ref.getArray(var39);
                  this.aux_crossings_ref.putArray(var13);
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_array_renderer_aux_crossings.add(var39);
                  }

                  this.aux_crossings = var13 = this.aux_crossings_ref.getArray(var39);
                  var41 = var11.length;
               }

               if (DO_STATS && var39 > var42) {
                  var42 = var39;
               }
            }
         }

         if (var39 != 0) {
            if (var43 >= 10 && var39 >= 40) {
               if (DO_STATS) {
                  this.rdrCtx.stats.stat_rdr_crossings_msorts.add(var39);
                  this.rdrCtx.stats.hist_rdr_crossings_ratio.add(1000 * var43 / var39);
                  this.rdrCtx.stats.hist_rdr_crossings_msorts.add(var39);
                  this.rdrCtx.stats.hist_rdr_crossings_msorts_adds.add(var43);
               }

               int var96 = Integer.MIN_VALUE;

               for (int var87 = 0; var87 < var39; var87++) {
                  int var94 = var12[var87];
                  long var78 = var26 + (long)var94;
                  int var113 = var25.getInt(var78);
                  int var95 = var113;
                  var113 += var25.getInt(var78 + var17);
                  int var120 = var25.getInt(var78 + var15) + var25.getInt(var78 + var19);
                  var25.putInt(var78, var113 - (var120 >> 30 & -2));
                  var25.putInt(var78 + var15, var120 & 2147483647);
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_rdr_crossings_updates.add(var39);
                  }

                  if (var87 >= var67) {
                     var11[var87] = var113;
                  } else if (var113 >= var96) {
                     var96 = var113;
                     var13[var87] = var113;
                     var14[var87] = var94;
                  } else {
                     if (DO_STATS) {
                        this.rdrCtx.stats.stat_rdr_crossings_sorts.add(var87);
                     }

                     int var91 = var87 - 1;
                     var13[var87] = var13[var91];
                     var14[var87] = var14[var91];

                     while (true) {
                        var91--;
                        if (var91 < 0 || var13[var91] <= var95) {
                           var13[var91 + 1] = var95;
                           var14[var91 + 1] = var94;
                           break;
                        }

                        var13[var91 + 1] = var13[var91];
                        var14[var91 + 1] = var14[var91];
                     }
                  }
               }

               MergeSort.mergeSortNoCopy(var11, var12, var13, var14, var39, var67);
            } else {
               if (DO_STATS) {
                  this.rdrCtx.stats.hist_rdr_crossings.add(var39);
                  this.rdrCtx.stats.hist_rdr_crossings_adds.add(var43);
               }

               boolean var68 = var39 >= 20;
               int var51 = Integer.MIN_VALUE;

               for (int var86 = 0; var86 < var39; var86++) {
                  int var93 = var12[var86];
                  long var77 = var26 + (long)var93;
                  int var57 = var25.getInt(var77);
                  int var50 = var57;
                  var57 += var25.getInt(var77 + var17);
                  int var60 = var25.getInt(var77 + var15) + var25.getInt(var77 + var19);
                  var25.putInt(var77, var57 - (var60 >> 30 & -2));
                  var25.putInt(var77 + var15, var60 & 2147483647);
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_rdr_crossings_updates.add(var39);
                  }

                  if (var57 >= var51) {
                     var51 = var57;
                     var11[var86] = var57;
                  } else {
                     if (DO_STATS) {
                        this.rdrCtx.stats.stat_rdr_crossings_sorts.add(var86);
                     }

                     if (var68 && var86 >= var67) {
                        if (DO_STATS) {
                           this.rdrCtx.stats.stat_rdr_crossings_bsearch.add(var86);
                        }

                        int var64 = 0;
                        int var65 = var86 - 1;

                        do {
                           int var66 = var64 + var65 >> 1;
                           if (var11[var66] < var50) {
                              var64 = var66 + 1;
                           } else {
                              var65 = var66 - 1;
                           }
                        } while (var64 <= var65);

                        for (int var90 = var86 - 1; var90 >= var64; var90--) {
                           var11[var90 + 1] = var11[var90];
                           var12[var90 + 1] = var12[var90];
                        }

                        var11[var64] = var50;
                        var12[var64] = var93;
                     } else {
                        int var48 = var86 - 1;
                        var11[var86] = var11[var48];
                        var12[var86] = var12[var48];

                        while (true) {
                           var48--;
                           if (var48 < 0 || var11[var48] <= var50) {
                              var11[var48 + 1] = var50;
                              var12[var48 + 1] = var93;
                              break;
                           }

                           var11[var48 + 1] = var11[var48];
                           var12[var48 + 1] = var12[var48];
                        }
                     }
                  }
               }
            }

            var43 = 0;
            int var58 = var11[0];
            int var52 = var58 >> 1;
            if (var52 < var35) {
               var35 = var52;
            }

            int var53 = var11[var39 - 1] >> 1;
            if (var53 > var36) {
               var36 = var53;
            }

            int var115 = var52;
            int var56 = var52;
            int var59 = ((var58 & 1) << 1) - 1;
            if (var6) {
               int var111 = var59;

               for (int var89 = 1; var89 < var39; var89++) {
                  var58 = var11[var89];
                  var115 = var58 >> 1;
                  var59 = ((var58 & 1) << 1) - 1;
                  if ((var111 & 1) != 0) {
                     var52 = var56 > var4 ? var56 : var4;
                     if (var115 < var5) {
                        var53 = var115;
                     } else {
                        var53 = var5;
                        var89 = var39;
                     }

                     if (var52 < var53) {
                        var52 -= var4;
                        var53 -= var4;
                        int var121 = var52 >> var30;
                        int var122 = var53 - 1 >> var30;
                        if (var121 == var122) {
                           int var107 = var53 - var52;
                           var7[var121] += var107;
                           var7[var121 + 1] = var7[var121 + 1] - var107;
                           if (var73) {
                              var69[var121 >> var70] = 1;
                           }
                        } else {
                           int var108 = var52 & var32;
                           var7[var121] += var34 - var108;
                           var7[var121 + 1] = var7[var121 + 1] + var108;
                           int var123 = var53 >> var30;
                           var108 = var53 & var32;
                           var7[var123] -= var34 - var108;
                           var7[var123 + 1] = var7[var123 + 1] - var108;
                           if (var73) {
                              var69[var121 >> var70] = 1;
                              var69[var123 >> var70] = 1;
                           }
                        }
                     }
                  }

                  var111 += var59;
                  var56 = var115;
               }
            } else {
               int var88 = 1;
               int var55 = 0;

               while (true) {
                  var55 += var59;
                  if (var55 != 0) {
                     if (var56 > var115) {
                        var56 = var115;
                     }
                  } else {
                     var52 = var56 > var4 ? var56 : var4;
                     if (var115 < var5) {
                        var53 = var115;
                     } else {
                        var53 = var5;
                        var88 = var39;
                     }

                     if (var52 < var53) {
                        var52 -= var4;
                        var53 -= var4;
                        int var61 = var52 >> var30;
                        int var62 = var53 - 1 >> var30;
                        if (var61 == var62) {
                           int var54 = var53 - var52;
                           var7[var61] += var54;
                           var7[var61 + 1] = var7[var61 + 1] - var54;
                           if (var73) {
                              var69[var61 >> var70] = 1;
                           }
                        } else {
                           int var105 = var52 & var32;
                           var7[var61] += var34 - var105;
                           var7[var61 + 1] = var7[var61 + 1] + var105;
                           int var63 = var53 >> var30;
                           var105 = var53 & var32;
                           var7[var63] -= var34 - var105;
                           var7[var63 + 1] = var7[var63 + 1] - var105;
                           if (var73) {
                              var69[var61 >> var70] = 1;
                              var69[var63 >> var70] = 1;
                           }
                        }
                     }

                     var56 = Integer.MAX_VALUE;
                  }

                  if (var88 == var39) {
                     break;
                  }

                  var58 = var11[var88];
                  var115 = var58 >> 1;
                  var59 = ((var58 & 1) << 1) - 1;
                  var88++;
               }
            }
         }

         if ((var37 & var33) == var33) {
            var75 = var37 >> var31;
            var35 = FloatMath.max(var35, var4) >> var30;
            var36 = FloatMath.min(var36, var5) >> var30;
            if (var36 >= var35) {
               this.copyAARow(var7, var75, var35, var36 + 1, var73, var3);
               if (var72) {
                  var36 -= var35;
                  var73 = var36 > var71 && var36 > (var39 >> var74) - 1 << var70;
                  if (DO_STATS) {
                     int var110 = FloatMath.max(1, (var39 >> var74) - 1);
                     this.rdrCtx.stats.hist_tile_generator_encoding_dist.add(var36 / var110);
                  }
               }
            } else {
               var3.clearAlphas(var75);
            }

            var35 = Integer.MAX_VALUE;
            var36 = Integer.MIN_VALUE;
         }

         var37++;
      }

      var37 = --var37 >> var31;
      var35 = FloatMath.max(var35, var4) >> var30;
      var36 = FloatMath.min(var36, var5) >> var30;
      if (var36 >= var35) {
         this.copyAARow(var7, var37, var35, var36 + 1, var73, var3);
      } else if (var37 != var75) {
         var3.clearAlphas(var37);
      }

      this.edgeCount = var39;
      this.prevUseBlkFlags = var73;
      if (DO_STATS) {
         this.activeEdgeMaxUsed = var42;
      }
   }

   void endRendering() {
      if (this.edgeMinY != Integer.MAX_VALUE) {
         int var1 = FloatMath.max(FloatMath.ceil_int(this.edgeMinX - 0.5), this.boundsMinX);
         int var2 = FloatMath.min(FloatMath.ceil_int(this.edgeMaxX - 0.5), this.boundsMaxX);
         int var3 = this.edgeMinY;
         int var4 = this.edgeMaxY;
         this.buckets_minY = var3 - this.boundsMinY;
         this.buckets_maxY = var4 - this.boundsMinY;
         if (DO_LOG_BOUNDS) {
            MarlinUtils.logInfo("edgesXY = [" + this.edgeMinX + " ... " + this.edgeMaxX + "[ [" + this.edgeMinY + " ... " + this.edgeMaxY + "[");
            MarlinUtils.logInfo("spXY    = [" + var1 + " ... " + var2 + "[ [" + var3 + " ... " + var4 + "[");
         }

         if (var1 < var2 && var3 < var4) {
            int var5 = var1 >> SUBPIXEL_LG_POSITIONS_X;
            int var6 = var2 + SUBPIXEL_MASK_X >> SUBPIXEL_LG_POSITIONS_X;
            int var7 = var3 >> SUBPIXEL_LG_POSITIONS_Y;
            int var8 = var4 + SUBPIXEL_MASK_Y >> SUBPIXEL_LG_POSITIONS_Y;
            this.initConsumer(var5, var7, var6, var8);
            if (ENABLE_BLOCK_FLAGS) {
               this.enableBlkFlags = this.useRLE;
               this.prevUseBlkFlags = this.enableBlkFlags && !ENABLE_BLOCK_FLAGS_HEURISTICS;
               if (this.enableBlkFlags) {
                  int var9 = (var6 - var5 >> BLOCK_SIZE_LG) + 2;
                  if (var9 > 256) {
                     this.blkFlags = this.blkFlags_ref.getArray(var9);
                  }
               }
            }

            this.bbox_spminX = var5 << SUBPIXEL_LG_POSITIONS_X;
            this.bbox_spmaxX = var6 << SUBPIXEL_LG_POSITIONS_X;
            this.bbox_spminY = var3;
            this.bbox_spmaxY = var4;
            if (DO_LOG_BOUNDS) {
               MarlinUtils.logInfo("pXY       = [" + var5 + " ... " + var6 + "[ [" + var7 + " ... " + var8 + "[");
               MarlinUtils.logInfo(
                  "bbox_spXY = [" + this.bbox_spminX + " ... " + this.bbox_spmaxX + "[ [" + this.bbox_spminY + " ... " + this.bbox_spmaxY + "["
               );
            }

            int var10 = var6 - var5 + 2;
            if (var10 > INITIAL_AA_ARRAY) {
               if (DO_STATS) {
                  this.rdrCtx.stats.stat_array_renderer_alphaline.add(var10);
               }

               this.alphaLine = this.alphaLine_ref.getArray(var10);
            }
         }
      }
   }

   void initConsumer(int var1, int var2, int var3, int var4) {
      this.bboxX0 = var1;
      this.bboxX1 = var3;
      this.bboxY0 = var2;
      this.bboxY1 = var4;
      int var5 = var3 - var1;
      if (FORCE_NO_RLE) {
         this.useRLE = false;
      } else if (FORCE_RLE) {
         this.useRLE = true;
      } else {
         this.useRLE = var5 > RLE_MIN_WIDTH;
      }
   }

   @Override
   public void produceAlphas(MarlinAlphaConsumer var1) {
      var1.setMaxAlpha(MAX_AA_ALPHA);
      if (this.enableBlkFlags && !var1.supportBlockFlags()) {
         this.enableBlkFlags = false;
         this.prevUseBlkFlags = false;
      }

      this._endRendering(this.bbox_spminY, this.bbox_spmaxY, var1);
   }

   void copyAARow(int[] var1, int var2, int var3, int var4, boolean var5, MarlinAlphaConsumer var6) {
      if (DO_STATS) {
         this.rdrCtx.stats.stat_cache_rowAA.add(var4 - var3);
      }

      if (var5) {
         if (DO_STATS) {
            this.rdrCtx.stats.hist_tile_generator_encoding.add(1);
         }

         var6.setAndClearRelativeAlphas(this.blkFlags, var1, var2, var3, var4);
      } else {
         if (DO_STATS) {
            this.rdrCtx.stats.hist_tile_generator_encoding.add(0);
         }

         var6.setAndClearRelativeAlphas(var1, var2, var3, var4);
      }
   }

   @Override
   public int getOutpixMinX() {
      return this.bboxX0;
   }

   @Override
   public int getOutpixMaxX() {
      return this.bboxX1;
   }

   @Override
   public int getOutpixMinY() {
      return this.bboxY0;
   }

   @Override
   public int getOutpixMaxY() {
      return this.bboxY1;
   }

   @Override
   public double getOffsetX() {
      return RDR_OFFSET_X;
   }

   @Override
   public double getOffsetY() {
      return RDR_OFFSET_Y;
   }
}
