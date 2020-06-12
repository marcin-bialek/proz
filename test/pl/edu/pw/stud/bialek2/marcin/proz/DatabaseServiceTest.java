package pl.edu.pw.stud.bialek2.marcin.proz;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageType;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.models.TextMessage;
import pl.edu.pw.stud.bialek2.marcin.proz.services.DatabaseService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.DatabaseServiceDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityService;

import java.io.File;
import java.security.KeyPair;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;


public class DatabaseServiceTest {
    public static final String DATABASE_FILENAME = "test.db";

    public Peer makePeer(String nick, String address, int port) {
        final KeyPair pair = SecurityService.generateKeyPair();
        return new Peer(0, nick, address, port, pair.getPublic());
    }

    @After
    public void deleteDatabaseFile() {
        final File file = new File(DATABASE_FILENAME);
        file.delete();
    }
    
    @Test
    public void testLoadAndClose() {
        final DatabaseService service = new DatabaseService(new DatabaseServiceDelegate(){
            @Override
            public void databaseServiceSQLError() {
                Assert.assertTrue(false);
            }
        });

        service.load(DATABASE_FILENAME, null);
        service.close();
    }

    @Test
    public void testInsertAndGetPeer() {
        final DatabaseService service = new DatabaseService(new DatabaseServiceDelegate(){
            @Override
            public void databaseServiceSQLError() {
                Assert.assertTrue(false);
            }
        });

        final SecretKey key = SecurityService.generateSecretKey();
        service.load(DATABASE_FILENAME, key);

        final Peer peer1 = this.makePeer("foo", "192.168.1.12", 1234);
        final Peer peer2 = this.makePeer("bar", "192.168.1.13", 2345);
        service.insertPeer(peer1);
        service.insertPeer(peer2);

        final ArrayList<Peer> peers = service.getPeers();
        Assert.assertEquals(2, peers.size());

        final int a = peers.get(0).getNick().equals("foo") ? 0 : 1;
        final int b = a == 0 ? 1 : 0;

        Assert.assertEquals("foo", peers.get(a).getNick());
        Assert.assertEquals("192.168.1.12", peers.get(a).getAddress());
        Assert.assertEquals(1234, peers.get(a).getPort());

        Assert.assertEquals("bar", peers.get(b).getNick());
        Assert.assertEquals("192.168.1.13", peers.get(b).getAddress());
        Assert.assertEquals(2345, peers.get(b).getPort());

        service.close();
    }

    @Test
    public void testInsertAndGetMessages() {
        final DatabaseService service = new DatabaseService(new DatabaseServiceDelegate(){
            @Override
            public void databaseServiceSQLError() {
                Assert.assertTrue(false);
            }
        });

        final SecretKey key = SecurityService.generateSecretKey();
        service.load(DATABASE_FILENAME, key);

        final Peer peer1 = this.makePeer("foo", "192.168.1.12", 1234);
        final Peer peer2 = this.makePeer("bar", "192.168.1.13", 2345);
        service.insertPeer(peer1);
        service.insertPeer(peer2);

        final String text1 = "Tempor velit amet culpa fugiat in.";
        final Message message1 = new TextMessage(peer1, false, text1);
        service.insertMessage(message1);
        
        final String text2 = "Velit adipisicing veniam non dolor eu consequat minim.";
        final Message message2 = new TextMessage(peer2, false, text2);
        service.insertMessage(message2);

        final ArrayList<Message> messages1 = service.getMessagesFor(peer1);
        Assert.assertEquals(1, messages1.size());
        Assert.assertEquals(MessageType.TEXT_MESSAGE, messages1.get(0).getType());
        Assert.assertEquals(text1, ((TextMessage)messages1.get(0)).getText());
        
        final ArrayList<Message> messages2 = service.getMessagesFor(peer2);
        Assert.assertEquals(1, messages2.size());
        Assert.assertEquals(MessageType.TEXT_MESSAGE, messages2.get(0).getType());
        Assert.assertEquals(text2, ((TextMessage)messages2.get(0)).getText());

        service.close();
    }

    @Test 
    public void testDeleteMessages() {
        final DatabaseService service = new DatabaseService(new DatabaseServiceDelegate(){
            @Override
            public void databaseServiceSQLError() {
                Assert.assertTrue(false);
            }
        });

        final SecretKey key = SecurityService.generateSecretKey();
        service.load(DATABASE_FILENAME, key);

        final Peer peer1 = this.makePeer("foo", "192.168.1.12", 1234);
        final Peer peer2 = this.makePeer("bar", "192.168.1.13", 2345);
        service.insertPeer(peer1);
        service.insertPeer(peer2);

        final Message message1 = new TextMessage(peer1, false, "Tempor velit amet culpa fugiat in.");
        final Message message2 = new TextMessage(peer2, false, "Velit adipisicing veniam non dolor eu consequat minim.");
        service.insertMessage(message1);
        service.insertMessage(message2);

        service.deleteMessagesFor(peer1);
        Assert.assertEquals(0, service.getMessagesFor(peer1).size());
        Assert.assertEquals(1, service.getMessagesFor(peer2).size());

        service.deleteMessagesFor(peer2);
        Assert.assertEquals(0, service.getMessagesFor(peer1).size());
        Assert.assertEquals(0, service.getMessagesFor(peer2).size());

        service.close();
    }

    @Test 
    public void testDeletePeer() {
        final DatabaseService service = new DatabaseService(new DatabaseServiceDelegate(){
            @Override
            public void databaseServiceSQLError() {
                Assert.assertTrue(false);
            }
        });

        final SecretKey key = SecurityService.generateSecretKey();
        service.load(DATABASE_FILENAME, key);

        final Peer peer1 = this.makePeer("foo", "192.168.1.12", 1234);
        final Peer peer2 = this.makePeer("bar", "192.168.1.13", 2345);
        service.insertPeer(peer1);
        service.insertPeer(peer2);

        service.deletePeer(peer1);
        Assert.assertEquals(1, service.getPeers().size());
        Assert.assertEquals("bar", service.getPeers().get(0).getNick());

        service.deletePeer(peer2);
        Assert.assertEquals(0, service.getPeers().size());

        service.close();
    }

}
