package com.example.projekt;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

/**
 * A kliens oldali grafikus felületért felelős osztály.
 * A szerverrel való kommunikációért felelős.
 */
public class Client extends Application{
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static boolean loggedin=false;
    private static int port=37737;
    private static String host;
    private static String messeges;
    private static String to="ALL";
    private static Vector<String> users=new Vector<String>();

    /**
     * A kliens oldali grafikus felületért felelős metódus.
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Bejelentkezés");
        stage.setWidth(450);
        stage.setHeight(800);
        connection(stage);
    }

    /**
     * Kezdő felület.
     * Bekéri a szerver hostját.
     * Csatlakozás gombbal lehet csatlakozni a szerverhez.
     */
    public static void connection(Stage stage){
        GridPane bas = new GridPane();
        bas.setAlignment(Pos.TOP_CENTER);
        bas.setHgap(10);
        bas.setVgap(10);
        bas.setPadding(new Insets(25, 25, 25, 25));

        Label gchat = new Label("GCHAT");
        gchat.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: green; -fx-font-family: 'Comic Sans MS';");
        bas.add(gchat, 0, 0);

        Text scenetitle = new Text("Üdvözöljük");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        bas.add(scenetitle, 0, 20, 2, 1);

        TextField hoste = new TextField();
        bas.add(hoste, 1,21);

        Label phost = new Label("Host:");
        bas.add(phost, 0, 21);

        Button btn = new Button("Csatlakozás");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        bas.add(hbBtn, 1, 24);

        btn.setOnAction(e -> {
            host=hoste.getText();
            basic(stage);
        });
        stage.setScene(new Scene(bas));
        stage.show();
    }

    /**
     * Bejelentkezési/regisztrációs felület.
     * Ki lehet választani hogy bejelentkezünk vagy regisztrálunk.
     */
    public static void basic(Stage stage){
        GridPane bas = new GridPane();
        bas.setAlignment(Pos.TOP_CENTER);
        bas.setHgap(10);
        bas.setVgap(10);
        bas.setPadding(new Insets(25, 25, 25, 25));

        Label gchat = new Label("GCHAT");
        gchat.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: green; -fx-font-family: 'Comic Sans MS';");
        bas.add(gchat, 0, 0);

        Text scenetitle = new Text("Üdvözöljük");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        bas.add(scenetitle, 0, 20, 2, 1);

        Button btn = new Button("Bejelentkezés");
        HBox hbBtn = new HBox(10);

        Button btn2 = new Button("Regisztráció");
        HBox hbBtn2 = new HBox(10);

        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        hbBtn2.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn2.getChildren().add(btn2);
        bas.add(hbBtn2, 0, 24);
        bas.add(hbBtn, 1, 24);

        stage.setScene(new Scene(bas));
        stage.show();
        btn.setOnAction(e -> login(stage,"Bejelentkezés"));
        btn2.setOnAction(e -> register(stage));
    }

    /**
     * Bejeletkezési felület.
     * Bekéri a felhasználónevet és a jelszót.
     * Ha sikeres a bejelentkezés akkor a chat felületre lépünk.
     * @param text Üzenet a bejeletkezésről.
     */
    public static void login(Stage stage, String text){
        GridPane login = new GridPane();
        login.setAlignment(Pos.TOP_CENTER);
        login.setHgap(10);
        login.setVgap(10);
        login.setPadding(new Insets(25, 25, 25, 25));

        Label gchat = new Label("GCHAT");
        gchat.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: green; -fx-font-family: 'Comic Sans MS';");
        login.add(gchat, 0, 0);

        Text scenetitle = new Text(text);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        login.add(scenetitle, 0, 20, 2, 1);

        Label userName = new Label("Felhasználónév:");
        login.add(userName, 0, 21);

        TextField userTextField = new TextField();
        login.add(userTextField, 1, 21);

        Label pw = new Label("Jelszó:");
        login.add(pw, 0, 22);

        PasswordField pwBox = new PasswordField();
        login.add(pwBox, 1, 22);

        Button btn = new Button("Bejelentkezés");
        HBox hbBtn = new HBox(10);
        Button btn2 = new Button("Regisztráció");
        HBox hbBtn2 = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        hbBtn2.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn2.getChildren().add(btn2);
        login.add(hbBtn2, 0, 24);
        login.add(hbBtn, 1, 24);
        stage.setScene(new Scene(login, 300, 600));
        stage.show();
        btn.setOnAction(event -> {
            String userNameText = userTextField.getText();
            String passwordText = pwBox.getText();
            cliens("LOGIN",userNameText,passwordText,stage);
        });
        btn2.setOnAction(event -> register(stage));
    }

