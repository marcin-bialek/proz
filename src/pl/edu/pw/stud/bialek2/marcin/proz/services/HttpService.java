package pl.edu.pw.stud.bialek2.marcin.proz.services;

// import java.net.URI;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.time.Duration;
import java.util.function.Function;


public class HttpService {
    public static void get(String address, Function<String, Void> callback) {
        // try {
        //     final HttpRequest request = HttpRequest
        //         .newBuilder(URI.create(address))
        //         .timeout(Duration.ofSeconds(30))
        //         .GET()
        //         .build();

        //     final HttpClient client = HttpClient.newHttpClient();

        //     client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
        //         callback.apply(response.body());
        //         return null;
        //     });
        // }
        // catch(Exception e) {
        //     e.printStackTrace();
        // }
        callback.apply("");
    }    
}
