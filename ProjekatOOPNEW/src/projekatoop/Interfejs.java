package projekatoop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.parser.ParseException;
import javafx.collections.FXCollections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Interfejs extends Application {
    private Osoba osobaKontakt;
    private JFrame f;
    private boolean dugmePritisnuto;
    private Stage primarnaPozornica;
    private Scene scena;

    private enum Status {
        POCETNA_STRANA, LOGOVANJE_STRANA, KREIRANJE_KORISNIKA, KREIRANJE_TRENERA, PORUKA_STRANA
    }

    private Status STATUS = Status.POCETNA_STRANA;

    public void slusajStatus() {
        if (STATUS == Status.LOGOVANJE_STRANA) {
            logovanje();
            System.out.println("logovanje");
        } else if (STATUS == Status.POCETNA_STRANA) {
            pocetnaStrana();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        primarnaPozornica = stage;
        pocetnaStrana();
    }

    public void pocetnaStrana() {
        Pane root = new Pane();

        Button b = new Button("Uloguj se");
        b.setLayoutX(130);
        b.setLayoutY(100);
        b.setMaxWidth(150);
        b.setMinWidth(150);
        b.setMaxHeight(40);
        b.setMinHeight(40);

        root.getChildren().add(b);

        Button b2 = new Button("Kreiraj profil");
        b2.setLayoutX(130);
        b2.setLayoutY(200);
        b2.setMaxWidth(150);
        b2.setMinWidth(150);
        b2.setMaxHeight(40);
        b2.setMinHeight(40);

        root.getChildren().add(b2);

        Button b3 = new Button("Postani trener");
        b3.setLayoutX(130);
        b3.setLayoutY(300);
        b3.setMaxWidth(150);
        b3.setMinWidth(150);
        b3.setMaxHeight(40);
        b3.setMinHeight(40);

        root.getChildren().add(b3);

        b.setOnAction(e -> {
            logovanje();
        });

        b2.setOnAction(e -> {
            try {
                kreiranjeKorisnika();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        b3.setOnAction(e -> {
            kreiranjeTrenera();
        });

        scena = new Scene(root, 400, 500);
        primarnaPozornica.setTitle("Pocetna strana");
        primarnaPozornica.setScene(scena);
        primarnaPozornica.show();
    }

    private Button createChooseButton(String buttonText, Trener tr, Label selectedTrainerLabel) {
        Button chooseButton = new Button(buttonText + tr.getIme());

        chooseButton.setOnAction(e -> {
            System.out.println("poz van ifa");
            if (osobaKontakt instanceof Korisnik) {
                Korisnik k = (Korisnik) osobaKontakt;
                System.out.println("poz iz ifa");
                try {
                    if (!k.izaberiTrenera(tr)) {
                        selectedTrainerLabel.setVisible(true);
                    }
                } catch (IOException | ParseException ex) {
                    Logger.getLogger(Interfejs.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return chooseButton;
    }

    private Button createChooseButton(String id, boolean odbijanje) {
        Button chooseButton = new Button("Odobri");
        if (!odbijanje)
            chooseButton.setOnAction(e -> {
                Termin.odobriTermin(id);
            });
        else {
            chooseButton.setText("Odbij");
            chooseButton.setOnAction(e -> {
                Termin.odbijTermin(id);
            });
        }
        return chooseButton;
    }

    private Button createChooseButton(String buttonText, String k, Label selectedTrainerLabel) {
        Button chooseButton = new Button(buttonText);

        chooseButton.setOnAction(e -> {
            formaSlanjePorukeKorisniku(k);
        });

        return chooseButton;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void kreiranjeKorisnika() throws FileNotFoundException {
        Pane root = new Pane();

        Button home = new Button("<--");
        home.setLayoutX(330);
        home.setLayoutY(20);
        root.getChildren().add(home);

        home.setOnAction(e -> {
            primarnaPozornica.close();
            pocetnaStrana();
        });

        Label labelaGreska = new Label("Pogresno ste uneli podatke");
        labelaGreska.setLayoutX(75);
        labelaGreska.setLayoutY(370);
        labelaGreska.setVisible(false);
        root.getChildren().add(labelaGreska);

        Button kreirajProfil = new Button("Kreiraj profil");
        kreirajProfil.setLayoutX(130);
        kreirajProfil.setLayoutY(100);
        kreirajProfil.setMinWidth(150);
        root.getChildren().add(kreirajProfil);

        Label labelaIme = new Label("Ime:");
        labelaIme.setLayoutX(100);
        labelaIme.setLayoutY(150);
        root.getChildren().add(labelaIme);

        TextField txtBoxIme = new TextField();
        txtBoxIme.setLayoutX(130);
        txtBoxIme.setLayoutY(150);
        txtBoxIme.setMinWidth(200);
        root.getChildren().add(txtBoxIme);

        Label labelaPrezime = new Label("Prezime:");
        labelaPrezime.setLayoutX(75);
        labelaPrezime.setLayoutY(200);
        root.getChildren().add(labelaPrezime);

        TextField txtBoxPrezime = new TextField();
        txtBoxPrezime.setLayoutX(130);
        txtBoxPrezime.setLayoutY(200);
        txtBoxPrezime.setMinWidth(200);
        root.getChildren().add(txtBoxPrezime);

        Label labelaGodine = new Label("Godine:");
        labelaGodine.setLayoutX(75);
        labelaGodine.setLayoutY(250);
        root.getChildren().add(labelaGodine);

        TextField txtBoxGodine = new TextField();
        txtBoxGodine.setLayoutX(130);
        txtBoxGodine.setLayoutY(250);
        txtBoxGodine.setMinWidth(200);
        root.getChildren().add(txtBoxGodine);

        Label labelaKorisnickoIme = new Label("Korisnicko ime:");
        labelaKorisnickoIme.setLayoutX(40);
        labelaKorisnickoIme.setLayoutY(300);
        root.getChildren().add(labelaKorisnickoIme);

        TextField txtBoxKorisnickoIme = new TextField();
        txtBoxKorisnickoIme.setLayoutX(130);
        txtBoxKorisnickoIme.setLayoutY(300);
        txtBoxKorisnickoIme.setMinWidth(200);
        root.getChildren().add(txtBoxKorisnickoIme);

        Label labelaLozinka = new Label("Lozinka:");
        labelaLozinka.setLayoutX(75);
        labelaLozinka.setLayoutY(350);
        root.getChildren().add(labelaLozinka);

        TextField txtBoxLozinka = new TextField();
        txtBoxLozinka.setLayoutX(130);
        txtBoxLozinka.setLayoutY(350);
        txtBoxLozinka.setMinWidth(200);
        root.getChildren().add(txtBoxLozinka);

        Label vecPostoji = new Label("Korisnik vec postoji!");
        vecPostoji.setLayoutX(75);
        vecPostoji.setLayoutY(400);
        vecPostoji.setVisible(false);
        root.getChildren().add(vecPostoji);

        kreirajProfil.setOnAction(e -> {
            try {
                labelaGreska.setVisible(false);
                vecPostoji.setVisible(false);

                if (txtBoxIme.getText().isBlank() || txtBoxPrezime.getText().isBlank() ||
                        txtBoxGodine.getText().isBlank() || txtBoxKorisnickoIme.getText().isBlank() ||
                        txtBoxLozinka.getText().isBlank()) {
                    labelaGreska.setVisible(true);
                    return;
                }

                // Check if the input for 'Godine' is numeric
                if (!txtBoxGodine.getText().matches("\\d+")) {
                    labelaGreska.setVisible(true);
                    return;
                }

                osobaKontakt = new Korisnik(txtBoxIme.getText(), txtBoxPrezime.getText(),
                        txtBoxKorisnickoIme.getText(), txtBoxLozinka.getText(),
                        Integer.parseInt(txtBoxGodine.getText()));

                if (!osobaKontakt.kreirajProfil()) {
                    vecPostoji.setVisible(true);
                    txtBoxIme.setText("");
                    txtBoxPrezime.setText("");
                    txtBoxGodine.setText("");
                    txtBoxKorisnickoIme.setText("");
                    txtBoxLozinka.setText("");
                } else {
                    primarnaPozornica.close();
                    logovanje();
                }

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        Scene scene = new Scene(root, 400, 500);
        primarnaPozornica.setTitle("Pocetna strana korisnik");
        primarnaPozornica.setScene(scene);
        primarnaPozornica.show();

    }

    public void logovanje() {
        Pane root = new Pane();

        Button home = new Button("<--");
        home.setLayoutX(330);
        home.setLayoutY(20);
        root.getChildren().add(home);

        home.setOnAction(e -> {
            primarnaPozornica.close();
            STATUS = Status.POCETNA_STRANA;
            slusajStatus();
        });

        Button b = new Button("Uloguj se");
        b.setLayoutX(130);
        b.setLayoutY(100);
        b.setMinWidth(150);

        Label labelaKorisnickoIme = new Label("Korisnicko ime:");
        labelaKorisnickoIme.setLayoutX(40);
        labelaKorisnickoIme.setLayoutY(300);

        TextField txtBoxKorisnickoIme = new TextField();
        txtBoxKorisnickoIme.setLayoutX(130);
        txtBoxKorisnickoIme.setLayoutY(300);
        txtBoxKorisnickoIme.setMinWidth(200);


        Label labelaLozinka = new Label("Lozinka:");
        labelaLozinka.setLayoutX(75);
        labelaLozinka.setLayoutY(350);

        TextField txtBoxLozinka = new PasswordField();
        txtBoxLozinka.setLayoutX(130);
        txtBoxLozinka.setLayoutY(350);
        txtBoxLozinka.setMinWidth(200);

        Label labelaZaProveru = new Label("Unesite sve podatke!");
        labelaZaProveru.setLayoutX(150);
        labelaZaProveru.setLayoutY(250);
        labelaZaProveru.setVisible(false);

        Label labelaZaPostoji = new Label("Profil ne postoji!");
        labelaZaPostoji.setLayoutX(150);
        labelaZaPostoji.setLayoutY(250);
        labelaZaPostoji.setVisible(false);

        b.setOnAction(e -> {
            try {
                if (txtBoxKorisnickoIme.getText().isBlank() || txtBoxLozinka.getText().isBlank()) {
                    labelaZaProveru.setVisible(true);
                    return;
                }
                labelaZaProveru.setVisible(false);

                boolean[] uspesno = new boolean[2];
                osobaKontakt = Osoba.ulogujSe(uspesno, txtBoxKorisnickoIme.getText(), txtBoxLozinka.getText());

                if (uspesno[0]) {
                    primarnaPozornica.close();
                    pocetnaKorisnik();
                } else {
                    labelaZaPostoji.setVisible(true);
                }
            } catch (UnknownHostException ex) {
                System.out.println(ex.getMessage());
                primarnaPozornica.close();
                terminiBezInterneta(primarnaPozornica);
            } catch (IOException | ParseException ex) {
                ex.printStackTrace();
            }
        });


        root.getChildren().addAll(b, labelaKorisnickoIme, txtBoxKorisnickoIme, labelaLozinka, txtBoxLozinka,
                labelaZaProveru, labelaZaPostoji);

        Scene scene = new Scene(root, 400, 500);
        primarnaPozornica.setTitle("LogIn");
        primarnaPozornica.setScene(scene);
        primarnaPozornica.show();
        File file = new File("profil.json");

        if (file.exists()) {
            try (FileReader fileReader = new FileReader("profil.json")) {
                JSONArray profil = (JSONArray) new JSONParser().parse(fileReader);

                txtBoxLozinka.setText(((JSONObject) profil.get(0)).get("sifra").toString());
                txtBoxKorisnickoIme.setText(((JSONObject) profil.get(0)).get("korisnicko_ime").toString());
                b.fire();


            } catch (IOException | ParseException ex) {
                System.out.println(ex.getMessage());
            }
        }


    }

    public void pocetnaKorisnik() {
        Pane root = new Pane();

        Button dodaj = new Button("Dodaj predmet");
        dodaj.setLayoutX(95);
        dodaj.setLayoutY(310);

        dodaj.setOnAction(e -> {
            primarnaPozornica.close();
            dodavanjePredmeta();
        });

        Button dgm = new Button("Pogledaj zahteve");
        dgm.setLayoutX(95);
        dgm.setLayoutY(340);

        dgm.setOnAction(e -> {
            primarnaPozornica.close();
            odobriZahteveForma();
        });

        Button home = new Button("X");
        home.setLayoutX(330);
        home.setLayoutY(20);
        root.getChildren().add(home);

        home.setOnAction(e -> {
            primarnaPozornica.close();
            pocetnaStrana();
        });

        Button termini = new Button("Termini");
        termini.setLayoutX(95);
        termini.setLayoutY(250);

        termini.setOnAction(e -> {
            primarnaPozornica.close();
            terminiKorisnik();
        });

        Button dugmePosalji = new Button("Posalji poruke");
        dugmePosalji.setLayoutX(95);
        dugmePosalji.setLayoutY(280);

        dugmePosalji.setOnAction(e -> {
            primarnaPozornica.close();
            vidiKorisnike();
        });

        Button logOut = new Button("Izlogujte se");
        logOut.setLayoutX(330);
        logOut.setLayoutY(90);
        root.getChildren().add(logOut);

        logOut.setOnAction(e -> {
            primarnaPozornica.close();
            File fileToDelete = new File("profil.json");

            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    System.out.println("File deleted successfully.");
                    pocetnaStrana();
                } else {
                    System.out.println("Failed to delete the file.");
                }
            } else {
                System.out.println("File does not exist");
            }
        });

        Button pogledajTermine = new Button("Pogledaj termine");
        pogledajTermine.setLayoutX(95);
        pogledajTermine.setLayoutY(340);

        pogledajTermine.setOnAction(e -> {
            primarnaPozornica.close();
            terminiKorisnik();
        });

        Button pronadjiTrenera2 = new Button("Pronadji TR");
        pronadjiTrenera2.setLayoutX(95);
        pronadjiTrenera2.setLayoutY(250);

        pronadjiTrenera2.setOnAction(e -> {
            primarnaPozornica.close();
            try {
                pretraziTrenere();
            } catch (Exception ex) {
                Logger.getLogger(Interfejs.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Button termin = new Button("Zakazite termin");
        termin.setLayoutX(95);
        termin.setLayoutY(310);

        termin.setOnAction(e -> {
            primarnaPozornica.close();
            try {
                zakaziTermin();
            } catch (ParseException | IOException ex) {
                Logger.getLogger(Interfejs.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Label ime = new Label("Ime:" + osobaKontakt.getIme());
        ime.setLayoutX(95);
        ime.setLayoutY(70);
        root.getChildren().add(ime);

        Label prezime = new Label("Prezime:" + osobaKontakt.getPrezime());
        prezime.setLayoutX(95);
        prezime.setLayoutY(120);
        root.getChildren().add(prezime);

        Label godine = new Label("Godine:" + osobaKontakt.getGodine() + "");
        godine.setLayoutX(95);
        godine.setLayoutY(170);
        root.getChildren().add(godine);

        Label korisnickoIme = new Label("Korisnicko ime:" + osobaKontakt.getKorisnickoIme());
        korisnickoIme.setLayoutX(95);
        korisnickoIme.setLayoutY(220);
        root.getChildren().add(korisnickoIme);

        Button btnTreneri = new Button("Pisi poruku");
        btnTreneri.setLayoutX(95);
        btnTreneri.setLayoutY(280);

        btnTreneri.setOnAction(e -> {
            primarnaPozornica.close();
            vidiTrenera();
        });

        try {
            if (osobaKontakt instanceof Korisnik) {
                root.getChildren().add(pronadjiTrenera2);
                Korisnik k = (Korisnik) osobaKontakt;
                if (k.trenerIzabran()) {
                    root.getChildren().addAll(pogledajTermine, termin, btnTreneri);
                }
            } else {
                root.getChildren().addAll(termini, dodaj, dgm, dugmePosalji);
            }
        } catch (Exception e) {

        }

        Scene scene = new Scene(root, 400, 500);
        primarnaPozornica.setTitle("Pocetna Korisnik");
        primarnaPozornica.setScene(scene);
        primarnaPozornica.show();
    }

    public void kreiranjeTrenera() {
        Pane root = new Pane();

        Label labelaGreska = new Label("Pogresno ste uneli podatke");
        labelaGreska.setLayoutX(75);
        labelaGreska.setLayoutY(580);
        labelaGreska.setVisible(false);

        Button home = new Button("<--");
        home.setLayoutX(330);
        home.setLayoutY(20);

        home.setOnAction(e -> {
            primarnaPozornica.close();
            pocetnaStrana();
        });

        Button b = new Button("Kreiraj profil trenera");
        b.setLayoutX(130);
        b.setLayoutY(100);

        Label labelaIme = new Label("Ime:");
        labelaIme.setLayoutX(100);
        labelaIme.setLayoutY(150);

        TextField txtBoxIme = new TextField();
        txtBoxIme.setLayoutX(130);
        txtBoxIme.setLayoutY(150);

        Label labelaPrezime = new Label("Prezime:");
        labelaPrezime.setLayoutX(75);
        labelaPrezime.setLayoutY(200);

        TextField txtBoxPrezime = new TextField();
        txtBoxPrezime.setLayoutX(130);
        txtBoxPrezime.setLayoutY(200);

        Label labelaGodine = new Label("Godine:");
        labelaGodine.setLayoutX(75);
        labelaGodine.setLayoutY(250);

        TextField txtBoxGodine = new TextField();
        txtBoxGodine.setLayoutX(130);
        txtBoxGodine.setLayoutY(250);

        Label labelaKorisnickoIme = new Label("Korisnicko ime:");
        labelaKorisnickoIme.setLayoutX(40);
        labelaKorisnickoIme.setLayoutY(300);

        TextField txtBoxKorisnickoIme = new TextField();
        txtBoxKorisnickoIme.setLayoutX(130);
        txtBoxKorisnickoIme.setLayoutY(300);

        Label labelaLozinka = new Label("Lozinka:");
        labelaLozinka.setLayoutX(75);
        labelaLozinka.setLayoutY(350);

        TextField txtBoxLozinka = new TextField();
        txtBoxLozinka.setLayoutX(130);
        txtBoxLozinka.setLayoutY(350);

        Label labelaGodIskustva = new Label("Godine iskustva:");
        labelaGodIskustva.setLayoutX(30);
        labelaGodIskustva.setLayoutY(400);

        TextField txtBoxGodIskustva = new TextField();
        txtBoxGodIskustva.setLayoutX(130);
        txtBoxGodIskustva.setLayoutY(400);

        Label labelaPol = new Label("Pol:");
        labelaPol.setLayoutX(75);
        labelaPol.setLayoutY(450);

        TextField txtBoxPol = new TextField();
        txtBoxPol.setLayoutX(130);
        txtBoxPol.setLayoutY(450);

        Label vecPostoji = new Label("Korisnik vec postoji!");
        vecPostoji.setLayoutX(75);
        vecPostoji.setLayoutY(500);
        vecPostoji.setVisible(false);

        b.setOnAction(e -> {
            Pattern patternBrojevi = Pattern.compile("\\d+");
            Matcher odgovaraGodine = patternBrojevi.matcher(txtBoxGodine.getText());
            Matcher odgovaraIskustvo = patternBrojevi.matcher(txtBoxGodIskustva.getText());
            if (!odgovaraGodine.matches() || !odgovaraIskustvo.matches()) {
                labelaGreska.setVisible(true);
                return;
            }
            try {
                osobaKontakt = new Trener(Integer.parseInt(txtBoxGodIskustva.getText()), txtBoxPol.getText(),
                        txtBoxIme.getText(), txtBoxPrezime.getText(), txtBoxKorisnickoIme.getText(),
                        txtBoxLozinka.getText(), Integer.parseInt(txtBoxGodine.getText()));

                if (!osobaKontakt.kreirajProfil()) {
                    vecPostoji.setVisible(true);
                    txtBoxIme.setText("");
                    txtBoxPrezime.setText("");
                    txtBoxGodine.setText("");
                    txtBoxKorisnickoIme.setText("");
                    txtBoxLozinka.setText("");
                    txtBoxGodIskustva.setText("");
                    txtBoxPol.setText("");
                } else {
                    primarnaPozornica.close();
                    logovanje();
                }

            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(home, b, labelaIme, txtBoxIme, labelaPrezime, txtBoxPrezime, labelaGodine,
                txtBoxGodine, labelaKorisnickoIme, txtBoxKorisnickoIme, labelaLozinka, txtBoxLozinka,
                labelaGodIskustva, txtBoxGodIskustva, labelaPol, txtBoxPol, vecPostoji, labelaGreska);

        Scene scene = new Scene(root, 500, 550);
        primarnaPozornica.setTitle("Kreiranje Trenera");
        primarnaPozornica.setScene(scene);
        primarnaPozornica.show();
    }

    public void vidiTrenera() {
        primarnaPozornica.setTitle("Poruke");

        Pane root = new Pane();
        ArrayList<Label> listaLabela = new ArrayList<>();

        Korisnik kor = (Korisnik) osobaKontakt;

        Button home = new Button("Idi na pocetnu");
        home.setOnAction(e -> {
            primarnaPozornica.close();
            pocetnaStrana();
        });
        home.setLayoutX(330);
        home.setLayoutY(20);

        Button dugmeUnazad = new Button("<-");
        dugmeUnazad.setOnAction(e -> {
            primarnaPozornica.close();
            pocetnaKorisnik();
        });

        TextField poruka = new TextField();
        poruka.setPromptText("Unesite poruku");
        poruka.setLayoutX(10);
        poruka.setLayoutY(500);
        poruka.setMinWidth(300);
        poruka.setMaxWidth(300);
        poruka.setMinHeight(40);
        poruka.setMaxHeight(40);

        ArrayList<Poruka> listaPoruka = kor.procitajPoruke();
        int brojac2 = 0;
        for (int i = listaPoruka.size() - 1; i >= 0; --i) {
            Label labela = new Label(listaPoruka.get(i).getSadrzaj());
            if (osobaKontakt.getKorisnickoIme().equals(listaPoruka.get(i).getPosiljaoc())) {
                labela.setLayoutX(280);
                labela.setLayoutY(470 - brojac2 * 40);
                //labela.setStyle("-fx-background-color: lightblue;");
            } else {
                labela.setLayoutX(10);
                labela.setLayoutY(470 - brojac2 * 40);
            }
            root.getChildren().add(labela);
            listaLabela.add(labela);
            brojac2++;
        }

        Button btnPosalji = new Button("Posalji");
        btnPosalji.setLayoutX(320);
        btnPosalji.setLayoutY(495);
        btnPosalji.setOnAction(e -> {
            if (osobaKontakt instanceof Korisnik) {
                Korisnik k = (Korisnik) osobaKontakt;
                k.posaljiPorukuTreneru(poruka.getText());
            }

            listaLabela.forEach(label -> {
                root.getChildren().remove(label);
            });

            ArrayList<Poruka> listaPoruka2 = kor.procitajPoruke();
            int brojac = 0;
            for (int i = listaPoruka2.size() - 1; i >= 0; --i) {
                Label labela = new Label(listaPoruka2.get(i).getSadrzaj());
                if (osobaKontakt.getKorisnickoIme().equals(listaPoruka2.get(i).getPosiljaoc())) {
                    labela.setLayoutX(280);
                    labela.setLayoutY(470 - brojac * 40);
                    //labela.setStyle("-fx-background-color: lightblue;");
                } else {
                    labela.setLayoutX(10);
                    labela.setLayoutY(470 - brojac * 40);
                }
                root.getChildren().add(labela);
                listaLabela.add(labela);
                brojac++;
            }
            poruka.clear();
        });

        int period = 2; // Period between updates (2 seconds)

        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(period), event -> {
            listaLabela.forEach(label -> {
                root.getChildren().remove(label);
            });

            ArrayList<Poruka> listaPoruka3 = kor.procitajPoruke();
            int brojac = 0;
            for (int i = listaPoruka3.size() - 1; i >= 0; --i) {
                Label labela = new Label(listaPoruka3.get(i).getSadrzaj());
                if (osobaKontakt.getKorisnickoIme().equals(listaPoruka3.get(i).getPosiljaoc())) {
                    labela.setLayoutX(280);
                    labela.setLayoutY(470 - brojac * 40);
                    //labela.setStyle("-fx-background-color: lightblue;");
                } else {
                    labela.setLayoutX(10);
                    labela.setLayoutY(470 - brojac * 40);
                }
                root.getChildren().add(labela);
                listaLabela.add(labela);
                brojac++;
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

        Button btnIzbrisi = new Button("Izbrisi");
        btnIzbrisi.setLayoutX(320);
        btnIzbrisi.setLayoutY(520);
        btnIzbrisi.setOnAction(e -> {
            if (osobaKontakt instanceof Korisnik) {
                Korisnik ko = (Korisnik) osobaKontakt;
                primarnaPozornica.close();
                timer.stop();
                ko.izbrisiTrenera();
                pocetnaKorisnik();
            }
        });

        root.getChildren().addAll(home, dugmeUnazad, poruka, btnPosalji, btnIzbrisi);
        Scene scene = new Scene(root, 425, 600);
        primarnaPozornica.setScene(scene);
        primarnaPozornica.show();
    }

    public void vidiKorisnike() {
        primarnaPozornica.setTitle("Vidi Korisnike");

        Pane root = new Pane();

        Button home = new Button("Idi na pocetnu");
        home.setLayoutX(330);
        home.setLayoutY(20);
        home.setOnAction(e -> {
            primarnaPozornica.close();
            pocetnaStrana();
        });

        Button dugmeNazad = createButtonUnazad();
        dugmeNazad.setLayoutX(20);
        dugmeNazad.setLayoutY(20);

        Label izabranK = new Label("");
        ArrayList<Korisnik> listaKorisnika = new ArrayList<>();
        try {
            Trener tr = (Trener) osobaKontakt;
            listaKorisnika = tr.procitajKorisnike();
            System.out.println("Posle procitaj korisnike");
            System.out.println(tr.getPol());
            for (Korisnik k : listaKorisnika) {
                System.out.println(k);
            }
        } catch (ParseException | IOException ex) {
            Logger.getLogger(Interfejs.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<Button> listaDugmadi = new ArrayList<>();

        for (int i = 0; i < listaKorisnika.size(); i++) {
            Label ime = new Label(listaKorisnika.get(i).getKorisnickoIme());
            ime.setLayoutX(25);
            ime.setLayoutY(i * 50 + 90);

            Button dugme = createChooseButton("Posalji", listaKorisnika.get(i).getKorisnickoIme(), izabranK);
            dugme.setLayoutX(115);
            dugme.setLayoutY(i * 50 + 90);

            root.getChildren().addAll(ime, dugme);
            listaDugmadi.add(dugme);
        }

        root.getChildren().addAll(home, dugmeNazad);

        primarnaPozornica.setScene(new Scene(root, 400, 500));
        primarnaPozornica.show();
    }

    public void formaSlanjePorukeKorisniku(String korisnik1) {
        primarnaPozornica.setTitle("Slanje Poruke");

        Pane contentPane = createContentPane(korisnik1);

        ScrollPane scrollPane = new ScrollPane(contentPane);
        scrollPane.setPrefSize(450, 550);

        primarnaPozornica.setScene(new Scene(scrollPane));
        primarnaPozornica.show();
    }

    private Pane createContentPane(String korisnik1) {
        Pane pane = new Pane();

        Button home = new Button("Idi na pocetnu");
        home.setLayoutX(330);
        home.setLayoutY(20);
        pane.getChildren().add(home);

        home.setOnAction(event -> {
            primarnaPozornica.close();
            pocetnaStrana();
        });

        Button dugmeUnazad = new Button("<-");
        dugmeUnazad.setLayoutX(20);
        dugmeUnazad.setLayoutY(20);
        pane.getChildren().add(dugmeUnazad);

        dugmeUnazad.setOnAction(event -> {
            primarnaPozornica.close();
            vidiKorisnike();
        });

        TextArea poruka1 = new TextArea();
        poruka1.setLayoutX(10);
        poruka1.setLayoutY(440);
        poruka1.setPrefSize(300, 40);
        pane.getChildren().add(poruka1);

        ArrayList<Text> listaLabela = new ArrayList<>();

        Trener tr = (Trener) osobaKontakt;

        int brojac3 = 0;
        ArrayList<Poruka> listaPoruka = tr.procitajPoruke(korisnik1);
        for (int i = listaPoruka.size() - 1; i >= 0; --i) {
            Text labela = new Text(listaPoruka.get(i).getSadrzaj());
            //labela.setFont(new Font(12));
            if (osobaKontakt.getKorisnickoIme().equals(listaPoruka.get(i).getPosiljaoc())) {
                labela.setLayoutX(280);
                labela.setLayoutY(210 - brojac3 * 40);
            } else {
                labela.setLayoutX(10);
                labela.setLayoutY(210 - brojac3 * 40);
            }
            pane.getChildren().add(labela);
            listaLabela.add(labela);
            brojac3++;
            System.out.println(listaPoruka.get(i).getSadrzaj());
        }

        Button btnPosalji = new Button("Posalji");
        btnPosalji.setLayoutX(330);
        btnPosalji.setLayoutY(445);
        pane.getChildren().add(btnPosalji);

        btnPosalji.setOnAction(event -> {
            if (osobaKontakt instanceof Trener) {
                Trener t = (Trener) osobaKontakt;
                t.posaljiPorukuKorisniku(poruka1.getText(), korisnik1);
            }

            for (int i = 0; i < listaLabela.size(); ++i) {
                listaLabela.get(i).setText(listaPoruka.get(i).getSadrzaj());
                listaLabela.remove(i--);
            }

            int brojac = 0;
            ArrayList<Poruka> listaPoruka2 = tr.procitajPoruke(korisnik1);
            for (int i = listaPoruka2.size() - 1; i >= 0; --i) {
                Text labela = new Text(listaPoruka2.get(i).getSadrzaj());

                if (osobaKontakt.getKorisnickoIme().equals(listaPoruka2.get(i).getPosiljaoc())) {
                    labela.setLayoutX(280);
                    labela.setLayoutY(400 - brojac * 40);
                } else {
                    labela.setLayoutX(10);
                    labela.setLayoutY(400 - brojac * 40);
                }
                pane.getChildren().add(labela);

                brojac++;
                System.out.println(listaPoruka2.get(i).getSadrzaj());
                listaLabela.add(labela);
            }

            poruka1.setText("");
        });

        int period = 2000;

        Timeline timer = new Timeline(new KeyFrame(Duration.millis(period), event -> {
            for (int i = 0; i < listaLabela.size(); ++i) {
                listaLabela.get(i).setText(listaPoruka.get(i).getSadrzaj());
                listaLabela.remove(i--);
            }

            System.out.println("piki");
            int brojac = 0;
            ArrayList<Poruka> listaPoruka3 = tr.procitajPoruke(korisnik1);
            for (int i = listaPoruka3.size() - 1; i >= 0; --i) {
                Text labela = new Text(listaPoruka3.get(i).getSadrzaj());

                if (osobaKontakt.getKorisnickoIme().equals(listaPoruka3.get(i).getPosiljaoc())) {
                    labela.setLayoutX(280);
                    labela.setLayoutY(400 - brojac * 40);
                } else {
                    labela.setLayoutX(10);
                    labela.setLayoutY(400 - brojac * 40);
                }
                pane.getChildren().add(labela);

                brojac++;
                System.out.println(listaPoruka3.get(i).getSadrzaj());
                listaLabela.add(labela);
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

        return pane;
    }

    public void pretraziTrenere() {
        Pane root = new Pane();

        ArrayList<Label> labele = new ArrayList<>();
        ArrayList<Button> dugmad = new ArrayList<>();

        Label labela = new Label("Vec ste izabrali!");
        labela.setLayoutX(20);
        labela.setLayoutY(450);
        labela.setVisible(false);

        Label labelaG = new Label("Greska pri unosu!");
        labelaG.setLayoutX(320);
        labelaG.setLayoutY(83);
        labelaG.setVisible(false);

        TextField unos = new TextField();
        unos.setLayoutX(20);
        unos.setLayoutY(83);

        Button home = new Button("X");
        home.setLayoutX(1070);
        home.setLayoutY(20);

        home.setOnAction(e -> {
            //primarnaPozornica.close();
            pocetnaStrana();
        });

        Button dugmeNazad = new Button("<-");
        dugmeNazad.setLayoutX(1020);
        dugmeNazad.setLayoutY(20);

        dugmeNazad.setOnAction(e -> {
            //primarnaPozornica.close();
            pocetnaKorisnik();
        });

        Button pretrazi = new Button("Pretrazi");
        pretrazi.setLayoutX(190);
        pretrazi.setLayoutY(83);
        pretrazi.setOnAction(e -> {
            int y = 150;
            String regex = ".*[a-zA-Z].*";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher1 = pattern.matcher(unos.getText());

            if (!matcher1.matches()) {
                labelaG.setVisible(true);
                return;
            }

            labelaG.setVisible(false);

            JSONArray jsonArray = Predmet.dohvatiPredmete(unos.getText());

            if (!labele.isEmpty()) {
                root.getChildren().removeAll(labele);
                labele.clear();
            }

            if (!dugmad.isEmpty()) {
                root.getChildren().removeAll(dugmad);
                dugmad.clear();
            }

            for (Object o : jsonArray) {
                JSONObject predmetObj = (JSONObject) o;

                Label nazivPredmeta = new Label("Predmet: " + predmetObj.get("naziv_predmeta"));
                Label korisnickoIme = new Label("Korisnicko ime: " + predmetObj.get("korisnicko_ime"));
                Label godine = new Label("Godine: " + predmetObj.get("godine"));
                Label godineIskustva = new Label("Godine iskustva: " + predmetObj.get("godine_iskustva"));
                Label pol = new Label("Pol: " + predmetObj.get("pol"));
                Label ime = new Label("Ime: " + predmetObj.get("ime"));

                labele.add(nazivPredmeta);
                labele.add(korisnickoIme);
                labele.add(godine);
                labele.add(godineIskustva);
                labele.add(pol);
                labele.add(ime);

                Trener tr = new Trener();
                tr.setKorisnickoIme(predmetObj.get("korisnicko_ime").toString());

                Button izaberi = createChooseButton("Izaberi", tr, labela);
                dugmad.add(izaberi);

                izaberi.setLayoutX(960);
                izaberi.setLayoutY(y);

                nazivPredmeta.setLayoutX(20);
                nazivPredmeta.setLayoutY(y);

                korisnickoIme.setLayoutX(180);
                korisnickoIme.setLayoutY(y);

                godine.setLayoutX(350);
                godine.setLayoutY(y);

                godineIskustva.setLayoutX(450);
                godineIskustva.setLayoutY(y);

                pol.setLayoutX(620);
                pol.setLayoutY(y);

                ime.setLayoutX(790);
                ime.setLayoutY(y);

                y += 40;

                root.getChildren().addAll(nazivPredmeta, korisnickoIme, godine, godineIskustva, pol, ime, izaberi);
            }
        });

        root.getChildren().addAll(labela, labelaG, unos, home, dugmeNazad, pretrazi);

        Scene scene = new Scene(root, 1150, 600);
        primarnaPozornica.setTitle("Pretrazi Trenere");
        primarnaPozornica.setScene(scene);
        primarnaPozornica.show();
    }

    private Button createButtonUnazad(){
        Button chooseButton = new Button("<-");

        chooseButton.setOnAction(e -> {
            pocetnaKorisnik();
        });

        return chooseButton;
    }

    public void zakaziTermin() throws ParseException, IOException {
        Pane root = new Pane();

        ObservableList<String> monthsList = FXCollections.observableArrayList();
        ObservableList<Integer> daysList = FXCollections.observableArrayList();
        ObservableList<String> timesList = FXCollections.observableArrayList();
        ObservableList<String> anotherTimesList = FXCollections.observableArrayList();
        ObservableList<String> predmetiList = FXCollections.observableArrayList();

        Button home = new Button("X");
        home.setLayoutX(750);
        home.setLayoutY(20);
        Button dugmeNazad = createButtonUnazad();
        dugmeNazad.setLayoutX(700);
        dugmeNazad.setLayoutY(20);

        home.setOnAction(e -> {
            primarnaPozornica.close();
            pocetnaStrana();
        });

        JSONArray predmetiZaComboBox = null;
        Korisnik k = (Korisnik) osobaKontakt;
        if (osobaKontakt instanceof Korisnik) {
            k = (Korisnik) osobaKontakt;

            System.out.println("DESILO SE");
            String stringPredmeti = k.dohvatiPredmetTrenera();
            System.out.println(stringPredmeti);
            predmetiZaComboBox = (JSONArray) new JSONParser().parse(stringPredmeti);
        }
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();

        ComboBox<String> monthComboBox = new ComboBox<>(monthsList);
        ComboBox<Integer> dayComboBox = new ComboBox<>(daysList);
        ComboBox<String> timeComboBox = new ComboBox<>(timesList);
        ComboBox<String> anotherComboBox = new ComboBox<>(anotherTimesList);
        ComboBox<String> predmetComboBox = new ComboBox<>(predmetiList);

        monthComboBox.setLayoutX(50);
        monthComboBox.setLayoutY(80);
        dayComboBox.setLayoutX(160);
        dayComboBox.setLayoutY(80);
        timeComboBox.setLayoutX(270);
        timeComboBox.setLayoutY(80);
        anotherComboBox.setLayoutX(380);
        anotherComboBox.setLayoutY(80);
        predmetComboBox.setLayoutX(490);
        predmetComboBox.setLayoutY(80);

        Button posalji = new Button("Posalji");
        posalji.setLayoutX(650);
        posalji.setLayoutY(80);

        Label labelaGreska = new Label("Greska pri unosu!");
        labelaGreska.setLayoutX(650);
        labelaGreska.setLayoutY(110);
        labelaGreska.setVisible(false);
        Termin ter = new Termin();
        ter.setKorisnicko_ime(k.getKorisnickoIme());
        ter.setKorisnicko_ime_tr(k.dohvatiTrenera());

        posalji.setOnAction(e -> {
            if (monthComboBox.getValue() != null && dayComboBox.getValue() != null &&
                    timeComboBox.getValue() != null && anotherComboBox.getValue() != null &&
                    predmetComboBox.getValue() != null) {

                ter.setDan(dayComboBox.getValue());
                ter.setMesec(Integer.parseInt(monthComboBox.getValue()));
                ter.setNaziv_prdmeta(predmetComboBox.getValue());
                ter.setOdobrenje(0);
                ter.setVreme_pocetka(timeComboBox.getValue());
                ter.setVreme_zavrsetka(anotherComboBox.getValue());
                try {
                    ter.upisiMe();
                } catch (Exception ioException) {
                    ioException.printStackTrace();
                }
                labelaGreska.setVisible(false);
            } else {
                labelaGreska.setVisible(true);
            }
        });

        for (int i = currentMonth; i <= 12; i++) {
            monthsList.add(String.valueOf(i));
        }

        // Initialize timeComboBox without a selected item
        timeComboBox.getSelectionModel().select(null);
        monthComboBox.getSelectionModel().select(null);
        for (int i = 8; i <= 19; i++) {
            String time = String.format("%02d:00", i);
            timesList.add(time);
        }
        timeComboBox.getSelectionModel().select(null);

        // Populate predmetComboBox with data from predmetiZaComboBox
        if (predmetiZaComboBox != null || true) {
            for (Object obj : predmetiZaComboBox) {
                JSONObject predmetObj = (JSONObject) obj;
                String nazivPredmeta = (String) predmetObj.get("naziv_predmeta");
                System.out.println(nazivPredmeta);
                predmetiList.add(nazivPredmeta);
            }
        }
        predmetComboBox.getSelectionModel().select(null);

        monthComboBox.setOnAction(e -> {
            int selectedMonthIndex = monthComboBox.getSelectionModel().getSelectedIndex() + currentMonth;
            YearMonth selectedYearMonth = YearMonth.of(currentDate.getYear(), selectedMonthIndex);

            int maxDays = selectedYearMonth.lengthOfMonth();
            int startDay = (selectedMonthIndex == currentMonth) ? currentDate.getDayOfMonth() : 1;

            daysList.clear();
            for (int i = startDay; i <= maxDays; i++) {
                daysList.add(i);
            }
        });

        timeComboBox.setOnAction(e -> {
            String selectedTime = timeComboBox.getValue();
            int hour = Integer.parseInt(selectedTime.substring(0, 2));
            anotherTimesList.clear();

            // Add times from selected time + 1 to 20:00
            for (int i = hour + 1; i <= 20; i++) {
                String time = String.format("%02d:00", i);
                anotherTimesList.add(time);
            }
        });

        root.getChildren().addAll(home, dugmeNazad, monthComboBox, dayComboBox, timeComboBox,
                anotherComboBox, predmetComboBox, posalji, labelaGreska);

        Scene scene = new Scene(root, 900, 270);
        primarnaPozornica.setTitle("Zakazi Termin");
        primarnaPozornica.setScene(scene);
        primarnaPozornica.show();
    }

    public Pane kreirajPanel(JSONArray jsonArray, Timeline t, boolean zaTermine, String terminZaPretragu) {
        Pane panel = new Pane();
        Label label = new Label("Vec ste izabrali trenera");
        label.setLayoutX(1100);

        label.setLayoutY(500);
        label.setVisible(false);

        Button home = new Button("Idi na pocetnu");
        home.setLayoutX(880);
        home.setLayoutY(20);
        home.setOnAction(e -> {
            t.stop();
            pocetnaStrana();
        });

        Button dugmeUnazad = new Button("<-");
        dugmeUnazad.setLayoutX(20);
        dugmeUnazad.setLayoutY(20);
        dugmeUnazad.setOnAction(e -> {
            t.stop();
            pocetnaKorisnik();
        });

        Label labelaNema = new Label("Trenutno nema termina");
        //labelaNema.setFont(new Font(label.getFont().getName(), Font.PLAIN, 36));
        labelaNema.setVisible(true);
        labelaNema.setLayoutX(300);
        labelaNema.setLayoutY(150);

        int y = 80;

        for (Object o : jsonArray) {
            labelaNema.setVisible(false);

            JSONObject terminObj = (JSONObject) o;

            Label labelaMesec = new Label("Mesec: " + terminObj.get("mesec"));
            Label labelaDan = new Label("Dan: " + terminObj.get("dan"));
            Label labelaVremePoc = new Label("Vreme pocetka: " + terminObj.get("vreme_pocetka"));
            Label labelaVremeZav = new Label("Vreme zavrsetka: " + terminObj.get("vreme_zavrsetka"));
            Label labelaPredmet = new Label("Predmet: " + terminObj.get("naziv_predmeta"));
            Label labelaKorisnik = new Label("Korisnicko ime: " + terminObj.get("korisnicko_ime"));

            System.out.println(terminObj.get("mesec"));

            Button odobri = createChooseButton(terminObj.get("ID").toString(), false);
            odobri.setLayoutX(800);
            odobri.setLayoutY(y);

            Button odbij = createChooseButton(terminObj.get("ID").toString(), true);
            odbij.setLayoutX(910);
            odbij.setLayoutY(y);

            labelaMesec.setLayoutX(20);
            labelaMesec.setLayoutY(y);
            labelaDan.setLayoutX(110);
            labelaDan.setLayoutY(y);
            labelaVremePoc.setLayoutX(200);
            labelaVremePoc.setLayoutY(y);
            labelaVremeZav.setLayoutX(350);
            labelaVremeZav.setLayoutY(y);
            labelaPredmet.setLayoutX(500);
            labelaPredmet.setLayoutY(y);
            labelaKorisnik.setLayoutX(650);
            labelaKorisnik.setLayoutY(y);

            panel.getChildren().addAll(labelaMesec, labelaDan, labelaVremePoc, labelaVremeZav, labelaPredmet, labelaKorisnik);

            y += 25;

            if (zaTermine) {
                panel.getChildren().addAll(odobri, odbij);
            }
        }

        panel.getChildren().addAll(label, home, dugmeUnazad, labelaNema);
        return panel;
    }

    public void odobriZahteveForma() {
        Pane root = new Pane();

        ArrayList<Pane>listaPanela = new ArrayList<>();
        String korisnickoIme = null;

        if (osobaKontakt instanceof Korisnik) {
            Korisnik k = (Korisnik) osobaKontakt;
            korisnickoIme = k.getKorisnickoIme();
        }

        if (osobaKontakt instanceof Trener) {
            Trener tr = (Trener) osobaKontakt;
            korisnickoIme = tr.getKorisnickoIme();

        }
        ArrayList<String> koIme = new ArrayList<>();
        koIme.add(korisnickoIme);

        int delay = 1;
        int period = 1;

        final Timeline[] timer = {null}; // Use an array to hold a reference to the Timeline

        timer[0] = new Timeline(new KeyFrame(Duration.seconds(period), event -> {
            if (!listaPanela.isEmpty()) {

                root.getChildren().remove(listaPanela.get(0));

                listaPanela.remove(0);

            }
            root.getChildren().clear();
            String terminZa = ""; // Update with the appropriate value
            Pane panel2 = kreirajPanel(Termin.dohvatiTermineZaTrenera(koIme.get(0)), timer[0],true, terminZa); // Use korisnickoIme
            listaPanela.add(panel2);
            root.getChildren().add(panel2);
        }));

        timer[0].setCycleCount(Animation.INDEFINITE);
        timer[0].play();

        Scene scene = new Scene(root, 970, 500);
        primarnaPozornica.setTitle("Termini Korisnik");
        primarnaPozornica.setScene(scene);
        primarnaPozornica.show();
    }

    public void dodavanjePredmeta() {
        primarnaPozornica.setTitle("Dodavanje Predmeta");

        ArrayList<String> koIme = new ArrayList<>();
        String korisnickoIme = "";
        if (osobaKontakt instanceof Trener) {
            Trener tr = (Trener) osobaKontakt;
            korisnickoIme = tr.getKorisnickoIme();
        }
        koIme.add(korisnickoIme);

        TextField unos = new TextField();
        unos.setPromptText("Unesite naziv predmeta");
        unos.setLayoutX(165);
        unos.setLayoutY(120);

        Label labelaGreska = new Label("Greska pri unosu!");
        labelaGreska.setLayoutX(165);
        labelaGreska.setLayoutY(140);
        labelaGreska.setVisible(false);

        Button dodaj = new Button("Dodaj predmet");
        dodaj.setLayoutX(20);
        dodaj.setLayoutY(120);
        dodaj.setOnAction(e -> {
            String regex = ".*[a-zA-Z].*";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher1 = pattern.matcher(unos.getText());

            if (matcher1.matches()) {
                Predmet p = new Predmet(unos.getText(), koIme.get(0));
                p.upisiPredmet();
                labelaGreska.setVisible(false);
                unos.clear();
            } else {
                labelaGreska.setVisible(true);
            }
        });

        Button dugmeNazad = createButtonUnazad();
        dugmeNazad.setLayoutX(270);
        dugmeNazad.setLayoutY(20);

        Button home = new Button("Idi na pocetnu");
        home.setLayoutX(330);
        home.setLayoutY(20);
        home.setOnAction(e -> {
            primarnaPozornica.close();
            pocetnaStrana();
        });

        Pane root = new Pane();
        root.getChildren().addAll(dodaj, unos, labelaGreska, dugmeNazad, home);

        primarnaPozornica.setScene(new Scene(root, 450, 225));
        primarnaPozornica.show();
    }

    private void terminiKorisnik() {
        Pane root = new Pane();

        ArrayList<Pane>listaPanela = new ArrayList<>();
        String korisnickoIme = null;

        if (osobaKontakt instanceof Korisnik) {
            Korisnik k = (Korisnik) osobaKontakt;
            korisnickoIme = k.getKorisnickoIme();
        }

        int delay = 1;
        int period = 1;

        final Timeline[] timer = {null}; // Use an array to hold a reference to the Timeline

        timer[0] = new Timeline(new KeyFrame(Duration.seconds(period), event -> {
            if (!listaPanela.isEmpty()) {

                root.getChildren().remove(listaPanela.get(0));

                listaPanela.remove(0);

            }
            root.getChildren().clear();
            String terminZa = ""; // Update with the appropriate value
            Pane panel2 = kreirajPanel(osobaKontakt.dohvatiTermine(), timer[0],false, terminZa); // Use korisnickoIme
            listaPanela.add(panel2);
            root.getChildren().add(panel2);
        }));

        timer[0].setCycleCount(Animation.INDEFINITE);
        timer[0].play();

        Scene scene = new Scene(root, 970, 500);
        primarnaPozornica.setTitle("Termini Korisnik");
        primarnaPozornica.setScene(scene);
        primarnaPozornica.show();
    }

    public void terminiBezInterneta(Stage primaryStage) {
        Pane pane = new Pane();
        int y = 20;

        try {
            JSONArray terminiJson = (JSONArray) new JSONParser().parse(new FileReader("termini.json"));
            for (Object o : terminiJson) {
                JSONObject terminObj = (JSONObject) o;

                Label labelaMesec = new Label("Mesec: " + terminObj.get("mesec"));
                Label labelaDan = new Label("Dan: " + terminObj.get("dan"));
                Label labelaVremePoc = new Label("Vreme pocetka: " + terminObj.get("vreme_pocetka"));
                Label labelaVremeZav = new Label("Vreme zavrsetka: " + terminObj.get("vreme_zavrsetka"));
                Label labelaPredmet = new Label("Predmet: " + terminObj.get("naziv_predmeta"));
                Label labelaKorisnik = new Label("Korisnicko ime: " + terminObj.get("korisnicko_ime"));

                System.out.println(terminObj.get("mesec"));

                labelaMesec.setLayoutX(20);
                labelaMesec.setLayoutY(y);

                labelaDan.setLayoutX(110);
                labelaDan.setLayoutY(y);

                labelaVremePoc.setLayoutX(200);
                labelaVremePoc.setLayoutY(y);

                labelaVremeZav.setLayoutX(350);
                labelaVremeZav.setLayoutY(y);

                labelaPredmet.setLayoutX(500);
                labelaPredmet.setLayoutY(y);

                labelaKorisnik.setLayoutX(650);
                labelaKorisnik.setLayoutY(y);

                y += 25;
            }

        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException | ParseException ex) {
            System.out.println(ex.getMessage());
        }

        Scene scene = new Scene(pane, 1000, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Termini Bez Interneta");
        primaryStage.show();
    }
}