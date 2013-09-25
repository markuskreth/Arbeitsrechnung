/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.kreth.arbeitsrechnungen;

/**
 *
 * @author markus
 * Dieser Renderer formatiert eine jTable
 * Die letzte Spalte wird rechtsbündig formatiert
 * Die vorletzte Spalte wird zentriert.
 * Die letzte Zeile wird mit SansSerif, fett, 12 formatiert und außerdem
 * wird die Vorder- und Hintergrundfarbe getauscht
 * 
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class StartFensterTableCellRenderer implements TableCellRenderer {

   private Font font = new Font("SansSerif", Font.BOLD, 12);

   public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

      JLabel label;
      if(!(value instanceof JLabel)) {
         label = new JLabel((String)value);
      } else {
         label = (JLabel) value;
      }
      label.setOpaque(true);
      
      label.setForeground(table.getForeground());
      label.setBackground(table.getBackground());

//      if(isSelected) {
//         label.setBackground(table.getSelectionBackground());
//         label.setForeground(table.getSelectionForeground());
//      }

      if(column == table.getColumnCount()-1) {
         label.setHorizontalAlignment(JLabel.RIGHT);
      }
      if(column == table.getColumnCount()-2) {
         label.setHorizontalAlignment(JLabel.CENTER);
      }
      if(row == table.getRowCount()-1){
          label.setFont(font);
          label.setForeground(table.getBackground());
          label.setBackground(table.getForeground());
      }else {
          label.setFont(table.getFont());
      }
    
      return label;
   }

}
