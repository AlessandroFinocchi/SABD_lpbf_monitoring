package it.uniroma2.entities.rest;

import org.json.JSONObject;

public class BenchConfig {
    private final String apiToken;
    private final String name;
    private final boolean test;
    private final int maxBatches;

    public BenchConfig(String apiToken, String name, int maxBatches, boolean test) {
        this.apiToken = apiToken;
        this.name = name;
        this.maxBatches = maxBatches;
        this.test = test;
    }

    @Override
    public String toString() {
        return "BenchConfig{" +
                "\"apiToken\"=\"" + apiToken + "\"," +
                "\"name\"=\"" + name + "\"," +
                "\"maxBatches\"=" + maxBatches + ","+
                "\"test\"=" + test +
                '}';
    }

    public JSONObject toJson() {
        JSONObject payload = new JSONObject();
        payload.put("apitoken", getApiToken());
        payload.put("name", getName());
        payload.put("test", getTest());
        payload.put("max_batches", this.getMaxBatches());

        return payload;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getName() {
        return name;
    }

    public int getMaxBatches() {
        return maxBatches;
    }

    public boolean getTest() {
        return test;
    }
}
