package org.apache.commons.math3.ml.neuralnet.twod;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.ml.neuralnet.FeatureInitializer;
import org.apache.commons.math3.ml.neuralnet.Network;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.SquareNeighbourhood;

public class NeuronSquareMesh2D implements Iterable<Neuron>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Network network;
   private final int numberOfRows;
   private final int numberOfColumns;
   private final boolean wrapRows;
   private final boolean wrapColumns;
   private final SquareNeighbourhood neighbourhood;
   private final long[][] identifiers;

   NeuronSquareMesh2D(boolean wrapRowDim, boolean wrapColDim, SquareNeighbourhood neighbourhoodType, double[][][] featuresList) {
      this.numberOfRows = featuresList.length;
      this.numberOfColumns = featuresList[0].length;
      if (this.numberOfRows < 2) {
         throw new NumberIsTooSmallException(this.numberOfRows, 2, true);
      } else if (this.numberOfColumns < 2) {
         throw new NumberIsTooSmallException(this.numberOfColumns, 2, true);
      } else {
         this.wrapRows = wrapRowDim;
         this.wrapColumns = wrapColDim;
         this.neighbourhood = neighbourhoodType;
         int fLen = featuresList[0][0].length;
         this.network = new Network(0L, fLen);
         this.identifiers = new long[this.numberOfRows][this.numberOfColumns];

         for (int i = 0; i < this.numberOfRows; i++) {
            for (int j = 0; j < this.numberOfColumns; j++) {
               this.identifiers[i][j] = this.network.createNeuron(featuresList[i][j]);
            }
         }

         this.createLinks();
      }
   }

   public NeuronSquareMesh2D(
      int numRows, boolean wrapRowDim, int numCols, boolean wrapColDim, SquareNeighbourhood neighbourhoodType, FeatureInitializer[] featureInit
   ) {
      if (numRows < 2) {
         throw new NumberIsTooSmallException(numRows, 2, true);
      } else if (numCols < 2) {
         throw new NumberIsTooSmallException(numCols, 2, true);
      } else {
         this.numberOfRows = numRows;
         this.wrapRows = wrapRowDim;
         this.numberOfColumns = numCols;
         this.wrapColumns = wrapColDim;
         this.neighbourhood = neighbourhoodType;
         this.identifiers = new long[this.numberOfRows][this.numberOfColumns];
         int fLen = featureInit.length;
         this.network = new Network(0L, fLen);

         for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
               double[] features = new double[fLen];

               for (int fIndex = 0; fIndex < fLen; fIndex++) {
                  features[fIndex] = featureInit[fIndex].value();
               }

               this.identifiers[i][j] = this.network.createNeuron(features);
            }
         }

         this.createLinks();
      }
   }

   private NeuronSquareMesh2D(boolean wrapRowDim, boolean wrapColDim, SquareNeighbourhood neighbourhoodType, Network net, long[][] idGrid) {
      this.numberOfRows = idGrid.length;
      this.numberOfColumns = idGrid[0].length;
      this.wrapRows = wrapRowDim;
      this.wrapColumns = wrapColDim;
      this.neighbourhood = neighbourhoodType;
      this.network = net;
      this.identifiers = idGrid;
   }

   public synchronized NeuronSquareMesh2D copy() {
      long[][] idGrid = new long[this.numberOfRows][this.numberOfColumns];

      for (int r = 0; r < this.numberOfRows; r++) {
         for (int c = 0; c < this.numberOfColumns; c++) {
            idGrid[r][c] = this.identifiers[r][c];
         }
      }

      return new NeuronSquareMesh2D(this.wrapRows, this.wrapColumns, this.neighbourhood, this.network.copy(), idGrid);
   }

   @Override
   public Iterator<Neuron> iterator() {
      return this.network.iterator();
   }

   public Network getNetwork() {
      return this.network;
   }

   public int getNumberOfRows() {
      return this.numberOfRows;
   }

   public int getNumberOfColumns() {
      return this.numberOfColumns;
   }

   public Neuron getNeuron(int i, int j) {
      if (i < 0 || i >= this.numberOfRows) {
         throw new OutOfRangeException(i, 0, this.numberOfRows - 1);
      } else if (j >= 0 && j < this.numberOfColumns) {
         return this.network.getNeuron(this.identifiers[i][j]);
      } else {
         throw new OutOfRangeException(j, 0, this.numberOfColumns - 1);
      }
   }

   public Neuron getNeuron(int row, int col, NeuronSquareMesh2D.HorizontalDirection alongRowDir, NeuronSquareMesh2D.VerticalDirection alongColDir) {
      int[] location = this.getLocation(row, col, alongRowDir, alongColDir);
      return location == null ? null : this.getNeuron(location[0], location[1]);
   }

   private int[] getLocation(int row, int col, NeuronSquareMesh2D.HorizontalDirection alongRowDir, NeuronSquareMesh2D.VerticalDirection alongColDir) {
      int colOffset;
      switch (alongRowDir) {
         case LEFT:
            colOffset = -1;
            break;
         case RIGHT:
            colOffset = 1;
            break;
         case CENTER:
            colOffset = 0;
            break;
         default:
            throw new MathInternalError();
      }

      int colIndex = col + colOffset;
      if (this.wrapColumns) {
         if (colIndex < 0) {
            colIndex += this.numberOfColumns;
         } else {
            colIndex %= this.numberOfColumns;
         }
      }

      int rowOffset;
      switch (alongColDir) {
         case UP:
            rowOffset = -1;
            break;
         case DOWN:
            rowOffset = 1;
            break;
         case CENTER:
            rowOffset = 0;
            break;
         default:
            throw new MathInternalError();
      }

      int rowIndex = row + rowOffset;
      if (this.wrapRows) {
         if (rowIndex < 0) {
            rowIndex += this.numberOfRows;
         } else {
            rowIndex %= this.numberOfRows;
         }
      }

      return rowIndex >= 0 && rowIndex < this.numberOfRows && colIndex >= 0 && colIndex < this.numberOfColumns ? new int[]{rowIndex, colIndex} : null;
   }

   private void createLinks() {
      List<Long> linkEnd = new ArrayList<>();
      int iLast = this.numberOfRows - 1;
      int jLast = this.numberOfColumns - 1;

      for (int i = 0; i < this.numberOfRows; i++) {
         for (int j = 0; j < this.numberOfColumns; j++) {
            linkEnd.clear();
            switch (this.neighbourhood) {
               case MOORE:
                  if (i > 0) {
                     if (j > 0) {
                        linkEnd.add(this.identifiers[i - 1][j - 1]);
                     }

                     if (j < jLast) {
                        linkEnd.add(this.identifiers[i - 1][j + 1]);
                     }
                  }

                  if (i < iLast) {
                     if (j > 0) {
                        linkEnd.add(this.identifiers[i + 1][j - 1]);
                     }

                     if (j < jLast) {
                        linkEnd.add(this.identifiers[i + 1][j + 1]);
                     }
                  }

                  if (this.wrapRows) {
                     if (i == 0) {
                        if (j > 0) {
                           linkEnd.add(this.identifiers[iLast][j - 1]);
                        }

                        if (j < jLast) {
                           linkEnd.add(this.identifiers[iLast][j + 1]);
                        }
                     } else if (i == iLast) {
                        if (j > 0) {
                           linkEnd.add(this.identifiers[0][j - 1]);
                        }

                        if (j < jLast) {
                           linkEnd.add(this.identifiers[0][j + 1]);
                        }
                     }
                  }

                  if (this.wrapColumns) {
                     if (j == 0) {
                        if (i > 0) {
                           linkEnd.add(this.identifiers[i - 1][jLast]);
                        }

                        if (i < iLast) {
                           linkEnd.add(this.identifiers[i + 1][jLast]);
                        }
                     } else if (j == jLast) {
                        if (i > 0) {
                           linkEnd.add(this.identifiers[i - 1][0]);
                        }

                        if (i < iLast) {
                           linkEnd.add(this.identifiers[i + 1][0]);
                        }
                     }
                  }

                  if (this.wrapRows && this.wrapColumns) {
                     if (i == 0 && j == 0) {
                        linkEnd.add(this.identifiers[iLast][jLast]);
                     } else if (i == 0 && j == jLast) {
                        linkEnd.add(this.identifiers[iLast][0]);
                     } else if (i == iLast && j == 0) {
                        linkEnd.add(this.identifiers[0][jLast]);
                     } else if (i == iLast && j == jLast) {
                        linkEnd.add(this.identifiers[0][0]);
                     }
                  }
               case VON_NEUMANN:
                  break;
               default:
                  throw new MathInternalError();
            }

            if (i > 0) {
               linkEnd.add(this.identifiers[i - 1][j]);
            }

            if (i < iLast) {
               linkEnd.add(this.identifiers[i + 1][j]);
            }

            if (this.wrapRows) {
               if (i == 0) {
                  linkEnd.add(this.identifiers[iLast][j]);
               } else if (i == iLast) {
                  linkEnd.add(this.identifiers[0][j]);
               }
            }

            if (j > 0) {
               linkEnd.add(this.identifiers[i][j - 1]);
            }

            if (j < jLast) {
               linkEnd.add(this.identifiers[i][j + 1]);
            }

            if (this.wrapColumns) {
               if (j == 0) {
                  linkEnd.add(this.identifiers[i][jLast]);
               } else if (j == jLast) {
                  linkEnd.add(this.identifiers[i][0]);
               }
            }

            Neuron aNeuron = this.network.getNeuron(this.identifiers[i][j]);

            for (long b : linkEnd) {
               Neuron bNeuron = this.network.getNeuron(b);
               this.network.addLink(aNeuron, bNeuron);
            }
         }
      }
   }

   private void readObject(ObjectInputStream in) {
      throw new IllegalStateException();
   }

   private Object writeReplace() {
      double[][][] featuresList = new double[this.numberOfRows][this.numberOfColumns][];

      for (int i = 0; i < this.numberOfRows; i++) {
         for (int j = 0; j < this.numberOfColumns; j++) {
            featuresList[i][j] = this.getNeuron(i, j).getFeatures();
         }
      }

      return new NeuronSquareMesh2D.SerializationProxy(this.wrapRows, this.wrapColumns, this.neighbourhood, featuresList);
   }

   public static enum HorizontalDirection {
      RIGHT,
      CENTER,
      LEFT;
   }

   private static class SerializationProxy implements Serializable {
      private static final long serialVersionUID = 20130226L;
      private final boolean wrapRows;
      private final boolean wrapColumns;
      private final SquareNeighbourhood neighbourhood;
      private final double[][][] featuresList;

      SerializationProxy(boolean wrapRows, boolean wrapColumns, SquareNeighbourhood neighbourhood, double[][][] featuresList) {
         this.wrapRows = wrapRows;
         this.wrapColumns = wrapColumns;
         this.neighbourhood = neighbourhood;
         this.featuresList = featuresList;
      }

      private Object readResolve() {
         return new NeuronSquareMesh2D(this.wrapRows, this.wrapColumns, this.neighbourhood, this.featuresList);
      }
   }

   public static enum VerticalDirection {
      UP,
      CENTER,
      DOWN;
   }
}
