package de.kreth.arbeitsrechnungen.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class LabelComponentBinding {

   private JLabel label;
   private JComponent component;

   public LabelComponentBinding(JLabel label, JComponent component) {
      super();
      this.label = label;
      this.component = component;
      this.label.addMouseListener(new BindingMouseListener());
   }

   private class BindingMouseListener extends MouseAdapter {

      @Override
      public void mouseClicked(MouseEvent e) {
         component.requestFocus();
      }
   }
}
