/**

 */
package server;

import java.io.*;
import java.net.*;

import java.io.File;
/**
 * Klasa wysy≥ajπca plik do klienta
 * @author Piotr Milewski & Krzysztof Rembiszewski
 */
public class SendBackFile {

    private Socket socket;
    private OutputStream myOutputStream;
    private InputStream myInputStream;
    private static final int SIZE_PAKIET = conf.Conf.SIZE_PAKIET;

    /**
     * Konstruktor klasy do wysy≈Çania plik√≥w
     * @param s Gniazdko do komunikacji z klientem przyjmujƒÖcym plik
     */
    @Deprecated
    public SendBackFile(Socket s) {
        this.socket = s;

        try {
            myOutputStream = socket.getOutputStream();
            myInputStream = socket.getInputStream();
        }
        catch(IOException ex) {

        }
    }
    public SendBackFile(InputStream in, OutputStream out) {

            myOutputStream = out;
            myInputStream = in;


    }
    /**
     * Przes≥anie pliku
     * 
     * @param plik
     * @return
     * @throws IOException
     */
    public int sendFile(File plik) throws IOException {

        File myFile = plik;
        /**
         * Stworzenie strumienia wyj≈õciowego, zeby klientowi wyslac niezbƒôdne dane o pliku
         */
        //dlugosc sciezki
        myOutputStream.write(myFile.getPath().getBytes().length);

        // sciezka
        myOutputStream.write(myFile.getPath().getBytes());

        // dlugosc pliku
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


        // wysylanie pliku
        byte[] data = new byte[SIZE_PAKIET];
        @SuppressWarnings("resource")
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
        
        String tr3n = new String(tren);
        if(tr3n.equals("TrEn")) System.out.println("Plik wyslany poprawnie");
       return 1;
    }

    public int sendFile(String path) throws IOException {
        File plik = new File(path);
        this.sendFile(plik);
        return 1;
    }


}
