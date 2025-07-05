package it.uniroma2;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static it.uniroma2.entities.GeneralConfig.PARALLELISM_LEV;

public class QueryBenchmarker {
    private static final int RUN_NUM = 5;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < RUN_NUM; i++) {
            System.out.println("Executing run " + i);
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(PARALLELISM_LEV);
            QueryExecutor.executeQueries(env, i+1);
        }
    }
}
