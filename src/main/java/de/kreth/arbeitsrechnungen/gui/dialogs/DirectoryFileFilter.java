package de.kreth.arbeitsrechnungen.gui.dialogs;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class DirectoryFileFilter extends FileFilter {

   @Override
   public String getDescription() {
      return "Nur Verzeichnisse";
   }

   @Override
   public boolean accept(File f) {
      return f.isDirectory();
   }
}
