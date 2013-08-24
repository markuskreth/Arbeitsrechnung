/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FormRechnungen.java
 *
 * Created on 22.09.2009, 19:08:09
 */

package arbeitsrechnungen;

/**
 *
 * @author markus
 */
import java.sql.ResultSet;
import arbeitsabrechnungendataclass.Verbindung;
import java.util.Properties;
import java.util.Vector;
//import java.lang.String;
//import java.lang.Exception;
import java.sql.SQLException;

import DatenKlassen.ClassRechnungen;
import java.beans.PropertyChangeSupport;
import java.util.Locale;


public class FormRechnungen extends javax.swing.JPanel {
	/** PropertyChangeEvent: eine Rechnung wurde geändert. */
	public static final String GEAENDERT = "FromRechnungen_geändert";
	/** PropertyChangeEvent: eine Rechnung wurde gelöscht. */
	public static final String GELOESCHT = "FromRechnungen_gelöscht";
    Verbindung verbindung;
    Properties optionen = new Properties();
	private PropertyChangeSupport pchListeners = new PropertyChangeSupport(this);

    int klienten_id;
    Vector<ClassRechnungen> rechnungen = new Vector<ClassRechnungen>();
	java.util.Properties sysprops = System.getProperties();

    /** FormRechnungen: nie benutzen! */
    // <editor-fold defaultstate="collapsed">
    public FormRechnungen() {
        this(1);
        javax.swing.JOptionPane.showMessageDialog(this, "Achtung!!! Falscher Konstruktor verwendet bei FormRechnungen", "Alarm!",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }// </editor-fold>

    /** Creates new form FormRechnungen */
    public FormRechnungen(int klienten_id) {
        super();
        optionen = getEinstellungen();
        verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));
        this.klienten_id = klienten_id;
        initComponents();
		System.out.println("Konstruktor FormRechnungen ausgeführt!");
        update();
    }

    private void update(){
        this.rechnungen.removeAllElements();
        
        String sql = "SELECT rechnungen_id, klienten_id, datum, rechnungnr, betrag, texdatei, pdfdatei," +
                "adresse, zusatz1, zusatz2, zusammenfassungen, zahldatum, geldeingang" +
                " FROM rechnungen WHERE klienten_id=" + this.klienten_id + ";";
        System.out.println("FormRechnungen: update: " + sql);
        ResultSet res_rechnungen = verbindung.query(sql);
        try {
            while (res_rechnungen.next()){
                ClassRechnungen tmp = new ClassRechnungen(res_rechnungen.getInt("rechnungen_id"));
                tmp.setKlienten_id(res_rechnungen.getInt("klienten_id"));
				java.util.GregorianCalendar kalender = new java.util.GregorianCalendar();
                 kalender.setTimeInMillis(res_rechnungen.getDate("datum").getTime());
                tmp.setDatum(kalender);
                tmp.setRechnungnr(res_rechnungen.getString("rechnungnr"));
                tmp.setBetrag(res_rechnungen.getDouble("betrag"));
                tmp.setTexdatei(res_rechnungen.getString("texdatei"));
                tmp.setPdfdatei(res_rechnungen.getString("pdfdatei"));
                tmp.setAdresse(res_rechnungen.getString("adresse"));
                tmp.setZusatz1(res_rechnungen.getBoolean("zusatz1"));
                tmp.setZusatz2(res_rechnungen.getBoolean("zusatz2"));
                tmp.setZusammenfassungen(res_rechnungen.getBoolean("zusammenfassungen"));
				java.util.GregorianCalendar kalender2 = new java.util.GregorianCalendar();
                 kalender2.setTimeInMillis(res_rechnungen.getDate("zahldatum").getTime());
                tmp.setZahldatum(kalender2);

                if (res_rechnungen.getDate("geldeingang") != null){
					java.util.GregorianCalendar kalender3 = new java.util.GregorianCalendar();
                    kalender3.setTimeInMillis(res_rechnungen.getDate("geldeingang").getTime());
                    tmp.setGeldeingang(kalender3);
                }
                this.rechnungen.add(tmp);
            }
            makeTable();
        }catch(SQLException e){
            System.err.println(e.toString());
            System.err.println(e.getSQLState());
        }
    }

    public void update(int klienten_id){
        this.klienten_id = klienten_id;
        update();
    }

    private void makeTable(){
        this.jTable1.setModel(getMyModel());
    }

    private javax.swing.table.DefaultTableModel getMyModel(){
        javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(){
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        };
        mymodel.setColumnIdentifiers(new String [] {
                "Datum", "Rechn.Nr", "Betrag", "Fällig", "Bezahlt"
            });
        java.text.DateFormat df;
        df = java.text.DateFormat.getDateInstance(java.text.DateFormat.DATE_FIELD, java.util.Locale.GERMAN);
        java.text.NumberFormat zf;
        zf = java.text.DecimalFormat.getCurrencyInstance(Locale.GERMANY);

        for (int i=0; i < this.rechnungen.size(); i++){
        //System.out.println(zf.format(rechnungen.elementAt(i).getBetrag()));
            Vector<String> zeile = new Vector<String>();
            zeile.add(df.format(rechnungen.elementAt(i).getDatum().getTime()));
            zeile.add(rechnungen.elementAt(i).getRechnungnr());
            zeile.add(zf.format(rechnungen.elementAt(i).getBetrag()));
            zeile.add(df.format(rechnungen.elementAt(i).getZahldatum().getTime()));
			if(rechnungen.elementAt(i).getGeldeingang() != null){
				zeile.add(df.format(rechnungen.elementAt(i).getGeldeingang().getTime()));
			}else{
				zeile.add("");
			}
            mymodel.addRow(zeile);
        }
        return mymodel;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonLoeschen = new javax.swing.JButton();
        jButtonAnsehen = new javax.swing.JButton();
        jButtonAendern = new javax.swing.JButton();
        jButtonBezahlt = new javax.swing.JButton();

        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(getMyModel());
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(arbeitsrechnungen.ArbeitsrechnungenApp.class).getContext().getResourceMap(FormRechnungen.class);
        jTable1.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable1.columnModel.title0")); // NOI18N
        jTable1.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable1.columnModel.title1")); // NOI18N
        jTable1.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTable1.columnModel.title2")); // NOI18N
        jTable1.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTable1.columnModel.title3")); // NOI18N

        jButtonLoeschen.setText(resourceMap.getString("jButtonLoeschen.text")); // NOI18N
        jButtonLoeschen.setName("jButtonLoeschen"); // NOI18N
        jButtonLoeschen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoeschenActionPerformed(evt);
            }
        });

        jButtonAnsehen.setText(resourceMap.getString("jButtonAnsehen.text")); // NOI18N
        jButtonAnsehen.setName("jButtonAnsehen"); // NOI18N
        jButtonAnsehen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnsehenActionPerformed(evt);
            }
        });

        jButtonAendern.setText(resourceMap.getString("jButtonAendern.text")); // NOI18N
        jButtonAendern.setName("jButtonAendern"); // NOI18N
        jButtonAendern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAendernActionPerformed(evt);
            }
        });

        jButtonBezahlt.setText(resourceMap.getString("jButtonBezahlt.text")); // NOI18N
        jButtonBezahlt.setName("jButtonBezahlt"); // NOI18N
        jButtonBezahlt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBezahltActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonBezahlt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addComponent(jButtonAendern)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAnsehen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonLoeschen)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonLoeschen)
                    .addComponent(jButtonAnsehen)
                    .addComponent(jButtonAendern)
                    .addComponent(jButtonBezahlt))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Rechnung ansehen - Viewer in optionen aufrufen
     * @param evt
     */
    private void jButtonAnsehenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnsehenActionPerformed
        String befehl = this.optionen.getProperty("pdfprogramm");
        if (this.jTable1.getSelectedRow() >= 0){
            System.out.println("Selected Row: " + this.jTable1.getSelectedRow());
            befehl += " " + this.optionen.getProperty("verzPdfFiles");
			if (!befehl.endsWith(sysprops.getProperty("file.separator"))){
				befehl += sysprops.getProperty("file.separator");
			}
			befehl += this.rechnungen.elementAt(jTable1.getSelectedRow()).getPdfdatei();
            System.out.println("FormRechnungen: " + befehl);
            try{
                Runtime.getRuntime().exec(befehl);
            }catch(java.io.IOException e){
                System.err.print("FormRechnungen: PDFansehen: ");
                e.printStackTrace();
            }
        }else{
            javax.swing.JOptionPane.showMessageDialog(null, "Zum Anzeigen bitte eine Rechnung auswählen.");
        }
    }//GEN-LAST:event_jButtonAnsehenActionPerformed

    private void jButtonAendernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAendernActionPerformed
        int rechnung_id = this.rechnungen.elementAt(this.jTable1.getSelectedRow()).getRechnungen_id();
