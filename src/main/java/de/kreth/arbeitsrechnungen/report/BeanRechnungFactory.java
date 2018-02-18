package de.kreth.arbeitsrechnungen.report;

import java.util.ArrayList;
import java.util.List;

import de.kreth.arbeitsrechnungen.data.Rechnung;

public class BeanRechnungFactory {

   private static BeanRechnungFactory instance = null;
   
   private Rechnung rechnung = null;
   
   private BeanRechnungFactory() {
      
   }
   
   public static BeanRechnungFactory getInstance() {
      if (instance == null)  {
         instance = new BeanRechnungFactory();
      }
      return instance;
         
   }
   
   
   public void setRechnung(Rechnung rechnung) {
      this.rechnung = rechnung;
   }
   
   public static List<Rechnung> getRechnung() {
      if (instance != null && instance.rechnung != null) {
         List<Rechnung> rechnungen = new ArrayList<>();
         rechnungen.add(instance.rechnung);
         return rechnungen;
      }
      throw new IllegalStateException("Keine Rechnung gesetzt!");
   }
}
