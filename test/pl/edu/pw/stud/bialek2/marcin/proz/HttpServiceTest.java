package pl.edu.pw.stud.bialek2.marcin.proz;

import pl.edu.pw.stud.bialek2.marcin.proz.services.HttpService;

import org.junit.Assert;
import org.junit.Test;


public class HttpServiceTest {
    public static final String ADDRESS = "https://postman-echo.com/status/200";
    public static final String EXPECTED = "{\"status\":200}";

    @Test
    public void testGet() {
        HttpService.get(ADDRESS, response -> {
            Assert.assertEquals(EXPECTED, response.trim());
            return null;
        });
    }    

    @Test 
    public void testAsyncGet() {
        HttpService.asyncGet(ADDRESS, response -> {
            Assert.assertEquals(EXPECTED, response.trim());
            return null;
        });
    }

}

