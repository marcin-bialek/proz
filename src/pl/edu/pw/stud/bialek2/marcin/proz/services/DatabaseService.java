package pl.edu.pw.stud.bialek2.marcin.proz.services;

import java.security.PublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageFactory;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageType;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;


public class DatabaseService {
    private final static String[] INIT_QUERIES = {
        "CREATE TABLE IF NOT EXISTS peers (" +
        "   id              INTEGER PRIMARY KEY AUTOINCREMENT," +
        "   nick            VARCHAR NOT NULL," +
        "   address         VARCHAR(15) NOT NULL," +
        "   port            INT NOT NULL," +
        "   public_key      VARBINARY(1024) NOT NULL" +
        ");",

        "CREATE TABLE IF NOT EXISTS messages (" +
        "   id              INTEGER PRIMARY KEY AUTOINCREMENT," +
        "   peer_id         INTEGER REFERENCES peers(id) NOT NULL," + 
        "   type            TINYINT NOT NULL," +
        "   value           MEDIUMBLOB NOT NULL," +
        "   incoming        BOOLEAN," +
        "   timestamp       TIMESTAMP NOT NULL" +
        ");",
    };

    private final static String INSERT_PEER_QUERY = 
        "INSERT INTO peers (" + 
        "   nick,           " +
        "   address,        " + 
        "   port,           " + 
        "   public_key      " + 
        ") VALUES (?, ?, ?, ?);";

    private final static String INSERT_MESSAGE_QUERY = 
        "INSERT INTO messages (" +
        "   peer_id,           " + 
        "   type,              " + 
        "   value,             " + 
        "   incoming,          " +
        "   timestamp          " + 
        ") VALUES (?, ?, ?, ?, ?);";

    private DatabaseServiceDelegate delegate;
    private Connection connection;
    
    public DatabaseService(DatabaseServiceDelegate delegate) {
        this.delegate = delegate;
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
            this.delegate.databaseServiceSQLError();
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
                this.delegate.databaseServiceSQLError();
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
            statement.setString(2, peer.getAddress());
            statement.setInt(3, peer.getPort());
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
            this.delegate.databaseServiceSQLError();
        }
    }

    public ArrayList<Peer> getPeers() {
        final ArrayList<Peer> peers = new ArrayList<>();

        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM peers;");
            ResultSet result = statement.executeQuery();

            while(result.next()) {
                final int id = result.getInt(1);
                final String nick = result.getString(2);
                final String address = result.getString(3);
                final int port = result.getInt(4);
                final PublicKey publicKey = SecurityService.generatePublicKey(result.getBytes(5));
                peers.add(new Peer(id, nick, address, port, publicKey));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            this.delegate.databaseServiceSQLError();
        }

        return peers;
    }

    public void insertMessage(final Message message) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(INSERT_MESSAGE_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, message.getPeer().getId()); 
            statement.setInt(2, message.getType().getValue());
            statement.setBytes(3, message.getValueAsBytes());
            System.out.println("insert message: " + SecurityService.byteArray2HexString(message.getValueAsBytes()));
            statement.setBoolean(4, message.isIncoming());
            statement.setTimestamp(5, Timestamp.valueOf(message.getTimestamp()));
            statement.execute();

            final ResultSet set = statement.getGeneratedKeys();
            
            if(set.next()) {
                message.setId(set.getInt(1));
            }
            else {
                throw new SQLException("Creating message failed, no id obtained.");
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            this.delegate.databaseServiceSQLError();
        }
    }

    public ArrayList<Message> getMessagesFor(Peer peer) {
        final ArrayList<Message> messages = new ArrayList<>();

        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM messages WHERE peer_id = ?;");
            statement.setInt(1, peer.getId());
            ResultSet result = statement.executeQuery();

            while(result.next()) {
                final int id = result.getInt(1);
                final MessageType type = MessageType.fromValue(result.getInt(3));
                final byte[] value = result.getBytes(4);
                System.out.println("read message: " + SecurityService.byteArray2HexString(value));
                final boolean incoming = result.getBoolean(5);
                final LocalDateTime timestamp = result.getTimestamp(6).toLocalDateTime();
                messages.add(MessageFactory.createMessage(type, id, peer, incoming, timestamp, value));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            this.delegate.databaseServiceSQLError();
        }

        return messages;
    }
}

