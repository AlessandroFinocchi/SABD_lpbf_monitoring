import os

STEP = "challenger"
INPUT_DIR = "input/"
OUTPUT_DIR = "output/"
OUTPUT_NAME = "_all.csv"
INPUT_PREFIX = "metrics_"

def aggregate(input_file, output_file):
    run_id = input_file.name.split("run_")[-1].split(".")[0]
    print(run_id)

    line = input_file.readline().strip()
    throughput, latency_min, latency_mean, latency_p99, latency_max = line.split(",")
    throughput = float(throughput)
    latency_min = int(latency_min)
    latency_mean = int(latency_mean)
    latency_p99 = int(latency_p99)
    latency_max = int(latency_max)

    input_file.close()

    output_file.write(
        f"{run_id}, {throughput:.6f}, {latency_min}, {latency_mean}, {latency_p99}, {latency_max}\n"
    )

if __name__ == "__main__":
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    step_files:list = [f for f in os.listdir(INPUT_DIR) if f.startswith(INPUT_PREFIX+STEP)]

    if step_files:
        output_file = open(OUTPUT_DIR + STEP + OUTPUT_NAME, "w")

        output_file.write(
            "run_id, final_throughput, latency_mean, latency_min, latency_max, latency_p99\n"
        )

        for file in step_files:
            if file.endswith(".csv"):
                with open(INPUT_DIR + file, "r") as input_file:
                    aggregate(input_file, output_file)

        output_file.close()