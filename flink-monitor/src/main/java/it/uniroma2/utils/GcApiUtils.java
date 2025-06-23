package it.uniroma2.utils;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class GcApiUtils {
    public static void main(String[] args) {
        try {
            // Endpoint API
            URL url = new URL("http://localhost:8866/api/create");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Configurazione richiesta
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Corpo della richiesta JSON
            String jsonInput = "{\"apitoken\": \"abcdefgd\", \"test\": true}";

            // Invia i dati
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            // Leggi la risposta
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
            }

            // Stampa o salva il bench_id
            String benchId = response.toString();
            System.out.println("bench_id: " + benchId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
