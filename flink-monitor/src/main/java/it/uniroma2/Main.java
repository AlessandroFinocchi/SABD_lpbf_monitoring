package it.uniroma2;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.uniroma2.entities.rest.BatchResponse;
import it.uniroma2.entities.rest.BenchConfig;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_MSGPACK = "application/x-msgpack";

    private static String getBenchId() throws Exception {
        String url = "http://localhost:8866"; // Replace with your actual URL
        int limit = 3600; // Replace with the desired limit

        // Prepare JSON payload
        BenchConfig config = new BenchConfig("polimi-deib", "unoptimized", limit, false);

        // Make the POST request
        URL apiUrl = new URL(url + "/api/create");
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
        connection.setDoOutput(true);

        // Write the JSON payload to the request body
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = config.toJson().toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) response.append(responseLine.trim());
            return response.toString().replaceAll("\"", "");
        }
    }

    private static void startBench(String benchId) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL("http://localhost:8866/api/start/"+benchId).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        if (conn.getResponseCode() != 200) throw new IOException("Failed to start the bench");
    }

    private static byte[] getBatch(String benchId) throws IOException {
            URL obj = new URL("http://localhost:8866/api/next_batch/" + benchId);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(CONTENT_TYPE, APPLICATION_MSGPACK);
            connection.setDoOutput(true);

            if (connection.getResponseCode() == 404) return null;  // No more batches

            try (InputStream in = connection.getInputStream()) { return in.readAllBytes(); }
    }

    public static void main(String[] args) throws Exception {
        String benchId = getBenchId();
        System.out.println("benchId: " + benchId);
        startBench(benchId);

        for(int i = 0; i < 64; i++) {
            byte[] batch = getBatch(benchId);

            if(batch == null) {
                System.out.println(i + "-th loop jumped");
                continue;
            }

            ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
            BatchResponse response = objectMapper.readValue(batch, BatchResponse.class);
        }
    }
}