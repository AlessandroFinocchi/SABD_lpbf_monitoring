# SABD project: Monitoring defects in L-PBF manufacturing

## Usage
1. First, unzip _micro-challenger/gc25cdocker.zip_ in the same directory of the zip file.

2. Secondly, compose docker services with `make gen`. It's also possible 
   to compose docker services with scaling parameter
    - num_taskmanagers $\in$ \{ $1, 2, \dots$ \}
    ```
    make gen_s TM=<num_taskmanagers>
    ```

3. Before executing the queries, execute in local with superuser privileges:
    ```
   rm -Rf results/queries/* performance_analyzer/input/* performance_analyzer/output/*
    ```

4. Execute flink query with `make q`

5. Benchmark flink query with `make b`

6. Eventually compose down docker services `make clean`
