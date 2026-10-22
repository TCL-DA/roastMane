package org.apache.commons.math3.primes;

import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class Primes {
   private Primes() {
   }

   public static boolean isPrime(int n) {
      if (n < 2) {
         return false;
      } else {
         for (int p : SmallPrimes.PRIMES) {
            if (0 == n % p) {
               return n == p;
            }
         }

         return SmallPrimes.millerRabinPrimeTest(n);
      }
   }

   public static int nextPrime(int n) {
      if (n < 0) {
         throw new MathIllegalArgumentException(LocalizedFormats.NUMBER_TOO_SMALL, n, 0);
      } else if (n == 2) {
         return 2;
      } else {
         n |= 1;
         if (n == 1) {
            return 2;
         } else if (isPrime(n)) {
            return n;
         } else {
            int rem = n % 3;
            if (0 == rem) {
               n += 2;
            } else if (1 == rem) {
               n += 4;
            }

            while (!isPrime(n)) {
               n += 2;
               if (isPrime(n)) {
                  return n;
               }

               n += 4;
            }

            return n;
         }
      }
   }

   public static List<Integer> primeFactors(int n) {
      if (n < 2) {
         throw new MathIllegalArgumentException(LocalizedFormats.NUMBER_TOO_SMALL, n, 2);
      } else {
         return SmallPrimes.trialDivision(n);
      }
   }
}
