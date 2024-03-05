package projekatoop;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Termin {
    private String naziv_predmeta;
    private String korisnicko_ime_tr;
    private int odobrenje;
    private int mesec, dan;
    private String vreme_pocetka;
    private String vreme_zavrsetka;
    private String korisnicko_ime;

    public Termin() {
        this.naziv_predmeta = "";
        this.korisnicko_ime_tr = "";
        this.odobrenje = 0;
        this.mesec = 0;
        this.dan = 0;
        this.vreme_pocetka = "";
        this.vreme_zavrsetka = "";
        this.korisnicko_ime = "";
    }

    public Termin(String naziv_predmeta, String korisnicko_ime_tr, int odobrenje, int mesec, int dan, String vreme_pocetka, String vreme_zavrsetka, String korisnicko_ime) {
        this.naziv_predmeta = naziv_predmeta;
        this.korisnicko_ime_tr = korisnicko_ime_tr;
        this.odobrenje = odobrenje;
        this.mesec = mesec;
        this.dan = dan;
        this.vreme_pocetka = vreme_pocetka;
        this.vreme_zavrsetka = vreme_zavrsetka;
        this.korisnicko_ime = korisnicko_ime;
    }

    public String getNaziv_prdmeta() {
        return naziv_predmeta;
    }

    public String getKorisnicko_ime_tr() {
        return korisnicko_ime_tr;
    }

    public int getOdobrenje() {
        return odobrenje;
    }

    public int getMesec() {
        return mesec;
    }

    public int getDan() {
        return dan;
    }

    public String getVreme_pocetka() {
        return vreme_pocetka;
    }

    public String getVreme_zavrsetka() {
        return vreme_zavrsetka;
    }

    public String getKorisnicko_ime() {
        return korisnicko_ime;
    }

    public void setNaziv_prdmeta(String naziv_prdmeta) {
        this.naziv_predmeta = naziv_prdmeta;
    }

    public void setKorisnicko_ime_tr(String korisnicko_ime_tr) {
        this.korisnicko_ime_tr = korisnicko_ime_tr;
    }

    public void setOdobrenje(int odobrenje) {
        this.odobrenje = odobrenje;
    }

    public void setMesec(int mesec) {
        this.mesec = mesec;
    }

    public void setDan(int dan) {
        this.dan = dan;
    }

    public void setVreme_pocetka(String vreme_pocetka) {
        this.vreme_pocetka = vreme_pocetka;
    }

    public void setVreme_zavrsetka(String vreme_zavrsetka) {
        this.vreme_zavrsetka = vreme_zavrsetka;
    }

    public void setKorisnicko_ime(String korisnicko_ime) {
        this.korisnicko_ime = korisnicko_ime;
    }

    public boolean upisiMe() {
        try {
            String url = "http://adonis03.pythonanywhere.com/upisi_termin";

            String data = "naziv_predmeta=" + naziv_predmeta + "&korisnicko_ime_tr=" + korisnicko_ime_tr + "&odobrenje=" + odobrenje + "&mesec=" + mesec + "&dan=" + dan + "&vreme_pocetka=" + vreme_pocetka + "&vreme_zavrsetka=" + vreme_zavrsetka + "&korisnicko_ime=" + korisnicko_ime;
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
        return true;
    }


    public static JSONArray dohvatiTermineZaTrenera(String korisnickoIme) {
        JSONArray terminiJson = null;
        try {

            String url = "http://adonis03.pythonanywhere.com/get_termini_trenera";

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

                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Server response: " + response.toString());

                    JSONParser parser = new JSONParser();
                    terminiJson = (JSONArray) parser.parse(response.toString());


                }
            } else {
                System.out.println("Request failed with response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return terminiJson;
    }

    public static JSONArray dohvatiTermineZaKorisnika(String korisnickoIme) {
        JSONArray terminiJson = null;
        try {

            String url = "http://adonis03.pythonanywhere.com/get_termini_korisnika";

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

                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Server response: " + response.toString());

                    JSONParser parser = new JSONParser();
                    terminiJson = (JSONArray) parser.parse(response.toString());


                }
            } else {
                System.out.println("Request failed with response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String filePath = "termini.json";

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(terminiJson.toJSONString());
            System.out.println("JSONArray written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return terminiJson;
    }

    public static void odobriTermin(String idTermina) {
        try {
            String url = "http://adonis03.pythonanywhere.com/odobri_termin";

            String jsonInputString = "{\"id\": \"" + idTermina + "\"}";
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
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Server response: " + response.toString());

                }
            } else {
                System.out.println("Request failed with response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void odbijTermin(String idTermina) {
        try {
            String url = "http://adonis03.pythonanywhere.com/odbij_termin3";

            String jsonInputString = "{\"id\": \"" + idTermina + "\"}";
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
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Server response: " + response.toString());

                }
            } else {
                System.out.println("Request failed with response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static JSONArray dohvatiTermineZaTrenera2(String korisnickoIme) {
        JSONArray terminiJson = null;
        try {

            String url = "http://adonis03.pythonanywhere.com/get_termini_trenera2";

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

                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Server response: " + response.toString());

                    JSONParser parser = new JSONParser();
                    terminiJson = (JSONArray) parser.parse(response.toString());


                }
            } else {
                System.out.println("Request failed with response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String filePath = "termini.json";

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(terminiJson.toJSONString());
            System.out.println("JSONArray written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return terminiJson;
    }
}
