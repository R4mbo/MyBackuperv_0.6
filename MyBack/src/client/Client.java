package client;

import javax.swing.JFrame;
import java.io.*;
import java.util.*;
import javax.swing.*;

import conf.Conf;

/**
 * Obs�uga polaczenia, interfejs graficzny, przesylanie pliku, kontrola
 * @author Piotr Milewski & Krzysztof Rembiszewski
 */
public class Client {

    Conf konf;
    MainFrame okno;
    Connector conn;
    FileContainer lista;
    private boolean isConnected;
    private Spy spy;

    /**
     * Konstruktor
     *
     */
    public Client() {
        lista = new FileContainer();
        okno = new MainFrame(this);
        konf = Conf.getInstance();
        okno.setParams(konf);
        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        okno.setVisible(true);
        okno.setTitle(Conf.version + " Disconnected");
        isConnected = false;
        spy = new Spy(this);

    }

    /**
     * Wy�wietlanie komunikat�w na standardowym wyj�ciu
     * @param name Informacje o akcji.
     */
    public void userAction(String name) {
        System.out.println("Uzytkownik wykonal: " + name);
    }

    public void connect(String name, char[] pass, String host, int port) {
        try {
            conn = new Connector(name, pass, host, port);

            if (conn.getConnection()) {
                lista.checkRemote();
                isConnected = true;
                okno.setTitle(Conf.version + "Connected with " + host + ":" + port);
                StringTokenizer st = new StringTokenizer(konf.getGodz(), ":");

                int godz = Integer.parseInt(st.nextToken());
                int godz_min = Integer.parseInt(st.nextToken());

                spy.makeSpy(konf.getMode(), Integer.parseInt(konf.getMin()), godz, godz_min);
            } else {
                okno.errorDialog(1, "Problem z polaczeniem\n Serwer nie odpowiada lub b��dne has�o");
            }
            okno.repaintPanel();
        } catch (Connector.ConnectionException ex) {
            ex.printStackTrace();
            okno.errorDialog(2, "Z�y login lub has�o");
        }


    }

    public void disconnect() {
        try {
            spy.stopSpy();
            conn.disconnect();
            isConnected = false;
            okno.setTitle(Conf.version + " Disconnected");
            okno.repaintPanel();
        } catch (NullPointerException ex) {
            okno.infoDialog("Nie jeste� po��czony");
        }

    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Backup
     *
     */
    public int backup() {
        try {
            okno.runBar();
        } catch (NullPointerException ex) {
        }
        int ret = 0;
        try {
            int czyPotrzebnyBackup = lista.prepareBackup(); //Czy pliki wymagaj� Backupu
            if (czyPotrzebnyBackup > 0) {
                int i = conn.doBackup(lista);
                if (i == 1) {
                    this.getListFromServer();
                    okno.infoDialog("Done ! Lista plik�w pobrana");
                    lista.createStates();// Tworzenie log�w plik�w lokalnych
                    okno.refreshList(); // Lista 
                    ret = 1; //jeszcze raz przesylamy plik z lista plikow
                }
            } else {
                System.out.println("Backup niepotrzebny");
            }

        } catch (NullPointerException ex) {
            okno.errorDialog("Connection Error");

        }
        okno.stopBar();
        return ret;

    }

    public void przywroc() {
        okno.runBar();
        try {
            int i = conn.przywroc();
            if (i == 1) {
                okno.infoDialog("Transmisja zako�czona poprawnie!");
            }
            this.getListFromServer();
            lista.createStates();
            okno.refreshList();
        } catch (NullPointerException ex) {
            okno.errorDialog("Connection Error");
        }
        okno.stopBar();
    }

    public void getFile(File plik) {
        conn.getFile(plik);
        this.getListFromServer();
        lista.createStates();

    }

    public void changeVisible() {
        okno.changeVisible();
    }

    /**
     * Metody zarządzania aktualną listą plików do backupowania
     */
    public void listAdd(File file) {
        int i = lista.add(file);
        if (i == 0) {
            okno.infoDialog("Plik " + file.getName() + "\n znajduje sie na liscie");
        }
    }
//To mo�na poprawic
    @SuppressWarnings("unchecked")
	public void listAdd(File file, @SuppressWarnings("rawtypes") DefaultListModel model) {
        int i = lista.add(file, model);
        if (i == 0) {
            okno.infoDialog("Plik " + file.getName() + "\n znajduje sie na liscie");
        }
    }

    public String listGet(int no) {
        return lista.get(no);
    }

    @SuppressWarnings("rawtypes")
	public ArrayList listGetContainer() {
        return lista.getContainer();
    }

    public int listGetSize() {
        return lista.getSize();
    }

    public void listSave() {
        lista.saveList();
    }

    public void listDelFile(File plik) {
        lista.delFile(plik);
        try {
            conn.delFileFromServer(plik);
            conn.receiveList();
        } catch (NullPointerException ex) {
        }
    }

    public int listGetEltState(int index) {

        return lista.getFileStatus(index);
    }

    public void getListFromServer() {
        conn.receiveList();
    }

    public void refresh() {
        this.getListFromServer();
        lista.reloadRemoteList();
        lista.createStates();
    }

    public void setSpy(int mode, int min, int h, int h_min) {
        spy.changeParam(mode, min, h, h_min);
    }
}
