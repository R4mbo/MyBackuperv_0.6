package client;

import java.util.*;
import java.io.*;

import javax.swing.*;

/**
 * Przechowywanie listy plikow
 * 
 * @author Piotr Milewski & Krzysztof Rembiszewski
 */
public class FileContainer {

    /**
     * Dotyczy plikow lokalnych
     */
    static String filename = "list";
    private ArrayList<File> pliki;        // lista plikow
    private ArrayList<File> doBackupu;    // generowana lista plikow (do backupu)
    private File listofFiles;
    PrintWriter out;
    BufferedReader in;
    private ArrayList<Integer> status; // status pliku
    RemoteFileContainer rfc;  // dowiazanie

    public FileContainer() {
        status = new ArrayList<Integer>();
        listofFiles = new File(filename);
        doBackupu = new ArrayList<File>();

        try {
            if (!listofFiles.isFile()) {
                listofFiles.createNewFile();
            }
            in = new BufferedReader(new FileReader(listofFiles));
            pliki = this.loadContainer();

            out = new PrintWriter(new FileWriter(listofFiles), true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.saveList();


    }

    public int add(File file) {
        if (pliki.contains(file)) {
            this.print("Plik jest juø na liúcie");
            return 0;
        } else {
            if (file.isDirectory()) {
                File[] katalogi = file.listFiles();
                for (int i = 0; i < katalogi.length; i++) {
                    add(katalogi[i]);
                }
                return 1;
            } else if (file.isFile()) {
                pliki.add(file);
                this.print("Uøytkownik dodal plik " + file.toString());
                out.println(file.toString());
                return 1;
            }
            return 0;
        }
    }

 
	public int add(File file, DefaultListModel<String> model) {
        if (pliki.contains(file)) {
            this.print("Plik znajduje siƒô ju≈º na li≈õcie");
            return 0;
        } else {
            if (file.isDirectory()) {
                File[] katalogi = file.listFiles();
                for (int i = 0; i < katalogi.length; i++) {
                    add(katalogi[i], model);
                }
                return 1;
            } else if (file.isFile()) {
                pliki.add(file);
                model.addElement(file.toString());
                this.print("U≈ºytkownik doda≈Ç plik " + file.toString());
                out.println(file.toString());
                this.createStates();
                return 1;
            }
            return 0;
        }

    }

    public String get(int no) {
        return pliki.get(no).getPath();
    }

    public ArrayList<File> getContainer() {
        return pliki;
    }

    public int getSize() {
        return pliki.size();
    }

    private void print(String tekst) {
        System.out.println("FileContainer: " + tekst);
    }

    private ArrayList<File> loadContainer() {
        String path;
        ArrayList<File> returnable = new ArrayList<File>();
        try {
            while ((path = in.readLine()) != null) {
                returnable.add(new File(path));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return returnable;
    }

    public void saveList() {
        try {
            out.close();
            in.close();

            try {
                listofFiles.delete();
                listofFiles.createNewFile();

                out = new PrintWriter(new FileWriter(listofFiles), true);
                in = new BufferedReader(new FileReader(listofFiles));
                for (int i = 0; i < pliki.size(); i++) {
                    out.println(pliki.get(i).getAbsolutePath());
                }
            } finally {
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    public void delFile(File plik) {
        System.out.println(pliki.remove(plik));
        this.createStates();
    }

    /**
     *  Porownanie plikow lokalnych z zdalnymi
     */
    public void checkRemote() {
        rfc = new RemoteFileContainer();
        this.createStates();

    }

    /**
 Tworzenie stanow plikÛw na zdalnym serwerze
     */
    public void createStates() {
        status.clear();
        for (int i = 0; i < pliki.size(); i++) {
            status.add(this.createFileStatus(i));
            System.out.println(i + ": " + status.get(i));
        }
    }

    private int createFileStatus(int index) {
        try {
            rfc.reload();
            long ostatniamodyfikacja = rfc.getLastMod(pliki.get(index).toString());
            long lastLocal = (pliki.get(index).lastModified());
            System.out.println(ostatniamodyfikacja);
            System.out.println(lastLocal);
            if (!pliki.get(index).exists()) {
                return 0;
            }
            if (ostatniamodyfikacja == -1) {
                return 3; // plik nie istnieje w backupie
            } else if (ostatniamodyfikacja >= lastLocal) {
                // plik w backupie jest mlodszy 
                return 1;
            } else if (ostatniamodyfikacja < lastLocal) {
                // plik w backupie jest starszy 
                return 2;
            }
        } catch (Exception ex) {
        }

        return -1;
    }

    public int getFileStatus(int index) {
        if (status.isEmpty() || status.size() < index + 1) {
            return 5;
        } else {
            return Integer.parseInt(status.get(index).toString());
        }

    }

    public int sendFiles(Object[] pliki) {


        return 0;
    }

    /**
     Przygotowanie tablicy plikow do wyslania
     */
    public int prepareBackup() {
        doBackupu.clear();

        for (int i = 0; i < pliki.size(); i++) {
            if (Integer.parseInt(status.get(i).toString()) >= 2) {
                System.out.println("Do backupu: " + i);
                doBackupu.add(pliki.get(i));
            }
        }
        return doBackupu.size();
    }

    /**
     *Przygotowanie plikow do wyslania przez klienta
     * @param pliki Tablica obiektow
     */
    public void prepareBackup(Object[] pliki) {
    }

    public int getBackupSize() {
        return doBackupu.size();
    }

    public String getBackup(int no) {
        return doBackupu.get(no).getPath();
    }

    public void reloadRemoteList() {
        rfc.reload();
    }
}
