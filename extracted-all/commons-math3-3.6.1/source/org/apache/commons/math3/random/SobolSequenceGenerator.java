package org.apache.commons.math3.random;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.MathParseException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;

public class SobolSequenceGenerator implements RandomVectorGenerator {
   private static final int BITS = 52;
   private static final double SCALE = FastMath.pow(2.0, 52);
   private static final int MAX_DIMENSION = 1000;
   private static final String RESOURCE_NAME = "/assets/org/apache/commons/math3/random/new-joe-kuo-6.1000";
   private static final String FILE_CHARSET = "US-ASCII";
   private final int dimension;
   private int count = 0;
   private final long[][] direction;
   private final long[] x;

   public SobolSequenceGenerator(int dimension) throws OutOfRangeException {
      if (dimension >= 1 && dimension <= 1000) {
         InputStream is = this.getClass().getResourceAsStream("/assets/org/apache/commons/math3/random/new-joe-kuo-6.1000");
         if (is == null) {
            throw new MathInternalError();
         } else {
            this.dimension = dimension;
            this.direction = new long[dimension][53];
            this.x = new long[dimension];

            try {
               this.initFromStream(is);
            } catch (IOException var12) {
               throw new MathInternalError();
            } catch (MathParseException var13) {
               throw new MathInternalError();
            } finally {
               try {
                  is.close();
               } catch (IOException var11) {
               }
            }
         }
      } else {
         throw new OutOfRangeException(dimension, 1, 1000);
      }
   }

   public SobolSequenceGenerator(int dimension, InputStream is) throws NotStrictlyPositiveException, MathParseException, IOException {
      if (dimension < 1) {
         throw new NotStrictlyPositiveException(dimension);
      } else {
         this.dimension = dimension;
         this.direction = new long[dimension][53];
         this.x = new long[dimension];
         int lastDimension = this.initFromStream(is);
         if (lastDimension < dimension) {
            throw new OutOfRangeException(dimension, 1, lastDimension);
         }
      }
   }

   private int initFromStream(InputStream is) throws MathParseException, IOException {
      for (int i = 1; i <= 52; i++) {
         this.direction[0][i] = 1L << 52 - i;
      }

      Charset charset = Charset.forName("US-ASCII");
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
      int dim = -1;

      try {
         reader.readLine();
         int lineNumber = 2;
         int index = 1;

         String line;
         for (line = null; (line = reader.readLine()) != null; lineNumber++) {
            StringTokenizer st = new StringTokenizer(line, " ");

            try {
               dim = Integer.parseInt(st.nextToken());
               if (dim >= 2 && dim <= this.dimension) {
                  int s = Integer.parseInt(st.nextToken());
                  int a = Integer.parseInt(st.nextToken());
                  int[] m = new int[s + 1];

                  for (int i = 1; i <= s; i++) {
                     m[i] = Integer.parseInt(st.nextToken());
                  }

                  this.initDirectionVector(index++, a, m);
               }

               if (dim > this.dimension) {
                  return dim;
               }
            } catch (NoSuchElementException var17) {
               throw new MathParseException(line, lineNumber);
            } catch (NumberFormatException var18) {
               throw new MathParseException(line, lineNumber);
            }
         }

         return dim;
      } finally {
         reader.close();
      }
   }

   private void initDirectionVector(int d, int a, int[] m) {
      int s = m.length - 1;

      for (int i = 1; i <= s; i++) {
         this.direction[d][i] = (long)m[i] << 52 - i;
      }

      for (int i = s + 1; i <= 52; i++) {
         this.direction[d][i] = this.direction[d][i - s] ^ this.direction[d][i - s] >> s;

         for (int k = 1; k <= s - 1; k++) {
            this.direction[d][i] = this.direction[d][i] ^ (long)(a >> s - 1 - k & 1) * this.direction[d][i - k];
         }
      }
   }

   @Override
   public double[] nextVector() {
      double[] v = new double[this.dimension];
      if (this.count == 0) {
         this.count++;
         return v;
      } else {
         int c = 1;

         for (int value = this.count - 1; (value & 1) == 1; c++) {
            value >>= 1;
         }

         for (int i = 0; i < this.dimension; i++) {
            this.x[i] = this.x[i] ^ this.direction[i][c];
            v[i] = (double)this.x[i] / SCALE;
         }

         this.count++;
         return v;
      }
   }

   public double[] skipTo(int index) throws NotPositiveException {
      if (index == 0) {
         Arrays.fill(this.x, 0L);
      } else {
         int i = index - 1;
         long grayCode = (long)(i ^ i >> 1);

         for (int j = 0; j < this.dimension; j++) {
            long result = 0L;

            for (int k = 1; k <= 52; k++) {
               long shift = grayCode >> k - 1;
               if (shift == 0L) {
                  break;
               }

               long ik = shift & 1L;
               result ^= ik * this.direction[j][k];
            }

            this.x[j] = result;
         }
      }

      this.count = index;
      return this.nextVector();
   }

   public int getNextIndex() {
      return this.count;
   }
}
