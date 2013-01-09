/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package server;


import java.io.*;
import java.net.*;
import java.util.*;
import java.io.File;

/**
 *Klasa wysy�aj�ca plik na wskazane gniazdko
 * @author Piotr Milewski & Krzysztof Rembiszewski
 */
public class SendFile {

    private Socket socket;
    private OutputStream myOutputStream;
    private InputStream myInputStream;
    private static final int SIZE_PAKIET =conf.Conf.SIZE_PAKIET;

    /**
     * Konstruktor klasy do wysyłania plików
     * @param s Gniazdko do komunikacji z klientem przyjmującym plik
     */
    @Deprecated
    public SendFile(Socket s) {
        this.socket = s;

        try {
            myOutputStream = socket.getOutputStream();
            myInputStream = socket.getInputStream();
        }
        catch(IOException ex) {

        }
    }
    public SendFile(InputStream in, OutputStream out) {

            myOutputStream = out;
            myInputStream = in;


    }
    /**
     * Metoda wywoływana w celu przesłania pliku na zdefiniowane wcześniej ciastko
     * Po stronie odbiorczej należy
     * @param plik po stronie serwera
     * @param path ścieżka po stronie klienta
     * @return
     * @throws IOException
     */
    public int sendFile(File plik, String path) throws IOException {

        File myFile = plik;
        Date date = new Date();
        System.out.println(myFile.setLastModified(date.getTime()+2000));
       


        myOutputStream.write(path.getBytes().length);


        myOutputStream.write(path.getBytes());


        String dlugosc = Long.toString(myFile.length());
        byte aaa[] = dlugosc.getBytes();
        myOutputStream.write(aaa.length);
        myOutputStream.write(aaa);

        int ile = (int)myFile.length() / SIZE_PAKIET;
        String ilee = Integer.toString(ile);
        byte[] ile_b = ilee.getBytes();
        myOutputStream.write(ile_b.length);
        myOutputStream.write(ile_b);

        myOutputStream.flush();


        byte[] data = new byte[SIZE_PAKIET];
        FileInputStream fis = new FileInputStream(myFile);
        for(int i = 0; i < ile; i++) {
            fis.read(data, 0 ,SIZE_PAKIET);
            myOutputStream.write(data, 0 , SIZE_PAKIET);

            myOutputStream.flush();


        }
        int dopelnienie = (int)myFile.length() - ile *SIZE_PAKIET;
        byte[] tmp = new byte[dopelnienie];

        fis.read(tmp, 0, tmp.length);
        myOutputStream.write(tmp, 0 ,tmp.length);

        myOutputStream.flush();
        byte[] tren = new byte[4];
        System.out.println(myInputStream.read(tren, 0, 4));

        fis.close();
        String tr3n = new String(tren);
        if(tr3n.equals("TrEn")) System.out.println("Plik wysłany poprawnie");
       return 1;
    }

    public int sendFile(String path) throws IOException {
        File plik = new File(path);
        this.sendFile(plik, path);
        return 1;
    }


}
