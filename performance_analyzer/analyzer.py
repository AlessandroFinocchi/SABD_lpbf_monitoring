import math
import os

INPUT_DIR = "input/"
OUTPUT_DIR = "output/"
OUTPUT_NAME = "_all.csv"
INPUT_PREFIX = "metrics_"
MAX_LIMIT = 3600


def analyze(input_file, output_file, limit=MAX_LIMIT):
    run_id = input_file.name.split("run_")[-1].split(".")[0]
    print(run_id)

    processed = 0
    latency_total = 0
    latency_min = math.inf
    latency_max = 0
    latencies = []
    final_throughput = 0

    for line in input_file.readlines()[1:]:
        if limit is not None and processed >= limit:
            break

        batch_id, timestamp, throughput, latency = line.split(",")
        batch_id = int(batch_id)
        timestamp = float(timestamp)
        throughput = float(throughput)
        latency = float(latency)

        latency_total += latency
        final_throughput = throughput
        latency_min = min(latency_min, latency)
        latency_max = max(latency_max, latency)
        latencies.append(latency)

        processed += 1

    input_file.close()

    latency_mean = latency_total / processed
    latency_p99 = sorted(latencies)[int(len(latencies) * 0.99)]

    output_file.write(
        f"{run_id}, {final_throughput:.6f}, {latency_min:.6f}, {latency_mean:.6f}, {latency_p99:.6f}, {latency_max:.6f}\n"
    )


if __name__ == "__main__":
    list_steps=["preprocess", "q1", "q2", "q3"]
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    for step in list_steps:
        step_files:list = [f for f in os.listdir(INPUT_DIR) if f.startswith(INPUT_PREFIX+step)]
        if step_files:
            output_file = open(OUTPUT_DIR + step + OUTPUT_NAME, "w")

            output_file.write(
                "run_id, final_throughput, latency_min, latency_mean, latency_p99, latency_max\n"
            )

            for file in step_files:
                if file.endswith(".csv"):
                    with open(INPUT_DIR + file, "r") as input_file:
                        analyze(input_file, output_file)

            output_file.close()
