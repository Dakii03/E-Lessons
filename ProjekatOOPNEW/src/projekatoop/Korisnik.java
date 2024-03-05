package projekatoop;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Korisnik extends Osoba {

    public Korisnik() {
        this.korisnickoIme = "";
        this.lozinka = "";
    }

    public Korisnik(String korisnickoIme, String lozinka) {
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
    }

    public Korisnik(String ime, String prezime, String korisnickoIme, String lozinka, int godine) {
        super(ime, prezime, korisnickoIme, lozinka, godine);
    }

    @Override
    public boolean kreirajProfil() throws FileNotFoundException {
        FileWriter fileWriter;
        PrintWriter pw = null;
        boolean kreiranProfil = true;

        try {
            if (!profilPostoji()) {
                try {
                    String url = "http://adonis03.pythonanywhere.com/dodajkorisnika";

                    String podaci = "ime=" + ime + "&prezime=" + prezime + "&godine=" + godine + "&korisnicko_ime=" + korisnickoIme + "&sifra=" + lozinka;
                    byte[] podaciZaSlanje = podaci.getBytes(StandardCharsets.UTF_8);

                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(podaciZaSlanje);
                    }

                    int odgovorServera = conn.getResponseCode();

                    if (odgovorServera == 200) {
                        System.out.println("GOOD");
                        System.out.println("Response Code: " + odgovorServera);
                    } else {
                        System.out.println("Response Code: " + odgovorServera);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return false;
            }
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Korisnik.class.getName()).log(Level.SEVERE, null, ex);
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
    }

    public boolean izaberiTrenera(Trener tr) throws IOException, FileNotFoundException, ParseException {

        boolean kreiranProfil = false;
        if (!trenerIzabran()) {
            try {
                String url = "http://adonis03.pythonanywhere.com/dodajtrenerakorisnika";

                // Construct the podaci to send
                String podaci = "korisnicko_ime=" + korisnickoIme + "&korisnicko_ime_tr=" + tr.getKorisnickoIme();
                byte[] podaciZaSlanje = podaci.getBytes(StandardCharsets.UTF_8);

                // Set up the connection
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send the podaci
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(podaciZaSlanje);
                }

                // Get the response code
                int odgovorServera = conn.getResponseCode();

                // Read the response
                if (odgovorServera == 200) {
                    // Response is "1"
                    kreiranProfil = true;
                    System.out.println("GOOD");
                    System.out.println("Response Code: " + odgovorServera);

                } else {
                    System.out.println("Response Code: " + odgovorServera);
                    kreiranProfil = false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            posaljiPorukuTreneru("");
        }

        return kreiranProfil;
    }

    public boolean trenerIzabran() throws FileNotFoundException, IOException, ParseException {
        try {
            URL url = new URL("http://adonis03.pythonanywhere.com/proverikorisnika"); // Replace with the actual server address
            HttpURLConnection konekcija = (HttpURLConnection) url.openConnection();

            konekcija.setRequestMethod("POST");
            konekcija.setRequestProperty("Content-Type", "application/json");
            konekcija.setDoOutput(true);

            String jsonInputString = "{\"korisnicko_ime\":\"" + korisnickoIme + "\"}";

            try (OutputStream os = konekcija.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int odgovorServera = konekcija.getResponseCode();
            System.out.println("Response Code from trener: " + odgovorServera);

            if (odgovorServera == 250) {
                System.out.println("ime korinsika");
                return true;
            } else {
                System.out.println("nema korisnika!");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void posaljiPorukuTreneru(String sadrzaj) {
        try {
            Poruka p = new Poruka(korisnickoIme, dohvatiTrenera(), sadrzaj);
            p.upisiPoruku();
        } catch (IOException | ParseException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private String findTrener(JSONArray jsonTreneri) {
        for (Object o : jsonTreneri) {
            JSONObject obj = (JSONObject) o;
            String kIme = obj.get("korisnik").toString();

            if (kIme.equals(korisnickoIme)) {
                return obj.get("trener").toString();
            }
        }
        return "";
    }

    public String dohvatiTrenera() throws FileNotFoundException, IOException, ParseException {
        String res = null;
        try {
            String jsonUlazniString = "{\"korisnicko_ime\":\"" + korisnickoIme + "\"}";
            String apiUrl = "http://adonis03.pythonanywhere.com/pronadji_mog_trenera";

            URL url = new URL(apiUrl);

            HttpURLConnection konekcija = (HttpURLConnection) url.openConnection();
            konekcija.setRequestMethod("POST");
            konekcija.setRequestProperty("Content-Type", "application/json");
            konekcija.setDoOutput(true);

            try (DataOutputStream os = new DataOutputStream(konekcija.getOutputStream())) {
                byte[] input = jsonUlazniString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(konekcija.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Server response: " + response.toString());
                res = response.toString();

            }

            konekcija.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public ArrayList<Poruka> procitajPoruke() {
        try {
            return new Poruka(korisnickoIme, dohvatiTrenera()).iscitajPoruke();
        } catch (IOException | ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public void renameFile(String currentFileName, String newFileName) {

        File trenutniFajl = new File(currentFileName);
        File noviFajl = new File(newFileName);

        boolean success = trenutniFajl.renameTo(noviFajl);

        if (success) {
            System.out.println("File renamed successfully.");
        } else {
            System.out.println("Failed to rename the file.");
        }
    }

    public void izbrisiTrenera() {
        try {
            String jsonInputString = "{\"korisnicko_ime\":\"" + korisnickoIme + "\"}";
            String apiUrl = "http://adonis03.pythonanywhere.com/izbrisi_trenera";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Server response: " + response.toString());
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String dohvatiPredmetTrenera() {
        String res = null;
        try {
            String jsonInputString = "{\"korisnicko_ime\":\"" + dohvatiTrenera() + "\"}";
            String apiUrl = "http://adonis03.pythonanywhere.com/dohvati_predmet_trenera";

            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Server response: " + response.toString());
                res = response.toString();

            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public JSONArray dohvatiTermine() {
        return Termin.dohvatiTermineZaKorisnika(korisnickoIme);
    }
}
