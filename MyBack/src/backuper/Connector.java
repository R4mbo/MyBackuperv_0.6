package backuper;

import java.net.Socket;
import java.io.*;

/**
 * Obs³ugiwanie po³¹czenia z serwerem
 * TODO: wysy³anie plikow, autoryzacja
 * @author Piotr Milewski & Krzysztof Rembiszewski
 */
public class Connector {

    Socket socket;
    String remoteHost;
    int portNo;
    String name;
    char[] password;
    InputStream myInputStream;
    OutputStream myOutputStream;

    /**
     * Konstrukotr zawieraj¹cy dane do polaczenia
     * Uwaga tutaj polaczenie jeszcze nie jest tworzone
     * @param remoteHost Adres zdalnego hosta
     * @param portNo Numer portu zdalnego hosta
     */
    public Connector(String name, char[] password, String remoteHost, int portNo) {
        this.remoteHost = remoteHost;
        remoteHost = "localhost";
        this.portNo = portNo;
        portNo = 8000;
        this.name = name;
        this.password = password;

    }

    /**
     * Obs³uga po³¹czenia
     * TODO: Autoryzacja
     */
    public boolean getConnection() throws ConnectionException {

        try {
            print("£¹czenie.... (" + name + "@" + remoteHost + ":" + portNo + ")...");
            try {
                socket = new Socket(remoteHost, portNo);
                this.myInputStream = socket.getInputStream();
                this.myOutputStream = socket.getOutputStream();
                byte[] hello = new byte[3];

                myInputStream.read(hello, 0, 3);
                System.out.write(hello);

                byte[] username = name.getBytes(); //Wysy³anie nazwy uzytkownika
                myOutputStream.write(username.length);
                myOutputStream.write(username);

                String t = new String(password); // wysy³anie has³a
                byte[] pass = t.getBytes();
                myOutputStream.write(pass.length);
                myOutputStream.write(pass);

                myInputStream.read(hello, 0, 3);
                String check = new String(hello); // Odpowiedz z serwera

                if (check.equals("oki")) {
                    ReceiveFile rf = new ReceiveFile(myInputStream, myOutputStream);
                    rf.receiveList();

                    return true;
                } else if (check.equals("err")) {
                    System.out.println("Auth error!");
                    throw new ConnectionException("Auth error");

                }

            } catch (Exception ex) {
            } finally {
                //socket.close();
            }
        } catch (Exception ex) {
        }

        return false;

    }

    class ConnectionException extends Exception {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ConnectionException(String msg) {
            super(msg);
        }
    }

    /**
     * Wyœwietlanie komunikatu informacyjnego
     * Pobieranie plikow(tylko te co potrzebne)
     * @param text Treœæ komunikatu
     */
    public int doBackup(FileContainer lista) {

        byte[] hello = new byte[3];

        try {

            hello = "dba".getBytes();
            myOutputStream.write(hello);
            /**
             * Wysy³anie plikow oraz potwierdzenie
             */
            int ile = lista.getBackupSize();
            byte[] ile_b = Integer.toString(ile).getBytes();
            System.out.println(ile_b.length);
            System.out.write(ile_b);
            myOutputStream.write(ile_b.length);
            myOutputStream.write(ile_b);
            for (int i = 0; i < ile; i++) {
                SendFile filetosent = new SendFile(myInputStream, myOutputStream);
                File tmp = new File(lista.getBackup(i));
                filetosent.sendFile(tmp);

            }

            myInputStream.read(hello, 0, 3);
            String koniec = new String(hello);
            if (koniec.equals("end")) {
                return 1;
            } else {
                return 0;
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }

    }

    public int przywroc() {
        byte[] hello = new byte[3];



        try {
            hello = "prz".getBytes();
            myOutputStream.write(hello);
            System.out.println("Przywracanie...");
            int ile_byte = myInputStream.read();
            System.out.println(ile_byte);
            byte[] ile_b = new byte[ile_byte];
            myInputStream.read(ile_b, 0, ile_b.length);

            String tmp = new String(ile_b);
            int ile = new Integer(tmp);
            System.out.println("plikow do obebrania");
           
            //odbieranie plikow
            for (int i = 0; i < ile; i++) {
                ReceiveFile odbierz = new ReceiveFile(myInputStream, myOutputStream);
                odbierz.receiveFile();
            }
            hello = "end".getBytes();
            myOutputStream.write(hello, 0, 3);

            myOutputStream.write(hello);


        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }

        return 1;
    }

    /**
     * Przywrocenie danego pliku z serwera
     * @param plik
     */
    public void getFile(File plik) {
        try {
            byte[] hello = new byte[3];
            hello = "get".getBytes();
            myOutputStream.write(hello);
            myOutputStream.write(plik.getPath().getBytes().length);
            myOutputStream.write(plik.getPath().getBytes());
            ReceiveFile odbierz = new ReceiveFile(myInputStream, myOutputStream);
            odbierz.receiveFile();
            hello = "end".getBytes();
            myOutputStream.write(hello, 0, 3);
            myOutputStream.write(hello);
        } catch (IOException ex) {
        }

    }

    public void receiveList() {
        try {
            byte[] hello = new byte[3];
            hello = "sli".getBytes();
            myOutputStream.write(hello);
            System.out.println("SendList");
            ReceiveFile rf = new ReceiveFile(myInputStream, myOutputStream);
            rf.receiveList();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void print(String text) {
        System.out.println("Connector: " + text);
    }

    public void disconnect() {
        try {
            byte[] kon = new byte[3];
            kon = "kon".getBytes();
            myOutputStream.write(kon, 0, 3);
            myOutputStream.close();
            myInputStream.close();
            socket.close();
        } catch (Exception ex) {
        }
        this.print("Disconnected..");

    }

    public void delFileFromServer(File path) {
        try {
            byte[] delete = new byte[3];
            delete = "del".getBytes();
            myOutputStream.write(delete, 0, 3);
            myOutputStream.write(path.getPath().getBytes().length);
            myOutputStream.write(path.getPath().getBytes());//œcie¿ka
        } catch (IOException ex) {
        }
    }
}
