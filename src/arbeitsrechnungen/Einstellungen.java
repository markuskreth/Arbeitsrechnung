package arbeitsrechnungen;

/**
 *
 * @author markus
 */
import java.util.Properties;

public class Einstellungen {

    public Properties getEinstellungen(){
        Properties optionen = new Properties();
        java.util.Properties sysprops = System.getProperties();
        java.io.File optionfile  = new java.io.File(sysprops.getProperty("user.home") + sysprops.getProperty("file.separator") + ".arbeitrechnungen"
                + sysprops.getProperty("file.separator") + "optionen.ini");

        try{
            optionen.load(new java.io.FileInputStream(optionfile));
            return optionen;
        }catch(Exception e){
            System.err.println("ArbeitsstundenTabelle.java: Options-Datei konnte nicht geladen werden.");
            return null;
        }
    }
}
