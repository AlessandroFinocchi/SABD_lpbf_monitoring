# SABD project: Monitoring defects in L-PBF manufacturing

## Usage
1. First, unzip _micro-challenger/gc25cdocker.zip_ in the same directory of the zip file.

2. Secondly, compose docker services with `make gen`. It's also possible 
   to compose docker services with scaling parameter
    - num_taskmanagers $\in$ \{ $1, 2, \dots$ \}
    ```
    make gen_s TM=<num_taskmanagers>
    ```

3. Before executing the queries, make sure the directories 
   `metrics` and `results` are empty to avoid mixing up results. 
   If they aren't, then execute inside one of the taskmanagers
   and the jobmanager
    ```
   rm -Rf /metrics/* && rm -Rf /results/*
    ```
   
4. Execute flink query with `make q`

5. Eventually compose down docker services `make clean`
