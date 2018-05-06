/*
 * ArbeitsstundenTabelle.java
 * Created on 14.05.2009, 23:13:56
 */
package de.kreth.arbeitsrechnungen.gui.panels;

import java.awt.Rectangle;
/**
 * @author markus
 */
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toedter.calendar.JDateChooser;

import de.kreth.arbeitsrechnungen.ArbeitRechnungFactory;
import de.kreth.arbeitsrechnungen.ArbeitsstundenSpalten;
import de.kreth.arbeitsrechnungen.MySqlDate;
import de.kreth.arbeitsrechnungen.data.Arbeitsstunde;
import de.kreth.arbeitsrechnungen.data.Klient;
import de.kreth.arbeitsrechnungen.gui.dialogs.Kalenderauswahl;
import de.kreth.arbeitsrechnungen.gui.dialogs.RechnungDialog;
import de.kreth.arbeitsrechnungen.gui.jframes.EinheitEinzelFrame;
import de.kreth.arbeitsrechnungen.persister.AngebotPersister;
import de.kreth.arbeitsrechnungen.persister.DatenPersister;
import de.kreth.arbeitsrechnungen.persister.KlientPersister;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;

@SuppressWarnings("boxing")
public class ArbeitsstundenTabelle extends JPanel implements WindowListener {

	private static final int INVALID_SELECTION = -10;

	private static final long serialVersionUID = 8161115991876323549L;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private List<Arbeitsstunde> arbeitsstunden;
	private Window parent = null;
	private int anzahl = 0;
	private BigDecimal summe = BigDecimal.ZERO;
	private Double stundenzahl = null;
	private int klient;
	private boolean zusatz1 = false;
	private boolean zusatz2 = false;
	private String zusatz1_name = "";
	private String zusatz2_name = "";
	private TableColumn spalte5;
	private TableColumn spalte6;
	private Integer[] geloeschte_spalten = new Integer[2];

	private String filter = "(ISNULL(Bezahlt) OR ISNULL(Rechnung_verschickt))";

	private DatenPersister datenPersister;

	// public static final String TEXUMBRUCH = "\\\\\\\\";
	// public static final String TEXLINE = "\\\\hline";
	// public static final String TEXEURO = "\\\\officialeuro";
	public static final int EINGEREICHTE = 1;
	public static final int NICHTEINGEREICHTE = 2;
	public static final int OFFENE = 3;

	public void setParent(JFrame parent) {
		this.parent = parent;
	}

	public ArbeitsstundenTabelle(JFrame parent) {
		// Füllt die Tabelle mit den Feuerwehr-Arbeitsstunden
		this(parent, 1);
		logger.debug("ArbeitsstundenTabelle(JFrame parent)");
	}

	public ArbeitsstundenTabelle() {
		// Füllt die Tabelle mit den Feuerwehr-Arbeitsstunden
		this(null, 1);
	}

	/** Creates new form ArbeitsstundenTabelle */
	public ArbeitsstundenTabelle(Window parent, int klienten_id) {
		
		ArbeitRechnungFactory factory = ArbeitRechnungFactory.getInstance();
		datenPersister = factory.getPersister(DatenPersister.class);
		geloeschte_spalten[0] = null;
		geloeschte_spalten[1] = null;


		Klient kl = factory.getPersister(KlientenEditorPersister.class)
				.getKlientById(klienten_id);
		if (kl != null) {
			if (kl.hasZusatz1()) {
				this.zusatz1 = true;
				this.zusatz1_name = kl.getZusatz1_Name();
			}
			if (kl.hasZusatz2()) {
				this.zusatz2 = true;
				this.zusatz2_name = kl.getZusatz2_Name();
			}
		}
		initComponents();
		this.jTextFieldStundenzahl.setVisible(false);
		this.jLabel6.setVisible(false);
		this.parent = parent;
		this.arbeitsstunden = new ArrayList<>();
		setFilter();
		update(klienten_id);
	}

	/**
	 * Füllt anhand der Klienten_id und des gesetzten Filters die Tabelle mit Daten.
	 * 
	 * @param klienten_id
	 */
	private void readList(int klienten_id) {
		this.klient = klienten_id;

		anzahl = 0;
		summe = BigDecimal.ZERO;
		stundenzahl = null;

		arbeitsstunden.clear();

		try {
			arbeitsstunden.addAll(datenPersister.getEinheiten(klienten_id, filter));
		} catch (SQLException e) {
			logger.error("Failure fetching Arbeitsstunden", e);
		}

		for (Arbeitsstunde std : arbeitsstunden) {

			summe = summe.add(std.getPreis());
			anzahl = anzahl + 1;

			if (std.isPreisProStunde()) {
				if (stundenzahl == null) {
					stundenzahl = 0.0;
				}
				stundenzahl += std.getDauerInMinutes();
			}
		}

		if (stundenzahl != null)
			stundenzahl = stundenzahl / 60.0; // Minuten in Stunden umrechnen
	}

	public void update(int klienten_id) {
		this.readList(klienten_id);
		this.updateTable();

		DecimalFormat df = new DecimalFormat("0.00");
		this.jTextFieldAnzahl.setText(String.valueOf(this.anzahl));
		this.jTextFieldSumme.setText(df.format(summe) + " €");

		if (stundenzahl != null) {
			df = new DecimalFormat("0.0");
			this.jTextFieldStundenzahl.setVisible(true);
			this.jLabel6.setVisible(true);

			this.jTextFieldStundenzahl.setText(df.format(stundenzahl));
		} else {
			this.jTextFieldStundenzahl.setText("");
			this.jTextFieldStundenzahl.setVisible(false);
			this.jLabel6.setVisible(false);
		}

	}

