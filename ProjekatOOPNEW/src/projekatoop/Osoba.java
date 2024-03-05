package projekatoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class Osoba {
    protected String ime;
    protected String prezime;
    protected String korisnickoIme;
    protected String lozinka;
    protected int godine;

    public Osoba() {
        this.ime = "";
        this.prezime = "";
        this.korisnickoIme = "";
        this.lozinka = "";
        this.godine = 0;
    }

    public Osoba(String ime, String prezime, String korisnickoIme, String lozinka, int godine) {
        this.ime = ime;
        this.prezime = prezime;
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.godine = godine;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public int getGodine() {
        return godine;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public void setGodine(int godine) {
        this.godine = godine;
    }

    public void formatirajJson(String fajl) {
        File file = new File(fajl);

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            long fileLength = randomAccessFile.length();
            if (fileLength > 0) {
                randomAccessFile.setLength(fileLength - 1);
            }
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract boolean kreirajProfil() throws FileNotFoundException;

    public boolean profilPostoji() throws IOException, ParseException {
        try {
            String username = korisnickoIme;
            String jsonInputString = "{\"korisnicko_ime\":\"" + username + "\"}";

            URL url = new URL("http://adonis03.pythonanywhere.com/pronadji");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            connection.disconnect();
            if (responseCode == 201) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Osoba ulogujSe(boolean[] uspesno, String kim, String lo) throws IOException, ParseException, UnknownHostException {
        uspesno[0] = false;


        String url = "http://adonis03.pythonanywhere.com/logovanje";

        String jsonInputString = "{\"korisnicko_ime\": \"" + kim + "\", \"lozinka\": \"" + lo + "\"}";
        byte[] postData = jsonInputString.getBytes();

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(postData);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            uspesno[0] = true;
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println("Server response: " + response.toString());

                String jsonString = response.toString();

                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
                JSONObject userDetails = (JSONObject) jsonObject.get("user_details");
                JSONArray jsonArray = new JSONArray();

                jsonArray.add(userDetails);

                System.out.println("user_details JSON Object:");
                System.out.println(userDetails);

                String filePath = "profil.json";
                File file = new File("profil.json");
                if (!file.exists()) {
                    try (FileWriter fileWriter = new FileWriter(filePath)) {
                        fileWriter.write(jsonArray.toJSONString());
                        System.out.println("JSONArray written to " + filePath);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                if (userDetails.containsKey("pol")) {
                    Trener tr = new Trener();
                    tr.dohvatiPodatke(userDetails);
                    return tr;
                } else {
                    Korisnik kor = new Korisnik();
                    kor.dohvatiPodatke(userDetails);
                    return kor;
                }

            }
        } else {
            System.out.println("Request failed with response code: " + responseCode);
        }

        return null;
    }

    public abstract void dohvatiPodatke(JSONObject osoba);

    public String sifrujLozinku() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(lozinka.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract JSONArray dohvatiTermine();
}
