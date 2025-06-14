# SABD project: Monitoring defects in L-PBF manufacturing

## Notes
* Docker is deployed in application-mode since the project is not a general
deployment platform but a specific one

## Usage
1. First, Compose docker services with `make gen`. It's also possible 
   to compose docker services with scaling parameter
    - num_taskmanagers $\in$ \{ $1, 2, \dots$ \}
    ```
    make gen_s <num_taskmanagers>
    ```

2. Eventually compose down docker services `make clean`