    /**
     * Regisztrációs felület.
     * Bekéri a felhasználónevet és a jelszót.
     * Ha sikeres a regisztráció akkor a bejelentkezési felületre lépünk.
     */
    public static void register(Stage stage){
        GridPane regis = new GridPane();
        regis.setAlignment(Pos.TOP_CENTER);
        regis.setHgap(10);
        regis.setVgap(10);
        regis.setPadding(new Insets(25, 25, 25, 25));

        Label gchat = new Label("GCHAT");
        gchat.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: green; -fx-font-family: 'Comic Sans MS';");
        regis.add(gchat, 0, 0);

        Text scenetitle = new Text("Regisztráció");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        regis.add(scenetitle, 0, 20, 2, 1);
        Label userName = new Label("Felhasználónév:");
        regis.add(userName, 0, 21);

        TextField userTextField = new TextField();
        regis.add(userTextField, 1, 21);

        Label pw = new Label("Jelszó:");
        regis.add(pw, 0, 22);

        PasswordField pwBox = new PasswordField();
        regis.add(pwBox, 1, 22);

        Label pw2 = new Label("Jelszó még egyszer:");
        regis.add(pw2, 0, 23);

        PasswordField pwBox2 = new PasswordField();
        regis.add(pwBox2, 1, 23);
        Button btn = new Button("Bejelentkezés");
        HBox hbBtn = new HBox(10);
        Button btn2 = new Button("Regisztráció");
        HBox hbBtn2 = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        hbBtn2.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn2.getChildren().add(btn2);
        regis.add(hbBtn2, 0, 24);
        regis.add(hbBtn, 1, 24);
        stage.setScene(new Scene(regis, 300, 600));
        stage.show();
        btn2.setOnAction(event -> {
            String userNameText = userTextField.getText();
            String passwordText = pwBox.getText();
            String passwordText2 = pwBox2.getText();

            // itt ellenőrizhetjük az adatok helyességét
            if (passwordText.equals(passwordText2)) {
                scenetitle.setText("Sikeres regisztráció!");
                cliens("REGISTER",userNameText,passwordText,stage);
            } else {
                scenetitle.setText("Hibás felhasználónév vagy jelszó!");
            }
        });
        btn.setOnAction(event -> login(stage,"Bejelentkezés"));
    }

