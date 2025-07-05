package it.uniroma2.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.uniroma2.entities.query.Centroid;
import it.uniroma2.entities.query.TileQ3;
import it.uniroma2.entities.rest.*;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static it.uniroma2.entities.GeneralConfig.*;

public class GcRestController {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_MSGPACK = "application/x-msgpack";

    public static synchronized String getBenchId() throws Exception {
        RESTBenchConfigRequest config = new RESTBenchConfigRequest(BENCH_API_TOKEN, BENCH_NAME, BENCH_LIMIT, BENCH_TEST);

        URL apiUrl = new URL(CHALLENGER_URL + "/api/create");
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
        HttpURLConnection conn = (HttpURLConnection) new URL(CHALLENGER_URL + "/api/start/" + benchId).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        if (conn.getResponseCode() != 200) throw new IOException("Failed to start the bench");
    }

    public static RESTBatchResponse getBatch(String benchId) throws IOException {
        URL obj = new URL(CHALLENGER_URL + "/api/next_batch/" + benchId);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_MSGPACK);
        connection.setDoOutput(true);

        if (connection.getResponseCode() == 404) return null;  // No more batches

        try (InputStream in = connection.getInputStream()) {
            byte[] response = in.readAllBytes();
            ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
            RESTBatchResponse b = objectMapper.readValue(response, RESTBatchResponse.class);
            return b;
        }
    }

    public static RESTPostResultResponse postResult(TileQ3 input, int query, String benchId) throws Exception {
        List<RESTCentroid> restCentroidList = new ArrayList<>();

        for(Centroid c: input.getCentroids()) {
            RESTCentroid rc = new RESTCentroid(c.getX(), c.getY(), c.getPoints());
            restCentroidList.add(rc);
        }

        RESTResultRequest rr = new RESTResultRequest(
                input.getSeqID(),
                query,
                input.getPrintID(),
                input.getTileID(),
                input.getSaturatedPoints(),
                restCentroidList
        );

        URL obj = new URL(CHALLENGER_URL + "/api/result/" + query + "/" + benchId + "/" + input.getSeqID());
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_MSGPACK);
        connection.setDoOutput(true);

        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        byte[] msgpack = objectMapper.writeValueAsBytes(rr);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(msgpack);
        } catch (IOException e) {
            System.out.println("Failed to write body: " + connection.getResponseCode());
            throw new IOException();
        }

        if (connection.getResponseCode() != 200) {
            System.out.println("Failed to send result: HTTP " + connection.getResponseCode());
            throw new IOException();
        }

        try (InputStream in = connection.getInputStream()) {
            byte[] response = in.readAllBytes();
            ObjectMapper objectMapper2 = new ObjectMapper();
            return objectMapper2.readValue(response, RESTPostResultResponse.class);
        }
    }

    public static RESTEndResponse endBench(String benchId) throws Exception {
        URL url = new URL(CHALLENGER_URL + "/api/end/" + benchId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
        conn.setDoOutput(true);

        if (conn.getResponseCode() != 200) throw new IOException("Failed to end the bench");

        try (InputStream in = conn.getInputStream()) {
            byte[] response = in.readAllBytes();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
            return objectMapper.readValue(response, RESTEndResponse.class);
        }

    }

}
