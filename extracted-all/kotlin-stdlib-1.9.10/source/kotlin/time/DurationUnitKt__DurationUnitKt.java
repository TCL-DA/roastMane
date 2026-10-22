package kotlin.time

// $VF: Class flags could not be determined
internal class DurationUnitKt__DurationUnitKt : DurationUnitKt__DurationUnitJvmKt {
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun DurationUnit.shortName(): String {
      var var10000: java.lang.String;
      switch (DurationUnitKt__DurationUnitKt.WhenMappings.$EnumSwitchMapping$0[$this$shortName.ordinal()]) {
         case 1:
            var10000 = "ns";
            break;
         case 2:
            var10000 = "us";
            break;
         case 3:
            var10000 = "ms";
            break;
         case 4:
            var10000 = "s";
            break;
         case 5:
            var10000 = "m";
            break;
         case 6:
            var10000 = "h";
            break;
         case 7:
            var10000 = "d";
            break;
         default:
            throw new IllegalStateException(("Unknown unit: " + `$this$shortName`).toString());
      }

      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @JvmStatic
   internal fun durationUnitByShortName(shortName: String): DurationUnit {
      var var10000: DurationUnit;
      switch (shortName.hashCode()) {
         case 100:
            if (!shortName.equals("d")) {
               throw new IllegalArgumentException("Unknown duration unit short name: " + shortName);
            }

            var10000 = DurationUnit.DAYS;
            break;
         case 104:
            if (!shortName.equals("h")) {
               throw new IllegalArgumentException("Unknown duration unit short name: " + shortName);
            }

            var10000 = DurationUnit.HOURS;
            break;
         case 109:
            if (!shortName.equals("m")) {
               throw new IllegalArgumentException("Unknown duration unit short name: " + shortName);
            }

            var10000 = DurationUnit.MINUTES;
            break;
         case 115:
            if (!shortName.equals("s")) {
               throw new IllegalArgumentException("Unknown duration unit short name: " + shortName);
            }

            var10000 = DurationUnit.SECONDS;
            break;
         case 3494:
            if (!shortName.equals("ms")) {
               throw new IllegalArgumentException("Unknown duration unit short name: " + shortName);
            }

            var10000 = DurationUnit.MILLISECONDS;
            break;
         case 3525:
            if (!shortName.equals("ns")) {
               throw new IllegalArgumentException("Unknown duration unit short name: " + shortName);
            }

            var10000 = DurationUnit.NANOSECONDS;
            break;
         case 3742:
            if (shortName.equals("us")) {
               var10000 = DurationUnit.MICROSECONDS;
               break;
            }

            throw new IllegalArgumentException("Unknown duration unit short name: " + shortName);
         default:
            throw new IllegalArgumentException("Unknown duration unit short name: " + shortName);
      }

      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @JvmStatic
   internal fun durationUnitByIsoChar(isoChar: Char, isTimeComponent: Boolean): DurationUnit {
      var var10000: DurationUnit;
      if (!isTimeComponent) {
         if (isoChar != 'D') {
            throw new IllegalArgumentException("Invalid or unsupported duration ISO non-time unit: " + isoChar);
         }

         var10000 = DurationUnit.DAYS;
      } else if (isoChar == 'H') {
         var10000 = DurationUnit.HOURS;
      } else if (isoChar == 'M') {
         var10000 = DurationUnit.MINUTES;
      } else {
         if (isoChar != 'S') {
            throw new IllegalArgumentException("Invalid duration ISO time unit: " + isoChar);
         }

         var10000 = DurationUnit.SECONDS;
      }

      return var10000;
   }

   open fun DurationUnitKt__DurationUnitKt() {
   }
}
