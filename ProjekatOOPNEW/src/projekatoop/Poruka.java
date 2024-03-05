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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Poruka {
    private String posiljaoc;
    private String primaoc;
    private String sadrzaj;

    public Poruka() {
        this.posiljaoc = "";
        this.primaoc = "";
        this.sadrzaj = "";
    }

    public Poruka(String posiljaoc, String primaoc) {
        this.posiljaoc = posiljaoc;
        this.primaoc = primaoc;
    }

    public Poruka(String posiljaoc, String primaoc, String sadrzaj) {
        this.posiljaoc = posiljaoc;
        this.primaoc = primaoc;
        this.sadrzaj = sadrzaj;
    }

    public String getPosiljaoc() {
        return posiljaoc;
    }

    public String getPrimaoc() {
        return primaoc;
    }

    public String getSadrzaj() {
        return sadrzaj;
    }

    public void setPosiljaoc(String posiljaoc) {
        this.posiljaoc = posiljaoc;
    }

    public void setPrimaoc(String primaoc) {
        this.primaoc = primaoc;
    }

    public void setSadrzaj(String sadrzaj) {
        this.sadrzaj = sadrzaj;
    }

    public ArrayList<Poruka> iscitajPoruke() throws FileNotFoundException, ParseException, IOException {

        ArrayList<Poruka> poruke = new ArrayList<>();
        boolean kreiranProfil = false;
        JSONArray porukeJson;
        try {
            String url = "http://adonis03.pythonanywhere.com/get_poruke3";

            String data = "primalac=" + primaoc + "&posiljalac=" + posiljaoc;
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
            porukeJson = (JSONArray) parser.parse(response.toString());

            for (Object porukaObj : porukeJson) {
                JSONObject poruka = (JSONObject) porukaObj;
                Poruka p = new Poruka(poruka.get("posiljalac").toString(), poruka.get("primalac").toString(), poruka.get("sadrzaj").toString());

                poruke.add(p);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return poruke;
    }

    public void formatirajJson(String fajl) {
        File file = new File(fajl);

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            long fileLength = randomAccessFile.length();
            if (fileLength > 0) {
                randomAccessFile.setLength(fileLength - 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upisiPoruku() {
        boolean kreiranProfil = false;

        try {
            String url = "http://adonis03.pythonanywhere.com/upisi_poruku";

            String data = "primalac=" + primaoc + "&posiljalac=" + posiljaoc + "&sadrzaj=" + sadrzaj;
            byte[] postData = data.getBytes(StandardCharsets.UTF_8);

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                kreiranProfil = true;
                System.out.println("GOOD");
                System.out.println("Response Code: " + responseCode);

            } else {
                System.out.println("Response Code: " + responseCode);
                kreiranProfil = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
