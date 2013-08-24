/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DialogBox;

/**
 *
 * @author markus
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class ModalDialog
        extends javax.swing.JDialog
        implements ActionListener
{
    String result;

    public static String OKDialog(Frame owner, String msg)
    {
        ModalDialog dlg;
        dlg = new ModalDialog(owner, "Nachricht", msg, "OK");
        dlg.setVisible(true);
        return dlg.getResult();
    }
    public static String YesNoDialog(Frame owner, String msg)
    {
        ModalDialog dlg;
        dlg = new ModalDialog(owner, "Frage", msg, "Ja,Nein");
        dlg.setVisible(true);
        return dlg.getResult();
    }
    public static String YesNoCancelDialog(Frame owner, String msg)
    {
        ModalDialog dlg;
        dlg = new ModalDialog(owner, "Frage", msg, "Ja,Nein,Abbruch");
        dlg.setVisible(true);
        return dlg.getResult();
    }
    public ModalDialog(
            Frame owner,
            String title,
            String msg,
            String buttons
            )
    {
        super(owner, title, true);
        //Fenster
        addWindowListener(new WindowClosingAdapter());
        setBackground(Color.lightGray);
        setLayout(new BorderLayout());
        setResizable(false);
        Point parloc = owner.getLocation();
        setLocation(parloc.x+30, parloc.y + 30);
        
        //Message
        if (msg.contains("\n")){
            String[] zeilen;
            zeilen = msg.split("\n");
            Panel panel2 = new Panel();
            panel2.setLayout(new GridLayout(zeilen.length,1));

//            System.out.println("Das Zeichen wurde gefunden!");
            for (int i = 0; i<zeilen.length;i++){
                System.out.println(i + ". Zeile");
                panel2.add(new Label(zeilen[i]));
            }
            add(panel2, BorderLayout.CENTER);
        }else {
  //          System.out.println("Kein zeichen gefunden!");
            add(new Label(msg), BorderLayout.CENTER);
        }
        //Buttons
        Panel panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        StringTokenizer strtok = new StringTokenizer(buttons,",");
        while (strtok.hasMoreTokens())
        {
            Button button = new Button(strtok.nextToken());
            button.addActionListener(this);
            panel.add(button);
        }
        add(panel, BorderLayout.SOUTH);
        pack();
    }

    public void actionPerformed(ActionEvent event)
    {
        result = event.getActionCommand();
        setVisible(false);
        dispose();
    }

    public String getResult()
    {
        return result;
    }
}