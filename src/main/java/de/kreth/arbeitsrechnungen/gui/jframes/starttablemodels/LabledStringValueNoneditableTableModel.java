package de.kreth.arbeitsrechnungen.gui.jframes.starttablemodels;

import javax.swing.table.DefaultTableModel;

/**
 * Tablemodel dem Titel übergeben werden können. Colums haben immer String-Class
 * und sind nicht edierbar.
 * 
 * @author markus
 */
public class LabledStringValueNoneditableTableModel extends DefaultTableModel {

   private static final long serialVersionUID = 1080987754441922362L;

   public LabledStringValueNoneditableTableModel(String[] titles) {
      super(new Object[][] {}, titles);
   }

   @Override
   public Class<?> getColumnClass(int columnIndex) {
      return String.class;
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
   }

}
