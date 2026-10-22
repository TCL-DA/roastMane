package org.apache.commons.math3.util;

class FastMathLiteralArrays {
   private static final double[] EXP_INT_A;
   private static final double[] EXP_INT_B;
   private static final double[] EXP_FRAC_A;
   private static final double[] EXP_FRAC_B;
   private static final double[][] LN_MANT;

   private FastMathLiteralArrays() {
   }

   static double[] loadExpIntA() {
      return (double[])EXP_INT_A.clone();
   }

   static double[] loadExpIntB() {
      return (double[])EXP_INT_B.clone();
   }

   static double[] loadExpFracA() {
      return (double[])EXP_FRAC_A.clone();
   }

   static double[] loadExpFracB() {
      return (double[])EXP_FRAC_B.clone();
   }

   static double[][] loadLnMant() {
      return (double[][])LN_MANT.clone();
   }

   static {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.OutOfMemoryError: Java heap space
      //   at org.jetbrains.java.decompiler.util.collections.FastSparseSetFactory$FastSparseSet.getCopy(FastSparseSetFactory.java:96)
      //   at org.jetbrains.java.decompiler.util.collections.SFormsFastMapDirect.getCopy(SFormsFastMapDirect.java:67)
      //   at org.jetbrains.java.decompiler.modules.decompiler.sforms.SSAUConstructorSparseEx.updateLiveMap(SSAUConstructorSparseEx.java:269)
      //   at org.jetbrains.java.decompiler.modules.decompiler.sforms.SSAUConstructorSparseEx.onAssignment(SSAUConstructorSparseEx.java:262)
      //   at org.jetbrains.java.decompiler.modules.decompiler.sforms.SFormsConstructor.updateVarExprent(SFormsConstructor.java:214)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.AssignmentExprent.processSforms(AssignmentExprent.java:306)
      //   at org.jetbrains.java.decompiler.modules.decompiler.sforms.SFormsConstructor.ssaStatements(SFormsConstructor.java:126)
      //   at org.jetbrains.java.decompiler.modules.decompiler.sforms.SSAUConstructorSparseEx.splitVariables(SSAUConstructorSparseEx.java:45)
      //   at org.jetbrains.java.decompiler.modules.decompiler.StackVarsProcessor.simplifyStackVars(StackVarsProcessor.java:65)
      //   at org.jetbrains.java.decompiler.modules.decompiler.StackVarsProcessor.simplifyStackVars(StackVarsProcessor.java:40)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:224)
      //
      // Bytecode:
      // 0000: sipush 1500
      // 0003: newarray 7
      // 0005: dup
      // 0006: bipush 0
      // 0007: dconst_0
      // 0008: dastore
      // 0009: dup
      // 000a: bipush 1
      // 000b: ldc2_w NaN
      // 000e: dastore
      // 000f: dup
      // 0010: bipush 2
      // 0011: ldc2_w NaN
      // 0014: dastore
      // 0015: dup
      // 0016: bipush 3
      // 0017: ldc2_w NaN
      // 001a: dastore
      // 001b: dup
      // 001c: bipush 4
      // 001d: ldc2_w NaN
      // 0020: dastore
      // 0021: dup
      // 0022: bipush 5
      // 0023: ldc2_w NaN
      // 0026: dastore
      // 0027: dup
      // 0028: bipush 6
      // 002a: ldc2_w NaN
      // 002d: dastore
      // 002e: dup
      // 002f: bipush 7
      // 0031: ldc2_w NaN
      // 0034: dastore
      // 0035: dup
      // 0036: bipush 8
      // 0038: ldc2_w NaN
      // 003b: dastore
      // 003c: dup
      // 003d: bipush 9
      // 003f: ldc2_w NaN
      // 0042: dastore
      // 0043: dup
      // 0044: bipush 10
      // 0046: ldc2_w NaN
      // 0049: dastore
      // 004a: dup
      // 004b: bipush 11
      // 004d: ldc2_w NaN
      // 0050: dastore
      // 0051: dup
      // 0052: bipush 12
      // 0054: ldc2_w NaN
      // 0057: dastore
      // 0058: dup
      // 0059: bipush 13
      // 005b: ldc2_w NaN
      // 005e: dastore
      // 005f: dup
      // 0060: bipush 14
      // 0062: ldc2_w NaN
      // 0065: dastore
      // 0066: dup
      // 0067: bipush 15
      // 0069: ldc2_w NaN
      // 006c: dastore
      // 006d: dup
      // 006e: bipush 16
      // 0070: ldc2_w NaN
      // 0073: dastore
      // 0074: dup
      // 0075: bipush 17
      // 0077: ldc2_w NaN
      // 007a: dastore
      // 007b: dup
      // 007c: bipush 18
      // 007e: ldc2_w NaN
      // 0081: dastore
      // 0082: dup
      // 0083: bipush 19
      // 0085: ldc2_w NaN
      // 0088: dastore
      // 0089: dup
      // 008a: bipush 20
      // 008c: ldc2_w NaN
      // 008f: dastore
      // 0090: dup
      // 0091: bipush 21
      // 0093: ldc2_w NaN
      // 0096: dastore
      // 0097: dup
      // 0098: bipush 22
      // 009a: ldc2_w NaN
      // 009d: dastore
      // 009e: dup
      // 009f: bipush 23
      // 00a1: ldc2_w NaN
      // 00a4: dastore
      // 00a5: dup
      // 00a6: bipush 24
      // 00a8: ldc2_w NaN
      // 00ab: dastore
      // 00ac: dup
      // 00ad: bipush 25
      // 00af: ldc2_w NaN
      // 00b2: dastore
      // 00b3: dup
      // 00b4: bipush 26
      // 00b6: ldc2_w NaN
      // 00b9: dastore
      // 00ba: dup
      // 00bb: bipush 27
      // 00bd: ldc2_w NaN
      // 00c0: dastore
      // 00c1: dup
      // 00c2: bipush 28
      // 00c4: ldc2_w NaN
      // 00c7: dastore
      // 00c8: dup
      // 00c9: bipush 29
      // 00cb: ldc2_w NaN
      // 00ce: dastore
      // 00cf: dup
      // 00d0: bipush 30
      // 00d2: ldc2_w NaN
      // 00d5: dastore
      // 00d6: dup
      // 00d7: bipush 31
      // 00d9: ldc2_w NaN
      // 00dc: dastore
      // 00dd: dup
      // 00de: bipush 32
      // 00e0: ldc2_w NaN
      // 00e3: dastore
      // 00e4: dup
      // 00e5: bipush 33
      // 00e7: ldc2_w NaN
      // 00ea: dastore
      // 00eb: dup
      // 00ec: bipush 34
      // 00ee: ldc2_w NaN
      // 00f1: dastore
      // 00f2: dup
      // 00f3: bipush 35
      // 00f5: ldc2_w NaN
      // 00f8: dastore
      // 00f9: dup
      // 00fa: bipush 36
      // 00fc: ldc2_w NaN
      // 00ff: dastore
      // 0100: dup
      // 0101: bipush 37
      // 0103: ldc2_w NaN
      // 0106: dastore
      // 0107: dup
      // 0108: bipush 38
      // 010a: ldc2_w NaN
      // 010d: dastore
      // 010e: dup
      // 010f: bipush 39
      // 0111: ldc2_w NaN
      // 0114: dastore
      // 0115: dup
      // 0116: bipush 40
      // 0118: ldc2_w NaN
      // 011b: dastore
      // 011c: dup
      // 011d: bipush 41
      // 011f: ldc2_w 1.2167807682331913E-308
      // 0122: dastore
      // 0123: dup
      // 0124: bipush 42
      // 0126: ldc2_w 3.3075532478807267E-308
      // 0129: dastore
      // 012a: dup
      // 012b: bipush 43
      // 012d: ldc2_w 8.990862214387203E-308
      // 0130: dastore
      // 0131: dup
      // 0132: bipush 44
      // 0134: ldc2_w 2.4439696075216986E-307
      // 0137: dastore
      // 0138: dup
      // 0139: bipush 45
      // 013b: ldc2_w 6.64339758024534E-307
      // 013e: dastore
      // 013f: dup
      // 0140: bipush 46
      // 0142: ldc2_w 1.8058628951432254E-306
      // 0145: dastore
      // 0146: dup
      // 0147: bipush 47
      // 0149: ldc2_w 4.908843759498681E-306
      // 014c: dastore
      // 014d: dup
      // 014e: bipush 48
      // 0150: ldc2_w 1.334362017065677E-305
      // 0153: dastore
      // 0154: dup
      // 0155: bipush 49
      // 0157: ldc2_w 3.627172425759641E-305
      // 015a: dastore
      // 015b: dup
      // 015c: bipush 50
      // 015e: ldc2_w 9.85967600992008E-305
      // 0161: dastore
      // 0162: dup
      // 0163: bipush 51
      // 0165: ldc2_w 2.680137967689915E-304
      // 0168: dastore
      // 0169: dup
      // 016a: bipush 52
      // 016c: ldc2_w 7.285370725133842E-304
      // 016f: dastore
      // 0170: dup
      // 0171: bipush 53
      // 0173: ldc2_w 1.9803689272433392E-303
      // 0176: dastore
      // 0177: dup
      // 0178: bipush 54
      // 017a: ldc2_w 5.3832011494782624E-303
      // 017d: dastore
      // 017e: dup
      // 017f: bipush 55
      // 0181: ldc2_w 1.463305638201413E-302
      // 0184: dastore
      // 0185: dup
      // 0186: bipush 56
      // 0188: ldc2_w 3.9776772027043775E-302
      // 018b: dastore
      // 018c: dup
      // 018d: bipush 57
      // 018f: ldc2_w 1.0812448255518705E-301
      // 0192: dastore
      // 0193: dup
      // 0194: bipush 58
      // 0196: ldc2_w 2.9391280956327795E-301
      // 0199: dastore
      // 019a: dup
      // 019b: bipush 59
      // 019d: ldc2_w 7.989378677301346E-301
      // 01a0: dastore
      // 01a1: dup
      // 01a2: bipush 60
      // 01a4: ldc2_w 2.1717383041010577E-300
      // 01a7: dastore
      // 01a8: dup
      // 01a9: bipush 61
      // 01ab: ldc2_w 5.903396499766243E-300
      // 01ae: dastore
      // 01af: dup
      // 01b0: bipush 62
      // 01b2: ldc2_w 1.604709595901607E-299
      // 01b5: dastore
      // 01b6: dup
      // 01b7: bipush 63
      // 01b9: ldc2_w 4.3620527352131126E-299
      // 01bc: dastore
      // 01bd: dup
      // 01be: bipush 64
      // 01c0: ldc2_w 1.1857289715706991E-298
      // 01c3: dastore
      // 01c4: dup
      // 01c5: bipush 65
      // 01c7: ldc2_w 3.2231452986239366E-298
      // 01ca: dastore
      // 01cb: dup
      // 01cc: bipush 66
      // 01ce: ldc2_w 8.761416875971053E-298
      // 01d1: dastore
      // 01d2: dup
      // 01d3: bipush 67
      // 01d5: ldc2_w 2.381600167287677E-297
      // 01d8: dastore
      // 01d9: dup
      // 01da: bipush 68
      // 01dc: ldc2_w 6.473860152384321E-297
      // 01df: dastore
      // 01e0: dup
      // 01e1: bipush 69
      // 01e3: ldc2_w 1.7597776278732318E-296
      // 01e6: dastore
      // 01e7: dup
      // 01e8: bipush 70
      // 01ea: ldc2_w 4.7835721669653157E-296
      // 01ed: dastore
      // 01ee: dup
      // 01ef: bipush 71
      // 01f1: ldc2_w 1.3003096668152053E-295
      // 01f4: dastore
      // 01f5: dup
      // 01f6: bipush 72
      // 01f8: ldc2_w 3.5346080979652066E-295
      // 01fb: dastore
      // 01fc: dup
      // 01fd: bipush 73
      // 01ff: ldc2_w 9.608060944124859E-295
      // 0202: dastore
      // 0203: dup
      // 0204: bipush 74
      // 0206: ldc2_w 2.6117415961302846E-294
      // 0209: dastore
      // 020a: dup
      // 020b: bipush 75
      // 020d: ldc2_w 7.099449830809996E-294
      // 0210: dastore
      // 0211: dup
      // 0212: bipush 76
      // 0214: ldc2_w 1.9298305829106006E-293
      // 0217: dastore
      // 0218: dup
      // 0219: bipush 77
      // 021b: ldc2_w 5.245823134132673E-293
      // 021e: dastore
      // 021f: dup
      // 0220: bipush 78
      // 0222: ldc2_w 1.4259627797225802E-292
      // 0225: dastore
      // 0226: dup
      // 0227: bipush 79
      // 0229: ldc2_w 3.8761686729764145E-292
      // 022c: dastore
      // 022d: dup
      // 022e: bipush 80
      // 0230: ldc2_w 1.0536518897078156E-291
      // 0233: dastore
      // 0234: dup
      // 0235: bipush 81
      // 0237: ldc2_w 2.864122672853628E-291
      // 023a: dastore
      // 023b: dup
      // 023c: bipush 82
      // 023e: ldc2_w 7.785491934690374E-291
      // 0241: dastore
      // 0242: dup
      // 0243: bipush 83
      // 0245: ldc2_w 2.116316283183901E-290
      // 0248: dastore
      // 0249: dup
      // 024a: bipush 84
      // 024c: ldc2_w 5.7527436249968E-290
      // 024f: dastore
      // 0250: dup
      // 0251: bipush 85
      // 0253: ldc2_w 1.5637579898345352E-289
      // 0256: dastore
      // 0257: dup
      // 0258: bipush 86
      // 025a: ldc2_w 4.250734424415339E-289
      // 025d: dastore
      // 025e: dup
      // 025f: bipush 87
      // 0261: ldc2_w 1.1554696041977512E-288
      // 0264: dastore
      // 0265: dup
      // 0266: bipush 88
      // 0268: ldc2_w 3.1408919441362495E-288
      // 026b: dastore
      // 026c: dup
      // 026d: bipush 89
      // 026f: ldc2_w 8.537829238438662E-288
      // 0272: dastore
      // 0273: dup
      // 0274: bipush 90
      // 0276: ldc2_w 2.320822576772103E-287
      // 0279: dastore
      // 027a: dup
      // 027b: bipush 91
      // 027d: ldc2_w 6.308649765138419E-287
      // 0280: dastore
      // 0281: dup
      // 0282: bipush 92
      // 0284: ldc2_w 1.7148689119310826E-286
      // 0287: dastore
      // 0288: dup
      // 0289: bipush 93
      // 028b: ldc2_w 4.66149719271323E-286
      // 028e: dastore
      // 028f: dup
      // 0290: bipush 94
      // 0292: ldc2_w 1.267126226441217E-285
      // 0295: dastore
      // 0296: dup
      // 0297: bipush 95
      // 0299: ldc2_w 3.444406231880653E-285
      // 029c: dastore
      // 029d: dup
      // 029e: bipush 96
      // 02a0: ldc2_w 9.362866914115166E-285
      // 02a3: dastore
      // 02a4: dup
      // 02a5: bipush 97
      // 02a7: ldc2_w 2.5450911557068313E-284
      // 02aa: dastore
      // 02ab: dup
      // 02ac: bipush 98
      // 02ae: ldc2_w 6.918275021321188E-284
      // 02b1: dastore
      // 02b2: dup
      // 02b3: bipush 99
      // 02b5: ldc2_w 1.880582039589629E-283
      // 02b8: dastore
      // 02b9: dup
      // 02ba: bipush 100
      // 02bc: ldc2_w 5.111952261540649E-283
      // 02bf: dastore
      // 02c0: dup
      // 02c1: bipush 101
      // 02c3: ldc2_w 1.3895726688907995E-282
      // 02c6: dastore
      // 02c7: dup
      // 02c8: bipush 102
      // 02ca: ldc2_w 3.7772500667438066E-282
      // 02cd: dastore
      // 02ce: dup
      // 02cf: bipush 103
      // 02d1: ldc2_w 1.026763015362553E-281
      // 02d4: dastore
      // 02d5: dup
      // 02d6: bipush 104
      // 02d8: ldc2_w 2.791031173360063E-281
      // 02db: dastore
      // 02dc: dup
      // 02dd: bipush 105
      // 02df: ldc2_w 7.586808748646825E-281
      // 02e2: dastore
      // 02e3: dup
      // 02e4: bipush 106
      // 02e6: ldc2_w 2.0623086887184633E-280
      // 02e9: dastore
      // 02ea: dup
      // 02eb: bipush 107
      // 02ed: ldc2_w 5.605936171588964E-280
      // 02f0: dastore
      // 02f1: dup
      // 02f2: bipush 108
      // 02f4: ldc2_w 1.5238514098804918E-279
      // 02f7: dastore
      // 02f8: dup
      // 02f9: bipush 109
      // 02fb: ldc2_w 4.1422578754033235E-279
      // 02fe: dastore
      // 02ff: dup
      // 0300: bipush 110
      // 0302: ldc2_w 1.1259823210174452E-278
      // 0305: dastore
      // 0306: dup
      // 0307: bipush 111
      // 0309: ldc2_w 3.060737220976933E-278
      // 030c: dastore
      // 030d: dup
      // 030e: bipush 112
      // 0310: ldc2_w 8.319947089683576E-278
      // 0313: dastore
      // 0314: dup
      // 0315: bipush 113
      // 0317: ldc2_w 2.2615958035357106E-277
      // 031a: dastore
      // 031b: dup
      // 031c: bipush 114
      // 031e: ldc2_w 6.147655179898435E-277
      // 0321: dastore
      // 0322: dup
      // 0323: bipush 115
      // 0325: ldc2_w 1.6711060014400145E-276
      // 0328: dastore
      // 0329: dup
      // 032a: bipush 116
      // 032c: ldc2_w 4.542536646012133E-276
      // 032f: dastore
      // 0330: dup
      // 0331: bipush 117
      // 0333: ldc2_w 1.2347896500246374E-275
      // 0336: dastore
      // 0337: dup
      // 0338: bipush 118
      // 033a: ldc2_w 3.3565057475434694E-275
      // 033d: dastore
      // 033e: dup
      // 033f: bipush 119
      // 0341: ldc2_w 9.123929070778758E-275
      // 0344: dastore
      // 0345: dup
      // 0346: bipush 120
      // 0348: ldc2_w 2.4801413921885483E-274
      // 034b: dastore
      // 034c: dup
      // 034d: bipush 121
      // 034f: ldc2_w 6.741722283079056E-274
      // 0352: dastore
      // 0353: dup
      // 0354: bipush 122
      // 0356: ldc2_w 1.8325902719086093E-273
      // 0359: dastore
      // 035a: dup
      // 035b: bipush 123
      // 035d: ldc2_w 4.981496462621207E-273
      // 0360: dastore
      // 0361: dup
      // 0362: bipush 124
      // 0364: ldc2_w 1.3541112064618357E-272
      // 0367: dastore
      // 0368: dup
      // 0369: bipush 125
      // 036b: ldc2_w 3.68085620656127E-272
      // 036e: dastore
      // 036f: dup
      // 0370: bipush 126
      // 0372: ldc2_w 1.0005602916630382E-271
      // 0375: dastore
      // 0376: dup
      // 0377: bipush 127
      // 0379: ldc2_w 2.719805132368625E-271
      // 037c: dastore
      // 037d: dup
      // 037e: sipush 128
      // 0381: ldc2_w 7.393196131284108E-271
      // 0384: dastore
      // 0385: dup
      // 0386: sipush 129
      // 0389: ldc2_w 2.0096791226867E-270
      // 038c: dastore
      // 038d: dup
      // 038e: sipush 130
      // 0391: ldc2_w 5.462874707256208E-270
      // 0394: dastore
      // 0395: dup
      // 0396: sipush 131
      // 0399: ldc2_w 1.4849631831943512E-269
      // 039c: dastore
      // 039d: dup
      // 039e: sipush 132
      // 03a1: ldc2_w 4.036548930895323E-269
      // 03a4: dastore
      // 03a5: dup
      // 03a6: sipush 133
      // 03a9: ldc2_w 1.0972476870931676E-268
      // 03ac: dastore
      // 03ad: dup
      // 03ae: sipush 134
      // 03b1: ldc2_w 2.9826282194717127E-268
      // 03b4: dastore
      // 03b5: dup
      // 03b6: sipush 135
      // 03b9: ldc2_w 8.107624153838987E-268
      // 03bc: dastore
      // 03bd: dup
      // 03be: sipush 136
      // 03c1: ldc2_w 2.2038806519542315E-267
      // 03c4: dastore
      // 03c5: dup
      // 03c6: sipush 137
      // 03c9: ldc2_w 5.990769236615968E-267
      // 03cc: dastore
      // 03cd: dup
      // 03ce: sipush 138
      // 03d1: ldc2_w 1.628459873440512E-266
      // 03d4: dastore
      // 03d5: dup
      // 03d6: sipush 139
      // 03d9: ldc2_w 4.4266130556431266E-266
      // 03dc: dastore
      // 03dd: dup
      // 03de: sipush 140
      // 03e1: ldc2_w 1.203278237867575E-265
      // 03e4: dastore
      // 03e5: dup
      // 03e6: sipush 141
      // 03e9: ldc2_w 3.270849446965521E-265
      // 03ec: dastore
      // 03ed: dup
      // 03ee: sipush 142
      // 03f1: ldc2_w 8.891090288030614E-265
      // 03f4: dastore
      // 03f5: dup
      // 03f6: sipush 143
      // 03f9: ldc2_w 2.4168487931443637E-264
      // 03fc: dastore
      // 03fd: dup
      // 03fe: sipush 144
      // 0401: ldc2_w 6.569676185250389E-264
      // 0404: dastore
      // 0405: dup
      // 0406: sipush 145
      // 0409: ldc2_w 1.7858231429575898E-263
      // 040c: dastore
      // 040d: dup
      // 040e: sipush 146
      // 0411: ldc2_w 4.85437090269903E-263
      // 0414: dastore
      // 0415: dup
      // 0416: sipush 147
      // 0419: ldc2_w 1.3195548295785448E-262
      // 041c: dastore
      // 041d: dup
      // 041e: sipush 148
      // 0421: ldc2_w 3.5869215528816054E-262
      // 0424: dastore
      // 0425: dup
      // 0426: sipush 149
      // 0429: ldc2_w 9.750264097807267E-262
      // 042c: dastore
      // 042d: dup
      // 042e: sipush 150
      // 0431: ldc2_w 2.650396454019762E-261
      // 0434: dastore
      // 0435: dup
      // 0436: sipush 151
      // 0439: ldc2_w 7.204525142098426E-261
      // 043c: dastore
      // 043d: dup
      // 043e: sipush 152
      // 0441: ldc2_w 1.958392846081373E-260
      // 0444: dastore
      // 0445: dup
      // 0446: sipush 153
      // 0449: ldc2_w 5.32346341339996E-260
      // 044c: dastore
      // 044d: dup
      // 044e: sipush 154
      // 0451: ldc2_w 1.4470673509275515E-259
      // 0454: dastore
      // 0455: dup
      // 0456: sipush 155
      // 0459: ldc2_w 3.9335373658569176E-259
      // 045c: dastore
      // 045d: dup
      // 045e: sipush 156
      // 0461: ldc2_w 1.0692462289051038E-258
      // 0464: dastore
      // 0465: dup
      // 0466: sipush 157
      // 0469: ldc2_w 2.9065128598079075E-258
      // 046c: dastore
      // 046d: dup
      // 046e: sipush 158
      // 0471: ldc2_w 7.900720862969045E-258
      // 0474: dastore
      // 0475: dup
      // 0476: sipush 159
      // 0479: ldc2_w 2.147638465376883E-257
      // 047c: dastore
      // 047d: dup
      // 047e: sipush 160
      // 0481: ldc2_w 5.8378869339035456E-257
      // 0484: dastore
      // 0485: dup
      // 0486: sipush 161
      // 0489: ldc2_w 1.5869022483809747E-256
      // 048c: dastore
      // 048d: dup
      // 048e: sipush 162
      // 0491: ldc2_w 4.3136475849391444E-256
      // 0494: dastore
      // 0495: dup
      // 0496: sipush 163
      // 0499: ldc2_w 1.1725710340687719E-255
      // 049c: dastore
      // 049d: dup
      // 049e: sipush 164
      // 04a1: ldc2_w 3.1873780814410126E-255
      // 04a4: dastore
      // 04a5: dup
      // 04a6: sipush 165
      // 04a9: ldc2_w 8.66419234315257E-255
      // 04ac: dastore
      // 04ad: dup
      // 04ae: sipush 166
      // 04b1: ldc2_w 2.35517168886351E-254
      // 04b4: dastore
      // 04b5: dup
      // 04b6: sipush 167
      // 04b9: ldc2_w 6.402020300783889E-254
      // 04bc: dastore
      // 04bd: dup
      // 04be: sipush 168
      // 04c1: ldc2_w 1.740249660600677E-253
      // 04c4: dastore
      // 04c5: dup
      // 04c6: sipush 169
      // 04c9: ldc2_w 4.7304887145310405E-253
      // 04cc: dastore
      // 04cd: dup
      // 04ce: sipush 170
      // 04d1: ldc2_w 1.2858802448614707E-252
      // 04d4: dastore
      // 04d5: dup
      // 04d6: sipush 171
      // 04d9: ldc2_w 3.495384792953975E-252
      // 04dc: dastore
      // 04dd: dup
      // 04de: sipush 172
      // 04e1: ldc2_w 9.501439740542955E-252
      // 04e4: dastore
      // 04e5: dup
      // 04e6: sipush 173
      // 04e9: ldc2_w 2.582759362004277E-251
      // 04ec: dastore
      // 04ed: dup
      // 04ee: sipush 174
      // 04f1: ldc2_w 7.020668578160457E-251
      // 04f4: dastore
      // 04f5: dup
      // 04f6: sipush 175
      // 04f9: ldc2_w 1.908415302517694E-250
      // 04fc: dastore
      // 04fd: dup
      // 04fe: sipush 176
      // 0501: ldc2_w 5.1876107490791666E-250
      // 0504: dastore
      // 0505: dup
      // 0506: sipush 177
      // 0509: ldc2_w 1.4101386971763257E-249
      // 050c: dastore
      // 050d: dup
      // 050e: sipush 178
      // 0511: ldc2_w 3.8331545111676784E-249
      // 0514: dastore
      // 0515: dup
      // 0516: sipush 179
      // 0519: ldc2_w 1.0419594359065132E-248
      // 051c: dastore
      // 051d: dup
      // 051e: sipush 180
      // 0521: ldc2_w 2.8323395451363237E-248
      // 0524: dastore
      // 0525: dup
      // 0526: sipush 181
      // 0529: ldc2_w 7.699097067385825E-248
      // 052c: dastore
      // 052d: dup
      // 052e: sipush 182
      // 0531: ldc2_w 2.0928317096428755E-247
      // 0534: dastore
      // 0535: dup
      // 0536: sipush 183
      // 0539: ldc2_w 5.688906371296133E-247
      // 053c: dastore
      // 053d: dup
      // 053e: sipush 184
      // 0541: ldc2_w 1.5464049837965422E-246
      // 0544: dastore
      // 0545: dup
      // 0546: sipush 185
      // 0549: ldc2_w 4.2035646586788297E-246
      // 054c: dastore
      // 054d: dup
      // 054e: sipush 186
      // 0551: ldc2_w 1.1426473877336358E-245
      // 0554: dastore
      // 0555: dup
      // 0556: sipush 187
      // 0559: ldc2_w 3.106037603716254E-245
      // 055c: dastore
      // 055d: dup
      // 055e: sipush 188
      // 0561: ldc2_w 8.443084996839363E-245
      // 0564: dastore
      // 0565: dup
      // 0566: sipush 189
      // 0569: ldc2_w 2.2950686306677644E-244
      // 056c: dastore
      // 056d: dup
      // 056e: sipush 190
      // 0571: ldc2_w 6.238642390386363E-244
      // 0574: dastore
      // 0575: dup
      // 0576: sipush 191
      // 0579: ldc2_w 1.695838923802857E-243
      // 057c: dastore
      // 057d: dup
      // 057e: sipush 192
      // 0581: ldc2_w 4.6097680405580995E-243
      // 0584: dastore
      // 0585: dup
      // 0586: sipush 193
      // 0589: ldc2_w 1.2530649392922358E-242
      // 058c: dastore
      // 058d: dup
      // 058e: sipush 194
      // 0591: ldc2_w 3.4061835424180075E-242
      // 0594: dastore
      // 0595: dup
      // 0596: sipush 195
      // 0599: ldc2_w 9.25896798127602E-242
      // 059c: dastore
      // 059d: dup
      // 059e: sipush 196
      // 05a1: ldc2_w 2.5168480541429286E-241
      // 05a4: dastore
      // 05a5: dup
      // 05a6: sipush 197
      // 05a9: ldc2_w 6.841502859109196E-241
      // 05ac: dastore
      // 05ad: dup
      // 05ae: sipush 198
      // 05b1: ldc2_w 1.8597132378953187E-240
      // 05b4: dastore
      // 05b5: dup
      // 05b6: sipush 199
      // 05b9: ldc2_w 5.055224959032211E-240
      // 05bc: dastore
      // 05bd: dup
      // 05be: sipush 200
      // 05c1: ldc2_w 1.374152583940637E-239
      // 05c4: dastore
      // 05c5: dup
      // 05c6: sipush 201
      // 05c9: ldc2_w 3.735333866258403E-239
      // 05cc: dastore
      // 05cd: dup
      // 05ce: sipush 202
      // 05d1: ldc2_w 1.0153690688015855E-238
      // 05d4: dastore
      // 05d5: dup
      // 05d6: sipush 203
      // 05d9: ldc2_w 2.7600590782738726E-238
      // 05dc: dastore
      // 05dd: dup
      // 05de: sipush 204
      // 05e1: ldc2_w 7.502618487550056E-238
      // 05e4: dastore
      // 05e5: dup
      // 05e6: sipush 205
      // 05e9: ldc2_w 2.0394233446495043E-237
      // 05ec: dastore
      // 05ed: dup
      // 05ee: sipush 206
      // 05f1: ldc2_w 5.543727690168612E-237
      // 05f4: dastore
      // 05f5: dup
      // 05f6: sipush 207
      // 05f9: ldc2_w 1.5069412868172555E-236
      // 05fc: dastore
      // 05fd: dup
      // 05fe: sipush 208
      // 0601: ldc2_w 4.0962906236847E-236
      // 0604: dastore
      // 0605: dup
      // 0606: sipush 209
      // 0609: ldc2_w 1.1134873918971586E-235
      // 060c: dastore
      // 060d: dup
      // 060e: sipush 210
      // 0611: ldc2_w 3.026772467749944E-235
      // 0614: dastore
      // 0615: dup
      // 0616: sipush 211
      // 0619: ldc2_w 8.227620163729258E-235
      // 061c: dastore
      // 061d: dup
      // 061e: sipush 212
      // 0621: ldc2_w 2.2364990583200056E-234
      // 0624: dastore
      // 0625: dup
      // 0626: sipush 213
      // 0629: ldc2_w 6.079434951446575E-234
      // 062c: dastore
      // 062d: dup
      // 062e: sipush 214
      // 0631: ldc2_w 1.6525617499662284E-233
      // 0634: dastore
      // 0635: dup
      // 0636: sipush 215
      // 0639: ldc2_w 4.4921289690525345E-233
      // 063c: dastore
      // 063d: dup
      // 063e: sipush 216
      // 0641: ldc2_w 1.2210872189854344E-232
      // 0644: dastore
      // 0645: dup
      // 0646: sipush 217
      // 0649: ldc2_w 3.3192593301633E-232
      // 064c: dastore
      // 064d: dup
      // 064e: sipush 218
      // 0651: ldc2_w 9.02268127425393E-232
      // 0654: dastore
      // 0655: dup
      // 0656: sipush 219
      // 0659: ldc2_w 2.4526190464373087E-231
      // 065c: dastore
      // 065d: dup
      // 065e: sipush 220
      // 0661: ldc2_w 6.666909874218774E-231
      // 0664: dastore
      // 0665: dup
      // 0666: sipush 221
      // 0669: ldc2_w 1.8122539547625083E-230
      // 066c: dastore
      // 066d: dup
      // 066e: sipush 222
      // 0671: ldc2_w 4.926216840507529E-230
      // 0674: dastore
      // 0675: dup
      // 0676: sipush 223
      // 0679: ldc2_w 1.3390847149416908E-229
      // 067c: dastore
      // 067d: dup
      // 067e: sipush 224
      // 0681: ldc2_w 3.6400093808551196E-229
      // 0684: dastore
      // 0685: dup
      // 0686: sipush 225
      // 0689: ldc2_w 9.894571625944288E-229
      // 068c: dastore
      // 068d: dup
      // 068e: sipush 226
      // 0691: ldc2_w 2.689623698321582E-228
      // 0694: dastore
      // 0695: dup
      // 0696: sipush 227
      // 0699: ldc2_w 7.31115423069187E-228
      // 069c: dastore
      // 069d: dup
      // 069e: sipush 228
      // 06a1: ldc2_w 1.9873779569310022E-227
      // 06a4: dastore
      // 06a5: dup
      // 06a6: sipush 229
      // 06a9: ldc2_w 5.402252865260326E-227
      // 06ac: dastore
      // 06ad: dup
      // 06ae: sipush 230
      // 06b1: ldc2_w 1.4684846983789053E-226
      // 06b4: dastore
      // 06b5: dup
      // 06b6: sipush 231
      // 06b9: ldc2_w 3.991755413823315E-226
      // 06bc: dastore
      // 06bd: dup
      // 06be: sipush 232
      // 06c1: ldc2_w 1.0850715739509136E-225
      // 06c4: dastore
      // 06c5: dup
      // 06c6: sipush 233
      // 06c9: ldc2_w 2.9495302004590423E-225
      // 06cc: dastore
      // 06cd: dup
      // 06ce: sipush 234
      // 06d1: ldc2_w 8.017654713159388E-225
      // 06d4: dastore
      // 06d5: dup
      // 06d6: sipush 235
      // 06d9: ldc2_w 2.179424521221378E-224
      // 06dc: dastore
      // 06dd: dup
      // 06de: sipush 236
      // 06e1: ldc2_w 5.924290380648597E-224
      // 06e4: dastore
      // 06e5: dup
      // 06e6: sipush 237
      // 06e9: ldc2_w 1.6103890140790331E-223
      // 06ec: dastore
      // 06ed: dup
      // 06ee: sipush 238
      // 06f1: ldc2_w 4.377491272857675E-223
      // 06f4: dastore
      // 06f5: dup
      // 06f6: sipush 239
      // 06f9: ldc2_w 1.1899254154663847E-222
      // 06fc: dastore
      // 06fd: dup
      // 06fe: sipush 240
      // 0701: ldc2_w 3.2345523990372546E-222
      // 0704: dastore
      // 0705: dup
      // 0706: sipush 241
      // 0709: ldc2_w 8.792425221770645E-222
      // 070c: dastore
      // 070d: dup
      // 070e: sipush 242
      // 0711: ldc2_w 2.3900289095512176E-221
      // 0714: dastore
      // 0715: dup
      // 0716: sipush 243
      // 0719: ldc2_w 6.496772856703278E-221
      // 071c: dastore
      // 071d: dup
      // 071e: sipush 244
      // 0721: ldc2_w 1.7660059778220905E-220
      // 0724: dastore
      // 0725: dup
      // 0726: sipush 245
      // 0729: ldc2_w 4.800501435803201E-220
      // 072c: dastore
      // 072d: dup
      // 072e: sipush 246
      // 0731: ldc2_w 1.3049116216750674E-219
      // 0734: dastore
      // 0735: dup
      // 0736: sipush 247
      // 0739: ldc2_w 3.5471180281159325E-219
      // 073c: dastore
      // 073d: dup
      // 073e: sipush 248
      // 0741: ldc2_w 9.642065709892252E-219
      // 0744: dastore
      // 0745: dup
      // 0746: sipush 249
      // 0749: ldc2_w 2.6209850274990846E-218
      // 074c: dastore
      // 074d: dup
      // 074e: sipush 250
      // 0751: ldc2_w 7.124574366530717E-218
      // 0754: dastore
      // 0755: dup
      // 0756: sipush 251
      // 0759: ldc2_w 1.9366601417010147E-217
      // 075c: dastore
      // 075d: dup
      // 075e: sipush 252
      // 0761: ldc2_w 5.264388476949737E-217
      // 0764: dastore
      // 0765: dup
      // 0766: sipush 253
      // 0769: ldc2_w 1.431009021985696E-216
      // 076c: dastore
      // 076d: dup
      // 076e: sipush 254
      // 0771: ldc2_w 3.889885799962507E-216
      // 0774: dastore
      // 0775: dup
      // 0776: sipush 255
      // 0779: ldc2_w 1.057380684430436E-215
      // 077c: dastore
      // 077d: dup
      // 077e: sipush 256
      // 0781: ldc2_w 2.8742587656021775E-215
      // 0784: dastore
      // 0785: dup
      // 0786: sipush 257
      // 0789: ldc2_w 7.813044552050569E-215
      // 078c: dastore
      // 078d: dup
      // 078e: sipush 258
      // 0791: ldc2_w 2.1238058974550874E-214
      // 0794: dastore
      // 0795: dup
      // 0796: sipush 259
      // 0799: ldc2_w 5.773102661099307E-214
      // 079c: dastore
      // 079d: dup
      // 079e: sipush 260
      // 07a1: ldc2_w 1.5692921723471877E-213
      // 07a4: dastore
      // 07a5: dup
      // 07a6: sipush 261
      // 07a9: ldc2_w 4.2657777816050375E-213
      // 07ac: dastore
      // 07ad: dup
      // 07ae: sipush 262
      // 07b1: ldc2_w 1.1595585743839232E-212
      // 07b4: dastore
      // 07b5: dup
      // 07b6: sipush 263
      // 07b9: ldc2_w 3.1520070828798975E-212
      // 07bc: dastore
      // 07bd: dup
      // 07be: sipush 264
      // 07c1: ldc2_w 8.568043768122183E-212
      // 07c4: dastore
      // 07c5: dup
      // 07c6: sipush 265
      // 07c9: ldc2_w 2.329035966595791E-211
      // 07cc: dastore
      // 07cd: dup
      // 07ce: sipush 266
      // 07d1: ldc2_w 6.33097561889469E-211
      // 07d4: dastore
      // 07d5: dup
      // 07d6: sipush 267
      // 07d9: ldc2_w 1.720937714565362E-210
      // 07dc: dastore
      // 07dd: dup
      // 07de: sipush 268
      // 07e1: ldc2_w 4.677993239821998E-210
      // 07e4: dastore
      // 07e5: dup
      // 07e6: sipush 269
      // 07e9: ldc2_w 1.2716105485691878E-209
      // 07ec: dastore
      // 07ed: dup
      // 07ee: sipush 270
      // 07f1: ldc2_w 3.456595573934475E-209
      // 07f4: dastore
      // 07f5: dup
      // 07f6: sipush 271
      // 07f9: ldc2_w 9.396000024637834E-209
      // 07fc: dastore
      // 07fd: dup
      // 07fe: sipush 272
      // 0801: ldc2_w 2.55409795397022E-208
      // 0804: dastore
      // 0805: dup
      // 0806: sipush 273
      // 0809: ldc2_w 6.942757623821567E-208
      // 080c: dastore
      // 080d: dup
      // 080e: sipush 274
      // 0811: ldc2_w 1.887237361505784E-207
      // 0814: dastore
      // 0815: dup
      // 0816: sipush 275
      // 0819: ldc2_w 5.13004286606108E-207
      // 081c: dastore
      // 081d: dup
      // 081e: sipush 276
      // 0821: ldc2_w 1.3944901709366118E-206
      // 0824: dastore
      // 0825: dup
      // 0826: sipush 277
      // 0829: ldc2_w 3.7906173667738715E-206
      // 082c: dastore
      // 082d: dup
      // 082e: sipush 278
      // 0831: ldc2_w 1.0303966192973381E-205
      // 0834: dastore
      // 0835: dup
      // 0836: sipush 279
      // 0839: ldc2_w 2.8009086220877197E-205
      // 083c: dastore
      // 083d: dup
      // 083e: sipush 280
      // 0841: ldc2_w 7.613657850210907E-205
      // 0844: dastore
      // 0845: dup
      // 0846: sipush 281
      // 0849: ldc2_w 2.0696069842597556E-204
      // 084c: dastore
      // 084d: dup
      // 084e: sipush 282
      // 0851: ldc2_w 5.6257755605305175E-204
      // 0854: dastore
      // 0855: dup
      // 0856: sipush 283
      // 0859: ldc2_w 1.5292444435954893E-203
      // 085c: dastore
      // 085d: dup
      // 085e: sipush 284
      // 0861: ldc2_w 4.156916476922876E-203
      // 0864: dastore
      // 0865: dup
      // 0866: sipush 285
      // 0869: ldc2_w 1.12996721591364E-202
      // 086c: dastore
      // 086d: dup
      // 086e: sipush 286
      // 0871: ldc2_w 3.071569248856111E-202
      // 0874: dastore
      // 0875: dup
      // 0876: sipush 287
      // 0879: ldc2_w 8.349390727162016E-202
      // 087c: dastore
      // 087d: dup
      // 087e: sipush 288
      // 0881: ldc2_w 2.2695999828608633E-201
      // 0884: dastore
      // 0885: dup
      // 0886: sipush 289
      // 0889: ldc2_w 6.1694117899971836E-201
      // 088c: dastore
      // 088d: dup
      // 088e: sipush 290
      // 0891: ldc2_w 1.677020107827128E-200
      // 0894: dastore
      // 0895: dup
      // 0896: sipush 291
      // 0899: ldc2_w 4.558612479525779E-200
      // 089c: dastore
      // 089d: dup
      // 089e: sipush 292
      // 08a1: ldc2_w 1.2391595516612638E-199
      // 08a4: dastore
      // 08a5: dup
      // 08a6: sipush 293
      // 08a9: ldc2_w 3.3683846288580648E-199
      // 08ac: dastore
      // 08ad: dup
      // 08ae: sipush 294
      // 08b1: ldc2_w 9.156218120779494E-199
      // 08b4: dastore
      // 08b5: dup
      // 08b6: sipush 295
      // 08b9: ldc2_w 2.4889182184335247E-198
      // 08bc: dastore
      // 08bd: dup
      // 08be: sipush 296
      // 08c1: ldc2_w 6.765580431441772E-198
      // 08c4: dastore
      // 08c5: dup
      // 08c6: sipush 297
      // 08c9: ldc2_w 1.839075686473352E-197
      // 08cc: dastore
      // 08cd: dup
      // 08ce: sipush 298
      // 08d1: ldc2_w 4.999126524757713E-197
      // 08d4: dastore
      // 08d5: dup
      // 08d6: sipush 299
      // 08d9: ldc2_w 1.3589033107846643E-196
      // 08dc: dastore
      // 08dd: dup
      // 08de: sipush 300
      // 08e1: ldc2_w 3.6938826366068014E-196
      // 08e4: dastore
      // 08e5: dup
      // 08e6: sipush 301
      // 08e9: ldc2_w 1.0041012794280992E-195
      // 08ec: dastore
      // 08ed: dup
      // 08ee: sipush 302
      // 08f1: ldc2_w 2.7294301888986675E-195
      // 08f4: dastore
      // 08f5: dup
      // 08f6: sipush 303
      // 08f9: ldc2_w 7.419361045185406E-195
      // 08fc: dastore
      // 08fd: dup
      // 08fe: sipush 304
      // 0901: ldc2_w 2.016791373353671E-194
      // 0904: dastore
      // 0905: dup
      // 0906: sipush 305
      // 0909: ldc2_w 5.482208065983983E-194
      // 090c: dastore
      // 090d: dup
      // 090e: sipush 306
      // 0911: ldc2_w 1.490218341008089E-193
      // 0914: dastore
      // 0915: dup
      // 0916: sipush 307
      // 0919: ldc2_w 4.050833763855709E-193
      // 091c: dastore
      // 091d: dup
      // 091e: sipush 308
      // 0921: ldc2_w 1.101130773265179E-192
      // 0924: dastore
      // 0925: dup
      // 0926: sipush 309
      // 0929: ldc2_w 2.993183789477209E-192
      // 092c: dastore
      // 092d: dup
      // 092e: sipush 310
      // 0931: ldc2_w 8.136316299122392E-192
      // 0934: dastore
      // 0935: dup
      // 0936: sipush 311
      // 0939: ldc2_w 2.2116799789922265E-191
      // 093c: dastore
      // 093d: dup
      // 093e: sipush 312
      // 0941: ldc2_w 6.011969568315371E-191
      // 0944: dastore
      // 0945: dup
      // 0946: sipush 313
      // 0949: ldc2_w 1.6342228966392253E-190
      // 094c: dastore
      // 094d: dup
      // 094e: sipush 314
      // 0951: ldc2_w 4.4422779589171113E-190
      // 0954: dastore
      // 0955: dup
      // 0956: sipush 315
      // 0959: ldc2_w 1.2075364784547675E-189
      // 095c: dastore
      // 095d: dup
      // 095e: sipush 316
      // 0961: ldc2_w 3.282424571107068E-189
      // 0964: dastore
      // 0965: dup
      // 0966: sipush 317
      // 0969: ldc2_w 8.92255448602772E-189
      // 096c: dastore
      // 096d: dup
      // 096e: sipush 318
      // 0971: ldc2_w 2.425402115319395E-188
      // 0974: dastore
      // 0975: dup
      // 0976: sipush 319
      // 0979: ldc2_w 6.592926904915355E-188
      // 097c: dastore
      // 097d: dup
      // 097e: sipush 320
      // 0981: ldc2_w 1.79214305133496E-187
      // 0984: dastore
      // 0985: dup
      // 0986: sipush 321
      // 0989: ldc2_w 4.871550528055661E-187
      // 098c: dastore
      // 098d: dup
      // 098e: sipush 322
      // 0991: ldc2_w 1.3242245776666673E-186
      // 0994: dastore
      // 0995: dup
      // 0996: sipush 323
      // 0999: ldc2_w 3.599615946028287E-186
      // 099c: dastore
      // 099d: dup
      // 099e: sipush 324
      // 09a1: ldc2_w 9.78476998200719E-186
      // 09a4: dastore
      // 09a5: dup
      // 09a6: sipush 325
      // 09a9: ldc2_w 2.659776075359514E-185
      // 09ac: dastore
      // 09ad: dup
      // 09ae: sipush 326
      // 09b1: ldc2_w 7.230020851688713E-185
      // 09b4: dastore
      // 09b5: dup
      // 09b6: sipush 327
      // 09b9: ldc2_w 1.9653234116333892E-184
      // 09bc: dastore
      // 09bd: dup
      // 09be: sipush 328
      // 09c1: ldc2_w 5.34230278107224E-184
      // 09c4: dastore
      // 09c5: dup
      // 09c6: sipush 329
      // 09c9: ldc2_w 1.4521887058451231E-183
      // 09cc: dastore
      // 09cd: dup
      // 09ce: sipush 330
      // 09d1: ldc2_w 3.947457923821984E-183
      // 09d4: dastore
      // 09d5: dup
      // 09d6: sipush 331
      // 09d9: ldc2_w 1.0730302255093144E-182
      // 09dc: dastore
      // 09dd: dup
      // 09de: sipush 332
      // 09e1: ldc2_w 2.9167986204137332E-182
      // 09e4: dastore
      // 09e5: dup
      // 09e6: sipush 333
      // 09e9: ldc2_w 7.928680793406766E-182
      // 09ec: dastore
      // 09ed: dup
      // 09ee: sipush 334
      // 09f1: ldc2_w 2.1552386987482013E-181
      // 09f4: dastore
      // 09f5: dup
      // 09f6: sipush 335
      // 09f9: ldc2_w 5.858546779607288E-181
      // 09fc: dastore
      // 09fd: dup
      // 09fe: sipush 336
      // 0a01: ldc2_w 1.5925182066949723E-180
      // 0a04: dastore
      // 0a05: dup
      // 0a06: sipush 337
      // 0a09: ldc2_w 4.328913614497258E-180
      // 0a0c: dastore
      // 0a0d: dup
      // 0a0e: sipush 338
      // 0a11: ldc2_w 1.1767205227552116E-179
      // 0a14: dastore
      // 0a15: dup
      // 0a16: sipush 339
      // 0a19: ldc2_w 3.198658219194836E-179
      // 0a1c: dastore
      // 0a1d: dup
      // 0a1e: sipush 340
      // 0a21: ldc2_w 8.694853785564504E-179
      // 0a24: dastore
      // 0a25: dup
      // 0a26: sipush 341
      // 0a29: ldc2_w 2.363506255864984E-178
      // 0a2c: dastore
      // 0a2d: dup
      // 0a2e: sipush 342
      // 0a31: ldc2_w 6.42467573615509E-178
      // 0a34: dastore
      // 0a35: dup
      // 0a36: sipush 343
      // 0a39: ldc2_w 1.746408207555959E-177
      // 0a3c: dastore
      // 0a3d: dup
      // 0a3e: sipush 344
      // 0a41: ldc2_w 4.747229597770176E-177
      // 0a44: dastore
      // 0a45: dup
      // 0a46: sipush 345
      // 0a49: ldc2_w 1.2904307529671472E-176
      // 0a4c: dastore
      // 0a4d: dup
      // 0a4e: sipush 346
      // 0a51: ldc2_w 3.507754341050756E-176
      // 0a54: dastore
      // 0a55: dup
      // 0a56: sipush 347
      // 0a59: ldc2_w 9.535066345267336E-176
      // 0a5c: dastore
      // 0a5d: dup
      // 0a5e: sipush 348
      // 0a61: ldc2_w 2.591899541396432E-175
      // 0a64: dastore
      // 0a65: dup
      // 0a66: sipush 349
      // 0a69: ldc2_w 7.045512786902009E-175
      // 0a6c: dastore
      // 0a6d: dup
      // 0a6e: sipush 350
      // 0a71: ldc2_w 1.9151693415969248E-174
      // 0a74: dastore
      // 0a75: dup
      // 0a76: sipush 351
      // 0a79: ldc2_w 5.205969622575851E-174
      // 0a7c: dastore
      // 0a7d: dup
      // 0a7e: sipush 352
      // 0a81: ldc2_w 1.4151292367806538E-173
      // 0a84: dastore
      // 0a85: dup
      // 0a86: sipush 353
      // 0a89: ldc2_w 3.846720258072078E-173
      // 0a8c: dastore
      // 0a8d: dup
      // 0a8e: sipush 354
      // 0a91: ldc2_w 1.045647032279984E-172
      // 0a94: dastore
      // 0a95: dup
      // 0a96: sipush 355
      // 0a99: ldc2_w 2.8423629805010285E-172
      // 0a9c: dastore
      // 0a9d: dup
      // 0a9e: sipush 356
      // 0aa1: ldc2_w 7.726344058192276E-172
      // 0aa4: dastore
      // 0aa5: dup
      // 0aa6: sipush 357
      // 0aa9: ldc2_w 2.1002377128928765E-171
      // 0aac: dastore
      // 0aad: dup
      // 0aae: sipush 358
      // 0ab1: ldc2_w 5.709039546124285E-171
      // 0ab4: dastore
      // 0ab5: dup
      // 0ab6: sipush 359
      // 0ab9: ldc2_w 1.5518778128928824E-170
      // 0abc: dastore
      // 0abd: dup
      // 0abe: sipush 360
      // 0ac1: ldc2_w 4.218440703602533E-170
      // 0ac4: dastore
      // 0ac5: dup
      // 0ac6: sipush 361
      // 0ac9: ldc2_w 1.1466910691560932E-169
      // 0acc: dastore
      // 0acd: dup
      // 0ace: sipush 362
      // 0ad1: ldc2_w 3.1170298734336303E-169
      // 0ad4: dastore
      // 0ad5: dup
      // 0ad6: sipush 363
      // 0ad9: ldc2_w 8.472965161251656E-169
      // 0adc: dastore
      // 0add: dup
      // 0ade: sipush 364
      // 0ae1: ldc2_w 2.303190374523956E-168
      // 0ae4: dastore
      // 0ae5: dup
      // 0ae6: sipush 365
      // 0ae9: ldc2_w 6.260720440258473E-168
      // 0aec: dastore
      // 0aed: dup
      // 0aee: sipush 366
      // 0af1: ldc2_w 1.701840523821621E-167
      // 0af4: dastore
      // 0af5: dup
      // 0af6: sipush 367
      // 0af9: ldc2_w 4.62608152166211E-167
      // 0afc: dastore
      // 0afd: dup
      // 0afe: sipush 368
      // 0b01: ldc2_w 1.2574995962791943E-166
      // 0b04: dastore
      // 0b05: dup
      // 0b06: sipush 369
      // 0b09: ldc2_w 3.418237608335161E-166
      // 0b0c: dastore
      // 0b0d: dup
      // 0b0e: sipush 370
      // 0b11: ldc2_w 9.29173407843235E-166
      // 0b14: dastore
      // 0b15: dup
      // 0b16: sipush 371
      // 0b19: ldc2_w 2.5257552661512635E-165
      // 0b1c: dastore
      // 0b1d: dup
      // 0b1e: sipush 372
      // 0b21: ldc2_w 6.865714679174435E-165
      // 0b24: dastore
      // 0b25: dup
      // 0b26: sipush 373
      // 0b29: ldc2_w 1.866294830116931E-164
      // 0b2c: dastore
      // 0b2d: dup
      // 0b2e: sipush 374
      // 0b31: ldc2_w 5.073114566291778E-164
      // 0b34: dastore
      // 0b35: dup
      // 0b36: sipush 375
      // 0b39: ldc2_w 1.3790154522394582E-163
      // 0b3c: dastore
      // 0b3d: dup
      // 0b3e: sipush 376
      // 0b41: ldc2_w 3.7485528226129495E-163
      // 0b44: dastore
      // 0b45: dup
      // 0b46: sipush 377
      // 0b49: ldc2_w 1.0189624503698769E-162
      // 0b4c: dastore
      // 0b4d: dup
      // 0b4e: sipush 378
      // 0b51: ldc2_w 2.7698267293941856E-162
      // 0b54: dastore
      // 0b55: dup
      // 0b56: sipush 379
      // 0b59: ldc2_w 7.529170882336924E-162
      // 0b5c: dastore
      // 0b5d: dup
      // 0b5e: sipush 380
      // 0b61: ldc2_w 2.0466404088178596E-161
      // 0b64: dastore
      // 0b65: dup
      // 0b66: sipush 381
      // 0b69: ldc2_w 5.56334611651382E-161
      // 0b6c: dastore
      // 0b6d: dup
      // 0b6e: sipush 382
      // 0b71: ldc2_w 1.512274346576166E-160
      // 0b74: dastore
      // 0b75: dup
      // 0b76: sipush 383
      // 0b79: ldc2_w 4.110787043867721E-160
      // 0b7c: dastore
      // 0b7d: dup
      // 0b7e: sipush 384
      // 0b81: ldc2_w 1.1174279267498045E-159
      // 0b84: dastore
      // 0b85: dup
      // 0b86: sipush 385
      // 0b89: ldc2_w 3.0374839443564585E-159
      // 0b8c: dastore
      // 0b8d: dup
      // 0b8e: sipush 386
      // 0b91: ldc2_w 8.25673801176584E-159
      // 0b94: dastore
      // 0b95: dup
      // 0b96: sipush 387
      // 0b99: ldc2_w 2.244414150254963E-158
      // 0b9c: dastore
      // 0b9d: dup
      // 0b9e: sipush 388
      // 0ba1: ldc2_w 6.1009492034592176E-158
      // 0ba4: dastore
      // 0ba5: dup
      // 0ba6: sipush 389
      // 0ba9: ldc2_w 1.6584100275603453E-157
      // 0bac: dastore
      // 0bad: dup
      // 0bae: sipush 390
      // 0bb1: ldc2_w 4.50802633729044E-157
      // 0bb4: dastore
      // 0bb5: dup
      // 0bb6: sipush 391
      // 0bb9: ldc2_w 1.2254085656601853E-156
      // 0bbc: dastore
      // 0bbd: dup
      // 0bbe: sipush 392
      // 0bc1: ldc2_w 3.3310057014599044E-156
      // 0bc4: dastore
      // 0bc5: dup
      // 0bc6: sipush 393
      // 0bc9: ldc2_w 9.054612259832416E-156
      // 0bcc: dastore
      // 0bcd: dup
      // 0bce: sipush 394
      // 0bd1: ldc2_w 2.4612985502035675E-155
      // 0bd4: dastore
      // 0bd5: dup
      // 0bd6: sipush 395
      // 0bd9: ldc2_w 6.690503835950083E-155
      // 0bdc: dastore
      // 0bdd: dup
      // 0bde: sipush 396
      // 0be1: ldc2_w 1.8186679660152888E-154
      // 0be4: dastore
      // 0be5: dup
      // 0be6: sipush 397
      // 0be9: ldc2_w 4.9436516047443576E-154
      // 0bec: dastore
      // 0bed: dup
      // 0bee: sipush 398
      // 0bf1: ldc2_w 1.3438240331106108E-153
      // 0bf4: dastore
      // 0bf5: dup
      // 0bf6: sipush 399
      // 0bf9: ldc2_w 3.652892398145774E-153
      // 0bfc: dastore
      // 0bfd: dup
      // 0bfe: sipush 400
      // 0c01: ldc2_w 9.92958982547828E-153
      // 0c04: dastore
      // 0c05: dup
      // 0c06: sipush 401
      // 0c09: ldc2_w 2.6991427376823027E-152
      // 0c0c: dastore
      // 0c0d: dup
      // 0c0e: sipush 402
      // 0c11: ldc2_w 7.3370297995122135E-152
      // 0c14: dastore
      // 0c15: dup
      // 0c16: sipush 403
      // 0c19: ldc2_w 1.994411660450821E-151
      // 0c1c: dastore
      // 0c1d: dup
      // 0c1e: sipush 404
      // 0c21: ldc2_w 5.421372463189529E-151
      // 0c24: dastore
      // 0c25: dup
      // 0c26: sipush 405
      // 0c29: ldc2_w 1.4736818914204564E-150
      // 0c2c: dastore
      // 0c2d: dup
      // 0c2e: sipush 406
      // 0c31: ldc2_w 4.005882964287806E-150
      // 0c34: dastore
      // 0c35: dup
      // 0c36: sipush 407
      // 0c39: ldc2_w 1.088911919926534E-149
      // 0c3c: dastore
      // 0c3d: dup
      // 0c3e: sipush 408
      // 0c41: ldc2_w 2.9599693109692324E-149
      // 0c44: dastore
      // 0c45: dup
      // 0c46: sipush 409
      // 0c49: ldc2_w 8.046030012041041E-149
      // 0c4c: dastore
      // 0c4d: dup
      // 0c4e: sipush 410
      // 0c51: ldc2_w 2.18713790898745E-148
      // 0c54: dastore
      // 0c55: dup
      // 0c56: sipush 411
      // 0c59: ldc2_w 5.945256705384597E-148
      // 0c5c: dastore
      // 0c5d: dup
      // 0c5e: sipush 412
      // 0c61: ldc2_w 1.6160884846515524E-147
      // 0c64: dastore
      // 0c65: dup
      // 0c66: sipush 413
      // 0c69: ldc2_w 4.392983574030969E-147
      // 0c6c: dastore
      // 0c6d: dup
      // 0c6e: sipush 414
      // 0c71: ldc2_w 1.1941366764543551E-146
      // 0c74: dastore
      // 0c75: dup
      // 0c76: sipush 415
      // 0c79: ldc2_w 3.2460001983475855E-146
      // 0c7c: dastore
      // 0c7d: dup
      // 0c7e: sipush 416
      // 0c81: ldc2_w 8.8235440586675E-146
      // 0c84: dastore
      // 0c85: dup
      // 0c86: sipush 417
      // 0c89: ldc2_w 2.3984878190403553E-145
      // 0c8c: dastore
      // 0c8d: dup
      // 0c8e: sipush 418
      // 0c91: ldc2_w 6.519765758635405E-145
      // 0c94: dastore
      // 0c95: dup
      // 0c96: sipush 419
      // 0c99: ldc2_w 1.772256261139753E-144
      // 0c9c: dastore
      // 0c9d: dup
      // 0c9e: sipush 420
      // 0ca1: ldc2_w 4.817491674217065E-144
      // 0ca4: dastore
      // 0ca5: dup
      // 0ca6: sipush 421
      // 0ca9: ldc2_w 1.3095299991573769E-143
      // 0cac: dastore
      // 0cad: dup
      // 0cae: sipush 422
      // 0cb1: ldc2_w 3.559671483107555E-143
      // 0cb4: dastore
      // 0cb5: dup
      // 0cb6: sipush 423
      // 0cb9: ldc2_w 9.676190774054103E-143
      // 0cbc: dastore
      // 0cbd: dup
      // 0cbe: sipush 424
      // 0cc1: ldc2_w 2.630261301303634E-142
      // 0cc4: dastore
      // 0cc5: dup
      // 0cc6: sipush 425
      // 0cc9: ldc2_w 7.149792225695347E-142
      // 0ccc: dastore
      // 0ccd: dup
      // 0cce: sipush 426
      // 0cd1: ldc2_w 1.943514969662872E-141
      // 0cd4: dastore
      // 0cd5: dup
      // 0cd6: sipush 427
      // 0cd9: ldc2_w 5.283020542151163E-141
      // 0cdc: dastore
      // 0cdd: dup
      // 0cde: sipush 428
      // 0ce1: ldc2_w 1.4360739330834996E-140
      // 0ce4: dastore
      // 0ce5: dup
      // 0ce6: sipush 429
      // 0ce9: ldc2_w 3.9036541111764032E-140
      // 0cec: dastore
      // 0ced: dup
      // 0cee: sipush 430
      // 0cf1: ldc2_w 1.0611230602364477E-139
      // 0cf4: dastore
      // 0cf5: dup
      // 0cf6: sipush 431
      // 0cf9: ldc2_w 2.8844319473099593E-139
      // 0cfc: dastore
      // 0cfd: dup
      // 0cfe: sipush 432
      // 0d01: ldc2_w 7.84069876400596E-139
      // 0d04: dastore
      // 0d05: dup
      // 0d06: sipush 433
      // 0d09: ldc2_w 2.1313228444765414E-138
      // 0d0c: dastore
      // 0d0d: dup
      // 0d0e: sipush 434
      // 0d11: ldc2_w 5.793536445518422E-138
      // 0d14: dastore
      // 0d15: dup
      // 0d16: sipush 435
      // 0d19: ldc2_w 1.5748463788034308E-137
      // 0d1c: dastore
      // 0d1d: dup
      // 0d1e: sipush 436
      // 0d21: ldc2_w 4.2808762411845363E-137
      // 0d24: dastore
      // 0d25: dup
      // 0d26: sipush 437
      // 0d29: ldc2_w 1.1636629220608724E-136
      // 0d2c: dastore
      // 0d2d: dup
      // 0d2e: sipush 438
      // 0d31: ldc2_w 3.163163464591171E-136
      // 0d34: dastore
      // 0d35: dup
      // 0d36: sipush 439
      // 0d39: ldc2_w 8.598369704466743E-136
      // 0d3c: dastore
      // 0d3d: dup
      // 0d3e: sipush 440
      // 0d41: ldc2_w 2.337279322276433E-135
      // 0d44: dastore
      // 0d45: dup
      // 0d46: sipush 441
      // 0d49: ldc2_w 6.353384093665193E-135
      // 0d4c: dastore
      // 0d4d: dup
      // 0d4e: sipush 442
      // 0d51: ldc2_w 1.7270287031459572E-134
      // 0d54: dastore
      // 0d55: dup
      // 0d56: sipush 443
      // 0d59: ldc2_w 4.694550492773212E-134
      // 0d5c: dastore
      // 0d5d: dup
      // 0d5e: sipush 444
      // 0d61: ldc2_w 1.2761111606368036E-133
      // 0d64: dastore
      // 0d65: dup
      // 0d66: sipush 445
      // 0d69: ldc2_w 3.4688299108856403E-133
      // 0d6c: dastore
      // 0d6d: dup
      // 0d6e: sipush 446
      // 0d71: ldc2_w 9.429257929713919E-133
      // 0d74: dastore
      // 0d75: dup
      // 0d76: sipush 447
      // 0d79: ldc2_w 2.5631381141873417E-132
      // 0d7c: dastore
      // 0d7d: dup
      // 0d7e: sipush 448
      // 0d81: ldc2_w 6.967331001069377E-132
      // 0d84: dastore
      // 0d85: dup
      // 0d86: sipush 449
      // 0d89: ldc2_w 1.8939170679975288E-131
      // 0d8c: dastore
      // 0d8d: dup
      // 0d8e: sipush 450
      // 0d91: ldc2_w 5.148199748336684E-131
      // 0d94: dastore
      // 0d95: dup
      // 0d96: sipush 451
      // 0d99: ldc2_w 1.3994258162094293E-130
      // 0d9c: dastore
      // 0d9d: dup
      // 0d9e: sipush 452
      // 0da1: ldc2_w 3.804034213613942E-130
      // 0da4: dastore
      // 0da5: dup
      // 0da6: sipush 453
      // 0da9: ldc2_w 1.0340436948077763E-129
      // 0dac: dastore
      // 0dad: dup
      // 0dae: sipush 454
      // 0db1: ldc2_w 2.8108219632627907E-129
      // 0db4: dastore
      // 0db5: dup
      // 0db6: sipush 455
      // 0db9: ldc2_w 7.640606938467665E-129
      // 0dbc: dastore
      // 0dbd: dup
      // 0dbe: sipush 456
      // 0dc1: ldc2_w 2.0769322678328357E-128
      // 0dc4: dastore
      // 0dc5: dup
      // 0dc6: sipush 457
      // 0dc9: ldc2_w 5.645687086879944E-128
      // 0dcc: dastore
      // 0dcd: dup
      // 0dce: sipush 458
      // 0dd1: ldc2_w 1.5346568127351796E-127
      // 0dd4: dastore
      // 0dd5: dup
      // 0dd6: sipush 459
      // 0dd9: ldc2_w 4.171630237420918E-127
      // 0ddc: dastore
      // 0ddd: dup
      // 0dde: sipush 460
      // 0de1: ldc2_w 1.1339665711932977E-126
      // 0de4: dastore
      // 0de5: dup
      // 0de6: sipush 461
      // 0de9: ldc2_w 3.0824406750909563E-126
      // 0dec: dastore
      // 0ded: dup
      // 0dee: sipush 462
      // 0df1: ldc2_w 8.37894218404787E-126
      // 0df4: dastore
      // 0df5: dup
      // 0df6: sipush 463
      // 0df9: ldc2_w 2.2776327994966818E-125
      // 0dfc: dastore
      // 0dfd: dup
      // 0dfe: sipush 464
      // 0e01: ldc2_w 6.191247522703296E-125
      // 0e04: dastore
      // 0e05: dup
      // 0e06: sipush 465
      // 0e09: ldc2_w 1.6829556040859853E-124
      // 0e0c: dastore
      // 0e0d: dup
      // 0e0e: sipush 466
      // 0e11: ldc2_w 4.5747479502862494E-124
      // 0e14: dastore
      // 0e15: dup
      // 0e16: sipush 467
      // 0e19: ldc2_w 1.2435453481209945E-123
      // 0e1c: dastore
      // 0e1d: dup
      // 0e1e: sipush 468
      // 0e21: ldc2_w 3.3803067202247166E-123
      // 0e24: dastore
      // 0e25: dup
      // 0e26: sipush 469
      // 0e29: ldc2_w 9.188625696750548E-123
      // 0e2c: dastore
      // 0e2d: dup
      // 0e2e: sipush 470
      // 0e31: ldc2_w 2.4977273040076145E-122
      // 0e34: dastore
      // 0e35: dup
      // 0e36: sipush 471
      // 0e39: ldc2_w 6.789527378582775E-122
      // 0e3c: dastore
      // 0e3d: dup
      // 0e3e: sipush 472
      // 0e41: ldc2_w 1.845584943222965E-121
      // 0e44: dastore
      // 0e45: dup
      // 0e46: sipush 473
      // 0e49: ldc2_w 5.016820182185716E-121
      // 0e4c: dastore
      // 0e4d: dup
      // 0e4e: sipush 474
      // 0e51: ldc2_w 1.3637129731022491E-120
      // 0e54: dastore
      // 0e55: dup
      // 0e56: sipush 475
      // 0e59: ldc2_w 3.706956710275979E-120
      // 0e5c: dastore
      // 0e5d: dup
      // 0e5e: sipush 476
      // 0e61: ldc2_w 1.0076552294433743E-119
      // 0e64: dastore
      // 0e65: dup
      // 0e66: sipush 477
      // 0e69: ldc2_w 2.739090595934893E-119
      // 0e6c: dastore
      // 0e6d: dup
      // 0e6e: sipush 478
      // 0e71: ldc2_w 7.445620503219039E-119
      // 0e74: dastore
      // 0e75: dup
      // 0e76: sipush 479
      // 0e79: ldc2_w 2.023929422267303E-118
      // 0e7c: dastore
      // 0e7d: dup
      // 0e7e: sipush 480
      // 0e81: ldc2_w 5.501611507503037E-118
      // 0e84: dastore
      // 0e85: dup
      // 0e86: sipush 481
      // 0e89: ldc2_w 1.4954928881576769E-117
      // 0e8c: dastore
      // 0e8d: dup
      // 0e8e: sipush 482
      // 0e91: ldc2_w 4.0651709187617596E-117
      // 0e94: dastore
      // 0e95: dup
      // 0e96: sipush 483
      // 0e99: ldc2_w 1.1050280679513555E-116
      // 0e9c: dastore
      // 0e9d: dup
      // 0e9e: sipush 484
      // 0ea1: ldc2_w 3.003777734030334E-116
      // 0ea4: dastore
      // 0ea5: dup
      // 0ea6: sipush 485
      // 0ea9: ldc2_w 8.165114384910189E-116
      // 0eac: dastore
      // 0ead: dup
      // 0eae: sipush 486
      // 0eb1: ldc2_w 2.219508285637377E-115
      // 0eb4: dastore
      // 0eb5: dup
      // 0eb6: sipush 487
      // 0eb9: ldc2_w 6.033249389304709E-115
      // 0ebc: dastore
      // 0ebd: dup
      // 0ebe: sipush 488
      // 0ec1: ldc2_w 1.6400070480930697E-114
      // 0ec4: dastore
      // 0ec5: dup
      // 0ec6: sipush 489
      // 0ec9: ldc2_w 4.458001565878111E-114
      // 0ecc: dastore
      // 0ecd: dup
      // 0ece: sipush 490
      // 0ed1: ldc2_w 1.2118105325725891E-113
      // 0ed4: dastore
      // 0ed5: dup
      // 0ed6: sipush 491
      // 0ed9: ldc2_w 3.2940421731384895E-113
      // 0edc: dastore
      // 0edd: dup
      // 0ede: sipush 492
      // 0ee1: ldc2_w 8.954135150208654E-113
      // 0ee4: dastore
      // 0ee5: dup
      // 0ee6: sipush 493
      // 0ee9: ldc2_w 2.433986351722258E-112
      // 0eec: dastore
      // 0eed: dup
      // 0eee: sipush 494
      // 0ef1: ldc2_w 6.616260705434716E-112
      // 0ef4: dastore
      // 0ef5: dup
      // 0ef6: sipush 495
      // 0ef9: ldc2_w 1.7984863104885375E-111
      // 0efc: dastore
      // 0efd: dup
      // 0efe: sipush 496
      // 0f01: ldc2_w 4.888792154132158E-111
      // 0f04: dastore
      // 0f05: dup
      // 0f06: sipush 497
      // 0f09: ldc2_w 1.3289115531074511E-110
      // 0f0c: dastore
      // 0f0d: dup
      // 0f0e: sipush 498
      // 0f11: ldc2_w 3.612356038181234E-110
      // 0f14: dastore
      // 0f15: dup
      // 0f16: sipush 499
      // 0f19: ldc2_w 9.819402293160495E-110
      // 0f1c: dastore
      // 0f1d: dup
      // 0f1e: sipush 500
      // 0f21: ldc2_w 2.6691899766673256E-109
      // 0f24: dastore
      // 0f25: dup
      // 0f26: sipush 501
      // 0f29: ldc2_w 7.255611264437603E-109
      // 0f2c: dastore
      // 0f2d: dup
      // 0f2e: sipush 502
      // 0f31: ldc2_w 1.9722796756250217E-108
      // 0f34: dastore
      // 0f35: dup
      // 0f36: sipush 503
      // 0f39: ldc2_w 5.361211684173837E-108
      // 0f3c: dastore
      // 0f3d: dup
      // 0f3e: sipush 504
      // 0f41: ldc2_w 1.4573285967670963E-107
      // 0f44: dastore
      // 0f45: dup
      // 0f46: sipush 505
      // 0f49: ldc2_w 3.961429477016909E-107
      // 0f4c: dastore
      // 0f4d: dup
      // 0f4e: sipush 506
      // 0f51: ldc2_w 1.0768281419102595E-106
      // 0f54: dastore
      // 0f55: dup
      // 0f56: sipush 507
      // 0f59: ldc2_w 2.9271223293841774E-106
      // 0f5c: dastore
      // 0f5d: dup
      // 0f5e: sipush 508
      // 0f61: ldc2_w 7.956744351476403E-106
      // 0f64: dastore
      // 0f65: dup
      // 0f66: sipush 509
      // 0f69: ldc2_w 2.1628672925745152E-105
      // 0f6c: dastore
      // 0f6d: dup
      // 0f6e: sipush 510
      // 0f71: ldc2_w 5.879282834821692E-105
      // 0f74: dastore
      // 0f75: dup
      // 0f76: sipush 511
      // 0f79: ldc2_w 1.5981547034872092E-104
      // 0f7c: dastore
      // 0f7d: dup
      // 0f7e: sipush 512
      // 0f81: ldc2_w 4.344234755347641E-104
      // 0f84: dastore
      // 0f85: dup
      // 0f86: sipush 513
      // 0f89: ldc2_w 1.1808855501885005E-103
      // 0f8c: dastore
      // 0f8d: dup
      // 0f8e: sipush 514
      // 0f91: ldc2_w 3.2099795870407646E-103
      // 0f94: dastore
      // 0f95: dup
      // 0f96: sipush 515
      // 0f99: ldc2_w 8.725629524586503E-103
      // 0f9c: dastore
      // 0f9d: dup
      // 0f9e: sipush 516
      // 0fa1: ldc2_w 2.3718718327094683E-102
      // 0fa4: dastore
      // 0fa5: dup
      // 0fa6: sipush 517
      // 0fa9: ldc2_w 6.44741641521183E-102
      // 0fac: dastore
      // 0fad: dup
      // 0fae: sipush 518
      // 0fb1: ldc2_w 1.7525895549820557E-101
      // 0fb4: dastore
      // 0fb5: dup
      // 0fb6: sipush 519
      // 0fb9: ldc2_w 4.7640323331013947E-101
      // 0fbc: dastore
      // 0fbd: dup
      // 0fbe: sipush 520
      // 0fc1: ldc2_w 1.2949980563724296E-100
      // 0fc4: dastore
      // 0fc5: dup
      // 0fc6: sipush 521
      // 0fc9: ldc2_w 3.5201699899499525E-100
      // 0fcc: dastore
      // 0fcd: dup
      // 0fce: sipush 522
      // 0fd1: ldc2_w 9.56881327374431E-100
      // 0fd4: dastore
      // 0fd5: dup
      // 0fd6: sipush 523
      // 0fd9: ldc2_w 2.6010732940533088E-99
      // 0fdc: dastore
      // 0fdd: dup
      // 0fde: sipush 524
      // 0fe1: ldc2_w 7.070450309820548E-99
      // 0fe4: dastore
      // 0fe5: dup
      // 0fe6: sipush 525
      // 0fe9: ldc2_w 1.9219478787856753E-98
      // 0fec: dastore
      // 0fed: dup
      // 0fee: sipush 526
      // 0ff1: ldc2_w 5.2243955659975294E-98
      // 0ff4: dastore
      // 0ff5: dup
      // 0ff6: sipush 527
      // 0ff9: ldc2_w 1.4201378353978042E-97
      // 0ffc: dastore
      // 0ffd: dup
      // 0ffe: sipush 528
      // 1001: ldc2_w 3.8603349913851996E-97
      // 1004: dastore
      // 1005: dup
      // 1006: sipush 529
      // 1009: ldc2_w 1.0493479260117497E-96
      // 100c: dastore
      // 100d: dup
      // 100e: sipush 530
      // 1011: ldc2_w 2.8524232604238555E-96
      // 1014: dastore
      // 1015: dup
      // 1016: sipush 531
      // 1019: ldc2_w 7.753690709912764E-96
      // 101c: dastore
      // 101d: dup
      // 101e: sipush 532
      // 1021: ldc2_w 2.1076716069929933E-95
      // 1024: dastore
      // 1025: dup
      // 1026: sipush 533
      // 1029: ldc2_w 5.72924572981599E-95
      // 102c: dastore
      // 102d: dup
      // 102e: sipush 534
      // 1031: ldc2_w 1.5573703263204683E-94
      // 1034: dastore
      // 1035: dup
      // 1036: sipush 535
      // 1039: ldc2_w 4.233371554108682E-94
      // 103c: dastore
      // 103d: dup
      // 103e: sipush 536
      // 1041: ldc2_w 1.1507496472539512E-93
      // 1044: dastore
      // 1045: dup
      // 1046: sipush 537
      // 1049: ldc2_w 3.1280620563875923E-93
      // 104c: dastore
      // 104d: dup
      // 104e: sipush 538
      // 1051: ldc2_w 8.5029538631631E-93
      // 1054: dastore
      // 1055: dup
      // 1056: sipush 539
      // 1059: ldc2_w 2.3113425190436427E-92
      // 105c: dastore
      // 105d: dup
      // 105e: sipush 540
      // 1061: ldc2_w 6.28287989314225E-92
      // 1064: dastore
      // 1065: dup
      // 1066: sipush 541
      // 1069: ldc2_w 1.7078641226055994E-91
      // 106c: dastore
      // 106d: dup
      // 106e: sipush 542
      // 1071: ldc2_w 4.6424556110307644E-91
      // 1074: dastore
      // 1075: dup
      // 1076: sipush 543
      // 1079: ldc2_w 1.261950308999819E-90
      // 107c: dastore
      // 107d: dup
      // 107e: sipush 544
      // 1081: ldc2_w 3.430336362898836E-90
      // 1084: dastore
      // 1085: dup
      // 1086: sipush 545
      // 1089: ldc2_w 9.324622137237299E-90
      // 108c: dastore
      // 108d: dup
      // 108e: sipush 546
      // 1091: ldc2_w 2.5346947846365435E-89
      // 1094: dastore
      // 1095: dup
      // 1096: sipush 547
      // 1099: ldc2_w 6.890014851450124E-89
      // 109c: dastore
      // 109d: dup
      // 109e: sipush 548
      // 10a1: ldc2_w 1.8729003560057785E-88
      // 10a4: dastore
      // 10a5: dup
      // 10a6: sipush 549
      // 10a9: ldc2_w 5.091070300111434E-88
      // 10ac: dastore
      // 10ad: dup
      // 10ae: sipush 550
      // 10b1: ldc2_w 1.3838964592430477E-87
      // 10b4: dastore
      // 10b5: dup
      // 10b6: sipush 551
      // 10b9: ldc2_w 3.761820584522275E-87
      // 10bc: dastore
      // 10bd: dup
      // 10be: sipush 552
      // 10c1: ldc2_w 1.0225689628581036E-86
      // 10c4: dastore
      // 10c5: dup
      // 10c6: sipush 553
      // 10c9: ldc2_w 2.7796303536272215E-86
      // 10cc: dastore
      // 10cd: dup
      // 10ce: sipush 554
      // 10d1: ldc2_w 7.555818934379333E-86
      // 10d4: dastore
      // 10d5: dup
      // 10d6: sipush 555
      // 10d9: ldc2_w 2.053884626293416E-85
      // 10dc: dastore
      // 10dd: dup
      // 10de: sipush 556
      // 10e1: ldc2_w 5.583037134407759E-85
      // 10e4: dastore
      // 10e5: dup
      // 10e6: sipush 557
      // 10e9: ldc2_w 1.5176268538776042E-84
      // 10ec: dastore
      // 10ed: dup
      // 10ee: sipush 558
      // 10f1: ldc2_w 4.125337057189083E-84
      // 10f4: dastore
      // 10f5: dup
      // 10f6: sipush 559
      // 10f9: ldc2_w 1.121383042095528E-83
      // 10fc: dastore
      // 10fd: dup
      // 10fe: sipush 560
      // 1101: ldc2_w 3.0482348236054953E-83
      // 1104: dastore
      // 1105: dup
      // 1106: sipush 561
      // 1109: ldc2_w 8.285962249116636E-83
      // 110c: dastore
      // 110d: dup
      // 110e: sipush 562
      // 1111: ldc2_w 2.2523580600947705E-82
      // 1114: dastore
      // 1115: dup
      // 1116: sipush 563
      // 1119: ldc2_w 6.122543452787843E-82
      // 111c: dastore
      // 111d: dup
      // 111e: sipush 564
      // 1121: ldc2_w 1.664279766968299E-81
      // 1124: dastore
      // 1125: dup
      // 1126: sipush 565
      // 1129: ldc2_w 4.523982262003404E-81
      // 112c: dastore
      // 112d: dup
      // 112e: sipush 566
      // 1131: ldc2_w 1.2297456769063303E-80
      // 1134: dastore
      // 1135: dup
      // 1136: sipush 567
      // 1139: ldc2_w 3.342795345742034E-80
      // 113c: dastore
      // 113d: dup
      // 113e: sipush 568
      // 1141: ldc2_w 9.086660081726823E-80
      // 1144: dastore
      // 1145: dup
      // 1146: sipush 569
      // 1149: ldc2_w 2.4700104681773258E-79
      // 114c: dastore
      // 114d: dup
      // 114e: sipush 570
      // 1151: ldc2_w 6.714184569587689E-79
      // 1154: dastore
      // 1155: dup
      // 1156: sipush 571
      // 1159: ldc2_w 1.8251046352720517E-78
      // 115c: dastore
      // 115d: dup
      // 115e: sipush 572
      // 1161: ldc2_w 4.961148056969105E-78
      // 1164: dastore
      // 1165: dup
      // 1166: sipush 573
      // 1169: ldc2_w 1.3485799924445315E-77
      // 116c: dastore
      // 116d: dup
      // 116e: sipush 574
      // 1171: ldc2_w 3.665820371396835E-77
      // 1174: dastore
      // 1175: dup
      // 1176: sipush 575
      // 1179: ldc2_w 9.964732578705785E-77
      // 117c: dastore
      // 117d: dup
      // 117e: sipush 576
      // 1181: ldc2_w 2.708695208461993E-76
      // 1184: dastore
      // 1185: dup
      // 1186: sipush 577
      // 1189: ldc2_w 7.362996533913695E-76
      // 118c: dastore
      // 118d: dup
      // 118e: sipush 578
      // 1191: ldc2_w 2.0014700145557332E-75
      // 1194: dastore
      // 1195: dup
      // 1196: sipush 579
      // 1199: ldc2_w 5.440559532453721E-75
      // 119c: dastore
      // 119d: dup
      // 119e: sipush 580
      // 11a1: ldc2_w 1.4788974793889734E-74
      // 11a4: dastore
      // 11a5: dup
      // 11a6: sipush 581
      // 11a9: ldc2_w 4.020060558571273E-74
      // 11ac: dastore
      // 11ad: dup
      // 11ae: sipush 582
      // 11b1: ldc2_w 1.092765612182012E-73
      // 11b4: dastore
      // 11b5: dup
      // 11b6: sipush 583
      // 11b9: ldc2_w 2.970445258959489E-73
      // 11bc: dastore
      // 11bd: dup
      // 11be: sipush 584
      // 11c1: ldc2_w 8.074507236705857E-73
      // 11c4: dastore
      // 11c5: dup
      // 11c6: sipush 585
      // 11c9: ldc2_w 2.1948784599535102E-72
      // 11cc: dastore
      // 11cd: dup
      // 11ce: sipush 586
      // 11d1: ldc2_w 5.966298125808066E-72
      // 11d4: dastore
      // 11d5: dup
      // 11d6: sipush 587
      // 11d9: ldc2_w 1.6218081151910012E-71
      // 11dc: dastore
      // 11dd: dup
      // 11de: sipush 588
      // 11e1: ldc2_w 4.408531734441582E-71
      // 11e4: dastore
      // 11e5: dup
      // 11e6: sipush 589
      // 11e9: ldc2_w 1.198363039426718E-70
      // 11ec: dastore
      // 11ed: dup
      // 11ee: sipush 590
      // 11f1: ldc2_w 3.257488853378793E-70
      // 11f4: dastore
      // 11f5: dup
      // 11f6: sipush 591
      // 11f9: ldc2_w 8.854771398921902E-70
      // 11fc: dastore
      // 11fd: dup
      // 11fe: sipush 592
      // 1201: ldc2_w 2.406976727302894E-69
      // 1204: dastore
      // 1205: dup
      // 1206: sipush 593
      // 1209: ldc2_w 6.542840888268955E-69
      // 120c: dastore
      // 120d: dup
      // 120e: sipush 594
      // 1211: ldc2_w 1.778528517418201E-68
      // 1214: dastore
      // 1215: dup
      // 1216: sipush 595
      // 1219: ldc2_w 4.834541417183388E-68
      // 121c: dastore
      // 121d: dup
      // 121e: sipush 596
      // 1221: ldc2_w 1.3141647465063647E-67
      // 1224: dastore
      // 1225: dup
      // 1226: sipush 597
      // 1229: ldc2_w 3.572270133517001E-67
      // 122c: dastore
      // 122d: dup
      // 122e: sipush 598
      // 1231: ldc2_w 9.710435805122717E-67
      // 1234: dastore
      // 1235: dup
      // 1236: sipush 599
      // 1239: ldc2_w 2.63957027915428E-66
      // 123c: dastore
      // 123d: dup
      // 123e: sipush 600
      // 1241: ldc2_w 7.175096392165733E-66
      // 1244: dastore
      // 1245: dup
      // 1246: sipush 601
      // 1249: ldc2_w 1.9503931430716318E-65
      // 124c: dastore
      // 124d: dup
      // 124e: sipush 602
      // 1251: ldc2_w 5.3017188565638215E-65
      // 1254: dastore
      // 1255: dup
      // 1256: sipush 603
      // 1259: ldc2_w 1.4411566290936352E-64
      // 125c: dastore
      // 125d: dup
      // 125e: sipush 604
      // 1261: ldc2_w 3.9174693825966044E-64
      // 1264: dastore
      // 1265: dup
      // 1266: sipush 605
      // 1269: ldc2_w 1.0648786018364265E-63
      // 126c: dastore
      // 126d: dup
      // 126e: sipush 606
      // 1271: ldc2_w 2.8946401383311E-63
      // 1274: dastore
      // 1275: dup
      // 1276: sipush 607
      // 1279: ldc2_w 7.868447965383903E-63
      // 127c: dastore
      // 127d: dup
      // 127e: sipush 608
      // 1281: ldc2_w 2.1388659707647114E-62
      // 1284: dastore
      // 1285: dup
      // 1286: sipush 609
      // 1289: ldc2_w 5.814040618670345E-62
      // 128c: dastore
      // 128d: dup
      // 128e: sipush 610
      // 1291: ldc2_w 1.5804200403673568E-61
      // 1294: dastore
      // 1295: dup
      // 1296: sipush 611
      // 1299: ldc2_w 4.296027044486766E-61
      // 129c: dastore
      // 129d: dup
      // 129e: sipush 612
      // 12a1: ldc2_w 1.1677812418806031E-60
      // 12a4: dastore
      // 12a5: dup
      // 12a6: sipush 613
      // 12a9: ldc2_w 3.174358801839755E-60
      // 12ac: dastore
      // 12ad: dup
      // 12ae: sipush 614
      // 12b1: ldc2_w 8.62880163941313E-60
      // 12b4: dastore
      // 12b5: dup
      // 12b6: sipush 615
      // 12b9: ldc2_w 2.345551464945955E-59
      // 12bc: dastore
      // 12bd: dup
      // 12be: sipush 616
      // 12c1: ldc2_w 6.3758692300917355E-59
      // 12c4: dastore
      // 12c5: dup
      // 12c6: sipush 617
      // 12c9: ldc2_w 1.733140900346534E-58
      // 12cc: dastore
      // 12cd: dup
      // 12ce: sipush 618
      // 12d1: ldc2_w 4.711165925070571E-58
      // 12d4: dastore
      // 12d5: dup
      // 12d6: sipush 619
      // 12d9: ldc2_w 1.2806275683797178E-57
      // 12dc: dastore
      // 12dd: dup
      // 12de: sipush 620
      // 12e1: ldc2_w 3.481106736845E-57
      // 12e4: dastore
      // 12e5: dup
      // 12e6: sipush 621
      // 12e9: ldc2_w 9.462629520363307E-57
      // 12ec: dastore
      // 12ed: dup
      // 12ee: sipush 622
      // 12f1: ldc2_w 2.5722094667974783E-56
      // 12f4: dastore
      // 12f5: dup
      // 12f6: sipush 623
      // 12f9: ldc2_w 6.9919903587080315E-56
      // 12fc: dastore
      // 12fd: dup
      // 12fe: sipush 624
      // 1301: ldc2_w 1.9006201022568844E-55
      // 1304: dastore
      // 1305: dup
      // 1306: sipush 625
      // 1309: ldc2_w 5.166420404109835E-55
      // 130c: dastore
      // 130d: dup
      // 130e: sipush 626
      // 1311: ldc2_w 1.4043786616805493E-54
      // 1314: dastore
      // 1315: dup
      // 1316: sipush 627
      // 1319: ldc2_w 3.8174968984748894E-54
      // 131c: dastore
      // 131d: dup
      // 131e: sipush 628
      // 1321: ldc2_w 1.03770335512154E-53
      // 1324: dastore
      // 1325: dup
      // 1326: sipush 629
      // 1329: ldc2_w 2.820769858672565E-53
      // 132c: dastore
      // 132d: dup
      // 132e: sipush 630
      // 1331: ldc2_w 7.667647949477605E-53
      // 1334: dastore
      // 1335: dup
      // 1336: sipush 631
      // 1339: ldc2_w 2.0842827711783212E-52
      // 133c: dastore
      // 133d: dup
      // 133e: sipush 632
      // 1341: ldc2_w 5.6656680900216754E-52
      // 1344: dastore
      // 1345: dup
      // 1346: sipush 633
      // 1349: ldc2_w 1.5400881501571645E-51
      // 134c: dastore
      // 134d: dup
      // 134e: sipush 634
      // 1351: ldc2_w 4.1863938339341257E-51
      // 1354: dastore
      // 1355: dup
      // 1356: sipush 635
      // 1359: ldc2_w 1.1379799629071911E-50
      // 135c: dastore
      // 135d: dup
      // 135e: sipush 636
      // 1361: ldc2_w 3.093350150840571E-50
      // 1364: dastore
      // 1365: dup
      // 1366: sipush 637
      // 1369: ldc2_w 8.408597060399334E-50
      // 136c: dastore
      // 136d: dup
      // 136e: sipush 638
      // 1371: ldc2_w 2.2856938448387544E-49
      // 1374: dastore
      // 1375: dup
      // 1376: sipush 639
      // 1379: ldc2_w 6.2131591878042886E-49
      // 137c: dastore
      // 137d: dup
      // 137e: sipush 640
      // 1381: ldc2_w 1.688911928929718E-48
      // 1384: dastore
      // 1385: dup
      // 1386: sipush 641
      // 1389: ldc2_w 4.5909386437919143E-48
      // 138c: dastore
      // 138d: dup
      // 138e: sipush 642
      // 1391: ldc2_w 1.2479464696643861E-47
      // 1394: dastore
      // 1395: dup
      // 1396: sipush 643
      // 1399: ldc2_w 3.3922703599272275E-47
      // 139c: dastore
      // 139d: dup
      // 139e: sipush 644
      // 13a1: ldc2_w 9.221146830884422E-47
      // 13a4: dastore
      // 13a5: dup
      // 13a6: sipush 645
      // 13a9: ldc2_w 2.5065676066043174E-46
      // 13ac: dastore
      // 13ad: dup
      // 13ae: sipush 646
      // 13b1: ldc2_w 6.8135571305481364E-46
      // 13b4: dastore
      // 13b5: dup
      // 13b6: sipush 647
      // 13b9: ldc2_w 1.8521166948363666E-45
      // 13bc: dastore
      // 13bd: dup
      // 13be: sipush 648
      // 13c1: ldc2_w 5.0345752964740226E-45
      // 13c4: dastore
      // 13c5: dup
      // 13c6: sipush 649
      // 13c9: ldc2_w 1.368539456379101E-44
      // 13cc: dastore
      // 13cd: dup
      // 13ce: sipush 650
      // 13d1: ldc2_w 3.720075801577098E-44
      // 13d4: dastore
      // 13d5: dup
      // 13d6: sipush 651
      // 13d9: ldc2_w 1.0112214979786464E-43
      // 13dc: dastore
      // 13dd: dup
      // 13de: sipush 652
      // 13e1: ldc2_w 2.7487849807248755E-43
      // 13e4: dastore
      // 13e5: dup
      // 13e6: sipush 653
      // 13e9: ldc2_w 7.47197247068667E-43
      // 13ec: dastore
      // 13ed: dup
      // 13ee: sipush 654
      // 13f1: ldc2_w 2.0310928323153876E-42
      // 13f4: dastore
      // 13f5: dup
      // 13f6: sipush 655
      // 13f9: ldc2_w 5.521082422279256E-42
      // 13fc: dastore
      // 13fd: dup
      // 13fe: sipush 656
      // 1401: ldc2_w 1.5007857288519654E-41
      // 1404: dastore
      // 1405: dup
      // 1406: sipush 657
      // 1409: ldc2_w 4.0795586181406803E-41
      // 140c: dastore
      // 140d: dup
      // 140e: sipush 658
      // 1411: ldc2_w 1.108938997126179E-40
      // 1414: dastore
      // 1415: dup
      // 1416: sipush 659
      // 1419: ldc2_w 3.0144088843073416E-40
      // 141c: dastore
      // 141d: dup
      // 141e: sipush 660
      // 1421: ldc2_w 8.194012195477669E-40
      // 1424: dastore
      // 1425: dup
      // 1426: sipush 661
      // 1429: ldc2_w 2.2273635587196807E-39
      // 142c: dastore
      // 142d: dup
      // 142e: sipush 662
      // 1431: ldc2_w 6.054601485195952E-39
      // 1434: dastore
      // 1435: dup
      // 1436: sipush 663
      // 1439: ldc2_w 1.6458113136245473E-38
      // 143c: dastore
      // 143d: dup
      // 143e: sipush 664
      // 1441: ldc2_w 4.473779311490168E-38
      // 1444: dastore
      // 1445: dup
      // 1446: sipush 665
      // 1449: ldc2_w 1.2160992719555806E-37
      // 144c: dastore
      // 144d: dup
      // 144e: sipush 666
      // 1451: ldc2_w 3.3057007442449645E-37
      // 1454: dastore
      // 1455: dup
      // 1456: sipush 667
      // 1459: ldc2_w 8.985825281444118E-37
      // 145c: dastore
      // 145d: dup
      // 145e: sipush 668
      // 1461: ldc2_w 2.442600707513088E-36
      // 1464: dastore
      // 1465: dup
      // 1466: sipush 669
      // 1469: ldc2_w 6.639677673630215E-36
      // 146c: dastore
      // 146d: dup
      // 146e: sipush 670
      // 1471: ldc2_w 1.8048513285848406E-35
      // 1474: dastore
      // 1475: dup
      // 1476: sipush 671
      // 1479: ldc2_w 4.906094420881007E-35
      // 147c: dastore
      // 147d: dup
      // 147e: sipush 672
      // 1481: ldc2_w 1.3336148713971936E-34
      // 1484: dastore
      // 1485: dup
      // 1486: sipush 673
      // 1489: ldc2_w 3.625141007634431E-34
      // 148c: dastore
      // 148d: dup
      // 148e: sipush 674
      // 1491: ldc2_w 9.854154449263851E-34
      // 1494: dastore
      // 1495: dup
      // 1496: sipush 675
      // 1499: ldc2_w 2.6786368134431636E-33
      // 149c: dastore
      // 149d: dup
      // 149e: sipush 676
      // 14a1: ldc2_w 7.28128971953363E-33
      // 14a4: dastore
      // 14a5: dup
      // 14a6: sipush 677
      // 14a9: ldc2_w 1.9792597720953414E-32
      // 14ac: dastore
      // 14ad: dup
      // 14ae: sipush 678
      // 14b1: ldc2_w 5.380185921962174E-32
      // 14b4: dastore
      // 14b5: dup
      // 14b6: sipush 679
      // 14b9: ldc2_w 1.4624861244004054E-31
      // 14bc: dastore
      // 14bd: dup
      // 14be: sipush 680
      // 14c1: ldc2_w 3.975449484028966E-31
      // 14c4: dastore
      // 14c5: dup
      // 14c6: sipush 681
      // 14c9: ldc2_w 1.080639291795678E-30
      // 14cc: dastore
      // 14cd: dup
      // 14ce: sipush 682
      // 14d1: ldc2_w 2.9374821418009058E-30
      // 14d4: dastore
      // 14d5: dup
      // 14d6: sipush 683
      // 14d9: ldc2_w 7.984904044796711E-30
      // 14dc: dastore
      // 14dd: dup
      // 14de: sipush 684
      // 14e1: ldc2_w 2.1705221445447534E-29
      // 14e4: dastore
      // 14e5: dup
      // 14e6: sipush 685
      // 14e9: ldc2_w 5.900089995748943E-29
      // 14ec: dastore
      // 14ed: dup
      // 14ee: sipush 686
      // 14f1: ldc2_w 1.6038109389511792E-28
      // 14f4: dastore
      // 14f5: dup
      // 14f6: sipush 687
      // 14f9: ldc2_w 4.359610133382778E-28
      // 14fc: dastore
      // 14fd: dup
      // 14fe: sipush 688
      // 1501: ldc2_w 1.185064946717304E-27
      // 1504: dastore
      // 1505: dup
      // 1506: sipush 689
      // 1509: ldc2_w 3.221340469489223E-27
      // 150c: dastore
      // 150d: dup
      // 150e: sipush 690
      // 1511: ldc2_w 8.756510122348782E-27
      // 1514: dastore
      // 1515: dup
      // 1516: sipush 691
      // 1519: ldc2_w 2.380266370880709E-26
      // 151c: dastore
      // 151d: dup
      // 151e: sipush 692
      // 1521: ldc2_w 6.47023467943241E-26
      // 1524: dastore
      // 1525: dup
      // 1526: sipush 693
      // 1529: ldc2_w 1.75879225876483E-25
      // 152c: dastore
      // 152d: dup
      // 152e: sipush 694
      // 1531: ldc2_w 4.780892502168074E-25
      // 1534: dastore
      // 1535: dup
      // 1536: sipush 695
      // 1539: ldc2_w 1.2995814853898995E-24
      // 153c: dastore
      // 153d: dup
      // 153e: sipush 696
      // 1541: ldc2_w 3.5326287852455166E-24
      // 1544: dastore
      // 1545: dup
      // 1546: sipush 697
      // 1549: ldc2_w 9.602680736954162E-24
      // 154c: dastore
      // 154d: dup
      // 154e: sipush 698
      // 1551: ldc2_w 2.6102792042257208E-23
      // 1554: dastore
      // 1555: dup
      // 1556: sipush 699
      // 1559: ldc2_w 7.095474414148981E-23
      // 155c: dastore
      // 155d: dup
      // 155e: sipush 700
      // 1561: ldc2_w 1.9287497671359936E-22
      // 1564: dastore
      // 1565: dup
      // 1566: sipush 701
      // 1569: ldc2_w 5.242885191553114E-22
      // 156c: dastore
      // 156d: dup
      // 156e: sipush 702
      // 1571: ldc2_w 1.4251641388208515E-21
      // 1574: dastore
      // 1575: dup
      // 1576: sipush 703
      // 1579: ldc2_w 3.873997809109103E-21
      // 157c: dastore
      // 157d: dup
      // 157e: sipush 704
      // 1581: ldc2_w 1.0530616658562386E-20
      // 1584: dastore
      // 1585: dup
      // 1586: sipush 705
      // 1589: ldc2_w 2.862518609581133E-20
      // 158c: dastore
      // 158d: dup
      // 158e: sipush 706
      // 1591: ldc2_w 7.78113163345177E-20
      // 1594: dastore
      // 1595: dup
      // 1596: sipush 707
      // 1599: ldc2_w 2.1151310700892382E-19
      // 159c: dastore
      // 159d: dup
      // 159e: sipush 708
      // 15a1: ldc2_w 5.74952254077566E-19
      // 15a4: dastore
      // 15a5: dup
      // 15a6: sipush 709
      // 15a9: ldc2_w 1.5628822871880503E-18
      // 15ac: dastore
      // 15ad: dup
      // 15ae: sipush 710
      // 15b1: ldc2_w 4.24835413113866E-18
      // 15b4: dastore
      // 15b5: dup
      // 15b6: sipush 711
      // 15b9: ldc2_w 1.1548223864099742E-17
      // 15bc: dastore
      // 15bd: dup
      // 15be: sipush 712
      // 15c1: ldc2_w 3.139132557537509E-17
      // 15c4: dastore
      // 15c5: dup
      // 15c6: sipush 713
      // 15c9: ldc2_w 8.533046968331264E-17
      // 15cc: dastore
      // 15cd: dup
      // 15ce: sipush 714
      // 15d1: ldc2_w 2.3195229636950566E-16
      // 15d4: dastore
      // 15d5: dup
      // 15d6: sipush 715
      // 15d9: ldc2_w 6.305116324200775E-16
      // 15dc: dastore
      // 15dd: dup
      // 15de: sipush 716
      // 15e1: ldc2_w 1.71390848833098E-15
      // 15e4: dastore
      // 15e5: dup
      // 15e6: sipush 717
      // 15e9: ldc2_w 4.6588861918718874E-15
      // 15ec: dastore
      // 15ed: dup
      // 15ee: sipush 718
      // 15f1: ldc2_w 1.2664165777252073E-14
      // 15f4: dastore
      // 15f5: dup
      // 15f6: sipush 719
      // 15f9: ldc2_w 3.442477422913037E-14
      // 15fc: dastore
      // 15fd: dup
      // 15fe: sipush 720
      // 1601: ldc2_w 9.357622912219837E-14
      // 1604: dastore
      // 1605: dup
      // 1606: sipush 721
      // 1609: ldc2_w 2.5436656904062604E-13
      // 160c: dastore
      // 160d: dup
      // 160e: sipush 722
      // 1611: ldc2_w 6.914399608426436E-13
      // 1614: dastore
      // 1615: dup
      // 1616: sipush 723
      // 1619: ldc2_w 1.879528650772233E-12
      // 161c: dastore
      // 161d: dup
      // 161e: sipush 724
      // 1621: ldc2_w 5.1090893668503945E-12
      // 1624: dastore
      // 1625: dup
      // 1626: sipush 725
      // 1629: ldc2_w 1.3887944613766301E-11
      // 162c: dastore
      // 162d: dup
      // 162e: sipush 726
      // 1631: ldc2_w 3.775134371775124E-11
      // 1634: dastore
      // 1635: dup
      // 1636: sipush 727
      // 1639: ldc2_w 1.0261880234452292E-10
      // 163c: dastore
      // 163d: dup
      // 163e: sipush 728
      // 1641: ldc2_w 2.789468100949932E-10
      // 1644: dastore
      // 1645: dup
      // 1646: sipush 729
      // 1649: ldc2_w 7.582560135332983E-10
      // 164c: dastore
      // 164d: dup
      // 164e: sipush 730
      // 1651: ldc2_w 2.061153470123145E-9
      // 1654: dastore
      // 1655: dup
      // 1656: sipush 731
      // 1659: ldc2_w 5.602796449011294E-9
      // 165c: dastore
      // 165d: dup
      // 165e: sipush 732
      // 1661: ldc2_w 1.5229979055675358E-8
      // 1664: dastore
      // 1665: dup
      // 1666: sipush 733
      // 1669: ldc2_w 4.139937459513021E-8
      // 166c: dastore
      // 166d: dup
      // 166e: sipush 734
      // 1671: ldc2_w 1.1253517584464134E-7
      // 1674: dastore
      // 1675: dup
      // 1676: sipush 735
      // 1679: ldc2_w 3.059023470086686E-7
      // 167c: dastore
      // 167d: dup
      // 167e: sipush 736
      // 1681: ldc2_w 8.315287232107949E-7
      // 1684: dastore
      // 1685: dup
      // 1686: sipush 737
      // 1689: ldc2_w 2.260329438286135E-6
      // 168c: dastore
      // 168d: dup
      // 168e: sipush 738
      // 1691: ldc2_w 6.1442124206223525E-6
      // 1694: dastore
      // 1695: dup
      // 1696: sipush 739
      // 1699: ldc2_w 1.670170240686275E-5
      // 169c: dastore
      // 169d: dup
      // 169e: sipush 740
      // 16a1: ldc2_w 4.539993096841499E-5
      // 16a4: dastore
      // 16a5: dup
      // 16a6: sipush 741
      // 16a9: ldc2_w 1.2340981629677117E-4
      // 16ac: dastore
      // 16ad: dup
      // 16ae: sipush 742
      // 16b1: ldc2_w 3.35462624207139E-4
      // 16b4: dastore
      // 16b5: dup
      // 16b6: sipush 743
      // 16b9: ldc2_w 9.118819143623114E-4
      // 16bc: dastore
      // 16bd: dup
      // 16be: sipush 744
      // 16c1: ldc2_w 0.0024787522852420807
      // 16c4: dastore
      // 16c5: dup
      // 16c6: sipush 745
      // 16c9: ldc2_w 0.006737947463989258
      // 16cc: dastore
      // 16cd: dup
      // 16ce: sipush 746
      // 16d1: ldc2_w 0.018315639346837997
      // 16d4: dastore
      // 16d5: dup
      // 16d6: sipush 747
      // 16d9: ldc2_w 0.049787066876888275
      // 16dc: dastore
      // 16dd: dup
      // 16de: sipush 748
      // 16e1: ldc2_w 0.1353352963924408
      // 16e4: dastore
      // 16e5: dup
      // 16e6: sipush 749
      // 16e9: ldc2_w 0.3678794503211975
      // 16ec: dastore
      // 16ed: dup
      // 16ee: sipush 750
      // 16f1: dconst_1
      // 16f2: dastore
      // 16f3: dup
      // 16f4: sipush 751
      // 16f7: ldc2_w 2.7182817459106445
      // 16fa: dastore
      // 16fb: dup
      // 16fc: sipush 752
      // 16ff: ldc2_w 7.389056205749512
      // 1702: dastore
      // 1703: dup
      // 1704: sipush 753
      // 1707: ldc2_w 20.08553695678711
      // 170a: dastore
      // 170b: dup
      // 170c: sipush 754
      // 170f: ldc2_w 54.59815216064453
      // 1712: dastore
      // 1713: dup
      // 1714: sipush 755
      // 1717: ldc2_w 148.41314697265625
      // 171a: dastore
      // 171b: dup
      // 171c: sipush 756
      // 171f: ldc2_w 403.42877197265625
      // 1722: dastore
      // 1723: dup
      // 1724: sipush 757
      // 1727: ldc2_w 1096.633056640625
      // 172a: dastore
      // 172b: dup
      // 172c: sipush 758
      // 172f: ldc2_w 2980.9580078125
      // 1732: dastore
      // 1733: dup
      // 1734: sipush 759
      // 1737: ldc2_w 8103.083984375
      // 173a: dastore
      // 173b: dup
      // 173c: sipush 760
      // 173f: ldc2_w 22026.46484375
      // 1742: dastore
      // 1743: dup
      // 1744: sipush 761
      // 1747: ldc2_w 59874.140625
      // 174a: dastore
      // 174b: dup
      // 174c: sipush 762
      // 174f: ldc2_w 162754.78125
      // 1752: dastore
      // 1753: dup
      // 1754: sipush 763
      // 1757: ldc2_w 442413.375
      // 175a: dastore
      // 175b: dup
      // 175c: sipush 764
      // 175f: ldc2_w 1202604.25
      // 1762: dastore
      // 1763: dup
      // 1764: sipush 765
      // 1767: ldc2_w 3269017.5
      // 176a: dastore
      // 176b: dup
      // 176c: sipush 766
      // 176f: ldc2_w 8886110.0
      // 1772: dastore
      // 1773: dup
      // 1774: sipush 767
      // 1777: ldc2_w 2.4154952E7
      // 177a: dastore
      // 177b: dup
      // 177c: sipush 768
      // 177f: ldc2_w 6.5659968E7
      // 1782: dastore
      // 1783: dup
      // 1784: sipush 769
      // 1787: ldc2_w 1.78482304E8
      // 178a: dastore
      // 178b: dup
      // 178c: sipush 770
      // 178f: ldc2_w 4.85165184E8
      // 1792: dastore
      // 1793: dup
      // 1794: sipush 771
      // 1797: ldc2_w 1.318815744E9
      // 179a: dastore
      // 179b: dup
      // 179c: sipush 772
      // 179f: ldc2_w 3.584912896E9
      // 17a2: dastore
      // 17a3: dup
      // 17a4: sipush 773
      // 17a7: ldc2_w 9.74480384E9
      // 17aa: dastore
      // 17ab: dup
      // 17ac: sipush 774
      // 17af: ldc2_w 2.6489122816E10
      // 17b2: dastore
      // 17b3: dup
      // 17b4: sipush 775
      // 17b7: ldc2_w 7.200489472E10
      // 17ba: dastore
      // 17bb: dup
      // 17bc: sipush 776
      // 17bf: ldc2_w 1.95729620992E11
      // 17c2: dastore
      // 17c3: dup
      // 17c4: sipush 777
      // 17c7: ldc2_w 5.32048248832E11
      // 17ca: dastore
      // 17cb: dup
      // 17cc: sipush 778
      // 17cf: ldc2_w 1.446257098752E12
      // 17d2: dastore
      // 17d3: dup
      // 17d4: sipush 779
      // 17d7: ldc2_w 3.9313342464E12
      // 17da: dastore
      // 17db: dup
      // 17dc: sipush 780
      // 17df: ldc2_w 1.0686474223616E13
      // 17e2: dastore
      // 17e3: dup
      // 17e4: sipush 781
      // 17e7: ldc2_w 2.904884772864E13
      // 17ea: dastore
      // 17eb: dup
      // 17ec: sipush 782
      // 17ef: ldc2_w 7.8962956959744E13
      // 17f2: dastore
      // 17f3: dup
      // 17f4: sipush 783
      // 17f7: ldc2_w 2.14643574308864E14
      // 17fa: dastore
      // 17fb: dup
      // 17fc: sipush 784
      // 17ff: ldc2_w 5.83461777702912E14
      // 1802: dastore
      // 1803: dup
      // 1804: sipush 785
      // 1807: ldc2_w 1.586013579247616E15
      // 180a: dastore
      // 180b: dup
      // 180c: sipush 786
      // 180f: ldc2_w 4.31123180027904E15
      // 1812: dastore
      // 1813: dup
      // 1814: sipush 787
      // 1817: ldc2_w 1.1719142537166848E16
      // 181a: dastore
      // 181b: dup
      // 181c: sipush 788
      // 181f: ldc2_w 3.185593134822195E16
      // 1822: dastore
      // 1823: dup
      // 1824: sipush 789
      // 1827: ldc2_w 8.659339545516442E16
      // 182a: dastore
      // 182b: dup
      // 182c: sipush 790
      // 182f: ldc2_w 2.353852703404196E17
      // 1832: dastore
      // 1833: dup
      // 1834: sipush 791
      // 1837: ldc2_w 6.398434744761057E17
      // 183a: dastore
      // 183b: dup
      // 183c: sipush 792
      // 183f: ldc2_w 1.739274837903278E18
      // 1842: dastore
      // 1843: dup
      // 1844: sipush 793
      // 1847: ldc2_w 4.727839526297272E18
      // 184a: dastore
      // 184b: dup
      // 184c: sipush 794
      // 184f: ldc2_w 1.285159987981792E19
      // 1852: dastore
      // 1853: dup
      // 1854: sipush 795
      // 1857: ldc2_w 3.493427277593156E19
      // 185a: dastore
      // 185b: dup
      // 185c: sipush 796
      // 185f: ldc2_w 9.496119530068797E19
      // 1862: dastore
      // 1863: dup
      // 1864: sipush 797
      // 1867: ldc2_w 2.581312717296228E20
      // 186a: dastore
      // 186b: dup
      // 186c: sipush 798
      // 186f: ldc2_w 7.016736290557636E20
      // 1872: dastore
      // 1873: dup
      // 1874: sipush 799
      // 1877: ldc2_w 1.907346499785443E21
      // 187a: dastore
      // 187b: dup
      // 187c: sipush 800
      // 187f: ldc2_w 5.1847060206155E21
      // 1882: dastore
      // 1883: dup
      // 1884: sipush 801
      // 1887: ldc2_w 1.4093490364499379E22
      // 188a: dastore
      // 188b: dup
      // 188c: sipush 802
      // 188f: ldc2_w 3.831007739580998E22
      // 1892: dastore
      // 1893: dup
      // 1894: sipush 803
      // 1897: ldc2_w 1.0413759887481643E23
      // 189a: dastore
      // 189b: dup
      // 189c: sipush 804
      // 189f: ldc2_w 2.8307533984544136E23
      // 18a2: dastore
      // 18a3: dup
      // 18a4: sipush 805
      // 18a7: ldc2_w 7.694785471490595E23
      // 18aa: dastore
      // 18ab: dup
      // 18ac: sipush 806
      // 18af: ldc2_w 2.0916595931561093E24
      // 18b2: dastore
      // 18b3: dup
      // 18b4: sipush 807
      // 18b7: ldc2_w 5.685720022003016E24
      // 18ba: dastore
      // 18bb: dup
      // 18bc: sipush 808
      // 18bf: ldc2_w 1.545539007875769E25
      // 18c2: dastore
      // 18c3: dup
      // 18c4: sipush 809
      // 18c7: ldc2_w 4.201209991636407E25
      // 18ca: dastore
      // 18cb: dup
      // 18cc: sipush 810
      // 18cf: ldc2_w 1.142007304008196E26
      // 18d2: dastore
      // 18d3: dup
      // 18d4: sipush 811
      // 18d7: ldc2_w 3.104297782658242E26
      // 18da: dastore
      // 18db: dup
      // 18dc: sipush 812
      // 18df: ldc2_w 8.43835682327257E26
      // 18e2: dastore
      // 18e3: dup
      // 18e4: sipush 813
      // 18e7: ldc2_w 2.2937832658080656E27
      // 18ea: dastore
      // 18eb: dup
      // 18ec: sipush 814
      // 18ef: ldc2_w 6.23514943204966E27
      // 18f2: dastore
      // 18f3: dup
      // 18f4: sipush 815
      // 18f7: ldc2_w 1.694889206675675E28
      // 18fa: dastore
      // 18fb: dup
      // 18fc: sipush 816
      // 18ff: ldc2_w 4.607187019879158E28
      // 1902: dastore
      // 1903: dup
      // 1904: sipush 817
      // 1907: ldc2_w 1.2523630909973607E29
      // 190a: dastore
      // 190b: dup
      // 190c: sipush 818
      // 190f: ldc2_w 3.4042761729010895E29
      // 1912: dastore
      // 1913: dup
      // 1914: sipush 819
      // 1917: ldc2_w 9.253781621373885E29
      // 191a: dastore
      // 191b: dup
      // 191c: sipush 820
      // 191f: ldc2_w 2.5154385492401904E30
      // 1922: dastore
      // 1923: dup
      // 1924: sipush 821
      // 1927: ldc2_w 6.837671137556327E30
      // 192a: dastore
      // 192b: dup
      // 192c: sipush 822
      // 192f: ldc2_w 1.8586717056324128E31
      // 1932: dastore
      // 1933: dup
      // 1934: sipush 823
      // 1937: ldc2_w 5.05239404378821E31
      // 193a: dastore
      // 193b: dup
      // 193c: sipush 824
      // 193f: ldc2_w 1.3733830589835937E32
      // 1942: dastore
      // 1943: dup
      // 1944: sipush 825
      // 1947: ldc2_w 3.733241849647479E32
      // 194a: dastore
      // 194b: dup
      // 194c: sipush 826
      // 194f: ldc2_w 1.014800418749161E33
      // 1952: dastore
      // 1953: dup
      // 1954: sipush 827
      // 1957: ldc2_w 2.758513549969986E33
      // 195a: dastore
      // 195b: dup
      // 195c: sipush 828
      // 195f: ldc2_w 7.498416981578345E33
      // 1962: dastore
      // 1963: dup
      // 1964: sipush 829
      // 1967: ldc2_w 2.0382811492597872E34
      // 196a: dastore
      // 196b: dup
      // 196c: sipush 830
      // 196f: ldc2_w 5.540622484676759E34
      // 1972: dastore
      // 1973: dup
      // 1974: sipush 831
      // 1977: ldc2_w 1.5060972626944096E35
      // 197a: dastore
      // 197b: dup
      // 197c: sipush 832
      // 197f: ldc2_w 4.0939972479624634E35
      // 1982: dastore
      // 1983: dup
      // 1984: sipush 833
      // 1987: ldc2_w 1.1128638067747114E36
      // 198a: dastore
      // 198b: dup
      // 198c: sipush 834
      // 198f: ldc2_w 3.0250770246136387E36
      // 1992: dastore
      // 1993: dup
      // 1994: sipush 835
      // 1997: ldc2_w 8.223012393018281E36
      // 199a: dastore
      // 199b: dup
      // 199c: sipush 836
      // 199f: ldc2_w 2.2352467822017166E37
      // 19a2: dastore
      // 19a3: dup
      // 19a4: sipush 837
      // 19a7: ldc2_w 6.076029840339376E37
      // 19aa: dastore
      // 19ab: dup
      // 19ac: sipush 838
      // 19af: ldc2_w 1.6516361647240826E38
      // 19b2: dastore
      // 19b3: dup
      // 19b4: sipush 839
      // 19b7: ldc2_w 4.4896127778163155E38
      // 19ba: dastore
      // 19bb: dup
      // 19bc: sipush 840
      // 19bf: ldc2_w 1.2204032949639917E39
      // 19c2: dastore
      // 19c3: dup
      // 19c4: sipush 841
      // 19c7: ldc2_w 3.3174000012927697E39
      // 19ca: dastore
      // 19cb: dup
      // 19cc: sipush 842
      // 19cf: ldc2_w 9.017628107716908E39
      // 19d2: dastore
      // 19d3: dup
      // 19d4: sipush 843
      // 19d7: ldc2_w 2.451245443147225E40
      // 19da: dastore
      // 19db: dup
      // 19dc: sipush 844
      // 19df: ldc2_w 6.663175904917432E40
      // 19e2: dastore
      // 19e3: dup
      // 19e4: sipush 845
      // 19e7: ldc2_w 1.8112388823726723E41
      // 19ea: dastore
      // 19eb: dup
      // 19ec: sipush 846
      // 19ef: ldc2_w 4.923458004084836E41
      // 19f2: dastore
      // 19f3: dup
      // 19f4: sipush 847
      // 19f7: ldc2_w 1.3383347029375378E42
      // 19fa: dastore
      // 19fb: dup
      // 19fc: sipush 848
      // 19ff: ldc2_w 3.637970747803715E42
      // 1a02: dastore
      // 1a03: dup
      // 1a04: sipush 849
      // 1a07: ldc2_w 9.889030935681123E42
      // 1a0a: dastore
      // 1a0b: dup
      // 1a0c: sipush 850
      // 1a0f: ldc2_w 2.6881169167589747E43
      // 1a12: dastore
      // 1a13: dup
      // 1a14: sipush 851
      // 1a17: ldc2_w 7.307059786371152E43
      // 1a1a: dastore
      // 1a1b: dup
      // 1a1c: sipush 852
      // 1a1f: ldc2_w 1.986264756071962E44
      // 1a22: dastore
      // 1a23: dup
      // 1a24: sipush 853
      // 1a27: ldc2_w 5.399227989109673E44
      // 1a2a: dastore
      // 1a2b: dup
      // 1a2c: sipush 854
      // 1a2f: ldc2_w 1.467662348860426E45
      // 1a32: dastore
      // 1a33: dup
      // 1a34: sipush 855
      // 1a37: ldc2_w 3.989519470441919E45
      // 1a3a: dastore
      // 1a3b: dup
      // 1a3c: sipush 856
      // 1a3f: ldc2_w 1.0844638420493122E46
      // 1a42: dastore
      // 1a43: dup
      // 1a44: sipush 857
      // 1a47: ldc2_w 2.9478781225754055E46
      // 1a4a: dastore
      // 1a4b: dup
      // 1a4c: sipush 858
      // 1a4f: ldc2_w 8.013164089994031E46
      // 1a52: dastore
      // 1a53: dup
      // 1a54: sipush 859
      // 1a57: ldc2_w 2.1782039447564253E47
      // 1a5a: dastore
      // 1a5b: dup
      // 1a5c: sipush 860
      // 1a5f: ldc2_w 5.920972420778763E47
      // 1a62: dastore
      // 1a63: dup
      // 1a64: sipush 861
      // 1a67: ldc2_w 1.609486943324346E48
      // 1a6a: dastore
      // 1a6b: dup
      // 1a6c: sipush 862
      // 1a6f: ldc2_w 4.3750396394525074E48
      // 1a72: dastore
      // 1a73: dup
      // 1a74: sipush 863
      // 1a77: ldc2_w 1.1892591576149107E49
      // 1a7a: dastore
      // 1a7b: dup
      // 1a7c: sipush 864
      // 1a7f: ldc2_w 3.2327411123173475E49
      // 1a82: dastore
      // 1a83: dup
      // 1a84: sipush 865
      // 1a87: ldc2_w 8.787501601904039E49
      // 1a8a: dastore
      // 1a8b: dup
      // 1a8c: sipush 866
      // 1a8f: ldc2_w 2.3886908001521312E50
      // 1a92: dastore
      // 1a93: dup
      // 1a94: sipush 867
      // 1a97: ldc2_w 6.493134033643613E50
      // 1a9a: dastore
      // 1a9b: dup
      // 1a9c: sipush 868
      // 1a9f: ldc2_w 1.7650169203544438E51
      // 1aa2: dastore
      // 1aa3: dup
      // 1aa4: sipush 869
      // 1aa7: ldc2_w 4.7978130078372714E51
      // 1aaa: dastore
      // 1aab: dup
      // 1aac: sipush 870
      // 1aaf: ldc2_w 1.3041809768060802E52
      // 1ab2: dastore
      // 1ab3: dup
      // 1ab4: sipush 871
      // 1ab7: ldc2_w 3.5451314095271004E52
      // 1aba: dastore
      // 1abb: dup
      // 1abc: sipush 872
      // 1abf: ldc2_w 9.636666808527841E52
      // 1ac2: dastore
      // 1ac3: dup
      // 1ac4: sipush 873
      // 1ac7: ldc2_w 2.6195174357581655E53
      // 1aca: dastore
      // 1acb: dup
      // 1acc: sipush 874
      // 1acf: ldc2_w 7.120586694432509E53
      // 1ad2: dastore
      // 1ad3: dup
      // 1ad4: sipush 875
      // 1ad7: ldc2_w 1.9355758655647052E54
      // 1ada: dastore
      // 1adb: dup
      // 1adc: sipush 876
      // 1adf: ldc2_w 5.2614409704305464E54
      // 1ae2: dastore
      // 1ae3: dup
      // 1ae4: sipush 877
      // 1ae7: ldc2_w 1.4302079642723736E55
      // 1aea: dastore
      // 1aeb: dup
      // 1aec: sipush 878
      // 1aef: ldc2_w 3.8877083524279136E55
      // 1af2: dastore
      // 1af3: dup
      // 1af4: sipush 879
      // 1af7: ldc2_w 1.0567886837680406E56
      // 1afa: dastore
      // 1afb: dup
      // 1afc: sipush 880
      // 1aff: ldc2_w 2.872649515690124E56
      // 1b02: dastore
      // 1b03: dup
      // 1b04: sipush 881
      // 1b07: ldc2_w 7.808670894670738E56
      // 1b0a: dastore
      // 1b0b: dup
      // 1b0c: sipush 882
      // 1b0f: ldc2_w 2.1226166967029073E57
      // 1b12: dastore
      // 1b13: dup
      // 1b14: sipush 883
      // 1b17: ldc2_w 5.769871153180574E57
      // 1b1a: dastore
      // 1b1b: dup
      // 1b1c: sipush 884
      // 1b1f: ldc2_w 1.568413405104933E58
      // 1b22: dastore
      // 1b23: dup
      // 1b24: sipush 885
      // 1b27: ldc2_w 4.263390023436419E58
      // 1b2a: dastore
      // 1b2b: dup
      // 1b2c: sipush 886
      // 1b2f: ldc2_w 1.1589095247718807E59
      // 1b32: dastore
      // 1b33: dup
      // 1b34: sipush 887
      // 1b37: ldc2_w 3.150242850860434E59
      // 1b3a: dastore
      // 1b3b: dup
      // 1b3c: sipush 888
      // 1b3f: ldc2_w 8.563247933339596E59
      // 1b42: dastore
      // 1b43: dup
      // 1b44: sipush 889
      // 1b47: ldc2_w 2.3277319969498524E60
      // 1b4a: dastore
      // 1b4b: dup
      // 1b4c: sipush 890
      // 1b4f: ldc2_w 6.327431953939798E60
      // 1b52: dastore
      // 1b53: dup
      // 1b54: sipush 891
      // 1b57: ldc2_w 1.719974302355042E61
      // 1b5a: dastore
      // 1b5b: dup
      // 1b5c: sipush 892
      // 1b5f: ldc2_w 4.675374788964851E61
      // 1b62: dastore
      // 1b63: dup
      // 1b64: sipush 893
      // 1b67: ldc2_w 1.2708985520400816E62
      // 1b6a: dastore
      // 1b6b: dup
      // 1b6c: sipush 894
      // 1b6f: ldc2_w 3.454660807101683E62
      // 1b72: dastore
      // 1b73: dup
      // 1b74: sipush 895
      // 1b77: ldc2_w 9.390740355567705E62
      // 1b7a: dastore
      // 1b7b: dup
      // 1b7c: sipush 896
      // 1b7f: ldc2_w 2.5526681615684215E63
      // 1b82: dastore
      // 1b83: dup
      // 1b84: sipush 897
      // 1b87: ldc2_w 6.938871462941557E63
      // 1b8a: dastore
      // 1b8b: dup
      // 1b8c: sipush 898
      // 1b8f: ldc2_w 1.8861808782043154E64
      // 1b92: dastore
      // 1b93: dup
      // 1b94: sipush 899
      // 1b97: ldc2_w 5.1271712215233855E64
      // 1b9a: dastore
      // 1b9b: dup
      // 1b9c: sipush 900
      // 1b9f: ldc2_w 1.3937096689052236E65
      // 1ba2: dastore
      // 1ba3: dup
      // 1ba4: sipush 901
      // 1ba7: ldc2_w 3.7884955399150257E65
      // 1baa: dastore
      // 1bab: dup
      // 1bac: sipush 902
      // 1baf: ldc2_w 1.0298199046367501E66
      // 1bb2: dastore
      // 1bb3: dup
      // 1bb4: sipush 903
      // 1bb7: ldc2_w 2.799340708992666E66
      // 1bba: dastore
      // 1bbb: dup
      // 1bbc: sipush 904
      // 1bbf: ldc2_w 7.609396391563323E66
      // 1bc2: dastore
      // 1bc3: dup
      // 1bc4: sipush 905
      // 1bc7: ldc2_w 2.0684484008569103E67
      // 1bca: dastore
      // 1bcb: dup
      // 1bcc: sipush 906
      // 1bcf: ldc2_w 5.622626080395226E67
      // 1bd2: dastore
      // 1bd3: dup
      // 1bd4: sipush 907
      // 1bd7: ldc2_w 1.528388084444653E68
      // 1bda: dastore
      // 1bdb: dup
      // 1bdc: sipush 908
      // 1bdf: ldc2_w 4.1545899609113734E68
      // 1be2: dastore
      // 1be3: dup
      // 1be4: sipush 909
      // 1be7: ldc2_w 1.1293346659459732E69
      // 1bea: dastore
      // 1beb: dup
      // 1bec: sipush 910
      // 1bef: ldc2_w 3.069849599753188E69
      // 1bf2: dastore
      // 1bf3: dup
      // 1bf4: sipush 911
      // 1bf7: ldc2_w 8.344717266683004E69
      // 1bfa: dastore
      // 1bfb: dup
      // 1bfc: sipush 912
      // 1bff: ldc2_w 2.268329019570017E70
      // 1c02: dastore
      // 1c03: dup
      // 1c04: sipush 913
      // 1c07: ldc2_w 6.165958325782564E70
      // 1c0a: dastore
      // 1c0b: dup
      // 1c0c: sipush 914
      // 1c0f: ldc2_w 1.676081191364984E71
      // 1c12: dastore
      // 1c13: dup
      // 1c14: sipush 915
      // 1c17: ldc2_w 4.556060380835955E71
      // 1c1a: dastore
      // 1c1b: dup
      // 1c1c: sipush 916
      // 1c1f: ldc2_w 1.2384658100355657E72
      // 1c22: dastore
      // 1c23: dup
      // 1c24: sipush 917
      // 1c27: ldc2_w 3.3664990715562672E72
      // 1c2a: dastore
      // 1c2b: dup
      // 1c2c: sipush 918
      // 1c2f: ldc2_w 9.15109220707761E72
      // 1c32: dastore
      // 1c33: dup
      // 1c34: sipush 919
      // 1c37: ldc2_w 2.4875248571153216E73
      // 1c3a: dastore
      // 1c3b: dup
      // 1c3c: sipush 920
      // 1c3f: ldc2_w 6.761793219649385E73
      // 1c42: dastore
      // 1c43: dup
      // 1c44: sipush 921
      // 1c47: ldc2_w 1.8380461271305958E74
      // 1c4a: dastore
      // 1c4b: dup
      // 1c4c: sipush 922
      // 1c4f: ldc2_w 4.996327312938759E74
      // 1c52: dastore
      // 1c53: dup
      // 1c54: sipush 923
      // 1c57: ldc2_w 1.3581426848077408E75
      // 1c5a: dastore
      // 1c5b: dup
      // 1c5c: sipush 924
      // 1c5f: ldc2_w 3.691814001080034E75
      // 1c62: dastore
      // 1c63: dup
      // 1c64: sipush 925
      // 1c67: ldc2_w 1.0035391101975138E76
      // 1c6a: dastore
      // 1c6b: dup
      // 1c6c: sipush 926
      // 1c6f: ldc2_w 2.7279024753382288E76
      // 1c72: dastore
      // 1c73: dup
      // 1c74: sipush 927
      // 1c77: ldc2_w 7.415207287657125E76
      // 1c7a: dastore
      // 1c7b: dup
      // 1c7c: sipush 928
      // 1c7f: ldc2_w 2.0156621983963848E77
      // 1c82: dastore
      // 1c83: dup
      // 1c84: sipush 929
      // 1c87: ldc2_w 5.479138512760614E77
      // 1c8a: dastore
      // 1c8b: dup
      // 1c8c: sipush 930
      // 1c8f: ldc2_w 1.4893842728520671E78
      // 1c92: dastore
      // 1c93: dup
      // 1c94: sipush 931
      // 1c97: ldc2_w 4.048565732162643E78
      // 1c9a: dastore
      // 1c9b: dup
      // 1c9c: sipush 932
      // 1c9f: ldc2_w 1.1005142643914475E79
      // 1ca2: dastore
      // 1ca3: dup
      // 1ca4: sipush 933
      // 1ca7: ldc2_w 2.991508131437659E79
      // 1caa: dastore
      // 1cab: dup
      // 1cac: sipush 934
      // 1caf: ldc2_w 8.131762373533769E79
      // 1cb2: dastore
      // 1cb3: dup
      // 1cb4: sipush 935
      // 1cb7: ldc2_w 2.210442148596269E80
      // 1cba: dastore
      // 1cbb: dup
      // 1cbc: sipush 936
      // 1cbf: ldc2_w 6.008604166110734E80
      // 1cc2: dastore
      // 1cc3: dup
      // 1cc4: sipush 937
      // 1cc7: ldc2_w 1.633308028614055E81
      // 1cca: dastore
      // 1ccb: dup
      // 1ccc: sipush 938
      // 1ccf: ldc2_w 4.439791652732591E81
      // 1cd2: dastore
      // 1cd3: dup
      // 1cd4: sipush 939
      // 1cd7: ldc2_w 1.206860599814453E82
      // 1cda: dastore
      // 1cdb: dup
      // 1cdc: sipush 940
      // 1cdf: ldc2_w 3.280586734644871E82
      // 1ce2: dastore
      // 1ce3: dup
      // 1ce4: sipush 941
      // 1ce7: ldc2_w 8.917559854082513E82
      // 1cea: dastore
      // 1ceb: dup
      // 1cec: sipush 942
      // 1cef: ldc2_w 2.4240442814945802E83
      // 1cf2: dastore
      // 1cf3: dup
      // 1cf4: sipush 943
      // 1cf7: ldc2_w 6.589235682116406E83
      // 1cfa: dastore
      // 1cfb: dup
      // 1cfc: sipush 944
      // 1cff: ldc2_w 1.7911398904871E84
      // 1d02: dastore
      // 1d03: dup
      // 1d04: sipush 945
      // 1d07: ldc2_w 4.86882298924053E84
      // 1d0a: dastore
      // 1d0b: dup
      // 1d0c: sipush 946
      // 1d0f: ldc2_w 1.3234832005748183E85
      // 1d12: dastore
      // 1d13: dup
      // 1d14: sipush 947
      // 1d17: ldc2_w 3.597600556519039E85
      // 1d1a: dastore
      // 1d1b: dup
      // 1d1c: sipush 948
      // 1d1f: ldc2_w 9.77929222446451E85
      // 1d22: dastore
      // 1d23: dup
      // 1d24: sipush 949
      // 1d27: ldc2_w 2.658286976862848E86
      // 1d2a: dastore
      // 1d2b: dup
      // 1d2c: sipush 950
      // 1d2f: ldc2_w 7.225974166887662E86
      // 1d32: dastore
      // 1d33: dup
      // 1d34: sipush 951
      // 1d37: ldc2_w 1.9642232209552433E87
      // 1d3a: dastore
      // 1d3b: dup
      // 1d3c: sipush 952
      // 1d3f: ldc2_w 5.3393125705958075E87
      // 1d42: dastore
      // 1d43: dup
      // 1d44: sipush 953
      // 1d47: ldc2_w 1.4513757076459615E88
      // 1d4a: dastore
      // 1d4b: dup
      // 1d4c: sipush 954
      // 1d4f: ldc2_w 3.945247871835613E88
      // 1d52: dastore
      // 1d53: dup
      // 1d54: sipush 955
      // 1d57: ldc2_w 1.0724295693252266E89
      // 1d5a: dastore
      // 1d5b: dup
      // 1d5c: sipush 956
      // 1d5f: ldc2_w 2.915165904253785E89
      // 1d62: dastore
      // 1d63: dup
      // 1d64: sipush 957
      // 1d67: ldc2_w 7.924242330665303E89
      // 1d6a: dastore
      // 1d6b: dup
      // 1d6c: sipush 958
      // 1d6f: ldc2_w 2.1540322390343345E90
      // 1d72: dastore
      // 1d73: dup
      // 1d74: sipush 959
      // 1d77: ldc2_w 5.855267177907345E90
      // 1d7a: dastore
      // 1d7b: dup
      // 1d7c: sipush 960
      // 1d7f: ldc2_w 1.5916266807316476E91
      // 1d82: dastore
      // 1d83: dup
      // 1d84: sipush 961
      // 1d87: ldc2_w 4.326489915443873E91
      // 1d8a: dastore
      // 1d8b: dup
      // 1d8c: sipush 962
      // 1d8f: ldc2_w 1.1760619079592718E92
      // 1d92: dastore
      // 1d93: dup
      // 1d94: sipush 963
      // 1d97: ldc2_w 3.1968677404735245E92
      // 1d9a: dastore
      // 1d9b: dup
      // 1d9c: sipush 964
      // 1d9f: ldc2_w 8.689987517871135E92
      // 1da2: dastore
      // 1da3: dup
      // 1da4: sipush 965
      // 1da7: ldc2_w 2.3621834216830225E93
      // 1daa: dastore
      // 1dab: dup
      // 1dac: sipush 966
      // 1daf: ldc2_w 6.421080550439423E93
      // 1db2: dastore
      // 1db3: dup
      // 1db4: sipush 967
      // 1db7: ldc2_w 1.7454306955949023E94
      // 1dba: dastore
      // 1dbb: dup
      // 1dbc: sipush 968
      // 1dbf: ldc2_w 4.744571892885607E94
      // 1dc2: dastore
      // 1dc3: dup
      // 1dc4: sipush 969
      // 1dc7: ldc2_w 1.2897084285532175E95
      // 1dca: dastore
      // 1dcb: dup
      // 1dcc: sipush 970
      // 1dcf: ldc2_w 3.505791114318544E95
      // 1dd2: dastore
      // 1dd3: dup
      // 1dd4: sipush 971
      // 1dd7: ldc2_w 9.529727908157224E95
      // 1dda: dastore
      // 1ddb: dup
      // 1ddc: sipush 972
      // 1ddf: ldc2_w 2.5904487437231458E96
      // 1de2: dastore
      // 1de3: dup
      // 1de4: sipush 973
      // 1de7: ldc2_w 7.041568925985714E96
      // 1dea: dastore
      // 1deb: dup
      // 1dec: sipush 974
      // 1def: ldc2_w 1.9140971884979424E97
      // 1df2: dastore
      // 1df3: dup
      // 1df4: sipush 975
      // 1df7: ldc2_w 5.203055142575272E97
      // 1dfa: dastore
      // 1dfb: dup
      // 1dfc: sipush 976
      // 1dff: ldc2_w 1.4143368931719686E98
      // 1e02: dastore
      // 1e03: dup
      // 1e04: sipush 977
      // 1e07: ldc2_w 3.8445667684706366E98
      // 1e0a: dastore
      // 1e0b: dup
      // 1e0c: sipush 978
      // 1e0f: ldc2_w 1.0450615121235744E99
      // 1e12: dastore
      // 1e13: dup
      // 1e14: sipush 979
      // 1e17: ldc2_w 2.8407720200442806E99
      // 1e1a: dastore
      // 1e1b: dup
      // 1e1c: sipush 980
      // 1e1f: ldc2_w 7.722018663521402E99
      // 1e22: dastore
      // 1e23: dup
      // 1e24: sipush 981
      // 1e27: ldc2_w 2.0990624115923312E100
      // 1e2a: dastore
      // 1e2b: dup
      // 1e2c: sipush 982
      // 1e2f: ldc2_w 5.705842978547001E100
      // 1e32: dastore
      // 1e33: dup
      // 1e34: sipush 983
      // 1e37: ldc2_w 1.5510089388648915E101
      // 1e3a: dastore
      // 1e3b: dup
      // 1e3c: sipush 984
      // 1e3f: ldc2_w 4.216079296087462E101
      // 1e42: dastore
      // 1e43: dup
      // 1e44: sipush 985
      // 1e47: ldc2_w 1.1460491592124923E102
      // 1e4a: dastore
      // 1e4b: dup
      // 1e4c: sipush 986
      // 1e4f: ldc2_w 3.1152847602082673E102
      // 1e52: dastore
      // 1e53: dup
      // 1e54: sipush 987
      // 1e57: ldc2_w 8.468222063292654E102
      // 1e5a: dastore
      // 1e5b: dup
      // 1e5c: sipush 988
      // 1e5f: ldc2_w 2.3019011105282883E103
      // 1e62: dastore
      // 1e63: dup
      // 1e64: sipush 989
      // 1e67: ldc2_w 6.257216813084462E103
      // 1e6a: dastore
      // 1e6b: dup
      // 1e6c: sipush 990
      // 1e6f: ldc2_w 1.7008878437355237E104
      // 1e72: dastore
      // 1e73: dup
      // 1e74: sipush 991
      // 1e77: ldc2_w 4.62349260394851E104
      // 1e7a: dastore
      // 1e7b: dup
      // 1e7c: sipush 992
      // 1e7f: ldc2_w 1.2567956334920216E105
      // 1e82: dastore
      // 1e83: dup
      // 1e84: sipush 993
      // 1e87: ldc2_w 3.416324322370112E105
      // 1e8a: dastore
      // 1e8b: dup
      // 1e8c: sipush 994
      // 1e8f: ldc2_w 9.286532888251822E105
      // 1e92: dastore
      // 1e93: dup
      // 1e94: sipush 995
      // 1e97: ldc2_w 2.5243410574836706E106
      // 1e9a: dastore
      // 1e9b: dup
      // 1e9c: sipush 996
      // 1e9f: ldc2_w 6.861870970598542E106
      // 1ea2: dastore
      // 1ea3: dup
      // 1ea4: sipush 997
      // 1ea7: ldc2_w 1.8652499723625443E107
      // 1eaa: dastore
      // 1eab: dup
      // 1eac: sipush 998
      // 1eaf: ldc2_w 5.070274654122399E107
      // 1eb2: dastore
      // 1eb3: dup
      // 1eb4: sipush 999
      // 1eb7: ldc2_w 1.3782437251846782E108
      // 1eba: dastore
      // 1ebb: dup
      // 1ebc: sipush 1000
      // 1ebf: ldc2_w 3.746454626411946E108
      // 1ec2: dastore
      // 1ec3: dup
      // 1ec4: sipush 1001
      // 1ec7: ldc2_w 1.0183920005400422E109
      // 1eca: dastore
      // 1ecb: dup
      // 1ecc: sipush 1002
      // 1ecf: ldc2_w 2.768276122845335E109
      // 1ed2: dastore
      // 1ed3: dup
      // 1ed4: sipush 1003
      // 1ed7: ldc2_w 7.524954624697075E109
      // 1eda: dastore
      // 1edb: dup
      // 1edc: sipush 1004
      // 1edf: ldc2_w 2.0454950851007314E110
      // 1ee2: dastore
      // 1ee3: dup
      // 1ee4: sipush 1005
      // 1ee7: ldc2_w 5.56023190218245E110
      // 1eea: dastore
      // 1eeb: dup
      // 1eec: sipush 1006
      // 1eef: ldc2_w 1.511427628805191E111
      // 1ef2: dastore
      // 1ef3: dup
      // 1ef4: sipush 1007
      // 1ef7: ldc2_w 4.1084862677372065E111
      // 1efa: dastore
      // 1efb: dup
      // 1efc: sipush 1008
      // 1eff: ldc2_w 1.1168024085164686E112
      // 1f02: dastore
      // 1f03: dup
      // 1f04: sipush 1009
      // 1f07: ldc2_w 3.0357834799588566E112
      // 1f0a: dastore
      // 1f0b: dup
      // 1f0c: sipush 1010
      // 1f0f: ldc2_w 8.252116273466952E112
      // 1f12: dastore
      // 1f13: dup
      // 1f14: sipush 1011
      // 1f17: ldc2_w 2.2431576057283144E113
      // 1f1a: dastore
      // 1f1b: dup
      // 1f1c: sipush 1012
      // 1f1f: ldc2_w 6.097534318207731E113
      // 1f22: dastore
      // 1f23: dup
      // 1f24: sipush 1013
      // 1f27: ldc2_w 1.65748157925005E114
      // 1f2a: dastore
      // 1f2b: dup
      // 1f2c: sipush 1014
      // 1f2f: ldc2_w 4.5055022172222453E114
      // 1f32: dastore
      // 1f33: dup
      // 1f34: sipush 1015
      // 1f37: ldc2_w 1.2247224482958058E115
      // 1f3a: dastore
      // 1f3b: dup
      // 1f3c: sipush 1016
      // 1f3f: ldc2_w 3.329140840363789E115
      // 1f42: dastore
      // 1f43: dup
      // 1f44: sipush 1017
      // 1f47: ldc2_w 9.049543313665034E115
      // 1f4a: dastore
      // 1f4b: dup
      // 1f4c: sipush 1018
      // 1f4f: ldc2_w 2.4599209935197392E116
      // 1f52: dastore
      // 1f53: dup
      // 1f54: sipush 1019
      // 1f57: ldc2_w 6.686758417135634E116
      // 1f5a: dastore
      // 1f5b: dup
      // 1f5c: sipush 1020
      // 1f5f: ldc2_w 1.817649308779104E117
      // 1f62: dastore
      // 1f63: dup
      // 1f64: sipush 1021
      // 1f67: ldc2_w 4.940883275207154E117
      // 1f6a: dastore
      // 1f6b: dup
      // 1f6c: sipush 1022
      // 1f6f: ldc2_w 1.3430713954289087E118
      // 1f72: dastore
      // 1f73: dup
      // 1f74: sipush 1023
      // 1f77: ldc2_w 3.6508464654683645E118
      // 1f7a: dastore
      // 1f7b: dup
      // 1f7c: sipush 1024
      // 1f7f: ldc2_w 9.924030156169606E118
      // 1f82: dastore
      // 1f83: dup
      // 1f84: sipush 1025
      // 1f87: ldc2_w 2.697631034485758E119
      // 1f8a: dastore
      // 1f8b: dup
      // 1f8c: sipush 1026
      // 1f8f: ldc2_w 7.332921137166064E119
      // 1f92: dastore
      // 1f93: dup
      // 1f94: sipush 1027
      // 1f97: ldc2_w 1.9932945470297703E120
      // 1f9a: dastore
      // 1f9b: dup
      // 1f9c: sipush 1028
      // 1f9f: ldc2_w 5.418336099279846E120
      // 1fa2: dastore
      // 1fa3: dup
      // 1fa4: sipush 1029
      // 1fa7: ldc2_w 1.472856595860236E121
      // 1faa: dastore
      // 1fab: dup
      // 1fac: sipush 1030
      // 1faf: ldc2_w 4.0036393271908754E121
      // 1fb2: dastore
      // 1fb3: dup
      // 1fb4: sipush 1031
      // 1fb7: ldc2_w 1.0883019300873278E122
      // 1fba: dastore
      // 1fbb: dup
      // 1fbc: sipush 1032
      // 1fbf: ldc2_w 2.9583112936666607E122
      // 1fc2: dastore
      // 1fc3: dup
      // 1fc4: sipush 1033
      // 1fc7: ldc2_w 8.041523923017192E122
      // 1fca: dastore
      // 1fcb: dup
      // 1fcc: sipush 1034
      // 1fcf: ldc2_w 2.1859129781586158E123
      // 1fd2: dastore
      // 1fd3: dup
      // 1fd4: sipush 1035
      // 1fd7: ldc2_w 5.941927186144745E123
      // 1fda: dastore
      // 1fdb: dup
      // 1fdc: sipush 1036
      // 1fdf: ldc2_w 1.6151834292371802E124
      // 1fe2: dastore
      // 1fe3: dup
      // 1fe4: sipush 1037
      // 1fe7: ldc2_w 4.390523815859274E124
      // 1fea: dastore
      // 1feb: dup
      // 1fec: sipush 1038
      // 1fef: ldc2_w 1.1934680816813702E125
      // 1ff2: dastore
      // 1ff3: dup
      // 1ff4: sipush 1039
      // 1ff7: ldc2_w 3.2441826014060764E125
      // 1ffa: dastore
      // 1ffb: dup
      // 1ffc: sipush 1040
      // 1fff: ldc2_w 8.81860282490643E125
      // 2002: dastore
      // 2003: dup
      // 2004: sipush 1041
      // 2007: ldc2_w 2.3971445233885962E126
      // 200a: dastore
      // 200b: dup
      // 200c: sipush 1042
      // 200f: ldc2_w 6.516115189736396E126
      // 2012: dastore
      // 2013: dup
      // 2014: sipush 1043
      // 2017: ldc2_w 1.7712635751001657E127
      // 201a: dastore
      // 201b: dup
      // 201c: sipush 1044
      // 201f: ldc2_w 4.814793918384117E127
      // 2022: dastore
      // 2023: dup
      // 2024: sipush 1045
      // 2027: ldc2_w 1.3087966177291396E128
      // 202a: dastore
      // 202b: dup
      // 202c: sipush 1046
      // 202f: ldc2_w 3.557678449715009E128
      // 2032: dastore
      // 2033: dup
      // 2034: sipush 1047
      // 2037: ldc2_w 9.670771210463886E128
      // 203a: dastore
      // 203b: dup
      // 203c: sipush 1048
      // 203f: ldc2_w 2.628788218289742E129
      // 2042: dastore
      // 2043: dup
      // 2044: sipush 1049
      // 2047: ldc2_w 7.145787619369324E129
      // 204a: dastore
      // 204b: dup
      // 204c: sipush 1050
      // 204f: ldc2_w 1.9424264981694277E130
      // 2052: dastore
      // 2053: dup
      // 2054: sipush 1051
      // 2057: ldc2_w 5.280062387569078E130
      // 205a: dastore
      // 205b: dup
      // 205c: sipush 1052
      // 205f: ldc2_w 1.4352697002457768E131
      // 2062: dastore
      // 2063: dup
      // 2064: sipush 1053
      // 2067: ldc2_w 3.901467289560222E131
      // 206a: dastore
      // 206b: dup
      // 206c: sipush 1054
      // 206f: ldc2_w 1.0605288965077546E132
      // 2072: dastore
      // 2073: dup
      // 2074: sipush 1055
      // 2077: ldc2_w 2.882816299252225E132
      // 207a: dastore
      // 207b: dup
      // 207c: sipush 1056
      // 207f: ldc2_w 7.836307815186044E132
      // 2082: dastore
      // 2083: dup
      // 2084: sipush 1057
      // 2087: ldc2_w 2.1301292155181736E133
      // 208a: dastore
      // 208b: dup
      // 208c: sipush 1058
      // 208f: ldc2_w 5.790291758828013E133
      // 2092: dastore
      // 2093: dup
      // 2094: sipush 1059
      // 2097: ldc2_w 1.573964437869041E134
      // 209a: dastore
      // 209b: dup
      // 209c: sipush 1060
      // 209f: ldc2_w 4.278478878300888E134
      // 20a2: dastore
      // 20a3: dup
      // 20a4: sipush 1061
      // 20a7: ldc2_w 1.1630112062985817E135
      // 20aa: dastore
      // 20ab: dup
      // 20ac: sipush 1062
      // 20af: ldc2_w 3.1613917467297413E135
      // 20b2: dastore
      // 20b3: dup
      // 20b4: sipush 1063
      // 20b7: ldc2_w 8.593554223894477E135
      // 20ba: dastore
      // 20bb: dup
      // 20bc: sipush 1064
      // 20bf: ldc2_w 2.335970335559215E136
      // 20c2: dastore
      // 20c3: dup
      // 20c4: sipush 1065
      // 20c7: ldc2_w 6.349826172787151E136
      // 20ca: dastore
      // 20cb: dup
      // 20cc: sipush 1066
      // 20cf: ldc2_w 1.7260616357651607E137
      // 20d2: dastore
      // 20d3: dup
      // 20d4: sipush 1067
      // 20d7: ldc2_w 4.691921416188566E137
      // 20da: dastore
      // 20db: dup
      // 20dc: sipush 1068
      // 20df: ldc2_w 1.2753966504932798E138
      // 20e2: dastore
      // 20e3: dup
      // 20e4: sipush 1069
      // 20e7: ldc2_w 3.466887271843006E138
      // 20ea: dastore
      // 20eb: dup
      // 20ec: sipush 1070
      // 20ef: ldc2_w 9.423976538577447E138
      // 20f2: dastore
      // 20f3: dup
      // 20f4: sipush 1071
      // 20f7: ldc2_w 2.561702766944378E139
      // 20fa: dastore
      // 20fb: dup
      // 20fc: sipush 1072
      // 20ff: ldc2_w 6.963429563637273E139
      // 2102: dastore
      // 2103: dup
      // 2104: sipush 1073
      // 2107: ldc2_w 1.892856346657855E140
      // 210a: dastore
      // 210b: dup
      // 210c: sipush 1074
      // 210f: ldc2_w 5.1453167686439515E140
      // 2112: dastore
      // 2113: dup
      // 2114: sipush 1075
      // 2117: ldc2_w 1.3986421289359558E141
      // 211a: dastore
      // 211b: dup
      // 211c: sipush 1076
      // 211f: ldc2_w 3.8019036618832785E141
      // 2122: dastore
      // 2123: dup
      // 2124: sipush 1077
      // 2127: ldc2_w 1.033464507572145E142
      // 212a: dastore
      // 212b: dup
      // 212c: sipush 1078
      // 212f: ldc2_w 2.809247950589945E142
      // 2132: dastore
      // 2133: dup
      // 2134: sipush 1079
      // 2137: ldc2_w 7.636326960498012E142
      // 213a: dastore
      // 213b: dup
      // 213c: sipush 1080
      // 213f: ldc2_w 2.075769060297565E143
      // 2142: dastore
      // 2143: dup
      // 2144: sipush 1081
      // 2147: ldc2_w 5.64252553828769E143
      // 214a: dastore
      // 214b: dup
      // 214c: sipush 1082
      // 214f: ldc2_w 1.5337974510118784E144
      // 2152: dastore
      // 2153: dup
      // 2154: sipush 1083
      // 2157: ldc2_w 4.169293918423203E144
      // 215a: dastore
      // 215b: dup
      // 215c: sipush 1084
      // 215f: ldc2_w 1.1333315586787883E145
      // 2162: dastore
      // 2163: dup
      // 2164: sipush 1085
      // 2167: ldc2_w 3.080714152600695E145
      // 216a: dastore
      // 216b: dup
      // 216c: sipush 1086
      // 216f: ldc2_w 8.374250298636991E145
      // 2172: dastore
      // 2173: dup
      // 2174: sipush 1087
      // 2177: ldc2_w 2.276357074042286E146
      // 217a: dastore
      // 217b: dup
      // 217c: sipush 1088
      // 217f: ldc2_w 6.187780443461367E146
      // 2182: dastore
      // 2183: dup
      // 2184: sipush 1089
      // 2187: ldc2_w 1.6820131331794073E147
      // 218a: dastore
      // 218b: dup
      // 218c: sipush 1090
      // 218f: ldc2_w 4.572185635487065E147
      // 2192: dastore
      // 2193: dup
      // 2194: sipush 1091
      // 2197: ldc2_w 1.2428488853188662E148
      // 219a: dastore
      // 219b: dup
      // 219c: sipush 1092
      // 219f: ldc2_w 3.378413594504258E148
      // 21a2: dastore
      // 21a3: dup
      // 21a4: sipush 1093
      // 21a7: ldc2_w 9.183480622172801E148
      // 21aa: dastore
      // 21ab: dup
      // 21ac: sipush 1094
      // 21af: ldc2_w 2.4963286658278886E149
      // 21b2: dastore
      // 21b3: dup
      // 21b4: sipush 1095
      // 21b7: ldc2_w 6.785725312893433E149
      // 21ba: dastore
      // 21bb: dup
      // 21bc: sipush 1096
      // 21bf: ldc2_w 1.8445514681108982E150
      // 21c2: dastore
      // 21c3: dup
      // 21c4: sipush 1097
      // 21c7: ldc2_w 5.014010481958507E150
      // 21ca: dastore
      // 21cb: dup
      // 21cc: sipush 1098
      // 21cf: ldc2_w 1.3629491735708616E151
      // 21d2: dastore
      // 21d3: dup
      // 21d4: sipush 1099
      // 21d7: ldc2_w 3.7048805655699485E151
      // 21da: dastore
      // 21db: dup
      // 21dc: sipush 1100
      // 21df: ldc2_w 1.0070909418550386E152
      // 21e2: dastore
      // 21e3: dup
      // 21e4: sipush 1101
      // 21e7: ldc2_w 2.7375567044077912E152
      // 21ea: dastore
      // 21eb: dup
      // 21ec: sipush 1102
      // 21ef: ldc2_w 7.441451374243517E152
      // 21f2: dastore
      // 21f3: dup
      // 21f4: sipush 1103
      // 21f7: ldc2_w 2.022795961737854E153
      // 21fa: dastore
      // 21fb: dup
      // 21fc: sipush 1104
      // 21ff: ldc2_w 5.4985298195094216E153
      // 2202: dastore
      // 2203: dup
      // 2204: sipush 1105
      // 2207: ldc2_w 1.494655405262451E154
      // 220a: dastore
      // 220b: dup
      // 220c: sipush 1106
      // 220f: ldc2_w 4.062894701808608E154
      // 2212: dastore
      // 2213: dup
      // 2214: sipush 1107
      // 2217: ldc2_w 1.1044092571980793E155
      // 221a: dastore
      // 221b: dup
      // 221c: sipush 1108
      // 221f: ldc2_w 3.002095574584687E155
      // 2222: dastore
      // 2223: dup
      // 2224: sipush 1109
      // 2227: ldc2_w 8.160542326793782E155
      // 222a: dastore
      // 222b: dup
      // 222c: sipush 1110
      // 222f: ldc2_w 2.218265110516721E156
      // 2232: dastore
      // 2233: dup
      // 2234: sipush 1111
      // 2237: ldc2_w 6.02987028472758E156
      // 223a: dastore
      // 223b: dup
      // 223c: sipush 1112
      // 223f: ldc2_w 1.6390888071605646E157
      // 2242: dastore
      // 2243: dup
      // 2244: sipush 1113
      // 2247: ldc2_w 4.455504920700703E157
      // 224a: dastore
      // 224b: dup
      // 224c: sipush 1114
      // 224f: ldc2_w 1.2111317421229415E158
      // 2252: dastore
      // 2253: dup
      // 2254: sipush 1115
      // 2257: ldc2_w 3.2921976772303727E158
      // 225a: dastore
      // 225b: dup
      // 225c: sipush 1116
      // 225f: ldc2_w 8.94912101169977E158
      // 2262: dastore
      // 2263: dup
      // 2264: sipush 1117
      // 2267: ldc2_w 2.432623425087251E159
      // 226a: dastore
      // 226b: dup
      // 226c: sipush 1118
      // 226f: ldc2_w 6.612555731556604E159
      // 2272: dastore
      // 2273: dup
      // 2274: sipush 1119
      // 2277: ldc2_w 1.7974788874847574E160
      // 227a: dastore
      // 227b: dup
      // 227c: sipush 1120
      // 227f: ldc2_w 4.8860545948985793E160
      // 2282: dastore
      // 2283: dup
      // 2284: sipush 1121
      // 2287: ldc2_w 1.328167263606087E161
      // 228a: dastore
      // 228b: dup
      // 228c: sipush 1122
      // 228f: ldc2_w 3.610333312791256E161
      // 2292: dastore
      // 2293: dup
      // 2294: sipush 1123
      // 2297: ldc2_w 9.813901863427107E161
      // 229a: dastore
      // 229b: dup
      // 229c: sipush 1124
      // 229f: ldc2_w 2.667695552814763E162
      // 22a2: dastore
      // 22a3: dup
      // 22a4: sipush 1125
      // 22a7: ldc2_w 7.251548346906463E162
      // 22aa: dastore
      // 22ab: dup
      // 22ac: sipush 1126
      // 22af: ldc2_w 1.9711751621240536E163
      // 22b2: dastore
      // 22b3: dup
      // 22b4: sipush 1127
      // 22b7: ldc2_w 5.3582093498119173E163
      // 22ba: dastore
      // 22bb: dup
      // 22bc: sipush 1128
      // 22bf: ldc2_w 1.4565123573071036E164
      // 22c2: dastore
      // 22c3: dup
      // 22c4: sipush 1129
      // 22c7: ldc2_w 3.959211091077107E164
      // 22ca: dastore
      // 22cb: dup
      // 22cc: sipush 1130
      // 22cf: ldc2_w 1.0762251933089556E165
      // 22d2: dastore
      // 22d3: dup
      // 22d4: sipush 1131
      // 22d7: ldc2_w 2.9254832789181E165
      // 22da: dastore
      // 22db: dup
      // 22dc: sipush 1132
      // 22df: ldc2_w 7.952287052787358E165
      // 22e2: dastore
      // 22e3: dup
      // 22e4: sipush 1133
      // 22e7: ldc2_w 2.161656025361765E166
      // 22ea: dastore
      // 22eb: dup
      // 22ec: sipush 1134
      // 22ef: ldc2_w 5.8759898326913254E166
      // 22f2: dastore
      // 22f3: dup
      // 22f4: sipush 1135
      // 22f7: ldc2_w 1.597259768214821E167
      // 22fa: dastore
      // 22fb: dup
      // 22fc: sipush 1136
      // 22ff: ldc2_w 4.3418021646459346E167
      // 2302: dastore
      // 2303: dup
      // 2304: sipush 1137
      // 2307: ldc2_w 1.1802241249113175E168
      // 230a: dastore
      // 230b: dup
      // 230c: sipush 1138
      // 230f: ldc2_w 3.2081817253680657E168
      // 2312: dastore
      // 2313: dup
      // 2314: sipush 1139
      // 2317: ldc2_w 8.720743087611513E168
      // 231a: dastore
      // 231b: dup
      // 231c: sipush 1140
      // 231f: ldc2_w 2.3705435424427623E169
      // 2322: dastore
      // 2323: dup
      // 2324: sipush 1141
      // 2327: ldc2_w 6.443805025317327E169
      // 232a: dastore
      // 232b: dup
      // 232c: sipush 1142
      // 232f: ldc2_w 1.7516078165936552E170
      // 2332: dastore
      // 2333: dup
      // 2334: sipush 1143
      // 2337: ldc2_w 4.7613641572445654E170
      // 233a: dastore
      // 233b: dup
      // 233c: sipush 1144
      // 233f: ldc2_w 1.2942728582966776E171
      // 2342: dastore
      // 2343: dup
      // 2344: sipush 1145
      // 2347: ldc2_w 3.518198614137319E171
      // 234a: dastore
      // 234b: dup
      // 234c: sipush 1146
      // 234f: ldc2_w 9.563454814394247E171
      // 2352: dastore
      // 2353: dup
      // 2354: sipush 1147
      // 2357: ldc2_w 2.5996166206245285E172
      // 235a: dastore
      // 235b: dup
      // 235c: sipush 1148
      // 235f: ldc2_w 7.066491077377918E172
      // 2362: dastore
      // 2363: dup
      // 2364: sipush 1149
      // 2367: ldc2_w 1.920871394985668E173
      // 236a: dastore
      // 236b: dup
      // 236c: sipush 1150
      // 236f: ldc2_w 5.221469250951617E173
      // 2372: dastore
      // 2373: dup
      // 2374: sipush 1151
      // 2377: ldc2_w 1.4193426880442385E174
      // 237a: dastore
      // 237b: dup
      // 237c: sipush 1152
      // 237f: ldc2_w 3.8581732071331E174
      // 2382: dastore
      // 2383: dup
      // 2384: sipush 1153
      // 2387: ldc2_w 1.0487601931965087E175
      // 238a: dastore
      // 238b: dup
      // 238c: sipush 1154
      // 238f: ldc2_w 2.850825930161946E175
      // 2392: dastore
      // 2393: dup
      // 2394: sipush 1155
      // 2397: ldc2_w 7.749348772180658E175
      // 239a: dastore
      // 239b: dup
      // 239c: sipush 1156
      // 239f: ldc2_w 2.1064911705560668E176
      // 23a2: dastore
      // 23a3: dup
      // 23a4: sipush 1157
      // 23a7: ldc2_w 5.726036941135634E176
      // 23aa: dastore
      // 23ab: dup
      // 23ac: sipush 1158
      // 23af: ldc2_w 1.5564982816556894E177
      // 23b2: dastore
      // 23b3: dup
      // 23b4: sipush 1159
      // 23b7: ldc2_w 4.231000988846797E177
      // 23ba: dastore
      // 23bb: dup
      // 23bc: sipush 1160
      // 23bf: ldc2_w 1.1501053030837989E178
      // 23c2: dastore
      // 23c3: dup
      // 23c4: sipush 1161
      // 23c7: ldc2_w 3.1263099916916113E178
      // 23ca: dastore
      // 23cb: dup
      // 23cc: sipush 1162
      // 23cf: ldc2_w 8.498192212235393E178
      // 23d2: dastore
      // 23d3: dup
      // 23d4: sipush 1163
      // 23d7: ldc2_w 2.3100480183046895E179
      // 23da: dastore
      // 23db: dup
      // 23dc: sipush 1164
      // 23df: ldc2_w 6.279361500971995E179
      // 23e2: dastore
      // 23e3: dup
      // 23e4: sipush 1165
      // 23e7: ldc2_w 1.7069074829463731E180
      // 23ea: dastore
      // 23eb: dup
      // 23ec: sipush 1166
      // 23ef: ldc2_w 4.63985600437427E180
      // 23f2: dastore
      // 23f3: dup
      // 23f4: sipush 1167
      // 23f7: ldc2_w 1.2612435745231905E181
      // 23fa: dastore
      // 23fb: dup
      // 23fc: sipush 1168
      // 23ff: ldc2_w 3.4284156709489884E181
      // 2402: dastore
      // 2403: dup
      // 2404: sipush 1169
      // 2407: ldc2_w 9.319400030019162E181
      // 240a: dastore
      // 240b: dup
      // 240c: sipush 1170
      // 240f: ldc2_w 2.5332752658571312E182
      // 2412: dastore
      // 2413: dup
      // 2414: sipush 1171
      // 2417: ldc2_w 6.88615578404537E182
      // 241a: dastore
      // 241b: dup
      // 241c: sipush 1172
      // 241f: ldc2_w 1.8718514371423056E183
      // 2422: dastore
      // 2423: dup
      // 2424: sipush 1173
      // 2427: ldc2_w 5.088219872370737E183
      // 242a: dastore
      // 242b: dup
      // 242c: sipush 1174
      // 242f: ldc2_w 1.3831214731781958E184
      // 2432: dastore
      // 2433: dup
      // 2434: sipush 1175
      // 2437: ldc2_w 3.759713966511158E184
      // 243a: dastore
      // 243b: dup
      // 243c: sipush 1176
      // 243f: ldc2_w 1.021996184153141E185
      // 2442: dastore
      // 2443: dup
      // 2444: sipush 1177
      // 2447: ldc2_w 2.778073442169904E185
      // 244a: dastore
      // 244b: dup
      // 244c: sipush 1178
      // 244f: ldc2_w 7.55158797540476E185
      // 2452: dastore
      // 2453: dup
      // 2454: sipush 1179
      // 2457: ldc2_w 2.0527342305586606E186
      // 245a: dastore
      // 245b: dup
      // 245c: sipush 1180
      // 245f: ldc2_w 5.579910641313343E186
      // 2462: dastore
      // 2463: dup
      // 2464: sipush 1181
      // 2467: ldc2_w 1.5167767828844167E187
      // 246a: dastore
      // 246b: dup
      // 246c: sipush 1182
      // 246f: ldc2_w 4.123026721295484E187
      // 2472: dastore
      // 2473: dup
      // 2474: sipush 1183
      // 2477: ldc2_w 1.1207549425651513E188
      // 247a: dastore
      // 247b: dup
      // 247c: sipush 1184
      // 247f: ldc2_w 3.0465278560980536E188
      // 2482: dastore
      // 2483: dup
      // 2484: sipush 1185
      // 2487: ldc2_w 8.281321669236493E188
      // 248a: dastore
      // 248b: dup
      // 248c: sipush 1186
      // 248f: ldc2_w 2.251096660331649E189
      // 2492: dastore
      // 2493: dup
      // 2494: sipush 1187
      // 2497: ldc2_w 6.119114404399683E189
      // 249a: dastore
      // 249b: dup
      // 249c: sipush 1188
      // 249f: ldc2_w 1.6633478556884994E190
      // 24a2: dastore
      // 24a3: dup
      // 24a4: sipush 1189
      // 24a7: ldc2_w 4.521448560089285E190
      // 24aa: dastore
      // 24ab: dup
      // 24ac: sipush 1190
      // 24af: ldc2_w 1.2290570545894685E191
      // 24b2: dastore
      // 24b3: dup
      // 24b4: sipush 1191
      // 24b7: ldc2_w 3.340923580982338E191
      // 24ba: dastore
      // 24bb: dup
      // 24bc: sipush 1192
      // 24bf: ldc2_w 9.081571104550255E191
      // 24c2: dastore
      // 24c3: dup
      // 24c4: sipush 1193
      // 24c7: ldc2_w 2.468626868232408E192
      // 24ca: dastore
      // 24cb: dup
      // 24cc: sipush 1194
      // 24cf: ldc2_w 6.710424255583952E192
      // 24d2: dastore
      // 24d3: dup
      // 24d4: sipush 1195
      // 24d7: ldc2_w 1.8240823171621646E193
      // 24da: dastore
      // 24db: dup
      // 24dc: sipush 1196
      // 24df: ldc2_w 4.958369974640573E193
      // 24e2: dastore
      // 24e3: dup
      // 24e4: sipush 1197
      // 24e7: ldc2_w 1.3478247120462365E194
      // 24ea: dastore
      // 24eb: dup
      // 24ec: sipush 1198
      // 24ef: ldc2_w 3.6637673548790206E194
      // 24f2: dastore
      // 24f3: dup
      // 24f4: sipush 1199
      // 24f7: ldc2_w 9.959152908532152E194
      // 24fa: dastore
      // 24fb: dup
      // 24fc: sipush 1200
      // 24ff: ldc2_w 2.707178052117959E195
      // 2502: dastore
      // 2503: dup
      // 2504: sipush 1201
      // 2507: ldc2_w 7.358873642076596E195
      // 250a: dastore
      // 250b: dup
      // 250c: sipush 1202
      // 250f: ldc2_w 2.0003490682463053E196
      // 2512: dastore
      // 2513: dup
      // 2514: sipush 1203
      // 2517: ldc2_w 5.4375131636754E196
      // 251a: dastore
      // 251b: dup
      // 251c: sipush 1204
      // 251f: ldc2_w 1.4780692924846082E197
      // 2522: dastore
      // 2523: dup
      // 2524: sipush 1205
      // 2527: ldc2_w 4.01780853635105E197
      // 252a: dastore
      // 252b: dup
      // 252c: sipush 1206
      // 252f: ldc2_w 1.0921536132159379E198
      // 2532: dastore
      // 2533: dup
      // 2534: sipush 1207
      // 2537: ldc2_w 2.968781250496917E198
      // 253a: dastore
      // 253b: dup
      // 253c: sipush 1208
      // 253f: ldc2_w 8.069984512111955E198
      // 2542: dastore
      // 2543: dup
      // 2544: sipush 1209
      // 2547: ldc2_w 2.193649279840519E199
      // 254a: dastore
      // 254b: dup
      // 254c: sipush 1210
      // 254f: ldc2_w 5.962956589227457E199
      // 2552: dastore
      // 2553: dup
      // 2554: sipush 1211
      // 2557: ldc2_w 1.620899738203635E200
      // 255a: dastore
      // 255b: dup
      // 255c: sipush 1212
      // 255f: ldc2_w 4.406062052965071E200
      // 2562: dastore
      // 2563: dup
      // 2564: sipush 1213
      // 2567: ldc2_w 1.1976919074588434E201
      // 256a: dastore
      // 256b: dup
      // 256c: sipush 1214
      // 256f: ldc2_w 3.2556641859513496E201
      // 2572: dastore
      // 2573: dup
      // 2574: sipush 1215
      // 2577: ldc2_w 8.849812639395597E201
      // 257a: dastore
      // 257b: dup
      // 257c: sipush 1216
      // 257f: ldc2_w 2.40562867677584E202
      // 2582: dastore
      // 2583: dup
      // 2584: sipush 1217
      // 2587: ldc2_w 6.539175932653188E202
      // 258a: dastore
      // 258b: dup
      // 258c: sipush 1218
      // 258f: ldc2_w 1.7775323307944624E203
      // 2592: dastore
      // 2593: dup
      // 2594: sipush 1219
      // 2597: ldc2_w 4.831833881898182E203
      // 259a: dastore
      // 259b: dup
      // 259c: sipush 1220
      // 259f: ldc2_w 1.3134287685114547E204
      // 25a2: dastore
      // 25a3: dup
      // 25a4: sipush 1221
      // 25a7: ldc2_w 3.5702693195009266E204
      // 25aa: dastore
      // 25ab: dup
      // 25ac: sipush 1222
      // 25af: ldc2_w 9.704997606668411E204
      // 25b2: dastore
      // 25b3: dup
      // 25b4: sipush 1223
      // 25b7: ldc2_w 2.63809219778715E205
      // 25ba: dastore
      // 25bb: dup
      // 25bc: sipush 1224
      // 25bf: ldc2_w 7.171077244202293E205
      // 25c2: dastore
      // 25c3: dup
      // 25c4: sipush 1225
      // 25c7: ldc2_w 1.949300880034352E206
      // 25ca: dastore
      // 25cb: dup
      // 25cc: sipush 1226
      // 25cf: ldc2_w 5.298749302736127E206
      // 25d2: dastore
      // 25d3: dup
      // 25d4: sipush 1227
      // 25d7: ldc2_w 1.4403494631058154E207
      // 25da: dastore
      // 25db: dup
      // 25dc: sipush 1228
      // 25df: ldc2_w 3.91527572177694E207
      // 25e2: dastore
      // 25e3: dup
      // 25e4: sipush 1229
      // 25e7: ldc2_w 1.0642823992403076E208
      // 25ea: dastore
      // 25eb: dup
      // 25ec: sipush 1230
      // 25ef: ldc2_w 2.8930193727937684E208
      // 25f2: dastore
      // 25f3: dup
      // 25f4: sipush 1231
      // 25f7: ldc2_w 7.8640411896421955E208
      // 25fa: dastore
      // 25fb: dup
      // 25fc: sipush 1232
      // 25ff: ldc2_w 2.1376680994038112E209
      // 2602: dastore
      // 2603: dup
      // 2604: sipush 1233
      // 2607: ldc2_w 5.8107841809216616E209
      // 260a: dastore
      // 260b: dup
      // 260c: sipush 1234
      // 260f: ldc2_w 1.5795351101531684E210
      // 2612: dastore
      // 2613: dup
      // 2614: sipush 1235
      // 2617: ldc2_w 4.293620869258453E210
      // 261a: dastore
      // 261b: dup
      // 261c: sipush 1236
      // 261f: ldc2_w 1.1671272667059652E211
      // 2622: dastore
      // 2623: dup
      // 2624: sipush 1237
      // 2627: ldc2_w 3.172580666390786E211
      // 262a: dastore
      // 262b: dup
      // 262c: sipush 1238
      // 262f: ldc2_w 8.623968972387222E211
      // 2632: dastore
      // 2633: dup
      // 2634: sipush 1239
      // 2637: ldc2_w 2.3442378838418366E212
      // 263a: dastore
      // 263b: dup
      // 263c: sipush 1240
      // 263f: ldc2_w 6.372298757235201E212
      // 2642: dastore
      // 2643: dup
      // 2644: sipush 1241
      // 2647: ldc2_w 1.7321703934464356E213
      // 264a: dastore
      // 264b: dup
      // 264c: sipush 1242
      // 264f: ldc2_w 4.708527306855985E213
      // 2652: dastore
      // 2653: dup
      // 2654: sipush 1243
      // 2657: ldc2_w 1.279910496643312E214
      // 265a: dastore
      // 265b: dup
      // 265c: sipush 1244
      // 265f: ldc2_w 3.479157135998568E214
      // 2662: dastore
      // 2663: dup
      // 2664: sipush 1245
      // 2667: ldc2_w 9.45732984079136E214
      // 266a: dastore
      // 266b: dup
      // 266c: sipush 1246
      // 266f: ldc2_w 2.5707689593428096E215
      // 2672: dastore
      // 2673: dup
      // 2674: sipush 1247
      // 2677: ldc2_w 6.988074107282322E215
      // 267a: dastore
      // 267b: dup
      // 267c: sipush 1248
      // 267f: ldc2_w 1.8995553996578656E216
      // 2682: dastore
      // 2683: dup
      // 2684: sipush 1249
      // 2687: ldc2_w 5.1635269305465607E216
      // 268a: dastore
      // 268b: dup
      // 268c: sipush 1250
      // 268f: ldc2_w 1.4035923083915864E217
      // 2692: dastore
      // 2693: dup
      // 2694: sipush 1251
      // 2697: ldc2_w 3.815359096108819E217
      // 269a: dastore
      // 269b: dup
      // 269c: sipush 1252
      // 269f: ldc2_w 1.0371220592190472E218
      // 26a2: dastore
      // 26a3: dup
      // 26a4: sipush 1253
      // 26a7: ldc2_w 2.819190456167585E218
      // 26aa: dastore
      // 26ab: dup
      // 26ac: sipush 1254
      // 26af: ldc2_w 7.663353127378024E218
      // 26b2: dastore
      // 26b3: dup
      // 26b4: sipush 1255
      // 26b7: ldc2_w 2.083115484919861E219
      // 26ba: dastore
      // 26bb: dup
      // 26bc: sipush 1256
      // 26bf: ldc2_w 5.662495731848751E219
      // 26c2: dastore
      // 26c3: dup
      // 26c4: sipush 1257
      // 26c7: ldc2_w 1.5392257142577226E220
      // 26ca: dastore
      // 26cb: dup
      // 26cc: sipush 1258
      // 26cf: ldc2_w 4.184049381430498E220
      // 26d2: dastore
      // 26d3: dup
      // 26d4: sipush 1259
      // 26d7: ldc2_w 1.1373425785132867E221
      // 26da: dastore
      // 26db: dup
      // 26dc: sipush 1260
      // 26df: ldc2_w 3.091617462831603E221
      // 26e2: dastore
      // 26e3: dup
      // 26e4: sipush 1261
      // 26e7: ldc2_w 8.403887374207366E221
      // 26ea: dastore
      // 26eb: dup
      // 26ec: sipush 1262
      // 26ef: ldc2_w 2.2844135610697528E222
      // 26f2: dastore
      // 26f3: dup
      // 26f4: sipush 1263
      // 26f7: ldc2_w 6.209679892802781E222
      // 26fa: dastore
      // 26fb: dup
      // 26fc: sipush 1264
      // 26ff: ldc2_w 1.6879660933816274E223
      // 2702: dastore
      // 2703: dup
      // 2704: sipush 1265
      // 2707: ldc2_w 4.588367423411997E223
      // 270a: dastore
      // 270b: dup
      // 270c: sipush 1266
      // 270f: ldc2_w 1.2472476068464461E224
      // 2712: dastore
      // 2713: dup
      // 2714: sipush 1267
      // 2717: ldc2_w 3.3903703993793316E224
      // 271a: dastore
      // 271b: dup
      // 271c: sipush 1268
      // 271f: ldc2_w 9.215982463319503E224
      // 2722: dastore
      // 2723: dup
      // 2724: sipush 1269
      // 2727: ldc2_w 2.5051637206758385E225
      // 272a: dastore
      // 272b: dup
      // 272c: sipush 1270
      // 272f: ldc2_w 6.809741127603255E225
      // 2732: dastore
      // 2733: dup
      // 2734: sipush 1271
      // 2737: ldc2_w 1.8510795864289367E226
      // 273a: dastore
      // 273b: dup
      // 273c: sipush 1272
      // 273f: ldc2_w 5.031755776868959E226
      // 2742: dastore
      // 2743: dup
      // 2744: sipush 1273
      // 2747: ldc2_w 1.3677729802316034E227
      // 274a: dastore
      // 274b: dup
      // 274c: sipush 1274
      // 274f: ldc2_w 3.7179924024793253E227
      // 2752: dastore
      // 2753: dup
      // 2754: sipush 1275
      // 2757: ldc2_w 1.0106552237522032E228
      // 275a: dastore
      // 275b: dup
      // 275c: sipush 1276
      // 275f: ldc2_w 2.7472456017809066E228
      // 2762: dastore
      // 2763: dup
      // 2764: sipush 1277
      // 2767: ldc2_w 7.467788172398272E228
      // 276a: dastore
      // 276b: dup
      // 276c: sipush 1278
      // 276f: ldc2_w 2.029955237703202E229
      // 2772: dastore
      // 2773: dup
      // 2774: sipush 1279
      // 2777: ldc2_w 5.517990469846618E229
      // 277a: dastore
      // 277b: dup
      // 277c: sipush 1280
      // 277f: ldc2_w 1.4999452522236406E230
      // 2782: dastore
      // 2783: dup
      // 2784: sipush 1281
      // 2787: ldc2_w 4.0772734783595525E230
      // 278a: dastore
      // 278b: dup
      // 278c: sipush 1282
      // 278f: ldc2_w 1.1083180046837618E231
      // 2792: dastore
      // 2793: dup
      // 2794: sipush 1283
      // 2797: ldc2_w 3.012720614547867E231
      // 279a: dastore
      // 279b: dup
      // 279c: sipush 1284
      // 279f: ldc2_w 8.18942426109357E231
      // 27a2: dastore
      // 27a3: dup
      // 27a4: sipush 1285
      // 27a7: ldc2_w 2.2261161215322043E232
      // 27aa: dastore
      // 27ab: dup
      // 27ac: sipush 1286
      // 27af: ldc2_w 6.051211457626543E232
      // 27b2: dastore
      // 27b3: dup
      // 27b4: sipush 1287
      // 27b7: ldc2_w 1.6448897917725177E233
      // 27ba: dastore
      // 27bb: dup
      // 27bc: sipush 1288
      // 27bf: ldc2_w 4.471273900208441E233
      // 27c2: dastore
      // 27c3: dup
      // 27c4: sipush 1289
      // 27c7: ldc2_w 1.2154183152078517E234
      // 27ca: dastore
      // 27cb: dup
      // 27cc: sipush 1290
      // 27cf: ldc2_w 3.3038494682728794E234
      // 27d2: dastore
      // 27d3: dup
      // 27d4: sipush 1291
      // 27d7: ldc2_w 8.98079409878202E234
      // 27da: dastore
      // 27db: dup
      // 27dc: sipush 1292
      // 27df: ldc2_w 2.4412328161430576E235
      // 27e2: dastore
      // 27e3: dup
      // 27e4: sipush 1293
      // 27e7: ldc2_w 6.63595840453991E235
      // 27ea: dastore
      // 27eb: dup
      // 27ec: sipush 1294
      // 27ef: ldc2_w 1.8038406914061554E236
      // 27f2: dastore
      // 27f3: dup
      // 27f4: sipush 1295
      // 27f7: ldc2_w 4.90334700062756E236
      // 27fa: dastore
      // 27fb: dup
      // 27fc: sipush 1296
      // 27ff: ldc2_w 1.3328680266667662E237
      // 2802: dastore
      // 2803: dup
      // 2804: sipush 1297
      // 2807: ldc2_w 3.623110695743118E237
      // 280a: dastore
      // 280b: dup
      // 280c: sipush 1298
      // 280f: ldc2_w 9.848636053777669E237
      // 2812: dastore
      // 2813: dup
      // 2814: sipush 1299
      // 2817: ldc2_w 2.677136737066629E238
      // 281a: dastore
      // 281b: dup
      // 281c: sipush 1300
      // 281f: ldc2_w 7.277212447141125E238
      // 2822: dastore
      // 2823: dup
      // 2824: sipush 1301
      // 2827: ldc2_w 1.978151484427976E239
      // 282a: dastore
      // 282b: dup
      // 282c: sipush 1302
      // 282f: ldc2_w 5.377173488599035E239
      // 2832: dastore
      // 2833: dup
      // 2834: sipush 1303
      // 2837: ldc2_w 1.4616672175682191E240
      // 283a: dastore
      // 283b: dup
      // 283c: sipush 1304
      // 283f: ldc2_w 3.973222981713661E240
      // 2842: dastore
      // 2843: dup
      // 2844: sipush 1305
      // 2847: ldc2_w 1.0800340064859439E241
      // 284a: dastore
      // 284b: dup
      // 284c: sipush 1306
      // 284f: ldc2_w 2.935837009891444E241
      // 2852: dastore
      // 2853: dup
      // 2854: sipush 1307
      // 2857: ldc2_w 7.980432566722885E241
      // 285a: dastore
      // 285b: dup
      // 285c: sipush 1308
      // 285f: ldc2_w 2.169306470354036E242
      // 2862: dastore
      // 2863: dup
      // 2864: sipush 1309
      // 2867: ldc2_w 5.896786161387733E242
      // 286a: dastore
      // 286b: dup
      // 286c: sipush 1310
      // 286f: ldc2_w 1.6029126916635028E243
      // 2872: dastore
      // 2873: dup
      // 2874: sipush 1311
      // 2877: ldc2_w 4.357168123448786E243
      // 287a: dastore
      // 287b: dup
      // 287c: sipush 1312
      // 287f: ldc2_w 1.1844011798406507E244
      // 2882: dastore
      // 2883: dup
      // 2884: sipush 1313
      // 2887: ldc2_w 3.2195361624179725E244
      // 288a: dastore
      // 288b: dup
      // 288c: sipush 1314
      // 288f: ldc2_w 8.751606149833694E244
      // 2892: dastore
      // 2893: dup
      // 2894: sipush 1315
      // 2897: ldc2_w 2.3789334438756013E245
      // 289a: dastore
      // 289b: dup
      // 289c: sipush 1316
      // 289f: ldc2_w 6.466611224443739E245
      // 28a2: dastore
      // 28a3: dup
      // 28a4: sipush 1317
      // 28a7: ldc2_w 1.7578073785142153E246
      // 28aa: dastore
      // 28ab: dup
      // 28ac: sipush 1318
      // 28af: ldc2_w 4.7782149589194885E246
      // 28b2: dastore
      // 28b3: dup
      // 28b4: sipush 1319
      // 28b7: ldc2_w 1.2988535295611824E247
      // 28ba: dastore
      // 28bb: dup
      // 28bc: sipush 1320
      // 28bf: ldc2_w 3.5306502960727705E247
      // 28c2: dastore
      // 28c3: dup
      // 28c4: sipush 1321
      // 28c7: ldc2_w 9.597302512507479E247
      // 28ca: dastore
      // 28cb: dup
      // 28cc: sipush 1322
      // 28cf: ldc2_w 2.608817438130718E248
      // 28d2: dastore
      // 28d3: dup
      // 28d4: sipush 1323
      // 28d7: ldc2_w 7.091500562953208E248
      // 28da: dastore
      // 28db: dup
      // 28dc: sipush 1324
      // 28df: ldc2_w 1.9276698418065647E249
      // 28e2: dastore
      // 28e3: dup
      // 28e4: sipush 1325
      // 28e7: ldc2_w 5.239949786641934E249
      // 28ea: dastore
      // 28eb: dup
      // 28ec: sipush 1326
      // 28ef: ldc2_w 1.42436589329759E250
      // 28f2: dastore
      // 28f3: dup
      // 28f4: sipush 1327
      // 28f7: ldc2_w 3.8718282216768776E250
      // 28fa: dastore
      // 28fb: dup
      // 28fc: sipush 1328
      // 28ff: ldc2_w 1.0524719896550007E251
      // 2902: dastore
      // 2903: dup
      // 2904: sipush 1329
      // 2907: ldc2_w 2.860915548426704E251
      // 290a: dastore
      // 290b: dup
      // 290c: sipush 1330
      // 290f: ldc2_w 7.77677492833005E251
      // 2912: dastore
      // 2913: dup
      // 2914: sipush 1331
      // 2917: ldc2_w 2.113946677051906E252
      // 291a: dastore
      // 291b: dup
      // 291c: sipush 1332
      // 291f: ldc2_w 5.7463023795153145E252
      // 2922: dastore
      // 2923: dup
      // 2924: sipush 1333
      // 2927: ldc2_w 1.56200679236425E253
      // 292a: dastore
      // 292b: dup
      // 292c: sipush 1334
      // 292f: ldc2_w 4.2459748085663055E253
      // 2932: dastore
      // 2933: dup
      // 2934: sipush 1335
      // 2937: ldc2_w 1.1541756557557508E254
      // 293a: dastore
      // 293b: dup
      // 293c: sipush 1336
      // 293f: ldc2_w 3.137374584307575E254
      // 2942: dastore
      // 2943: dup
      // 2944: sipush 1337
      // 2947: ldc2_w 8.528268445871411E254
      // 294a: dastore
      // 294b: dup
      // 294c: sipush 1338
      // 294f: ldc2_w 2.3182239583484444E255
      // 2952: dastore
      // 2953: dup
      // 2954: sipush 1339
      // 2957: ldc2_w 6.301585387776819E255
      // 295a: dastore
      // 295b: dup
      // 295c: sipush 1340
      // 295f: ldc2_w 1.7129486892266285E256
      // 2962: dastore
      // 2963: dup
      // 2964: sipush 1341
      // 2967: ldc2_w 4.6562769567905925E256
      // 296a: dastore
      // 296b: dup
      // 296c: sipush 1342
      // 296f: ldc2_w 1.26570724146049E257
      // 2972: dastore
      // 2973: dup
      // 2974: sipush 1343
      // 2977: ldc2_w 3.4405490416979487E257
      // 297a: dastore
      // 297b: dup
      // 297c: sipush 1344
      // 297f: ldc2_w 9.352382323649647E257
      // 2982: dastore
      // 2983: dup
      // 2984: sipush 1345
      // 2987: ldc2_w 2.54224113415832E258
      // 298a: dastore
      // 298b: dup
      // 298c: sipush 1346
      // 298f: ldc2_w 6.910528108396216E258
      // 2992: dastore
      // 2993: dup
      // 2994: sipush 1347
      // 2997: ldc2_w 1.8784760208391767E259
      // 299a: dastore
      // 299b: dup
      // 299c: sipush 1348
      // 299f: ldc2_w 5.106228040084293E259
      // 29a2: dastore
      // 29a3: dup
      // 29a4: sipush 1349
      // 29a7: ldc2_w 1.3880166914480165E260
      // 29aa: dastore
      // 29ab: dup
      // 29ac: sipush 1350
      // 29af: ldc2_w 3.7730204737910044E260
      // 29b2: dastore
      // 29b3: dup
      // 29b4: sipush 1351
      // 29b7: ldc2_w 1.0256131352582533E261
      // 29ba: dastore
      // 29bb: dup
      // 29bc: sipush 1352
      // 29bf: ldc2_w 2.787906051540986E261
      // 29c2: dastore
      // 29c3: dup
      // 29c4: sipush 1353
      // 29c7: ldc2_w 7.578313650939932E261
      // 29ca: dastore
      // 29cb: dup
      // 29cc: sipush 1354
      // 29cf: ldc2_w 2.0599991793068063E262
      // 29d2: dastore
      // 29d3: dup
      // 29d4: sipush 1355
      // 29d7: ldc2_w 5.5996586041611455E262
      // 29da: dastore
      // 29db: dup
      // 29dc: sipush 1356
      // 29df: ldc2_w 1.522145133131402E263
      // 29e2: dastore
      // 29e3: dup
      // 29e4: sipush 1357
      // 29e7: ldc2_w 4.137618951061827E263
      // 29ea: dastore
      // 29eb: dup
      // 29ec: sipush 1358
      // 29ef: ldc2_w 1.1247213964487372E264
      // 29f2: dastore
      // 29f3: dup
      // 29f4: sipush 1359
      // 29f7: ldc2_w 3.0573102223682595E264
      // 29fa: dastore
      // 29fb: dup
      // 29fc: sipush 1360
      // 29ff: ldc2_w 8.310629417537063E264
      // 2a02: dastore
      // 2a03: dup
      // 2a04: sipush 1361
      // 2a07: ldc2_w 2.2590636576955473E265
      // 2a0a: dastore
      // 2a0b: dup
      // 2a0c: sipush 1362
      // 2a0f: ldc2_w 6.1407711078356886E265
      // 2a12: dastore
      // 2a13: dup
      // 2a14: sipush 1363
      // 2a17: ldc2_w 1.6692346202615142E266
      // 2a1a: dastore
      // 2a1b: dup
      // 2a1c: sipush 1364
      // 2a1f: ldc2_w 4.5374504961394207E266
      // 2a22: dastore
      // 2a23: dup
      // 2a24: sipush 1365
      // 2a27: ldc2_w 1.2334070098307164E267
      // 2a2a: dastore
      // 2a2b: dup
      // 2a2c: sipush 1366
      // 2a2f: ldc2_w 3.3527476928456816E267
      // 2a32: dastore
      // 2a33: dup
      // 2a34: sipush 1367
      // 2a37: ldc2_w 9.113713162029408E267
      // 2a3a: dastore
      // 2a3b: dup
      // 2a3c: sipush 1368
      // 2a3f: ldc2_w 2.4773638527240193E268
      // 2a42: dastore
      // 2a43: dup
      // 2a44: sipush 1369
      // 2a47: ldc2_w 6.734172833429278E268
      // 2a4a: dastore
      // 2a4b: dup
      // 2a4c: sipush 1370
      // 2a4f: ldc2_w 1.8305382378470305E269
      // 2a52: dastore
      // 2a53: dup
      // 2a54: sipush 1371
      // 2a57: ldc2_w 4.9759187284770303E269
      // 2a5a: dastore
      // 2a5b: dup
      // 2a5c: sipush 1372
      // 2a5f: ldc2_w 1.352594940263854E270
      // 2a62: dastore
      // 2a63: dup
      // 2a64: sipush 1373
      // 2a67: ldc2_w 3.6767339705169146E270
      // 2a6a: dastore
      // 2a6b: dup
      // 2a6c: sipush 1374
      // 2a6f: ldc2_w 9.994400500679653E270
      // 2a72: dastore
      // 2a73: dup
      // 2a74: sipush 1375
      // 2a77: ldc2_w 2.716759624268743E271
      // 2a7a: dastore
      // 2a7b: dup
      // 2a7c: sipush 1376
      // 2a7f: ldc2_w 7.384918458508588E271
      // 2a82: dastore
      // 2a83: dup
      // 2a84: sipush 1377
      // 2a87: ldc2_w 2.007428933605617E272
      // 2a8a: dastore
      // 2a8b: dup
      // 2a8c: sipush 1378
      // 2a8f: ldc2_w 5.456757565532369E272
      // 2a92: dastore
      // 2a93: dup
      // 2a94: sipush 1379
      // 2a97: ldc2_w 1.4833003969415539E273
      // 2a9a: dastore
      // 2a9b: dup
      // 2a9c: sipush 1380
      // 2a9f: ldc2_w 4.0320284712983994E273
      // 2aa2: dastore
      // 2aa3: dup
      // 2aa4: sipush 1381
      // 2aa7: ldc2_w 1.096019026243815E274
      // 2aaa: dastore
      // 2aab: dup
      // 2aac: sipush 1382
      // 2aaf: ldc2_w 2.979288529962515E274
      // 2ab2: dastore
      // 2ab3: dup
      // 2ab4: sipush 1383
      // 2ab7: ldc2_w 8.098545495417704E274
      // 2aba: dastore
      // 2abb: dup
      // 2abc: sipush 1384
      // 2abf: ldc2_w 2.201412886580694E275
      // 2ac2: dastore
      // 2ac3: dup
      // 2ac4: sipush 1385
      // 2ac7: ldc2_w 5.984060832462728E275
      // 2aca: dastore
      // 2acb: dup
      // 2acc: sipush 1386
      // 2acf: ldc2_w 1.6266362950862408E276
      // 2ad2: dastore
      // 2ad3: dup
      // 2ad4: sipush 1387
      // 2ad7: ldc2_w 4.4216561713555547E276
      // 2ada: dastore
      // 2adb: dup
      // 2adc: sipush 1388
      // 2adf: ldc2_w 1.2019307065458128E277
      // 2ae2: dastore
      // 2ae3: dup
      // 2ae4: sipush 1389
      // 2ae7: ldc2_w 3.2671863888979078E277
      // 2aea: dastore
      // 2aeb: dup
      // 2aec: sipush 1390
      // 2aef: ldc2_w 8.881133159512924E277
      // 2af2: dastore
      // 2af3: dup
      // 2af4: sipush 1391
      // 2af7: ldc2_w 2.4141423627760256E278
      // 2afa: dastore
      // 2afb: dup
      // 2afc: sipush 1392
      // 2aff: ldc2_w 6.562319473965767E278
      // 2b02: dastore
      // 2b03: dup
      // 2b04: sipush 1393
      // 2b07: ldc2_w 1.7838233889223407E279
      // 2b0a: dastore
      // 2b0b: dup
      // 2b0c: sipush 1394
      // 2b0f: ldc2_w 4.848934634563382E279
      // 2b12: dastore
      // 2b13: dup
      // 2b14: sipush 1395
      // 2b17: ldc2_w 1.3180771991576186E280
      // 2b1a: dastore
      // 2b1b: dup
      // 2b1c: sipush 1396
      // 2b1f: ldc2_w 3.5829049382293792E280
      // 2b22: dastore
      // 2b23: dup
      // 2b24: sipush 1397
      // 2b27: ldc2_w 9.739345931419228E280
      // 2b2a: dastore
      // 2b2b: dup
      // 2b2c: sipush 1398
      // 2b2f: ldc2_w 2.6474285478041252E281
      // 2b32: dastore
      // 2b33: dup
      // 2b34: sipush 1399
      // 2b37: ldc2_w 7.196457718729758E281
      // 2b3a: dastore
      // 2b3b: dup
      // 2b3c: sipush 1400
      // 2b3f: ldc2_w 1.956199868121249E282
      // 2b42: dastore
      // 2b43: dup
      // 2b44: sipush 1401
      // 2b47: ldc2_w 5.31750271790054E282
      // 2b4a: dastore
      // 2b4b: dup
      // 2b4c: sipush 1402
      // 2b4f: ldc2_w 1.4454470027638629E283
      // 2b52: dastore
      // 2b53: dup
      // 2b54: sipush 1403
      // 2b57: ldc2_w 3.929132560365955E283
      // 2b5a: dastore
      // 2b5b: dup
      // 2b5c: sipush 1404
      // 2b5f: ldc2_w 1.0680488848057261E284
      // 2b62: dastore
      // 2b63: dup
      // 2b64: sipush 1405
      // 2b67: ldc2_w 2.9032581477488686E284
      // 2b6a: dastore
      // 2b6b: dup
      // 2b6c: sipush 1406
      // 2b6f: ldc2_w 7.89187408872514E284
      // 2b72: dastore
      // 2b73: dup
      // 2b74: sipush 1407
      // 2b77: ldc2_w 2.1452336456259667E285
      // 2b7a: dastore
      // 2b7b: dup
      // 2b7c: sipush 1408
      // 2b7f: ldc2_w 5.831349876080173E285
      // 2b82: dastore
      // 2b83: dup
      // 2b84: sipush 1409
      // 2b87: ldc2_w 1.5851251724785243E286
      // 2b8a: dastore
      // 2b8b: dup
      // 2b8c: sipush 1410
      // 2b8f: ldc2_w 4.308816643345461E286
      // 2b92: dastore
      // 2b93: dup
      // 2b94: sipush 1411
      // 2b97: ldc2_w 1.1712579802975851E287
      // 2b9a: dastore
      // 2b9b: dup
      // 2b9c: sipush 1412
      // 2b9f: ldc2_w 3.1838092090922606E287
      // 2ba2: dastore
      // 2ba3: dup
      // 2ba4: sipush 1413
      // 2ba7: ldc2_w 8.654490685278886E287
      // 2baa: dastore
      // 2bab: dup
      // 2bac: sipush 1414
      // 2baf: ldc2_w 2.3525345191912968E288
      // 2bb2: dastore
      // 2bb3: dup
      // 2bb4: sipush 1415
      // 2bb7: ldc2_w 6.39485115791896E288
      // 2bba: dastore
      // 2bbb: dup
      // 2bbc: sipush 1416
      // 2bbf: ldc2_w 1.7383009254496851E289
      // 2bc2: dastore
      // 2bc3: dup
      // 2bc4: sipush 1417
      // 2bc7: ldc2_w 4.725191397657393E289
      // 2bca: dastore
      // 2bcb: dup
      // 2bcc: sipush 1418
      // 2bcf: ldc2_w 1.2844402232816276E290
      // 2bd2: dastore
      // 2bd3: dup
      // 2bd4: sipush 1419
      // 2bd7: ldc2_w 3.491470347090126E290
      // 2bda: dastore
      // 2bdb: dup
      // 2bdc: sipush 1420
      // 2bdf: ldc2_w 9.490800658395667E290
      // 2be2: dastore
      // 2be3: dup
      // 2be4: sipush 1421
      // 2be7: ldc2_w 2.579867270991543E291
      // 2bea: dastore
      // 2beb: dup
      // 2bec: sipush 1422
      // 2bef: ldc2_w 7.012806239173502E291
      // 2bf2: dastore
      // 2bf3: dup
      // 2bf4: sipush 1423
      // 2bf7: ldc2_w 1.906278351789277E292
      // 2bfa: dastore
      // 2bfb: dup
      // 2bfc: sipush 1424
      // 2bff: ldc2_w 5.181801397059486E292
      // 2c02: dastore
      // 2c03: dup
      // 2c04: sipush 1425
      // 2c07: ldc2_w 1.408559707497606E293
      // 2c0a: dastore
      // 2c0b: dup
      // 2c0c: sipush 1426
      // 2c0f: ldc2_w 3.8288623079292884E293
      // 2c12: dastore
      // 2c13: dup
      // 2c14: sipush 1427
      // 2c17: ldc2_w 1.0407926842436056E294
      // 2c1a: dastore
      // 2c1b: dup
      // 2c1c: sipush 1428
      // 2c1f: ldc2_w 2.829168201470791E294
      // 2c22: dastore
      // 2c23: dup
      // 2c24: sipush 1429
      // 2c27: ldc2_w 7.690475570840264E294
      // 2c2a: dastore
      // 2c2b: dup
      // 2c2c: sipush 1430
      // 2c2f: ldc2_w 2.0904882610105383E295
      // 2c32: dastore
      // 2c33: dup
      // 2c34: sipush 1431
      // 2c37: ldc2_w 5.68253547942899E295
      // 2c3a: dastore
      // 2c3b: dup
      // 2c3c: sipush 1432
      // 2c3f: ldc2_w 1.544673396032028E296
      // 2c42: dastore
      // 2c43: dup
      // 2c44: sipush 1433
      // 2c47: ldc2_w 4.1988574190754736E296
      // 2c4a: dastore
      // 2c4b: dup
      // 2c4c: sipush 1434
      // 2c4f: ldc2_w 1.1413677466646359E297
      // 2c52: dastore
      // 2c53: dup
      // 2c54: sipush 1435
      // 2c57: ldc2_w 3.102559332875688E297
      // 2c5a: dastore
      // 2c5b: dup
      // 2c5c: sipush 1436
      // 2c5f: ldc2_w 8.433630296371073E297
      // 2c62: dastore
      // 2c63: dup
      // 2c64: sipush 1437
      // 2c67: ldc2_w 2.292498520423419E298
      // 2c6a: dastore
      // 2c6b: dup
      // 2c6c: sipush 1438
      // 2c6f: ldc2_w 6.23165710486722E298
      // 2c72: dastore
      // 2c73: dup
      // 2c74: sipush 1439
      // 2c77: ldc2_w 1.6939399242810123E299
      // 2c7a: dastore
      // 2c7b: dup
      // 2c7c: sipush 1440
      // 2c7f: ldc2_w 4.604606371472047E299
      // 2c82: dastore
      // 2c83: dup
      // 2c84: sipush 1441
      // 2c87: ldc2_w 1.2516618713553432E300
      // 2c8a: dastore
      // 2c8b: dup
      // 2c8c: sipush 1442
      // 2c8f: ldc2_w 3.402369329874797E300
      // 2c92: dastore
      // 2c93: dup
      // 2c94: sipush 1443
      // 2c97: ldc2_w 9.248598815279678E300
      // 2c9a: dastore
      // 2c9b: dup
      // 2c9c: sipush 1444
      // 2c9f: ldc2_w 2.51402968559859E301
      // 2ca2: dastore
      // 2ca3: dup
      // 2ca4: sipush 1445
      // 2ca7: ldc2_w 6.833842035076675E301
      // 2caa: dastore
      // 2cab: dup
      // 2cac: sipush 1446
      // 2caf: ldc2_w 1.8576309291617257E302
      // 2cb2: dastore
      // 2cb3: dup
      // 2cb4: sipush 1447
      // 2cb7: ldc2_w 5.049564425991982E302
      // 2cba: dastore
      // 2cbb: dup
      // 2cbc: sipush 1448
      // 2cbf: ldc2_w 1.3726137091534984E303
      // 2cc2: dastore
      // 2cc3: dup
      // 2cc4: sipush 1449
      // 2cc7: ldc2_w 3.7311513682845094E303
      // 2cca: dastore
      // 2ccb: dup
      // 2ccc: sipush 1450
      // 2ccf: ldc2_w 1.0142320772726397E304
      // 2cd2: dastore
      // 2cd3: dup
      // 2cd4: sipush 1451
      // 2cd7: ldc2_w 2.7569686255975333E304
      // 2cda: dastore
      // 2cdb: dup
      // 2cdc: sipush 1452
      // 2cdf: ldc2_w 7.494218049456063E304
      // 2ce2: dastore
      // 2ce3: dup
      // 2ce4: sipush 1453
      // 2ce7: ldc2_w 2.037139607241041E305
      // 2cea: dastore
      // 2ceb: dup
      // 2cec: sipush 1454
      // 2cef: ldc2_w 5.5375196488302575E305
      // 2cf2: dastore
      // 2cf3: dup
      // 2cf4: sipush 1455
      // 2cf7: ldc2_w 1.5052539519895093E306
      // 2cfa: dastore
      // 2cfb: dup
      // 2cfc: sipush 1456
      // 2cff: ldc2_w 4.091704288360009E306
      // 2d02: dastore
      // 2d03: dup
      // 2d04: sipush 1457
      // 2d07: ldc2_w 1.1122405335641184E307
      // 2d0a: dastore
      // 2d0b: dup
      // 2d0c: sipush 1458
      // 2d0f: ldc2_w 3.023383151402969E307
      // 2d12: dastore
      // 2d13: dup
      // 2d14: sipush 1459
      // 2d17: ldc2_w 8.218407798110846E307
      // 2d1a: dastore
      // 2d1b: dup
      // 2d1c: sipush 1460
      // 2d1f: ldc2_w NaN
      // 2d22: dastore
      // 2d23: dup
      // 2d24: sipush 1461
      // 2d27: ldc2_w NaN
      // 2d2a: dastore
      // 2d2b: dup
      // 2d2c: sipush 1462
      // 2d2f: ldc2_w NaN
      // 2d32: dastore
      // 2d33: dup
      // 2d34: sipush 1463
      // 2d37: ldc2_w NaN
      // 2d3a: dastore
      // 2d3b: dup
      // 2d3c: sipush 1464
      // 2d3f: ldc2_w NaN
      // 2d42: dastore
      // 2d43: dup
      // 2d44: sipush 1465
      // 2d47: ldc2_w NaN
      // 2d4a: dastore
      // 2d4b: dup
      // 2d4c: sipush 1466
      // 2d4f: ldc2_w NaN
      // 2d52: dastore
      // 2d53: dup
      // 2d54: sipush 1467
      // 2d57: ldc2_w NaN
      // 2d5a: dastore
      // 2d5b: dup
      // 2d5c: sipush 1468
      // 2d5f: ldc2_w NaN
      // 2d62: dastore
      // 2d63: dup
      // 2d64: sipush 1469
      // 2d67: ldc2_w NaN
      // 2d6a: dastore
      // 2d6b: dup
      // 2d6c: sipush 1470
      // 2d6f: ldc2_w NaN
      // 2d72: dastore
      // 2d73: dup
      // 2d74: sipush 1471
      // 2d77: ldc2_w NaN
      // 2d7a: dastore
      // 2d7b: dup
      // 2d7c: sipush 1472
      // 2d7f: ldc2_w NaN
      // 2d82: dastore
      // 2d83: dup
      // 2d84: sipush 1473
      // 2d87: ldc2_w NaN
      // 2d8a: dastore
      // 2d8b: dup
      // 2d8c: sipush 1474
      // 2d8f: ldc2_w NaN
      // 2d92: dastore
      // 2d93: dup
      // 2d94: sipush 1475
      // 2d97: ldc2_w NaN
      // 2d9a: dastore
      // 2d9b: dup
      // 2d9c: sipush 1476
      // 2d9f: ldc2_w NaN
      // 2da2: dastore
      // 2da3: dup
      // 2da4: sipush 1477
      // 2da7: ldc2_w NaN
      // 2daa: dastore
      // 2dab: dup
      // 2dac: sipush 1478
      // 2daf: ldc2_w NaN
      // 2db2: dastore
      // 2db3: dup
      // 2db4: sipush 1479
      // 2db7: ldc2_w NaN
      // 2dba: dastore
      // 2dbb: dup
      // 2dbc: sipush 1480
      // 2dbf: ldc2_w NaN
      // 2dc2: dastore
      // 2dc3: dup
      // 2dc4: sipush 1481
      // 2dc7: ldc2_w NaN
      // 2dca: dastore
      // 2dcb: dup
      // 2dcc: sipush 1482
      // 2dcf: ldc2_w NaN
      // 2dd2: dastore
      // 2dd3: dup
      // 2dd4: sipush 1483
      // 2dd7: ldc2_w NaN
      // 2dda: dastore
      // 2ddb: dup
      // 2ddc: sipush 1484
      // 2ddf: ldc2_w NaN
      // 2de2: dastore
      // 2de3: dup
      // 2de4: sipush 1485
      // 2de7: ldc2_w NaN
      // 2dea: dastore
      // 2deb: dup
      // 2dec: sipush 1486
      // 2def: ldc2_w NaN
      // 2df2: dastore
      // 2df3: dup
      // 2df4: sipush 1487
      // 2df7: ldc2_w NaN
      // 2dfa: dastore
      // 2dfb: dup
      // 2dfc: sipush 1488
      // 2dff: ldc2_w NaN
      // 2e02: dastore
      // 2e03: dup
      // 2e04: sipush 1489
      // 2e07: ldc2_w NaN
      // 2e0a: dastore
      // 2e0b: dup
      // 2e0c: sipush 1490
      // 2e0f: ldc2_w NaN
      // 2e12: dastore
      // 2e13: dup
      // 2e14: sipush 1491
      // 2e17: ldc2_w NaN
      // 2e1a: dastore
      // 2e1b: dup
      // 2e1c: sipush 1492
      // 2e1f: ldc2_w NaN
      // 2e22: dastore
      // 2e23: dup
      // 2e24: sipush 1493
      // 2e27: ldc2_w NaN
      // 2e2a: dastore
      // 2e2b: dup
      // 2e2c: sipush 1494
      // 2e2f: ldc2_w NaN
      // 2e32: dastore
      // 2e33: dup
      // 2e34: sipush 1495
      // 2e37: ldc2_w NaN
      // 2e3a: dastore
      // 2e3b: dup
      // 2e3c: sipush 1496
      // 2e3f: ldc2_w NaN
      // 2e42: dastore
      // 2e43: dup
      // 2e44: sipush 1497
      // 2e47: ldc2_w NaN
      // 2e4a: dastore
      // 2e4b: dup
      // 2e4c: sipush 1498
      // 2e4f: ldc2_w NaN
      // 2e52: dastore
      // 2e53: dup
      // 2e54: sipush 1499
      // 2e57: ldc2_w NaN
      // 2e5a: dastore
      // 2e5b: putstatic org/apache/commons/math3/util/FastMathLiteralArrays.EXP_INT_A [D
      // 2e5e: sipush 1500
      // 2e61: newarray 7
      // 2e63: dup
      // 2e64: bipush 0
      // 2e65: dconst_0
      // 2e66: dastore
      // 2e67: dup
      // 2e68: bipush 1
      // 2e69: ldc2_w NaN
      // 2e6c: dastore
      // 2e6d: dup
      // 2e6e: bipush 2
      // 2e6f: ldc2_w NaN
      // 2e72: dastore
      // 2e73: dup
      // 2e74: bipush 3
      // 2e75: ldc2_w NaN
      // 2e78: dastore
      // 2e79: dup
      // 2e7a: bipush 4
      // 2e7b: ldc2_w NaN
      // 2e7e: dastore
      // 2e7f: dup
      // 2e80: bipush 5
      // 2e81: ldc2_w NaN
      // 2e84: dastore
      // 2e85: dup
      // 2e86: bipush 6
      // 2e88: ldc2_w NaN
      // 2e8b: dastore
      // 2e8c: dup
      // 2e8d: bipush 7
      // 2e8f: ldc2_w NaN
      // 2e92: dastore
      // 2e93: dup
      // 2e94: bipush 8
      // 2e96: ldc2_w NaN
      // 2e99: dastore
      // 2e9a: dup
      // 2e9b: bipush 9
      // 2e9d: ldc2_w NaN
      // 2ea0: dastore
      // 2ea1: dup
      // 2ea2: bipush 10
      // 2ea4: ldc2_w NaN
      // 2ea7: dastore
      // 2ea8: dup
      // 2ea9: bipush 11
      // 2eab: ldc2_w NaN
      // 2eae: dastore
      // 2eaf: dup
      // 2eb0: bipush 12
      // 2eb2: ldc2_w NaN
      // 2eb5: dastore
      // 2eb6: dup
      // 2eb7: bipush 13
      // 2eb9: ldc2_w NaN
      // 2ebc: dastore
      // 2ebd: dup
      // 2ebe: bipush 14
      // 2ec0: ldc2_w NaN
      // 2ec3: dastore
      // 2ec4: dup
      // 2ec5: bipush 15
      // 2ec7: ldc2_w NaN
      // 2eca: dastore
      // 2ecb: dup
      // 2ecc: bipush 16
      // 2ece: ldc2_w NaN
      // 2ed1: dastore
      // 2ed2: dup
      // 2ed3: bipush 17
      // 2ed5: ldc2_w NaN
      // 2ed8: dastore
      // 2ed9: dup
      // 2eda: bipush 18
      // 2edc: ldc2_w NaN
      // 2edf: dastore
      // 2ee0: dup
      // 2ee1: bipush 19
      // 2ee3: ldc2_w NaN
      // 2ee6: dastore
      // 2ee7: dup
      // 2ee8: bipush 20
      // 2eea: ldc2_w NaN
      // 2eed: dastore
      // 2eee: dup
      // 2eef: bipush 21
      // 2ef1: ldc2_w NaN
      // 2ef4: dastore
      // 2ef5: dup
      // 2ef6: bipush 22
      // 2ef8: ldc2_w NaN
      // 2efb: dastore
      // 2efc: dup
      // 2efd: bipush 23
      // 2eff: ldc2_w NaN
      // 2f02: dastore
      // 2f03: dup
      // 2f04: bipush 24
      // 2f06: ldc2_w NaN
      // 2f09: dastore
      // 2f0a: dup
      // 2f0b: bipush 25
      // 2f0d: ldc2_w NaN
      // 2f10: dastore
      // 2f11: dup
      // 2f12: bipush 26
      // 2f14: ldc2_w NaN
      // 2f17: dastore
      // 2f18: dup
      // 2f19: bipush 27
      // 2f1b: ldc2_w NaN
      // 2f1e: dastore
      // 2f1f: dup
      // 2f20: bipush 28
      // 2f22: ldc2_w NaN
      // 2f25: dastore
      // 2f26: dup
      // 2f27: bipush 29
      // 2f29: ldc2_w NaN
      // 2f2c: dastore
      // 2f2d: dup
      // 2f2e: bipush 30
      // 2f30: ldc2_w NaN
      // 2f33: dastore
      // 2f34: dup
      // 2f35: bipush 31
      // 2f37: ldc2_w NaN
      // 2f3a: dastore
      // 2f3b: dup
      // 2f3c: bipush 32
      // 2f3e: ldc2_w NaN
      // 2f41: dastore
      // 2f42: dup
      // 2f43: bipush 33
      // 2f45: ldc2_w NaN
      // 2f48: dastore
      // 2f49: dup
      // 2f4a: bipush 34
      // 2f4c: ldc2_w NaN
      // 2f4f: dastore
      // 2f50: dup
      // 2f51: bipush 35
      // 2f53: ldc2_w NaN
      // 2f56: dastore
      // 2f57: dup
      // 2f58: bipush 36
      // 2f5a: ldc2_w NaN
      // 2f5d: dastore
      // 2f5e: dup
      // 2f5f: bipush 37
      // 2f61: ldc2_w NaN
      // 2f64: dastore
      // 2f65: dup
      // 2f66: bipush 38
      // 2f68: ldc2_w NaN
      // 2f6b: dastore
      // 2f6c: dup
      // 2f6d: bipush 39
      // 2f6f: ldc2_w NaN
      // 2f72: dastore
      // 2f73: dup
      // 2f74: bipush 40
      // 2f76: ldc2_w NaN
      // 2f79: dastore
      // 2f7a: dup
      // 2f7b: bipush 41
      // 2f7d: ldc2_w -1.76097684E-316
      // 2f80: dastore
      // 2f81: dup
      // 2f82: bipush 42
      // 2f84: ldc2_w -2.44242319E-315
      // 2f87: dastore
      // 2f88: dup
      // 2f89: bipush 43
      // 2f8b: ldc2_w -9.879317845E-315
      // 2f8e: dastore
      // 2f8f: dup
      // 2f90: bipush 44
      // 2f92: ldc2_w -1.3811462167E-314
      // 2f95: dastore
      // 2f96: dup
      // 2f97: bipush 45
      // 2f99: ldc2_w 2.1775261204E-314
      // 2f9c: dastore
      // 2f9d: dup
      // 2f9e: bipush 46
      // 2fa0: ldc2_w -1.4379095864E-313
      // 2fa3: dastore
      // 2fa4: dup
      // 2fa5: bipush 47
      // 2fa7: ldc2_w 1.4219324087E-313
      // 2faa: dastore
      // 2fab: dup
      // 2fac: bipush 48
      // 2fae: ldc2_w 1.00605438061E-312
      // 2fb1: dastore
      // 2fb2: dup
      // 2fb3: bipush 49
      // 2fb5: ldc2_w -1.287101187097E-312
      // 2fb8: dastore
      // 2fb9: dup
      // 2fba: bipush 50
      // 2fbc: ldc2_w 5.33839690397E-312
      // 2fbf: dastore
      // 2fc0: dup
      // 2fc1: bipush 51
      // 2fc3: ldc2_w -9.35130825405E-313
      // 2fc6: dastore
      // 2fc7: dup
      // 2fc8: bipush 52
      // 2fca: ldc2_w -4.15218681073E-311
      // 2fcd: dastore
      // 2fce: dup
      // 2fcf: bipush 53
      // 2fd1: ldc2_w 4.546040329134E-311
      // 2fd4: dastore
      // 2fd5: dup
      // 2fd6: bipush 54
      // 2fd8: ldc2_w -1.57333572310673E-310
      // 2fdb: dastore
      // 2fdc: dup
      // 2fdd: bipush 55
      // 2fdf: ldc2_w 1.05387548454467E-309
      // 2fe2: dastore
      // 2fe3: dup
      // 2fe4: bipush 56
      // 2fe6: ldc2_w 2.095732474644446E-309
      // 2fe9: dastore
      // 2fea: dup
      // 2feb: bipush 57
      // 2fed: ldc2_w -2.62524392470767E-310
      // 2ff0: dastore
      // 2ff1: dup
      // 2ff2: bipush 58
      // 2ff4: ldc2_w 5.86440876259637E-309
      // 2ff7: dastore
      // 2ff8: dup
      // 2ff9: bipush 59
      // 2ffb: ldc2_w -2.401816502004675E-309
      // 2ffe: dastore
      // 2fff: dup
      // 3000: bipush 60
      // 3002: ldc2_w -2.2711230715729753E-308
      // 3005: dastore
      // 3006: dup
      // 3007: bipush 61
      // 3009: ldc2_w 2.0670460065057715E-307
      // 300c: dastore
      // 300d: dup
      // 300e: bipush 62
      // 3010: ldc2_w 3.436860020483706E-308
      // 3013: dastore
      // 3014: dup
      // 3015: bipush 63
      // 3017: ldc2_w 2.0862243734177337E-306
      // 301a: dastore
      // 301b: dup
      // 301c: bipush 64
      // 301e: ldc2_w -4.637025318037353E-306
      // 3021: dastore
      // 3022: dup
      // 3023: bipush 65
      // 3025: ldc2_w 9.222671009756424E-306
      // 3028: dastore
      // 3029: dup
      // 302a: bipush 66
      // 302c: ldc2_w 6.704597874020559E-305
      // 302f: dastore
      // 3030: dup
      // 3031: bipush 67
      // 3033: ldc2_w 4.351284159444109E-305
      // 3036: dastore
      // 3037: dup
      // 3038: bipush 68
      // 303a: ldc2_w 4.232889602759328E-304
      // 303d: dastore
      // 303e: dup
      // 303f: bipush 69
      // 3041: ldc2_w 1.2840977763293412E-303
      // 3044: dastore
      // 3045: dup
      // 3046: bipush 70
      // 3048: ldc2_w -2.6993478083348727E-303
      // 304b: dastore
      // 304c: dup
      // 304d: bipush 71
      // 304f: ldc2_w -1.053265874779237E-303
      // 3052: dastore
      // 3053: dup
      // 3054: bipush 72
      // 3056: ldc2_w 1.207746682843556E-303
      // 3059: dastore
      // 305a: dup
      // 305b: bipush 73
      // 305d: ldc2_w 5.21281096513035E-303
      // 3060: dastore
      // 3061: dup
      // 3062: bipush 74
      // 3064: ldc2_w 1.6515377082609677E-301
      // 3067: dastore
      // 3068: dup
      // 3069: bipush 75
      // 306b: ldc2_w 3.3951607353932444E-301
      // 306e: dastore
      // 306f: dup
      // 3070: bipush 76
      // 3072: ldc2_w 5.609418227003629E-301
      // 3075: dastore
      // 3076: dup
      // 3077: bipush 77
      // 3079: ldc2_w 4.238775357914848E-300
      // 307c: dastore
      // 307d: dup
      // 307e: bipush 78
      // 3080: ldc2_w -9.441842771290538E-300
      // 3083: dastore
      // 3084: dup
      // 3085: bipush 79
      // 3087: ldc2_w -2.1745347282493023E-299
      // 308a: dastore
      // 308b: dup
      // 308c: bipush 80
      // 308e: ldc2_w -6.203839803215248E-299
      // 3091: dastore
      // 3092: dup
      // 3093: bipush 81
      // 3095: ldc2_w -5.617718879466363E-299
      // 3098: dastore
      // 3099: dup
      // 309a: bipush 82
      // 309c: ldc2_w 5.2869976233132615E-298
      // 309f: dastore
      // 30a0: dup
      // 30a1: bipush 83
      // 30a3: ldc2_w -1.4300075619643524E-298
      // 30a6: dastore
      // 30a7: dup
      // 30a8: bipush 84
      // 30aa: ldc2_w 4.3198234936686506E-297
      // 30ad: dastore
      // 30ae: dup
      // 30af: bipush 85
      // 30b1: ldc2_w -2.6448316331572387E-297
      // 30b4: dastore
      // 30b5: dup
      // 30b6: bipush 86
      // 30b8: ldc2_w 4.315655444002347E-296
      // 30bb: dastore
      // 30bc: dup
      // 30bd: bipush 87
      // 30bf: ldc2_w -7.253671992213344E-296
      // 30c2: dastore
      // 30c3: dup
      // 30c4: bipush 88
      // 30c6: ldc2_w -1.1288398461391523E-295
      // 30c9: dastore
      // 30ca: dup
      // 30cb: bipush 89
      // 30cd: ldc2_w -4.83901764243093E-296
      // 30d0: dastore
      // 30d1: dup
      // 30d2: bipush 90
      // 30d4: ldc2_w 1.7407497662694827E-295
      // 30d7: dastore
      // 30d8: dup
      // 30d9: bipush 91
      // 30db: ldc2_w 1.1969717029666017E-294
      // 30de: dastore
      // 30df: dup
      // 30e0: bipush 92
      // 30e2: ldc2_w -7.752519943329177E-294
      // 30e5: dastore
      // 30e6: dup
      // 30e7: bipush 93
      // 30e9: ldc2_w -4.019569741253664E-293
      // 30ec: dastore
      // 30ed: dup
      // 30ee: bipush 94
      // 30f0: ldc2_w -2.4467928392518484E-293
      // 30f3: dastore
      // 30f4: dup
      // 30f5: bipush 95
      // 30f7: ldc2_w -1.0269233640424235E-292
      // 30fa: dastore
      // 30fb: dup
      // 30fc: bipush 96
      // 30fe: ldc2_w -3.2330960700986594E-292
      // 3101: dastore
      // 3102: dup
      // 3103: bipush 97
      // 3105: ldc2_w -1.440995270758115E-291
      // 3108: dastore
      // 3109: dup
      // 310a: bipush 98
      // 310c: ldc2_w -3.726946038150935E-291
      // 310f: dastore
      // 3110: dup
      // 3111: bipush 99
      // 3113: ldc2_w -1.3424576100819801E-291
      // 3116: dastore
      // 3117: dup
      // 3118: bipush 100
      // 311a: ldc2_w -3.128894928199484E-290
      // 311d: dastore
      // 311e: dup
      // 311f: bipush 101
      // 3121: ldc2_w -5.989337506920005E-290
      // 3124: dastore
      // 3125: dup
      // 3126: bipush 102
      // 3128: ldc2_w -9.438168176533759E-290
      // 312b: dastore
      // 312c: dup
      // 312d: bipush 103
      // 312f: ldc2_w -1.9220613500411237E-289
      // 3132: dastore
      // 3133: dup
      // 3134: bipush 104
      // 3136: ldc2_w 2.1186736024949195E-289
      // 3139: dastore
      // 313a: dup
      // 313b: bipush 105
      // 313d: ldc2_w 6.3015208029537436E-288
      // 3140: dastore
      // 3141: dup
      // 3142: bipush 106
      // 3144: ldc2_w -8.168129112703755E-288
      // 3147: dastore
      // 3148: dup
      // 3149: bipush 107
      // 314b: ldc2_w -1.6040513288090055E-287
      // 314e: dastore
      // 314f: dup
      // 3150: bipush 108
      // 3152: ldc2_w -1.0809972724404233E-287
      // 3155: dastore
      // 3156: dup
      // 3157: bipush 109
      // 3159: ldc2_w -3.080380385962424E-286
      // 315c: dastore
      // 315d: dup
      // 315e: bipush 110
      // 3160: ldc2_w 2.6399157174374624E-286
      // 3163: dastore
      // 3164: dup
      // 3165: bipush 111
      // 3167: ldc2_w 1.3317127674213423E-285
      // 316a: dastore
      // 316b: dup
      // 316c: bipush 112
      // 316e: ldc2_w -3.5821668044872306E-285
      // 3171: dastore
      // 3172: dup
      // 3173: bipush 113
      // 3175: ldc2_w 1.978536584535392E-284
      // 3178: dastore
      // 3179: dup
      // 317a: bipush 114
      // 317c: ldc2_w 1.3399392455370071E-284
      // 317f: dastore
      // 3180: dup
      // 3181: bipush 115
      // 3183: ldc2_w -2.870168560029448E-284
      // 3186: dastore
      // 3187: dup
      // 3188: bipush 116
      // 318a: ldc2_w 3.5311184272663063E-283
      // 318d: dastore
      // 318e: dup
      // 318f: bipush 117
      // 3191: ldc2_w -7.204247881190918E-283
      // 3194: dastore
      // 3195: dup
      // 3196: bipush 118
      // 3198: ldc2_w 3.2425604548983798E-282
      // 319b: dastore
      // 319c: dup
      // 319d: bipush 119
      // 319f: ldc2_w 3.913063150326019E-282
      // 31a2: dastore
      // 31a3: dup
      // 31a4: bipush 120
      // 31a6: ldc2_w -2.260957518848075E-281
      // 31a9: dastore
      // 31aa: dup
      // 31ab: bipush 121
      // 31ad: ldc2_w 3.807242187736102E-281
      // 31b0: dastore
      // 31b1: dup
      // 31b2: bipush 122
      // 31b4: ldc2_w -5.095591405025083E-281
      // 31b7: dastore
      // 31b8: dup
      // 31b9: bipush 123
      // 31bb: ldc2_w 2.3400625068490396E-280
      // 31be: dastore
      // 31bf: dup
      // 31c0: bipush 124
      // 31c2: ldc2_w -1.1564717694090882E-280
      // 31c5: dastore
      // 31c6: dup
      // 31c7: bipush 125
      // 31c9: ldc2_w -3.517594695450786E-279
      // 31cc: dastore
      // 31cd: dup
      // 31ce: bipush 126
      // 31d0: ldc2_w 6.666544384808297E-279
      // 31d3: dastore
      // 31d4: dup
      // 31d5: bipush 127
      // 31d7: ldc2_w -9.204784113858607E-279
      // 31da: dastore
      // 31db: dup
      // 31dc: sipush 128
      // 31df: ldc2_w 4.8677119923665573E-278
      // 31e2: dastore
      // 31e3: dup
      // 31e4: sipush 129
      // 31e7: ldc2_w 7.942176091555472E-278
      // 31ea: dastore
      // 31eb: dup
      // 31ec: sipush 130
      // 31ef: ldc2_w -2.5113270522478854E-277
      // 31f2: dastore
      // 31f3: dup
      // 31f4: sipush 131
      // 31f7: ldc2_w 5.332900939354667E-277
      // 31fa: dastore
      // 31fb: dup
      // 31fc: sipush 132
      // 31ff: ldc2_w -3.491241408725929E-276
      // 3202: dastore
      // 3203: dup
      // 3204: sipush 133
      // 3207: ldc2_w -2.1141094074221325E-276
      // 320a: dastore
      // 320b: dup
      // 320c: sipush 134
      // 320f: ldc2_w 1.722049095222509E-275
      // 3212: dastore
      // 3213: dup
      // 3214: sipush 135
      // 3217: ldc2_w 4.0430160253378594E-275
      // 321a: dastore
      // 321b: dup
      // 321c: sipush 136
      // 321f: ldc2_w 1.9888195459082551E-274
      // 3222: dastore
      // 3223: dup
      // 3224: sipush 137
      // 3227: ldc2_w 3.230089643550739E-275
      // 322a: dastore
      // 322b: dup
      // 322c: sipush 138
      // 322f: ldc2_w 5.077824728028163E-274
      // 3232: dastore
      // 3233: dup
      // 3234: sipush 139
      // 3237: ldc2_w -3.526547961682877E-274
      // 323a: dastore
      // 323b: dup
      // 323c: sipush 140
      // 323f: ldc2_w -6.4376298274983765E-273
      // 3242: dastore
      // 3243: dup
      // 3244: sipush 141
      // 3247: ldc2_w -2.5338279333399964E-272
      // 324a: dastore
      // 324b: dup
      // 324c: sipush 142
      // 324f: ldc2_w -3.614847626733713E-272
      // 3252: dastore
      // 3253: dup
      // 3254: sipush 143
      // 3257: ldc2_w 2.510812179067931E-272
      // 325a: dastore
      // 325b: dup
      // 325c: sipush 144
      // 325f: ldc2_w 3.953806005373127E-272
      // 3262: dastore
      // 3263: dup
      // 3264: sipush 145
      // 3267: ldc2_w 7.112596406315374E-272
      // 326a: dastore
      // 326b: dup
      // 326c: sipush 146
      // 326f: ldc2_w -2.850217520533226E-270
      // 3272: dastore
      // 3273: dup
      // 3274: sipush 147
      // 3277: ldc2_w -8.571477929711754E-270
      // 327a: dastore
      // 327b: dup
      // 327c: sipush 148
      // 327f: ldc2_w 1.2902019831221148E-269
      // 3282: dastore
      // 3283: dup
      // 3284: sipush 149
      // 3287: ldc2_w -6.978783784755863E-270
      // 328a: dastore
      // 328b: dup
      // 328c: sipush 150
      // 328f: ldc2_w 9.89845486618531E-269
      // 3292: dastore
      // 3293: dup
      // 3294: sipush 151
      // 3297: ldc2_w -3.538563171970534E-268
      // 329a: dastore
      // 329b: dup
      // 329c: sipush 152
      // 329f: ldc2_w 3.537475449241181E-268
      // 32a2: dastore
      // 32a3: dup
      // 32a4: sipush 153
      // 32a7: ldc2_w 3.6924578046381256E-267
      // 32aa: dastore
      // 32ab: dup
      // 32ac: sipush 154
      // 32af: ldc2_w 1.3555502536444713E-266
      // 32b2: dastore
      // 32b3: dup
      // 32b4: sipush 155
      // 32b7: ldc2_w -1.1279742372661484E-266
      // 32ba: dastore
      // 32bb: dup
      // 32bc: sipush 156
      // 32bf: ldc2_w 5.475072932318336E-266
      // 32c2: dastore
      // 32c3: dup
      // 32c4: sipush 157
      // 32c7: ldc2_w -1.1679889049814275E-265
      // 32ca: dastore
      // 32cb: dup
      // 32cc: sipush 158
      // 32cf: ldc2_w -8.946297908979776E-266
      // 32d2: dastore
      // 32d3: dup
      // 32d4: sipush 159
      // 32d7: ldc2_w 1.0565816011650582E-264
      // 32da: dastore
      // 32db: dup
      // 32dc: sipush 160
      // 32df: ldc2_w -3.2161237736296753E-265
      // 32e2: dastore
      // 32e3: dup
      // 32e4: sipush 161
      // 32e7: ldc2_w -6.022045553485609E-264
      // 32ea: dastore
      // 32eb: dup
      // 32ec: sipush 162
      // 32ef: ldc2_w -2.0332050860436034E-263
      // 32f2: dastore
      // 32f3: dup
      // 32f4: sipush 163
      // 32f7: ldc2_w -1.0488538406930105E-262
      // 32fa: dastore
      // 32fb: dup
      // 32fc: sipush 164
      // 32ff: ldc2_w 1.6793752843984384E-262
      // 3302: dastore
      // 3303: dup
      // 3304: sipush 165
      // 3307: ldc2_w 3.2558720916543104E-263
      // 330a: dastore
      // 330b: dup
      // 330c: sipush 166
      // 330f: ldc2_w -1.9546569053899882E-262
      // 3312: dastore
      // 3313: dup
      // 3314: sipush 167
      // 3317: ldc2_w 5.082190670014963E-262
      // 331a: dastore
      // 331b: dup
      // 331c: sipush 168
      // 331f: ldc2_w -1.0188117475357564E-260
      // 3322: dastore
      // 3323: dup
      // 3324: sipush 169
      // 3327: ldc2_w 3.7920054509691455E-261
      // 332a: dastore
      // 332b: dup
      // 332c: sipush 170
      // 332f: ldc2_w -8.330969967504819E-260
      // 3332: dastore
      // 3333: dup
      // 3334: sipush 171
      // 3337: ldc2_w -1.1623181434592597E-259
      // 333a: dastore
      // 333b: dup
      // 333c: sipush 172
      // 333f: ldc2_w 9.09665088462258E-259
      // 3342: dastore
      // 3343: dup
      // 3344: sipush 173
      // 3347: ldc2_w -1.56400149127482E-259
      // 334a: dastore
      // 334b: dup
      // 334c: sipush 174
      // 334f: ldc2_w -7.796557225750673E-258
      // 3352: dastore
      // 3353: dup
      // 3354: sipush 175
      // 3357: ldc2_w 6.751460509863465E-258
      // 335a: dastore
      // 335b: dup
      // 335c: sipush 176
      // 335f: ldc2_w 7.243157658226935E-258
      // 3362: dastore
      // 3363: dup
      // 3364: sipush 177
      // 3367: ldc2_w 1.2574668958946027E-256
      // 336a: dastore
      // 336b: dup
      // 336c: sipush 178
      // 336f: ldc2_w 2.2678858131411216E-256
      // 3372: dastore
      // 3373: dup
      // 3374: sipush 179
      // 3377: ldc2_w 5.1079306249351287E-256
      // 337a: dastore
      // 337b: dup
      // 337c: sipush 180
      // 337f: ldc2_w -5.672261759108003E-257
      // 3382: dastore
      // 3383: dup
      // 3384: sipush 181
      // 3387: ldc2_w 3.476539491009769E-256
      // 338a: dastore
      // 338b: dup
      // 338c: sipush 182
      // 338f: ldc2_w -1.3481093992496937E-254
      // 3392: dastore
      // 3393: dup
      // 3394: sipush 183
      // 3397: ldc2_w -3.314051560952014E-254
      // 339a: dastore
      // 339b: dup
      // 339c: sipush 184
      // 339f: ldc2_w 7.408112967339146E-255
      // 33a2: dastore
      // 33a3: dup
      // 33a4: sipush 185
      // 33a7: ldc2_w -7.164884605413269E-254
      // 33aa: dastore
      // 33ab: dup
      // 33ac: sipush 186
      // 33af: ldc2_w -6.456588023278983E-253
      // 33b2: dastore
      // 33b3: dup
      // 33b4: sipush 187
      // 33b7: ldc2_w -1.4881197370811587E-252
      // 33ba: dastore
      // 33bb: dup
      // 33bc: sipush 188
      // 33bf: ldc2_w 1.7534012237555307E-252
      // 33c2: dastore
      // 33c3: dup
      // 33c4: sipush 189
      // 33c7: ldc2_w -1.3070101381473173E-251
      // 33ca: dastore
      // 33cb: dup
      // 33cc: sipush 190
      // 33cf: ldc2_w 6.081420141954215E-251
      // 33d2: dastore
      // 33d3: dup
      // 33d4: sipush 191
      // 33d7: ldc2_w 6.591143677421159E-251
      // 33da: dastore
      // 33db: dup
      // 33dc: sipush 192
      // 33df: ldc2_w 2.6917461073773043E-250
      // 33e2: dastore
      // 33e3: dup
      // 33e4: sipush 193
      // 33e7: ldc2_w 3.683043641790553E-251
      // 33ea: dastore
      // 33eb: dup
      // 33ec: sipush 194
      // 33ef: ldc2_w 1.2195076420741757E-249
      // 33f2: dastore
      // 33f3: dup
      // 33f4: sipush 195
      // 33f7: ldc2_w -8.220283439582378E-249
      // 33fa: dastore
      // 33fb: dup
      // 33fc: sipush 196
      // 33ff: ldc2_w 1.637852737426943E-248
      // 3402: dastore
      // 3403: dup
      // 3404: sipush 197
      // 3407: ldc2_w -8.332543237340988E-249
      // 340a: dastore
      // 340b: dup
      // 340c: sipush 198
      // 340f: ldc2_w 2.9581193516975647E-248
      // 3412: dastore
      // 3413: dup
      // 3414: sipush 199
      // 3417: ldc2_w -1.7790661150204172E-247
      // 341a: dastore
      // 341b: dup
      // 341c: sipush 200
      // 341f: ldc2_w -1.7809679916043692E-247
      // 3422: dastore
      // 3423: dup
      // 3424: sipush 201
      // 3427: ldc2_w 8.378574405736031E-247
      // 342a: dastore
      // 342b: dup
      // 342c: sipush 202
      // 342f: ldc2_w -2.883847036065813E-246
      // 3432: dastore
      // 3433: dup
      // 3434: sipush 203
      // 3437: ldc2_w 1.3223776943337897E-245
      // 343a: dastore
      // 343b: dup
      // 343c: sipush 204
      // 343f: ldc2_w 3.098547586845664E-245
      // 3442: dastore
      // 3443: dup
      // 3444: sipush 205
      // 3447: ldc2_w -1.1036542789147287E-244
      // 344a: dastore
      // 344b: dup
      // 344c: sipush 206
      // 344f: ldc2_w -5.7187703271582225E-244
      // 3452: dastore
      // 3453: dup
      // 3454: sipush 207
      // 3457: ldc2_w -1.8058492822440396E-244
      // 345a: dastore
      // 345b: dup
      // 345c: sipush 208
      // 345f: ldc2_w 4.4373726292703545E-243
      // 3462: dastore
      // 3463: dup
      // 3464: sipush 209
      // 3467: ldc2_w -3.4631935816990754E-243
      // 346a: dastore
      // 346b: dup
      // 346c: sipush 210
      // 346f: ldc2_w -1.82770041073856E-243
      // 3472: dastore
      // 3473: dup
      // 3474: sipush 211
      // 3477: ldc2_w 3.845535085273936E-242
      // 347a: dastore
      // 347b: dup
      // 347c: sipush 212
      // 347f: ldc2_w 8.446532344375812E-242
      // 3482: dastore
      // 3483: dup
      // 3484: sipush 213
      // 3487: ldc2_w 2.7751016140238277E-242
      // 348a: dastore
      // 348b: dup
      // 348c: sipush 214
      // 348f: ldc2_w 1.3158882241538003E-241
      // 3492: dastore
      // 3493: dup
      // 3494: sipush 215
      // 3497: ldc2_w -3.579433051074272E-240
      // 349a: dastore
      // 349b: dup
      // 349c: sipush 216
      // 349f: ldc2_w -6.151751570213211E-240
      // 34a2: dastore
      // 34a3: dup
      // 34a4: sipush 217
      // 34a7: ldc2_w -2.990535475079021E-239
      // 34aa: dastore
      // 34ab: dup
      // 34ac: sipush 218
      // 34af: ldc2_w 2.3396028616528764E-239
      // 34b2: dastore
      // 34b3: dup
      // 34b4: sipush 219
      // 34b7: ldc2_w 7.233790684263346E-239
      // 34ba: dastore
      // 34bb: dup
      // 34bc: sipush 220
      // 34bf: ldc2_w 1.0847913100494912E-238
      // 34c2: dastore
      // 34c3: dup
      // 34c4: sipush 221
      // 34c7: ldc2_w 7.103148400942551E-238
      // 34ca: dastore
      // 34cb: dup
      // 34cc: sipush 222
      // 34cf: ldc2_w 3.463600299750966E-237
      // 34d2: dastore
      // 34d3: dup
      // 34d4: sipush 223
      // 34d7: ldc2_w -4.873121855093712E-237
      // 34da: dastore
      // 34db: dup
      // 34dc: sipush 224
      // 34df: ldc2_w 1.3407295326570417E-236
      // 34e2: dastore
      // 34e3: dup
      // 34e4: sipush 225
      // 34e7: ldc2_w 9.390271617387205E-237
      // 34ea: dastore
      // 34eb: dup
      // 34ec: sipush 226
      // 34ef: ldc2_w -2.4767709454727603E-235
      // 34f2: dastore
      // 34f3: dup
      // 34f4: sipush 227
      // 34f7: ldc2_w 3.205923535388443E-235
      // 34fa: dastore
      // 34fb: dup
      // 34fc: sipush 228
      // 34ff: ldc2_w -1.0074984709952582E-234
      // 3502: dastore
      // 3503: dup
      // 3504: sipush 229
      // 3507: ldc2_w 2.4747880175747574E-234
      // 350a: dastore
      // 350b: dup
      // 350c: sipush 230
      // 350f: ldc2_w -5.146939682310558E-234
      // 3512: dastore
      // 3513: dup
      // 3514: sipush 231
      // 3517: ldc2_w -2.827581009333298E-233
      // 351a: dastore
      // 351b: dup
      // 351c: sipush 232
      // 351f: ldc2_w -3.0307641004671077E-233
      // 3522: dastore
      // 3523: dup
      // 3524: sipush 233
      // 3527: ldc2_w 5.92044714050651E-233
      // 352a: dastore
      // 352b: dup
      // 352c: sipush 234
      // 352f: ldc2_w -2.0582596893119236E-232
      // 3532: dastore
      // 3533: dup
      // 3534: sipush 235
      // 3537: ldc2_w -6.58066591313112E-232
      // 353a: dastore
      // 353b: dup
      // 353c: sipush 236
      // 353f: ldc2_w -4.869955151949929E-231
      // 3542: dastore
      // 3543: dup
      // 3544: sipush 237
      // 3547: ldc2_w -5.763495903609913E-231
      // 354a: dastore
      // 354b: dup
      // 354c: sipush 238
      // 354f: ldc2_w -2.3580462372762525E-230
      // 3552: dastore
      // 3553: dup
      // 3554: sipush 239
      // 3557: ldc2_w 1.8559980428862584E-230
      // 355a: dastore
      // 355b: dup
      // 355c: sipush 240
      // 355f: ldc2_w 2.854978560542175E-229
      // 3562: dastore
      // 3563: dup
      // 3564: sipush 241
      // 3567: ldc2_w 5.637945686485334E-229
      // 356a: dastore
      // 356b: dup
      // 356c: sipush 242
      // 356f: ldc2_w 2.1454644909004582E-228
      // 3572: dastore
      // 3573: dup
      // 3574: sipush 243
      // 3577: ldc2_w -1.1918070206953359E-228
      // 357a: dastore
      // 357b: dup
      // 357c: sipush 244
      // 357f: ldc2_w -5.021851606912854E-228
      // 3582: dastore
      // 3583: dup
      // 3584: sipush 245
      // 3587: ldc2_w 3.861525553653117E-227
      // 358a: dastore
      // 358b: dup
      // 358c: sipush 246
      // 358f: ldc2_w 6.533561982617909E-227
      // 3592: dastore
      // 3593: dup
      // 3594: sipush 247
      // 3597: ldc2_w -3.015709444206057E-226
      // 359a: dastore
      // 359b: dup
      // 359c: sipush 248
      // 359f: ldc2_w -5.042005018212734E-227
      // 35a2: dastore
      // 35a3: dup
      // 35a4: sipush 249
      // 35a7: ldc2_w 1.5959614205422845E-225
      // 35aa: dastore
      // 35ab: dup
      // 35ac: sipush 250
      // 35af: ldc2_w 2.0402105689098835E-224
      // 35b2: dastore
      // 35b3: dup
      // 35b4: sipush 251
      // 35b7: ldc2_w 5.164902728917601E-224
      // 35ba: dastore
      // 35bb: dup
      // 35bc: sipush 252
      // 35bf: ldc2_w 9.981031744879876E-224
      // 35c2: dastore
      // 35c3: dup
      // 35c4: sipush 253
      // 35c7: ldc2_w 4.0281104210095145E-223
      // 35ca: dastore
      // 35cb: dup
      // 35cc: sipush 254
      // 35cf: ldc2_w 1.1158160971176672E-222
      // 35d2: dastore
      // 35d3: dup
      // 35d4: sipush 255
      // 35d7: ldc2_w 2.0736172194624895E-222
      // 35da: dastore
      // 35db: dup
      // 35dc: sipush 256
      // 35df: ldc2_w 4.983162653734032E-222
      // 35e2: dastore
      // 35e3: dup
      // 35e4: sipush 257
      // 35e7: ldc2_w 2.1753390051977871E-221
      // 35ea: dastore
      // 35eb: dup
      // 35ec: sipush 258
      // 35ef: ldc2_w 3.969413618002761E-221
      // 35f2: dastore
      // 35f3: dup
      // 35f4: sipush 259
      // 35f7: ldc2_w 1.3961255018698695E-220
      // 35fa: dastore
      // 35fb: dup
      // 35fc: sipush 260
      // 35ff: ldc2_w 2.1290855095314206E-220
      // 3602: dastore
      // 3603: dup
      // 3604: sipush 261
      // 3607: ldc2_w 1.1927747883417406E-219
      // 360a: dastore
      // 360b: dup
      // 360c: sipush 262
      // 360f: ldc2_w 3.7264401117998796E-219
      // 3612: dastore
      // 3613: dup
      // 3614: sipush 263
      // 3617: ldc2_w 9.318532410862293E-219
      // 361a: dastore
      // 361b: dup
      // 361c: sipush 264
      // 361f: ldc2_w 2.3414841777613345E-218
      // 3622: dastore
      // 3623: dup
      // 3624: sipush 265
      // 3627: ldc2_w 4.3791842770430786E-218
      // 362a: dastore
      // 362b: dup
      // 362c: sipush 266
      // 362f: ldc2_w 1.7173159016511951E-217
      // 3632: dastore
      // 3633: dup
      // 3634: sipush 267
      // 3637: ldc2_w 3.5037536832675478E-217
      // 363a: dastore
      // 363b: dup
      // 363c: sipush 268
      // 363f: ldc2_w 1.4300098613455884E-216
      // 3642: dastore
      // 3643: dup
      // 3644: sipush 269
      // 3647: ldc2_w 2.4189403362149483E-216
      // 364a: dastore
      // 364b: dup
      // 364c: sipush 270
      // 364f: ldc2_w 9.306541421999056E-216
      // 3652: dastore
      // 3653: dup
      // 3654: sipush 271
      // 3657: ldc2_w 3.442100456607687E-215
      // 365a: dastore
      // 365b: dup
      // 365c: sipush 272
      // 365f: ldc2_w 5.94407068841904E-215
      // 3662: dastore
      // 3663: dup
      // 3664: sipush 273
      // 3667: ldc2_w 2.0483260435783403E-214
      // 366a: dastore
      // 366b: dup
      // 366c: sipush 274
      // 366f: ldc2_w 3.8410992889527954E-214
      // 3672: dastore
      // 3673: dup
      // 3674: sipush 275
      // 3677: ldc2_w 1.2038281262953917E-213
      // 367a: dastore
      // 367b: dup
      // 367c: sipush 276
      // 367f: ldc2_w 3.865007795216205E-213
      // 3682: dastore
      // 3683: dup
      // 3684: sipush 277
      // 3687: ldc2_w 9.754659138599756E-213
      // 368a: dastore
      // 368b: dup
      // 368c: sipush 278
      // 368f: ldc2_w 2.7653605770745684E-212
      // 3692: dastore
      // 3693: dup
      // 3694: sipush 279
      // 3697: ldc2_w 5.359568079675375E-212
      // 369a: dastore
      // 369b: dup
      // 369c: sipush 280
      // 369f: ldc2_w 2.61726605666378E-211
      // 36a2: dastore
      // 36a3: dup
      // 36a4: sipush 281
      // 36a7: ldc2_w 5.054202073556894E-211
      // 36aa: dastore
      // 36ab: dup
      // 36ac: sipush 282
      // 36af: ldc2_w 8.707092668016246E-211
      // 36b2: dastore
      // 36b3: dup
      // 36b4: sipush 283
      // 36b7: ldc2_w 1.4080573899148006E-210
      // 36ba: dastore
      // 36bb: dup
      // 36bc: sipush 284
      // 36bf: ldc2_w 1.288124387778789E-209
      // 36c2: dastore
      // 36c3: dup
      // 36c4: sipush 285
      // 36c7: ldc2_w 1.8639901642011898E-209
      // 36ca: dastore
      // 36cb: dup
      // 36cc: sipush 286
      // 36cf: ldc2_w 6.076014540574561E-209
      // 36d2: dastore
      // 36d3: dup
      // 36d4: sipush 287
      // 36d7: ldc2_w 1.798489141298457E-208
      // 36da: dastore
      // 36db: dup
      // 36dc: sipush 288
      // 36df: ldc2_w 2.1525406805994896E-208
      // 36e2: dastore
      // 36e3: dup
      // 36e4: sipush 289
      // 36e7: ldc2_w 1.1864056832305874E-207
      // 36ea: dastore
      // 36eb: dup
      // 36ec: sipush 290
      // 36ef: ldc2_w 2.1077440662171152E-207
      // 36f2: dastore
      // 36f3: dup
      // 36f4: sipush 291
      // 36f7: ldc2_w 1.3784853708457332E-206
      // 36fa: dastore
      // 36fb: dup
      // 36fc: sipush 292
      // 36ff: ldc2_w 1.6965806532093783E-206
      // 3702: dastore
      // 3703: dup
      // 3704: sipush 293
      // 3707: ldc2_w 7.241626420445137E-206
      // 370a: dastore
      // 370b: dup
      // 370c: sipush 294
      // 370f: ldc2_w 2.575584299085016E-205
      // 3712: dastore
      // 3713: dup
      // 3714: sipush 295
      // 3717: ldc2_w 6.151951078101721E-205
      // 371a: dastore
      // 371b: dup
      // 371c: sipush 296
      // 371f: ldc2_w 2.40652042118887E-204
      // 3722: dastore
      // 3723: dup
      // 3724: sipush 297
      // 3727: ldc2_w 4.022633486003565E-204
      // 372a: dastore
      // 372b: dup
      // 372c: sipush 298
      // 372f: ldc2_w 5.8840879519086286E-204
      // 3732: dastore
      // 3733: dup
      // 3734: sipush 299
      // 3737: ldc2_w 3.2820308007277566E-203
      // 373a: dastore
      // 373b: dup
      // 373c: sipush 300
      // 373f: ldc2_w 4.31880454864738E-203
      // 3742: dastore
      // 3743: dup
      // 3744: sipush 301
      // 3747: ldc2_w 2.427240455243201E-202
      // 374a: dastore
      // 374b: dup
      // 374c: sipush 302
      // 374f: ldc2_w 7.326955749884755E-202
      // 3752: dastore
      // 3753: dup
      // 3754: sipush 303
      // 3757: ldc2_w 1.4310184489676175E-201
      // 375a: dastore
      // 375b: dup
      // 375c: sipush 304
      // 375f: ldc2_w 4.464279133463661E-201
      // 3762: dastore
      // 3763: dup
      // 3764: sipush 305
      // 3767: ldc2_w 4.895131474682867E-201
      // 376a: dastore
      // 376b: dup
      // 376c: sipush 306
      // 376f: ldc2_w 4.48614966943544E-200
      // 3772: dastore
      // 3773: dup
      // 3774: sipush 307
      // 3777: ldc2_w 8.924048768324976E-200
      // 377a: dastore
      // 377b: dup
      // 377c: sipush 308
      // 377f: ldc2_w 2.5035535029701945E-199
      // 3782: dastore
      // 3783: dup
      // 3784: sipush 309
      // 3787: ldc2_w 6.627829836338812E-199
      // 378a: dastore
      // 378b: dup
      // 378c: sipush 310
      // 378f: ldc2_w 2.6066826304502746E-198
      // 3792: dastore
      // 3793: dup
      // 3794: sipush 311
      // 3797: ldc2_w 8.042275310036546E-198
      // 379a: dastore
      // 379b: dup
      // 379c: sipush 312
      // 379f: ldc2_w 2.115062964308555E-197
      // 37a2: dastore
      // 37a3: dup
      // 37a4: sipush 313
      // 37a7: ldc2_w 4.413745413236018E-197
      // 37aa: dastore
      // 37ab: dup
      // 37ac: sipush 314
      // 37af: ldc2_w 1.644449394585716E-196
      // 37b2: dastore
      // 37b3: dup
      // 37b4: sipush 315
      // 37b7: ldc2_w 3.138217752973845E-196
      // 37ba: dastore
      // 37bb: dup
      // 37bc: sipush 316
      // 37bf: ldc2_w 7.48533983136081E-196
      // 37c2: dastore
      // 37c3: dup
      // 37c4: sipush 317
      // 37c7: ldc2_w 2.613626422028823E-195
      // 37ca: dastore
      // 37cb: dup
      // 37cc: sipush 318
      // 37cf: ldc2_w 3.6741841454219095E-195
      // 37d2: dastore
      // 37d3: dup
      // 37d4: sipush 319
      // 37d7: ldc2_w 5.906102862953403E-195
      // 37da: dastore
      // 37db: dup
      // 37dc: sipush 320
      // 37df: ldc2_w 4.4940857547850743E-194
      // 37e2: dastore
      // 37e3: dup
      // 37e4: sipush 321
      // 37e7: ldc2_w 5.840064709376958E-194
      // 37ea: dastore
      // 37eb: dup
      // 37ec: sipush 322
      // 37ef: ldc2_w 3.087661273836024E-193
      // 37f2: dastore
      // 37f3: dup
      // 37f4: sipush 323
      // 37f7: ldc2_w 4.995552216100365E-193
      // 37fa: dastore
      // 37fb: dup
      // 37fc: sipush 324
      // 37ff: ldc2_w 1.991444798915497E-192
      // 3802: dastore
      // 3803: dup
      // 3804: sipush 325
      // 3807: ldc2_w 7.097454751809522E-192
      // 380a: dastore
      // 380b: dup
      // 380c: sipush 326
      // 380f: ldc2_w 2.0510193986749737E-191
      // 3812: dastore
      // 3813: dup
      // 3814: sipush 327
      // 3817: ldc2_w 5.759440286608551E-191
      // 381a: dastore
      // 381b: dup
      // 381c: sipush 328
      // 381f: ldc2_w 1.7013941257113314E-190
      // 3822: dastore
      // 3823: dup
      // 3824: sipush 329
      // 3827: ldc2_w 2.1383323934483528E-190
      // 382a: dastore
      // 382b: dup
      // 382c: sipush 330
      // 382f: ldc2_w 8.280292810015406E-190
      // 3832: dastore
      // 3833: dup
      // 3834: sipush 331
      // 3837: ldc2_w 3.138655772049104E-189
      // 383a: dastore
      // 383b: dup
      // 383c: sipush 332
      // 383f: ldc2_w 7.961506427685701E-189
      // 3842: dastore
      // 3843: dup
      // 3844: sipush 333
      // 3847: ldc2_w 2.0579001228504997E-188
      // 384a: dastore
      // 384b: dup
      // 384c: sipush 334
      // 384f: ldc2_w 7.530840351477639E-188
      // 3852: dastore
      // 3853: dup
      // 3854: sipush 335
      // 3857: ldc2_w 1.4582863136475673E-187
      // 385a: dastore
      // 385b: dup
      // 385c: sipush 336
      // 385f: ldc2_w 3.149267215638608E-187
      // 3862: dastore
      // 3863: dup
      // 3864: sipush 337
      // 3867: ldc2_w 5.443114553057336E-187
      // 386a: dastore
      // 386b: dup
      // 386c: sipush 338
      // 386f: ldc2_w 3.4672966834277804E-186
      // 3872: dastore
      // 3873: dup
      // 3874: sipush 339
      // 3877: ldc2_w 7.374944406615125E-186
      // 387a: dastore
      // 387b: dup
      // 387c: sipush 340
      // 387f: ldc2_w 2.7318417252599104E-185
      // 3882: dastore
      // 3883: dup
      // 3884: sipush 341
      // 3887: ldc2_w 7.913674211949961E-185
      // 388a: dastore
      // 388b: dup
      // 388c: sipush 342
      // 388f: ldc2_w 2.5217716516462005E-184
      // 3892: dastore
      // 3893: dup
      // 3894: sipush 343
      // 3897: ldc2_w 4.0866585874353075E-184
      // 389a: dastore
      // 389b: dup
      // 389c: sipush 344
      // 389f: ldc2_w 1.2087698972768686E-183
      // 38a2: dastore
      // 38a3: dup
      // 38a4: sipush 345
      // 38a7: ldc2_w 3.7072473866919033E-183
      // 38aa: dastore
      // 38ab: dup
      // 38ac: sipush 346
      // 38af: ldc2_w 1.1333588840402273E-182
      // 38b2: dastore
      // 38b3: dup
      // 38b4: sipush 347
      // 38b7: ldc2_w 1.61949812578045E-182
      // 38ba: dastore
      // 38bb: dup
      // 38bc: sipush 348
      // 38bf: ldc2_w 6.567779607147072E-182
      // 38c2: dastore
      // 38c3: dup
      // 38c4: sipush 349
      // 38c7: ldc2_w 2.422974840736314E-181
      // 38ca: dastore
      // 38cb: dup
      // 38cc: sipush 350
      // 38cf: ldc2_w 2.551170809294396E-181
      // 38d2: dastore
      // 38d3: dup
      // 38d4: sipush 351
      // 38d7: ldc2_w 1.0905890688083124E-180
      // 38da: dastore
      // 38db: dup
      // 38dc: sipush 352
      // 38df: ldc2_w 3.221279639653057E-180
      // 38e2: dastore
      // 38e3: dup
      // 38e4: sipush 353
      // 38e7: ldc2_w 7.068244813489027E-180
      // 38ea: dastore
      // 38eb: dup
      // 38ec: sipush 354
      // 38ef: ldc2_w 1.3752309224575428E-179
      // 38f2: dastore
      // 38f3: dup
      // 38f4: sipush 355
      // 38f7: ldc2_w 7.20154303462761E-179
      // 38fa: dastore
      // 38fb: dup
      // 38fc: sipush 356
      // 38ff: ldc2_w 1.5391707185581056E-178
      // 3902: dastore
      // 3903: dup
      // 3904: sipush 357
      // 3907: ldc2_w 7.708777608683431E-178
      // 390a: dastore
      // 390b: dup
      // 390c: sipush 358
      // 390f: ldc2_w 5.597398155472547E-178
      // 3912: dastore
      // 3913: dup
      // 3914: sipush 359
      // 3917: ldc2_w 1.8487854656676722E-177
      // 391a: dastore
      // 391b: dup
      // 391c: sipush 360
      // 391f: ldc2_w 1.0577249492414076E-176
      // 3922: dastore
      // 3923: dup
      // 3924: sipush 361
      // 3927: ldc2_w 2.8926683313922764E-176
      // 392a: dastore
      // 392b: dup
      // 392c: sipush 362
      // 392f: ldc2_w 4.090184282164232E-176
      // 3932: dastore
      // 3933: dup
      // 3934: sipush 363
      // 3937: ldc2_w 1.6142943398013813E-175
      // 393a: dastore
      // 393b: dup
      // 393c: sipush 364
      // 393f: ldc2_w 7.873864351702525E-175
      // 3942: dastore
      // 3943: dup
      // 3944: sipush 365
      // 3947: ldc2_w 2.242630017261011E-174
      // 394a: dastore
      // 394b: dup
      // 394c: sipush 366
      // 394f: ldc2_w 3.4637009373878283E-174
      // 3952: dastore
      // 3953: dup
      // 3954: sipush 367
      // 3957: ldc2_w 1.5907089565090164E-173
      // 395a: dastore
      // 395b: dup
      // 395c: sipush 368
      // 395f: ldc2_w 1.6985075903314236E-173
      // 3962: dastore
      // 3963: dup
      // 3964: sipush 369
      // 3967: ldc2_w 1.1552273904608563E-172
      // 396a: dastore
      // 396b: dup
      // 396c: sipush 370
      // 396f: ldc2_w 2.237894048535414E-172
      // 3972: dastore
      // 3973: dup
      // 3974: sipush 371
      // 3977: ldc2_w 5.321990399912051E-172
      // 397a: dastore
      // 397b: dup
      // 397c: sipush 372
      // 397f: ldc2_w 1.4106062639738257E-171
      // 3982: dastore
      // 3983: dup
      // 3984: sipush 373
      // 3987: ldc2_w 2.9850404523368767E-171
      // 398a: dastore
      // 398b: dup
      // 398c: sipush 374
      // 398f: ldc2_w 1.5683802588004895E-170
      // 3992: dastore
      // 3993: dup
      // 3994: sipush 375
      // 3997: ldc2_w 4.880146806045633E-170
      // 399a: dastore
      // 399b: dup
      // 399c: sipush 376
      // 399f: ldc2_w 1.1489352403441815E-169
      // 39a2: dastore
      // 39a3: dup
      // 39a4: sipush 377
      // 39a7: ldc2_w 1.6401586605693734E-169
      // 39aa: dastore
      // 39ab: dup
      // 39ac: sipush 378
      // 39af: ldc2_w 8.29169700697816E-169
      // 39b2: dastore
      // 39b3: dup
      // 39b4: sipush 379
      // 39b7: ldc2_w 1.0380723705441457E-168
      // 39ba: dastore
      // 39bb: dup
      // 39bc: sipush 380
      // 39bf: ldc2_w 7.126414081261746E-168
      // 39c2: dastore
      // 39c3: dup
      // 39c4: sipush 381
      // 39c7: ldc2_w 1.253325949455206E-167
      // 39ca: dastore
      // 39cb: dup
      // 39cc: sipush 382
      // 39cf: ldc2_w 2.595079206183114E-167
      // 39d2: dastore
      // 39d3: dup
      // 39d4: sipush 383
      // 39d7: ldc2_w 1.537490712803659E-166
      // 39da: dastore
      // 39db: dup
      // 39dc: sipush 384
      // 39df: ldc2_w 2.6338455225993276E-166
      // 39e2: dastore
      // 39e3: dup
      // 39e4: sipush 385
      // 39e7: ldc2_w 7.994936425058567E-166
      // 39ea: dastore
      // 39eb: dup
      // 39ec: sipush 386
      // 39ef: ldc2_w 1.5716634677516025E-165
      // 39f2: dastore
      // 39f3: dup
      // 39f4: sipush 387
      // 39f7: ldc2_w 3.669404761339415E-165
      // 39fa: dastore
      // 39fb: dup
      // 39fc: sipush 388
      // 39ff: ldc2_w 1.9941628263579332E-164
      // 3a02: dastore
      // 3a03: dup
      // 3a04: sipush 389
      // 3a07: ldc2_w 4.5012079983352374E-164
      // 3a0a: dastore
      // 3a0b: dup
      // 3a0c: sipush 390
      // 3a0f: ldc2_w 7.283163019991001E-164
      // 3a12: dastore
      // 3a13: dup
      // 3a14: sipush 391
      // 3a17: ldc2_w 2.398038505188316E-163
      // 3a1a: dastore
      // 3a1b: dup
      // 3a1c: sipush 392
      // 3a1f: ldc2_w 7.868666894503849E-163
      // 3a22: dastore
      // 3a23: dup
      // 3a24: sipush 393
      // 3a27: ldc2_w 2.1478649410390003E-162
      // 3a2a: dastore
      // 3a2b: dup
      // 3a2c: sipush 394
      // 3a2f: ldc2_w 8.306111510463843E-162
      // 3a32: dastore
      // 3a33: dup
      // 3a34: sipush 395
      // 3a37: ldc2_w 1.5453160659068463E-161
      // 3a3a: dastore
      // 3a3b: dup
      // 3a3c: sipush 396
      // 3a3f: ldc2_w -4.590496588813841E-162
      // 3a42: dastore
      // 3a43: dup
      // 3a44: sipush 397
      // 3a47: ldc2_w 3.5449293983801232E-161
      // 3a4a: dastore
      // 3a4b: dup
      // 3a4c: sipush 398
      // 3a4f: ldc2_w -1.0440854056870505E-160
      // 3a52: dastore
      // 3a53: dup
      // 3a54: sipush 399
      // 3a57: ldc2_w -2.321064927632431E-160
      // 3a5a: dastore
      // 3a5b: dup
      // 3a5c: sipush 400
      // 3a5f: ldc2_w 5.707867001443433E-160
      // 3a62: dastore
      // 3a63: dup
      // 3a64: sipush 401
      // 3a67: ldc2_w -2.238614484037969E-159
      // 3a6a: dastore
      // 3a6b: dup
      // 3a6c: sipush 402
      // 3a6f: ldc2_w 2.482282821883242E-159
      // 3a72: dastore
      // 3a73: dup
      // 3a74: sipush 403
      // 3a77: ldc2_w -1.1508772192025259E-158
      // 3a7a: dastore
      // 3a7b: dup
      // 3a7c: sipush 404
      // 3a7f: ldc2_w 1.9903990578876104E-158
      // 3a82: dastore
      // 3a83: dup
      // 3a84: sipush 405
      // 3a87: ldc2_w -1.2116165315442256E-158
      // 3a8a: dastore
      // 3a8b: dup
      // 3a8c: sipush 406
      // 3a8f: ldc2_w -2.9084557554502667E-157
      // 3a92: dastore
      // 3a93: dup
      // 3a94: sipush 407
      // 3a97: ldc2_w -1.1211083853006645E-156
      // 3a9a: dastore
      // 3a9b: dup
      // 3a9c: sipush 408
      // 3a9f: ldc2_w -1.309893394818129E-156
      // 3aa2: dastore
      // 3aa3: dup
      // 3aa4: sipush 409
      // 3aa7: ldc2_w 4.2269712317468864E-156
      // 3aaa: dastore
      // 3aab: dup
      // 3aac: sipush 410
      // 3aaf: ldc2_w -7.678973146281339E-156
      // 3ab2: dastore
      // 3ab3: dup
      // 3ab4: sipush 411
      // 3ab7: ldc2_w 3.2021376921211934E-155
      // 3aba: dastore
      // 3abb: dup
      // 3abc: sipush 412
      // 3abf: ldc2_w -7.08313012515209E-155
      // 3ac2: dastore
      // 3ac3: dup
      // 3ac4: sipush 413
      // 3ac7: ldc2_w 1.944398214330544E-154
      // 3aca: dastore
      // 3acb: dup
      // 3acc: sipush 414
      // 3acf: ldc2_w 1.1860061363751161E-153
      // 3ad2: dastore
      // 3ad3: dup
      // 3ad4: sipush 415
      // 3ad7: ldc2_w 1.5234465914578058E-153
      // 3ada: dastore
      // 3adb: dup
      // 3adc: sipush 416
      // 3adf: ldc2_w -2.9020908354550263E-153
      // 3ae2: dastore
      // 3ae3: dup
      // 3ae4: sipush 417
      // 3ae7: ldc2_w 4.980100072851796E-153
      // 3aea: dastore
      // 3aeb: dup
      // 3aec: sipush 418
      // 3aef: ldc2_w 2.3101551448625578E-152
      // 3af2: dastore
      // 3af3: dup
      // 3af4: sipush 419
      // 3af7: ldc2_w -1.1959241322537072E-151
      // 3afa: dastore
      // 3afb: dup
      // 3afc: sipush 420
      // 3aff: ldc2_w -9.27398924154364E-153
      // 3b02: dastore
      // 3b03: dup
      // 3b04: sipush 421
      // 3b07: ldc2_w 5.999390491704392E-152
      // 3b0a: dastore
      // 3b0b: dup
      // 3b0c: sipush 422
      // 3b0f: ldc2_w 1.3373196561281372E-150
      // 3b12: dastore
      // 3b13: dup
      // 3b14: sipush 423
      // 3b17: ldc2_w -1.0271780540759147E-150
      // 3b1a: dastore
      // 3b1b: dup
      // 3b1c: sipush 424
      // 3b1f: ldc2_w 2.575620466387945E-150
      // 3b22: dastore
      // 3b23: dup
      // 3b24: sipush 425
      // 3b27: ldc2_w -6.56250013356227E-149
      // 3b2a: dastore
      // 3b2b: dup
      // 3b2c: sipush 426
      // 3b2f: ldc2_w -1.1961357917482867E-148
      // 3b32: dastore
      // 3b33: dup
      // 3b34: sipush 427
      // 3b37: ldc2_w 5.5807813570926636E-148
      // 3b3a: dastore
      // 3b3b: dup
      // 3b3c: sipush 428
      // 3b3f: ldc2_w 9.252840189663807E-148
      // 3b42: dastore
      // 3b43: dup
      // 3b44: sipush 429
      // 3b47: ldc2_w -1.830335419852293E-147
      // 3b4a: dastore
      // 3b4b: dup
      // 3b4c: sipush 430
      // 3b4f: ldc2_w 9.350990339947455E-147
      // 3b52: dastore
      // 3b53: dup
      // 3b54: sipush 431
      // 3b57: ldc2_w -1.6072409939877762E-146
      // 3b5a: dastore
      // 3b5b: dup
      // 3b5c: sipush 432
      // 3b5f: ldc2_w -2.5309995887229526E-146
      // 3b62: dastore
      // 3b63: dup
      // 3b64: sipush 433
      // 3b67: ldc2_w -1.6014373376410622E-146
      // 3b6a: dastore
      // 3b6b: dup
      // 3b6c: sipush 434
      // 3b6f: ldc2_w -3.303297758377758E-145
      // 3b72: dastore
      // 3b73: dup
      // 3b74: sipush 435
      // 3b77: ldc2_w 1.5640419864850202E-145
      // 3b7a: dastore
      // 3b7b: dup
      // 3b7c: sipush 436
      // 3b7f: ldc2_w 9.544642884951585E-145
      // 3b82: dastore
      // 3b83: dup
      // 3b84: sipush 437
      // 3b87: ldc2_w -8.64864445321803E-144
      // 3b8a: dastore
      // 3b8b: dup
      // 3b8c: sipush 438
      // 3b8f: ldc2_w 7.580392204597681E-144
      // 3b92: dastore
      // 3b93: dup
      // 3b94: sipush 439
      // 3b97: ldc2_w 2.678334184447568E-143
      // 3b9a: dastore
      // 3b9b: dup
      // 3b9c: sipush 440
      // 3b9f: ldc2_w -3.7269289985326055E-143
      // 3ba2: dastore
      // 3ba3: dup
      // 3ba4: sipush 441
      // 3ba7: ldc2_w -2.851965258161176E-142
      // 3baa: dastore
      // 3bab: dup
      // 3bac: sipush 442
      // 3baf: ldc2_w 7.243267286265823E-142
      // 3bb2: dastore
      // 3bb3: dup
      // 3bb4: sipush 443
      // 3bb7: ldc2_w 4.4510805312036926E-141
      // 3bba: dastore
      // 3bbb: dup
      // 3bbc: sipush 444
      // 3bbf: ldc2_w 9.008499734799015E-141
      // 3bc2: dastore
      // 3bc3: dup
      // 3bc4: sipush 445
      // 3bc7: ldc2_w 1.130435759928337E-140
      // 3bca: dastore
      // 3bcb: dup
      // 3bcc: sipush 446
      // 3bcf: ldc2_w -3.096539751496479E-140
      // 3bd2: dastore
      // 3bd3: dup
      // 3bd4: sipush 447
      // 3bd7: ldc2_w -1.497405487919762E-139
      // 3bda: dastore
      // 3bdb: dup
      // 3bdc: sipush 448
      // 3bdf: ldc2_w 3.51519845948652E-139
      // 3be2: dastore
      // 3be3: dup
      // 3be4: sipush 449
      // 3be7: ldc2_w -4.713790209541894E-139
      // 3bea: dastore
      // 3beb: dup
      // 3bec: sipush 450
      // 3bef: ldc2_w 4.740753295616865E-138
      // 3bf2: dastore
      // 3bf3: dup
      // 3bf4: sipush 451
      // 3bf7: ldc2_w 9.517570994930463E-138
      // 3bfa: dastore
      // 3bfb: dup
      // 3bfc: sipush 452
      // 3bff: ldc2_w -1.8842098029339485E-137
      // 3c02: dastore
      // 3c03: dup
      // 3c04: sipush 453
      // 3c07: ldc2_w -3.825558165008403E-137
      // 3c0a: dastore
      // 3c0b: dup
      // 3c0c: sipush 454
      // 3c0f: ldc2_w 1.1817638600528107E-136
      // 3c12: dastore
      // 3c13: dup
      // 3c14: sipush 455
      // 3c17: ldc2_w -3.514601201473235E-136
      // 3c1a: dastore
      // 3c1b: dup
      // 3c1c: sipush 456
      // 3c1f: ldc2_w -6.344612631552417E-136
      // 3c22: dastore
      // 3c23: dup
      // 3c24: sipush 457
      // 3c27: ldc2_w -1.6754164785291923E-136
      // 3c2a: dastore
      // 3c2b: dup
      // 3c2c: sipush 458
      // 3c2f: ldc2_w 4.445372986583078E-135
      // 3c32: dastore
      // 3c33: dup
      // 3c34: sipush 459
      // 3c37: ldc2_w -3.89604237755475E-134
      // 3c3a: dastore
      // 3c3b: dup
      // 3c3c: sipush 460
      // 3c3f: ldc2_w -1.0155552195374609E-134
      // 3c42: dastore
      // 3c43: dup
      // 3c44: sipush 461
      // 3c47: ldc2_w 2.1858142063550155E-134
      // 3c4a: dastore
      // 3c4b: dup
      // 3c4c: sipush 462
      // 3c4f: ldc2_w 3.497714990137842E-133
      // 3c52: dastore
      // 3c53: dup
      // 3c54: sipush 463
      // 3c57: ldc2_w -7.635830383612894E-133
      // 3c5a: dastore
      // 3c5b: dup
      // 3c5c: sipush 464
      // 3c5f: ldc2_w 1.2050744860079718E-132
      // 3c62: dastore
      // 3c63: dup
      // 3c64: sipush 465
      // 3c67: ldc2_w -7.683019590615251E-133
      // 3c6a: dastore
      // 3c6b: dup
      // 3c6c: sipush 466
      // 3c6f: ldc2_w -3.344806129021162E-131
      // 3c72: dastore
      // 3c73: dup
      // 3c74: sipush 467
      // 3c77: ldc2_w -1.6737914131474577E-131
      // 3c7a: dastore
      // 3c7b: dup
      // 3c7c: sipush 468
      // 3c7f: ldc2_w -4.30610076666344E-131
      // 3c82: dastore
      // 3c83: dup
      // 3c84: sipush 469
      // 3c87: ldc2_w 5.184023388254773E-130
      // 3c8a: dastore
      // 3c8b: dup
      // 3c8c: sipush 470
      // 3c8f: ldc2_w 2.6290763595212492E-129
      // 3c92: dastore
      // 3c93: dup
      // 3c94: sipush 471
      // 3c97: ldc2_w 7.90041744728452E-130
      // 3c9a: dastore
      // 3c9b: dup
      // 3c9c: sipush 472
      // 3c9f: ldc2_w -3.204473056113754E-129
      // 3ca2: dastore
      // 3ca3: dup
      // 3ca4: sipush 473
      // 3ca7: ldc2_w -2.552517201762272E-128
      // 3caa: dastore
      // 3cab: dup
      // 3cac: sipush 474
      // 3caf: ldc2_w 7.130134251490065E-128
      // 3cb2: dastore
      // 3cb3: dup
      // 3cb4: sipush 475
      // 3cb7: ldc2_w -3.2244113258340395E-127
      // 3cba: dastore
      // 3cbb: dup
      // 3cbc: sipush 476
      // 3cbf: ldc2_w -1.064920993515727E-127
      // 3cc2: dastore
      // 3cc3: dup
      // 3cc4: sipush 477
      // 3cc7: ldc2_w 2.7466520735457463E-126
      // 3cca: dastore
      // 3ccb: dup
      // 3ccc: sipush 478
      // 3ccf: ldc2_w 4.368312797746065E-126
      // 3cd2: dastore
      // 3cd3: dup
      // 3cd4: sipush 479
      // 3cd7: ldc2_w 1.8802599072446818E-125
      // 3cda: dastore
      // 3cdb: dup
      // 3cdc: sipush 480
      // 3cdf: ldc2_w -4.257625799463564E-125
      // 3ce2: dastore
      // 3ce3: dup
      // 3ce4: sipush 481
      // 3ce7: ldc2_w 5.491672256552995E-125
      // 3cea: dastore
      // 3ceb: dup
      // 3cec: sipush 482
      // 3cef: ldc2_w 3.7298611779671127E-124
      // 3cf2: dastore
      // 3cf3: dup
      // 3cf4: sipush 483
      // 3cf7: ldc2_w 5.724180836308973E-124
      // 3cfa: dastore
      // 3cfb: dup
      // 3cfc: sipush 484
      // 3cff: ldc2_w 1.3861841053630075E-123
      // 3d02: dastore
      // 3d03: dup
      // 3d04: sipush 485
      // 3d07: ldc2_w 4.2303826056297614E-123
      // 3d0a: dastore
      // 3d0b: dup
      // 3d0c: sipush 486
      // 3d0f: ldc2_w 3.5335436928899096E-123
      // 3d12: dastore
      // 3d13: dup
      // 3d14: sipush 487
      // 3d17: ldc2_w -2.522906629540626E-122
      // 3d1a: dastore
      // 3d1b: dup
      // 3d1c: sipush 488
      // 3d1f: ldc2_w 1.0147808005267102E-121
      // 3d22: dastore
      // 3d23: dup
      // 3d24: sipush 489
      // 3d27: ldc2_w 6.734406065735473E-122
      // 3d2a: dastore
      // 3d2b: dup
      // 3d2c: sipush 490
      // 3d2f: ldc2_w -4.948973160958133E-121
      // 3d32: dastore
      // 3d33: dup
      // 3d34: sipush 491
      // 3d37: ldc2_w 2.4256181927024344E-120
      // 3d3a: dastore
      // 3d3b: dup
      // 3d3c: sipush 492
      // 3d3f: ldc2_w 4.9056283164780554E-120
      // 3d42: dastore
      // 3d43: dup
      // 3d44: sipush 493
      // 3d47: ldc2_w 6.846440394397547E-120
      // 3d4a: dastore
      // 3d4b: dup
      // 3d4c: sipush 494
      // 3d4f: ldc2_w 3.512747689569002E-119
      // 3d52: dastore
      // 3d53: dup
      // 3d54: sipush 495
      // 3d57: ldc2_w -9.020907406701404E-119
      // 3d5a: dastore
      // 3d5b: dup
      // 3d5c: sipush 496
      // 3d5f: ldc2_w 2.5718749916003624E-118
      // 3d62: dastore
      // 3d63: dup
      // 3d64: sipush 497
      // 3d67: ldc2_w 4.3724191002977524E-119
      // 3d6a: dastore
      // 3d6b: dup
      // 3d6c: sipush 498
      // 3d6f: ldc2_w 1.001455050575191E-117
      // 3d72: dastore
      // 3d73: dup
      // 3d74: sipush 499
      // 3d77: ldc2_w -2.4442443105031435E-117
      // 3d7a: dastore
      // 3d7b: dup
      // 3d7c: sipush 500
      // 3d7f: ldc2_w 2.38873950760028E-116
      // 3d82: dastore
      // 3d83: dup
      // 3d84: sipush 501
      // 3d87: ldc2_w -4.831068747037129E-118
      // 3d8a: dastore
      // 3d8b: dup
      // 3d8c: sipush 502
      // 3d8f: ldc2_w -5.148989321866988E-116
      // 3d92: dastore
      // 3d93: dup
      // 3d94: sipush 503
      // 3d97: ldc2_w 1.7875271881514469E-115
      // 3d9a: dastore
      // 3d9b: dup
      // 3d9c: sipush 504
      // 3d9f: ldc2_w -1.1821586412088555E-114
      // 3da2: dastore
      // 3da3: dup
      // 3da4: sipush 505
      // 3da7: ldc2_w 4.43247726423679E-115
      // 3daa: dastore
      // 3dab: dup
      // 3dac: sipush 506
      // 3daf: ldc2_w 4.634817120492781E-114
      // 3db2: dastore
      // 3db3: dup
      // 3db4: sipush 507
      // 3db7: ldc2_w 1.671311907037975E-113
      // 3dba: dastore
      // 3dbb: dup
      // 3dbc: sipush 508
      // 3dbf: ldc2_w -4.595250028278979E-113
      // 3dc2: dastore
      // 3dc3: dup
      // 3dc4: sipush 509
      // 3dc7: ldc2_w -5.905511605694905E-113
      // 3dca: dastore
      // 3dcb: dup
      // 3dcc: sipush 510
      // 3dcf: ldc2_w -1.3657642265608213E-112
      // 3dd2: dastore
      // 3dd3: dup
      // 3dd4: sipush 511
      // 3dd7: ldc2_w 2.881416869529271E-112
      // 3dda: dastore
      // 3ddb: dup
      // 3ddc: sipush 512
      // 3ddf: ldc2_w 2.1253302469985373E-111
      // 3de2: dastore
      // 3de3: dup
      // 3de4: sipush 513
      // 3de7: ldc2_w -5.301386276260592E-111
      // 3dea: dastore
      // 3deb: dup
      // 3dec: sipush 514
      // 3def: ldc2_w 1.4198782892306878E-112
      // 3df2: dastore
      // 3df3: dup
      // 3df4: sipush 515
      // 3df7: ldc2_w -3.395494928605007E-110
      // 3dfa: dastore
      // 3dfb: dup
      // 3dfc: sipush 516
      // 3dff: ldc2_w 9.284633292147283E-110
      // 3e02: dastore
      // 3e03: dup
      // 3e04: sipush 517
      // 3e07: ldc2_w -6.054133004896379E-110
      // 3e0a: dastore
      // 3e0b: dup
      // 3e0c: sipush 518
      // 3e0f: ldc2_w -8.324100783330331E-109
      // 3e12: dastore
      // 3e13: dup
      // 3e14: sipush 519
      // 3e17: ldc2_w -2.193190669794277E-108
      // 3e1a: dastore
      // 3e1b: dup
      // 3e1c: sipush 520
      // 3e1f: ldc2_w 1.3613655394659198E-107
      // 3e22: dastore
      // 3e23: dup
      // 3e24: sipush 521
      // 3e27: ldc2_w 6.463452607647978E-108
      // 3e2a: dastore
      // 3e2b: dup
      // 3e2c: sipush 522
      // 3e2f: ldc2_w 1.0187183636134904E-106
      // 3e32: dastore
      // 3e33: dup
      // 3e34: sipush 523
      // 3e37: ldc2_w 1.0705673935006142E-106
      // 3e3a: dastore
      // 3e3b: dup
      // 3e3c: sipush 524
      // 3e3f: ldc2_w 2.509050608571019E-106
      // 3e42: dastore
      // 3e43: dup
      // 3e44: sipush 525
      // 3e47: ldc2_w -1.5096182622106617E-105
      // 3e4a: dastore
      // 3e4b: dup
      // 3e4c: sipush 526
      // 3e4f: ldc2_w 1.7794190449526737E-106
      // 3e52: dastore
      // 3e53: dup
      // 3e54: sipush 527
      // 3e57: ldc2_w 1.2261246749706581E-104
      // 3e5a: dastore
      // 3e5b: dup
      // 3e5c: sipush 528
      // 3e5f: ldc2_w 2.1377905661197194E-104
      // 3e62: dastore
      // 3e63: dup
      // 3e64: sipush 529
      // 3e67: ldc2_w -2.2015877944429946E-104
      // 3e6a: dastore
      // 3e6b: dup
      // 3e6c: sipush 530
      // 3e6f: ldc2_w 7.873970951802825E-104
      // 3e72: dastore
      // 3e73: dup
      // 3e74: sipush 531
      // 3e77: ldc2_w -1.7999197335480384E-103
      // 3e7a: dastore
      // 3e7b: dup
      // 3e7c: sipush 532
      // 3e7f: ldc2_w 1.0487383011058756E-105
      // 3e82: dastore
      // 3e83: dup
      // 3e84: sipush 533
      // 3e87: ldc2_w -2.9988278531841556E-102
      // 3e8a: dastore
      // 3e8b: dup
      // 3e8c: sipush 534
      // 3e8f: ldc2_w 4.7976477743232285E-102
      // 3e92: dastore
      // 3e93: dup
      // 3e94: sipush 535
      // 3e97: ldc2_w 3.452316818502442E-102
      // 3e9a: dastore
      // 3e9b: dup
      // 3e9c: sipush 536
      // 3e9f: ldc2_w 5.89953246760617E-101
      // 3ea2: dastore
      // 3ea3: dup
      // 3ea4: sipush 537
      // 3ea7: ldc2_w -4.0785601577267006E-101
      // 3eaa: dastore
      // 3eab: dup
      // 3eac: sipush 538
      // 3eaf: ldc2_w 2.7214076662438963E-100
      // 3eb2: dastore
      // 3eb3: dup
      // 3eb4: sipush 539
      // 3eb7: ldc2_w 5.237807655758373E-100
      // 3eba: dastore
      // 3ebb: dup
      // 3ebc: sipush 540
      // 3ebf: ldc2_w 6.180972117932364E-99
      // 3ec2: dastore
      // 3ec3: dup
      // 3ec4: sipush 541
      // 3ec7: ldc2_w -1.3019742873005683E-98
      // 3eca: dastore
      // 3ecb: dup
      // 3ecc: sipush 542
      // 3ecf: ldc2_w 4.501188264957416E-99
      // 3ed2: dastore
      // 3ed3: dup
      // 3ed4: sipush 543
      // 3ed7: ldc2_w -2.4075054705261798E-98
      // 3eda: dastore
      // 3edb: dup
      // 3edc: sipush 544
      // 3edf: ldc2_w 1.6503086546628772E-97
      // 3ee2: dastore
      // 3ee3: dup
      // 3ee4: sipush 545
      // 3ee7: ldc2_w -6.878666975101243E-97
      // 3eea: dastore
      // 3eeb: dup
      // 3eec: sipush 546
      // 3eef: ldc2_w 1.196718116616528E-96
      // 3ef2: dastore
      // 3ef3: dup
      // 3ef4: sipush 547
      // 3ef7: ldc2_w 2.476190162339265E-96
      // 3efa: dastore
      // 3efb: dup
      // 3efc: sipush 548
      // 3eff: ldc2_w -7.1844969234484515E-96
      // 3f02: dastore
      // 3f03: dup
      // 3f04: sipush 549
      // 3f07: ldc2_w 5.088386759261555E-95
      // 3f0a: dastore
      // 3f0b: dup
      // 3f0c: sipush 550
      // 3f0f: ldc2_w 6.749368983223726E-95
      // 3f12: dastore
      // 3f13: dup
      // 3f14: sipush 551
      // 3f17: ldc2_w 1.965737856765605E-94
      // 3f1a: dastore
      // 3f1b: dup
      // 3f1c: sipush 552
      // 3f1f: ldc2_w -5.574080023496771E-94
      // 3f22: dastore
      // 3f23: dup
      // 3f24: sipush 553
      // 3f27: ldc2_w 1.2493696959436675E-93
      // 3f2a: dastore
      // 3f2b: dup
      // 3f2c: sipush 554
      // 3f2f: ldc2_w 8.533262777516794E-94
      // 3f32: dastore
      // 3f33: dup
      // 3f34: sipush 555
      // 3f37: ldc2_w -7.225259028588793E-93
      // 3f3a: dastore
      // 3f3b: dup
      // 3f3c: sipush 556
      // 3f3f: ldc2_w -7.340587186324432E-93
      // 3f42: dastore
      // 3f43: dup
      // 3f44: sipush 557
      // 3f47: ldc2_w -3.482412195764625E-92
      // 3f4a: dastore
      // 3f4b: dup
      // 3f4c: sipush 558
      // 3f4f: ldc2_w 3.4742610108480497E-91
      // 3f52: dastore
      // 3f53: dup
      // 3f54: sipush 559
      // 3f57: ldc2_w -7.177274244758699E-91
      // 3f5a: dastore
      // 3f5b: dup
      // 3f5c: sipush 560
      // 3f5f: ldc2_w 1.2736636153072213E-90
      // 3f62: dastore
      // 3f63: dup
      // 3f64: sipush 561
      // 3f67: ldc2_w -5.730160886217076E-90
      // 3f6a: dastore
      // 3f6b: dup
      // 3f6c: sipush 562
      // 3f6f: ldc2_w -1.545495535488274E-89
      // 3f72: dastore
      // 3f73: dup
      // 3f74: sipush 563
      // 3f77: ldc2_w 1.1304179460367007E-89
      // 3f7a: dastore
      // 3f7b: dup
      // 3f7c: sipush 564
      // 3f7f: ldc2_w 1.249260560756154E-88
      // 3f82: dastore
      // 3f83: dup
      // 3f84: sipush 565
      // 3f87: ldc2_w -4.7439719382414206E-88
      // 3f8a: dastore
      // 3f8b: dup
      // 3f8c: sipush 566
      // 3f8f: ldc2_w 7.164663249266942E-88
      // 3f92: dastore
      // 3f93: dup
      // 3f94: sipush 567
      // 3f97: ldc2_w 1.7617425105337704E-87
      // 3f9a: dastore
      // 3f9b: dup
      // 3f9c: sipush 568
      // 3f9f: ldc2_w 2.4175248449172035E-87
      // 3fa2: dastore
      // 3fa3: dup
      // 3fa4: sipush 569
      // 3fa7: ldc2_w -1.043079666926483E-86
      // 3faa: dastore
      // 3fab: dup
      // 3fac: sipush 570
      // 3faf: ldc2_w -2.8137609614326677E-86
      // 3fb2: dastore
      // 3fb3: dup
      // 3fb4: sipush 571
      // 3fb7: ldc2_w -1.2091497144395591E-85
      // 3fba: dastore
      // 3fbb: dup
      // 3fbc: sipush 572
      // 3fbf: ldc2_w 3.7944631664558904E-85
      // 3fc2: dastore
      // 3fc3: dup
      // 3fc4: sipush 573
      // 3fc7: ldc2_w -2.8144926807308225E-85
      // 3fca: dastore
      // 3fcb: dup
      // 3fcc: sipush 574
      // 3fcf: ldc2_w 3.9782728352520784E-85
      // 3fd2: dastore
      // 3fd3: dup
      // 3fd4: sipush 575
      // 3fd7: ldc2_w 4.313978872469646E-84
      // 3fda: dastore
      // 3fdb: dup
      // 3fdc: sipush 576
      // 3fdf: ldc2_w 5.82190887044604E-84
      // 3fe2: dastore
      // 3fe3: dup
      // 3fe4: sipush 577
      // 3fe7: ldc2_w 5.883385169571802E-83
      // 3fea: dastore
      // 3feb: dup
      // 3fec: sipush 578
      // 3fef: ldc2_w 1.134857098306787E-82
      // 3ff2: dastore
      // 3ff3: dup
      // 3ff4: sipush 579
      // 3ff7: ldc2_w 3.468049324128309E-82
      // 3ffa: dastore
      // 3ffb: dup
      // 3ffc: sipush 580
      // 3fff: ldc2_w 2.625423995658143E-82
      // 4002: dastore
      // 4003: dup
      // 4004: sipush 581
      // 4007: ldc2_w -3.42827917465521E-81
      // 400a: dastore
      // 400b: dup
      // 400c: sipush 582
      // 400f: ldc2_w 5.119461911618321E-81
      // 4012: dastore
      // 4013: dup
      // 4014: sipush 583
      // 4017: ldc2_w -2.134387988350615E-80
      // 401a: dastore
      // 401b: dup
      // 401c: sipush 584
      // 401f: ldc2_w -4.4703076268400615E-80
      // 4022: dastore
      // 4023: dup
      // 4024: sipush 585
      // 4027: ldc2_w 4.806078883451016E-80
      // 402a: dastore
      // 402b: dup
      // 402c: sipush 586
      // 402f: ldc2_w 2.3820250362443495E-79
      // 4032: dastore
      // 4033: dup
      // 4034: sipush 587
      // 4037: ldc2_w -7.258551497833573E-79
      // 403a: dastore
      // 403b: dup
      // 403c: sipush 588
      // 403f: ldc2_w -4.0297835558876335E-78
      // 4042: dastore
      // 4043: dup
      // 4044: sipush 589
      // 4047: ldc2_w 2.1424166787650852E-78
      // 404a: dastore
      // 404b: dup
      // 404c: sipush 590
      // 404f: ldc2_w -3.2117127164185917E-77
      // 4052: dastore
      // 4053: dup
      // 4054: sipush 591
      // 4057: ldc2_w 4.8459153070935316E-77
      // 405a: dastore
      // 405b: dup
      // 405c: sipush 592
      // 405f: ldc2_w -1.766924303914492E-76
      // 4062: dastore
      // 4063: dup
      // 4064: sipush 593
      // 4067: ldc2_w -2.6921749814579492E-76
      // 406a: dastore
      // 406b: dup
      // 406c: sipush 594
      // 406f: ldc2_w -4.1291070428848755E-76
      // 4072: dastore
      // 4073: dup
      // 4074: sipush 595
      // 4077: ldc2_w 2.2086994756104319E-75
      // 407a: dastore
      // 407b: dup
      // 407c: sipush 596
      // 407f: ldc2_w -7.814146377574201E-75
      // 4082: dastore
      // 4083: dup
      // 4084: sipush 597
      // 4087: ldc2_w -1.9589778310104216E-74
      // 408a: dastore
      // 408b: dup
      // 408c: sipush 598
      // 408f: ldc2_w 6.52658129486538E-74
      // 4092: dastore
      // 4093: dup
      // 4094: sipush 599
      // 4097: ldc2_w 1.7804909509998055E-74
      // 409a: dastore
      // 409b: dup
      // 409c: sipush 600
      // 409f: ldc2_w -4.1900132227036916E-73
      // 40a2: dastore
      // 40a3: dup
      // 40a4: sipush 601
      // 40a7: ldc2_w 1.5705861683841123E-72
      // 40aa: dastore
      // 40ab: dup
      // 40ac: sipush 602
      // 40af: ldc2_w -1.904714978998808E-72
      // 40b2: dastore
      // 40b3: dup
      // 40b4: sipush 603
      // 40b7: ldc2_w -7.81295459930537E-72
      // 40ba: dastore
      // 40bb: dup
      // 40bc: sipush 604
      // 40bf: ldc2_w 2.818537910881676E-71
      // 40c2: dastore
      // 40c3: dup
      // 40c4: sipush 605
      // 40c7: ldc2_w 5.840507984320445E-71
      // 40ca: dastore
      // 40cb: dup
      // 40cc: sipush 606
      // 40cf: ldc2_w 1.7331720051707087E-70
      // 40d2: dastore
      // 40d3: dup
      // 40d4: sipush 607
      // 40d7: ldc2_w 1.936946987935961E-70
      // 40da: dastore
      // 40db: dup
      // 40dc: sipush 608
      // 40df: ldc2_w -5.86517231340979E-71
      // 40e2: dastore
      // 40e3: dup
      // 40e4: sipush 609
      // 40e7: ldc2_w -1.3277440528416646E-69
      // 40ea: dastore
      // 40eb: dup
      // 40ec: sipush 610
      // 40ef: ldc2_w 1.9906256185827793E-69
      // 40f2: dastore
      // 40f3: dup
      // 40f4: sipush 611
      // 40f7: ldc2_w 8.668714514280051E-69
      // 40fa: dastore
      // 40fb: dup
      // 40fc: sipush 612
      // 40ff: ldc2_w 6.643105496829061E-69
      // 4102: dastore
      // 4103: dup
      // 4104: sipush 613
      // 4107: ldc2_w -2.5436254170647032E-67
      // 410a: dastore
      // 410b: dup
      // 410c: sipush 614
      // 410f: ldc2_w -4.8279217213630774E-67
      // 4112: dastore
      // 4113: dup
      // 4114: sipush 615
      // 4117: ldc2_w -1.2640304072937576E-66
      // 411a: dastore
      // 411b: dup
      // 411c: sipush 616
      // 411f: ldc2_w 3.51187258511716E-66
      // 4122: dastore
      // 4123: dup
      // 4124: sipush 617
      // 4127: ldc2_w 1.4199501303738373E-65
      // 412a: dastore
      // 412b: dup
      // 412c: sipush 618
      // 412f: ldc2_w -1.2351697477129173E-65
      // 4132: dastore
      // 4133: dup
      // 4134: sipush 619
      // 4137: ldc2_w 7.0542365522414836E-65
      // 413a: dastore
      // 413b: dup
      // 413c: sipush 620
      // 413f: ldc2_w 1.030593104122615E-64
      // 4142: dastore
      // 4143: dup
      // 4144: sipush 621
      // 4147: ldc2_w -5.452692909894593E-65
      // 414a: dastore
      // 414b: dup
      // 414c: sipush 622
      // 414f: ldc2_w -9.415506349675128E-64
      // 4152: dastore
      // 4153: dup
      // 4154: sipush 623
      // 4157: ldc2_w -3.6206211446779087E-63
      // 415a: dastore
      // 415b: dup
      // 415c: sipush 624
      // 415f: ldc2_w -1.6699188275658641E-62
      // 4162: dastore
      // 4163: dup
      // 4164: sipush 625
      // 4167: ldc2_w 2.287280262665656E-62
      // 416a: dastore
      // 416b: dup
      // 416c: sipush 626
      // 416f: ldc2_w 7.076135457313529E-62
      // 4172: dastore
      // 4173: dup
      // 4174: sipush 627
      // 4177: ldc2_w 2.9019628518165404E-61
      // 417a: dastore
      // 417b: dup
      // 417c: sipush 628
      // 417f: ldc2_w -3.1305705497720186E-61
      // 4182: dastore
      // 4183: dup
      // 4184: sipush 629
      // 4187: ldc2_w 2.2978757040142953E-60
      // 418a: dastore
      // 418b: dup
      // 418c: sipush 630
      // 418f: ldc2_w 1.2424439441817321E-60
      // 4192: dastore
      // 4193: dup
      // 4194: sipush 631
      // 4197: ldc2_w 7.140343013236265E-60
      // 419a: dastore
      // 419b: dup
      // 419c: sipush 632
      // 419f: ldc2_w 8.633726388939636E-60
      // 41a2: dastore
      // 41a3: dup
      // 41a4: sipush 633
      // 41a7: ldc2_w 1.3483035574114863E-58
      // 41aa: dastore
      // 41ab: dup
      // 41ac: sipush 634
      // 41af: ldc2_w 1.653701058949654E-58
      // 41b2: dastore
      // 41b3: dup
      // 41b4: sipush 635
      // 41b7: ldc2_w -8.939932297357388E-58
      // 41ba: dastore
      // 41bb: dup
      // 41bc: sipush 636
      // 41bf: ldc2_w -1.395320103272191E-57
      // 41c2: dastore
      // 41c3: dup
      // 41c4: sipush 637
      // 41c7: ldc2_w 6.440430933947252E-58
      // 41ca: dastore
      // 41cb: dup
      // 41cc: sipush 638
      // 41cf: ldc2_w -1.681200826841738E-56
      // 41d2: dastore
      // 41d3: dup
      // 41d4: sipush 639
      // 41d7: ldc2_w 3.9904382022898837E-56
      // 41da: dastore
      // 41db: dup
      // 41dc: sipush 640
      // 41df: ldc2_w -4.870518577546228E-56
      // 41e2: dastore
      // 41e3: dup
      // 41e4: sipush 641
      // 41e7: ldc2_w -1.6990896855901115E-55
      // 41ea: dastore
      // 41eb: dup
      // 41ec: sipush 642
      // 41ef: ldc2_w -6.751434891261518E-56
      // 41f2: dastore
      // 41f3: dup
      // 41f4: sipush 643
      // 41f7: ldc2_w -1.669012123121194E-54
      // 41fa: dastore
      // 41fb: dup
      // 41fc: sipush 644
      // 41ff: ldc2_w -4.079585475491198E-54
      // 4202: dastore
      // 4203: dup
      // 4204: sipush 645
      // 4207: ldc2_w -1.3070436427679952E-53
      // 420a: dastore
      // 420b: dup
      // 420c: sipush 646
      // 420f: ldc2_w -3.090028378908628E-53
      // 4212: dastore
      // 4213: dup
      // 4214: sipush 647
      // 4217: ldc2_w 7.468160889798606E-53
      // 421a: dastore
      // 421b: dup
      // 421c: sipush 648
      // 421f: ldc2_w 6.229095980733463E-53
      // 4222: dastore
      // 4223: dup
      // 4224: sipush 649
      // 4227: ldc2_w 1.4794751934479566E-52
      // 422a: dastore
      // 422b: dup
      // 422c: sipush 650
      // 422f: ldc2_w 1.7444373785853918E-51
      // 4232: dastore
      // 4233: dup
      // 4234: sipush 651
      // 4237: ldc2_w -5.3681978363391484E-52
      // 423a: dastore
      // 423b: dup
      // 423c: sipush 652
      // 423f: ldc2_w 2.71853394036182E-51
      // 4242: dastore
      // 4243: dup
      // 4244: sipush 653
      // 4247: ldc2_w -1.3334367969274016E-50
      // 424a: dastore
      // 424b: dup
      // 424c: sipush 654
      // 424f: ldc2_w -1.6958057665854177E-49
      // 4252: dastore
      // 4253: dup
      // 4254: sipush 655
      // 4257: ldc2_w -1.452507231312146E-49
      // 425a: dastore
      // 425b: dup
      // 425c: sipush 656
      // 425f: ldc2_w 3.3855429446520427E-49
      // 4262: dastore
      // 4263: dup
      // 4264: sipush 657
      // 4267: ldc2_w 4.903687986212687E-49
      // 426a: dastore
      // 426b: dup
      // 426c: sipush 658
      // 426f: ldc2_w 2.2185957416622524E-48
      // 4272: dastore
      // 4273: dup
      // 4274: sipush 659
      // 4277: ldc2_w -9.924196700842429E-48
      // 427a: dastore
      // 427b: dup
      // 427c: sipush 660
      // 427f: ldc2_w 4.285128462851149E-47
      // 4282: dastore
      // 4283: dup
      // 4284: sipush 661
      // 4287: ldc2_w 3.076063086193525E-48
      // 428a: dastore
      // 428b: dup
      // 428c: sipush 662
      // 428f: ldc2_w 4.102052341676543E-46
      // 4292: dastore
      // 4293: dup
      // 4294: sipush 663
      // 4297: ldc2_w 1.1745772638457318E-45
      // 429a: dastore
      // 429b: dup
      // 429c: sipush 664
      // 429f: ldc2_w -5.309047216809048E-47
      // 42a2: dastore
      // 42a3: dup
      // 42a4: sipush 665
      // 42a7: ldc2_w 2.72972449891179E-45
      // 42aa: dastore
      // 42ab: dup
      // 42ac: sipush 666
      // 42af: ldc2_w -1.1748423022293739E-44
      // 42b2: dastore
      // 42b3: dup
      // 42b4: sipush 667
      // 42b7: ldc2_w 6.626052626622228E-44
      // 42ba: dastore
      // 42bb: dup
      // 42bc: sipush 668
      // 42bf: ldc2_w 3.0227439688367925E-44
      // 42c2: dastore
      // 42c3: dup
      // 42c4: sipush 669
      // 42c7: ldc2_w -4.740494808228372E-43
      // 42ca: dastore
      // 42cb: dup
      // 42cc: sipush 670
      // 42cf: ldc2_w 5.926057457356852E-43
      // 42d2: dastore
      // 42d3: dup
      // 42d4: sipush 671
      // 42d7: ldc2_w 3.09768273342776E-42
      // 42da: dastore
      // 42db: dup
      // 42dc: sipush 672
      // 42df: ldc2_w -5.589493227475577E-42
      // 42e2: dastore
      // 42e3: dup
      // 42e4: sipush 673
      // 42e7: ldc2_w -8.84908716783327E-42
      // 42ea: dastore
      // 42eb: dup
      // 42ec: sipush 674
      // 42ef: ldc2_w 2.3684740712822874E-41
      // 42f2: dastore
      // 42f3: dup
      // 42f4: sipush 675
      // 42f7: ldc2_w 1.4836491430755657E-40
      // 42fa: dastore
      // 42fb: dup
      // 42fc: sipush 676
      // 42ff: ldc2_w 4.5878801324451396E-40
      // 4302: dastore
      // 4303: dup
      // 4304: sipush 677
      // 4307: ldc2_w 1.0585156316103144E-39
      // 430a: dastore
      // 430b: dup
      // 430c: sipush 678
      // 430f: ldc2_w 2.3805896467049493E-39
      // 4312: dastore
      // 4313: dup
      // 4314: sipush 679
      // 4317: ldc2_w 1.0285082556185196E-38
      // 431a: dastore
      // 431b: dup
      // 431c: sipush 680
      // 431f: ldc2_w 2.5187968110874885E-38
      // 4322: dastore
      // 4323: dup
      // 4324: sipush 681
      // 4327: ldc2_w -1.4088399542613178E-38
      // 432a: dastore
      // 432b: dup
      // 432c: sipush 682
      // 432f: ldc2_w -3.00901028043488E-38
      // 4332: dastore
      // 4333: dup
      // 4334: sipush 683
      // 4337: ldc2_w 2.0089026801414973E-37
      // 433a: dastore
      // 433b: dup
      // 433c: sipush 684
      // 433f: ldc2_w -1.3324111396289096E-36
      // 4342: dastore
      // 4343: dup
      // 4344: sipush 685
      // 4347: ldc2_w 5.458481186294964E-36
      // 434a: dastore
      // 434b: dup
      // 434c: sipush 686
      // 434f: ldc2_w -4.8402541351522003E-36
      // 4352: dastore
      // 4353: dup
      // 4354: sipush 687
      // 4357: ldc2_w -1.3331969720555312E-35
      // 435a: dastore
      // 435b: dup
      // 435c: sipush 688
      // 435f: ldc2_w -8.248332290732976E-35
      // 4362: dastore
      // 4363: dup
      // 4364: sipush 689
      // 4367: ldc2_w -1.8349670703969982E-34
      // 436a: dastore
      // 436b: dup
      // 436c: sipush 690
      // 436f: ldc2_w 6.403477383195494E-34
      // 4372: dastore
      // 4373: dup
      // 4374: sipush 691
      // 4377: ldc2_w 3.7813691654412385E-34
      // 437a: dastore
      // 437b: dup
      // 437c: sipush 692
      // 437f: ldc2_w 2.4621305031382827E-33
      // 4382: dastore
      // 4383: dup
      // 4384: sipush 693
      // 4387: ldc2_w -5.634051826192439E-33
      // 438a: dastore
      // 438b: dup
      // 438c: sipush 694
      // 438f: ldc2_w 3.817173955083142E-32
      // 4392: dastore
      // 4393: dup
      // 4394: sipush 695
      // 4397: ldc2_w -6.038239639506472E-32
      // 439a: dastore
      // 439b: dup
      // 439c: sipush 696
      // 439f: ldc2_w -2.130447095555397E-31
      // 43a2: dastore
      // 43a3: dup
      // 43a4: sipush 697
      // 43a7: ldc2_w -6.824454861992054E-31
      // 43aa: dastore
      // 43ab: dup
      // 43ac: sipush 698
      // 43af: ldc2_w -1.3455801602048414E-30
      // 43b2: dastore
      // 43b3: dup
      // 43b4: sipush 699
      // 43b7: ldc2_w -2.518642767561659E-30
      // 43ba: dastore
      // 43bb: dup
      // 43bc: sipush 700
      // 43bf: ldc2_w 8.082792416221215E-30
      // 43c2: dastore
      // 43c3: dup
      // 43c4: sipush 701
      // 43c7: ldc2_w 4.718103502869148E-29
      // 43ca: dastore
      // 43cb: dup
      // 43cc: sipush 702
      // 43cf: ldc2_w -5.607991635038776E-29
      // 43d2: dastore
      // 43d3: dup
      // 43d4: sipush 703
      // 43d7: ldc2_w -1.8042191582018579E-28
      // 43da: dastore
      // 43db: dup
      // 43dc: sipush 704
      // 43df: ldc2_w 6.989914264479507E-28
      // 43e2: dastore
      // 43e3: dup
      // 43e4: sipush 705
      // 43e7: ldc2_w -2.9031739430339586E-28
      // 43ea: dastore
      // 43eb: dup
      // 43ec: sipush 706
      // 43ef: ldc2_w 6.076820259849921E-27
      // 43f2: dastore
      // 43f3: dup
      // 43f4: sipush 707
      // 43f7: ldc2_w -3.24981577480893E-27
      // 43fa: dastore
      // 43fb: dup
      // 43fc: sipush 708
      // 43ff: ldc2_w -2.7648210023059463E-26
      // 4402: dastore
      // 4403: dup
      // 4404: sipush 709
      // 4407: ldc2_w -9.785306155980342E-26
      // 440a: dastore
      // 440b: dup
      // 440c: sipush 710
      // 440f: ldc2_w 1.241529292737115E-25
      // 4412: dastore
      // 4413: dup
      // 4414: sipush 711
      // 4417: ldc2_w 3.0891604448087654E-25
      // 441a: dastore
      // 441b: dup
      // 441c: sipush 712
      // 441f: ldc2_w 2.3451052074796954E-24
      // 4422: dastore
      // 4423: dup
      // 4424: sipush 713
      // 4427: ldc2_w 6.574128018028633E-24
      // 442a: dastore
      // 442b: dup
      // 442c: sipush 714
      // 442f: ldc2_w -1.3345148716925826E-23
      // 4432: dastore
      // 4433: dup
      // 4434: sipush 715
      // 4437: ldc2_w 4.3594621428644293E-23
      // 443a: dastore
      // 443b: dup
      // 443c: sipush 716
      // 443f: ldc2_w -5.678896695157704E-23
      // 4442: dastore
      // 4443: dup
      // 4444: sipush 717
      // 4447: ldc2_w -4.676849004137386E-23
      // 444a: dastore
      // 444b: dup
      // 444c: sipush 718
      // 444f: ldc2_w -2.281578975407609E-22
      // 4452: dastore
      // 4453: dup
      // 4454: sipush 719
      // 4457: ldc2_w -3.144430608076357E-21
      // 445a: dastore
      // 445b: dup
      // 445c: sipush 720
      // 445f: ldc2_w 5.662033727488754E-22
      // 4462: dastore
      // 4463: dup
      // 4464: sipush 721
      // 4467: ldc2_w -4.30293375386492E-21
      // 446a: dastore
      // 446b: dup
      // 446c: sipush 722
      // 446f: ldc2_w 4.985137671479376E-20
      // 4472: dastore
      // 4473: dup
      // 4474: sipush 723
      // 4477: ldc2_w 1.657668502165438E-19
      // 447a: dastore
      // 447b: dup
      // 447c: sipush 724
      // 447f: ldc2_w -3.3878706977811337E-19
      // 4482: dastore
      // 4483: dup
      // 4484: sipush 725
      // 4487: ldc2_w -7.488022803661722E-19
      // 448a: dastore
      // 448b: dup
      // 448c: sipush 726
      // 448f: ldc2_w 1.725039737424264E-18
      // 4492: dastore
      // 4493: dup
      // 4494: sipush 727
      // 4497: ldc2_w -6.0275040161173166E-18
      // 449a: dastore
      // 449b: dup
      // 449c: sipush 728
      // 449f: ldc2_w -8.081007442213538E-19
      // 44a2: dastore
      // 44a3: dup
      // 44a4: sipush 729
      // 44a7: ldc2_w 2.9257892371894816E-17
      // 44aa: dastore
      // 44ab: dup
      // 44ac: sipush 730
      // 44af: ldc2_w 1.5231541295722552E-16
      // 44b2: dastore
      // 44b3: dup
      // 44b4: sipush 731
      // 44b7: ldc2_w -1.1474026049124666E-17
      // 44ba: dastore
      // 44bb: dup
      // 44bc: sipush 732
      // 44bf: ldc2_w 6.890372706231206E-16
      // 44c2: dastore
      // 44c3: dup
      // 44c4: sipush 733
      // 44c7: ldc2_w 2.592721454922832E-15
      // 44ca: dastore
      // 44cb: dup
      // 44cc: sipush 734
      // 44cf: ldc2_w -1.1253822296423454E-15
      // 44d2: dastore
      // 44d3: dup
      // 44d4: sipush 735
      // 44d7: ldc2_w -2.650684279637763E-14
      // 44da: dastore
      // 44db: dup
      // 44dc: sipush 736
      // 44df: ldc2_w -4.107226967119929E-15
      // 44e2: dastore
      // 44e3: dup
      // 44e4: sipush 737
      // 44e7: ldc2_w -3.130508064738312E-14
      // 44ea: dastore
      // 44eb: dup
      // 44ec: sipush 738
      // 44ef: ldc2_w -6.729414275200856E-14
      // 44f2: dastore
      // 44f3: dup
      // 44f4: sipush 739
      // 44f7: ldc2_w -1.6166170913368169E-12
      // 44fa: dastore
      // 44fb: dup
      // 44fc: sipush 740
      // 44ff: ldc2_w -1.2059301405584488E-12
      // 4502: dastore
      // 4503: dup
      // 4504: sipush 741
      // 4507: ldc2_w -1.2210091619211167E-11
      // 450a: dastore
      // 450b: dup
      // 450c: sipush 742
      // 450f: ldc2_w 3.695372823623631E-12
      // 4512: dastore
      // 4513: dup
      // 4514: sipush 743
      // 4517: ldc2_w 5.119220484478292E-11
      // 451a: dastore
      // 451b: dup
      // 451c: sipush 744
      // 451f: ldc2_w -1.0857572226543142E-10
      // 4522: dastore
      // 4523: dup
      // 4524: sipush 745
      // 4527: ldc2_w -4.6490379071586397E-10
      // 452a: dastore
      // 452b: dup
      // 452c: sipush 746
      // 452f: ldc2_w -4.5810381714280557E-10
      // 4532: dastore
      // 4533: dup
      // 4534: sipush 747
      // 4537: ldc2_w 1.4909756678328582E-9
      // 453a: dastore
      // 453b: dup
      // 453c: sipush 748
      // 453f: ldc2_w -1.3155828104004438E-8
      // 4542: dastore
      // 4543: dup
      // 4544: sipush 749
      // 4547: ldc2_w -9.149755188170102E-9
      // 454a: dastore
      // 454b: dup
      // 454c: sipush 750
      // 454f: dconst_0
      // 4550: dastore
      // 4551: dup
      // 4552: sipush 751
      // 4555: ldc2_w 8.254840070411029E-8
      // 4558: dastore
      // 4559: dup
      // 455a: sipush 752
      // 455d: ldc2_w -1.0681886149151956E-7
      // 4560: dastore
      // 4561: dup
      // 4562: sipush 753
      // 4565: ldc2_w -3.359944163407147E-8
      // 4568: dastore
      // 4569: dup
      // 456a: sipush 754
      // 456d: ldc2_w -2.1275002921718894E-6
      // 4570: dastore
      // 4571: dup
      // 4572: sipush 755
      // 4575: ldc2_w 1.2129920353421116E-5
      // 4578: dastore
      // 4579: dup
      // 457a: sipush 756
      // 457d: ldc2_w 2.1520078872608393E-5
      // 4580: dastore
      // 4581: dup
      // 4582: sipush 757
      // 4585: ldc2_w 1.0178783359926372E-4
      // 4588: dastore
      // 4589: dup
      // 458a: sipush 758
      // 458d: ldc2_w -2.077077172525637E-5
      // 4590: dastore
      // 4591: dup
      // 4592: sipush 759
      // 4595: ldc2_w -5.67996159922899E-5
      // 4598: dastore
      // 4599: dup
      // 459a: sipush 760
      // 459d: ldc2_w 9.510567165169581E-4
      // 45a0: dastore
      // 45a1: dup
      // 45a2: sipush 761
      // 45a5: ldc2_w 0.0010901978184553272
      // 45a8: dastore
      // 45a9: dup
      // 45aa: sipush 762
      // 45ad: ldc2_w 0.010169003920808009
      // 45b0: dastore
      // 45b1: dup
      // 45b2: sipush 763
      // 45b5: ldc2_w 0.017008920503326107
      // 45b8: dastore
      // 45b9: dup
      // 45ba: sipush 764
      // 45bd: ldc2_w 0.03416477677774927
      // 45c0: dastore
      // 45c1: dup
      // 45c2: sipush 765
      // 45c5: ldc2_w -0.1275278893606981
      // 45c8: dastore
      // 45c9: dup
      // 45ca: sipush 766
      // 45cd: ldc2_w 0.5205078726367633
      // 45d0: dastore
      // 45d1: dup
      // 45d2: sipush 767
      // 45d5: ldc2_w 0.7535752982147762
      // 45d8: dastore
      // 45d9: dup
      // 45da: sipush 768
      // 45dd: ldc2_w 1.1373305111387886
      // 45e0: dastore
      // 45e1: dup
      // 45e2: sipush 769
      // 45e5: ldc2_w -3.036812739155085
      // 45e8: dastore
      // 45e9: dup
      // 45ea: sipush 770
      // 45ed: ldc2_w 11.409790277969124
      // 45f0: dastore
      // 45f1: dup
      // 45f2: sipush 771
      // 45f5: ldc2_w -9.516785302789955
      // 45f8: dastore
      // 45f9: dup
      // 45fa: sipush 772
      // 45fd: ldc2_w -49.86840843831867
      // 4600: dastore
      // 4601: dup
      // 4602: sipush 773
      // 4605: ldc2_w -393.7510973999651
      // 4608: dastore
      // 4609: dup
      // 460a: sipush 774
      // 460d: ldc2_w -686.1565277058598
      // 4610: dastore
      // 4611: dup
      // 4612: sipush 775
      // 4615: ldc2_w 4617.385872524165
      // 4618: dastore
      // 4619: dup
      // 461a: sipush 776
      // 461d: ldc2_w -11563.161235730215
      // 4620: dastore
      // 4621: dup
      // 4622: sipush 777
      // 4625: ldc2_w -8230.201383316231
      // 4628: dastore
      // 4629: dup
      // 462a: sipush 778
      // 462d: ldc2_w -34460.52482632287
      // 4630: dastore
      // 4631: dup
      // 4632: sipush 779
      // 4635: ldc2_w 50744.04207438878
      // 4638: dastore
      // 4639: dup
      // 463a: sipush 780
      // 463d: ldc2_w 357908.46214699093
      // 4640: dastore
      // 4641: dup
      // 4642: sipush 781
      // 4645: ldc2_w 1936607.425231087
      // 4648: dastore
      // 4649: dup
      // 464a: sipush 782
      // 464d: ldc2_w 3222936.695160983
      // 4650: dastore
      // 4651: dup
      // 4652: sipush 783
      // 4655: ldc2_w 5477052.0646243105
      // 4658: dastore
      // 4659: dup
      // 465a: sipush 784
      // 465d: ldc2_w -3.517545711859706E7
      // 4660: dastore
      // 4661: dup
      // 4662: sipush 785
      // 4665: ldc2_w -1.2693418527187027E8
      // 4668: dastore
      // 4669: dup
      // 466a: sipush 786
      // 466d: ldc2_w -2.5316384477288628E8
      // 4670: dastore
      // 4671: dup
      // 4672: sipush 787
      // 4675: ldc2_w -1.6436423669122624E8
      // 4678: dastore
      // 4679: dup
      // 467a: sipush 788
      // 467d: ldc2_w 4.0889180422033095E8
      // 4680: dastore
      // 4681: dup
      // 4682: sipush 789
      // 4685: ldc2_w 4.968829330953611E9
      // 4688: dastore
      // 4689: dup
      // 468a: sipush 790
      // 468d: ldc2_w -3.503399598592085E9
      // 4690: dastore
      // 4691: dup
      // 4692: sipush 791
      // 4695: ldc2_w 1.905394922122271E10
      // 4698: dastore
      // 4699: dup
      // 469a: sipush 792
      // 469d: ldc2_w 1.0361722296739479E11
      // 46a0: dastore
      // 46a1: dup
      // 46a2: sipush 793
      // 46a5: ldc2_w -5.806792575852521E10
      // 46a8: dastore
      // 46a9: dup
      // 46aa: sipush 794
      // 46ad: ldc2_w 2.3454138776381036E11
      // 46b0: dastore
      // 46b1: dup
      // 46b2: sipush 795
      // 46b5: ldc2_w -1.718446464587963E12
      // 46b8: dastore
      // 46b9: dup
      // 46ba: sipush 796
      // 46bd: ldc2_w -1.0946634815588584E12
      // 46c0: dastore
      // 46c1: dup
      // 46c2: sipush 797
      // 46c5: ldc2_w 1.6889383928999305E13
      // 46c8: dastore
      // 46c9: dup
      // 46ca: sipush 798
      // 46cd: ldc2_w -3.784600043778247E13
      // 46d0: dastore
      // 46d1: dup
      // 46d2: sipush 799
      // 46d5: ldc2_w 7.270965670658928E13
      // 46d8: dastore
      // 46d9: dup
      // 46da: sipush 800
      // 46dd: ldc2_w -4.9202842786896806E14
      // 46e0: dastore
      // 46e1: dup
      // 46e2: sipush 801
      // 46e5: ldc2_w 4.597700093952774E14
      // 46e8: dastore
      // 46e9: dup
      // 46ea: sipush 802
      // 46ed: ldc2_w 2.6113557852262235E15
      // 46f0: dastore
      // 46f1: dup
      // 46f2: sipush 803
      // 46f5: ldc2_w -4.544525556171388E15
      // 46f8: dastore
      // 46f9: dup
      // 46fa: sipush 804
      // 46fd: ldc2_w -9.517971970450354E15
      // 4700: dastore
      // 4701: dup
      // 4702: sipush 805
      // 4705: ldc2_w -2.0634857819227416E16
      // 4708: dastore
      // 4709: dup
      // 470a: sipush 806
      // 470d: ldc2_w -9.714311310454981E16
      // 4710: dastore
      // 4711: dup
      // 4712: sipush 807
      // 4715: ldc2_w -2.2667083759873216E16
      // 4718: dastore
      // 4719: dup
      // 471a: sipush 808
      // 471d: ldc2_w -7.228566516443958E17
      // 4720: dastore
      // 4721: dup
      // 4722: sipush 809
      // 4725: ldc2_w 4.1215410760803866E18
      // 4728: dastore
      // 4729: dup
      // 472a: sipush 810
      // 472d: ldc2_w 8.580748830097221E18
      // 4730: dastore
      // 4731: dup
      // 4732: sipush 811
      // 4735: ldc2_w 1.530436781375042E19
      // 4738: dastore
      // 4739: dup
      // 473a: sipush 812
      // 473d: ldc2_w -1.5453111533064765E19
      // 4740: dastore
      // 4741: dup
      // 4742: sipush 813
      // 4745: ldc2_w -1.0633845571643594E20
      // 4748: dastore
      // 4749: dup
      // 474a: sipush 814
      // 474d: ldc2_w -3.512380426745336E20
      // 4750: dastore
      // 4751: dup
      // 4752: sipush 815
      // 4755: ldc2_w 3.7734658676841284E20
      // 4758: dastore
      // 4759: dup
      // 475a: sipush 816
      // 475d: ldc2_w -3.855478664503271E21
      // 4760: dastore
      // 4761: dup
      // 4762: sipush 817
      // 4765: ldc2_w 7.984485303520287E21
      // 4768: dastore
      // 4769: dup
      // 476a: sipush 818
      // 476d: ldc2_w -1.2296934902142301E22
      // 4770: dastore
      // 4771: dup
      // 4772: sipush 819
      // 4775: ldc2_w 1.042139023692827E22
      // 4778: dastore
      // 4779: dup
      // 477a: sipush 820
      // 477d: ldc2_w 1.2167897656061312E23
      // 4780: dastore
      // 4781: dup
      // 4782: sipush 821
      // 4785: ldc2_w 9.22064170155394E22
      // 4788: dastore
      // 4789: dup
      // 478a: sipush 822
      // 478d: ldc2_w 3.965171513035854E23
      // 4790: dastore
      // 4791: dup
      // 4792: sipush 823
      // 4795: ldc2_w -4.135121057126514E24
      // 4798: dastore
      // 4799: dup
      // 479a: sipush 824
      // 479d: ldc2_w -7.944341754299148E24
      // 47a0: dastore
      // 47a1: dup
      // 47a2: sipush 825
      // 47a5: ldc2_w 1.4715152230577016E25
      // 47a8: dastore
      // 47a9: dup
      // 47aa: sipush 826
      // 47ad: ldc2_w -3.0635272288480756E25
      // 47b0: dastore
      // 47b1: dup
      // 47b2: sipush 827
      // 47b5: ldc2_w -9.54468158713835E25
      // 47b8: dastore
      // 47b9: dup
      // 47ba: sipush 828
      // 47bd: ldc2_w 1.5411775738825048E25
      // 47c0: dastore
      // 47c1: dup
      // 47c2: sipush 829
      // 47c5: ldc2_w -8.274711842374368E26
      // 47c8: dastore
      // 47c9: dup
      // 47ca: sipush 830
      // 47cd: ldc2_w -1.0028324930788433E27
      // 47d0: dastore
      // 47d1: dup
      // 47d2: sipush 831
      // 47d5: ldc2_w 5.189062091114782E27
      // 47d8: dastore
      // 47d9: dup
      // 47da: sipush 832
      // 47dd: ldc2_w -2.8583500869462184E28
      // 47e0: dastore
      // 47e1: dup
      // 47e2: sipush 833
      // 47e5: ldc2_w -5.198295198128238E28
      // 47e8: dastore
      // 47e9: dup
      // 47ea: sipush 834
      // 47ed: ldc2_w 2.9758750368256437E29
      // 47f0: dastore
      // 47f1: dup
      // 47f2: sipush 835
      // 47f5: ldc2_w 3.216046320616945E29
      // 47f8: dastore
      // 47f9: dup
      // 47fa: sipush 836
      // 47fd: ldc2_w -1.7846700158234043E30
      // 4800: dastore
      // 4801: dup
      // 4802: sipush 837
      // 4805: ldc2_w 3.847174961282827E30
      // 4808: dastore
      // 4809: dup
      // 480a: sipush 838
      // 480d: ldc2_w 9.026991921214922E30
      // 4810: dastore
      // 4811: dup
      // 4812: sipush 839
      // 4815: ldc2_w 4.1358029739592175E30
      // 4818: dastore
      // 4819: dup
      // 481a: sipush 840
      // 481d: ldc2_w -6.461509354879894E29
      // 4820: dastore
      // 4821: dup
      // 4822: sipush 841
      // 4825: ldc2_w 9.704297297526684E31
      // 4828: dastore
      // 4829: dup
      // 482a: sipush 842
      // 482d: ldc2_w 2.9731739067444943E32
      // 4830: dastore
      // 4831: dup
      // 4832: sipush 843
      // 4835: ldc2_w 9.97728609663656E32
      // 4838: dastore
      // 4839: dup
      // 483a: sipush 844
      // 483d: ldc2_w 3.1149346370027763E33
      // 4840: dastore
      // 4841: dup
      // 4842: sipush 845
      // 4845: ldc2_w 2.0051635097366476E34
      // 4848: dastore
      // 4849: dup
      // 484a: sipush 846
      // 484d: ldc2_w 2.819272221032373E34
      // 4850: dastore
      // 4851: dup
      // 4852: sipush 847
      // 4855: ldc2_w 1.6266731695798413E34
      // 4858: dastore
      // 4859: dup
      // 485a: sipush 848
      // 485d: ldc2_w 1.998050894021586E35
      // 4860: dastore
      // 4861: dup
      // 4862: sipush 849
      // 4865: ldc2_w -6.1633417615076335E35
      // 4868: dastore
      // 4869: dup
      // 486a: sipush 850
      // 486d: ldc2_w 2.2505716077585116E36
      // 4870: dastore
      // 4871: dup
      // 4872: sipush 851
      // 4875: ldc2_w 1.9299691540987203E36
      // 4878: dastore
      // 4879: dup
      // 487a: sipush 852
      // 487d: ldc2_w 8.006569251375383E36
      // 4880: dastore
      // 4881: dup
      // 4882: sipush 853
      // 4885: ldc2_w -3.785295042408568E37
      // 4888: dastore
      // 4889: dup
      // 488a: sipush 854
      // 488d: ldc2_w -1.1870498357197593E38
      // 4890: dastore
      // 4891: dup
      // 4892: sipush 855
      // 4895: ldc2_w 1.0010529668998112E38
      // 4898: dastore
      // 4899: dup
      // 489a: sipush 856
      // 489d: ldc2_w 1.3240710866573994E38
      // 48a0: dastore
      // 48a1: dup
      // 48a2: sipush 857
      // 48a5: ldc2_w 2.6888010385137123E39
      // 48a8: dastore
      // 48a9: dup
      // 48aa: sipush 858
      // 48ad: ldc2_w 1.7400655988987023E39
      // 48b0: dastore
      // 48b1: dup
      // 48b2: sipush 859
      // 48b5: ldc2_w -6.402740469853475E39
      // 48b8: dastore
      // 48b9: dup
      // 48ba: sipush 860
      // 48bd: ldc2_w -3.93114092562274E40
      // 48c0: dastore
      // 48c1: dup
      // 48c2: sipush 861
      // 48c5: ldc2_w 1.2363717201084252E41
      // 48c8: dastore
      // 48c9: dup
      // 48ca: sipush 862
      // 48cd: ldc2_w -1.9219116633978794E41
      // 48d0: dastore
      // 48d1: dup
      // 48d2: sipush 863
      // 48d5: ldc2_w -1.347867098583136E42
      // 48d8: dastore
      // 48d9: dup
      // 48da: sipush 864
      // 48dd: ldc2_w 7.87675118338788E41
      // 48e0: dastore
      // 48e1: dup
      // 48e2: sipush 865
      // 48e5: ldc2_w 3.3932984011177642E41
      // 48e8: dastore
      // 48e9: dup
      // 48ea: sipush 866
      // 48ed: ldc2_w -1.9872713979884691E43
      // 48f0: dastore
      // 48f1: dup
      // 48f2: sipush 867
      // 48f5: ldc2_w 2.220208491349658E43
      // 48f8: dastore
      // 48f9: dup
      // 48fa: sipush 868
      // 48fd: ldc2_w -3.466267817480825E43
      // 4900: dastore
      // 4901: dup
      // 4902: sipush 869
      // 4905: ldc2_w 3.19462030745197E44
      // 4908: dastore
      // 4909: dup
      // 490a: sipush 870
      // 490d: ldc2_w -9.841244788104406E44
      // 4910: dastore
      // 4911: dup
      // 4912: sipush 871
      // 4915: ldc2_w -2.2676593395522725E45
      // 4918: dastore
      // 4919: dup
      // 491a: sipush 872
      // 491d: ldc2_w -1.1349246400274207E46
      // 4920: dastore
      // 4921: dup
      // 4922: sipush 873
      // 4925: ldc2_w -1.1700910284427406E46
      // 4928: dastore
      // 4929: dup
      // 492a: sipush 874
      // 492d: ldc2_w -3.6754317105801715E46
      // 4930: dastore
      // 4931: dup
      // 4932: sipush 875
      // 4935: ldc2_w 1.7647101734915075E47
      // 4938: dastore
      // 4939: dup
      // 493a: sipush 876
      // 493d: ldc2_w 2.122358392979746E47
      // 4940: dastore
      // 4941: dup
      // 4942: sipush 877
      // 4945: ldc2_w 3.156243682143956E47
      // 4948: dastore
      // 4949: dup
      // 494a: sipush 878
      // 494d: ldc2_w 5.356668151937413E47
      // 4950: dastore
      // 4951: dup
      // 4952: sipush 879
      // 4955: ldc2_w 2.7668218233914262E48
      // 4958: dastore
      // 4959: dup
      // 495a: sipush 880
      // 495d: ldc2_w 3.5127708120698784E48
      // 4960: dastore
      // 4961: dup
      // 4962: sipush 881
      // 4965: ldc2_w 1.7884841356632925E49
      // 4968: dastore
      // 4969: dup
      // 496a: sipush 882
      // 496d: ldc2_w 1.716531820904728E50
      // 4970: dastore
      // 4971: dup
      // 4972: sipush 883
      // 4975: ldc2_w -2.9114757102866277E50
      // 4978: dastore
      // 4979: dup
      // 497a: sipush 884
      // 497d: ldc2_w 1.0657703081219677E51
      // 4980: dastore
      // 4981: dup
      // 4982: sipush 885
      // 4985: ldc2_w -7.512169809356372E50
      // 4988: dastore
      // 4989: dup
      // 498a: sipush 886
      // 498d: ldc2_w 1.764200470879736E51
      // 4990: dastore
      // 4991: dup
      // 4992: sipush 887
      // 4995: ldc2_w -1.0088898215431471E52
      // 4998: dastore
      // 4999: dup
      // 499a: sipush 888
      // 499d: ldc2_w -3.1085734725176E52
      // 49a0: dastore
      // 49a1: dup
      // 49a2: sipush 889
      // 49a5: ldc2_w 4.3529009584292495E52
      // 49a8: dastore
      // 49a9: dup
      // 49aa: sipush 890
      // 49ad: ldc2_w -2.467842129213774E53
      // 49b0: dastore
      // 49b1: dup
      // 49b2: sipush 891
      // 49b5: ldc2_w -3.9317379627195146E53
      // 49b8: dastore
      // 49b9: dup
      // 49ba: sipush 892
      // 49bd: ldc2_w -4.332335454045836E52
      // 49c0: dastore
      // 49c1: dup
      // 49c2: sipush 893
      // 49c5: ldc2_w 7.979013724931926E54
      // 49c8: dastore
      // 49c9: dup
      // 49ca: sipush 894
      // 49cd: ldc2_w -1.5038413653121357E55
      // 49d0: dastore
      // 49d1: dup
      // 49d2: sipush 895
      // 49d5: ldc2_w 9.310799925566843E55
      // 49d8: dastore
      // 49d9: dup
      // 49da: sipush 896
      // 49dd: ldc2_w -2.2042966348036592E55
      // 49e0: dastore
      // 49e1: dup
      // 49e2: sipush 897
      // 49e5: ldc2_w -4.518315366841937E55
      // 49e8: dastore
      // 49e9: dup
      // 49ea: sipush 898
      // 49ed: ldc2_w -6.971366338144781E56
      // 49f0: dastore
      // 49f1: dup
      // 49f2: sipush 899
      // 49f5: ldc2_w -2.0461505570781806E57
      // 49f8: dastore
      // 49f9: dup
      // 49fa: sipush 900
      // 49fd: ldc2_w -8.823884392655312E57
      // 4a00: dastore
      // 4a01: dup
      // 4a02: sipush 901
      // 4a05: ldc2_w -1.1264032993918548E58
      // 4a08: dastore
      // 4a09: dup
      // 4a0a: sipush 902
      // 4a0d: ldc2_w -7.692065092509875E58
      // 4a10: dastore
      // 4a11: dup
      // 4a12: sipush 903
      // 4a15: ldc2_w -1.8472516879728875E59
      // 4a18: dastore
      // 4a19: dup
      // 4a1a: sipush 904
      // 4a1d: ldc2_w 8.72220314694275E58
      // 4a20: dastore
      // 4a21: dup
      // 4a22: sipush 905
      // 4a25: ldc2_w 1.6525336989036362E59
      // 4a28: dastore
      // 4a29: dup
      // 4a2a: sipush 906
      // 4a2d: ldc2_w -3.343201925128334E60
      // 4a30: dastore
      // 4a31: dup
      // 4a32: sipush 907
      // 4a35: ldc2_w 5.493352163155986E60
      // 4a38: dastore
      // 4a39: dup
      // 4a3a: sipush 908
      // 4a3d: ldc2_w -2.548073509300398E61
      // 4a40: dastore
      // 4a41: dup
      // 4a42: sipush 909
      // 4a45: ldc2_w -9.566541624209933E61
      // 4a48: dastore
      // 4a49: dup
      // 4a4a: sipush 910
      // 4a4d: ldc2_w 4.0891054447206644E61
      // 4a50: dastore
      // 4a51: dup
      // 4a52: sipush 911
      // 4a55: ldc2_w -7.724182294653349E62
      // 4a58: dastore
      // 4a59: dup
      // 4a5a: sipush 912
      // 4a5d: ldc2_w 1.0143022354947225E63
      // 4a60: dastore
      // 4a61: dup
      // 4a62: sipush 913
      // 4a65: ldc2_w -4.952031310451961E63
      // 4a68: dastore
      // 4a69: dup
      // 4a6a: sipush 914
      // 4a6d: ldc2_w -7.877410133454722E63
      // 4a70: dastore
      // 4a71: dup
      // 4a72: sipush 915
      // 4a75: ldc2_w 4.505432606253564E64
      // 4a78: dastore
      // 4a79: dup
      // 4a7a: sipush 916
      // 4a7d: ldc2_w -7.330635250808021E64
      // 4a80: dastore
      // 4a81: dup
      // 4a82: sipush 917
      // 4a85: ldc2_w -1.642361029990822E65
      // 4a88: dastore
      // 4a89: dup
      // 4a8a: sipush 918
      // 4a8d: ldc2_w 5.982180242124184E65
      // 4a90: dastore
      // 4a91: dup
      // 4a92: sipush 919
      // 4a95: ldc2_w 7.120242132370469E65
      // 4a98: dastore
      // 4a99: dup
      // 4a9a: sipush 920
      // 4a9d: ldc2_w 5.908356249789671E66
      // 4aa0: dastore
      // 4aa1: dup
      // 4aa2: sipush 921
      // 4aa5: ldc2_w -2.8477710945673134E65
      // 4aa8: dastore
      // 4aa9: dup
      // 4aaa: sipush 922
      // 4aad: ldc2_w 6.65688196961235E66
      // 4ab0: dastore
      // 4ab1: dup
      // 4ab2: sipush 923
      // 4ab5: ldc2_w -9.233295580238604E67
      // 4ab8: dastore
      // 4ab9: dup
      // 4aba: sipush 924
      // 4abd: ldc2_w 3.2850043261803593E68
      // 4ac0: dastore
      // 4ac1: dup
      // 4ac2: sipush 925
      // 4ac5: ldc2_w 7.041681569694413E68
      // 4ac8: dastore
      // 4ac9: dup
      // 4aca: sipush 926
      // 4acd: ldc2_w -1.5652761725518397E69
      // 4ad0: dastore
      // 4ad1: dup
      // 4ad2: sipush 927
      // 4ad5: ldc2_w 1.5377053215489084E68
      // 4ad8: dastore
      // 4ad9: dup
      // 4ada: sipush 928
      // 4add: ldc2_w 1.282130763903269E70
      // 4ae0: dastore
      // 4ae1: dup
      // 4ae2: sipush 929
      // 4ae5: ldc2_w -2.380286345847567E70
      // 4ae8: dastore
      // 4ae9: dup
      // 4aea: sipush 930
      // 4aed: ldc2_w -7.207022875977515E70
      // 4af0: dastore
      // 4af1: dup
      // 4af2: sipush 931
      // 4af5: ldc2_w 2.7641662602473095E71
      // 4af8: dastore
      // 4af9: dup
      // 4afa: sipush 932
      // 4afd: ldc2_w 7.685235201534525E71
      // 4b00: dastore
      // 4b01: dup
      // 4b02: sipush 933
      // 4b05: ldc2_w 4.3239378585884645E70
      // 4b08: dastore
      // 4b09: dup
      // 4b0a: sipush 934
      // 4b0d: ldc2_w -1.6840562544109314E72
      // 4b10: dastore
      // 4b11: dup
      // 4b12: sipush 935
      // 4b15: ldc2_w -5.04128025464686E71
      // 4b18: dastore
      // 4b19: dup
      // 4b1a: sipush 936
      // 4b1d: ldc2_w 5.4557485189210095E73
      // 4b20: dastore
      // 4b21: dup
      // 4b22: sipush 937
      // 4b25: ldc2_w 7.160277784358221E73
      // 4b28: dastore
      // 4b29: dup
      // 4b2a: sipush 938
      // 4b2d: ldc2_w 7.636179075087608E73
      // 4b30: dastore
      // 4b31: dup
      // 4b32: sipush 939
      // 4b35: ldc2_w -8.18804507680012E74
      // 4b38: dastore
      // 4b39: dup
      // 4b3a: sipush 940
      // 4b3d: ldc2_w 2.807397988979441E75
      // 4b40: dastore
      // 4b41: dup
      // 4b42: sipush 941
      // 4b45: ldc2_w 2.165163304600171E75
      // 4b48: dastore
      // 4b49: dup
      // 4b4a: sipush 942
      // 4b4d: ldc2_w -1.3208450062862734E76
      // 4b50: dastore
      // 4b51: dup
      // 4b52: sipush 943
      // 4b55: ldc2_w -5.1939252391404724E76
      // 4b58: dastore
      // 4b59: dup
      // 4b5a: sipush 944
      // 4b5d: ldc2_w -6.985952908805853E76
      // 4b60: dastore
      // 4b61: dup
      // 4b62: sipush 945
      // 4b65: ldc2_w -1.6259920998287064E77
      // 4b68: dastore
      // 4b69: dup
      // 4b6a: sipush 946
      // 4b6d: ldc2_w 6.098975200926637E77
      // 4b70: dastore
      // 4b71: dup
      // 4b72: sipush 947
      // 4b75: ldc2_w -5.63383579957466E77
      // 4b78: dastore
      // 4b79: dup
      // 4b7a: sipush 948
      // 4b7d: ldc2_w -1.5876819186852907E78
      // 4b80: dastore
      // 4b81: dup
      // 4b82: sipush 949
      // 4b85: ldc2_w 2.1487475413123092E79
      // 4b88: dastore
      // 4b89: dup
      // 4b8a: sipush 950
      // 4b8d: ldc2_w -3.987619123706934E79
      // 4b90: dastore
      // 4b91: dup
      // 4b92: sipush 951
      // 4b95: ldc2_w 9.772655251656639E79
      // 4b98: dastore
      // 4b99: dup
      // 4b9a: sipush 952
      // 4b9d: ldc2_w -1.638756156057952E79
      // 4ba0: dastore
      // 4ba1: dup
      // 4ba2: sipush 953
      // 4ba5: ldc2_w -7.83892088580041E80
      // 4ba8: dastore
      // 4ba9: dup
      // 4baa: sipush 954
      // 4bad: ldc2_w 1.274413296252691E81
      // 4bb0: dastore
      // 4bb1: dup
      // 4bb2: sipush 955
      // 4bb5: ldc2_w 2.51946651720982E81
      // 4bb8: dastore
      // 4bb9: dup
      // 4bba: sipush 956
      // 4bbd: ldc2_w -2.516866097506943E81
      // 4bc0: dastore
      // 4bc1: dup
      // 4bc2: sipush 957
      // 4bc5: ldc2_w 1.053956282234684E82
      // 4bc8: dastore
      // 4bc9: dup
      // 4bca: sipush 958
      // 4bcd: ldc2_w 1.8279051206232177E83
      // 4bd0: dastore
      // 4bd1: dup
      // 4bd2: sipush 959
      // 4bd5: ldc2_w 1.2250764591564252E82
      // 4bd8: dastore
      // 4bd9: dup
      // 4bda: sipush 960
      // 4bdd: ldc2_w -4.0353723442917463E83
      // 4be0: dastore
      // 4be1: dup
      // 4be2: sipush 961
      // 4be5: ldc2_w -1.4121324224340735E84
      // 4be8: dastore
      // 4be9: dup
      // 4bea: sipush 962
      // 4bed: ldc2_w -5.45287716696021E84
      // 4bf0: dastore
      // 4bf1: dup
      // 4bf2: sipush 963
      // 4bf5: ldc2_w -1.7514953095665195E85
      // 4bf8: dastore
      // 4bf9: dup
      // 4bfa: sipush 964
      // 4bfd: ldc2_w -5.0706081370522526E85
      // 4c00: dastore
      // 4c01: dup
      // 4c02: sipush 965
      // 4c05: ldc2_w -4.35799392139009E85
      // 4c08: dastore
      // 4c09: dup
      // 4c0a: sipush 966
      // 4c0d: ldc2_w -3.982538093450217E86
      // 4c10: dastore
      // 4c11: dup
      // 4c12: sipush 967
      // 4c15: ldc2_w -1.4591838284752642E87
      // 4c18: dastore
      // 4c19: dup
      // 4c1a: sipush 968
      // 4c1d: ldc2_w 2.5313735821872488E87
      // 4c20: dastore
      // 4c21: dup
      // 4c22: sipush 969
      // 4c25: ldc2_w -3.718501227185903E86
      // 4c28: dastore
      // 4c29: dup
      // 4c2a: sipush 970
      // 4c2d: ldc2_w -1.3907979640327008E88
      // 4c30: dastore
      // 4c31: dup
      // 4c32: sipush 971
      // 4c35: ldc2_w -5.79002114093961E86
      // 4c38: dastore
      // 4c39: dup
      // 4c3a: sipush 972
      // 4c3d: ldc2_w -1.2500675565781447E89
      // 4c40: dastore
      // 4c41: dup
      // 4c42: sipush 973
      // 4c45: ldc2_w 4.8182788286170926E89
      // 4c48: dastore
      // 4c49: dup
      // 4c4a: sipush 974
      // 4c4d: ldc2_w -1.7198866036687559E90
      // 4c50: dastore
      // 4c51: dup
      // 4c52: sipush 975
      // 4c55: ldc2_w -4.690417668647599E88
      // 4c58: dastore
      // 4c59: dup
      // 4c5a: sipush 976
      // 4c5d: ldc2_w 1.3020631859056421E91
      // 4c60: dastore
      // 4c61: dup
      // 4c62: sipush 977
      // 4c65: ldc2_w -1.3850458263351744E91
      // 4c68: dastore
      // 4c69: dup
      // 4c6a: sipush 978
      // 4c6d: ldc2_w 4.87301010703588E91
      // 4c70: dastore
      // 4c71: dup
      // 4c72: sipush 979
      // 4c75: ldc2_w -1.695546877943826E92
      // 4c78: dastore
      // 4c79: dup
      // 4c7a: sipush 980
      // 4c7d: ldc2_w -1.6353756659909833E92
      // 4c80: dastore
      // 4c81: dup
      // 4c82: sipush 981
      // 4c85: ldc2_w -1.5483926773679628E93
      // 4c88: dastore
      // 4c89: dup
      // 4c8a: sipush 982
      // 4c8d: ldc2_w -1.8921091400297595E93
      // 4c90: dastore
      // 4c91: dup
      // 4c92: sipush 983
      // 4c95: ldc2_w -6.183525570536406E93
      // 4c98: dastore
      // 4c99: dup
      // 4c9a: sipush 984
      // 4c9d: ldc2_w -4.987913342551977E93
      // 4ca0: dastore
      // 4ca1: dup
      // 4ca2: sipush 985
      // 4ca5: ldc2_w 1.0186485886120274E93
      // 4ca8: dastore
      // 4ca9: dup
      // 4caa: sipush 986
      // 4cad: ldc2_w -1.5343120819745468E95
      // 4cb0: dastore
      // 4cb1: dup
      // 4cb2: sipush 987
      // 4cb5: ldc2_w -5.262123923229857E95
      // 4cb8: dastore
      // 4cb9: dup
      // 4cba: sipush 988
      // 4cbd: ldc2_w 1.618327917706804E96
      // 4cc0: dastore
      // 4cc1: dup
      // 4cc2: sipush 989
      // 4cc5: ldc2_w -4.135185828158998E96
      // 4cc8: dastore
      // 4cc9: dup
      // 4cca: sipush 990
      // 4ccd: ldc2_w -8.016793741945299E96
      // 4cd0: dastore
      // 4cd1: dup
      // 4cd2: sipush 991
      // 4cd5: ldc2_w -3.0399439534134115E97
      // 4cd8: dastore
      // 4cd9: dup
      // 4cda: sipush 992
      // 4cdd: ldc2_w -1.2319346292749103E98
      // 4ce0: dastore
      // 4ce1: dup
      // 4ce2: sipush 993
      // 4ce5: ldc2_w 7.536337311795176E97
      // 4ce8: dastore
      // 4ce9: dup
      // 4cea: sipush 994
      // 4ced: ldc2_w -3.577715974851322E98
      // 4cf0: dastore
      // 4cf1: dup
      // 4cf2: sipush 995
      // 4cf5: ldc2_w 2.0521614818695524E99
      // 4cf8: dastore
      // 4cf9: dup
      // 4cfa: sipush 996
      // 4cfd: ldc2_w 1.2627736197958951E98
      // 4d00: dastore
      // 4d01: dup
      // 4d02: sipush 997
      // 4d05: ldc2_w -5.206910481915062E99
      // 4d08: dastore
      // 4d09: dup
      // 4d0a: sipush 998
      // 4d0d: ldc2_w 3.0974593993948837E100
      // 4d10: dastore
      // 4d11: dup
      // 4d12: sipush 999
      // 4d15: ldc2_w -9.522726334561169E100
      // 4d18: dastore
      // 4d19: dup
      // 4d1a: sipush 1000
      // 4d1d: ldc2_w -1.1909272509710985E100
      // 4d20: dastore
      // 4d21: dup
      // 4d22: sipush 1001
      // 4d25: ldc2_w -5.056512677995137E101
      // 4d28: dastore
      // 4d29: dup
      // 4d2a: sipush 1002
      // 4d2d: ldc2_w 2.0902045062932175E102
      // 4d30: dastore
      // 4d31: dup
      // 4d32: sipush 1003
      // 4d35: ldc2_w 6.243669516810509E102
      // 4d38: dastore
      // 4d39: dup
      // 4d3a: sipush 1004
      // 4d3d: ldc2_w -1.7375090618655787E103
      // 4d40: dastore
      // 4d41: dup
      // 4d42: sipush 1005
      // 4d45: ldc2_w -2.5445477450140954E103
      // 4d48: dastore
      // 4d49: dup
      // 4d4a: sipush 1006
      // 4d4d: ldc2_w 3.619891246849381E103
      // 4d50: dastore
      // 4d51: dup
      // 4d52: sipush 1007
      // 4d55: ldc2_w 8.90737333900943E103
      // 4d58: dastore
      // 4d59: dup
      // 4d5a: sipush 1008
      // 4d5d: ldc2_w -2.7897360297480367E104
      // 4d60: dastore
      // 4d61: dup
      // 4d62: sipush 1009
      // 4d65: ldc2_w 1.3725786770437066E105
      // 4d68: dastore
      // 4d69: dup
      // 4d6a: sipush 1010
      // 4d6d: ldc2_w -8.316530604593264E105
      // 4d70: dastore
      // 4d71: dup
      // 4d72: sipush 1011
      // 4d75: ldc2_w -6.054541568735673E105
      // 4d78: dastore
      // 4d79: dup
      // 4d7a: sipush 1012
      // 4d7d: ldc2_w 7.523374196797555E105
      // 4d80: dastore
      // 4d81: dup
      // 4d82: sipush 1013
      // 4d85: ldc2_w 1.1475955030427985E107
      // 4d88: dastore
      // 4d89: dup
      // 4d8a: sipush 1014
      // 4d8d: ldc2_w 1.5260756679495707E107
      // 4d90: dastore
      // 4d91: dup
      // 4d92: sipush 1015
      // 4d95: ldc2_w 7.370294848920685E107
      // 4d98: dastore
      // 4d99: dup
      // 4d9a: sipush 1016
      // 4d9d: ldc2_w 1.3608995799112174E108
      // 4da0: dastore
      // 4da1: dup
      // 4da2: sipush 1017
      // 4da5: ldc2_w 1.0700758858011432E108
      // 4da8: dastore
      // 4da9: dup
      // 4daa: sipush 1018
      // 4dad: ldc2_w -4.989318918773146E108
      // 4db0: dastore
      // 4db1: dup
      // 4db2: sipush 1019
      // 4db5: ldc2_w -1.6629755787634093E108
      // 4db8: dastore
      // 4db9: dup
      // 4dba: sipush 1020
      // 4dbd: ldc2_w 7.635999584053557E109
      // 4dc0: dastore
      // 4dc1: dup
      // 4dc2: sipush 1021
      // 4dc5: ldc2_w 1.892621828736983E109
      // 4dc8: dastore
      // 4dc9: dup
      // 4dca: sipush 1022
      // 4dcd: ldc2_w -6.793094743406533E110
      // 4dd0: dastore
      // 4dd1: dup
      // 4dd2: sipush 1023
      // 4dd5: ldc2_w -8.160628910742724E110
      // 4dd8: dastore
      // 4dd9: dup
      // 4dda: sipush 1024
      // 4ddd: ldc2_w -7.724219106106896E111
      // 4de0: dastore
      // 4de1: dup
      // 4de2: sipush 1025
      // 4de5: ldc2_w -1.6059226011778748E112
      // 4de8: dastore
      // 4de9: dup
      // 4dea: sipush 1026
      // 4ded: ldc2_w -1.5277127454062126E112
      // 4df0: dastore
      // 4df1: dup
      // 4df2: sipush 1027
      // 4df5: ldc2_w 3.911086668967361E112
      // 4df8: dastore
      // 4df9: dup
      // 4dfa: sipush 1028
      // 4dfd: ldc2_w 3.529920406834134E113
      // 4e00: dastore
      // 4e01: dup
      // 4e02: sipush 1029
      // 4e05: ldc2_w -4.3991443996021166E113
      // 4e08: dastore
      // 4e09: dup
      // 4e0a: sipush 1030
      // 4e0d: ldc2_w -1.2631909085915044E114
      // 4e10: dastore
      // 4e11: dup
      // 4e12: sipush 1031
      // 4e15: ldc2_w 3.8656278695544835E114
      // 4e18: dastore
      // 4e19: dup
      // 4e1a: sipush 1032
      // 4e1d: ldc2_w 1.71845288713123E115
      // 4e20: dastore
      // 4e21: dup
      // 4e22: sipush 1033
      // 4e25: ldc2_w 3.7660598745907915E115
      // 4e28: dastore
      // 4e29: dup
      // 4e2a: sipush 1034
      // 4e2d: ldc2_w -4.048086182363988E115
      // 4e30: dastore
      // 4e31: dup
      // 4e32: sipush 1035
      // 4e35: ldc2_w 2.3093822298965837E116
      // 4e38: dastore
      // 4e39: dup
      // 4e3a: sipush 1036
      // 4e3d: ldc2_w -9.684925795536813E116
      // 4e40: dastore
      // 4e41: dup
      // 4e42: sipush 1037
      // 4e45: ldc2_w -3.137992585221854E117
      // 4e48: dastore
      // 4e49: dup
      // 4e4a: sipush 1038
      // 4e4d: ldc2_w -5.637415935329794E117
      // 4e50: dastore
      // 4e51: dup
      // 4e52: sipush 1039
      // 4e55: ldc2_w -1.5536658521931418E118
      // 4e58: dastore
      // 4e59: dup
      // 4e5a: sipush 1040
      // 4e5d: ldc2_w -6.336314643222911E118
      // 4e60: dastore
      // 4e61: dup
      // 4e62: sipush 1041
      // 4e65: ldc2_w 8.550658957115427E118
      // 4e68: dastore
      // 4e69: dup
      // 4e6a: sipush 1042
      // 4e6d: ldc2_w -5.591880480212007E119
      // 4e70: dastore
      // 4e71: dup
      // 4e72: sipush 1043
      // 4e75: ldc2_w 2.4137404318673354E119
      // 4e78: dastore
      // 4e79: dup
      // 4e7a: sipush 1044
      // 4e7d: ldc2_w -2.631656656397244E120
      // 4e80: dastore
      // 4e81: dup
      // 4e82: sipush 1045
      // 4e85: ldc2_w -7.653117429165879E119
      // 4e88: dastore
      // 4e89: dup
      // 4e8a: sipush 1046
      // 4e8d: ldc2_w -4.073965591445897E121
      // 4e90: dastore
      // 4e91: dup
      // 4e92: sipush 1047
      // 4e95: ldc2_w 3.634781057940233E121
      // 4e98: dastore
      // 4e99: dup
      // 4e9a: sipush 1048
      // 4e9d: ldc2_w 4.537273754534966E121
      // 4ea0: dastore
      // 4ea1: dup
      // 4ea2: sipush 1049
      // 4ea5: ldc2_w -2.5138919966097735E122
      // 4ea8: dastore
      // 4ea9: dup
      // 4eaa: sipush 1050
      // 4ead: ldc2_w -1.0292817180691822E123
      // 4eb0: dastore
      // 4eb1: dup
      // 4eb2: sipush 1051
      // 4eb5: ldc2_w -1.4265564976097062E122
      // 4eb8: dastore
      // 4eb9: dup
      // 4eba: sipush 1052
      // 4ebd: ldc2_w 6.000235114895513E123
      // 4ec0: dastore
      // 4ec1: dup
      // 4ec2: sipush 1053
      // 4ec5: ldc2_w 4.186590347846346E124
      // 4ec8: dastore
      // 4ec9: dup
      // 4eca: sipush 1054
      // 4ecd: ldc2_w -1.8950538406321535E124
      // 4ed0: dastore
      // 4ed1: dup
      // 4ed2: sipush 1055
      // 4ed5: ldc2_w 7.716762345695022E124
      // 4ed8: dastore
      // 4ed9: dup
      // 4eda: sipush 1056
      // 4edd: ldc2_w -4.443798187035849E125
      // 4ee0: dastore
      // 4ee1: dup
      // 4ee2: sipush 1057
      // 4ee5: ldc2_w -2.268994961992292E125
      // 4ee8: dastore
      // 4ee9: dup
      // 4eea: sipush 1058
      // 4eed: ldc2_w -2.8169291774231604E126
      // 4ef0: dastore
      // 4ef1: dup
      // 4ef2: sipush 1059
      // 4ef5: ldc2_w -2.749127978087685E126
      // 4ef8: dastore
      // 4ef9: dup
      // 4efa: sipush 1060
      // 4efd: ldc2_w -2.2929764629585683E126
      // 4f00: dastore
      // 4f01: dup
      // 4f02: sipush 1061
      // 4f05: ldc2_w -7.369842361872221E127
      // 4f08: dastore
      // 4f09: dup
      // 4f0a: sipush 1062
      // 4f0d: ldc2_w 2.81312841469177E128
      // 4f10: dastore
      // 4f11: dup
      // 4f12: sipush 1063
      // 4f15: ldc2_w 2.7856896414497757E128
      // 4f18: dastore
      // 4f19: dup
      // 4f1a: sipush 1064
      // 4f1d: ldc2_w -3.096733638475319E128
      // 4f20: dastore
      // 4f21: dup
      // 4f22: sipush 1065
      // 4f25: ldc2_w -5.4199510725063615E129
      // 4f28: dastore
      // 4f29: dup
      // 4f2a: sipush 1066
      // 4f2d: ldc2_w -7.315860999413894E129
      // 4f30: dastore
      // 4f31: dup
      // 4f32: sipush 1067
      // 4f35: ldc2_w 3.6424644535156437E130
      // 4f38: dastore
      // 4f39: dup
      // 4f3a: sipush 1068
      // 4f3d: ldc2_w -7.886250961456327E130
      // 4f40: dastore
      // 4f41: dup
      // 4f42: sipush 1069
      // 4f45: ldc2_w 5.289988151341401E130
      // 4f48: dastore
      // 4f49: dup
      // 4f4a: sipush 1070
      // 4f4d: ldc2_w 2.7758613753516344E131
      // 4f50: dastore
      // 4f51: dup
      // 4f52: sipush 1071
      // 4f55: ldc2_w -2.738246981762776E132
      // 4f58: dastore
      // 4f59: dup
      // 4f5a: sipush 1072
      // 4f5d: ldc2_w -2.2667181460478093E132
      // 4f60: dastore
      // 4f61: dup
      // 4f62: sipush 1073
      // 4f65: ldc2_w -3.614672661225457E131
      // 4f68: dastore
      // 4f69: dup
      // 4f6a: sipush 1074
      // 4f6d: ldc2_w 2.325337720526947E133
      // 4f70: dastore
      // 4f71: dup
      // 4f72: sipush 1075
      // 4f75: ldc2_w 4.16603235883392E133
      // 4f78: dastore
      // 4f79: dup
      // 4f7a: sipush 1076
      // 4f7d: ldc2_w -6.50348962894948E133
      // 4f80: dastore
      // 4f81: dup
      // 4f82: sipush 1077
      // 4f85: ldc2_w 3.851445905038431E134
      // 4f88: dastore
      // 4f89: dup
      // 4f8a: sipush 1078
      // 4f8d: ldc2_w -5.46060534001412E134
      // 4f90: dastore
      // 4f91: dup
      // 4f92: sipush 1079
      // 4f95: ldc2_w 5.4679180659102885E135
      // 4f98: dastore
      // 4f99: dup
      // 4f9a: sipush 1080
      // 4f9d: ldc2_w -3.037477806841494E135
      // 4fa0: dastore
      // 4fa1: dup
      // 4fa2: sipush 1081
      // 4fa5: ldc2_w -3.0417051809209134E136
      // 4fa8: dastore
      // 4fa9: dup
      // 4faa: sipush 1082
      // 4fad: ldc2_w -6.995964550587914E136
      // 4fb0: dastore
      // 4fb1: dup
      // 4fb2: sipush 1083
      // 4fb5: ldc2_w -3.6897084415718804E137
      // 4fb8: dastore
      // 4fb9: dup
      // 4fba: sipush 1084
      // 4fbd: ldc2_w -6.938000231893302E137
      // 4fc0: dastore
      // 4fc1: dup
      // 4fc2: sipush 1085
      // 4fc5: ldc2_w 2.403806217004454E138
      // 4fc8: dastore
      // 4fc9: dup
      // 4fca: sipush 1086
      // 4fcd: ldc2_w -3.4552363953199905E138
      // 4fd0: dastore
      // 4fd1: dup
      // 4fd2: sipush 1087
      // 4fd5: ldc2_w 7.3409917428393E138
      // 4fd8: dastore
      // 4fd9: dup
      // 4fda: sipush 1088
      // 4fdd: ldc2_w -1.7445917446236717E139
      // 4fe0: dastore
      // 4fe1: dup
      // 4fe2: sipush 1089
      // 4fe5: ldc2_w -6.680679913078676E139
      // 4fe8: dastore
      // 4fe9: dup
      // 4fea: sipush 1090
      // 4fed: ldc2_w -8.193572619487537E139
      // 4ff0: dastore
      // 4ff1: dup
      // 4ff2: sipush 1091
      // 4ff5: ldc2_w 5.337290292186291E139
      // 4ff8: dastore
      // 4ff9: dup
      // 4ffa: sipush 1092
      // 4ffd: ldc2_w -3.951314467739045E140
      // 5000: dastore
      // 5001: dup
      // 5002: sipush 1093
      // 5005: ldc2_w -4.4662073456574476E141
      // 5008: dastore
      // 5009: dup
      // 500a: sipush 1094
      // 500d: ldc2_w 6.249381778908997E141
      // 5010: dastore
      // 5011: dup
      // 5012: sipush 1095
      // 5015: ldc2_w -2.928362616578011E142
      // 5018: dastore
      // 5019: dup
      // 501a: sipush 1096
      // 501d: ldc2_w -1.6661676835672304E143
      // 5020: dastore
      // 5021: dup
      // 5022: sipush 1097
      // 5025: ldc2_w -1.974465323891493E143
      // 5028: dastore
      // 5029: dup
      // 502a: sipush 1098
      // 502d: ldc2_w 1.3083870531380308E144
      // 5030: dastore
      // 5031: dup
      // 5032: sipush 1099
      // 5035: ldc2_w -2.382825271750576E144
      // 5038: dastore
      // 5039: dup
      // 503a: sipush 1100
      // 503d: ldc2_w -5.4826958838142734E144
      // 5040: dastore
      // 5041: dup
      // 5042: sipush 1101
      // 5045: ldc2_w 1.5340733916570804E145
      // 5048: dastore
      // 5049: dup
      // 504a: sipush 1102
      // 504d: ldc2_w -3.1327120557842516E145
      // 5050: dastore
      // 5051: dup
      // 5052: sipush 1103
      // 5055: ldc2_w 1.5790297768522832E146
      // 5058: dastore
      // 5059: dup
      // 505a: sipush 1104
      // 505d: ldc2_w 1.1518771984292262E146
      // 5060: dastore
      // 5061: dup
      // 5062: sipush 1105
      // 5065: ldc2_w -4.789917000227385E145
      // 5068: dastore
      // 5069: dup
      // 506a: sipush 1106
      // 506d: ldc2_w -8.689594184775204E146
      // 5070: dastore
      // 5071: dup
      // 5072: sipush 1107
      // 5075: ldc2_w 3.0680417869552433E146
      // 5078: dastore
      // 5079: dup
      // 507a: sipush 1108
      // 507d: ldc2_w 4.877860620031438E147
      // 5080: dastore
      // 5081: dup
      // 5082: sipush 1109
      // 5085: ldc2_w -3.4650891244084597E148
      // 5088: dastore
      // 5089: dup
      // 508a: sipush 1110
      // 508d: ldc2_w 1.8702183451052442E149
      // 5090: dastore
      // 5091: dup
      // 5092: sipush 1111
      // 5095: ldc2_w -3.5727227900139915E148
      // 5098: dastore
      // 5099: dup
      // 509a: sipush 1112
      // 509d: ldc2_w -1.3457821696677932E150
      // 50a0: dastore
      // 50a1: dup
      // 50a2: sipush 1113
      // 50a5: ldc2_w 3.3212950284273017E149
      // 50a8: dastore
      // 50a9: dup
      // 50aa: sipush 1114
      // 50ad: ldc2_w 7.316033240396569E150
      // 50b0: dastore
      // 50b1: dup
      // 50b2: sipush 1115
      // 50b5: ldc2_w -7.187723217018267E150
      // 50b8: dastore
      // 50b9: dup
      // 50ba: sipush 1116
      // 50bd: ldc2_w -8.537194547485455E150
      // 50c0: dastore
      // 50c1: dup
      // 50c2: sipush 1117
      // 50c5: ldc2_w -1.4561530066010593E152
      // 50c8: dastore
      // 50c9: dup
      // 50ca: sipush 1118
      // 50cd: ldc2_w -7.548155147049997E151
      // 50d0: dastore
      // 50d1: dup
      // 50d2: sipush 1119
      // 50d5: ldc2_w 1.0047353208353007E153
      // 50d8: dastore
      // 50d9: dup
      // 50da: sipush 1120
      // 50dd: ldc2_w -1.2489460589853119E153
      // 50e0: dastore
      // 50e1: dup
      // 50e2: sipush 1121
      // 50e5: ldc2_w 4.426120229279107E153
      // 50e8: dastore
      // 50e9: dup
      // 50ea: sipush 1122
      // 50ed: ldc2_w -2.5466223330961086E154
      // 50f0: dastore
      // 50f1: dup
      // 50f2: sipush 1123
      // 50f5: ldc2_w 8.831699889789037E154
      // 50f8: dastore
      // 50f9: dup
      // 50fa: sipush 1124
      // 50fd: ldc2_w -2.0258084311749475E155
      // 5100: dastore
      // 5101: dup
      // 5102: sipush 1125
      // 5105: ldc2_w -5.525009099476396E155
      // 5108: dastore
      // 5109: dup
      // 510a: sipush 1126
      // 510d: ldc2_w -1.0235056525096769E156
      // 5110: dastore
      // 5111: dup
      // 5112: sipush 1127
      // 5115: ldc2_w -4.117971654572494E154
      // 5118: dastore
      // 5119: dup
      // 511a: sipush 1128
      // 511d: ldc2_w -4.7559175309753334E156
      // 5120: dastore
      // 5121: dup
      // 5122: sipush 1129
      // 5125: ldc2_w -1.4656240137098836E157
      // 5128: dastore
      // 5129: dup
      // 512a: sipush 1130
      // 512d: ldc2_w -7.675790582869644E157
      // 5130: dastore
      // 5131: dup
      // 5132: sipush 1131
      // 5135: ldc2_w -1.0126616322947826E158
      // 5138: dastore
      // 5139: dup
      // 513a: sipush 1132
      // 513d: ldc2_w 7.084865265284368E158
      // 5140: dastore
      // 5141: dup
      // 5142: sipush 1133
      // 5145: ldc2_w -9.374695893307895E158
      // 5148: dastore
      // 5149: dup
      // 514a: sipush 1134
      // 514d: ldc2_w 2.05597910889115E159
      // 5150: dastore
      // 5151: dup
      // 5152: sipush 1135
      // 5155: ldc2_w -7.368602086210704E159
      // 5158: dastore
      // 5159: dup
      // 515a: sipush 1136
      // 515d: ldc2_w -1.6167825196198978E160
      // 5160: dastore
      // 5161: dup
      // 5162: sipush 1137
      // 5165: ldc2_w 2.3832096207000712E160
      // 5168: dastore
      // 5169: dup
      // 516a: sipush 1138
      // 516d: ldc2_w 1.3166970112139726E161
      // 5170: dastore
      // 5171: dup
      // 5172: sipush 1139
      // 5175: ldc2_w -6.432337568761393E161
      // 5178: dastore
      // 5179: dup
      // 517a: sipush 1140
      // 517d: ldc2_w 2.9279594746502846E161
      // 5180: dastore
      // 5181: dup
      // 5182: sipush 1141
      // 5185: ldc2_w 4.8926595743317624E162
      // 5188: dastore
      // 5189: dup
      // 518a: sipush 1142
      // 518d: ldc2_w 1.2704793774453618E163
      // 5190: dastore
      // 5191: dup
      // 5192: sipush 1143
      // 5195: ldc2_w -1.1345910784680524E163
      // 5198: dastore
      // 5199: dup
      // 519a: sipush 1144
      // 519d: ldc2_w 7.75933511025868E163
      // 51a0: dastore
      // 51a1: dup
      // 51a2: sipush 1145
      // 51a5: ldc2_w -1.1441115218462356E163
      // 51a8: dastore
      // 51a9: dup
      // 51aa: sipush 1146
      // 51ad: ldc2_w 5.162248481759758E164
      // 51b0: dastore
      // 51b1: dup
      // 51b2: sipush 1147
      // 51b5: ldc2_w 6.362563919556132E164
      // 51b8: dastore
      // 51b9: dup
      // 51ba: sipush 1148
      // 51bd: ldc2_w -2.8362173224732088E165
      // 51c0: dastore
      // 51c1: dup
      // 51c2: sipush 1149
      // 51c5: ldc2_w -4.342161053332263E165
      // 51c8: dastore
      // 51c9: dup
      // 51ca: sipush 1150
      // 51cd: ldc2_w 4.388125271425036E166
      // 51d0: dastore
      // 51d1: dup
      // 51d2: sipush 1151
      // 51d5: ldc2_w -7.049068240916723E166
      // 51d8: dastore
      // 51d9: dup
      // 51da: sipush 1152
      // 51dd: ldc2_w 3.8520227881415595E166
      // 51e0: dastore
      // 51e1: dup
      // 51e2: sipush 1153
      // 51e5: ldc2_w 2.9274120974020826E167
      // 51e8: dastore
      // 51e9: dup
      // 51ea: sipush 1154
      // 51ed: ldc2_w -7.500936767542933E167
      // 51f0: dastore
      // 51f1: dup
      // 51f2: sipush 1155
      // 51f5: ldc2_w -6.540181860667302E168
      // 51f8: dastore
      // 51f9: dup
      // 51fa: sipush 1156
      // 51fd: ldc2_w 4.664436780622191E168
      // 5200: dastore
      // 5201: dup
      // 5202: sipush 1157
      // 5205: ldc2_w -1.436111169285268E169
      // 5208: dastore
      // 5209: dup
      // 520a: sipush 1158
      // 520d: ldc2_w -1.0407581736224179E170
      // 5210: dastore
      // 5211: dup
      // 5212: sipush 1159
      // 5215: ldc2_w -2.7670181051374297E170
      // 5218: dastore
      // 5219: dup
      // 521a: sipush 1160
      // 521d: ldc2_w -6.788169932297778E170
      // 5220: dastore
      // 5221: dup
      // 5222: sipush 1161
      // 5225: ldc2_w 1.6997387217850427E171
      // 5228: dastore
      // 5229: dup
      // 522a: sipush 1162
      // 522d: ldc2_w -1.0965324942770584E171
      // 5230: dastore
      // 5231: dup
      // 5232: sipush 1163
      // 5235: ldc2_w 9.841563119484623E171
      // 5238: dastore
      // 5239: dup
      // 523a: sipush 1164
      // 523d: ldc2_w 3.175748919314254E172
      // 5240: dastore
      // 5241: dup
      // 5242: sipush 1165
      // 5245: ldc2_w 2.9621181706527444E172
      // 5248: dastore
      // 5249: dup
      // 524a: sipush 1166
      // 524d: ldc2_w -3.30101656090905E173
      // 5250: dastore
      // 5251: dup
      // 5252: sipush 1167
      // 5255: ldc2_w -3.791840683760427E173
      // 5258: dastore
      // 5259: dup
      // 525a: sipush 1168
      // 525d: ldc2_w -2.841347842666459E174
      // 5260: dastore
      // 5261: dup
      // 5262: sipush 1169
      // 5265: ldc2_w -7.836327226971707E174
      // 5268: dastore
      // 5269: dup
      // 526a: sipush 1170
      // 526d: ldc2_w 9.650358667643114E174
      // 5270: dastore
      // 5271: dup
      // 5272: sipush 1171
      // 5275: ldc2_w 5.9994277301267294E175
      // 5278: dastore
      // 5279: dup
      // 527a: sipush 1172
      // 527d: ldc2_w -6.0490084078440215E175
      // 5280: dastore
      // 5281: dup
      // 5282: sipush 1173
      // 5285: ldc2_w -2.8964095485948707E176
      // 5288: dastore
      // 5289: dup
      // 528a: sipush 1174
      // 528d: ldc2_w 9.916187343252014E175
      // 5290: dastore
      // 5291: dup
      // 5292: sipush 1175
      // 5295: ldc2_w 2.7535627955313556E176
      // 5298: dastore
      // 5299: dup
      // 529a: sipush 1176
      // 529d: ldc2_w 3.886891475472745E177
      // 52a0: dastore
      // 52a1: dup
      // 52a2: sipush 1177
      // 52a5: ldc2_w 3.1962472803616787E178
      // 52a8: dastore
      // 52a9: dup
      // 52aa: sipush 1178
      // 52ad: ldc2_w -5.50599549115449E178
      // 52b0: dastore
      // 52b1: dup
      // 52b2: sipush 1179
      // 52b5: ldc2_w 5.672812341879918E178
      // 52b8: dastore
      // 52b9: dup
      // 52ba: sipush 1180
      // 52bd: ldc2_w -3.295268490032475E179
      // 52c0: dastore
      // 52c1: dup
      // 52c2: sipush 1181
      // 52c5: ldc2_w 9.761163062156018E179
      // 52c8: dastore
      // 52c9: dup
      // 52ca: sipush 1182
      // 52cd: ldc2_w 3.107837179570674E180
      // 52d0: dastore
      // 52d1: dup
      // 52d2: sipush 1183
      // 52d5: ldc2_w 3.3894811576571423E179
      // 52d8: dastore
      // 52d9: dup
      // 52da: sipush 1184
      // 52dd: ldc2_w -5.235397688850367E180
      // 52e0: dastore
      // 52e1: dup
      // 52e2: sipush 1185
      // 52e5: ldc2_w -5.004237248003625E181
      // 52e8: dastore
      // 52e9: dup
      // 52ea: sipush 1186
      // 52ed: ldc2_w -1.7544995191195304E182
      // 52f0: dastore
      // 52f1: dup
      // 52f2: sipush 1187
      // 52f5: ldc2_w 2.645622651144787E182
      // 52f8: dastore
      // 52f9: dup
      // 52fa: sipush 1188
      // 52fd: ldc2_w -3.459885432869825E182
      // 5300: dastore
      // 5301: dup
      // 5302: sipush 1189
      // 5305: ldc2_w -4.0361435606199565E183
      // 5308: dastore
      // 5309: dup
      // 530a: sipush 1190
      // 530d: ldc2_w -1.8382923511801317E183
      // 5310: dastore
      // 5311: dup
      // 5312: sipush 1191
      // 5315: ldc2_w -1.7332235571505177E184
      // 5318: dastore
      // 5319: dup
      // 531a: sipush 1192
      // 531d: ldc2_w 2.847653455671381E184
      // 5320: dastore
      // 5321: dup
      // 5322: sipush 1193
      // 5325: ldc2_w 1.7991060813894693E185
      // 5328: dastore
      // 5329: dup
      // 532a: sipush 1194
      // 532d: ldc2_w -2.0937429891059164E185
      // 5330: dastore
      // 5331: dup
      // 5332: sipush 1195
      // 5335: ldc2_w 5.744446753652847E185
      // 5338: dastore
      // 5339: dup
      // 533a: sipush 1196
      // 533d: ldc2_w -2.1349396267483754E184
      // 5340: dastore
      // 5341: dup
      // 5342: sipush 1197
      // 5345: ldc2_w -1.2542332720182776E186
      // 5348: dastore
      // 5349: dup
      // 534a: sipush 1198
      // 534d: ldc2_w 3.3730714236579374E186
      // 5350: dastore
      // 5351: dup
      // 5352: sipush 1199
      // 5355: ldc2_w -5.923734606208998E187
      // 5358: dastore
      // 5359: dup
      // 535a: sipush 1200
      // 535d: ldc2_w 2.24669039465627E188
      // 5360: dastore
      // 5361: dup
      // 5362: sipush 1201
      // 5365: ldc2_w -1.2588742703536392E188
      // 5368: dastore
      // 5369: dup
      // 536a: sipush 1202
      // 536d: ldc2_w 1.474522484905093E189
      // 5370: dastore
      // 5371: dup
      // 5372: sipush 1203
      // 5375: ldc2_w -2.4006971787803736E189
      // 5378: dastore
      // 5379: dup
      // 537a: sipush 1204
      // 537d: ldc2_w -3.52597540499141E189
      // 5380: dastore
      // 5381: dup
      // 5382: sipush 1205
      // 5385: ldc2_w 2.6676722922838097E190
      // 5388: dastore
      // 5389: dup
      // 538a: sipush 1206
      // 538d: ldc2_w 5.27579825970359E190
      // 5390: dastore
      // 5391: dup
      // 5392: sipush 1207
      // 5395: ldc2_w 2.1360492104281465E191
      // 5398: dastore
      // 5399: dup
      // 539a: sipush 1208
      // 539d: ldc2_w 1.9442210982008953E191
      // 53a0: dastore
      // 53a1: dup
      // 53a2: sipush 1209
      // 53a5: ldc2_w -1.4691239161932232E190
      // 53a8: dastore
      // 53a9: dup
      // 53aa: sipush 1210
      // 53ad: ldc2_w 3.8218180377739526E192
      // 53b0: dastore
      // 53b1: dup
      // 53b2: sipush 1211
      // 53b5: ldc2_w 1.9722862688653467E192
      // 53b8: dastore
      // 53b9: dup
      // 53ba: sipush 1212
      // 53bd: ldc2_w 3.047601928063002E193
      // 53c0: dastore
      // 53c1: dup
      // 53c2: sipush 1213
      // 53c5: ldc2_w 1.6747356805175311E193
      // 53c8: dastore
      // 53c9: dup
      // 53ca: sipush 1214
      // 53cd: ldc2_w 7.710512446969693E192
      // 53d0: dastore
      // 53d1: dup
      // 53d2: sipush 1215
      // 53d5: ldc2_w 1.7780021277684035E194
      // 53d8: dastore
      // 53d9: dup
      // 53da: sipush 1216
      // 53dd: ldc2_w -1.4015110811648513E195
      // 53e0: dastore
      // 53e1: dup
      // 53e2: sipush 1217
      // 53e5: ldc2_w 4.0447634595724164E195
      // 53e8: dastore
      // 53e9: dup
      // 53ea: sipush 1218
      // 53ed: ldc2_w 9.023639664212642E195
      // 53f0: dastore
      // 53f1: dup
      // 53f2: sipush 1219
      // 53f5: ldc2_w 1.976868146639626E196
      // 53f8: dastore
      // 53f9: dup
      // 53fa: sipush 1220
      // 53fd: ldc2_w -9.084495133765657E196
      // 5400: dastore
      // 5401: dup
      // 5402: sipush 1221
      // 5405: ldc2_w -1.2023077889892748E196
      // 5408: dastore
      // 5409: dup
      // 540a: sipush 1222
      // 540d: ldc2_w 5.7455368446308694E197
      // 5410: dastore
      // 5411: dup
      // 5412: sipush 1223
      // 5415: ldc2_w -1.7766273910482863E198
      // 5418: dastore
      // 5419: dup
      // 541a: sipush 1224
      // 541d: ldc2_w 3.5590470673352285E198
      // 5420: dastore
      // 5421: dup
      // 5422: sipush 1225
      // 5425: ldc2_w 1.1304970373249033E199
      // 5428: dastore
      // 5429: dup
      // 542a: sipush 1226
      // 542d: ldc2_w 1.6496143246803731E199
      // 5430: dastore
      // 5431: dup
      // 5432: sipush 1227
      // 5435: ldc2_w -2.394588390685223E199
      // 5438: dastore
      // 5439: dup
      // 543a: sipush 1228
      // 543d: ldc2_w -1.4677321100833294E199
      // 5440: dastore
      // 5441: dup
      // 5442: sipush 1229
      // 5445: ldc2_w -1.1843870433971731E201
      // 5448: dastore
      // 5449: dup
      // 544a: sipush 1230
      // 544d: ldc2_w -1.8853982316037226E201
      // 5450: dastore
      // 5451: dup
      // 5452: sipush 1231
      // 5455: ldc2_w 2.8829871423339434E201
      // 5458: dastore
      // 5459: dup
      // 545a: sipush 1232
      // 545d: ldc2_w 5.369687677705385E200
      // 5460: dastore
      // 5461: dup
      // 5462: sipush 1233
      // 5465: ldc2_w 1.8356062677502141E202
      // 5468: dastore
      // 5469: dup
      // 546a: sipush 1234
      // 546d: ldc2_w -1.5544655377217875E203
      // 5470: dastore
      // 5471: dup
      // 5472: sipush 1235
      // 5475: ldc2_w 2.955364187248884E203
      // 5478: dastore
      // 5479: dup
      // 547a: sipush 1236
      // 547d: ldc2_w -2.7651059253677425E203
      // 5480: dastore
      // 5481: dup
      // 5482: sipush 1237
      // 5485: ldc2_w 9.903174064539538E203
      // 5488: dastore
      // 5489: dup
      // 548a: sipush 1238
      // 548d: ldc2_w -3.284204788892967E204
      // 5490: dastore
      // 5491: dup
      // 5492: sipush 1239
      // 5495: ldc2_w -1.5843229740595697E205
      // 5498: dastore
      // 5499: dup
      // 549a: sipush 1240
      // 549d: ldc2_w 5.333371443528904E204
      // 54a0: dastore
      // 54a1: dup
      // 54a2: sipush 1241
      // 54a5: ldc2_w 1.2781631468016048E205
      // 54a8: dastore
      // 54a9: dup
      // 54aa: sipush 1242
      // 54ad: ldc2_w 3.2188292385399854E205
      // 54b0: dastore
      // 54b1: dup
      // 54b2: sipush 1243
      // 54b5: ldc2_w -6.619064395428225E206
      // 54b8: dastore
      // 54b9: dup
      // 54ba: sipush 1244
      // 54bd: ldc2_w 1.291561142865928E207
      // 54c0: dastore
      // 54c1: dup
      // 54c2: sipush 1245
      // 54c5: ldc2_w 1.3142988156905172E207
      // 54c8: dastore
      // 54c9: dup
      // 54ca: sipush 1246
      // 54cd: ldc2_w -1.3841980097978606E208
      // 54d0: dastore
      // 54d1: dup
      // 54d2: sipush 1247
      // 54d5: ldc2_w 6.380177790989479E207
      // 54d8: dastore
      // 54d9: dup
      // 54da: sipush 1248
      // 54dd: ldc2_w 1.0386032577072378E209
      // 54e0: dastore
      // 54e1: dup
      // 54e2: sipush 1249
      // 54e5: ldc2_w 2.7681631086098026E209
      // 54e8: dastore
      // 54e9: dup
      // 54ea: sipush 1250
      // 54ed: ldc2_w -9.053874899534375E209
      // 54f0: dastore
      // 54f1: dup
      // 54f2: sipush 1251
      // 54f5: ldc2_w 1.2424707839848734E210
      // 54f8: dastore
      // 54f9: dup
      // 54fa: sipush 1252
      // 54fd: ldc2_w 1.045546633850141E211
      // 5500: dastore
      // 5501: dup
      // 5502: sipush 1253
      // 5505: ldc2_w -1.2448938139338362E211
      // 5508: dastore
      // 5509: dup
      // 550a: sipush 1254
      // 550d: ldc2_w 7.221902646057552E211
      // 5510: dastore
      // 5511: dup
      // 5512: sipush 1255
      // 5515: ldc2_w 6.651345415954053E211
      // 5518: dastore
      // 5519: dup
      // 551a: sipush 1256
      // 551d: ldc2_w -5.8180712702152444E212
      // 5520: dastore
      // 5521: dup
      // 5522: sipush 1257
      // 5525: ldc2_w 5.275183961165903E212
      // 5528: dastore
      // 5529: dup
      // 552a: sipush 1258
      // 552d: ldc2_w 5.092753117288608E212
      // 5530: dastore
      // 5531: dup
      // 5532: sipush 1259
      // 5535: ldc2_w -2.437796532151255E213
      // 5538: dastore
      // 5539: dup
      // 553a: sipush 1260
      // 553d: ldc2_w 1.3480763914637323E214
      // 5540: dastore
      // 5541: dup
      // 5542: sipush 1261
      // 5545: ldc2_w 5.619995933180841E214
      // 5548: dastore
      // 5549: dup
      // 554a: sipush 1262
      // 554d: ldc2_w 2.547000388735681E214
      // 5550: dastore
      // 5551: dup
      // 5552: sipush 1263
      // 5555: ldc2_w 4.817319356453926E214
      // 5558: dastore
      // 5559: dup
      // 555a: sipush 1264
      // 555d: ldc2_w -7.897146442236022E215
      // 5560: dastore
      // 5561: dup
      // 5562: sipush 1265
      // 5565: ldc2_w -7.93844120619577E215
      // 5568: dastore
      // 5569: dup
      // 556a: sipush 1266
      // 556d: ldc2_w -4.9489938500591624E216
      // 5570: dastore
      // 5571: dup
      // 5572: sipush 1267
      // 5575: ldc2_w -2.862720607805682E216
      // 5578: dastore
      // 5579: dup
      // 557a: sipush 1268
      // 557d: ldc2_w -2.9275804461593914E217
      // 5580: dastore
      // 5581: dup
      // 5582: sipush 1269
      // 5585: ldc2_w -3.411186219855533E217
      // 5588: dastore
      // 5589: dup
      // 558a: sipush 1270
      // 558d: ldc2_w -2.0110092718356274E218
      // 5590: dastore
      // 5591: dup
      // 5592: sipush 1271
      // 5595: ldc2_w -8.472642266772353E218
      // 5598: dastore
      // 5599: dup
      // 559a: sipush 1272
      // 559d: ldc2_w -4.357990742470246E217
      // 55a0: dastore
      // 55a1: dup
      // 55a2: sipush 1273
      // 55a5: ldc2_w 4.793444363780116E219
      // 55a8: dastore
      // 55a9: dup
      // 55aa: sipush 1274
      // 55ad: ldc2_w 1.6544084224626834E220
      // 55b0: dastore
      // 55b1: dup
      // 55b2: sipush 1275
      // 55b5: ldc2_w -6.017988576347111E220
      // 55b8: dastore
      // 55b9: dup
      // 55ba: sipush 1276
      // 55bd: ldc2_w -3.580397221598409E220
      // 55c0: dastore
      // 55c1: dup
      // 55c2: sipush 1277
      // 55c5: ldc2_w -4.7208848667217906E221
      // 55c8: dastore
      // 55c9: dup
      // 55ca: sipush 1278
      // 55cd: ldc2_w -7.724899660259369E221
      // 55d0: dastore
      // 55d1: dup
      // 55d2: sipush 1279
      // 55d5: ldc2_w -2.4459728627968634E222
      // 55d8: dastore
      // 55d9: dup
      // 55da: sipush 1280
      // 55dd: ldc2_w 3.667348665023154E221
      // 55e0: dastore
      // 55e1: dup
      // 55e2: sipush 1281
      // 55e5: ldc2_w 4.544122762558404E223
      // 55e8: dastore
      // 55e9: dup
      // 55ea: sipush 1282
      // 55ed: ldc2_w -4.0573420909530794E223
      // 55f0: dastore
      // 55f1: dup
      // 55f2: sipush 1283
      // 55f5: ldc2_w -3.2552002992257195E223
      // 55f8: dastore
      // 55f9: dup
      // 55fa: sipush 1284
      // 55fd: ldc2_w -6.488296536838142E224
      // 5600: dastore
      // 5601: dup
      // 5602: sipush 1285
      // 5605: ldc2_w 1.7544839352461719E224
      // 5608: dastore
      // 5609: dup
      // 560a: sipush 1286
      // 560d: ldc2_w -4.0873400635183405E225
      // 5610: dastore
      // 5611: dup
      // 5612: sipush 1287
      // 5615: ldc2_w -8.833499967268279E225
      // 5618: dastore
      // 5619: dup
      // 561a: sipush 1288
      // 561d: ldc2_w -1.0953484767704112E226
      // 5620: dastore
      // 5621: dup
      // 5622: sipush 1289
      // 5625: ldc2_w -8.56825295972308E226
      // 5628: dastore
      // 5629: dup
      // 562a: sipush 1290
      // 562d: ldc2_w -1.8097633115378247E227
      // 5630: dastore
      // 5631: dup
      // 5632: sipush 1291
      // 5635: ldc2_w -6.171564449018882E227
      // 5638: dastore
      // 5639: dup
      // 563a: sipush 1292
      // 563d: ldc2_w -4.351843341274115E227
      // 5640: dastore
      // 5641: dup
      // 5642: sipush 1293
      // 5645: ldc2_w 2.8032429752543687E228
      // 5648: dastore
      // 5649: dup
      // 564a: sipush 1294
      // 564d: ldc2_w -1.0065901934522996E229
      // 5650: dastore
      // 5651: dup
      // 5652: sipush 1295
      // 5655: ldc2_w 9.863720960170636E228
      // 5658: dastore
      // 5659: dup
      // 565a: sipush 1296
      // 565d: ldc2_w -9.481088691357648E229
      // 5660: dastore
      // 5661: dup
      // 5662: sipush 1297
      // 5665: ldc2_w -1.6843492713373762E229
      // 5668: dastore
      // 5669: dup
      // 566a: sipush 1298
      // 566d: ldc2_w -1.3282890219894906E230
      // 5670: dastore
      // 5671: dup
      // 5672: sipush 1299
      // 5675: ldc2_w 6.883577595238845E230
      // 5678: dastore
      // 5679: dup
      // 567a: sipush 1300
      // 567d: ldc2_w -1.153577281189635E231
      // 5680: dastore
      // 5681: dup
      // 5682: sipush 1301
      // 5685: ldc2_w -8.009548754642203E231
      // 5688: dastore
      // 5689: dup
      // 568a: sipush 1302
      // 568d: ldc2_w -4.722612904888278E232
      // 5690: dastore
      // 5691: dup
      // 5692: sipush 1303
      // 5695: ldc2_w -4.768909872963015E232
      // 5698: dastore
      // 5699: dup
      // 569a: sipush 1304
      // 569d: ldc2_w 3.2542391242036633E233
      // 56a0: dastore
      // 56a1: dup
      // 56a2: sipush 1305
      // 56a5: ldc2_w 6.513425781583774E233
      // 56a8: dastore
      // 56a9: dup
      // 56aa: sipush 1306
      // 56ad: ldc2_w -1.8889614379831606E233
      // 56b0: dastore
      // 56b1: dup
      // 56b2: sipush 1307
      // 56b5: ldc2_w -2.227647301474917E234
      // 56b8: dastore
      // 56b9: dup
      // 56ba: sipush 1308
      // 56bd: ldc2_w -4.7971208532986115E234
      // 56c0: dastore
      // 56c1: dup
      // 56c2: sipush 1309
      // 56c5: ldc2_w 6.693500938105557E234
      // 56c8: dastore
      // 56c9: dup
      // 56ca: sipush 1310
      // 56cd: ldc2_w -6.587776621471115E234
      // 56d0: dastore
      // 56d1: dup
      // 56d2: sipush 1311
      // 56d5: ldc2_w 3.0099905634916516E236
      // 56d8: dastore
      // 56d9: dup
      // 56da: sipush 1312
      // 56dd: ldc2_w -4.6694407626686244E235
      // 56e0: dastore
      // 56e1: dup
      // 56e2: sipush 1313
      // 56e5: ldc2_w 2.965546585110978E236
      // 56e8: dastore
      // 56e9: dup
      // 56ea: sipush 1314
      // 56ed: ldc2_w 5.771457643937893E237
      // 56f0: dastore
      // 56f1: dup
      // 56f2: sipush 1315
      // 56f5: ldc2_w -9.029878114318277E237
      // 56f8: dastore
      // 56f9: dup
      // 56fa: sipush 1316
      // 56fd: ldc2_w 8.169926810324408E237
      // 5700: dastore
      // 5701: dup
      // 5702: sipush 1317
      // 5705: ldc2_w -1.779945804977441E239
      // 5708: dastore
      // 5709: dup
      // 570a: sipush 1318
      // 570d: ldc2_w 4.1218749988429474E239
      // 5710: dastore
      // 5711: dup
      // 5712: sipush 1319
      // 5715: ldc2_w 7.201319954099161E239
      // 5718: dastore
      // 5719: dup
      // 571a: sipush 1320
      // 571d: ldc2_w -1.530845432304069E240
      // 5720: dastore
      // 5721: dup
      // 5722: sipush 1321
      // 5725: ldc2_w -3.861762510530086E240
      // 5728: dastore
      // 5729: dup
      // 572a: sipush 1322
      // 572d: ldc2_w -2.4090696463777446E241
      // 5730: dastore
      // 5731: dup
      // 5732: sipush 1323
      // 5735: ldc2_w -1.8196842273916379E241
      // 5738: dastore
      // 5739: dup
      // 573a: sipush 1324
      // 573d: ldc2_w -1.7959243076374794E242
      // 5740: dastore
      // 5741: dup
      // 5742: sipush 1325
      // 5745: ldc2_w -3.7257346819782323E242
      // 5748: dastore
      // 5749: dup
      // 574a: sipush 1326
      // 574d: ldc2_w 3.413310324247329E242
      // 5750: dastore
      // 5751: dup
      // 5752: sipush 1327
      // 5755: ldc2_w -2.0406580894051073E243
      // 5758: dastore
      // 5759: dup
      // 575a: sipush 1328
      // 575d: ldc2_w -1.5335923091350053E243
      // 5760: dastore
      // 5761: dup
      // 5762: sipush 1329
      // 5765: ldc2_w -1.056727406551016E244
      // 5768: dastore
      // 5769: dup
      // 576a: sipush 1330
      // 576d: ldc2_w -4.6753408714233723E244
      // 5770: dastore
      // 5771: dup
      // 5772: sipush 1331
      // 5775: ldc2_w -2.0697130057384643E245
      // 5778: dastore
      // 5779: dup
      // 577a: sipush 1332
      // 577d: ldc2_w -1.0356006160554071E245
      // 5780: dastore
      // 5781: dup
      // 5782: sipush 1333
      // 5785: ldc2_w 1.1339195187304043E246
      // 5788: dastore
      // 5789: dup
      // 578a: sipush 1334
      // 578d: ldc2_w 1.792783182582235E246
      // 5790: dastore
      // 5791: dup
      // 5792: sipush 1335
      // 5795: ldc2_w 9.599214853681978E245
      // 5798: dastore
      // 5799: dup
      // 579a: sipush 1336
      // 579d: ldc2_w 1.5367645598839362E247
      // 57a0: dastore
      // 57a1: dup
      // 57a2: sipush 1337
      // 57a5: ldc2_w 2.934570385464815E247
      // 57a8: dastore
      // 57a9: dup
      // 57aa: sipush 1338
      // 57ad: ldc2_w -1.6411525886171892E248
      // 57b0: dastore
      // 57b1: dup
      // 57b2: sipush 1339
      // 57b5: ldc2_w 2.2638862982382794E248
      // 57b8: dastore
      // 57b9: dup
      // 57ba: sipush 1340
      // 57bd: ldc2_w -1.2268014119628852E249
      // 57c0: dastore
      // 57c1: dup
      // 57c2: sipush 1341
      // 57c5: ldc2_w 4.737693450915584E247
      // 57c8: dastore
      // 57c9: dup
      // 57ca: sipush 1342
      // 57cd: ldc2_w 6.3818993714899675E249
      // 57d0: dastore
      // 57d1: dup
      // 57d2: sipush 1343
      // 57d5: ldc2_w 1.2639113706171572E250
      // 57d8: dastore
      // 57d9: dup
      // 57da: sipush 1344
      // 57dd: ldc2_w -4.011320021817099E249
      // 57e0: dastore
      // 57e1: dup
      // 57e2: sipush 1345
      // 57e5: ldc2_w -5.2744376732859406E250
      // 57e8: dastore
      // 57e9: dup
      // 57ea: sipush 1346
      // 57ed: ldc2_w -3.732266217624991E251
      // 57f0: dastore
      // 57f1: dup
      // 57f2: sipush 1347
      // 57f5: ldc2_w 1.7591819833844019E252
      // 57f8: dastore
      // 57f9: dup
      // 57fa: sipush 1348
      // 57fd: ldc2_w -3.292458622014749E252
      // 5800: dastore
      // 5801: dup
      // 5802: sipush 1349
      // 5805: ldc2_w -9.161340309319204E252
      // 5808: dastore
      // 5809: dup
      // 580a: sipush 1350
      // 580d: ldc2_w -1.728610646009749E253
      // 5810: dastore
      // 5811: dup
      // 5812: sipush 1351
      // 5815: ldc2_w 1.1698424008604891E254
      // 5818: dastore
      // 5819: dup
      // 581a: sipush 1352
      // 581d: ldc2_w -1.8494343291160577E254
      // 5820: dastore
      // 5821: dup
      // 5822: sipush 1353
      // 5825: ldc2_w 2.0568656302182574E254
      // 5828: dastore
      // 5829: dup
      // 582a: sipush 1354
      // 582d: ldc2_w 1.0537591246531136E255
      // 5830: dastore
      // 5831: dup
      // 5832: sipush 1355
      // 5835: ldc2_w 1.803052068234866E254
      // 5838: dastore
      // 5839: dup
      // 583a: sipush 1356
      // 583d: ldc2_w -1.053036399720808E256
      // 5840: dastore
      // 5841: dup
      // 5842: sipush 1357
      // 5845: ldc2_w 2.1836166619192508E256
      // 5848: dastore
      // 5849: dup
      // 584a: sipush 1358
      // 584d: ldc2_w 1.0368403169781264E257
      // 5850: dastore
      // 5851: dup
      // 5852: sipush 1359
      // 5855: ldc2_w -2.0648015610276362E257
      // 5858: dastore
      // 5859: dup
      // 585a: sipush 1360
      // 585d: ldc2_w 8.426174035728768E257
      // 5860: dastore
      // 5861: dup
      // 5862: sipush 1361
      // 5865: ldc2_w -1.3577357192972777E258
      // 5868: dastore
      // 5869: dup
      // 586a: sipush 1362
      // 586d: ldc2_w 2.1313950901331177E258
      // 5870: dastore
      // 5871: dup
      // 5872: sipush 1363
      // 5875: ldc2_w 8.919141843592823E258
      // 5878: dastore
      // 5879: dup
      // 587a: sipush 1364
      // 587d: ldc2_w -1.1800039972549816E259
      // 5880: dastore
      // 5881: dup
      // 5882: sipush 1365
      // 5885: ldc2_w -1.1878772398311421E260
      // 5888: dastore
      // 5889: dup
      // 588a: sipush 1366
      // 588d: ldc2_w -1.538273497873993E260
      // 5890: dastore
      // 5891: dup
      // 5892: sipush 1367
      // 5895: ldc2_w -4.51305093266001E260
      // 5898: dastore
      // 5899: dup
      // 589a: sipush 1368
      // 589d: ldc2_w 1.1241179396053055E261
      // 58a0: dastore
      // 58a1: dup
      // 58a2: sipush 1369
      // 58a5: ldc2_w 6.154786508667658E261
      // 58a8: dastore
      // 58a9: dup
      // 58aa: sipush 1370
      // 58ad: ldc2_w -1.0626125049032536E262
      // 58b0: dastore
      // 58b1: dup
      // 58b2: sipush 1371
      // 58b5: ldc2_w -1.8908603201210102E262
      // 58b8: dastore
      // 58b9: dup
      // 58ba: sipush 1372
      // 58bd: ldc2_w -4.571195152299358E262
      // 58c0: dastore
      // 58c1: dup
      // 58c2: sipush 1373
      // 58c5: ldc2_w 1.526100002923062E263
      // 58c8: dastore
      // 58c9: dup
      // 58ca: sipush 1374
      // 58cd: ldc2_w -9.457084582570225E263
      // 58d0: dastore
      // 58d1: dup
      // 58d2: sipush 1375
      // 58d5: ldc2_w -1.5460500618825853E264
      // 58d8: dastore
      // 58d9: dup
      // 58da: sipush 1376
      // 58dd: ldc2_w -5.598276199126451E264
      // 58e0: dastore
      // 58e1: dup
      // 58e2: sipush 1377
      // 58e5: ldc2_w -1.2074097381167957E265
      // 58e8: dastore
      // 58e9: dup
      // 58ea: sipush 1378
      // 58ed: ldc2_w -3.015972957475025E265
      // 58f0: dastore
      // 58f1: dup
      // 58f2: sipush 1379
      // 58f5: ldc2_w 1.4345106852061226E265
      // 58f8: dastore
      // 58f9: dup
      // 58fa: sipush 1380
      // 58fd: ldc2_w 8.28479585346867E265
      // 5900: dastore
      // 5901: dup
      // 5902: sipush 1381
      // 5905: ldc2_w -3.118741081244705E266
      // 5908: dastore
      // 5909: dup
      // 590a: sipush 1382
      // 590d: ldc2_w -1.2054747399765794E266
      // 5910: dastore
      // 5911: dup
      // 5912: sipush 1383
      // 5915: ldc2_w 3.4454766202661184E267
      // 5918: dastore
      // 5919: dup
      // 591a: sipush 1384
      // 591d: ldc2_w 1.1279135096919439E268
      // 5920: dastore
      // 5921: dup
      // 5922: sipush 1385
      // 5925: ldc2_w 1.2066382528772518E268
      // 5928: dastore
      // 5929: dup
      // 592a: sipush 1386
      // 592d: ldc2_w 1.1984128162292276E269
      // 5930: dastore
      // 5931: dup
      // 5932: sipush 1387
      // 5935: ldc2_w 3.685169705587367E268
      // 5938: dastore
      // 5939: dup
      // 593a: sipush 1388
      // 593d: ldc2_w 6.570047690198998E269
      // 5940: dastore
      // 5941: dup
      // 5942: sipush 1389
      // 5945: ldc2_w 1.8836492887460383E270
      // 5948: dastore
      // 5949: dup
      // 594a: sipush 1390
      // 594d: ldc2_w 7.4364594917181125E270
      // 5950: dastore
      // 5951: dup
      // 5952: sipush 1391
      // 5955: ldc2_w 1.2773080633674971E271
      // 5958: dastore
      // 5959: dup
      // 595a: sipush 1392
      // 595d: ldc2_w 1.8928981707279692E271
      // 5960: dastore
      // 5961: dup
      // 5962: sipush 1393
      // 5965: ldc2_w 4.039437286589528E271
      // 5968: dastore
      // 5969: dup
      // 596a: sipush 1394
      // 596d: ldc2_w 1.785277385538302E272
      // 5970: dastore
      // 5971: dup
      // 5972: sipush 1395
      // 5975: ldc2_w -6.017681359527226E272
      // 5978: dastore
      // 5979: dup
      // 597a: sipush 1396
      // 597d: ldc2_w 1.9716943051755635E273
      // 5980: dastore
      // 5981: dup
      // 5982: sipush 1397
      // 5985: ldc2_w -8.772048092842086E271
      // 5988: dastore
      // 5989: dup
      // 598a: sipush 1398
      // 598d: ldc2_w 1.5645672698520312E274
      // 5990: dastore
      // 5991: dup
      // 5992: sipush 1399
      // 5995: ldc2_w -3.7979660725865874E274
      // 5998: dastore
      // 5999: dup
      // 599a: sipush 1400
      // 599d: ldc2_w 5.324902289537048E274
      // 59a0: dastore
      // 59a1: dup
      // 59a2: sipush 1401
      // 59a5: ldc2_w -1.8806716685063293E274
      // 59a8: dastore
      // 59a9: dup
      // 59aa: sipush 1402
      // 59ad: ldc2_w 9.320900373401115E275
      // 59b0: dastore
      // 59b1: dup
      // 59b2: sipush 1403
      // 59b5: ldc2_w 1.4615985810260016E275
      // 59b8: dastore
      // 59b9: dup
      // 59ba: sipush 1404
      // 59bd: ldc2_w 8.321226457219046E276
      // 59c0: dastore
      // 59c1: dup
      // 59c2: sipush 1405
      // 59c5: ldc2_w -4.608112855795952E276
      // 59c8: dastore
      // 59c9: dup
      // 59ca: sipush 1406
      // 59cd: ldc2_w -3.476352191116455E277
      // 59d0: dastore
      // 59d1: dup
      // 59d2: sipush 1407
      // 59d5: ldc2_w 5.266381689434054E277
      // 59d8: dastore
      // 59d9: dup
      // 59da: sipush 1408
      // 59dd: ldc2_w -9.622106063561645E277
      // 59e0: dastore
      // 59e1: dup
      // 59e2: sipush 1409
      // 59e5: ldc2_w 4.1719443712336026E278
      // 59e8: dastore
      // 59e9: dup
      // 59ea: sipush 1410
      // 59ed: ldc2_w 4.222411269063919E279
      // 59f0: dastore
      // 59f1: dup
      // 59f2: sipush 1411
      // 59f5: ldc2_w -6.714376022102489E279
      // 59f8: dastore
      // 59f9: dup
      // 59fa: sipush 1412
      // 59fd: ldc2_w -1.0732735585199074E280
      // 5a00: dastore
      // 5a01: dup
      // 5a02: sipush 1413
      // 5a05: ldc2_w -2.5866883048437488E280
      // 5a08: dastore
      // 5a09: dup
      // 5a0a: sipush 1414
      // 5a0d: ldc2_w -1.1306860837934988E281
      // 5a10: dastore
      // 5a11: dup
      // 5a12: sipush 1415
      // 5a15: ldc2_w 3.690690354793168E281
      // 5a18: dastore
      // 5a19: dup
      // 5a1a: sipush 1416
      // 5a1d: ldc2_w -5.5299180508885456E281
      // 5a20: dastore
      // 5a21: dup
      // 5a22: sipush 1417
      // 5a25: ldc2_w 2.7006726968568243E282
      // 5a28: dastore
      // 5a29: dup
      // 5a2a: sipush 1418
      // 5a2d: ldc2_w 4.135457669031131E282
      // 5a30: dastore
      // 5a31: dup
      // 5a32: sipush 1419
      // 5a35: ldc2_w 2.8401159516008676E283
      // 5a38: dastore
      // 5a39: dup
      // 5a3a: sipush 1420
      // 5a3d: ldc2_w 5.127265762024798E283
      // 5a40: dastore
      // 5a41: dup
      // 5a42: sipush 1421
      // 5a45: ldc2_w -3.4893601256685762E283
      // 5a48: dastore
      // 5a49: dup
      // 5a4a: sipush 1422
      // 5a4d: ldc2_w -1.145160459652136E283
      // 5a50: dastore
      // 5a51: dup
      // 5a52: sipush 1423
      // 5a55: ldc2_w 2.1742808735341656E284
      // 5a58: dastore
      // 5a59: dup
      // 5a5a: sipush 1424
      // 5a5d: ldc2_w 4.656972469326391E285
      // 5a60: dastore
      // 5a61: dup
      // 5a62: sipush 1425
      // 5a65: ldc2_w 7.672307991205681E285
      // 5a68: dastore
      // 5a69: dup
      // 5a6a: sipush 1426
      // 5a6d: ldc2_w 1.5781599575584034E286
      // 5a70: dastore
      // 5a71: dup
      // 5a72: sipush 1427
      // 5a75: ldc2_w 4.218682431618625E286
      // 5a78: dastore
      // 5a79: dup
      // 5a7a: sipush 1428
      // 5a7d: ldc2_w -2.4602260687026867E287
      // 5a80: dastore
      // 5a81: dup
      // 5a82: sipush 1429
      // 5a85: ldc2_w 2.7211316452521414E287
      // 5a88: dastore
      // 5a89: dup
      // 5a8a: sipush 1430
      // 5a8d: ldc2_w -1.8740018211089393E288
      // 5a90: dastore
      // 5a91: dup
      // 5a92: sipush 1431
      // 5a95: ldc2_w 2.6367639658206183E288
      // 5a98: dastore
      // 5a99: dup
      // 5a9a: sipush 1432
      // 5a9d: ldc2_w -3.102678910525039E288
      // 5aa0: dastore
      // 5aa1: dup
      // 5aa2: sipush 1433
      // 5aa5: ldc2_w 1.1992295328636466E289
      // 5aa8: dastore
      // 5aa9: dup
      // 5aaa: sipush 1434
      // 5aad: ldc2_w 6.8190133180135345E289
      // 5ab0: dastore
      // 5ab1: dup
      // 5ab2: sipush 1435
      // 5ab5: ldc2_w 5.783203879030497E289
      // 5ab8: dastore
      // 5ab9: dup
      // 5aba: sipush 1436
      // 5abd: ldc2_w 5.171047077293295E290
      // 5ac0: dastore
      // 5ac1: dup
      // 5ac2: sipush 1437
      // 5ac5: ldc2_w 1.8396930096213817E290
      // 5ac8: dastore
      // 5ac9: dup
      // 5aca: sipush 1438
      // 5acd: ldc2_w 1.4977047507315718E290
      // 5ad0: dastore
      // 5ad1: dup
      // 5ad2: sipush 1439
      // 5ad5: ldc2_w 1.0672499803427623E292
      // 5ad8: dastore
      // 5ad9: dup
      // 5ada: sipush 1440
      // 5add: ldc2_w 3.3310942289102464E291
      // 5ae0: dastore
      // 5ae1: dup
      // 5ae2: sipush 1441
      // 5ae5: ldc2_w -7.962256961838823E292
      // 5ae8: dastore
      // 5ae9: dup
      // 5aea: sipush 1442
      // 5aed: ldc2_w 1.7396889119023863E293
      // 5af0: dastore
      // 5af1: dup
      // 5af2: sipush 1443
      // 5af5: ldc2_w 3.8072183820435085E293
      // 5af8: dastore
      // 5af9: dup
      // 5afa: sipush 1444
      // 5afd: ldc2_w 2.2772059538865722E294
      // 5b00: dastore
      // 5b01: dup
      // 5b02: sipush 1445
      // 5b05: ldc2_w -2.0549866377878678E294
      // 5b08: dastore
      // 5b09: dup
      // 5b0a: sipush 1446
      // 5b0d: ldc2_w -1.2277120342804144E295
      // 5b10: dastore
      // 5b11: dup
      // 5b12: sipush 1447
      // 5b15: ldc2_w -3.609949022969024E295
      // 5b18: dastore
      // 5b19: dup
      // 5b1a: sipush 1448
      // 5b1d: ldc2_w 1.1479863663699871E296
      // 5b20: dastore
      // 5b21: dup
      // 5b22: sipush 1449
      // 5b25: ldc2_w -1.5314373779304356E296
      // 5b28: dastore
      // 5b29: dup
      // 5b2a: sipush 1450
      // 5b2d: ldc2_w -2.2537635160762597E296
      // 5b30: dastore
      // 5b31: dup
      // 5b32: sipush 1451
      // 5b35: ldc2_w -6.1370690793508674E296
      // 5b38: dastore
      // 5b39: dup
      // 5b3a: sipush 1452
      // 5b3d: ldc2_w -4.996854125490041E297
      // 5b40: dastore
      // 5b41: dup
      // 5b42: sipush 1453
      // 5b45: ldc2_w -6.883499809714189E297
      // 5b48: dastore
      // 5b49: dup
      // 5b4a: sipush 1454
      // 5b4d: ldc2_w -2.595456638706416E298
      // 5b50: dastore
      // 5b51: dup
      // 5b52: sipush 1455
      // 5b55: ldc2_w -1.1892631528580186E299
      // 5b58: dastore
      // 5b59: dup
      // 5b5a: sipush 1456
      // 5b5d: ldc2_w -1.4672600326020399E299
      // 5b60: dastore
      // 5b61: dup
      // 5b62: sipush 1457
      // 5b65: ldc2_w -3.200068509818696E299
      // 5b68: dastore
      // 5b69: dup
      // 5b6a: sipush 1458
      // 5b6d: ldc2_w -7.126913872617518E298
      // 5b70: dastore
      // 5b71: dup
      // 5b72: sipush 1459
      // 5b75: ldc2_w -3.3655587417265094E300
      // 5b78: dastore
      // 5b79: dup
      // 5b7a: sipush 1460
      // 5b7d: ldc2_w NaN
      // 5b80: dastore
      // 5b81: dup
      // 5b82: sipush 1461
      // 5b85: ldc2_w NaN
      // 5b88: dastore
      // 5b89: dup
      // 5b8a: sipush 1462
      // 5b8d: ldc2_w NaN
      // 5b90: dastore
      // 5b91: dup
      // 5b92: sipush 1463
      // 5b95: ldc2_w NaN
      // 5b98: dastore
      // 5b99: dup
      // 5b9a: sipush 1464
      // 5b9d: ldc2_w NaN
      // 5ba0: dastore
      // 5ba1: dup
      // 5ba2: sipush 1465
      // 5ba5: ldc2_w NaN
      // 5ba8: dastore
      // 5ba9: dup
      // 5baa: sipush 1466
      // 5bad: ldc2_w NaN
      // 5bb0: dastore
      // 5bb1: dup
      // 5bb2: sipush 1467
      // 5bb5: ldc2_w NaN
      // 5bb8: dastore
      // 5bb9: dup
      // 5bba: sipush 1468
      // 5bbd: ldc2_w NaN
      // 5bc0: dastore
      // 5bc1: dup
      // 5bc2: sipush 1469
      // 5bc5: ldc2_w NaN
      // 5bc8: dastore
      // 5bc9: dup
      // 5bca: sipush 1470
      // 5bcd: ldc2_w NaN
      // 5bd0: dastore
      // 5bd1: dup
      // 5bd2: sipush 1471
      // 5bd5: ldc2_w NaN
      // 5bd8: dastore
      // 5bd9: dup
      // 5bda: sipush 1472
      // 5bdd: ldc2_w NaN
      // 5be0: dastore
      // 5be1: dup
      // 5be2: sipush 1473
      // 5be5: ldc2_w NaN
      // 5be8: dastore
      // 5be9: dup
      // 5bea: sipush 1474
      // 5bed: ldc2_w NaN
      // 5bf0: dastore
      // 5bf1: dup
      // 5bf2: sipush 1475
      // 5bf5: ldc2_w NaN
      // 5bf8: dastore
      // 5bf9: dup
      // 5bfa: sipush 1476
      // 5bfd: ldc2_w NaN
      // 5c00: dastore
      // 5c01: dup
      // 5c02: sipush 1477
      // 5c05: ldc2_w NaN
      // 5c08: dastore
      // 5c09: dup
      // 5c0a: sipush 1478
      // 5c0d: ldc2_w NaN
      // 5c10: dastore
      // 5c11: dup
      // 5c12: sipush 1479
      // 5c15: ldc2_w NaN
      // 5c18: dastore
      // 5c19: dup
      // 5c1a: sipush 1480
      // 5c1d: ldc2_w NaN
      // 5c20: dastore
      // 5c21: dup
      // 5c22: sipush 1481
      // 5c25: ldc2_w NaN
      // 5c28: dastore
      // 5c29: dup
      // 5c2a: sipush 1482
      // 5c2d: ldc2_w NaN
      // 5c30: dastore
      // 5c31: dup
      // 5c32: sipush 1483
      // 5c35: ldc2_w NaN
      // 5c38: dastore
      // 5c39: dup
      // 5c3a: sipush 1484
      // 5c3d: ldc2_w NaN
      // 5c40: dastore
      // 5c41: dup
      // 5c42: sipush 1485
      // 5c45: ldc2_w NaN
      // 5c48: dastore
      // 5c49: dup
      // 5c4a: sipush 1486
      // 5c4d: ldc2_w NaN
      // 5c50: dastore
      // 5c51: dup
      // 5c52: sipush 1487
      // 5c55: ldc2_w NaN
      // 5c58: dastore
      // 5c59: dup
      // 5c5a: sipush 1488
      // 5c5d: ldc2_w NaN
      // 5c60: dastore
      // 5c61: dup
      // 5c62: sipush 1489
      // 5c65: ldc2_w NaN
      // 5c68: dastore
      // 5c69: dup
      // 5c6a: sipush 1490
      // 5c6d: ldc2_w NaN
      // 5c70: dastore
      // 5c71: dup
      // 5c72: sipush 1491
      // 5c75: ldc2_w NaN
      // 5c78: dastore
      // 5c79: dup
      // 5c7a: sipush 1492
      // 5c7d: ldc2_w NaN
      // 5c80: dastore
      // 5c81: dup
      // 5c82: sipush 1493
      // 5c85: ldc2_w NaN
      // 5c88: dastore
      // 5c89: dup
      // 5c8a: sipush 1494
      // 5c8d: ldc2_w NaN
      // 5c90: dastore
      // 5c91: dup
      // 5c92: sipush 1495
      // 5c95: ldc2_w NaN
      // 5c98: dastore
      // 5c99: dup
      // 5c9a: sipush 1496
      // 5c9d: ldc2_w NaN
      // 5ca0: dastore
      // 5ca1: dup
      // 5ca2: sipush 1497
      // 5ca5: ldc2_w NaN
      // 5ca8: dastore
      // 5ca9: dup
      // 5caa: sipush 1498
      // 5cad: ldc2_w NaN
      // 5cb0: dastore
      // 5cb1: dup
      // 5cb2: sipush 1499
      // 5cb5: ldc2_w NaN
      // 5cb8: dastore
      // 5cb9: putstatic org/apache/commons/math3/util/FastMathLiteralArrays.EXP_INT_B [D
      // 5cbc: sipush 1025
      // 5cbf: newarray 7
      // 5cc1: dup
      // 5cc2: bipush 0
      // 5cc3: dconst_1
      // 5cc4: dastore
      // 5cc5: dup
      // 5cc6: bipush 1
      // 5cc7: ldc2_w 1.0009770393371582
      // 5cca: dastore
      // 5ccb: dup
      // 5ccc: bipush 2
      // 5ccd: ldc2_w 1.0019550323486328
      // 5cd0: dastore
      // 5cd1: dup
      // 5cd2: bipush 3
      // 5cd3: ldc2_w 1.0029339790344238
      // 5cd6: dastore
      // 5cd7: dup
      // 5cd8: bipush 4
      // 5cd9: ldc2_w 1.0039138793945312
      // 5cdc: dastore
      // 5cdd: dup
      // 5cde: bipush 5
      // 5cdf: ldc2_w 1.004894733428955
      // 5ce2: dastore
      // 5ce3: dup
      // 5ce4: bipush 6
      // 5ce6: ldc2_w 1.0058765411376953
      // 5ce9: dastore
      // 5cea: dup
      // 5ceb: bipush 7
      // 5ced: ldc2_w 1.006859302520752
      // 5cf0: dastore
      // 5cf1: dup
      // 5cf2: bipush 8
      // 5cf4: ldc2_w 1.007843017578125
      // 5cf7: dastore
      // 5cf8: dup
      // 5cf9: bipush 9
      // 5cfb: ldc2_w 1.0088276863098145
      // 5cfe: dastore
      // 5cff: dup
      // 5d00: bipush 10
      // 5d02: ldc2_w 1.0098135471343994
      // 5d05: dastore
      // 5d06: dup
      // 5d07: bipush 11
      // 5d09: ldc2_w 1.0108001232147217
      // 5d0c: dastore
      // 5d0d: dup
      // 5d0e: bipush 12
      // 5d10: ldc2_w 1.0117876529693604
      // 5d13: dastore
      // 5d14: dup
      // 5d15: bipush 13
      // 5d17: ldc2_w 1.0127761363983154
      // 5d1a: dastore
      // 5d1b: dup
      // 5d1c: bipush 14
      // 5d1e: ldc2_w 1.013765811920166
      // 5d21: dastore
      // 5d22: dup
      // 5d23: bipush 15
      // 5d25: ldc2_w 1.014756202697754
      // 5d28: dastore
      // 5d29: dup
      // 5d2a: bipush 16
      // 5d2c: ldc2_w 1.0157477855682373
      // 5d2f: dastore
      // 5d30: dup
      // 5d31: bipush 17
      // 5d33: ldc2_w 1.016740083694458
      // 5d36: dastore
      // 5d37: dup
      // 5d38: bipush 18
      // 5d3a: ldc2_w 1.0177335739135742
      // 5d3d: dastore
      // 5d3e: dup
      // 5d3f: bipush 19
      // 5d41: ldc2_w 1.0187277793884277
      // 5d44: dastore
      // 5d45: dup
      // 5d46: bipush 20
      // 5d48: ldc2_w 1.0197231769561768
      // 5d4b: dastore
      // 5d4c: dup
      // 5d4d: bipush 21
      // 5d4f: ldc2_w 1.0207195281982422
      // 5d52: dastore
      // 5d53: dup
      // 5d54: bipush 22
      // 5d56: ldc2_w 1.021716833114624
      // 5d59: dastore
      // 5d5a: dup
      // 5d5b: bipush 23
      // 5d5d: ldc2_w 1.0227150917053223
      // 5d60: dastore
      // 5d61: dup
      // 5d62: bipush 24
      // 5d64: ldc2_w 1.023714303970337
      // 5d67: dastore
      // 5d68: dup
      // 5d69: bipush 25
      // 5d6b: ldc2_w 1.024714469909668
      // 5d6e: dastore
      // 5d6f: dup
      // 5d70: bipush 26
      // 5d72: ldc2_w 1.0257158279418945
      // 5d75: dastore
      // 5d76: dup
      // 5d77: bipush 27
      // 5d79: ldc2_w 1.0267179012298584
      // 5d7c: dastore
      // 5d7d: dup
      // 5d7e: bipush 28
      // 5d80: ldc2_w 1.0277209281921387
      // 5d83: dastore
      // 5d84: dup
      // 5d85: bipush 29
      // 5d87: ldc2_w 1.0287251472473145
      // 5d8a: dastore
      // 5d8b: dup
      // 5d8c: bipush 30
      // 5d8e: ldc2_w 1.0297303199768066
      // 5d91: dastore
      // 5d92: dup
      // 5d93: bipush 31
      // 5d95: ldc2_w 1.0307364463806152
      // 5d98: dastore
      // 5d99: dup
      // 5d9a: bipush 32
      // 5d9c: ldc2_w 1.0317435264587402
      // 5d9f: dastore
      // 5da0: dup
      // 5da1: bipush 33
      // 5da3: ldc2_w 1.0327515602111816
      // 5da6: dastore
      // 5da7: dup
      // 5da8: bipush 34
      // 5daa: ldc2_w 1.0337605476379395
      // 5dad: dastore
      // 5dae: dup
      // 5daf: bipush 35
      // 5db1: ldc2_w 1.0347704887390137
      // 5db4: dastore
      // 5db5: dup
      // 5db6: bipush 36
      // 5db8: ldc2_w 1.0357816219329834
      // 5dbb: dastore
      // 5dbc: dup
      // 5dbd: bipush 37
      // 5dbf: ldc2_w 1.0367934703826904
      // 5dc2: dastore
      // 5dc3: dup
      // 5dc4: bipush 38
      // 5dc6: ldc2_w 1.037806510925293
      // 5dc9: dastore
      // 5dca: dup
      // 5dcb: bipush 39
      // 5dcd: ldc2_w 1.038820505142212
      // 5dd0: dastore
      // 5dd1: dup
      // 5dd2: bipush 40
      // 5dd4: ldc2_w 1.0398354530334473
      // 5dd7: dastore
      // 5dd8: dup
      // 5dd9: bipush 41
      // 5ddb: ldc2_w 1.040851354598999
      // 5dde: dastore
      // 5ddf: dup
      // 5de0: bipush 42
      // 5de2: ldc2_w 1.0418684482574463
      // 5de5: dastore
      // 5de6: dup
      // 5de7: bipush 43
      // 5de9: ldc2_w 1.0428862571716309
      // 5dec: dastore
      // 5ded: dup
      // 5dee: bipush 44
      // 5df0: ldc2_w 1.043905258178711
      // 5df3: dastore
      // 5df4: dup
      // 5df5: bipush 45
      // 5df7: ldc2_w 1.0449252128601074
      // 5dfa: dastore
      // 5dfb: dup
      // 5dfc: bipush 46
      // 5dfe: ldc2_w 1.0459461212158203
      // 5e01: dastore
      // 5e02: dup
      // 5e03: bipush 47
      // 5e05: ldc2_w 1.0469679832458496
      // 5e08: dastore
      // 5e09: dup
      // 5e0a: bipush 48
      // 5e0c: ldc2_w 1.0479910373687744
      // 5e0f: dastore
      // 5e10: dup
      // 5e11: bipush 49
      // 5e13: ldc2_w 1.0490150451660156
      // 5e16: dastore
      // 5e17: dup
      // 5e18: bipush 50
      // 5e1a: ldc2_w 1.0500397682189941
      // 5e1d: dastore
      // 5e1e: dup
      // 5e1f: bipush 51
      // 5e21: ldc2_w 1.0510656833648682
      // 5e24: dastore
      // 5e25: dup
      // 5e26: bipush 52
      // 5e28: ldc2_w 1.0520927906036377
      // 5e2b: dastore
      // 5e2c: dup
      // 5e2d: bipush 53
      // 5e2f: ldc2_w 1.0531206130981445
      // 5e32: dastore
      // 5e33: dup
      // 5e34: bipush 54
      // 5e36: ldc2_w 1.0541496276855469
      // 5e39: dastore
      // 5e3a: dup
      // 5e3b: bipush 55
      // 5e3d: ldc2_w 1.0551795959472656
      // 5e40: dastore
      // 5e41: dup
      // 5e42: bipush 56
      // 5e44: ldc2_w 1.0562105178833008
      // 5e47: dastore
      // 5e48: dup
      // 5e49: bipush 57
      // 5e4b: ldc2_w 1.0572423934936523
      // 5e4e: dastore
      // 5e4f: dup
      // 5e50: bipush 58
      // 5e52: ldc2_w 1.0582754611968994
      // 5e55: dastore
      // 5e56: dup
      // 5e57: bipush 59
      // 5e59: ldc2_w 1.059309482574463
      // 5e5c: dastore
      // 5e5d: dup
      // 5e5e: bipush 60
      // 5e60: ldc2_w 1.0603444576263428
      // 5e63: dastore
      // 5e64: dup
      // 5e65: bipush 61
      // 5e67: ldc2_w 1.061380386352539
      // 5e6a: dastore
      // 5e6b: dup
      // 5e6c: bipush 62
      // 5e6e: ldc2_w 1.0624175071716309
      // 5e71: dastore
      // 5e72: dup
      // 5e73: bipush 63
      // 5e75: ldc2_w 1.06345534324646
      // 5e78: dastore
      // 5e79: dup
      // 5e7a: bipush 64
      // 5e7c: ldc2_w 1.0644943714141846
      // 5e7f: dastore
      // 5e80: dup
      // 5e81: bipush 65
      // 5e83: ldc2_w 1.0655345916748047
      // 5e86: dastore
      // 5e87: dup
      // 5e88: bipush 66
      // 5e8a: ldc2_w 1.066575527191162
      // 5e8d: dastore
      // 5e8e: dup
      // 5e8f: bipush 67
      // 5e91: ldc2_w 1.067617654800415
      // 5e94: dastore
      // 5e95: dup
      // 5e96: bipush 68
      // 5e98: ldc2_w 1.0686607360839844
      // 5e9b: dastore
      // 5e9c: dup
      // 5e9d: bipush 69
      // 5e9f: ldc2_w 1.0697050094604492
      // 5ea2: dastore
      // 5ea3: dup
      // 5ea4: bipush 70
      // 5ea6: ldc2_w 1.0707499980926514
      // 5ea9: dastore
      // 5eaa: dup
      // 5eab: bipush 71
      // 5ead: ldc2_w 1.071796178817749
      // 5eb0: dastore
      // 5eb1: dup
      // 5eb2: bipush 72
      // 5eb4: ldc2_w 1.072843313217163
      // 5eb7: dastore
      // 5eb8: dup
      // 5eb9: bipush 73
      // 5ebb: ldc2_w 1.0738916397094727
      // 5ebe: dastore
      // 5ebf: dup
      // 5ec0: bipush 74
      // 5ec2: ldc2_w 1.0749409198760986
      // 5ec5: dastore
      // 5ec6: dup
      // 5ec7: bipush 75
      // 5ec9: ldc2_w 1.075991153717041
      // 5ecc: dastore
      // 5ecd: dup
      // 5ece: bipush 76
      // 5ed0: ldc2_w 1.0770423412322998
      // 5ed3: dastore
      // 5ed4: dup
      // 5ed5: bipush 77
      // 5ed7: ldc2_w 1.078094720840454
      // 5eda: dastore
      // 5edb: dup
      // 5edc: bipush 78
      // 5ede: ldc2_w 1.0791480541229248
      // 5ee1: dastore
      // 5ee2: dup
      // 5ee3: bipush 79
      // 5ee5: ldc2_w 1.080202341079712
      // 5ee8: dastore
      // 5ee9: dup
      // 5eea: bipush 80
      // 5eec: ldc2_w 1.0812578201293945
      // 5eef: dastore
      // 5ef0: dup
      // 5ef1: bipush 81
      // 5ef3: ldc2_w 1.0823142528533936
      // 5ef6: dastore
      // 5ef7: dup
      // 5ef8: bipush 82
      // 5efa: ldc2_w 1.083371639251709
      // 5efd: dastore
      // 5efe: dup
      // 5eff: bipush 83
      // 5f01: ldc2_w 1.08443021774292
      // 5f04: dastore
      // 5f05: dup
      // 5f06: bipush 84
      // 5f08: ldc2_w 1.0854897499084473
      // 5f0b: dastore
      // 5f0c: dup
      // 5f0d: bipush 85
      // 5f0f: ldc2_w 1.086550235748291
      // 5f12: dastore
      // 5f13: dup
      // 5f14: bipush 86
      // 5f16: ldc2_w 1.0876119136810303
      // 5f19: dastore
      // 5f1a: dup
      // 5f1b: bipush 87
      // 5f1d: ldc2_w 1.088674545288086
      // 5f20: dastore
      // 5f21: dup
      // 5f22: bipush 88
      // 5f24: ldc2_w 1.089738130569458
      // 5f27: dastore
      // 5f28: dup
      // 5f29: bipush 89
      // 5f2b: ldc2_w 1.0908029079437256
      // 5f2e: dastore
      // 5f2f: dup
      // 5f30: bipush 90
      // 5f32: ldc2_w 1.0918686389923096
      // 5f35: dastore
      // 5f36: dup
      // 5f37: bipush 91
      // 5f39: ldc2_w 1.092935562133789
      // 5f3c: dastore
      // 5f3d: dup
      // 5f3e: bipush 92
      // 5f40: ldc2_w 1.094003438949585
      // 5f43: dastore
      // 5f44: dup
      // 5f45: bipush 93
      // 5f47: ldc2_w 1.0950722694396973
      // 5f4a: dastore
      // 5f4b: dup
      // 5f4c: bipush 94
      // 5f4e: ldc2_w 1.096142053604126
      // 5f51: dastore
      // 5f52: dup
      // 5f53: bipush 95
      // 5f55: ldc2_w 1.0972130298614502
      // 5f58: dastore
      // 5f59: dup
      // 5f5a: bipush 96
      // 5f5c: ldc2_w 1.09828519821167
      // 5f5f: dastore
      // 5f60: dup
      // 5f61: bipush 97
      // 5f63: ldc2_w 1.099358320236206
      // 5f66: dastore
      // 5f67: dup
      // 5f68: bipush 98
      // 5f6a: ldc2_w 1.1004323959350586
      // 5f6d: dastore
      // 5f6e: dup
      // 5f6f: bipush 99
      // 5f71: ldc2_w 1.1015074253082275
      // 5f74: dastore
      // 5f75: dup
      // 5f76: bipush 100
      // 5f78: ldc2_w 1.102583646774292
      // 5f7b: dastore
      // 5f7c: dup
      // 5f7d: bipush 101
      // 5f7f: ldc2_w 1.103661060333252
      // 5f82: dastore
      // 5f83: dup
      // 5f84: bipush 102
      // 5f86: ldc2_w 1.1047391891479492
      // 5f89: dastore
      // 5f8a: dup
      // 5f8b: bipush 103
      // 5f8d: ldc2_w 1.105818748474121
      // 5f90: dastore
      // 5f91: dup
      // 5f92: bipush 104
      // 5f94: ldc2_w 1.1068990230560303
      // 5f97: dastore
      // 5f98: dup
      // 5f99: bipush 105
      // 5f9b: ldc2_w 1.107980489730835
      // 5f9e: dastore
      // 5f9f: dup
      // 5fa0: bipush 106
      // 5fa2: ldc2_w 1.1090631484985352
      // 5fa5: dastore
      // 5fa6: dup
      // 5fa7: bipush 107
      // 5fa9: ldc2_w 1.1101467609405518
      // 5fac: dastore
      // 5fad: dup
      // 5fae: bipush 108
      // 5fb0: ldc2_w 1.1112313270568848
      // 5fb3: dastore
      // 5fb4: dup
      // 5fb5: bipush 109
      // 5fb7: ldc2_w 1.1123170852661133
      // 5fba: dastore
      // 5fbb: dup
      // 5fbc: bipush 110
      // 5fbe: ldc2_w 1.1134037971496582
      // 5fc1: dastore
      // 5fc2: dup
      // 5fc3: bipush 111
      // 5fc5: ldc2_w 1.1144917011260986
      // 5fc8: dastore
      // 5fc9: dup
      // 5fca: bipush 112
      // 5fcc: ldc2_w 1.1155805587768555
      // 5fcf: dastore
      // 5fd0: dup
      // 5fd1: bipush 113
      // 5fd3: ldc2_w 1.1166706085205078
      // 5fd6: dastore
      // 5fd7: dup
      // 5fd8: bipush 114
      // 5fda: ldc2_w 1.1177616119384766
      // 5fdd: dastore
      // 5fde: dup
      // 5fdf: bipush 115
      // 5fe1: ldc2_w 1.1188538074493408
      // 5fe4: dastore
      // 5fe5: dup
      // 5fe6: bipush 116
      // 5fe8: ldc2_w 1.1199469566345215
      // 5feb: dastore
      // 5fec: dup
      // 5fed: bipush 117
      // 5fef: ldc2_w 1.1210410594940186
      // 5ff2: dastore
      // 5ff3: dup
      // 5ff4: bipush 118
      // 5ff6: ldc2_w 1.1221363544464111
      // 5ff9: dastore
      // 5ffa: dup
      // 5ffb: bipush 119
      // 5ffd: ldc2_w 1.1232328414916992
      // 6000: dastore
      // 6001: dup
      // 6002: bipush 120
      // 6004: ldc2_w 1.1243302822113037
      // 6007: dastore
      // 6008: dup
      // 6009: bipush 121
      // 600b: ldc2_w 1.1254286766052246
      // 600e: dastore
      // 600f: dup
      // 6010: bipush 122
      // 6012: ldc2_w 1.126528263092041
      // 6015: dastore
      // 6016: dup
      // 6017: bipush 123
      // 6019: ldc2_w 1.127629041671753
      // 601c: dastore
      // 601d: dup
      // 601e: bipush 124
      // 6020: ldc2_w 1.1287307739257812
      // 6023: dastore
      // 6024: dup
      // 6025: bipush 125
      // 6027: ldc2_w 1.129833459854126
      // 602a: dastore
      // 602b: dup
      // 602c: bipush 126
      // 602e: ldc2_w 1.1309373378753662
      // 6031: dastore
      // 6032: dup
      // 6033: bipush 127
      // 6035: ldc2_w 1.132042407989502
      // 6038: dastore
      // 6039: dup
      // 603a: sipush 128
      // 603d: ldc2_w 1.133148431777954
      // 6040: dastore
      // 6041: dup
      // 6042: sipush 129
      // 6045: ldc2_w 1.1342556476593018
      // 6048: dastore
      // 6049: dup
      // 604a: sipush 130
      // 604d: ldc2_w 1.1353638172149658
      // 6050: dastore
      // 6051: dup
      // 6052: sipush 131
      // 6055: ldc2_w 1.1364731788635254
      // 6058: dastore
      // 6059: dup
      // 605a: sipush 132
      // 605d: ldc2_w 1.1375834941864014
      // 6060: dastore
      // 6061: dup
      // 6062: sipush 133
      // 6065: ldc2_w 1.1386950016021729
      // 6068: dastore
      // 6069: dup
      // 606a: sipush 134
      // 606d: ldc2_w 1.1398074626922607
      // 6070: dastore
      // 6071: dup
      // 6072: sipush 135
      // 6075: ldc2_w 1.1409211158752441
      // 6078: dastore
      // 6079: dup
      // 607a: sipush 136
      // 607d: ldc2_w 1.142035961151123
      // 6080: dastore
      // 6081: dup
      // 6082: sipush 137
      // 6085: ldc2_w 1.1431517601013184
      // 6088: dastore
      // 6089: dup
      // 608a: sipush 138
      // 608d: ldc2_w 1.14426851272583
      // 6090: dastore
      // 6091: dup
      // 6092: sipush 139
      // 6095: ldc2_w 1.1453864574432373
      // 6098: dastore
      // 6099: dup
      // 609a: sipush 140
      // 609d: ldc2_w 1.14650559425354
      // 60a0: dastore
      // 60a1: dup
      // 60a2: sipush 141
      // 60a5: ldc2_w 1.1476259231567383
      // 60a8: dastore
      // 60a9: dup
      // 60aa: sipush 142
      // 60ad: ldc2_w 1.148747205734253
      // 60b0: dastore
      // 60b1: dup
      // 60b2: sipush 143
      // 60b5: ldc2_w 1.149869441986084
      // 60b8: dastore
      // 60b9: dup
      // 60ba: sipush 144
      // 60bd: ldc2_w 1.1509928703308105
      // 60c0: dastore
      // 60c1: dup
      // 60c2: sipush 145
      // 60c5: ldc2_w 1.1521174907684326
      // 60c8: dastore
      // 60c9: dup
      // 60ca: sipush 146
      // 60cd: ldc2_w 1.153243064880371
      // 60d0: dastore
      // 60d1: dup
      // 60d2: sipush 147
      // 60d5: ldc2_w 1.154369831085205
      // 60d8: dastore
      // 60d9: dup
      // 60da: sipush 148
      // 60dd: ldc2_w 1.1554977893829346
      // 60e0: dastore
      // 60e1: dup
      // 60e2: sipush 149
      // 60e5: ldc2_w 1.1566267013549805
      // 60e8: dastore
      // 60e9: dup
      // 60ea: sipush 150
      // 60ed: ldc2_w 1.1577568054199219
      // 60f0: dastore
      // 60f1: dup
      // 60f2: sipush 151
      // 60f5: ldc2_w 1.1588881015777588
      // 60f8: dastore
      // 60f9: dup
      // 60fa: sipush 152
      // 60fd: ldc2_w 1.160020351409912
      // 6100: dastore
      // 6101: dup
      // 6102: sipush 153
      // 6105: ldc2_w 1.161153793334961
      // 6108: dastore
      // 6109: dup
      // 610a: sipush 154
      // 610d: ldc2_w 1.1622881889343262
      // 6110: dastore
      // 6111: dup
      // 6112: sipush 155
      // 6115: ldc2_w 1.163423776626587
      // 6118: dastore
      // 6119: dup
      // 611a: sipush 156
      // 611d: ldc2_w 1.1645605564117432
      // 6120: dastore
      // 6121: dup
      // 6122: sipush 157
      // 6125: ldc2_w 1.1656982898712158
      // 6128: dastore
      // 6129: dup
      // 612a: sipush 158
      // 612d: ldc2_w 1.166837215423584
      // 6130: dastore
      // 6131: dup
      // 6132: sipush 159
      // 6135: ldc2_w 1.1679773330688477
      // 6138: dastore
      // 6139: dup
      // 613a: sipush 160
      // 613d: ldc2_w 1.1691184043884277
      // 6140: dastore
      // 6141: dup
      // 6142: sipush 161
      // 6145: ldc2_w 1.1702606678009033
      // 6148: dastore
      // 6149: dup
      // 614a: sipush 162
      // 614d: ldc2_w 1.1714041233062744
      // 6150: dastore
      // 6151: dup
      // 6152: sipush 163
      // 6155: ldc2_w 1.172548532485962
      // 6158: dastore
      // 6159: dup
      // 615a: sipush 164
      // 615d: ldc2_w 1.173694133758545
      // 6160: dastore
      // 6161: dup
      // 6162: sipush 165
      // 6165: ldc2_w 1.1748409271240234
      // 6168: dastore
      // 6169: dup
      // 616a: sipush 166
      // 616d: ldc2_w 1.1759889125823975
      // 6170: dastore
      // 6171: dup
      // 6172: sipush 167
      // 6175: ldc2_w 1.177137851715088
      // 6178: dastore
      // 6179: dup
      // 617a: sipush 168
      // 617d: ldc2_w 1.1782879829406738
      // 6180: dastore
      // 6181: dup
      // 6182: sipush 169
      // 6185: ldc2_w 1.1794393062591553
      // 6188: dastore
      // 6189: dup
      // 618a: sipush 170
      // 618d: ldc2_w 1.1805915832519531
      // 6190: dastore
      // 6191: dup
      // 6192: sipush 171
      // 6195: ldc2_w 1.1817450523376465
      // 6198: dastore
      // 6199: dup
      // 619a: sipush 172
      // 619d: ldc2_w 1.1828997135162354
      // 61a0: dastore
      // 61a1: dup
      // 61a2: sipush 173
      // 61a5: ldc2_w 1.1840553283691406
      // 61a8: dastore
      // 61a9: dup
      // 61aa: sipush 174
      // 61ad: ldc2_w 1.1852121353149414
      // 61b0: dastore
      // 61b1: dup
      // 61b2: sipush 175
      // 61b5: ldc2_w 1.1863701343536377
      // 61b8: dastore
      // 61b9: dup
      // 61ba: sipush 176
      // 61bd: ldc2_w 1.1875293254852295
      // 61c0: dastore
      // 61c1: dup
      // 61c2: sipush 177
      // 61c5: ldc2_w 1.1886897087097168
      // 61c8: dastore
      // 61c9: dup
      // 61ca: sipush 178
      // 61cd: ldc2_w 1.1898510456085205
      // 61d0: dastore
      // 61d1: dup
      // 61d2: sipush 179
      // 61d5: ldc2_w 1.1910135746002197
      // 61d8: dastore
      // 61d9: dup
      // 61da: sipush 180
      // 61dd: ldc2_w 1.1921772956848145
      // 61e0: dastore
      // 61e1: dup
      // 61e2: sipush 181
      // 61e5: ldc2_w 1.1933419704437256
      // 61e8: dastore
      // 61e9: dup
      // 61ea: sipush 182
      // 61ed: ldc2_w 1.1945080757141113
      // 61f0: dastore
      // 61f1: dup
      // 61f2: sipush 183
      // 61f5: ldc2_w 1.1956751346588135
      // 61f8: dastore
      // 61f9: dup
      // 61fa: sipush 184
      // 61fd: ldc2_w 1.1968433856964111
      // 6200: dastore
      // 6201: dup
      // 6202: sipush 185
      // 6205: ldc2_w 1.1980125904083252
      // 6208: dastore
      // 6209: dup
      // 620a: sipush 186
      // 620d: ldc2_w 1.1991832256317139
      // 6210: dastore
      // 6211: dup
      // 6212: sipush 187
      // 6215: ldc2_w 1.200354814529419
      // 6218: dastore
      // 6219: dup
      // 621a: sipush 188
      // 621d: ldc2_w 1.2015275955200195
      // 6220: dastore
      // 6221: dup
      // 6222: sipush 189
      // 6225: ldc2_w 1.2027015686035156
      // 6228: dastore
      // 6229: dup
      // 622a: sipush 190
      // 622d: ldc2_w 1.2038767337799072
      // 6230: dastore
      // 6231: dup
      // 6232: sipush 191
      // 6235: ldc2_w 1.2050528526306152
      // 6238: dastore
      // 6239: dup
      // 623a: sipush 192
      // 623d: ldc2_w 1.2062301635742188
      // 6240: dastore
      // 6241: dup
      // 6242: sipush 193
      // 6245: ldc2_w 1.2074086666107178
      // 6248: dastore
      // 6249: dup
      // 624a: sipush 194
      // 624d: ldc2_w 1.2085883617401123
      // 6250: dastore
      // 6251: dup
      // 6252: sipush 195
      // 6255: ldc2_w 1.2097692489624023
      // 6258: dastore
      // 6259: dup
      // 625a: sipush 196
      // 625d: ldc2_w 1.210951328277588
      // 6260: dastore
      // 6261: dup
      // 6262: sipush 197
      // 6265: ldc2_w 1.2121343612670898
      // 6268: dastore
      // 6269: dup
      // 626a: sipush 198
      // 626d: ldc2_w 1.2133188247680664
      // 6270: dastore
      // 6271: dup
      // 6272: sipush 199
      // 6275: ldc2_w 1.2145042419433594
      // 6278: dastore
      // 6279: dup
      // 627a: sipush 200
      // 627d: ldc2_w 1.2156908512115479
      // 6280: dastore
      // 6281: dup
      // 6282: sipush 201
      // 6285: ldc2_w 1.2168786525726318
      // 6288: dastore
      // 6289: dup
      // 628a: sipush 202
      // 628d: ldc2_w 1.2180676460266113
      // 6290: dastore
      // 6291: dup
      // 6292: sipush 203
      // 6295: ldc2_w 1.2192575931549072
      // 6298: dastore
      // 6299: dup
      // 629a: sipush 204
      // 629d: ldc2_w 1.2204489707946777
      // 62a0: dastore
      // 62a1: dup
      // 62a2: sipush 205
      // 62a5: ldc2_w 1.2216413021087646
      // 62a8: dastore
      // 62a9: dup
      // 62aa: sipush 206
      // 62ad: ldc2_w 1.222834825515747
      // 62b0: dastore
      // 62b1: dup
      // 62b2: sipush 207
      // 62b5: ldc2_w 1.224029779434204
      // 62b8: dastore
      // 62b9: dup
      // 62ba: sipush 208
      // 62bd: ldc2_w 1.2252256870269775
      // 62c0: dastore
      // 62c1: dup
      // 62c2: sipush 209
      // 62c5: ldc2_w 1.2264227867126465
      // 62c8: dastore
      // 62c9: dup
      // 62ca: sipush 210
      // 62cd: ldc2_w 1.227621078491211
      // 62d0: dastore
      // 62d1: dup
      // 62d2: sipush 211
      // 62d5: ldc2_w 1.2288203239440918
      // 62d8: dastore
      // 62d9: dup
      // 62da: sipush 212
      // 62dd: ldc2_w 1.2300209999084473
      // 62e0: dastore
      // 62e1: dup
      // 62e2: sipush 213
      // 62e5: ldc2_w 1.2312228679656982
      // 62e8: dastore
      // 62e9: dup
      // 62ea: sipush 214
      // 62ed: ldc2_w 1.2324256896972656
      // 62f0: dastore
      // 62f1: dup
      // 62f2: sipush 215
      // 62f5: ldc2_w 1.2336299419403076
      // 62f8: dastore
      // 62f9: dup
      // 62fa: sipush 216
      // 62fd: ldc2_w 1.234835147857666
      // 6300: dastore
      // 6301: dup
      // 6302: sipush 217
      // 6305: ldc2_w 1.23604154586792
      // 6308: dastore
      // 6309: dup
      // 630a: sipush 218
      // 630d: ldc2_w 1.2372493743896484
      // 6310: dastore
      // 6311: dup
      // 6312: sipush 219
      // 6315: ldc2_w 1.2384581565856934
      // 6318: dastore
      // 6319: dup
      // 631a: sipush 220
      // 631d: ldc2_w 1.2396681308746338
      // 6320: dastore
      // 6321: dup
      // 6322: sipush 221
      // 6325: ldc2_w 1.2408792972564697
      // 6328: dastore
      // 6329: dup
      // 632a: sipush 222
      // 632d: ldc2_w 1.2420918941497803
      // 6330: dastore
      // 6331: dup
      // 6332: sipush 223
      // 6335: ldc2_w 1.2433054447174072
      // 6338: dastore
      // 6339: dup
      // 633a: sipush 224
      // 633d: ldc2_w 1.2445201873779297
      // 6340: dastore
      // 6341: dup
      // 6342: sipush 225
      // 6345: ldc2_w 1.2457361221313477
      // 6348: dastore
      // 6349: dup
      // 634a: sipush 226
      // 634d: ldc2_w 1.2469532489776611
      // 6350: dastore
      // 6351: dup
      // 6352: sipush 227
      // 6355: ldc2_w 1.2481715679168701
      // 6358: dastore
      // 6359: dup
      // 635a: sipush 228
      // 635d: ldc2_w 1.2493910789489746
      // 6360: dastore
      // 6361: dup
      // 6362: sipush 229
      // 6365: ldc2_w 1.2506117820739746
      // 6368: dastore
      // 6369: dup
      // 636a: sipush 230
      // 636d: ldc2_w 1.2518336772918701
      // 6370: dastore
      // 6371: dup
      // 6372: sipush 231
      // 6375: ldc2_w 1.2530567646026611
      // 6378: dastore
      // 6379: dup
      // 637a: sipush 232
      // 637d: ldc2_w 1.2542810440063477
      // 6380: dastore
      // 6381: dup
      // 6382: sipush 233
      // 6385: ldc2_w 1.2555065155029297
      // 6388: dastore
      // 6389: dup
      // 638a: sipush 234
      // 638d: ldc2_w 1.2567331790924072
      // 6390: dastore
      // 6391: dup
      // 6392: sipush 235
      // 6395: ldc2_w 1.2579610347747803
      // 6398: dastore
      // 6399: dup
      // 639a: sipush 236
      // 639d: ldc2_w 1.2591900825500488
      // 63a0: dastore
      // 63a1: dup
      // 63a2: sipush 237
      // 63a5: ldc2_w 1.260420322418213
      // 63a8: dastore
      // 63a9: dup
      // 63aa: sipush 238
      // 63ad: ldc2_w 1.2616519927978516
      // 63b0: dastore
      // 63b1: dup
      // 63b2: sipush 239
      // 63b5: ldc2_w 1.2628846168518066
      // 63b8: dastore
      // 63b9: dup
      // 63ba: sipush 240
      // 63bd: ldc2_w 1.2641184329986572
      // 63c0: dastore
      // 63c1: dup
      // 63c2: sipush 241
      // 63c5: ldc2_w 1.2653534412384033
      // 63c8: dastore
      // 63c9: dup
      // 63ca: sipush 242
      // 63cd: ldc2_w 1.266589879989624
      // 63d0: dastore
      // 63d1: dup
      // 63d2: sipush 243
      // 63d5: ldc2_w 1.2678272724151611
      // 63d8: dastore
      // 63d9: dup
      // 63da: sipush 244
      // 63dd: ldc2_w 1.2690660953521729
      // 63e0: dastore
      // 63e1: dup
      // 63e2: sipush 245
      // 63e5: ldc2_w 1.27030611038208
      // 63e8: dastore
      // 63e9: dup
      // 63ea: sipush 246
      // 63ed: ldc2_w 1.2715470790863037
      // 63f0: dastore
      // 63f1: dup
      // 63f2: sipush 247
      // 63f5: ldc2_w 1.272789478302002
      // 63f8: dastore
      // 63f9: dup
      // 63fa: sipush 248
      // 63fd: ldc2_w 1.2740330696105957
      // 6400: dastore
      // 6401: dup
      // 6402: sipush 249
      // 6405: ldc2_w 1.275277853012085
      // 6408: dastore
      // 6409: dup
      // 640a: sipush 250
      // 640d: ldc2_w 1.2765238285064697
      // 6410: dastore
      // 6411: dup
      // 6412: sipush 251
      // 6415: ldc2_w 1.27777099609375
      // 6418: dastore
      // 6419: dup
      // 641a: sipush 252
      // 641d: ldc2_w 1.2790195941925049
      // 6420: dastore
      // 6421: dup
      // 6422: sipush 253
      // 6425: ldc2_w 1.2802691459655762
      // 6428: dastore
      // 6429: dup
      // 642a: sipush 254
      // 642d: ldc2_w 1.281519889831543
      // 6430: dastore
      // 6431: dup
      // 6432: sipush 255
      // 6435: ldc2_w 1.2827720642089844
      // 6438: dastore
      // 6439: dup
      // 643a: sipush 256
      // 643d: ldc2_w 1.2840254306793213
      // 6440: dastore
      // 6441: dup
      // 6442: sipush 257
      // 6445: ldc2_w 1.2852799892425537
      // 6448: dastore
      // 6449: dup
      // 644a: sipush 258
      // 644d: ldc2_w 1.2865357398986816
      // 6450: dastore
      // 6451: dup
      // 6452: sipush 259
      // 6455: ldc2_w 1.287792682647705
      // 6458: dastore
      // 6459: dup
      // 645a: sipush 260
      // 645d: ldc2_w 1.2890510559082031
      // 6460: dastore
      // 6461: dup
      // 6462: sipush 261
      // 6465: ldc2_w 1.2903103828430176
      // 6468: dastore
      // 6469: dup
      // 646a: sipush 262
      // 646d: ldc2_w 1.2915711402893066
      // 6470: dastore
      // 6471: dup
      // 6472: sipush 263
      // 6475: ldc2_w 1.2928330898284912
      // 6478: dastore
      // 6479: dup
      // 647a: sipush 264
      // 647d: ldc2_w 1.2940962314605713
      // 6480: dastore
      // 6481: dup
      // 6482: sipush 265
      // 6485: ldc2_w 1.2953605651855469
      // 6488: dastore
      // 6489: dup
      // 648a: sipush 266
      // 648d: ldc2_w 1.296626091003418
      // 6490: dastore
      // 6491: dup
      // 6492: sipush 267
      // 6495: ldc2_w 1.2978930473327637
      // 6498: dastore
      // 6499: dup
      // 649a: sipush 268
      // 649d: ldc2_w 1.2991611957550049
      // 64a0: dastore
      // 64a1: dup
      // 64a2: sipush 269
      // 64a5: ldc2_w 1.3004305362701416
      // 64a8: dastore
      // 64a9: dup
      // 64aa: sipush 270
      // 64ad: ldc2_w 1.3017010688781738
      // 64b0: dastore
      // 64b1: dup
      // 64b2: sipush 271
      // 64b5: ldc2_w 1.3029727935791016
      // 64b8: dastore
      // 64b9: dup
      // 64ba: sipush 272
      // 64bd: ldc2_w 1.304245948791504
      // 64c0: dastore
      // 64c1: dup
      // 64c2: sipush 273
      // 64c5: ldc2_w 1.3055200576782227
      // 64c8: dastore
      // 64c9: dup
      // 64ca: sipush 274
      // 64cd: ldc2_w 1.3067958354949951
      // 64d0: dastore
      // 64d1: dup
      // 64d2: sipush 275
      // 64d5: ldc2_w 1.308072566986084
      // 64d8: dastore
      // 64d9: dup
      // 64da: sipush 276
      // 64dd: ldc2_w 1.3093504905700684
      // 64e0: dastore
      // 64e1: dup
      // 64e2: sipush 277
      // 64e5: ldc2_w 1.3106298446655273
      // 64e8: dastore
      // 64e9: dup
      // 64ea: sipush 278
      // 64ed: ldc2_w 1.3119103908538818
      // 64f0: dastore
      // 64f1: dup
      // 64f2: sipush 279
      // 64f5: ldc2_w 1.3131921291351318
      // 64f8: dastore
      // 64f9: dup
      // 64fa: sipush 280
      // 64fd: ldc2_w 1.3144752979278564
      // 6500: dastore
      // 6501: dup
      // 6502: sipush 281
      // 6505: ldc2_w 1.3157594203948975
      // 6508: dastore
      // 6509: dup
      // 650a: sipush 282
      // 650d: ldc2_w 1.317044973373413
      // 6510: dastore
      // 6511: dup
      // 6512: sipush 283
      // 6515: ldc2_w 1.3183319568634033
      // 6518: dastore
      // 6519: dup
      // 651a: sipush 284
      // 651d: ldc2_w 1.31961989402771
      // 6520: dastore
      // 6521: dup
      // 6522: sipush 285
      // 6525: ldc2_w 1.3209092617034912
      // 6528: dastore
      // 6529: dup
      // 652a: sipush 286
      // 652d: ldc2_w 1.322199821472168
      // 6530: dastore
      // 6531: dup
      // 6532: sipush 287
      // 6535: ldc2_w 1.3234915733337402
      // 6538: dastore
      // 6539: dup
      // 653a: sipush 288
      // 653d: ldc2_w 1.324784755706787
      // 6540: dastore
      // 6541: dup
      // 6542: sipush 289
      // 6545: ldc2_w 1.3260791301727295
      // 6548: dastore
      // 6549: dup
      // 654a: sipush 290
      // 654d: ldc2_w 1.3273746967315674
      // 6550: dastore
      // 6551: dup
      // 6552: sipush 291
      // 6555: ldc2_w 1.3286716938018799
      // 6558: dastore
      // 6559: dup
      // 655a: sipush 292
      // 655d: ldc2_w 1.329969882965088
      // 6560: dastore
      // 6561: dup
      // 6562: sipush 293
      // 6565: ldc2_w 1.3312692642211914
      // 6568: dastore
      // 6569: dup
      // 656a: sipush 294
      // 656d: ldc2_w 1.3325698375701904
      // 6570: dastore
      // 6571: dup
      // 6572: sipush 295
      // 6575: ldc2_w 1.333871841430664
      // 6578: dastore
      // 6579: dup
      // 657a: sipush 296
      // 657d: ldc2_w 1.3351752758026123
      // 6580: dastore
      // 6581: dup
      // 6582: sipush 297
      // 6585: ldc2_w 1.336479663848877
      // 6588: dastore
      // 6589: dup
      // 658a: sipush 298
      // 658d: ldc2_w 1.3377854824066162
      // 6590: dastore
      // 6591: dup
      // 6592: sipush 299
      // 6595: ldc2_w 1.339092493057251
      // 6598: dastore
      // 6599: dup
      // 659a: sipush 300
      // 659d: ldc2_w 1.3404009342193604
      // 65a0: dastore
      // 65a1: dup
      // 65a2: sipush 301
      // 65a5: ldc2_w 1.3417105674743652
      // 65a8: dastore
      // 65a9: dup
      // 65aa: sipush 302
      // 65ad: ldc2_w 1.3430213928222656
      // 65b0: dastore
      // 65b1: dup
      // 65b2: sipush 303
      // 65b5: ldc2_w 1.3443336486816406
      // 65b8: dastore
      // 65b9: dup
      // 65ba: sipush 304
      // 65bd: ldc2_w 1.3456470966339111
      // 65c0: dastore
      // 65c1: dup
      // 65c2: sipush 305
      // 65c5: ldc2_w 1.3469617366790771
      // 65c8: dastore
      // 65c9: dup
      // 65ca: sipush 306
      // 65cd: ldc2_w 1.3482778072357178
      // 65d0: dastore
      // 65d1: dup
      // 65d2: sipush 307
      // 65d5: ldc2_w 1.349595069885254
      // 65d8: dastore
      // 65d9: dup
      // 65da: sipush 308
      // 65dd: ldc2_w 1.3509137630462646
      // 65e0: dastore
      // 65e1: dup
      // 65e2: sipush 309
      // 65e5: ldc2_w 1.352233648300171
      // 65e8: dastore
      // 65e9: dup
      // 65ea: sipush 310
      // 65ed: ldc2_w 1.3535549640655518
      // 65f0: dastore
      // 65f1: dup
      // 65f2: sipush 311
      // 65f5: ldc2_w 1.3548774719238281
      // 65f8: dastore
      // 65f9: dup
      // 65fa: sipush 312
      // 65fd: ldc2_w 1.356201171875
      // 6600: dastore
      // 6601: dup
      // 6602: sipush 313
      // 6605: ldc2_w 1.3575263023376465
      // 6608: dastore
      // 6609: dup
      // 660a: sipush 314
      // 660d: ldc2_w 1.3588526248931885
      // 6610: dastore
      // 6611: dup
      // 6612: sipush 315
      // 6615: ldc2_w 1.360180139541626
      // 6618: dastore
      // 6619: dup
      // 661a: sipush 316
      // 661d: ldc2_w 1.361509084701538
      // 6620: dastore
      // 6621: dup
      // 6622: sipush 317
      // 6625: ldc2_w 1.3628394603729248
      // 6628: dastore
      // 6629: dup
      // 662a: sipush 318
      // 662d: ldc2_w 1.364171028137207
      // 6630: dastore
      // 6631: dup
      // 6632: sipush 319
      // 6635: ldc2_w 1.3655037879943848
      // 6638: dastore
      // 6639: dup
      // 663a: sipush 320
      // 663d: ldc2_w 1.366837978363037
      // 6640: dastore
      // 6641: dup
      // 6642: sipush 321
      // 6645: ldc2_w 1.368173360824585
      // 6648: dastore
      // 6649: dup
      // 664a: sipush 322
      // 664d: ldc2_w 1.3695101737976074
      // 6650: dastore
      // 6651: dup
      // 6652: sipush 323
      // 6655: ldc2_w 1.3708481788635254
      // 6658: dastore
      // 6659: dup
      // 665a: sipush 324
      // 665d: ldc2_w 1.372187614440918
      // 6660: dastore
      // 6661: dup
      // 6662: sipush 325
      // 6665: ldc2_w 1.373528242111206
      // 6668: dastore
      // 6669: dup
      // 666a: sipush 326
      // 666d: ldc2_w 1.3748703002929688
      // 6670: dastore
      // 6671: dup
      // 6672: sipush 327
      // 6675: ldc2_w 1.376213550567627
      // 6678: dastore
      // 6679: dup
      // 667a: sipush 328
      // 667d: ldc2_w 1.3775582313537598
      // 6680: dastore
      // 6681: dup
      // 6682: sipush 329
      // 6685: ldc2_w 1.378904104232788
      // 6688: dastore
      // 6689: dup
      // 668a: sipush 330
      // 668d: ldc2_w 1.380251407623291
      // 6690: dastore
      // 6691: dup
      // 6692: sipush 331
      // 6695: ldc2_w 1.3815999031066895
      // 6698: dastore
      // 6699: dup
      // 669a: sipush 332
      // 669d: ldc2_w 1.3829498291015625
      // 66a0: dastore
      // 66a1: dup
      // 66a2: sipush 333
      // 66a5: ldc2_w 1.384300947189331
      // 66a8: dastore
      // 66a9: dup
      // 66aa: sipush 334
      // 66ad: ldc2_w 1.3856534957885742
      // 66b0: dastore
      // 66b1: dup
      // 66b2: sipush 335
      // 66b5: ldc2_w 1.387007236480713
      // 66b8: dastore
      // 66b9: dup
      // 66ba: sipush 336
      // 66bd: ldc2_w 1.3883624076843262
      // 66c0: dastore
      // 66c1: dup
      // 66c2: sipush 337
      // 66c5: ldc2_w 1.389719009399414
      // 66c8: dastore
      // 66c9: dup
      // 66ca: sipush 338
      // 66cd: ldc2_w 1.3910768032073975
      // 66d0: dastore
      // 66d1: dup
      // 66d2: sipush 339
      // 66d5: ldc2_w 1.3924360275268555
      // 66d8: dastore
      // 66d9: dup
      // 66da: sipush 340
      // 66dd: ldc2_w 1.393796443939209
      // 66e0: dastore
      // 66e1: dup
      // 66e2: sipush 341
      // 66e5: ldc2_w 1.395158290863037
      // 66e8: dastore
      // 66e9: dup
      // 66ea: sipush 342
      // 66ed: ldc2_w 1.3965213298797607
      // 66f0: dastore
      // 66f1: dup
      // 66f2: sipush 343
      // 66f5: ldc2_w 1.397885799407959
      // 66f8: dastore
      // 66f9: dup
      // 66fa: sipush 344
      // 66fd: ldc2_w 1.3992514610290527
      // 6700: dastore
      // 6701: dup
      // 6702: sipush 345
      // 6705: ldc2_w 1.4006187915802002
      // 6708: dastore
      // 6709: dup
      // 670a: sipush 346
      // 670d: ldc2_w 1.401987075805664
      // 6710: dastore
      // 6711: dup
      // 6712: sipush 347
      // 6715: ldc2_w 1.4033570289611816
      // 6718: dastore
      // 6719: dup
      // 671a: sipush 348
      // 671d: ldc2_w 1.4047281742095947
      // 6720: dastore
      // 6721: dup
      // 6722: sipush 349
      // 6725: ldc2_w 1.4061005115509033
      // 6728: dastore
      // 6729: dup
      // 672a: sipush 350
      // 672d: ldc2_w 1.4074742794036865
      // 6730: dastore
      // 6731: dup
      // 6732: sipush 351
      // 6735: ldc2_w 1.4088494777679443
      // 6738: dastore
      // 6739: dup
      // 673a: sipush 352
      // 673d: ldc2_w 1.4102261066436768
      // 6740: dastore
      // 6741: dup
      // 6742: sipush 353
      // 6745: ldc2_w 1.4116039276123047
      // 6748: dastore
      // 6749: dup
      // 674a: sipush 354
      // 674d: ldc2_w 1.4129831790924072
      // 6750: dastore
      // 6751: dup
      // 6752: sipush 355
      // 6755: ldc2_w 1.4143636226654053
      // 6758: dastore
      // 6759: dup
      // 675a: sipush 356
      // 675d: ldc2_w 1.415745496749878
      // 6760: dastore
      // 6761: dup
      // 6762: sipush 357
      // 6765: ldc2_w 1.4171288013458252
      // 6768: dastore
      // 6769: dup
      // 676a: sipush 358
      // 676d: ldc2_w 1.418513298034668
      // 6770: dastore
      // 6771: dup
      // 6772: sipush 359
      // 6775: ldc2_w 1.4198992252349854
      // 6778: dastore
      // 6779: dup
      // 677a: sipush 360
      // 677d: ldc2_w 1.4212865829467773
      // 6780: dastore
      // 6781: dup
      // 6782: sipush 361
      // 6785: ldc2_w 1.4226751327514648
      // 6788: dastore
      // 6789: dup
      // 678a: sipush 362
      // 678d: ldc2_w 1.424065351486206
      // 6790: dastore
      // 6791: dup
      // 6792: sipush 363
      // 6795: ldc2_w 1.4254565238952637
      // 6798: dastore
      // 6799: dup
      // 679a: sipush 364
      // 679d: ldc2_w 1.426849365234375
      // 67a0: dastore
      // 67a1: dup
      // 67a2: sipush 365
      // 67a5: ldc2_w 1.4282433986663818
      // 67a8: dastore
      // 67a9: dup
      // 67aa: sipush 366
      // 67ad: ldc2_w 1.4296388626098633
      // 67b0: dastore
      // 67b1: dup
      // 67b2: sipush 367
      // 67b5: ldc2_w 1.4310357570648193
      // 67b8: dastore
      // 67b9: dup
      // 67ba: sipush 368
      // 67bd: ldc2_w 1.432433843612671
      // 67c0: dastore
      // 67c1: dup
      // 67c2: sipush 369
      // 67c5: ldc2_w 1.433833360671997
      // 67c8: dastore
      // 67c9: dup
      // 67ca: sipush 370
      // 67cd: ldc2_w 1.4352343082427979
      // 67d0: dastore
      // 67d1: dup
      // 67d2: sipush 371
      // 67d5: ldc2_w 1.4366366863250732
      // 67d8: dastore
      // 67d9: dup
      // 67da: sipush 372
      // 67dd: ldc2_w 1.4380402565002441
      // 67e0: dastore
      // 67e1: dup
      // 67e2: sipush 373
      // 67e5: ldc2_w 1.4394452571868896
      // 67e8: dastore
      // 67e9: dup
      // 67ea: sipush 374
      // 67ed: ldc2_w 1.4408516883850098
      // 67f0: dastore
      // 67f1: dup
      // 67f2: sipush 375
      // 67f5: ldc2_w 1.4422595500946045
      // 67f8: dastore
      // 67f9: dup
      // 67fa: sipush 376
      // 67fd: ldc2_w 1.4436686038970947
      // 6800: dastore
      // 6801: dup
      // 6802: sipush 377
      // 6805: ldc2_w 1.4450790882110596
      // 6808: dastore
      // 6809: dup
      // 680a: sipush 378
      // 680d: ldc2_w 1.446491003036499
      // 6810: dastore
      // 6811: dup
      // 6812: sipush 379
      // 6815: ldc2_w 1.447904348373413
      // 6818: dastore
      // 6819: dup
      // 681a: sipush 380
      // 681d: ldc2_w 1.4493188858032227
      // 6820: dastore
      // 6821: dup
      // 6822: sipush 381
      // 6825: ldc2_w 1.450735092163086
      // 6828: dastore
      // 6829: dup
      // 682a: sipush 382
      // 682d: ldc2_w 1.4521524906158447
      // 6830: dastore
      // 6831: dup
      // 6832: sipush 383
      // 6835: ldc2_w 1.4535713195800781
      // 6838: dastore
      // 6839: dup
      // 683a: sipush 384
      // 683d: ldc2_w 1.454991340637207
      // 6840: dastore
      // 6841: dup
      // 6842: sipush 385
      // 6845: ldc2_w 1.4564130306243896
      // 6848: dastore
      // 6849: dup
      // 684a: sipush 386
      // 684d: ldc2_w 1.4578359127044678
      // 6850: dastore
      // 6851: dup
      // 6852: sipush 387
      // 6855: ldc2_w 1.4592602252960205
      // 6858: dastore
      // 6859: dup
      // 685a: sipush 388
      // 685d: ldc2_w 1.460686206817627
      // 6860: dastore
      // 6861: dup
      // 6862: sipush 389
      // 6865: ldc2_w 1.4621131420135498
      // 6868: dastore
      // 6869: dup
      // 686a: sipush 390
      // 686d: ldc2_w 1.4635417461395264
      // 6870: dastore
      // 6871: dup
      // 6872: sipush 391
      // 6875: ldc2_w 1.4649717807769775
      // 6878: dastore
      // 6879: dup
      // 687a: sipush 392
      // 687d: ldc2_w 1.4664030075073242
      // 6880: dastore
      // 6881: dup
      // 6882: sipush 393
      // 6885: ldc2_w 1.4678359031677246
      // 6888: dastore
      // 6889: dup
      // 688a: sipush 394
      // 688d: ldc2_w 1.4692699909210205
      // 6890: dastore
      // 6891: dup
      // 6892: sipush 395
      // 6895: ldc2_w 1.470705509185791
      // 6898: dastore
      // 6899: dup
      // 689a: sipush 396
      // 689d: ldc2_w 1.4721424579620361
      // 68a0: dastore
      // 68a1: dup
      // 68a2: sipush 397
      // 68a5: ldc2_w 1.4735808372497559
      // 68a8: dastore
      // 68a9: dup
      // 68aa: sipush 398
      // 68ad: ldc2_w 1.475020408630371
      // 68b0: dastore
      // 68b1: dup
      // 68b2: sipush 399
      // 68b5: ldc2_w 1.47646164894104
      // 68b8: dastore
      // 68b9: dup
      // 68ba: sipush 400
      // 68bd: ldc2_w 1.4779040813446045
      // 68c0: dastore
      // 68c1: dup
      // 68c2: sipush 401
      // 68c5: ldc2_w 1.4793481826782227
      // 68c8: dastore
      // 68c9: dup
      // 68ca: sipush 402
      // 68cd: ldc2_w 1.4807934761047363
      // 68d0: dastore
      // 68d1: dup
      // 68d2: sipush 403
      // 68d5: ldc2_w 1.4822404384613037
      // 68d8: dastore
      // 68d9: dup
      // 68da: sipush 404
      // 68dd: ldc2_w 1.4836885929107666
      // 68e0: dastore
      // 68e1: dup
      // 68e2: sipush 405
      // 68e5: ldc2_w 1.485138177871704
      // 68e8: dastore
      // 68e9: dup
      // 68ea: sipush 406
      // 68ed: ldc2_w 1.4865891933441162
      // 68f0: dastore
      // 68f1: dup
      // 68f2: sipush 407
      // 68f5: ldc2_w 1.488041639328003
      // 68f8: dastore
      // 68f9: dup
      // 68fa: sipush 408
      // 68fd: ldc2_w 1.4894955158233643
      // 6900: dastore
      // 6901: dup
      // 6902: sipush 409
      // 6905: ldc2_w 1.4909508228302002
      // 6908: dastore
      // 6909: dup
      // 690a: sipush 410
      // 690d: ldc2_w 1.4924075603485107
      // 6910: dastore
      // 6911: dup
      // 6912: sipush 411
      // 6915: ldc2_w 1.493865728378296
      // 6918: dastore
      // 6919: dup
      // 691a: sipush 412
      // 691d: ldc2_w 1.4953253269195557
      // 6920: dastore
      // 6921: dup
      // 6922: sipush 413
      // 6925: ldc2_w 1.49678635597229
      // 6928: dastore
      // 6929: dup
      // 692a: sipush 414
      // 692d: ldc2_w 1.49824857711792
      // 6930: dastore
      // 6931: dup
      // 6932: sipush 415
      // 6935: ldc2_w 1.4997124671936035
      // 6938: dastore
      // 6939: dup
      // 693a: sipush 416
      // 693d: ldc2_w 1.5011777877807617
      // 6940: dastore
      // 6941: dup
      // 6942: sipush 417
      // 6945: ldc2_w 1.5026445388793945
      // 6948: dastore
      // 6949: dup
      // 694a: sipush 418
      // 694d: ldc2_w 1.504112720489502
      // 6950: dastore
      // 6951: dup
      // 6952: sipush 419
      // 6955: ldc2_w 1.505582332611084
      // 6958: dastore
      // 6959: dup
      // 695a: sipush 420
      // 695d: ldc2_w 1.5070531368255615
      // 6960: dastore
      // 6961: dup
      // 6962: sipush 421
      // 6965: ldc2_w 1.5085256099700928
      // 6968: dastore
      // 6969: dup
      // 696a: sipush 422
      // 696d: ldc2_w 1.5099995136260986
      // 6970: dastore
      // 6971: dup
      // 6972: sipush 423
      // 6975: ldc2_w 1.511474847793579
      // 6978: dastore
      // 6979: dup
      // 697a: sipush 424
      // 697d: ldc2_w 1.5129516124725342
      // 6980: dastore
      // 6981: dup
      // 6982: sipush 425
      // 6985: ldc2_w 1.5144298076629639
      // 6988: dastore
      // 6989: dup
      // 698a: sipush 426
      // 698d: ldc2_w 1.5159096717834473
      // 6990: dastore
      // 6991: dup
      // 6992: sipush 427
      // 6995: ldc2_w 1.5173907279968262
      // 6998: dastore
      // 6999: dup
      // 699a: sipush 428
      // 699d: ldc2_w 1.5188732147216797
      // 69a0: dastore
      // 69a1: dup
      // 69a2: sipush 429
      // 69a5: ldc2_w 1.5203571319580078
      // 69a8: dastore
      // 69a9: dup
      // 69aa: sipush 430
      // 69ad: ldc2_w 1.5218427181243896
      // 69b0: dastore
      // 69b1: dup
      // 69b2: sipush 431
      // 69b5: ldc2_w 1.523329496383667
      // 69b8: dastore
      // 69b9: dup
      // 69ba: sipush 432
      // 69bd: ldc2_w 1.524817943572998
      // 69c0: dastore
      // 69c1: dup
      // 69c2: sipush 433
      // 69c5: ldc2_w 1.5263078212738037
      // 69c8: dastore
      // 69c9: dup
      // 69ca: sipush 434
      // 69cd: ldc2_w 1.5277988910675049
      // 69d0: dastore
      // 69d1: dup
      // 69d2: sipush 435
      // 69d5: ldc2_w 1.5292916297912598
      // 69d8: dastore
      // 69d9: dup
      // 69da: sipush 436
      // 69dd: ldc2_w 1.5307857990264893
      // 69e0: dastore
      // 69e1: dup
      // 69e2: sipush 437
      // 69e5: ldc2_w 1.5322813987731934
      // 69e8: dastore
      // 69e9: dup
      // 69ea: sipush 438
      // 69ed: ldc2_w 1.5337786674499512
      // 69f0: dastore
      // 69f1: dup
      // 69f2: sipush 439
      // 69f5: ldc2_w 1.5352771282196045
      // 69f8: dastore
      // 69f9: dup
      // 69fa: sipush 440
      // 69fd: ldc2_w 1.5367772579193115
      // 6a00: dastore
      // 6a01: dup
      // 6a02: sipush 441
      // 6a05: ldc2_w 1.538278579711914
      // 6a08: dastore
      // 6a09: dup
      // 6a0a: sipush 442
      // 6a0d: ldc2_w 1.5397815704345703
      // 6a10: dastore
      // 6a11: dup
      // 6a12: sipush 443
      // 6a15: ldc2_w 1.5412859916687012
      // 6a18: dastore
      // 6a19: dup
      // 6a1a: sipush 444
      // 6a1d: ldc2_w 1.5427920818328857
      // 6a20: dastore
      // 6a21: dup
      // 6a22: sipush 445
      // 6a25: ldc2_w 1.5442993640899658
      // 6a28: dastore
      // 6a29: dup
      // 6a2a: sipush 446
      // 6a2d: ldc2_w 1.5458080768585205
      // 6a30: dastore
      // 6a31: dup
      // 6a32: sipush 447
      // 6a35: ldc2_w 1.547318458557129
      // 6a38: dastore
      // 6a39: dup
      // 6a3a: sipush 448
      // 6a3d: ldc2_w 1.548830270767212
      // 6a40: dastore
      // 6a41: dup
      // 6a42: sipush 449
      // 6a45: ldc2_w 1.5503435134887695
      // 6a48: dastore
      // 6a49: dup
      // 6a4a: sipush 450
      // 6a4d: ldc2_w 1.5518584251403809
      // 6a50: dastore
      // 6a51: dup
      // 6a52: sipush 451
      // 6a55: ldc2_w 1.5533745288848877
      // 6a58: dastore
      // 6a59: dup
      // 6a5a: sipush 452
      // 6a5d: ldc2_w 1.5548923015594482
      // 6a60: dastore
      // 6a61: dup
      // 6a62: sipush 453
      // 6a65: ldc2_w 1.5564115047454834
      // 6a68: dastore
      // 6a69: dup
      // 6a6a: sipush 454
      // 6a6d: ldc2_w 1.5579321384429932
      // 6a70: dastore
      // 6a71: dup
      // 6a72: sipush 455
      // 6a75: ldc2_w 1.5594542026519775
      // 6a78: dastore
      // 6a79: dup
      // 6a7a: sipush 456
      // 6a7d: ldc2_w 1.5609779357910156
      // 6a80: dastore
      // 6a81: dup
      // 6a82: sipush 457
      // 6a85: ldc2_w 1.5625030994415283
      // 6a88: dastore
      // 6a89: dup
      // 6a8a: sipush 458
      // 6a8d: ldc2_w 1.5640296936035156
      // 6a90: dastore
      // 6a91: dup
      // 6a92: sipush 459
      // 6a95: ldc2_w 1.5655577182769775
      // 6a98: dastore
      // 6a99: dup
      // 6a9a: sipush 460
      // 6a9d: ldc2_w 1.5670874118804932
      // 6aa0: dastore
      // 6aa1: dup
      // 6aa2: sipush 461
      // 6aa5: ldc2_w 1.5686185359954834
      // 6aa8: dastore
      // 6aa9: dup
      // 6aaa: sipush 462
      // 6aad: ldc2_w 1.5701510906219482
      // 6ab0: dastore
      // 6ab1: dup
      // 6ab2: sipush 463
      // 6ab5: ldc2_w 1.5716853141784668
      // 6ab8: dastore
      // 6ab9: dup
      // 6aba: sipush 464
      // 6abd: ldc2_w 1.5732207298278809
      // 6ac0: dastore
      // 6ac1: dup
      // 6ac2: sipush 465
      // 6ac5: ldc2_w 1.5747578144073486
      // 6ac8: dastore
      // 6ac9: dup
      // 6aca: sipush 466
      // 6acd: ldc2_w 1.5762965679168701
      // 6ad0: dastore
      // 6ad1: dup
      // 6ad2: sipush 467
      // 6ad5: ldc2_w 1.577836513519287
      // 6ad8: dastore
      // 6ad9: dup
      // 6ada: sipush 468
      // 6add: ldc2_w 1.5793781280517578
      // 6ae0: dastore
      // 6ae1: dup
      // 6ae2: sipush 469
      // 6ae5: ldc2_w 1.5809214115142822
      // 6ae8: dastore
      // 6ae9: dup
      // 6aea: sipush 470
      // 6aed: ldc2_w 1.5824658870697021
      // 6af0: dastore
      // 6af1: dup
      // 6af2: sipush 471
      // 6af5: ldc2_w 1.5840120315551758
      // 6af8: dastore
      // 6af9: dup
      // 6afa: sipush 472
      // 6afd: ldc2_w 1.5855598449707031
      // 6b00: dastore
      // 6b01: dup
      // 6b02: sipush 473
      // 6b05: ldc2_w 1.587108850479126
      // 6b08: dastore
      // 6b09: dup
      // 6b0a: sipush 474
      // 6b0d: ldc2_w 1.5886595249176025
      // 6b10: dastore
      // 6b11: dup
      // 6b12: sipush 475
      // 6b15: ldc2_w 1.5902118682861328
      // 6b18: dastore
      // 6b19: dup
      // 6b1a: sipush 476
      // 6b1d: ldc2_w 1.5917654037475586
      // 6b20: dastore
      // 6b21: dup
      // 6b22: sipush 477
      // 6b25: ldc2_w 1.593320608139038
      // 6b28: dastore
      // 6b29: dup
      // 6b2a: sipush 478
      // 6b2d: ldc2_w 1.5948774814605713
      // 6b30: dastore
      // 6b31: dup
      // 6b32: sipush 479
      // 6b35: ldc2_w 1.596435785293579
      // 6b38: dastore
      // 6b39: dup
      // 6b3a: sipush 480
      // 6b3d: ldc2_w 1.5979955196380615
      // 6b40: dastore
      // 6b41: dup
      // 6b42: sipush 481
      // 6b45: ldc2_w 1.5995566844940186
      // 6b48: dastore
      // 6b49: dup
      // 6b4a: sipush 482
      // 6b4d: ldc2_w 1.6011195182800293
      // 6b50: dastore
      // 6b51: dup
      // 6b52: sipush 483
      // 6b55: ldc2_w 1.6026840209960938
      // 6b58: dastore
      // 6b59: dup
      // 6b5a: sipush 484
      // 6b5d: ldc2_w 1.6042497158050537
      // 6b60: dastore
      // 6b61: dup
      // 6b62: sipush 485
      // 6b65: ldc2_w 1.6058173179626465
      // 6b68: dastore
      // 6b69: dup
      // 6b6a: sipush 486
      // 6b6d: ldc2_w 1.6073861122131348
      // 6b70: dastore
      // 6b71: dup
      // 6b72: sipush 487
      // 6b75: ldc2_w 1.6089565753936768
      // 6b78: dastore
      // 6b79: dup
      // 6b7a: sipush 488
      // 6b7d: ldc2_w 1.6105287075042725
      // 6b80: dastore
      // 6b81: dup
      // 6b82: sipush 489
      // 6b85: ldc2_w 1.6121022701263428
      // 6b88: dastore
      // 6b89: dup
      // 6b8a: sipush 490
      // 6b8d: ldc2_w 1.6136772632598877
      // 6b90: dastore
      // 6b91: dup
      // 6b92: sipush 491
      // 6b95: ldc2_w 1.6152539253234863
      // 6b98: dastore
      // 6b99: dup
      // 6b9a: sipush 492
      // 6b9d: ldc2_w 1.6168320178985596
      // 6ba0: dastore
      // 6ba1: dup
      // 6ba2: sipush 493
      // 6ba5: ldc2_w 1.6184117794036865
      // 6ba8: dastore
      // 6ba9: dup
      // 6baa: sipush 494
      // 6bad: ldc2_w 1.619992971420288
      // 6bb0: dastore
      // 6bb1: dup
      // 6bb2: sipush 495
      // 6bb5: ldc2_w 1.6215758323669434
      // 6bb8: dastore
      // 6bb9: dup
      // 6bba: sipush 496
      // 6bbd: ldc2_w 1.6231601238250732
      // 6bc0: dastore
      // 6bc1: dup
      // 6bc2: sipush 497
      // 6bc5: ldc2_w 1.6247460842132568
      // 6bc8: dastore
      // 6bc9: dup
      // 6bca: sipush 498
      // 6bcd: ldc2_w 1.626333475112915
      // 6bd0: dastore
      // 6bd1: dup
      // 6bd2: sipush 499
      // 6bd5: ldc2_w 1.627922534942627
      // 6bd8: dastore
      // 6bd9: dup
      // 6bda: sipush 500
      // 6bdd: ldc2_w 1.6295130252838135
      // 6be0: dastore
      // 6be1: dup
      // 6be2: sipush 501
      // 6be5: ldc2_w 1.6311051845550537
      // 6be8: dastore
      // 6be9: dup
      // 6bea: sipush 502
      // 6bed: ldc2_w 1.6326987743377686
      // 6bf0: dastore
      // 6bf1: dup
      // 6bf2: sipush 503
      // 6bf5: ldc2_w 1.634294033050537
      // 6bf8: dastore
      // 6bf9: dup
      // 6bfa: sipush 504
      // 6bfd: ldc2_w 1.6358907222747803
      // 6c00: dastore
      // 6c01: dup
      // 6c02: sipush 505
      // 6c05: ldc2_w 1.6374890804290771
      // 6c08: dastore
      // 6c09: dup
      // 6c0a: sipush 506
      // 6c0d: ldc2_w 1.6390891075134277
      // 6c10: dastore
      // 6c11: dup
      // 6c12: sipush 507
      // 6c15: ldc2_w 1.640690565109253
      // 6c18: dastore
      // 6c19: dup
      // 6c1a: sipush 508
      // 6c1d: ldc2_w 1.6422934532165527
      // 6c20: dastore
      // 6c21: dup
      // 6c22: sipush 509
      // 6c25: ldc2_w 1.6438980102539062
      // 6c28: dastore
      // 6c29: dup
      // 6c2a: sipush 510
      // 6c2d: ldc2_w 1.6455042362213135
      // 6c30: dastore
      // 6c31: dup
      // 6c32: sipush 511
      // 6c35: ldc2_w 1.6471118927001953
      // 6c38: dastore
      // 6c39: dup
      // 6c3a: sipush 512
      // 6c3d: ldc2_w 1.6487212181091309
      // 6c40: dastore
      // 6c41: dup
      // 6c42: sipush 513
      // 6c45: ldc2_w 1.6503322124481201
      // 6c48: dastore
      // 6c49: dup
      // 6c4a: sipush 514
      // 6c4d: ldc2_w 1.651944637298584
      // 6c50: dastore
      // 6c51: dup
      // 6c52: sipush 515
      // 6c55: ldc2_w 1.6535584926605225
      // 6c58: dastore
      // 6c59: dup
      // 6c5a: sipush 516
      // 6c5d: ldc2_w 1.6551742553710938
      // 6c60: dastore
      // 6c61: dup
      // 6c62: sipush 517
      // 6c65: ldc2_w 1.6567914485931396
      // 6c68: dastore
      // 6c69: dup
      // 6c6a: sipush 518
      // 6c6d: ldc2_w 1.6584100723266602
      // 6c70: dastore
      // 6c71: dup
      // 6c72: sipush 519
      // 6c75: ldc2_w 1.6600303649902344
      // 6c78: dastore
      // 6c79: dup
      // 6c7a: sipush 520
      // 6c7d: ldc2_w 1.6616523265838623
      // 6c80: dastore
      // 6c81: dup
      // 6c82: sipush 521
      // 6c85: ldc2_w 1.663275957107544
      // 6c88: dastore
      // 6c89: dup
      // 6c8a: sipush 522
      // 6c8d: ldc2_w 1.6649010181427002
      // 6c90: dastore
      // 6c91: dup
      // 6c92: sipush 523
      // 6c95: ldc2_w 1.666527509689331
      // 6c98: dastore
      // 6c99: dup
      // 6c9a: sipush 524
      // 6c9d: ldc2_w 1.6681559085845947
      // 6ca0: dastore
      // 6ca1: dup
      // 6ca2: sipush 525
      // 6ca5: ldc2_w 1.669785737991333
      // 6ca8: dastore
      // 6ca9: dup
      // 6caa: sipush 526
      // 6cad: ldc2_w 1.671417236328125
      // 6cb0: dastore
      // 6cb1: dup
      // 6cb2: sipush 527
      // 6cb5: ldc2_w 1.6730501651763916
      // 6cb8: dastore
      // 6cb9: dup
      // 6cba: sipush 528
      // 6cbd: ldc2_w 1.674684762954712
      // 6cc0: dastore
      // 6cc1: dup
      // 6cc2: sipush 529
      // 6cc5: ldc2_w 1.676321029663086
      // 6cc8: dastore
      // 6cc9: dup
      // 6cca: sipush 530
      // 6ccd: ldc2_w 1.6779589653015137
      // 6cd0: dastore
      // 6cd1: dup
      // 6cd2: sipush 531
      // 6cd5: ldc2_w 1.679598331451416
      // 6cd8: dastore
      // 6cd9: dup
      // 6cda: sipush 532
      // 6cdd: ldc2_w 1.681239366531372
      // 6ce0: dastore
      // 6ce1: dup
      // 6ce2: sipush 533
      // 6ce5: ldc2_w 1.6828820705413818
      // 6ce8: dastore
      // 6ce9: dup
      // 6cea: sipush 534
      // 6ced: ldc2_w 1.6845262050628662
      // 6cf0: dastore
      // 6cf1: dup
      // 6cf2: sipush 535
      // 6cf5: ldc2_w 1.6861720085144043
      // 6cf8: dastore
      // 6cf9: dup
      // 6cfa: sipush 536
      // 6cfd: ldc2_w 1.687819480895996
      // 6d00: dastore
      // 6d01: dup
      // 6d02: sipush 537
      // 6d05: ldc2_w 1.6894686222076416
      // 6d08: dastore
      // 6d09: dup
      // 6d0a: sipush 538
      // 6d0d: ldc2_w 1.6911191940307617
      // 6d10: dastore
      // 6d11: dup
      // 6d12: sipush 539
      // 6d15: ldc2_w 1.6927716732025146
      // 6d18: dastore
      // 6d19: dup
      // 6d1a: sipush 540
      // 6d1d: ldc2_w 1.6944255828857422
      // 6d20: dastore
      // 6d21: dup
      // 6d22: sipush 541
      // 6d25: ldc2_w 1.6960809230804443
      // 6d28: dastore
      // 6d29: dup
      // 6d2a: sipush 542
      // 6d2d: ldc2_w 1.6977381706237793
      // 6d30: dastore
      // 6d31: dup
      // 6d32: sipush 543
      // 6d35: ldc2_w 1.6993968486785889
      // 6d38: dastore
      // 6d39: dup
      // 6d3a: sipush 544
      // 6d3d: ldc2_w 1.7010571956634521
      // 6d40: dastore
      // 6d41: dup
      // 6d42: sipush 545
      // 6d45: ldc2_w 1.7027192115783691
      // 6d48: dastore
      // 6d49: dup
      // 6d4a: sipush 546
      // 6d4d: ldc2_w 1.7043828964233398
      // 6d50: dastore
      // 6d51: dup
      // 6d52: sipush 547
      // 6d55: ldc2_w 1.7060482501983643
      // 6d58: dastore
      // 6d59: dup
      // 6d5a: sipush 548
      // 6d5d: ldc2_w 1.7077150344848633
      // 6d60: dastore
      // 6d61: dup
      // 6d62: sipush 549
      // 6d65: ldc2_w 1.709383487701416
      // 6d68: dastore
      // 6d69: dup
      // 6d6a: sipush 550
      // 6d6d: ldc2_w 1.7110536098480225
      // 6d70: dastore
      // 6d71: dup
      // 6d72: sipush 551
      // 6d75: ldc2_w 1.7127254009246826
      // 6d78: dastore
      // 6d79: dup
      // 6d7a: sipush 552
      // 6d7d: ldc2_w 1.7143988609313965
      // 6d80: dastore
      // 6d81: dup
      // 6d82: sipush 553
      // 6d85: ldc2_w 1.716073989868164
      // 6d88: dastore
      // 6d89: dup
      // 6d8a: sipush 554
      // 6d8d: ldc2_w 1.7177505493164062
      // 6d90: dastore
      // 6d91: dup
      // 6d92: sipush 555
      // 6d95: ldc2_w 1.7194287776947021
      // 6d98: dastore
      // 6d99: dup
      // 6d9a: sipush 556
      // 6d9d: ldc2_w 1.7211089134216309
      // 6da0: dastore
      // 6da1: dup
      // 6da2: sipush 557
      // 6da5: ldc2_w 1.7227904796600342
      // 6da8: dastore
      // 6da9: dup
      // 6daa: sipush 558
      // 6dad: ldc2_w 1.7244737148284912
      // 6db0: dastore
      // 6db1: dup
      // 6db2: sipush 559
      // 6db5: ldc2_w 1.726158618927002
      // 6db8: dastore
      // 6db9: dup
      // 6dba: sipush 560
      // 6dbd: ldc2_w 1.7278449535369873
      // 6dc0: dastore
      // 6dc1: dup
      // 6dc2: sipush 561
      // 6dc5: ldc2_w 1.7295331954956055
      // 6dc8: dastore
      // 6dc9: dup
      // 6dca: sipush 562
      // 6dcd: ldc2_w 1.7312231063842773
      // 6dd0: dastore
      // 6dd1: dup
      // 6dd2: sipush 563
      // 6dd5: ldc2_w 1.7329144477844238
      // 6dd8: dastore
      // 6dd9: dup
      // 6dda: sipush 564
      // 6ddd: ldc2_w 1.7346076965332031
      // 6de0: dastore
      // 6de1: dup
      // 6de2: sipush 565
      // 6de5: ldc2_w 1.736302375793457
      // 6de8: dastore
      // 6de9: dup
      // 6dea: sipush 566
      // 6ded: ldc2_w 1.7379989624023438
      // 6df0: dastore
      // 6df1: dup
      // 6df2: sipush 567
      // 6df5: ldc2_w 1.739696979522705
      // 6df8: dastore
      // 6df9: dup
      // 6dfa: sipush 568
      // 6dfd: ldc2_w 1.7413966655731201
      // 6e00: dastore
      // 6e01: dup
      // 6e02: sipush 569
      // 6e05: ldc2_w 1.7430980205535889
      // 6e08: dastore
      // 6e09: dup
      // 6e0a: sipush 570
      // 6e0d: ldc2_w 1.7448012828826904
      // 6e10: dastore
      // 6e11: dup
      // 6e12: sipush 571
      // 6e15: ldc2_w 1.7465059757232666
      // 6e18: dastore
      // 6e19: dup
      // 6e1a: sipush 572
      // 6e1d: ldc2_w 1.7482123374938965
      // 6e20: dastore
      // 6e21: dup
      // 6e22: sipush 573
      // 6e25: ldc2_w 1.74992036819458
      // 6e28: dastore
      // 6e29: dup
      // 6e2a: sipush 574
      // 6e2d: ldc2_w 1.7516300678253174
      // 6e30: dastore
      // 6e31: dup
      // 6e32: sipush 575
      // 6e35: ldc2_w 1.7533416748046875
      // 6e38: dastore
      // 6e39: dup
      // 6e3a: sipush 576
      // 6e3d: ldc2_w 1.7550547122955322
      // 6e40: dastore
      // 6e41: dup
      // 6e42: sipush 577
      // 6e45: ldc2_w 1.7567694187164307
      // 6e48: dastore
      // 6e49: dup
      // 6e4a: sipush 578
      // 6e4d: ldc2_w 1.7584857940673828
      // 6e50: dastore
      // 6e51: dup
      // 6e52: sipush 579
      // 6e55: ldc2_w 1.7602040767669678
      // 6e58: dastore
      // 6e59: dup
      // 6e5a: sipush 580
      // 6e5d: ldc2_w 1.7619237899780273
      // 6e60: dastore
      // 6e61: dup
      // 6e62: sipush 581
      // 6e65: ldc2_w 1.7636451721191406
      // 6e68: dastore
      // 6e69: dup
      // 6e6a: sipush 582
      // 6e6d: ldc2_w 1.7653684616088867
      // 6e70: dastore
      // 6e71: dup
      // 6e72: sipush 583
      // 6e75: ldc2_w 1.7670931816101074
      // 6e78: dastore
      // 6e79: dup
      // 6e7a: sipush 584
      // 6e7d: ldc2_w 1.768819808959961
      // 6e80: dastore
      // 6e81: dup
      // 6e82: sipush 585
      // 6e85: ldc2_w 1.770547866821289
      // 6e88: dastore
      // 6e89: dup
      // 6e8a: sipush 586
      // 6e8d: ldc2_w 1.77227783203125
      // 6e90: dastore
      // 6e91: dup
      // 6e92: sipush 587
      // 6e95: ldc2_w 1.7740094661712646
      // 6e98: dastore
      // 6e99: dup
      // 6e9a: sipush 588
      // 6e9d: ldc2_w 1.775742769241333
      // 6ea0: dastore
      // 6ea1: dup
      // 6ea2: sipush 589
      // 6ea5: ldc2_w 1.777477741241455
      // 6ea8: dastore
      // 6ea9: dup
      // 6eaa: sipush 590
      // 6ead: ldc2_w 1.7792143821716309
      // 6eb0: dastore
      // 6eb1: dup
      // 6eb2: sipush 591
      // 6eb5: ldc2_w 1.7809526920318604
      // 6eb8: dastore
      // 6eb9: dup
      // 6eba: sipush 592
      // 6ebd: ldc2_w 1.7826926708221436
      // 6ec0: dastore
      // 6ec1: dup
      // 6ec2: sipush 593
      // 6ec5: ldc2_w 1.7844345569610596
      // 6ec8: dastore
      // 6ec9: dup
      // 6eca: sipush 594
      // 6ecd: ldc2_w 1.7861778736114502
      // 6ed0: dastore
      // 6ed1: dup
      // 6ed2: sipush 595
      // 6ed5: ldc2_w 1.7879230976104736
      // 6ed8: dastore
      // 6ed9: dup
      // 6eda: sipush 596
      // 6edd: ldc2_w 1.7896699905395508
      // 6ee0: dastore
      // 6ee1: dup
      // 6ee2: sipush 597
      // 6ee5: ldc2_w 1.7914185523986816
      // 6ee8: dastore
      // 6ee9: dup
      // 6eea: sipush 598
      // 6eed: ldc2_w 1.7931687831878662
      // 6ef0: dastore
      // 6ef1: dup
      // 6ef2: sipush 599
      // 6ef5: ldc2_w 1.7949209213256836
      // 6ef8: dastore
      // 6ef9: dup
      // 6efa: sipush 600
      // 6efd: ldc2_w 1.7966744899749756
      // 6f00: dastore
      // 6f01: dup
      // 6f02: sipush 601
      // 6f05: ldc2_w 1.7984299659729004
      // 6f08: dastore
      // 6f09: dup
      // 6f0a: sipush 602
      // 6f0d: ldc2_w 1.800187110900879
      // 6f10: dastore
      // 6f11: dup
      // 6f12: sipush 603
      // 6f15: ldc2_w 1.8019459247589111
      // 6f18: dastore
      // 6f19: dup
      // 6f1a: sipush 604
      // 6f1d: ldc2_w 1.8037066459655762
      // 6f20: dastore
      // 6f21: dup
      // 6f22: sipush 605
      // 6f25: ldc2_w 1.8054687976837158
      // 6f28: dastore
      // 6f29: dup
      // 6f2a: sipush 606
      // 6f2d: ldc2_w 1.8072328567504883
      // 6f30: dastore
      // 6f31: dup
      // 6f32: sipush 607
      // 6f35: ldc2_w 1.8089985847473145
      // 6f38: dastore
      // 6f39: dup
      // 6f3a: sipush 608
      // 6f3d: ldc2_w 1.8107659816741943
      // 6f40: dastore
      // 6f41: dup
      // 6f42: sipush 609
      // 6f45: ldc2_w 1.812535285949707
      // 6f48: dastore
      // 6f49: dup
      // 6f4a: sipush 610
      // 6f4d: ldc2_w 1.8143062591552734
      // 6f50: dastore
      // 6f51: dup
      // 6f52: sipush 611
      // 6f55: ldc2_w 1.8160789012908936
      // 6f58: dastore
      // 6f59: dup
      // 6f5a: sipush 612
      // 6f5d: ldc2_w 1.8178532123565674
      // 6f60: dastore
      // 6f61: dup
      // 6f62: sipush 613
      // 6f65: ldc2_w 1.819629430770874
      // 6f68: dastore
      // 6f69: dup
      // 6f6a: sipush 614
      // 6f6d: ldc2_w 1.8214070796966553
      // 6f70: dastore
      // 6f71: dup
      // 6f72: sipush 615
      // 6f75: ldc2_w 1.8231868743896484
      // 6f78: dastore
      // 6f79: dup
      // 6f7a: sipush 616
      // 6f7d: ldc2_w 1.8249680995941162
      // 6f80: dastore
      // 6f81: dup
      // 6f82: sipush 617
      // 6f85: ldc2_w 1.8267512321472168
      // 6f88: dastore
      // 6f89: dup
      // 6f8a: sipush 618
      // 6f8d: ldc2_w 1.828536033630371
      // 6f90: dastore
      // 6f91: dup
      // 6f92: sipush 619
      // 6f95: ldc2_w 1.830322504043579
      // 6f98: dastore
      // 6f99: dup
      // 6f9a: sipush 620
      // 6f9d: ldc2_w 1.83211088180542
      // 6fa0: dastore
      // 6fa1: dup
      // 6fa2: sipush 621
      // 6fa5: ldc2_w 1.8339009284973145
      // 6fa8: dastore
      // 6fa9: dup
      // 6faa: sipush 622
      // 6fad: ldc2_w 1.8356926441192627
      // 6fb0: dastore
      // 6fb1: dup
      // 6fb2: sipush 623
      // 6fb5: ldc2_w 1.8374862670898438
      // 6fb8: dastore
      // 6fb9: dup
      // 6fba: sipush 624
      // 6fbd: ldc2_w 1.8392815589904785
      // 6fc0: dastore
      // 6fc1: dup
      // 6fc2: sipush 625
      // 6fc5: ldc2_w 1.841078519821167
      // 6fc8: dastore
      // 6fc9: dup
      // 6fca: sipush 626
      // 6fcd: ldc2_w 1.8428773880004883
      // 6fd0: dastore
      // 6fd1: dup
      // 6fd2: sipush 627
      // 6fd5: ldc2_w 1.8446779251098633
      // 6fd8: dastore
      // 6fd9: dup
      // 6fda: sipush 628
      // 6fdd: ldc2_w 1.846480131149292
      // 6fe0: dastore
      // 6fe1: dup
      // 6fe2: sipush 629
      // 6fe5: ldc2_w 1.8482842445373535
      // 6fe8: dastore
      // 6fe9: dup
      // 6fea: sipush 630
      // 6fed: ldc2_w 1.8500902652740479
      // 6ff0: dastore
      // 6ff1: dup
      // 6ff2: sipush 631
      // 6ff5: ldc2_w 1.8518977165222168
      // 6ff8: dastore
      // 6ff9: dup
      // 6ffa: sipush 632
      // 6ffd: ldc2_w 1.8537070751190186
      // 7000: dastore
      // 7001: dup
      // 7002: sipush 633
      // 7005: ldc2_w 1.8555183410644531
      // 7008: dastore
      // 7009: dup
      // 700a: sipush 634
      // 700d: ldc2_w 1.8573312759399414
      // 7010: dastore
      // 7011: dup
      // 7012: sipush 635
      // 7015: ldc2_w 1.8591458797454834
      // 7018: dastore
      // 7019: dup
      // 701a: sipush 636
      // 701d: ldc2_w 1.8609623908996582
      // 7020: dastore
      // 7021: dup
      // 7022: sipush 637
      // 7025: ldc2_w 1.8627805709838867
      // 7028: dastore
      // 7029: dup
      // 702a: sipush 638
      // 702d: ldc2_w 1.864600658416748
      // 7030: dastore
      // 7031: dup
      // 7032: sipush 639
      // 7035: ldc2_w 1.866422414779663
      // 7038: dastore
      // 7039: dup
      // 703a: sipush 640
      // 703d: ldc2_w 1.8682458400726318
      // 7040: dastore
      // 7041: dup
      // 7042: sipush 641
      // 7045: ldc2_w 1.8700714111328125
      // 7048: dastore
      // 7049: dup
      // 704a: sipush 642
      // 704d: ldc2_w 1.8718984127044678
      // 7050: dastore
      // 7051: dup
      // 7052: sipush 643
      // 7055: ldc2_w 1.8737273216247559
      // 7058: dastore
      // 7059: dup
      // 705a: sipush 644
      // 705d: ldc2_w 1.8755581378936768
      // 7060: dastore
      // 7061: dup
      // 7062: sipush 645
      // 7065: ldc2_w 1.8773906230926514
      // 7068: dastore
      // 7069: dup
      // 706a: sipush 646
      // 706d: ldc2_w 1.8792247772216797
      // 7070: dastore
      // 7071: dup
      // 7072: sipush 647
      // 7075: ldc2_w 1.8810608386993408
      // 7078: dastore
      // 7079: dup
      // 707a: sipush 648
      // 707d: ldc2_w 1.8828988075256348
      // 7080: dastore
      // 7081: dup
      // 7082: sipush 649
      // 7085: ldc2_w 1.8847384452819824
      // 7088: dastore
      // 7089: dup
      // 708a: sipush 650
      // 708d: ldc2_w 1.886579990386963
      // 7090: dastore
      // 7091: dup
      // 7092: sipush 651
      // 7095: ldc2_w 1.888423204421997
      // 7098: dastore
      // 7099: dup
      // 709a: sipush 652
      // 709d: ldc2_w 1.890268325805664
      // 70a0: dastore
      // 70a1: dup
      // 70a2: sipush 653
      // 70a5: ldc2_w 1.8921151161193848
      // 70a8: dastore
      // 70a9: dup
      // 70aa: sipush 654
      // 70ad: ldc2_w 1.8939638137817383
      // 70b0: dastore
      // 70b1: dup
      // 70b2: sipush 655
      // 70b5: ldc2_w 1.8958141803741455
      // 70b8: dastore
      // 70b9: dup
      // 70ba: sipush 656
      // 70bd: ldc2_w 1.8976664543151855
      // 70c0: dastore
      // 70c1: dup
      // 70c2: sipush 657
      // 70c5: ldc2_w 1.8995206356048584
      // 70c8: dastore
      // 70c9: dup
      // 70ca: sipush 658
      // 70cd: ldc2_w 1.901376485824585
      // 70d0: dastore
      // 70d1: dup
      // 70d2: sipush 659
      // 70d5: ldc2_w 1.9032342433929443
      // 70d8: dastore
      // 70d9: dup
      // 70da: sipush 660
      // 70dd: ldc2_w 1.9050939083099365
      // 70e0: dastore
      // 70e1: dup
      // 70e2: sipush 661
      // 70e5: ldc2_w 1.9069552421569824
      // 70e8: dastore
      // 70e9: dup
      // 70ea: sipush 662
      // 70ed: ldc2_w 1.908818244934082
      // 70f0: dastore
      // 70f1: dup
      // 70f2: sipush 663
      // 70f5: ldc2_w 1.9106833934783936
      // 70f8: dastore
      // 70f9: dup
      // 70fa: sipush 664
      // 70fd: ldc2_w 1.9125502109527588
      // 7100: dastore
      // 7101: dup
      // 7102: sipush 665
      // 7105: ldc2_w 1.9144186973571777
      // 7108: dastore
      // 7109: dup
      // 710a: sipush 666
      // 710d: ldc2_w 1.9162893295288086
      // 7110: dastore
      // 7111: dup
      // 7112: sipush 667
      // 7115: ldc2_w 1.9181616306304932
      // 7118: dastore
      // 7119: dup
      // 711a: sipush 668
      // 711d: ldc2_w 1.9200356006622314
      // 7120: dastore
      // 7121: dup
      // 7122: sipush 669
      // 7125: ldc2_w 1.9219114780426025
      // 7128: dastore
      // 7129: dup
      // 712a: sipush 670
      // 712d: ldc2_w 1.9237892627716064
      // 7130: dastore
      // 7131: dup
      // 7132: sipush 671
      // 7135: ldc2_w 1.9256689548492432
      // 7138: dastore
      // 7139: dup
      // 713a: sipush 672
      // 713d: ldc2_w 1.9275505542755127
      // 7140: dastore
      // 7141: dup
      // 7142: sipush 673
      // 7145: ldc2_w 1.929433822631836
      // 7148: dastore
      // 7149: dup
      // 714a: sipush 674
      // 714d: ldc2_w 1.931318759918213
      // 7150: dastore
      // 7151: dup
      // 7152: sipush 675
      // 7155: ldc2_w 1.9332058429718018
      // 7158: dastore
      // 7159: dup
      // 715a: sipush 676
      // 715d: ldc2_w 1.9350945949554443
      // 7160: dastore
      // 7161: dup
      // 7162: sipush 677
      // 7165: ldc2_w 1.9369852542877197
      // 7168: dastore
      // 7169: dup
      // 716a: sipush 678
      // 716d: ldc2_w 1.938877820968628
      // 7170: dastore
      // 7171: dup
      // 7172: sipush 679
      // 7175: ldc2_w 1.940772294998169
      // 7178: dastore
      // 7179: dup
      // 717a: sipush 680
      // 717d: ldc2_w 1.9426684379577637
      // 7180: dastore
      // 7181: dup
      // 7182: sipush 681
      // 7185: ldc2_w 1.9445664882659912
      // 7188: dastore
      // 7189: dup
      // 718a: sipush 682
      // 718d: ldc2_w 1.9464664459228516
      // 7190: dastore
      // 7191: dup
      // 7192: sipush 683
      // 7195: ldc2_w 1.9483680725097656
      // 7198: dastore
      // 7199: dup
      // 719a: sipush 684
      // 719d: ldc2_w 1.9502718448638916
      // 71a0: dastore
      // 71a1: dup
      // 71a2: sipush 685
      // 71a5: ldc2_w 1.9521772861480713
      // 71a8: dastore
      // 71a9: dup
      // 71aa: sipush 686
      // 71ad: ldc2_w 1.9540846347808838
      // 71b0: dastore
      // 71b1: dup
      // 71b2: sipush 687
      // 71b5: ldc2_w 1.955993890762329
      // 71b8: dastore
      // 71b9: dup
      // 71ba: sipush 688
      // 71bd: ldc2_w 1.9579050540924072
      // 71c0: dastore
      // 71c1: dup
      // 71c2: sipush 689
      // 71c5: ldc2_w 1.959817886352539
      // 71c8: dastore
      // 71c9: dup
      // 71ca: sipush 690
      // 71cd: ldc2_w 1.9617326259613037
      // 71d0: dastore
      // 71d1: dup
      // 71d2: sipush 691
      // 71d5: ldc2_w 1.9636495113372803
      // 71d8: dastore
      // 71d9: dup
      // 71da: sipush 692
      // 71dd: ldc2_w 1.9655680656433105
      // 71e0: dastore
      // 71e1: dup
      // 71e2: sipush 693
      // 71e5: ldc2_w 1.9674885272979736
      // 71e8: dastore
      // 71e9: dup
      // 71ea: sipush 694
      // 71ed: ldc2_w 1.9694106578826904
      // 71f0: dastore
      // 71f1: dup
      // 71f2: sipush 695
      // 71f5: ldc2_w 1.9713349342346191
      // 71f8: dastore
      // 71f9: dup
      // 71fa: sipush 696
      // 71fd: ldc2_w 1.9732608795166016
      // 7200: dastore
      // 7201: dup
      // 7202: sipush 697
      // 7205: ldc2_w 1.975188970565796
      // 7208: dastore
      // 7209: dup
      // 720a: sipush 698
      // 720d: ldc2_w 1.977118730545044
      // 7210: dastore
      // 7211: dup
      // 7212: sipush 699
      // 7215: ldc2_w 1.9790503978729248
      // 7218: dastore
      // 7219: dup
      // 721a: sipush 700
      // 721d: ldc2_w 1.9809842109680176
      // 7220: dastore
      // 7221: dup
      // 7222: sipush 701
      // 7225: ldc2_w 1.982919692993164
      // 7228: dastore
      // 7229: dup
      // 722a: sipush 702
      // 722d: ldc2_w 1.9848570823669434
      // 7230: dastore
      // 7231: dup
      // 7232: sipush 703
      // 7235: ldc2_w 1.9867963790893555
      // 7238: dastore
      // 7239: dup
      // 723a: sipush 704
      // 723d: ldc2_w 1.9887375831604004
      // 7240: dastore
      // 7241: dup
      // 7242: sipush 705
      // 7245: ldc2_w 1.990680456161499
      // 7248: dastore
      // 7249: dup
      // 724a: sipush 706
      // 724d: ldc2_w 1.9926254749298096
      // 7250: dastore
      // 7251: dup
      // 7252: sipush 707
      // 7255: ldc2_w 1.994572401046753
      // 7258: dastore
      // 7259: dup
      // 725a: sipush 708
      // 725d: ldc2_w 1.996521234512329
      // 7260: dastore
      // 7261: dup
      // 7262: sipush 709
      // 7265: ldc2_w 1.998471736907959
      // 7268: dastore
      // 7269: dup
      // 726a: sipush 710
      // 726d: ldc2_w 2.000424385070801
      // 7270: dastore
      // 7271: dup
      // 7272: sipush 711
      // 7275: ldc2_w 2.0023789405822754
      // 7278: dastore
      // 7279: dup
      // 727a: sipush 712
      // 727d: ldc2_w 2.004335403442383
      // 7280: dastore
      // 7281: dup
      // 7282: sipush 713
      // 7285: ldc2_w 2.006293773651123
      // 7288: dastore
      // 7289: dup
      // 728a: sipush 714
      // 728d: ldc2_w 2.008254051208496
      // 7290: dastore
      // 7291: dup
      // 7292: sipush 715
      // 7295: ldc2_w 2.010216236114502
      // 7298: dastore
      // 7299: dup
      // 729a: sipush 716
      // 729d: ldc2_w 2.0121798515319824
      // 72a0: dastore
      // 72a1: dup
      // 72a2: sipush 717
      // 72a5: ldc2_w 2.014145851135254
      // 72a8: dastore
      // 72a9: dup
      // 72aa: sipush 718
      // 72ad: ldc2_w 2.016113758087158
      // 72b0: dastore
      // 72b1: dup
      // 72b2: sipush 719
      // 72b5: ldc2_w 2.0180835723876953
      // 72b8: dastore
      // 72b9: dup
      // 72ba: sipush 720
      // 72bd: ldc2_w 2.0200552940368652
      // 72c0: dastore
      // 72c1: dup
      // 72c2: sipush 721
      // 72c5: ldc2_w 2.022029399871826
      // 72c8: dastore
      // 72c9: dup
      // 72ca: sipush 722
      // 72cd: ldc2_w 2.0240049362182617
      // 72d0: dastore
      // 72d1: dup
      // 72d2: sipush 723
      // 72d5: ldc2_w 2.02598237991333
      // 72d8: dastore
      // 72d9: dup
      // 72da: sipush 724
      // 72dd: ldc2_w 2.0279617309570312
      // 72e0: dastore
      // 72e1: dup
      // 72e2: sipush 725
      // 72e5: ldc2_w 2.0299429893493652
      // 72e8: dastore
      // 72e9: dup
      // 72ea: sipush 726
      // 72ed: ldc2_w 2.0319266319274902
      // 72f0: dastore
      // 72f1: dup
      // 72f2: sipush 727
      // 72f5: ldc2_w 2.03391170501709
      // 72f8: dastore
      // 72f9: dup
      // 72fa: sipush 728
      // 72fd: ldc2_w 2.0358991622924805
      // 7300: dastore
      // 7301: dup
      // 7302: sipush 729
      // 7305: ldc2_w 2.0378880500793457
      // 7308: dastore
      // 7309: dup
      // 730a: sipush 730
      // 730d: ldc2_w 2.039879322052002
      // 7310: dastore
      // 7311: dup
      // 7312: sipush 731
      // 7315: ldc2_w 2.041872501373291
      // 7318: dastore
      // 7319: dup
      // 731a: sipush 732
      // 731d: ldc2_w 2.0438671112060547
      // 7320: dastore
      // 7321: dup
      // 7322: sipush 733
      // 7325: ldc2_w 2.0458641052246094
      // 7328: dastore
      // 7329: dup
      // 732a: sipush 734
      // 732d: ldc2_w 2.047863006591797
      // 7330: dastore
      // 7331: dup
      // 7332: sipush 735
      // 7335: ldc2_w 2.049863815307617
      // 7338: dastore
      // 7339: dup
      // 733a: sipush 736
      // 733d: ldc2_w 2.0518670082092285
      // 7340: dastore
      // 7341: dup
      // 7342: sipush 737
      // 7345: ldc2_w 2.0538716316223145
      // 7348: dastore
      // 7349: dup
      // 734a: sipush 738
      // 734d: ldc2_w 2.055878162384033
      // 7350: dastore
      // 7351: dup
      // 7352: sipush 739
      // 7355: ldc2_w 2.057887077331543
      // 7358: dastore
      // 7359: dup
      // 735a: sipush 740
      // 735d: ldc2_w 2.0598974227905273
      // 7360: dastore
      // 7361: dup
      // 7362: sipush 741
      // 7365: ldc2_w 2.0619101524353027
      // 7368: dastore
      // 7369: dup
      // 736a: sipush 742
      // 736d: ldc2_w 2.063924789428711
      // 7370: dastore
      // 7371: dup
      // 7372: sipush 743
      // 7375: ldc2_w 2.065941333770752
      // 7378: dastore
      // 7379: dup
      // 737a: sipush 744
      // 737d: ldc2_w 2.067959785461426
      // 7380: dastore
      // 7381: dup
      // 7382: sipush 745
      // 7385: ldc2_w 2.0699801445007324
      // 7388: dastore
      // 7389: dup
      // 738a: sipush 746
      // 738d: ldc2_w 2.07200288772583
      // 7390: dastore
      // 7391: dup
      // 7392: sipush 747
      // 7395: ldc2_w 2.0740270614624023
      // 7398: dastore
      // 7399: dup
      // 739a: sipush 748
      // 739d: ldc2_w 2.0760536193847656
      // 73a0: dastore
      // 73a1: dup
      // 73a2: sipush 749
      // 73a5: ldc2_w 2.0780820846557617
      // 73a8: dastore
      // 73a9: dup
      // 73aa: sipush 750
      // 73ad: ldc2_w 2.0801124572753906
      // 73b0: dastore
      // 73b1: dup
      // 73b2: sipush 751
      // 73b5: ldc2_w 2.0821447372436523
      // 73b8: dastore
      // 73b9: dup
      // 73ba: sipush 752
      // 73bd: ldc2_w 2.084178924560547
      // 73c0: dastore
      // 73c1: dup
      // 73c2: sipush 753
      // 73c5: ldc2_w 2.0862154960632324
      // 73c8: dastore
      // 73c9: dup
      // 73ca: sipush 754
      // 73cd: ldc2_w 2.0882534980773926
      // 73d0: dastore
      // 73d1: dup
      // 73d2: sipush 755
      // 73d5: ldc2_w 2.0902938842773438
      // 73d8: dastore
      // 73d9: dup
      // 73da: sipush 756
      // 73dd: ldc2_w 2.0923361778259277
      // 73e0: dastore
      // 73e1: dup
      // 73e2: sipush 757
      // 73e5: ldc2_w 2.0943803787231445
      // 73e8: dastore
      // 73e9: dup
      // 73ea: sipush 758
      // 73ed: ldc2_w 2.0964269638061523
      // 73f0: dastore
      // 73f1: dup
      // 73f2: sipush 759
      // 73f5: ldc2_w 2.0984749794006348
      // 73f8: dastore
      // 73f9: dup
      // 73fa: sipush 760
      // 73fd: ldc2_w 2.100525379180908
      // 7400: dastore
      // 7401: dup
      // 7402: sipush 761
      // 7405: ldc2_w 2.1025776863098145
      // 7408: dastore
      // 7409: dup
      // 740a: sipush 762
      // 740d: ldc2_w 2.1046319007873535
      // 7410: dastore
      // 7411: dup
      // 7412: sipush 763
      // 7415: ldc2_w 2.1066884994506836
      // 7418: dastore
      // 7419: dup
      // 741a: sipush 764
      // 741d: ldc2_w 2.1087465286254883
      // 7420: dastore
      // 7421: dup
      // 7422: sipush 765
      // 7425: ldc2_w 2.110806941986084
      // 7428: dastore
      // 7429: dup
      // 742a: sipush 766
      // 742d: ldc2_w 2.1128692626953125
      // 7430: dastore
      // 7431: dup
      // 7432: sipush 767
      // 7435: ldc2_w 2.114933490753174
      // 7438: dastore
      // 7439: dup
      // 743a: sipush 768
      // 743d: ldc2_w 2.117000102996826
      // 7440: dastore
      // 7441: dup
      // 7442: sipush 769
      // 7445: ldc2_w 2.1190686225891113
      // 7448: dastore
      // 7449: dup
      // 744a: sipush 770
      // 744d: ldc2_w 2.1211390495300293
      // 7450: dastore
      // 7451: dup
      // 7452: sipush 771
      // 7455: ldc2_w 2.12321138381958
      // 7458: dastore
      // 7459: dup
      // 745a: sipush 772
      // 745d: ldc2_w 2.1252856254577637
      // 7460: dastore
      // 7461: dup
      // 7462: sipush 773
      // 7465: ldc2_w 2.1273622512817383
      // 7468: dastore
      // 7469: dup
      // 746a: sipush 774
      // 746d: ldc2_w 2.1294407844543457
      // 7470: dastore
      // 7471: dup
      // 7472: sipush 775
      // 7475: ldc2_w 2.131521224975586
      // 7478: dastore
      // 7479: dup
      // 747a: sipush 776
      // 747d: ldc2_w 2.133604049682617
      // 7480: dastore
      // 7481: dup
      // 7482: sipush 777
      // 7485: ldc2_w 2.135688304901123
      // 7488: dastore
      // 7489: dup
      // 748a: sipush 778
      // 748d: ldc2_w 2.13777494430542
      // 7490: dastore
      // 7491: dup
      // 7492: sipush 779
      // 7495: ldc2_w 2.139863967895508
      // 7498: dastore
      // 7499: dup
      // 749a: sipush 780
      // 749d: ldc2_w 2.1419544219970703
      // 74a0: dastore
      // 74a1: dup
      // 74a2: sipush 781
      // 74a5: ldc2_w 2.144047260284424
      // 74a8: dastore
      // 74a9: dup
      // 74aa: sipush 782
      // 74ad: ldc2_w 2.14614200592041
      // 74b0: dastore
      // 74b1: dup
      // 74b2: sipush 783
      // 74b5: ldc2_w 2.1482391357421875
      // 74b8: dastore
      // 74b9: dup
      // 74ba: sipush 784
      // 74bd: ldc2_w 2.1503376960754395
      // 74c0: dastore
      // 74c1: dup
      // 74c2: sipush 785
      // 74c5: ldc2_w 2.1524391174316406
      // 74c8: dastore
      // 74c9: dup
      // 74ca: sipush 786
      // 74cd: ldc2_w 2.1545419692993164
      // 74d0: dastore
      // 74d1: dup
      // 74d2: sipush 787
      // 74d5: ldc2_w 2.156647205352783
      // 74d8: dastore
      // 74d9: dup
      // 74da: sipush 788
      // 74dd: ldc2_w 2.1587538719177246
      // 74e0: dastore
      // 74e1: dup
      // 74e2: sipush 789
      // 74e5: ldc2_w 2.1608633995056152
      // 74e8: dastore
      // 74e9: dup
      // 74ea: sipush 790
      // 74ed: ldc2_w 2.1629743576049805
      // 74f0: dastore
      // 74f1: dup
      // 74f2: sipush 791
      // 74f5: ldc2_w 2.1650876998901367
      // 74f8: dastore
      // 74f9: dup
      // 74fa: sipush 792
      // 74fd: ldc2_w 2.167203426361084
      // 7500: dastore
      // 7501: dup
      // 7502: sipush 793
      // 7505: ldc2_w 2.169320583343506
      // 7508: dastore
      // 7509: dup
      // 750a: sipush 794
      // 750d: ldc2_w 2.1714401245117188
      // 7510: dastore
      // 7511: dup
      // 7512: sipush 795
      // 7515: ldc2_w 2.1735615730285645
      // 7518: dastore
      // 7519: dup
      // 751a: sipush 796
      // 751d: ldc2_w 2.175685405731201
      // 7520: dastore
      // 7521: dup
      // 7522: sipush 797
      // 7525: ldc2_w 2.1778111457824707
      // 7528: dastore
      // 7529: dup
      // 752a: sipush 798
      // 752d: ldc2_w 2.179938793182373
      // 7530: dastore
      // 7531: dup
      // 7532: sipush 799
      // 7535: ldc2_w 2.1820688247680664
      // 7538: dastore
      // 7539: dup
      // 753a: sipush 800
      // 753d: ldc2_w 2.1842007637023926
      // 7540: dastore
      // 7541: dup
      // 7542: sipush 801
      // 7545: ldc2_w 2.1863350868225098
      // 7548: dastore
      // 7549: dup
      // 754a: sipush 802
      // 754d: ldc2_w 2.1884708404541016
      // 7550: dastore
      // 7551: dup
      // 7552: sipush 803
      // 7555: ldc2_w 2.1906094551086426
      // 7558: dastore
      // 7559: dup
      // 755a: sipush 804
      // 755d: ldc2_w 2.192749500274658
      // 7560: dastore
      // 7561: dup
      // 7562: sipush 805
      // 7565: ldc2_w 2.194891929626465
      // 7568: dastore
      // 7569: dup
      // 756a: sipush 806
      // 756d: ldc2_w 2.1970362663269043
      // 7570: dastore
      // 7571: dup
      // 7572: sipush 807
      // 7575: ldc2_w 2.1991829872131348
      // 7578: dastore
      // 7579: dup
      // 757a: sipush 808
      // 757d: ldc2_w 2.201331615447998
      // 7580: dastore
      // 7581: dup
      // 7582: sipush 809
      // 7585: ldc2_w 2.2034826278686523
      // 7588: dastore
      // 7589: dup
      // 758a: sipush 810
      // 758d: ldc2_w 2.2056355476379395
      // 7590: dastore
      // 7591: dup
      // 7592: sipush 811
      // 7595: ldc2_w 2.2077903747558594
      // 7598: dastore
      // 7599: dup
      // 759a: sipush 812
      // 759d: ldc2_w 2.2099475860595703
      // 75a0: dastore
      // 75a1: dup
      // 75a2: sipush 813
      // 75a5: ldc2_w 2.212106704711914
      // 75a8: dastore
      // 75a9: dup
      // 75aa: sipush 814
      // 75ad: ldc2_w 2.214268207550049
      // 75b0: dastore
      // 75b1: dup
      // 75b2: sipush 815
      // 75b5: ldc2_w 2.2164316177368164
      // 75b8: dastore
      // 75b9: dup
      // 75ba: sipush 816
      // 75bd: ldc2_w 2.218596935272217
      // 75c0: dastore
      // 75c1: dup
      // 75c2: sipush 817
      // 75c5: ldc2_w 2.220764636993408
      // 75c8: dastore
      // 75c9: dup
      // 75ca: sipush 818
      // 75cd: ldc2_w 2.2229342460632324
      // 75d0: dastore
      // 75d1: dup
      // 75d2: sipush 819
      // 75d5: ldc2_w 2.2251062393188477
      // 75d8: dastore
      // 75d9: dup
      // 75da: sipush 820
      // 75dd: ldc2_w 2.2272801399230957
      // 75e0: dastore
      // 75e1: dup
      // 75e2: sipush 821
      // 75e5: ldc2_w 2.2294564247131348
      // 75e8: dastore
      // 75e9: dup
      // 75ea: sipush 822
      // 75ed: ldc2_w 2.2316346168518066
      // 75f0: dastore
      // 75f1: dup
      // 75f2: sipush 823
      // 75f5: ldc2_w 2.2338151931762695
      // 75f8: dastore
      // 75f9: dup
      // 75fa: sipush 824
      // 75fd: ldc2_w 2.2359976768493652
      // 7600: dastore
      // 7601: dup
      // 7602: sipush 825
      // 7605: ldc2_w 2.2381820678710938
      // 7608: dastore
      // 7609: dup
      // 760a: sipush 826
      // 760d: ldc2_w 2.2403693199157715
      // 7610: dastore
      // 7611: dup
      // 7612: sipush 827
      // 7615: ldc2_w 2.242558002471924
      // 7618: dastore
      // 7619: dup
      // 761a: sipush 828
      // 761d: ldc2_w 2.244749069213867
      // 7620: dastore
      // 7621: dup
      // 7622: sipush 829
      // 7625: ldc2_w 2.2469425201416016
      // 7628: dastore
      // 7629: dup
      // 762a: sipush 830
      // 762d: ldc2_w 2.2491378784179688
      // 7630: dastore
      // 7631: dup
      // 7632: sipush 831
      // 7635: ldc2_w 2.2513351440429688
      // 7638: dastore
      // 7639: dup
      // 763a: sipush 832
      // 763d: ldc2_w 2.2535347938537598
      // 7640: dastore
      // 7641: dup
      // 7642: sipush 833
      // 7645: ldc2_w 2.2557363510131836
      // 7648: dastore
      // 7649: dup
      // 764a: sipush 834
      // 764d: ldc2_w 2.2579402923583984
      // 7650: dastore
      // 7651: dup
      // 7652: sipush 835
      // 7655: ldc2_w 2.2601466178894043
      // 7658: dastore
      // 7659: dup
      // 765a: sipush 836
      // 765d: ldc2_w 2.262354850769043
      // 7660: dastore
      // 7661: dup
      // 7662: sipush 837
      // 7665: ldc2_w 2.2645654678344727
      // 7668: dastore
      // 7669: dup
      // 766a: sipush 838
      // 766d: ldc2_w 2.266777992248535
      // 7670: dastore
      // 7671: dup
      // 7672: sipush 839
      // 7675: ldc2_w 2.2689924240112305
      // 7678: dastore
      // 7679: dup
      // 767a: sipush 840
      // 767d: ldc2_w 2.271209716796875
      // 7680: dastore
      // 7681: dup
      // 7682: sipush 841
      // 7685: ldc2_w 2.273428440093994
      // 7688: dastore
      // 7689: dup
      // 768a: sipush 842
      // 768d: ldc2_w 2.2756495475769043
      // 7690: dastore
      // 7691: dup
      // 7692: sipush 843
      // 7695: ldc2_w 2.2778730392456055
      // 7698: dastore
      // 7699: dup
      // 769a: sipush 844
      // 769d: ldc2_w 2.2800989151000977
      // 76a0: dastore
      // 76a1: dup
      // 76a2: sipush 845
      // 76a5: ldc2_w 2.2823266983032227
      // 76a8: dastore
      // 76a9: dup
      // 76aa: sipush 846
      // 76ad: ldc2_w 2.2845563888549805
      // 76b0: dastore
      // 76b1: dup
      // 76b2: sipush 847
      // 76b5: ldc2_w 2.2867884635925293
      // 76b8: dastore
      // 76b9: dup
      // 76ba: sipush 848
      // 76bd: ldc2_w 2.289022922515869
      // 76c0: dastore
      // 76c1: dup
      // 76c2: sipush 849
      // 76c5: ldc2_w 2.291259288787842
      // 76c8: dastore
      // 76c9: dup
      // 76ca: sipush 850
      // 76cd: ldc2_w 2.2934980392456055
      // 76d0: dastore
      // 76d1: dup
      // 76d2: sipush 851
      // 76d5: ldc2_w 2.295738697052002
      // 76d8: dastore
      // 76d9: dup
      // 76da: sipush 852
      // 76dd: ldc2_w 2.2979817390441895
      // 76e0: dastore
      // 76e1: dup
      // 76e2: sipush 853
      // 76e5: ldc2_w 2.300227165222168
      // 76e8: dastore
      // 76e9: dup
      // 76ea: sipush 854
      // 76ed: ldc2_w 2.3024744987487793
      // 76f0: dastore
      // 76f1: dup
      // 76f2: sipush 855
      // 76f5: ldc2_w 2.3047242164611816
      // 76f8: dastore
      // 76f9: dup
      // 76fa: sipush 856
      // 76fd: ldc2_w 2.306975841522217
      // 7700: dastore
      // 7701: dup
      // 7702: sipush 857
      // 7705: ldc2_w 2.309229850769043
      // 7708: dastore
      // 7709: dup
      // 770a: sipush 858
      // 770d: ldc2_w 2.31148624420166
      // 7710: dastore
      // 7711: dup
      // 7712: sipush 859
      // 7715: ldc2_w 2.31374454498291
      // 7718: dastore
      // 7719: dup
      // 771a: sipush 860
      // 771d: ldc2_w 2.316005229949951
      // 7720: dastore
      // 7721: dup
      // 7722: sipush 861
      // 7725: ldc2_w 2.318267822265625
      // 7728: dastore
      // 7729: dup
      // 772a: sipush 862
      // 772d: ldc2_w 2.32053279876709
      // 7730: dastore
      // 7731: dup
      // 7732: sipush 863
      // 7735: ldc2_w 2.3228001594543457
      // 7738: dastore
      // 7739: dup
      // 773a: sipush 864
      // 773d: ldc2_w 2.3250694274902344
      // 7740: dastore
      // 7741: dup
      // 7742: sipush 865
      // 7745: ldc2_w 2.3273415565490723
      // 7748: dastore
      // 7749: dup
      // 774a: sipush 866
      // 774d: ldc2_w 2.3296151161193848
      // 7750: dastore
      // 7751: dup
      // 7752: sipush 867
      // 7755: ldc2_w 2.3318915367126465
      // 7758: dastore
      // 7759: dup
      // 775a: sipush 868
      // 775d: ldc2_w 2.334169864654541
      // 7760: dastore
      // 7761: dup
      // 7762: sipush 869
      // 7765: ldc2_w 2.3364500999450684
      // 7768: dastore
      // 7769: dup
      // 776a: sipush 870
      // 776d: ldc2_w 2.338733196258545
      // 7770: dastore
      // 7771: dup
      // 7772: sipush 871
      // 7775: ldc2_w 2.3410181999206543
      // 7778: dastore
      // 7779: dup
      // 777a: sipush 872
      // 777d: ldc2_w 2.3433055877685547
      // 7780: dastore
      // 7781: dup
      // 7782: sipush 873
      // 7785: ldc2_w 2.345594882965088
      // 7788: dastore
      // 7789: dup
      // 778a: sipush 874
      // 778d: ldc2_w 2.347886562347412
      // 7790: dastore
      // 7791: dup
      // 7792: sipush 875
      // 7795: ldc2_w 2.3501806259155273
      // 7798: dastore
      // 7799: dup
      // 779a: sipush 876
      // 779d: ldc2_w 2.3524770736694336
      // 77a0: dastore
      // 77a1: dup
      // 77a2: sipush 877
      // 77a5: ldc2_w 2.3547754287719727
      // 77a8: dastore
      // 77a9: dup
      // 77aa: sipush 878
      // 77ad: ldc2_w 2.3570761680603027
      // 77b0: dastore
      // 77b1: dup
      // 77b2: sipush 879
      // 77b5: ldc2_w 2.3593788146972656
      // 77b8: dastore
      // 77b9: dup
      // 77ba: sipush 880
      // 77bd: ldc2_w 2.3616843223571777
      // 77c0: dastore
      // 77c1: dup
      // 77c2: sipush 881
      // 77c5: ldc2_w 2.3639917373657227
      // 77c8: dastore
      // 77c9: dup
      // 77ca: sipush 882
      // 77cd: ldc2_w 2.3663015365600586
      // 77d0: dastore
      // 77d1: dup
      // 77d2: sipush 883
      // 77d5: ldc2_w 2.3686132431030273
      // 77d8: dastore
      // 77d9: dup
      // 77da: sipush 884
      // 77dd: ldc2_w 2.370927333831787
      // 77e0: dastore
      // 77e1: dup
      // 77e2: sipush 885
      // 77e5: ldc2_w 2.373243808746338
      // 77e8: dastore
      // 77e9: dup
      // 77ea: sipush 886
      // 77ed: ldc2_w 2.3755626678466797
      // 77f0: dastore
      // 77f1: dup
      // 77f2: sipush 887
      // 77f5: ldc2_w 2.3778839111328125
      // 77f8: dastore
      // 77f9: dup
      // 77fa: sipush 888
      // 77fd: ldc2_w 2.380207061767578
      // 7800: dastore
      // 7801: dup
      // 7802: sipush 889
      // 7805: ldc2_w 2.3825325965881348
      // 7808: dastore
      // 7809: dup
      // 780a: sipush 890
      // 780d: ldc2_w 2.3848605155944824
      // 7810: dastore
      // 7811: dup
      // 7812: sipush 891
      // 7815: ldc2_w 2.387190818786621
      // 7818: dastore
      // 7819: dup
      // 781a: sipush 892
      // 781d: ldc2_w 2.3895230293273926
      // 7820: dastore
      // 7821: dup
      // 7822: sipush 893
      // 7825: ldc2_w 2.391857624053955
      // 7828: dastore
      // 7829: dup
      // 782a: sipush 894
      // 782d: ldc2_w 2.3941946029663086
      // 7830: dastore
      // 7831: dup
      // 7832: sipush 895
      // 7835: ldc2_w 2.396533966064453
      // 7838: dastore
      // 7839: dup
      // 783a: sipush 896
      // 783d: ldc2_w 2.3988752365112305
      // 7840: dastore
      // 7841: dup
      // 7842: sipush 897
      // 7845: ldc2_w 2.401218891143799
      // 7848: dastore
      // 7849: dup
      // 784a: sipush 898
      // 784d: ldc2_w 2.4035654067993164
      // 7850: dastore
      // 7851: dup
      // 7852: sipush 899
      // 7855: ldc2_w 2.4059133529663086
      // 7858: dastore
      // 7859: dup
      // 785a: sipush 900
      // 785d: ldc2_w 2.40826416015625
      // 7860: dastore
      // 7861: dup
      // 7862: sipush 901
      // 7865: ldc2_w 2.4106173515319824
      // 7868: dastore
      // 7869: dup
      // 786a: sipush 902
      // 786d: ldc2_w 2.4129724502563477
      // 7870: dastore
      // 7871: dup
      // 7872: sipush 903
      // 7875: ldc2_w 2.415329933166504
      // 7878: dastore
      // 7879: dup
      // 787a: sipush 904
      // 787d: ldc2_w 2.417689800262451
      // 7880: dastore
      // 7881: dup
      // 7882: sipush 905
      // 7885: ldc2_w 2.4200520515441895
      // 7888: dastore
      // 7889: dup
      // 788a: sipush 906
      // 788d: ldc2_w 2.4224166870117188
      // 7890: dastore
      // 7891: dup
      // 7892: sipush 907
      // 7895: ldc2_w 2.424783229827881
      // 7898: dastore
      // 7899: dup
      // 789a: sipush 908
      // 789d: ldc2_w 2.427152633666992
      // 78a0: dastore
      // 78a1: dup
      // 78a2: sipush 909
      // 78a5: ldc2_w 2.4295239448547363
      // 78a8: dastore
      // 78a9: dup
      // 78aa: sipush 910
      // 78ad: ldc2_w 2.4318976402282715
      // 78b0: dastore
      // 78b1: dup
      // 78b2: sipush 911
      // 78b5: ldc2_w 2.4342737197875977
      // 78b8: dastore
      // 78b9: dup
      // 78ba: sipush 912
      // 78bd: ldc2_w 2.436652183532715
      // 78c0: dastore
      // 78c1: dup
      // 78c2: sipush 913
      // 78c5: ldc2_w 2.439032554626465
      // 78c8: dastore
      // 78c9: dup
      // 78ca: sipush 914
      // 78cd: ldc2_w 2.441415786743164
      // 78d0: dastore
      // 78d1: dup
      // 78d2: sipush 915
      // 78d5: ldc2_w 2.4438014030456543
      // 78d8: dastore
      // 78d9: dup
      // 78da: sipush 916
      // 78dd: ldc2_w 2.4461889266967773
      // 78e0: dastore
      // 78e1: dup
      // 78e2: sipush 917
      // 78e5: ldc2_w 2.4485788345336914
      // 78e8: dastore
      // 78e9: dup
      // 78ea: sipush 918
      // 78ed: ldc2_w 2.4509711265563965
      // 78f0: dastore
      // 78f1: dup
      // 78f2: sipush 919
      // 78f5: ldc2_w 2.4533658027648926
      // 78f8: dastore
      // 78f9: dup
      // 78fa: sipush 920
      // 78fd: ldc2_w 2.4557628631591797
      // 7900: dastore
      // 7901: dup
      // 7902: sipush 921
      // 7905: ldc2_w 2.458162307739258
      // 7908: dastore
      // 7909: dup
      // 790a: sipush 922
      // 790d: ldc2_w 2.460564136505127
      // 7910: dastore
      // 7911: dup
      // 7912: sipush 923
      // 7915: ldc2_w 2.462968349456787
      // 7918: dastore
      // 7919: dup
      // 791a: sipush 924
      // 791d: ldc2_w 2.46537446975708
      // 7920: dastore
      // 7921: dup
      // 7922: sipush 925
      // 7925: ldc2_w 2.4677834510803223
      // 7928: dastore
      // 7929: dup
      // 792a: sipush 926
      // 792d: ldc2_w 2.4701943397521973
      // 7930: dastore
      // 7931: dup
      // 7932: sipush 927
      // 7935: ldc2_w 2.4726080894470215
      // 7938: dastore
      // 7939: dup
      // 793a: sipush 928
      // 793d: ldc2_w 2.4750237464904785
      // 7940: dastore
      // 7941: dup
      // 7942: sipush 929
      // 7945: ldc2_w 2.4774417877197266
      // 7948: dastore
      // 7949: dup
      // 794a: sipush 930
      // 794d: ldc2_w 2.479862689971924
      // 7950: dastore
      // 7951: dup
      // 7952: sipush 931
      // 7955: ldc2_w 2.482285499572754
      // 7958: dastore
      // 7959: dup
      // 795a: sipush 932
      // 795d: ldc2_w 2.484710693359375
      // 7960: dastore
      // 7961: dup
      // 7962: sipush 933
      // 7965: ldc2_w 2.487138271331787
      // 7968: dastore
      // 7969: dup
      // 796a: sipush 934
      // 796d: ldc2_w 2.4895682334899902
      // 7970: dastore
      // 7971: dup
      // 7972: sipush 935
      // 7975: ldc2_w 2.4920010566711426
      // 7978: dastore
      // 7979: dup
      // 797a: sipush 936
      // 797d: ldc2_w 2.4944357872009277
      // 7980: dastore
      // 7981: dup
      // 7982: sipush 937
      // 7985: ldc2_w 2.496872901916504
      // 7988: dastore
      // 7989: dup
      // 798a: sipush 938
      // 798d: ldc2_w 2.499312400817871
      // 7990: dastore
      // 7991: dup
      // 7992: sipush 939
      // 7995: ldc2_w 2.5017542839050293
      // 7998: dastore
      // 7999: dup
      // 799a: sipush 940
      // 799d: ldc2_w 2.5041985511779785
      // 79a0: dastore
      // 79a1: dup
      // 79a2: sipush 941
      // 79a5: ldc2_w 2.5066452026367188
      // 79a8: dastore
      // 79a9: dup
      // 79aa: sipush 942
      // 79ad: ldc2_w 2.50909423828125
      // 79b0: dastore
      // 79b1: dup
      // 79b2: sipush 943
      // 79b5: ldc2_w 2.5115456581115723
      // 79b8: dastore
      // 79b9: dup
      // 79ba: sipush 944
      // 79bd: ldc2_w 2.5139999389648438
      // 79c0: dastore
      // 79c1: dup
      // 79c2: sipush 945
      // 79c5: ldc2_w 2.516456127166748
      // 79c8: dastore
      // 79c9: dup
      // 79ca: sipush 946
      // 79cd: ldc2_w 2.5189146995544434
      // 79d0: dastore
      // 79d1: dup
      // 79d2: sipush 947
      // 79d5: ldc2_w 2.5213756561279297
      // 79d8: dastore
      // 79d9: dup
      // 79da: sipush 948
      // 79dd: ldc2_w 2.5238394737243652
      // 79e0: dastore
      // 79e1: dup
      // 79e2: sipush 949
      // 79e5: ldc2_w 2.5263051986694336
      // 79e8: dastore
      // 79e9: dup
      // 79ea: sipush 950
      // 79ed: ldc2_w 2.528773307800293
      // 79f0: dastore
      // 79f1: dup
      // 79f2: sipush 951
      // 79f5: ldc2_w 2.5312442779541016
      // 79f8: dastore
      // 79f9: dup
      // 79fa: sipush 952
      // 79fd: ldc2_w 2.533717155456543
      // 7a00: dastore
      // 7a01: dup
      // 7a02: sipush 953
      // 7a05: ldc2_w 2.5361928939819336
      // 7a08: dastore
      // 7a09: dup
      // 7a0a: sipush 954
      // 7a0d: ldc2_w 2.538670539855957
      // 7a10: dastore
      // 7a11: dup
      // 7a12: sipush 955
      // 7a15: ldc2_w 2.5411510467529297
      // 7a18: dastore
      // 7a19: dup
      // 7a1a: sipush 956
      // 7a1d: ldc2_w 2.5436339378356934
      // 7a20: dastore
      // 7a21: dup
      // 7a22: sipush 957
      // 7a25: ldc2_w 2.546119213104248
      // 7a28: dastore
      // 7a29: dup
      // 7a2a: sipush 958
      // 7a2d: ldc2_w 2.5486068725585938
      // 7a30: dastore
      // 7a31: dup
      // 7a32: sipush 959
      // 7a35: ldc2_w 2.5510969161987305
      // 7a38: dastore
      // 7a39: dup
      // 7a3a: sipush 960
      // 7a3d: ldc2_w 2.553589344024658
      // 7a40: dastore
      // 7a41: dup
      // 7a42: sipush 961
      // 7a45: ldc2_w 2.556084632873535
      // 7a48: dastore
      // 7a49: dup
      // 7a4a: sipush 962
      // 7a4d: ldc2_w 2.558581829071045
      // 7a50: dastore
      // 7a51: dup
      // 7a52: sipush 963
      // 7a55: ldc2_w 2.5610814094543457
      // 7a58: dastore
      // 7a59: dup
      // 7a5a: sipush 964
      // 7a5d: ldc2_w 2.5635838508605957
      // 7a60: dastore
      // 7a61: dup
      // 7a62: sipush 965
      // 7a65: ldc2_w 2.5660886764526367
      // 7a68: dastore
      // 7a69: dup
      // 7a6a: sipush 966
      // 7a6d: ldc2_w 2.5685958862304688
      // 7a70: dastore
      // 7a71: dup
      // 7a72: sipush 967
      // 7a75: ldc2_w 2.571105480194092
      // 7a78: dastore
      // 7a79: dup
      // 7a7a: sipush 968
      // 7a7d: ldc2_w 2.573617458343506
      // 7a80: dastore
      // 7a81: dup
      // 7a82: sipush 969
      // 7a85: ldc2_w 2.576131820678711
      // 7a88: dastore
      // 7a89: dup
      // 7a8a: sipush 970
      // 7a8d: ldc2_w 2.5786490440368652
      // 7a90: dastore
      // 7a91: dup
      // 7a92: sipush 971
      // 7a95: ldc2_w 2.5811686515808105
      // 7a98: dastore
      // 7a99: dup
      // 7a9a: sipush 972
      // 7a9d: ldc2_w 2.5836901664733887
      // 7aa0: dastore
      // 7aa1: dup
      // 7aa2: sipush 973
      // 7aa5: ldc2_w 2.586214542388916
      // 7aa8: dastore
      // 7aa9: dup
      // 7aaa: sipush 974
      // 7aad: ldc2_w 2.5887417793273926
      // 7ab0: dastore
      // 7ab1: dup
      // 7ab2: sipush 975
      // 7ab5: ldc2_w 2.591270923614502
      // 7ab8: dastore
      // 7ab9: dup
      // 7aba: sipush 976
      // 7abd: ldc2_w 2.5938024520874023
      // 7ac0: dastore
      // 7ac1: dup
      // 7ac2: sipush 977
      // 7ac5: ldc2_w 2.596336841583252
      // 7ac8: dastore
      // 7ac9: dup
      // 7aca: sipush 978
      // 7acd: ldc2_w 2.5988736152648926
      // 7ad0: dastore
      // 7ad1: dup
      // 7ad2: sipush 979
      // 7ad5: ldc2_w 2.601412773132324
      // 7ad8: dastore
      // 7ad9: dup
      // 7ada: sipush 980
      // 7add: ldc2_w 2.603954315185547
      // 7ae0: dastore
      // 7ae1: dup
      // 7ae2: sipush 981
      // 7ae5: ldc2_w 2.6064987182617188
      // 7ae8: dastore
      // 7ae9: dup
      // 7aea: sipush 982
      // 7aed: ldc2_w 2.6090455055236816
      // 7af0: dastore
      // 7af1: dup
      // 7af2: sipush 983
      // 7af5: ldc2_w 2.6115946769714355
      // 7af8: dastore
      // 7af9: dup
      // 7afa: sipush 984
      // 7afd: ldc2_w 2.6141462326049805
      // 7b00: dastore
      // 7b01: dup
      // 7b02: sipush 985
      // 7b05: ldc2_w 2.6167001724243164
      // 7b08: dastore
      // 7b09: dup
      // 7b0a: sipush 986
      // 7b0d: ldc2_w 2.6192569732666016
      // 7b10: dastore
      // 7b11: dup
      // 7b12: sipush 987
      // 7b15: ldc2_w 2.6218161582946777
      // 7b18: dastore
      // 7b19: dup
      // 7b1a: sipush 988
      // 7b1d: ldc2_w 2.624377727508545
      // 7b20: dastore
      // 7b21: dup
      // 7b22: sipush 989
      // 7b25: ldc2_w 2.626941680908203
      // 7b28: dastore
      // 7b29: dup
      // 7b2a: sipush 990
      // 7b2d: ldc2_w 2.6295084953308105
      // 7b30: dastore
      // 7b31: dup
      // 7b32: sipush 991
      // 7b35: ldc2_w 2.632077217102051
      // 7b38: dastore
      // 7b39: dup
      // 7b3a: sipush 992
      // 7b3d: ldc2_w 2.6346492767333984
      // 7b40: dastore
      // 7b41: dup
      // 7b42: sipush 993
      // 7b45: ldc2_w 2.637223243713379
      // 7b48: dastore
      // 7b49: dup
      // 7b4a: sipush 994
      // 7b4d: ldc2_w 2.6398000717163086
      // 7b50: dastore
      // 7b51: dup
      // 7b52: sipush 995
      // 7b55: ldc2_w 2.6423792839050293
      // 7b58: dastore
      // 7b59: dup
      // 7b5a: sipush 996
      // 7b5d: ldc2_w 2.644960880279541
      // 7b60: dastore
      // 7b61: dup
      // 7b62: sipush 997
      // 7b65: ldc2_w 2.6475448608398438
      // 7b68: dastore
      // 7b69: dup
      // 7b6a: sipush 998
      // 7b6d: ldc2_w 2.6501317024230957
      // 7b70: dastore
      // 7b71: dup
      // 7b72: sipush 999
      // 7b75: ldc2_w 2.6527209281921387
      // 7b78: dastore
      // 7b79: dup
      // 7b7a: sipush 1000
      // 7b7d: ldc2_w 2.655313014984131
      // 7b80: dastore
      // 7b81: dup
      // 7b82: sipush 1001
      // 7b85: ldc2_w 2.657907009124756
      // 7b88: dastore
      // 7b89: dup
      // 7b8a: sipush 1002
      // 7b8d: ldc2_w 2.6605043411254883
      // 7b90: dastore
      // 7b91: dup
      // 7b92: sipush 1003
      // 7b95: ldc2_w 2.6631035804748535
      // 7b98: dastore
      // 7b99: dup
      // 7b9a: sipush 1004
      // 7b9d: ldc2_w 2.665705680847168
      // 7ba0: dastore
      // 7ba1: dup
      // 7ba2: sipush 1005
      // 7ba5: ldc2_w 2.6683101654052734
      // 7ba8: dastore
      // 7ba9: dup
      // 7baa: sipush 1006
      // 7bad: ldc2_w 2.67091703414917
      // 7bb0: dastore
      // 7bb1: dup
      // 7bb2: sipush 1007
      // 7bb5: ldc2_w 2.6735267639160156
      // 7bb8: dastore
      // 7bb9: dup
      // 7bba: sipush 1008
      // 7bbd: ldc2_w 2.6761388778686523
      // 7bc0: dastore
      // 7bc1: dup
      // 7bc2: sipush 1009
      // 7bc5: ldc2_w 2.67875337600708
      // 7bc8: dastore
      // 7bc9: dup
      // 7bca: sipush 1010
      // 7bcd: ldc2_w 2.681370735168457
      // 7bd0: dastore
      // 7bd1: dup
      // 7bd2: sipush 1011
      // 7bd5: ldc2_w 2.683990478515625
      // 7bd8: dastore
      // 7bd9: dup
      // 7bda: sipush 1012
      // 7bdd: ldc2_w 2.686613082885742
      // 7be0: dastore
      // 7be1: dup
      // 7be2: sipush 1013
      // 7be5: ldc2_w 2.689237594604492
      // 7be8: dastore
      // 7be9: dup
      // 7bea: sipush 1014
      // 7bed: ldc2_w 2.6918654441833496
      // 7bf0: dastore
      // 7bf1: dup
      // 7bf2: sipush 1015
      // 7bf5: ldc2_w 2.69449520111084
      // 7bf8: dastore
      // 7bf9: dup
      // 7bfa: sipush 1016
      // 7bfd: ldc2_w 2.6971278190612793
      // 7c00: dastore
      // 7c01: dup
      // 7c02: sipush 1017
      // 7c05: ldc2_w 2.699763298034668
      // 7c08: dastore
      // 7c09: dup
      // 7c0a: sipush 1018
      // 7c0d: ldc2_w 2.7024011611938477
      // 7c10: dastore
      // 7c11: dup
      // 7c12: sipush 1019
      // 7c15: ldc2_w 2.7050414085388184
      // 7c18: dastore
      // 7c19: dup
      // 7c1a: sipush 1020
      // 7c1d: ldc2_w 2.70768404006958
      // 7c20: dastore
      // 7c21: dup
      // 7c22: sipush 1021
      // 7c25: ldc2_w 2.710329532623291
      // 7c28: dastore
      // 7c29: dup
      // 7c2a: sipush 1022
      // 7c2d: ldc2_w 2.712977886199951
      // 7c30: dastore
      // 7c31: dup
      // 7c32: sipush 1023
      // 7c35: ldc2_w 2.7156286239624023
      // 7c38: dastore
      // 7c39: dup
      // 7c3a: sipush 1024
      // 7c3d: ldc2_w 2.7182817459106445
      // 7c40: dastore
      // 7c41: putstatic org/apache/commons/math3/util/FastMathLiteralArrays.EXP_FRAC_A [D
      // 7c44: sipush 1025
      // 7c47: newarray 7
      // 7c49: dup
      // 7c4a: bipush 0
      // 7c4b: dconst_0
      // 7c4c: dastore
      // 7c4d: dup
      // 7c4e: bipush 1
      // 7c4f: ldc2_w 1.552583321178453E-10
      // 7c52: dastore
      // 7c53: dup
      // 7c54: bipush 2
      // 7c55: ldc2_w 1.2423699995465188E-9
      // 7c58: dastore
      // 7c59: dup
      // 7c5a: bipush 3
      // 7c5b: ldc2_w 4.194022929828008E-9
      // 7c5e: dastore
      // 7c5f: dup
      // 7c60: bipush 4
      // 7c61: ldc2_w 9.94381632344361E-9
      // 7c64: dastore
      // 7c65: dup
      // 7c66: bipush 5
      // 7c67: ldc2_w 1.9426261544163577E-8
      // 7c6a: dastore
      // 7c6b: dup
      // 7c6c: bipush 6
      // 7c6e: ldc2_w 3.3576783010266685E-8
      // 7c71: dastore
      // 7c72: dup
      // 7c73: bipush 7
      // 7c75: ldc2_w 5.3331719086630523E-8
      // 7c78: dastore
      // 7c79: dup
      // 7c7a: bipush 8
      // 7c7c: ldc2_w 7.962832297769345E-8
      // 7c7f: dastore
      // 7c80: dup
      // 7c81: bipush 9
      // 7c83: ldc2_w 1.1340476362128895E-7
      // 7c86: dastore
      // 7c87: dup
      // 7c88: bipush 10
      // 7c8a: ldc2_w -8.281845251820919E-8
      // 7c8d: dastore
      // 7c8e: dup
      // 7c8f: bipush 11
      // 7c91: ldc2_w -3.126416414805498E-8
      // 7c94: dastore
      // 7c95: dup
      // 7c96: bipush 12
      // 7c98: ldc2_w 3.058997113995161E-8
      // 7c9b: dastore
      // 7c9c: dup
      // 7c9d: bipush 13
      // 7c9f: ldc2_w 1.0368579417304741E-7
      // 7ca2: dastore
      // 7ca3: dup
      // 7ca4: bipush 14
      // 7ca6: ldc2_w -4.9452513107409435E-8
      // 7ca9: dastore
      // 7caa: dup
      // 7cab: bipush 15
      // 7cad: ldc2_w 4.8955889659397494E-8
      // 7cb0: dastore
      // 7cb1: dup
      // 7cb2: bipush 16
      // 7cb4: ldc2_w -7.698155155722897E-8
      // 7cb7: dastore
      // 7cb8: dup
      // 7cb9: bipush 17
      // 7cbb: ldc2_w 5.051784853384516E-8
      // 7cbe: dastore
      // 7cbf: dup
      // 7cc0: bipush 18
      // 7cc2: ldc2_w -4.443661736519001E-8
      // 7cc5: dastore
      // 7cc6: dup
      // 7cc7: bipush 19
      // 7cc9: ldc2_w 1.1593958457401774E-7
      // 7ccc: dastore
      // 7ccd: dup
      // 7cce: bipush 20
      // 7cd0: ldc2_w 5.575759739697068E-8
      // 7cd3: dastore
      // 7cd4: dup
      // 7cd5: bipush 21
      // 7cd7: ldc2_w 1.4385227981629147E-8
      // 7cda: dastore
      // 7cdb: dup
      // 7cdc: bipush 22
      // 7cde: ldc2_w -7.227368462584163E-9
      // 7ce1: dastore
      // 7ce2: dup
      // 7ce3: bipush 23
      // 7ce5: ldc2_w -8.129108387083023E-9
      // 7ce8: dastore
      // 7ce9: dup
      // 7cea: bipush 24
      // 7cec: ldc2_w 1.263202100290635E-8
      // 7cef: dastore
      // 7cf0: dup
      // 7cf1: bipush 25
      // 7cf3: ldc2_w 5.600896265625552E-8
      // 7cf6: dastore
      // 7cf7: dup
      // 7cf8: bipush 26
      // 7cfa: ldc2_w -1.154629885168314E-7
      // 7cfd: dastore
      // 7cfe: dup
      // 7cff: bipush 27
      // 7d01: ldc2_w -2.399186832888246E-8
      // 7d04: dastore
      // 7d05: dup
      // 7d06: bipush 28
      // 7d08: ldc2_w 9.295948298604103E-8
      // 7d0b: dastore
      // 7d0c: dup
      // 7d0d: bipush 29
      // 7d0f: ldc2_w -2.070841011504222E-9
      // 7d12: dastore
      // 7d13: dup
      // 7d14: bipush 30
      // 7d16: ldc2_w -6.97066538508643E-8
      // 7d19: dastore
      // 7d1a: dup
      // 7d1b: bipush 31
      // 7d1d: ldc2_w -1.0898941254272996E-7
      // 7d20: dastore
      // 7d21: dup
      // 7d22: bipush 32
      // 7d24: ldc2_w -1.1895963756343625E-7
      // 7d27: dastore
      // 7d28: dup
      // 7d29: bipush 33
      // 7d2b: ldc2_w -9.865691193993138E-8
      // 7d2e: dastore
      // 7d2f: dup
      // 7d30: bipush 34
      // 7d32: ldc2_w -4.711988033385175E-8
      // 7d35: dastore
      // 7d36: dup
      // 7d37: bipush 35
      // 7d39: ldc2_w 3.6613751875298095E-8
      // 7d3c: dastore
      // 7d3d: dup
      // 7d3e: bipush 36
      // 7d40: ldc2_w -8.491135959370133E-8
      // 7d43: dastore
      // 7d44: dup
      // 7d45: bipush 37
      // 7d47: ldc2_w 6.610611940107793E-8
      // 7d4a: dastore
      // 7d4b: dup
      // 7d4c: bipush 38
      // 7d4e: ldc2_w 1.3794148633283659E-8
      // 7d51: dastore
      // 7d52: dup
      // 7d53: bipush 39
      // 7d55: ldc2_w -2.462631860370667E-9
      // 7d58: dastore
      // 7d59: dup
      // 7d5a: bipush 40
      // 7d5c: ldc2_w 1.830278273495162E-8
      // 7d5f: dastore
      // 7d60: dup
      // 7d61: bipush 41
      // 7d63: ldc2_w 7.705834203598065E-8
      // 7d66: dastore
      // 7d67: dup
      // 7d68: bipush 42
      // 7d6a: ldc2_w -6.364563771711373E-8
      // 7d6d: dastore
      // 7d6e: dup
      // 7d6f: bipush 43
      // 7d71: ldc2_w 7.39978436695387E-8
      // 7d74: dastore
      // 7d75: dup
      // 7d76: bipush 44
      // 7d78: ldc2_w 1.4122417557484554E-8
      // 7d7b: dastore
      // 7d7c: dup
      // 7d7d: bipush 45
      // 7d7f: ldc2_w -3.881598887298574E-9
      // 7d82: dastore
      // 7d83: dup
      // 7d84: bipush 46
      // 7d86: ldc2_w 2.0958481826069642E-8
      // 7d89: dastore
      // 7d8a: dup
      // 7d8b: bipush 47
      // 7d8d: ldc2_w 8.96162975425619E-8
      // 7d90: dastore
      // 7d91: dup
      // 7d92: bipush 48
      // 7d94: ldc2_w -3.535214171178576E-8
      // 7d97: dastore
      // 7d98: dup
      // 7d99: bipush 49
      // 7d9b: ldc2_w -1.1455271549574576E-7
      // 7d9e: dastore
      // 7d9f: dup
      // 7da0: bipush 50
      // 7da2: ldc2_w 9.140964977432485E-8
      // 7da5: dastore
      // 7da6: dup
      // 7da7: bipush 51
      // 7da9: ldc2_w 1.0667524445105459E-7
      // 7dac: dastore
      // 7dad: dup
      // 7dae: bipush 52
      // 7db0: ldc2_w -6.777752790396222E-8
      // 7db3: dastore
      // 7db4: dup
      // 7db5: bipush 53
      // 7db7: ldc2_w 4.586785041291296E-8
      // 7dba: dastore
      // 7dbb: dup
      // 7dbc: bipush 54
      // 7dbe: ldc2_w -2.8245462428022094E-8
      // 7dc1: dastore
      // 7dc2: dup
      // 7dc3: bipush 55
      // 7dc5: ldc2_w -5.071761314397018E-8
      // 7dc8: dastore
      // 7dc9: dup
      // 7dca: bipush 56
      // 7dcc: ldc2_w -2.0566368810068663E-8
      // 7dcf: dastore
      // 7dd0: dup
      // 7dd1: bipush 57
      // 7dd3: ldc2_w 6.319146317890346E-8
      // 7dd6: dastore
      // 7dd7: dup
      // 7dd8: bipush 58
      // 7dda: ldc2_w -3.687854305539139E-8
      // 7ddd: dastore
      // 7dde: dup
      // 7ddf: bipush 59
      // 7de1: ldc2_w -8.137269363160008E-8
      // 7de4: dastore
      // 7de5: dup
      // 7de6: bipush 60
      // 7de8: ldc2_w -6.930491127388755E-8
      // 7deb: dastore
      // 7dec: dup
      // 7ded: bipush 61
      // 7def: ldc2_w 3.1184473002226595E-10
      // 7df2: dastore
      // 7df3: dup
      // 7df4: bipush 62
      // 7df6: ldc2_w -1.0995299963140049E-7
      // 7df9: dastore
      // 7dfa: dup
      // 7dfb: bipush 63
      // 7dfd: ldc2_w 7.772668425499348E-8
      // 7e00: dastore
      // 7e01: dup
      // 7e02: bipush 64
      // 7e04: ldc2_w 8.750367485925089E-8
      // 7e07: dastore
      // 7e08: dup
      // 7e09: bipush 65
      // 7e0b: ldc2_w -7.963112393823186E-8
      // 7e0e: dastore
      // 7e0f: dup
      // 7e10: bipush 66
      // 7e12: ldc2_w 5.415131809829094E-8
      // 7e15: dastore
      // 7e16: dup
      // 7e17: bipush 67
      // 7e19: ldc2_w 1.3006683896462346E-8
      // 7e1c: dastore
      // 7e1d: dup
      // 7e1e: bipush 68
      // 7e20: ldc2_w 3.634736373360733E-8
      // 7e23: dastore
      // 7e24: dup
      // 7e25: bipush 69
      // 7e27: ldc2_w -1.132504393233074E-7
      // 7e2a: dastore
      // 7e2b: dup
      // 7e2c: bipush 70
      // 7e2e: ldc2_w 4.2046187038837375E-8
      // 7e31: dastore
      // 7e32: dup
      // 7e33: bipush 71
      // 7e35: ldc2_w 2.6396811618001066E-8
      // 7e38: dastore
      // 7e39: dup
      // 7e3a: bipush 72
      // 7e3c: ldc2_w 7.92177143584738E-8
      // 7e3f: dastore
      // 7e40: dup
      // 7e41: bipush 73
      // 7e43: ldc2_w -3.691100820545433E-8
      // 7e46: dastore
      // 7e47: dup
      // 7e48: bipush 74
      // 7e4a: ldc2_w -8.257112559083188E-8
      // 7e4d: dastore
      // 7e4e: dup
      // 7e4f: bipush 75
      // 7e51: ldc2_w -5.676200971739166E-8
      // 7e54: dastore
      // 7e55: dup
      // 7e56: bipush 76
      // 7e58: ldc2_w 4.151794514828518E-8
      // 7e5b: dastore
      // 7e5c: dup
      // 7e5d: bipush 77
      // 7e5f: ldc2_w -2.5147255753587636E-8
      // 7e62: dastore
      // 7e63: dup
      // 7e64: bipush 78
      // 7e66: ldc2_w -1.7335469415174996E-8
      // 7e69: dastore
      // 7e6a: dup
      // 7e6b: bipush 79
      // 7e6d: ldc2_w 6.595784859136531E-8
      // 7e70: dastore
      // 7e71: dup
      // 7e72: bipush 80
      // 7e74: ldc2_w -1.2680354928109105E-8
      // 7e77: dastore
      // 7e78: dup
      // 7e79: bipush 81
      // 7e7b: ldc2_w -1.3824992526093461E-8
      // 7e7e: dastore
      // 7e7f: dup
      // 7e80: bipush 82
      // 7e82: ldc2_w 6.353142754175797E-8
      // 7e85: dastore
      // 7e86: dup
      // 7e87: bipush 83
      // 7e89: ldc2_w -1.8021197722549054E-8
      // 7e8c: dastore
      // 7e8d: dup
      // 7e8e: bipush 84
      // 7e90: ldc2_w -1.9054827792903468E-8
      // 7e93: dastore
      // 7e94: dup
      // 7e95: bipush 85
      // 7e97: ldc2_w 6.144098503892116E-8
      // 7e9a: dastore
      // 7e9b: dup
      // 7e9c: bipush 86
      // 7e9e: ldc2_w -1.3940903373095247E-8
      // 7ea1: dastore
      // 7ea2: dup
      // 7ea3: bipush 87
      // 7ea5: ldc2_w -5.7694907599522404E-9
      // 7ea8: dastore
      // 7ea9: dup
      // 7eaa: bipush 88
      // 7eac: ldc2_w 8.696863522320578E-8
      // 7eaf: dastore
      // 7eb0: dup
      // 7eb1: bipush 89
      // 7eb3: ldc2_w 2.6869297963554945E-8
      // 7eb6: dastore
      // 7eb7: dup
      // 7eb8: bipush 90
      // 7eba: ldc2_w 5.3366470162689076E-8
      // 7ebd: dastore
      // 7ebe: dup
      // 7ebf: bipush 91
      // 7ec1: ldc2_w -7.094204160127543E-8
      // 7ec4: dastore
      // 7ec5: dup
      // 7ec6: bipush 92
      // 7ec8: ldc2_w -1.0662027949814858E-7
      // 7ecb: dastore
      // 7ecc: dup
      // 7ecd: bipush 93
      // 7ecf: ldc2_w -5.26498707801063E-8
      // 7ed2: dastore
      // 7ed3: dup
      // 7ed4: bipush 94
      // 7ed6: ldc2_w 9.198855229106814E-8
      // 7ed9: dastore
      // 7eda: dup
      // 7edb: bipush 95
      // 7edd: ldc2_w 8.989677431456647E-8
      // 7ee0: dastore
      // 7ee1: dup
      // 7ee2: bipush 96
      // 7ee4: ldc2_w -5.790384407322479E-8
      // 7ee7: dastore
      // 7ee8: dup
      // 7ee9: bipush 97
      // 7eeb: ldc2_w -1.1197236522467887E-7
      // 7eee: dastore
      // 7eef: dup
      // 7ef0: bipush 98
      // 7ef2: ldc2_w -7.12854317090566E-8
      // 7ef5: dastore
      // 7ef6: dup
      // 7ef7: bipush 99
      // 7ef9: ldc2_w 6.51813137650059E-8
      // 7efc: dastore
      // 7efd: dup
      // 7efe: bipush 100
      // 7f00: ldc2_w 6.003465022483798E-8
      // 7f03: dastore
      // 7f04: dup
      // 7f05: bipush 101
      // 7f07: ldc2_w -8.569906238528267E-8
      // 7f0a: dastore
      // 7f0b: dup
      // 7f0c: bipush 102
      // 7f0e: ldc2_w 1.0584469687624562E-7
      // 7f11: dastore
      // 7f12: dup
      // 7f13: bipush 103
      // 7f15: ldc2_w -7.956144278281947E-8
      // 7f18: dastore
      // 7f19: dup
      // 7f1a: bipush 104
      // 7f1c: ldc2_w 7.43676272093501E-8
      // 7f1f: dastore
      // 7f20: dup
      // 7f21: bipush 105
      // 7f23: ldc2_w 9.182512565315022E-8
      // 7f26: dastore
      // 7f27: dup
      // 7f28: bipush 106
      // 7f2a: ldc2_w -2.6157563728873715E-8
      // 7f2d: dastore
      // 7f2e: dup
      // 7f2f: bipush 107
      // 7f31: ldc2_w -4.012947040998503E-8
      // 7f34: dastore
      // 7f35: dup
      // 7f36: bipush 108
      // 7f38: ldc2_w 5.094280572218447E-8
      // 7f3b: dastore
      // 7f3c: dup
      // 7f3d: bipush 109
      // 7f3f: ldc2_w 9.675095351161728E-9
      // 7f42: dastore
      // 7f43: dup
      // 7f44: bipush 110
      // 7f46: ldc2_w 7.552139802281006E-8
      // 7f49: dastore
      // 7f4a: dup
      // 7f4b: bipush 111
      // 7f4d: ldc2_w 1.1099566726533146E-8
      // 7f50: dastore
      // 7f51: dup
      // 7f52: bipush 112
      // 7f54: ldc2_w 5.58656252899437E-8
      // 7f57: dastore
      // 7f58: dup
      // 7f59: bipush 113
      // 7f5b: ldc2_w -2.756054703800197E-8
      // 7f5e: dastore
      // 7f5f: dup
      // 7f60: bipush 114
      // 7f62: ldc2_w 2.791018095971047E-10
      // 7f65: dastore
      // 7f66: dup
      // 7f67: bipush 115
      // 7f69: ldc2_w -9.799351869734466E-8
      // 7f6c: dastore
      // 7f6d: dup
      // 7f6e: bipush 116
      // 7f70: ldc2_w -8.291832428736212E-8
      // 7f73: dastore
      // 7f74: dup
      // 7f75: bipush 117
      // 7f77: ldc2_w 4.654720780112994E-8
      // 7f7a: dastore
      // 7f7b: dup
      // 7f7c: bipush 118
      // 7f7e: ldc2_w 5.302803981406403E-8
      // 7f81: dastore
      // 7f82: dup
      // 7f83: bipush 119
      // 7f85: ldc2_w -6.243126731995636E-8
      // 7f88: dastore
      // 7f89: dup
      // 7f8a: bipush 120
      // 7f8c: ldc2_w -6.036655299348577E-8
      // 7f8f: dastore
      // 7f90: dup
      // 7f91: bipush 121
      // 7f93: ldc2_w 6.026878587378257E-8
      // 7f96: dastore
      // 7f97: dup
      // 7f98: bipush 122
      // 7f9a: ldc2_w 6.210379583313526E-8
      // 7f9d: dastore
      // 7f9e: dup
      // 7f9f: bipush 123
      // 7fa1: ldc2_w -5.381287389094251E-8
      // 7fa4: dastore
      // 7fa5: dup
      // 7fa6: bipush 124
      // 7fa8: ldc2_w -4.8012970400697E-8
      // 7fab: dastore
      // 7fac: dup
      // 7fad: bipush 125
      // 7faf: ldc2_w 8.055420567281602E-8
      // 7fb2: dastore
      // 7fb3: dup
      // 7fb4: bipush 126
      // 7fb6: ldc2_w 9.452180117175641E-8
      // 7fb9: dastore
      // 7fba: dup
      // 7fbb: bipush 127
      // 7fbd: ldc2_w -5.057430382371206E-9
      // 7fc0: dastore
      // 7fc1: dup
      // 7fc2: sipush 128
      // 7fc5: ldc2_w 2.1288872215266507E-8
      // 7fc8: dastore
      // 7fc9: dup
      // 7fca: sipush 129
      // 7fcd: ldc2_w -6.380305844689076E-8
      // 7fd0: dastore
      // 7fd1: dup
      // 7fd2: sipush 130
      // 7fd5: ldc2_w -2.0858800984600168E-8
      // 7fd8: dastore
      // 7fd9: dup
      // 7fda: sipush 131
      // 7fdd: ldc2_w -8.724006061713588E-8
      // 7fe0: dastore
      // 7fe1: dup
      // 7fe2: sipush 132
      // 7fe5: ldc2_w -2.3470351753125604E-8
      // 7fe8: dastore
      // 7fe9: dup
      // 7fea: sipush 133
      // 7fed: ldc2_w -6.690931338790221E-8
      // 7ff0: dastore
      // 7ff1: dup
      // 7ff2: sipush 134
      // 7ff5: ldc2_w 2.192160831263035E-8
      // 7ff8: dastore
      // 7ff9: dup
      // 7ffa: sipush 135
      // 7ffd: ldc2_w 5.6648446166177225E-9
      // 8000: dastore
      // 8001: dup
      // 8002: sipush 136
      // 8005: ldc2_w -1.1461755745719884E-7
      // 8008: dastore
      // 8009: dup
      // 800a: sipush 137
      // 800d: ldc2_w -9.944393412663547E-8
      // 8010: dastore
      // 8011: dup
      // 8012: sipush 138
      // 8015: ldc2_w 5.2249837964645906E-8
      // 8018: dastore
      // 8019: dup
      // 801a: sipush 139
      // 801d: ldc2_w 1.0311034276196487E-7
      // 8020: dastore
      // 8021: dup
      // 8022: sipush 140
      // 8025: ldc2_w 5.4203784018566126E-8
      // 8028: dastore
      // 8029: dup
      // 802a: sipush 141
      // 802d: ldc2_w -9.340259278913173E-8
      // 8030: dastore
      // 8031: dup
      // 8032: sipush 142
      // 8035: ldc2_w -1.0022192034216903E-7
      // 8038: dastore
      // 8039: dup
      // 803a: sipush 143
      // 803d: ldc2_w 3.481513333662908E-8
      // 8040: dastore
      // 8041: dup
      // 8042: sipush 144
      // 8045: ldc2_w 7.436036590244714E-8
      // 8048: dastore
      // 8049: dup
      // 804a: sipush 145
      // 804d: ldc2_w 1.9485199912395296E-8
      // 8050: dastore
      // 8051: dup
      // 8052: sipush 146
      // 8055: ldc2_w 1.0968068384729757E-7
      // 8058: dastore
      // 8059: dup
      // 805a: sipush 147
      // 805d: ldc2_w 1.0760175582979094E-7
      // 8060: dastore
      // 8061: dup
      // 8062: sipush 148
      // 8065: ldc2_w 1.4322981952798675E-8
      // 8068: dastore
      // 8069: dup
      // 806a: sipush 149
      // 806d: ldc2_w 6.933855730431659E-8
      // 8070: dastore
      // 8071: dup
      // 8072: sipush 150
      // 8075: ldc2_w 3.530656968851287E-8
      // 8078: dastore
      // 8079: dup
      // 807a: sipush 151
      // 807d: ldc2_w -8.669526204279467E-8
      // 8080: dastore
      // 8081: dup
      // 8082: sipush 152
      // 8085: ldc2_w -5.7169586962345785E-8
      // 8088: dastore
      // 8089: dup
      // 808a: sipush 153
      // 808d: ldc2_w -1.1345515834332824E-7
      // 8090: dastore
      // 8091: dup
      // 8092: sipush 154
      // 8095: ldc2_w -1.605251622332555E-8
      // 8098: dastore
      // 8099: dup
      // 809a: sipush 155
      // 809d: ldc2_w -2.298302779758532E-9
      // 80a0: dastore
      // 80a1: dup
      // 80a2: sipush 156
      // 80a5: ldc2_w -7.110952399338234E-8
      // 80a8: dastore
      // 80a9: dup
      // 80aa: sipush 157
      // 80ad: ldc2_w 1.70164513845372E-8
      // 80b0: dastore
      // 80b1: dup
      // 80b2: sipush 158
      // 80b5: ldc2_w 2.4746155561368937E-8
      // 80b8: dastore
      // 80b9: dup
      // 80ba: sipush 159
      // 80bd: ldc2_w -4.6834239957353325E-8
      // 80c0: dastore
      // 80c1: dup
      // 80c2: sipush 160
      // 80c5: ldc2_w 4.1781076667923185E-8
      // 80c8: dastore
      // 80c9: dup
      // 80ca: sipush 161
      // 80cd: ldc2_w 5.326182134294869E-8
      // 80d0: dastore
      // 80d1: dup
      // 80d2: sipush 162
      // 80d5: ldc2_w -1.1302647617762544E-8
      // 80d8: dastore
      // 80d9: dup
      // 80da: sipush 163
      // 80dd: ldc2_w 8.759667154796094E-8
      // 80e0: dastore
      // 80e1: dup
      // 80e2: sipush 164
      // 80e5: ldc2_w 1.126326877851684E-7
      // 80e8: dastore
      // 80e9: dup
      // 80ea: sipush 165
      // 80ed: ldc2_w 6.48979555673987E-8
      // 80f0: dastore
      // 80f1: dup
      // 80f2: sipush 166
      // 80f5: ldc2_w -5.451390316294111E-8
      // 80f8: dastore
      // 80f9: dup
      // 80fa: sipush 167
      // 80fd: ldc2_w -6.0896188500539086E-9
      // 8100: dastore
      // 8101: dup
      // 8102: sipush 168
      // 8105: ldc2_w -2.7152010585461855E-8
      // 8108: dastore
      // 8109: dup
      // 810a: sipush 169
      // 810d: ldc2_w -1.1660424775832058E-7
      // 8110: dastore
      // 8111: dup
      // 8112: sipush 170
      // 8115: ldc2_w -3.492984900939992E-8
      // 8118: dastore
      // 8119: dup
      // 811a: sipush 171
      // 811d: ldc2_w -1.944841848873016E-8
      // 8120: dastore
      // 8121: dup
      // 8122: sipush 172
      // 8125: ldc2_w -6.905990750285027E-8
      // 8128: dastore
      // 8129: dup
      // 812a: sipush 173
      // 812d: ldc2_w 5.575538653428039E-8
      // 8130: dastore
      // 8131: dup
      // 8132: sipush 174
      // 8135: ldc2_w 1.1768108384670781E-7
      // 8138: dastore
      // 8139: dup
      // 813a: sipush 175
      // 813d: ldc2_w 1.178204606523101E-7
      // 8140: dastore
      // 8141: dup
      // 8142: sipush 176
      // 8145: ldc2_w 5.727787111340131E-8
      // 8148: dastore
      // 8149: dup
      // 814a: sipush 177
      // 814d: ldc2_w -6.284125161007433E-8
      // 8150: dastore
      // 8151: dup
      // 8152: sipush 178
      // 8155: ldc2_w -3.0118152047565877E-9
      // 8158: dastore
      // 8159: dup
      // 815a: sipush 179
      // 815d: ldc2_w -5.448044533034374E-10
      // 8160: dastore
      // 8161: dup
      // 8162: sipush 180
      // 8165: ldc2_w -5.433154287341921E-8
      // 8168: dastore
      // 8169: dup
      // 816a: sipush 181
      // 816d: ldc2_w 7.515630833946181E-8
      // 8170: dastore
      // 8171: dup
      // 8172: sipush 182
      // 8175: ldc2_w -8.780756503572527E-8
      // 8178: dastore
      // 8179: dup
      // 817a: sipush 183
      // 817d: ldc2_w -6.527407547535494E-8
      // 8180: dastore
      // 8181: dup
      // 8182: sipush 184
      // 8185: ldc2_w -9.45487863616303E-8
      // 8188: dastore
      // 8189: dup
      // 818a: sipush 185
      // 818d: ldc2_w 6.390098458668406E-8
      // 8190: dastore
      // 8191: dup
      // 8192: sipush 186
      // 8195: ldc2_w -6.564672913105876E-8
      // 8198: dastore
      // 8199: dup
      // 819a: sipush 187
      // 819d: ldc2_w -5.238488022920792E-9
      // 81a0: dastore
      // 81a1: dup
      // 81a2: sipush 188
      // 81a5: ldc2_w 7.824500749252316E-9
      // 81a8: dastore
      // 81a9: dup
      // 81aa: sipush 189
      // 81ad: ldc2_w -2.5339299158309795E-8
      // 81b0: dastore
      // 81b1: dup
      // 81b2: sipush 190
      // 81b5: ldc2_w -1.036103313062145E-7
      // 81b8: dastore
      // 81b9: dup
      // 81ba: sipush 191
      // 81bd: ldc2_w 1.2550633697348567E-8
      // 81c0: dastore
      // 81c1: dup
      // 81c2: sipush 192
      // 81c5: ldc2_w 8.584676196065558E-8
      // 81c8: dastore
      // 81c9: dup
      // 81ca: sipush 193
      // 81cd: ldc2_w 1.1740089468291563E-7
      // 81d0: dastore
      // 81d1: dup
      // 81d2: sipush 194
      // 81d5: ldc2_w 1.0833697012353316E-7
      // 81d8: dastore
      // 81d9: dup
      // 81da: sipush 195
      // 81dd: ldc2_w 5.978002467397905E-8
      // 81e0: dastore
      // 81e1: dup
      // 81e2: sipush 196
      // 81e5: ldc2_w -2.7143806069290897E-8
      // 81e8: dastore
      // 81e9: dup
      // 81ea: sipush 197
      // 81ed: ldc2_w 8.711129287069315E-8
      // 81f0: dastore
      // 81f1: dup
      // 81f2: sipush 198
      // 81f5: ldc2_w -7.316349947981893E-8
      // 81f8: dastore
      // 81f9: dup
      // 81fa: sipush 199
      // 81fd: ldc2_w -3.00015852582934E-8
      // 8200: dastore
      // 8201: dup
      // 8202: sipush 200
      // 8205: ldc2_w -2.0691000399732483E-8
      // 8208: dastore
      // 8209: dup
      // 820a: sipush 201
      // 820d: ldc2_w -4.4100097152254264E-8
      // 8210: dastore
      // 8211: dup
      // 8212: sipush 202
      // 8215: ldc2_w -9.909612209943178E-8
      // 8218: dastore
      // 8219: dup
      // 821a: sipush 203
      // 821d: ldc2_w 5.38733640215475E-8
      // 8220: dastore
      // 8221: dup
      // 8222: sipush 204
      // 8225: ldc2_w -6.0893829005035E-8
      // 8228: dastore
      // 8229: dup
      // 822a: sipush 205
      // 822d: ldc2_w 3.457553391989844E-8
      // 8230: dastore
      // 8231: dup
      // 8232: sipush 206
      // 8235: ldc2_w 1.0300006058273187E-7
      // 8238: dastore
      // 8239: dup
      // 823a: sipush 207
      // 823d: ldc2_w -9.290053015365092E-8
      // 8240: dastore
      // 8241: dup
      // 8242: sipush 208
      // 8245: ldc2_w -7.514966995961323E-8
      // 8248: dastore
      // 8249: dup
      // 824a: sipush 209
      // 824d: ldc2_w -8.10254145615142E-8
      // 8250: dastore
      // 8251: dup
      // 8252: sipush 210
      // 8255: ldc2_w -1.0938612624777085E-7
      // 8258: dastore
      // 8259: dup
      // 825a: sipush 211
      // 825d: ldc2_w 7.932952721989251E-8
      // 8260: dastore
      // 8261: dup
      // 8262: sipush 212
      // 8265: ldc2_w 9.428257290008738E-9
      // 8268: dastore
      // 8269: dup
      // 826a: sipush 213
      // 826d: ldc2_w -7.952636967837795E-8
      // 8270: dastore
      // 8271: dup
      // 8272: sipush 214
      // 8275: ldc2_w 5.203033137154554E-8
      // 8278: dastore
      // 8279: dup
      // 827a: sipush 215
      // 827d: ldc2_w -7.159157201731446E-8
      // 8280: dastore
      // 8281: dup
      // 8282: sipush 216
      // 8285: ldc2_w 2.7593424989059015E-8
      // 8288: dastore
      // 8289: dup
      // 828a: sipush 217
      // 828d: ldc2_w 1.1231621190000476E-7
      // 8290: dastore
      // 8291: dup
      // 8292: sipush 218
      // 8295: ldc2_w -5.469119869891027E-8
      // 8298: dastore
      // 8299: dup
      // 829a: sipush 219
      // 829d: ldc2_w 4.560067256086347E-9
      // 82a0: dastore
      // 82a1: dup
      // 82a2: sipush 220
      // 82a5: ldc2_w 5.280427179595944E-8
      // 82a8: dastore
      // 82a9: dup
      // 82aa: sipush 221
      // 82ad: ldc2_w 9.119538242455128E-8
      // 82b0: dastore
      // 82b1: dup
      // 82b2: sipush 222
      // 82b5: ldc2_w -1.1753008498403413E-7
      // 82b8: dastore
      // 82b9: dup
      // 82ba: sipush 223
      // 82bd: ldc2_w -9.537874867759656E-8
      // 82c0: dastore
      // 82c1: dup
      // 82c2: sipush 224
      // 82c5: ldc2_w -7.96118345325538E-8
      // 82c8: dastore
      // 82c9: dup
      // 82ca: sipush 225
      // 82cd: ldc2_w -6.907085854395348E-8
      // 82d0: dastore
      // 82d1: dup
      // 82d2: sipush 226
      // 82d5: ldc2_w -6.259620482221904E-8
      // 82d8: dastore
      // 82d9: dup
      // 82da: sipush 227
      // 82dd: ldc2_w -5.902712448725381E-8
      // 82e0: dastore
      // 82e1: dup
      // 82e2: sipush 228
      // 82e5: ldc2_w -5.720173456146447E-8
      // 82e8: dastore
      // 82e9: dup
      // 82ea: sipush 229
      // 82ed: ldc2_w -5.5957016861703E-8
      // 82f0: dastore
      // 82f1: dup
      // 82f2: sipush 230
      // 82f5: ldc2_w -5.412881689012608E-8
      // 82f8: dastore
      // 82f9: dup
      // 82fa: sipush 231
      // 82fd: ldc2_w -5.0551842723970724E-8
      // 8300: dastore
      // 8301: dup
      // 8302: sipush 232
      // 8305: ldc2_w -4.405966390424518E-8
      // 8308: dastore
      // 8309: dup
      // 830a: sipush 233
      // 830d: ldc2_w -3.348471032333413E-8
      // 8310: dastore
      // 8311: dup
      // 8312: sipush 234
      // 8315: ldc2_w -1.7658271111516935E-8
      // 8318: dastore
      // 8319: dup
      // 831a: sipush 235
      // 831d: ldc2_w 4.589506477601956E-9
      // 8320: dastore
      // 8321: dup
      // 8322: sipush 236
      // 8325: ldc2_w 3.4429618182751655E-8
      // 8328: dastore
      // 8329: dup
      // 832a: sipush 237
      // 832d: ldc2_w 7.303420385174346E-8
      // 8330: dastore
      // 8331: dup
      // 8332: sipush 238
      // 8335: ldc2_w -1.168420305422519E-7
      // 8338: dastore
      // 8339: dup
      // 833a: sipush 239
      // 833d: ldc2_w -5.718749537552229E-8
      // 8340: dastore
      // 8341: dup
      // 8342: sipush 240
      // 8345: ldc2_w 1.4754809136835937E-8
      // 8348: dastore
      // 8349: dup
      // 834a: sipush 241
      // 834d: ldc2_w 1.001616104682875E-7
      // 8350: dastore
      // 8351: dup
      // 8352: sipush 242
      // 8355: ldc2_w -3.8207793300052055E-8
      // 8358: dastore
      // 8359: dup
      // 835a: sipush 243
      // 835d: ldc2_w 7.766278405014509E-8
      // 8360: dastore
      // 8361: dup
      // 8362: sipush 244
      // 8365: ldc2_w -2.7883635712109803E-8
      // 8368: dastore
      // 8369: dup
      // 836a: sipush 245
      // 836d: ldc2_w -1.1524714043067699E-7
      // 8370: dastore
      // 8371: dup
      // 8372: sipush 246
      // 8375: ldc2_w 5.517333625963128E-8
      // 8378: dastore
      // 8379: dup
      // 837a: sipush 247
      // 837d: ldc2_w 7.724278756071081E-9
      // 8380: dastore
      // 8381: dup
      // 8382: sipush 248
      // 8385: ldc2_w -1.7990934773848504E-8
      // 8388: dastore
      // 8389: dup
      // 838a: sipush 249
      // 838d: ldc2_w -2.0786347668702902E-8
      // 8390: dastore
      // 8391: dup
      // 8392: sipush 250
      // 8395: ldc2_w 5.251554594269693E-10
      // 8398: dastore
      // 8399: dup
      // 839a: sipush 251
      // 839d: ldc2_w 4.7131849857076246E-8
      // 83a0: dastore
      // 83a1: dup
      // 83a2: sipush 252
      // 83a5: ldc2_w -1.1819540733893871E-7
      // 83a8: dastore
      // 83a9: dup
      // 83aa: sipush 253
      // 83ad: ldc2_w -1.742885956093543E-8
      // 83b0: dastore
      // 83b1: dup
      // 83b2: sipush 254
      // 83b5: ldc2_w 1.1220467571570283E-7
      // 83b8: dastore
      // 83b9: dup
      // 83ba: sipush 255
      // 83bd: ldc2_w 3.347954541376715E-8
      // 83c0: dastore
      // 83c1: dup
      // 83c2: sipush 256
      // 83c5: ldc2_w -1.399157980498908E-8
      // 83c8: dastore
      // 83c9: dup
      // 83ca: sipush 257
      // 83cd: ldc2_w -2.9013441705763093E-8
      // 83d0: dastore
      // 83d1: dup
      // 83d2: sipush 258
      // 83d5: ldc2_w -1.0389614239253089E-8
      // 83d8: dastore
      // 83d9: dup
      // 83da: sipush 259
      // 83dd: ldc2_w 4.307749759934266E-8
      // 83e0: dastore
      // 83e1: dup
      // 83e2: sipush 260
      // 83e5: ldc2_w -1.0583192018912101E-7
      // 83e8: dastore
      // 83e9: dup
      // 83ea: sipush 261
      // 83ed: ldc2_w 2.0919226941745448E-8
      // 83f0: dastore
      // 83f1: dup
      // 83f2: sipush 262
      // 83f5: ldc2_w -5.2305110482722706E-8
      // 83f8: dastore
      // 83f9: dup
      // 83fa: sipush 263
      // 83fd: ldc2_w -8.588407110184028E-8
      // 8400: dastore
      // 8401: dup
      // 8402: sipush 264
      // 8405: ldc2_w -7.861419797923639E-8
      // 8408: dastore
      // 8409: dup
      // 840a: sipush 265
      // 840d: ldc2_w -2.929085835358592E-8
      // 8410: dastore
      // 8411: dup
      // 8412: sipush 266
      // 8415: ldc2_w 6.329175751021792E-8
      // 8418: dastore
      // 8419: dup
      // 841a: sipush 267
      // 841d: ldc2_w -3.807794163054899E-8
      // 8420: dastore
      // 8421: dup
      // 8422: sipush 268
      // 8425: ldc2_w -9.377320954068088E-8
      // 8428: dastore
      // 8429: dup
      // 842a: sipush 269
      // 842d: ldc2_w -1.0258469865953145E-7
      // 8430: dastore
      // 8431: dup
      // 8432: sipush 270
      // 8435: ldc2_w -6.330187984612758E-8
      // 8438: dastore
      // 8439: dup
      // 843a: sipush 271
      // 843d: ldc2_w 2.5286958775281306E-8
      // 8440: dastore
      // 8441: dup
      // 8442: sipush 272
      // 8445: ldc2_w -7.40238661307607E-8
      // 8448: dastore
      // 8449: dup
      // 844a: sipush 273
      // 844d: ldc2_w 1.1681688445204168E-7
      // 8450: dastore
      // 8451: dup
      // 8452: sipush 274
      // 8455: ldc2_w -1.1623125976292733E-7
      // 8458: dastore
      // 8459: dup
      // 845a: sipush 275
      // 845d: ldc2_w -5.6696107089038004E-8
      // 8460: dastore
      // 8461: dup
      // 8462: sipush 276
      // 8465: ldc2_w 5.822140627806124E-8
      // 8468: dastore
      // 8469: dup
      // 846a: sipush 277
      // 846d: ldc2_w -8.678466172071259E-9
      // 8470: dastore
      // 8471: dup
      // 8472: sipush 278
      // 8475: ldc2_w -1.7757121899175995E-8
      // 8478: dastore
      // 8479: dup
      // 847a: sipush 279
      // 847d: ldc2_w 3.220665454652531E-8
      // 8480: dastore
      // 8481: dup
      // 8482: sipush 280
      // 8485: ldc2_w -9.598330731102836E-8
      // 8488: dastore
      // 8489: dup
      // 848a: sipush 281
      // 848d: ldc2_w 7.573375369829243E-8
      // 8490: dastore
      // 8491: dup
      // 8492: sipush 282
      // 8495: ldc2_w 7.174547784678893E-8
      // 8498: dastore
      // 8499: dup
      // 849a: sipush 283
      // 849d: ldc2_w -1.0672213971363184E-7
      // 84a0: dastore
      // 84a1: dup
      // 84a2: sipush 284
      // 84a5: ldc2_w 1.8395252217743006E-8
      // 84a8: dastore
      // 84a9: dup
      // 84aa: sipush 285
      // 84ad: ldc2_w -2.8511112548600118E-8
      // 84b0: dastore
      // 84b1: dup
      // 84b2: sipush 286
      // 84b5: ldc2_w -7.79306270997787E-9
      // 84b8: dastore
      // 84b9: dup
      // 84ba: sipush 287
      // 84bd: ldc2_w 8.178019529487065E-8
      // 84c0: dastore
      // 84c1: dup
      // 84c2: sipush 288
      // 84c5: ldc2_w 3.0220784595602374E-9
      // 84c8: dastore
      // 84c9: dup
      // 84ca: sipush 289
      // 84cd: ldc2_w -4.4156343103298585E-9
      // 84d0: dastore
      // 84d1: dup
      // 84d2: sipush 290
      // 84d5: ldc2_w 6.07014616741277E-8
      // 84d8: dastore
      // 84d9: dup
      // 84da: sipush 291
      // 84dd: ldc2_w -3.8809601937571554E-8
      // 84e0: dastore
      // 84e1: dup
      // 84e2: sipush 292
      // 84e5: ldc2_w -6.329342805230603E-8
      // 84e8: dastore
      // 84e9: dup
      // 84ea: sipush 293
      // 84ed: ldc2_w -1.1511990258493999E-8
      // 84f0: dastore
      // 84f1: dup
      // 84f2: sipush 294
      // 84f5: ldc2_w 1.177739474561431E-7
      // 84f8: dastore
      // 84f9: dup
      // 84fa: sipush 295
      // 84fd: ldc2_w 8.738625278484571E-8
      // 8500: dastore
      // 8501: dup
      // 8502: sipush 296
      // 8505: ldc2_w -1.0143341551207646E-7
      // 8508: dastore
      // 8509: dup
      // 850a: sipush 297
      // 850d: ldc2_w 2.9394972678456236E-8
      // 8510: dastore
      // 8511: dup
      // 8512: sipush 298
      // 8515: ldc2_w 4.278345398213486E-9
      // 8518: dastore
      // 8519: dup
      // 851a: sipush 299
      // 851d: ldc2_w 6.28805835150457E-8
      // 8520: dastore
      // 8521: dup
      // 8522: sipush 300
      // 8525: ldc2_w -3.197037359731606E-8
      // 8528: dastore
      // 8529: dup
      // 852a: sipush 301
      // 852d: ldc2_w -4.060821046423735E-8
      // 8530: dastore
      // 8531: dup
      // 8532: sipush 302
      // 8535: ldc2_w 3.82160283750664E-8
      // 8538: dastore
      // 8539: dup
      // 853a: sipush 303
      // 853d: ldc2_w -3.2666060441373307E-8
      // 8540: dastore
      // 8541: dup
      // 8542: sipush 304
      // 8545: ldc2_w -1.3584500601329896E-8
      // 8548: dastore
      // 8549: dup
      // 854a: sipush 305
      // 854d: ldc2_w 9.671332777035621E-8
      // 8550: dastore
      // 8551: dup
      // 8552: sipush 306
      // 8555: ldc2_w 6.10626893063691E-8
      // 8558: dastore
      // 8559: dup
      // 855a: sipush 307
      // 855d: ldc2_w 1.1913723189736356E-7
      // 8560: dastore
      // 8561: dup
      // 8562: sipush 308
      // 8565: ldc2_w 3.3774671482641995E-8
      // 8568: dastore
      // 8569: dup
      // 856a: sipush 309
      // 856d: ldc2_w 4.4651109654500895E-8
      // 8570: dastore
      // 8571: dup
      // 8572: sipush 310
      // 8575: ldc2_w -8.539328154875224E-8
      // 8578: dastore
      // 8579: dup
      // 857a: sipush 311
      // 857d: ldc2_w -1.166799420361101E-7
      // 8580: dastore
      // 8581: dup
      // 8582: sipush 312
      // 8585: ldc2_w -4.794765976694151E-8
      // 8588: dastore
      // 8589: dup
      // 858a: sipush 313
      // 858d: ldc2_w -1.1635256954820579E-7
      // 8590: dastore
      // 8591: dup
      // 8592: sipush 314
      // 8595: ldc2_w -8.221241452580445E-8
      // 8598: dastore
      // 8599: dup
      // 859a: sipush 315
      // 859d: ldc2_w 5.5737717715868425E-8
      // 85a0: dastore
      // 85a1: dup
      // 85a2: sipush 316
      // 85a5: ldc2_w 6.034539636024073E-8
      // 85a8: dastore
      // 85a9: dup
      // 85aa: sipush 317
      // 85ad: ldc2_w -6.712199323081945E-8
      // 85b0: dastore
      // 85b1: dup
      // 85b2: sipush 318
      // 85b5: ldc2_w -8.697724830833087E-8
      // 85b8: dastore
      // 85b9: dup
      // 85ba: sipush 319
      // 85bd: ldc2_w 2.0494942705297694E-9
      // 85c0: dastore
      // 85c1: dup
      // 85c2: sipush 320
      // 85c5: ldc2_w -3.718924074653624E-8
      // 85c8: dastore
      // 85c9: dup
      // 85ca: sipush 321
      // 85cd: ldc2_w 3.499747150995707E-8
      // 85d0: dastore
      // 85d1: dup
      // 85d2: sipush 322
      // 85d5: ldc2_w -1.8535359161566028E-8
      // 85d8: dastore
      // 85d9: dup
      // 85da: sipush 323
      // 85dd: ldc2_w 4.1905679587096103E-8
      // 85e0: dastore
      // 85e1: dup
      // 85e2: sipush 324
      // 85e5: ldc2_w -2.0821912536551675E-8
      // 85e8: dastore
      // 85e9: dup
      // 85ea: sipush 325
      // 85ed: ldc2_w 3.297776915751238E-8
      // 85f0: dastore
      // 85f1: dup
      // 85f2: sipush 326
      // 85f5: ldc2_w -3.3835280846270374E-8
      // 85f8: dastore
      // 85f9: dup
      // 85fa: sipush 327
      // 85fd: ldc2_w 1.8437339356553904E-8
      // 8600: dastore
      // 8601: dup
      // 8602: sipush 328
      // 8605: ldc2_w -4.734187609526424E-8
      // 8608: dastore
      // 8609: dup
      // 860a: sipush 329
      // 860d: ldc2_w 8.527976799299225E-9
      // 8610: dastore
      // 8611: dup
      // 8612: sipush 330
      // 8615: ldc2_w -5.1088103279787804E-8
      // 8618: dastore
      // 8619: dup
      // 861a: sipush 331
      // 861d: ldc2_w 1.3513294656751725E-8
      // 8620: dastore
      // 8621: dup
      // 8622: sipush 332
      // 8625: ldc2_w -3.480032127343472E-8
      // 8628: dastore
      // 8629: dup
      // 862a: sipush 333
      // 862d: ldc2_w 4.367697180842916E-8
      // 8630: dastore
      // 8631: dup
      // 8632: sipush 334
      // 8635: ldc2_w 1.1815196363705356E-8
      // 8638: dastore
      // 8639: dup
      // 863a: sipush 335
      // 863d: ldc2_w 1.0932279207149782E-7
      // 8640: dastore
      // 8641: dup
      // 8642: sipush 336
      // 8645: ldc2_w 9.907230065250944E-8
      // 8648: dastore
      // 8649: dup
      // 864a: sipush 337
      // 864d: ldc2_w -1.764389559496152E-8
      // 8650: dastore
      // 8651: dup
      // 8652: sipush 338
      // 8655: ldc2_w -1.1135725625095859E-9
      // 8658: dastore
      // 8659: dup
      // 865a: sipush 339
      // 865d: ldc2_w -8.846040040259342E-8
      // 8660: dastore
      // 8661: dup
      // 8662: sipush 340
      // 8665: ldc2_w -3.996962588736431E-8
      // 8668: dastore
      // 8669: dup
      // 866a: sipush 341
      // 866d: ldc2_w -9.276238757878814E-8
      // 8670: dastore
      // 8671: dup
      // 8672: sipush 342
      // 8675: ldc2_w -7.12139818505956E-9
      // 8678: dastore
      // 8679: dup
      // 867a: sipush 343
      // 867d: ldc2_w -2.016525972830718E-8
      // 8680: dastore
      // 8681: dup
      // 8682: sipush 344
      // 8685: ldc2_w 1.0782585410141121E-7
      // 8688: dastore
      // 8689: dup
      // 868a: sipush 345
      // 868d: ldc2_w -9.868269632073771E-8
      // 8690: dastore
      // 8691: dup
      // 8692: sipush 346
      // 8695: ldc2_w 7.686861750031585E-8
      // 8698: dastore
      // 8699: dup
      // 869a: sipush 347
      // 869d: ldc2_w -7.947087669425045E-8
      // 86a0: dastore
      // 86a1: dup
      // 86a2: sipush 348
      // 86a5: ldc2_w -8.955768055535647E-8
      // 86a8: dastore
      // 86a9: dup
      // 86aa: sipush 349
      // 86ad: ldc2_w 4.791582240886607E-8
      // 86b0: dastore
      // 86b1: dup
      // 86b2: sipush 350
      // 86b5: ldc2_w 9.583994718167641E-8
      // 86b8: dastore
      // 86b9: dup
      // 86ba: sipush 351
      // 86bd: ldc2_w 5.5524866689108584E-8
      // 86c0: dastore
      // 86c1: dup
      // 86c2: sipush 352
      // 86c5: ldc2_w -7.171796605211277E-8
      // 86c8: dastore
      // 86c9: dup
      // 86ca: sipush 353
      // 86cd: ldc2_w -4.6157237582310713E-8
      // 86d0: dastore
      // 86d1: dup
      // 86d2: sipush 354
      // 86d5: ldc2_w -1.0489751005162237E-7
      // 86d8: dastore
      // 86d9: dup
      // 86da: sipush 355
      // 86dd: ldc2_w -8.204903560604627E-9
      // 86e0: dastore
      // 86e1: dup
      // 86e2: sipush 356
      // 86e5: ldc2_w 6.818588687884566E-9
      // 86e8: dastore
      // 86e9: dup
      // 86ea: sipush 357
      // 86ed: ldc2_w -5.850916105103205E-8
      // 86f0: dastore
      // 86f1: dup
      // 86f2: sipush 358
      // 86f5: ldc2_w 3.5549586192569994E-8
      // 86f8: dastore
      // 86f9: dup
      // 86fa: sipush 359
      // 86fd: ldc2_w 5.1896700056778354E-8
      // 8700: dastore
      // 8701: dup
      // 8702: sipush 360
      // 8705: ldc2_w -8.146080588190463E-9
      // 8708: dastore
      // 8709: dup
      // 870a: sipush 361
      // 870d: ldc2_w 9.516285362051742E-8
      // 8710: dastore
      // 8711: dup
      // 8712: sipush 362
      // 8715: ldc2_w -1.1368933260611668E-7
      // 8718: dastore
      // 8719: dup
      // 871a: sipush 363
      // 871d: ldc2_w 8.187871486648885E-8
      // 8720: dastore
      // 8721: dup
      // 8722: sipush 364
      // 8725: ldc2_w -3.206182925646474E-8
      // 8728: dastore
      // 8729: dup
      // 872a: sipush 365
      // 872d: ldc2_w 2.265440168347286E-8
      // 8730: dastore
      // 8731: dup
      // 8732: sipush 366
      // 8735: ldc2_w 8.938334752179552E-9
      // 8738: dastore
      // 8739: dup
      // 873a: sipush 367
      // 873d: ldc2_w -7.187922490287331E-8
      // 8740: dastore
      // 8741: dup
      // 8742: sipush 368
      // 8745: ldc2_w 1.9952407216533937E-8
      // 8748: dastore
      // 8749: dup
      // 874a: sipush 369
      // 874d: ldc2_w 4.734805892507655E-8
      // 8750: dastore
      // 8751: dup
      // 8752: sipush 370
      // 8755: ldc2_w 1.1642439930208906E-8
      // 8758: dastore
      // 8759: dup
      // 875a: sipush 371
      // 875d: ldc2_w -8.582843599651953E-8
      // 8760: dastore
      // 8761: dup
      // 8762: sipush 372
      // 8765: ldc2_w -5.3086706437795354E-9
      // 8768: dastore
      // 8769: dup
      // 876a: sipush 373
      // 876d: ldc2_w 1.6121782610217253E-8
      // 8770: dastore
      // 8771: dup
      // 8772: sipush 374
      // 8775: ldc2_w -2.0197142620980974E-8
      // 8778: dastore
      // 8779: dup
      // 877a: sipush 375
      // 877d: ldc2_w -1.129242035557684E-7
      // 8780: dastore
      // 8781: dup
      // 8782: sipush 376
      // 8785: ldc2_w -2.2298267863810133E-8
      // 8788: dastore
      // 8789: dup
      // 878a: sipush 377
      // 878d: ldc2_w 1.4605950309628873E-8
      // 8790: dastore
      // 8791: dup
      // 8792: sipush 378
      // 8795: ldc2_w -8.663710700190489E-10
      // 8798: dastore
      // 8799: dup
      // 879a: sipush 379
      // 879d: ldc2_w -6.736873974532501E-8
      // 87a0: dastore
      // 87a1: dup
      // 87a2: sipush 380
      // 87a5: ldc2_w 5.486523121881414E-8
      // 87a8: dastore
      // 87a9: dup
      // 87aa: sipush 381
      // 87ad: ldc2_w -1.0965249168570443E-7
      // 87b0: dastore
      // 87b1: dup
      // 87b2: sipush 382
      // 87b5: ldc2_w -8.27343074126263E-8
      // 87b8: dastore
      // 87b9: dup
      // 87ba: sipush 383
      // 87bd: ldc2_w -1.0144703278439455E-7
      // 87c0: dastore
      // 87c1: dup
      // 87c2: sipush 384
      // 87c5: ldc2_w 7.39809943048038E-8
      // 87c8: dastore
      // 87c9: dup
      // 87ca: sipush 385
      // 87cd: ldc2_w -3.193297932837415E-8
      // 87d0: dastore
      // 87d1: dup
      // 87d2: sipush 386
      // 87d5: ldc2_w 5.900393284617182E-8
      // 87d8: dastore
      // 87d9: dup
      // 87da: sipush 387
      // 87dd: ldc2_w 1.0973020465397083E-7
      // 87e0: dastore
      // 87e1: dup
      // 87e2: sipush 388
      // 87e5: ldc2_w -1.1681436418514489E-7
      // 87e8: dastore
      // 87e9: dup
      // 87ea: sipush 389
      // 87ed: ldc2_w 9.5985669644661E-8
      // 87f0: dastore
      // 87f1: dup
      // 87f2: sipush 390
      // 87f5: ldc2_w 3.423560333632085E-8
      // 87f8: dastore
      // 87f9: dup
      // 87fa: sipush 391
      // 87fd: ldc2_w -6.22836197265283E-8
      // 8800: dastore
      // 8801: dup
      // 8802: sipush 392
      // 8805: ldc2_w 4.621027492345726E-8
      // 8808: dastore
      // 8809: dup
      // 880a: sipush 393
      // 880d: ldc2_w -1.1575484316683829E-7
      // 8810: dastore
      // 8811: dup
      // 8812: sipush 394
      // 8815: ldc2_w -6.997545435826076E-8
      // 8818: dastore
      // 8819: dup
      // 881a: sipush 395
      // 881d: ldc2_w -5.3502441327259514E-8
      // 8820: dastore
      // 8821: dup
      // 8822: sipush 396
      // 8825: ldc2_w -6.49667713553005E-8
      // 8828: dastore
      // 8829: dup
      // 882a: sipush 397
      // 882d: ldc2_w -1.029980741248172E-7
      // 8830: dastore
      // 8831: dup
      // 8832: sipush 398
      // 8835: ldc2_w 7.219393868923887E-8
      // 8838: dastore
      // 8839: dup
      // 883a: sipush 399
      // 883d: ldc2_w -1.4854841678687828E-8
      // 8840: dastore
      // 8841: dup
      // 8842: sipush 400
      // 8845: ldc2_w 1.1406713393562271E-7
      // 8848: dastore
      // 8849: dup
      // 884a: sipush 401
      // 884d: ldc2_w -1.650155887561251E-8
      // 8850: dastore
      // 8851: dup
      // 8852: sipush 402
      // 8855: ldc2_w 7.165331603232264E-8
      // 8858: dastore
      // 8859: dup
      // 885a: sipush 403
      // 885d: ldc2_w -9.692697614257269E-8
      // 8860: dastore
      // 8861: dup
      // 8862: sipush 404
      // 8865: ldc2_w -4.402550702194912E-8
      // 8868: dastore
      // 8869: dup
      // 886a: sipush 405
      // 886d: ldc2_w -6.679737442193143E-9
      // 8870: dastore
      // 8871: dup
      // 8872: sipush 406
      // 8875: ldc2_w 1.6492800268960003E-8
      // 8878: dastore
      // 8879: dup
      // 887a: sipush 407
      // 887d: ldc2_w 2.68759245092879E-8
      // 8880: dastore
      // 8881: dup
      // 8882: sipush 408
      // 8885: ldc2_w 2.5854805721793077E-8
      // 8888: dastore
      // 8889: dup
      // 888a: sipush 409
      // 888d: ldc2_w 1.4815967715704613E-8
      // 8890: dastore
      // 8891: dup
      // 8892: sipush 410
      // 8895: ldc2_w -4.852711011229633E-9
      // 8898: dastore
      // 8899: dup
      // 889a: sipush 411
      // 889d: ldc2_w -3.176199594915881E-8
      // 88a0: dastore
      // 88a1: dup
      // 88a2: sipush 412
      // 88a5: ldc2_w -6.452129525125173E-8
      // 88a8: dastore
      // 88a9: dup
      // 88aa: sipush 413
      // 88ad: ldc2_w -1.01738658407525E-7
      // 88b0: dastore
      // 88b1: dup
      // 88b2: sipush 414
      // 88b5: ldc2_w 9.639780418418697E-8
      // 88b8: dastore
      // 88b9: dup
      // 88ba: sipush 415
      // 88bd: ldc2_w 5.4445606140746644E-8
      // 88c0: dastore
      // 88c1: dup
      // 88c2: sipush 416
      // 88c5: ldc2_w 1.2219361033150988E-8
      // 88c8: dastore
      // 88c9: dup
      // 88ca: sipush 417
      // 88cd: ldc2_w -2.8883532688356087E-8
      // 88d0: dastore
      // 88d1: dup
      // 88d2: sipush 418
      // 88d5: ldc2_w -6.746431126005811E-8
      // 88d8: dastore
      // 88d9: dup
      // 88da: sipush 419
      // 88dd: ldc2_w -1.0212284427080097E-7
      // 88e0: dastore
      // 88e1: dup
      // 88e2: sipush 420
      // 88e5: ldc2_w 1.0696094577483825E-7
      // 88e8: dastore
      // 88e9: dup
      // 88ea: sipush 421
      // 88ed: ldc2_w 8.43527683868743E-8
      // 88f0: dastore
      // 88f1: dup
      // 88f2: sipush 422
      // 88f5: ldc2_w 6.987544103716777E-8
      // 88f8: dastore
      // 88f9: dup
      // 88fa: sipush 423
      // 88fd: ldc2_w 6.493457409236137E-8
      // 8900: dastore
      // 8901: dup
      // 8902: sipush 424
      // 8905: ldc2_w 7.093715125593688E-8
      // 8908: dastore
      // 8909: dup
      // 890a: sipush 425
      // 890d: ldc2_w 8.929153091001965E-8
      // 8910: dastore
      // 8911: dup
      // 8912: sipush 426
      // 8915: ldc2_w -1.1701113164306871E-7
      // 8918: dastore
      // 8919: dup
      // 891a: sipush 427
      // 891d: ldc2_w -6.972256643013266E-8
      // 8920: dastore
      // 8921: dup
      // 8922: sipush 428
      // 8925: ldc2_w -5.848862070736576E-9
      // 8928: dastore
      // 8929: dup
      // 892a: sipush 429
      // 892d: ldc2_w 7.602385197610123E-8
      // 8930: dastore
      // 8931: dup
      // 8932: sipush 430
      // 8935: ldc2_w -6.110775144284437E-8
      // 8938: dastore
      // 8939: dup
      // 893a: sipush 431
      // 893d: ldc2_w 6.101012058093429E-8
      // 8940: dastore
      // 8941: dup
      // 8942: sipush 432
      // 8945: ldc2_w -3.304167134225169E-8
      // 8948: dastore
      // 8949: dup
      // 894a: sipush 433
      // 894d: ldc2_w -1.0342514383702196E-7
      // 8950: dastore
      // 8951: dup
      // 8952: sipush 434
      // 8955: ldc2_w 8.969907328603505E-8
      // 8958: dastore
      // 8959: dup
      // 895a: sipush 435
      // 895d: ldc2_w 7.091600108064668E-8
      // 8960: dastore
      // 8961: dup
      // 8962: sipush 436
      // 8965: ldc2_w 8.006778743052707E-8
      // 8968: dastore
      // 8969: dup
      // 896a: sipush 437
      // 896d: ldc2_w 1.1857939200074815E-7
      // 8970: dastore
      // 8971: dup
      // 8972: sipush 438
      // 8975: ldc2_w -5.0541412403312774E-8
      // 8978: dastore
      // 8979: dup
      // 897a: sipush 439
      // 897d: ldc2_w 5.0970277930552287E-8
      // 8980: dastore
      // 8981: dup
      // 8982: sipush 440
      // 8985: ldc2_w -5.229355472795119E-8
      // 8988: dastore
      // 8989: dup
      // 898a: sipush 441
      // 898d: ldc2_w 1.1793478462381443E-7
      // 8990: dastore
      // 8991: dup
      // 8992: sipush 442
      // 8995: ldc2_w 8.625007227318527E-8
      // 8998: dastore
      // 8999: dup
      // 899a: sipush 443
      // 899d: ldc2_w 9.250422086873268E-8
      // 89a0: dastore
      // 89a1: dup
      // 89a2: sipush 444
      // 89a5: ldc2_w -1.0028661472061573E-7
      // 89a8: dastore
      // 89a9: dup
      // 89aa: sipush 445
      // 89ad: ldc2_w -1.384914052949463E-8
      // 89b0: dastore
      // 89b1: dup
      // 89b2: sipush 446
      // 89b5: ldc2_w 1.1483560326413004E-7
      // 89b8: dastore
      // 89b9: dup
      // 89ba: sipush 447
      // 89bd: ldc2_w 4.878798101459259E-8
      // 89c0: dastore
      // 89c1: dup
      // 89c2: sipush 448
      // 89c5: ldc2_w 2.7866921183936055E-8
      // 89c8: dastore
      // 89c9: dup
      // 89ca: sipush 449
      // 89cd: ldc2_w 5.3514180410849046E-8
      // 89d0: dastore
      // 89d1: dup
      // 89d2: sipush 450
      // 89d5: ldc2_w -1.1124565511436785E-7
      // 89d8: dastore
      // 89d9: dup
      // 89da: sipush 451
      // 89dd: ldc2_w 1.186914813275767E-8
      // 89e0: dastore
      // 89e1: dup
      // 89e2: sipush 452
      // 89e5: ldc2_w -5.253258132241335E-8
      // 89e8: dastore
      // 89e9: dup
      // 89ea: sipush 453
      // 89ed: ldc2_w -6.458486486369316E-8
      // 89f0: dastore
      // 89f1: dup
      // 89f2: sipush 454
      // 89f5: ldc2_w -2.2838888809969377E-8
      // 89f8: dastore
      // 89f9: dup
      // 89fa: sipush 455
      // 89fd: ldc2_w 7.415557606805398E-8
      // 8a00: dastore
      // 8a01: dup
      // 8a02: sipush 456
      // 8a05: ldc2_w -1.0568403170659571E-8
      // 8a08: dastore
      // 8a09: dup
      // 8a0a: sipush 457
      // 8a0d: ldc2_w -3.7139182948393606E-8
      // 8a10: dastore
      // 8a11: dup
      // 8a12: sipush 458
      // 8a15: ldc2_w -4.1022790876160215E-9
      // 8a18: dastore
      // 8a19: dup
      // 8a1a: sipush 459
      // 8a1d: ldc2_w 8.999821367768787E-8
      // 8a20: dastore
      // 8a21: dup
      // 8a22: sipush 460
      // 8a25: ldc2_w 8.201043988912348E-9
      // 8a28: dastore
      // 8a29: dup
      // 8a2a: sipush 461
      // 8a2d: ldc2_w -9.616457442665051E-9
      // 8a30: dastore
      // 8a31: dup
      // 8a32: sipush 462
      // 8a35: ldc2_w 3.8005886250603055E-8
      // 8a38: dastore
      // 8a39: dup
      // 8a3a: sipush 463
      // 8a3d: ldc2_w -8.588890051473289E-8
      // 8a40: dastore
      // 8a41: dup
      // 8a42: sipush 464
      // 8a45: ldc2_w 9.699937202692456E-8
      // 8a48: dastore
      // 8a49: dup
      // 8a4a: sipush 465
      // 8a4d: ldc2_w 1.11298006674538E-7
      // 8a50: dastore
      // 8a51: dup
      // 8a52: sipush 466
      // 8a55: ldc2_w -4.1527104733570825E-8
      // 8a58: dastore
      // 8a59: dup
      // 8a5a: sipush 467
      // 8a5d: ldc2_w 1.1682852007826251E-7
      // 8a60: dastore
      // 8a61: dup
      // 8a62: sipush 468
      // 8a65: ldc2_w 1.1099648061301941E-7
      // 8a68: dastore
      // 8a69: dup
      // 8a6a: sipush 469
      // 8a6d: ldc2_w -5.755303038890997E-8
      // 8a70: dastore
      // 8a71: dup
      // 8a72: sipush 470
      // 8a75: ldc2_w 8.948877445235827E-8
      // 8a78: dastore
      // 8a79: dup
      // 8a7a: sipush 471
      // 8a7d: ldc2_w 7.675780395028194E-8
      // 8a80: dastore
      // 8a81: dup
      // 8a82: sipush 472
      // 8a85: ldc2_w -9.427143563390596E-8
      // 8a88: dastore
      // 8a89: dup
      // 8a8a: sipush 473
      // 8a8d: ldc2_w 5.471416081500162E-8
      // 8a90: dastore
      // 8a91: dup
      // 8a92: sipush 474
      // 8a95: ldc2_w 4.8354824064383506E-8
      // 8a98: dastore
      // 8a99: dup
      // 8a9a: sipush 475
      // 8a9d: ldc2_w -1.118706134478866E-7
      // 8aa0: dastore
      // 8aa1: dup
      // 8aa2: sipush 476
      // 8aa5: ldc2_w 5.235528379688445E-8
      // 8aa8: dastore
      // 8aa9: dup
      // 8aaa: sipush 477
      // 8aad: ldc2_w 6.567708120053687E-8
      // 8ab0: dastore
      // 8ab1: dup
      // 8ab2: sipush 478
      // 8ab5: ldc2_w -7.042204992948526E-8
      // 8ab8: dastore
      // 8ab9: dup
      // 8aba: sipush 479
      // 8abd: ldc2_w -1.1603891006723397E-7
      // 8ac0: dastore
      // 8ac1: dup
      // 8ac2: sipush 480
      // 8ac5: ldc2_w -6.968742825553785E-8
      // 8ac8: dastore
      // 8ac9: dup
      // 8aca: sipush 481
      // 8acd: ldc2_w 7.01199184127881E-8
      // 8ad0: dastore
      // 8ad1: dup
      // 8ad2: sipush 482
      // 8ad5: ldc2_w 6.645352711199266E-8
      // 8ad8: dastore
      // 8ad9: dup
      // 8ada: sipush 483
      // 8add: ldc2_w -7.919617109348822E-8
      // 8ae0: dastore
      // 8ae1: dup
      // 8ae2: sipush 484
      // 8ae5: ldc2_w 1.1149986927391714E-7
      // 8ae8: dastore
      // 8ae9: dup
      // 8aea: sipush 485
      // 8aed: ldc2_w -7.522074418324674E-8
      // 8af0: dastore
      // 8af1: dup
      // 8af2: sipush 486
      // 8af5: ldc2_w 7.739252980388984E-8
      // 8af8: dastore
      // 8af9: dup
      // 8afa: sipush 487
      // 8afd: ldc2_w 9.39987974788905E-8
      // 8b00: dastore
      // 8b01: dup
      // 8b02: sipush 488
      // 8b05: ldc2_w -2.390421480210064E-8
      // 8b08: dastore
      // 8b09: dup
      // 8b0a: sipush 489
      // 8b0d: ldc2_w -3.639873824357815E-8
      // 8b10: dastore
      // 8b11: dup
      // 8b12: sipush 490
      // 8b15: ldc2_w 5.8015881615938497E-8
      // 8b18: dastore
      // 8b19: dup
      // 8b1a: sipush 491
      // 8b1d: ldc2_w 2.2423186335040668E-8
      // 8b20: dastore
      // 8b21: dup
      // 8b22: sipush 492
      // 8b25: ldc2_w 9.674534330665206E-8
      // 8b28: dastore
      // 8b29: dup
      // 8b2a: sipush 493
      // 8b2d: ldc2_w 4.4068830785712375E-8
      // 8b30: dastore
      // 8b31: dup
      // 8b32: sipush 494
      // 8b35: ldc2_w 1.0431875573076199E-7
      // 8b38: dastore
      // 8b39: dup
      // 8b3a: sipush 495
      // 8b3d: ldc2_w 4.0584538834428926E-8
      // 8b40: dastore
      // 8b41: dup
      // 8b42: sipush 496
      // 8b45: ldc2_w 9.279423236781974E-8
      // 8b48: dastore
      // 8b49: dup
      // 8b4a: sipush 497
      // 8b4d: ldc2_w 2.404020521381534E-8
      // 8b50: dastore
      // 8b51: dup
      // 8b52: sipush 498
      // 8b55: ldc2_w 7.425346071427343E-8
      // 8b58: dastore
      // 8b59: dup
      // 8b5a: sipush 499
      // 8b5d: ldc2_w 6.529321706138789E-9
      // 8b60: dastore
      // 8b61: dup
      // 8b62: sipush 500
      // 8b65: ldc2_w 6.080174837146273E-8
      // 8b68: dastore
      // 8b69: dup
      // 8b6a: sipush 501
      // 8b6d: ldc2_w 1.6902327633329284E-10
      // 8b70: dastore
      // 8b71: dup
      // 8b72: sipush 502
      // 8b75: ldc2_w 6.456806922371733E-8
      // 8b78: dastore
      // 8b79: dup
      // 8b7a: sipush 503
      // 8b7d: ldc2_w 1.7100134295216033E-8
      // 8b80: dastore
      // 8b81: dup
      // 8b82: sipush 504
      // 8b85: ldc2_w 9.770510970673519E-8
      // 8b88: dastore
      // 8b89: dup
      // 8b8a: sipush 505
      // 8b8d: ldc2_w 6.94872148530716E-8
      // 8b90: dastore
      // 8b91: dup
      // 8b92: sipush 506
      // 8b95: ldc2_w -6.602926393514549E-8
      // 8b98: dastore
      // 8b99: dup
      // 8b9a: sipush 507
      // 8b9d: ldc2_w -6.889997193778161E-8
      // 8ba0: dastore
      // 8ba1: dup
      // 8ba2: sipush 508
      // 8ba5: ldc2_w 6.240235720677117E-8
      // 8ba8: dastore
      // 8ba9: dup
      // 8baa: sipush 509
      // 8bad: ldc2_w 9.098790295810902E-8
      // 8bb0: dastore
      // 8bb1: dup
      // 8bb2: sipush 510
      // 8bb5: ldc2_w 1.8386917534879182E-8
      // 8bb8: dastore
      // 8bb9: dup
      // 8bba: sipush 511
      // 8bbd: ldc2_w 8.454972737414241E-8
      // 8bc0: dastore
      // 8bc1: dup
      // 8bc2: sipush 512
      // 8bc5: ldc2_w 5.259099728747365E-8
      // 8bc8: dastore
      // 8bc9: dup
      // 8bca: sipush 513
      // 8bcd: ldc2_w -7.595453077213505E-8
      // 8bd0: dastore
      // 8bd1: dup
      // 8bd2: sipush 514
      // 8bd5: ldc2_w -6.113203624663034E-8
      // 8bd8: dastore
      // 8bd9: dup
      // 8bda: sipush 515
      // 8bdd: ldc2_w 9.859622328905143E-8
      // 8be0: dastore
      // 8be1: dup
      // 8be2: sipush 516
      // 8be5: ldc2_w -7.206766550807255E-8
      // 8be8: dastore
      // 8be9: dup
      // 8bea: sipush 517
      // 8bed: ldc2_w -9.474579567171831E-8
      // 8bf0: dastore
      // 8bf1: dup
      // 8bf2: sipush 518
      // 8bf5: ldc2_w 3.210408693366267E-8
      // 8bf8: dastore
      // 8bf9: dup
      // 8bfa: sipush 519
      // 8bfd: ldc2_w 7.160716418525417E-8
      // 8c00: dastore
      // 8c01: dup
      // 8c02: sipush 520
      // 8c05: ldc2_w 2.530870537724554E-8
      // 8c08: dastore
      // 8c09: dup
      // 8c0a: sipush 521
      // 8c0d: ldc2_w -1.0524451040704701E-7
      // 8c10: dastore
      // 8c11: dup
      // 8c12: sipush 522
      // 8c15: ldc2_w -8.008561371849434E-8
      // 8c18: dastore
      // 8c19: dup
      // 8c1a: sipush 523
      // 8c1d: ldc2_w 1.0233519853128553E-7
      // 8c20: dastore
      // 8c21: dup
      // 8c22: sipush 524
      // 8c25: ldc2_w -3.326791455362767E-8
      // 8c28: dastore
      // 8c29: dup
      // 8c2a: sipush 525
      // 8c2d: ldc2_w -8.504961764629757E-9
      // 8c30: dastore
      // 8c31: dup
      // 8c32: sipush 526
      // 8c35: ldc2_w -6.024017201863256E-8
      // 8c38: dastore
      // 8c39: dup
      // 8c3a: sipush 527
      // 8c3d: ldc2_w 5.1500902632092514E-8
      // 8c40: dastore
      // 8c41: dup
      // 8c42: sipush 528
      // 8c45: ldc2_w 8.98570720774568E-8
      // 8c48: dastore
      // 8c49: dup
      // 8c4a: sipush 529
      // 8c4d: ldc2_w 5.638724693948384E-8
      // 8c50: dastore
      // 8c51: dup
      // 8c52: sipush 530
      // 8c55: ldc2_w -4.734813904255994E-8
      // 8c58: dastore
      // 8c59: dup
      // 8c5a: sipush 531
      // 8c5d: ldc2_w 1.8631451577542948E-8
      // 8c60: dastore
      // 8c61: dup
      // 8c62: sipush 532
      // 8c65: ldc2_w 1.7470924137873214E-8
      // 8c68: dastore
      // 8c69: dup
      // 8c6a: sipush 533
      // 8c6d: ldc2_w -4.926470933588261E-8
      // 8c70: dastore
      // 8c71: dup
      // 8c72: sipush 534
      // 8c75: ldc2_w 5.84096713620797E-8
      // 8c78: dastore
      // 8c79: dup
      // 8c7a: sipush 535
      // 8c7d: ldc2_w 1.0364355880696472E-7
      // 8c80: dastore
      // 8c81: dup
      // 8c82: sipush 536
      // 8c85: ldc2_w 8.800655674349468E-8
      // 8c88: dastore
      // 8c89: dup
      // 8c8a: sipush 537
      // 8c8d: ldc2_w 1.3069802481237792E-8
      // 8c90: dastore
      // 8c91: dup
      // 8c92: sipush 538
      // 8c95: ldc2_w 1.1882454749452428E-7
      // 8c98: dastore
      // 8c99: dup
      // 8c9a: sipush 539
      // 8c9d: ldc2_w -6.999215748398631E-8
      // 8ca0: dastore
      // 8ca1: dup
      // 8ca2: sipush 540
      // 8ca5: ldc2_w -7.49674072510849E-8
      // 8ca8: dastore
      // 8ca9: dup
      // 8caa: sipush 541
      // 8cad: ldc2_w 1.054760847603618E-7
      // 8cb0: dastore
      // 8cb1: dup
      // 8cb2: sipush 542
      // 8cb5: ldc2_w -3.920012014371067E-9
      // 8cb8: dastore
      // 8cb9: dup
      // 8cba: sipush 543
      // 8cbd: ldc2_w 7.526183084319617E-8
      // 8cc0: dastore
      // 8cc1: dup
      // 8cc2: sipush 544
      // 8cc5: ldc2_w 1.0618494853096868E-7
      // 8cc8: dastore
      // 8cc9: dup
      // 8cca: sipush 545
      // 8ccd: ldc2_w 9.043280094115832E-8
      // 8cd0: dastore
      // 8cd1: dup
      // 8cd2: sipush 546
      // 8cd5: ldc2_w 2.9590395068826316E-8
      // 8cd8: dastore
      // 8cd9: dup
      // 8cda: sipush 547
      // 8cdd: ldc2_w -7.475571347653619E-8
      // 8ce0: dastore
      // 8ce1: dup
      // 8ce2: sipush 548
      // 8ce5: ldc2_w 1.7401160143611842E-8
      // 8ce8: dastore
      // 8ce9: dup
      // 8cea: sipush 549
      // 8ced: ldc2_w 6.923209420670962E-8
      // 8cf0: dastore
      // 8cf1: dup
      // 8cf2: sipush 550
      // 8cf5: ldc2_w 8.232829924979753E-8
      // 8cf8: dastore
      // 8cf9: dup
      // 8cfa: sipush 551
      // 8cfd: ldc2_w 5.82825404854514E-8
      // 8d00: dastore
      // 8d01: dup
      // 8d02: sipush 552
      // 8d05: ldc2_w -1.3108606792380822E-9
      // 8d08: dastore
      // 8d09: dup
      // 8d0a: sipush 553
      // 8d0d: ldc2_w -9.485602512220194E-8
      // 8d10: dastore
      // 8d11: dup
      // 8d12: sipush 554
      // 8d15: ldc2_w 1.7663064617118723E-8
      // 8d18: dastore
      // 8d19: dup
      // 8d1a: sipush 555
      // 8d1d: ldc2_w 9.942682855652123E-8
      // 8d20: dastore
      // 8d21: dup
      // 8d22: sipush 556
      // 8d25: ldc2_w -8.638275100090915E-8
      // 8d28: dastore
      // 8d29: dup
      // 8d2a: sipush 557
      // 8d2d: ldc2_w -6.132639063569726E-8
      // 8d30: dastore
      // 8d31: dup
      // 8d32: sipush 558
      // 8d35: ldc2_w -6.221897889344726E-8
      // 8d38: dastore
      // 8d39: dup
      // 8d3a: sipush 559
      // 8d3d: ldc2_w -8.745525834919404E-8
      // 8d40: dastore
      // 8d41: dup
      // 8d42: sipush 560
      // 8d45: ldc2_w 1.029901759234897E-7
      // 8d48: dastore
      // 8d49: dup
      // 8d4a: sipush 561
      // 8d4d: ldc2_w 3.3888561478632076E-8
      // 8d50: dastore
      // 8d51: dup
      // 8d52: sipush 562
      // 8d55: ldc2_w -5.47315553588771E-8
      // 8d58: dastore
      // 8d59: dup
      // 8d5a: sipush 563
      // 8d5d: ldc2_w 7.715994473741065E-8
      // 8d60: dastore
      // 8d61: dup
      // 8d62: sipush 564
      // 8d65: ldc2_w -4.566098167230033E-8
      // 8d68: dastore
      // 8d69: dup
      // 8d6a: sipush 565
      // 8d6d: ldc2_w 5.5257514455273825E-8
      // 8d70: dastore
      // 8d71: dup
      // 8d72: sipush 566
      // 8d75: ldc2_w -9.530545662611411E-8
      // 8d78: dastore
      // 8d79: dup
      // 8d7a: sipush 567
      // 8d7d: ldc2_w -1.889488909834863E-8
      // 8d80: dastore
      // 8d81: dup
      // 8d82: sipush 568
      // 8d85: ldc2_w 4.769006625301079E-8
      // 8d88: dastore
      // 8d89: dup
      // 8d8a: sipush 569
      // 8d8d: ldc2_w 1.0607041998938709E-7
      // 8d90: dastore
      // 8d91: dup
      // 8d92: sipush 570
      // 8d95: ldc2_w -8.054981263802322E-8
      // 8d98: dastore
      // 8d99: dup
      // 8d9a: sipush 571
      // 8d9d: ldc2_w -3.370929373457322E-8
      // 8da0: dastore
      // 8da1: dup
      // 8da2: sipush 572
      // 8da5: ldc2_w 9.799164177397836E-9
      // 8da8: dastore
      // 8da9: dup
      // 8daa: sipush 573
      // 8dad: ldc2_w 5.160291611526656E-8
      // 8db0: dastore
      // 8db1: dup
      // 8db2: sipush 574
      // 8db5: ldc2_w 9.333090708652975E-8
      // 8db8: dastore
      // 8db9: dup
      // 8dba: sipush 575
      // 8dbd: ldc2_w -1.0180490545927503E-7
      // 8dc0: dastore
      // 8dc1: dup
      // 8dc2: sipush 576
      // 8dc5: ldc2_w -5.533523366931846E-8
      // 8dc8: dastore
      // 8dc9: dup
      // 8dca: sipush 577
      // 8dcd: ldc2_w -4.044932340334176E-9
      // 8dd0: dastore
      // 8dd1: dup
      // 8dd2: sipush 578
      // 8dd5: ldc2_w 5.370131904567218E-8
      // 8dd8: dastore
      // 8dd9: dup
      // 8dda: sipush 579
      // 8ddd: ldc2_w -1.1887814032213867E-7
      // 8de0: dastore
      // 8de1: dup
      // 8de2: sipush 580
      // 8de5: ldc2_w -4.3307634616102625E-8
      // 8de8: dastore
      // 8de9: dup
      // 8dea: sipush 581
      // 8ded: ldc2_w 4.363437558318513E-8
      // 8df0: dastore
      // 8df1: dup
      // 8df2: sipush 582
      // 8df5: ldc2_w -9.482896784430338E-8
      // 8df8: dastore
      // 8df9: dup
      // 8dfa: sipush 583
      // 8dfd: ldc2_w 1.9782818312325887E-8
      // 8e00: dastore
      // 8e01: dup
      // 8e02: sipush 584
      // 8e05: ldc2_w -8.77224935488516E-8
      // 8e08: dastore
      // 8e09: dup
      // 8e0a: sipush 585
      // 8e0d: ldc2_w 6.113879253864931E-8
      // 8e10: dastore
      // 8e11: dup
      // 8e12: sipush 586
      // 8e15: ldc2_w -8.822335132515693E-9
      // 8e18: dastore
      // 8e19: dup
      // 8e1a: sipush 587
      // 8e1d: ldc2_w -5.753754066078771E-8
      // 8e20: dastore
      // 8e21: dup
      // 8e22: sipush 588
      // 8e25: ldc2_w -8.335545536862392E-8
      // 8e28: dastore
      // 8e29: dup
      // 8e2a: sipush 589
      // 8e2d: ldc2_w -8.462309712606694E-8
      // 8e30: dastore
      // 8e31: dup
      // 8e32: sipush 590
      // 8e35: ldc2_w -5.968586877433824E-8
      // 8e38: dastore
      // 8e39: dup
      // 8e3a: sipush 591
      // 8e3d: ldc2_w -6.887556547891059E-9
      // 8e40: dastore
      // 8e41: dup
      // 8e42: sipush 592
      // 8e45: ldc2_w 7.542967150507818E-8
      // 8e48: dastore
      // 8e49: dup
      // 8e4a: sipush 593
      // 8e4d: ldc2_w -4.949331199790077E-8
      // 8e50: dastore
      // 8e51: dup
      // 8e52: sipush 594
      // 8e55: ldc2_w 9.684172421525468E-8
      // 8e58: dastore
      // 8e59: dup
      // 8e5a: sipush 595
      // 8e5d: ldc2_w 3.9260317944365246E-8
      // 8e60: dastore
      // 8e61: dup
      // 8e62: sipush 596
      // 8e65: ldc2_w 1.784536881359796E-8
      // 8e68: dastore
      // 8e69: dup
      // 8e6a: sipush 597
      // 8e6d: ldc2_w 3.426282345243592E-8
      // 8e70: dastore
      // 8e71: dup
      // 8e72: sipush 598
      // 8e75: ldc2_w 9.018025618601154E-8
      // 8e78: dastore
      // 8e79: dup
      // 8e7a: sipush 599
      // 8e7d: ldc2_w -5.1151708476133135E-8
      // 8e80: dastore
      // 8e81: dup
      // 8e82: sipush 600
      // 8e85: ldc2_w 8.877492215808044E-8
      // 8e88: dastore
      // 8e89: dup
      // 8e8a: sipush 601
      // 8e8d: ldc2_w 3.479545684576179E-8
      // 8e90: dastore
      // 8e91: dup
      // 8e92: sipush 602
      // 8e95: ldc2_w 2.7002575714977818E-8
      // 8e98: dastore
      // 8e99: dup
      // 8e9a: sipush 603
      // 8e9d: ldc2_w 6.707201545505014E-8
      // 8ea0: dastore
      // 8ea1: dup
      // 8ea2: sipush 604
      // 8ea5: ldc2_w -8.173742908533777E-8
      // 8ea8: dastore
      // 8ea9: dup
      // 8eaa: sipush 605
      // 8ead: ldc2_w 5.909041310777802E-8
      // 8eb0: dastore
      // 8eb1: dup
      // 8eb2: sipush 606
      // 8eb5: ldc2_w 1.439903710393587E-8
      // 8eb8: dastore
      // 8eb9: dup
      // 8eba: sipush 607
      // 8ebd: ldc2_w 2.4289317341982113E-8
      // 8ec0: dastore
      // 8ec1: dup
      // 8ec2: sipush 608
      // 8ec5: ldc2_w 9.044519282818302E-8
      // 8ec8: dastore
      // 8ec9: dup
      // 8eca: sipush 609
      // 8ecd: ldc2_w -2.3866331257845713E-8
      // 8ed0: dastore
      // 8ed1: dup
      // 8ed2: sipush 610
      // 8ed5: ldc2_w -7.853944465095286E-8
      // 8ed8: dastore
      // 8ed9: dup
      // 8eda: sipush 611
      // 8edd: ldc2_w -7.188526769607005E-8
      // 8ee0: dastore
      // 8ee1: dup
      // 8ee2: sipush 612
      // 8ee5: ldc2_w -2.2132706360079843E-9
      // 8ee8: dastore
      // 8ee9: dup
      // 8eea: sipush 613
      // 8eed: ldc2_w -1.0624985110080394E-7
      // 8ef0: dastore
      // 8ef1: dup
      // 8ef2: sipush 614
      // 8ef5: ldc2_w 9.453598391231829E-8
      // 8ef8: dastore
      // 8ef9: dup
      // 8efa: sipush 615
      // 8efd: ldc2_w -1.134160131581847E-7
      // 8f00: dastore
      // 8f01: dup
      // 8f02: sipush 616
      // 8f05: ldc2_w -1.315295870404327E-8
      // 8f08: dastore
      // 8f09: dup
      // 8f0a: sipush 617
      // 8f0d: ldc2_w -7.981320644583728E-8
      // 8f10: dastore
      // 8f11: dup
      // 8f12: sipush 618
      // 8f15: ldc2_w -7.327771300038971E-8
      // 8f18: dastore
      // 8f19: dup
      // 8f1a: sipush 619
      // 8f1d: ldc2_w 8.155647334672472E-9
      // 8f20: dastore
      // 8f21: dup
      // 8f22: sipush 620
      // 8f25: ldc2_w -7.222791579580787E-8
      // 8f28: dastore
      // 8f29: dup
      // 8f2a: sipush 621
      // 8f2d: ldc2_w -7.430436987497092E-8
      // 8f30: dastore
      // 8f31: dup
      // 8f32: sipush 622
      // 8f35: ldc2_w 3.633404807819848E-9
      // 8f38: dastore
      // 8f39: dup
      // 8f3a: sipush 623
      // 8f3d: ldc2_w -7.512438321498593E-8
      // 8f40: dastore
      // 8f41: dup
      // 8f42: sipush 624
      // 8f45: ldc2_w -7.044869765481105E-8
      // 8f48: dastore
      // 8f49: dup
      // 8f4a: sipush 625
      // 8f4d: ldc2_w 1.9372589859580955E-8
      // 8f50: dastore
      // 8f51: dup
      // 8f52: sipush 626
      // 8f55: ldc2_w -4.2365298585101096E-8
      // 8f58: dastore
      // 8f59: dup
      // 8f5a: sipush 627
      // 8f5d: ldc2_w -1.552830824758035E-8
      // 8f60: dastore
      // 8f61: dup
      // 8f62: sipush 628
      // 8f65: ldc2_w 1.0160071259930585E-7
      // 8f68: dastore
      // 8f69: dup
      // 8f6a: sipush 629
      // 8f6d: ldc2_w 7.232201430620959E-8
      // 8f70: dastore
      // 8f71: dup
      // 8f72: sipush 630
      // 8f75: ldc2_w -1.0164389431039905E-7
      // 8f78: dastore
      // 8f79: dup
      // 8f7a: sipush 631
      // 8f7d: ldc2_w 5.826233477413577E-8
      // 8f80: dastore
      // 8f81: dup
      // 8f82: sipush 632
      // 8f85: ldc2_w 7.6927415825689E-8
      // 8f88: dastore
      // 8f89: dup
      // 8f8a: sipush 633
      // 8f8d: ldc2_w -4.392309439525734E-8
      // 8f90: dastore
      // 8f91: dup
      // 8f92: sipush 634
      // 8f95: ldc2_w -6.414337408955734E-8
      // 8f98: dastore
      // 8f99: dup
      // 8f9a: sipush 635
      // 8f9d: ldc2_w 1.799550702470095E-8
      // 8fa0: dastore
      // 8fa1: dup
      // 8fa2: sipush 636
      // 8fa5: ldc2_w -3.4194410638967946E-8
      // 8fa8: dastore
      // 8fa9: dup
      // 8faa: sipush 637
      // 8fad: ldc2_w 1.9437762419688045E-8
      // 8fb0: dastore
      // 8fb1: dup
      // 8fb2: sipush 638
      // 8fb5: ldc2_w -5.7792549966531335E-8
      // 8fb8: dastore
      // 8fb9: dup
      // 8fba: sipush 639
      // 8fbd: ldc2_w -2.5731071572354522E-8
      // 8fc0: dastore
      // 8fc1: dup
      // 8fc2: sipush 640
      // 8fc5: ldc2_w 1.173595905705643E-7
      // 8fc8: dastore
      // 8fc9: dup
      // 8fca: sipush 641
      // 8fcd: ldc2_w -1.0361863127101014E-7
      // 8fd0: dastore
      // 8fd1: dup
      // 8fd2: sipush 642
      // 8fd5: ldc2_w 2.8330789837569332E-8
      // 8fd8: dastore
      // 8fd9: dup
      // 8fda: sipush 643
      // 8fdd: ldc2_w 3.81131861433539E-8
      // 8fe0: dastore
      // 8fe1: dup
      // 8fe2: sipush 644
      // 8fe5: ldc2_w -7.252724942149532E-8
      // 8fe8: dastore
      // 8fe9: dup
      // 8fea: sipush 645
      // 8fed: ldc2_w -6.342604067787756E-8
      // 8ff0: dastore
      // 8ff1: dup
      // 8ff2: sipush 646
      // 8ff5: ldc2_w 6.716441526213986E-8
      // 8ff8: dastore
      // 8ff9: dup
      // 8ffa: sipush 647
      // 8ffd: ldc2_w 8.257484966196574E-8
      // 9000: dastore
      // 9001: dup
      // 9002: sipush 648
      // 9005: ldc2_w -1.5443717968117592E-8
      // 9008: dastore
      // 9009: dup
      // 900a: sipush 649
      // 900d: ldc2_w 1.3280021798948244E-8
      // 9010: dastore
      // 9011: dup
      // 9012: sipush 650
      // 9015: ldc2_w -6.79180673261558E-8
      // 9018: dastore
      // 9019: dup
      // 901a: sipush 651
      // 901d: ldc2_w -1.8863249269709046E-8
      // 9020: dastore
      // 9021: dup
      // 9022: sipush 652
      // 9025: ldc2_w -7.62162303263991E-8
      // 9028: dastore
      // 9029: dup
      // 902a: sipush 653
      // 902d: ldc2_w 2.011589233663723E-10
      // 9030: dastore
      // 9031: dup
      // 9032: sipush 654
      // 9035: ldc2_w -2.62683511147141E-8
      // 9038: dastore
      // 9039: dup
      // 903a: sipush 655
      // 903d: ldc2_w 8.455684903712996E-8
      // 9040: dastore
      // 9041: dup
      // 9042: sipush 656
      // 9045: ldc2_w 9.602293320384794E-8
      // 9048: dastore
      // 9049: dup
      // 904a: sipush 657
      // 904d: ldc2_w 9.896378545255258E-9
      // 9050: dastore
      // 9051: dup
      // 9052: sipush 658
      // 9055: ldc2_w 6.636396724067746E-8
      // 9058: dastore
      // 9059: dup
      // 905a: sipush 659
      // 905d: ldc2_w 2.8777050870552646E-8
      // 9060: dastore
      // 9061: dup
      // 9062: sipush 660
      // 9065: ldc2_w -1.0109271059094341E-7
      // 9068: dastore
      // 9069: dup
      // 906a: sipush 661
      // 906d: ldc2_w -8.305334708631055E-8
      // 9070: dastore
      // 9071: dup
      // 9072: sipush 662
      // 9075: ldc2_w 8.467026501338835E-8
      // 9078: dastore
      // 9079: dup
      // 907a: sipush 663
      // 907d: ldc2_w -7.29821745001452E-8
      // 9080: dastore
      // 9081: dup
      // 9082: sipush 664
      // 9085: ldc2_w -7.739491336852633E-8
      // 9088: dastore
      // 9089: dup
      // 908a: sipush 665
      // 908d: ldc2_w 7.321238022013781E-8
      // 9090: dastore
      // 9091: dup
      // 9092: sipush 666
      // 9095: ldc2_w -9.621538067089515E-8
      // 9098: dastore
      // 9099: dup
      // 909a: sipush 667
      // 909d: ldc2_w -1.0705722541811197E-7
      // 90a0: dastore
      // 90a1: dup
      // 90a2: sipush 668
      // 90a5: ldc2_w 4.247240125405735E-8
      // 90a8: dastore
      // 90a9: dup
      // 90aa: sipush 669
      // 90ad: ldc2_w 1.1574222007764044E-7
      // 90b0: dastore
      // 90b1: dup
      // 90b2: sipush 670
      // 90b5: ldc2_w 1.145412771487496E-7
      // 90b8: dastore
      // 90b9: dup
      // 90ba: sipush 671
      // 90bd: ldc2_w 4.066036653218687E-8
      // 90c0: dastore
      // 90c1: dup
      // 90c2: sipush 672
      // 90c5: ldc2_w -1.0410796803072171E-7
      // 90c8: dastore
      // 90c9: dup
      // 90ca: sipush 673
      // 90cd: ldc2_w -7.955085231106037E-8
      // 90d0: dastore
      // 90d1: dup
      // 90d2: sipush 674
      // 90d5: ldc2_w 1.1612776191572459E-7
      // 90d8: dastore
      // 90d9: dup
      // 90da: sipush 675
      // 90dd: ldc2_w 7.888519481107568E-9
      // 90e0: dastore
      // 90e1: dup
      // 90e2: sipush 676
      // 90e5: ldc2_w 7.436813814737735E-8
      // 90e8: dastore
      // 90e9: dup
      // 90ea: sipush 677
      // 90ed: ldc2_w 7.894935661289349E-8
      // 90f0: dastore
      // 90f1: dup
      // 90f2: sipush 678
      // 90f5: ldc2_w 2.343525263620692E-8
      // 90f8: dastore
      // 90f9: dup
      // 90fa: sipush 679
      // 90fd: ldc2_w -9.036933434595339E-8
      // 9100: dastore
      // 9101: dup
      // 9102: sipush 680
      // 9105: ldc2_w -2.2239222395888823E-8
      // 9108: dastore
      // 9109: dup
      // 910a: sipush 681
      // 910d: ldc2_w -8.784622656707742E-9
      // 9110: dastore
      // 9111: dup
      // 9112: sipush 682
      // 9115: ldc2_w -4.819540032304379E-8
      // 9118: dastore
      // 9119: dup
      // 911a: sipush 683
      // 911d: ldc2_w 9.975892708522332E-8
      // 9120: dastore
      // 9121: dup
      // 9122: sipush 684
      // 9125: ldc2_w -3.9945124955316294E-8
      // 9128: dastore
      // 9129: dup
      // 912a: sipush 685
      // 912d: ldc2_w 1.1345047468988893E-8
      // 9130: dastore
      // 9131: dup
      // 9132: sipush 686
      // 9135: ldc2_w 1.702808472925844E-8
      // 9138: dastore
      // 9139: dup
      // 913a: sipush 687
      // 913d: ldc2_w -2.10770182066344E-8
      // 9140: dastore
      // 9141: dup
      // 9142: sipush 688
      // 9145: ldc2_w -1.0114948914089626E-7
      // 9148: dastore
      // 9149: dup
      // 914a: sipush 689
      // 914d: ldc2_w 1.70518021921727E-8
      // 9150: dastore
      // 9151: dup
      // 9152: sipush 690
      // 9155: ldc2_w 9.693260855961159E-8
      // 9158: dastore
      // 9159: dup
      // 915a: sipush 691
      // 915d: ldc2_w -9.809953482725758E-8
      // 9160: dastore
      // 9161: dup
      // 9162: sipush 692
      // 9165: ldc2_w -8.937957126662392E-8
      // 9168: dastore
      // 9169: dup
      // 916a: sipush 693
      // 916d: ldc2_w -1.134963954323427E-7
      // 9170: dastore
      // 9171: dup
      // 9172: sipush 694
      // 9175: ldc2_w 6.980004387880031E-8
      // 9178: dastore
      // 9179: dup
      // 917a: sipush 695
      // 917d: ldc2_w -1.4494150014095534E-8
      // 9180: dastore
      // 9181: dup
      // 9182: sipush 696
      // 9185: ldc2_w 1.122932337832262E-7
      // 9188: dastore
      // 9189: dup
      // 918a: sipush 697
      // 918d: ldc2_w -2.483811732227808E-8
      // 9190: dastore
      // 9191: dup
      // 9192: sipush 698
      // 9195: ldc2_w 5.278759515330048E-8
      // 9198: dastore
      // 9199: dup
      // 919a: sipush 699
      // 919d: ldc2_w 1.0859222881334994E-7
      // 91a0: dastore
      // 91a1: dup
      // 91a2: sipush 700
      // 91a5: ldc2_w -9.400056055939758E-8
      // 91a8: dastore
      // 91a9: dup
      // 91aa: sipush 701
      // 91ad: ldc2_w -7.630957994128623E-8
      // 91b0: dastore
      // 91b1: dup
      // 91b2: sipush 702
      // 91b5: ldc2_w -7.490757191850264E-8
      // 91b8: dastore
      // 91b9: dup
      // 91ba: sipush 703
      // 91bd: ldc2_w -8.794689652049879E-8
      // 91c0: dastore
      // 91c1: dup
      // 91c2: sipush 704
      // 91c5: ldc2_w -1.1357810855950775E-7
      // 91c8: dastore
      // 91c9: dup
      // 91ca: sipush 705
      // 91cd: ldc2_w 8.846862323478745E-8
      // 91d0: dastore
      // 91d1: dup
      // 91d2: sipush 706
      // 91d5: ldc2_w 4.32092015744956E-8
      // 91d8: dastore
      // 91d9: dup
      // 91da: sipush 707
      // 91dd: ldc2_w -9.082923009890997E-9
      // 91e0: dastore
      // 91e1: dup
      // 91e2: sipush 708
      // 91e5: ldc2_w -6.655106680680314E-8
      // 91e8: dastore
      // 91e9: dup
      // 91ea: sipush 709
      // 91ed: ldc2_w 1.1108184705020206E-7
      // 91f0: dastore
      // 91f1: dup
      // 91f2: sipush 710
      // 91f5: ldc2_w 4.8838973948592766E-8
      // 91f8: dastore
      // 91f9: dup
      // 91fa: sipush 711
      // 91fd: ldc2_w -1.2998975819628988E-8
      // 9200: dastore
      // 9201: dup
      // 9202: sipush 712
      // 9205: ldc2_w -7.25680516883106E-8
      // 9208: dastore
      // 9209: dup
      // 920a: sipush 713
      // 920d: ldc2_w -1.280024819379844E-7
      // 9210: dastore
      // 9211: dup
      // 9212: sipush 714
      // 9215: ldc2_w -1.7743467191652895E-7
      // 9218: dastore
      // 9219: dup
      // 921a: sipush 715
      // 921d: ldc2_w -2.1899520225809197E-7
      // 9220: dastore
      // 9221: dup
      // 9222: sipush 716
      // 9225: ldc2_w 2.2602433110285232E-7
      // 9228: dastore
      // 9229: dup
      // 922a: sipush 717
      // 922d: ldc2_w 2.0582268590356215E-7
      // 9230: dastore
      // 9231: dup
      // 9232: sipush 718
      // 9235: ldc2_w 1.9911192455808124E-7
      // 9238: dastore
      // 9239: dup
      // 923a: sipush 719
      // 923d: ldc2_w 2.0776878313278689E-7
      // 9240: dastore
      // 9241: dup
      // 9242: sipush 720
      // 9245: ldc2_w 2.3367183133931002E-7
      // 9248: dastore
      // 9249: dup
      // 924a: sipush 721
      // 924d: ldc2_w -1.9813568387704588E-7
      // 9250: dastore
      // 9251: dup
      // 9252: sipush 722
      // 9255: ldc2_w -1.320972037315105E-7
      // 9258: dastore
      // 9259: dup
      // 925a: sipush 723
      // 925d: ldc2_w -4.316580502355056E-8
      // 9260: dastore
      // 9261: dup
      // 9262: sipush 724
      // 9265: ldc2_w 7.054443447243064E-8
      // 9268: dastore
      // 9269: dup
      // 926a: sipush 725
      // 926d: ldc2_w 2.109212796025238E-7
      // 9270: dastore
      // 9271: dup
      // 9272: sipush 726
      // 9275: ldc2_w -9.698281856949837E-8
      // 9278: dastore
      // 9279: dup
      // 927a: sipush 727
      // 927d: ldc2_w 1.0239791185239086E-7
      // 9280: dastore
      // 9281: dup
      // 9282: sipush 728
      // 9285: ldc2_w -1.4271754202157014E-7
      // 9288: dastore
      // 9289: dup
      // 928a: sipush 729
      // 928d: ldc2_w 1.232402895636637E-7
      // 9290: dastore
      // 9291: dup
      // 9292: sipush 730
      // 9295: ldc2_w -5.150590480969644E-8
      // 9298: dastore
      // 9299: dup
      // 929a: sipush 731
      // 929d: ldc2_w -1.882201085012735E-7
      // 92a0: dastore
      // 92a1: dup
      // 92a2: sipush 732
      // 92a5: ldc2_w 1.918355503889933E-7
      // 92a8: dastore
      // 92a9: dup
      // 92aa: sipush 733
      // 92ad: ldc2_w 1.368893262241355E-7
      // 92b0: dastore
      // 92b1: dup
      // 92b2: sipush 734
      // 92b5: ldc2_w 1.256828068633383E-7
      // 92b8: dastore
      // 92b9: dup
      // 92ba: sipush 735
      // 92bd: ldc2_w 1.601222826656464E-7
      // 92c0: dastore
      // 92c1: dup
      // 92c2: sipush 736
      // 92c5: ldc2_w -2.3472125169205568E-7
      // 92c8: dastore
      // 92c9: dup
      // 92ca: sipush 737
      // 92cd: ldc2_w -1.032634625827871E-7
      // 92d0: dastore
      // 92d1: dup
      // 92d2: sipush 738
      // 92d5: ldc2_w 7.957037517331382E-8
      // 92d8: dastore
      // 92d9: dup
      // 92da: sipush 739
      // 92dd: ldc2_w -1.6114314525832115E-7
      // 92e0: dastore
      // 92e1: dup
      // 92e2: sipush 740
      // 92e5: ldc2_w 1.3018591370778052E-7
      // 92e8: dastore
      // 92e9: dup
      // 92ea: sipush 741
      // 92ed: ldc2_w 1.8007284821359149E-9
      // 92f0: dastore
      // 92f1: dup
      // 92f2: sipush 742
      // 92f5: ldc2_w -6.75421764491544E-8
      // 92f8: dastore
      // 92f9: dup
      // 92fa: sipush 743
      // 92fd: ldc2_w -7.592155950645605E-8
      // 9300: dastore
      // 9301: dup
      // 9302: sipush 744
      // 9305: ldc2_w -2.1414301981236817E-8
      // 9308: dastore
      // 9309: dup
      // 930a: sipush 745
      // 930d: ldc2_w 9.79045937979623E-8
      // 9310: dastore
      // 9311: dup
      // 9312: sipush 746
      // 9315: ldc2_w -1.9287515190177685E-7
      // 9318: dastore
      // 9319: dup
      // 931a: sipush 747
      // 931d: ldc2_w 6.184953843236509E-8
      // 9320: dastore
      // 9321: dup
      // 9322: sipush 748
      // 9325: ldc2_w -8.966500602352001E-8
      // 9328: dastore
      // 9329: dup
      // 932a: sipush 749
      // 932d: ldc2_w -1.686490951669855E-7
      // 9330: dastore
      // 9331: dup
      // 9332: sipush 750
      // 9335: ldc2_w -1.7316830893872364E-7
      // 9338: dastore
      // 9339: dup
      // 933a: sipush 751
      // 933d: ldc2_w -1.0128633727463388E-7
      // 9340: dastore
      // 9341: dup
      // 9342: sipush 752
      // 9345: ldc2_w 4.8935021740786486E-8
      // 9348: dastore
      // 9349: dup
      // 934a: sipush 753
      // 934d: ldc2_w -1.9740129448026905E-7
      // 9350: dastore
      // 9351: dup
      // 9352: sipush 754
      // 9355: ldc2_w 1.1532102163380318E-7
      // 9358: dastore
      // 9359: dup
      // 935a: sipush 755
      // 935d: ldc2_w 3.5371542244169364E-8
      // 9360: dastore
      // 9361: dup
      // 9362: sipush 756
      // 9365: ldc2_w 4.153321337726989E-8
      // 9368: dastore
      // 9369: dup
      // 936a: sipush 757
      // 936d: ldc2_w 1.3575372396796738E-7
      // 9370: dastore
      // 9371: dup
      // 9372: sipush 758
      // 9375: ldc2_w -1.5685449228299222E-7
      // 9378: dastore
      // 9379: dup
      // 937a: sipush 759
      // 937d: ldc2_w 1.1933437776279623E-7
      // 9380: dastore
      // 9381: dup
      // 9382: sipush 760
      // 9385: ldc2_w 1.2599421120614435E-8
      // 9388: dastore
      // 9389: dup
      // 938a: sipush 761
      // 938d: ldc2_w 1.7331079674066365E-9
      // 9390: dastore
      // 9391: dup
      // 9392: sipush 762
      // 9395: ldc2_w 8.869266069401045E-8
      // 9398: dastore
      // 9399: dup
      // 939a: sipush 763
      // 939d: ldc2_w -2.013999442282902E-7
      // 93a0: dastore
      // 93a1: dup
      // 93a2: sipush 764
      // 93a5: ldc2_w 8.709065843311144E-8
      // 93a8: dastore
      // 93a9: dup
      // 93aa: sipush 765
      // 93ad: ldc2_w 2.453117120472083E-9
      // 93b0: dastore
      // 93b1: dup
      // 93b2: sipush 766
      // 93b5: ldc2_w 2.3489472779602617E-8
      // 93b8: dastore
      // 93b9: dup
      // 93ba: sipush 767
      // 93bd: ldc2_w 1.5216652792122652E-7
      // 93c0: dastore
      // 93c1: dup
      // 93c2: sipush 768
      // 93c5: ldc2_w -8.638415150333099E-8
      // 93c8: dastore
      // 93c9: dup
      // 93ca: sipush 769
      // 93cd: ldc2_w -2.1335475961524608E-7
      // 93d0: dastore
      // 93d1: dup
      // 93d2: sipush 770
      // 93d5: ldc2_w -2.2677272333821516E-7
      // 93d8: dastore
      // 93d9: dup
      // 93da: sipush 771
      // 93dd: ldc2_w -1.246635423141374E-7
      // 93e0: dastore
      // 93e1: dup
      // 93e2: sipush 772
      // 93e5: ldc2_w 9.494921297991565E-8
      // 93e8: dastore
      // 93e9: dup
      // 93ea: sipush 773
      // 93ed: ldc2_w -4.27932550865546E-8
      // 93f0: dastore
      // 93f1: dup
      // 93f2: sipush 774
      // 93f5: ldc2_w -5.907349480138712E-8
      // 93f8: dastore
      // 93f9: dup
      // 93fa: sipush 775
      // 93fd: ldc2_w 4.809072216941908E-8
      // 9400: dastore
      // 9401: dup
      // 9402: sipush 776
      // 9405: ldc2_w -1.9615359732789476E-7
      // 9408: dastore
      // 9409: dup
      // 940a: sipush 777
      // 940d: ldc2_w 1.6385396676990034E-7
      // 9410: dastore
      // 9411: dup
      // 9412: sipush 778
      // 9415: ldc2_w 1.7642714221524228E-7
      // 9418: dastore
      // 9419: dup
      // 941a: sipush 779
      // 941d: ldc2_w -1.564440844355254E-7
      // 9420: dastore
      // 9421: dup
      // 9422: sipush 780
      // 9425: ldc2_w 1.2090653407564583E-7
      // 9428: dastore
      // 9429: dup
      // 942a: sipush 781
      // 942d: ldc2_w 5.679855838941285E-8
      // 9430: dastore
      // 9431: dup
      // 9432: sipush 782
      // 9435: ldc2_w 1.3006497185242537E-7
      // 9438: dastore
      // 9439: dup
      // 943a: sipush 783
      // 943d: ldc2_w -1.341336085949317E-7
      // 9440: dastore
      // 9441: dup
      // 9442: sipush 784
      // 9445: ldc2_w 2.1987686050231372E-7
      // 9448: dastore
      // 9449: dup
      // 944a: sipush 785
      // 944d: ldc2_w -2.3641341460419062E-7
      // 9450: dastore
      // 9451: dup
      // 9452: sipush 786
      // 9455: ldc2_w -7.048932272279454E-8
      // 9458: dastore
      // 9459: dup
      // 945a: sipush 787
      // 945d: ldc2_w -2.3401958604540354E-7
      // 9460: dastore
      // 9461: dup
      // 9462: sipush 788
      // 9465: ldc2_w 2.2867766559333004E-7
      // 9468: dastore
      // 9469: dup
      // 946a: sipush 789
      // 946d: ldc2_w -1.1089952719756529E-7
      // 9470: dastore
      // 9471: dup
      // 9472: sipush 790
      // 9475: ldc2_w 1.7977178878541792E-7
      // 9478: dastore
      // 9479: dup
      // 947a: sipush 791
      // 947d: ldc2_w 1.4903074102418675E-7
      // 9480: dastore
      // 9481: dup
      // 9482: sipush 792
      // 9485: ldc2_w -2.011072593789072E-7
      // 9488: dastore
      // 9489: dup
      // 948a: sipush 793
      // 948d: ldc2_w 8.504948422097802E-8
      // 9490: dastore
      // 9491: dup
      // 9492: sipush 794
      // 9495: ldc2_w 5.5846006716348844E-8
      // 9498: dastore
      // 9499: dup
      // 949a: sipush 795
      // 949d: ldc2_w 1.9014079059505456E-7
      // 94a0: dastore
      // 94a1: dup
      // 94a2: sipush 796
      // 94a5: ldc2_w 1.3119976852347583E-8
      // 94a8: dastore
      // 94a9: dup
      // 94aa: sipush 797
      // 94ad: ldc2_w 3.645999732952202E-9
      // 94b0: dastore
      // 94b1: dup
      // 94b2: sipush 798
      // 94b5: ldc2_w 1.6374611405314333E-7
      // 94b8: dastore
      // 94b9: dup
      // 94ba: sipush 799
      // 94bd: ldc2_w 1.8612397134087598E-8
      // 94c0: dastore
      // 94c1: dup
      // 94c2: sipush 800
      // 94c5: ldc2_w 4.7113225346448296E-8
      // 94c8: dastore
      // 94c9: dup
      // 94ca: sipush 801
      // 94cd: ldc2_w -2.2555535676499395E-7
      // 94d0: dastore
      // 94d1: dup
      // 94d2: sipush 802
      // 94d5: ldc2_w 1.5631615647329739E-7
      // 94d8: dastore
      // 94d9: dup
      // 94da: sipush 803
      // 94dd: ldc2_w -2.3574653182047758E-7
      // 94e0: dastore
      // 94e1: dup
      // 94e2: sipush 804
      // 94e5: ldc2_w 3.08072210937242E-8
      // 94e8: dastore
      // 94e9: dup
      // 94ea: sipush 805
      // 94ed: ldc2_w 4.344259288116142E-9
      // 94f0: dastore
      // 94f1: dup
      // 94f2: sipush 806
      // 94f5: ldc2_w 1.6374489573868447E-7
      // 94f8: dastore
      // 94f9: dup
      // 94fa: sipush 807
      // 94fd: ldc2_w 3.42171232580676E-8
      // 9500: dastore
      // 9501: dup
      // 9502: sipush 808
      // 9505: ldc2_w 9.46452492584643E-8
      // 9508: dastore
      // 9509: dup
      // 950a: sipush 809
      // 950d: ldc2_w -1.297587351085525E-7
      // 9510: dastore
      // 9511: dup
      // 9512: sipush 810
      // 9515: ldc2_w -1.601065201853145E-7
      // 9518: dastore
      // 9519: dup
      // 951a: sipush 811
      // 951d: ldc2_w 5.6550495386976275E-9
      // 9520: dastore
      // 9521: dup
      // 9522: sipush 812
      // 9525: ldc2_w -1.0725602261510391E-7
      // 9528: dastore
      // 9529: dup
      // 952a: sipush 813
      // 952d: ldc2_w -1.9945408945084193E-8
      // 9530: dastore
      // 9531: dup
      // 9532: sipush 814
      // 9535: ldc2_w -2.071910882200156E-7
      // 9538: dastore
      // 9539: dup
      // 953a: sipush 815
      // 953d: ldc2_w -1.900947109027913E-7
      // 9540: dastore
      // 9541: dup
      // 9542: sipush 816
      // 9545: ldc2_w 3.34069282059055E-8
      // 9548: dastore
      // 9549: dup
      // 954a: sipush 817
      // 954d: ldc2_w -1.145810806477298E-8
      // 9550: dastore
      // 9551: dup
      // 9552: sipush 818
      // 9555: ldc2_w 1.5421457732308477E-7
      // 9558: dastore
      // 9559: dup
      // 955a: sipush 819
      // 955d: ldc2_w 5.5657084775121975E-8
      // 9560: dastore
      // 9561: dup
      // 9562: sipush 820
      // 9565: ldc2_w 1.7177785285061278E-7
      // 9568: dastore
      // 9569: dup
      // 956a: sipush 821
      // 956d: ldc2_w 2.7813027425289027E-8
      // 9570: dastore
      // 9571: dup
      // 9572: sipush 822
      // 9575: ldc2_w 1.0267509648109748E-7
      // 9578: dastore
      // 9579: dup
      // 957a: sipush 823
      // 957d: ldc2_w -7.839574072711142E-8
      // 9580: dastore
      // 9581: dup
      // 9582: sipush 824
      // 9585: ldc2_w -3.648293887796095E-8
      // 9588: dastore
      // 9589: dup
      // 958a: sipush 825
      // 958d: ldc2_w 2.3049492079013518E-7
      // 9590: dastore
      // 9591: dup
      // 9592: sipush 826
      // 9595: ldc2_w -2.290530257391564E-7
      // 9598: dastore
      // 9599: dup
      // 959a: sipush 827
      // 959d: ldc2_w 1.747018414872141E-8
      // 95a0: dastore
      // 95a1: dup
      // 95a2: sipush 828
      // 95a5: ldc2_w 1.8477759656842807E-8
      // 95a8: dastore
      // 95a9: dup
      // 95aa: sipush 829
      // 95ad: ldc2_w -2.2394073401050633E-7
      // 95b0: dastore
      // 95b1: dup
      // 95b2: sipush 830
      // 95b5: ldc2_w -2.3085653185818848E-7
      // 95b8: dastore
      // 95b9: dup
      // 95ba: sipush 831
      // 95bd: ldc2_w -1.7598351175286083E-10
      // 95c0: dastore
      // 95c1: dup
      // 95c2: sipush 832
      // 95c5: ldc2_w -6.640551220774385E-9
      // 95c8: dastore
      // 95c9: dup
      // 95ca: sipush 833
      // 95cd: ldc2_w 2.2868466674913266E-7
      // 95d0: dastore
      // 95d1: dup
      // 95d2: sipush 834
      // 95d5: ldc2_w 2.3106230530437902E-7
      // 95d8: dastore
      // 95d9: dup
      // 95da: sipush 835
      // 95dd: ldc2_w 2.594209135294356E-9
      // 95e0: dastore
      // 95e1: dup
      // 95e2: sipush 836
      // 95e5: ldc2_w 2.2221434720602702E-8
      // 95e8: dastore
      // 95e9: dup
      // 95ea: sipush 837
      // 95ed: ldc2_w -1.847872222755186E-7
      // 95f0: dastore
      // 95f1: dup
      // 95f2: sipush 838
      // 95f5: ldc2_w -1.3948659218254467E-7
      // 95f8: dastore
      // 95f9: dup
      // 95fa: sipush 839
      // 95fd: ldc2_w 1.6023339607737848E-7
      // 9600: dastore
      // 9601: dup
      // 9602: sipush 840
      // 9605: ldc2_w -2.3718944120137026E-7
      // 9608: dastore
      // 9609: dup
      // 960a: sipush 841
      // 960d: ldc2_w 1.0087056692827474E-7
      // 9610: dastore
      // 9611: dup
      // 9612: sipush 842
      // 9615: ldc2_w 2.228553660510707E-7
      // 9618: dastore
      // 9619: dup
      // 961a: sipush 843
      // 961d: ldc2_w 1.3088328582956644E-7
      // 9620: dastore
      // 9621: dup
      // 9622: sipush 844
      // 9625: ldc2_w -1.7292527438195104E-7
      // 9628: dastore
      // 9629: dup
      // 962a: sipush 845
      // 962d: ldc2_w -2.0961068531216087E-7
      // 9630: dastore
      // 9631: dup
      // 9632: sipush 846
      // 9635: ldc2_w 2.2951597845188004E-8
      // 9638: dastore
      // 9639: dup
      // 963a: sipush 847
      // 963d: ldc2_w 5.005103745740068E-8
      // 9640: dastore
      // 9641: dup
      // 9642: sipush 848
      // 9645: ldc2_w -1.2618366811281002E-7
      // 9648: dastore
      // 9649: dup
      // 964a: sipush 849
      // 964d: ldc2_w -2.6784582477238417E-8
      // 9650: dastore
      // 9651: dup
      // 9652: sipush 850
      // 9655: ldc2_w -1.2645600379949252E-7
      // 9658: dastore
      // 9659: dup
      // 965a: sipush 851
      // 965d: ldc2_w 5.3774170051560117E-8
      // 9660: dastore
      // 9661: dup
      // 9662: sipush 852
      // 9665: ldc2_w 3.9205810725333715E-8
      // 9668: dastore
      // 9669: dup
      // 966a: sipush 853
      // 966d: ldc2_w -1.6802196396307013E-7
      // 9670: dastore
      // 9671: dup
      // 9672: sipush 854
      // 9675: ldc2_w -8.893078799284047E-8
      // 9678: dastore
      // 9679: dup
      // 967a: sipush 855
      // 967d: ldc2_w -1.9821451970481713E-7
      // 9680: dastore
      // 9681: dup
      // 9682: sipush 856
      // 9685: ldc2_w -1.689060694498032E-8
      // 9688: dastore
      // 9689: dup
      // 968a: sipush 857
      // 968d: ldc2_w -1.9648717830943396E-8
      // 9690: dastore
      // 9691: dup
      // 9692: sipush 858
      // 9695: ldc2_w -2.0433926409457167E-7
      // 9698: dastore
      // 9699: dup
      // 969a: sipush 859
      // 969d: ldc2_w -9.1973399031975E-8
      // 96a0: dastore
      // 96a1: dup
      // 96a2: sipush 860
      // 96a5: ldc2_w -1.5723449006087263E-7
      // 96a8: dastore
      // 96a9: dup
      // 96aa: sipush 861
      // 96ad: ldc2_w 7.887051614592191E-8
      // 96b0: dastore
      // 96b1: dup
      // 96b2: sipush 862
      // 96b5: ldc2_w 1.4166246290402286E-7
      // 96b8: dastore
      // 96b9: dup
      // 96ba: sipush 863
      // 96bd: ldc2_w 3.330146018487787E-8
      // 96c0: dastore
      // 96c1: dup
      // 96c2: sipush 864
      // 96c5: ldc2_w 2.3278688667580978E-7
      // 96c8: dastore
      // 96c9: dup
      // 96ca: sipush 865
      // 96cd: ldc2_w -2.1139124097042925E-7
      // 96d0: dastore
      // 96d1: dup
      // 96d2: sipush 866
      // 96d5: ldc2_w 1.334449995534113E-7
      // 96d8: dastore
      // 96d9: dup
      // 96da: sipush 867
      // 96dd: ldc2_w -1.6104730195920897E-7
      // 96e0: dastore
      // 96e1: dup
      // 96e2: sipush 868
      // 96e5: ldc2_w -1.3902314592614197E-7
      // 96e8: dastore
      // 96e9: dup
      // 96ea: sipush 869
      // 96ed: ldc2_w 2.0169027167169864E-7
      // 96f0: dastore
      // 96f1: dup
      // 96f2: sipush 870
      // 96f5: ldc2_w -9.040643863751471E-8
      // 96f8: dastore
      // 96f9: dup
      // 96fa: sipush 871
      // 96fd: ldc2_w -5.946190852360168E-8
      // 9700: dastore
      // 9701: dup
      // 9702: sipush 872
      // 9705: ldc2_w -1.8013411720005014E-7
      // 9708: dastore
      // 9709: dup
      // 970a: sipush 873
      // 970d: ldc2_w 2.6595401669835947E-8
      // 9710: dastore
      // 9711: dup
      // 9712: sipush 874
      // 9715: ldc2_w 8.607292924069425E-8
      // 9718: dastore
      // 9719: dup
      // 971a: sipush 875
      // 971d: ldc2_w 4.84038176769263E-10
      // 9720: dastore
      // 9721: dup
      // 9722: sipush 876
      // 9725: ldc2_w -2.2798356346688802E-7
      // 9728: dastore
      // 9729: dup
      // 972a: sipush 877
      // 972d: ldc2_w -1.203028719549339E-7
      // 9730: dastore
      // 9731: dup
      // 9732: sipush 878
      // 9735: ldc2_w -1.5111906039270745E-7
      // 9738: dastore
      // 9739: dup
      // 973a: sipush 879
      // 973d: ldc2_w 1.5859915617670956E-7
      // 9740: dastore
      // 9741: dup
      // 9742: sipush 880
      // 9745: ldc2_w -1.426262681506497E-7
      // 9748: dastore
      // 9749: dup
      // 974a: sipush 881
      // 974d: ldc2_w -9.892260062323546E-8
      // 9750: dastore
      // 9751: dup
      // 9752: sipush 882
      // 9755: ldc2_w -1.8492643515928268E-7
      // 9758: dastore
      // 9759: dup
      // 975a: sipush 883
      // 975d: ldc2_w 7.840210076743552E-8
      // 9760: dastore
      // 9761: dup
      // 9762: sipush 884
      // 9765: ldc2_w 2.1643071541578027E-7
      // 9768: dastore
      // 9769: dup
      // 976a: sipush 885
      // 976d: ldc2_w 2.313664294893465E-7
      // 9770: dastore
      // 9771: dup
      // 9772: sipush 886
      // 9775: ldc2_w 1.2541842003811723E-7
      // 9778: dastore
      // 9779: dup
      // 977a: sipush 887
      // 977d: ldc2_w -9.920197743470107E-8
      // 9780: dastore
      // 9781: dup
      // 9782: sipush 888
      // 9785: ldc2_w 3.655589133934081E-8
      // 9788: dastore
      // 9789: dup
      // 978a: sipush 889
      // 978d: ldc2_w 5.807052689551411E-8
      // 9790: dastore
      // 9791: dup
      // 9792: sipush 890
      // 9795: ldc2_w -3.244024724169575E-8
      // 9798: dastore
      // 9799: dup
      // 979a: sipush 891
      // 979d: ldc2_w -2.327564406466327E-7
      // 97a0: dastore
      // 97a1: dup
      // 97a2: sipush 892
      // 97a5: ldc2_w -6.38187356721971E-8
      // 97a8: dastore
      // 97a9: dup
      // 97aa: sipush 893
      // 97ad: ldc2_w -2.3995994000400915E-10
      // 97b0: dastore
      // 97b1: dup
      // 97b2: sipush 894
      // 97b5: ldc2_w -3.9793609609721186E-8
      // 97b8: dastore
      // 97b9: dup
      // 97ba: sipush 895
      // 97bd: ldc2_w -1.802510054588344E-7
      // 97c0: dastore
      // 97c1: dup
      // 97c2: sipush 896
      // 97c5: ldc2_w 5.745586744591196E-8
      // 97c8: dastore
      // 97c9: dup
      // 97ca: sipush 897
      // 97cd: ldc2_w 1.987228872666507E-7
      // 97d0: dastore
      // 97d1: dup
      // 97d2: sipush 898
      // 97d5: ldc2_w -2.3105188606976847E-7
      // 97d8: dastore
      // 97d9: dup
      // 97da: sipush 899
      // 97dd: ldc2_w 2.0088042407239129E-7
      // 97e0: dastore
      // 97e1: dup
      // 97e2: sipush 900
      // 97e5: ldc2_w 6.624793114025702E-8
      // 97e8: dastore
      // 97e9: dup
      // 97ea: sipush 901
      // 97ed: ldc2_w -1.5587043044056635E-7
      // 97f0: dastore
      // 97f1: dup
      // 97f2: sipush 902
      // 97f5: ldc2_w 1.3606464059428694E-8
      // 97f8: dastore
      // 97f9: dup
      // 97fa: sipush 903
      // 97fd: ldc2_w 1.0008761540741556E-7
      // 9800: dastore
      // 9801: dup
      // 9802: sipush 904
      // 9805: ldc2_w 1.058213771597129E-7
      // 9808: dastore
      // 9809: dup
      // 980a: sipush 905
      // 980d: ldc2_w 3.3058299602856804E-8
      // 9810: dastore
      // 9811: dup
      // 9812: sipush 906
      // 9815: ldc2_w -1.1594886810010702E-7
      // 9818: dastore
      // 9819: dup
      // 981a: sipush 907
      // 981d: ldc2_w 1.378919824418909E-7
      // 9820: dastore
      // 9821: dup
      // 9822: sipush 908
      // 9825: ldc2_w -1.5683631181406778E-7
      // 9828: dastore
      // 9829: dup
      // 982a: sipush 909
      // 982d: ldc2_w -4.4200075770425176E-8
      // 9830: dastore
      // 9831: dup
      // 9832: sipush 910
      // 9835: ldc2_w 1.2250985436706623E-9
      // 9838: dastore
      // 9839: dup
      // 983a: sipush 911
      // 983d: ldc2_w -1.8297013058336644E-8
      // 9840: dastore
      // 9841: dup
      // 9842: sipush 912
      // 9845: ldc2_w -1.005004229646318E-7
      // 9848: dastore
      // 9849: dup
      // 984a: sipush 913
      // 984d: ldc2_w 2.337202285991116E-7
      // 9850: dastore
      // 9851: dup
      // 9852: sipush 914
      // 9855: ldc2_w 3.296104292035678E-8
      // 9858: dastore
      // 9859: dup
      // 985a: sipush 915
      // 985d: ldc2_w -2.23668185816307E-7
      // 9860: dastore
      // 9861: dup
      // 9862: sipush 916
      // 9865: ldc2_w -5.7055442971184756E-8
      // 9868: dastore
      // 9869: dup
      // 986a: sipush 917
      // 986d: ldc2_w 5.82391923137467E-8
      // 9870: dastore
      // 9871: dup
      // 9872: sipush 918
      // 9875: ldc2_w 1.244950238958056E-7
      // 9878: dastore
      // 9879: dup
      // 987a: sipush 919
      // 987d: ldc2_w 1.4399358260219398E-7
      // 9880: dastore
      // 9881: dup
      // 9882: sipush 920
      // 9885: ldc2_w 1.1901862840583523E-7
      // 9888: dastore
      // 9889: dup
      // 988a: sipush 921
      // 988d: ldc2_w 5.1856152603337505E-8
      // 9890: dastore
      // 9891: dup
      // 9892: sipush 922
      // 9895: ldc2_w -5.520562000491495E-8
      // 9898: dastore
      // 9899: dup
      // 989a: sipush 923
      // 989d: ldc2_w -1.9987622893254038E-7
      // 98a0: dastore
      // 98a1: dup
      // 98a2: sipush 924
      // 98a5: ldc2_w 9.697418238031897E-8
      // 98a8: dastore
      // 98a9: dup
      // 98aa: sipush 925
      // 98ad: ldc2_w -1.1603376405901542E-7
      // 98b0: dastore
      // 98b1: dup
      // 98b2: sipush 926
      // 98b5: ldc2_w 1.170714288147407E-7
      // 98b8: dastore
      // 98b9: dup
      // 98ba: sipush 927
      // 98bd: ldc2_w -1.550851303094034E-7
      // 98c0: dastore
      // 98c1: dup
      // 98c2: sipush 928
      // 98c5: ldc2_w 2.3472546699189522E-8
      // 98c8: dastore
      // 98c9: dup
      // 98ca: sipush 929
      // 98cd: ldc2_w 1.78211222185955E-7
      // 98d0: dastore
      // 98d1: dup
      // 98d2: sipush 930
      // 98d5: ldc2_w -1.6540009048230807E-7
      // 98d8: dastore
      // 98d9: dup
      // 98da: sipush 931
      // 98dd: ldc2_w -5.137865010872577E-8
      // 98e0: dastore
      // 98e1: dup
      // 98e2: sipush 932
      // 98e5: ldc2_w 4.57490653163866E-8
      // 98e8: dastore
      // 98e9: dup
      // 98ea: sipush 933
      // 98ed: ldc2_w 1.2829599363166098E-7
      // 98f0: dastore
      // 98f1: dup
      // 98f2: sipush 934
      // 98f5: ldc2_w 1.985773325073412E-7
      // 98f8: dastore
      // 98f9: dup
      // 98fa: sipush 935
      // 98fd: ldc2_w -2.1792661654989742E-7
      // 9900: dastore
      // 9901: dup
      // 9902: sipush 936
      // 9905: ldc2_w -1.652218131743459E-7
      // 9908: dastore
      // 9909: dup
      // 990a: sipush 937
      // 990d: ldc2_w -1.178234251477505E-7
      // 9910: dastore
      // 9911: dup
      // 9912: sipush 938
      // 9915: ldc2_w -7.34071933723896E-8
      // 9918: dastore
      // 9919: dup
      // 991a: sipush 939
      // 991d: ldc2_w -2.9646587857612632E-8
      // 9920: dastore
      // 9921: dup
      // 9922: sipush 940
      // 9925: ldc2_w 1.5787194498912167E-8
      // 9928: dastore
      // 9929: dup
      // 992a: sipush 941
      // 992d: ldc2_w 6.52252321321176E-8
      // 9930: dastore
      // 9931: dup
      // 9932: sipush 942
      // 9935: ldc2_w 1.2100088103262734E-7
      // 9938: dastore
      // 9939: dup
      // 993a: sipush 943
      // 993d: ldc2_w 1.8544977697201776E-7
      // 9940: dastore
      // 9941: dup
      // 9942: sipush 944
      // 9945: ldc2_w -2.159273204728711E-7
      // 9948: dastore
      // 9949: dup
      // 994a: sipush 945
      // 994d: ldc2_w -1.2711589287782304E-7
      // 9950: dastore
      // 9951: dup
      // 9952: sipush 946
      // 9955: ldc2_w -2.2610609958205195E-8
      // 9958: dastore
      // 9959: dup
      // 995a: sipush 947
      // 995d: ldc2_w 9.993330547750349E-8
      // 9960: dastore
      // 9961: dup
      // 9962: sipush 948
      // 9965: ldc2_w -2.33974236642384E-7
      // 9968: dastore
      // 9969: dup
      // 996a: sipush 949
      // 996d: ldc2_w -6.830955860192377E-8
      // 9970: dastore
      // 9971: dup
      // 9972: sipush 950
      // 9975: ldc2_w 1.2244183812423448E-7
      // 9978: dastore
      // 9979: dup
      // 997a: sipush 951
      // 997d: ldc2_w -1.3620325027706252E-7
      // 9980: dastore
      // 9981: dup
      // 9982: sipush 952
      // 9985: ldc2_w 1.1178574689680927E-7
      // 9988: dastore
      // 9989: dup
      // 998a: sipush 953
      // 998d: ldc2_w -8.490693031052439E-8
      // 9990: dastore
      // 9991: dup
      // 9992: sipush 954
      // 9995: ldc2_w 2.2975389535985893E-7
      // 9998: dastore
      // 9999: dup
      // 999a: sipush 955
      // 999d: ldc2_w 1.0445707500867073E-7
      // 99a0: dastore
      // 99a1: dup
      // 99a2: sipush 956
      // 99a5: ldc2_w 1.8405243253979117E-8
      // 99a8: dastore
      // 99a9: dup
      // 99aa: sipush 957
      // 99ad: ldc2_w -2.6033812325397097E-8
      // 99b0: dastore
      // 99b1: dup
      // 99b2: sipush 958
      // 99b5: ldc2_w -2.6489990728664908E-8
      // 99b8: dastore
      // 99b9: dup
      // 99ba: sipush 959
      // 99bd: ldc2_w 1.9409124727247465E-8
      // 99c0: dastore
      // 99c1: dup
      // 99c2: sipush 960
      // 99c5: ldc2_w 1.1403826867020365E-7
      // 99c8: dastore
      // 99c9: dup
      // 99ca: sipush 961
      // 99cd: ldc2_w -2.1706266226554237E-7
      // 99d0: dastore
      // 99d1: dup
      // 99d2: sipush 962
      // 99d5: ldc2_w -1.7839974359909697E-8
      // 99d8: dastore
      // 99d9: dup
      // 99da: sipush 963
      // 99dd: ldc2_w 2.3725087624341041E-7
      // 99e0: dastore
      // 99e1: dup
      // 99e2: sipush 964
      // 99e5: ldc2_w 7.37567604176979E-8
      // 99e8: dastore
      // 99e9: dup
      // 99ea: sipush 965
      // 99ed: ldc2_w -2.9098805266958403E-8
      // 99f0: dastore
      // 99f1: dup
      // 99f2: sipush 966
      // 99f5: ldc2_w -6.892713087722722E-8
      // 99f8: dastore
      // 99f9: dup
      // 99fa: sipush 967
      // 99fd: ldc2_w -4.333719263537725E-8
      // 9a00: dastore
      // 9a01: dup
      // 9a02: sipush 968
      // 9a05: ldc2_w 5.006436936098099E-8
      // 9a08: dastore
      // 9a09: dup
      // 9a0a: sipush 969
      // 9a0d: ldc2_w 2.1367325342138113E-7
      // 9a10: dastore
      // 9a11: dup
      // 9a12: sipush 970
      // 9a15: ldc2_w -2.6949659655907758E-8
      // 9a18: dastore
      // 9a19: dup
      // 9a1a: sipush 971
      // 9a1d: ldc2_w -1.9256682968755803E-7
      // 9a20: dastore
      // 9a21: dup
      // 9a22: sipush 972
      // 9a25: ldc2_w 1.960616287777496E-7
      // 9a28: dastore
      // 9a29: dup
      // 9a2a: sipush 973
      // 9a2d: ldc2_w 1.876664741413704E-7
      // 9a30: dastore
      // 9a31: dup
      // 9a32: sipush 974
      // 9a35: ldc2_w -2.1534486893602122E-7
      // 9a38: dastore
      // 9a39: dup
      // 9a3a: sipush 975
      // 9a3d: ldc2_w -5.688830723853217E-8
      // 9a40: dastore
      // 9a41: dup
      // 9a42: sipush 976
      // 9a45: ldc2_w 1.8861113228746644E-7
      // 9a48: dastore
      // 9a49: dup
      // 9a4a: sipush 977
      // 9a4d: ldc2_w 4.6730779443102234E-8
      // 9a50: dastore
      // 9a51: dup
      // 9a52: sipush 978
      // 9a55: ldc2_w -3.275360514112964E-9
      // 9a58: dastore
      // 9a59: dup
      // 9a5a: sipush 979
      // 9a5d: ldc2_w 4.1011920825226876E-8
      // 9a60: dastore
      // 9a61: dup
      // 9a62: sipush 980
      // 9a65: ldc2_w 1.820141955326842E-7
      // 9a68: dastore
      // 9a69: dup
      // 9a6a: sipush 981
      // 9a6d: ldc2_w -5.468175655175594E-8
      // 9a70: dastore
      // 9a71: dup
      // 9a72: sipush 982
      // 9a75: ldc2_w -1.8981247089866317E-7
      // 9a78: dastore
      // 9a79: dup
      // 9a7a: sipush 983
      // 9a7d: ldc2_w -2.209492705846306E-7
      // 9a80: dastore
      // 9a81: dup
      // 9a82: sipush 984
      // 9a85: ldc2_w -1.4566110577298295E-7
      // 9a88: dastore
      // 9a89: dup
      // 9a8a: sipush 985
      // 9a8d: ldc2_w 3.848544860465368E-8
      // 9a90: dastore
      // 9a91: dup
      // 9a92: sipush 986
      // 9a95: ldc2_w -1.429109630340783E-7
      // 9a98: dastore
      // 9a99: dup
      // 9a9a: sipush 987
      // 9a9d: ldc2_w -2.105749999899302E-7
      // 9aa0: dastore
      // 9aa1: dup
      // 9aa2: sipush 988
      // 9aa5: ldc2_w -1.6206609756618993E-7
      // 9aa8: dastore
      // 9aa9: dup
      // 9aaa: sipush 989
      // 9aad: ldc2_w 5.058693461947143E-9
      // 9ab0: dastore
      // 9ab1: dup
      // 9ab2: sipush 990
      // 9ab5: ldc2_w -1.8359244902596882E-7
      // 9ab8: dastore
      // 9ab9: dup
      // 9aba: sipush 991
      // 9abd: ldc2_w 2.2810251664891242E-7
      // 9ac0: dastore
      // 9ac1: dup
      // 9ac2: sipush 992
      // 9ac5: ldc2_w -1.8791776732592608E-7
      // 9ac8: dastore
      // 9ac9: dup
      // 9aca: sipush 993
      // 9acd: ldc2_w 1.3106843166204263E-9
      // 9ad0: dastore
      // 9ad1: dup
      // 9ad2: sipush 994
      // 9ad5: ldc2_w -1.5543153797220025E-7
      // 9ad8: dastore
      // 9ad9: dup
      // 9ada: sipush 995
      // 9add: ldc2_w -1.7884997059081524E-7
      // 9ae0: dastore
      // 9ae1: dup
      // 9ae2: sipush 996
      // 9ae5: ldc2_w -6.648490725635754E-8
      // 9ae8: dastore
      // 9ae9: dup
      // 9aea: sipush 997
      // 9aed: ldc2_w 1.8412576154421806E-7
      // 9af0: dastore
      // 9af1: dup
      // 9af2: sipush 998
      // 9af5: ldc2_w 9.860939269906055E-8
      // 9af8: dastore
      // 9af9: dup
      // 9afa: sipush 999
      // 9afd: ldc2_w 1.5627006743114285E-7
      // 9b00: dastore
      // 9b01: dup
      // 9b02: sipush 1000
      // 9b05: ldc2_w -1.17260039161597E-7
      // 9b08: dastore
      // 9b09: dup
      // 9b0a: sipush 1001
      // 9b0d: ldc2_w 2.3416513526430908E-7
      // 9b10: dastore
      // 9b11: dup
      // 9b12: sipush 1002
      // 9b15: ldc2_w -2.1749172296989992E-7
      // 9b18: dastore
      // 9b19: dup
      // 9b1a: sipush 1003
      // 9b1d: ldc2_w -3.9242560971295217E-8
      // 9b20: dastore
      // 9b21: dup
      // 9b22: sipush 1004
      // 9b25: ldc2_w -1.822826971477839E-7
      // 9b28: dastore
      // 9b29: dup
      // 9b2a: sipush 1005
      // 9b2d: ldc2_w -1.6729355321895212E-7
      // 9b30: dastore
      // 9b31: dup
      // 9b32: sipush 1006
      // 9b35: ldc2_w 8.208715337901827E-9
      // 9b38: dastore
      // 9b39: dup
      // 9b3a: sipush 1007
      // 9b3d: ldc2_w -1.301267783434537E-7
      // 9b40: dastore
      // 9b41: dup
      // 9b42: sipush 1008
      // 9b45: ldc2_w -1.029741755377153E-7
      // 9b48: dastore
      // 9b49: dup
      // 9b4a: sipush 1009
      // 9b4d: ldc2_w 9.215765583599035E-8
      // 9b50: dastore
      // 9b51: dup
      // 9b52: sipush 1010
      // 9b55: ldc2_w -1.907487641016455E-8
      // 9b58: dastore
      // 9b59: dup
      // 9b5a: sipush 1011
      // 9b5d: ldc2_w 4.2661388254716074E-8
      // 9b60: dastore
      // 9b61: dup
      // 9b62: sipush 1012
      // 9b65: ldc2_w -1.9697226735187428E-7
      // 9b68: dastore
      // 9b69: dup
      // 9b6a: sipush 1013
      // 9b6d: ldc2_w 2.1819935527247946E-7
      // 9b70: dastore
      // 9b71: dup
      // 9b72: sipush 1014
      // 9b75: ldc2_w -1.398318929248588E-7
      // 9b78: dastore
      // 9b79: dup
      // 9b7a: sipush 1015
      // 9b7d: ldc2_w 1.6195123407015624E-7
      // 9b80: dastore
      // 9b81: dup
      // 9b82: sipush 1016
      // 9b85: ldc2_w 1.723826394935661E-7
      // 9b88: dastore
      // 9b89: dup
      // 9b8a: sipush 1017
      // 9b8d: ldc2_w -1.0602700638269148E-7
      // 9b90: dastore
      // 9b91: dup
      // 9b92: sipush 1018
      // 9b95: ldc2_w -1.9392742205954563E-7
      // 9b98: dastore
      // 9b99: dup
      // 9b9a: sipush 1019
      // 9b9d: ldc2_w -8.880302882034106E-8
      // 9ba0: dastore
      // 9ba1: dup
      // 9ba2: sipush 1020
      // 9ba5: ldc2_w 2.1186420987133E-7
      // 9ba8: dastore
      // 9ba9: dup
      // 9baa: sipush 1021
      // 9bad: ldc2_w 2.3375763256988976E-7
      // 9bb0: dastore
      // 9bb1: dup
      // 9bb2: sipush 1022
      // 9bb5: ldc2_w -2.0599801342241997E-8
      // 9bb8: dastore
      // 9bb9: dup
      // 9bba: sipush 1023
      // 9bbd: ldc2_w -7.184550924856607E-8
      // 9bc0: dastore
      // 9bc1: dup
      // 9bc2: sipush 1024
      // 9bc5: ldc2_w 8.254840070367875E-8
      // 9bc8: dastore
      // 9bc9: putstatic org/apache/commons/math3/util/FastMathLiteralArrays.EXP_FRAC_B [D
      // 9bcc: sipush 1024
      // 9bcf: anewarray 4
      // 9bd2: dup
      // 9bd3: bipush 0
      // 9bd4: bipush 2
      // 9bd5: newarray 7
      // 9bd7: dup
      // 9bd8: bipush 0
      // 9bd9: dconst_0
      // 9bda: dastore
      // 9bdb: dup
      // 9bdc: bipush 1
      // 9bdd: dconst_0
      // 9bde: dastore
      // 9bdf: aastore
      // 9be0: dup
      // 9be1: bipush 1
      // 9be2: bipush 2
      // 9be3: newarray 7
      // 9be5: dup
      // 9be6: bipush 0
      // 9be7: ldc2_w 9.760860120877624E-4
      // 9bea: dastore
      // 9beb: dup
      // 9bec: bipush 1
      // 9bed: ldc2_w -3.903230345984362E-11
      // 9bf0: dastore
      // 9bf1: aastore
      // 9bf2: dup
      // 9bf3: bipush 2
      // 9bf4: bipush 2
      // 9bf5: newarray 7
      // 9bf7: dup
      // 9bf8: bipush 0
      // 9bf9: ldc2_w 0.0019512202125042677
      // 9bfc: dastore
      // 9bfd: dup
      // 9bfe: bipush 1
      // 9bff: ldc2_w -8.124251825289188E-11
      // 9c02: dastore
      // 9c03: aastore
      // 9c04: dup
      // 9c05: bipush 3
      // 9c06: bipush 2
      // 9c07: newarray 7
      // 9c09: dup
      // 9c0a: bipush 0
      // 9c0b: ldc2_w 0.0029254043474793434
      // 9c0e: dastore
      // 9c0f: dup
      // 9c10: bipush 1
      // 9c11: ldc2_w -1.8374207360194882E-11
      // 9c14: dastore
      // 9c15: aastore
      // 9c16: dup
      // 9c17: bipush 4
      // 9c18: bipush 2
      // 9c19: newarray 7
      // 9c1b: dup
      // 9c1c: bipush 0
      // 9c1d: ldc2_w 0.0038986406289041042
      // 9c20: dastore
      // 9c21: dup
      // 9c22: bipush 1
      // 9c23: ldc2_w -2.1324678121885073E-10
      // 9c26: dastore
      // 9c27: aastore
      // 9c28: dup
      // 9c29: bipush 5
      // 9c2a: bipush 2
      // 9c2b: newarray 7
      // 9c2d: dup
      // 9c2e: bipush 0
      // 9c2f: ldc2_w 0.004870930686593056
      // 9c32: dastore
      // 9c33: dup
      // 9c34: bipush 1
      // 9c35: ldc2_w -4.5199654318611534E-10
      // 9c38: dastore
      // 9c39: aastore
      // 9c3a: dup
      // 9c3b: bipush 6
      // 9c3d: bipush 2
      // 9c3e: newarray 7
      // 9c40: dup
      // 9c41: bipush 0
      // 9c42: ldc2_w 0.00584227591753006
      // 9c45: dastore
      // 9c46: dup
      // 9c47: bipush 1
      // 9c48: ldc2_w -2.933016992001806E-10
      // 9c4b: dastore
      // 9c4c: aastore
      // 9c4d: dup
      // 9c4e: bipush 7
      // 9c50: bipush 2
      // 9c51: newarray 7
      // 9c53: dup
      // 9c54: bipush 0
      // 9c55: ldc2_w 0.006812678650021553
      // 9c58: dastore
      // 9c59: dup
      // 9c5a: bipush 1
      // 9c5b: ldc2_w -2.325147219074669E-10
      // 9c5e: dastore
      // 9c5f: aastore
      // 9c60: dup
      // 9c61: bipush 8
      // 9c63: bipush 2
      // 9c64: newarray 7
      // 9c66: dup
      // 9c67: bipush 0
      // 9c68: ldc2_w 0.007782140746712685
      // 9c6b: dastore
      // 9c6c: dup
      // 9c6d: bipush 1
      // 9c6e: ldc2_w -3.046577356838847E-10
      // 9c71: dastore
      // 9c72: aastore
      // 9c73: dup
      // 9c74: bipush 9
      // 9c76: bipush 2
      // 9c77: newarray 7
      // 9c79: dup
      // 9c7a: bipush 0
      // 9c7b: ldc2_w 0.008750664070248604
      // 9c7e: dastore
      // 9c7f: dup
      // 9c80: bipush 1
      // 9c81: ldc2_w -5.500631513861575E-10
      // 9c84: dastore
      // 9c85: aastore
      // 9c86: dup
      // 9c87: bipush 10
      // 9c89: bipush 2
      // 9c8a: newarray 7
      // 9c8c: dup
      // 9c8d: bipush 0
      // 9c8e: ldc2_w 0.00971824862062931
      // 9c91: dastore
      // 9c92: dup
      // 9c93: bipush 1
      // 9c94: ldc2_w 8.48292035519895E-10
      // 9c97: dastore
      // 9c98: aastore
      // 9c99: dup
      // 9c9a: bipush 11
      // 9c9c: bipush 2
      // 9c9d: newarray 7
      // 9c9f: dup
      // 9ca0: bipush 0
      // 9ca1: ldc2_w 0.010684899985790253
      // 9ca4: dastore
      // 9ca5: dup
      // 9ca6: bipush 1
      // 9ca7: ldc2_w 1.1422610134013436E-10
      // 9caa: dastore
      // 9cab: aastore
      // 9cac: dup
      // 9cad: bipush 12
      // 9caf: bipush 2
      // 9cb0: newarray 7
      // 9cb2: dup
      // 9cb3: bipush 0
      // 9cb4: ldc2_w 0.01165061630308628
      // 9cb7: dastore
      // 9cb8: dup
      // 9cb9: bipush 1
      // 9cba: ldc2_w 9.168889933128375E-10
      // 9cbd: dastore
      // 9cbe: aastore
      // 9cbf: dup
      // 9cc0: bipush 13
      // 9cc2: bipush 2
      // 9cc3: newarray 7
      // 9cc5: dup
      // 9cc6: bipush 0
      // 9cc7: ldc2_w 0.012615403160452843
      // 9cca: dastore
      // 9ccb: dup
      // 9ccc: bipush 1
      // 9ccd: ldc2_w -5.303786078838E-10
      // 9cd0: dastore
      // 9cd1: aastore
      // 9cd2: dup
      // 9cd3: bipush 14
      // 9cd5: bipush 2
      // 9cd6: newarray 7
      // 9cd8: dup
      // 9cd9: bipush 0
      // 9cda: ldc2_w 0.013579258695244789
      // 9cdd: dastore
      // 9cde: dup
      // 9cdf: bipush 1
      // 9ce0: ldc2_w -5.688639355498786E-10
      // 9ce3: dastore
      // 9ce4: aastore
      // 9ce5: dup
      // 9ce6: bipush 15
      // 9ce8: bipush 2
      // 9ce9: newarray 7
      // 9ceb: dup
      // 9cec: bipush 0
      // 9ced: ldc2_w 0.01454218477010727
      // 9cf0: dastore
      // 9cf1: dup
      // 9cf2: bipush 1
      // 9cf3: ldc2_w 7.296670293275653E-10
      // 9cf6: dastore
      // 9cf7: aastore
      // 9cf8: dup
      // 9cf9: bipush 16
      // 9cfb: bipush 2
      // 9cfc: newarray 7
      // 9cfe: dup
      // 9cff: bipush 0
      // 9d00: ldc2_w 0.015504186972975731
      // 9d03: dastore
      // 9d04: dup
      // 9d05: bipush 1
      // 9d06: ldc2_w -4.370104767451421E-10
      // 9d09: dastore
      // 9d0a: aastore
      // 9d0b: dup
      // 9d0c: bipush 17
      // 9d0e: bipush 2
      // 9d0f: newarray 7
      // 9d11: dup
      // 9d12: bipush 0
      // 9d13: ldc2_w 0.016465261578559875
      // 9d16: dastore
      // 9d17: dup
      // 9d18: bipush 1
      // 9d19: ldc2_w 1.43695591408832E-9
      // 9d1c: dastore
      // 9d1d: aastore
      // 9d1e: dup
      // 9d1f: bipush 18
      // 9d21: bipush 2
      // 9d22: newarray 7
      // 9d24: dup
      // 9d25: bipush 0
      // 9d26: ldc2_w 0.01742541790008545
      // 9d29: dastore
      // 9d2a: dup
      // 9d2b: bipush 1
      // 9d2c: ldc2_w -1.1862263158849434E-9
      // 9d2f: dastore
      // 9d30: aastore
      // 9d31: dup
      // 9d32: bipush 19
      // 9d34: bipush 2
      // 9d35: newarray 7
      // 9d37: dup
      // 9d38: bipush 0
      // 9d39: ldc2_w 0.018384650349617004
      // 9d3c: dastore
      // 9d3d: dup
      // 9d3e: bipush 1
      // 9d3f: ldc2_w -9.482976524690715E-10
      // 9d42: dastore
      // 9d43: aastore
      // 9d44: dup
      // 9d45: bipush 20
      // 9d47: bipush 2
      // 9d48: newarray 7
      // 9d4a: dup
      // 9d4b: bipush 0
      // 9d4c: ldc2_w 0.01934296265244484
      // 9d4f: dastore
      // 9d50: dup
      // 9d51: bipush 1
      // 9d52: ldc2_w 1.9068609515836638E-10
      // 9d55: dastore
      // 9d56: aastore
      // 9d57: dup
      // 9d58: bipush 21
      // 9d5a: bipush 2
      // 9d5b: newarray 7
      // 9d5d: dup
      // 9d5e: bipush 0
      // 9d5f: ldc2_w 0.020300358533859253
      // 9d62: dastore
      // 9d63: dup
      // 9d64: bipush 1
      // 9d65: ldc2_w 2.655990315697216E-10
      // 9d68: dastore
      // 9d69: aastore
      // 9d6a: dup
      // 9d6b: bipush 22
      // 9d6d: bipush 2
      // 9d6e: newarray 7
      // 9d70: dup
      // 9d71: bipush 0
      // 9d72: ldc2_w 0.021256837993860245
      // 9d75: dastore
      // 9d76: dup
      // 9d77: bipush 1
      // 9d78: ldc2_w 1.0315548713040775E-9
      // 9d7b: dastore
      // 9d7c: aastore
      // 9d7d: dup
      // 9d7e: bipush 23
      // 9d80: bipush 2
      // 9d81: newarray 7
      // 9d83: dup
      // 9d84: bipush 0
      // 9d85: ldc2_w 0.022212404757738113
      // 9d88: dastore
      // 9d89: dup
      // 9d8a: bipush 1
      // 9d8b: ldc2_w 5.13345647019085E-10
      // 9d8e: dastore
      // 9d8f: aastore
      // 9d90: dup
      // 9d91: bipush 24
      // 9d93: bipush 2
      // 9d94: newarray 7
      // 9d96: dup
      // 9d97: bipush 0
      // 9d98: ldc2_w 0.02316705882549286
      // 9d9b: dastore
      // 9d9c: dup
      // 9d9d: bipush 1
      // 9d9e: ldc2_w 4.5604151934208014E-10
      // 9da1: dastore
      // 9da2: aastore
      // 9da3: dup
      // 9da4: bipush 25
      // 9da6: bipush 2
      // 9da7: newarray 7
      // 9da9: dup
      // 9daa: bipush 0
      // 9dab: ldc2_w 0.02412080392241478
      // 9dae: dastore
      // 9daf: dup
      // 9db0: bipush 1
      // 9db1: ldc2_w -1.1255706987475148E-9
      // 9db4: dastore
      // 9db5: aastore
      // 9db6: dup
      // 9db7: bipush 26
      // 9db9: bipush 2
      // 9dba: newarray 7
      // 9dbc: dup
      // 9dbd: bipush 0
      // 9dbe: ldc2_w 0.025073636323213577
      // 9dc1: dastore
      // 9dc2: dup
      // 9dc3: bipush 1
      // 9dc4: ldc2_w 1.2289023836765196E-9
      // 9dc7: dastore
      // 9dc8: aastore
      // 9dc9: dup
      // 9dca: bipush 27
      // 9dcc: bipush 2
      // 9dcd: newarray 7
      // 9dcf: dup
      // 9dd0: bipush 0
      // 9dd1: ldc2_w 0.02602556347846985
      // 9dd4: dastore
      // 9dd5: dup
      // 9dd6: bipush 1
      // 9dd7: ldc2_w 1.7990281828096504E-9
      // 9dda: dastore
      // 9ddb: aastore
      // 9ddc: dup
      // 9ddd: bipush 28
      // 9ddf: bipush 2
      // 9de0: newarray 7
      // 9de2: dup
      // 9de3: bipush 0
      // 9de4: ldc2_w 0.026976589113473892
      // 9de7: dastore
      // 9de8: dup
      // 9de9: bipush 1
      // 9dea: ldc2_w -1.4152718164638451E-9
      // 9ded: dastore
      // 9dee: aastore
      // 9def: dup
      // 9df0: bipush 29
      // 9df2: bipush 2
      // 9df3: newarray 7
      // 9df5: dup
      // 9df6: bipush 0
      // 9df7: ldc2_w 0.02792670577764511
      // 9dfa: dastore
      // 9dfb: dup
      // 9dfc: bipush 1
      // 9dfd: ldc2_w 7.568772963781632E-10
      // 9e00: dastore
      // 9e01: aastore
      // 9e02: dup
      // 9e03: bipush 30
      // 9e05: bipush 2
      // 9e06: newarray 7
      // 9e08: dup
      // 9e09: bipush 0
      // 9e0a: ldc2_w 0.0288759246468544
      // 9e0d: dastore
      // 9e0e: dup
      // 9e0f: bipush 1
      // 9e10: ldc2_w -1.1449998592111558E-9
      // 9e13: dastore
      // 9e14: aastore
      // 9e15: dup
      // 9e16: bipush 31
      // 9e18: bipush 2
      // 9e19: newarray 7
      // 9e1b: dup
      // 9e1c: bipush 0
      // 9e1d: ldc2_w 0.029824241995811462
      // 9e20: dastore
      // 9e21: dup
      // 9e22: bipush 1
      // 9e23: ldc2_w -1.6850976862319495E-9
      // 9e26: dastore
      // 9e27: aastore
      // 9e28: dup
      // 9e29: bipush 32
      // 9e2b: bipush 2
      // 9e2c: newarray 7
      // 9e2e: dup
      // 9e2f: bipush 0
      // 9e30: ldc2_w 0.030771657824516296
      // 9e33: dastore
      // 9e34: dup
      // 9e35: bipush 1
      // 9e36: ldc2_w 8.422373919843096E-10
      // 9e39: dastore
      // 9e3a: aastore
      // 9e3b: dup
      // 9e3c: bipush 33
      // 9e3e: bipush 2
      // 9e3f: newarray 7
      // 9e41: dup
      // 9e42: bipush 0
      // 9e43: ldc2_w 0.0317181795835495
      // 9e46: dastore
      // 9e47: dup
      // 9e48: bipush 1
      // 9e49: ldc2_w 6.872350402175489E-10
      // 9e4c: dastore
      // 9e4d: aastore
      // 9e4e: dup
      // 9e4f: bipush 34
      // 9e51: bipush 2
      // 9e52: newarray 7
      // 9e54: dup
      // 9e55: bipush 0
      // 9e56: ldc2_w 0.03266380727291107
      // 9e59: dastore
      // 9e5a: dup
      // 9e5b: bipush 1
      // 9e5c: ldc2_w -4.541194749189272E-10
      // 9e5f: dastore
      // 9e60: aastore
      // 9e61: dup
      // 9e62: bipush 35
      // 9e64: bipush 2
      // 9e65: newarray 7
      // 9e67: dup
      // 9e68: bipush 0
      // 9e69: ldc2_w 0.03360854089260101
      // 9e6c: dastore
      // 9e6d: dup
      // 9e6e: bipush 1
      // 9e6f: ldc2_w -8.9064764856495E-10
      // 9e72: dastore
      // 9e73: aastore
      // 9e74: dup
      // 9e75: bipush 36
      // 9e77: bipush 2
      // 9e78: newarray 7
      // 9e7a: dup
      // 9e7b: bipush 0
      // 9e7c: ldc2_w 0.034552380442619324
      // 9e7f: dastore
      // 9e80: dup
      // 9e81: bipush 1
      // 9e82: ldc2_w 1.0640404096769032E-9
      // 9e85: dastore
      // 9e86: aastore
      // 9e87: dup
      // 9e88: bipush 37
      // 9e8a: bipush 2
      // 9e8b: newarray 7
      // 9e8d: dup
      // 9e8e: bipush 0
      // 9e8f: ldc2_w 0.0354953333735466
      // 9e92: dastore
      // 9e93: dup
      // 9e94: bipush 1
      // 9e95: ldc2_w -3.5901655945224663E-10
      // 9e98: dastore
      // 9e99: aastore
      // 9e9a: dup
      // 9e9b: bipush 38
      // 9e9d: bipush 2
      // 9e9e: newarray 7
      // 9ea0: dup
      // 9ea1: bipush 0
      // 9ea2: ldc2_w 0.03643739968538284
      // 9ea5: dastore
      // 9ea6: dup
      // 9ea7: bipush 1
      // 9ea8: ldc2_w -3.4829517943661266E-9
      // 9eab: dastore
      // 9eac: aastore
      // 9ead: dup
      // 9eae: bipush 39
      // 9eb0: bipush 2
      // 9eb1: newarray 7
      // 9eb3: dup
      // 9eb4: bipush 0
      // 9eb5: ldc2_w 0.037378571927547455
      // 9eb8: dastore
      // 9eb9: dup
      // 9eba: bipush 1
      // 9ebb: ldc2_w 8.149473794244232E-10
      // 9ebe: dastore
      // 9ebf: aastore
      // 9ec0: dup
      // 9ec1: bipush 40
      // 9ec3: bipush 2
      // 9ec4: newarray 7
      // 9ec6: dup
      // 9ec7: bipush 0
      // 9ec8: ldc2_w 0.03831886500120163
      // 9ecb: dastore
      // 9ecc: dup
      // 9ecd: bipush 1
      // 9ece: ldc2_w -6.990650304449166E-10
      // 9ed1: dastore
      // 9ed2: aastore
      // 9ed3: dup
      // 9ed4: bipush 41
      // 9ed6: bipush 2
      // 9ed7: newarray 7
      // 9ed9: dup
      // 9eda: bipush 0
      // 9edb: ldc2_w 0.03925827145576477
      // 9ede: dastore
      // 9edf: dup
      // 9ee0: bipush 1
      // 9ee1: ldc2_w 1.0883076226453258E-9
      // 9ee4: dastore
      // 9ee5: aastore
      // 9ee6: dup
      // 9ee7: bipush 42
      // 9ee9: bipush 2
      // 9eea: newarray 7
      // 9eec: dup
      // 9eed: bipush 0
      // 9eee: ldc2_w 0.040196798741817474
      // 9ef1: dastore
      // 9ef2: dup
      // 9ef3: bipush 1
      // 9ef4: ldc2_w 3.845192807999274E-10
      // 9ef7: dastore
      // 9ef8: aastore
      // 9ef9: dup
      // 9efa: bipush 43
      // 9efc: bipush 2
      // 9efd: newarray 7
      // 9eff: dup
      // 9f00: bipush 0
      // 9f01: ldc2_w 0.04113444685935974
      // 9f04: dastore
      // 9f05: dup
      // 9f06: bipush 1
      // 9f07: ldc2_w -1.1570594692045927E-9
      // 9f0a: dastore
      // 9f0b: aastore
      // 9f0c: dup
      // 9f0d: bipush 44
      // 9f0f: bipush 2
      // 9f10: newarray 7
      // 9f12: dup
      // 9f13: bipush 0
      // 9f14: ldc2_w 0.04207121580839157
      // 9f17: dastore
      // 9f18: dup
      // 9f19: bipush 1
      // 9f1a: ldc2_w -1.8877045166697178E-9
      // 9f1d: dastore
      // 9f1e: aastore
      // 9f1f: dup
      // 9f20: bipush 45
      // 9f22: bipush 2
      // 9f23: newarray 7
      // 9f25: dup
      // 9f26: bipush 0
      // 9f27: ldc2_w 0.043007105588912964
      // 9f2a: dastore
      // 9f2b: dup
      // 9f2c: bipush 1
      // 9f2d: ldc2_w -1.6332083257987747E-10
      // 9f30: dastore
      // 9f31: aastore
      // 9f32: dup
      // 9f33: bipush 46
      // 9f35: bipush 2
      // 9f36: newarray 7
      // 9f38: dup
      // 9f39: bipush 0
      // 9f3a: ldc2_w 0.04394212365150452
      // 9f3d: dastore
      // 9f3e: dup
      // 9f3f: bipush 1
      // 9f40: ldc2_w -1.7950057534514933E-9
      // 9f43: dastore
      // 9f44: aastore
      // 9f45: dup
      // 9f46: bipush 47
      // 9f48: bipush 2
      // 9f49: newarray 7
      // 9f4b: dup
      // 9f4c: bipush 0
      // 9f4d: ldc2_w 0.04487626254558563
      // 9f50: dastore
      // 9f51: dup
      // 9f52: bipush 1
      // 9f53: ldc2_w 2.302710041648838E-9
      // 9f56: dastore
      // 9f57: aastore
      // 9f58: dup
      // 9f59: bipush 48
      // 9f5b: bipush 2
      // 9f5c: newarray 7
      // 9f5e: dup
      // 9f5f: bipush 0
      // 9f60: ldc2_w 0.045809537172317505
      // 9f63: dastore
      // 9f64: dup
      // 9f65: bipush 1
      // 9f66: ldc2_w -1.1410233017161343E-9
      // 9f69: dastore
      // 9f6a: aastore
      // 9f6b: dup
      // 9f6c: bipush 49
      // 9f6e: bipush 2
      // 9f6f: newarray 7
      // 9f71: dup
      // 9f72: bipush 0
      // 9f73: ldc2_w 0.04674194008111954
      // 9f76: dastore
      // 9f77: dup
      // 9f78: bipush 1
      // 9f79: ldc2_w -3.0498741599744685E-9
      // 9f7c: dastore
      // 9f7d: aastore
      // 9f7e: dup
      // 9f7f: bipush 50
      // 9f81: bipush 2
      // 9f82: newarray 7
      // 9f84: dup
      // 9f85: bipush 0
      // 9f86: ldc2_w 0.04767347127199173
      // 9f89: dastore
      // 9f8a: dup
      // 9f8b: bipush 1
      // 9f8c: ldc2_w -1.8026348269183678E-9
      // 9f8f: dastore
      // 9f90: aastore
      // 9f91: dup
      // 9f92: bipush 51
      // 9f94: bipush 2
      // 9f95: newarray 7
      // 9f97: dup
      // 9f98: bipush 0
      // 9f99: ldc2_w 0.04860413819551468
      // 9f9c: dastore
      // 9f9d: dup
      // 9f9e: bipush 1
      // 9f9f: ldc2_w -3.233204600453039E-9
      // 9fa2: dastore
      // 9fa3: aastore
      // 9fa4: dup
      // 9fa5: bipush 52
      // 9fa7: bipush 2
      // 9fa8: newarray 7
      // 9faa: dup
      // 9fab: bipush 0
      // 9fac: ldc2_w 0.04953393340110779
      // 9faf: dastore
      // 9fb0: dup
      // 9fb1: bipush 1
      // 9fb2: ldc2_w 1.7211688427961583E-9
      // 9fb5: dastore
      // 9fb6: aastore
      // 9fb7: dup
      // 9fb8: bipush 53
      // 9fba: bipush 2
      // 9fbb: newarray 7
      // 9fbd: dup
      // 9fbe: bipush 0
      // 9fbf: ldc2_w 0.05046287178993225
      // 9fc2: dastore
      // 9fc3: dup
      // 9fc4: bipush 1
      // 9fc5: ldc2_w -2.329967807055457E-10
      // 9fc8: dastore
      // 9fc9: aastore
      // 9fca: dup
      // 9fcb: bipush 54
      // 9fcd: bipush 2
      // 9fce: newarray 7
      // 9fd0: dup
      // 9fd1: bipush 0
      // 9fd2: ldc2_w 0.05139094591140747
      // 9fd5: dastore
      // 9fd6: dup
      // 9fd7: bipush 1
      // 9fd8: ldc2_w -4.191810118556531E-11
      // 9fdb: dastore
      // 9fdc: aastore
      // 9fdd: dup
      // 9fde: bipush 55
      // 9fe0: bipush 2
      // 9fe1: newarray 7
      // 9fe3: dup
      // 9fe4: bipush 0
      // 9fe5: ldc2_w 0.052318163216114044
      // 9fe8: dastore
      // 9fe9: dup
      // 9fea: bipush 1
      // 9feb: ldc2_w -3.5574324788328143E-9
      // 9fee: dastore
      // 9fef: aastore
      // 9ff0: dup
      // 9ff1: bipush 56
      // 9ff3: bipush 2
      // 9ff4: newarray 7
      // 9ff6: dup
      // 9ff7: bipush 0
      // 9ff8: ldc2_w 0.053244516253471375
      // 9ffb: dastore
      // 9ffc: dup
      // 9ffd: bipush 1
      // 9ffe: ldc2_w -1.7346590916458485E-9
      // a001: dastore
      // a002: aastore
      // a003: dup
      // a004: bipush 57
      // a006: bipush 2
      // a007: newarray 7
      // a009: dup
      // a00a: bipush 0
      // a00b: ldc2_w 0.05417001247406006
      // a00e: dastore
      // a00f: dup
      // a010: bipush 1
      // a011: ldc2_w -4.343048751383674E-10
      // a014: dastore
      // a015: aastore
      // a016: dup
      // a017: bipush 58
      // a019: bipush 2
      // a01a: newarray 7
      // a01c: dup
      // a01d: bipush 0
      // a01e: ldc2_w 0.055094651877880096
      // a021: dastore
      // a022: dup
      // a023: bipush 1
      // a024: ldc2_w 1.92909364037955E-9
      // a027: dastore
      // a028: aastore
      // a029: dup
      // a02a: bipush 59
      // a02c: bipush 2
      // a02d: newarray 7
      // a02f: dup
      // a030: bipush 0
      // a031: ldc2_w 0.056018441915512085
      // a034: dastore
      // a035: dup
      // a036: bipush 1
      // a037: ldc2_w -5.139745677199588E-10
      // a03a: dastore
      // a03b: aastore
      // a03c: dup
      // a03d: bipush 60
      // a03f: bipush 2
      // a040: newarray 7
      // a042: dup
      // a043: bipush 0
      // a044: ldc2_w 0.05694137513637543
      // a047: dastore
      // a048: dup
      // a049: bipush 1
      // a04a: ldc2_w 1.2637629975129189E-9
      // a04d: dastore
      // a04e: aastore
      // a04f: dup
      // a050: bipush 61
      // a052: bipush 2
      // a053: newarray 7
      // a055: dup
      // a056: bipush 0
      // a057: ldc2_w 0.05786345899105072
      // a05a: dastore
      // a05b: dup
      // a05c: bipush 1
      // a05d: ldc2_w 1.3840561112481119E-9
      // a060: dastore
      // a061: aastore
      // a062: dup
      // a063: bipush 62
      // a065: bipush 2
      // a066: newarray 7
      // a068: dup
      // a069: bipush 0
      // a06a: ldc2_w 0.058784693479537964
      // a06d: dastore
      // a06e: dup
      // a06f: bipush 1
      // a070: ldc2_w 1.414889689612056E-9
      // a073: dastore
      // a074: aastore
      // a075: dup
      // a076: bipush 63
      // a078: bipush 2
      // a079: newarray 7
      // a07b: dup
      // a07c: bipush 0
      // a07d: ldc2_w 0.05970507860183716
      // a080: dastore
      // a081: dup
      // a082: bipush 1
      // a083: ldc2_w 2.9199191907666474E-9
      // a086: dastore
      // a087: aastore
      // a088: dup
      // a089: bipush 64
      // a08b: bipush 2
      // a08c: newarray 7
      // a08e: dup
      // a08f: bipush 0
      // a090: ldc2_w 0.0606246218085289
      // a093: dastore
      // a094: dup
      // a095: bipush 1
      // a096: ldc2_w 7.90594243412116E-12
      // a099: dastore
      // a09a: aastore
      // a09b: dup
      // a09c: bipush 65
      // a09e: bipush 2
      // a09f: newarray 7
      // a0a1: dup
      // a0a2: bipush 0
      // a0a3: ldc2_w 0.06154331564903259
      // a0a6: dastore
      // a0a7: dup
      // a0a8: bipush 1
      // a0a9: ldc2_w 1.6844747839686189E-9
      // a0ac: dastore
      // a0ad: aastore
      // a0ae: dup
      // a0af: bipush 66
      // a0b1: bipush 2
      // a0b2: newarray 7
      // a0b4: dup
      // a0b5: bipush 0
      // a0b6: ldc2_w 0.06246116757392883
      // a0b9: dastore
      // a0ba: dup
      // a0bb: bipush 1
      // a0bc: ldc2_w 2.0498074572151747E-9
      // a0bf: dastore
      // a0c0: aastore
      // a0c1: dup
      // a0c2: bipush 67
      // a0c4: bipush 2
      // a0c5: newarray 7
      // a0c7: dup
      // a0c8: bipush 0
      // a0c9: ldc2_w 0.06337818503379822
      // a0cc: dastore
      // a0cd: dup
      // a0ce: bipush 1
      // a0cf: ldc2_w -4.800180493433863E-9
      // a0d2: dastore
      // a0d3: aastore
      // a0d4: dup
      // a0d5: bipush 68
      // a0d7: bipush 2
      // a0d8: newarray 7
      // a0da: dup
      // a0db: bipush 0
      // a0dc: ldc2_w 0.06429435312747955
      // a0df: dastore
      // a0e0: dup
      // a0e1: bipush 1
      // a0e2: ldc2_w -2.4220822960064277E-9
      // a0e5: dastore
      // a0e6: aastore
      // a0e7: dup
      // a0e8: bipush 69
      // a0ea: bipush 2
      // a0eb: newarray 7
      // a0ed: dup
      // a0ee: bipush 0
      // a0ef: ldc2_w 0.06520968675613403
      // a0f2: dastore
      // a0f3: dup
      // a0f4: bipush 1
      // a0f5: ldc2_w -4.179048566709334E-9
      // a0f8: dastore
      // a0f9: aastore
      // a0fa: dup
      // a0fb: bipush 70
      // a0fd: bipush 2
      // a0fe: newarray 7
      // a100: dup
      // a101: bipush 0
      // a102: ldc2_w 0.06612417101860046
      // a105: dastore
      // a106: dup
      // a107: bipush 1
      // a108: ldc2_w 6.363872957010456E-9
      // a10b: dastore
      // a10c: aastore
      // a10d: dup
      // a10e: bipush 71
      // a110: bipush 2
      // a111: newarray 7
      // a113: dup
      // a114: bipush 0
      // a115: ldc2_w 0.06703783571720123
      // a118: dastore
      // a119: dup
      // a11a: bipush 1
      // a11b: ldc2_w 9.339468680056365E-10
      // a11e: dastore
      // a11f: aastore
      // a120: dup
      // a121: bipush 72
      // a123: bipush 2
      // a124: newarray 7
      // a126: dup
      // a127: bipush 0
      // a128: ldc2_w 0.06795066595077515
      // a12b: dastore
      // a12c: dup
      // a12d: bipush 1
      // a12e: ldc2_w -4.04226739708981E-9
      // a131: dastore
      // a132: aastore
      // a133: dup
      // a134: bipush 73
      // a136: bipush 2
      // a137: newarray 7
      // a139: dup
      // a13a: bipush 0
      // a13b: ldc2_w 0.0688626617193222
      // a13e: dastore
      // a13f: dup
      // a140: bipush 1
      // a141: ldc2_w -7.043545052852817E-9
      // a144: dastore
      // a145: aastore
      // a146: dup
      // a147: bipush 74
      // a149: bipush 2
      // a14a: newarray 7
      // a14c: dup
      // a14d: bipush 0
      // a14e: ldc2_w 0.06977382302284241
      // a151: dastore
      // a152: dup
      // a153: bipush 1
      // a154: ldc2_w -6.552819560439773E-9
      // a157: dastore
      // a158: aastore
      // a159: dup
      // a15a: bipush 75
      // a15c: bipush 2
      // a15d: newarray 7
      // a15f: dup
      // a160: bipush 0
      // a161: ldc2_w 0.07068414986133575
      // a164: dastore
      // a165: dup
      // a166: bipush 1
      // a167: ldc2_w -1.0571674860370546E-9
      // a16a: dastore
      // a16b: aastore
      // a16c: dup
      // a16d: bipush 76
      // a16f: bipush 2
      // a170: newarray 7
      // a172: dup
      // a173: bipush 0
      // a174: ldc2_w 0.07159365713596344
      // a177: dastore
      // a178: dup
      // a179: bipush 1
      // a17a: ldc2_w -3.948954622015801E-9
      // a17d: dastore
      // a17e: aastore
      // a17f: dup
      // a180: bipush 77
      // a182: bipush 2
      // a183: newarray 7
      // a185: dup
      // a186: bipush 0
      // a187: ldc2_w 0.07250232994556427
      // a18a: dastore
      // a18b: dup
      // a18c: bipush 1
      // a18d: ldc2_w 1.1776625988228244E-9
      // a190: dastore
      // a191: aastore
      // a192: dup
      // a193: bipush 78
      // a195: bipush 2
      // a196: newarray 7
      // a198: dup
      // a199: bipush 0
      // a19a: ldc2_w 0.07341018319129944
      // a19d: dastore
      // a19e: dup
      // a19f: bipush 1
      // a1a0: ldc2_w 9.221072639606492E-10
      // a1a3: dastore
      // a1a4: aastore
      // a1a5: dup
      // a1a6: bipush 79
      // a1a8: bipush 2
      // a1a9: newarray 7
      // a1ab: dup
      // a1ac: bipush 0
      // a1ad: ldc2_w 0.07431721687316895
      // a1b0: dastore
      // a1b1: dup
      // a1b2: bipush 1
      // a1b3: ldc2_w -3.219119568928366E-9
      // a1b6: dastore
      // a1b7: aastore
      // a1b8: dup
      // a1b9: bipush 80
      // a1bb: bipush 2
      // a1bc: newarray 7
      // a1be: dup
      // a1bf: bipush 0
      // a1c0: ldc2_w 0.0752234160900116
      // a1c3: dastore
      // a1c4: dup
      // a1c5: bipush 1
      // a1c6: ldc2_w 5.147575929018918E-9
      // a1c9: dastore
      // a1ca: aastore
      // a1cb: dup
      // a1cc: bipush 81
      // a1ce: bipush 2
      // a1cf: newarray 7
      // a1d1: dup
      // a1d2: bipush 0
      // a1d3: ldc2_w 0.07612881064414978
      // a1d6: dastore
      // a1d7: dup
      // a1d8: bipush 1
      // a1d9: ldc2_w -2.291749683541979E-9
      // a1dc: dastore
      // a1dd: aastore
      // a1de: dup
      // a1df: bipush 82
      // a1e1: bipush 2
      // a1e2: newarray 7
      // a1e4: dup
      // a1e5: bipush 0
      // a1e6: ldc2_w 0.07703337073326111
      // a1e9: dastore
      // a1ea: dup
      // a1eb: bipush 1
      // a1ec: ldc2_w 5.749565906124772E-9
      // a1ef: dastore
      // a1f0: aastore
      // a1f1: dup
      // a1f2: bipush 83
      // a1f4: bipush 2
      // a1f5: newarray 7
      // a1f7: dup
      // a1f8: bipush 0
      // a1f9: ldc2_w 0.07793712615966797
      // a1fc: dastore
      // a1fd: dup
      // a1fe: bipush 1
      // a1ff: ldc2_w 9.495158151301779E-10
      // a202: dastore
      // a203: aastore
      // a204: dup
      // a205: bipush 84
      // a207: bipush 2
      // a208: newarray 7
      // a20a: dup
      // a20b: bipush 0
      // a20c: ldc2_w 0.07884006202220917
      // a20f: dastore
      // a210: dup
      // a211: bipush 1
      // a212: ldc2_w -3.144331429489291E-10
      // a215: dastore
      // a216: aastore
      // a217: dup
      // a218: bipush 85
      // a21a: bipush 2
      // a21b: newarray 7
      // a21d: dup
      // a21e: bipush 0
      // a21f: ldc2_w 0.0797421783208847
      // a222: dastore
      // a223: dup
      // a224: bipush 1
      // a225: ldc2_w 3.430029236134205E-9
      // a228: dastore
      // a229: aastore
      // a22a: dup
      // a22b: bipush 86
      // a22d: bipush 2
      // a22e: newarray 7
      // a230: dup
      // a231: bipush 0
      // a232: ldc2_w 0.08064348995685577
      // a235: dastore
      // a236: dup
      // a237: bipush 1
      // a238: ldc2_w -1.2499290483167703E-9
      // a23b: dastore
      // a23c: aastore
      // a23d: dup
      // a23e: bipush 87
      // a240: bipush 2
      // a241: newarray 7
      // a243: dup
      // a244: bipush 0
      // a245: ldc2_w 0.08154398202896118
      // a248: dastore
      // a249: dup
      // a24a: bipush 1
      // a24b: ldc2_w 2.011215719133196E-9
      // a24e: dastore
      // a24f: aastore
      // a250: dup
      // a251: bipush 88
      // a253: bipush 2
      // a254: newarray 7
      // a256: dup
      // a257: bipush 0
      // a258: ldc2_w 0.08244366943836212
      // a25b: dastore
      // a25c: dup
      // a25d: bipush 1
      // a25e: ldc2_w -2.2728753031387152E-10
      // a261: dastore
      // a262: aastore
      // a263: dup
      // a264: bipush 89
      // a266: bipush 2
      // a267: newarray 7
      // a269: dup
      // a26a: bipush 0
      // a26b: ldc2_w 0.0833425521850586
      // a26e: dastore
      // a26f: dup
      // a270: bipush 1
      // a271: ldc2_w -6.508966857277253E-9
      // a274: dastore
      // a275: aastore
      // a276: dup
      // a277: bipush 90
      // a279: bipush 2
      // a27a: newarray 7
      // a27c: dup
      // a27d: bipush 0
      // a27e: ldc2_w 0.0842406153678894
      // a281: dastore
      // a282: dup
      // a283: bipush 1
      // a284: ldc2_w -4.801131671405377E-10
      // a287: dastore
      // a288: aastore
      // a289: dup
      // a28a: bipush 91
      // a28c: bipush 2
      // a28d: newarray 7
      // a28f: dup
      // a290: bipush 0
      // a291: ldc2_w 0.08513787388801575
      // a294: dastore
      // a295: dup
      // a296: bipush 1
      // a297: ldc2_w 4.406750291994231E-9
      // a29a: dastore
      // a29b: aastore
      // a29c: dup
      // a29d: bipush 92
      // a29f: bipush 2
      // a2a0: newarray 7
      // a2a2: dup
      // a2a3: bipush 0
      // a2a4: ldc2_w 0.08603434264659882
      // a2a7: dastore
      // a2a8: dup
      // a2a9: bipush 1
      // a2aa: ldc2_w -5.304795662536171E-9
      // a2ad: dastore
      // a2ae: aastore
      // a2af: dup
      // a2b0: bipush 93
      // a2b2: bipush 2
      // a2b3: newarray 7
      // a2b5: dup
      // a2b6: bipush 0
      // a2b7: ldc2_w 0.08692999184131622
      // a2ba: dastore
      // a2bb: dup
      // a2bc: bipush 1
      // a2bd: ldc2_w 1.6284313912612293E-9
      // a2c0: dastore
      // a2c1: aastore
      // a2c2: dup
      // a2c3: bipush 94
      // a2c5: bipush 2
      // a2c6: newarray 7
      // a2c8: dup
      // a2c9: bipush 0
      // a2ca: ldc2_w 0.08782485127449036
      // a2cd: dastore
      // a2ce: dup
      // a2cf: bipush 1
      // a2d0: ldc2_w -3.158898981674071E-9
      // a2d3: dastore
      // a2d4: aastore
      // a2d5: dup
      // a2d6: bipush 95
      // a2d8: bipush 2
      // a2d9: newarray 7
      // a2db: dup
      // a2dc: bipush 0
      // a2dd: ldc2_w 0.08871890604496002
      // a2e0: dastore
      // a2e1: dup
      // a2e2: bipush 1
      // a2e3: ldc2_w -3.3324878834139977E-9
      // a2e6: dastore
      // a2e7: aastore
      // a2e8: dup
      // a2e9: bipush 96
      // a2eb: bipush 2
      // a2ec: newarray 7
      // a2ee: dup
      // a2ef: bipush 0
      // a2f0: ldc2_w 0.08961215615272522
      // a2f3: dastore
      // a2f4: dup
      // a2f5: bipush 1
      // a2f6: ldc2_w 2.536961912893389E-9
      // a2f9: dastore
      // a2fa: aastore
      // a2fb: dup
      // a2fc: bipush 97
      // a2fe: bipush 2
      // a2ff: newarray 7
      // a301: dup
      // a302: bipush 0
      // a303: ldc2_w 0.09050461649894714
      // a306: dastore
      // a307: dup
      // a308: bipush 1
      // a309: ldc2_w 9.737596728980696E-10
      // a30c: dastore
      // a30d: aastore
      // a30e: dup
      // a30f: bipush 98
      // a311: bipush 2
      // a312: newarray 7
      // a314: dup
      // a315: bipush 0
      // a316: ldc2_w 0.0913962870836258
      // a319: dastore
      // a31a: dup
      // a31b: bipush 1
      // a31c: ldc2_w -6.600437262505396E-9
      // a31f: dastore
      // a320: aastore
      // a321: dup
      // a322: bipush 99
      // a324: bipush 2
      // a325: newarray 7
      // a327: dup
      // a328: bipush 0
      // a329: ldc2_w 0.09228715300559998
      // a32c: dastore
      // a32d: dup
      // a32e: bipush 1
      // a32f: ldc2_w -3.866609889222889E-9
      // a332: dastore
      // a333: aastore
      // a334: dup
      // a335: bipush 100
      // a337: bipush 2
      // a338: newarray 7
      // a33a: dup
      // a33b: bipush 0
      // a33c: ldc2_w 0.09317722916603088
      // a33f: dastore
      // a340: dup
      // a341: bipush 1
      // a342: ldc2_w -4.311847594020281E-9
      // a345: dastore
      // a346: aastore
      // a347: dup
      // a348: bipush 101
      // a34a: bipush 2
      // a34b: newarray 7
      // a34d: dup
      // a34e: bipush 0
      // a34f: ldc2_w 0.09406651556491852
      // a352: dastore
      // a353: dup
      // a354: bipush 1
      // a355: ldc2_w -6.525851105645959E-9
      // a358: dastore
      // a359: aastore
      // a35a: dup
      // a35b: bipush 102
      // a35d: bipush 2
      // a35e: newarray 7
      // a360: dup
      // a361: bipush 0
      // a362: ldc2_w 0.09495499730110168
      // a365: dastore
      // a366: dup
      // a367: bipush 1
      // a368: ldc2_w 5.799080912675435E-9
      // a36b: dastore
      // a36c: aastore
      // a36d: dup
      // a36e: bipush 103
      // a370: bipush 2
      // a371: newarray 7
      // a373: dup
      // a374: bipush 0
      // a375: ldc2_w 0.09584270417690277
      // a378: dastore
      // a379: dup
      // a37a: bipush 1
      // a37b: ldc2_w 4.2634204358490415E-9
      // a37e: dastore
      // a37f: aastore
      // a380: dup
      // a381: bipush 104
      // a383: bipush 2
      // a384: newarray 7
      // a386: dup
      // a387: bipush 0
      // a388: ldc2_w 0.09672962129116058
      // a38b: dastore
      // a38c: dup
      // a38d: bipush 1
      // a38e: ldc2_w 5.167390528799477E-9
      // a391: dastore
      // a392: aastore
      // a393: dup
      // a394: bipush 105
      // a396: bipush 2
      // a397: newarray 7
      // a399: dup
      // a39a: bipush 0
      // a39b: ldc2_w 0.09761576354503632
      // a39e: dastore
      // a39f: dup
      // a3a0: bipush 1
      // a3a1: ldc2_w -4.994827392841906E-9
      // a3a4: dastore
      // a3a5: aastore
      // a3a6: dup
      // a3a7: bipush 106
      // a3a9: bipush 2
      // a3aa: newarray 7
      // a3ac: dup
      // a3ad: bipush 0
      // a3ae: ldc2_w 0.09850110113620758
      // a3b1: dastore
      // a3b2: dup
      // a3b3: bipush 1
      // a3b4: ldc2_w 4.970725577861395E-9
      // a3b7: dastore
      // a3b8: aastore
      // a3b9: dup
      // a3ba: bipush 107
      // a3bc: bipush 2
      // a3bd: newarray 7
      // a3bf: dup
      // a3c0: bipush 0
      // a3c1: ldc2_w 0.09938566386699677
      // a3c4: dastore
      // a3c5: dup
      // a3c6: bipush 1
      // a3c7: ldc2_w 6.6496705953229645E-9
      // a3ca: dastore
      // a3cb: aastore
      // a3cc: dup
      // a3cd: bipush 108
      // a3cf: bipush 2
      // a3d0: newarray 7
      // a3d2: dup
      // a3d3: bipush 0
      // a3d4: ldc2_w 0.10026945173740387
      // a3d7: dastore
      // a3d8: dup
      // a3d9: bipush 1
      // a3da: ldc2_w 1.4262712796792241E-9
      // a3dd: dastore
      // a3de: aastore
      // a3df: dup
      // a3e0: bipush 109
      // a3e2: bipush 2
      // a3e3: newarray 7
      // a3e5: dup
      // a3e6: bipush 0
      // a3e7: ldc2_w 0.1011524498462677
      // a3ea: dastore
      // a3eb: dup
      // a3ec: bipush 1
      // a3ed: ldc2_w 5.5822855204629114E-9
      // a3f0: dastore
      // a3f1: aastore
      // a3f2: dup
      // a3f3: bipush 110
      // a3f5: bipush 2
      // a3f6: newarray 7
      // a3f8: dup
      // a3f9: bipush 0
      // a3fa: ldc2_w 0.10203467309474945
      // a3fd: dastore
      // a3fe: dup
      // a3ff: bipush 1
      // a400: ldc2_w 5.593494835247651E-9
      // a403: dastore
      // a404: aastore
      // a405: dup
      // a406: bipush 111
      // a408: bipush 2
      // a409: newarray 7
      // a40b: dup
      // a40c: bipush 0
      // a40d: ldc2_w 0.10291612148284912
      // a410: dastore
      // a411: dup
      // a412: bipush 1
      // a413: ldc2_w 2.8332008343480686E-9
      // a416: dastore
      // a417: aastore
      // a418: dup
      // a419: bipush 112
      // a41b: bipush 2
      // a41c: newarray 7
      // a41e: dup
      // a41f: bipush 0
      // a420: ldc2_w 0.10379679501056671
      // a423: dastore
      // a424: dup
      // a425: bipush 1
      // a426: ldc2_w -1.3289231465997192E-9
      // a429: dastore
      // a42a: aastore
      // a42b: dup
      // a42c: bipush 113
      // a42e: bipush 2
      // a42f: newarray 7
      // a431: dup
      // a432: bipush 0
      // a433: ldc2_w 0.10467669367790222
      // a436: dastore
      // a437: dup
      // a438: bipush 1
      // a439: ldc2_w -5.526819276639527E-9
      // a43c: dastore
      // a43d: aastore
      // a43e: dup
      // a43f: bipush 114
      // a441: bipush 2
      // a442: newarray 7
      // a444: dup
      // a445: bipush 0
      // a446: ldc2_w 0.10555580258369446
      // a449: dastore
      // a44a: dup
      // a44b: bipush 1
      // a44c: ldc2_w 6.503128678219282E-9
      // a44f: dastore
      // a450: aastore
      // a451: dup
      // a452: bipush 115
      // a454: bipush 2
      // a455: newarray 7
      // a457: dup
      // a458: bipush 0
      // a459: ldc2_w 0.10643415153026581
      // a45c: dastore
      // a45d: dup
      // a45e: bipush 1
      // a45f: ldc2_w 6.317463237641817E-9
      // a462: dastore
      // a463: aastore
      // a464: dup
      // a465: bipush 116
      // a467: bipush 2
      // a468: newarray 7
      // a46a: dup
      // a46b: bipush 0
      // a46c: ldc2_w 0.10731174051761627
      // a46f: dastore
      // a470: dup
      // a471: bipush 1
      // a472: ldc2_w -4.728528221305482E-9
      // a475: dastore
      // a476: aastore
      // a477: dup
      // a478: bipush 117
      // a47a: bipush 2
      // a47b: newarray 7
      // a47d: dup
      // a47e: bipush 0
      // a47f: ldc2_w 0.10818853974342346
      // a482: dastore
      // a483: dup
      // a484: bipush 1
      // a485: ldc2_w 4.519199083083901E-9
      // a488: dastore
      // a489: aastore
      // a48a: dup
      // a48b: bipush 118
      // a48d: bipush 2
      // a48e: newarray 7
      // a490: dup
      // a491: bipush 0
      // a492: ldc2_w 0.10906457901000977
      // a495: dastore
      // a496: dup
      // a497: bipush 1
      // a498: ldc2_w 5.606492666349878E-9
      // a49b: dastore
      // a49c: aastore
      // a49d: dup
      // a49e: bipush 119
      // a4a0: bipush 2
      // a4a1: newarray 7
      // a4a3: dup
      // a4a4: bipush 0
      // a4a5: ldc2_w 0.10993985831737518
      // a4a8: dastore
      // a4a9: dup
      // a4aa: bipush 1
      // a4ab: ldc2_w -1.220176214398581E-10
      // a4ae: dastore
      // a4af: aastore
      // a4b0: dup
      // a4b1: bipush 120
      // a4b3: bipush 2
      // a4b4: newarray 7
      // a4b6: dup
      // a4b7: bipush 0
      // a4b8: ldc2_w 0.11081436276435852
      // a4bb: dastore
      // a4bc: dup
      // a4bd: bipush 1
      // a4be: ldc2_w 3.5759315936869937E-9
      // a4c1: dastore
      // a4c2: aastore
      // a4c3: dup
      // a4c4: bipush 121
      // a4c6: bipush 2
      // a4c7: newarray 7
      // a4c9: dup
      // a4ca: bipush 0
      // a4cb: ldc2_w 0.11168810725212097
      // a4ce: dastore
      // a4cf: dup
      // a4d0: bipush 1
      // a4d1: ldc2_w 3.1367659571899855E-9
      // a4d4: dastore
      // a4d5: aastore
      // a4d6: dup
      // a4d7: bipush 122
      // a4d9: bipush 2
      // a4da: newarray 7
      // a4dc: dup
      // a4dd: bipush 0
      // a4de: ldc2_w 0.11256109178066254
      // a4e1: dastore
      // a4e2: dup
      // a4e3: bipush 1
      // a4e4: ldc2_w -1.0543075713098835E-10
      // a4e7: dastore
      // a4e8: aastore
      // a4e9: dup
      // a4ea: bipush 123
      // a4ec: bipush 2
      // a4ed: newarray 7
      // a4ef: dup
      // a4f0: bipush 0
      // a4f1: ldc2_w 0.11343331634998322
      // a4f4: dastore
      // a4f5: dup
      // a4f6: bipush 1
      // a4f7: ldc2_w -4.820065619207094E-9
      // a4fa: dastore
      // a4fb: aastore
      // a4fc: dup
      // a4fd: bipush 124
      // a4ff: bipush 2
      // a500: newarray 7
      // a502: dup
      // a503: bipush 0
      // a504: ldc2_w 0.11430476605892181
      // a507: dastore
      // a508: dup
      // a509: bipush 1
      // a50a: ldc2_w 5.221136819669415E-9
      // a50d: dastore
      // a50e: aastore
      // a50f: dup
      // a510: bipush 125
      // a512: bipush 2
      // a513: newarray 7
      // a515: dup
      // a516: bipush 0
      // a517: ldc2_w 0.11517547070980072
      // a51a: dastore
      // a51b: dup
      // a51c: bipush 1
      // a51d: ldc2_w 1.5395018670011342E-9
      // a520: dastore
      // a521: aastore
      // a522: dup
      // a523: bipush 126
      // a525: bipush 2
      // a526: newarray 7
      // a528: dup
      // a529: bipush 0
      // a52a: ldc2_w 0.11604541540145874
      // a52d: dastore
      // a52e: dup
      // a52f: bipush 1
      // a530: ldc2_w 3.5638391501880846E-10
      // a533: dastore
      // a534: aastore
      // a535: dup
      // a536: bipush 127
      // a538: bipush 2
      // a539: newarray 7
      // a53b: dup
      // a53c: bipush 0
      // a53d: ldc2_w 0.11691460013389587
      // a540: dastore
      // a541: dup
      // a542: bipush 1
      // a543: ldc2_w 2.9885336757136527E-9
      // a546: dastore
      // a547: aastore
      // a548: dup
      // a549: sipush 128
      // a54c: bipush 2
      // a54d: newarray 7
      // a54f: dup
      // a550: bipush 0
      // a551: ldc2_w 0.11778303980827332
      // a554: dastore
      // a555: dup
      // a556: bipush 1
      // a557: ldc2_w -4.151889860890893E-9
      // a55a: dastore
      // a55b: aastore
      // a55c: dup
      // a55d: sipush 129
      // a560: bipush 2
      // a561: newarray 7
      // a563: dup
      // a564: bipush 0
      // a565: ldc2_w 0.11865071952342987
      // a568: dastore
      // a569: dup
      // a56a: bipush 1
      // a56b: ldc2_w -4.853823938804204E-9
      // a56e: dastore
      // a56f: aastore
      // a570: dup
      // a571: sipush 130
      // a574: bipush 2
      // a575: newarray 7
      // a577: dup
      // a578: bipush 0
      // a579: ldc2_w 0.11951763927936554
      // a57c: dastore
      // a57d: dup
      // a57e: bipush 1
      // a57f: ldc2_w 2.189226237170704E-9
      // a582: dastore
      // a583: aastore
      // a584: dup
      // a585: sipush 131
      // a588: bipush 2
      // a589: newarray 7
      // a58b: dup
      // a58c: bipush 0
      // a58d: ldc2_w 0.12038381397724152
      // a590: dastore
      // a591: dup
      // a592: bipush 1
      // a593: ldc2_w 3.3791993048776982E-9
      // a596: dastore
      // a597: aastore
      // a598: dup
      // a599: sipush 132
      // a59c: bipush 2
      // a59d: newarray 7
      // a59f: dup
      // a5a0: bipush 0
      // a5a1: ldc2_w 0.1212492436170578
      // a5a4: dastore
      // a5a5: dup
      // a5a6: bipush 1
      // a5a7: ldc2_w 1.5811884868243975E-11
      // a5aa: dastore
      // a5ab: aastore
      // a5ac: dup
      // a5ad: sipush 133
      // a5b0: bipush 2
      // a5b1: newarray 7
      // a5b3: dup
      // a5b4: bipush 0
      // a5b5: ldc2_w 0.12211392819881439
      // a5b8: dastore
      // a5b9: dup
      // a5ba: bipush 1
      // a5bb: ldc2_w -6.6045909118908625E-9
      // a5be: dastore
      // a5bf: aastore
      // a5c0: dup
      // a5c1: sipush 134
      // a5c4: bipush 2
      // a5c5: newarray 7
      // a5c7: dup
      // a5c8: bipush 0
      // a5c9: ldc2_w 0.1229778528213501
      // a5cc: dastore
      // a5cd: dup
      // a5ce: bipush 1
      // a5cf: ldc2_w -2.8786263916116364E-10
      // a5d2: dastore
      // a5d3: aastore
      // a5d4: dup
      // a5d5: sipush 135
      // a5d8: bipush 2
      // a5d9: newarray 7
      // a5db: dup
      // a5dc: bipush 0
      // a5dd: ldc2_w 0.12384103238582611
      // a5e0: dastore
      // a5e1: dup
      // a5e2: bipush 1
      // a5e3: ldc2_w 5.354472503748251E-9
      // a5e6: dastore
      // a5e7: aastore
      // a5e8: dup
      // a5e9: sipush 136
      // a5ec: bipush 2
      // a5ed: newarray 7
      // a5ef: dup
      // a5f0: bipush 0
      // a5f1: ldc2_w 0.12470348179340363
      // a5f4: dastore
      // a5f5: dup
      // a5f6: bipush 1
      // a5f7: ldc2_w -3.2924463896248744E-9
      // a5fa: dastore
      // a5fb: aastore
      // a5fc: dup
      // a5fd: sipush 137
      // a600: bipush 2
      // a601: newarray 7
      // a603: dup
      // a604: bipush 0
      // a605: ldc2_w 0.12556517124176025
      // a608: dastore
      // a609: dup
      // a60a: bipush 1
      // a60b: ldc2_w 4.856678149580005E-9
      // a60e: dastore
      // a60f: aastore
      // a610: dup
      // a611: sipush 138
      // a614: bipush 2
      // a615: newarray 7
      // a617: dup
      // a618: bipush 0
      // a619: ldc2_w 0.12642613053321838
      // a61c: dastore
      // a61d: dup
      // a61e: bipush 1
      // a61f: ldc2_w 1.2791850600366742E-9
      // a622: dastore
      // a623: aastore
      // a624: dup
      // a625: sipush 139
      // a628: bipush 2
      // a629: newarray 7
      // a62b: dup
      // a62c: bipush 0
      // a62d: ldc2_w 0.12728634476661682
      // a630: dastore
      // a631: dup
      // a632: bipush 1
      // a633: ldc2_w 2.1525945093362843E-9
      // a636: dastore
      // a637: aastore
      // a638: dup
      // a639: sipush 140
      // a63c: bipush 2
      // a63d: newarray 7
      // a63f: dup
      // a640: bipush 0
      // a641: ldc2_w 0.12814581394195557
      // a644: dastore
      // a645: dup
      // a646: bipush 1
      // a647: ldc2_w 8.749974471767862E-9
      // a64a: dastore
      // a64b: aastore
      // a64c: dup
      // a64d: sipush 141
      // a650: bipush 2
      // a651: newarray 7
      // a653: dup
      // a654: bipush 0
      // a655: ldc2_w 0.129004567861557
      // a658: dastore
      // a659: dup
      // a65a: bipush 1
      // a65b: ldc2_w -7.461209161105275E-9
      // a65e: dastore
      // a65f: aastore
      // a660: dup
      // a661: sipush 142
      // a664: bipush 2
      // a665: newarray 7
      // a667: dup
      // a668: bipush 0
      // a669: ldc2_w 0.12986254692077637
      // a66c: dastore
      // a66d: dup
      // a66e: bipush 1
      // a66f: ldc2_w 1.4390208226263824E-8
      // a672: dastore
      // a673: aastore
      // a674: dup
      // a675: sipush 143
      // a678: bipush 2
      // a679: newarray 7
      // a67b: dup
      // a67c: bipush 0
      // a67d: ldc2_w 0.1307198405265808
      // a680: dastore
      // a681: dup
      // a682: bipush 1
      // a683: ldc2_w -1.3839477920475328E-8
      // a686: dastore
      // a687: aastore
      // a688: dup
      // a689: sipush 144
      // a68c: bipush 2
      // a68d: newarray 7
      // a68f: dup
      // a690: bipush 0
      // a691: ldc2_w 0.13157635927200317
      // a694: dastore
      // a695: dup
      // a696: bipush 1
      // a697: ldc2_w -1.483283901239408E-9
      // a69a: dastore
      // a69b: aastore
      // a69c: dup
      // a69d: sipush 145
      // a6a0: bipush 2
      // a6a1: newarray 7
      // a6a3: dup
      // a6a4: bipush 0
      // a6a5: ldc2_w 0.13243216276168823
      // a6a8: dastore
      // a6a9: dup
      // a6aa: bipush 1
      // a6ab: ldc2_w -6.889072914229094E-9
      // a6ae: dastore
      // a6af: aastore
      // a6b0: dup
      // a6b1: sipush 146
      // a6b4: bipush 2
      // a6b5: newarray 7
      // a6b7: dup
      // a6b8: bipush 0
      // a6b9: ldc2_w 0.1332872211933136
      // a6bc: dastore
      // a6bd: dup
      // a6be: bipush 1
      // a6bf: ldc2_w 9.990351100568362E-10
      // a6c2: dastore
      // a6c3: aastore
      // a6c4: dup
      // a6c5: sipush 147
      // a6c8: bipush 2
      // a6c9: newarray 7
      // a6cb: dup
      // a6cc: bipush 0
      // a6cd: ldc2_w 0.13414156436920166
      // a6d0: dastore
      // a6d1: dup
      // a6d2: bipush 1
      // a6d3: ldc2_w -6.370937412495338E-9
      // a6d6: dastore
      // a6d7: aastore
      // a6d8: dup
      // a6d9: sipush 148
      // a6dc: bipush 2
      // a6dd: newarray 7
      // a6df: dup
      // a6e0: bipush 0
      // a6e1: ldc2_w 0.13499516248703003
      // a6e4: dastore
      // a6e5: dup
      // a6e6: bipush 1
      // a6e7: ldc2_w 2.05047480130511E-9
      // a6ea: dastore
      // a6eb: aastore
      // a6ec: dup
      // a6ed: sipush 149
      // a6f0: bipush 2
      // a6f1: newarray 7
      // a6f3: dup
      // a6f4: bipush 0
      // a6f5: ldc2_w 0.1358480453491211
      // a6f8: dastore
      // a6f9: dup
      // a6fa: bipush 1
      // a6fb: ldc2_w -2.29509872547079E-9
      // a6fe: dastore
      // a6ff: aastore
      // a700: dup
      // a701: sipush 150
      // a704: bipush 2
      // a705: newarray 7
      // a707: dup
      // a708: bipush 0
      // a709: ldc2_w 0.13670018315315247
      // a70c: dastore
      // a70d: dup
      // a70e: bipush 1
      // a70f: ldc2_w 1.16354361977249E-8
      // a712: dastore
      // a713: aastore
      // a714: dup
      // a715: sipush 151
      // a718: bipush 2
      // a719: newarray 7
      // a71b: dup
      // a71c: bipush 0
      // a71d: ldc2_w 0.13755163550376892
      // a720: dastore
      // a721: dup
      // a722: bipush 1
      // a723: ldc2_w -1.452496267904829E-8
      // a726: dastore
      // a727: aastore
      // a728: dup
      // a729: sipush 152
      // a72c: bipush 2
      // a72d: newarray 7
      // a72f: dup
      // a730: bipush 0
      // a731: ldc2_w 0.1384023129940033
      // a734: dastore
      // a735: dup
      // a736: bipush 1
      // a737: ldc2_w 9.865115839786888E-9
      // a73a: dastore
      // a73b: aastore
      // a73c: dup
      // a73d: sipush 153
      // a740: bipush 2
      // a741: newarray 7
      // a743: dup
      // a744: bipush 0
      // a745: ldc2_w 0.13925230503082275
      // a748: dastore
      // a749: dup
      // a74a: bipush 1
      // a74b: ldc2_w -3.369999130712228E-9
      // a74e: dastore
      // a74f: aastore
      // a750: dup
      // a751: sipush 154
      // a754: bipush 2
      // a755: newarray 7
      // a757: dup
      // a758: bipush 0
      // a759: ldc2_w 0.14010155200958252
      // a75c: dastore
      // a75d: dup
      // a75e: bipush 1
      // a75f: ldc2_w 6.602496401651853E-9
      // a762: dastore
      // a763: aastore
      // a764: dup
      // a765: sipush 155
      // a768: bipush 2
      // a769: newarray 7
      // a76b: dup
      // a76c: bipush 0
      // a76d: ldc2_w 0.14095008373260498
      // a770: dastore
      // a771: dup
      // a772: bipush 1
      // a773: ldc2_w 1.1205312852298845E-8
      // a776: dastore
      // a777: aastore
      // a778: dup
      // a779: sipush 156
      // a77c: bipush 2
      // a77d: newarray 7
      // a77f: dup
      // a780: bipush 0
      // a781: ldc2_w 0.14179790019989014
      // a784: dastore
      // a785: dup
      // a786: bipush 1
      // a787: ldc2_w 1.1660367213160203E-8
      // a78a: dastore
      // a78b: aastore
      // a78c: dup
      // a78d: sipush 157
      // a790: bipush 2
      // a791: newarray 7
      // a793: dup
      // a794: bipush 0
      // a795: ldc2_w 0.142645001411438
      // a798: dastore
      // a799: dup
      // a79a: bipush 1
      // a79b: ldc2_w 9.186471222585239E-9
      // a79e: dastore
      // a79f: aastore
      // a7a0: dup
      // a7a1: sipush 158
      // a7a4: bipush 2
      // a7a5: newarray 7
      // a7a7: dup
      // a7a8: bipush 0
      // a7a9: ldc2_w 0.14349138736724854
      // a7ac: dastore
      // a7ad: dup
      // a7ae: bipush 1
      // a7af: ldc2_w 4.999341878263704E-9
      // a7b2: dastore
      // a7b3: aastore
      // a7b4: dup
      // a7b5: sipush 159
      // a7b8: bipush 2
      // a7b9: newarray 7
      // a7bb: dup
      // a7bc: bipush 0
      // a7bd: ldc2_w 0.14433705806732178
      // a7c0: dastore
      // a7c1: dup
      // a7c2: bipush 1
      // a7c3: ldc2_w 3.11611905696257E-10
      // a7c6: dastore
      // a7c7: aastore
      // a7c8: dup
      // a7c9: sipush 160
      // a7cc: bipush 2
      // a7cd: newarray 7
      // a7cf: dup
      // a7d0: bipush 0
      // a7d1: ldc2_w 0.14518201351165771
      // a7d4: dastore
      // a7d5: dup
      // a7d6: bipush 1
      // a7d7: ldc2_w -3.6671598175618173E-9
      // a7da: dastore
      // a7db: aastore
      // a7dc: dup
      // a7dd: sipush 161
      // a7e0: bipush 2
      // a7e1: newarray 7
      // a7e3: dup
      // a7e4: bipush 0
      // a7e5: ldc2_w 0.14602625370025635
      // a7e8: dastore
      // a7e9: dup
      // a7ea: bipush 1
      // a7eb: ldc2_w -5.730477881659618E-9
      // a7ee: dastore
      // a7ef: aastore
      // a7f0: dup
      // a7f1: sipush 162
      // a7f4: bipush 2
      // a7f5: newarray 7
      // a7f7: dup
      // a7f8: bipush 0
      // a7f9: ldc2_w 0.14686977863311768
      // a7fc: dastore
      // a7fd: dup
      // a7fe: bipush 1
      // a7ff: ldc2_w -4.674900007989718E-9
      // a802: dastore
      // a803: aastore
      // a804: dup
      // a805: sipush 163
      // a808: bipush 2
      // a809: newarray 7
      // a80b: dup
      // a80c: bipush 0
      // a80d: ldc2_w 0.1477125883102417
      // a810: dastore
      // a811: dup
      // a812: bipush 1
      // a813: ldc2_w 6.999732437141968E-10
      // a816: dastore
      // a817: aastore
      // a818: dup
      // a819: sipush 164
      // a81c: bipush 2
      // a81d: newarray 7
      // a81f: dup
      // a820: bipush 0
      // a821: ldc2_w 0.14855468273162842
      // a824: dastore
      // a825: dup
      // a826: bipush 1
      // a827: ldc2_w 1.159150872494107E-8
      // a82a: dastore
      // a82b: aastore
      // a82c: dup
      // a82d: sipush 165
      // a830: bipush 2
      // a831: newarray 7
      // a833: dup
      // a834: bipush 0
      // a835: ldc2_w 0.14939609169960022
      // a838: dastore
      // a839: dup
      // a83a: bipush 1
      // a83b: ldc2_w -6.082714828488485E-10
      // a83e: dastore
      // a83f: aastore
      // a840: dup
      // a841: sipush 166
      // a844: bipush 2
      // a845: newarray 7
      // a847: dup
      // a848: bipush 0
      // a849: ldc2_w 0.15023678541183472
      // a84c: dastore
      // a84d: dup
      // a84e: bipush 1
      // a84f: ldc2_w -4.905712741596318E-9
      // a852: dastore
      // a853: aastore
      // a854: dup
      // a855: sipush 167
      // a858: bipush 2
      // a859: newarray 7
      // a85b: dup
      // a85c: bipush 0
      // a85d: ldc2_w 0.1510767638683319
      // a860: dastore
      // a861: dup
      // a862: bipush 1
      // a863: ldc2_w -1.124848988733307E-10
      // a866: dastore
      // a867: aastore
      // a868: dup
      // a869: sipush 168
      // a86c: bipush 2
      // a86d: newarray 7
      // a86f: dup
      // a870: bipush 0
      // a871: ldc2_w 0.15191605687141418
      // a874: dastore
      // a875: dup
      // a876: bipush 1
      // a877: ldc2_w -1.484557220949851E-8
      // a87a: dastore
      // a87b: aastore
      // a87c: dup
      // a87d: sipush 169
      // a880: bipush 2
      // a881: newarray 7
      // a883: dup
      // a884: bipush 0
      // a885: ldc2_w 0.15275460481643677
      // a888: dastore
      // a889: dup
      // a88a: bipush 1
      // a88b: ldc2_w 1.1682026251371384E-8
      // a88e: dastore
      // a88f: aastore
      // a890: dup
      // a891: sipush 170
      // a894: bipush 2
      // a895: newarray 7
      // a897: dup
      // a898: bipush 0
      // a899: ldc2_w 0.15359249711036682
      // a89c: dastore
      // a89d: dup
      // a89e: bipush 1
      // a89f: ldc2_w -8.757272519238786E-9
      // a8a2: dastore
      // a8a3: aastore
      // a8a4: dup
      // a8a5: sipush 171
      // a8a8: bipush 2
      // a8a9: newarray 7
      // a8ab: dup
      // a8ac: bipush 0
      // a8ad: ldc2_w 0.15442964434623718
      // a8b0: dastore
      // a8b1: dup
      // a8b2: bipush 1
      // a8b3: ldc2_w 1.4419920764774415E-8
      // a8b6: dastore
      // a8b7: aastore
      // a8b8: dup
      // a8b9: sipush 172
      // a8bc: bipush 2
      // a8bd: newarray 7
      // a8bf: dup
      // a8c0: bipush 0
      // a8c1: ldc2_w 0.15526613593101501
      // a8c4: dastore
      // a8c5: dup
      // a8c6: bipush 1
      // a8c7: ldc2_w -7.019891063126053E-9
      // a8ca: dastore
      // a8cb: aastore
      // a8cc: dup
      // a8cd: sipush 173
      // a8d0: bipush 2
      // a8d1: newarray 7
      // a8d3: dup
      // a8d4: bipush 0
      // a8d5: ldc2_w 0.15610191226005554
      // a8d8: dastore
      // a8d9: dup
      // a8da: bipush 1
      // a8db: ldc2_w -1.230153548825964E-8
      // a8de: dastore
      // a8df: aastore
      // a8e0: dup
      // a8e1: sipush 174
      // a8e4: bipush 2
      // a8e5: newarray 7
      // a8e7: dup
      // a8e8: bipush 0
      // a8e9: ldc2_w 0.15693697333335876
      // a8ec: dastore
      // a8ed: dup
      // a8ee: bipush 1
      // a8ef: ldc2_w -2.574172005933276E-10
      // a8f2: dastore
      // a8f3: aastore
      // a8f4: dup
      // a8f5: sipush 175
      // a8f8: bipush 2
      // a8f9: newarray 7
      // a8fb: dup
      // a8fc: bipush 0
      // a8fd: ldc2_w 0.15777134895324707
      // a900: dastore
      // a901: dup
      // a902: bipush 1
      // a903: ldc2_w 4.748140799544371E-10
      // a906: dastore
      // a907: aastore
      // a908: dup
      // a909: sipush 176
      // a90c: bipush 2
      // a90d: newarray 7
      // a90f: dup
      // a910: bipush 0
      // a911: ldc2_w 0.15860503911972046
      // a914: dastore
      // a915: dup
      // a916: bipush 1
      // a917: ldc2_w -8.943081874891003E-9
      // a91a: dastore
      // a91b: aastore
      // a91c: dup
      // a91d: sipush 177
      // a920: bipush 2
      // a921: newarray 7
      // a923: dup
      // a924: bipush 0
      // a925: ldc2_w 0.15943801403045654
      // a928: dastore
      // a929: dup
      // a92a: bipush 1
      // a92b: ldc2_w 2.4500739038517657E-9
      // a92e: dastore
      // a92f: aastore
      // a930: dup
      // a931: sipush 178
      // a934: bipush 2
      // a935: newarray 7
      // a937: dup
      // a938: bipush 0
      // a939: ldc2_w 0.1602703034877777
      // a93c: dastore
      // a93d: dup
      // a93e: bipush 1
      // a93f: ldc2_w 6.007922084557054E-9
      // a942: dastore
      // a943: aastore
      // a944: dup
      // a945: sipush 179
      // a948: bipush 2
      // a949: newarray 7
      // a94b: dup
      // a94c: bipush 0
      // a94d: ldc2_w 0.16110190749168396
      // a950: dastore
      // a951: dup
      // a952: bipush 1
      // a953: ldc2_w 2.8835418231126645E-9
      // a956: dastore
      // a957: aastore
      // a958: dup
      // a959: sipush 180
      // a95c: bipush 2
      // a95d: newarray 7
      // a95f: dup
      // a960: bipush 0
      // a961: ldc2_w 0.1619328260421753
      // a964: dastore
      // a965: dup
      // a966: bipush 1
      // a967: ldc2_w -5.772862039728412E-9
      // a96a: dastore
      // a96b: aastore
      // a96c: dup
      // a96d: sipush 181
      // a970: bipush 2
      // a971: newarray 7
      // a973: dup
      // a974: bipush 0
      // a975: ldc2_w 0.16276302933692932
      // a978: dastore
      // a979: dup
      // a97a: bipush 1
      // a97b: ldc2_w 1.0988372954605789E-8
      // a97e: dastore
      // a97f: aastore
      // a980: dup
      // a981: sipush 182
      // a984: bipush 2
      // a985: newarray 7
      // a987: dup
      // a988: bipush 0
      // a989: ldc2_w 0.16359257698059082
      // a98c: dastore
      // a98d: dup
      // a98e: bipush 1
      // a98f: ldc2_w -5.292913162607026E-9
      // a992: dastore
      // a993: aastore
      // a994: dup
      // a995: sipush 183
      // a998: bipush 2
      // a999: newarray 7
      // a99b: dup
      // a99c: bipush 0
      // a99d: ldc2_w 0.16442140936851501
      // a9a0: dastore
      // a9a1: dup
      // a9a2: bipush 1
      // a9a3: ldc2_w 6.12956339275823E-9
      // a9a6: dastore
      // a9a7: aastore
      // a9a8: dup
      // a9a9: sipush 184
      // a9ac: bipush 2
      // a9ad: newarray 7
      // a9af: dup
      // a9b0: bipush 0
      // a9b1: ldc2_w 0.16524958610534668
      // a9b4: dastore
      // a9b5: dup
      // a9b6: bipush 1
      // a9b7: ldc2_w -1.3210039516811888E-8
      // a9ba: dastore
      // a9bb: aastore
      // a9bc: dup
      // a9bd: sipush 185
      // a9c0: bipush 2
      // a9c1: newarray 7
      // a9c3: dup
      // a9c4: bipush 0
      // a9c5: ldc2_w 0.16607704758644104
      // a9c8: dastore
      // a9c9: dup
      // a9ca: bipush 1
      // a9cb: ldc2_w -2.5711014608334873E-9
      // a9ce: dastore
      // a9cf: aastore
      // a9d0: dup
      // a9d1: sipush 186
      // a9d4: bipush 2
      // a9d5: newarray 7
      // a9d7: dup
      // a9d8: bipush 0
      // a9d9: ldc2_w 0.16690382361412048
      // a9dc: dastore
      // a9dd: dup
      // a9de: bipush 1
      // a9df: ldc2_w 9.37721319457112E-9
      // a9e2: dastore
      // a9e3: aastore
      // a9e4: dup
      // a9e5: sipush 187
      // a9e8: bipush 2
      // a9e9: newarray 7
      // a9eb: dup
      // a9ec: bipush 0
      // a9ed: ldc2_w 0.1677299439907074
      // a9f0: dastore
      // a9f1: dup
      // a9f2: bipush 1
      // a9f3: ldc2_w -6.0370682395944045E-9
      // a9f6: dastore
      // a9f7: aastore
      // a9f8: dup
      // a9f9: sipush 188
      // a9fc: bipush 2
      // a9fd: newarray 7
      // a9ff: dup
      // aa00: bipush 0
      // aa01: ldc2_w 0.168555349111557
      // aa04: dastore
      // aa05: dup
      // aa06: bipush 1
      // aa07: ldc2_w 1.1918249660105651E-8
      // aa0a: dastore
      // aa0b: aastore
      // aa0c: dup
      // aa0d: sipush 189
      // aa10: bipush 2
      // aa11: newarray 7
      // aa13: dup
      // aa14: bipush 0
      // aa15: ldc2_w 0.1693800985813141
      // aa18: dastore
      // aa19: dup
      // aa1a: bipush 1
      // aa1b: ldc2_w 4.763282949656017E-9
      // aa1e: dastore
      // aa1f: aastore
      // aa20: dup
      // aa21: sipush 190
      // aa24: bipush 2
      // aa25: newarray 7
      // aa27: dup
      // aa28: bipush 0
      // aa29: ldc2_w 0.17020416259765625
      // aa2c: dastore
      // aa2d: dup
      // aa2e: bipush 1
      // aa2f: ldc2_w 3.4223342273948817E-9
      // aa32: dastore
      // aa33: aastore
      // aa34: dup
      // aa35: sipush 191
      // aa38: bipush 2
      // aa39: newarray 7
      // aa3b: dup
      // aa3c: bipush 0
      // aa3d: ldc2_w 0.1710275411605835
      // aa40: dastore
      // aa41: dup
      // aa42: bipush 1
      // aa43: ldc2_w 9.014612241310916E-9
      // aa46: dastore
      // aa47: aastore
      // aa48: dup
      // aa49: sipush 192
      // aa4c: bipush 2
      // aa4d: newarray 7
      // aa4f: dup
      // aa50: bipush 0
      // aa51: ldc2_w 0.1718502640724182
      // aa54: dastore
      // aa55: dup
      // aa56: bipush 1
      // aa57: ldc2_w -7.145758990550526E-9
      // aa5a: dastore
      // aa5b: aastore
      // aa5c: dup
      // aa5d: sipush 193
      // aa60: bipush 2
      // aa61: newarray 7
      // aa63: dup
      // aa64: bipush 0
      // aa65: ldc2_w 0.172672301530838
      // aa68: dastore
      // aa69: dup
      // aa6a: bipush 1
      // aa6b: ldc2_w -1.4142763934081504E-8
      // aa6e: dastore
      // aa6f: aastore
      // aa70: dup
      // aa71: sipush 194
      // aa74: bipush 2
      // aa75: newarray 7
      // aa77: dup
      // aa78: bipush 0
      // aa79: ldc2_w 0.1734936535358429
      // aa7c: dastore
      // aa7d: dup
      // aa7e: bipush 1
      // aa7f: ldc2_w -1.0865453656579032E-8
      // aa82: dastore
      // aa83: aastore
      // aa84: dup
      // aa85: sipush 195
      // aa88: bipush 2
      // aa89: newarray 7
      // aa8b: dup
      // aa8c: bipush 0
      // aa8d: ldc2_w 0.17431432008743286
      // aa90: dastore
      // aa91: dup
      // aa92: bipush 1
      // aa93: ldc2_w 3.794385569450774E-9
      // aa96: dastore
      // aa97: aastore
      // aa98: dup
      // aa99: sipush 196
      // aa9c: bipush 2
      // aa9d: newarray 7
      // aa9f: dup
      // aaa0: bipush 0
      // aaa1: ldc2_w 0.1751343309879303
      // aaa4: dastore
      // aaa5: dup
      // aaa6: bipush 1
      // aaa7: ldc2_w 1.1399188501627291E-9
      // aaaa: dastore
      // aaab: aastore
      // aaac: dup
      // aaad: sipush 197
      // aab0: bipush 2
      // aab1: newarray 7
      // aab3: dup
      // aab4: bipush 0
      // aab5: ldc2_w 0.17595365643501282
      // aab8: dastore
      // aab9: dup
      // aaba: bipush 1
      // aabb: ldc2_w 1.2076238768270153E-8
      // aabe: dastore
      // aabf: aastore
      // aac0: dup
      // aac1: sipush 198
      // aac4: bipush 2
      // aac5: newarray 7
      // aac7: dup
      // aac8: bipush 0
      // aac9: ldc2_w 0.1767723262310028
      // aacc: dastore
      // aacd: dup
      // aace: bipush 1
      // aacf: ldc2_w 7.901084730502162E-9
      // aad2: dastore
      // aad3: aastore
      // aad4: dup
      // aad5: sipush 199
      // aad8: bipush 2
      // aad9: newarray 7
      // aadb: dup
      // aadc: bipush 0
      // aadd: ldc2_w 0.17759034037590027
      // aae0: dastore
      // aae1: dup
      // aae2: bipush 1
      // aae3: ldc2_w -1.0288181007465474E-8
      // aae6: dastore
      // aae7: aastore
      // aae8: dup
      // aae9: sipush 200
      // aaec: bipush 2
      // aaed: newarray 7
      // aaef: dup
      // aaf0: bipush 0
      // aaf1: ldc2_w 0.1784076690673828
      // aaf4: dastore
      // aaf5: dup
      // aaf6: bipush 1
      // aaf7: ldc2_w -1.15945645153806E-8
      // aafa: dastore
      // aafb: aastore
      // aafc: dup
      // aafd: sipush 201
      // ab00: bipush 2
      // ab01: newarray 7
      // ab03: dup
      // ab04: bipush 0
      // ab05: ldc2_w 0.17922431230545044
      // ab08: dastore
      // ab09: dup
      // ab0a: bipush 1
      // ab0b: ldc2_w 5.073923825786778E-9
      // ab0e: dastore
      // ab0f: aastore
      // ab10: dup
      // ab11: sipush 202
      // ab14: bipush 2
      // ab15: newarray 7
      // ab17: dup
      // ab18: bipush 0
      // ab19: ldc2_w 0.18004029989242554
      // ab1c: dastore
      // ab1d: dup
      // ab1e: bipush 1
      // ab1f: ldc2_w 1.1004278077575267E-8
      // ab22: dastore
      // ab23: aastore
      // ab24: dup
      // ab25: sipush 203
      // ab28: bipush 2
      // ab29: newarray 7
      // ab2b: dup
      // ab2c: bipush 0
      // ab2d: ldc2_w 0.1808556318283081
      // ab30: dastore
      // ab31: dup
      // ab32: bipush 1
      // ab33: ldc2_w 7.2831502374676964E-9
      // ab36: dastore
      // ab37: aastore
      // ab38: dup
      // ab39: sipush 204
      // ab3c: bipush 2
      // ab3d: newarray 7
      // ab3f: dup
      // ab40: bipush 0
      // ab41: ldc2_w 0.18167030811309814
      // ab44: dastore
      // ab45: dup
      // ab46: bipush 1
      // ab47: ldc2_w -5.0054634662706464E-9
      // ab4a: dastore
      // ab4b: aastore
      // ab4c: dup
      // ab4d: sipush 205
      // ab50: bipush 2
      // ab51: newarray 7
      // ab53: dup
      // ab54: bipush 0
      // ab55: ldc2_w 0.18248429894447327
      // ab58: dastore
      // ab59: dup
      // ab5a: bipush 1
      // ab5b: ldc2_w 5.022108460298934E-9
      // ab5e: dastore
      // ab5f: aastore
      // ab60: dup
      // ab61: sipush 206
      // ab64: bipush 2
      // ab65: newarray 7
      // ab67: dup
      // ab68: bipush 0
      // ab69: ldc2_w 0.18329763412475586
      // ab6c: dastore
      // ab6d: dup
      // ab6e: bipush 1
      // ab6f: ldc2_w 8.642254225732676E-9
      // ab72: dastore
      // ab73: aastore
      // ab74: dup
      // ab75: sipush 207
      // ab78: bipush 2
      // ab79: newarray 7
      // ab7b: dup
      // ab7c: bipush 0
      // ab7d: ldc2_w 0.18411031365394592
      // ab80: dastore
      // ab81: dup
      // ab82: bipush 1
      // ab83: ldc2_w 6.931054493326395E-9
      // ab86: dastore
      // ab87: aastore
      // ab88: dup
      // ab89: sipush 208
      // ab8c: bipush 2
      // ab8d: newarray 7
      // ab8f: dup
      // ab90: bipush 0
      // ab91: ldc2_w 0.18492233753204346
      // ab94: dastore
      // ab95: dup
      // ab96: bipush 1
      // ab97: ldc2_w 9.619685356326533E-10
      // ab9a: dastore
      // ab9b: aastore
      // ab9c: dup
      // ab9d: sipush 209
      // aba0: bipush 2
      // aba1: newarray 7
      // aba3: dup
      // aba4: bipush 0
      // aba5: ldc2_w 0.18573370575904846
      // aba8: dastore
      // aba9: dup
      // abaa: bipush 1
      // abab: ldc2_w -8.194157257980706E-9
      // abae: dastore
      // abaf: aastore
      // abb0: dup
      // abb1: sipush 210
      // abb4: bipush 2
      // abb5: newarray 7
      // abb7: dup
      // abb8: bipush 0
      // abb9: ldc2_w 0.18654438853263855
      // abbc: dastore
      // abbd: dup
      // abbe: bipush 1
      // abbf: ldc2_w 1.0333241479437797E-8
      // abc2: dastore
      // abc3: aastore
      // abc4: dup
      // abc5: sipush 211
      // abc8: bipush 2
      // abc9: newarray 7
      // abcb: dup
      // abcc: bipush 0
      // abcd: ldc2_w 0.1873544454574585
      // abd0: dastore
      // abd1: dup
      // abd2: bipush 1
      // abd3: ldc2_w -1.9948340196027965E-9
      // abd6: dastore
      // abd7: aastore
      // abd8: dup
      // abd9: sipush 212
      // abdc: bipush 2
      // abdd: newarray 7
      // abdf: dup
      // abe0: bipush 0
      // abe1: ldc2_w 0.1881638467311859
      // abe4: dastore
      // abe5: dup
      // abe6: bipush 1
      // abe7: ldc2_w -1.4313002926259948E-8
      // abea: dastore
      // abeb: aastore
      // abec: dup
      // abed: sipush 213
      // abf0: bipush 2
      // abf1: newarray 7
      // abf3: dup
      // abf4: bipush 0
      // abf5: ldc2_w 0.1889725625514984
      // abf8: dastore
      // abf9: dup
      // abfa: bipush 1
      // abfb: ldc2_w 4.241536392174967E-9
      // abfe: dastore
      // abff: aastore
      // ac00: dup
      // ac01: sipush 214
      // ac04: bipush 2
      // ac05: newarray 7
      // ac07: dup
      // ac08: bipush 0
      // ac09: ldc2_w 0.18978065252304077
      // ac0c: dastore
      // ac0d: dup
      // ac0e: bipush 1
      // ac0f: ldc2_w -4.877952454011428E-9
      // ac12: dastore
      // ac13: aastore
      // ac14: dup
      // ac15: sipush 215
      // ac18: bipush 2
      // ac19: newarray 7
      // ac1b: dup
      // ac1c: bipush 0
      // ac1d: ldc2_w 0.1905880868434906
      // ac20: dastore
      // ac21: dup
      // ac22: bipush 1
      // ac23: ldc2_w -1.0813801247641613E-8
      // ac26: dastore
      // ac27: aastore
      // ac28: dup
      // ac29: sipush 216
      // ac2c: bipush 2
      // ac2d: newarray 7
      // ac2f: dup
      // ac30: bipush 0
      // ac31: ldc2_w 0.1913948655128479
      // ac34: dastore
      // ac35: dup
      // ac36: bipush 1
      // ac37: ldc2_w -1.2513218445781325E-8
      // ac3a: dastore
      // ac3b: aastore
      // ac3c: dup
      // ac3d: sipush 217
      // ac40: bipush 2
      // ac41: newarray 7
      // ac43: dup
      // ac44: bipush 0
      // ac45: ldc2_w 0.19220098853111267
      // ac48: dastore
      // ac49: dup
      // ac4a: bipush 1
      // ac4b: ldc2_w -8.925958555729115E-9
      // ac4e: dastore
      // ac4f: aastore
      // ac50: dup
      // ac51: sipush 218
      // ac54: bipush 2
      // ac55: newarray 7
      // ac57: dup
      // ac58: bipush 0
      // ac59: ldc2_w 0.1930064558982849
      // ac5c: dastore
      // ac5d: dup
      // ac5e: bipush 1
      // ac5f: ldc2_w 9.956860681280245E-10
      // ac62: dastore
      // ac63: aastore
      // ac64: dup
      // ac65: sipush 219
      // ac68: bipush 2
      // ac69: newarray 7
      // ac6b: dup
      // ac6c: bipush 0
      // ac6d: ldc2_w 0.193811297416687
      // ac70: dastore
      // ac71: dup
      // ac72: bipush 1
      // ac73: ldc2_w -1.1505428993246996E-8
      // ac76: dastore
      // ac77: aastore
      // ac78: dup
      // ac79: sipush 220
      // ac7c: bipush 2
      // ac7d: newarray 7
      // ac7f: dup
      // ac80: bipush 0
      // ac81: ldc2_w 0.1946154534816742
      // ac84: dastore
      // ac85: dup
      // ac86: bipush 1
      // ac87: ldc2_w 1.4217997464522202E-8
      // ac8a: dastore
      // ac8b: aastore
      // ac8c: dup
      // ac8d: sipush 221
      // ac90: bipush 2
      // ac91: newarray 7
      // ac93: dup
      // ac94: bipush 0
      // ac95: ldc2_w 0.19541901350021362
      // ac98: dastore
      // ac99: dup
      // ac9a: bipush 1
      // ac9b: ldc2_w -1.0200858727747717E-8
      // ac9e: dastore
      // ac9f: aastore
      // aca0: dup
      // aca1: sipush 222
      // aca4: bipush 2
      // aca5: newarray 7
      // aca7: dup
      // aca8: bipush 0
      // aca9: ldc2_w 0.19622188806533813
      // acac: dastore
      // acad: dup
      // acae: bipush 1
      // acaf: ldc2_w 5.682607223902455E-9
      // acb2: dastore
      // acb3: aastore
      // acb4: dup
      // acb5: sipush 223
      // acb8: bipush 2
      // acb9: newarray 7
      // acbb: dup
      // acbc: bipush 0
      // acbd: ldc2_w 0.1970241367816925
      // acc0: dastore
      // acc1: dup
      // acc2: bipush 1
      // acc3: ldc2_w 3.2988908516009827E-9
      // acc6: dastore
      // acc7: aastore
      // acc8: dup
      // acc9: sipush 224
      // accc: bipush 2
      // accd: newarray 7
      // accf: dup
      // acd0: bipush 0
      // acd1: ldc2_w 0.19782572984695435
      // acd4: dastore
      // acd5: dup
      // acd6: bipush 1
      // acd7: ldc2_w 1.3482965534659446E-8
      // acda: dastore
      // acdb: aastore
      // acdc: dup
      // acdd: sipush 225
      // ace0: bipush 2
      // ace1: newarray 7
      // ace3: dup
      // ace4: bipush 0
      // ace5: ldc2_w 0.19862669706344604
      // ace8: dastore
      // ace9: dup
      // acea: bipush 1
      // aceb: ldc2_w 7.462678536479685E-9
      // acee: dastore
      // acef: aastore
      // acf0: dup
      // acf1: sipush 226
      // acf4: bipush 2
      // acf5: newarray 7
      // acf7: dup
      // acf8: bipush 0
      // acf9: ldc2_w 0.1994270384311676
      // acfc: dastore
      // acfd: dup
      // acfe: bipush 1
      // acff: ldc2_w -1.3734273888891115E-8
      // ad02: dastore
      // ad03: aastore
      // ad04: dup
      // ad05: sipush 227
      // ad08: bipush 2
      // ad09: newarray 7
      // ad0b: dup
      // ad0c: bipush 0
      // ad0d: ldc2_w 0.20022669434547424
      // ad10: dastore
      // ad11: dup
      // ad12: bipush 1
      // ad13: ldc2_w 1.0521983802642893E-8
      // ad16: dastore
      // ad17: aastore
      // ad18: dup
      // ad19: sipush 228
      // ad1c: bipush 2
      // ad1d: newarray 7
      // ad1f: dup
      // ad20: bipush 0
      // ad21: ldc2_w 0.20102575421333313
      // ad24: dastore
      // ad25: dup
      // ad26: bipush 1
      // ad27: ldc2_w -8.152742388541905E-9
      // ad2a: dastore
      // ad2b: aastore
      // ad2c: dup
      // ad2d: sipush 229
      // ad30: bipush 2
      // ad31: newarray 7
      // ad33: dup
      // ad34: bipush 0
      // ad35: ldc2_w 0.2018241584300995
      // ad38: dastore
      // ad39: dup
      // ad3a: bipush 1
      // ad3b: ldc2_w -9.133484280193855E-9
      // ad3e: dastore
      // ad3f: aastore
      // ad40: dup
      // ad41: sipush 230
      // ad44: bipush 2
      // ad45: newarray 7
      // ad47: dup
      // ad48: bipush 0
      // ad49: ldc2_w 0.20262190699577332
      // ad4c: dastore
      // ad4d: dup
      // ad4e: bipush 1
      // ad4f: ldc2_w 8.59763959528144E-9
      // ad52: dastore
      // ad53: aastore
      // ad54: dup
      // ad55: sipush 231
      // ad58: bipush 2
      // ad59: newarray 7
      // ad5b: dup
      // ad5c: bipush 0
      // ad5d: ldc2_w 0.2034190595149994
      // ad60: dastore
      // ad61: dup
      // ad62: bipush 1
      // ad63: ldc2_w -1.3548568223001414E-8
      // ad66: dastore
      // ad67: aastore
      // ad68: dup
      // ad69: sipush 232
      // ad6c: bipush 2
      // ad6d: newarray 7
      // ad6f: dup
      // ad70: bipush 0
      // ad71: ldc2_w 0.20421552658081055
      // ad74: dastore
      // ad75: dup
      // ad76: bipush 1
      // ad77: ldc2_w 1.4847880344628818E-8
      // ad7a: dastore
      // ad7b: aastore
      // ad7c: dup
      // ad7d: sipush 233
      // ad80: bipush 2
      // ad81: newarray 7
      // ad83: dup
      // ad84: bipush 0
      // ad85: ldc2_w 0.20501139760017395
      // ad88: dastore
      // ad89: dup
      // ad8a: bipush 1
      // ad8b: ldc2_w 5.390620378060543E-9
      // ad8e: dastore
      // ad8f: aastore
      // ad90: dup
      // ad91: sipush 234
      // ad94: bipush 2
      // ad95: newarray 7
      // ad97: dup
      // ad98: bipush 0
      // ad99: ldc2_w 0.2058066427707672
      // ad9c: dastore
      // ad9d: dup
      // ad9e: bipush 1
      // ad9f: ldc2_w -1.1109834472051523E-8
      // ada2: dastore
      // ada3: aastore
      // ada4: dup
      // ada5: sipush 235
      // ada8: bipush 2
      // ada9: newarray 7
      // adab: dup
      // adac: bipush 0
      // adad: ldc2_w 0.20660123229026794
      // adb0: dastore
      // adb1: dup
      // adb2: bipush 1
      // adb3: ldc2_w -3.845373872038116E-9
      // adb6: dastore
      // adb7: aastore
      // adb8: dup
      // adb9: sipush 236
      // adbc: bipush 2
      // adbd: newarray 7
      // adbf: dup
      // adc0: bipush 0
      // adc1: ldc2_w 0.20739519596099854
      // adc4: dastore
      // adc5: dup
      // adc6: bipush 1
      // adc7: ldc2_w -1.6149279479975042E-9
      // adca: dastore
      // adcb: aastore
      // adcc: dup
      // adcd: sipush 237
      // add0: bipush 2
      // add1: newarray 7
      // add3: dup
      // add4: bipush 0
      // add5: ldc2_w 0.20818853378295898
      // add8: dastore
      // add9: dup
      // adda: bipush 1
      // addb: ldc2_w -3.4174925203771133E-9
      // adde: dastore
      // addf: aastore
      // ade0: dup
      // ade1: sipush 238
      // ade4: bipush 2
      // ade5: newarray 7
      // ade7: dup
      // ade8: bipush 0
      // ade9: ldc2_w 0.2089812457561493
      // adec: dastore
      // aded: dup
      // adee: bipush 1
      // adef: ldc2_w -8.254443919468538E-9
      // adf2: dastore
      // adf3: aastore
      // adf4: dup
      // adf5: sipush 239
      // adf8: bipush 2
      // adf9: newarray 7
      // adfb: dup
      // adfc: bipush 0
      // adfd: ldc2_w 0.20977330207824707
      // ae00: dastore
      // ae01: dup
      // ae02: bipush 1
      // ae03: ldc2_w 1.4672790944499144E-8
      // ae06: dastore
      // ae07: aastore
      // ae08: dup
      // ae09: sipush 240
      // ae0c: bipush 2
      // ae0d: newarray 7
      // ae0f: dup
      // ae10: bipush 0
      // ae11: ldc2_w 0.2105647623538971
      // ae14: dastore
      // ae15: dup
      // ae16: bipush 1
      // ae17: ldc2_w 6.753452542942992E-9
      // ae1a: dastore
      // ae1b: aastore
      // ae1c: dup
      // ae1d: sipush 241
      // ae20: bipush 2
      // ae21: newarray 7
      // ae23: dup
      // ae24: bipush 0
      // ae25: ldc2_w 0.21135559678077698
      // ae28: dastore
      // ae29: dup
      // ae2a: bipush 1
      // ae2b: ldc2_w -1.218609462241927E-9
      // ae2e: dastore
      // ae2f: aastore
      // ae30: dup
      // ae31: sipush 242
      // ae34: bipush 2
      // ae35: newarray 7
      // ae37: dup
      // ae38: bipush 0
      // ae39: ldc2_w 0.21214580535888672
      // ae3c: dastore
      // ae3d: dup
      // ae3e: bipush 1
      // ae3f: ldc2_w -8.254218316367887E-9
      // ae42: dastore
      // ae43: aastore
      // ae44: dup
      // ae45: sipush 243
      // ae48: bipush 2
      // ae49: newarray 7
      // ae4b: dup
      // ae4c: bipush 0
      // ae4d: ldc2_w 0.21293538808822632
      // ae50: dastore
      // ae51: dup
      // ae52: bipush 1
      // ae53: ldc2_w -1.3366540360587255E-8
      // ae56: dastore
      // ae57: aastore
      // ae58: dup
      // ae59: sipush 244
      // ae5c: bipush 2
      // ae5d: newarray 7
      // ae5f: dup
      // ae60: bipush 0
      // ae61: ldc2_w 0.2137243151664734
      // ae64: dastore
      // ae65: dup
      // ae66: bipush 1
      // ae67: ldc2_w 1.4231244750190031E-8
      // ae6a: dastore
      // ae6b: aastore
      // ae6c: dup
      // ae6d: sipush 245
      // ae70: bipush 2
      // ae71: newarray 7
      // ae73: dup
      // ae74: bipush 0
      // ae75: ldc2_w 0.2145126760005951
      // ae78: dastore
      // ae79: dup
      // ae7a: bipush 1
      // ae7b: ldc2_w -1.3885660525939072E-8
      // ae7e: dastore
      // ae7f: aastore
      // ae80: dup
      // ae81: sipush 246
      // ae84: bipush 2
      // ae85: newarray 7
      // ae87: dup
      // ae88: bipush 0
      // ae89: ldc2_w 0.21530038118362427
      // ae8c: dastore
      // ae8d: dup
      // ae8e: bipush 1
      // ae8f: ldc2_w -7.3304404046850136E-9
      // ae92: dastore
      // ae93: aastore
      // ae94: dup
      // ae95: sipush 247
      // ae98: bipush 2
      // ae99: newarray 7
      // ae9b: dup
      // ae9c: bipush 0
      // ae9d: ldc2_w 0.2160874605178833
      // aea0: dastore
      // aea1: dup
      // aea2: bipush 1
      // aea3: ldc2_w 5.072117654842356E-9
      // aea6: dastore
      // aea7: aastore
      // aea8: dup
      // aea9: sipush 248
      // aeac: bipush 2
      // aead: newarray 7
      // aeaf: dup
      // aeb0: bipush 0
      // aeb1: ldc2_w 0.21687394380569458
      // aeb4: dastore
      // aeb5: dup
      // aeb6: bipush 1
      // aeb7: ldc2_w -5.505080220459036E-9
      // aeba: dastore
      // aebb: aastore
      // aebc: dup
      // aebd: sipush 249
      // aec0: bipush 2
      // aec1: newarray 7
      // aec3: dup
      // aec4: bipush 0
      // aec5: ldc2_w 0.21765980124473572
      // aec8: dastore
      // aec9: dup
      // aeca: bipush 1
      // aecb: ldc2_w -8.286782292266659E-9
      // aece: dastore
      // aecf: aastore
      // aed0: dup
      // aed1: sipush 250
      // aed4: bipush 2
      // aed5: newarray 7
      // aed7: dup
      // aed8: bipush 0
      // aed9: ldc2_w 0.2184450328350067
      // aedc: dastore
      // aedd: dup
      // aede: bipush 1
      // aedf: ldc2_w -2.302351152358085E-9
      // aee2: dastore
      // aee3: aastore
      // aee4: dup
      // aee5: sipush 251
      // aee8: bipush 2
      // aee9: newarray 7
      // aeeb: dup
      // aeec: bipush 0
      // aeed: ldc2_w 0.21922963857650757
      // aef0: dastore
      // aef1: dup
      // aef2: bipush 1
      // aef3: ldc2_w 1.3416565858314603E-8
      // aef6: dastore
      // aef7: aastore
      // aef8: dup
      // aef9: sipush 252
      // aefc: bipush 2
      // aefd: newarray 7
      // aeff: dup
      // af00: bipush 0
      // af01: ldc2_w 0.22001364827156067
      // af04: dastore
      // af05: dup
      // af06: bipush 1
      // af07: ldc2_w 1.0033721426962048E-8
      // af0a: dastore
      // af0b: aastore
      // af0c: dup
      // af0d: sipush 253
      // af10: bipush 2
      // af11: newarray 7
      // af13: dup
      // af14: bipush 0
      // af15: ldc2_w 0.22079706192016602
      // af18: dastore
      // af19: dup
      // af1a: bipush 1
      // af1b: ldc2_w -1.1487079818684332E-8
      // af1e: dastore
      // af1f: aastore
      // af20: dup
      // af21: sipush 254
      // af24: bipush 2
      // af25: newarray 7
      // af27: dup
      // af28: bipush 0
      // af29: ldc2_w 0.22157981991767883
      // af2c: dastore
      // af2d: dup
      // af2e: bipush 1
      // af2f: ldc2_w 9.420348186357043E-9
      // af32: dastore
      // af33: aastore
      // af34: dup
      // af35: sipush 255
      // af38: bipush 2
      // af39: newarray 7
      // af3b: dup
      // af3c: bipush 0
      // af3d: ldc2_w 0.2223619818687439
      // af40: dastore
      // af41: dup
      // af42: bipush 1
      // af43: ldc2_w 1.4110645699377834E-8
      // af46: dastore
      // af47: aastore
      // af48: dup
      // af49: sipush 256
      // af4c: bipush 2
      // af4d: newarray 7
      // af4f: dup
      // af50: bipush 0
      // af51: ldc2_w 0.2231435477733612
      // af54: dastore
      // af55: dup
      // af56: bipush 1
      // af57: ldc2_w 3.5408485497116107E-9
      // af5a: dastore
      // af5b: aastore
      // af5c: dup
      // af5d: sipush 257
      // af60: bipush 2
      // af61: newarray 7
      // af63: dup
      // af64: bipush 0
      // af65: ldc2_w 0.22392448782920837
      // af68: dastore
      // af69: dup
      // af6a: bipush 1
      // af6b: ldc2_w 8.468072777056227E-9
      // af6e: dastore
      // af6f: aastore
      // af70: dup
      // af71: sipush 258
      // af74: bipush 2
      // af75: newarray 7
      // af77: dup
      // af78: bipush 0
      // af79: ldc2_w 0.2247048318386078
      // af7c: dastore
      // af7d: dup
      // af7e: bipush 1
      // af7f: ldc2_w 4.255446699237779E-11
      // af82: dastore
      // af83: aastore
      // af84: dup
      // af85: sipush 259
      // af88: bipush 2
      // af89: newarray 7
      // af8b: dup
      // af8c: bipush 0
      // af8d: ldc2_w 0.22548454999923706
      // af90: dastore
      // af91: dup
      // af92: bipush 1
      // af93: ldc2_w 9.016946273084244E-9
      // af96: dastore
      // af97: aastore
      // af98: dup
      // af99: sipush 260
      // af9c: bipush 2
      // af9d: newarray 7
      // af9f: dup
      // afa0: bipush 0
      // afa1: ldc2_w 0.22626367211341858
      // afa4: dastore
      // afa5: dup
      // afa6: bipush 1
      // afa7: ldc2_w 6.537034810260226E-9
      // afaa: dastore
      // afab: aastore
      // afac: dup
      // afad: sipush 261
      // afb0: bipush 2
      // afb1: newarray 7
      // afb3: dup
      // afb4: bipush 0
      // afb5: ldc2_w 0.22704219818115234
      // afb8: dastore
      // afb9: dup
      // afba: bipush 1
      // afbb: ldc2_w -6.451285264969768E-9
      // afbe: dastore
      // afbf: aastore
      // afc0: dup
      // afc1: sipush 262
      // afc4: bipush 2
      // afc5: newarray 7
      // afc7: dup
      // afc8: bipush 0
      // afc9: ldc2_w 0.22782009840011597
      // afcc: dastore
      // afcd: dup
      // afce: bipush 1
      // afcf: ldc2_w 7.979956357126066E-10
      // afd2: dastore
      // afd3: aastore
      // afd4: dup
      // afd5: sipush 263
      // afd8: bipush 2
      // afd9: newarray 7
      // afdb: dup
      // afdc: bipush 0
      // afdd: ldc2_w 0.22859740257263184
      // afe0: dastore
      // afe1: dup
      // afe2: bipush 1
      // afe3: ldc2_w -5.759582672039005E-10
      // afe6: dastore
      // afe7: aastore
      // afe8: dup
      // afe9: sipush 264
      // afec: bipush 2
      // afed: newarray 7
      // afef: dup
      // aff0: bipush 0
      // aff1: ldc2_w 0.22937411069869995
      // aff4: dastore
      // aff5: dup
      // aff6: bipush 1
      // aff7: ldc2_w -9.633854121180397E-9
      // affa: dastore
      // affb: aastore
      // affc: dup
      // affd: sipush 265
      // b000: bipush 2
      // b001: newarray 7
      // b003: dup
      // b004: bipush 0
      // b005: ldc2_w 0.23015019297599792
      // b008: dastore
      // b009: dup
      // b00a: bipush 1
      // b00b: ldc2_w 4.363736368635843E-9
      // b00e: dastore
      // b00f: aastore
      // b010: dup
      // b011: sipush 266
      // b014: bipush 2
      // b015: newarray 7
      // b017: dup
      // b018: bipush 0
      // b019: ldc2_w 0.23092567920684814
      // b01c: dastore
      // b01d: dup
      // b01e: bipush 1
      // b01f: ldc2_w 1.2549416560182509E-8
      // b022: dastore
      // b023: aastore
      // b024: dup
      // b025: sipush 267
      // b028: bipush 2
      // b029: newarray 7
      // b02b: dup
      // b02c: bipush 0
      // b02d: ldc2_w 0.231700599193573
      // b030: dastore
      // b031: dup
      // b032: bipush 1
      // b033: ldc2_w -1.3946383592553814E-8
      // b036: dastore
      // b037: aastore
      // b038: dup
      // b039: sipush 268
      // b03c: bipush 2
      // b03d: newarray 7
      // b03f: dup
      // b040: bipush 0
      // b041: ldc2_w 0.2324748933315277
      // b044: dastore
      // b045: dup
      // b046: bipush 1
      // b047: ldc2_w -1.458843364504023E-8
      // b04a: dastore
      // b04b: aastore
      // b04c: dup
      // b04d: sipush 269
      // b050: bipush 2
      // b051: newarray 7
      // b053: dup
      // b054: bipush 0
      // b055: ldc2_w 0.23324856162071228
      // b058: dastore
      // b059: dup
      // b05a: bipush 1
      // b05b: ldc2_w 1.1551692104697154E-8
      // b05e: dastore
      // b05f: aastore
      // b060: dup
      // b061: sipush 270
      // b064: bipush 2
      // b065: newarray 7
      // b067: dup
      // b068: bipush 0
      // b069: ldc2_w 0.23402166366577148
      // b06c: dastore
      // b06d: dup
      // b06e: bipush 1
      // b06f: ldc2_w 5.795621295524984E-9
      // b072: dastore
      // b073: aastore
      // b074: dup
      // b075: sipush 271
      // b078: bipush 2
      // b079: newarray 7
      // b07b: dup
      // b07c: bipush 0
      // b07d: ldc2_w 0.23479416966438293
      // b080: dastore
      // b081: dup
      // b082: bipush 1
      // b083: ldc2_w -1.1301979046684263E-9
      // b086: dastore
      // b087: aastore
      // b088: dup
      // b089: sipush 272
      // b08c: bipush 2
      // b08d: newarray 7
      // b08f: dup
      // b090: bipush 0
      // b091: ldc2_w 0.23556607961654663
      // b094: dastore
      // b095: dup
      // b096: bipush 1
      // b097: ldc2_w -8.303779721781787E-9
      // b09a: dastore
      // b09b: aastore
      // b09c: dup
      // b09d: sipush 273
      // b0a0: bipush 2
      // b0a1: newarray 7
      // b0a3: dup
      // b0a4: bipush 0
      // b0a5: ldc2_w 0.23633739352226257
      // b0a8: dastore
      // b0a9: dup
      // b0aa: bipush 1
      // b0ab: ldc2_w -1.4805271785394075E-8
      // b0ae: dastore
      // b0af: aastore
      // b0b0: dup
      // b0b1: sipush 274
      // b0b4: bipush 2
      // b0b5: newarray 7
      // b0b7: dup
      // b0b8: bipush 0
      // b0b9: ldc2_w 0.23710808157920837
      // b0bc: dastore
      // b0bd: dup
      // b0be: bipush 1
      // b0bf: ldc2_w 1.0085373835899469E-8
      // b0c2: dastore
      // b0c3: aastore
      // b0c4: dup
      // b0c5: sipush 275
      // b0c8: bipush 2
      // b0c9: newarray 7
      // b0cb: dup
      // b0cc: bipush 0
      // b0cd: ldc2_w 0.2378782033920288
      // b0d0: dastore
      // b0d1: dup
      // b0d2: bipush 1
      // b0d3: ldc2_w 7.679117635349454E-9
      // b0d6: dastore
      // b0d7: aastore
      // b0d8: dup
      // b0d9: sipush 276
      // b0dc: bipush 2
      // b0dd: newarray 7
      // b0df: dup
      // b0e0: bipush 0
      // b0e1: ldc2_w 0.2386477291584015
      // b0e4: dastore
      // b0e5: dup
      // b0e6: bipush 1
      // b0e7: ldc2_w 8.69177352065934E-9
      // b0ea: dastore
      // b0eb: aastore
      // b0ec: dup
      // b0ed: sipush 277
      // b0f0: bipush 2
      // b0f1: newarray 7
      // b0f3: dup
      // b0f4: bipush 0
      // b0f5: ldc2_w 0.23941665887832642
      // b0f8: dastore
      // b0f9: dup
      // b0fa: bipush 1
      // b0fb: ldc2_w 1.4034725764547136E-8
      // b0fe: dastore
      // b0ff: aastore
      // b100: dup
      // b101: sipush 278
      // b104: bipush 2
      // b105: newarray 7
      // b107: dup
      // b108: bipush 0
      // b109: ldc2_w 0.24018502235412598
      // b10c: dastore
      // b10d: dup
      // b10e: bipush 1
      // b10f: ldc2_w -5.185064518887831E-9
      // b112: dastore
      // b113: aastore
      // b114: dup
      // b115: sipush 279
      // b118: bipush 2
      // b119: newarray 7
      // b11b: dup
      // b11c: bipush 0
      // b11d: ldc2_w 0.2409527599811554
      // b120: dastore
      // b121: dup
      // b122: bipush 1
      // b123: ldc2_w 1.1544236628121676E-8
      // b126: dastore
      // b127: aastore
      // b128: dup
      // b129: sipush 280
      // b12c: bipush 2
      // b12d: newarray 7
      // b12f: dup
      // b130: bipush 0
      // b131: ldc2_w 0.24171993136405945
      // b134: dastore
      // b135: dup
      // b136: bipush 1
      // b137: ldc2_w 5.523085719902123E-9
      // b13a: dastore
      // b13b: aastore
      // b13c: dup
      // b13d: sipush 281
      // b140: bipush 2
      // b141: newarray 7
      // b143: dup
      // b144: bipush 0
      // b145: ldc2_w 0.24248650670051575
      // b148: dastore
      // b149: dup
      // b14a: bipush 1
      // b14b: ldc2_w 7.456824943331887E-9
      // b14e: dastore
      // b14f: aastore
      // b150: dup
      // b151: sipush 282
      // b154: bipush 2
      // b155: newarray 7
      // b157: dup
      // b158: bipush 0
      // b159: ldc2_w 0.24325251579284668
      // b15c: dastore
      // b15d: dup
      // b15e: bipush 1
      // b15f: ldc2_w -1.1555923403029638E-8
      // b162: dastore
      // b163: aastore
      // b164: dup
      // b165: sipush 283
      // b168: bipush 2
      // b169: newarray 7
      // b16b: dup
      // b16c: bipush 0
      // b16d: ldc2_w 0.24401789903640747
      // b170: dastore
      // b171: dup
      // b172: bipush 1
      // b173: ldc2_w 8.988361382732908E-9
      // b176: dastore
      // b177: aastore
      // b178: dup
      // b179: sipush 284
      // b17c: bipush 2
      // b17d: newarray 7
      // b17f: dup
      // b180: bipush 0
      // b181: ldc2_w 0.2447827160358429
      // b184: dastore
      // b185: dup
      // b186: bipush 1
      // b187: ldc2_w 1.0381848020926893E-8
      // b18a: dastore
      // b18b: aastore
      // b18c: dup
      // b18d: sipush 285
      // b190: bipush 2
      // b191: newarray 7
      // b193: dup
      // b194: bipush 0
      // b195: ldc2_w 0.24554696679115295
      // b198: dastore
      // b199: dup
      // b19a: bipush 1
      // b19b: ldc2_w -6.480706118857055E-9
      // b19e: dastore
      // b19f: aastore
      // b1a0: dup
      // b1a1: sipush 286
      // b1a4: bipush 2
      // b1a5: newarray 7
      // b1a7: dup
      // b1a8: bipush 0
      // b1a9: ldc2_w 0.24631062150001526
      // b1ac: dastore
      // b1ad: dup
      // b1ae: bipush 1
      // b1af: ldc2_w -1.0904271124793968E-8
      // b1b2: dastore
      // b1b3: aastore
      // b1b4: dup
      // b1b5: sipush 287
      // b1b8: bipush 2
      // b1b9: newarray 7
      // b1bb: dup
      // b1bc: bipush 0
      // b1bd: ldc2_w 0.2470736801624298
      // b1c0: dastore
      // b1c1: dup
      // b1c2: bipush 1
      // b1c3: ldc2_w -1.998183061531611E-9
      // b1c6: dastore
      // b1c7: aastore
      // b1c8: dup
      // b1c9: sipush 288
      // b1cc: bipush 2
      // b1cd: newarray 7
      // b1cf: dup
      // b1d0: bipush 0
      // b1d1: ldc2_w 0.247836172580719
      // b1d4: dastore
      // b1d5: dup
      // b1d6: bipush 1
      // b1d7: ldc2_w -8.676137737360023E-9
      // b1da: dastore
      // b1db: aastore
      // b1dc: dup
      // b1dd: sipush 289
      // b1e0: bipush 2
      // b1e1: newarray 7
      // b1e3: dup
      // b1e4: bipush 0
      // b1e5: ldc2_w 0.24859806895256042
      // b1e8: dastore
      // b1e9: dup
      // b1ea: bipush 1
      // b1eb: ldc2_w -2.4921733203932487E-10
      // b1ee: dastore
      // b1ef: aastore
      // b1f0: dup
      // b1f1: sipush 290
      // b1f4: bipush 2
      // b1f5: newarray 7
      // b1f7: dup
      // b1f8: bipush 0
      // b1f9: ldc2_w 0.2493593990802765
      // b1fc: dastore
      // b1fd: dup
      // b1fe: bipush 1
      // b1ff: ldc2_w -5.635173762130303E-9
      // b202: dastore
      // b203: aastore
      // b204: dup
      // b205: sipush 291
      // b208: bipush 2
      // b209: newarray 7
      // b20b: dup
      // b20c: bipush 0
      // b20d: ldc2_w 0.2501201629638672
      // b210: dastore
      // b211: dup
      // b212: bipush 1
      // b213: ldc2_w -2.3951455355985637E-8
      // b216: dastore
      // b217: aastore
      // b218: dup
      // b219: sipush 292
      // b21c: bipush 2
      // b21d: newarray 7
      // b21f: dup
      // b220: bipush 0
      // b221: ldc2_w 0.25088030099868774
      // b224: dastore
      // b225: dup
      // b226: bipush 1
      // b227: ldc2_w 5.287121672447825E-9
      // b22a: dastore
      // b22b: aastore
      // b22c: dup
      // b22d: sipush 293
      // b230: bipush 2
      // b231: newarray 7
      // b233: dup
      // b234: bipush 0
      // b235: ldc2_w 0.2516399025917053
      // b238: dastore
      // b239: dup
      // b23a: bipush 1
      // b23b: ldc2_w -6.447877375049486E-9
      // b23e: dastore
      // b23f: aastore
      // b240: dup
      // b241: sipush 294
      // b244: bipush 2
      // b245: newarray 7
      // b247: dup
      // b248: bipush 0
      // b249: ldc2_w 0.25239890813827515
      // b24c: dastore
      // b24d: dup
      // b24e: bipush 1
      // b24f: ldc2_w 1.32472428796441E-9
      // b252: dastore
      // b253: aastore
      // b254: dup
      // b255: sipush 295
      // b258: bipush 2
      // b259: newarray 7
      // b25b: dup
      // b25c: bipush 0
      // b25d: ldc2_w 0.2531573176383972
      // b260: dastore
      // b261: dup
      // b262: bipush 1
      // b263: ldc2_w 2.9479464287605006E-8
      // b266: dastore
      // b267: aastore
      // b268: dup
      // b269: sipush 296
      // b26c: bipush 2
      // b26d: newarray 7
      // b26f: dup
      // b270: bipush 0
      // b271: ldc2_w 0.2539151906967163
      // b274: dastore
      // b275: dup
      // b276: bipush 1
      // b277: ldc2_w 1.9284247135543574E-8
      // b27a: dastore
      // b27b: aastore
      // b27c: dup
      // b27d: sipush 297
      // b280: bipush 2
      // b281: newarray 7
      // b283: dup
      // b284: bipush 0
      // b285: ldc2_w 0.2546725273132324
      // b288: dastore
      // b289: dup
      // b28a: bipush 1
      // b28b: ldc2_w -2.8390360197221716E-8
      // b28e: dastore
      // b28f: aastore
      // b290: dup
      // b291: sipush 298
      // b294: bipush 2
      // b295: newarray 7
      // b297: dup
      // b298: bipush 0
      // b299: ldc2_w 0.255429208278656
      // b29c: dastore
      // b29d: dup
      // b29e: bipush 1
      // b29f: ldc2_w 6.533522495226226E-9
      // b2a2: dastore
      // b2a3: aastore
      // b2a4: dup
      // b2a5: sipush 299
      // b2a8: bipush 2
      // b2a9: newarray 7
      // b2ab: dup
      // b2ac: bipush 0
      // b2ad: ldc2_w 0.2561853528022766
      // b2b0: dastore
      // b2b1: dup
      // b2b2: bipush 1
      // b2b3: ldc2_w 5.713225978895991E-9
      // b2b6: dastore
      // b2b7: aastore
      // b2b8: dup
      // b2b9: sipush 300
      // b2bc: bipush 2
      // b2bd: newarray 7
      // b2bf: dup
      // b2c0: bipush 0
      // b2c1: ldc2_w 0.25694090127944946
      // b2c4: dastore
      // b2c5: dup
      // b2c6: bipush 1
      // b2c7: ldc2_w 2.9618050962556135E-8
      // b2ca: dastore
      // b2cb: aastore
      // b2cc: dup
      // b2cd: sipush 301
      // b2d0: bipush 2
      // b2d1: newarray 7
      // b2d3: dup
      // b2d4: bipush 0
      // b2d5: ldc2_w 0.25769591331481934
      // b2d8: dastore
      // b2d9: dup
      // b2da: bipush 1
      // b2db: ldc2_w 1.950605015323617E-8
      // b2de: dastore
      // b2df: aastore
      // b2e0: dup
      // b2e1: sipush 302
      // b2e4: bipush 2
      // b2e5: newarray 7
      // b2e7: dup
      // b2e8: bipush 0
      // b2e9: ldc2_w 0.25845038890838623
      // b2ec: dastore
      // b2ed: dup
      // b2ee: bipush 1
      // b2ef: ldc2_w -2.3762031507525576E-8
      // b2f2: dastore
      // b2f3: aastore
      // b2f4: dup
      // b2f5: sipush 303
      // b2f8: bipush 2
      // b2f9: newarray 7
      // b2fb: dup
      // b2fc: bipush 0
      // b2fd: ldc2_w 0.2592042088508606
      // b300: dastore
      // b301: dup
      // b302: bipush 1
      // b303: ldc2_w 1.98818938195077E-8
      // b306: dastore
      // b307: aastore
      // b308: dup
      // b309: sipush 304
      // b30c: bipush 2
      // b30d: newarray 7
      // b30f: dup
      // b310: bipush 0
      // b311: ldc2_w 0.25995755195617676
      // b314: dastore
      // b315: dup
      // b316: bipush 1
      // b317: ldc2_w -2.751925069084042E-8
      // b31a: dastore
      // b31b: aastore
      // b31c: dup
      // b31d: sipush 305
      // b320: bipush 2
      // b321: newarray 7
      // b323: dup
      // b324: bipush 0
      // b325: ldc2_w 0.2607102394104004
      // b328: dastore
      // b329: dup
      // b32a: bipush 1
      // b32b: ldc2_w 1.3703391844683932E-8
      // b32e: dastore
      // b32f: aastore
      // b330: dup
      // b331: sipush 306
      // b334: bipush 2
      // b335: newarray 7
      // b337: dup
      // b338: bipush 0
      // b339: ldc2_w 0.26146239042282104
      // b33c: dastore
      // b33d: dup
      // b33e: bipush 1
      // b33f: ldc2_w 2.5193525310038174E-8
      // b342: dastore
      // b343: aastore
      // b344: dup
      // b345: sipush 307
      // b348: bipush 2
      // b349: newarray 7
      // b34b: dup
      // b34c: bipush 0
      // b34d: ldc2_w 0.2622140049934387
      // b350: dastore
      // b351: dup
      // b352: bipush 1
      // b353: ldc2_w 7.802219817310385E-9
      // b356: dastore
      // b357: aastore
      // b358: dup
      // b359: sipush 308
      // b35c: bipush 2
      // b35d: newarray 7
      // b35f: dup
      // b360: bipush 0
      // b361: ldc2_w 0.26296502351760864
      // b364: dastore
      // b365: dup
      // b366: bipush 1
      // b367: ldc2_w 2.1983272709242607E-8
      // b36a: dastore
      // b36b: aastore
      // b36c: dup
      // b36d: sipush 309
      // b370: bipush 2
      // b371: newarray 7
      // b373: dup
      // b374: bipush 0
      // b375: ldc2_w 0.2637155055999756
      // b378: dastore
      // b379: dup
      // b37a: bipush 1
      // b37b: ldc2_w 8.979279989292184E-9
      // b37e: dastore
      // b37f: aastore
      // b380: dup
      // b381: sipush 310
      // b384: bipush 2
      // b385: newarray 7
      // b387: dup
      // b388: bipush 0
      // b389: ldc2_w 0.2644653916358948
      // b38c: dastore
      // b38d: dup
      // b38e: bipush 1
      // b38f: ldc2_w 2.9240221157844312E-8
      // b392: dastore
      // b393: aastore
      // b394: dup
      // b395: sipush 311
      // b398: bipush 2
      // b399: newarray 7
      // b39b: dup
      // b39c: bipush 0
      // b39d: ldc2_w 0.265214741230011
      // b3a0: dastore
      // b3a1: dup
      // b3a2: bipush 1
      // b3a3: ldc2_w 2.4004885823813374E-8
      // b3a6: dastore
      // b3a7: aastore
      // b3a8: dup
      // b3a9: sipush 312
      // b3ac: bipush 2
      // b3ad: newarray 7
      // b3af: dup
      // b3b0: bipush 0
      // b3b1: ldc2_w 0.2659635543823242
      // b3b4: dastore
      // b3b5: dup
      // b3b6: bipush 1
      // b3b7: ldc2_w -5.885186277410878E-9
      // b3ba: dastore
      // b3bb: aastore
      // b3bc: dup
      // b3bd: sipush 313
      // b3c0: bipush 2
      // b3c1: newarray 7
      // b3c3: dup
      // b3c4: bipush 0
      // b3c5: ldc2_w 0.2667117714881897
      // b3c8: dastore
      // b3c9: dup
      // b3ca: bipush 1
      // b3cb: ldc2_w 1.4300386517357162E-11
      // b3ce: dastore
      // b3cf: aastore
      // b3d0: dup
      // b3d1: sipush 314
      // b3d4: bipush 2
      // b3d5: newarray 7
      // b3d7: dup
      // b3d8: bipush 0
      // b3d9: ldc2_w 0.2674594521522522
      // b3dc: dastore
      // b3dd: dup
      // b3de: bipush 1
      // b3df: ldc2_w -1.7063531531989365E-8
      // b3e2: dastore
      // b3e3: aastore
      // b3e4: dup
      // b3e5: sipush 315
      // b3e8: bipush 2
      // b3e9: newarray 7
      // b3eb: dup
      // b3ec: bipush 0
      // b3ed: ldc2_w 0.26820653676986694
      // b3f0: dastore
      // b3f1: dup
      // b3f2: bipush 1
      // b3f3: ldc2_w 3.3218524692903896E-9
      // b3f6: dastore
      // b3f7: aastore
      // b3f8: dup
      // b3f9: sipush 316
      // b3fc: bipush 2
      // b3fd: newarray 7
      // b3ff: dup
      // b400: bipush 0
      // b401: ldc2_w 0.2689530849456787
      // b404: dastore
      // b405: dup
      // b406: bipush 1
      // b407: ldc2_w 2.3998252479954764E-9
      // b40a: dastore
      // b40b: aastore
      // b40c: dup
      // b40d: sipush 317
      // b410: bipush 2
      // b411: newarray 7
      // b413: dup
      // b414: bipush 0
      // b415: ldc2_w 0.2696990966796875
      // b418: dastore
      // b419: dup
      // b41a: bipush 1
      // b41b: ldc2_w -1.8997462070389404E-8
      // b41e: dastore
      // b41f: aastore
      // b420: dup
      // b421: sipush 318
      // b424: bipush 2
      // b425: newarray 7
      // b427: dup
      // b428: bipush 0
      // b429: ldc2_w 0.27044451236724854
      // b42c: dastore
      // b42d: dup
      // b42e: bipush 1
      // b42f: ldc2_w -4.350745270980051E-10
      // b432: dastore
      // b433: aastore
      // b434: dup
      // b435: sipush 319
      // b438: bipush 2
      // b439: newarray 7
      // b43b: dup
      // b43c: bipush 0
      // b43d: ldc2_w 0.2711893916130066
      // b440: dastore
      // b441: dup
      // b442: bipush 1
      // b443: ldc2_w -6.892221115467135E-10
      // b446: dastore
      // b447: aastore
      // b448: dup
      // b449: sipush 320
      // b44c: bipush 2
      // b44d: newarray 7
      // b44f: dup
      // b450: bipush 0
      // b451: ldc2_w 0.27193373441696167
      // b454: dastore
      // b455: dup
      // b456: bipush 1
      // b457: ldc2_w -1.89333199110902E-8
      // b45a: dastore
      // b45b: aastore
      // b45c: dup
      // b45d: sipush 321
      // b460: bipush 2
      // b461: newarray 7
      // b463: dup
      // b464: bipush 0
      // b465: ldc2_w 0.272677481174469
      // b468: dastore
      // b469: dup
      // b46a: bipush 1
      // b46b: ldc2_w 5.262017392507765E-9
      // b46e: dastore
      // b46f: aastore
      // b470: dup
      // b471: sipush 322
      // b474: bipush 2
      // b475: newarray 7
      // b477: dup
      // b478: bipush 0
      // b479: ldc2_w 0.27342069149017334
      // b47c: dastore
      // b47d: dup
      // b47e: bipush 1
      // b47f: ldc2_w 1.3115046679980076E-8
      // b482: dastore
      // b483: aastore
      // b484: dup
      // b485: sipush 323
      // b488: bipush 2
      // b489: newarray 7
      // b48b: dup
      // b48c: bipush 0
      // b48d: ldc2_w 0.2741633653640747
      // b490: dastore
      // b491: dup
      // b492: bipush 1
      // b493: ldc2_w 5.4468361834451975E-9
      // b496: dastore
      // b497: aastore
      // b498: dup
      // b499: sipush 324
      // b49c: bipush 2
      // b49d: newarray 7
      // b49f: dup
      // b4a0: bipush 0
      // b4a1: ldc2_w 0.2749055027961731
      // b4a4: dastore
      // b4a5: dup
      // b4a6: bipush 1
      // b4a7: ldc2_w -1.692337384653611E-8
      // b4aa: dastore
      // b4ab: aastore
      // b4ac: dup
      // b4ad: sipush 325
      // b4b0: bipush 2
      // b4b1: newarray 7
      // b4b3: dup
      // b4b4: bipush 0
      // b4b5: ldc2_w 0.27564704418182373
      // b4b8: dastore
      // b4b9: dup
      // b4ba: bipush 1
      // b4bb: ldc2_w 6.426479056697412E-9
      // b4be: dastore
      // b4bf: aastore
      // b4c0: dup
      // b4c1: sipush 326
      // b4c4: bipush 2
      // b4c5: newarray 7
      // b4c7: dup
      // b4c8: bipush 0
      // b4c9: ldc2_w 0.2763880491256714
      // b4cc: dastore
      // b4cd: dup
      // b4ce: bipush 1
      // b4cf: ldc2_w 1.670735065191342E-8
      // b4d2: dastore
      // b4d3: aastore
      // b4d4: dup
      // b4d5: sipush 327
      // b4d8: bipush 2
      // b4d9: newarray 7
      // b4db: dup
      // b4dc: bipush 0
      // b4dd: ldc2_w 0.27712851762771606
      // b4e0: dastore
      // b4e1: dup
      // b4e2: bipush 1
      // b4e3: ldc2_w 1.4733029698334834E-8
      // b4e6: dastore
      // b4e7: aastore
      // b4e8: dup
      // b4e9: sipush 328
      // b4ec: bipush 2
      // b4ed: newarray 7
      // b4ef: dup
      // b4f0: bipush 0
      // b4f1: ldc2_w 0.27786844968795776
      // b4f4: dastore
      // b4f5: dup
      // b4f6: bipush 1
      // b4f7: ldc2_w 1.315498542514467E-9
      // b4fa: dastore
      // b4fb: aastore
      // b4fc: dup
      // b4fd: sipush 329
      // b500: bipush 2
      // b501: newarray 7
      // b503: dup
      // b504: bipush 0
      // b505: ldc2_w 0.2786078453063965
      // b508: dastore
      // b509: dup
      // b50a: bipush 1
      // b50b: ldc2_w -2.2735061539223372E-8
      // b50e: dastore
      // b50f: aastore
      // b510: dup
      // b511: sipush 330
      // b514: bipush 2
      // b515: newarray 7
      // b517: dup
      // b518: bipush 0
      // b519: ldc2_w 0.27934664487838745
      // b51c: dastore
      // b51d: dup
      // b51e: bipush 1
      // b51f: ldc2_w 2.994379757313727E-9
      // b522: dastore
      // b523: aastore
      // b524: dup
      // b525: sipush 331
      // b528: bipush 2
      // b529: newarray 7
      // b52b: dup
      // b52c: bipush 0
      // b52d: ldc2_w 0.28008490800857544
      // b530: dastore
      // b531: dup
      // b532: bipush 1
      // b533: ldc2_w 1.970577274107218E-8
      // b536: dastore
      // b537: aastore
      // b538: dup
      // b539: sipush 332
      // b53c: bipush 2
      // b53d: newarray 7
      // b53f: dup
      // b540: bipush 0
      // b541: ldc2_w 0.28082263469696045
      // b544: dastore
      // b545: dup
      // b546: bipush 1
      // b547: ldc2_w 2.820392733542077E-8
      // b54a: dastore
      // b54b: aastore
      // b54c: dup
      // b54d: sipush 333
      // b550: bipush 2
      // b551: newarray 7
      // b553: dup
      // b554: bipush 0
      // b555: ldc2_w 0.2815598249435425
      // b558: dastore
      // b559: dup
      // b55a: bipush 1
      // b55b: ldc2_w 2.929187356678173E-8
      // b55e: dastore
      // b55f: aastore
      // b560: dup
      // b561: sipush 334
      // b564: bipush 2
      // b565: newarray 7
      // b567: dup
      // b568: bipush 0
      // b569: ldc2_w 0.28229647874832153
      // b56c: dastore
      // b56d: dup
      // b56e: bipush 1
      // b56f: ldc2_w 2.377086680926386E-8
      // b572: dastore
      // b573: aastore
      // b574: dup
      // b575: sipush 335
      // b578: bipush 2
      // b579: newarray 7
      // b57b: dup
      // b57c: bipush 0
      // b57d: ldc2_w 0.2830325961112976
      // b580: dastore
      // b581: dup
      // b582: bipush 1
      // b583: ldc2_w 1.2440393009992529E-8
      // b586: dastore
      // b587: aastore
      // b588: dup
      // b589: sipush 336
      // b58c: bipush 2
      // b58d: newarray 7
      // b58f: dup
      // b590: bipush 0
      // b591: ldc2_w 0.2837681770324707
      // b594: dastore
      // b595: dup
      // b596: bipush 1
      // b597: ldc2_w -3.901826104778096E-9
      // b59a: dastore
      // b59b: aastore
      // b59c: dup
      // b59d: sipush 337
      // b5a0: bipush 2
      // b5a1: newarray 7
      // b5a3: dup
      // b5a4: bipush 0
      // b5a5: ldc2_w 0.2845032215118408
      // b5a8: dastore
      // b5a9: dup
      // b5aa: bipush 1
      // b5ab: ldc2_w -2.4459827842685974E-8
      // b5ae: dastore
      // b5af: aastore
      // b5b0: dup
      // b5b1: sipush 338
      // b5b4: bipush 2
      // b5b5: newarray 7
      // b5b7: dup
      // b5b8: bipush 0
      // b5b9: ldc2_w 0.2852376699447632
      // b5bc: dastore
      // b5bd: dup
      // b5be: bipush 1
      // b5bf: ldc2_w 1.1165241398059789E-8
      // b5c2: dastore
      // b5c3: aastore
      // b5c4: dup
      // b5c5: sipush 339
      // b5c8: bipush 2
      // b5c9: newarray 7
      // b5cb: dup
      // b5cc: bipush 0
      // b5cd: ldc2_w 0.28597164154052734
      // b5d0: dastore
      // b5d1: dup
      // b5d2: bipush 1
      // b5d3: ldc2_w -1.54434478239181E-8
      // b5d6: dastore
      // b5d7: aastore
      // b5d8: dup
      // b5d9: sipush 340
      // b5dc: bipush 2
      // b5dd: newarray 7
      // b5df: dup
      // b5e0: bipush 0
      // b5e1: ldc2_w 0.28670501708984375
      // b5e4: dastore
      // b5e5: dup
      // b5e6: bipush 1
      // b5e7: ldc2_w 1.5714110564653245E-8
      // b5ea: dastore
      // b5eb: aastore
      // b5ec: dup
      // b5ed: sipush 341
      // b5f0: bipush 2
      // b5f1: newarray 7
      // b5f3: dup
      // b5f4: bipush 0
      // b5f5: ldc2_w 0.28743791580200195
      // b5f8: dastore
      // b5f9: dup
      // b5fa: bipush 1
      // b5fb: ldc2_w -1.3782394940142479E-8
      // b5fe: dastore
      // b5ff: aastore
      // b600: dup
      // b601: sipush 342
      // b604: bipush 2
      // b605: newarray 7
      // b607: dup
      // b608: bipush 0
      // b609: ldc2_w 0.2881702184677124
      // b60c: dastore
      // b60d: dup
      // b60e: bipush 1
      // b60f: ldc2_w 1.6063569876284005E-8
      // b612: dastore
      // b613: aastore
      // b614: dup
      // b615: sipush 343
      // b618: bipush 2
      // b619: newarray 7
      // b61b: dup
      // b61c: bipush 0
      // b61d: ldc2_w 0.28890204429626465
      // b620: dastore
      // b621: dup
      // b622: bipush 1
      // b623: ldc2_w -1.317176818216125E-8
      // b626: dastore
      // b627: aastore
      // b628: dup
      // b629: sipush 344
      // b62c: bipush 2
      // b62d: newarray 7
      // b62f: dup
      // b630: bipush 0
      // b631: ldc2_w 0.28963327407836914
      // b634: dastore
      // b635: dup
      // b636: bipush 1
      // b637: ldc2_w 1.8504673536253893E-8
      // b63a: dastore
      // b63b: aastore
      // b63c: dup
      // b63d: sipush 345
      // b640: bipush 2
      // b641: newarray 7
      // b643: dup
      // b644: bipush 0
      // b645: ldc2_w 0.29036402702331543
      // b648: dastore
      // b649: dup
      // b64a: bipush 1
      // b64b: ldc2_w -7.334319635123628E-9
      // b64e: dastore
      // b64f: aastore
      // b650: dup
      // b651: sipush 346
      // b654: bipush 2
      // b655: newarray 7
      // b657: dup
      // b658: bipush 0
      // b659: ldc2_w 0.29109418392181396
      // b65c: dastore
      // b65d: dup
      // b65e: bipush 1
      // b65f: ldc2_w 2.9300903540317107E-8
      // b662: dastore
      // b663: aastore
      // b664: dup
      // b665: sipush 347
      // b668: bipush 2
      // b669: newarray 7
      // b66b: dup
      // b66c: bipush 0
      // b66d: ldc2_w 0.2918238639831543
      // b670: dastore
      // b671: dup
      // b672: bipush 1
      // b673: ldc2_w 9.979706999541057E-9
      // b676: dastore
      // b677: aastore
      // b678: dup
      // b679: sipush 348
      // b67c: bipush 2
      // b67d: newarray 7
      // b67f: dup
      // b680: bipush 0
      // b681: ldc2_w 0.29255300760269165
      // b684: dastore
      // b685: dup
      // b686: bipush 1
      // b687: ldc2_w -4.916314210412424E-9
      // b68a: dastore
      // b68b: aastore
      // b68c: dup
      // b68d: sipush 349
      // b690: bipush 2
      // b691: newarray 7
      // b693: dup
      // b694: bipush 0
      // b695: ldc2_w 0.293281614780426
      // b698: dastore
      // b699: dup
      // b69a: bipush 1
      // b69b: ldc2_w -1.4611908070155308E-8
      // b69e: dastore
      // b69f: aastore
      // b6a0: dup
      // b6a1: sipush 350
      // b6a4: bipush 2
      // b6a5: newarray 7
      // b6a7: dup
      // b6a8: bipush 0
      // b6a9: ldc2_w 0.2940096855163574
      // b6ac: dastore
      // b6ad: dup
      // b6ae: bipush 1
      // b6af: ldc2_w -1.833351586679361E-8
      // b6b2: dastore
      // b6b3: aastore
      // b6b4: dup
      // b6b5: sipush 351
      // b6b8: bipush 2
      // b6b9: newarray 7
      // b6bb: dup
      // b6bc: bipush 0
      // b6bd: ldc2_w 0.29473721981048584
      // b6c0: dastore
      // b6c1: dup
      // b6c2: bipush 1
      // b6c3: ldc2_w -1.530926726615185E-8
      // b6c6: dastore
      // b6c7: aastore
      // b6c8: dup
      // b6c9: sipush 352
      // b6cc: bipush 2
      // b6cd: newarray 7
      // b6cf: dup
      // b6d0: bipush 0
      // b6d1: ldc2_w 0.2954642176628113
      // b6d4: dastore
      // b6d5: dup
      // b6d6: bipush 1
      // b6d7: ldc2_w -4.7689754029101934E-9
      // b6da: dastore
      // b6db: aastore
      // b6dc: dup
      // b6dd: sipush 353
      // b6e0: bipush 2
      // b6e1: newarray 7
      // b6e3: dup
      // b6e4: bipush 0
      // b6e5: ldc2_w 0.29619067907333374
      // b6e8: dastore
      // b6e9: dup
      // b6ea: bipush 1
      // b6eb: ldc2_w 1.4055868011423819E-8
      // b6ee: dastore
      // b6ef: aastore
      // b6f0: dup
      // b6f1: sipush 354
      // b6f4: bipush 2
      // b6f5: newarray 7
      // b6f7: dup
      // b6f8: bipush 0
      // b6f9: ldc2_w 0.296916663646698
      // b6fc: dastore
      // b6fd: dup
      // b6fe: bipush 1
      // b6ff: ldc2_w -1.7672547212604003E-8
      // b702: dastore
      // b703: aastore
      // b704: dup
      // b705: sipush 355
      // b708: bipush 2
      // b709: newarray 7
      // b70b: dup
      // b70c: bipush 0
      // b70d: ldc2_w 0.2976420521736145
      // b710: dastore
      // b711: dup
      // b712: bipush 1
      // b713: ldc2_w 2.0020234215759705E-8
      // b716: dastore
      // b717: aastore
      // b718: dup
      // b719: sipush 356
      // b71c: bipush 2
      // b71d: newarray 7
      // b71f: dup
      // b720: bipush 0
      // b721: ldc2_w 0.2983669638633728
      // b724: dastore
      // b725: dup
      // b726: bipush 1
      // b727: ldc2_w 8.688424478730524E-9
      // b72a: dastore
      // b72b: aastore
      // b72c: dup
      // b72d: sipush 357
      // b730: bipush 2
      // b731: newarray 7
      // b733: dup
      // b734: bipush 0
      // b735: ldc2_w 0.2990913391113281
      // b738: dastore
      // b739: dup
      // b73a: bipush 1
      // b73b: ldc2_w 8.69851089918337E-9
      // b73e: dastore
      // b73f: aastore
      // b740: dup
      // b741: sipush 358
      // b744: bipush 2
      // b745: newarray 7
      // b747: dup
      // b748: bipush 0
      // b749: ldc2_w 0.29981517791748047
      // b74c: dastore
      // b74d: dup
      // b74e: bipush 1
      // b74f: ldc2_w 2.0810681643102672E-8
      // b752: dastore
      // b753: aastore
      // b754: dup
      // b755: sipush 359
      // b758: bipush 2
      // b759: newarray 7
      // b75b: dup
      // b75c: bipush 0
      // b75d: ldc2_w 0.3005385398864746
      // b760: dastore
      // b761: dup
      // b762: bipush 1
      // b763: ldc2_w -1.3821169493779352E-8
      // b766: dastore
      // b767: aastore
      // b768: dup
      // b769: sipush 360
      // b76c: bipush 2
      // b76d: newarray 7
      // b76f: dup
      // b770: bipush 0
      // b771: ldc2_w 0.301261305809021
      // b774: dastore
      // b775: dup
      // b776: bipush 1
      // b777: ldc2_w 2.4769140784919128E-8
      // b77a: dastore
      // b77b: aastore
      // b77c: dup
      // b77d: sipush 361
      // b780: bipush 2
      // b781: newarray 7
      // b783: dup
      // b784: bipush 0
      // b785: ldc2_w 0.3019835948944092
      // b788: dastore
      // b789: dup
      // b78a: bipush 1
      // b78b: ldc2_w 1.8127576600610336E-8
      // b78e: dastore
      // b78f: aastore
      // b790: dup
      // b791: sipush 362
      // b794: bipush 2
      // b795: newarray 7
      // b797: dup
      // b798: bipush 0
      // b799: ldc2_w 0.3027053475379944
      // b79c: dastore
      // b79d: dup
      // b79e: bipush 1
      // b79f: ldc2_w 2.6612401062437074E-8
      // b7a2: dastore
      // b7a3: aastore
      // b7a4: dup
      // b7a5: sipush 363
      // b7a8: bipush 2
      // b7a9: newarray 7
      // b7ab: dup
      // b7ac: bipush 0
      // b7ad: ldc2_w 0.3034266233444214
      // b7b0: dastore
      // b7b1: dup
      // b7b2: bipush 1
      // b7b3: ldc2_w -8.629042891789934E-9
      // b7b6: dastore
      // b7b7: aastore
      // b7b8: dup
      // b7b9: sipush 364
      // b7bc: bipush 2
      // b7bd: newarray 7
      // b7bf: dup
      // b7c0: bipush 0
      // b7c1: ldc2_w 0.3041473627090454
      // b7c4: dastore
      // b7c5: dup
      // b7c6: bipush 1
      // b7c7: ldc2_w -2.724174869314043E-8
      // b7ca: dastore
      // b7cb: aastore
      // b7cc: dup
      // b7cd: sipush 365
      // b7d0: bipush 2
      // b7d1: newarray 7
      // b7d3: dup
      // b7d4: bipush 0
      // b7d5: ldc2_w 0.30486756563186646
      // b7d8: dastore
      // b7d9: dup
      // b7da: bipush 1
      // b7db: ldc2_w -2.8476975783775358E-8
      // b7de: dastore
      // b7df: aastore
      // b7e0: dup
      // b7e1: sipush 366
      // b7e4: bipush 2
      // b7e5: newarray 7
      // b7e7: dup
      // b7e8: bipush 0
      // b7e9: ldc2_w 0.3055872321128845
      // b7ec: dastore
      // b7ed: dup
      // b7ee: bipush 1
      // b7ef: ldc2_w -1.1587600174449919E-8
      // b7f2: dastore
      // b7f3: aastore
      // b7f4: dup
      // b7f5: sipush 367
      // b7f8: bipush 2
      // b7f9: newarray 7
      // b7fb: dup
      // b7fc: bipush 0
      // b7fd: ldc2_w 0.3063063621520996
      // b800: dastore
      // b801: dup
      // b802: bipush 1
      // b803: ldc2_w 2.417189020581056E-8
      // b806: dastore
      // b807: aastore
      // b808: dup
      // b809: sipush 368
      // b80c: bipush 2
      // b80d: newarray 7
      // b80f: dup
      // b810: bipush 0
      // b811: ldc2_w 0.3070250153541565
      // b814: dastore
      // b815: dup
      // b816: bipush 1
      // b817: ldc2_w 1.99407553679345E-8
      // b81a: dastore
      // b81b: aastore
      // b81c: dup
      // b81d: sipush 369
      // b820: bipush 2
      // b821: newarray 7
      // b823: dup
      // b824: bipush 0
      // b825: ldc2_w 0.3077431917190552
      // b828: dastore
      // b829: dup
      // b82a: bipush 1
      // b82b: ldc2_w -2.35387025694381E-8
      // b82e: dastore
      // b82f: aastore
      // b830: dup
      // b831: sipush 370
      // b834: bipush 2
      // b835: newarray 7
      // b837: dup
      // b838: bipush 0
      // b839: ldc2_w 0.3084607720375061
      // b83c: dastore
      // b83d: dup
      // b83e: bipush 1
      // b83f: ldc2_w 1.3683509995845583E-8
      // b842: dastore
      // b843: aastore
      // b844: dup
      // b845: sipush 371
      // b848: bipush 2
      // b849: newarray 7
      // b84b: dup
      // b84c: bipush 0
      // b84d: ldc2_w 0.30917787551879883
      // b850: dastore
      // b851: dup
      // b852: bipush 1
      // b853: ldc2_w 1.3137214081023085E-8
      // b856: dastore
      // b857: aastore
      // b858: dup
      // b859: sipush 372
      // b85c: bipush 2
      // b85d: newarray 7
      // b85f: dup
      // b860: bipush 0
      // b861: ldc2_w 0.30989450216293335
      // b864: dastore
      // b865: dup
      // b866: bipush 1
      // b867: ldc2_w -2.444006866174775E-8
      // b86a: dastore
      // b86b: aastore
      // b86c: dup
      // b86d: sipush 373
      // b870: bipush 2
      // b871: newarray 7
      // b873: dup
      // b874: bipush 0
      // b875: ldc2_w 0.3106105327606201
      // b878: dastore
      // b879: dup
      // b87a: bipush 1
      // b87b: ldc2_w 2.0896888605749563E-8
      // b87e: dastore
      // b87f: aastore
      // b880: dup
      // b881: sipush 374
      // b884: bipush 2
      // b885: newarray 7
      // b887: dup
      // b888: bipush 0
      // b889: ldc2_w 0.31132614612579346
      // b88c: dastore
      // b88d: dup
      // b88e: bipush 1
      // b88f: ldc2_w -2.893149098508887E-8
      // b892: dastore
      // b893: aastore
      // b894: dup
      // b895: sipush 375
      // b898: bipush 2
      // b899: newarray 7
      // b89b: dup
      // b89c: bipush 0
      // b89d: ldc2_w 0.31204116344451904
      // b8a0: dastore
      // b8a1: dup
      // b8a2: bipush 1
      // b8a3: ldc2_w 5.621509038251498E-9
      // b8a6: dastore
      // b8a7: aastore
      // b8a8: dup
      // b8a9: sipush 376
      // b8ac: bipush 2
      // b8ad: newarray 7
      // b8af: dup
      // b8b0: bipush 0
      // b8b1: ldc2_w 0.3127557039260864
      // b8b4: dastore
      // b8b5: dup
      // b8b6: bipush 1
      // b8b7: ldc2_w 6.0778104626050015E-9
      // b8ba: dastore
      // b8bb: aastore
      // b8bc: dup
      // b8bd: sipush 377
      // b8c0: bipush 2
      // b8c1: newarray 7
      // b8c3: dup
      // b8c4: bipush 0
      // b8c5: ldc2_w 0.3134697675704956
      // b8c8: dastore
      // b8c9: dup
      // b8ca: bipush 1
      // b8cb: ldc2_w -2.6832941696716294E-8
      // b8ce: dastore
      // b8cf: aastore
      // b8d0: dup
      // b8d1: sipush 378
      // b8d4: bipush 2
      // b8d5: newarray 7
      // b8d7: dup
      // b8d8: bipush 0
      // b8d9: ldc2_w 0.31418323516845703
      // b8dc: dastore
      // b8dd: dup
      // b8de: bipush 1
      // b8df: ldc2_w 2.6826625274495256E-8
      // b8e2: dastore
      // b8e3: aastore
      // b8e4: dup
      // b8e5: sipush 379
      // b8e8: bipush 2
      // b8e9: newarray 7
      // b8eb: dup
      // b8ec: bipush 0
      // b8ed: ldc2_w 0.31489628553390503
      // b8f0: dastore
      // b8f1: dup
      // b8f2: bipush 1
      // b8f3: ldc2_w -1.1030897183911054E-8
      // b8f6: dastore
      // b8f7: aastore
      // b8f8: dup
      // b8f9: sipush 380
      // b8fc: bipush 2
      // b8fd: newarray 7
      // b8ff: dup
      // b900: bipush 0
      // b901: ldc2_w 0.31560879945755005
      // b904: dastore
      // b905: dup
      // b906: bipush 1
      // b907: ldc2_w -2.047124671392676E-8
      // b90a: dastore
      // b90b: aastore
      // b90c: dup
      // b90d: sipush 381
      // b910: bipush 2
      // b911: newarray 7
      // b913: dup
      // b914: bipush 0
      // b915: ldc2_w 0.3163207769393921
      // b918: dastore
      // b919: dup
      // b91a: bipush 1
      // b91b: ldc2_w -7.709990443086711E-10
      // b91e: dastore
      // b91f: aastore
      // b920: dup
      // b921: sipush 382
      // b924: bipush 2
      // b925: newarray 7
      // b927: dup
      // b928: bipush 0
      // b929: ldc2_w 0.3170322775840759
      // b92c: dastore
      // b92d: dup
      // b92e: bipush 1
      // b92f: ldc2_w -1.0812918808112342E-8
      // b932: dastore
      // b933: aastore
      // b934: dup
      // b935: sipush 383
      // b938: bipush 2
      // b939: newarray 7
      // b93b: dup
      // b93c: bipush 0
      // b93d: ldc2_w 0.3177432417869568
      // b940: dastore
      // b941: dup
      // b942: bipush 1
      // b943: ldc2_w 9.727979174888975E-9
      // b946: dastore
      // b947: aastore
      // b948: dup
      // b949: sipush 384
      // b94c: bipush 2
      // b94d: newarray 7
      // b94f: dup
      // b950: bipush 0
      // b951: ldc2_w 0.31845372915267944
      // b954: dastore
      // b955: dup
      // b956: bipush 1
      // b957: ldc2_w 1.9658551724508715E-9
      // b95a: dastore
      // b95b: aastore
      // b95c: dup
      // b95d: sipush 385
      // b960: bipush 2
      // b961: newarray 7
      // b963: dup
      // b964: bipush 0
      // b965: ldc2_w 0.3191636800765991
      // b968: dastore
      // b969: dup
      // b96a: bipush 1
      // b96b: ldc2_w 2.6222628001695826E-8
      // b96e: dastore
      // b96f: aastore
      // b970: dup
      // b971: sipush 386
      // b974: bipush 2
      // b975: newarray 7
      // b977: dup
      // b978: bipush 0
      // b979: ldc2_w 0.3198731541633606
      // b97c: dastore
      // b97d: dup
      // b97e: bipush 1
      // b97f: ldc2_w 2.3609400272358744E-8
      // b982: dastore
      // b983: aastore
      // b984: dup
      // b985: sipush 387
      // b988: bipush 2
      // b989: newarray 7
      // b98b: dup
      // b98c: bipush 0
      // b98d: ldc2_w 0.32058215141296387
      // b990: dastore
      // b991: dup
      // b992: bipush 1
      // b993: ldc2_w -5.159602957634814E-9
      // b996: dastore
      // b997: aastore
      // b998: dup
      // b999: sipush 388
      // b99c: bipush 2
      // b99d: newarray 7
      // b99f: dup
      // b9a0: bipush 0
      // b9a1: ldc2_w 0.32129061222076416
      // b9a4: dastore
      // b9a5: dup
      // b9a6: bipush 1
      // b9a7: ldc2_w 2.329701319016099E-10
      // b9aa: dastore
      // b9ab: aastore
      // b9ac: dup
      // b9ad: sipush 389
      // b9b0: bipush 2
      // b9b1: newarray 7
      // b9b3: dup
      // b9b4: bipush 0
      // b9b5: ldc2_w 0.32199859619140625
      // b9b8: dastore
      // b9b9: dup
      // b9ba: bipush 1
      // b9bb: ldc2_w -1.910633190395738E-8
      // b9be: dastore
      // b9bf: aastore
      // b9c0: dup
      // b9c1: sipush 390
      // b9c4: bipush 2
      // b9c5: newarray 7
      // b9c7: dup
      // b9c8: bipush 0
      // b9c9: ldc2_w 0.32270604372024536
      // b9cc: dastore
      // b9cd: dup
      // b9ce: bipush 1
      // b9cf: ldc2_w -2.863180390093667E-9
      // b9d2: dastore
      // b9d3: aastore
      // b9d4: dup
      // b9d5: sipush 391
      // b9d8: bipush 2
      // b9d9: newarray 7
      // b9db: dup
      // b9dc: bipush 0
      // b9dd: ldc2_w 0.32341301441192627
      // b9e0: dastore
      // b9e1: dup
      // b9e2: bipush 1
      // b9e3: ldc2_w -9.934041364456825E-9
      // b9e6: dastore
      // b9e7: aastore
      // b9e8: dup
      // b9e9: sipush 392
      // b9ec: bipush 2
      // b9ed: newarray 7
      // b9ef: dup
      // b9f0: bipush 0
      // b9f1: ldc2_w 0.3241194486618042
      // b9f4: dastore
      // b9f5: dup
      // b9f6: bipush 1
      // b9f7: ldc2_w 1.999240777687192E-8
      // b9fa: dastore
      // b9fb: aastore
      // b9fc: dup
      // b9fd: sipush 393
      // ba00: bipush 2
      // ba01: newarray 7
      // ba03: dup
      // ba04: bipush 0
      // ba05: ldc2_w 0.3248254060745239
      // ba08: dastore
      // ba09: dup
      // ba0a: bipush 1
      // ba0b: ldc2_w 2.801670341647724E-8
      // ba0e: dastore
      // ba0f: aastore
      // ba10: dup
      // ba11: sipush 394
      // ba14: bipush 2
      // ba15: newarray 7
      // ba17: dup
      // ba18: bipush 0
      // ba19: ldc2_w 0.32553088665008545
      // ba1c: dastore
      // ba1d: dup
      // ba1e: bipush 1
      // ba1f: ldc2_w 1.4842534265191358E-8
      // ba22: dastore
      // ba23: aastore
      // ba24: dup
      // ba25: sipush 395
      // ba28: bipush 2
      // ba29: newarray 7
      // ba2b: dup
      // ba2c: bipush 0
      // ba2d: ldc2_w 0.32623589038848877
      // ba30: dastore
      // ba31: dup
      // ba32: bipush 1
      // ba33: ldc2_w -1.882789920477354E-8
      // ba36: dastore
      // ba37: aastore
      // ba38: dup
      // ba39: sipush 396
      // ba3c: bipush 2
      // ba3d: newarray 7
      // ba3f: dup
      // ba40: bipush 0
      // ba41: ldc2_w 0.3269403576850891
      // ba44: dastore
      // ba45: dup
      // ba46: bipush 1
      // ba47: ldc2_w -1.268923579073577E-8
      // ba4a: dastore
      // ba4b: aastore
      // ba4c: dup
      // ba4d: sipush 397
      // ba50: bipush 2
      // ba51: newarray 7
      // ba53: dup
      // ba54: bipush 0
      // ba55: ldc2_w 0.32764434814453125
      // ba58: dastore
      // ba59: dup
      // ba5a: bipush 1
      // ba5b: ldc2_w -2.564688370677835E-8
      // ba5e: dastore
      // ba5f: aastore
      // ba60: dup
      // ba61: sipush 398
      // ba64: bipush 2
      // ba65: newarray 7
      // ba67: dup
      // ba68: bipush 0
      // ba69: ldc2_w 0.3283478021621704
      // ba6c: dastore
      // ba6d: dup
      // ba6e: bipush 1
      // ba6f: ldc2_w 2.6015626820520968E-9
      // ba72: dastore
      // ba73: aastore
      // ba74: dup
      // ba75: sipush 399
      // ba78: bipush 2
      // ba79: newarray 7
      // ba7b: dup
      // ba7c: bipush 0
      // ba7d: ldc2_w 0.32905077934265137
      // ba80: dastore
      // ba81: dup
      // ba82: bipush 1
      // ba83: ldc2_w 1.3147747907784344E-8
      // ba86: dastore
      // ba87: aastore
      // ba88: dup
      // ba89: sipush 400
      // ba8c: bipush 2
      // ba8d: newarray 7
      // ba8f: dup
      // ba90: bipush 0
      // ba91: ldc2_w 0.3297532796859741
      // ba94: dastore
      // ba95: dup
      // ba96: bipush 1
      // ba97: ldc2_w 6.686493860720675E-9
      // ba9a: dastore
      // ba9b: aastore
      // ba9c: dup
      // ba9d: sipush 401
      // baa0: bipush 2
      // baa1: newarray 7
      // baa3: dup
      // baa4: bipush 0
      // baa5: ldc2_w 0.33045530319213867
      // baa8: dastore
      // baa9: dup
      // baaa: bipush 1
      // baab: ldc2_w -1.608884086544153E-8
      // baae: dastore
      // baaf: aastore
      // bab0: dup
      // bab1: sipush 402
      // bab4: bipush 2
      // bab5: newarray 7
      // bab7: dup
      // bab8: bipush 0
      // bab9: ldc2_w 0.33115679025650024
      // babc: dastore
      // babd: dup
      // babe: bipush 1
      // babf: ldc2_w 5.118287907840204E-9
      // bac2: dastore
      // bac3: aastore
      // bac4: dup
      // bac5: sipush 403
      // bac8: bipush 2
      // bac9: newarray 7
      // bacb: dup
      // bacc: bipush 0
      // bacd: ldc2_w 0.3318578004837036
      // bad0: dastore
      // bad1: dup
      // bad2: bipush 1
      // bad3: ldc2_w 1.139367970944884E-8
      // bad6: dastore
      // bad7: aastore
      // bad8: dup
      // bad9: sipush 404
      // badc: bipush 2
      // badd: newarray 7
      // badf: dup
      // bae0: bipush 0
      // bae1: ldc2_w 0.3325583338737488
      // bae4: dastore
      // bae5: dup
      // bae6: bipush 1
      // bae7: ldc2_w 3.426327822115399E-9
      // baea: dastore
      // baeb: aastore
      // baec: dup
      // baed: sipush 405
      // baf0: bipush 2
      // baf1: newarray 7
      // baf3: dup
      // baf4: bipush 0
      // baf5: ldc2_w 0.33325839042663574
      // baf8: dastore
      // baf9: dup
      // bafa: bipush 1
      // bafb: ldc2_w -1.809622142990733E-8
      // bafe: dastore
      // baff: aastore
      // bb00: dup
      // bb01: sipush 406
      // bb04: bipush 2
      // bb05: newarray 7
      // bb07: dup
      // bb08: bipush 0
      // bb09: ldc2_w 0.3339579105377197
      // bb0c: dastore
      // bb0d: dup
      // bb0e: bipush 1
      // bb0f: ldc2_w 7.116780143398601E-9
      // bb12: dastore
      // bb13: aastore
      // bb14: dup
      // bb15: sipush 407
      // bb18: bipush 2
      // bb19: newarray 7
      // bb1b: dup
      // bb1c: bipush 0
      // bb1d: ldc2_w 0.3346569538116455
      // bb20: dastore
      // bb21: dup
      // bb22: bipush 1
      // bb23: ldc2_w 2.0145352306345386E-8
      // bb26: dastore
      // bb27: aastore
      // bb28: dup
      // bb29: sipush 408
      // bb2c: bipush 2
      // bb2d: newarray 7
      // bb2f: dup
      // bb30: bipush 0
      // bb31: ldc2_w 0.3353555202484131
      // bb34: dastore
      // bb35: dup
      // bb36: bipush 1
      // bb37: ldc2_w 2.167272474431968E-8
      // bb3a: dastore
      // bb3b: aastore
      // bb3c: dup
      // bb3d: sipush 409
      // bb40: bipush 2
      // bb41: newarray 7
      // bb43: dup
      // bb44: bipush 0
      // bb45: ldc2_w 0.33605360984802246
      // bb48: dastore
      // bb49: dup
      // bb4a: bipush 1
      // bb4b: ldc2_w 1.2380696294966822E-8
      // bb4e: dastore
      // bb4f: aastore
      // bb50: dup
      // bb51: sipush 410
      // bb54: bipush 2
      // bb55: newarray 7
      // bb57: dup
      // bb58: bipush 0
      // bb59: ldc2_w 0.33675122261047363
      // bb5c: dastore
      // bb5d: dup
      // bb5e: bipush 1
      // bb5f: ldc2_w -7.050361059209181E-9
      // bb62: dastore
      // bb63: aastore
      // bb64: dup
      // bb65: sipush 411
      // bb68: bipush 2
      // bb69: newarray 7
      // bb6b: dup
      // bb6c: bipush 0
      // bb6d: ldc2_w 0.3374482989311218
      // bb70: dastore
      // bb71: dup
      // bb72: bipush 1
      // bb73: ldc2_w 2.366314656322868E-8
      // bb76: dastore
      // bb77: aastore
      // bb78: dup
      // bb79: sipush 412
      // bb7c: bipush 2
      // bb7d: newarray 7
      // bb7f: dup
      // bb80: bipush 0
      // bb81: ldc2_w 0.3381449580192566
      // bb84: dastore
      // bb85: dup
      // bb86: bipush 1
      // bb87: ldc2_w -1.4010540194086646E-8
      // bb8a: dastore
      // bb8b: aastore
      // bb8c: dup
      // bb8d: sipush 413
      // bb90: bipush 2
      // bb91: newarray 7
      // bb93: dup
      // bb94: bipush 0
      // bb95: ldc2_w 0.3388410806655884
      // bb98: dastore
      // bb99: dup
      // bb9a: bipush 1
      // bb9b: ldc2_w -1.860165465666482E-10
      // bb9e: dastore
      // bb9f: aastore
      // bba0: dup
      // bba1: sipush 414
      // bba4: bipush 2
      // bba5: newarray 7
      // bba7: dup
      // bba8: bipush 0
      // bba9: ldc2_w 0.33953672647476196
      // bbac: dastore
      // bbad: dup
      // bbae: bipush 1
      // bbaf: ldc2_w 6.206776940880773E-9
      // bbb2: dastore
      // bbb3: aastore
      // bbb4: dup
      // bbb5: sipush 415
      // bbb8: bipush 2
      // bbb9: newarray 7
      // bbbb: dup
      // bbbc: bipush 0
      // bbbd: ldc2_w 0.34023189544677734
      // bbc0: dastore
      // bbc1: dup
      // bbc2: bipush 1
      // bbc3: ldc2_w 5.841137379010982E-9
      // bbc6: dastore
      // bbc7: aastore
      // bbc8: dup
      // bbc9: sipush 416
      // bbcc: bipush 2
      // bbcd: newarray 7
      // bbcf: dup
      // bbd0: bipush 0
      // bbd1: ldc2_w 0.3409265875816345
      // bbd4: dastore
      // bbd5: dup
      // bbd6: bipush 1
      // bbd7: ldc2_w -6.11041311179286E-10
      // bbda: dastore
      // bbdb: aastore
      // bbdc: dup
      // bbdd: sipush 417
      // bbe0: bipush 2
      // bbe1: newarray 7
      // bbe3: dup
      // bbe4: bipush 0
      // bbe5: ldc2_w 0.3416208028793335
      // bbe8: dastore
      // bbe9: dup
      // bbea: bipush 1
      // bbeb: ldc2_w -1.2479264502054702E-8
      // bbee: dastore
      // bbef: aastore
      // bbf0: dup
      // bbf1: sipush 418
      // bbf4: bipush 2
      // bbf5: newarray 7
      // bbf7: dup
      // bbf8: bipush 0
      // bbf9: ldc2_w 0.34231454133987427
      // bbfc: dastore
      // bbfd: dup
      // bbfe: bipush 1
      // bbff: ldc2_w -2.909443297645926E-8
      // bc02: dastore
      // bc03: aastore
      // bc04: dup
      // bc05: sipush 419
      // bc08: bipush 2
      // bc09: newarray 7
      // bc0b: dup
      // bc0c: bipush 0
      // bc0d: ldc2_w 0.34300774335861206
      // bc10: dastore
      // bc11: dup
      // bc12: bipush 1
      // bc13: ldc2_w 9.815805717097634E-9
      // bc16: dastore
      // bc17: aastore
      // bc18: dup
      // bc19: sipush 420
      // bc1c: bipush 2
      // bc1d: newarray 7
      // bc1f: dup
      // bc20: bipush 0
      // bc21: ldc2_w 0.3437005281448364
      // bc24: dastore
      // bc25: dup
      // bc26: bipush 1
      // bc27: ldc2_w -1.4291517981101049E-8
      // bc2a: dastore
      // bc2b: aastore
      // bc2c: dup
      // bc2d: sipush 421
      // bc30: bipush 2
      // bc31: newarray 7
      // bc33: dup
      // bc34: bipush 0
      // bc35: ldc2_w 0.3443927764892578
      // bc38: dastore
      // bc39: dup
      // bc3a: bipush 1
      // bc3b: ldc2_w 1.8457821628427503E-8
      // bc3e: dastore
      // bc3f: aastore
      // bc40: dup
      // bc41: sipush 422
      // bc44: bipush 2
      // bc45: newarray 7
      // bc47: dup
      // bc48: bipush 0
      // bc49: ldc2_w 0.34508460760116577
      // bc4c: dastore
      // bc4d: dup
      // bc4e: bipush 1
      // bc4f: ldc2_w -1.0481908869377813E-8
      // bc52: dastore
      // bc53: aastore
      // bc54: dup
      // bc55: sipush 423
      // bc58: bipush 2
      // bc59: newarray 7
      // bc5b: dup
      // bc5c: bipush 0
      // bc5d: ldc2_w 0.34577590227127075
      // bc60: dastore
      // bc61: dup
      // bc62: bipush 1
      // bc63: ldc2_w 1.876076001514746E-8
      // bc66: dastore
      // bc67: aastore
      // bc68: dup
      // bc69: sipush 424
      // bc6c: bipush 2
      // bc6d: newarray 7
      // bc6f: dup
      // bc70: bipush 0
      // bc71: ldc2_w 0.3464667797088623
      // bc74: dastore
      // bc75: dup
      // bc76: bipush 1
      // bc77: ldc2_w -1.2362653723769037E-8
      // bc7a: dastore
      // bc7b: aastore
      // bc7c: dup
      // bc7d: sipush 425
      // bc80: bipush 2
      // bc81: newarray 7
      // bc83: dup
      // bc84: bipush 0
      // bc85: ldc2_w 0.3471571207046509
      // bc88: dastore
      // bc89: dup
      // bc8a: bipush 1
      // bc8b: ldc2_w 1.6016578405624026E-8
      // bc8e: dastore
      // bc8f: aastore
      // bc90: dup
      // bc91: sipush 426
      // bc94: bipush 2
      // bc95: newarray 7
      // bc97: dup
      // bc98: bipush 0
      // bc99: ldc2_w 0.347847044467926
      // bc9c: dastore
      // bc9d: dup
      // bc9e: bipush 1
      // bc9f: ldc2_w -1.4652759033760925E-8
      // bca2: dastore
      // bca3: aastore
      // bca4: dup
      // bca5: sipush 427
      // bca8: bipush 2
      // bca9: newarray 7
      // bcab: dup
      // bcac: bipush 0
      // bcad: ldc2_w 0.3485364317893982
      // bcb0: dastore
      // bcb1: dup
      // bcb2: bipush 1
      // bcb3: ldc2_w 1.549533655901835E-8
      // bcb6: dastore
      // bcb7: aastore
      // bcb8: dup
      // bcb9: sipush 428
      // bcbc: bipush 2
      // bcbd: newarray 7
      // bcbf: dup
      // bcc0: bipush 0
      // bcc1: ldc2_w 0.34922540187835693
      // bcc4: dastore
      // bcc5: dup
      // bcc6: bipush 1
      // bcc7: ldc2_w -1.2093068629412478E-8
      // bcca: dastore
      // bccb: aastore
      // bccc: dup
      // bccd: sipush 429
      // bcd0: bipush 2
      // bcd1: newarray 7
      // bcd3: dup
      // bcd4: bipush 0
      // bcd5: ldc2_w 0.3499138355255127
      // bcd8: dastore
      // bcd9: dup
      // bcda: bipush 1
      // bcdb: ldc2_w 2.244531711424792E-8
      // bcde: dastore
      // bcdf: aastore
      // bce0: dup
      // bce1: sipush 430
      // bce4: bipush 2
      // bce5: newarray 7
      // bce7: dup
      // bce8: bipush 0
      // bce9: ldc2_w 0.35060185194015503
      // bcec: dastore
      // bced: dup
      // bcee: bipush 1
      // bcef: ldc2_w 5.538565518604807E-10
      // bcf2: dastore
      // bcf3: aastore
      // bcf4: dup
      // bcf5: sipush 431
      // bcf8: bipush 2
      // bcf9: newarray 7
      // bcfb: dup
      // bcfc: bipush 0
      // bcfd: ldc2_w 0.35128939151763916
      // bd00: dastore
      // bd01: dup
      // bd02: bipush 1
      // bd03: ldc2_w -1.7511499366215853E-8
      // bd06: dastore
      // bd07: aastore
      // bd08: dup
      // bd09: sipush 432
      // bd0c: bipush 2
      // bd0d: newarray 7
      // bd0f: dup
      // bd10: bipush 0
      // bd11: ldc2_w 0.3519763946533203
      // bd14: dastore
      // bd15: dup
      // bd16: bipush 1
      // bd17: ldc2_w 2.850385787215544E-8
      // bd1a: dastore
      // bd1b: aastore
      // bd1c: dup
      // bd1d: sipush 433
      // bd20: bipush 2
      // bd21: newarray 7
      // bd23: dup
      // bd24: bipush 0
      // bd25: ldc2_w 0.35266298055648804
      // bd28: dastore
      // bd29: dup
      // bd2a: bipush 1
      // bd2b: ldc2_w 2.003926370146842E-8
      // bd2e: dastore
      // bd2f: aastore
      // bd30: dup
      // bd31: sipush 434
      // bd34: bipush 2
      // bd35: newarray 7
      // bd37: dup
      // bd38: bipush 0
      // bd39: ldc2_w 0.35334908962249756
      // bd3c: dastore
      // bd3d: dup
      // bd3e: bipush 1
      // bd3f: ldc2_w 1.734665280502264E-8
      // bd42: dastore
      // bd43: aastore
      // bd44: dup
      // bd45: sipush 435
      // bd48: bipush 2
      // bd49: newarray 7
      // bd4b: dup
      // bd4c: bipush 0
      // bd4d: ldc2_w 0.3540347218513489
      // bd50: dastore
      // bd51: dup
      // bd52: bipush 1
      // bd53: ldc2_w 2.1071983674869414E-8
      // bd56: dastore
      // bd57: aastore
      // bd58: dup
      // bd59: sipush 436
      // bd5c: bipush 2
      // bd5d: newarray 7
      // bd5f: dup
      // bd60: bipush 0
      // bd61: ldc2_w 0.35471993684768677
      // bd64: dastore
      // bd65: dup
      // bd66: bipush 1
      // bd67: ldc2_w -2.774475773922311E-8
      // bd6a: dastore
      // bd6b: aastore
      // bd6c: dup
      // bd6d: sipush 437
      // bd70: bipush 2
      // bd71: newarray 7
      // bd73: dup
      // bd74: bipush 0
      // bd75: ldc2_w 0.3554046154022217
      // bd78: dastore
      // bd79: dup
      // bd7a: bipush 1
      // bd7b: ldc2_w -9.250975291734664E-9
      // bd7e: dastore
      // bd7f: aastore
      // bd80: dup
      // bd81: sipush 438
      // bd84: bipush 2
      // bd85: newarray 7
      // bd87: dup
      // bd88: bipush 0
      // bd89: ldc2_w 0.3560888171195984
      // bd8c: dastore
      // bd8d: dup
      // bd8e: bipush 1
      // bd8f: ldc2_w 1.7590672330295415E-8
      // bd92: dastore
      // bd93: aastore
      // bd94: dup
      // bd95: sipush 439
      // bd98: bipush 2
      // bd99: newarray 7
      // bd9b: dup
      // bd9c: bipush 0
      // bd9d: ldc2_w 0.35677260160446167
      // bda0: dastore
      // bda1: dup
      // bda2: bipush 1
      // bda3: ldc2_w -6.1837904549178745E-9
      // bda6: dastore
      // bda7: aastore
      // bda8: dup
      // bda9: sipush 440
      // bdac: bipush 2
      // bdad: newarray 7
      // bdaf: dup
      // bdb0: bipush 0
      // bdb1: ldc2_w 0.35745590925216675
      // bdb4: dastore
      // bdb5: dup
      // bdb6: bipush 1
      // bdb7: ldc2_w -2.0330362973820856E-8
      // bdba: dastore
      // bdbb: aastore
      // bdbc: dup
      // bdbd: sipush 441
      // bdc0: bipush 2
      // bdc1: newarray 7
      // bdc3: dup
      // bdc4: bipush 0
      // bdc5: ldc2_w 0.3581387400627136
      // bdc8: dastore
      // bdc9: dup
      // bdca: bipush 1
      // bdcb: ldc2_w -2.42109990366786E-8
      // bdce: dastore
      // bdcf: aastore
      // bdd0: dup
      // bdd1: sipush 442
      // bdd4: bipush 2
      // bdd5: newarray 7
      // bdd7: dup
      // bdd8: bipush 0
      // bdd9: ldc2_w 0.3588210940361023
      // bddc: dastore
      // bddd: dup
      // bdde: bipush 1
      // bddf: ldc2_w -1.7188958587407816E-8
      // bde2: dastore
      // bde3: aastore
      // bde4: dup
      // bde5: sipush 443
      // bde8: bipush 2
      // bde9: newarray 7
      // bdeb: dup
      // bdec: bipush 0
      // bded: ldc2_w 0.35950297117233276
      // bdf0: dastore
      // bdf1: dup
      // bdf2: bipush 1
      // bdf3: ldc2_w 1.3711958590112228E-9
      // bdf6: dastore
      // bdf7: aastore
      // bdf8: dup
      // bdf9: sipush 444
      // bdfc: bipush 2
      // bdfd: newarray 7
      // bdff: dup
      // be00: bipush 0
      // be01: ldc2_w 0.3601844310760498
      // be04: dastore
      // be05: dup
      // be06: bipush 1
      // be07: ldc2_w -2.7501042008405925E-8
      // be0a: dastore
      // be0b: aastore
      // be0c: dup
      // be0d: sipush 445
      // be10: bipush 2
      // be11: newarray 7
      // be13: dup
      // be14: bipush 0
      // be15: ldc2_w 0.36086535453796387
      // be18: dastore
      // be19: dup
      // be1a: bipush 1
      // be1b: ldc2_w 1.6036460343275798E-8
      // be1e: dastore
      // be1f: aastore
      // be20: dup
      // be21: sipush 446
      // be24: bipush 2
      // be25: newarray 7
      // be27: dup
      // be28: bipush 0
      // be29: ldc2_w 0.3615458607673645
      // be2c: dastore
      // be2d: dup
      // be2e: bipush 1
      // be2f: ldc2_w 1.3405964389498495E-8
      // be32: dastore
      // be33: aastore
      // be34: dup
      // be35: sipush 447
      // be38: bipush 2
      // be39: newarray 7
      // be3b: dup
      // be3c: bipush 0
      // be3d: ldc2_w 0.36222589015960693
      // be40: dastore
      // be41: dup
      // be42: bipush 1
      // be43: ldc2_w 2.484237749027735E-8
      // be46: dastore
      // be47: aastore
      // be48: dup
      // be49: sipush 448
      // be4c: bipush 2
      // be4d: newarray 7
      // be4f: dup
      // be50: bipush 0
      // be51: ldc2_w 0.36290550231933594
      // be54: dastore
      // be55: dup
      // be56: bipush 1
      // be57: ldc2_w -8.629967484362177E-9
      // be5a: dastore
      // be5b: aastore
      // be5c: dup
      // be5d: sipush 449
      // be60: bipush 2
      // be61: newarray 7
      // be63: dup
      // be64: bipush 0
      // be65: ldc2_w 0.36358463764190674
      // be68: dastore
      // be69: dup
      // be6a: bipush 1
      // be6b: ldc2_w -2.6778729562324134E-8
      // be6e: dastore
      // be6f: aastore
      // be70: dup
      // be71: sipush 450
      // be74: bipush 2
      // be75: newarray 7
      // be77: dup
      // be78: bipush 0
      // be79: ldc2_w 0.36426329612731934
      // be7c: dastore
      // be7d: dup
      // be7e: bipush 1
      // be7f: ldc2_w -2.8977490516960565E-8
      // be82: dastore
      // be83: aastore
      // be84: dup
      // be85: sipush 451
      // be88: bipush 2
      // be89: newarray 7
      // be8b: dup
      // be8c: bipush 0
      // be8d: ldc2_w 0.36494147777557373
      // be90: dastore
      // be91: dup
      // be92: bipush 1
      // be93: ldc2_w -1.4601106624823502E-8
      // be96: dastore
      // be97: aastore
      // be98: dup
      // be99: sipush 452
      // be9c: bipush 2
      // be9d: newarray 7
      // be9f: dup
      // bea0: bipush 0
      // bea1: ldc2_w 0.3656191825866699
      // bea4: dastore
      // bea5: dup
      // bea6: bipush 1
      // bea7: ldc2_w 1.69742947894444E-8
      // beaa: dastore
      // beab: aastore
      // beac: dup
      // bead: sipush 453
      // beb0: bipush 2
      // beb1: newarray 7
      // beb3: dup
      // beb4: bipush 0
      // beb5: ldc2_w 0.3662964701652527
      // beb8: dastore
      // beb9: dup
      // beba: bipush 1
      // bebb: ldc2_w 6.7666740211281175E-9
      // bebe: dastore
      // bebf: aastore
      // bec0: dup
      // bec1: sipush 454
      // bec4: bipush 2
      // bec5: newarray 7
      // bec7: dup
      // bec8: bipush 0
      // bec9: ldc2_w 0.36697328090667725
      // becc: dastore
      // becd: dup
      // bece: bipush 1
      // becf: ldc2_w 1.500201674336832E-8
      // bed2: dastore
      // bed3: aastore
      // bed4: dup
      // bed5: sipush 455
      // bed8: bipush 2
      // bed9: newarray 7
      // bedb: dup
      // bedc: bipush 0
      // bedd: ldc2_w 0.3676496744155884
      // bee0: dastore
      // bee1: dup
      // bee2: bipush 1
      // bee3: ldc2_w -1.730424167425052E-8
      // bee6: dastore
      // bee7: aastore
      // bee8: dup
      // bee9: sipush 456
      // beec: bipush 2
      // beed: newarray 7
      // beef: dup
      // bef0: bipush 0
      // bef1: ldc2_w 0.36832553148269653
      // bef4: dastore
      // bef5: dup
      // bef6: bipush 1
      // bef7: ldc2_w 2.9676011119845104E-8
      // befa: dastore
      // befb: aastore
      // befc: dup
      // befd: sipush 457
      // bf00: bipush 2
      // bf01: newarray 7
      // bf03: dup
      // bf04: bipush 0
      // bf05: ldc2_w 0.36900103092193604
      // bf08: dastore
      // bf09: dup
      // bf0a: bipush 1
      // bf0b: ldc2_w -2.2253590346826743E-8
      // bf0e: dastore
      // bf0f: aastore
      // bf10: dup
      // bf11: sipush 458
      // bf14: bipush 2
      // bf15: newarray 7
      // bf17: dup
      // bf18: bipush 0
      // bf19: ldc2_w 0.36967599391937256
      // bf1c: dastore
      // bf1d: dup
      // bf1e: bipush 1
      // bf1f: ldc2_w 6.3372065441089185E-9
      // bf22: dastore
      // bf23: aastore
      // bf24: dup
      // bf25: sipush 459
      // bf28: bipush 2
      // bf29: newarray 7
      // bf2b: dup
      // bf2c: bipush 0
      // bf2d: ldc2_w 0.37035053968429565
      // bf30: dastore
      // bf31: dup
      // bf32: bipush 1
      // bf33: ldc2_w -3.145816653215968E-9
      // bf36: dastore
      // bf37: aastore
      // bf38: dup
      // bf39: sipush 460
      // bf3c: bipush 2
      // bf3d: newarray 7
      // bf3f: dup
      // bf40: bipush 0
      // bf41: ldc2_w 0.37102460861206055
      // bf44: dastore
      // bf45: dup
      // bf46: bipush 1
      // bf47: ldc2_w 9.515812117036965E-9
      // bf4a: dastore
      // bf4b: aastore
      // bf4c: dup
      // bf4d: sipush 461
      // bf50: bipush 2
      // bf51: newarray 7
      // bf53: dup
      // bf54: bipush 0
      // bf55: ldc2_w 0.371698260307312
      // bf58: dastore
      // bf59: dup
      // bf5a: bipush 1
      // bf5b: ldc2_w -1.4669965113042639E-8
      // bf5e: dastore
      // bf5f: aastore
      // bf60: dup
      // bf61: sipush 462
      // bf64: bipush 2
      // bf65: newarray 7
      // bf67: dup
      // bf68: bipush 0
      // bf69: ldc2_w 0.3723714351654053
      // bf6c: dastore
      // bf6d: dup
      // bf6e: bipush 1
      // bf6f: ldc2_w -1.548715389333397E-8
      // bf72: dastore
      // bf73: aastore
      // bf74: dup
      // bf75: sipush 463
      // bf78: bipush 2
      // bf79: newarray 7
      // bf7b: dup
      // bf7c: bipush 0
      // bf7d: ldc2_w 0.37304413318634033
      // bf80: dastore
      // bf81: dup
      // bf82: bipush 1
      // bf83: ldc2_w 7.674361647125109E-9
      // bf86: dastore
      // bf87: aastore
      // bf88: dup
      // bf89: sipush 464
      // bf8c: bipush 2
      // bf8d: newarray 7
      // bf8f: dup
      // bf90: bipush 0
      // bf91: ldc2_w 0.37371641397476196
      // bf94: dastore
      // bf95: dup
      // bf96: bipush 1
      // bf97: ldc2_w -4.181177882069608E-9
      // bf9a: dastore
      // bf9b: aastore
      // bf9c: dup
      // bf9d: sipush 465
      // bfa0: bipush 2
      // bfa1: newarray 7
      // bfa3: dup
      // bfa4: bipush 0
      // bfa5: ldc2_w 0.3743882179260254
      // bfa8: dastore
      // bfa9: dup
      // bfaa: bipush 1
      // bfab: ldc2_w 9.158530500130718E-9
      // bfae: dastore
      // bfaf: aastore
      // bfb0: dup
      // bfb1: sipush 466
      // bfb4: bipush 2
      // bfb5: newarray 7
      // bfb7: dup
      // bfb8: bipush 0
      // bfb9: ldc2_w 0.3750596046447754
      // bfbc: dastore
      // bfbd: dup
      // bfbe: bipush 1
      // bfbf: ldc2_w -1.13047236597869E-8
      // bfc2: dastore
      // bfc3: aastore
      // bfc4: dup
      // bfc5: sipush 467
      // bfc8: bipush 2
      // bfc9: newarray 7
      // bfcb: dup
      // bfcc: bipush 0
      // bfcd: ldc2_w 0.3757305145263672
      // bfd0: dastore
      // bfd1: dup
      // bfd2: bipush 1
      // bfd3: ldc2_w -5.36108186384227E-9
      // bfd6: dastore
      // bfd7: aastore
      // bfd8: dup
      // bfd9: sipush 468
      // bfdc: bipush 2
      // bfdd: newarray 7
      // bfdf: dup
      // bfe0: bipush 0
      // bfe1: ldc2_w 0.3764009475708008
      // bfe4: dastore
      // bfe5: dup
      // bfe6: bipush 1
      // bfe7: ldc2_w 2.7593452284747873E-8
      // bfea: dastore
      // bfeb: aastore
      // bfec: dup
      // bfed: sipush 469
      // bff0: bipush 2
      // bff1: newarray 7
      // bff3: dup
      // bff4: bipush 0
      // bff5: ldc2_w 0.37707096338272095
      // bff8: dastore
      // bff9: dup
      // bffa: bipush 1
      // bffb: ldc2_w 2.8557016344085205E-8
      // bffe: dastore
      // bfff: aastore
      // c000: dup
      // c001: sipush 470
      // c004: bipush 2
      // c005: newarray 7
      // c007: dup
      // c008: bipush 0
      // c009: ldc2_w 0.3777405619621277
      // c00c: dastore
      // c00d: dup
      // c00e: bipush 1
      // c00f: ldc2_w -1.868818164036E-9
      // c012: dastore
      // c013: aastore
      // c014: dup
      // c015: sipush 471
      // c018: bipush 2
      // c019: newarray 7
      // c01b: dup
      // c01c: bipush 0
      // c01d: ldc2_w 0.3784096837043762
      // c020: dastore
      // c021: dup
      // c022: bipush 1
      // c023: ldc2_w -3.479042513414447E-9
      // c026: dastore
      // c027: aastore
      // c028: dup
      // c029: sipush 472
      // c02c: bipush 2
      // c02d: newarray 7
      // c02f: dup
      // c030: bipush 0
      // c031: ldc2_w 0.37907832860946655
      // c034: dastore
      // c035: dup
      // c036: bipush 1
      // c037: ldc2_w 2.432550290565648E-8
      // c03a: dastore
      // c03b: aastore
      // c03c: dup
      // c03d: sipush 473
      // c040: bipush 2
      // c041: newarray 7
      // c043: dup
      // c044: bipush 0
      // c045: ldc2_w 0.37974655628204346
      // c048: dastore
      // c049: dup
      // c04a: bipush 1
      // c04b: ldc2_w 2.2538131805476768E-8
      // c04e: dastore
      // c04f: aastore
      // c050: dup
      // c051: sipush 474
      // c054: bipush 2
      // c055: newarray 7
      // c057: dup
      // c058: bipush 0
      // c059: ldc2_w 0.38041436672210693
      // c05c: dastore
      // c05d: dup
      // c05e: bipush 1
      // c05f: ldc2_w -8.244395239939089E-9
      // c062: dastore
      // c063: aastore
      // c064: dup
      // c065: sipush 475
      // c068: bipush 2
      // c069: newarray 7
      // c06b: dup
      // c06c: bipush 0
      // c06d: ldc2_w 0.3810817003250122
      // c070: dastore
      // c071: dup
      // c072: bipush 1
      // c073: ldc2_w -7.821867597227376E-9
      // c076: dastore
      // c077: aastore
      // c078: dup
      // c079: sipush 476
      // c07c: bipush 2
      // c07d: newarray 7
      // c07f: dup
      // c080: bipush 0
      // c081: ldc2_w 0.3817485570907593
      // c084: dastore
      // c085: dup
      // c086: bipush 1
      // c087: ldc2_w 2.4400089062515914E-8
      // c08a: dastore
      // c08b: aastore
      // c08c: dup
      // c08d: sipush 477
      // c090: bipush 2
      // c091: newarray 7
      // c093: dup
      // c094: bipush 0
      // c095: ldc2_w 0.3824149966239929
      // c098: dastore
      // c099: dup
      // c09a: bipush 1
      // c09b: ldc2_w 2.9410015940087773E-8
      // c09e: dastore
      // c09f: aastore
      // c0a0: dup
      // c0a1: sipush 478
      // c0a4: bipush 2
      // c0a5: newarray 7
      // c0a7: dup
      // c0a8: bipush 0
      // c0a9: ldc2_w 0.38308101892471313
      // c0ac: dastore
      // c0ad: dup
      // c0ae: bipush 1
      // c0af: ldc2_w 7.799913824734797E-9
      // c0b2: dastore
      // c0b3: aastore
      // c0b4: dup
      // c0b5: sipush 479
      // c0b8: bipush 2
      // c0b9: newarray 7
      // c0bb: dup
      // c0bc: bipush 0
      // c0bd: ldc2_w 0.38374656438827515
      // c0c0: dastore
      // c0c1: dup
      // c0c2: bipush 1
      // c0c3: ldc2_w 1.976524624939355E-8
      // c0c6: dastore
      // c0c7: aastore
      // c0c8: dup
      // c0c9: sipush 480
      // c0cc: bipush 2
      // c0cd: newarray 7
      // c0cf: dup
      // c0d0: bipush 0
      // c0d1: ldc2_w 0.38441169261932373
      // c0d4: dastore
      // c0d5: dup
      // c0d6: bipush 1
      // c0d7: ldc2_w 6.291008309266035E-9
      // c0da: dastore
      // c0db: aastore
      // c0dc: dup
      // c0dd: sipush 481
      // c0e0: bipush 2
      // c0e1: newarray 7
      // c0e3: dup
      // c0e4: bipush 0
      // c0e5: ldc2_w 0.3850763440132141
      // c0e8: dastore
      // c0e9: dup
      // c0ea: bipush 1
      // c0eb: ldc2_w 2.757030889767851E-8
      // c0ee: dastore
      // c0ef: aastore
      // c0f0: dup
      // c0f1: sipush 482
      // c0f4: bipush 2
      // c0f5: newarray 7
      // c0f7: dup
      // c0f8: bipush 0
      // c0f9: ldc2_w 0.38574057817459106
      // c0fc: dastore
      // c0fd: dup
      // c0fe: bipush 1
      // c0ff: ldc2_w 2.4585794728405612E-8
      // c102: dastore
      // c103: aastore
      // c104: dup
      // c105: sipush 483
      // c108: bipush 2
      // c109: newarray 7
      // c10b: dup
      // c10c: bipush 0
      // c10d: ldc2_w 0.3864043951034546
      // c110: dastore
      // c111: dup
      // c112: bipush 1
      // c113: ldc2_w -2.0764122246389383E-9
      // c116: dastore
      // c117: aastore
      // c118: dup
      // c119: sipush 484
      // c11c: bipush 2
      // c11d: newarray 7
      // c11f: dup
      // c120: bipush 0
      // c121: ldc2_w 0.3870677351951599
      // c124: dastore
      // c125: dup
      // c126: bipush 1
      // c127: ldc2_w 7.77328837578952E-9
      // c12a: dastore
      // c12b: aastore
      // c12c: dup
      // c12d: sipush 485
      // c130: bipush 2
      // c131: newarray 7
      // c133: dup
      // c134: bipush 0
      // c135: ldc2_w 0.3877306580543518
      // c138: dastore
      // c139: dup
      // c13a: bipush 1
      // c13b: ldc2_w -4.8859560029989374E-9
      // c13e: dastore
      // c13f: aastore
      // c140: dup
      // c141: sipush 486
      // c144: bipush 2
      // c145: newarray 7
      // c147: dup
      // c148: bipush 0
      // c149: ldc2_w 0.3883931040763855
      // c14c: dastore
      // c14d: dup
      // c14e: bipush 1
      // c14f: ldc2_w 2.0133131420595028E-8
      // c152: dastore
      // c153: aastore
      // c154: dup
      // c155: sipush 487
      // c158: bipush 2
      // c159: newarray 7
      // c15b: dup
      // c15c: bipush 0
      // c15d: ldc2_w 0.38905513286590576
      // c160: dastore
      // c161: dup
      // c162: bipush 1
      // c163: ldc2_w 2.380738071335498E-8
      // c166: dastore
      // c167: aastore
      // c168: dup
      // c169: sipush 488
      // c16c: bipush 2
      // c16d: newarray 7
      // c16f: dup
      // c170: bipush 0
      // c171: ldc2_w 0.3897167444229126
      // c174: dastore
      // c175: dup
      // c176: bipush 1
      // c177: ldc2_w 6.7171126157142075E-9
      // c17a: dastore
      // c17b: aastore
      // c17c: dup
      // c17d: sipush 489
      // c180: bipush 2
      // c181: newarray 7
      // c183: dup
      // c184: bipush 0
      // c185: ldc2_w 0.39037787914276123
      // c188: dastore
      // c189: dup
      // c18a: bipush 1
      // c18b: ldc2_w 2.9046141593926277E-8
      // c18e: dastore
      // c18f: aastore
      // c190: dup
      // c191: sipush 490
      // c194: bipush 2
      // c195: newarray 7
      // c197: dup
      // c198: bipush 0
      // c199: ldc2_w 0.3910386562347412
      // c19c: dastore
      // c19d: dup
      // c19e: bipush 1
      // c19f: ldc2_w -2.7836800219410262E-8
      // c1a2: dastore
      // c1a3: aastore
      // c1a4: dup
      // c1a5: sipush 491
      // c1a8: bipush 2
      // c1a9: newarray 7
      // c1ab: dup
      // c1ac: bipush 0
      // c1ad: ldc2_w 0.3916988968849182
      // c1b0: dastore
      // c1b1: dup
      // c1b2: bipush 1
      // c1b3: ldc2_w 1.545909820981726E-8
      // c1b6: dastore
      // c1b7: aastore
      // c1b8: dup
      // c1b9: sipush 492
      // c1bc: bipush 2
      // c1bd: newarray 7
      // c1bf: dup
      // c1c0: bipush 0
      // c1c1: ldc2_w 0.39235877990722656
      // c1c4: dastore
      // c1c5: dup
      // c1c6: bipush 1
      // c1c7: ldc2_w -1.930436269002062E-8
      // c1ca: dastore
      // c1cb: aastore
      // c1cc: dup
      // c1cd: sipush 493
      // c1d0: bipush 2
      // c1d1: newarray 7
      // c1d3: dup
      // c1d4: bipush 0
      // c1d5: ldc2_w 0.3930181860923767
      // c1d8: dastore
      // c1d9: dup
      // c1da: bipush 1
      // c1db: ldc2_w -1.2343297554921835E-8
      // c1de: dastore
      // c1df: aastore
      // c1e0: dup
      // c1e1: sipush 494
      // c1e4: bipush 2
      // c1e5: newarray 7
      // c1e7: dup
      // c1e8: bipush 0
      // c1e9: ldc2_w 0.3936771750450134
      // c1ec: dastore
      // c1ed: dup
      // c1ee: bipush 1
      // c1ef: ldc2_w -2.268889128622553E-8
      // c1f2: dastore
      // c1f3: aastore
      // c1f4: dup
      // c1f5: sipush 495
      // c1f8: bipush 2
      // c1f9: newarray 7
      // c1fb: dup
      // c1fc: bipush 0
      // c1fd: ldc2_w 0.39433568716049194
      // c200: dastore
      // c201: dup
      // c202: bipush 1
      // c203: ldc2_w 9.835827818608177E-9
      // c206: dastore
      // c207: aastore
      // c208: dup
      // c209: sipush 496
      // c20c: bipush 2
      // c20d: newarray 7
      // c20f: dup
      // c210: bipush 0
      // c211: ldc2_w 0.39499378204345703
      // c214: dastore
      // c215: dup
      // c216: bipush 1
      // c217: ldc2_w 2.6197411946856397E-8
      // c21a: dastore
      // c21b: aastore
      // c21c: dup
      // c21d: sipush 497
      // c220: bipush 2
      // c221: newarray 7
      // c223: dup
      // c224: bipush 0
      // c225: ldc2_w 0.3956514596939087
      // c228: dastore
      // c229: dup
      // c22a: bipush 1
      // c22b: ldc2_w 2.6965931069318893E-8
      // c22e: dastore
      // c22f: aastore
      // c230: dup
      // c231: sipush 498
      // c234: bipush 2
      // c235: newarray 7
      // c237: dup
      // c238: bipush 0
      // c239: ldc2_w 0.3963087201118469
      // c23c: dastore
      // c23d: dup
      // c23e: bipush 1
      // c23f: ldc2_w 1.2710331127772166E-8
      // c242: dastore
      // c243: aastore
      // c244: dup
      // c245: sipush 499
      // c248: bipush 2
      // c249: newarray 7
      // c24b: dup
      // c24c: bipush 0
      // c24d: ldc2_w 0.39696556329727173
      // c250: dastore
      // c251: dup
      // c252: bipush 1
      // c253: ldc2_w -1.6001563011916016E-8
      // c256: dastore
      // c257: aastore
      // c258: dup
      // c259: sipush 500
      // c25c: bipush 2
      // c25d: newarray 7
      // c25f: dup
      // c260: bipush 0
      // c261: ldc2_w 0.39762192964553833
      // c264: dastore
      // c265: dup
      // c266: bipush 1
      // c267: ldc2_w 1.0016001590267064E-9
      // c26a: dastore
      // c26b: aastore
      // c26c: dup
      // c26d: sipush 501
      // c270: bipush 2
      // c271: newarray 7
      // c273: dup
      // c274: bipush 0
      // c275: ldc2_w 0.3982778787612915
      // c278: dastore
      // c279: dup
      // c27a: bipush 1
      // c27b: ldc2_w 4.680767399874334E-9
      // c27e: dastore
      // c27f: aastore
      // c280: dup
      // c281: sipush 502
      // c284: bipush 2
      // c285: newarray 7
      // c287: dup
      // c288: bipush 0
      // c289: ldc2_w 0.39893341064453125
      // c28c: dastore
      // c28d: dup
      // c28e: bipush 1
      // c28f: ldc2_w -4.399582029272418E-9
      // c292: dastore
      // c293: aastore
      // c294: dup
      // c295: sipush 503
      // c298: bipush 2
      // c299: newarray 7
      // c29b: dup
      // c29c: bipush 0
      // c29d: ldc2_w 0.39958852529525757
      // c2a0: dastore
      // c2a1: dup
      // c2a2: bipush 1
      // c2a3: ldc2_w -2.5676078228301587E-8
      // c2a6: dastore
      // c2a7: aastore
      // c2a8: dup
      // c2a9: sipush 504
      // c2ac: bipush 2
      // c2ad: newarray 7
      // c2af: dup
      // c2b0: bipush 0
      // c2b1: ldc2_w 0.4002431631088257
      // c2b4: dastore
      // c2b5: dup
      // c2b6: bipush 1
      // c2b7: ldc2_w 1.0181870233355787E-9
      // c2ba: dastore
      // c2bb: aastore
      // c2bc: dup
      // c2bd: sipush 505
      // c2c0: bipush 2
      // c2c1: newarray 7
      // c2c3: dup
      // c2c4: bipush 0
      // c2c5: ldc2_w 0.40089738368988037
      // c2c8: dastore
      // c2c9: dup
      // c2ca: bipush 1
      // c2cb: ldc2_w 1.6639728835984655E-8
      // c2ce: dastore
      // c2cf: aastore
      // c2d0: dup
      // c2d1: sipush 506
      // c2d4: bipush 2
      // c2d5: newarray 7
      // c2d7: dup
      // c2d8: bipush 0
      // c2d9: ldc2_w 0.40155118703842163
      // c2dc: dastore
      // c2dd: dup
      // c2de: bipush 1
      // c2df: ldc2_w 2.174860642202632E-8
      // c2e2: dastore
      // c2e3: aastore
      // c2e4: dup
      // c2e5: sipush 507
      // c2e8: bipush 2
      // c2e9: newarray 7
      // c2eb: dup
      // c2ec: bipush 0
      // c2ed: ldc2_w 0.40220457315444946
      // c2f0: dastore
      // c2f1: dup
      // c2f2: bipush 1
      // c2f3: ldc2_w 1.6903781197123503E-8
      // c2f6: dastore
      // c2f7: aastore
      // c2f8: dup
      // c2f9: sipush 508
      // c2fc: bipush 2
      // c2fd: newarray 7
      // c2ff: dup
      // c300: bipush 0
      // c301: ldc2_w 0.40285754203796387
      // c304: dastore
      // c305: dup
      // c306: bipush 1
      // c307: ldc2_w 2.663119647467697E-9
      // c30a: dastore
      // c30b: aastore
      // c30c: dup
      // c30d: sipush 509
      // c310: bipush 2
      // c311: newarray 7
      // c313: dup
      // c314: bipush 0
      // c315: ldc2_w 0.40351009368896484
      // c318: dastore
      // c319: dup
      // c31a: bipush 1
      // c31b: ldc2_w -2.0416603812329616E-8
      // c31e: dastore
      // c31f: aastore
      // c320: dup
      // c321: sipush 510
      // c324: bipush 2
      // c325: newarray 7
      // c327: dup
      // c328: bipush 0
      // c329: ldc2_w 0.4041621685028076
      // c32c: dastore
      // c32d: dup
      // c32e: bipush 1
      // c32f: ldc2_w 7.82494078472695E-9
      // c332: dastore
      // c333: aastore
      // c334: dup
      // c335: sipush 511
      // c338: bipush 2
      // c339: newarray 7
      // c33b: dup
      // c33c: bipush 0
      // c33d: ldc2_w 0.40481382608413696
      // c340: dastore
      // c341: dup
      // c342: bipush 1
      // c343: ldc2_w 2.833770747113627E-8
      // c346: dastore
      // c347: aastore
      // c348: dup
      // c349: sipush 512
      // c34c: bipush 2
      // c34d: newarray 7
      // c34f: dup
      // c350: bipush 0
      // c351: ldc2_w 0.40546512603759766
      // c354: dastore
      // c355: dup
      // c356: bipush 1
      // c357: ldc2_w -1.7929433274271985E-8
      // c35a: dastore
      // c35b: aastore
      // c35c: dup
      // c35d: sipush 513
      // c360: bipush 2
      // c361: newarray 7
      // c363: dup
      // c364: bipush 0
      // c365: ldc2_w 0.40611594915390015
      // c368: dastore
      // c369: dup
      // c36a: bipush 1
      // c36b: ldc2_w -1.1214757379328965E-8
      // c36e: dastore
      // c36f: aastore
      // c370: dup
      // c371: sipush 514
      // c374: bipush 2
      // c375: newarray 7
      // c377: dup
      // c378: bipush 0
      // c379: ldc2_w 0.4067663550376892
      // c37c: dastore
      // c37d: dup
      // c37e: bipush 1
      // c37f: ldc2_w -1.0571553019207106E-8
      // c382: dastore
      // c383: aastore
      // c384: dup
      // c385: sipush 515
      // c388: bipush 2
      // c389: newarray 7
      // c38b: dup
      // c38c: bipush 0
      // c38d: ldc2_w 0.40741634368896484
      // c390: dastore
      // c391: dup
      // c392: bipush 1
      // c393: ldc2_w -1.5449538712332313E-8
      // c396: dastore
      // c397: aastore
      // c398: dup
      // c399: sipush 516
      // c39c: bipush 2
      // c39d: newarray 7
      // c39f: dup
      // c3a0: bipush 0
      // c3a1: ldc2_w 0.40806591510772705
      // c3a4: dastore
      // c3a5: dup
      // c3a6: bipush 1
      // c3a7: ldc2_w -2.529950530235105E-8
      // c3aa: dastore
      // c3ab: aastore
      // c3ac: dup
      // c3ad: sipush 517
      // c3b0: bipush 2
      // c3b1: newarray 7
      // c3b3: dup
      // c3b4: bipush 0
      // c3b5: ldc2_w 0.40871500968933105
      // c3b8: dastore
      // c3b9: dup
      // c3ba: bipush 1
      // c3bb: ldc2_w 2.0031331601617008E-8
      // c3be: dastore
      // c3bf: aastore
      // c3c0: dup
      // c3c1: sipush 518
      // c3c4: bipush 2
      // c3c5: newarray 7
      // c3c7: dup
      // c3c8: bipush 0
      // c3c9: ldc2_w 0.4093637466430664
      // c3cc: dastore
      // c3cd: dup
      // c3ce: bipush 1
      // c3cf: ldc2_w 1.880755298741952E-9
      // c3d2: dastore
      // c3d3: aastore
      // c3d4: dup
      // c3d5: sipush 519
      // c3d8: bipush 2
      // c3d9: newarray 7
      // c3db: dup
      // c3dc: bipush 0
      // c3dd: ldc2_w 0.41001206636428833
      // c3e0: dastore
      // c3e1: dup
      // c3e2: bipush 1
      // c3e3: ldc2_w -1.9600580584843318E-8
      // c3e6: dastore
      // c3e7: aastore
      // c3e8: dup
      // c3e9: sipush 520
      // c3ec: bipush 2
      // c3ed: newarray 7
      // c3ef: dup
      // c3f0: bipush 0
      // c3f1: ldc2_w 0.41065990924835205
      // c3f4: dastore
      // c3f5: dup
      // c3f6: bipush 1
      // c3f7: ldc2_w 1.573691633515306E-8
      // c3fa: dastore
      // c3fb: aastore
      // c3fc: dup
      // c3fd: sipush 521
      // c400: bipush 2
      // c401: newarray 7
      // c403: dup
      // c404: bipush 0
      // c405: ldc2_w 0.4113073945045471
      // c408: dastore
      // c409: dup
      // c40a: bipush 1
      // c40b: ldc2_w -1.0772154376548336E-8
      // c40e: dastore
      // c40f: aastore
      // c410: dup
      // c411: sipush 522
      // c414: bipush 2
      // c415: newarray 7
      // c417: dup
      // c418: bipush 0
      // c419: ldc2_w 0.411954402923584
      // c41c: dastore
      // c41d: dup
      // c41e: bipush 1
      // c41f: ldc2_w 2.0624330192486066E-8
      // c422: dastore
      // c423: aastore
      // c424: dup
      // c425: sipush 523
      // c428: bipush 2
      // c429: newarray 7
      // c42b: dup
      // c42c: bipush 0
      // c42d: ldc2_w 0.4126010537147522
      // c430: dastore
      // c431: dup
      // c432: bipush 1
      // c433: ldc2_w -8.741139170029572E-9
      // c436: dastore
      // c437: aastore
      // c438: dup
      // c439: sipush 524
      // c43c: bipush 2
      // c43d: newarray 7
      // c43f: dup
      // c440: bipush 0
      // c441: ldc2_w 0.4132472276687622
      // c444: dastore
      // c445: dup
      // c446: bipush 1
      // c447: ldc2_w 2.0881457123894216E-8
      // c44a: dastore
      // c44b: aastore
      // c44c: dup
      // c44d: sipush 525
      // c450: bipush 2
      // c451: newarray 7
      // c453: dup
      // c454: bipush 0
      // c455: ldc2_w 0.41389304399490356
      // c458: dastore
      // c459: dup
      // c45a: bipush 1
      // c45b: ldc2_w -9.177488027521808E-9
      // c45e: dastore
      // c45f: aastore
      // c460: dup
      // c461: sipush 526
      // c464: bipush 2
      // c465: newarray 7
      // c467: dup
      // c468: bipush 0
      // c469: ldc2_w 0.4145383834838867
      // c46c: dastore
      // c46d: dup
      // c46e: bipush 1
      // c46f: ldc2_w 2.0829952491625585E-8
      // c472: dastore
      // c473: aastore
      // c474: dup
      // c475: sipush 527
      // c478: bipush 2
      // c479: newarray 7
      // c47b: dup
      // c47c: bipush 0
      // c47d: ldc2_w 0.4151833653450012
      // c480: dastore
      // c481: dup
      // c482: bipush 1
      // c483: ldc2_w -7.767915492597301E-9
      // c486: dastore
      // c487: aastore
      // c488: dup
      // c489: sipush 528
      // c48c: bipush 2
      // c48d: newarray 7
      // c48f: dup
      // c490: bipush 0
      // c491: ldc2_w 0.4158278703689575
      // c494: dastore
      // c495: dup
      // c496: bipush 1
      // c497: ldc2_w 2.4774753446082082E-8
      // c49a: dastore
      // c49b: aastore
      // c49c: dup
      // c49d: sipush 529
      // c4a0: bipush 2
      // c4a1: newarray 7
      // c4a3: dup
      // c4a4: bipush 0
      // c4a5: ldc2_w 0.41647201776504517
      // c4a8: dastore
      // c4a9: dup
      // c4aa: bipush 1
      // c4ab: ldc2_w -2.1581119071750435E-10
      // c4ae: dastore
      // c4af: aastore
      // c4b0: dup
      // c4b1: sipush 530
      // c4b4: bipush 2
      // c4b5: newarray 7
      // c4b7: dup
      // c4b8: bipush 0
      // c4b9: ldc2_w 0.4171157479286194
      // c4bc: dastore
      // c4bd: dup
      // c4be: bipush 1
      // c4bf: ldc2_w -2.260047972865202E-8
      // c4c2: dastore
      // c4c3: aastore
      // c4c4: dup
      // c4c5: sipush 531
      // c4c8: bipush 2
      // c4c9: newarray 7
      // c4cb: dup
      // c4cc: bipush 0
      // c4cd: ldc2_w 0.4177590012550354
      // c4d0: dastore
      // c4d1: dup
      // c4d2: bipush 1
      // c4d3: ldc2_w 1.775884601423381E-8
      // c4d6: dastore
      // c4d7: aastore
      // c4d8: dup
      // c4d9: sipush 532
      // c4dc: bipush 2
      // c4dd: newarray 7
      // c4df: dup
      // c4e0: bipush 0
      // c4e1: ldc2_w 0.41840189695358276
      // c4e4: dastore
      // c4e5: dup
      // c4e6: bipush 1
      // c4e7: ldc2_w 2.185301053838889E-9
      // c4ea: dastore
      // c4eb: aastore
      // c4ec: dup
      // c4ed: sipush 533
      // c4f0: bipush 2
      // c4f1: newarray 7
      // c4f3: dup
      // c4f4: bipush 0
      // c4f5: ldc2_w 0.4190443754196167
      // c4f8: dastore
      // c4f9: dup
      // c4fa: bipush 1
      // c4fb: ldc2_w -9.185071463667081E-9
      // c4fe: dastore
      // c4ff: aastore
      // c500: dup
      // c501: sipush 534
      // c504: bipush 2
      // c505: newarray 7
      // c507: dup
      // c508: bipush 0
      // c509: ldc2_w 0.4196864366531372
      // c50c: dastore
      // c50d: dup
      // c50e: bipush 1
      // c50f: ldc2_w -1.5821896727910552E-8
      // c512: dastore
      // c513: aastore
      // c514: dup
      // c515: sipush 535
      // c518: bipush 2
      // c519: newarray 7
      // c51b: dup
      // c51c: bipush 0
      // c51d: ldc2_w 0.4203280806541443
      // c520: dastore
      // c521: dup
      // c522: bipush 1
      // c523: ldc2_w -1.719582086188318E-8
      // c526: dastore
      // c527: aastore
      // c528: dup
      // c529: sipush 536
      // c52c: bipush 2
      // c52d: newarray 7
      // c52f: dup
      // c530: bipush 0
      // c531: ldc2_w 0.42096930742263794
      // c534: dastore
      // c535: dup
      // c536: bipush 1
      // c537: ldc2_w -1.2778508303324259E-8
      // c53a: dastore
      // c53b: aastore
      // c53c: dup
      // c53d: sipush 537
      // c540: bipush 2
      // c541: newarray 7
      // c543: dup
      // c544: bipush 0
      // c545: ldc2_w 0.42161011695861816
      // c548: dastore
      // c549: dup
      // c54a: bipush 1
      // c54b: ldc2_w -2.042639194493364E-9
      // c54e: dastore
      // c54f: aastore
      // c550: dup
      // c551: sipush 538
      // c554: bipush 2
      // c555: newarray 7
      // c557: dup
      // c558: bipush 0
      // c559: ldc2_w 0.42225050926208496
      // c55c: dastore
      // c55d: dup
      // c55e: bipush 1
      // c55f: ldc2_w 1.5538093219698803E-8
      // c562: dastore
      // c563: aastore
      // c564: dup
      // c565: sipush 539
      // c568: bipush 2
      // c569: newarray 7
      // c56b: dup
      // c56c: bipush 0
      // c56d: ldc2_w 0.4228905439376831
      // c570: dastore
      // c571: dup
      // c572: bipush 1
      // c573: ldc2_w -1.9115659590156936E-8
      // c576: dastore
      // c577: aastore
      // c578: dup
      // c579: sipush 540
      // c57c: bipush 2
      // c57d: newarray 7
      // c57f: dup
      // c580: bipush 0
      // c581: ldc2_w 0.42353010177612305
      // c584: dastore
      // c585: dup
      // c586: bipush 1
      // c587: ldc2_w 1.3729680248843432E-8
      // c58a: dastore
      // c58b: aastore
      // c58c: dup
      // c58d: sipush 541
      // c590: bipush 2
      // c591: newarray 7
      // c593: dup
      // c594: bipush 0
      // c595: ldc2_w 0.42416930198669434
      // c598: dastore
      // c599: dup
      // c59a: bipush 1
      // c59b: ldc2_w -4.611893838830296E-9
      // c59e: dastore
      // c59f: aastore
      // c5a0: dup
      // c5a1: sipush 542
      // c5a4: bipush 2
      // c5a5: newarray 7
      // c5a7: dup
      // c5a8: bipush 0
      // c5a9: ldc2_w 0.4248080849647522
      // c5ac: dastore
      // c5ad: dup
      // c5ae: bipush 1
      // c5af: ldc2_w -1.4013456880651706E-8
      // c5b2: dastore
      // c5b3: aastore
      // c5b4: dup
      // c5b5: sipush 543
      // c5b8: bipush 2
      // c5b9: newarray 7
      // c5bb: dup
      // c5bc: bipush 0
      // c5bd: ldc2_w 0.42544645071029663
      // c5c0: dastore
      // c5c1: dup
      // c5c2: bipush 1
      // c5c3: ldc2_w -1.3953728897042917E-8
      // c5c6: dastore
      // c5c7: aastore
      // c5c8: dup
      // c5c9: sipush 544
      // c5cc: bipush 2
      // c5cd: newarray 7
      // c5cf: dup
      // c5d0: bipush 0
      // c5d1: ldc2_w 0.42608439922332764
      // c5d4: dastore
      // c5d5: dup
      // c5d6: bipush 1
      // c5d7: ldc2_w -3.912427573594197E-9
      // c5da: dastore
      // c5db: aastore
      // c5dc: dup
      // c5dd: sipush 545
      // c5e0: bipush 2
      // c5e1: newarray 7
      // c5e3: dup
      // c5e4: bipush 0
      // c5e5: ldc2_w 0.4267219305038452
      // c5e8: dastore
      // c5e9: dup
      // c5ea: bipush 1
      // c5eb: ldc2_w 1.6629734283189315E-8
      // c5ee: dastore
      // c5ef: aastore
      // c5f0: dup
      // c5f1: sipush 546
      // c5f4: bipush 2
      // c5f5: newarray 7
      // c5f7: dup
      // c5f8: bipush 0
      // c5f9: ldc2_w 0.42735910415649414
      // c5fc: dastore
      // c5fd: dup
      // c5fe: bipush 1
      // c5ff: ldc2_w -1.1413593493354881E-8
      // c602: dastore
      // c603: aastore
      // c604: dup
      // c605: sipush 547
      // c608: bipush 2
      // c609: newarray 7
      // c60b: dup
      // c60c: bipush 0
      // c60d: ldc2_w 0.42799586057662964
      // c610: dastore
      // c611: dup
      // c612: bipush 1
      // c613: ldc2_w -2.792046157580119E-8
      // c616: dastore
      // c617: aastore
      // c618: dup
      // c619: sipush 548
      // c61c: bipush 2
      // c61d: newarray 7
      // c61f: dup
      // c620: bipush 0
      // c621: ldc2_w 0.42863214015960693
      // c624: dastore
      // c625: dup
      // c626: bipush 1
      // c627: ldc2_w 2.723009182661306E-8
      // c62a: dastore
      // c62b: aastore
      // c62c: dup
      // c62d: sipush 549
      // c630: bipush 2
      // c631: newarray 7
      // c633: dup
      // c634: bipush 0
      // c635: ldc2_w 0.42926812171936035
      // c638: dastore
      // c639: dup
      // c63a: bipush 1
      // c63b: ldc2_w -2.4260535621557444E-8
      // c63e: dastore
      // c63f: aastore
      // c640: dup
      // c641: sipush 550
      // c644: bipush 2
      // c645: newarray 7
      // c647: dup
      // c648: bipush 0
      // c649: ldc2_w 0.42990362644195557
      // c64c: dastore
      // c64d: dup
      // c64e: bipush 1
      // c64f: ldc2_w -3.064060124024764E-9
      // c652: dastore
      // c653: aastore
      // c654: dup
      // c655: sipush 551
      // c658: bipush 2
      // c659: newarray 7
      // c65b: dup
      // c65c: bipush 0
      // c65d: ldc2_w 0.43053877353668213
      // c660: dastore
      // c661: dup
      // c662: bipush 1
      // c663: ldc2_w -2.787640178598121E-8
      // c666: dastore
      // c667: aastore
      // c668: dup
      // c669: sipush 552
      // c66c: bipush 2
      // c66d: newarray 7
      // c66f: dup
      // c670: bipush 0
      // c671: ldc2_w 0.4311734437942505
      // c674: dastore
      // c675: dup
      // c676: bipush 1
      // c677: ldc2_w 2.102412085257792E-8
      // c67a: dastore
      // c67b: aastore
      // c67c: dup
      // c67d: sipush 553
      // c680: bipush 2
      // c681: newarray 7
      // c683: dup
      // c684: bipush 0
      // c685: ldc2_w 0.4318077564239502
      // c688: dastore
      // c689: dup
      // c68a: bipush 1
      // c68b: ldc2_w 2.4939635093999683E-8
      // c68e: dastore
      // c68f: aastore
      // c690: dup
      // c691: sipush 554
      // c694: bipush 2
      // c695: newarray 7
      // c697: dup
      // c698: bipush 0
      // c699: ldc2_w 0.43244171142578125
      // c69c: dastore
      // c69d: dup
      // c69e: bipush 1
      // c69f: ldc2_w -1.5619414792273914E-8
      // c6a2: dastore
      // c6a3: aastore
      // c6a4: dup
      // c6a5: sipush 555
      // c6a8: bipush 2
      // c6a9: newarray 7
      // c6ab: dup
      // c6ac: bipush 0
      // c6ad: ldc2_w 0.4330751895904541
      // c6b0: dastore
      // c6b1: dup
      // c6b2: bipush 1
      // c6b3: ldc2_w 1.9065734894871523E-8
      // c6b6: dastore
      // c6b7: aastore
      // c6b8: dup
      // c6b9: sipush 556
      // c6bc: bipush 2
      // c6bd: newarray 7
      // c6bf: dup
      // c6c0: bipush 0
      // c6c1: ldc2_w 0.4337083101272583
      // c6c4: dastore
      // c6c5: dup
      // c6c6: bipush 1
      // c6c7: ldc2_w 1.0294301092654604E-8
      // c6ca: dastore
      // c6cb: aastore
      // c6cc: dup
      // c6cd: sipush 557
      // c6d0: bipush 2
      // c6d1: newarray 7
      // c6d3: dup
      // c6d4: bipush 0
      // c6d5: ldc2_w 0.4343410134315491
      // c6d8: dastore
      // c6d9: dup
      // c6da: bipush 1
      // c6db: ldc2_w 1.8178469851136E-8
      // c6de: dastore
      // c6df: aastore
      // c6e0: dup
      // c6e1: sipush 558
      // c6e4: bipush 2
      // c6e5: newarray 7
      // c6e7: dup
      // c6e8: bipush 0
      // c6e9: ldc2_w 0.4349733591079712
      // c6ec: dastore
      // c6ed: dup
      // c6ee: bipush 1
      // c6ef: ldc2_w -1.6379825102473853E-8
      // c6f2: dastore
      // c6f3: aastore
      // c6f4: dup
      // c6f5: sipush 559
      // c6f8: bipush 2
      // c6f9: newarray 7
      // c6fb: dup
      // c6fc: bipush 0
      // c6fd: ldc2_w 0.4356052279472351
      // c700: dastore
      // c701: dup
      // c702: bipush 1
      // c703: ldc2_w 2.6334323946685834E-8
      // c706: dastore
      // c707: aastore
      // c708: dup
      // c709: sipush 560
      // c70c: bipush 2
      // c70d: newarray 7
      // c70f: dup
      // c710: bipush 0
      // c711: ldc2_w 0.43623673915863037
      // c714: dastore
      // c715: dup
      // c716: bipush 1
      // c717: ldc2_w 2.761628769925529E-8
      // c71a: dastore
      // c71b: aastore
      // c71c: dup
      // c71d: sipush 561
      // c720: bipush 2
      // c721: newarray 7
      // c723: dup
      // c724: bipush 0
      // c725: ldc2_w 0.436867892742157
      // c728: dastore
      // c729: dup
      // c72a: bipush 1
      // c72b: ldc2_w -1.2030229087793677E-8
      // c72e: dastore
      // c72f: aastore
      // c730: dup
      // c731: sipush 562
      // c734: bipush 2
      // c735: newarray 7
      // c737: dup
      // c738: bipush 0
      // c739: ldc2_w 0.4374985694885254
      // c73c: dastore
      // c73d: dup
      // c73e: bipush 1
      // c73f: ldc2_w 2.7106814809424793E-8
      // c742: dastore
      // c743: aastore
      // c744: dup
      // c745: sipush 563
      // c748: bipush 2
      // c749: newarray 7
      // c74b: dup
      // c74c: bipush 0
      // c74d: ldc2_w 0.43812888860702515
      // c750: dastore
      // c751: dup
      // c752: bipush 1
      // c753: ldc2_w 2.631993083235205E-8
      // c756: dastore
      // c757: aastore
      // c758: dup
      // c759: sipush 564
      // c75c: bipush 2
      // c75d: newarray 7
      // c75f: dup
      // c760: bipush 0
      // c761: ldc2_w 0.43875885009765625
      // c764: dastore
      // c765: dup
      // c766: bipush 1
      // c767: ldc2_w -1.3890028312254422E-8
      // c76a: dastore
      // c76b: aastore
      // c76c: dup
      // c76d: sipush 565
      // c770: bipush 2
      // c771: newarray 7
      // c773: dup
      // c774: bipush 0
      // c775: ldc2_w 0.43938833475112915
      // c778: dastore
      // c779: dup
      // c77a: bipush 1
      // c77b: ldc2_w 2.6186133735555794E-8
      // c77e: dastore
      // c77f: aastore
      // c780: dup
      // c781: sipush 566
      // c784: bipush 2
      // c785: newarray 7
      // c787: dup
      // c788: bipush 0
      // c789: ldc2_w 0.4400174617767334
      // c78c: dastore
      // c78d: dup
      // c78e: bipush 1
      // c78f: ldc2_w 2.783809071694788E-8
      // c792: dastore
      // c793: aastore
      // c794: dup
      // c795: sipush 567
      // c798: bipush 2
      // c799: newarray 7
      // c79b: dup
      // c79c: bipush 0
      // c79d: ldc2_w 0.440646231174469
      // c7a0: dastore
      // c7a1: dup
      // c7a2: bipush 1
      // c7a3: ldc2_w -8.436135220472006E-9
      // c7a6: dastore
      // c7a7: aastore
      // c7a8: dup
      // c7a9: sipush 568
      // c7ac: bipush 2
      // c7ad: newarray 7
      // c7af: dup
      // c7b0: bipush 0
      // c7b1: ldc2_w 0.44127458333969116
      // c7b4: dastore
      // c7b5: dup
      // c7b6: bipush 1
      // c7b7: ldc2_w -2.2534815932619883E-8
      // c7ba: dastore
      // c7bb: aastore
      // c7bc: dup
      // c7bd: sipush 569
      // c7c0: bipush 2
      // c7c1: newarray 7
      // c7c3: dup
      // c7c4: bipush 0
      // c7c5: ldc2_w 0.4419025182723999
      // c7c8: dastore
      // c7c9: dup
      // c7ca: bipush 1
      // c7cb: ldc2_w -1.3961804471714283E-8
      // c7ce: dastore
      // c7cf: aastore
      // c7d0: dup
      // c7d1: sipush 570
      // c7d4: bipush 2
      // c7d5: newarray 7
      // c7d7: dup
      // c7d8: bipush 0
      // c7d9: ldc2_w 0.4425300359725952
      // c7dc: dastore
      // c7dd: dup
      // c7de: bipush 1
      // c7df: ldc2_w 1.7778112039716255E-8
      // c7e2: dastore
      // c7e3: aastore
      // c7e4: dup
      // c7e5: sipush 571
      // c7e8: bipush 2
      // c7e9: newarray 7
      // c7eb: dup
      // c7ec: bipush 0
      // c7ed: ldc2_w 0.4431571960449219
      // c7f0: dastore
      // c7f1: dup
      // c7f2: bipush 1
      // c7f3: ldc2_w 1.3574569976673652E-8
      // c7f6: dastore
      // c7f7: aastore
      // c7f8: dup
      // c7f9: sipush 572
      // c7fc: bipush 2
      // c7fd: newarray 7
      // c7ff: dup
      // c800: bipush 0
      // c801: ldc2_w 0.4437839984893799
      // c804: dastore
      // c805: dup
      // c806: bipush 1
      // c807: ldc2_w -2.607907890164073E-8
      // c80a: dastore
      // c80b: aastore
      // c80c: dup
      // c80d: sipush 573
      // c810: bipush 2
      // c811: newarray 7
      // c813: dup
      // c814: bipush 0
      // c815: ldc2_w 0.4444103240966797
      // c818: dastore
      // c819: dup
      // c81a: bipush 1
      // c81b: ldc2_w 1.8518879652136628E-8
      // c81e: dastore
      // c81f: aastore
      // c820: dup
      // c821: sipush 574
      // c824: bipush 2
      // c825: newarray 7
      // c827: dup
      // c828: bipush 0
      // c829: ldc2_w 0.44503629207611084
      // c82c: dastore
      // c82d: dup
      // c82e: bipush 1
      // c82f: ldc2_w 2.865065604247164E-8
      // c832: dastore
      // c833: aastore
      // c834: dup
      // c835: sipush 575
      // c838: bipush 2
      // c839: newarray 7
      // c83b: dup
      // c83c: bipush 0
      // c83d: ldc2_w 0.44566190242767334
      // c840: dastore
      // c841: dup
      // c842: bipush 1
      // c843: ldc2_w 4.806827797299427E-9
      // c846: dastore
      // c847: aastore
      // c848: dup
      // c849: sipush 576
      // c84c: bipush 2
      // c84d: newarray 7
      // c84f: dup
      // c850: bipush 0
      // c851: ldc2_w 0.4462870955467224
      // c854: dastore
      // c855: dup
      // c856: bipush 1
      // c857: ldc2_w 7.0816970994232115E-9
      // c85a: dastore
      // c85b: aastore
      // c85c: dup
      // c85d: sipush 577
      // c860: bipush 2
      // c861: newarray 7
      // c863: dup
      // c864: bipush 0
      // c865: ldc2_w 0.44691193103790283
      // c868: dastore
      // c869: dup
      // c86a: bipush 1
      // c86b: ldc2_w -2.3640641240074437E-8
      // c86e: dastore
      // c86f: aastore
      // c870: dup
      // c871: sipush 578
      // c874: bipush 2
      // c875: newarray 7
      // c877: dup
      // c878: bipush 0
      // c879: ldc2_w 0.4475363492965698
      // c87c: dastore
      // c87d: dup
      // c87e: bipush 1
      // c87f: ldc2_w -2.7267718387865538E-8
      // c882: dastore
      // c883: aastore
      // c884: dup
      // c885: sipush 579
      // c888: bipush 2
      // c889: newarray 7
      // c88b: dup
      // c88c: bipush 0
      // c88d: ldc2_w 0.4481603503227234
      // c890: dastore
      // c891: dup
      // c892: bipush 1
      // c893: ldc2_w -3.3126235292976077E-9
      // c896: dastore
      // c897: aastore
      // c898: dup
      // c899: sipush 580
      // c89c: bipush 2
      // c89d: newarray 7
      // c89f: dup
      // c8a0: bipush 0
      // c8a1: ldc2_w 0.4487839937210083
      // c8a4: dastore
      // c8a5: dup
      // c8a6: bipush 1
      // c8a7: ldc2_w -1.0894001590268427E-8
      // c8aa: dastore
      // c8ab: aastore
      // c8ac: dup
      // c8ad: sipush 581
      // c8b0: bipush 2
      // c8b1: newarray 7
      // c8b3: dup
      // c8b4: bipush 0
      // c8b5: ldc2_w 0.4494072198867798
      // c8b8: dastore
      // c8b9: dup
      // c8ba: bipush 1
      // c8bb: ldc2_w 1.0077883359971829E-8
      // c8be: dastore
      // c8bf: aastore
      // c8c0: dup
      // c8c1: sipush 582
      // c8c4: bipush 2
      // c8c5: newarray 7
      // c8c7: dup
      // c8c8: bipush 0
      // c8c9: ldc2_w 0.4500300884246826
      // c8cc: dastore
      // c8cd: dup
      // c8ce: bipush 1
      // c8cf: ldc2_w 4.825712712114668E-10
      // c8d2: dastore
      // c8d3: aastore
      // c8d4: dup
      // c8d5: sipush 583
      // c8d8: bipush 2
      // c8d9: newarray 7
      // c8db: dup
      // c8dc: bipush 0
      // c8dd: ldc2_w 0.450652539730072
      // c8e0: dastore
      // c8e1: dup
      // c8e2: bipush 1
      // c8e3: ldc2_w 2.0407987470746858E-8
      // c8e6: dastore
      // c8e7: aastore
      // c8e8: dup
      // c8e9: sipush 584
      // c8ec: bipush 2
      // c8ed: newarray 7
      // c8ef: dup
      // c8f0: bipush 0
      // c8f1: ldc2_w 0.4512746334075928
      // c8f4: dastore
      // c8f5: dup
      // c8f6: bipush 1
      // c8f7: ldc2_w 1.073186581170719E-8
      // c8fa: dastore
      // c8fb: aastore
      // c8fc: dup
      // c8fd: sipush 585
      // c900: bipush 2
      // c901: newarray 7
      // c903: dup
      // c904: bipush 0
      // c905: ldc2_w 0.4518963694572449
      // c908: dastore
      // c909: dup
      // c90a: bipush 1
      // c90b: ldc2_w -2.8064314757880205E-8
      // c90e: dastore
      // c90f: aastore
      // c910: dup
      // c911: sipush 586
      // c914: bipush 2
      // c915: newarray 7
      // c917: dup
      // c918: bipush 0
      // c919: ldc2_w 0.45251762866973877
      // c91c: dastore
      // c91d: dup
      // c91e: bipush 1
      // c91f: ldc2_w 2.3709316816226527E-8
      // c922: dastore
      // c923: aastore
      // c924: dup
      // c925: sipush 587
      // c928: bipush 2
      // c929: newarray 7
      // c92b: dup
      // c92c: bipush 0
      // c92d: ldc2_w 0.4531385898590088
      // c930: dastore
      // c931: dup
      // c932: bipush 1
      // c933: ldc2_w -1.2281487504266522E-8
      // c936: dastore
      // c937: aastore
      // c938: dup
      // c939: sipush 588
      // c93c: bipush 2
      // c93d: newarray 7
      // c93f: dup
      // c940: bipush 0
      // c941: ldc2_w 0.4537591338157654
      // c944: dastore
      // c945: dup
      // c946: bipush 1
      // c947: ldc2_w -1.634864487421458E-8
      // c94a: dastore
      // c94b: aastore
      // c94c: dup
      // c94d: sipush 589
      // c950: bipush 2
      // c951: newarray 7
      // c953: dup
      // c954: bipush 0
      // c955: ldc2_w 0.45437926054000854
      // c958: dastore
      // c959: dup
      // c95a: bipush 1
      // c95b: ldc2_w 1.1985747222409522E-8
      // c95e: dastore
      // c95f: aastore
      // c960: dup
      // c961: sipush 590
      // c964: bipush 2
      // c965: newarray 7
      // c967: dup
      // c968: bipush 0
      // c969: ldc2_w 0.45499902963638306
      // c96c: dastore
      // c96d: dup
      // c96e: bipush 1
      // c96f: ldc2_w 1.3594057956219485E-8
      // c972: dastore
      // c973: aastore
      // c974: dup
      // c975: sipush 591
      // c978: bipush 2
      // c979: newarray 7
      // c97b: dup
      // c97c: bipush 0
      // c97d: ldc2_w 0.4556184411048889
      // c980: dastore
      // c981: dup
      // c982: bipush 1
      // c983: ldc2_w -1.1047585095328619E-8
      // c986: dastore
      // c987: aastore
      // c988: dup
      // c989: sipush 592
      // c98c: bipush 2
      // c98d: newarray 7
      // c98f: dup
      // c990: bipush 0
      // c991: ldc2_w 0.45623743534088135
      // c994: dastore
      // c995: dup
      // c996: bipush 1
      // c997: ldc2_w -1.8592937532754405E-9
      // c99a: dastore
      // c99b: aastore
      // c99c: dup
      // c99d: sipush 593
      // c9a0: bipush 2
      // c9a1: newarray 7
      // c9a3: dup
      // c9a4: bipush 0
      // c9a5: ldc2_w 0.4568560719490051
      // c9a8: dastore
      // c9a9: dup
      // c9aa: bipush 1
      // c9ab: ldc2_w -1.797135137545755E-8
      // c9ae: dastore
      // c9af: aastore
      // c9b0: dup
      // c9b1: sipush 594
      // c9b4: bipush 2
      // c9b5: newarray 7
      // c9b7: dup
      // c9b8: bipush 0
      // c9b9: ldc2_w 0.4574742913246155
      // c9bc: dastore
      // c9bd: dup
      // c9be: bipush 1
      // c9bf: ldc2_w 6.943684261645378E-10
      // c9c2: dastore
      // c9c3: aastore
      // c9c4: dup
      // c9c5: sipush 595
      // c9c8: bipush 2
      // c9c9: newarray 7
      // c9cb: dup
      // c9cc: bipush 0
      // c9cd: ldc2_w 0.4580921530723572
      // c9d0: dastore
      // c9d1: dup
      // c9d2: bipush 1
      // c9d3: ldc2_w -4.994175141684681E-9
      // c9d6: dastore
      // c9d7: aastore
      // c9d8: dup
      // c9d9: sipush 596
      // c9dc: bipush 2
      // c9dd: newarray 7
      // c9df: dup
      // c9e0: bipush 0
      // c9e1: ldc2_w 0.45870959758758545
      // c9e4: dastore
      // c9e5: dup
      // c9e6: bipush 1
      // c9e7: ldc2_w 2.5039391215625133E-8
      // c9ea: dastore
      // c9eb: aastore
      // c9ec: dup
      // c9ed: sipush 597
      // c9f0: bipush 2
      // c9f1: newarray 7
      // c9f3: dup
      // c9f4: bipush 0
      // c9f5: ldc2_w 0.45932674407958984
      // c9f8: dastore
      // c9f9: dup
      // c9fa: bipush 1
      // c9fb: ldc2_w -2.7943366835352838E-8
      // c9fe: dastore
      // c9ff: aastore
      // ca00: dup
      // ca01: sipush 598
      // ca04: bipush 2
      // ca05: newarray 7
      // ca07: dup
      // ca08: bipush 0
      // ca09: ldc2_w 0.45994341373443604
      // ca0c: dastore
      // ca0d: dup
      // ca0e: bipush 1
      // ca0f: ldc2_w 1.534146910128904E-8
      // ca12: dastore
      // ca13: aastore
      // ca14: dup
      // ca15: sipush 599
      // ca18: bipush 2
      // ca19: newarray 7
      // ca1b: dup
      // ca1c: bipush 0
      // ca1d: ldc2_w 0.46055978536605835
      // ca20: dastore
      // ca21: dup
      // ca22: bipush 1
      // ca23: ldc2_w -2.3450920230816267E-8
      // ca26: dastore
      // ca27: aastore
      // ca28: dup
      // ca29: sipush 600
      // ca2c: bipush 2
      // ca2d: newarray 7
      // ca2f: dup
      // ca30: bipush 0
      // ca31: ldc2_w 0.46117573976516724
      // ca34: dastore
      // ca35: dup
      // ca36: bipush 1
      // ca37: ldc2_w -2.4642997069960124E-8
      // ca3a: dastore
      // ca3b: aastore
      // ca3c: dup
      // ca3d: sipush 601
      // ca40: bipush 2
      // ca41: newarray 7
      // ca43: dup
      // ca44: bipush 0
      // ca45: ldc2_w 0.4617912769317627
      // ca48: dastore
      // ca49: dup
      // ca4a: bipush 1
      // ca4b: ldc2_w 1.2232622070370946E-8
      // ca4e: dastore
      // ca4f: aastore
      // ca50: dup
      // ca51: sipush 602
      // ca54: bipush 2
      // ca55: newarray 7
      // ca57: dup
      // ca58: bipush 0
      // ca59: ldc2_w 0.4624064564704895
      // ca5c: dastore
      // ca5d: dup
      // ca5e: bipush 1
      // ca5f: ldc2_w 2.80378133047839E-8
      // ca62: dastore
      // ca63: aastore
      // ca64: dup
      // ca65: sipush 603
      // ca68: bipush 2
      // ca69: newarray 7
      // ca6b: dup
      // ca6c: bipush 0
      // ca6d: ldc2_w 0.46302127838134766
      // ca70: dastore
      // ca71: dup
      // ca72: bipush 1
      // ca73: ldc2_w 2.3238237048117092E-8
      // ca76: dastore
      // ca77: aastore
      // ca78: dup
      // ca79: sipush 604
      // ca7c: bipush 2
      // ca7d: newarray 7
      // ca7f: dup
      // ca80: bipush 0
      // ca81: ldc2_w 0.46363574266433716
      // ca84: dastore
      // ca85: dup
      // ca86: bipush 1
      // ca87: ldc2_w -1.7013046451109475E-9
      // ca8a: dastore
      // ca8b: aastore
      // ca8c: dup
      // ca8d: sipush 605
      // ca90: bipush 2
      // ca91: newarray 7
      // ca93: dup
      // ca94: bipush 0
      // ca95: ldc2_w 0.46424978971481323
      // ca98: dastore
      // ca99: dup
      // ca9a: bipush 1
      // ca9b: ldc2_w 1.3287778803035383E-8
      // ca9e: dastore
      // ca9f: aastore
      // caa0: dup
      // caa1: sipush 606
      // caa4: bipush 2
      // caa5: newarray 7
      // caa7: dup
      // caa8: bipush 0
      // caa9: ldc2_w 0.46486347913742065
      // caac: dastore
      // caad: dup
      // caae: bipush 1
      // caaf: ldc2_w 9.06393426961373E-9
      // cab2: dastore
      // cab3: aastore
      // cab4: dup
      // cab5: sipush 607
      // cab8: bipush 2
      // cab9: newarray 7
      // cabb: dup
      // cabc: bipush 0
      // cabd: ldc2_w 0.4654768109321594
      // cac0: dastore
      // cac1: dup
      // cac2: bipush 1
      // cac3: ldc2_w -1.3910598647592876E-8
      // cac6: dastore
      // cac7: aastore
      // cac8: dup
      // cac9: sipush 608
      // cacc: bipush 2
      // cacd: newarray 7
      // cacf: dup
      // cad0: bipush 0
      // cad1: ldc2_w 0.46608972549438477
      // cad4: dastore
      // cad5: dup
      // cad6: bipush 1
      // cad7: ldc2_w 4.430214458933614E-9
      // cada: dastore
      // cadb: aastore
      // cadc: dup
      // cadd: sipush 609
      // cae0: bipush 2
      // cae1: newarray 7
      // cae3: dup
      // cae4: bipush 0
      // cae5: ldc2_w 0.46670228242874146
      // cae8: dastore
      // cae9: dup
      // caea: bipush 1
      // caeb: ldc2_w 4.942270562885745E-9
      // caee: dastore
      // caef: aastore
      // caf0: dup
      // caf1: sipush 610
      // caf4: bipush 2
      // caf5: newarray 7
      // caf7: dup
      // caf8: bipush 0
      // caf9: ldc2_w 0.4673144817352295
      // cafc: dastore
      // cafd: dup
      // cafe: bipush 1
      // caff: ldc2_w -1.1914734393460718E-8
      // cb02: dastore
      // cb03: aastore
      // cb04: dup
      // cb05: sipush 611
      // cb08: bipush 2
      // cb09: newarray 7
      // cb0b: dup
      // cb0c: bipush 0
      // cb0d: ldc2_w 0.4679262638092041
      // cb10: dastore
      // cb11: dup
      // cb12: bipush 1
      // cb13: ldc2_w 1.3922696570638494E-8
      // cb16: dastore
      // cb17: aastore
      // cb18: dup
      // cb19: sipush 612
      // cb1c: bipush 2
      // cb1d: newarray 7
      // cb1f: dup
      // cb20: bipush 0
      // cb21: ldc2_w 0.46853768825531006
      // cb24: dastore
      // cb25: dup
      // cb26: bipush 1
      // cb27: ldc2_w 2.3307929211781914E-8
      // cb2a: dastore
      // cb2b: aastore
      // cb2c: dup
      // cb2d: sipush 613
      // cb30: bipush 2
      // cb31: newarray 7
      // cb33: dup
      // cb34: bipush 0
      // cb35: ldc2_w 0.46914875507354736
      // cb38: dastore
      // cb39: dup
      // cb3a: bipush 1
      // cb3b: ldc2_w 1.669813444584674E-8
      // cb3e: dastore
      // cb3f: aastore
      // cb40: dup
      // cb41: sipush 614
      // cb44: bipush 2
      // cb45: newarray 7
      // cb47: dup
      // cb48: bipush 0
      // cb49: ldc2_w 0.469759464263916
      // cb4c: dastore
      // cb4d: dup
      // cb4e: bipush 1
      // cb4f: ldc2_w -5.450354376430758E-9
      // cb52: dastore
      // cb53: aastore
      // cb54: dup
      // cb55: sipush 615
      // cb58: bipush 2
      // cb59: newarray 7
      // cb5b: dup
      // cb5c: bipush 0
      // cb5d: ldc2_w 0.47036975622177124
      // cb60: dastore
      // cb61: dup
      // cb62: bipush 1
      // cb63: ldc2_w 1.6922605350647674E-8
      // cb66: dastore
      // cb67: aastore
      // cb68: dup
      // cb69: sipush 616
      // cb6c: bipush 2
      // cb6d: newarray 7
      // cb6f: dup
      // cb70: bipush 0
      // cb71: ldc2_w 0.4709796905517578
      // cb74: dastore
      // cb75: dup
      // cb76: bipush 1
      // cb77: ldc2_w 2.4667033200046904E-8
      // cb7a: dastore
      // cb7b: aastore
      // cb7c: dup
      // cb7d: sipush 617
      // cb80: bipush 2
      // cb81: newarray 7
      // cb83: dup
      // cb84: bipush 0
      // cb85: ldc2_w 0.47158926725387573
      // cb88: dastore
      // cb89: dup
      // cb8a: bipush 1
      // cb8b: ldc2_w 1.8236762070433784E-8
      // cb8e: dastore
      // cb8f: aastore
      // cb90: dup
      // cb91: sipush 618
      // cb94: bipush 2
      // cb95: newarray 7
      // cb97: dup
      // cb98: bipush 0
      // cb99: ldc2_w 0.472198486328125
      // cb9c: dastore
      // cb9d: dup
      // cb9e: bipush 1
      // cb9f: ldc2_w -1.915204563140137E-9
      // cba2: dastore
      // cba3: aastore
      // cba4: dup
      // cba5: sipush 619
      // cba8: bipush 2
      // cba9: newarray 7
      // cbab: dup
      // cbac: bipush 0
      // cbad: ldc2_w 0.47280728816986084
      // cbb0: dastore
      // cbb1: dup
      // cbb2: bipush 1
      // cbb3: ldc2_w 2.426795414605756E-8
      // cbb6: dastore
      // cbb7: aastore
      // cbb8: dup
      // cbb9: sipush 620
      // cbbc: bipush 2
      // cbbd: newarray 7
      // cbbf: dup
      // cbc0: bipush 0
      // cbc1: ldc2_w 0.4734157919883728
      // cbc4: dastore
      // cbc5: dup
      // cbc6: bipush 1
      // cbc7: ldc2_w -2.19717006713618E-8
      // cbca: dastore
      // cbcb: aastore
      // cbcc: dup
      // cbcd: sipush 621
      // cbd0: bipush 2
      // cbd1: newarray 7
      // cbd3: dup
      // cbd4: bipush 0
      // cbd5: ldc2_w 0.47402387857437134
      // cbd8: dastore
      // cbd9: dup
      // cbda: bipush 1
      // cbdb: ldc2_w -2.0974352165535873E-8
      // cbde: dastore
      // cbdf: aastore
      // cbe0: dup
      // cbe1: sipush 622
      // cbe4: bipush 2
      // cbe5: newarray 7
      // cbe7: dup
      // cbe8: bipush 0
      // cbe9: ldc2_w 0.47463154792785645
      // cbec: dastore
      // cbed: dup
      // cbee: bipush 1
      // cbef: ldc2_w 2.770970558184228E-8
      // cbf2: dastore
      // cbf3: aastore
      // cbf4: dup
      // cbf5: sipush 623
      // cbf8: bipush 2
      // cbf9: newarray 7
      // cbfb: dup
      // cbfc: bipush 0
      // cbfd: ldc2_w 0.4752389192581177
      // cc00: dastore
      // cc01: dup
      // cc02: bipush 1
      // cc03: ldc2_w 5.32006955298355E-9
      // cc06: dastore
      // cc07: aastore
      // cc08: dup
      // cc09: sipush 624
      // cc0c: bipush 2
      // cc0d: newarray 7
      // cc0f: dup
      // cc10: bipush 0
      // cc11: ldc2_w 0.47584593296051025
      // cc14: dastore
      // cc15: dup
      // cc16: bipush 1
      // cc17: ldc2_w -2.809054633964104E-8
      // cc1a: dastore
      // cc1b: aastore
      // cc1c: dup
      // cc1d: sipush 625
      // cc20: bipush 2
      // cc21: newarray 7
      // cc23: dup
      // cc24: bipush 0
      // cc25: ldc2_w 0.4764525294303894
      // cc28: dastore
      // cc29: dup
      // cc2a: bipush 1
      // cc2b: ldc2_w -1.2470243596102937E-8
      // cc2e: dastore
      // cc2f: aastore
      // cc30: dup
      // cc31: sipush 626
      // cc34: bipush 2
      // cc35: newarray 7
      // cc37: dup
      // cc38: bipush 0
      // cc39: ldc2_w 0.4770587682723999
      // cc3c: dastore
      // cc3d: dup
      // cc3e: bipush 1
      // cc3f: ldc2_w -6.977226702440138E-9
      // cc42: dastore
      // cc43: aastore
      // cc44: dup
      // cc45: sipush 627
      // cc48: bipush 2
      // cc49: newarray 7
      // cc4b: dup
      // cc4c: bipush 0
      // cc4d: ldc2_w 0.47766464948654175
      // cc50: dastore
      // cc51: dup
      // cc52: bipush 1
      // cc53: ldc2_w -1.1165866833118273E-8
      // cc56: dastore
      // cc57: aastore
      // cc58: dup
      // cc59: sipush 628
      // cc5c: bipush 2
      // cc5d: newarray 7
      // cc5f: dup
      // cc60: bipush 0
      // cc61: ldc2_w 0.47827017307281494
      // cc64: dastore
      // cc65: dup
      // cc66: bipush 1
      // cc67: ldc2_w -2.4591344661022708E-8
      // cc6a: dastore
      // cc6b: aastore
      // cc6c: dup
      // cc6d: sipush 629
      // cc70: bipush 2
      // cc71: newarray 7
      // cc73: dup
      // cc74: bipush 0
      // cc75: ldc2_w 0.4788752794265747
      // cc78: dastore
      // cc79: dup
      // cc7a: bipush 1
      // cc7b: ldc2_w 1.2794996377383974E-8
      // cc7e: dastore
      // cc7f: aastore
      // cc80: dup
      // cc81: sipush 630
      // cc84: bipush 2
      // cc85: newarray 7
      // cc87: dup
      // cc88: bipush 0
      // cc89: ldc2_w 0.4794800877571106
      // cc8c: dastore
      // cc8d: dup
      // cc8e: bipush 1
      // cc8f: ldc2_w -1.7772927065973874E-8
      // cc92: dastore
      // cc93: aastore
      // cc94: dup
      // cc95: sipush 631
      // cc98: bipush 2
      // cc99: newarray 7
      // cc9b: dup
      // cc9c: bipush 0
      // cc9d: ldc2_w 0.48008447885513306
      // cca0: dastore
      // cca1: dup
      // cca2: bipush 1
      // cca3: ldc2_w 3.35657712457243E-9
      // cca6: dastore
      // cca7: aastore
      // cca8: dup
      // cca9: sipush 632
      // ccac: bipush 2
      // ccad: newarray 7
      // ccaf: dup
      // ccb0: bipush 0
      // ccb1: ldc2_w 0.48068851232528687
      // ccb4: dastore
      // ccb5: dup
      // ccb6: bipush 1
      // ccb7: ldc2_w 1.7020465042442242E-8
      // ccba: dastore
      // ccbb: aastore
      // ccbc: dup
      // ccbd: sipush 633
      // ccc0: bipush 2
      // ccc1: newarray 7
      // ccc3: dup
      // ccc4: bipush 0
      // ccc5: ldc2_w 0.481292188167572
      // ccc8: dastore
      // ccc9: dup
      // ccca: bipush 1
      // cccb: ldc2_w 2.365953779624783E-8
      // ccce: dastore
      // cccf: aastore
      // ccd0: dup
      // ccd1: sipush 634
      // ccd4: bipush 2
      // ccd5: newarray 7
      // ccd7: dup
      // ccd8: bipush 0
      // ccd9: ldc2_w 0.4818955063819885
      // ccdc: dastore
      // ccdd: dup
      // ccde: bipush 1
      // ccdf: ldc2_w 2.3713798664443718E-8
      // cce2: dastore
      // cce3: aastore
      // cce4: dup
      // cce5: sipush 635
      // cce8: bipush 2
      // cce9: newarray 7
      // cceb: dup
      // ccec: bipush 0
      // cced: ldc2_w 0.4824984669685364
      // ccf0: dastore
      // ccf1: dup
      // ccf2: bipush 1
      // ccf3: ldc2_w 1.7622455019548098E-8
      // ccf6: dastore
      // ccf7: aastore
      // ccf8: dup
      // ccf9: sipush 636
      // ccfc: bipush 2
      // ccfd: newarray 7
      // ccff: dup
      // cd00: bipush 0
      // cd01: ldc2_w 0.4831010699272156
      // cd04: dastore
      // cd05: dup
      // cd06: bipush 1
      // cd07: ldc2_w 5.823920246566496E-9
      // cd0a: dastore
      // cd0b: aastore
      // cd0c: dup
      // cd0d: sipush 637
      // cd10: bipush 2
      // cd11: newarray 7
      // cd13: dup
      // cd14: bipush 0
      // cd15: ldc2_w 0.4837033152580261
      // cd18: dastore
      // cd19: dup
      // cd1a: bipush 1
      // cd1b: ldc2_w -1.1244184344361017E-8
      // cd1e: dastore
      // cd1f: aastore
      // cd20: dup
      // cd21: sipush 638
      // cd24: bipush 2
      // cd25: newarray 7
      // cd27: dup
      // cd28: bipush 0
      // cd29: ldc2_w 0.48430514335632324
      // cd2c: dastore
      // cd2d: dup
      // cd2e: bipush 1
      // cd2f: ldc2_w 2.645961716432205E-8
      // cd32: dastore
      // cd33: aastore
      // cd34: dup
      // cd35: sipush 639
      // cd38: bipush 2
      // cd39: newarray 7
      // cd3b: dup
      // cd3c: bipush 0
      // cd3d: ldc2_w 0.4849066734313965
      // cd40: dastore
      // cd41: dup
      // cd42: bipush 1
      // cd43: ldc2_w 1.6207809718247905E-10
      // cd46: dastore
      // cd47: aastore
      // cd48: dup
      // cd49: sipush 640
      // cd4c: bipush 2
      // cd4d: newarray 7
      // cd4f: dup
      // cd50: bipush 0
      // cd51: ldc2_w 0.4855077862739563
      // cd54: dastore
      // cd55: dup
      // cd56: bipush 1
      // cd57: ldc2_w 2.9507744508973654E-8
      // cd5a: dastore
      // cd5b: aastore
      // cd5c: dup
      // cd5d: sipush 641
      // cd60: bipush 2
      // cd61: newarray 7
      // cd63: dup
      // cd64: bipush 0
      // cd65: ldc2_w 0.48610860109329224
      // cd68: dastore
      // cd69: dup
      // cd6a: bipush 1
      // cd6b: ldc2_w -4.278201128741098E-9
      // cd6e: dastore
      // cd6f: aastore
      // cd70: dup
      // cd71: sipush 642
      // cd74: bipush 2
      // cd75: newarray 7
      // cd77: dup
      // cd78: bipush 0
      // cd79: ldc2_w 0.48670899868011475
      // cd7c: dastore
      // cd7d: dup
      // cd7e: bipush 1
      // cd7f: ldc2_w 1.844722015961139E-8
      // cd82: dastore
      // cd83: aastore
      // cd84: dup
      // cd85: sipush 643
      // cd88: bipush 2
      // cd89: newarray 7
      // cd8b: dup
      // cd8c: bipush 0
      // cd8d: ldc2_w 0.4873090982437134
      // cd90: dastore
      // cd91: dup
      // cd92: bipush 1
      // cd93: ldc2_w -2.1092372471088425E-8
      // cd96: dastore
      // cd97: aastore
      // cd98: dup
      // cd99: sipush 644
      // cd9c: bipush 2
      // cd9d: newarray 7
      // cd9f: dup
      // cda0: bipush 0
      // cda1: ldc2_w 0.4879087805747986
      // cda4: dastore
      // cda5: dup
      // cda6: bipush 1
      // cda7: ldc2_w -3.2555596107382053E-9
      // cdaa: dastore
      // cdab: aastore
      // cdac: dup
      // cdad: sipush 645
      // cdb0: bipush 2
      // cdb1: newarray 7
      // cdb3: dup
      // cdb4: bipush 0
      // cdb5: ldc2_w 0.48850810527801514
      // cdb8: dastore
      // cdb9: dup
      // cdba: bipush 1
      // cdbb: ldc2_w 1.2784366845429667E-8
      // cdbe: dastore
      // cdbf: aastore
      // cdc0: dup
      // cdc1: sipush 646
      // cdc4: bipush 2
      // cdc5: newarray 7
      // cdc7: dup
      // cdc8: bipush 0
      // cdc9: ldc2_w 0.48910707235336304
      // cdcc: dastore
      // cdcd: dup
      // cdce: bipush 1
      // cdcf: ldc2_w 2.7457984659996047E-8
      // cdd2: dastore
      // cdd3: aastore
      // cdd4: dup
      // cdd5: sipush 647
      // cdd8: bipush 2
      // cdd9: newarray 7
      // cddb: dup
      // cddc: bipush 0
      // cddd: ldc2_w 0.48970574140548706
      // cde0: dastore
      // cde1: dup
      // cde2: bipush 1
      // cde3: ldc2_w -1.8409546441412518E-8
      // cde6: dastore
      // cde7: aastore
      // cde8: dup
      // cde9: sipush 648
      // cdec: bipush 2
      // cded: newarray 7
      // cdef: dup
      // cdf0: bipush 0
      // cdf1: ldc2_w 0.49030399322509766
      // cdf4: dastore
      // cdf5: dup
      // cdf6: bipush 1
      // cdf7: ldc2_w -5.179903818099661E-9
      // cdfa: dastore
      // cdfb: aastore
      // cdfc: dup
      // cdfd: sipush 649
      // ce00: bipush 2
      // ce01: newarray 7
      // ce03: dup
      // ce04: bipush 0
      // ce05: ldc2_w 0.4909018874168396
      // ce08: dastore
      // ce09: dup
      // ce0a: bipush 1
      // ce0b: ldc2_w 7.97053127828682E-9
      // ce0e: dastore
      // ce0f: aastore
      // ce10: dup
      // ce11: sipush 650
      // ce14: bipush 2
      // ce15: newarray 7
      // ce17: dup
      // ce18: bipush 0
      // ce19: ldc2_w 0.4914994239807129
      // ce1c: dastore
      // ce1d: dup
      // ce1e: bipush 1
      // ce1f: ldc2_w 2.146925464473481E-8
      // ce22: dastore
      // ce23: aastore
      // ce24: dup
      // ce25: sipush 651
      // ce28: bipush 2
      // ce29: newarray 7
      // ce2b: dup
      // ce2c: bipush 0
      // ce2d: ldc2_w 0.4920966625213623
      // ce30: dastore
      // ce31: dup
      // ce32: bipush 1
      // ce33: ldc2_w -2.3861648589988232E-8
      // ce36: dastore
      // ce37: aastore
      // ce38: dup
      // ce39: sipush 652
      // ce3c: bipush 2
      // ce3d: newarray 7
      // ce3f: dup
      // ce40: bipush 0
      // ce41: ldc2_w 0.4926934838294983
      // ce44: dastore
      // ce45: dup
      // ce46: bipush 1
      // ce47: ldc2_w -8.386923035320549E-9
      // ce4a: dastore
      // ce4b: aastore
      // ce4c: dup
      // ce4d: sipush 653
      // ce50: bipush 2
      // ce51: newarray 7
      // ce53: dup
      // ce54: bipush 0
      // ce55: ldc2_w 0.4932899475097656
      // ce58: dastore
      // ce59: dup
      // ce5a: bipush 1
      // ce5b: ldc2_w 8.713990131749256E-9
      // ce5e: dastore
      // ce5f: aastore
      // ce60: dup
      // ce61: sipush 654
      // ce64: bipush 2
      // ce65: newarray 7
      // ce67: dup
      // ce68: bipush 0
      // ce69: ldc2_w 0.4938860535621643
      // ce6c: dastore
      // ce6d: dup
      // ce6e: bipush 1
      // ce6f: ldc2_w 2.7865534085810115E-8
      // ce72: dastore
      // ce73: aastore
      // ce74: dup
      // ce75: sipush 655
      // ce78: bipush 2
      // ce79: newarray 7
      // ce7b: dup
      // ce7c: bipush 0
      // ce7d: ldc2_w 0.4944818615913391
      // ce80: dastore
      // ce81: dup
      // ce82: bipush 1
      // ce83: ldc2_w -1.011325138560159E-8
      // ce86: dastore
      // ce87: aastore
      // ce88: dup
      // ce89: sipush 656
      // ce8c: bipush 2
      // ce8d: newarray 7
      // ce8f: dup
      // ce90: bipush 0
      // ce91: ldc2_w 0.4950772523880005
      // ce94: dastore
      // ce95: dup
      // ce96: bipush 1
      // ce97: ldc2_w 1.4409851026316708E-8
      // ce9a: dastore
      // ce9b: aastore
      // ce9c: dup
      // ce9d: sipush 657
      // cea0: bipush 2
      // cea1: newarray 7
      // cea3: dup
      // cea4: bipush 0
      // cea5: ldc2_w 0.495672345161438
      // cea8: dastore
      // cea9: dup
      // ceaa: bipush 1
      // ceab: ldc2_w -1.735227547472004E-8
      // ceae: dastore
      // ceaf: aastore
      // ceb0: dup
      // ceb1: sipush 658
      // ceb4: bipush 2
      // ceb5: newarray 7
      // ceb7: dup
      // ceb8: bipush 0
      // ceb9: ldc2_w 0.49626702070236206
      // cebc: dastore
      // cebd: dup
      // cebe: bipush 1
      // cebf: ldc2_w 1.4231078209064581E-8
      // cec2: dastore
      // cec3: aastore
      // cec4: dup
      // cec5: sipush 659
      // cec8: bipush 2
      // cec9: newarray 7
      // cecb: dup
      // cecc: bipush 0
      // cecd: ldc2_w 0.49686139822006226
      // ced0: dastore
      // ced1: dup
      // ced2: bipush 1
      // ced3: ldc2_w -9.628709342929729E-9
      // ced6: dastore
      // ced7: aastore
      // ced8: dup
      // ced9: sipush 660
      // cedc: bipush 2
      // cedd: newarray 7
      // cedf: dup
      // cee0: bipush 0
      // cee1: ldc2_w 0.4974554181098938
      // cee4: dastore
      // cee5: dup
      // cee6: bipush 1
      // cee7: ldc2_w -2.8907074856577267E-8
      // ceea: dastore
      // ceeb: aastore
      // ceec: dup
      // ceed: sipush 661
      // cef0: bipush 2
      // cef1: newarray 7
      // cef3: dup
      // cef4: bipush 0
      // cef5: ldc2_w 0.4980490207672119
      // cef8: dastore
      // cef9: dup
      // cefa: bipush 1
      // cefb: ldc2_w 1.6419797090870802E-8
      // cefe: dastore
      // ceff: aastore
      // cf00: dup
      // cf01: sipush 662
      // cf04: bipush 2
      // cf05: newarray 7
      // cf07: dup
      // cf08: bipush 0
      // cf09: ldc2_w 0.49864232540130615
      // cf0c: dastore
      // cf0d: dup
      // cf0e: bipush 1
      // cf0f: ldc2_w 7.561041519403049E-9
      // cf12: dastore
      // cf13: aastore
      // cf14: dup
      // cf15: sipush 663
      // cf18: bipush 2
      // cf19: newarray 7
      // cf1b: dup
      // cf1c: bipush 0
      // cf1d: ldc2_w 0.49923527240753174
      // cf20: dastore
      // cf21: dup
      // cf22: bipush 1
      // cf23: ldc2_w 4.538983468118194E-9
      // cf26: dastore
      // cf27: aastore
      // cf28: dup
      // cf29: sipush 664
      // cf2c: bipush 2
      // cf2d: newarray 7
      // cf2f: dup
      // cf30: bipush 0
      // cf31: ldc2_w 0.49982786178588867
      // cf34: dastore
      // cf35: dup
      // cf36: bipush 1
      // cf37: ldc2_w 7.770560657946324E-9
      // cf3a: dastore
      // cf3b: aastore
      // cf3c: dup
      // cf3d: sipush 665
      // cf40: bipush 2
      // cf41: newarray 7
      // cf43: dup
      // cf44: bipush 0
      // cf45: ldc2_w 0.500420093536377
      // cf48: dastore
      // cf49: dup
      // cf4a: bipush 1
      // cf4b: ldc2_w 1.767197002609876E-8
      // cf4e: dastore
      // cf4f: aastore
      // cf50: dup
      // cf51: sipush 666
      // cf54: bipush 2
      // cf55: newarray 7
      // cf57: dup
      // cf58: bipush 0
      // cf59: ldc2_w 0.5010119676589966
      // cf5c: dastore
      // cf5d: dup
      // cf5e: bipush 1
      // cf5f: ldc2_w 3.46586694799214E-8
      // cf62: dastore
      // cf63: aastore
      // cf64: dup
      // cf65: sipush 667
      // cf68: bipush 2
      // cf69: newarray 7
      // cf6b: dup
      // cf6c: bipush 0
      // cf6d: ldc2_w 0.5016034841537476
      // cf70: dastore
      // cf71: dup
      // cf72: bipush 1
      // cf73: ldc2_w 5.914537964556077E-8
      // cf76: dastore
      // cf77: aastore
      // cf78: dup
      // cf79: sipush 668
      // cf7c: bipush 2
      // cf7d: newarray 7
      // cf7f: dup
      // cf80: bipush 0
      // cf81: ldc2_w 0.5021947622299194
      // cf84: dastore
      // cf85: dup
      // cf86: bipush 1
      // cf87: ldc2_w -2.7663203939320167E-8
      // cf8a: dastore
      // cf8b: aastore
      // cf8c: dup
      // cf8d: sipush 669
      // cf90: bipush 2
      // cf91: newarray 7
      // cf93: dup
      // cf94: bipush 0
      // cf95: ldc2_w 0.5027855634689331
      // cf98: dastore
      // cf99: dup
      // cf9a: bipush 1
      // cf9b: ldc2_w 1.3064749115929298E-8
      // cf9e: dastore
      // cf9f: aastore
      // cfa0: dup
      // cfa1: sipush 670
      // cfa4: bipush 2
      // cfa5: newarray 7
      // cfa7: dup
      // cfa8: bipush 0
      // cfa9: ldc2_w 0.5033761262893677
      // cfac: dastore
      // cfad: dup
      // cfae: bipush 1
      // cfaf: ldc2_w -5.667682106730711E-8
      // cfb2: dastore
      // cfb3: aastore
      // cfb4: dup
      // cfb5: sipush 671
      // cfb8: bipush 2
      // cfb9: newarray 7
      // cfbb: dup
      // cfbc: bipush 0
      // cfbd: ldc2_w 0.503966212272644
      // cfc0: dastore
      // cfc1: dup
      // cfc2: bipush 1
      // cfc3: ldc2_w 1.9424534974370594E-9
      // cfc6: dastore
      // cfc7: aastore
      // cfc8: dup
      // cfc9: sipush 672
      // cfcc: bipush 2
      // cfcd: newarray 7
      // cfcf: dup
      // cfd0: bipush 0
      // cfd1: ldc2_w 0.5045560598373413
      // cfd4: dastore
      // cfd5: dup
      // cfd6: bipush 1
      // cfd7: ldc2_w -4.908494602153544E-8
      // cfda: dastore
      // cfdb: aastore
      // cfdc: dup
      // cfdd: sipush 673
      // cfe0: bipush 2
      // cfe1: newarray 7
      // cfe3: dup
      // cfe4: bipush 0
      // cfe5: ldc2_w 0.5051454305648804
      // cfe8: dastore
      // cfe9: dup
      // cfea: bipush 1
      // cfeb: ldc2_w 2.906989285008994E-8
      // cfee: dastore
      // cfef: aastore
      // cff0: dup
      // cff1: sipush 674
      // cff4: bipush 2
      // cff5: newarray 7
      // cff7: dup
      // cff8: bipush 0
      // cff9: ldc2_w 0.5057345628738403
      // cffc: dastore
      // cffd: dup
      // cffe: bipush 1
      // cfff: ldc2_w -1.602000800745108E-9
      // d002: dastore
      // d003: aastore
      // d004: dup
      // d005: sipush 675
      // d008: bipush 2
      // d009: newarray 7
      // d00b: dup
      // d00c: bipush 0
      // d00d: ldc2_w 0.5063233375549316
      // d010: dastore
      // d011: dup
      // d012: bipush 1
      // d013: ldc2_w -2.148245271118002E-8
      // d016: dastore
      // d017: aastore
      // d018: dup
      // d019: sipush 676
      // d01c: bipush 2
      // d01d: newarray 7
      // d01f: dup
      // d020: bipush 0
      // d021: ldc2_w 0.5069117546081543
      // d024: dastore
      // d025: dup
      // d026: bipush 1
      // d027: ldc2_w -3.016329994276181E-8
      // d02a: dastore
      // d02b: aastore
      // d02c: dup
      // d02d: sipush 677
      // d030: bipush 2
      // d031: newarray 7
      // d033: dup
      // d034: bipush 0
      // d035: ldc2_w 0.5074998140335083
      // d038: dastore
      // d039: dup
      // d03a: bipush 1
      // d03b: ldc2_w -2.7237099632871992E-8
      // d03e: dastore
      // d03f: aastore
      // d040: dup
      // d041: sipush 678
      // d044: bipush 2
      // d045: newarray 7
      // d047: dup
      // d048: bipush 0
      // d049: ldc2_w 0.5080875158309937
      // d04c: dastore
      // d04d: dup
      // d04e: bipush 1
      // d04f: ldc2_w -1.2297127301923986E-8
      // d052: dastore
      // d053: aastore
      // d054: dup
      // d055: sipush 679
      // d058: bipush 2
      // d059: newarray 7
      // d05b: dup
      // d05c: bipush 0
      // d05d: ldc2_w 0.5086748600006104
      // d060: dastore
      // d061: dup
      // d062: bipush 1
      // d063: ldc2_w 1.5062624834468093E-8
      // d066: dastore
      // d067: aastore
      // d068: dup
      // d069: sipush 680
      // d06c: bipush 2
      // d06d: newarray 7
      // d06f: dup
      // d070: bipush 0
      // d071: ldc2_w 0.5092618465423584
      // d074: dastore
      // d075: dup
      // d076: bipush 1
      // d077: ldc2_w 5.524744954836658E-8
      // d07a: dastore
      // d07b: aastore
      // d07c: dup
      // d07d: sipush 681
      // d080: bipush 2
      // d081: newarray 7
      // d083: dup
      // d084: bipush 0
      // d085: ldc2_w 0.5098485946655273
      // d088: dastore
      // d089: dup
      // d08a: bipush 1
      // d08b: ldc2_w -1.054736327333046E-8
      // d08e: dastore
      // d08f: aastore
      // d090: dup
      // d091: sipush 682
      // d094: bipush 2
      // d095: newarray 7
      // d097: dup
      // d098: bipush 0
      // d099: ldc2_w 0.5104348659515381
      // d09c: dastore
      // d09d: dup
      // d09e: bipush 1
      // d09f: ldc2_w 5.650063324725722E-8
      // d0a2: dastore
      // d0a3: aastore
      // d0a4: dup
      // d0a5: sipush 683
      // d0a8: bipush 2
      // d0a9: newarray 7
      // d0ab: dup
      // d0ac: bipush 0
      // d0ad: ldc2_w 0.5110208988189697
      // d0b0: dastore
      // d0b1: dup
      // d0b2: bipush 1
      // d0b3: ldc2_w 1.8376017791642605E-8
      // d0b6: dastore
      // d0b7: aastore
      // d0b8: dup
      // d0b9: sipush 684
      // d0bc: bipush 2
      // d0bd: newarray 7
      // d0bf: dup
      // d0c0: bipush 0
      // d0c1: ldc2_w 0.5116065740585327
      // d0c4: dastore
      // d0c5: dup
      // d0c6: bipush 1
      // d0c7: ldc2_w -5.309470636324855E-9
      // d0ca: dastore
      // d0cb: aastore
      // d0cc: dup
      // d0cd: sipush 685
      // d0d0: bipush 2
      // d0d1: newarray 7
      // d0d3: dup
      // d0d4: bipush 0
      // d0d5: ldc2_w 0.512191891670227
      // d0d8: dastore
      // d0d9: dup
      // d0da: bipush 1
      // d0db: ldc2_w -1.4154089255217218E-8
      // d0de: dastore
      // d0df: aastore
      // d0e0: dup
      // d0e1: sipush 686
      // d0e4: bipush 2
      // d0e5: newarray 7
      // d0e7: dup
      // d0e8: bipush 0
      // d0e9: ldc2_w 0.5127768516540527
      // d0ec: dastore
      // d0ed: dup
      // d0ee: bipush 1
      // d0ef: ldc2_w -7.756800301729815E-9
      // d0f2: dastore
      // d0f3: aastore
      // d0f4: dup
      // d0f5: sipush 687
      // d0f8: bipush 2
      // d0f9: newarray 7
      // d0fb: dup
      // d0fc: bipush 0
      // d0fd: ldc2_w 0.5133614540100098
      // d100: dastore
      // d101: dup
      // d102: bipush 1
      // d103: ldc2_w 1.4282730618002001E-8
      // d106: dastore
      // d107: aastore
      // d108: dup
      // d109: sipush 688
      // d10c: bipush 2
      // d10d: newarray 7
      // d10f: dup
      // d110: bipush 0
      // d111: ldc2_w 0.5139456987380981
      // d114: dastore
      // d115: dup
      // d116: bipush 1
      // d117: ldc2_w 5.2364136172269755E-8
      // d11a: dastore
      // d11b: aastore
      // d11c: dup
      // d11d: sipush 689
      // d120: bipush 2
      // d121: newarray 7
      // d123: dup
      // d124: bipush 0
      // d125: ldc2_w 0.5145297050476074
      // d128: dastore
      // d129: dup
      // d12a: bipush 1
      // d12b: ldc2_w -1.2322940607922115E-8
      // d12e: dastore
      // d12f: aastore
      // d130: dup
      // d131: sipush 690
      // d134: bipush 2
      // d135: newarray 7
      // d137: dup
      // d138: bipush 0
      // d139: ldc2_w 0.5151132345199585
      // d13c: dastore
      // d13d: dup
      // d13e: bipush 1
      // d13f: ldc2_w 5.903831350855322E-8
      // d142: dastore
      // d143: aastore
      // d144: dup
      // d145: sipush 691
      // d148: bipush 2
      // d149: newarray 7
      // d14b: dup
      // d14c: bipush 0
      // d14d: ldc2_w 0.5156965255737305
      // d150: dastore
      // d151: dup
      // d152: bipush 1
      // d153: ldc2_w 2.8426856726994483E-8
      // d156: dastore
      // d157: aastore
      // d158: dup
      // d159: sipush 692
      // d15c: bipush 2
      // d15d: newarray 7
      // d15f: dup
      // d160: bipush 0
      // d161: ldc2_w 0.5162794589996338
      // d164: dastore
      // d165: dup
      // d166: bipush 1
      // d167: ldc2_w 1.544882070711032E-8
      // d16a: dastore
      // d16b: aastore
      // d16c: dup
      // d16d: sipush 693
      // d170: bipush 2
      // d171: newarray 7
      // d173: dup
      // d174: bipush 0
      // d175: ldc2_w 0.5168620347976685
      // d178: dastore
      // d179: dup
      // d17a: bipush 1
      // d17b: ldc2_w 2.0500353979930155E-8
      // d17e: dastore
      // d17f: aastore
      // d180: dup
      // d181: sipush 694
      // d184: bipush 2
      // d185: newarray 7
      // d187: dup
      // d188: bipush 0
      // d189: ldc2_w 0.5174442529678345
      // d18c: dastore
      // d18d: dup
      // d18e: bipush 1
      // d18f: ldc2_w 4.397691311390564E-8
      // d192: dastore
      // d193: aastore
      // d194: dup
      // d195: sipush 695
      // d198: bipush 2
      // d199: newarray 7
      // d19b: dup
      // d19c: bipush 0
      // d19d: ldc2_w 0.5180262327194214
      // d1a0: dastore
      // d1a1: dup
      // d1a2: bipush 1
      // d1a3: ldc2_w -3.2936025225250634E-8
      // d1a6: dastore
      // d1a7: aastore
      // d1a8: dup
      // d1a9: sipush 696
      // d1ac: bipush 2
      // d1ad: newarray 7
      // d1af: dup
      // d1b0: bipush 0
      // d1b1: ldc2_w 0.5186077356338501
      // d1b4: dastore
      // d1b5: dup
      // d1b6: bipush 1
      // d1b7: ldc2_w 2.857419553449673E-8
      // d1ba: dastore
      // d1bb: aastore
      // d1bc: dup
      // d1bd: sipush 697
      // d1c0: bipush 2
      // d1c1: newarray 7
      // d1c3: dup
      // d1c4: bipush 0
      // d1c5: ldc2_w 0.5191890001296997
      // d1c8: dastore
      // d1c9: dup
      // d1ca: bipush 1
      // d1cb: ldc2_w -9.51761338269325E-9
      // d1ce: dastore
      // d1cf: aastore
      // d1d0: dup
      // d1d1: sipush 698
      // d1d4: bipush 2
      // d1d5: newarray 7
      // d1d7: dup
      // d1d8: bipush 0
      // d1d9: ldc2_w 0.5197699069976807
      // d1dc: dastore
      // d1dd: dup
      // d1de: bipush 1
      // d1df: ldc2_w -2.7609457648450225E-8
      // d1e2: dastore
      // d1e3: aastore
      // d1e4: dup
      // d1e5: sipush 699
      // d1e8: bipush 2
      // d1e9: newarray 7
      // d1eb: dup
      // d1ec: bipush 0
      // d1ed: ldc2_w 0.520350456237793
      // d1f0: dastore
      // d1f1: dup
      // d1f2: bipush 1
      // d1f3: ldc2_w -2.5309316441333305E-8
      // d1f6: dastore
      // d1f7: aastore
      // d1f8: dup
      // d1f9: sipush 700
      // d1fc: bipush 2
      // d1fd: newarray 7
      // d1ff: dup
      // d200: bipush 0
      // d201: ldc2_w 0.5209306478500366
      // d204: dastore
      // d205: dup
      // d206: bipush 1
      // d207: ldc2_w -2.2258513086839407E-9
      // d20a: dastore
      // d20b: aastore
      // d20c: dup
      // d20d: sipush 701
      // d210: bipush 2
      // d211: newarray 7
      // d213: dup
      // d214: bipush 0
      // d215: ldc2_w 0.5215104818344116
      // d218: dastore
      // d219: dup
      // d21a: bipush 1
      // d21b: ldc2_w 4.203159541613745E-8
      // d21e: dastore
      // d21f: aastore
      // d220: dup
      // d221: sipush 702
      // d224: bipush 2
      // d225: newarray 7
      // d227: dup
      // d228: bipush 0
      // d229: ldc2_w 0.5220900774002075
      // d22c: dastore
      // d22d: dup
      // d22e: bipush 1
      // d22f: ldc2_w -1.1356287358852729E-8
      // d232: dastore
      // d233: aastore
      // d234: dup
      // d235: sipush 703
      // d238: bipush 2
      // d239: newarray 7
      // d23b: dup
      // d23c: bipush 0
      // d23d: ldc2_w 0.5226693153381348
      // d240: dastore
      // d241: dup
      // d242: bipush 1
      // d243: ldc2_w -4.279090925831093E-8
      // d246: dastore
      // d247: aastore
      // d248: dup
      // d249: sipush 704
      // d24c: bipush 2
      // d24d: newarray 7
      // d24f: dup
      // d250: bipush 0
      // d251: ldc2_w 0.5232481956481934
      // d254: dastore
      // d255: dup
      // d256: bipush 1
      // d257: ldc2_w -5.188364552285819E-8
      // d25a: dastore
      // d25b: aastore
      // d25c: dup
      // d25d: sipush 705
      // d260: bipush 2
      // d261: newarray 7
      // d263: dup
      // d264: bipush 0
      // d265: ldc2_w 0.5238267183303833
      // d268: dastore
      // d269: dup
      // d26a: bipush 1
      // d26b: ldc2_w -3.82465458937857E-8
      // d26e: dastore
      // d26f: aastore
      // d270: dup
      // d271: sipush 706
      // d274: bipush 2
      // d275: newarray 7
      // d277: dup
      // d278: bipush 0
      // d279: ldc2_w 0.5244048833847046
      // d27c: dastore
      // d27d: dup
      // d27e: bipush 1
      // d27f: ldc2_w -1.4923330530645769E-9
      // d282: dastore
      // d283: aastore
      // d284: dup
      // d285: sipush 707
      // d288: bipush 2
      // d289: newarray 7
      // d28b: dup
      // d28c: bipush 0
      // d28d: ldc2_w 0.5249826908111572
      // d290: dastore
      // d291: dup
      // d292: bipush 1
      // d293: ldc2_w 5.8765598932137004E-8
      // d296: dastore
      // d297: aastore
      // d298: dup
      // d299: sipush 708
      // d29c: bipush 2
      // d29d: newarray 7
      // d29f: dup
      // d2a0: bipush 0
      // d2a1: ldc2_w 0.5255602598190308
      // d2a4: dastore
      // d2a5: dup
      // d2a6: bipush 1
      // d2a7: ldc2_w 2.3703896609663678E-8
      // d2aa: dastore
      // d2ab: aastore
      // d2ac: dup
      // d2ad: sipush 709
      // d2b0: bipush 2
      // d2b1: newarray 7
      // d2b3: dup
      // d2b4: bipush 0
      // d2b5: ldc2_w 0.5261374711990356
      // d2b8: dastore
      // d2b9: dup
      // d2ba: bipush 1
      // d2bb: ldc2_w 1.2917117341231647E-8
      // d2be: dastore
      // d2bf: aastore
      // d2c0: dup
      // d2c1: sipush 710
      // d2c4: bipush 2
      // d2c5: newarray 7
      // d2c7: dup
      // d2c8: bipush 0
      // d2c9: ldc2_w 0.5267143249511719
      // d2cc: dastore
      // d2cd: dup
      // d2ce: bipush 1
      // d2cf: ldc2_w 2.6789862192139226E-8
      // d2d2: dastore
      // d2d3: aastore
      // d2d4: dup
      // d2d5: sipush 711
      // d2d8: bipush 2
      // d2d9: newarray 7
      // d2db: dup
      // d2dc: bipush 0
      // d2dd: ldc2_w 0.527290940284729
      // d2e0: dastore
      // d2e1: dup
      // d2e2: bipush 1
      // d2e3: ldc2_w -5.350322253112414E-8
      // d2e6: dastore
      // d2e7: aastore
      // d2e8: dup
      // d2e9: sipush 712
      // d2ec: bipush 2
      // d2ed: newarray 7
      // d2ef: dup
      // d2f0: bipush 0
      // d2f1: ldc2_w 0.5278670787811279
      // d2f4: dastore
      // d2f5: dup
      // d2f6: bipush 1
      // d2f7: ldc2_w 1.0839714455426386E-8
      // d2fa: dastore
      // d2fb: aastore
      // d2fc: dup
      // d2fd: sipush 713
      // d300: bipush 2
      // d301: newarray 7
      // d303: dup
      // d304: bipush 0
      // d305: ldc2_w 0.5284429788589478
      // d308: dastore
      // d309: dup
      // d30a: bipush 1
      // d30b: ldc2_w -1.821729591343314E-8
      // d30e: dastore
      // d30f: aastore
      // d310: dup
      // d311: sipush 714
      // d314: bipush 2
      // d315: newarray 7
      // d317: dup
      // d318: bipush 0
      // d319: ldc2_w 0.5290185213088989
      // d31c: dastore
      // d31d: dup
      // d31e: bipush 1
      // d31f: ldc2_w -2.1083014672301448E-8
      // d322: dastore
      // d323: aastore
      // d324: dup
      // d325: sipush 715
      // d328: bipush 2
      // d329: newarray 7
      // d32b: dup
      // d32c: bipush 0
      // d32d: ldc2_w 0.5295937061309814
      // d330: dastore
      // d331: dup
      // d332: bipush 1
      // d333: ldc2_w 2.623848491704216E-9
      // d336: dastore
      // d337: aastore
      // d338: dup
      // d339: sipush 716
      // d33c: bipush 2
      // d33d: newarray 7
      // d33f: dup
      // d340: bipush 0
      // d341: ldc2_w 0.5301685333251953
      // d344: dastore
      // d345: dup
      // d346: bipush 1
      // d347: ldc2_w 5.328392630534142E-8
      // d34a: dastore
      // d34b: aastore
      // d34c: dup
      // d34d: sipush 717
      // d350: bipush 2
      // d351: newarray 7
      // d353: dup
      // d354: bipush 0
      // d355: ldc2_w 0.5307431221008301
      // d358: dastore
      // d359: dup
      // d35a: bipush 1
      // d35b: ldc2_w 1.206790586971942E-8
      // d35e: dastore
      // d35f: aastore
      // d360: dup
      // d361: sipush 718
      // d364: bipush 2
      // d365: newarray 7
      // d367: dup
      // d368: bipush 0
      // d369: ldc2_w 0.5313173532485962
      // d36c: dastore
      // d36d: dup
      // d36e: bipush 1
      // d36f: ldc2_w -1.4356011804377797E-9
      // d372: dastore
      // d373: aastore
      // d374: dup
      // d375: sipush 719
      // d378: bipush 2
      // d379: newarray 7
      // d37b: dup
      // d37c: bipush 0
      // d37d: ldc2_w 0.5318912267684937
      // d380: dastore
      // d381: dup
      // d382: bipush 1
      // d383: ldc2_w 1.3152074173459994E-8
      // d386: dastore
      // d387: aastore
      // d388: dup
      // d389: sipush 720
      // d38c: bipush 2
      // d38d: newarray 7
      // d38f: dup
      // d390: bipush 0
      // d391: ldc2_w 0.5324647426605225
      // d394: dastore
      // d395: dup
      // d396: bipush 1
      // d397: ldc2_w 5.6208949382936426E-8
      // d39a: dastore
      // d39b: aastore
      // d39c: dup
      // d39d: sipush 721
      // d3a0: bipush 2
      // d3a1: newarray 7
      // d3a3: dup
      // d3a4: bipush 0
      // d3a5: ldc2_w 0.5330380201339722
      // d3a8: dastore
      // d3a9: dup
      // d3aa: bipush 1
      // d3ab: ldc2_w 8.90310227565917E-9
      // d3ae: dastore
      // d3af: aastore
      // d3b0: dup
      // d3b1: sipush 722
      // d3b4: bipush 2
      // d3b5: newarray 7
      // d3b7: dup
      // d3b8: bipush 0
      // d3b9: ldc2_w 0.5336109399795532
      // d3bc: dastore
      // d3bd: dup
      // d3be: bipush 1
      // d3bf: ldc2_w -9.179458802504127E-9
      // d3c2: dastore
      // d3c3: aastore
      // d3c4: dup
      // d3c5: sipush 723
      // d3c8: bipush 2
      // d3c9: newarray 7
      // d3cb: dup
      // d3cc: bipush 0
      // d3cd: ldc2_w 0.5341835021972656
      // d3d0: dastore
      // d3d1: dup
      // d3d2: bipush 1
      // d3d3: ldc2_w 2.337337845617735E-9
      // d3d6: dastore
      // d3d7: aastore
      // d3d8: dup
      // d3d9: sipush 724
      // d3dc: bipush 2
      // d3dd: newarray 7
      // d3df: dup
      // d3e0: bipush 0
      // d3e1: ldc2_w 0.5347557067871094
      // d3e4: dastore
      // d3e5: dup
      // d3e6: bipush 1
      // d3e7: ldc2_w 4.3828918300477925E-8
      // d3ea: dastore
      // d3eb: aastore
      // d3ec: dup
      // d3ed: sipush 725
      // d3f0: bipush 2
      // d3f1: newarray 7
      // d3f3: dup
      // d3f4: bipush 0
      // d3f5: ldc2_w 0.535327672958374
      // d3f8: dastore
      // d3f9: dup
      // d3fa: bipush 1
      // d3fb: ldc2_w -3.5392250480081715E-9
      // d3fe: dastore
      // d3ff: aastore
      // d400: dup
      // d401: sipush 726
      // d404: bipush 2
      // d405: newarray 7
      // d407: dup
      // d408: bipush 0
      // d409: ldc2_w 0.53589928150177
      // d40c: dastore
      // d40d: dup
      // d40e: bipush 1
      // d40f: ldc2_w -2.0183663375378704E-8
      // d412: dastore
      // d413: aastore
      // d414: dup
      // d415: sipush 727
      // d418: bipush 2
      // d419: newarray 7
      // d41b: dup
      // d41c: bipush 0
      // d41d: ldc2_w 0.5364705324172974
      // d420: dastore
      // d421: dup
      // d422: bipush 1
      // d423: ldc2_w -5.730898606435436E-9
      // d426: dastore
      // d427: aastore
      // d428: dup
      // d429: sipush 728
      // d42c: bipush 2
      // d42d: newarray 7
      // d42f: dup
      // d430: bipush 0
      // d431: ldc2_w 0.537041425704956
      // d434: dastore
      // d435: dup
      // d436: bipush 1
      // d437: ldc2_w 4.0191927599879235E-8
      // d43a: dastore
      // d43b: aastore
      // d43c: dup
      // d43d: sipush 729
      // d440: bipush 2
      // d441: newarray 7
      // d443: dup
      // d444: bipush 0
      // d445: ldc2_w 0.5376120805740356
      // d448: dastore
      // d449: dup
      // d44a: bipush 1
      // d44b: ldc2_w -1.2522542401353875E-9
      // d44e: dastore
      // d44f: aastore
      // d450: dup
      // d451: sipush 730
      // d454: bipush 2
      // d455: newarray 7
      // d457: dup
      // d458: bipush 0
      // d459: ldc2_w 0.5381823778152466
      // d45c: dastore
      // d45d: dup
      // d45e: bipush 1
      // d45f: ldc2_w -1.0482571326594316E-8
      // d462: dastore
      // d463: aastore
      // d464: dup
      // d465: sipush 731
      // d468: bipush 2
      // d469: newarray 7
      // d46b: dup
      // d46c: bipush 0
      // d46d: ldc2_w 0.5387523174285889
      // d470: dastore
      // d471: dup
      // d472: bipush 1
      // d473: ldc2_w 1.2871924223480165E-8
      // d476: dastore
      // d477: aastore
      // d478: dup
      // d479: sipush 732
      // d47c: bipush 2
      // d47d: newarray 7
      // d47f: dup
      // d480: bipush 0
      // d481: ldc2_w 0.539322018623352
      // d484: dastore
      // d485: dup
      // d486: bipush 1
      // d487: ldc2_w -5.002774317612589E-8
      // d48a: dastore
      // d48b: aastore
      // d48c: dup
      // d48d: sipush 733
      // d490: bipush 2
      // d491: newarray 7
      // d493: dup
      // d494: bipush 0
      // d495: ldc2_w 0.539891242980957
      // d498: dastore
      // d499: dup
      // d49a: bipush 1
      // d49b: ldc2_w 3.960668706590162E-8
      // d49e: dastore
      // d49f: aastore
      // d4a0: dup
      // d4a1: sipush 734
      // d4a4: bipush 2
      // d4a5: newarray 7
      // d4a7: dup
      // d4a8: bipush 0
      // d4a9: ldc2_w 0.5404602289199829
      // d4ac: dastore
      // d4ad: dup
      // d4ae: bipush 1
      // d4af: ldc2_w 4.372568630242375E-8
      // d4b2: dastore
      // d4b3: aastore
      // d4b4: dup
      // d4b5: sipush 735
      // d4b8: bipush 2
      // d4b9: newarray 7
      // d4bb: dup
      // d4bc: bipush 0
      // d4bd: ldc2_w 0.5410289764404297
      // d4c0: dastore
      // d4c1: dup
      // d4c2: bipush 1
      // d4c3: ldc2_w -3.730232461206926E-8
      // d4c6: dastore
      // d4c7: aastore
      // d4c8: dup
      // d4c9: sipush 736
      // d4cc: bipush 2
      // d4cd: newarray 7
      // d4cf: dup
      // d4d0: bipush 0
      // d4d1: ldc2_w 0.5415972471237183
      // d4d4: dastore
      // d4d5: dup
      // d4d6: bipush 1
      // d4d7: ldc2_w 3.5309026109857795E-8
      // d4da: dastore
      // d4db: aastore
      // d4dc: dup
      // d4dd: sipush 737
      // d4e0: bipush 2
      // d4e1: newarray 7
      // d4e3: dup
      // d4e4: bipush 0
      // d4e5: ldc2_w 0.5421652793884277
      // d4e8: dastore
      // d4e9: dup
      // d4ea: bipush 1
      // d4eb: ldc2_w 2.3508325311148225E-8
      // d4ee: dastore
      // d4ef: aastore
      // d4f0: dup
      // d4f1: sipush 738
      // d4f4: bipush 2
      // d4f5: newarray 7
      // d4f7: dup
      // d4f8: bipush 0
      // d4f9: ldc2_w 0.5427329540252686
      // d4fc: dastore
      // d4fd: dup
      // d4fe: bipush 1
      // d4ff: ldc2_w 4.6871403168921666E-8
      // d502: dastore
      // d503: aastore
      // d504: dup
      // d505: sipush 739
      // d508: bipush 2
      // d509: newarray 7
      // d50b: dup
      // d50c: bipush 0
      // d50d: ldc2_w 0.5433003902435303
      // d510: dastore
      // d511: dup
      // d512: bipush 1
      // d513: ldc2_w -1.3445113140270216E-8
      // d516: dastore
      // d517: aastore
      // d518: dup
      // d519: sipush 740
      // d51c: bipush 2
      // d51d: newarray 7
      // d51f: dup
      // d520: bipush 0
      // d521: ldc2_w 0.5438674688339233
      // d524: dastore
      // d525: dup
      // d526: bipush 1
      // d527: ldc2_w -3.786663982218041E-8
      // d52a: dastore
      // d52b: aastore
      // d52c: dup
      // d52d: sipush 741
      // d530: bipush 2
      // d531: newarray 7
      // d533: dup
      // d534: bipush 0
      // d535: ldc2_w 0.5444341897964478
      // d538: dastore
      // d539: dup
      // d53a: bipush 1
      // d53b: ldc2_w -2.602850370608209E-8
      // d53e: dastore
      // d53f: aastore
      // d540: dup
      // d541: sipush 742
      // d544: bipush 2
      // d545: newarray 7
      // d547: dup
      // d548: bipush 0
      // d549: ldc2_w 0.5450005531311035
      // d54c: dastore
      // d54d: dup
      // d54e: bipush 1
      // d54f: ldc2_w 2.2433348713144506E-8
      // d552: dastore
      // d553: aastore
      // d554: dup
      // d555: sipush 743
      // d558: bipush 2
      // d559: newarray 7
      // d55b: dup
      // d55c: bipush 0
      // d55d: ldc2_w 0.5455666780471802
      // d560: dastore
      // d561: dup
      // d562: bipush 1
      // d563: ldc2_w -1.1326936872620137E-8
      // d566: dastore
      // d567: aastore
      // d568: dup
      // d569: sipush 744
      // d56c: bipush 2
      // d56d: newarray 7
      // d56f: dup
      // d570: bipush 0
      // d571: ldc2_w 0.5461324453353882
      // d574: dastore
      // d575: dup
      // d576: bipush 1
      // d577: ldc2_w -7.737252533211342E-9
      // d57a: dastore
      // d57b: aastore
      // d57c: dup
      // d57d: sipush 745
      // d580: bipush 2
      // d581: newarray 7
      // d583: dup
      // d584: bipush 0
      // d585: ldc2_w 0.5466978549957275
      // d588: dastore
      // d589: dup
      // d58a: bipush 1
      // d58b: ldc2_w 3.3564604642699844E-8
      // d58e: dastore
      // d58f: aastore
      // d590: dup
      // d591: sipush 746
      // d594: bipush 2
      // d595: newarray 7
      // d597: dup
      // d598: bipush 0
      // d599: ldc2_w 0.5472630262374878
      // d59c: dastore
      // d59d: dup
      // d59e: bipush 1
      // d59f: ldc2_w -6.269066061111782E-9
      // d5a2: dastore
      // d5a3: aastore
      // d5a4: dup
      // d5a5: sipush 747
      // d5a8: bipush 2
      // d5a9: newarray 7
      // d5ab: dup
      // d5ac: bipush 0
      // d5ad: ldc2_w 0.5478278398513794
      // d5b0: dastore
      // d5b1: dup
      // d5b2: bipush 1
      // d5b3: ldc2_w -7.667998948729528E-9
      // d5b6: dastore
      // d5b7: aastore
      // d5b8: dup
      // d5b9: sipush 748
      // d5bc: bipush 2
      // d5bd: newarray 7
      // d5bf: dup
      // d5c0: bipush 0
      // d5c1: ldc2_w 0.5483922958374023
      // d5c4: dastore
      // d5c5: dup
      // d5c6: bipush 1
      // d5c7: ldc2_w 2.9728170818998143E-8
      // d5ca: dastore
      // d5cb: aastore
      // d5cc: dup
      // d5cd: sipush 749
      // d5d0: bipush 2
      // d5d1: newarray 7
      // d5d3: dup
      // d5d4: bipush 0
      // d5d5: ldc2_w 0.5489565134048462
      // d5d8: dastore
      // d5d9: dup
      // d5da: bipush 1
      // d5db: ldc2_w -1.2930091396008281E-8
      // d5de: dastore
      // d5df: aastore
      // d5e0: dup
      // d5e1: sipush 750
      // d5e4: bipush 2
      // d5e5: newarray 7
      // d5e7: dup
      // d5e8: bipush 0
      // d5e9: ldc2_w 0.5495203733444214
      // d5ec: dastore
      // d5ed: dup
      // d5ee: bipush 1
      // d5ef: ldc2_w -1.607434968107079E-8
      // d5f2: dastore
      // d5f3: aastore
      // d5f4: dup
      // d5f5: sipush 751
      // d5f8: bipush 2
      // d5f9: newarray 7
      // d5fb: dup
      // d5fc: bipush 0
      // d5fd: ldc2_w 0.5500838756561279
      // d600: dastore
      // d601: dup
      // d602: bipush 1
      // d603: ldc2_w 2.0653935146671156E-8
      // d606: dastore
      // d607: aastore
      // d608: dup
      // d609: sipush 752
      // d60c: bipush 2
      // d60d: newarray 7
      // d60f: dup
      // d610: bipush 0
      // d611: ldc2_w 0.5506471395492554
      // d614: dastore
      // d615: dup
      // d616: bipush 1
      // d617: ldc2_w -2.1596593091833788E-8
      // d61a: dastore
      // d61b: aastore
      // d61c: dup
      // d61d: sipush 753
      // d620: bipush 2
      // d621: newarray 7
      // d623: dup
      // d624: bipush 0
      // d625: ldc2_w 0.5512100458145142
      // d628: dastore
      // d629: dup
      // d62a: bipush 1
      // d62b: ldc2_w -2.3259315921149476E-8
      // d62e: dastore
      // d62f: aastore
      // d630: dup
      // d631: sipush 754
      // d634: bipush 2
      // d635: newarray 7
      // d637: dup
      // d638: bipush 0
      // d639: ldc2_w 0.5517725944519043
      // d63c: dastore
      // d63d: dup
      // d63e: bipush 1
      // d63f: ldc2_w 1.6022492496522704E-8
      // d642: dastore
      // d643: aastore
      // d644: dup
      // d645: sipush 755
      // d648: bipush 2
      // d649: newarray 7
      // d64b: dup
      // d64c: bipush 0
      // d64d: ldc2_w 0.5523349046707153
      // d650: dastore
      // d651: dup
      // d652: bipush 1
      // d653: ldc2_w -2.260433328226171E-8
      // d656: dastore
      // d657: aastore
      // d658: dup
      // d659: sipush 756
      // d65c: bipush 2
      // d65d: newarray 7
      // d65f: dup
      // d660: bipush 0
      // d661: ldc2_w 0.5528968572616577
      // d664: dastore
      // d665: dup
      // d666: bipush 1
      // d667: ldc2_w -1.957497997726303E-8
      // d66a: dastore
      // d66b: aastore
      // d66c: dup
      // d66d: sipush 757
      // d670: bipush 2
      // d671: newarray 7
      // d673: dup
      // d674: bipush 0
      // d675: ldc2_w 0.5534584522247314
      // d678: dastore
      // d679: dup
      // d67a: bipush 1
      // d67b: ldc2_w 2.5465477111883854E-8
      // d67e: dastore
      // d67f: aastore
      // d680: dup
      // d681: sipush 758
      // d684: bipush 2
      // d685: newarray 7
      // d687: dup
      // d688: bipush 0
      // d689: ldc2_w 0.5540198087692261
      // d68c: dastore
      // d68d: dup
      // d68e: bipush 1
      // d68f: ldc2_w -6.33792454933092E-9
      // d692: dastore
      // d693: aastore
      // d694: dup
      // d695: sipush 759
      // d698: bipush 2
      // d699: newarray 7
      // d69b: dup
      // d69c: bipush 0
      // d69d: ldc2_w 0.554580807685852
      // d6a0: dastore
      // d6a1: dup
      // d6a2: bipush 1
      // d6a3: ldc2_w 4.577835263278281E-9
      // d6a6: dastore
      // d6a7: aastore
      // d6a8: dup
      // d6a9: sipush 760
      // d6ac: bipush 2
      // d6ad: newarray 7
      // d6af: dup
      // d6b0: bipush 0
      // d6b1: ldc2_w 0.5551414489746094
      // d6b4: dastore
      // d6b5: dup
      // d6b6: bipush 1
      // d6b7: ldc2_w 5.856589221771548E-8
      // d6ba: dastore
      // d6bb: aastore
      // d6bc: dup
      // d6bd: sipush 761
      // d6c0: bipush 2
      // d6c1: newarray 7
      // d6c3: dup
      // d6c4: bipush 0
      // d6c5: ldc2_w 0.5557018518447876
      // d6c8: dastore
      // d6c9: dup
      // d6ca: bipush 1
      // d6cb: ldc2_w 3.6769498759522324E-8
      // d6ce: dastore
      // d6cf: aastore
      // d6d0: dup
      // d6d1: sipush 762
      // d6d4: bipush 2
      // d6d5: newarray 7
      // d6d7: dup
      // d6d8: bipush 0
      // d6d9: ldc2_w 0.5562618970870972
      // d6dc: dastore
      // d6dd: dup
      // d6de: bipush 1
      // d6df: ldc2_w 5.874989409410614E-8
      // d6e2: dastore
      // d6e3: aastore
      // d6e4: dup
      // d6e5: sipush 763
      // d6e8: bipush 2
      // d6e9: newarray 7
      // d6eb: dup
      // d6ec: bipush 0
      // d6ed: ldc2_w 0.5568217039108276
      // d6f0: dastore
      // d6f1: dup
      // d6f2: bipush 1
      // d6f3: ldc2_w 5.649147309876989E-9
      // d6f6: dastore
      // d6f7: aastore
      // d6f8: dup
      // d6f9: sipush 764
      // d6fc: bipush 2
      // d6fd: newarray 7
      // d6ff: dup
      // d700: bipush 0
      // d701: ldc2_w 0.5573811531066895
      // d704: dastore
      // d705: dup
      // d706: bipush 1
      // d707: ldc2_w -2.9726830960751796E-9
      // d70a: dastore
      // d70b: aastore
      // d70c: dup
      // d70d: sipush 765
      // d710: bipush 2
      // d711: newarray 7
      // d713: dup
      // d714: bipush 0
      // d715: ldc2_w 0.5579402446746826
      // d718: dastore
      // d719: dup
      // d71a: bipush 1
      // d71b: ldc2_w 3.323458344853057E-8
      // d71e: dastore
      // d71f: aastore
      // d720: dup
      // d721: sipush 766
      // d724: bipush 2
      // d725: newarray 7
      // d727: dup
      // d728: bipush 0
      // d729: ldc2_w 0.5584990978240967
      // d72c: dastore
      // d72d: dup
      // d72e: bipush 1
      // d72f: ldc2_w -4.588749093664028E-9
      // d732: dastore
      // d733: aastore
      // d734: dup
      // d735: sipush 767
      // d738: bipush 2
      // d739: newarray 7
      // d73b: dup
      // d73c: bipush 0
      // d73d: ldc2_w 0.5590575933456421
      // d740: dastore
      // d741: dup
      // d742: bipush 1
      // d743: ldc2_w 3.115616594184543E-9
      // d746: dastore
      // d747: aastore
      // d748: dup
      // d749: sipush 768
      // d74c: bipush 2
      // d74d: newarray 7
      // d74f: dup
      // d750: bipush 0
      // d751: ldc2_w 0.5596157312393188
      // d754: dastore
      // d755: dup
      // d756: bipush 1
      // d757: ldc2_w 5.6696103838614634E-8
      // d75a: dastore
      // d75b: aastore
      // d75c: dup
      // d75d: sipush 769
      // d760: bipush 2
      // d761: newarray 7
      // d763: dup
      // d764: bipush 0
      // d765: ldc2_w 0.5601736307144165
      // d768: dastore
      // d769: dup
      // d76a: bipush 1
      // d76b: ldc2_w 3.7291263280048303E-8
      // d76e: dastore
      // d76f: aastore
      // d770: dup
      // d771: sipush 770
      // d774: bipush 2
      // d775: newarray 7
      // d777: dup
      // d778: bipush 0
      // d779: ldc2_w 0.5607312917709351
      // d77c: dastore
      // d77d: dup
      // d77e: bipush 1
      // d77f: ldc2_w -5.4751646725093355E-8
      // d782: dastore
      // d783: aastore
      // d784: dup
      // d785: sipush 771
      // d788: bipush 2
      // d789: newarray 7
      // d78b: dup
      // d78c: bipush 0
      // d78d: ldc2_w 0.5612884759902954
      // d790: dastore
      // d791: dup
      // d792: bipush 1
      // d793: ldc2_w 1.9332630743320287E-8
      // d796: dastore
      // d797: aastore
      // d798: dup
      // d799: sipush 772
      // d79c: bipush 2
      // d79d: newarray 7
      // d79f: dup
      // d7a0: bipush 0
      // d7a1: ldc2_w 0.5618454217910767
      // d7a4: dastore
      // d7a5: dup
      // d7a6: bipush 1
      // d7a7: ldc2_w 2.147161515775941E-8
      // d7aa: dastore
      // d7ab: aastore
      // d7ac: dup
      // d7ad: sipush 773
      // d7b0: bipush 2
      // d7b1: newarray 7
      // d7b3: dup
      // d7b4: bipush 0
      // d7b5: ldc2_w 0.5624021291732788
      // d7b8: dastore
      // d7b9: dup
      // d7ba: bipush 1
      // d7bb: ldc2_w -4.7989172862560625E-8
      // d7be: dastore
      // d7bf: aastore
      // d7c0: dup
      // d7c1: sipush 774
      // d7c4: bipush 2
      // d7c5: newarray 7
      // d7c7: dup
      // d7c8: bipush 0
      // d7c9: ldc2_w 0.5629583597183228
      // d7cc: dastore
      // d7cd: dup
      // d7ce: bipush 1
      // d7cf: ldc2_w 4.971378973445109E-8
      // d7d2: dastore
      // d7d3: aastore
      // d7d4: dup
      // d7d5: sipush 775
      // d7d8: bipush 2
      // d7d9: newarray 7
      // d7db: dup
      // d7dc: bipush 0
      // d7dd: ldc2_w 0.5635144710540771
      // d7e0: dastore
      // d7e1: dup
      // d7e2: bipush 1
      // d7e3: ldc2_w -4.2702997139152675E-8
      // d7e6: dastore
      // d7e7: aastore
      // d7e8: dup
      // d7e9: sipush 776
      // d7ec: bipush 2
      // d7ed: newarray 7
      // d7ef: dup
      // d7f0: bipush 0
      // d7f1: ldc2_w 0.5640701055526733
      // d7f4: dastore
      // d7f5: dup
      // d7f6: bipush 1
      // d7f7: ldc2_w 3.273212962622764E-8
      // d7fa: dastore
      // d7fb: aastore
      // d7fc: dup
      // d7fd: sipush 777
      // d800: bipush 2
      // d801: newarray 7
      // d803: dup
      // d804: bipush 0
      // d805: ldc2_w 0.5646255016326904
      // d808: dastore
      // d809: dup
      // d80a: bipush 1
      // d80b: ldc2_w 3.79438125545842E-8
      // d80e: dastore
      // d80f: aastore
      // d810: dup
      // d811: sipush 778
      // d814: bipush 2
      // d815: newarray 7
      // d817: dup
      // d818: bipush 0
      // d819: ldc2_w 0.5651806592941284
      // d81c: dastore
      // d81d: dup
      // d81e: bipush 1
      // d81f: ldc2_w -2.6725298288329835E-8
      // d822: dastore
      // d823: aastore
      // d824: dup
      // d825: sipush 779
      // d828: bipush 2
      // d829: newarray 7
      // d82b: dup
      // d82c: bipush 0
      // d82d: ldc2_w 0.5657354593276978
      // d830: dastore
      // d831: dup
      // d832: bipush 1
      // d833: ldc2_w -4.1723833577410244E-8
      // d836: dastore
      // d837: aastore
      // d838: dup
      // d839: sipush 780
      // d83c: bipush 2
      // d83d: newarray 7
      // d83f: dup
      // d840: bipush 0
      // d841: ldc2_w 0.5662899017333984
      // d844: dastore
      // d845: dup
      // d846: bipush 1
      // d847: ldc2_w -6.71028256490915E-9
      // d84a: dastore
      // d84b: aastore
      // d84c: dup
      // d84d: sipush 781
      // d850: bipush 2
      // d851: newarray 7
      // d853: dup
      // d854: bipush 0
      // d855: ldc2_w 0.56684410572052
      // d858: dastore
      // d859: dup
      // d85a: bipush 1
      // d85b: ldc2_w -4.055299181908475E-8
      // d85e: dastore
      // d85f: aastore
      // d860: dup
      // d861: sipush 782
      // d864: bipush 2
      // d865: newarray 7
      // d867: dup
      // d868: bipush 0
      // d869: ldc2_w 0.567397952079773
      // d86c: dastore
      // d86d: dup
      // d86e: bipush 1
      // d86f: ldc2_w -2.3702295314000405E-8
      // d872: dastore
      // d873: aastore
      // d874: dup
      // d875: sipush 783
      // d878: bipush 2
      // d879: newarray 7
      // d87b: dup
      // d87c: bipush 0
      // d87d: ldc2_w 0.5679514408111572
      // d880: dastore
      // d881: dup
      // d882: bipush 1
      // d883: ldc2_w 4.4181618172507453E-8
      // d886: dastore
      // d887: aastore
      // d888: dup
      // d889: sipush 784
      // d88c: bipush 2
      // d88d: newarray 7
      // d88f: dup
      // d890: bipush 0
      // d891: ldc2_w 0.5685046911239624
      // d894: dastore
      // d895: dup
      // d896: bipush 1
      // d897: ldc2_w 4.4228706309734985E-8
      // d89a: dastore
      // d89b: aastore
      // d89c: dup
      // d89d: sipush 785
      // d8a0: bipush 2
      // d8a1: newarray 7
      // d8a3: dup
      // d8a4: bipush 0
      // d8a5: ldc2_w 0.5690577030181885
      // d8a8: dastore
      // d8a9: dup
      // d8aa: bipush 1
      // d8ab: ldc2_w -2.3222346436879016E-8
      // d8ae: dastore
      // d8af: aastore
      // d8b0: dup
      // d8b1: sipush 786
      // d8b4: bipush 2
      // d8b5: newarray 7
      // d8b7: dup
      // d8b8: bipush 0
      // d8b9: ldc2_w 0.5696103572845459
      // d8bc: dastore
      // d8bd: dup
      // d8be: bipush 1
      // d8bf: ldc2_w -3.862412756175274E-8
      // d8c2: dastore
      // d8c3: aastore
      // d8c4: dup
      // d8c5: sipush 787
      // d8c8: bipush 2
      // d8c9: newarray 7
      // d8cb: dup
      // d8cc: bipush 0
      // d8cd: ldc2_w 0.5701626539230347
      // d8d0: dastore
      // d8d1: dup
      // d8d2: bipush 1
      // d8d3: ldc2_w -1.6390743801589046E-9
      // d8d6: dastore
      // d8d7: aastore
      // d8d8: dup
      // d8d9: sipush 788
      // d8dc: bipush 2
      // d8dd: newarray 7
      // d8df: dup
      // d8e0: bipush 0
      // d8e1: ldc2_w 0.5707147121429443
      // d8e4: dastore
      // d8e5: dup
      // d8e6: bipush 1
      // d8e7: ldc2_w -3.1139472791083883E-8
      // d8ea: dastore
      // d8eb: aastore
      // d8ec: dup
      // d8ed: sipush 789
      // d8f0: bipush 2
      // d8f1: newarray 7
      // d8f3: dup
      // d8f4: bipush 0
      // d8f5: ldc2_w 0.5712664127349854
      // d8f8: dastore
      // d8f9: dup
      // d8fa: bipush 1
      // d8fb: ldc2_w -7.579587391156013E-9
      // d8fe: dastore
      // d8ff: aastore
      // d900: dup
      // d901: sipush 790
      // d904: bipush 2
      // d905: newarray 7
      // d907: dup
      // d908: bipush 0
      // d909: ldc2_w 0.5718178749084473
      // d90c: dastore
      // d90d: dup
      // d90e: bipush 1
      // d90f: ldc2_w -4.983281844744412E-8
      // d912: dastore
      // d913: aastore
      // d914: dup
      // d915: sipush 791
      // d918: bipush 2
      // d919: newarray 7
      // d91b: dup
      // d91c: bipush 0
      // d91d: ldc2_w 0.5723689794540405
      // d920: dastore
      // d921: dup
      // d922: bipush 1
      // d923: ldc2_w -3.835454246739619E-8
      // d926: dastore
      // d927: aastore
      // d928: dup
      // d929: sipush 792
      // d92c: bipush 2
      // d92d: newarray 7
      // d92f: dup
      // d930: bipush 0
      // d931: ldc2_w 0.5729197263717651
      // d934: dastore
      // d935: dup
      // d936: bipush 1
      // d937: ldc2_w 2.7190020372374008E-8
      // d93a: dastore
      // d93b: aastore
      // d93c: dup
      // d93d: sipush 793
      // d940: bipush 2
      // d941: newarray 7
      // d943: dup
      // d944: bipush 0
      // d945: ldc2_w 0.5734702348709106
      // d948: dastore
      // d949: dup
      // d94a: bipush 1
      // d94b: ldc2_w 2.7925807446276126E-8
      // d94e: dastore
      // d94f: aastore
      // d950: dup
      // d951: sipush 794
      // d954: bipush 2
      // d955: newarray 7
      // d957: dup
      // d958: bipush 0
      // d959: ldc2_w 0.574020504951477
      // d95c: dastore
      // d95d: dup
      // d95e: bipush 1
      // d95f: ldc2_w -3.5813506001861646E-8
      // d962: dastore
      // d963: aastore
      // d964: dup
      // d965: sipush 795
      // d968: bipush 2
      // d969: newarray 7
      // d96b: dup
      // d96c: bipush 0
      // d96d: ldc2_w 0.5745704174041748
      // d970: dastore
      // d971: dup
      // d972: bipush 1
      // d973: ldc2_w -4.448550564530588E-8
      // d976: dastore
      // d977: aastore
      // d978: dup
      // d979: sipush 796
      // d97c: bipush 2
      // d97d: newarray 7
      // d97f: dup
      // d980: bipush 0
      // d981: ldc2_w 0.5751199722290039
      // d984: dastore
      // d985: dup
      // d986: bipush 1
      // d987: ldc2_w 2.2423840341717488E-9
      // d98a: dastore
      // d98b: aastore
      // d98c: dup
      // d98d: sipush 797
      // d990: bipush 2
      // d991: newarray 7
      // d993: dup
      // d994: bipush 0
      // d995: ldc2_w 0.5756692886352539
      // d998: dastore
      // d999: dup
      // d99a: bipush 1
      // d99b: ldc2_w -1.450709904687712E-8
      // d99e: dastore
      // d99f: aastore
      // d9a0: dup
      // d9a1: sipush 798
      // d9a4: bipush 2
      // d9a5: newarray 7
      // d9a7: dup
      // d9a8: bipush 0
      // d9a9: ldc2_w 0.5762182474136353
      // d9ac: dastore
      // d9ad: dup
      // d9ae: bipush 1
      // d9af: ldc2_w 2.4806815282282017E-8
      // d9b2: dastore
      // d9b3: aastore
      // d9b4: dup
      // d9b5: sipush 799
      // d9b8: bipush 2
      // d9b9: newarray 7
      // d9bb: dup
      // d9bc: bipush 0
      // d9bd: ldc2_w 0.5767669677734375
      // d9c0: dastore
      // d9c1: dup
      // d9c2: bipush 1
      // d9c3: ldc2_w 1.3057724436551892E-9
      // d9c6: dastore
      // d9c7: aastore
      // d9c8: dup
      // d9c9: sipush 800
      // d9cc: bipush 2
      // d9cd: newarray 7
      // d9cf: dup
      // d9d0: bipush 0
      // d9d1: ldc2_w 0.5773153305053711
      // d9d4: dastore
      // d9d5: dup
      // d9d6: bipush 1
      // d9d7: ldc2_w 3.4529452510568104E-8
      // d9da: dastore
      // d9db: aastore
      // d9dc: dup
      // d9dd: sipush 801
      // d9e0: bipush 2
      // d9e1: newarray 7
      // d9e3: dup
      // d9e4: bipush 0
      // d9e5: ldc2_w 0.5778634548187256
      // d9e8: dastore
      // d9e9: dup
      // d9ea: bipush 1
      // d9eb: ldc2_w 5.598413198183808E-9
      // d9ee: dastore
      // d9ef: aastore
      // d9f0: dup
      // d9f1: sipush 802
      // d9f4: bipush 2
      // d9f5: newarray 7
      // d9f7: dup
      // d9f8: bipush 0
      // d9f9: ldc2_w 0.5784112215042114
      // d9fc: dastore
      // d9fd: dup
      // d9fe: bipush 1
      // d9ff: ldc2_w 3.405124925700107E-8
      // da02: dastore
      // da03: aastore
      // da04: dup
      // da05: sipush 803
      // da08: bipush 2
      // da09: newarray 7
      // da0b: dup
      // da0c: bipush 0
      // da0d: ldc2_w 0.5789587497711182
      // da10: dastore
      // da11: dup
      // da12: bipush 1
      // da13: ldc2_w 1.0074354568442952E-9
      // da16: dastore
      // da17: aastore
      // da18: dup
      // da19: sipush 804
      // da1c: bipush 2
      // da1d: newarray 7
      // da1f: dup
      // da20: bipush 0
      // da21: ldc2_w 0.5795059204101562
      // da24: dastore
      // da25: dup
      // da26: bipush 1
      // da27: ldc2_w 2.600448597385527E-8
      // da2a: dastore
      // da2b: aastore
      // da2c: dup
      // da2d: sipush 805
      // da30: bipush 2
      // da31: newarray 7
      // da33: dup
      // da34: bipush 0
      // da35: ldc2_w 0.5800528526306152
      // da38: dastore
      // da39: dup
      // da3a: bipush 1
      // da3b: ldc2_w -9.83920263200211E-9
      // da3e: dastore
      // da3f: aastore
      // da40: dup
      // da41: sipush 806
      // da44: bipush 2
      // da45: newarray 7
      // da47: dup
      // da48: bipush 0
      // da49: ldc2_w 0.5805994272232056
      // da4c: dastore
      // da4d: dup
      // da4e: bipush 1
      // da4f: ldc2_w 1.3012807963586057E-8
      // da52: dastore
      // da53: aastore
      // da54: dup
      // da55: sipush 807
      // da58: bipush 2
      // da59: newarray 7
      // da5b: dup
      // da5c: bipush 0
      // da5d: ldc2_w 0.5811457633972168
      // da60: dastore
      // da61: dup
      // da62: bipush 1
      // da63: ldc2_w -2.432215917965441E-8
      // da66: dastore
      // da67: aastore
      // da68: dup
      // da69: sipush 808
      // da6c: bipush 2
      // da6d: newarray 7
      // da6f: dup
      // da70: bipush 0
      // da71: ldc2_w 0.5816917419433594
      // da74: dastore
      // da75: dup
      // da76: bipush 1
      // da77: ldc2_w -2.308736892479391E-9
      // da7a: dastore
      // da7b: aastore
      // da7c: dup
      // da7d: sipush 809
      // da80: bipush 2
      // da81: newarray 7
      // da83: dup
      // da84: bipush 0
      // da85: ldc2_w 0.5822374820709229
      // da88: dastore
      // da89: dup
      // da8a: bipush 1
      // da8b: ldc2_w -3.983067093146514E-8
      // da8e: dastore
      // da8f: aastore
      // da90: dup
      // da91: sipush 810
      // da94: bipush 2
      // da95: newarray 7
      // da97: dup
      // da98: bipush 0
      // da99: ldc2_w 0.5827828645706177
      // da9c: dastore
      // da9d: dup
      // da9e: bipush 1
      // da9f: ldc2_w -1.735366061128156E-8
      // daa2: dastore
      // daa3: aastore
      // daa4: dup
      // daa5: sipush 811
      // daa8: bipush 2
      // daa9: newarray 7
      // daab: dup
      // daac: bipush 0
      // daad: ldc2_w 0.5833280086517334
      // dab0: dastore
      // dab1: dup
      // dab2: bipush 1
      // dab3: ldc2_w -5.376251584638963E-8
      // dab6: dastore
      // dab7: aastore
      // dab8: dup
      // dab9: sipush 812
      // dabc: bipush 2
      // dabd: newarray 7
      // dabf: dup
      // dac0: bipush 0
      // dac1: ldc2_w 0.5838727951049805
      // dac4: dastore
      // dac5: dup
      // dac6: bipush 1
      // dac7: ldc2_w -2.952399778965259E-8
      // daca: dastore
      // dacb: aastore
      // dacc: dup
      // dacd: sipush 813
      // dad0: bipush 2
      // dad1: newarray 7
      // dad3: dup
      // dad4: bipush 0
      // dad5: ldc2_w 0.5844172239303589
      // dad8: dastore
      // dad9: dup
      // dada: bipush 1
      // dadb: ldc2_w 5.5685313670430624E-8
      // dade: dastore
      // dadf: aastore
      // dae0: dup
      // dae1: sipush 814
      // dae4: bipush 2
      // dae5: newarray 7
      // dae7: dup
      // dae8: bipush 0
      // dae9: ldc2_w 0.5849615335464478
      // daec: dastore
      // daed: dup
      // daee: bipush 1
      // daef: ldc2_w -3.6230268489088716E-8
      // daf2: dastore
      // daf3: aastore
      // daf4: dup
      // daf5: sipush 815
      // daf8: bipush 2
      // daf9: newarray 7
      // dafb: dup
      // dafc: bipush 0
      // dafd: ldc2_w 0.5855053663253784
      // db00: dastore
      // db01: dup
      // db02: bipush 1
      // db03: ldc2_w 5.267948957869391E-8
      // db06: dastore
      // db07: aastore
      // db08: dup
      // db09: sipush 816
      // db0c: bipush 2
      // db0d: newarray 7
      // db0f: dup
      // db10: bipush 0
      // db11: ldc2_w 0.5860490798950195
      // db14: dastore
      // db15: dup
      // db16: bipush 1
      // db17: ldc2_w -3.489144132234588E-8
      // db1a: dastore
      // db1b: aastore
      // db1c: dup
      // db1d: sipush 817
      // db20: bipush 2
      // db21: newarray 7
      // db23: dup
      // db24: bipush 0
      // db25: ldc2_w 0.5865923166275024
      // db28: dastore
      // db29: dup
      // db2a: bipush 1
      // db2b: ldc2_w 5.9006122320612716E-8
      // db2e: dastore
      // db2f: aastore
      // db30: dup
      // db31: sipush 818
      // db34: bipush 2
      // db35: newarray 7
      // db37: dup
      // db38: bipush 0
      // db39: ldc2_w 0.5871354341506958
      // db3c: dastore
      // db3d: dup
      // db3e: bipush 1
      // db3f: ldc2_w -2.2934896740542648E-8
      // db42: dastore
      // db43: aastore
      // db44: dup
      // db45: sipush 819
      // db48: bipush 2
      // db49: newarray 7
      // db4b: dup
      // db4c: bipush 0
      // db4d: ldc2_w 0.5876781940460205
      // db50: dastore
      // db51: dup
      // db52: bipush 1
      // db53: ldc2_w -4.1975650319859075E-8
      // db56: dastore
      // db57: aastore
      // db58: dup
      // db59: sipush 820
      // db5c: bipush 2
      // db5d: newarray 7
      // db5f: dup
      // db60: bipush 0
      // db61: ldc2_w 0.5882205963134766
      // db64: dastore
      // db65: dup
      // db66: bipush 1
      // db67: ldc2_w 2.2036094805348692E-9
      // db6a: dastore
      // db6b: aastore
      // db6c: dup
      // db6d: sipush 821
      // db70: bipush 2
      // db71: newarray 7
      // db73: dup
      // db74: bipush 0
      // db75: ldc2_w 0.5887627601623535
      // db78: dastore
      // db79: dup
      // db7a: bipush 1
      // db7b: ldc2_w -9.287179048539306E-9
      // db7e: dastore
      // db7f: aastore
      // db80: dup
      // db81: sipush 822
      // db84: bipush 2
      // db85: newarray 7
      // db87: dup
      // db88: bipush 0
      // db89: ldc2_w 0.5893045663833618
      // db8c: dastore
      // db8d: dup
      // db8e: bipush 1
      // db8f: ldc2_w 4.3079982556221595E-8
      // db92: dastore
      // db93: aastore
      // db94: dup
      // db95: sipush 823
      // db98: bipush 2
      // db99: newarray 7
      // db9b: dup
      // db9c: bipush 0
      // db9d: ldc2_w 0.589846134185791
      // dba0: dastore
      // dba1: dup
      // dba2: bipush 1
      // dba3: ldc2_w 4.041399585161321E-8
      // dba6: dastore
      // dba7: aastore
      // dba8: dup
      // dba9: sipush 824
      // dbac: bipush 2
      // dbad: newarray 7
      // dbaf: dup
      // dbb0: bipush 0
      // dbb1: ldc2_w 0.5903874635696411
      // dbb4: dastore
      // dbb5: dup
      // dbb6: bipush 1
      // dbb7: ldc2_w -1.696746473863933E-8
      // dbba: dastore
      // dbbb: aastore
      // dbbc: dup
      // dbbd: sipush 825
      // dbc0: bipush 2
      // dbc1: newarray 7
      // dbc3: dup
      // dbc4: bipush 0
      // dbc5: ldc2_w 0.5909284353256226
      // dbc8: dastore
      // dbc9: dup
      // dbca: bipush 1
      // dbcb: ldc2_w -9.53795080582038E-9
      // dbce: dastore
      // dbcf: aastore
      // dbd0: dup
      // dbd1: sipush 826
      // dbd4: bipush 2
      // dbd5: newarray 7
      // dbd7: dup
      // dbd8: bipush 0
      // dbd9: ldc2_w 0.5914691686630249
      // dbdc: dastore
      // dbdd: dup
      // dbde: bipush 1
      // dbdf: ldc2_w -5.619010749352923E-8
      // dbe2: dastore
      // dbe3: aastore
      // dbe4: dup
      // dbe5: sipush 827
      // dbe8: bipush 2
      // dbe9: newarray 7
      // dbeb: dup
      // dbec: bipush 0
      // dbed: ldc2_w 0.5920095443725586
      // dbf0: dastore
      // dbf1: dup
      // dbf2: bipush 1
      // dbf3: ldc2_w -3.7398514182529506E-8
      // dbf6: dastore
      // dbf7: aastore
      // dbf8: dup
      // dbf9: sipush 828
      // dbfc: bipush 2
      // dbfd: newarray 7
      // dbff: dup
      // dc00: bipush 0
      // dc01: ldc2_w 0.5925495624542236
      // dc04: dastore
      // dc05: dup
      // dc06: bipush 1
      // dc07: ldc2_w 4.71524479659295E-8
      // dc0a: dastore
      // dc0b: aastore
      // dc0c: dup
      // dc0d: sipush 829
      // dc10: bipush 2
      // dc11: newarray 7
      // dc13: dup
      // dc14: bipush 0
      // dc15: ldc2_w 0.5930894613265991
      // dc18: dastore
      // dc19: dup
      // dc1a: bipush 1
      // dc1b: ldc2_w -4.0640692434639215E-8
      // dc1e: dastore
      // dc1f: aastore
      // dc20: dup
      // dc21: sipush 830
      // dc24: bipush 2
      // dc25: newarray 7
      // dc27: dup
      // dc28: bipush 0
      // dc29: ldc2_w 0.5936288833618164
      // dc2c: dastore
      // dc2d: dup
      // dc2e: bipush 1
      // dc2f: ldc2_w 5.716453096255401E-8
      // dc32: dastore
      // dc33: aastore
      // dc34: dup
      // dc35: sipush 831
      // dc38: bipush 2
      // dc39: newarray 7
      // dc3b: dup
      // dc3c: bipush 0
      // dc3d: ldc2_w 0.5941681861877441
      // dc40: dastore
      // dc41: dup
      // dc42: bipush 1
      // dc43: ldc2_w -1.6745661720946737E-8
      // dc46: dastore
      // dc47: aastore
      // dc48: dup
      // dc49: sipush 832
      // dc4c: bipush 2
      // dc4d: newarray 7
      // dc4f: dup
      // dc50: bipush 0
      // dc51: ldc2_w 0.5947071313858032
      // dc54: dastore
      // dc55: dup
      // dc56: bipush 1
      // dc57: ldc2_w -2.3639110433141897E-8
      // dc5a: dastore
      // dc5b: aastore
      // dc5c: dup
      // dc5d: sipush 833
      // dc60: bipush 2
      // dc61: newarray 7
      // dc63: dup
      // dc64: bipush 0
      // dc65: ldc2_w 0.5952457189559937
      // dc68: dastore
      // dc69: dup
      // dc6a: bipush 1
      // dc6b: ldc2_w 3.67972590471072E-8
      // dc6e: dastore
      // dc6f: aastore
      // dc70: dup
      // dc71: sipush 834
      // dc74: bipush 2
      // dc75: newarray 7
      // dc77: dup
      // dc78: bipush 0
      // dc79: ldc2_w 0.595784068107605
      // dc7c: dastore
      // dc7d: dup
      // dc7e: bipush 1
      // dc7f: ldc2_w 4.566672575206695E-8
      // dc82: dastore
      // dc83: aastore
      // dc84: dup
      // dc85: sipush 835
      // dc88: bipush 2
      // dc89: newarray 7
      // dc8b: dup
      // dc8c: bipush 0
      // dc8d: ldc2_w 0.5963221788406372
      // dc90: dastore
      // dc91: dup
      // dc92: bipush 1
      // dc93: ldc2_w 3.2813537149653483E-9
      // dc96: dastore
      // dc97: aastore
      // dc98: dup
      // dc99: sipush 836
      // dc9c: bipush 2
      // dc9d: newarray 7
      // dc9f: dup
      // dca0: bipush 0
      // dca1: ldc2_w 0.5968599319458008
      // dca4: dastore
      // dca5: dup
      // dca6: bipush 1
      // dca7: ldc2_w 2.916199305533732E-8
      // dcaa: dastore
      // dcab: aastore
      // dcac: dup
      // dcad: sipush 837
      // dcb0: bipush 2
      // dcb1: newarray 7
      // dcb3: dup
      // dcb4: bipush 0
      // dcb5: ldc2_w 0.5973974466323853
      // dcb8: dastore
      // dcb9: dup
      // dcba: bipush 1
      // dcbb: ldc2_w 4.410412409109416E-9
      // dcbe: dastore
      // dcbf: aastore
      // dcc0: dup
      // dcc1: sipush 838
      // dcc4: bipush 2
      // dcc5: newarray 7
      // dcc7: dup
      // dcc8: bipush 0
      // dcc9: ldc2_w 0.5979346036911011
      // dccc: dastore
      // dccd: dup
      // dcce: bipush 1
      // dccf: ldc2_w 4.85464582112459E-8
      // dcd2: dastore
      // dcd3: aastore
      // dcd4: dup
      // dcd5: sipush 839
      // dcd8: bipush 2
      // dcd9: newarray 7
      // dcdb: dup
      // dcdc: bipush 0
      // dcdd: ldc2_w 0.5984715223312378
      // dce0: dastore
      // dce1: dup
      // dce2: bipush 1
      // dce3: ldc2_w 4.267089756924666E-8
      // dce6: dastore
      // dce7: aastore
      // dce8: dup
      // dce9: sipush 840
      // dcec: bipush 2
      // dced: newarray 7
      // dcef: dup
      // dcf0: bipush 0
      // dcf1: ldc2_w 0.5990082025527954
      // dcf4: dastore
      // dcf5: dup
      // dcf6: bipush 1
      // dcf7: ldc2_w -1.2906712010774655E-8
      // dcfa: dastore
      // dcfb: aastore
      // dcfc: dup
      // dcfd: sipush 841
      // dd00: bipush 2
      // dd01: newarray 7
      // dd03: dup
      // dd04: bipush 0
      // dd05: ldc2_w 0.5995445251464844
      // dd08: dastore
      // dd09: dup
      // dd0a: bipush 1
      // dd0b: ldc2_w 1.3319784467641742E-9
      // dd0e: dastore
      // dd0f: aastore
      // dd10: dup
      // dd11: sipush 842
      // dd14: bipush 2
      // dd15: newarray 7
      // dd17: dup
      // dd18: bipush 0
      // dd19: ldc2_w 0.6000806093215942
      // dd1c: dastore
      // dd1d: dup
      // dd1e: bipush 1
      // dd1f: ldc2_w -3.35137581974451E-8
      // dd22: dastore
      // dd23: aastore
      // dd24: dup
      // dd25: sipush 843
      // dd28: bipush 2
      // dd29: newarray 7
      // dd2b: dup
      // dd2c: bipush 0
      // dd2d: ldc2_w 0.6006163358688354
      // dd30: dastore
      // dd31: dup
      // dd32: bipush 1
      // dd33: ldc2_w 2.0734340706476473E-9
      // dd36: dastore
      // dd37: aastore
      // dd38: dup
      // dd39: sipush 844
      // dd3c: bipush 2
      // dd3d: newarray 7
      // dd3f: dup
      // dd40: bipush 0
      // dd41: ldc2_w 0.6011518239974976
      // dd44: dastore
      // dd45: dup
      // dd46: bipush 1
      // dd47: ldc2_w -1.0808162722402073E-8
      // dd4a: dastore
      // dd4b: aastore
      // dd4c: dup
      // dd4d: sipush 845
      // dd50: bipush 2
      // dd51: newarray 7
      // dd53: dup
      // dd54: bipush 0
      // dd55: ldc2_w 0.601686954498291
      // dd58: dastore
      // dd59: dup
      // dd5a: bipush 1
      // dd5b: ldc2_w 4.735781872502109E-8
      // dd5e: dastore
      // dd5f: aastore
      // dd60: dup
      // dd61: sipush 846
      // dd64: bipush 2
      // dd65: newarray 7
      // dd67: dup
      // dd68: bipush 0
      // dd69: ldc2_w 0.6022218465805054
      // dd6c: dastore
      // dd6d: dup
      // dd6e: bipush 1
      // dd6f: ldc2_w 5.76686738430634E-8
      // dd72: dastore
      // dd73: aastore
      // dd74: dup
      // dd75: sipush 847
      // dd78: bipush 2
      // dd79: newarray 7
      // dd7b: dup
      // dd7c: bipush 0
      // dd7d: ldc2_w 0.6027565002441406
      // dd80: dastore
      // dd81: dup
      // dd82: bipush 1
      // dd83: ldc2_w 2.043049589651736E-8
      // dd86: dastore
      // dd87: aastore
      // dd88: dup
      // dd89: sipush 848
      // dd8c: bipush 2
      // dd8d: newarray 7
      // dd8f: dup
      // dd90: bipush 0
      // dd91: ldc2_w 0.6032907962799072
      // dd94: dastore
      // dd95: dup
      // dd96: bipush 1
      // dd97: ldc2_w 5.515817703577808E-8
      // dd9a: dastore
      // dd9b: aastore
      // dd9c: dup
      // dd9d: sipush 849
      // dda0: bipush 2
      // dda1: newarray 7
      // dda3: dup
      // dda4: bipush 0
      // dda5: ldc2_w 0.6038248538970947
      // dda8: dastore
      // dda9: dup
      // ddaa: bipush 1
      // ddab: ldc2_w 4.2947540692649586E-8
      // ddae: dastore
      // ddaf: aastore
      // ddb0: dup
      // ddb1: sipush 850
      // ddb4: bipush 2
      // ddb5: newarray 7
      // ddb7: dup
      // ddb8: bipush 0
      // ddb9: ldc2_w 0.6043586730957031
      // ddbc: dastore
      // ddbd: dup
      // ddbe: bipush 1
      // ddbf: ldc2_w -1.589678872195875E-8
      // ddc2: dastore
      // ddc3: aastore
      // ddc4: dup
      // ddc5: sipush 851
      // ddc8: bipush 2
      // ddc9: newarray 7
      // ddcb: dup
      // ddcc: bipush 0
      // ddcd: ldc2_w 0.6048921346664429
      // ddd0: dastore
      // ddd1: dup
      // ddd2: bipush 1
      // ddd3: ldc2_w -1.8613847754677912E-9
      // ddd6: dastore
      // ddd7: aastore
      // ddd8: dup
      // ddd9: sipush 852
      // dddc: bipush 2
      // dddd: newarray 7
      // dddf: dup
      // dde0: bipush 0
      // dde1: ldc2_w 0.6054253578186035
      // dde4: dastore
      // dde5: dup
      // dde6: bipush 1
      // dde7: ldc2_w -3.3851886626187444E-8
      // ddea: dastore
      // ddeb: aastore
      // ddec: dup
      // dded: sipush 853
      // ddf0: bipush 2
      // ddf1: newarray 7
      // ddf3: dup
      // ddf4: bipush 0
      // ddf5: ldc2_w 0.6059582233428955
      // ddf8: dastore
      // ddf9: dup
      // ddfa: bipush 1
      // ddfb: ldc2_w 7.64416021682279E-9
      // ddfe: dastore
      // ddff: aastore
      // de00: dup
      // de01: sipush 854
      // de04: bipush 2
      // de05: newarray 7
      // de07: dup
      // de08: bipush 0
      // de09: ldc2_w 0.6064908504486084
      // de0c: dastore
      // de0d: dup
      // de0e: bipush 1
      // de0f: ldc2_w 3.7201467248814224E-9
      // de12: dastore
      // de13: aastore
      // de14: dup
      // de15: sipush 855
      // de18: bipush 2
      // de19: newarray 7
      // de1b: dup
      // de1c: bipush 0
      // de1d: ldc2_w 0.6070232391357422
      // de20: dastore
      // de21: dup
      // de22: bipush 1
      // de23: ldc2_w -4.532172996647129E-8
      // de26: dastore
      // de27: aastore
      // de28: dup
      // de29: sipush 856
      // de2c: bipush 2
      // de2d: newarray 7
      // de2f: dup
      // de30: bipush 0
      // de31: ldc2_w 0.6075552701950073
      // de34: dastore
      // de35: dup
      // de36: bipush 1
      // de37: ldc2_w -1.997046552871766E-8
      // de3a: dastore
      // de3b: aastore
      // de3c: dup
      // de3d: sipush 857
      // de40: bipush 2
      // de41: newarray 7
      // de43: dup
      // de44: bipush 0
      // de45: ldc2_w 0.6080870628356934
      // de48: dastore
      // de49: dup
      // de4a: bipush 1
      // de4b: ldc2_w -3.913411606668587E-8
      // de4e: dastore
      // de4f: aastore
      // de50: dup
      // de51: sipush 858
      // de54: bipush 2
      // de55: newarray 7
      // de57: dup
      // de58: bipush 0
      // de59: ldc2_w 0.6086184978485107
      // de5c: dastore
      // de5d: dup
      // de5e: bipush 1
      // de5f: ldc2_w 1.6697361107868944E-8
      // de62: dastore
      // de63: aastore
      // de64: dup
      // de65: sipush 859
      // de68: bipush 2
      // de69: newarray 7
      // de6b: dup
      // de6c: bipush 0
      // de6d: ldc2_w 0.609149694442749
      // de70: dastore
      // de71: dup
      // de72: bipush 1
      // de73: ldc2_w 2.8614950293715483E-8
      // de76: dastore
      // de77: aastore
      // de78: dup
      // de79: sipush 860
      // de7c: bipush 2
      // de7d: newarray 7
      // de7f: dup
      // de80: bipush 0
      // de81: ldc2_w 0.6096806526184082
      // de84: dastore
      // de85: dup
      // de86: bipush 1
      // de87: ldc2_w -3.081552929643174E-9
      // de8a: dastore
      // de8b: aastore
      // de8c: dup
      // de8d: sipush 861
      // de90: bipush 2
      // de91: newarray 7
      // de93: dup
      // de94: bipush 0
      // de95: ldc2_w 0.6102112531661987
      // de98: dastore
      // de99: dup
      // de9a: bipush 1
      // de9b: ldc2_w 4.111645931319645E-8
      // de9e: dastore
      // de9f: aastore
      // dea0: dup
      // dea1: sipush 862
      // dea4: bipush 2
      // dea5: newarray 7
      // dea7: dup
      // dea8: bipush 0
      // dea9: ldc2_w 0.6107416152954102
      // deac: dastore
      // dead: dup
      // deae: bipush 1
      // deaf: ldc2_w 4.2298539553668435E-8
      // deb2: dastore
      // deb3: aastore
      // deb4: dup
      // deb5: sipush 863
      // deb8: bipush 2
      // deb9: newarray 7
      // debb: dup
      // debc: bipush 0
      // debd: ldc2_w 0.6112717390060425
      // dec0: dastore
      // dec1: dup
      // dec2: bipush 1
      // dec3: ldc2_w 7.630546413718035E-10
      // dec6: dastore
      // dec7: aastore
      // dec8: dup
      // dec9: sipush 864
      // decc: bipush 2
      // decd: newarray 7
      // decf: dup
      // ded0: bipush 0
      // ded1: ldc2_w 0.6118015050888062
      // ded4: dastore
      // ded5: dup
      // ded6: bipush 1
      // ded7: ldc2_w 3.601718675118614E-8
      // deda: dastore
      // dedb: aastore
      // dedc: dup
      // dedd: sipush 865
      // dee0: bipush 2
      // dee1: newarray 7
      // dee3: dup
      // dee4: bipush 0
      // dee5: ldc2_w 0.6123310327529907
      // dee8: dastore
      // dee9: dup
      // deea: bipush 1
      // deeb: ldc2_w 2.914906573537692E-8
      // deee: dastore
      // deef: aastore
      // def0: dup
      // def1: sipush 866
      // def4: bipush 2
      // def5: newarray 7
      // def7: dup
      // def8: bipush 0
      // def9: ldc2_w 0.6128603219985962
      // defc: dastore
      // defd: dup
      // defe: bipush 1
      // deff: ldc2_w -1.9544361222269494E-8
      // df02: dastore
      // df03: aastore
      // df04: dup
      // df05: sipush 867
      // df08: bipush 2
      // df09: newarray 7
      // df0b: dup
      // df0c: bipush 0
      // df0d: ldc2_w 0.613389253616333
      // df10: dastore
      // df11: dup
      // df12: bipush 1
      // df13: ldc2_w 9.442671392695732E-9
      // df16: dastore
      // df17: aastore
      // df18: dup
      // df19: sipush 868
      // df1c: bipush 2
      // df1d: newarray 7
      // df1f: dup
      // df20: bipush 0
      // df21: ldc2_w 0.6139179468154907
      // df24: dastore
      // df25: dup
      // df26: bipush 1
      // df27: ldc2_w -2.8031202304593286E-9
      // df2a: dastore
      // df2b: aastore
      // df2c: dup
      // df2d: sipush 869
      // df30: bipush 2
      // df31: newarray 7
      // df33: dup
      // df34: bipush 0
      // df35: ldc2_w 0.6144464015960693
      // df38: dastore
      // df39: dup
      // df3a: bipush 1
      // df3b: ldc2_w -5.598619958143586E-8
      // df3e: dastore
      // df3f: aastore
      // df40: dup
      // df41: sipush 870
      // df44: bipush 2
      // df45: newarray 7
      // df47: dup
      // df48: bipush 0
      // df49: ldc2_w 0.6149744987487793
      // df4c: dastore
      // df4d: dup
      // df4e: bipush 1
      // df4f: ldc2_w -3.060220883766096E-8
      // df52: dastore
      // df53: aastore
      // df54: dup
      // df55: sipush 871
      // df58: bipush 2
      // df59: newarray 7
      // df5b: dup
      // df5c: bipush 0
      // df5d: ldc2_w 0.6155023574829102
      // df60: dastore
      // df61: dup
      // df62: bipush 1
      // df63: ldc2_w -4.556583652800433E-8
      // df66: dastore
      // df67: aastore
      // df68: dup
      // df69: sipush 872
      // df6c: bipush 2
      // df6d: newarray 7
      // df6f: dup
      // df70: bipush 0
      // df71: ldc2_w 0.6160298585891724
      // df74: dastore
      // df75: dup
      // df76: bipush 1
      // df77: ldc2_w 1.8626341656366314E-8
      // df7a: dastore
      // df7b: aastore
      // df7c: dup
      // df7d: sipush 873
      // df80: bipush 2
      // df81: newarray 7
      // df83: dup
      // df84: bipush 0
      // df85: ldc2_w 0.6165571212768555
      // df88: dastore
      // df89: dup
      // df8a: bipush 1
      // df8b: ldc2_w 4.305870564227991E-8
      // df8e: dastore
      // df8f: aastore
      // df90: dup
      // df91: sipush 874
      // df94: bipush 2
      // df95: newarray 7
      // df97: dup
      // df98: bipush 0
      // df99: ldc2_w 0.6170841455459595
      // df9c: dastore
      // df9d: dup
      // df9e: bipush 1
      // df9f: ldc2_w 2.8024460607734262E-8
      // dfa2: dastore
      // dfa3: aastore
      // dfa4: dup
      // dfa5: sipush 875
      // dfa8: bipush 2
      // dfa9: newarray 7
      // dfab: dup
      // dfac: bipush 0
      // dfad: ldc2_w 0.6176109313964844
      // dfb0: dastore
      // dfb1: dup
      // dfb2: bipush 1
      // dfb3: ldc2_w -2.6183651590639875E-8
      // dfb6: dastore
      // dfb7: aastore
      // dfb8: dup
      // dfb9: sipush 876
      // dfbc: bipush 2
      // dfbd: newarray 7
      // dfbf: dup
      // dfc0: bipush 0
      // dfc1: ldc2_w 0.6181373596191406
      // dfc4: dastore
      // dfc5: dup
      // dfc6: bipush 1
      // dfc7: ldc2_w -6.406189112730307E-11
      // dfca: dastore
      // dfcb: aastore
      // dfcc: dup
      // dfcd: sipush 877
      // dfd0: bipush 2
      // dfd1: newarray 7
      // dfd3: dup
      // dfd4: bipush 0
      // dfd5: ldc2_w 0.6186635494232178
      // dfd8: dastore
      // dfd9: dup
      // dfda: bipush 1
      // dfdb: ldc2_w -1.2534241706168776E-8
      // dfde: dastore
      // dfdf: aastore
      // dfe0: dup
      // dfe1: sipush 878
      // dfe4: bipush 2
      // dfe5: newarray 7
      // dfe7: dup
      // dfe8: bipush 0
      // dfe9: ldc2_w 0.6191893815994263
      // dfec: dastore
      // dfed: dup
      // dfee: bipush 1
      // dfef: ldc2_w 5.5906456251308664E-8
      // dff2: dastore
      // dff3: aastore
      // dff4: dup
      // dff5: sipush 879
      // dff8: bipush 2
      // dff9: newarray 7
      // dffb: dup
      // dffc: bipush 0
      // dffd: ldc2_w 0.6197150945663452
      // e000: dastore
      // e001: dup
      // e002: bipush 1
      // e003: ldc2_w -3.286964881802063E-8
      // e006: dastore
      // e007: aastore
      // e008: dup
      // e009: sipush 880
      // e00c: bipush 2
      // e00d: newarray 7
      // e00f: dup
      // e010: bipush 0
      // e011: ldc2_w 0.6202404499053955
      // e014: dastore
      // e015: dup
      // e016: bipush 1
      // e017: ldc2_w -4.0153537978961E-8
      // e01a: dastore
      // e01b: aastore
      // e01c: dup
      // e01d: sipush 881
      // e020: bipush 2
      // e021: newarray 7
      // e023: dup
      // e024: bipush 0
      // e025: ldc2_w 0.6207654476165771
      // e028: dastore
      // e029: dup
      // e02a: bipush 1
      // e02b: ldc2_w 3.434477109643361E-8
      // e02e: dastore
      // e02f: aastore
      // e030: dup
      // e031: sipush 882
      // e034: bipush 2
      // e035: newarray 7
      // e037: dup
      // e038: bipush 0
      // e039: ldc2_w 0.6212903261184692
      // e03c: dastore
      // e03d: dup
      // e03e: bipush 1
      // e03f: ldc2_w -4.750377491075032E-8
      // e042: dastore
      // e043: aastore
      // e044: dup
      // e045: sipush 883
      // e048: bipush 2
      // e049: newarray 7
      // e04b: dup
      // e04c: bipush 0
      // e04d: ldc2_w 0.6218148469924927
      // e050: dastore
      // e051: dup
      // e052: bipush 1
      // e053: ldc2_w -4.699152670372743E-8
      // e056: dastore
      // e057: aastore
      // e058: dup
      // e059: sipush 884
      // e05c: bipush 2
      // e05d: newarray 7
      // e05f: dup
      // e060: bipush 0
      // e061: ldc2_w 0.6223390102386475
      // e064: dastore
      // e065: dup
      // e066: bipush 1
      // e067: ldc2_w 3.617013128065961E-8
      // e06a: dastore
      // e06b: aastore
      // e06c: dup
      // e06d: sipush 885
      // e070: bipush 2
      // e071: newarray 7
      // e073: dup
      // e074: bipush 0
      // e075: ldc2_w 0.6228630542755127
      // e078: dastore
      // e079: dup
      // e07a: bipush 1
      // e07b: ldc2_w -3.6149218175202596E-8
      // e07e: dastore
      // e07f: aastore
      // e080: dup
      // e081: sipush 886
      // e084: bipush 2
      // e085: newarray 7
      // e087: dup
      // e088: bipush 0
      // e089: ldc2_w 0.6233867406845093
      // e08c: dastore
      // e08d: dup
      // e08e: bipush 1
      // e08f: ldc2_w -2.5243286814648133E-8
      // e092: dastore
      // e093: aastore
      // e094: dup
      // e095: sipush 887
      // e098: bipush 2
      // e099: newarray 7
      // e09b: dup
      // e09c: bipush 0
      // e09d: ldc2_w 0.6239101886749268
      // e0a0: dastore
      // e0a1: dup
      // e0a2: bipush 1
      // e0a3: ldc2_w -5.003410681432538E-8
      // e0a6: dastore
      // e0a7: aastore
      // e0a8: dup
      // e0a9: sipush 888
      // e0ac: bipush 2
      // e0ad: newarray 7
      // e0af: dup
      // e0b0: bipush 0
      // e0b1: ldc2_w 0.6244332790374756
      // e0b4: dastore
      // e0b5: dup
      // e0b6: bipush 1
      // e0b7: ldc2_w 8.974417915105033E-9
      // e0ba: dastore
      // e0bb: aastore
      // e0bc: dup
      // e0bd: sipush 889
      // e0c0: bipush 2
      // e0c1: newarray 7
      // e0c3: dup
      // e0c4: bipush 0
      // e0c5: ldc2_w 0.6249561309814453
      // e0c8: dastore
      // e0c9: dup
      // e0ca: bipush 1
      // e0cb: ldc2_w 3.285935446876949E-8
      // e0ce: dastore
      // e0cf: aastore
      // e0d0: dup
      // e0d1: sipush 890
      // e0d4: bipush 2
      // e0d5: newarray 7
      // e0d7: dup
      // e0d8: bipush 0
      // e0d9: ldc2_w 0.6254787445068359
      // e0dc: dastore
      // e0dd: dup
      // e0de: bipush 1
      // e0df: ldc2_w 2.190661054038537E-8
      // e0e2: dastore
      // e0e3: aastore
      // e0e4: dup
      // e0e5: sipush 891
      // e0e8: bipush 2
      // e0e9: newarray 7
      // e0eb: dup
      // e0ec: bipush 0
      // e0ed: ldc2_w 0.6260011196136475
      // e0f0: dastore
      // e0f1: dup
      // e0f2: bipush 1
      // e0f3: ldc2_w -2.3598354190515998E-8
      // e0f6: dastore
      // e0f7: aastore
      // e0f8: dup
      // e0f9: sipush 892
      // e0fc: bipush 2
      // e0fd: newarray 7
      // e0ff: dup
      // e100: bipush 0
      // e101: ldc2_w 0.6265231370925903
      // e104: dastore
      // e105: dup
      // e106: bipush 1
      // e107: ldc2_w 1.5838762427747586E-8
      // e10a: dastore
      // e10b: aastore
      // e10c: dup
      // e10d: sipush 893
      // e110: bipush 2
      // e111: newarray 7
      // e113: dup
      // e114: bipush 0
      // e115: ldc2_w 0.6270449161529541
      // e118: dastore
      // e119: dup
      // e11a: bipush 1
      // e11b: ldc2_w 2.129323729978037E-8
      // e11e: dastore
      // e11f: aastore
      // e120: dup
      // e121: sipush 894
      // e124: bipush 2
      // e125: newarray 7
      // e127: dup
      // e128: bipush 0
      // e129: ldc2_w 0.6275664567947388
      // e12c: dastore
      // e12d: dup
      // e12e: bipush 1
      // e12f: ldc2_w -6.950808333865794E-9
      // e132: dastore
      // e133: aastore
      // e134: dup
      // e135: sipush 895
      // e138: bipush 2
      // e139: newarray 7
      // e13b: dup
      // e13c: bipush 0
      // e13d: ldc2_w 0.6280876398086548
      // e140: dastore
      // e141: dup
      // e142: bipush 1
      // e143: ldc2_w 5.059959203156465E-8
      // e146: dastore
      // e147: aastore
      // e148: dup
      // e149: sipush 896
      // e14c: bipush 2
      // e14d: newarray 7
      // e14f: dup
      // e150: bipush 0
      // e151: ldc2_w 0.6286087036132812
      // e154: dastore
      // e155: dup
      // e156: bipush 1
      // e157: ldc2_w -4.41909071122557E-8
      // e15a: dastore
      // e15b: aastore
      // e15c: dup
      // e15d: sipush 897
      // e160: bipush 2
      // e161: newarray 7
      // e163: dup
      // e164: bipush 0
      // e165: ldc2_w 0.6291294097900391
      // e168: dastore
      // e169: dup
      // e16a: bipush 1
      // e16b: ldc2_w -5.262093550784066E-8
      // e16e: dastore
      // e16f: aastore
      // e170: dup
      // e171: sipush 898
      // e174: bipush 2
      // e175: newarray 7
      // e177: dup
      // e178: bipush 0
      // e179: ldc2_w 0.6296497583389282
      // e17c: dastore
      // e17d: dup
      // e17e: bipush 1
      // e17f: ldc2_w 2.559185648444699E-8
      // e182: dastore
      // e183: aastore
      // e184: dup
      // e185: sipush 899
      // e188: bipush 2
      // e189: newarray 7
      // e18b: dup
      // e18c: bipush 0
      // e18d: ldc2_w 0.6301699876785278
      // e190: dastore
      // e191: dup
      // e192: bipush 1
      // e193: ldc2_w -4.768920119497491E-8
      // e196: dastore
      // e197: aastore
      // e198: dup
      // e199: sipush 900
      // e19c: bipush 2
      // e19d: newarray 7
      // e19f: dup
      // e1a0: bipush 0
      // e1a1: ldc2_w 0.6306898593902588
      // e1a4: dastore
      // e1a5: dup
      // e1a6: bipush 1
      // e1a7: ldc2_w -3.376406008397877E-8
      // e1aa: dastore
      // e1ab: aastore
      // e1ac: dup
      // e1ad: sipush 901
      // e1b0: bipush 2
      // e1b1: newarray 7
      // e1b3: dup
      // e1b4: bipush 0
      // e1b5: ldc2_w 0.6312094926834106
      // e1b8: dastore
      // e1b9: dup
      // e1ba: bipush 1
      // e1bb: ldc2_w -5.156097914033476E-8
      // e1be: dastore
      // e1bf: aastore
      // e1c0: dup
      // e1c1: sipush 902
      // e1c4: bipush 2
      // e1c5: newarray 7
      // e1c7: dup
      // e1c8: bipush 0
      // e1c9: ldc2_w 0.6317287683486938
      // e1cc: dastore
      // e1cd: dup
      // e1ce: bipush 1
      // e1cf: ldc2_w 1.840992392368355E-8
      // e1d2: dastore
      // e1d3: aastore
      // e1d4: dup
      // e1d5: sipush 903
      // e1d8: bipush 2
      // e1d9: newarray 7
      // e1db: dup
      // e1dc: bipush 0
      // e1dd: ldc2_w 0.632247805595398
      // e1e0: dastore
      // e1e1: dup
      // e1e2: bipush 1
      // e1e3: ldc2_w 5.721951534729663E-8
      // e1e6: dastore
      // e1e7: aastore
      // e1e8: dup
      // e1e9: sipush 904
      // e1ec: bipush 2
      // e1ed: newarray 7
      // e1ef: dup
      // e1f0: bipush 0
      // e1f1: ldc2_w 0.6327667236328125
      // e1f4: dastore
      // e1f5: dup
      // e1f6: bipush 1
      // e1f7: ldc2_w -5.406177467045421E-8
      // e1fa: dastore
      // e1fb: aastore
      // e1fc: dup
      // e1fd: sipush 905
      // e200: bipush 2
      // e201: newarray 7
      // e203: dup
      // e204: bipush 0
      // e205: ldc2_w 0.6332851648330688
      // e208: dastore
      // e209: dup
      // e20a: bipush 1
      // e20b: ldc2_w 4.247320713683124E-8
      // e20e: dastore
      // e20f: aastore
      // e210: dup
      // e211: sipush 906
      // e214: bipush 2
      // e215: newarray 7
      // e217: dup
      // e218: bipush 0
      // e219: ldc2_w 0.6338034868240356
      // e21c: dastore
      // e21d: dup
      // e21e: bipush 1
      // e21f: ldc2_w -1.0524557502830645E-8
      // e222: dastore
      // e223: aastore
      // e224: dup
      // e225: sipush 907
      // e228: bipush 2
      // e229: newarray 7
      // e22b: dup
      // e22c: bipush 0
      // e22d: ldc2_w 0.6343214511871338
      // e230: dastore
      // e231: dup
      // e232: bipush 1
      // e233: ldc2_w 2.5641927558519502E-8
      // e236: dastore
      // e237: aastore
      // e238: dup
      // e239: sipush 908
      // e23c: bipush 2
      // e23d: newarray 7
      // e23f: dup
      // e240: bipush 0
      // e241: ldc2_w 0.6348391771316528
      // e244: dastore
      // e245: dup
      // e246: bipush 1
      // e247: ldc2_w 3.204135737993823E-8
      // e24a: dastore
      // e24b: aastore
      // e24c: dup
      // e24d: sipush 909
      // e250: bipush 2
      // e251: newarray 7
      // e253: dup
      // e254: bipush 0
      // e255: ldc2_w 0.6353566646575928
      // e258: dastore
      // e259: dup
      // e25a: bipush 1
      // e25b: ldc2_w 8.951285029786536E-9
      // e25e: dastore
      // e25f: aastore
      // e260: dup
      // e261: sipush 910
      // e264: bipush 2
      // e265: newarray 7
      // e267: dup
      // e268: bipush 0
      // e269: ldc2_w 0.6358739137649536
      // e26c: dastore
      // e26d: dup
      // e26e: bipush 1
      // e26f: ldc2_w -4.335116707228395E-8
      // e272: dastore
      // e273: aastore
      // e274: dup
      // e275: sipush 911
      // e278: bipush 2
      // e279: newarray 7
      // e27b: dup
      // e27c: bipush 0
      // e27d: ldc2_w 0.6363908052444458
      // e280: dastore
      // e281: dup
      // e282: bipush 1
      // e283: ldc2_w -5.380016714089483E-9
      // e286: dastore
      // e287: aastore
      // e288: dup
      // e289: sipush 912
      // e28c: bipush 2
      // e28d: newarray 7
      // e28f: dup
      // e290: bipush 0
      // e291: ldc2_w 0.6369074583053589
      // e294: dastore
      // e295: dup
      // e296: bipush 1
      // e297: ldc2_w 3.931710344901743E-9
      // e29a: dastore
      // e29b: aastore
      // e29c: dup
      // e29d: sipush 913
      // e2a0: bipush 2
      // e2a1: newarray 7
      // e2a3: dup
      // e2a4: bipush 0
      // e2a5: ldc2_w 0.6374238729476929
      // e2a8: dastore
      // e2a9: dup
      // e2aa: bipush 1
      // e2ab: ldc2_w -1.5140150088220166E-8
      // e2ae: dastore
      // e2af: aastore
      // e2b0: dup
      // e2b1: sipush 914
      // e2b4: bipush 2
      // e2b5: newarray 7
      // e2b7: dup
      // e2b8: bipush 0
      // e2b9: ldc2_w 0.6379399299621582
      // e2bc: dastore
      // e2bd: dup
      // e2be: bipush 1
      // e2bf: ldc2_w 5.688910024377372E-8
      // e2c2: dastore
      // e2c3: aastore
      // e2c4: dup
      // e2c5: sipush 915
      // e2c8: bipush 2
      // e2c9: newarray 7
      // e2cb: dup
      // e2cc: bipush 0
      // e2cd: ldc2_w 0.638455867767334
      // e2d0: dastore
      // e2d1: dup
      // e2d2: bipush 1
      // e2d3: ldc2_w -1.8124135273572568E-8
      // e2d6: dastore
      // e2d7: aastore
      // e2d8: dup
      // e2d9: sipush 916
      // e2dc: bipush 2
      // e2dd: newarray 7
      // e2df: dup
      // e2e0: bipush 0
      // e2e1: ldc2_w 0.6389714479446411
      // e2e4: dastore
      // e2e5: dup
      // e2e6: bipush 1
      // e2e7: ldc2_w -1.486720391901626E-9
      // e2ea: dastore
      // e2eb: aastore
      // e2ec: dup
      // e2ed: sipush 917
      // e2f0: bipush 2
      // e2f1: newarray 7
      // e2f3: dup
      // e2f4: bipush 0
      // e2f5: ldc2_w 0.6394867897033691
      // e2f8: dastore
      // e2f9: dup
      // e2fa: bipush 1
      // e2fb: ldc2_w -1.2133811978747018E-8
      // e2fe: dastore
      // e2ff: aastore
      // e300: dup
      // e301: sipush 918
      // e304: bipush 2
      // e305: newarray 7
      // e307: dup
      // e308: bipush 0
      // e309: ldc2_w 0.6400018930435181
      // e30c: dastore
      // e30d: dup
      // e30e: bipush 1
      // e30f: ldc2_w -4.9791700939901716E-8
      // e312: dastore
      // e313: aastore
      // e314: dup
      // e315: sipush 919
      // e318: bipush 2
      // e319: newarray 7
      // e31b: dup
      // e31c: bipush 0
      // e31d: ldc2_w 0.6405166387557983
      // e320: dastore
      // e321: dup
      // e322: bipush 1
      // e323: ldc2_w 5.022188652837274E-9
      // e326: dastore
      // e327: aastore
      // e328: dup
      // e329: sipush 920
      // e32c: bipush 2
      // e32d: newarray 7
      // e32f: dup
      // e330: bipush 0
      // e331: ldc2_w 0.6410311460494995
      // e334: dastore
      // e335: dup
      // e336: bipush 1
      // e337: ldc2_w 3.337143177933685E-8
      // e33a: dastore
      // e33b: aastore
      // e33c: dup
      // e33d: sipush 921
      // e340: bipush 2
      // e341: newarray 7
      // e343: dup
      // e344: bipush 0
      // e345: ldc2_w 0.6415454149246216
      // e348: dastore
      // e349: dup
      // e34a: bipush 1
      // e34b: ldc2_w 3.55284719912458E-8
      // e34e: dastore
      // e34f: aastore
      // e350: dup
      // e351: sipush 922
      // e354: bipush 2
      // e355: newarray 7
      // e357: dup
      // e358: bipush 0
      // e359: ldc2_w 0.6420594453811646
      // e35c: dastore
      // e35d: dup
      // e35e: bipush 1
      // e35f: ldc2_w 1.1765332726757802E-8
      // e362: dastore
      // e363: aastore
      // e364: dup
      // e365: sipush 923
      // e368: bipush 2
      // e369: newarray 7
      // e36b: dup
      // e36c: bipush 0
      // e36d: ldc2_w 0.6425732374191284
      // e370: dastore
      // e371: dup
      // e372: bipush 1
      // e373: ldc2_w -3.7646381826067834E-8
      // e376: dastore
      // e377: aastore
      // e378: dup
      // e379: sipush 924
      // e37c: bipush 2
      // e37d: newarray 7
      // e37f: dup
      // e380: bipush 0
      // e381: ldc2_w 0.6430866718292236
      // e384: dastore
      // e385: dup
      // e386: bipush 1
      // e387: ldc2_w 6.773803682579552E-9
      // e38a: dastore
      // e38b: aastore
      // e38c: dup
      // e38d: sipush 925
      // e390: bipush 2
      // e391: newarray 7
      // e393: dup
      // e394: bipush 0
      // e395: ldc2_w 0.6435998678207397
      // e398: dastore
      // e399: dup
      // e39a: bipush 1
      // e39b: ldc2_w 2.608736797081283E-8
      // e39e: dastore
      // e39f: aastore
      // e3a0: dup
      // e3a1: sipush 926
      // e3a4: bipush 2
      // e3a5: newarray 7
      // e3a7: dup
      // e3a8: bipush 0
      // e3a9: ldc2_w 0.6441128253936768
      // e3ac: dastore
      // e3ad: dup
      // e3ae: bipush 1
      // e3af: ldc2_w 2.056466263408266E-8
      // e3b2: dastore
      // e3b3: aastore
      // e3b4: dup
      // e3b5: sipush 927
      // e3b8: bipush 2
      // e3b9: newarray 7
      // e3bb: dup
      // e3bc: bipush 0
      // e3bd: ldc2_w 0.6446255445480347
      // e3c0: dastore
      // e3c1: dup
      // e3c2: bipush 1
      // e3c3: ldc2_w -9.524376551107945E-9
      // e3c6: dastore
      // e3c7: aastore
      // e3c8: dup
      // e3c9: sipush 928
      // e3cc: bipush 2
      // e3cd: newarray 7
      // e3cf: dup
      // e3d0: bipush 0
      // e3d1: ldc2_w 0.6451379060745239
      // e3d4: dastore
      // e3d5: dup
      // e3d6: bipush 1
      // e3d7: ldc2_w 5.5299060775883977E-8
      // e3da: dastore
      // e3db: aastore
      // e3dc: dup
      // e3dd: sipush 929
      // e3e0: bipush 2
      // e3e1: newarray 7
      // e3e3: dup
      // e3e4: bipush 0
      // e3e5: ldc2_w 0.6456501483917236
      // e3e8: dastore
      // e3e9: dup
      // e3ea: bipush 1
      // e3eb: ldc2_w -2.3114497793159813E-8
      // e3ee: dastore
      // e3ef: aastore
      // e3f0: dup
      // e3f1: sipush 930
      // e3f4: bipush 2
      // e3f5: newarray 7
      // e3f7: dup
      // e3f8: bipush 0
      // e3f9: ldc2_w 0.6461620330810547
      // e3fc: dastore
      // e3fd: dup
      // e3fe: bipush 1
      // e3ff: ldc2_w -6.077779731902102E-9
      // e402: dastore
      // e403: aastore
      // e404: dup
      // e405: sipush 931
      // e408: bipush 2
      // e409: newarray 7
      // e40b: dup
      // e40c: bipush 0
      // e40d: ldc2_w 0.6466736793518066
      // e410: dastore
      // e411: dup
      // e412: bipush 1
      // e413: ldc2_w -1.2531793589140273E-8
      // e416: dastore
      // e417: aastore
      // e418: dup
      // e419: sipush 932
      // e41c: bipush 2
      // e41d: newarray 7
      // e41f: dup
      // e420: bipush 0
      // e421: ldc2_w 0.6471850872039795
      // e424: dastore
      // e425: dup
      // e426: bipush 1
      // e427: ldc2_w -4.220866994206517E-8
      // e42a: dastore
      // e42b: aastore
      // e42c: dup
      // e42d: sipush 933
      // e430: bipush 2
      // e431: newarray 7
      // e433: dup
      // e434: bipush 0
      // e435: ldc2_w 0.6476961374282837
      // e438: dastore
      // e439: dup
      // e43a: bipush 1
      // e43b: ldc2_w 2.4368339445199057E-8
      // e43e: dastore
      // e43f: aastore
      // e440: dup
      // e441: sipush 934
      // e444: bipush 2
      // e445: newarray 7
      // e447: dup
      // e448: bipush 0
      // e449: ldc2_w 0.6482070684432983
      // e44c: dastore
      // e44d: dup
      // e44e: bipush 1
      // e44f: ldc2_w -5.095229574221907E-8
      // e452: dastore
      // e453: aastore
      // e454: dup
      // e455: sipush 935
      // e458: bipush 2
      // e459: newarray 7
      // e45b: dup
      // e45c: bipush 0
      // e45d: ldc2_w 0.6487176418304443
      // e460: dastore
      // e461: dup
      // e462: bipush 1
      // e463: ldc2_w -2.9485356677301627E-8
      // e466: dastore
      // e467: aastore
      // e468: dup
      // e469: sipush 936
      // e46c: bipush 2
      // e46d: newarray 7
      // e46f: dup
      // e470: bipush 0
      // e471: ldc2_w 0.6492279767990112
      // e474: dastore
      // e475: dup
      // e476: bipush 1
      // e477: ldc2_w -3.0173901411577916E-8
      // e47a: dastore
      // e47b: aastore
      // e47c: dup
      // e47d: sipush 937
      // e480: bipush 2
      // e481: newarray 7
      // e483: dup
      // e484: bipush 0
      // e485: ldc2_w 0.649738073348999
      // e488: dastore
      // e489: dup
      // e48a: bipush 1
      // e48b: ldc2_w -5.275210583909726E-8
      // e48e: dastore
      // e48f: aastore
      // e490: dup
      // e491: sipush 938
      // e494: bipush 2
      // e495: newarray 7
      // e497: dup
      // e498: bipush 0
      // e499: ldc2_w 0.6502478122711182
      // e49c: dastore
      // e49d: dup
      // e49e: bipush 1
      // e49f: ldc2_w 2.2254737134350224E-8
      // e4a2: dastore
      // e4a3: aastore
      // e4a4: dup
      // e4a5: sipush 939
      // e4a8: bipush 2
      // e4a9: newarray 7
      // e4ab: dup
      // e4ac: bipush 0
      // e4ad: ldc2_w 0.6507574319839478
      // e4b0: dastore
      // e4b1: dup
      // e4b2: bipush 1
      // e4b3: ldc2_w -4.330693978322885E-8
      // e4b6: dastore
      // e4b7: aastore
      // e4b8: dup
      // e4b9: sipush 940
      // e4bc: bipush 2
      // e4bd: newarray 7
      // e4bf: dup
      // e4c0: bipush 0
      // e4c1: ldc2_w 0.6512666940689087
      // e4c4: dastore
      // e4c5: dup
      // e4c6: bipush 1
      // e4c7: ldc2_w -1.0753950588009912E-8
      // e4ca: dastore
      // e4cb: aastore
      // e4cc: dup
      // e4cd: sipush 941
      // e4d0: bipush 2
      // e4d1: newarray 7
      // e4d3: dup
      // e4d4: bipush 0
      // e4d5: ldc2_w 0.6517757177352905
      // e4d8: dastore
      // e4d9: dup
      // e4da: bipush 1
      // e4db: ldc2_w 9.686179886293545E-10
      // e4de: dastore
      // e4df: aastore
      // e4e0: dup
      // e4e1: sipush 942
      // e4e4: bipush 2
      // e4e5: newarray 7
      // e4e7: dup
      // e4e8: bipush 0
      // e4e9: ldc2_w 0.6522845029830933
      // e4ec: dastore
      // e4ed: dup
      // e4ee: bipush 1
      // e4ef: ldc2_w -7.875434494414498E-9
      // e4f2: dastore
      // e4f3: aastore
      // e4f4: dup
      // e4f5: sipush 943
      // e4f8: bipush 2
      // e4f9: newarray 7
      // e4fb: dup
      // e4fc: bipush 0
      // e4fd: ldc2_w 0.6527930498123169
      // e500: dastore
      // e501: dup
      // e502: bipush 1
      // e503: ldc2_w -3.702271091849158E-8
      // e506: dastore
      // e507: aastore
      // e508: dup
      // e509: sipush 944
      // e50c: bipush 2
      // e50d: newarray 7
      // e50f: dup
      // e510: bipush 0
      // e511: ldc2_w 0.6533012390136719
      // e514: dastore
      // e515: dup
      // e516: bipush 1
      // e517: ldc2_w 3.2999073763758614E-8
      // e51a: dastore
      // e51b: aastore
      // e51c: dup
      // e51d: sipush 945
      // e520: bipush 2
      // e521: newarray 7
      // e523: dup
      // e524: bipush 0
      // e525: ldc2_w 0.6538093090057373
      // e528: dastore
      // e529: dup
      // e52a: bipush 1
      // e52b: ldc2_w -3.5966064858620067E-8
      // e52e: dastore
      // e52f: aastore
      // e530: dup
      // e531: sipush 946
      // e534: bipush 2
      // e535: newarray 7
      // e537: dup
      // e538: bipush 0
      // e539: ldc2_w 0.6543170213699341
      // e53c: dastore
      // e53d: dup
      // e53e: bipush 1
      // e53f: ldc2_w -5.23735298540578E-9
      // e542: dastore
      // e543: aastore
      // e544: dup
      // e545: sipush 947
      // e548: bipush 2
      // e549: newarray 7
      // e54b: dup
      // e54c: bipush 0
      // e54d: ldc2_w 0.6548244953155518
      // e550: dastore
      // e551: dup
      // e552: bipush 1
      // e553: ldc2_w 6.237715351293023E-9
      // e556: dastore
      // e557: aastore
      // e558: dup
      // e559: sipush 948
      // e55c: bipush 2
      // e55d: newarray 7
      // e55f: dup
      // e560: bipush 0
      // e561: ldc2_w 0.6553317308425903
      // e564: dastore
      // e565: dup
      // e566: bipush 1
      // e567: ldc2_w -1.279462699936282E-9
      // e56a: dastore
      // e56b: aastore
      // e56c: dup
      // e56d: sipush 949
      // e570: bipush 2
      // e571: newarray 7
      // e573: dup
      // e574: bipush 0
      // e575: ldc2_w 0.6558387279510498
      // e578: dastore
      // e579: dup
      // e57a: bipush 1
      // e57b: ldc2_w -2.7527887552743672E-8
      // e57e: dastore
      // e57f: aastore
      // e580: dup
      // e581: sipush 950
      // e584: bipush 2
      // e585: newarray 7
      // e587: dup
      // e588: bipush 0
      // e589: ldc2_w 0.6563453674316406
      // e58c: dastore
      // e58d: dup
      // e58e: bipush 1
      // e58f: ldc2_w 4.696233317356646E-8
      // e592: dastore
      // e593: aastore
      // e594: dup
      // e595: sipush 951
      // e598: bipush 2
      // e599: newarray 7
      // e59b: dup
      // e59c: bipush 0
      // e59d: ldc2_w 0.6568518877029419
      // e5a0: dastore
      // e5a1: dup
      // e5a2: bipush 1
      // e5a3: ldc2_w -1.5967172745329108E-8
      // e5a6: dastore
      // e5a7: aastore
      // e5a8: dup
      // e5a9: sipush 952
      // e5ac: bipush 2
      // e5ad: newarray 7
      // e5af: dup
      // e5b0: bipush 0
      // e5b1: ldc2_w 0.6573580503463745
      // e5b4: dastore
      // e5b5: dup
      // e5b6: bipush 1
      // e5b7: ldc2_w 2.2361985518423144E-8
      // e5ba: dastore
      // e5bb: aastore
      // e5bc: dup
      // e5bd: sipush 953
      // e5c0: bipush 2
      // e5c1: newarray 7
      // e5c3: dup
      // e5c4: bipush 0
      // e5c5: ldc2_w 0.657863974571228
      // e5c8: dastore
      // e5c9: dup
      // e5ca: bipush 1
      // e5cb: ldc2_w 4.2999935789083046E-8
      // e5ce: dastore
      // e5cf: aastore
      // e5d0: dup
      // e5d1: sipush 954
      // e5d4: bipush 2
      // e5d5: newarray 7
      // e5d7: dup
      // e5d8: bipush 0
      // e5d9: ldc2_w 0.6583696603775024
      // e5dc: dastore
      // e5dd: dup
      // e5de: bipush 1
      // e5df: ldc2_w 4.620570188811826E-8
      // e5e2: dastore
      // e5e3: aastore
      // e5e4: dup
      // e5e5: sipush 955
      // e5e8: bipush 2
      // e5e9: newarray 7
      // e5eb: dup
      // e5ec: bipush 0
      // e5ed: ldc2_w 0.6588751077651978
      // e5f0: dastore
      // e5f1: dup
      // e5f2: bipush 1
      // e5f3: ldc2_w 3.223791487908353E-8
      // e5f6: dastore
      // e5f7: aastore
      // e5f8: dup
      // e5f9: sipush 956
      // e5fc: bipush 2
      // e5fd: newarray 7
      // e5ff: dup
      // e600: bipush 0
      // e601: ldc2_w 0.659380316734314
      // e604: dastore
      // e605: dup
      // e606: bipush 1
      // e607: ldc2_w 1.3548138612715822E-9
      // e60a: dastore
      // e60b: aastore
      // e60c: dup
      // e60d: sipush 957
      // e610: bipush 2
      // e611: newarray 7
      // e613: dup
      // e614: bipush 0
      // e615: ldc2_w 0.6598852872848511
      // e618: dastore
      // e619: dup
      // e61a: bipush 1
      // e61b: ldc2_w -4.618575323863973E-8
      // e61e: dastore
      // e61f: aastore
      // e620: dup
      // e621: sipush 958
      // e624: bipush 2
      // e625: newarray 7
      // e627: dup
      // e628: bipush 0
      // e629: ldc2_w 0.6603899002075195
      // e62c: dastore
      // e62d: dup
      // e62e: bipush 1
      // e62f: ldc2_w 9.082960673843353E-9
      // e632: dastore
      // e633: aastore
      // e634: dup
      // e635: sipush 959
      // e638: bipush 2
      // e639: newarray 7
      // e63b: dup
      // e63c: bipush 0
      // e63d: ldc2_w 0.6608942747116089
      // e640: dastore
      // e641: dup
      // e642: bipush 1
      // e643: ldc2_w 4.820873399634487E-8
      // e646: dastore
      // e647: aastore
      // e648: dup
      // e649: sipush 960
      // e64c: bipush 2
      // e64d: newarray 7
      // e64f: dup
      // e650: bipush 0
      // e651: ldc2_w 0.6613985300064087
      // e654: dastore
      // e655: dup
      // e656: bipush 1
      // e657: ldc2_w -4.776104368314602E-8
      // e65a: dastore
      // e65b: aastore
      // e65c: dup
      // e65d: sipush 961
      // e660: bipush 2
      // e661: newarray 7
      // e663: dup
      // e664: bipush 0
      // e665: ldc2_w 0.6619024276733398
      // e668: dastore
      // e669: dup
      // e66a: bipush 1
      // e66b: ldc2_w -4.0151502150238136E-8
      // e66e: dastore
      // e66f: aastore
      // e670: dup
      // e671: sipush 962
      // e674: bipush 2
      // e675: newarray 7
      // e677: dup
      // e678: bipush 0
      // e679: ldc2_w 0.6624060869216919
      // e67c: dastore
      // e67d: dup
      // e67e: bipush 1
      // e67f: ldc2_w -4.791602708710648E-8
      // e682: dastore
      // e683: aastore
      // e684: dup
      // e685: sipush 963
      // e688: bipush 2
      // e689: newarray 7
      // e68b: dup
      // e68c: bipush 0
      // e68d: ldc2_w 0.6629093885421753
      // e690: dastore
      // e691: dup
      // e692: bipush 1
      // e693: ldc2_w 4.8410188461165925E-8
      // e696: dastore
      // e697: aastore
      // e698: dup
      // e699: sipush 964
      // e69c: bipush 2
      // e69d: newarray 7
      // e69f: dup
      // e6a0: bipush 0
      // e6a1: ldc2_w 0.6634125709533691
      // e6a4: dastore
      // e6a5: dup
      // e6a6: bipush 1
      // e6a7: ldc2_w 1.0663697110471944E-8
      // e6aa: dastore
      // e6ab: aastore
      // e6ac: dup
      // e6ad: sipush 965
      // e6b0: bipush 2
      // e6b1: newarray 7
      // e6b3: dup
      // e6b4: bipush 0
      // e6b5: ldc2_w 0.6639155149459839
      // e6b8: dastore
      // e6b9: dup
      // e6ba: bipush 1
      // e6bb: ldc2_w -4.1691464781797555E-8
      // e6be: dastore
      // e6bf: aastore
      // e6c0: dup
      // e6c1: sipush 966
      // e6c4: bipush 2
      // e6c5: newarray 7
      // e6c7: dup
      // e6c8: bipush 0
      // e6c9: ldc2_w 0.66441810131073
      // e6cc: dastore
      // e6cd: dup
      // e6ce: bipush 1
      // e6cf: ldc2_w 1.080835500478704E-8
      // e6d2: dastore
      // e6d3: aastore
      // e6d4: dup
      // e6d5: sipush 967
      // e6d8: bipush 2
      // e6d9: newarray 7
      // e6db: dup
      // e6dc: bipush 0
      // e6dd: ldc2_w 0.664920449256897
      // e6e0: dastore
      // e6e1: dup
      // e6e2: bipush 1
      // e6e3: ldc2_w 4.920784622407246E-8
      // e6e6: dastore
      // e6e7: aastore
      // e6e8: dup
      // e6e9: sipush 968
      // e6ec: bipush 2
      // e6ed: newarray 7
      // e6ef: dup
      // e6f0: bipush 0
      // e6f1: ldc2_w 0.6654226779937744
      // e6f4: dastore
      // e6f5: dup
      // e6f6: bipush 1
      // e6f7: ldc2_w -4.544868396511241E-8
      // e6fa: dastore
      // e6fb: aastore
      // e6fc: dup
      // e6fd: sipush 969
      // e700: bipush 2
      // e701: newarray 7
      // e703: dup
      // e704: bipush 0
      // e705: ldc2_w 0.6659245491027832
      // e708: dastore
      // e709: dup
      // e70a: bipush 1
      // e70b: ldc2_w -3.448944157854234E-8
      // e70e: dastore
      // e70f: aastore
      // e710: dup
      // e711: sipush 970
      // e714: bipush 2
      // e715: newarray 7
      // e717: dup
      // e718: bipush 0
      // e719: ldc2_w 0.6664261817932129
      // e71c: dastore
      // e71d: dup
      // e71e: bipush 1
      // e71f: ldc2_w -3.6870882345139385E-8
      // e722: dastore
      // e723: aastore
      // e724: dup
      // e725: sipush 971
      // e728: bipush 2
      // e729: newarray 7
      // e72b: dup
      // e72c: bipush 0
      // e72d: ldc2_w 0.6669275760650635
      // e730: dastore
      // e731: dup
      // e732: bipush 1
      // e733: ldc2_w -5.234055273962444E-8
      // e736: dastore
      // e737: aastore
      // e738: dup
      // e739: sipush 972
      // e73c: bipush 2
      // e73d: newarray 7
      // e73f: dup
      // e740: bipush 0
      // e741: ldc2_w 0.6674286127090454
      // e744: dastore
      // e745: dup
      // e746: bipush 1
      // e747: ldc2_w 3.856291077979099E-8
      // e74a: dastore
      // e74b: aastore
      // e74c: dup
      // e74d: sipush 973
      // e750: bipush 2
      // e751: newarray 7
      // e753: dup
      // e754: bipush 0
      // e755: ldc2_w 0.6679295301437378
      // e758: dastore
      // e759: dup
      // e75a: bipush 1
      // e75b: ldc2_w -2.327375671320742E-9
      // e75e: dastore
      // e75f: aastore
      // e760: dup
      // e761: sipush 974
      // e764: bipush 2
      // e765: newarray 7
      // e767: dup
      // e768: bipush 0
      // e769: ldc2_w 0.6684302091598511
      // e76c: dastore
      // e76d: dup
      // e76e: bipush 1
      // e76f: ldc2_w -5.555080534042001E-8
      // e772: dastore
      // e773: aastore
      // e774: dup
      // e775: sipush 975
      // e778: bipush 2
      // e779: newarray 7
      // e77b: dup
      // e77c: bipush 0
      // e77d: ldc2_w 0.6689305305480957
      // e780: dastore
      // e781: dup
      // e782: bipush 1
      // e783: ldc2_w -1.6471487337453832E-9
      // e786: dastore
      // e787: aastore
      // e788: dup
      // e789: sipush 976
      // e78c: bipush 2
      // e78d: newarray 7
      // e78f: dup
      // e790: bipush 0
      // e791: ldc2_w 0.6694306135177612
      // e794: dastore
      // e795: dup
      // e796: bipush 1
      // e797: ldc2_w 4.042486803683015E-8
      // e79a: dastore
      // e79b: aastore
      // e79c: dup
      // e79d: sipush 977
      // e7a0: bipush 2
      // e7a1: newarray 7
      // e7a3: dup
      // e7a4: bipush 0
      // e7a5: ldc2_w 0.6699305772781372
      // e7a8: dastore
      // e7a9: dup
      // e7aa: bipush 1
      // e7ab: ldc2_w -4.8293856891818295E-8
      // e7ae: dastore
      // e7af: aastore
      // e7b0: dup
      // e7b1: sipush 978
      // e7b4: bipush 2
      // e7b5: newarray 7
      // e7b7: dup
      // e7b8: bipush 0
      // e7b9: ldc2_w 0.6704301834106445
      // e7bc: dastore
      // e7bd: dup
      // e7be: bipush 1
      // e7bf: ldc2_w -2.9134931730784303E-8
      // e7c2: dastore
      // e7c3: aastore
      // e7c4: dup
      // e7c5: sipush 979
      // e7c8: bipush 2
      // e7c9: newarray 7
      // e7cb: dup
      // e7cc: bipush 0
      // e7cd: ldc2_w 0.6709295511245728
      // e7d0: dastore
      // e7d1: dup
      // e7d2: bipush 1
      // e7d3: ldc2_w -2.1058207594753368E-8
      // e7d6: dastore
      // e7d7: aastore
      // e7d8: dup
      // e7d9: sipush 980
      // e7dc: bipush 2
      // e7dd: newarray 7
      // e7df: dup
      // e7e0: bipush 0
      // e7e1: ldc2_w 0.6714286804199219
      // e7e4: dastore
      // e7e5: dup
      // e7e6: bipush 1
      // e7e7: ldc2_w -2.3814619551682855E-8
      // e7ea: dastore
      // e7eb: aastore
      // e7ec: dup
      // e7ed: sipush 981
      // e7f0: bipush 2
      // e7f1: newarray 7
      // e7f3: dup
      // e7f4: bipush 0
      // e7f5: ldc2_w 0.6719275712966919
      // e7f8: dastore
      // e7f9: dup
      // e7fa: bipush 1
      // e7fb: ldc2_w -3.7155475428252136E-8
      // e7fe: dastore
      // e7ff: aastore
      // e800: dup
      // e801: sipush 982
      // e804: bipush 2
      // e805: newarray 7
      // e807: dup
      // e808: bipush 0
      // e809: ldc2_w 0.6724261045455933
      // e80c: dastore
      // e80d: dup
      // e80e: bipush 1
      // e80f: ldc2_w 5.8376834484391746E-8
      // e812: dastore
      // e813: aastore
      // e814: dup
      // e815: sipush 983
      // e818: bipush 2
      // e819: newarray 7
      // e81b: dup
      // e81c: bipush 0
      // e81d: ldc2_w 0.6729245185852051
      // e820: dastore
      // e821: dup
      // e822: bipush 1
      // e823: ldc2_w 2.4611679969129262E-8
      // e826: dastore
      // e827: aastore
      // e828: dup
      // e829: sipush 984
      // e82c: bipush 2
      // e82d: newarray 7
      // e82f: dup
      // e830: bipush 0
      // e831: ldc2_w 0.6734226942062378
      // e834: dastore
      // e835: dup
      // e836: bipush 1
      // e837: ldc2_w -1.899407107267079E-8
      // e83a: dastore
      // e83b: aastore
      // e83c: dup
      // e83d: sipush 985
      // e840: bipush 2
      // e841: newarray 7
      // e843: dup
      // e844: bipush 0
      // e845: ldc2_w 0.6739205121994019
      // e848: dastore
      // e849: dup
      // e84a: bipush 1
      // e84b: ldc2_w 4.7016079464436395E-8
      // e84e: dastore
      // e84f: aastore
      // e850: dup
      // e851: sipush 986
      // e854: bipush 2
      // e855: newarray 7
      // e857: dup
      // e858: bipush 0
      // e859: ldc2_w 0.6744182109832764
      // e85c: dastore
      // e85d: dup
      // e85e: bipush 1
      // e85f: ldc2_w -1.5529608026276525E-8
      // e862: dastore
      // e863: aastore
      // e864: dup
      // e865: sipush 987
      // e868: bipush 2
      // e869: newarray 7
      // e86b: dup
      // e86c: bipush 0
      // e86d: ldc2_w 0.6749155521392822
      // e870: dastore
      // e871: dup
      // e872: bipush 1
      // e873: ldc2_w 3.203391672602453E-8
      // e876: dastore
      // e877: aastore
      // e878: dup
      // e879: sipush 988
      // e87c: bipush 2
      // e87d: newarray 7
      // e87f: dup
      // e880: bipush 0
      // e881: ldc2_w 0.6754127740859985
      // e884: dastore
      // e885: dup
      // e886: bipush 1
      // e887: ldc2_w -4.8465821804075345E-8
      // e88a: dastore
      // e88b: aastore
      // e88c: dup
      // e88d: sipush 989
      // e890: bipush 2
      // e891: newarray 7
      // e893: dup
      // e894: bipush 0
      // e895: ldc2_w 0.6759096384048462
      // e898: dastore
      // e899: dup
      // e89a: bipush 1
      // e89b: ldc2_w -1.8364507801369988E-8
      // e89e: dastore
      // e89f: aastore
      // e8a0: dup
      // e8a1: sipush 990
      // e8a4: bipush 2
      // e8a5: newarray 7
      // e8a7: dup
      // e8a8: bipush 0
      // e8a9: ldc2_w 0.6764062643051147
      // e8ac: dastore
      // e8ad: dup
      // e8ae: bipush 1
      // e8af: ldc2_w 3.3739397633046517E-9
      // e8b2: dastore
      // e8b3: aastore
      // e8b4: dup
      // e8b5: sipush 991
      // e8b8: bipush 2
      // e8b9: newarray 7
      // e8bb: dup
      // e8bc: bipush 0
      // e8bd: ldc2_w 0.6769026517868042
      // e8c0: dastore
      // e8c1: dup
      // e8c2: bipush 1
      // e8c3: ldc2_w 1.6994526063192333E-8
      // e8c6: dastore
      // e8c7: aastore
      // e8c8: dup
      // e8c9: sipush 992
      // e8cc: bipush 2
      // e8cd: newarray 7
      // e8cf: dup
      // e8d0: bipush 0
      // e8d1: ldc2_w 0.6773988008499146
      // e8d4: dastore
      // e8d5: dup
      // e8d6: bipush 1
      // e8d7: ldc2_w 2.2741891590028428E-8
      // e8da: dastore
      // e8db: aastore
      // e8dc: dup
      // e8dd: sipush 993
      // e8e0: bipush 2
      // e8e1: newarray 7
      // e8e3: dup
      // e8e4: bipush 0
      // e8e5: ldc2_w 0.6778947114944458
      // e8e8: dastore
      // e8e9: dup
      // e8ea: bipush 1
      // e8eb: ldc2_w 2.0860312877435047E-8
      // e8ee: dastore
      // e8ef: aastore
      // e8f0: dup
      // e8f1: sipush 994
      // e8f4: bipush 2
      // e8f5: newarray 7
      // e8f7: dup
      // e8f8: bipush 0
      // e8f9: ldc2_w 0.678390383720398
      // e8fc: dastore
      // e8fd: dup
      // e8fe: bipush 1
      // e8ff: ldc2_w 1.1593703222523284E-8
      // e902: dastore
      // e903: aastore
      // e904: dup
      // e905: sipush 995
      // e908: bipush 2
      // e909: newarray 7
      // e90b: dup
      // e90c: bipush 0
      // e90d: ldc2_w 0.678885817527771
      // e910: dastore
      // e911: dup
      // e912: bipush 1
      // e913: ldc2_w -4.814386594291911E-9
      // e916: dastore
      // e917: aastore
      // e918: dup
      // e919: sipush 996
      // e91c: bipush 2
      // e91d: newarray 7
      // e91f: dup
      // e920: bipush 0
      // e921: ldc2_w 0.6793810129165649
      // e924: dastore
      // e925: dup
      // e926: bipush 1
      // e927: ldc2_w -2.812076759125914E-8
      // e92a: dastore
      // e92b: aastore
      // e92c: dup
      // e92d: sipush 997
      // e930: bipush 2
      // e931: newarray 7
      // e933: dup
      // e934: bipush 0
      // e935: ldc2_w 0.6798759698867798
      // e938: dastore
      // e939: dup
      // e93a: bipush 1
      // e93b: ldc2_w -5.808261186903479E-8
      // e93e: dastore
      // e93f: aastore
      // e940: dup
      // e941: sipush 998
      // e944: bipush 2
      // e945: newarray 7
      // e947: dup
      // e948: bipush 0
      // e949: ldc2_w 0.680370569229126
      // e94c: dastore
      // e94d: dup
      // e94e: bipush 1
      // e94f: ldc2_w 2.4751837654582522E-8
      // e952: dastore
      // e953: aastore
      // e954: dup
      // e955: sipush 999
      // e958: bipush 2
      // e959: newarray 7
      // e95b: dup
      // e95c: bipush 0
      // e95d: ldc2_w 0.6808650493621826
      // e960: dastore
      // e961: dup
      // e962: bipush 1
      // e963: ldc2_w -1.7793890245755405E-8
      // e966: dastore
      // e967: aastore
      // e968: dup
      // e969: sipush 1000
      // e96c: bipush 2
      // e96d: newarray 7
      // e96f: dup
      // e970: bipush 0
      // e971: ldc2_w 0.6813591718673706
      // e974: dastore
      // e975: dup
      // e976: bipush 1
      // e977: ldc2_w 5.294053246347931E-8
      // e97a: dastore
      // e97b: aastore
      // e97c: dup
      // e97d: sipush 1001
      // e980: bipush 2
      // e981: newarray 7
      // e983: dup
      // e984: bipush 0
      // e985: ldc2_w 0.681853175163269
      // e988: dastore
      // e989: dup
      // e98a: bipush 1
      // e98b: ldc2_w -1.2220826223585654E-9
      // e98e: dastore
      // e98f: aastore
      // e990: dup
      // e991: sipush 1002
      // e994: bipush 2
      // e995: newarray 7
      // e997: dup
      // e998: bipush 0
      // e999: ldc2_w 0.6823468208312988
      // e99c: dastore
      // e99d: dup
      // e99e: bipush 1
      // e99f: ldc2_w 5.8377876767612725E-8
      // e9a2: dastore
      // e9a3: aastore
      // e9a4: dup
      // e9a5: sipush 1003
      // e9a8: bipush 2
      // e9a9: newarray 7
      // e9ab: dup
      // e9ac: bipush 0
      // e9ad: ldc2_w 0.6828403472900391
      // e9b0: dastore
      // e9b1: dup
      // e9b2: bipush 1
      // e9b3: ldc2_w -6.437492120743254E-9
      // e9b6: dastore
      // e9b7: aastore
      // e9b8: dup
      // e9b9: sipush 1004
      // e9bc: bipush 2
      // e9bd: newarray 7
      // e9bf: dup
      // e9c0: bipush 0
      // e9c1: ldc2_w 0.6833335161209106
      // e9c4: dastore
      // e9c5: dup
      // e9c6: bipush 1
      // e9c7: ldc2_w 4.2990710043633113E-8
      // e9ca: dastore
      // e9cb: aastore
      // e9cc: dup
      // e9cd: sipush 1005
      // e9d0: bipush 2
      // e9d1: newarray 7
      // e9d3: dup
      // e9d4: bipush 0
      // e9d5: ldc2_w 0.6838265657424927
      // e9d8: dastore
      // e9d9: dup
      // e9da: bipush 1
      // e9db: ldc2_w -3.1516131027023284E-8
      // e9de: dastore
      // e9df: aastore
      // e9e0: dup
      // e9e1: sipush 1006
      // e9e4: bipush 2
      // e9e5: newarray 7
      // e9e7: dup
      // e9e8: bipush 0
      // e9e9: ldc2_w 0.684319257736206
      // e9ec: dastore
      // e9ed: dup
      // e9ee: bipush 1
      // e9ef: ldc2_w 8.70017386744679E-9
      // e9f2: dastore
      // e9f3: aastore
      // e9f4: dup
      // e9f5: sipush 1007
      // e9f8: bipush 2
      // e9f9: newarray 7
      // e9fb: dup
      // e9fc: bipush 0
      // e9fd: ldc2_w 0.6848117113113403
      // ea00: dastore
      // ea01: dup
      // ea02: bipush 1
      // ea03: ldc2_w 4.466959125843237E-8
      // ea06: dastore
      // ea07: aastore
      // ea08: dup
      // ea09: sipush 1008
      // ea0c: bipush 2
      // ea0d: newarray 7
      // ea0f: dup
      // ea10: bipush 0
      // ea11: ldc2_w 0.6853040456771851
      // ea14: dastore
      // ea15: dup
      // ea16: bipush 1
      // ea17: ldc2_w -4.25782656420497E-8
      // ea1a: dastore
      // ea1b: aastore
      // ea1c: dup
      // ea1d: sipush 1009
      // ea20: bipush 2
      // ea21: newarray 7
      // ea23: dup
      // ea24: bipush 0
      // ea25: ldc2_w 0.6857960224151611
      // ea28: dastore
      // ea29: dup
      // ea2a: bipush 1
      // ea2b: ldc2_w -1.4386267593671393E-8
      // ea2e: dastore
      // ea2f: aastore
      // ea30: dup
      // ea31: sipush 1010
      // ea34: bipush 2
      // ea35: newarray 7
      // ea37: dup
      // ea38: bipush 0
      // ea39: ldc2_w 0.6862877607345581
      // ea3c: dastore
      // ea3d: dup
      // ea3e: bipush 1
      // ea3f: ldc2_w 1.0274494061148778E-8
      // ea42: dastore
      // ea43: aastore
      // ea44: dup
      // ea45: sipush 1011
      // ea48: bipush 2
      // ea49: newarray 7
      // ea4b: dup
      // ea4c: bipush 0
      // ea4d: ldc2_w 0.686779260635376
      // ea50: dastore
      // ea51: dup
      // ea52: bipush 1
      // ea53: ldc2_w 3.164186629229597E-8
      // ea56: dastore
      // ea57: aastore
      // ea58: dup
      // ea59: sipush 1012
      // ea5c: bipush 2
      // ea5d: newarray 7
      // ea5f: dup
      // ea60: bipush 0
      // ea61: ldc2_w 0.6872705221176147
      // ea64: dastore
      // ea65: dup
      // ea66: bipush 1
      // ea67: ldc2_w 4.995334552140326E-8
      // ea6a: dastore
      // ea6b: aastore
      // ea6c: dup
      // ea6d: sipush 1013
      // ea70: bipush 2
      // ea71: newarray 7
      // ea73: dup
      // ea74: bipush 0
      // ea75: ldc2_w 0.687761664390564
      // ea78: dastore
      // ea79: dup
      // ea7a: bipush 1
      // ea7b: ldc2_w -5.3763211240398744E-8
      // ea7e: dastore
      // ea7f: aastore
      // ea80: dup
      // ea81: sipush 1014
      // ea84: bipush 2
      // ea85: newarray 7
      // ea87: dup
      // ea88: bipush 0
      // ea89: ldc2_w 0.6882524490356445
      // ea8c: dastore
      // ea8d: dup
      // ea8e: bipush 1
      // ea8f: ldc2_w -4.0852427502515625E-8
      // ea92: dastore
      // ea93: aastore
      // ea94: dup
      // ea95: sipush 1015
      // ea98: bipush 2
      // ea99: newarray 7
      // ea9b: dup
      // ea9c: bipush 0
      // ea9d: ldc2_w 0.688742995262146
      // eaa0: dastore
      // eaa1: dup
      // eaa2: bipush 1
      // eaa3: ldc2_w -3.0287143914420064E-8
      // eaa6: dastore
      // eaa7: aastore
      // eaa8: dup
      // eaa9: sipush 1016
      // eaac: bipush 2
      // eaad: newarray 7
      // eaaf: dup
      // eab0: bipush 0
      // eab1: ldc2_w 0.6892333030700684
      // eab4: dastore
      // eab5: dup
      // eab6: bipush 1
      // eab7: ldc2_w -2.183125937905008E-8
      // eaba: dastore
      // eabb: aastore
      // eabc: dup
      // eabd: sipush 1017
      // eac0: bipush 2
      // eac1: newarray 7
      // eac3: dup
      // eac4: bipush 0
      // eac5: ldc2_w 0.6897233724594116
      // eac8: dastore
      // eac9: dup
      // eaca: bipush 1
      // eacb: ldc2_w -1.524901992178814E-8
      // eace: dastore
      // eacf: aastore
      // ead0: dup
      // ead1: sipush 1018
      // ead4: bipush 2
      // ead5: newarray 7
      // ead7: dup
      // ead8: bipush 0
      // ead9: ldc2_w 0.6902132034301758
      // eadc: dastore
      // eadd: dup
      // eade: bipush 1
      // eadf: ldc2_w -1.0305018010328949E-8
      // eae2: dastore
      // eae3: aastore
      // eae4: dup
      // eae5: sipush 1019
      // eae8: bipush 2
      // eae9: newarray 7
      // eaeb: dup
      // eaec: bipush 0
      // eaed: ldc2_w 0.6907027959823608
      // eaf0: dastore
      // eaf1: dup
      // eaf2: bipush 1
      // eaf3: ldc2_w -6.764191876212205E-9
      // eaf6: dastore
      // eaf7: aastore
      // eaf8: dup
      // eaf9: sipush 1020
      // eafc: bipush 2
      // eafd: newarray 7
      // eaff: dup
      // eb00: bipush 0
      // eb01: ldc2_w 0.6911921501159668
      // eb04: dastore
      // eb05: dup
      // eb06: bipush 1
      // eb07: ldc2_w -4.391824838015402E-9
      // eb0a: dastore
      // eb0b: aastore
      // eb0c: dup
      // eb0d: sipush 1021
      // eb10: bipush 2
      // eb11: newarray 7
      // eb13: dup
      // eb14: bipush 0
      // eb15: ldc2_w 0.6916812658309937
      // eb18: dastore
      // eb19: dup
      // eb1a: bipush 1
      // eb1b: ldc2_w -2.9535446262017846E-9
      // eb1e: dastore
      // eb1f: aastore
      // eb20: dup
      // eb21: sipush 1022
      // eb24: bipush 2
      // eb25: newarray 7
      // eb27: dup
      // eb28: bipush 0
      // eb29: ldc2_w 0.6921701431274414
      // eb2c: dastore
      // eb2d: dup
      // eb2e: bipush 1
      // eb2f: ldc2_w -2.2153227096187463E-9
      // eb32: dastore
      // eb33: aastore
      // eb34: dup
      // eb35: sipush 1023
      // eb38: bipush 2
      // eb39: newarray 7
      // eb3b: dup
      // eb3c: bipush 0
      // eb3d: ldc2_w 0.6926587820053101
      // eb40: dastore
      // eb41: dup
      // eb42: bipush 1
      // eb43: ldc2_w -1.943473623641502E-9
      // eb46: dastore
      // eb47: aastore
      // eb48: putstatic org/apache/commons/math3/util/FastMathLiteralArrays.LN_MANT [[D
      // eb4b: return
   }
}
