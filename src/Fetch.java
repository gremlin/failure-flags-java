import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Fetch {
    public static void main(String[] args) {
        String name = "your_experiment_name";

        try {
            String result = fetchExperiment(name);
            System.out.println("Experiment Result: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fetchExperiment(String name) throws IOException {
        String url = "http://localhost:5032/experiment";
        String payload = String.format("{\"name\": \"%s\", \"labels\": {}}", name);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                if (statusCode >= 200 && statusCode < 300 && entity != null) {
                    // If the response is successful and has a valid entity, parse the content
                    return EntityUtils.toString(entity, StandardCharsets.UTF_8);
                } else {
                    // Handle error response
                    throw new IOException("HTTP status code: " + statusCode + ", message: " + response.getStatusLine().getReasonPhrase());
                }
            }
        }
    }
}