    /**
     * A szerverhez való csatlakozás.
     * Itt kezeljük a bejelnentkezés és a regisztráció sikerességét.
     * Ha sikeres a bejelentkezés akkor a lobby felületre lépünk.
     * Ha sikeres a regisztráció akkor a bejelentkezési felületre lépünk.
     * @param type LOGIN vagy REGISTER
     * @param username felhasználónév
     * @param password jelszó
     */
    public static void cliens(String type,String username, String password,Stage stage){
        try {
            try {
                socket = new Socket(host, port);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            readout(type);
            readout(username);
            readout(password);
            String input = in.readLine();
            if (type.equals("LOGIN") && input.equals("true")) {
                System.out.println("Sikeres bejelentkezés");
                lobby(stage, username);
                loggedin = true;
            }else if (type.equals("REGISTER") && input.equals("true")) {
                System.out.println("Sikeres regisztráció");
                login(stage,"Sikeres regisztráció, jelentkezz be!");
            }else if (type.equals("LOGIN") && input.equals("false")) {
                System.out.println("Sikertelen bejelentkezés");
                login(stage,"Sikertelen bejelentkezés");
            } else {
                System.out.println("Sikertelen regisztráció");
                register(stage);
            }

        }
        catch (IOException e){
            System.out.println("Hiba a kommunikációban a szerverrel");
        }
    }
    /**
     * A chat fő ablaka.
     * Itt kezeljük a chat üzeneteket.
     * Itt kommunikálunk a szerverrel leginkább.
     * van lehetőség: kijelentkezésre ekkor visszalépünk a bejelentkezési felületre.
     * üzenetek küldésére.
     * a chat frissitésére
     * ki tudjuk választani, hogy kinek szeretnénk üzenni.
     */
    public static void lobby(Stage stage,String username) {
        GridPane lobby = new GridPane();
        lobby.setAlignment(Pos.TOP_LEFT);
        lobby.setHgap(10);
        lobby.setVgap(10);
        lobby.setPadding(new Insets(25, 25, 25, 25));

        Label gchat = new Label("GCHAT");
        gchat.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: green; -fx-font-family: 'Comic Sans MS';");
        lobby.add(gchat, 0, 0,10,1);


        Label user = new Label(username);
        user.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        lobby.add(user, 20, 0,7,1);

        Label tos = new Label(to);
        tos.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        lobby.add(tos, 5, 2,10,1);

        TextField userTextField = new TextField();
        GridPane.setConstraints(userTextField, 3, 53, 15, 3);
        lobby.getChildren().add(userTextField);

        TextArea chat = new TextArea();
        chat.setEditable(false);
        GridPane.setConstraints(chat, 3, 3, 20, 50);
        lobby.getChildren().add(chat);

        Button btn = new Button("Kilépés");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        GridPane.setConstraints(hbBtn, 20, 1,10,1);
        lobby.add(hbBtn, 20, 1,10,1);

        Button btn2 = new Button("Küldés");
        HBox hbBtn2 = new HBox(10);
        hbBtn2.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn2.getChildren().add(btn2);
        GridPane.setConstraints(hbBtn2, 20, 55,10,1);
        lobby.getChildren().add(hbBtn2);

        Button btn3 = new Button("Frissités");
        HBox hbBtn3 = new HBox(10);
        hbBtn3.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn3.getChildren().add(btn3);
        lobby.add(hbBtn3, 20, 2,10,1);
        GridPane.setConstraints(hbBtn3, 20, 2);

        Button btn4 = new Button("Felhasználók");
        HBox hbBtn4 = new HBox(10);
        hbBtn4.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn4.getChildren().add(btn4);
        GridPane.setConstraints(hbBtn4, 0, 3);
        lobby.getChildren().add(hbBtn4);

        ContextMenu contextMenu = new ContextMenu();
        users.add("ALL");
        readout("USER");
        readout("ALL");
        try {
            messeges = in.readLine();
            String inp = messeges.replaceAll(";", "\n");
            chat.setText(inp);
            readout("LOBBY");
            String us= in.readLine();
            String[] users2 = us.split(";");
            for(String user2 : users2){
                users.add(user2);
            }

            for(String user2 : users){
                MenuItem item = new MenuItem(user2);
                contextMenu.getItems().add(item);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            btn.setOnAction(event -> {
                readout("LOGOUT");
                login(stage,"Sikeres kijelentkezés");
                loggedin = false;
                users.clear();
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            btn2.setOnAction(event -> {
                readout("CHAT");
                readout(to);
                readout(userTextField.getText());
                userTextField.clear();
                try {
                    readout("USER");
                    readout(to);
                    String input = in.readLine();
                    String inp = input.replaceAll(";", "\n");
                    chat.setText(inp);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            btn3.setOnAction(event -> {
                try {
                    readout("USER");
                    readout(to);
                    String input = in.readLine();
                    String inp = input.replaceAll(";", "\n");
                    chat.setText(inp);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        btn4.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                } else {
                    contextMenu.show(btn4, Side.LEFT, 0, 0);
                }
            }
        });
        EventHandler<ActionEvent>menuItemHandler=event->{
          MenuItem item=(MenuItem)event.getSource();
            String user2=item.getText();
            to=user2;
            tos.setText(to);
            readout("USER");
            readout(user2);
            String input;
            try {
                input = in.readLine();
                String inp = input.replaceAll(";", "\n");
                chat.setText(inp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        };
        for(MenuItem item : contextMenu.getItems()){
            item.setOnAction(menuItemHandler);
        }

        stage.setScene(new Scene(lobby, 300, 600));
        stage.show();
    }
    /**
     * Az outputstream kiírásáért felelős metódus.
     * @param oute A kiírandó szöveg.
     */
    public static void readout(String oute){
        out.print(oute+"\r\n");
        out.flush();

    }
    
    public static void main(String[] args) {
        launch();
    }
}