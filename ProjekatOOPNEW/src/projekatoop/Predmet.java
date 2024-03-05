package projekatoop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Predmet {
    private String nazivPredmeta;
    private String korisnickoImeTr;

    public Predmet() {
        nazivPredmeta = "";
        korisnickoImeTr = "";
    }

    public Predmet(String nazivPredmeta, String korisnickoImeTr) {
        this.nazivPredmeta = nazivPredmeta;
        this.korisnickoImeTr = korisnickoImeTr;
    }

    public String getNazivPredmeta() {
        return nazivPredmeta;
    }

    public String getKorisnickoImeTr() {
        return korisnickoImeTr;
    }

    public void setNazivPredmeta(String nazivPredmeta) {
        this.nazivPredmeta = nazivPredmeta;
    }

    public void setKorisnickoImeTr(String korisnickoImeTr) {
        this.korisnickoImeTr = korisnickoImeTr;
    }

    public void upisiPredmet() {
        try {
            String url = "http://adonis03.pythonanywhere.com/upisi_predmet";

            String data = "naziv_predmeta=" + nazivPredmeta + "&korisnicko_ime_tr=" + korisnickoImeTr;
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
    }

    public static JSONArray dohvatiPredmete(String pojamPretrage) {
        ArrayList<Poruka> poruke = new ArrayList<>();
        boolean kreiranProfil = false;
        JSONArray jsonArray = new JSONArray();
        try {
            String url = "http://adonis03.pythonanywhere.com/get_predmet";

            String data = "naziv_predmeta=" + pojamPretrage;
            byte[] postData = data.getBytes(StandardCharsets.UTF_8);

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData);
            }

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println("hj JSON Response:");
            System.out.println(response.toString());

            JSONParser parser = new JSONParser();
            jsonArray = (JSONArray) parser.parse(response.toString());

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }


}
