package it.uniroma2.client;

import okhttp3.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;
import java.io.IOException;

public class GCProducer {

    private static final String API_URL = "http://localhost:8866";
    private static final String TOPIC = "benches";
    private static final String BOOTSTRAP_SERVERS = "kafka:9092";

    private static final OkHttpClient httpClient = new OkHttpClient();

    public static void main(String[] args) {
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        kafkaProps.put("key.serializer", StringSerializer.class.getName());
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        try (KafkaProducer<String, byte[]> producer = new KafkaProducer<>(kafkaProps)) {

            String benchId = createBench();
            System.out.println("Created bench: " + benchId);

            post("/api/start/" + benchId);

            int i = 0;
            while (true) {
                Response response = get("/api/next_batch/" + benchId);
                if (response.code() == 404) {
                    System.out.println("No more batches.");
                    break;
                }
                byte[] batchBytes = response.body().bytes();
                System.out.println("✅ Got batch " + i + ", sending to Kafka...");
                producer.send(new ProducerRecord<>(TOPIC, batchBytes));
                i++;
            }

            post("/api/end/" + benchId);
            System.out.println("✅ Finished producing all batches!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createBench() throws IOException {
        String json = "{\"apitoken\":\"polimi-deib\",\"name\":\"unoptimized\",\"test\":true}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(API_URL + "/api/create")
                .post(body)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string().replace("\"", "");
        }
    }

    private static Response get(String path) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + path)
                .get()
                .build();
        return httpClient.newCall(request).execute();
    }

    private static void post(String path) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + path)
                .post(RequestBody.create(new byte[0], null))
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("POST failed: " + response.code());
            }
        }
    }
}
