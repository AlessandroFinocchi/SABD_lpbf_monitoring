package it.uniroma2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.uniroma2.entities.rest.RESTResponse;
import it.uniroma2.entities.rest.BenchConfig;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GcRestController {
    private static final String  BENCH_API_TOKEN = "polimi-deib";
    private static final String  BENCH_NAME      = "unoptimized";
    private static final int     BENCH_LIMIT     = 3600;
    private static final boolean BENCH_TEST      = false;

    private static final String URL = "http://localhost:8866";
//    private static final String URL = "http://micro-challenger:8866";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_MSGPACK = "application/x-msgpack";

    public static String getBenchId() throws Exception {
        BenchConfig config = new BenchConfig(BENCH_API_TOKEN, BENCH_NAME, BENCH_LIMIT, BENCH_TEST);

        URL apiUrl = new URL(URL + "/api/create");
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
        connection.setDoOutput(true);

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

    public static void startBench(String benchId) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(URL + "/api/start/"+benchId).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        if (conn.getResponseCode() != 200) throw new IOException("Failed to start the bench");
    }

    public static RESTResponse getBatch(String benchId) throws IOException {
        URL obj = new URL(URL + "/api/next_batch/" + benchId);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_MSGPACK);
        connection.setDoOutput(true);

        if (connection.getResponseCode() == 404) return null;  // No more batches

        try (InputStream in = connection.getInputStream()) {
            byte[] response = in.readAllBytes();
            ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
            RESTResponse b = objectMapper.readValue(response, RESTResponse.class);
            return b;
        }
    }
}
