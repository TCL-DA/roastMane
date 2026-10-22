package com.sun.marlin;

import sun.misc.Unsafe;

public final class RendererNoAA implements MarlinRenderer, MarlinConst {
   static final boolean DISABLE_RENDER = false;
   private static final int ALL_BUT_LSB = -2;
   private static final int ERR_STEP_MAX = Integer.MAX_VALUE;
   private static final double POWER_2_TO_32 = 4.2949673E9F;
   private static final double RDR_OFFSET_X = 0.5;
   private static final double RDR_OFFSET_Y = 0.5;
   public static final long OFF_CURX_OR = 0L;
   public static final long OFF_ERROR = 0L + (long)OffHeapArray.SIZE_INT;
   public static final long OFF_BUMP_X = OFF_ERROR + (long)OffHeapArray.SIZE_INT;
   public static final long OFF_BUMP_ERR = OFF_BUMP_X + (long)OffHeapArray.SIZE_INT;
   public static final long OFF_NEXT = OFF_BUMP_ERR + (long)OffHeapArray.SIZE_INT;
   public static final long OFF_YMAX = OFF_NEXT + (long)OffHeapArray.SIZE_INT;
   public static final int SIZEOF_EDGE_BYTES = (int)(OFF_YMAX + (long)OffHeapArray.SIZE_INT);
   private static final double CUB_DEC_ERR_SUBPIX = (double)MarlinProperties.getCubicDecD2() * 0.125;
   private static final double CUB_INC_ERR_SUBPIX = (double)MarlinProperties.getCubicIncD1() * 0.125;
   public static final double CUB_DEC_BND = 8.0 * CUB_DEC_ERR_SUBPIX;
   public static final double CUB_INC_BND = 8.0 * CUB_INC_ERR_SUBPIX;
   public static final int CUB_COUNT_LG = 2;
   private static final int CUB_COUNT = 4;
   private static final int CUB_COUNT_2 = 16;
   private static final int CUB_COUNT_3 = 64;
   private static final double CUB_INV_COUNT = 0.25;
   private static final double CUB_INV_COUNT_2 = 0.0625;
   private static final double CUB_INV_COUNT_3 = 0.015625;
   private static final double QUAD_DEC_ERR_SUBPIX = (double)MarlinProperties.getQuadDecD2() * 0.125;
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
      double var11 = Math.abs(var5.dbx) + Math.abs(var5.dby);
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
      double var28 = var1;

