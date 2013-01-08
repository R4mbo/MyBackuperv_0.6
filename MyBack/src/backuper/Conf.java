package backuper;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.PrintWriter;
import java.io.FileWriter;

/*
 * Klasa z parametrami konfiguracyjnymi
 * @author Piotr Milewski & Krzysztof Rembiszewski
 */

public final class Conf {

    public static final int WIELKOSC_PROBKI = 1460; //Wielkosc pakietu
    public static final String REMOTE_ADDR = new String("localhost"); //Adres Serwera
    public static final int PORT = 8000; //Port serwera
    public static final String version = new String("MyBackUper v.0.6"); 
    private static Conf instance = null;
    String username, host,port,min,godz,min_on,godz_on;

    @SuppressWarnings("deprecation")
	protected Conf() {

        File file = new File("conf");

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        try {
            fis = new FileInputStream(file);

            // Here BufferedInputStream is added for fast reading.
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            username = dis.readLine();
            host = dis.readLine();
            port = dis.readLine();
            min = dis.readLine();
            godz = dis.readLine();
            min_on = dis.readLine();
            godz_on = dis.readLine();
            fis.close();
            bis.close();
            dis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getGodz() {
        return godz;
    }

    public String getHost() {
        return host;
    }

    public String getMin() {
        return min;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public static String getVersion() {
        return version;
    }

    public String[] getTable() {
        String[] table = {username, host, port, min, godz, min_on, godz_on};
        return table;
    }

    public void setTable(String[] s) {
        username = s[0];
        host = s[1];
        port = s[2];
        min = s[3];
        godz = s[4];
        min_on = s[5];
        godz_on = s[6];
    }

    public void saveFile() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("conf"));
            pw.println(username);
            pw.println(host);
            pw.println(port);
            pw.println(min);
            pw.println(godz);
            pw.println(min_on);
            pw.println(godz_on);

            pw.flush();
            pw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Conf getInstance() {
        if (instance == null) {
            instance = new Conf();
        }
        return instance;
    }

    public int getMode() {
        int mod1 = (min_on.equals("true") ? 1 : 0);
        int mod2 = (godz_on.equals("true") ? 2 : 0);
        return mod1 + mod2;
    }
}