// System.out.println("Elemente in rechnungen: " + this.rechnungen.size());
// System.out.println("Selected Column: " + this.jTable1.getSelectedColumn());
// System.out.println("Geefundene Rechnung_id: " + rechnung_id);
        RechnungDialog dialog = new RechnungDialog(null, rechnung_id);
        dialog.setVisible(true);
		pchListeners.fireIndexedPropertyChange(GEAENDERT, rechnung_id, true, false);
    }//GEN-LAST:event_jButtonAendernActionPerformed

	private void jButtonLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoeschenActionPerformed
		int rechnung_id = this.rechnungen.elementAt(this.jTable1.getSelectedRow()).getRechnungen_id();

		if(javax.swing.JOptionPane.showConfirmDialog(this.getParent(),
				"Wollen Sie die gewählte Rechnung endgültig löschen?",
				"Endgültige Löschung!", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION){
			String sql = "UPDATE einheiten SET Rechnung_verschickt=null, Rechnung_Datum=null, rechnung_id=null" +
                                           " WHERE rechnung_id=" + rechnung_id + ";";
			System.out.println("FormRechnungen: jButtonLoeschenActionPerformed: " + sql);
			if(verbindung.sql(sql)){
				// Weiter nur, wenn update der einheiten erfolgreich war. Sollte - weshalb nicht?
				sql = "DELETE from rechnungen WHERE rechnungen_id=" + rechnung_id + ";";
				if(verbindung.sql(sql)){
					sql = "Rechnung erfolgreich gelöscht!";
					pchListeners.fireIndexedPropertyChange(GELOESCHT, rechnung_id, true, false);
				}else{
					sql = "Achtung! Rechnung konnte nicht gelöscht werden!";
				}
				javax.swing.JOptionPane.showMessageDialog(this.getParent(), sql);
			}
		}
	}//GEN-LAST:event_jButtonLoeschenActionPerformed

	private void jButtonBezahltActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBezahltActionPerformed
		// TODO  Rechnungen identifizieren
		/**
		 * Bezahlte Einheiten...
		 */
