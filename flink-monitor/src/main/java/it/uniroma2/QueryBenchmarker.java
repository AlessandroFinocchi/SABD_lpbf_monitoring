package it.uniroma2;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class QueryBenchmarker {
    private static final int RUN_NUM = 5;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < RUN_NUM; i++) {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(16);
            QueryExecutor.executeQueries(env, i+1);
        }
    }
}