      for (double var30 = var3; var10 > 0; var3 = var30) {
         while (var10 % 2 == 0 && Math.abs(var15) + Math.abs(var17) <= var26) {
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

         while (Math.abs(var15) + Math.abs(var17) >= var24) {
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

         var28 += var19;
         var30 += var21;
         var19 += var15;
         var21 += var17;
         var15 += var11;
         var17 += var13;
         this.addLine(var1, var3, var28, var30);
         var1 = var28;
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

   RendererNoAA(RendererContext var1) {
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

   public RendererNoAA init(int var1, int var2, int var3, int var4, int var5) {
      this.windingRule = var5;
      this.boundsMinX = var1;
      this.boundsMaxX = var1 + var3;
      this.boundsMinY = var2;
      this.boundsMaxY = var2 + var4;
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
      return var0;
   }

   private static double tosubpixy(double var0) {
      return var0 - 0.5;
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
      int var30 = Integer.MAX_VALUE;
      int var31 = Integer.MIN_VALUE;
      int var32 = var1;
      int var33 = var1 - this.boundsMinY;
      int var34 = this.edgeCount;
      int var35 = var12.length;
      int var36 = var11.length;
      int var37 = this.activeEdgeMaxUsed;
      int var38 = 0;
      int[] var61 = this.blkFlags;
      int var62 = BLOCK_SIZE_LG;
      int var63 = BLOCK_SIZE;
      boolean var64 = ENABLE_BLOCK_FLAGS_HEURISTICS && this.enableBlkFlags;
      boolean var65 = this.prevUseBlkFlags;
      int var66 = this.rdrCtx.stroking;

      int var67;
      for (var67 = -1; var32 < var2; var33++) {
         int var41 = var10[var33];
         int var59 = var34;
         if (var41 != 0) {
            if (DO_STATS) {
               this.rdrCtx.stats.stat_rdr_activeEdges_updates.add(var34);
            }

            if ((var41 & 1) != 0) {
               long var28 = var26 + var23;
               int var42 = 0;

               int var39;
               for (var39 = 0; var42 < var34; var42++) {
                  int var44 = var12[var42];
                  if (var25.getInt(var28 + (long)var44) > var32) {
                     var12[var39++] = var44;
                  }
               }

               var34 = var39;
               var59 = var39;
            }

            var38 = var41 >> 1;
            if (var38 != 0) {
               if (DO_STATS) {
                  this.rdrCtx.stats.stat_rdr_activeEdges_adds.add(var38);
                  if (var38 > 10) {
                     this.rdrCtx.stats.stat_rdr_activeEdges_adds_high.add(var38);
                  }
               }

               int var40 = var34 + var38;
               if (var35 < var40) {
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_array_renderer_edgePtrs.add(var40);
                  }

                  this.edgePtrs = var12 = this.edgePtrs_ref.widenArray(var12, var34, var40);
                  var35 = var12.length;
                  this.aux_edgePtrs_ref.putArray(var14);
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_array_renderer_aux_edgePtrs.add(var40);
                  }

                  this.aux_edgePtrs = var14 = this.aux_edgePtrs_ref.getArray(ArrayCacheConst.getNewSize(var34, var40));
               }

               long var68 = var26 + var21;

               for (int var83 = var9[var33]; var34 < var40; var34++) {
                  var12[var34] = var83;
                  var83 = var25.getInt(var68 + (long)var83);
               }

               if (var36 < var34) {
                  this.crossings_ref.putArray(var11);
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_array_renderer_crossings.add(var34);
                  }

                  this.crossings = var11 = this.crossings_ref.getArray(var34);
                  this.aux_crossings_ref.putArray(var13);
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_array_renderer_aux_crossings.add(var34);
                  }

                  this.aux_crossings = var13 = this.aux_crossings_ref.getArray(var34);
                  var36 = var11.length;
               }

               if (DO_STATS && var34 > var37) {
                  var37 = var34;
               }
            }
         }

         if (var34 != 0) {
            if (var38 >= 10 && var34 >= 40) {
               if (DO_STATS) {
                  this.rdrCtx.stats.stat_rdr_crossings_msorts.add(var34);
                  this.rdrCtx.stats.hist_rdr_crossings_ratio.add(1000 * var38 / var34);
                  this.rdrCtx.stats.hist_rdr_crossings_msorts.add(var34);
                  this.rdrCtx.stats.hist_rdr_crossings_msorts_adds.add(var38);
               }

               int var87 = Integer.MIN_VALUE;

               for (int var78 = 0; var78 < var34; var78++) {
                  int var85 = var12[var78];
                  long var70 = var26 + (long)var85;
                  int var98 = var25.getInt(var70);
                  int var86 = var98;
                  var98 += var25.getInt(var70 + var17);
                  int var105 = var25.getInt(var70 + var15) + var25.getInt(var70 + var19);
                  var25.putInt(var70, var98 - (var105 >> 30 & -2));
                  var25.putInt(var70 + var15, var105 & 2147483647);
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_rdr_crossings_updates.add(var34);
                  }

                  if (var78 >= var59) {
                     var11[var78] = var98;
                  } else if (var98 >= var87) {
                     var87 = var98;
                     var13[var78] = var98;
                     var14[var78] = var85;
                  } else {
                     if (DO_STATS) {
                        this.rdrCtx.stats.stat_rdr_crossings_sorts.add(var78);
                     }

                     int var82 = var78 - 1;
                     var13[var78] = var13[var82];
                     var14[var78] = var14[var82];

                     while (true) {
                        var82--;
                        if (var82 < 0 || var13[var82] <= var86) {
                           var13[var82 + 1] = var86;
                           var14[var82 + 1] = var85;
                           break;
                        }

                        var13[var82 + 1] = var13[var82];
                        var14[var82 + 1] = var14[var82];
                     }
                  }
               }

               MergeSort.mergeSortNoCopy(var11, var12, var13, var14, var34, var59);
            } else {
               if (DO_STATS) {
                  this.rdrCtx.stats.hist_rdr_crossings.add(var34);
                  this.rdrCtx.stats.hist_rdr_crossings_adds.add(var38);
               }

               boolean var60 = var34 >= 20;
               int var46 = Integer.MIN_VALUE;

               for (int var77 = 0; var77 < var34; var77++) {
                  int var84 = var12[var77];
                  long var69 = var26 + (long)var84;
                  int var52 = var25.getInt(var69);
                  int var45 = var52;
                  var52 += var25.getInt(var69 + var17);
                  int var55 = var25.getInt(var69 + var15) + var25.getInt(var69 + var19);
                  var25.putInt(var69, var52 - (var55 >> 30 & -2));
                  var25.putInt(var69 + var15, var55 & 2147483647);
                  if (DO_STATS) {
                     this.rdrCtx.stats.stat_rdr_crossings_updates.add(var34);
                  }

                  if (var52 >= var46) {
                     var46 = var52;
                     var11[var77] = var52;
                  } else {
                     if (DO_STATS) {
                        this.rdrCtx.stats.stat_rdr_crossings_sorts.add(var77);
                     }

                     if (var60 && var77 >= var59) {
                        if (DO_STATS) {
                           this.rdrCtx.stats.stat_rdr_crossings_bsearch.add(var77);
                        }

                        int var56 = 0;
                        int var57 = var77 - 1;

                        do {
                           int var58 = var56 + var57 >> 1;
                           if (var11[var58] < var45) {
                              var56 = var58 + 1;
                           } else {
                              var57 = var58 - 1;
                           }
                        } while (var56 <= var57);

                        for (int var81 = var77 - 1; var81 >= var56; var81--) {
                           var11[var81 + 1] = var11[var81];
                           var12[var81 + 1] = var12[var81];
                        }

                        var11[var56] = var45;
                        var12[var56] = var84;
                     } else {
                        int var43 = var77 - 1;
                        var11[var77] = var11[var43];
                        var12[var77] = var12[var43];

                        while (true) {
                           var43--;
                           if (var43 < 0 || var11[var43] <= var45) {
                              var11[var43 + 1] = var45;
                              var12[var43 + 1] = var84;
                              break;
                           }

                           var11[var43 + 1] = var11[var43];
                           var12[var43 + 1] = var12[var43];
                        }
                     }
                  }
               }
            }

            var38 = 0;
            int var53 = var11[0];
            int var47 = var53 >> 1;
            if (var47 < var30) {
               var30 = var47;
            }

            int var48 = var11[var34 - 1] >> 1;
            if (var48 > var31) {
               var31 = var48;
            }

            int var100 = var47;
            int var51 = var47;
            int var54 = ((var53 & 1) << 1) - 1;
            if (var6) {
               int var96 = var54;

               for (int var80 = 1; var80 < var34; var80++) {
                  var53 = var11[var80];
                  var100 = var53 >> 1;
                  var54 = ((var53 & 1) << 1) - 1;
                  if ((var96 & 1) != 0) {
                     var47 = var51 > var4 ? var51 : var4;
                     if (var100 < var5) {
                        var48 = var100;
                     } else {
                        var48 = var5;
                        var80 = var34;
                     }

                     if (var47 < var48) {
                        var47 -= var4;
                        var48 -= var4;
                        var7[var47]++;
                        var7[var48]--;
                        if (var65) {
                           var61[var47 >> var62] = 1;
                           var61[var48 >> var62] = 1;
                        }
                     }
                  }

                  var96 += var54;
                  var51 = var100;
               }
            } else {
               int var79 = 1;
               int var50 = 0;

               while (true) {
                  var50 += var54;
                  if (var50 != 0) {
                     if (var51 > var100) {
                        var51 = var100;
                     }
                  } else {
                     var47 = var51 > var4 ? var51 : var4;
                     if (var100 < var5) {
                        var48 = var100;
                     } else {
                        var48 = var5;
                        var79 = var34;
                     }

                     if (var47 < var48) {
                        var47 -= var4;
                        var48 -= var4;
                        var7[var47]++;
                        var7[var48]--;
                        if (var65) {
                           var61[var47 >> var62] = 1;
                           var61[var48 >> var62] = 1;
                        }
                     }

                     var51 = Integer.MAX_VALUE;
                  }

                  if (var79 == var34) {
                     break;
                  }

                  var53 = var11[var79];
                  var100 = var53 >> 1;
                  var54 = ((var53 & 1) << 1) - 1;
                  var79++;
               }
            }
         }

         var67 = var32;
         var30 = FloatMath.max(var30, var4);
         var31 = FloatMath.min(var31, var5);
         if (var31 >= var30) {
            this.copyAARow(var7, var32, var30, var31 + 1, var65, var3);
            if (var64) {
               var31 -= var30;
               var65 = var31 > var63 && var31 > (var34 >> var66) - 1 << var62;
               if (DO_STATS) {
                  int var49 = FloatMath.max(1, (var34 >> var66) - 1);
                  this.rdrCtx.stats.hist_tile_generator_encoding_dist.add(var31 / var49);
               }
            }
         } else {
            var3.clearAlphas(var32);
         }

         var30 = Integer.MAX_VALUE;
         var31 = Integer.MIN_VALUE;
         var32++;
      }