//		Vector<Integer> einheiten = new Vector<Integer>();
		int rechnung_id;

		if (this.jTable1.getSelectedColumnCount() == 0){
			// keine Rechnungen gewählt
			javax.swing.JOptionPane.showMessageDialog(this, "Bitte wählen sie eine oder mehrere Rechnungen aus, um den Zahlungseingang zu bestätigen", "Keine Rechnung ausgewählt!", javax.swing.JOptionPane.ERROR_MESSAGE);
		}else{
			// eine Rechnung ausgewählt - direkt ausführen
			int[] rechnung = this.jTable1.getSelectedRows();
			String in_klausel = "(" + this.rechnungen.elementAt(rechnung[0]).getRechnungen_id();
			for (int i=1; i<rechnung.length; i++){
//				rechnung[i] = this.rechnungen.elementAt(i).getRechnungen_id();
				in_klausel += ", " + this.rechnungen.elementAt(rechnung[i]).getRechnungen_id();
			}
			in_klausel += ")";
			// Datum abfragen
			java.sql.Date sql_date;
			Kalenderauswahl kalender = new Kalenderauswahl(null);
			if(kalender.run()){
				sql_date = new java.sql.Date(kalender.getDatum().getTime());
				String sql = "UPDATE einheiten SET Bezahlt=1, Bezahlt_Datum=\"" + sql_date.toString() + "\" WHERE rechnung_id IN " + in_klausel + ";";
				System.out.println("FormRechnungen: jButtonBezahltAction: " + sql);
				if(verbindung.sql(sql)){
					sql = "UPDATE rechnungen SET geldeingang=\"" + sql_date.toString() + "\" WHERE rechnungen_id IN " + in_klausel + ";";
					if(verbindung.sql(sql)){
						javax.swing.JOptionPane.showMessageDialog(null, "Rechnung ist abgerechnet", "Rechnung bezahlt", javax.swing.JOptionPane.INFORMATION_MESSAGE);
						this.update();
						for (int i=1; i<rechnung.length; i++){
							pchListeners.fireIndexedPropertyChange(GEAENDERT, this.rechnungen.elementAt(rechnung[i]).getRechnungen_id(), true, false);
						}
					}else{
						javax.swing.JOptionPane.showMessageDialog(null, "Rechnung ist nicht abgerechnet!!!\nDie Einheiten aber schon!!! ", "Achtung! Achtung! Achtung!", javax.swing.JOptionPane.ERROR_MESSAGE);
					}
				}else{
					javax.swing.JOptionPane.showMessageDialog(null, "Rechnung ist nicht abgerechnet", "Achtung!", javax.swing.JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}//GEN-LAST:event_jButtonBezahltActionPerformed

    private Properties getEinstellungen(){
        java.util.Properties opt = new java.util.Properties();
        java.io.File optionfile  = new java.io.File(sysprops.getProperty("user.home") + sysprops.getProperty("file.separator") + ".arbeitrechnungen"
                + sysprops.getProperty("file.separator") + "optionen.ini");
        try{
            opt.load(new java.io.FileInputStream(optionfile));
            return opt;
        }catch(Exception e){
            System.err.println("ArbeitsstundenTabelle.java: Options-Datei konnte nicht geladen werden.");
            return null;
        }
    }

	@Override
	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener){
		System.out.println("FormRechnungen: Listener übergeben und wird hinzugefügt...");
		System.out.println("pchListeners hat bisher Listener: " + pchListeners.hasListeners(null));
		System.out.println("Jetzt add:");
		pchListeners.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener){
		pchListeners.removePropertyChangeListener(listener);
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAendern;
    private javax.swing.JButton jButtonAnsehen;
    private javax.swing.JButton jButtonBezahlt;
    private javax.swing.JButton jButtonLoeschen;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