	private void updateTable() {
		// int[] selected = this.jTable1.getSelectedRows();
		int[] breitenMax = new int[10];
		int[] breite = new int[10];
		int[] breitenOpt = new int[10];
		int[] breitenMin = new int[10];

		int j = 0; // Wenn Spalten eingefügt werden müssen, dann werden die
					// Einschübe addiert.
		for (int i = 0; i < this.jTable1.getColumnModel().getColumnCount(); i++) {
			breite[i + j] = this.jTable1.getColumnModel().getColumn(i).getWidth();
			breitenMax[i + j] = this.jTable1.getColumnModel().getColumn(i).getMaxWidth();
			breitenOpt[i + j] = this.jTable1.getColumnModel().getColumn(i).getPreferredWidth();
			breitenMin[i + j] = this.jTable1.getColumnModel().getColumn(i).getMinWidth();
			if (i == 4) {
				if (this.geloeschte_spalten[0] != null) {
					j += 1;
					breite[i + j] = this.spalte5.getWidth();
					breitenMax[i + j] = this.spalte5.getMaxWidth();
					breitenOpt[i + j] = this.spalte5.getPreferredWidth();
					breitenMin[i + j] = this.spalte5.getMinWidth();
				}
				if (this.geloeschte_spalten[1] != null) {
					j += 1;
					breite[i + j] = this.spalte6.getWidth();
					breitenMax[i + j] = this.spalte6.getMaxWidth();
					breitenOpt[i + j] = this.spalte6.getPreferredWidth();
					breitenMin[i + j] = this.spalte6.getMinWidth();
				}
			}
		}
		List<String> captions = Arrays.asList(ArbeitsstundenSpalten.Datum.toString(),
				ArbeitsstundenSpalten.Inhalt.toString(), ArbeitsstundenSpalten.Start.toString(),
				ArbeitsstundenSpalten.Ende.toString(), ArbeitsstundenSpalten.Preis.toString(), this.zusatz1_name,
				this.zusatz2_name, ArbeitsstundenSpalten.Preisänderung.toString(),
				ArbeitsstundenSpalten.Eingereicht.toString(), ArbeitsstundenSpalten.Bezahlt.toString());

		// Model mit Überschriften erstellen
		DefaultTableModel mymodel = new DefaultTableModel(new Object[][] {}, captions.toArray()) {

			private static final long serialVersionUID = 1913170267962749520L;

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
		};

		// Vektor aus Daten erstellen und einzeln zum Model hinzufügen
		DecimalFormat df = new DecimalFormat("0.00");

		for (int i = 0; i < arbeitsstunden.size(); i++) {
			Arbeitsstunde elementAt = this.arbeitsstunden.get(i);
			Vector<Object> daten = elementAt.toVector();
			daten.removeElementAt(0);
			daten.removeElementAt(0);
			daten.removeElementAt(0);
			String preis = df.format(daten.elementAt(4)) + " €";
			daten.removeElementAt(4);
			daten.add(4, preis);
			mymodel.addRow(daten);
		}

		jTable1.setModel(mymodel);
		for (int i = 0; i < breitenMax.length; i++) {
			jTable1.getColumnModel().getColumn(i).setWidth(breite[i]);
			jTable1.getColumnModel().getColumn(i).setMaxWidth(breitenMax[i]);
			jTable1.getColumnModel().getColumn(i).setPreferredWidth(breitenOpt[i]);
			jTable1.getColumnModel().getColumn(i).setMinWidth(breitenMin[i]);
		}

		if (!this.zusatz2) {
			this.geloeschte_spalten[1] = 6;
			this.spalte6 = jTable1.getColumnModel().getColumn(6);
			jTable1.removeColumn(jTable1.getColumnModel().getColumn(6));
		} else {
			this.geloeschte_spalten[1] = null;
		}
		if (!this.zusatz1) {
			this.geloeschte_spalten[0] = 5;
			this.spalte5 = jTable1.getColumnModel().getColumn(5);
			jTable1.removeColumn(jTable1.getColumnModel().getColumn(5));
		} else {
			this.geloeschte_spalten[0] = null;
		}

		scrollToVisible(jTable1, mymodel.getRowCount(), 0);
	}

	public static void scrollToVisible(JTable table, int rowIndex, int vColIndex) {

		if (!(table.getParent() instanceof JViewport)) {
			return;
		}

		Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
		table.scrollRectToVisible(rect);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		buttonGroupFilter = new ButtonGroup();
		jPopupMenu1 = new JPopupMenu();
		jMenuItembearbeiten = new JMenuItem();
		jSeparator1 = new JSeparator();
		jMenuItemEditAll = new JMenuItem();
		jMenuItemRechnungDatum = new JMenuItem();
		jMenuItemBezahltDatum = new JMenuItem();
		jMenuItem1 = new JMenuItem();
		jScrollPane1 = new JScrollPane();
		jTable1 = new JTable();
		jButtonDelete = new JButton();
		jButtonBearbeiten = new JButton();
		jButtonNeuerDatensatz = new JButton();
		jRadioButtonAbgeschlossene = new JRadioButton();
		jRadioButtonNichtBezahlte = new JRadioButton();
		jRadioButtonNichtEingereichte = new JRadioButton();
		jRadioButtonAlle = new JRadioButton();
		jLabel1 = new JLabel();
		jButtonRechnung = new JButton();
		jRadioButtonOffene = new JRadioButton();
		jTextFieldSumme = new JTextField();
		jLabel2 = new JLabel();
		jLabel3 = new JLabel();
		jTextFieldAnzahl = new JTextField();
		jDateChooserVonDatum = new JDateChooser();
		jDateChooserBisDatum = new JDateChooser();
		jLabel4 = new JLabel();
		jLabel5 = new JLabel();
		jTextFieldStundenzahl = new JTextField();
		jLabel6 = new JLabel();
		jLabel7 = new JLabel();

		jPopupMenu1.setInvoker(jTable1);
		jPopupMenu1.setName("jPopupMenu1"); // NOI18N

		ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());

