/*
 * To change this template, ch

            public void setSelectedItem(Object anItem) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public Object getSelectedItem() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public int getSize() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public Object getElementAt(int index) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void addListDataListener(ListDataListener l) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void removeListDataListener(ListDataListener l) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }ose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Arbeitsstunden.java
 *
 * Created on 03.05.2009, 19:48:36
 */

package arbeitsrechnungen.gui.jframes;

/**
 *
 * @author markus
 */

import java.awt.Font;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.Vector;

import arbeitsabrechnungendataclass.Verbindung;
import arbeitsrechnungen.Punkt;

// import DialogBox.*;

public class Arbeitsstunden extends javax.swing.JFrame {

	private static final long serialVersionUID = 4796722096135537141L;
	Verbindung verbindung;
	ResultSet Auftraggeber;
	Vector<Punkt> klientenid = new Vector<Punkt>(); // a ist der Klient, b ist
													// der Listenwert
	Boolean ready = true;

	/** Creates new form Arbeitsstunden */
	public Arbeitsstunden() {
		ready = false;
		System.out.println("Init beginnt:");
		initComponents();
		this.arbeitsstundenTabelle1.setParent(this);
		// this.arbeitsstundenTabelle1.update(this.klientenid.elementAt(jComboBoxKunden.getSelectedIndex()).getA());
		// updateKlienten();
		ready = true;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jComboBoxKunden = new javax.swing.JComboBox<String>();
		jLabel1 = new javax.swing.JLabel();
		jButton2 = new javax.swing.JButton();
		arbeitsstundenTabelle1 = new arbeitsrechnungen.gui.panels.ArbeitsstundenTabelle();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setName("Form"); // NOI18N

		verbindung = new Verbindung("Arbeitrechnungen", "markus", "0773");
		String sqltext = "SELECT klienten_id, Auftraggeber from klienten;";
		System.out.println("----------------------------------------");
		System.out.println(sqltext);
		System.out.println("----------------------------------------");
		this.jComboBoxKunden.removeAllItems();
		System.out.println("Combobox geleert!");
		Auftraggeber = verbindung.query(sqltext);
		Vector<String> auftraggeberliste = new Vector<String>();
		try {
			int i = 0;
			System.out.println("Fuellen des Vectors beginnt");
			while (Auftraggeber.next()) {
				auftraggeberliste.add(Auftraggeber.getString("Auftraggeber"));
				this.klientenid.addElement(new Punkt(Auftraggeber
						.getInt("klienten_id"), i));
				i++;
				System.out.println(i + ". Auftraggeber");
			}
		} catch (Exception e) {
			System.out.println("Arbeitsstunden.java::updateKlienten:");
			e.printStackTrace();
		}
		jComboBoxKunden.setModel(new javax.swing.DefaultComboBoxModel<String>(
				auftraggeberliste));
		jComboBoxKunden.setName("jComboBoxKunden"); // NOI18N
		jComboBoxKunden.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				jComboBoxKundenItemStateChanged(evt);
			}
		});

        ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());
        
		jLabel1.setFont(Font.getFont(resourceMap.getString("jLabel1.font"))); // NOI18N
		jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
		jLabel1.setName("jLabel1"); // NOI18N

		jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
		jButton2.setName("jButton2"); // NOI18N
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		arbeitsstundenTabelle1.setName("arbeitsstundenTabelle1"); // NOI18N

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jComboBoxKunden,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										131,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										361, Short.MAX_VALUE)
								.addComponent(jButton2).addContainerGap())
				.addComponent(arbeitsstundenTabelle1,
						javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE, 609,
						Short.MAX_VALUE)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jLabel1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										214,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(383, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jLabel1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										21,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(12, 12, 12)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jButton2)
												.addComponent(
														jComboBoxKunden,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(arbeitsstundenTabelle1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										359, Short.MAX_VALUE)));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void jComboBoxKundenItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_jComboBoxKundenItemStateChanged
		if (ready)
			this.arbeitsstundenTabelle1.update(this.klientenid.elementAt(
					jComboBoxKunden.getSelectedIndex()).getA());
		if (ready)
			System.out.println("Tabelle aktualisiert - Klienten_id: "
					+ this.klientenid.elementAt(
							jComboBoxKunden.getSelectedIndex()).getA());
	}// GEN-LAST:event_jComboBoxKundenItemStateChanged

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
		//
		this.setVisible(false);
		this.dispose();
	}// GEN-LAST:event_jButton2ActionPerformed

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Arbeitsstunden().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private arbeitsrechnungen.gui.panels.ArbeitsstundenTabelle arbeitsstundenTabelle1;
	private javax.swing.JButton jButton2;
	private javax.swing.JComboBox<String> jComboBoxKunden;
	private javax.swing.JLabel jLabel1;
	// End of variables declaration//GEN-END:variables

}
