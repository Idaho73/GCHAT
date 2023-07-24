package com.example.projekt;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A szerver főosztálya, ami létrehozza a szerver socketet, és a kliensek csatlakozását kezeli.
 */

public class MainServer implements Runnable{
    public static final int PORT_NUMBER = 37737;
    protected ServerSocket serverSocket;
    public MainServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);

    }
    /**
     * A szerver főciklusa, ami a kliensek csatlakozását kezeli.
     */
    @Override
    public void run() {
        try{
            datab();
            while(!Thread.currentThread().isInterrupted()){
                Server server = new Server(serverSocket.accept());
                new Thread(server).start();
            }
        }
        catch (IOException e){
            System.out.println("Accept failed!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try{
            serverSocket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("A Szerver leáll");
    }

    /**
     * A szerver adatbázisának létrehozása.
     * @throws SQLException
     */
    protected void datab() throws SQLException {
        String url= "jdbc:sqlite:usersdb.db";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            /*String drop = "DROP TABLE users";
            stmt.execute(drop);
            String drop2 = "DROP TABLE chat";
            stmt.execute(drop2);*/
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (\n"
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "username TEXT NOT NULL,\n"
                    + "password TEXT NOT NULL\n"
                    + ");";
            String createchatTable = "CREATE TABLE IF NOT EXISTS chat (\n"
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "username TEXT NOT NULL,\n"
                    + "address TEXT NOT NULL,\n"
                    + "message TEXT NOT NULL\n"
                    + ");";
            stmt.execute(createchatTable);
            stmt.execute(createUsersTable);
        }
    }

    /**
     * A szerver főosztályának main függvénye, ami elindítja a szerver főciklusát.
     * @throws IOException
     */
    public static void main(String[] args){
        try {
            new Thread(new MainServer(PORT_NUMBER)).start();
            System.out.println("A Szerver elindult");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
