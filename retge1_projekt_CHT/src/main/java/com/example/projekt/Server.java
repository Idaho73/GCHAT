package com.example.projekt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;
import java.util.Vector;

/**
 * A szerver egy kliensének a kezeléséért felelős osztály.
 * A klienssel való kommunikációért felelős.
 * A kliens kéréseit kezeli.
 */
public class Server extends Thread{
    private static Socket Clientsocket;
    private static Connection conn;
    private BufferedReader serverInput;
    private PrintWriter serverOutput;
    private String clientusername;
    private static Vector<String> loggedin=new Vector<String>();

    /**
     * A klienssel való kommunikációért felelős konstruktor.
     * Létrehozza a szükséges bemeneti és kimeneti csatornákat.
     * @param Clientsocket A kliens socketje.
     * @throws IOException
     */
    public Server(Socket Clientsocket) throws IOException {
        this.Clientsocket = Clientsocket;
        serverInput = new BufferedReader(new InputStreamReader(Clientsocket.getInputStream()));
        serverOutput = new PrintWriter(Clientsocket.getOutputStream());

    }

    /**
     * Az outputstream kiírásáért felelős metódus.
     * @param out A kiírandó szöveg.
     */
    protected void readout(String out){

        serverOutput.print(out+"\r\n");
        serverOutput.flush();

    }

    /**
     * A Klenssel való kommunikációért felelős metódus.
     * A kliens kéréseit kezeli.
     */
    public void run() {
        try {
            String input;
            while ((input = serverInput.readLine()) != null) {
                if (input.equals("LOGIN")) {
                    logIn();
                }
                if (input.equals("REGISTER")) {
                    register();
                }
                if (input.equals("LOBBY")) {
                    lobby();
                }
                if (input.equals("CHAT")) {
                    chat();
                }
                if(input.equals("USER")){
                    users();
                }
                if(input.equals("LOGOUT")){
                    loggedin.remove(clientusername);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Betölti a felhasználókat.
     */
    protected synchronized void lobby(){
            try {
                database();
                String sql2 = "SELECT * FROM users";
                Statement stmt = conn.createStatement();
                ResultSet rs2 = stmt.executeQuery(sql2);
                String users = "";
                while (rs2.next()) {
                    users += rs2.getString("username") + ";";
                }
                readout(users);
                close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    /**
     * Megynitja az adatbázist.
     */
    protected synchronized void database(){
        try{
            String url= "jdbc:sqlite:usersdb.db";
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    /**
     * Bezárja az adatbázist.
     */
    protected synchronized void close(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * kezelni a felhasználók üzeneteit.
     * chat táblába menti a felhasználók üzeneteit.
     * @throws SQLException
     */
    protected void chat() {
        try {
            String to = serverInput.readLine();
            String message = serverInput.readLine();
        try  {
                database();
                String sql = "INSERT INTO chat(username,address,message) VALUES(?,?,?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, clientusername);
                pstmt.setString(2, to);
                pstmt.setString(3, message);
                pstmt.executeUpdate();
                close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Ellenörzi hogy az adott felhasználónév létezik-e.
     * Ellenőrzi hogy az adott felhasználónév bevan-e már jelentkezve.
     * Ha igen akkor nem engedi bejelnkezni.
     */
    protected void logIn() {

        try {
            String username = serverInput.readLine();
            String password = serverInput.readLine();
            for(int i=0;i<loggedin.size();i++){
                if(loggedin.get(i).equals(username)){
                    readout("false");
                    serverOutput.println("true");
                    return;
                }
            }
            try  {
                database();
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    readout("true");
                    loggedin.add(username);
                    this.clientusername = username;
                } else {
                    readout("false");
                }
                close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ellenőrzi hogy az adott felhasználónév létezik-e.
     * Ha igen akkor nem engedi regisztrálni.
     * Ha nem akkor regisztrálja.
     * A regisztrált felhasználókat betölti a users táblába.
     * @throws SQLException
     */
    protected synchronized void register() throws SQLException {
        try {
            String username = serverInput.readLine();
            String password = serverInput.readLine();
            try {
                database();
                if (!valid(username)) {
                    readout("false");
                    return;
                }
                String insert1 = "INSERT INTO users(username,password) VALUES(?,?)";
                PreparedStatement pstmt = conn.prepareStatement(insert1);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.executeUpdate();
                readout("true");
                close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ellenőrzi hogy az adott felhasználónév létezik-e.
     * @param username
     * @return
     */
    protected boolean valid(String username){
        database();
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Betölti az adott felhasználó üzeneteit.
     * @throws SQLException
     */
    protected void users(){
        try {
            String to=serverInput.readLine();

        try{
            database();
            if(to.equals("ALL")){
                String sql2="SELECT * FROM chat WHERE address = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setString(1, to);
                ResultSet rs2 = pstmt2.executeQuery();
                String users="";
                while(rs2.next()){
                    users+=rs2.getString("username")+": "+rs2.getString("message")+";";
                }
                readout(users);
            }
            else {
                String sql = "SELECT * FROM chat WHERE (username = ? AND address = ?) OR (username = ? AND address = ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, clientusername);
                pstmt.setString(2, to);
                pstmt.setString(3, to);
                pstmt.setString(4, clientusername);
                ResultSet rs = pstmt.executeQuery();
                String chat = "";
                while (rs.next()) {
                    chat += rs.getString("username") + ": " + rs.getString("message") + ";";
                }
                readout(chat);
            }
            close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}