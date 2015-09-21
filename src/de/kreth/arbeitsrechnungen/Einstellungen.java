package de.kreth.arbeitsrechnungen;

/**
 *
 * @author markus
 */
import java.util.Properties;

import org.apache.log4j.Logger;

public class Einstellungen {

    public Options getEinstellungen(){
        Properties optionen = new Properties();
        java.util.Properties sysprops = System.getProperties();
        java.io.File optionfile  = new java.io.File(sysprops.getProperty("user.home") + sysprops.getProperty("file.separator") + ".arbeitrechnungen"
                + sysprops.getProperty("file.separator") + "optionen.ini");

        try{
            optionen.load(new java.io.FileInputStream(optionfile));
            Options.Build builder = new Options.Build(optionen);
            return builder.build();
        }catch(Exception e){
           Logger.getLogger(getClass()).error("Options-Datei konnte nicht geladen werden.", e);
            return null;
        }
    }
    
}
