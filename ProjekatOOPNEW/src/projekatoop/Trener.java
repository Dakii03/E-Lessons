package projekatoop;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Trener extends Osoba {
    private int godineIskustva;
    private String pol;

    public Trener() {
        super();
        this.godineIskustva = 0;
        this.pol = "";
    }

    public Trener(int godineIskustva, String pol, String ime, String prezime, String korisnickoIme, String lozinka, int godine) {
        super(ime, prezime, korisnickoIme, lozinka, godine);
        this.godineIskustva = godineIskustva;
        this.pol = pol;
    }

    public Trener(String korisnickoIme, String lozinka) {
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
    }

    public int getGodineIskustva() {
        return godineIskustva;
    }

    public String getPol() {
        return pol;
    }

    public void setGodineIskustva(int godineIskustva) {
        this.godineIskustva = godineIskustva;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }


    @Override
    public boolean kreirajProfil() throws FileNotFoundException {
        FileWriter fileWriter;
        PrintWriter pw = null;
        boolean kreiranProfil = true;

        try {
            if (!profilPostoji()) {
                try {
                    String url = "http://adonis03.pythonanywhere.com/dodajtrenera";

                    String data = "ime=" + ime + "&prezime=" + prezime + "&godine=" + godine + "&godine_iskustva=" + godineIskustva + "&pol=" + pol + "&korisnicko_ime_tr=" + korisnickoIme + "&sifra=" + lozinka;
                    byte[] postData = data.getBytes(StandardCharsets.UTF_8);

                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(postData);
                    }

                    int responseCode = conn.getResponseCode();

                    if (responseCode == 200) {
                        System.out.println("GOOD");
                        System.out.println("Response Code: " + responseCode);
                    } else {
                        System.out.println("Response Code: " + responseCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return false;
            }
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Trener.class.getName()).log(Level.SEVERE, null, ex);
            kreiranProfil = false;
        } finally {
            if (pw != null) {
                pw.close();
            }
        }

        return kreiranProfil;
    }

    @Override
    public void dohvatiPodatke(JSONObject osoba) {
        ime = osoba.get("ime").toString();
        prezime = osoba.get("prezime").toString();
        godine = Integer.parseInt(osoba.get("godine").toString());
        korisnickoIme = osoba.get("korisnicko_ime").toString();
        lozinka = osoba.get("sifra").toString();
        godineIskustva = Integer.parseInt(osoba.get("godine_iskustva").toString());
        pol = osoba.get("pol").toString();
    }

    public static ArrayList<Trener> procitajTrenere() throws FileNotFoundException, IOException, ParseException {
        ArrayList<Trener> treneri = new ArrayList<>();
        String apiUrl = "http://adonis03.pythonanywhere.com/get_trainers";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        JSONArray treneriJson;
        StringBuilder response = new StringBuilder();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println("Trainers JSON Response:");
            System.out.println(response.toString());
        } else {
            System.out.println("GET request failed with response code: " + responseCode);
        }

        JSONParser parser = new JSONParser();
        treneriJson = (JSONArray) parser.parse(response.toString());

        for (Object trenerObj : treneriJson) {
            JSONObject trener = (JSONObject) trenerObj;
            Trener tr = new Trener();
            tr.dohvatiPodatke(trener);
            treneri.add(tr);
        }

        connection.disconnect();

        return treneri;
    }

    public ArrayList<Korisnik> procitajKorisnike() throws FileNotFoundException, IOException, ParseException {
        ArrayList<Korisnik> korisnici = new ArrayList<>();

        try {
            String url = "http://adonis03.pythonanywhere.com/get_korisnici";

            String jsonInputString = "{\"korisnicko_ime\": \"" + korisnickoIme + "\"}";
            byte[] postData = jsonInputString.getBytes();

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    JSONArray korisniciJson;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Server response: " + response.toString());

                    JSONParser parser = new JSONParser();
                    korisniciJson = (JSONArray) parser.parse(response.toString());

                    for (Object korisnikObj : korisniciJson) {
                        JSONObject korisnik = (JSONObject) korisnikObj;
                        Korisnik ko = new Korisnik();
                        ko.dohvatiPodatke(korisnik);
                        korisnici.add(ko);
                    }

                }
            } else {
                System.out.println("Request failed with response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return korisnici;
    }

    public void posaljiPorukuKorisniku(String sadrzaj, String korisnik) {
        Poruka p = new Poruka(korisnickoIme, korisnik, sadrzaj);
        p.upisiPoruku();
    }

    public ArrayList<Poruka> procitajPoruke(String korisnik) {
        try {
            return new Poruka(korisnickoIme, korisnik).iscitajPoruke();
        } catch (IOException | ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public JSONArray dohvatiTermine() {
        return Termin.dohvatiTermineZaTrenera2(korisnickoIme);
    }
}
