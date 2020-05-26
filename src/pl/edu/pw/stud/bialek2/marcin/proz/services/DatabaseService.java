package pl.edu.pw.stud.bialek2.marcin.proz.services;

import java.security.PublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;


public class DatabaseService {
    private final static String INSERT_PEER_QUERY = "INSERT INTO peers (nick, last_address, last_port, public_key) VALUES (?, ?, ?, ?);";
    private final static String INSERT_CHATROOM_QUERY = "INSERT INTO chatrooms (uuid, name) VALUES (?, ?);";
    private final static String[] INIT_QUERIES = {
        "CREATE TABLE IF NOT EXISTS peers (" +
        "   id              INTEGER PRIMARY KEY AUTOINCREMENT," +
        "   nick            VARCHAR NOT NULL," +
        "   last_address    VARCHAR(15) NOT NULL," +
        "   last_port       INT NOT NULL," +
        "   public_key      VARBINARY(1024) NOT NULL" +
        ");",

        "CREATE TABLE IF NOT EXISTS chatrooms (" +
        "   id              INTEGER PRIMARY KEY AUTOINCREMENT," +
        "   uuid            VARCHAR(64) UNIQUE NOT NULL," +
        "   name            VARCHAR NOT NULL" +
        ");",

        "CREATE TABLE IF NOT EXISTS peer_chatroom (" +
        "   id              INTEGER PRIMARY KEY AUTOINCREMENT," +
        "   peer_id         INTEGER REFERENCES peers(id) ON DELETE CASCADE," +
        "   chatroom_id     INTEGER REFERENCES chatrooms(id) ON DELETE CASCADE," +
        "   UNIQUE(peer_id, chatroom_id)" +
        ");",

        "CREATE TABLE IF NOT EXISTS messages (" +
        "   id              INTEGER PRIMARY KEY AUTOINCREMENT," +
        "   peer_chatroom_id INTEGER REFERENCES peer_chatroom(id) NOT NULL," +
        "   type            TINYINT NOT NULL," +
        "   value           MEDIUMBLOB NOT NULL," +
        "   timestamp       TIMESTAMP NOT NULL" +
        ");",
    };

    private DatabaseServiceListener listener;
    private Connection connection;
    
    public DatabaseService(DatabaseServiceListener listener) {
        this.listener = listener;
    }

    public void load(String path) {
        try {
            if(this.connection != null) {
                this.connection.close();
            }

            this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            this.initTables();
        } 
        catch (SQLException e) {
            e.printStackTrace();
            this.listener.databaseServiceSQLError();
        }
    }

    public void close() {
        if(this.connection != null) {
            try {
                this.connection.close();
                this.connection = null;
            }
            catch(SQLException e) {
                e.printStackTrace();
                this.listener.databaseServiceSQLError();
            }
        }
    }

    private void initTables() throws SQLException {
        Statement statement = this.connection.createStatement();

        for(String q : INIT_QUERIES) {
            statement.addBatch(q);
        }

        statement.executeBatch();
    }

    public void insertPeer(final Peer peer) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(INSERT_PEER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, peer.getNick());
            statement.setString(2, peer.getLastAddress());
            statement.setInt(3, peer.getLastPort());
            statement.setBytes(4, peer.getPublicKey().getEncoded());
            statement.execute();

            final ResultSet set = statement.getGeneratedKeys();
            
            if(set.next()) {
                peer.setId(set.getInt(1));
            }
            else {
                throw new SQLException("Creating peer failed, no id obtained.");
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            this.listener.databaseServiceSQLError();
        }
    }

    public ArrayList<Peer> getPeers() {
        final ArrayList<Peer> peers = new ArrayList<>();

        try {
            Statement statement = this.connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM peers;");

            while(result.next()) {
                final int id = result.getInt(1);
                final String nick = result.getString(2);
                final String lastAddress = result.getString(3);
                final int lastPort = result.getInt(4);
                final PublicKey publicKey = SecurityService.generatePublicKey(result.getBytes(5));
                peers.add(new Peer(id, nick, lastAddress, lastPort, publicKey));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            this.listener.databaseServiceSQLError();
        }

        return peers;
    }

    public void insertChatroom(final Chatroom chatroom) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(INSERT_CHATROOM_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, chatroom.getUuid().toString());
            statement.setString(2, chatroom.getName());
            statement.execute();

            final ResultSet set = statement.getGeneratedKeys();
            
            if(set.next()) {
                chatroom.setId(set.getInt(1));
            }
            else {
                throw new SQLException("Creating chatroom failed, no id obtained.");
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            this.listener.databaseServiceSQLError();
        }
    }

    public ArrayList<Chatroom> getChatrooms() {
        final ArrayList<Chatroom> chatrooms = new ArrayList<>();

        try {
            Statement statement = this.connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM chatrooms;");

            while(result.next()) {
                final int id = result.getInt(1);
                final UUID uuid = UUID.fromString(result.getString(2));
                final String name = result.getString(3);
                chatrooms.add(new Chatroom(id, uuid, name));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            this.listener.databaseServiceSQLError();
        }

        return chatrooms;
    }
}

