
package client;


import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import java.io.File;

/**
 *
 * @author Piotr Milewski & Krzysztof Rembiszewski
 */
public class Main {
    public static void main(String[] args) {
    	int portNo = 8000;
    	if (args.length > 0 && args[0].equals("serve")) {
            if (args.length == 1) {
                System.out.println("W³¹cz serwer");
                @SuppressWarnings("unused")
				server.Server srv = new server.Server(portNo);

            } else if (args.length == 2) {
                System.out.println("Wybierz inny port serwera");
            } else if (args.length == 4 && args[1].endsWith("adduser")) {
                System.out.println("Dodano uzytkownika"+ "adduser");
                File path = new File(System.getProperty("user.dir") + "\\back\\" + args[2] + "_" + args[3] + "\\");
                path.mkdirs();
                path = new File(path.getAbsolutePath() + "\\list");
                try {
                    path.createNewFile();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }


        } else if (args.length > 0) {
            System.out.println("Nierozpoznane polecenie\n" +
                    "Zajrzyj do README.txt");
        } else {
            System.out.println("Ramka otwarta");
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (UnsupportedLookAndFeelException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }

            final Client klient = new Client();
            if (!SystemTray.isSupported()) {
                System.out.println("SystemTray is not supported");
                return;
            }
            final PopupMenu popup = new PopupMenu();


            final MenuItem przywroc = new MenuItem("Przywroc");
            MenuItem exitItem = new MenuItem("Exit");

            przywroc.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent a) {
                    klient.changeVisible();
                }
            });
            exitItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent a) {
                    System.exit(0);
                }
            });
            popup.add(przywroc);
            popup.addSeparator();
            popup.add(exitItem);

        }
    }

    protected static Image createImage(String path, String description) {
        URL imageURL = TrayIconDemo.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}
