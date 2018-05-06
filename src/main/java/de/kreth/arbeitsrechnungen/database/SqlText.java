
package de.kreth.arbeitsrechnungen.database;

/**
 *
 * @author markus
 *
 * Diese Klasse soll den Sqltext verwalten.
 * Select, update und insert-methoden sind public
 * über eine add-methode sollen für update und insert Felder augenommen werden
 * können, bevor der SQL-Testerstellt und zurück gegeben wird.
 *
 */
import java.util.Vector;

public class SqlText {
    private Vector<String> felderliste = new Vector<String>();   // Werte für die bearbeiteten Tabellenfelder
    protected String tabellenname;
    protected String where = "";

    /**
     * Get the value of where
     *
     * @return the value of where
     */
    public String getWhere() {
        return where;
    }

    /**
     * Set the value of where
     *
     * @param where new value of where
     */
    public void setWhere(String where) {
        this.where = where;
    }

    public SqlText(String Tabellenname, String spalten_namen){
        this.setTabellenname(Tabellenname);
        
        // Übergebene Felder aufteilen und speichern
        String[] meinefelder;
        String spalten = spalten_namen.replace(" ", "");
        meinefelder = spalten.split(",");
        for (int i=0; i<meinefelder.length; i++){
            felderliste.add(meinefelder[i]);
        }
    }

    /**
     * Get the value of tabellenname
     *
     * @return the value of tabellenname
     */

    public String getTabellenname() {
        return tabellenname;
    }

    /**
     * Set the value of tabellenname
     *
     * @param tabellenname new value of tabellenname
     */
    public void setTabellenname(String tabellenname) {
        this.tabellenname = tabellenname;
    }

    public String select(){
        String resultsql = "SELECT ";
        String feldliste = "";

        //Felder aufzählen und durch kommata trennen
        if(this.felderliste.isEmpty()) return "";
        for(int i=0; i<felderliste.size(); i++){
            if(feldliste.isEmpty()){
                feldliste = felderliste.elementAt(i);
            }else{
                feldliste = feldliste + ", " + this.felderliste.elementAt(i);
            }
        }

        resultsql = resultsql + feldliste + " FROM " + this.tabellenname +
                this.where + ";";
        return resultsql;
    }

    public void add(String Werte){
        /**
         * Ein Sting mit Werten wird erwartet - je getrennt durch kommata
         * Beachtet werden formate:
         * "Wert1, Wert2, \"Wert 3\""
         * "(Wert1, Wert2, \"Wert 3\")"
         * "(\"Wert1\",  Wert2, \"Wert 3\")"
         * gespeichert wird in Form "(\"Wert1\", \"Wert2\", ...)"
         */
        Vector<String> meinewerte = new Vector<String>();
        String[] meinefelder;
        meinefelder = Werte.split(",");
        for (int i=0; i<meinefelder.length; i++){
            meinewerte.add(meinefelder[i]);
        }
        // Führende Klammer entfernen
        if(meinewerte.elementAt(0).startsWith("(")){
            String dump = meinewerte.remove(0).substring(1);
            meinewerte.insertElementAt(dump, 0);
        }
        if(meinewerte.lastElement().endsWith(")")){
            int i = meinewerte.size();
            String dump = meinewerte.remove(i);
            meinewerte.add(dump);
        }
    }

    public String insert(){


        return "";
    }
}