package com.sun.marlin;

public interface MarlinConst {
   boolean ENABLE_LOGS = MarlinProperties.isLoggingEnabled();
   boolean USE_LOGGER = ENABLE_LOGS && MarlinProperties.isUseLogger();
   boolean LOG_CREATE_CONTEXT = ENABLE_LOGS && MarlinProperties.isLogCreateContext();
   boolean LOG_UNSAFE_MALLOC = ENABLE_LOGS && MarlinProperties.isLogUnsafeMalloc();
   boolean DO_CHECK_UNSAFE = false;
   boolean DO_STATS = ENABLE_LOGS && MarlinProperties.isDoStats();
   boolean DO_MONITORS = false;
   boolean DO_CHECKS = ENABLE_LOGS && MarlinProperties.isDoChecks();
   boolean DO_AA_RANGE_CHECK = false;
   boolean DO_LOG_WIDEN_ARRAY;
   boolean DO_LOG_OVERSIZE;
   boolean DO_TRACE;
   boolean DO_FLUSH_STATS = true;
   boolean DO_FLUSH_MONITORS = true;
   boolean USE_DUMP_THREAD = false;
   long DUMP_INTERVAL = 5000L;
   boolean DO_CLEAN_DIRTY = false;
   boolean USE_SIMPLIFIER;
   boolean USE_PATH_SIMPLIFIER;
   boolean DO_CLIP_SUBDIVIDER;
   boolean DO_LOG_BOUNDS;
   boolean DO_LOG_CLIP;
   int INITIAL_PIXEL_WIDTH;
   int INITIAL_PIXEL_HEIGHT;
   int INITIAL_ARRAY = 256;
   int INITIAL_AA_ARRAY;
   int INITIAL_EDGES_COUNT;
   int INITIAL_EDGES_CAPACITY;
   int INITIAL_CROSSING_COUNT;
   byte BYTE_0 = 0;
   int SUBPIXEL_LG_POSITIONS_X;
   int SUBPIXEL_LG_POSITIONS_Y;
   int MIN_SUBPIXEL_LG_POSITIONS;
   int SUBPIXEL_POSITIONS_X;
   int SUBPIXEL_POSITIONS_Y;
   float MIN_SUBPIXELS;
   int INITIAL_BUCKET_ARRAY;
   int MAX_AA_ALPHA;
   int BLOCK_SIZE_LG;
   int BLOCK_SIZE;
   boolean ENABLE_BLOCK_FLAGS;
   boolean ENABLE_BLOCK_FLAGS_HEURISTICS;
   boolean FORCE_RLE;
   boolean FORCE_NO_RLE;
   int RLE_MIN_WIDTH;
   int WIND_EVEN_ODD = 0;
   int WIND_NON_ZERO = 1;
   int JOIN_MITER = 0;
   int JOIN_ROUND = 1;
   int JOIN_BEVEL = 2;
   int CAP_BUTT = 0;
   int CAP_ROUND = 1;
   int CAP_SQUARE = 2;
   int OUTCODE_TOP = 1;
   int OUTCODE_BOTTOM = 2;
   int OUTCODE_LEFT = 4;
   int OUTCODE_RIGHT = 8;
   int OUTCODE_MASK_T_B = 3;
   int OUTCODE_MASK_L_R = 12;
   int OUTCODE_MASK_T_B_L_R = 15;

   static {
      if (ENABLE_LOGS) {
      }

      DO_LOG_WIDEN_ARRAY = false;
      if (ENABLE_LOGS) {
      }

      DO_LOG_OVERSIZE = false;
      if (ENABLE_LOGS) {
      }

      DO_TRACE = false;
      USE_SIMPLIFIER = MarlinProperties.isUseSimplifier();
      USE_PATH_SIMPLIFIER = MarlinProperties.isUsePathSimplifier();
      DO_CLIP_SUBDIVIDER = MarlinProperties.isDoClipSubdivider();
      if (ENABLE_LOGS) {
      }

      DO_LOG_BOUNDS = false;
      if (ENABLE_LOGS) {
      }

      DO_LOG_CLIP = false;
      INITIAL_PIXEL_WIDTH = MarlinProperties.getInitialPixelWidth();
      INITIAL_PIXEL_HEIGHT = MarlinProperties.getInitialPixelHeight();
      INITIAL_AA_ARRAY = INITIAL_PIXEL_WIDTH;
      INITIAL_EDGES_COUNT = MarlinProperties.getInitialEdges();
      INITIAL_EDGES_CAPACITY = INITIAL_EDGES_COUNT * 24;
      INITIAL_CROSSING_COUNT = INITIAL_EDGES_COUNT >> 2;
      SUBPIXEL_LG_POSITIONS_X = MarlinProperties.getSubPixel_Log2_X();
      SUBPIXEL_LG_POSITIONS_Y = MarlinProperties.getSubPixel_Log2_Y();
      MIN_SUBPIXEL_LG_POSITIONS = Math.min(SUBPIXEL_LG_POSITIONS_X, SUBPIXEL_LG_POSITIONS_Y);
      SUBPIXEL_POSITIONS_X = 1 << SUBPIXEL_LG_POSITIONS_X;
      SUBPIXEL_POSITIONS_Y = 1 << SUBPIXEL_LG_POSITIONS_Y;
      MIN_SUBPIXELS = (float)(1 << MIN_SUBPIXEL_LG_POSITIONS);
      INITIAL_BUCKET_ARRAY = INITIAL_PIXEL_HEIGHT * SUBPIXEL_POSITIONS_Y;
      MAX_AA_ALPHA = SUBPIXEL_POSITIONS_X * SUBPIXEL_POSITIONS_Y;
      BLOCK_SIZE_LG = MarlinProperties.getBlockSize_Log2();
      BLOCK_SIZE = 1 << BLOCK_SIZE_LG;
      ENABLE_BLOCK_FLAGS = MarlinProperties.isUseTileFlags();
      ENABLE_BLOCK_FLAGS_HEURISTICS = MarlinProperties.isUseTileFlagsWithHeuristics();
      FORCE_RLE = MarlinProperties.isForceRLE();
      FORCE_NO_RLE = MarlinProperties.isForceNoRLE();
      RLE_MIN_WIDTH = Math.max(BLOCK_SIZE, MarlinProperties.getRLEMinWidth());
   }
}
