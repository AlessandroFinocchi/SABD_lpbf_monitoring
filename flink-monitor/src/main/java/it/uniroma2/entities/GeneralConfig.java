package it.uniroma2.entities;

import it.uniroma2.QueryExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GeneralConfig {
    private static final String CONFIG_FILE = "config.properties";

    public static final int PARALLELISM_LEV;
    public static final String  BENCH_API_TOKEN;
    public static final String  BENCH_NAME;
    public static final int     BENCH_LIMIT;
    public static final boolean BENCH_TEST;

    public static final String CHALLENGER_URL;

    public static final String RESULT_QUERY_DIR;
    public static final String RESULT_QUERY_FILENAME_PREFIX;
    public static final String RESULT_METRICS_DIR;
    public static final String RESULT_METRICS_FILENAME_PREFIX;

    public static final int RUN_NUM;

    static {
        try(InputStream in = QueryExecutor.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)){
            Properties props = new Properties();
            props.load(in);
            PARALLELISM_LEV = Integer.parseInt(props.getProperty("parallelism.level"));
            BENCH_API_TOKEN = props.getProperty("bench.api.token");
            BENCH_NAME = props.getProperty("bench.name");
            BENCH_LIMIT = Integer.parseInt(props.getProperty("bench.limit"));
            BENCH_TEST = Boolean.parseBoolean(props.getProperty("bench.test"));
            CHALLENGER_URL = props.getProperty("challenger.url");
            RESULT_QUERY_DIR = props.getProperty("result.query.directory");
            RESULT_QUERY_FILENAME_PREFIX = props.getProperty("result.query.filename.prefix");
            RESULT_METRICS_DIR = props.getProperty("result.metrics.directory");
            RESULT_METRICS_FILENAME_PREFIX = props.getProperty("result.metrics.filename.prefix");
            RUN_NUM = Integer.parseInt(props.getProperty("results.metrics.run_num"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Impossible loading " + CONFIG_FILE + ": " + e.getMessage());
        }
    }
}
