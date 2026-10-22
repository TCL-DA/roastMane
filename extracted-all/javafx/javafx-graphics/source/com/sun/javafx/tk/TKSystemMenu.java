package com.sun.javafx.tk;

import com.sun.javafx.menu.MenuBase;
import java.util.List;

public interface TKSystemMenu {
   boolean isSupported();

   void setMenus(List<MenuBase> var1);
}
