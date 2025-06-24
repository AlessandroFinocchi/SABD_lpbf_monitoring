package it.uniroma2;

import it.uniroma2.entities.BatchTile;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestSourceFunction implements SourceFunction<BatchTile> {
    private volatile boolean isRunning = true;
    private final String url;
    private final String benchId;
    private final int limit;

    public RestSourceFunction(String url, int limit) {
        this.url = url;
        this.limit = limit;

        try {
            this.benchId = getBenchId();
            startBench();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private String getBenchId() throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("apitoken", "polimi-deib");
        payload.put("name", "unoptimized");
        payload.put("test", true);
        payload.put("max_batches", limit);

        // Make the POST request
        URL apiUrl = new URL(url + "/api/create");
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Write the JSON payload to the request body
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString().replaceAll("\"", "");
        }
    }

    private void startBench() throws Exception {
        URL apiUrl = new URL(url + "/api/start/" + benchId);
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        if (conn.getResponseCode() != 200) {
            throw new IOException("Failed to start the bench");
        }
    }

    @Override
    public void run(SourceContext<BatchTile> sourceContext) throws Exception {

    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
