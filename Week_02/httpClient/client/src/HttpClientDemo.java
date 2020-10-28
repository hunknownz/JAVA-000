import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClientDemo {
    static HttpClient httpClient = HttpClient.newBuilder().build();

    public static void main(String[] args) throws Exception {
        String url = "http://localhost:8081";
        HttpRequest request = HttpRequest.newBuilder(new URI(url))
        .header("Accept", "*/*").timeout(Duration.ofSeconds(10)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String s = response.body();
        System.out.println(s);
    }
}
