/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DialogBox;

/**
 *
 * @author markus
 */

//import listing3111a.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;



public class Main
    extends Frame
    implements ActionListener
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main wnd = new Main();
        wnd.setVisible(true);;
    }

    public Main()
    {
        super("Drei modale Standarddialoge");
        setLayout(new FlowLayout());
        setBackground(Color.lightGray);

        Button button = new Button("OKDialog");
        button.addActionListener(this);
        add(button);

        button = new Button("YesNoDialog");
        button.addActionListener(this);
        add(button);

        button = new Button("YesNoCancelDialog");
        button.addActionListener(this);
        add(button);

        setLocation(100,100);
        setSize(400,200);

        addWindowListener(new WindowClosingAdapter(true));
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event)
    {
        String cmd = event.getActionCommand();
        if (cmd.equals("OKDialog")){
            ModalDialog.OKDialog(this, "1 Dream, dream, dream, ..." +
                    "\n2 Dream, dream, dream, ..." +
                    "\n3 Dream, dream, dream, ...");
        }else if(cmd.equals("YesNoDialog")){
            String ret = ModalDialog.YesNoDialog(
                    this,
                    "Programm beenden?"
                    );
            if (ret.equals("Ja")){
                setVisible(false);
                dispose();
                System.exit(0);
            }
        }else if (cmd.equals("YesNoCancelDialog")){
            String msg = "Verzeichnis erstellen?";
            String ret = ModalDialog.YesNoCancelDialog(this, msg);
            ModalDialog.OKDialog(this, "Rückgabe: " + ret);
        }
    }
}