		jMenuItembearbeiten.setText(resourceMap.getString("jMenuItembearbeiten.text")); // NOI18N
		jMenuItembearbeiten.setFocusPainted(true);
		jMenuItembearbeiten.setName("jMenuItembearbeiten"); // NOI18N
		jMenuItembearbeiten.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				jMenuItembearbeitenActionPerformed(evt);
			}
		});
		jPopupMenu1.add(jMenuItembearbeiten);

		jSeparator1.setName("jSeparator1"); // NOI18N
		jPopupMenu1.add(jSeparator1);

		jMenuItemEditAll.setText(resourceMap.getString("jMenuItemEditAll.text")); // NOI18N
		jMenuItemEditAll.setToolTipText(resourceMap.getString("jMenuItemEditAll.toolTipText")); // NOI18N
		jMenuItemEditAll.setName("jMenuItemEditAll"); // NOI18N
		jPopupMenu1.add(jMenuItemEditAll);

		jMenuItemRechnungDatum.setText(resourceMap.getString("jMenuItemRechnungDatum.text")); // NOI18N
		jMenuItemRechnungDatum.setName("jMenuItemRechnungDatum"); // NOI18N
		jMenuItemRechnungDatum.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				jMenuItemRechnungDatumActionPerformed(evt);
			}
		});
		jPopupMenu1.add(jMenuItemRechnungDatum);

		jMenuItemBezahltDatum.setText(resourceMap.getString("jMenuItemBezahltDatum.text")); // NOI18N
		jMenuItemBezahltDatum.setName("jMenuItemBezahltDatum"); // NOI18N
		jMenuItemBezahltDatum.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				jMenuItemBezahltDatumActionPerformed(evt);
			}
		});
		jPopupMenu1.add(jMenuItemBezahltDatum);

		jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
		jMenuItem1.setActionCommand(resourceMap.getString("jMenuItem1.actionCommand")); // NOI18N
		jMenuItem1.setName("jMenuItem1"); // NOI18N
		jMenuItem1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				jMenuItem1ActionPerformed(evt);
			}
		});
		jPopupMenu1.add(jMenuItem1);

		setName("Form"); // NOI18N

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		jTable1.setAutoCreateRowSorter(true);
		jTable1.setModel(new DefaultTableModel(
				new Object[][] {
						{ "13.12.09", null, "13:50", "14:50", "25,50", null, null, "", "25.12.09", "30.12.09" } },
				new String[] { "Datum", "Inhalt", "Start", "Ende", "Preis", "Zusatz1", "Zusatz2", "Preisänderung",
						"Eingereicht", "Bezahlt" }) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 516135999448412054L;
			Class<?>[] types = new Class[] { java.lang.String.class, java.lang.String.class, java.lang.String.class,
					java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
					java.lang.String.class, java.lang.String.class, java.lang.String.class };
			boolean[] canEdit = new boolean[] { false, false, false, false, false, false, false, false, false, false };

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		jTable1.setEditingColumn(0);
		jTable1.setEditingRow(0);
		jTable1.setName("jTable1"); // NOI18N
		jTable1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent evt) {
				jTable1MouseClicked(evt);
			}

			@Override
			public void mousePressed(MouseEvent evt) {
				jTable1MousePressed(evt);
			}
		});
		jScrollPane1.setViewportView(jTable1);
		jTable1.getColumnModel().getColumn(0).setMinWidth(80);
		jTable1.getColumnModel().getColumn(0).setPreferredWidth(80);
		jTable1.getColumnModel().getColumn(0).setMaxWidth(120);
		jTable1.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable1.columnModel.title0")); // NOI18N
		jTable1.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable1.columnModel.title8")); // NOI18N
		jTable1.getColumnModel().getColumn(2).setMinWidth(30);
		jTable1.getColumnModel().getColumn(2).setPreferredWidth(55);
		jTable1.getColumnModel().getColumn(2).setMaxWidth(75);
		jTable1.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTable1.columnModel.title1")); // NOI18N
		jTable1.getColumnModel().getColumn(3).setMinWidth(30);
		jTable1.getColumnModel().getColumn(3).setPreferredWidth(55);
		jTable1.getColumnModel().getColumn(3).setMaxWidth(75);
		jTable1.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTable1.columnModel.title2")); // NOI18N
		jTable1.getColumnModel().getColumn(4).setMinWidth(30);
		jTable1.getColumnModel().getColumn(4).setPreferredWidth(60);
		jTable1.getColumnModel().getColumn(4).setMaxWidth(70);
		jTable1.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTable1.columnModel.title3")); // NOI18N
		jTable1.getColumnModel().getColumn(5).setMinWidth(30);
		jTable1.getColumnModel().getColumn(5).setPreferredWidth(50);
		jTable1.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("jTable1.columnModel.title8")); // NOI18N
		jTable1.getColumnModel().getColumn(6).setMinWidth(30);
		jTable1.getColumnModel().getColumn(6).setPreferredWidth(50);
		jTable1.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("jTable1.columnModel.title9")); // NOI18N
		jTable1.getColumnModel().getColumn(7).setMinWidth(30);
		jTable1.getColumnModel().getColumn(7).setPreferredWidth(60);
		jTable1.getColumnModel().getColumn(7).setMaxWidth(70);
		jTable1.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("jTable1.columnModel.title4")); // NOI18N
		jTable1.getColumnModel().getColumn(8).setMinWidth(80);
		jTable1.getColumnModel().getColumn(8).setPreferredWidth(80);
		jTable1.getColumnModel().getColumn(8).setMaxWidth(120);
		jTable1.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("jTable1.columnModel.title5")); // NOI18N
		jTable1.getColumnModel().getColumn(9).setMinWidth(80);
		jTable1.getColumnModel().getColumn(9).setPreferredWidth(80);
		jTable1.getColumnModel().getColumn(9).setMaxWidth(120);
		jTable1.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("jTable1.columnModel.title6")); // NOI18N

		jButtonDelete.setText(resourceMap.getString("jButtonDelete.text")); // NOI18N
		jButtonDelete.setName("jButtonDelete"); // NOI18N
		jButtonDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				jButtonDeleteActionPerformed(evt);
			}
		});

		jButtonBearbeiten.setText(resourceMap.getString("jButtonBearbeiten.text")); // NOI18N
		jButtonBearbeiten.setName("jButtonBearbeiten"); // NOI18N
		jButtonBearbeiten.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				jButtonBearbeitenActionPerformed(evt);
			}
		});

		jButtonNeuerDatensatz.setText(resourceMap.getString("jButtonNeuerDatensatz.text")); // NOI18N
		jButtonNeuerDatensatz.setName("jButtonNeuerDatensatz"); // NOI18N
		jButtonNeuerDatensatz.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				jButtonNeuerDatensatzActionPerformed(evt);
			}
		});

		buttonGroupFilter.add(jRadioButtonAbgeschlossene);
		jRadioButtonAbgeschlossene.setText(resourceMap.getString("jRadioButtonAbgeschlossene.text")); // NOI18N
		jRadioButtonAbgeschlossene.setActionCommand(resourceMap.getString("jRadioButtonAbgeschlossene.actionCommand")); // NOI18N
		jRadioButtonAbgeschlossene.setName("jRadioButtonAbgeschlossene"); // NOI18N
		jRadioButtonAbgeschlossene.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent evt) {
				jRadioButtonAbgeschlosseneItemStateChanged(evt);
			}
		});

		buttonGroupFilter.add(jRadioButtonNichtBezahlte);
		jRadioButtonNichtBezahlte.setText(resourceMap.getString("jRadioButtonNichtBezahlte.text")); // NOI18N
		jRadioButtonNichtBezahlte.setActionCommand(resourceMap.getString("jRadioButtonNichtBezahlte.actionCommand")); // NOI18N
		jRadioButtonNichtBezahlte.setName("jRadioButtonNichtBezahlte"); // NOI18N
		jRadioButtonNichtBezahlte.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent evt) {
				jRadioButtonNichtBezahlteItemStateChanged(evt);
			}
		});

		buttonGroupFilter.add(jRadioButtonNichtEingereichte);
		jRadioButtonNichtEingereichte.setText(resourceMap.getString("jRadioButtonNichtEingereichte.text")); // NOI18N
		jRadioButtonNichtEingereichte
				.setActionCommand(resourceMap.getString("jRadioButtonNichtEingereichte.actionCommand")); // NOI18N
		jRadioButtonNichtEingereichte.setName("jRadioButtonNichtEingereichte"); // NOI18N
		jRadioButtonNichtEingereichte.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent evt) {
				jRadioButtonNichtEingereichteItemStateChanged(evt);
			}
		});

		buttonGroupFilter.add(jRadioButtonAlle);
		jRadioButtonAlle.setText(resourceMap.getString("jRadioButtonAlle.text")); // NOI18N
		jRadioButtonAlle.setActionCommand(resourceMap.getString("jRadioButtonAlle.actionCommand")); // NOI18N
		jRadioButtonAlle.setName("jRadioButtonAlle"); // NOI18N
		jRadioButtonAlle.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent evt) {
				jRadioButtonAlleItemStateChanged(evt);
			}
		});

		jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
		jLabel1.setName("jLabel1"); // NOI18N

		jButtonRechnung.setText(resourceMap.getString("jButtonRechnung.text")); // NOI18N
		jButtonRechnung.setName("jButtonRechnung"); // NOI18N
		jButtonRechnung.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				jButtonRechnungActionPerformed(evt);
			}
		});

		buttonGroupFilter.add(jRadioButtonOffene);
		jRadioButtonOffene.setSelected(true);
		jRadioButtonOffene.setText(resourceMap.getString("jRadioButtonOffene.text")); // NOI18N
		jRadioButtonOffene.setActionCommand(resourceMap.getString("jRadioButtonOffene.actionCommand")); // NOI18N
		jRadioButtonOffene.setName("jRadioButtonOffene"); // NOI18N
		jRadioButtonOffene.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent evt) {
				jRadioButtonOffeneItemStateChanged(evt);
			}
		});

		jTextFieldSumme.setEditable(false);
		jTextFieldSumme.setText(resourceMap.getString("jTextFieldSumme.text")); // NOI18N
		jTextFieldSumme.setFocusable(false);
		jTextFieldSumme.setName("jTextFieldSumme"); // NOI18N

		jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
		jLabel2.setName("jLabel2"); // NOI18N

		jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
		jLabel3.setName("jLabel3"); // NOI18N

		jTextFieldAnzahl.setEditable(false);
		jTextFieldAnzahl.setText(resourceMap.getString("jTextFieldAnzahl.text")); // NOI18N
		jTextFieldAnzahl.setFocusable(false);
		jTextFieldAnzahl.setName("jTextFieldAnzahl"); // NOI18N

		jDateChooserVonDatum.setName("jDateChooserVonDatum"); // NOI18N
		jDateChooserVonDatum.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

			@Override
			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				jDateChooserVonDatumPropertyChange(evt);
			}
		});

		jDateChooserBisDatum.setName("jDateChooserBisDatum"); // NOI18N
		jDateChooserBisDatum.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

			@Override
			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				jDateChooserBisDatumPropertyChange(evt);
			}
		});

		jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
		jLabel4.setName("jLabel4"); // NOI18N

		jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
		jLabel5.setName("jLabel5"); // NOI18N

		jTextFieldStundenzahl.setEditable(false);
		jTextFieldStundenzahl.setText(resourceMap.getString("jTextFieldStundenzahl.text")); // NOI18N
		jTextFieldStundenzahl.setFocusable(false);
		jTextFieldStundenzahl.setName("jTextFieldStundenzahl"); // NOI18N

		jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
		jLabel6.setName("jLabel6"); // NOI18N

		jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
		jLabel7.setName("jLabel7"); // NOI18N

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
				.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jButtonRechnung)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jButtonNeuerDatensatz, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jButtonBearbeiten, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jButtonDelete, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(162, Short.MAX_VALUE))
				.addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
						.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(layout.createSequentialGroup().addComponent(jRadioButtonNichtEingereichte)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jRadioButtonAbgeschlossene))
						.addGroup(layout.createSequentialGroup().addComponent(jLabel1)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jRadioButtonAlle)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jRadioButtonNichtBezahlte)))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addGroup(layout.createSequentialGroup().addComponent(jRadioButtonOffene)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jDateChooserVonDatum, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(jDateChooserBisDatum, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jLabel4)
								.addComponent(jLabel5))
						.addContainerGap(89, Short.MAX_VALUE))
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap(217, Short.MAX_VALUE).addComponent(jLabel6)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jTextFieldStundenzahl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jLabel3)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jTextFieldAnzahl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel2)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jTextFieldSumme,
								GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(jLabel7, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE));

		layout.linkSize(SwingConstants.HORIZONTAL,
				new java.awt.Component[] { jButtonBearbeiten, jButtonDelete, jButtonNeuerDatensatz });

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] { jRadioButtonAbgeschlossene,
				jRadioButtonAlle, jRadioButtonNichtBezahlte, jRadioButtonNichtEingereichte });

		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(jTextFieldSumme, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel2)
										.addComponent(jTextFieldAnzahl, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel3)
										.addComponent(jTextFieldStundenzahl, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel6))
								.addGap(13, 13, 13)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(jRadioButtonAlle).addComponent(jLabel1)
														.addComponent(jRadioButtonNichtBezahlte)
														.addComponent(jRadioButtonOffene))
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(jRadioButtonNichtEingereichte)
														.addComponent(jRadioButtonAbgeschlossene))
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 2,
														GroupLayout.PREFERRED_SIZE))
										.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 4,
														GroupLayout.PREFERRED_SIZE)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
														.addComponent(jLabel4).addComponent(jDateChooserVonDatum,
																GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
														.addComponent(jDateChooserBisDatum, GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel5))))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(jButtonRechnung).addComponent(jButtonNeuerDatensatz)
										.addComponent(jButtonBearbeiten).addComponent(jButtonDelete))
								.addGap(12, 12, 12).addComponent(jLabel7)));

	}

	private void jButtonNeuerDatensatzActionPerformed(ActionEvent evt) {

		ArbeitRechnungFactory factory = ArbeitRechnungFactory.getInstance();

		EinheitEinzelFrame fenster = new EinheitEinzelFrame(
				factory.getPersister(KlientPersister.class),
				factory.getPersister(AngebotPersister.class));
		fenster.load(this.klient, -1);
		fenster.addWindowListener(this);
		fenster.setVisible(true);
	}

	private void jButtonBearbeitenActionPerformed(ActionEvent evt) {
		// Datensatz bearbeiten
		editEinheit();
	}

	private void editEinheit() {
		int einheit_id = this.jTable1.getSelectedRow();
		if (einheit_id == -1) {
			JOptionPane.showMessageDialog(this, "Bitte wählen Sie einen Datensatz aus der Tabelle zum Edieren!",
					"Kein Datensatz ausgewählt!", JOptionPane.INFORMATION_MESSAGE);
		} else {
			einheit_id = this.arbeitsstunden.get(einheit_id).getID();
			ArbeitRechnungFactory factory = ArbeitRechnungFactory.getInstance();

			EinheitEinzelFrame fenster = new EinheitEinzelFrame(factory.getPersister(KlientPersister.class),
					factory.getPersister(AngebotPersister.class));
			fenster.load(this.klient, einheit_id);
			fenster.addWindowListener(this);
			fenster.setVisible(true);
		}
	}

	private void jButtonDeleteActionPerformed(ActionEvent evt) {
		// Datensatz löschen

		int[] einheit_id = this.jTable1.getSelectedRows();

		int selection = INVALID_SELECTION;

		for (int i = 0; i < einheit_id.length; i++) {
			einheit_id[i] = this.arbeitsstunden.get(einheit_id[i]).getID();
		}
		String frageText = "";

		if (this.jTable1.getSelectedRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Bitte wählen Sie einen Datensatz aus der Tabelle zum Löschen!",
					"Kein Datensatz ausgewählt!", JOptionPane.INFORMATION_MESSAGE);
		} else {

			List<Date> daten = datenPersister.getDatumForEinheiten(einheit_id);

			try {
				if (einheit_id.length == 1) {
					frageText = "Soll der Datensatz Nr. \"" + einheit_id[0];
				} else {
					frageText = "Sollen die Datensätze Nr. \"" + einheit_id[0];
				}
				for (int i = 1; i < einheit_id.length; i++) {
					frageText += ", " + einheit_id[i];
				}
				DateFormat dateInstance = DateFormat.getDateInstance();
				for (int i = 0; i < daten.size(); i++) {
					if (i == 0) {
						frageText += "\" vom " + dateInstance.format(daten.get(i));
					} else {
						frageText += ", " + dateInstance.format(daten.get(i));
					}
				}

				frageText += " wirklich gelöscht werden?";

				if (daten.size() != einheit_id.length) {
					frageText += "\nAchtung! Anzahl der Id's und der Daten stimmt nicht überein!";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			selection = JOptionPane.showConfirmDialog(this, frageText);
			if (selection == JOptionPane.YES_OPTION) {
				datenPersister.deleteEinheiten(einheit_id);
				this.update(klient);
				this.firePropertyChange("ArbeitsstundenTabelle.Tabellendaten", true, false);
			}
		}
	}

	// private String rechnungStart(){
	// String inputfile =
	// "/home/markus/programming/NetBeansProjects/Arbeitsrechnungen/Tex-Vorlagen/Rechnung_Allgemein.tex";
	// String auftraggeber="";
	//
	// //Adresse festlegen und in 'adresse' speichern
	// String adresse = "";
	// Verbindung verbindung = new Verbindung(optionen.getProperty("sqlserver"),
	// optionen.getProperty("datenbank") ,
	// optionen.getProperty("user"), optionen.getProperty("password"));
	// String sqltext = "SELECT * FROM klienten WHERE klienten_id=" +
	// this.klient + ";";
	// System.out.println("ArbeitsstundenTabelle::rechnungStart: " + sqltext);
	// ResultSet angebote = verbindung.query(sqltext);
	// try{
	// angebote.first();
	// System.out.println(angebote.getString("Auftraggeber"));
	// System.out.println(angebote.getString("AAdresse1"));
	// System.out.println(angebote.getString("APLZ"));
	// System.out.println(angebote.getString("AOrt"));
	// auftraggeber = angebote.getString("Auftraggeber");
	// adresse = angebote.getString("Auftraggeber");
	// adresse = adresse + TEXUMBRUCH + angebote.getString("AAdresse1");
	// if(angebote.getString("AAdresse2").length()>2) {
	// adresse = adresse + TEXUMBRUCH + angebote.getString("AAdresse2");
	// }
	// adresse = adresse + TEXUMBRUCH + angebote.getString("APLZ") + " " +
	// angebote.getString("AOrt");
	//
	// try{
	// if(!angebote.getString("tex_datei").isEmpty()) {
	// inputfile = angebote.getString("tex_datei");
	// }
	// }catch (Exception e){
	// System.err.println("tex-datei ist NULL");
	// }
	// }catch (Exception e){
	// System.err.println("Angebote-Exeption");
	// //e.printStackTrace();
	// }
	//
	// System.err.println("Inputfile: " + inputfile);
	//
	// // Tex-Datei öffnen und in String 'latex' speichern
	// java.io.FileReader latexdatei;
	// String latex = "";
	// try{
	// latexdatei = new java.io.FileReader(inputfile);
	// int c;
	// try{
	// while ((c = latexdatei.read()) != -1) {
	// // System.out.print((char)c);
	// latex = latex.concat(java.lang.String.valueOf((char)c));
	// }
	// latexdatei.close();
	// }catch (java.io.IOException e){
	// System.err.println("Tex-Datei auslesen und in Variable \"latex\"
	// speichern.");
	// e.printStackTrace();
	// }
	// }catch(java.io.FileNotFoundException e){
	// e.printStackTrace();
	// System.err.println("Tex-Datei öffnen und in String 'latex' speichern");
	// }
	// java.text.DateFormat df;
	// df = java.text.DateFormat.getDateInstance(java.text.DateFormat.DATE_FIELD,
	// Locale.US);
	// String datum = df.format(new java.util.GregorianCalendar().getTime());
	// datum = datum.replace("/", "-");
	// datum = datum.replace(" ", "_");
	//
	// if (this.klient==1){
	// latex = this.rechnungFeuerwehr(latex, adresse);
	// outputfile_pdf = outputfile_pdf + "Feuerwehr_" + datum + ".pdf";
	// }else{
	// latex = this.rechnungAllgemein(latex, adresse);
	// outputfile_pdf = outputfile_pdf + auftraggeber.replace(" ",
	// "_").replace("/", "-") + "_" + datum + ".pdf";
	// }
	// return latex;
	// }

	// private String rechnungFeuerwehr(String latex, String adresse){
	//
	// // Tabellenkopf
	// String tabelle =
	// "\\\\begin{tabular}{|l|l|l|l|l|}\n" +
	// TEXLINE +
	// " Datum/ Tag & Wache/ Abteilung & Teilnehmer & Ausgefallen & Betrag ";
	// // Daten in Tabelle schreiben und Summe sammeln
	// double rechnung_summe = 0;
	//
	// String zeilenumbruch = TEXUMBRUCH + TEXLINE + "\n ";
	// //"\\\\\\\\\\\\hline\n ";
	// for (int i = 0; i<Arbeitsstunden.size(); i++){
	// if (i==0){
	// zeilenumbruch = "\\\\\\\\\\\\hline\\\\hline\n ";
	// }else {
	// zeilenumbruch = "\\\\\\\\\\\\hline\n ";
	// }
	// tabelle = tabelle + zeilenumbruch +
	// Arbeitsstunden.elementAt(i).getDatum() + " & " +
	// Arbeitsstunden.elementAt(i).getZusatz1() + " & ";
	// try{
	// Integer.parseInt(Arbeitsstunden.elementAt(i).getZusatz2());
	// tabelle = tabelle + Arbeitsstunden.elementAt(i).getZusatz2() + " & & ";
	// }catch (NumberFormatException e){
	// tabelle = tabelle + " & Ja & ";
	// }
	// // tabelle = tabelle + Arbeitsstunden.elementAt(i).getZusatz2() +
	// " & & ";
	// tabelle = tabelle + (Arbeitsstunden.elementAt(i).getPreis() )
	// +" \\\\officialeuro";
	// rechnung_summe = rechnung_summe + Arbeitsstunden.elementAt(i).getPreis();
	// //+ Arbeitsstunden.elementAt(i).getPreisAenderung()
	// }
	//
	// // Zeile mit Summe hinzufügen
	// tabelle = tabelle + TEXUMBRUCH + TEXLINE + TEXLINE +
	// "\n\\\\textbf{Summe} & & & & " + rechnung_summe + " " + TEXEURO;
	// // Tabellenfuß
	// tabelle = tabelle + TEXUMBRUCH + TEXLINE + "\n\\\\end{tabular}\n";
	// java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	// cal.setTimeInMillis(System.currentTimeMillis());
	// String betreff = " " + cal.get(Calendar.DAY_OF_WEEK) + "." +
	// cal.get(Calendar.YEAR);
	// // Text in latex ersetzen
	// // JOptionPane.showMessageDialog(this, latex);
	// adresse = "-";
	// System.out.println("Adresse: "+ adresse);
	// latex = latex.replaceAll("%%Betreff%%\n", betreff);
	// latex = latex.replaceAll("%%Adresse%%\n", adresse);
	// latex = latex.replaceAll("%%Anrede%%\n", "-");
	// latex = latex.replaceAll("%%Text%%\n",
	// "Rechnungstext:" + TEXUMBRUCH +
	// // "\\\\\\\\\n------tab------------\\\\\\\\\n" +
	// tabelle +
	// // "\\\\\\\\\n------tab------------\\\\\\\\\n" +
	// TEXUMBRUCH + "[.5cm]Ich erlaube mir für " + Arbeitsstunden.size() +
	// " Stunden insgesamt " + rechnung_summe + " " + TEXEURO + " zu berechnen. "
	// +
	// "Bitte überweisen Sie die Summe auf das unten angegebene Konto."
	// );
	// latex = latex.replaceAll("%%Closing%%\n", "Mit freundlichen Grüßen");
	// latex = latex.replaceAll("\n}", "}");
	// return latex;
	// }
	//
	// private String rechnungAllgemein(String latex, String adresse){
	// double summe_rechnung = 0;
	// String tabelle;
	//
	// // Tabellenkopf
	// if(Arbeitsstunden.elementAt(0).getDauer()==0){
	// tabelle =
	// "\\\\begin{tabular}{|l|l|}\n" +
	// TEXLINE + " Datum & Betrag ";
	// }else{
	// tabelle =
	// "\\\\begin{tabular}{|l|l|l|l|}\n" +
	// TEXLINE + " Datum & Tätigkeit & Dauer & Betrag ";
	// }
	// // Daten in Tabelle schreiben
	// for (int i = 0; i<Arbeitsstunden.size(); i++){
	// String zeilenumbruch = "\\\\\\\\\\\\hline\n ";
	// if (i==0){
	// zeilenumbruch = "\\\\\\\\\\\\hline\\\\hline\n ";
	// }
	// if(Arbeitsstunden.elementAt(0).getDauer()==0){
	// tabelle = tabelle + zeilenumbruch +
	// DateFormat.getDateInstance(java.text.DateFormat.MEDIUM,
	// Locale.GERMAN).format(
	// Arbeitsstunden.elementAt(i).getDatum()) + " & ";
	// tabelle = tabelle + (Arbeitsstunden.elementAt(i).getPreis())
	// +" \\\\officialeuro";
	// }else{
	// tabelle = tabelle + zeilenumbruch +
	// DateFormat.getDateInstance(java.text.DateFormat.MEDIUM,
	// Locale.GERMAN).format(
	// Arbeitsstunden.elementAt(i).getDatum()) + " & ";
	// tabelle = tabelle + Arbeitsstunden.elementAt(i).getInhalt() + " & ";
	// tabelle = tabelle + Arbeitsstunden.elementAt(i).getDauer()/60 +
	// " Std. & ";
	// tabelle = tabelle + (Arbeitsstunden.elementAt(i).getPreis())
	// +" \\\\officialeuro";
	// }
	// summe_rechnung = summe_rechnung + Arbeitsstunden.elementAt(i).getPreis();
	// // + Arbeitsstunden.elementAt(i).getPreisAenderung()
	// }
	//
	// // Tabellenfuß
	// tabelle = tabelle + "\\\\\\\\\\\\hline\n\\\\end{tabular}\n";
	//
	// java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	// cal.setTimeInMillis(System.currentTimeMillis());
	// String betreff = "Rechnung " + cal.get(Calendar.DAY_OF_WEEK) + "." +
	// cal.get(Calendar.YEAR);
	//
	// // Text in latex ersetzen
	// System.out.println("Adresse: "+ adresse);
	// latex = latex.replaceAll("%%Betreff%%\n", betreff);
	// latex = latex.replaceAll("%%Adresse%%\n", adresse);
	// latex = latex.replaceAll("%%Anrede%%\n", "Sehr geehrte Damen und Herren");
	// latex = latex.replaceAll("%%Text%%\n",
	// "für die folgenden Leistungen erlaube ich mir " + summe_rechnung + " " +
	// TEXEURO +
	// "\\\\ zu berechnen.\\\\\\\\" + TEXUMBRUCH +
	// // "\\\\\\\\\n------tab------------\\\\\\\\\n" +
	// tabelle +
	// // "\\\\\\\\\n------tab------------\\\\\\\\\n" +
	// "\nBitte überweisen Sie dem genannten Betrag auf dieses Konto:" +
	// TEXUMBRUCH + TEXUMBRUCH +
	// "\n\\\\begin{tabular}[c]{ll}" +
	// "\nBank: & SK Hannover" + TEXUMBRUCH +
	// "\nBLZ: & 25050180" + TEXUMBRUCH +
	// "\nKonto: & 37793160" +
	// "\n\\\\end{tabular}" + "\n"
	// );
	// latex = latex.replaceAll("%%Closing%%\n", "Mit freundlichen Grüßen");
	// latex = latex.replaceAll("\n}", "}");
	// return latex;
	// }

	private void jButtonRechnungActionPerformed(ActionEvent evt) {
		boolean isAnySubmitted = false;

		Vector<Integer> einheitenIDs = new Vector<Integer>();
		for (int i = 0; i < this.arbeitsstunden.size(); i++) {
			if ((!this.arbeitsstunden.get(i).isVerschickt()) && (!this.arbeitsstunden.get(i).isBezahlt()))
				einheitenIDs.add(this.arbeitsstunden.get(i).getID());
			else {
				String nachricht = "Die Einheit vom " + this.arbeitsstunden.get(i).getDatum()
						+ " ist bereits verschickt oder bezahlt!\n" + "Erstellung der Rechnung abgebrochen.";
				isAnySubmitted = true;
				einheitenIDs.removeAllElements();
				i = this.arbeitsstunden.size();
				JOptionPane.showMessageDialog(parent, nachricht, "Einheit bereits abgerechnet",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		if (!isAnySubmitted) {
			RechnungDialog dialog = new RechnungDialog(this.parent, einheitenIDs);
			dialog.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().matches(RechnungDialog.ERSTELLT)) {
						System.out.println("Rechnung fertig: " + evt);
					}
				}
			});

			dialog.setVisible(true);
		}

		this.updateTable();
	}

	private void jTable1MouseClicked(MouseEvent evt) {

		if (this.jTable1.getSelectedRowCount() == 0)
			jTable1SetSelection(evt);
		if (this.jPopupMenu1.isVisible())
			this.jPopupMenu1.setVisible(false);
		if (evt.getClickCount() > 1 && !evt.isPopupTrigger())
			editEinheit();
		if (evt.isPopupTrigger()) {
			this.jPopupMenu1.show(jTable1, evt.getX(), evt.getY());

		}
	}

	/**
	 * In der Tabelle wird die Zeile auf die geklickt wurde ausgwählt.
	 * 
	 * @param evt
	 */
	private void jTable1SetSelection(MouseEvent evt) {
		java.awt.Point p = evt.getPoint();
		int zeile = this.jTable1.rowAtPoint(p);
		this.jTable1.getSelectionModel().setSelectionInterval(zeile, zeile);
	}

	private void jRadioButtonAlleItemStateChanged(ItemEvent evt) {
		setFilter();
		update(this.klient);
	}

	private void jRadioButtonNichtEingereichteItemStateChanged(ItemEvent evt) {
		setFilter();
		update(this.klient);
	}

	private void jRadioButtonNichtBezahlteItemStateChanged(ItemEvent evt) {
		setFilter();
		update(this.klient);
	}

	private void jRadioButtonAbgeschlosseneItemStateChanged(ItemEvent evt) {
		setFilter();
		update(this.klient);
	}

	private void jRadioButtonOffeneItemStateChanged(ItemEvent evt) {
		setFilter();
		update(this.klient);
	}

	private void setFilter() {
		if (buttonGroupFilter.getSelection() != null) {
			filter = buttonGroupFilter.getSelection().getActionCommand();

			if (this.jDateChooserVonDatum.getDate() != null) {
				MySqlDate tmpdate = new MySqlDate(this.jDateChooserVonDatum.getDate());
				filter = filter + " AND Datum>=\"" + tmpdate.getSqlDate() + "\"";
			}

			if (this.jDateChooserBisDatum.getDate() != null) {
				MySqlDate tmpdate = new MySqlDate(this.jDateChooserBisDatum.getDate());
				filter = filter + " AND Datum<=\"" + tmpdate.getSqlDate() + "\"";
			}
			logger.debug("filter: " + filter);
		}
	}

	public void setFilter(int Filter) {
		switch (Filter) {
		case EINGEREICHTE:
			jRadioButtonNichtBezahlte.setSelected(true);
			break;
		case NICHTEINGEREICHTE:
			jRadioButtonNichtEingereichte.setSelected(true);
			break;
		case OFFENE:
			jRadioButtonOffene.setSelected(true);
			break;
		}
	}

	private void jTable1MousePressed(MouseEvent evt) {
		if (this.jTable1.getSelectedRowCount() == 0)
			jTable1SetSelection(evt);
		if (evt.isPopupTrigger()) {
			this.jPopupMenu1.show(jTable1, evt.getX(), evt.getY());
		}
	}

	private void jMenuItembearbeitenActionPerformed(ActionEvent evt) {
		editEinheit();
		this.firePropertyChange("ArbeitsstundenTabelle.Tabellendaten", true, false);
	}

	private void jMenuItemBezahltDatumActionPerformed(ActionEvent evt) {
		// Setzt das Bezahlt_Datum
		setDatumOnField("Bezahlt_Datum", "Bezahlt");
		update(klient);
		this.firePropertyChange("ArbeitsstundenTabelle.Tabellendaten", true, false);
	}

	private boolean setDatumOnField(String feld, String feld2) {
		// Sowohl das Datum-Feld (feld) als auch das Boolean-Feld (feld2) müssen
		// übergeben und gesetzt werden

		// Datum auswählen
		java.util.Date datum = null;
		Kalenderauswahl dialog = new Kalenderauswahl(this.parent);
		dialog.setVisible(true);

		if (dialog.isBestaetigt()) {

			datum = dialog.getDatum(); // Wenn Dialog nicht abgebrochen wurde,
										// setzte Datum (auch null)
			dialog.dispose();

			// ID der Datensätze herausfinden
			int[] einheitId = this.jTable1.getSelectedRows();
			for (int i = 0; i < einheitId.length; i++) {
				einheitId[i] = this.arbeitsstunden.get(einheitId[i]).getID();
			}

			return datenPersister.updateFields(feld, feld2, datum, einheitId);
		} else
			return false;
	}

	private void jDateChooserVonDatumPropertyChange(java.beans.PropertyChangeEvent evt) {
		// Läd Liste mit neuem Filter
		setFilter();
	}

	private void jDateChooserBisDatumPropertyChange(java.beans.PropertyChangeEvent evt) {
		// Läd Liste mit neuem Filter
		setFilter();
		update(this.klient);
	}

	private void jMenuItemRechnungDatumActionPerformed(ActionEvent evt) {
		// Setzt das Rechnung_Datum
		if (setDatumOnField("Rechnung_Datum", "Rechnung_verschickt")) {
			update(klient);
			this.firePropertyChange("ArbeitsstundenTabelle.Tabellendaten", true, false);
		}
	}

	private void jMenuItem1ActionPerformed(ActionEvent evt) {
		this.jTable1.clearSelection();
	}

	@Override
	public void windowActivated(WindowEvent evt) {
		// Nothing to do here
	}

	@Override
	public void windowClosed(WindowEvent evt) {
		// Ein Fenster, dass Daten modifiziert hat wurde geschlossen, Tabelle wird
		// neu geladen
		update(klient);
		this.firePropertyChange("ArbeitsstundenTabelle.Tabellendaten", true, false);
	}

	@Override
	public void windowClosing(WindowEvent evt) {
		// Nothing to do here
	}

	@Override
	public void windowDeactivated(WindowEvent evt) {
		// Nothing to do here
	}

	@Override
	public void windowDeiconified(WindowEvent evt) {
		// Nothing to do here
	}

	@Override
	public void windowIconified(WindowEvent evt) {
		// Nothing to do here
	}

	@Override
	public void windowOpened(WindowEvent evt) {
		// Nothing to do here
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private ButtonGroup buttonGroupFilter;
	private JButton jButtonBearbeiten;
	private JButton jButtonDelete;
	private JButton jButtonNeuerDatensatz;
	private JButton jButtonRechnung;
	private JDateChooser jDateChooserBisDatum;
	private JDateChooser jDateChooserVonDatum;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JLabel jLabel5;
	private JLabel jLabel6;
	private JLabel jLabel7;
	private JMenuItem jMenuItem1;
	private JMenuItem jMenuItemBezahltDatum;
	private JMenuItem jMenuItemEditAll;
	private JMenuItem jMenuItemRechnungDatum;
	private JMenuItem jMenuItembearbeiten;
	private JPopupMenu jPopupMenu1;
	private JRadioButton jRadioButtonAbgeschlossene;
	private JRadioButton jRadioButtonAlle;
	private JRadioButton jRadioButtonNichtBezahlte;
	private JRadioButton jRadioButtonNichtEingereichte;
	private JRadioButton jRadioButtonOffene;
	private JScrollPane jScrollPane1;
	private JSeparator jSeparator1;
	private JTable jTable1;
	private JTextField jTextFieldAnzahl;
	private JTextField jTextFieldStundenzahl;
	private JTextField jTextFieldSumme;
	// End of variables declaration//GEN-END:variables
}
