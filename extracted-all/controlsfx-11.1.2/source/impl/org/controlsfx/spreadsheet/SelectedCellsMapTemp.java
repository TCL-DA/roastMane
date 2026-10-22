package impl.org.controlsfx.spreadsheet;

import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TablePositionBase;

public class SelectedCellsMapTemp<T extends TablePositionBase> {
   private final ObservableList<T> selectedCells = FXCollections.observableArrayList();
   private final ObservableList<T> sortedSelectedCells = new SortedList(this.selectedCells, (o1, o2) -> {
      int result = o1.getRow() - o2.getRow();
      return result == 0 ? o1.getColumn() - o2.getColumn() : result;
   });
   private final Map<Integer, BitSet> selectedCellBitSetMap;

   public SelectedCellsMapTemp(ListChangeListener<T> listener) {
      this.sortedSelectedCells.addListener(listener);
      this.selectedCellBitSetMap = new TreeMap<>((o1, o2) -> o1.compareTo(o2));
   }

   public int size() {
      return this.selectedCells.size();
   }

   public T get(int i) {
      return (T)(i < 0 ? null : this.sortedSelectedCells.get(i));
   }

   public void add(T tp) {
      int row = tp.getRow();
      int columnIndex = tp.getColumn();
      BitSet bitset;
      if (!this.selectedCellBitSetMap.containsKey(row)) {
         bitset = new BitSet();
         this.selectedCellBitSetMap.put(row, bitset);
      } else {
         bitset = this.selectedCellBitSetMap.get(row);
      }

      if (columnIndex >= 0) {
         boolean isAlreadySet = bitset.get(columnIndex);
         bitset.set(columnIndex);
         if (!isAlreadySet) {
            this.selectedCells.add(tp);
         }
      } else if (!this.selectedCells.contains(tp)) {
         this.selectedCells.add(tp);
      }
   }

   public void addAll(Collection<T> cells) {
      for (T tp : cells) {
         int row = tp.getRow();
         int columnIndex = tp.getColumn();
         BitSet bitset;
         if (!this.selectedCellBitSetMap.containsKey(row)) {
            bitset = new BitSet();
            this.selectedCellBitSetMap.put(row, bitset);
         } else {
            bitset = this.selectedCellBitSetMap.get(row);
         }

         if (columnIndex >= 0) {
            bitset.set(columnIndex);
         }
      }

      this.selectedCells.addAll(cells);
   }

   public void setAll(Collection<T> cells) {
      this.selectedCellBitSetMap.clear();

      for (T tp : cells) {
         int row = tp.getRow();
         int columnIndex = tp.getColumn();
         BitSet bitset;
         if (!this.selectedCellBitSetMap.containsKey(row)) {
            bitset = new BitSet();
            this.selectedCellBitSetMap.put(row, bitset);
         } else {
            bitset = this.selectedCellBitSetMap.get(row);
         }

         if (columnIndex >= 0) {
            bitset.set(columnIndex);
         }
      }

      this.selectedCells.setAll(cells);
   }

   public void remove(T tp) {
      int row = tp.getRow();
      int columnIndex = tp.getColumn();
      if (this.selectedCellBitSetMap.containsKey(row)) {
         BitSet bitset = this.selectedCellBitSetMap.get(row);
         if (columnIndex >= 0) {
            bitset.clear(columnIndex);
         }

         if (bitset.isEmpty()) {
            this.selectedCellBitSetMap.remove(row);
         }
      }

      this.selectedCells.remove(tp);
   }

   public void clear() {
      this.selectedCellBitSetMap.clear();
      this.selectedCells.clear();
   }

   public boolean isSelected(int row, int columnIndex) {
      if (columnIndex < 0) {
         return this.selectedCellBitSetMap.containsKey(row);
      } else {
         return this.selectedCellBitSetMap.containsKey(row) ? this.selectedCellBitSetMap.get(row).get(columnIndex) : false;
      }
   }

   public int indexOf(T tp) {
      return this.sortedSelectedCells.indexOf(tp);
   }

   public boolean isEmpty() {
      return this.selectedCells.isEmpty();
   }

   public ObservableList<T> getSelectedCells() {
      return this.selectedCells;
   }
}
