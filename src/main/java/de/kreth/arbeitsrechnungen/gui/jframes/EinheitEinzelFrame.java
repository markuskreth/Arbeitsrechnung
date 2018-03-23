/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Einheit_einzel.java
 * Created on 21.05.2009, 16:18:44
 */

package de.kreth.arbeitsrechnungen.gui.jframes;

import java.awt.Component;
/**
 * @author markus
 */
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.text.MaskFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.arbeitsrechnungen.Einstellungen;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Angebot;
import de.kreth.arbeitsrechnungen.data.Einheit;
import de.kreth.arbeitsrechnungen.data.Einheit.Builder;
import de.kreth.arbeitsrechnungen.persister.AngebotPersister;
import de.kreth.arbeitsrechnungen.persister.KlientPersister;
import de.kreth.arbeitsrechnungen.persister.KlientPersister.Auftraggeber;

public class EinheitEinzelFrame extends JFrame {

	private static final long serialVersionUID = 3963303174102985288L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final NumberFormat preisFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);

	private Options optionen;
	//
	// private int klient;
	// private int einheit = -1;
	// private boolean zusatz1 = false;
	// private boolean zusatz2 = false;
	// private String zusatz1_name = "";
	// private String zusatz2_name = "";
	private KlientPersister klientPersister;
	private AngebotPersister angebotPersister;
	private Auftraggeber klient;
	private List<Angebot> angebote;
	private Einheit einheit;

	/**
	 * Sollte nicht benutzt werden! Oder nur in Kombination mit setKlient() Creates
	 * new form Einheit_einzel
	 */
	public EinheitEinzelFrame() {
		this(1, 2);
	}

	/**
	 * Neuen Datensatz anlegen Creates new form Einheit_einzel
	 */
	public EinheitEinzelFrame(final int klient) {
		this(klient, -1);
	}

	/**
	 * Bestehenden Datensatz edieren Creates new form Einheit_einzel
	 */
	public EinheitEinzelFrame(final int klient, final int einheit) {

		optionen = Einstellungen.getInstance().getEinstellungen();
		klientPersister = new KlientPersister(optionen);
		angebotPersister = new AngebotPersister(optionen);

		this.klient = klientPersister.getAuftraggeber(klient);
		
		initComponents();
		initAngebote();
		setAuftraggeber();
		setZusaetze();
		if (einheit > -1) {
			this.einheit = klientPersister.getEinheitById(einheit);
		} else {
			this.einheit = new Einheit.Builder().klientenId(klient).id(einheit).datum(new Date()).build();
		}
		setEinheit();
	}

	private void setZusaetze() {
		if (this.klient.hasZusatz1())
			this.jLabel8.setText(this.klient.getZusatz1());
		else {
			this.jLabel8.setVisible(false);
			this.jTextFieldZusatz1.setVisible(false);
		}
		if (this.klient.hasZustz2())
			this.jLabel9.setText(this.klient.getZusatz2());
		else {
			this.jLabel9.setVisible(false);
			this.jTextFieldZusatz2.setVisible(false);
		}
	}

	private void setAuftraggeber() {

		if (this.klient != null) {
			this.jTextField1.setText(String.valueOf(this.klient.getKlientId()));

			this.jTextField6.setText(this.klient.getName());

		}
	}

	private void setEinheit() {
      // Füllt das Formular mit existierenden Feldern
      if (this.einheit != null) {

         if (this.einheit.getKlientenId() != this.klient.getKlientId()) {
            String msg = "Achtung!!!\n" + "Klienten_id des Konstruktors stimmt nicht mit der des übegebenen" + " Datensatzes überein!" + "\nDatensatz-Klient: " + einheit.getKlientenId()
                  + "\nKonstruktor-Klient: " + klient;
            JOptionPane.showMessageDialog(this, msg);
         }

         Angebot a = null;
         for (Angebot an : angebote) {
        	 if(an.getAngebote_id() == einheit.getAngebotId()) {
        		 a = an;
                 this.jComboBoxAngebot.setSelectedItem(a);
        	 }
         }

         if (einheit.getBeginn() != null) {
             // Set Uhrzeit Beginn
             String stzeit = DateFormat.getTimeInstance().format(einheit.getBeginn());
             stzeit = stzeit.substring(0, stzeit.length() - 2);
             this.jFormattedTextFieldStart.setText(stzeit);

         }
         if(einheit.getEnde() != null) {
             // Set Uhrzeit Ende
             String stzeit = DateFormat.getTimeInstance().format(einheit.getEnde());
             stzeit = stzeit.substring(0, stzeit.length() - 2);
             this.jFormattedTextFieldEnde.setText(stzeit);
         }
         
         this.jDateChooserDatum.setDate(einheit.getDatum());
         this.jTextFieldPreisAenderung.setText(String.valueOf(einheit.getPreisAenderung()));
         this.jTextFieldZusatz1.setText(einheit.getZusatz1());
         this.jTextFieldZusatz2.setText(einheit.getZusatz2());
         this.jDateChooserEingereicht.setDate(einheit.getRechnungDatum());
         this.jDateChooserBezahlt.setDate(einheit.getBezahltDatum());
      } else {
         JOptionPane.showMessageDialog(this, "Bitte wählen Sie einen Datensatz aus der Tabelle zum Edieren!", "Kein Datensatz ausgewählt!", JOptionPane.INFORMATION_MESSAGE);
         this.setVisible(false);
         this.dispose();
      }
   }

	private void initAngebote() {
		this.jComboBoxAngebot.removeAllItems();

		angebote = angebotPersister.getForKlient(this.klient.getKlientId());

		for (Angebot a : angebote) {
			this.jComboBoxAngebot.addItem(a);
		}
		if (angebote.size()>0) {
			this.jComboBoxAngebot.setSelectedIndex(0);
		}
	}

	private void saveData() {
		// Wenn this.einheit = -1 dann existiert der Datensatz noch nicht und
		// muss angelegt werden.

		Builder bld = new Einheit.Builder(einheit)
				.datum(jDateChooserDatum.getDate())
				.bezahltDatum(jDateChooserBezahlt.getDate())
				.rechnungDatum(jDateChooserEingereicht.getDate());

		// Setzten der Angebot-Elemente für diese Einheit
		
		Angebot a = (Angebot) jComboBoxAngebot.getSelectedItem();
		if (a != null) {
			bld.preisAenderung(Double.parseDouble(this.jTextFieldPreisAenderung.getText()));
			bld.angebotId(a.getAngebote_id());
			bld.angebot(a);

			if (a.isPreis_pro_stunde()) {
				Date einheitDatum = jDateChooserDatum.getDate();
				String starttext = this.jFormattedTextFieldStart.getText();
				Calendar startDate = parseDate(einheitDatum, starttext);
				bld.beginn(startDate.getTime());
				
				String endetext = this.jFormattedTextFieldEnde.getText();
				Calendar endecal =parseDate(einheitDatum, endetext);
				bld.ende(endecal.getTime());
				
				einheit = bld.build();

				if(logger.isDebugEnabled()) {
					logger.debug("Dauer: " + einheit.getDauerInMinutes() + " Minuten\n" 
							+ "Dauer: " + (double) einheit.getDauerInMinutes() / 60 
							+ " Stunden\n Preis: " + einheit.getPreis());
				}

				try {

					angebotPersister.storeEinheit(klient, einheit);
//					angebotPersister.storeEinheit(this.klient.getKlientId(), this.einheit.getId()
//							, preis, dauer, datum, eingereichtDatum,
//							isEingereicht, bezahltDatum, isBezahlt, sqlBeginn, sqlEnde, a.getAngebote_id(),
//							this.jTextFieldZusatz1.getText(), this.jTextFieldZusatz2.getText(),
//							this.jTextFieldPreisAenderung.getText());
				} catch (SQLException e) {
					logger.error("Error storing Einheit", e);
				}
			}
		}

		this.setVisible(false);
		this.dispose();
	}

	private Calendar parseDate(Date einheitDatum, String starttext) {
		GregorianCalendar startcal = new GregorianCalendar();
		startcal.setTime(einheitDatum);
		String[] startfeld = starttext.split(":");

		startcal.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(startfeld[0]));
		startcal.set(GregorianCalendar.MINUTE, Integer.parseInt(startfeld[1]));
		return startcal;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jTextField1 = new javax.swing.JTextField();
		jTextFieldPreisAenderung = new javax.swing.JTextField();
		jComboBoxAngebot = new javax.swing.JComboBox<Angebot>();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jLabel7 = new javax.swing.JLabel();
		jTextField6 = new javax.swing.JTextField();
		jFormattedTextFieldStart = new javax.swing.JFormattedTextField();
		jFormattedTextFieldEnde = new javax.swing.JFormattedTextField();
		jDateChooserDatum = new com.toedter.calendar.JDateChooser();
		Calendar now = new GregorianCalendar();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		jDateChooserDatum.setCalendar(now);

		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jTextFieldZusatz2 = new javax.swing.JTextField();
		jTextFieldZusatz1 = new javax.swing.JTextField();
		jLabel8 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		jDateChooserEingereicht = new com.toedter.calendar.JDateChooser();
		jDateChooserBezahlt = new com.toedter.calendar.JDateChooser();
		jLabel10 = new javax.swing.JLabel();
		jLabel11 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());

		setTitle(resourceMap.getString("Form.title")); // NOI18N
		setName("Form"); // NOI18N

		jLabel1.setFont(Font.getFont(resourceMap.getString("jLabel1.font"))); // NOI18N
		jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
		jLabel1.setName("jLabel1"); // NOI18N

		jTextField1.setEditable(false);
		jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
		jTextField1.setEnabled(false);
		jTextField1.setName("jTextField1"); // NOI18N

		jTextFieldPreisAenderung.setText(resourceMap.getString("jTextFieldPreisAenderung.text")); // NOI18N
		jTextFieldPreisAenderung.setMinimumSize(new java.awt.Dimension(70, 60));
		jTextFieldPreisAenderung.setName("jTextFieldPreisAenderung"); // NOI18N

		jComboBoxAngebot.setModel(new javax.swing.DefaultComboBoxModel<Angebot>());
		jComboBoxAngebot.setName("jComboBoxAngebot"); // NOI18N

		jComboBoxAngebot.setRenderer(new ListCellRenderer<Angebot>() {

			@Override
			public Component getListCellRendererComponent(JList<? extends Angebot> list, Angebot value, int index,
					boolean isSelected, boolean cellHasFocus) {
				if (value == null) {
					return new JLabel();
				}
				String text = value.getInhalt() + "|" + preisFormat.format(value.getPreis());
				return new JLabel(text);
			}
		});
		jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
		jLabel2.setName("jLabel2"); // NOI18N

		jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
		jLabel3.setName("jLabel3"); // NOI18N

		jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
		jLabel4.setName("jLabel4"); // NOI18N

		jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
		jLabel5.setName("jLabel5"); // NOI18N

		jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
		jLabel6.setName("jLabel6"); // NOI18N

		jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
		jLabel7.setName("jLabel7"); // NOI18N

		jTextField6.setEditable(false);
		jTextField6.setText(resourceMap.getString("jTextField6.text")); // NOI18N
		jTextField6.setEnabled(false);
		jTextField6.setName("jTextField6"); // NOI18N

		jFormattedTextFieldStart.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
				new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
		jFormattedTextFieldStart.setName("jFormattedTextFieldStart"); // NOI18N

		jFormattedTextFieldEnde.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
				new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
		jFormattedTextFieldEnde.setName("jFormattedTextFieldEnde"); // NOI18N

		jDateChooserDatum.setName("jDateChooserDatum"); // NOI18N

		jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
		jButton1.setName("jButton1"); // NOI18N
		jButton1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
		jButton2.setName("jButton2"); // NOI18N
		jButton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		jTextFieldZusatz2.setText(resourceMap.getString("jTextFieldZusatz2.text")); // NOI18N
		jTextFieldZusatz2.setName("jTextFieldZusatz2"); // NOI18N

		jTextFieldZusatz1.setText(resourceMap.getString("jTextFieldZusatz1.text")); // NOI18N
		jTextFieldZusatz1.setName("jTextFieldZusatz1"); // NOI18N

		jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
		jLabel8.setName("jLabel8"); // NOI18N

		jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
		jLabel9.setName("jLabel9"); // NOI18N

		jDateChooserEingereicht.setName("jDateChooserEingereicht"); // NOI18N

		jDateChooserBezahlt.setName("jDateChooserBezahlt"); // NOI18N

		jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
		jLabel10.setName("jLabel10"); // NOI18N

		jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
		jLabel11.setName("jLabel11"); // NOI18N

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel1))
						.addGroup(layout.createSequentialGroup().addGap(30, 30, 30).addGroup(layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jLabel2)
								.addComponent(jLabel3).addComponent(jLabel4).addComponent(jLabel5).addComponent(jLabel6)
								.addComponent(jLabel7).addComponent(jLabel11))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
												.addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 57,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 268,
														Short.MAX_VALUE))
										.addComponent(jComboBoxAngebot, 0, 331, Short.MAX_VALUE)
										.addGroup(layout.createSequentialGroup()
												.addComponent(jTextFieldPreisAenderung,
														javax.swing.GroupLayout.PREFERRED_SIZE, 115,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(203, 203, 203))
										.addComponent(jDateChooserDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 114,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGroup(layout.createSequentialGroup().addGroup(layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addGroup(layout.createSequentialGroup().addGroup(layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING,
																false)
														.addComponent(jFormattedTextFieldEnde,
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jFormattedTextFieldStart,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE, 63,
																Short.MAX_VALUE))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(jLabel8).addComponent(jLabel9)))
												.addComponent(jDateChooserEingereicht,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(jTextFieldZusatz1,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE, 194,
																Short.MAX_VALUE)
														.addComponent(jTextFieldZusatz2,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE, 194,
																Short.MAX_VALUE)
														.addGroup(layout.createSequentialGroup().addComponent(jLabel10)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(jDateChooserBezahlt,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))))))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								layout.createSequentialGroup().addContainerGap(262, Short.MAX_VALUE)
										.addComponent(jButton2)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jButton1)))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel1).addGap(18, 18, 18)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
								.addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel2).addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jComboBoxAngebot, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel3))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jLabel4)
								.addComponent(jDateChooserDatum, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(10, 10, 10)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
								.addComponent(jLabel5)
								.addComponent(jFormattedTextFieldStart, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel8).addComponent(
										jTextFieldZusatz1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel6)
								.addComponent(jFormattedTextFieldEnde, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel9)
								.addComponent(jTextFieldZusatz2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(layout
								.createSequentialGroup()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
										.addComponent(jTextFieldPreisAenderung, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel7))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jLabel11).addComponent(jDateChooserEingereicht,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jLabel10).addComponent(jDateChooserBezahlt,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jButton1).addComponent(jButton2))
						.addContainerGap()));

		MaskFormatter startmask;
		MaskFormatter endemask;
		try {
			startmask = new MaskFormatter("##:##");
			startmask.setPlaceholderCharacter('_');
			startmask.install(jFormattedTextFieldStart);
			endemask = new MaskFormatter("##:##");
			endemask.setPlaceholderCharacter('_');
			endemask.install(jFormattedTextFieldEnde);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		pack();
	}

	private void jButton2ActionPerformed(final ActionEvent evt) {
		// TODO Kontrolle der Eingaben auf vollständigkeit und richtigkeit

		saveData();
	}

	private void jButton1ActionPerformed(final ActionEvent evt) {
		// Fenster schließen
		this.setVisible(false);
		this.dispose();
	}

	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JComboBox<Angebot> jComboBoxAngebot;
	private com.toedter.calendar.JDateChooser jDateChooserBezahlt;
	private com.toedter.calendar.JDateChooser jDateChooserDatum;
	private com.toedter.calendar.JDateChooser jDateChooserEingereicht;
	private javax.swing.JFormattedTextField jFormattedTextFieldEnde;
	private javax.swing.JFormattedTextField jFormattedTextFieldStart;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField jTextField6;
	private javax.swing.JTextField jTextFieldPreisAenderung;
	private javax.swing.JTextField jTextFieldZusatz1;
	private javax.swing.JTextField jTextFieldZusatz2;

}
