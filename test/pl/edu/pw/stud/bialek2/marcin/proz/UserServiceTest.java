package pl.edu.pw.stud.bialek2.marcin.proz;

import org.junit.Test;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import pl.edu.pw.stud.bialek2.marcin.proz.models.User;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserServiceDelegate;


public class UserServiceTest {

    @Test
    public void testCreateUser() {
        final HashMap<String, String> values = new HashMap<>();

        final UserService service = new UserService(new UserServiceDelegate() {
            public void userServiceWrongPassword() {
                Assert.assertTrue(false);
            }
            public void userServiceNeedsUser() {
                Assert.assertTrue(false);
            }
            public void userServiceNeedsPassword() {
                Assert.assertTrue(false);
            }
            public void userServiceDidLoadUser(User user) {
                Assert.assertTrue(false);
            }
            public void userServiceDidCreateUser(User user) { 
                values.put("nick", user.getNick());
                values.put("port", "" + user.getPort());
                values.put("dbfilename", "" + user.getDBFilename());
            }
        }, this.getClass().getName());

        service.createUser("foo", new char[] { '1', '2', '3' }, 1234, "foo.db");

        Assert.assertEquals("foo", values.get("nick"));
        Assert.assertEquals("1234", values.get("port"));
        Assert.assertEquals("foo.db", values.get("dbfilename"));
    }

    @Test
    public void testLoadUser() {
        final HashMap<String, String> values = new HashMap<>();

        final UserService service = new UserService(new UserServiceDelegate() {
            public void userServiceWrongPassword() {
                Assert.assertTrue(false);
            }
            public void userServiceNeedsUser() {
                Assert.assertTrue(false);
            }
            public void userServiceNeedsPassword() {
                Assert.assertTrue(false);
            }
            public void userServiceDidLoadUser(User user) {
                values.put("nick", user.getNick());
                values.put("port", "" + user.getPort());
                values.put("dbfilename", "" + user.getDBFilename());
            }
            public void userServiceDidCreateUser(User user) {}
        }, this.getClass().getName());

        final char[] password =  new char[] { '1', '2', '3' };
        service.createUser("foo", password, 1234, "foo.db");
        service.loadUser(password);

        Assert.assertEquals("foo", values.get("nick"));
        Assert.assertEquals("1234", values.get("port"));
        Assert.assertEquals("foo.db", values.get("dbfilename"));
    }

    @Test
    public void testDeleteUser() {
        final UserService service = new UserService(new UserServiceDelegate() {
            public void userServiceWrongPassword() {
                Assert.assertTrue(false);
            }
            public void userServiceNeedsUser() {}
            public void userServiceNeedsPassword() {
                Assert.assertTrue(false);
            }
            public void userServiceDidLoadUser(User user) {
                Assert.assertTrue(false);
            }
            public void userServiceDidCreateUser(User user) {}
        }, this.getClass().getName());

        service.createUser("foo", new char[] { '1', '2', '3' }, 1234, "foo.db");
        service.deleteUser();
        
        new UserService(new UserServiceDelegate() {
            public void userServiceWrongPassword() {
                Assert.assertTrue(false);
            }
            public void userServiceNeedsUser() {}
            public void userServiceNeedsPassword() {
                Assert.assertTrue(false);
            }
            public void userServiceDidLoadUser(User user) {
                Assert.assertTrue(false);
            }
            public void userServiceDidCreateUser(User user) {
                Assert.assertTrue(false);
            }
        }, this.getClass().getName()).loadUser(null);
    }

}

