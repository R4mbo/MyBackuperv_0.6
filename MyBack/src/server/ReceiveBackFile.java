/*

 */
package server;

import java.io.*;
import java.net.*;
import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
/**
 * Klasa do odbierania pliku *
 * @author Piotr Milewski & Krzysztof Rembiszewski
 */
public class ReceiveBackFile {

    Socket socket;
    InputStream myInputStream;
    OutputStream myOutputStream;
    private static final int SIZE_PAKIET = conf.Conf.SIZE_PAKIET;
    String name;
    String pass;

    @Deprecated
    public ReceiveBackFile(Socket sock) throws IOException {
        this.socket = sock;
        this.myInputStream = socket.getInputStream();
        this.myOutputStream = socket.getOutputStream();

    }

    public ReceiveBackFile(InputStream in, OutputStream out, String name, String pass) {
        myInputStream = in;
        myOutputStream = out;
        this.name = name;
        this.pass = pass;
    }

    public void reinit() throws IOException {
        this.myInputStream = socket.getInputStream();
        this.myOutputStream = socket.getOutputStream();

    }

    private File makePath(File plik) {
        String nazwa = plik.getName();
        String dysk = plik.getAbsolutePath().substring(0, 1);
        String sciezka = plik.getAbsolutePath().substring(3, plik.getAbsolutePath().length() - nazwa.length());

        String path = new String(System.getProperty("user.dir") + "\\back\\" + name + "_" + pass + "\\" + dysk + "\\" + sciezka);

        File returnable = new File(path);
        returnable.mkdirs();
        path += nazwa;
        returnable = new File(path);
        try {
            returnable.createNewFile();

        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        return returnable;
    }

    @SuppressWarnings("unused")
	public File receiveFile() throws IOException {
        long start = System.currentTimeMillis();
        long current = 0;
        // Dlugosc sciezki
        int wielkosc;
        do {
            wielkosc = myInputStream.read();
        } while (wielkosc == -1);


        System.out.println(wielkosc);

        // Sciezka
        byte[] sciezka_t = new byte[wielkosc];
        myInputStream.read(sciezka_t, 0, sciezka_t.length);
        String sciezka = new String(sciezka_t);
        System.out.println(sciezka);

        // Dlugosc dlugosci
        int dlugosc_t = myInputStream.read();

        // Dlugosc pliku

        byte[] dlugosc_te = new byte[dlugosc_t];
        myInputStream.read(dlugosc_te, 0, dlugosc_te.length);
        String dlugosc = new String(dlugosc_te);
        System.out.println(dlugosc);
        int dlugosc_pliku = new Integer(dlugosc);

        // Dlugosc probki
        int dlugosc_ile_probek = myInputStream.read();
        byte[] ile_probek = new byte[dlugosc_ile_probek];
        myInputStream.read(ile_probek, 0, ile_probek.length);
        String probek = new String(ile_probek);
        System.out.println(probek);
        int ile = new Integer(probek);

        /**
         * Tu jest definiowana sciezka zapisu pliku
         */
        File temporary = new File(sciezka);

        File myFile = this.makePath(temporary);

        FileOutputStream fos = new FileOutputStream(myFile);

        byte[] c = new byte[SIZE_PAKIET];


        for (int i = 0; i < ile; i++) {
            int size = myInputStream.read(c, 0, SIZE_PAKIET);
            fos.write(c, 0, size);
            fos.flush();
            current += size;
        }

        int dopelnienie = dlugosc_pliku - SIZE_PAKIET * ile;

        byte[] tmp = new byte[dopelnienie];
        int size = myInputStream.read(tmp, 0, dopelnienie);
        fos.write(tmp, 0, size);
          myOutputStream.write("TrEn".getBytes());
        try {
            FileChannel fileChannel = fos.getChannel();
            FileLock lock = fileChannel.lock();
            lock.release();
        }
        catch(Exception ex) {ex.printStackTrace();}

        fos.flush();
        fos.close();




        System.out.println(myFile.hashCode());
        long stop = System.currentTimeMillis();
        return temporary;
    }
}
