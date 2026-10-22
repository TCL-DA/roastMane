package impl.org.controlsfx;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.TableViewSkinBase;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Region;

public class ReflectionUtils {
   private ReflectionUtils() {
   }

   public static Optional<NestedTableColumnHeader> getRootHeaderFrom(TableHeaderRow tableHeaderRow) {
      try {
         Method method = tableHeaderRow.getClass().getDeclaredMethod("getRootHeader");
         method.setAccessible(true);
         return Optional.of((NestedTableColumnHeader)method.invoke(tableHeaderRow));
      } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException var2) {
         var2.printStackTrace();
         return Optional.empty();
      }
   }

   public static Optional<Region> columnReorderLine(NestedTableColumnHeader tableColumnHeader) {
      try {
         Field field = tableColumnHeader.getClass().getDeclaredField("columnReorderLine");
         field.setAccessible(true);
         return Optional.of((Region)field.get(tableColumnHeader));
      } catch (NoSuchFieldException | IllegalAccessException var2) {
         var2.printStackTrace();
         return Optional.empty();
      }
   }

   public static Optional<TableHeaderRow> getTableHeaderRowFrom(TableViewSkin<?> skin) {
      try {
         Method method = skin.getClass().getSuperclass().getDeclaredMethod("getTableHeaderRow");
         method.setAccessible(true);
         return Optional.of((TableHeaderRow)method.invoke(skin));
      } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException var2) {
         var2.printStackTrace();
         return Optional.empty();
      }
   }

   public static Optional<TableViewSkinBase<?, ?, ?, ?, TableColumnBase<?, ?>>> getTableSkin(NestedTableColumnHeader tableColumnHeader) {
      try {
         Method method = tableColumnHeader.getClass().getDeclaredMethod("getTableSkin");
         method.setAccessible(true);
         return Optional.of((TableViewSkinBase<?, ?, ?, ?, TableColumnBase<?, ?>>)method.invoke(tableColumnHeader));
      } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException var2) {
         var2.printStackTrace();
         return Optional.empty();
      }
   }

   public static VirtualFlow getVirtualFlow(TableViewSkinBase tableViewSkinBase) {
      Class<? extends TableViewSkinBase> clazz = (Class<? extends TableViewSkinBase>)tableViewSkinBase.getClass();

      try {
         Field field = clazz.getDeclaredField("flow");
         field.setAccessible(true);
         return (VirtualFlow)field.get(tableViewSkinBase);
      } catch (NoSuchFieldException | IllegalAccessException var3) {
         throw new RuntimeException("Cannot fetch VirtualFlow from TableViewSkinBase");
      }
   }

   public static Integer onScroll(TableViewSkinBase tableViewSkinBase, String methodName, Boolean bool) {
      Class<?> clazz = tableViewSkinBase.getClass();

      try {
         Method method = clazz.getDeclaredMethod(methodName, Boolean.class);
         method.setAccessible(true);
         return (Integer)method.invoke(tableViewSkinBase, bool);
      } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException var5) {
         var5.printStackTrace();
         return -1;
      }
   }

   public static void callMethod(Object object, String methodName, Object... params) {
      Class<?> clazz = object.getClass();

      try {
         Method method = clazz.getDeclaredMethod(methodName, Arrays.stream(params).map(Object::getClass).toArray(Class[]::new));
         method.setAccessible(true);
         method.invoke(object, params);
      } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException var5) {
         throw new RuntimeException("Cannot call method " + methodName + " on " + object.getClass());
      }
   }
}
