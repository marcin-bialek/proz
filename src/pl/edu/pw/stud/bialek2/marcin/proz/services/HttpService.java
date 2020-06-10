package pl.edu.pw.stud.bialek2.marcin.proz.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;


public class HttpService {
    public static void asyncGet(String address, Function<String, Void> callback) {
        CompletableFuture.runAsync(new Runnable(){
            @Override
            public void run() {
                HttpService.get(address, callback);
            }
        });
    }    

    public static void get(String address, Function<String, Void> callback) {
        try {
            final URL url = new URL(address);
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);

            final InputStreamReader inputReader = new InputStreamReader(connection.getInputStream());
            final BufferedReader reader = new BufferedReader(inputReader);
            final StringBuilder builder = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();
            connection.disconnect();
            callback.apply(builder.toString());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
