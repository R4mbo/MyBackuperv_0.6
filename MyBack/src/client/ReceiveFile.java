
package client;

import java.io.*;
import java.net.*;
import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import conf.Conf;

/**
 * Odbieranie pliku
 * Po nawiązaniu połączenia trzeba stworzyć jej obiekt przekazując gniazdko
 *
 * @author Piotr Milewski & Krzysztof Rembiszewski
 */
public class ReceiveFile {

    Socket socket;
    InputStream is;
    OutputStream os;
    private static final int SIZE_PAKIET = Conf.SIZE_PAKIET;

    @Deprecated
    public ReceiveFile(Socket sock) throws IOException {
        this.socket = sock;
        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();

    }

    public ReceiveFile(InputStream in, OutputStream out) {
        is = in;
        os = out;
    }

    public void reinit() throws IOException {
        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();

    }

    public File receiveFile() throws IOException {
        // Dlugosc sciezki
        int wielkosc;
        do {
            wielkosc = is.read();
        } while (wielkosc == -1);


        System.out.println(wielkosc);

        // Sciezka
        byte[] sciezka_t = new byte[wielkosc];
        is.read(sciezka_t, 0, sciezka_t.length);
        String sciezka = new String(sciezka_t);
        System.out.println(sciezka);

        // Dlugosc dlugosci
        int dlugosc_t = is.read();

        // Dlugosc pliku

        byte[] dlugosc_te = new byte[dlugosc_t];
        is.read(dlugosc_te, 0, dlugosc_te.length);
        String dlugosc = new String(dlugosc_te);
        System.out.println(dlugosc);
        int dlugosc_pliku = new Integer(dlugosc);

        // Dlugosc probki
        int dlugosc_ile_probek = is.read();
        byte[] ile_probek = new byte[dlugosc_ile_probek];
        is.read(ile_probek, 0, ile_probek.length);
        String probek = new String(ile_probek);
        System.out.println(probek);
        int ile = new Integer(probek);

        /**
         * Tu jest definiowana sciezka zapisu pliku
         */
        File myFile = new File(sciezka);
        myFile = this.makePath(myFile);
        FileOutputStream fos = new FileOutputStream(myFile);

        byte[] c = new byte[SIZE_PAKIET];


        for (int i = 0; i < ile; i++) {
            int size = is.read(c, 0, SIZE_PAKIET);
            fos.write(c, 0, size);
            fos.flush();
        }

        int dopelnienie = dlugosc_pliku - SIZE_PAKIET * ile;

        byte[] tmp = new byte[dopelnienie];
        int size = is.read(tmp, 0, dopelnienie);
        // System.out.write(tmp);
        fos.write(tmp, 0, size);



        os.write("TrEn".getBytes());

        FileChannel fileChannel = fos.getChannel();
        FileLock lock = fileChannel.lock();
        lock.release();


        fos.flush();
        fos.close();




        System.out.println(myFile.hashCode());
        //is.close();
        return myFile;
    }

    public File makePath(File plik) {
        String abspath = plik.getAbsolutePath();
        String name = plik.getName();
        String path = abspath.substring(0, abspath.length() - name.length());

        File tmp = new File(path);
        tmp.mkdirs();
        tmp = new File(abspath);
        try {
            tmp.createNewFile();
        } catch (Exception ex) {
        }
        return tmp;
    }

    public File receiveList() throws IOException {
        // Dlugosc sciezki
        int wielkosc;
        do {
            wielkosc = is.read();
        } while (wielkosc == -1);


        System.out.println(wielkosc);

        // Sciezka
        byte[] sciezka_t = new byte[wielkosc];
        is.read(sciezka_t, 0, sciezka_t.length);
        String sciezka = new String(sciezka_t);
        System.out.println(sciezka);

        // Dlugosc dlugosci
        int dlugosc_t = is.read();

        // Dlugosc pliku

        byte[] dlugosc_te = new byte[dlugosc_t];
        is.read(dlugosc_te, 0, dlugosc_te.length);
        String dlugosc = new String(dlugosc_te);
        System.out.println(dlugosc);
        int dlugosc_pliku = new Integer(dlugosc);

        // Dlugosc probki
        int dlugosc_ile_probek = is.read();
        byte[] ile_probek = new byte[dlugosc_ile_probek];
        is.read(ile_probek, 0, ile_probek.length);
        String probek = new String(ile_probek);
        System.out.println(probek);
        int ile = new Integer(probek);

        /**
         * Tu jest definiowana sciezka zapisu pliku
         */
        File myFile = new File("list_z_serwera");
        FileOutputStream fos = new FileOutputStream(myFile);

        byte[] c = new byte[SIZE_PAKIET];


        for (int i = 0; i < ile; i++) {
            int size = is.read(c, 0, SIZE_PAKIET);
            fos.write(c, 0, size);
            fos.flush();
        }

        int dopelnienie = dlugosc_pliku - SIZE_PAKIET * ile;

        byte[] tmp = new byte[dopelnienie];
        int size = is.read(tmp, 0, dopelnienie);
        // System.out.write(tmp);
        fos.write(tmp, 0, size);



        os.write("TrEn".getBytes());


        fos.flush();
        fos.close();




        System.out.println(myFile.hashCode());
        return myFile;
    }
}
