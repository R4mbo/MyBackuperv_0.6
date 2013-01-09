
package client;

import java.util.*;
import java.io.*;

/**
 * Przechowuje informacje o plikach zdalnych, zawiera œciê¿kê , datê modyfikacji
 * @author Piotr Milewski & Krzysztof Rembiszewski
 */
public class RemoteFileContainer {

    String filename = "list_z_serwera";
    private File listaPlikow;
    BufferedReader in;
    @SuppressWarnings("rawtypes")
	private ArrayList pliki;
    @SuppressWarnings("rawtypes")
	private ArrayList timestamp;

    /**
     * Konstrutktor, po po³¹czeniu
     *
     */
    @SuppressWarnings("rawtypes")
	public RemoteFileContainer() {


        timestamp = new ArrayList();


        listaPlikow = new File(filename);


        try {
            in = new BufferedReader(new FileReader(listaPlikow));
            pliki = this.loadContainer();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * ³adowanie kontenera
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList loadContainer() {
        timestamp.clear();
        String path;
        ArrayList returnable = new ArrayList();
        try {
            while ((path = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(path, ";");
                path = st.nextToken();
                File tmp = new File(path);
                returnable.add(tmp);
                timestamp.add(st.nextToken());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return returnable;
    }

    public void printAll() {
        for (int i = 0; i < pliki.size(); i++) {
            System.out.println(pliki.get(i).toString() + " " + timestamp.get(i).toString());
        }
    }

    /**
     * Funkcja zwraca date ostatniej modyfikacji pliku, ktory znajduje sie na serwerze
     * @param path Sciezka dostepu do pliku w systemie lokalnym
     * @return Data pliku w UnixTime
     */
    public Long getLastMod(String path) {
        int position = pliki.indexOf(new File(path));
        Long mod;
        if (position != -1) {
            mod = Long.parseLong(timestamp.get(position).toString());

        } else {
            mod = new Long(-1);  // pliku nie ma
        }
        return mod;
    }

    public void reload() {
        try {
            in.close();
            in = new BufferedReader(new FileReader(listaPlikow));
            pliki = this.loadContainer();
        } catch (IOException ex) {
        }

    }
}
