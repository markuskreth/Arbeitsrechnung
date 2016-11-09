package de.kreth.arbeitsrechnungen.gui.jframes.starttablemodels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;


public abstract class LabeledTableModel<T> implements TableModel {

   private final String[] titles;
   private final List<T> values = new ArrayList<T>();
   private final List<TableModelListener> listeners = new ArrayList<>();
   
   public LabeledTableModel(String[] titles) {
      super();
      this.titles = titles;
   }

   public boolean add(T e) {
      TableModelEvent ev = new TableModelEvent(this,  values.size());
      boolean add = values.add(e);
      tableChanged(ev);
      return add;
   }

   private void tableChanged(TableModelEvent ev) {
      for(TableModelListener l : listeners) {
         l.tableChanged(ev);
      }
   }
   
   public boolean addAll(Collection<? extends T> c) {
      TableModelEvent ev = new TableModelEvent(this, values.size(), values.size() + c.size()-1);
      boolean addAll = values.addAll(c);
      tableChanged(ev);
      return addAll;
   }

   public void clear() {
      
      values.clear();
      TableModelEvent e = new TableModelEvent(this);
      tableChanged(e);
   }

   @Override
   public int getRowCount() {
      return values.size();
   }

   @Override
   public int getColumnCount() {
      return titles.length;
   }

   @Override
   public String getColumnName(int columnIndex) {
      return titles[columnIndex];
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
   }

   /**
    * Not Supported! Values not writable.
    */
   @Override
   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
   }

   public T getItem(int rowIndex) {
      return values.get(rowIndex);
   }
   
   @Override
   public void addTableModelListener(TableModelListener l) {
      listeners.add(l);
   }

   @Override
   public void removeTableModelListener(TableModelListener l) {
      listeners.remove(l);
   }

}
