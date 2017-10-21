package de.kreth.arbeitsrechnungen.report;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.kreth.arbeitsrechnungen.data.Arbeitsstunde;
import de.kreth.arbeitsrechnungen.data.Rechnung;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class RechnungDataSource implements JRDataSource {

   private Rechnung r;
   private Iterator<Arbeitsstunde> einheiten;
   private Arbeitsstunde current;

   private final Logger logger = LogManager.getLogger(getClass());
   
   public RechnungDataSource(Rechnung r) {
      super();
      this.r = r;
      einheiten = this.r.getEinheiten().iterator();
      logger.debug("Loglevel=" + logger.getLevel());
   }

   @SuppressWarnings("boxing")
   @Override
   public Object getFieldValue(JRField arg0) throws JRException {
      logger.trace(arg0.getName() + ": " + arg0.getValueClass());
      
      switch (arg0.getName()) {
         case "geldeingang":
            return r.getGeldeingang()!=null?r.getGeldeingang().getTime():null;
         case "Zusatz2_Name":
            return r.getZusatz2_name();
         case "zusatz1Val":
            return current.getZusatz1();
         case "zusatz2Val":
            return current.getZusatz2();
         case "datum":
            return current.getDatum();
         case "Dauer":
            return Float.valueOf(current.getDauerInMinutes());
         case "betrag":
            return current.getEinzelPreis().floatValue();
         default:
            break;
      }

      if(current != null) {
         
         Object findGetterAndExecute = findGetterAndExecute(arg0, current);
         if(findGetterAndExecute != null) {
            return findGetterAndExecute;
         }
               
      }
      
      return findGetterAndExecute(arg0, r);
   }

   private Object cast(JRField arg0, Object findGetterAndExecute) {
      if(findGetterAndExecute == null) {
         logger.warn("Didn't find Value for " + arg0.getName() + ": " + arg0.getValueClass());
      } else if(arg0.getValueClass().equals(findGetterAndExecute.getClass()) == false) {
         switch(arg0.getValueClass().getName()) {
            case "java.lang.Float":
               return Float.valueOf(findGetterAndExecute.toString());
         }
      }
      return findGetterAndExecute;
   }

   private Object findGetterAndExecute(JRField arg0, Object obj) {
      final String argName = arg0.getName();
      for (Method m: obj.getClass().getMethods()) {
         String methodName = m.getName().toLowerCase();
         
         if((methodName.startsWith("get") || methodName.startsWith("is")) 
               && methodName.endsWith(argName.toLowerCase())) {
            try {
               Object invoke = m.invoke(obj);
               logger.debug(argName + " (" + arg0.getValueClass() + ")=" + invoke);
               return cast(arg0, invoke);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
               logger.error("assumed method " + m + " to be getter for " + argName + ": " + arg0.getValueClass(), e);
            }
         }
      }
      return null;
   }

   @Override
   public boolean next() throws JRException {
      
      if (einheiten.hasNext()) {
         current = einheiten.next();
      } else {
         current = null;
      }
      
      return current != null;
   }

   
}