      var32--;
      var30 = FloatMath.max(var30, var4);
      var31 = FloatMath.min(var31, var5);
      if (var31 >= var30) {
         this.copyAARow(var7, var32, var30, var31 + 1, var65, var3);
      } else if (var32 != var67) {
         var3.clearAlphas(var32);
      }

      this.edgeCount = var34;
      this.prevUseBlkFlags = var65;
      if (DO_STATS) {
         this.activeEdgeMaxUsed = var37;
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
            this.initConsumer(var1, var3, var2, var4);
            if (ENABLE_BLOCK_FLAGS) {
               this.enableBlkFlags = this.useRLE;
               this.prevUseBlkFlags = this.enableBlkFlags && !ENABLE_BLOCK_FLAGS_HEURISTICS;
               if (this.enableBlkFlags) {
                  int var9 = (var2 - var1 >> BLOCK_SIZE_LG) + 2;
                  if (var9 > 256) {
                     this.blkFlags = this.blkFlags_ref.getArray(var9);
                  }
               }
            }

            this.bbox_spminX = var1;
            this.bbox_spmaxX = var2;
            this.bbox_spminY = var3;
            this.bbox_spmaxY = var4;
            if (DO_LOG_BOUNDS) {
               MarlinUtils.logInfo("pXY       = [" + var1 + " ... " + var2 + "[ [" + var3 + " ... " + var4 + "[");
               MarlinUtils.logInfo(
                  "bbox_spXY = [" + this.bbox_spminX + " ... " + this.bbox_spmaxX + "[ [" + this.bbox_spminY + " ... " + this.bbox_spmaxY + "["
               );
            }

            int var10 = var2 - var1 + 2;
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
      var1.setMaxAlpha(1);
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
      return 0.5;
   }

   @Override
   public double getOffsetY() {
      return 0.5;
   }
}
