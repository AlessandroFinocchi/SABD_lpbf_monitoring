import os
import pandas as pd

BASE = "metrics_4_16_kernel"
INPUT_DIR = f"input/{BASE}/input/"
OUTPUT_FILE = f"{BASE}_batches.csv"
entries = {}


def find_csv_files(directory):
    csv_files = []
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".csv") and "_1.csv" in file and "challenger" not in file:
                csv_files.append(os.path.join(root, file))
    return csv_files


def import_file(input):
    print(input)
    
    phase = input.split("/")[3].split("_")[1]

    input_file = open(input, "r")
    for run in input_file.readlines():
        batch_id = int(run.split(", ")[0])
        key = (batch_id)

        entry = entries.get(key)
        if entry is None:
            entries[key] = {}
            entries[key]["batch_id"] = batch_id

        entries[key][phase + "_timestamp"] = float(run.split(", ")[1])
        entries[key][phase + "_throughput"] = float(run.split(", ")[2])
        entries[key][phase + "_latency_mean"] = float(run.split(", ")[3])


if __name__ == "__main__":

    input_files = find_csv_files(INPUT_DIR)
    print(input_files)
    for input in input_files:
        import_file(input)

    df = pd.DataFrame(entries.values())

    # df["key"] = df["tm"] + "_" + df["parallel"] + "_" + df["mode"]

    # df["q1_only_latency_min"] = df["q1_latency_min"] - df["preprocess_latency_min"]
    # df["q1_only_latency_mean"] = df["q1_latency_mean"] - df["preprocess_latency_mean"]
    # df["q1_only_latency_p99"] = df["q1_latency_p99"] - df["preprocess_latency_p99"]
    # df["q1_only_latency_max"] = df["q1_latency_max"] - df["preprocess_latency_max"]

    # df["q2_only_latency_min"] = df["q2_latency_min"] - df["q1_latency_min"]
    # df["q2_only_latency_mean"] = df["q2_latency_mean"] - df["q1_latency_mean"]
    # df["q2_only_latency_p99"] = df["q2_latency_p99"] - df["q1_latency_p99"]
    # df["q2_only_latency_max"] = df["q2_latency_max"] - df["q1_latency_max"]

    # df["q3_only_latency_min"] = df["q3_latency_min"] - df["q2_latency_min"]
    # df["q3_only_latency_mean"] = df["q3_latency_mean"] - df["q2_latency_mean"]
    # df["q3_only_latency_p99"] = df["q3_latency_p99"] - df["q2_latency_p99"]
    # df["q3_only_latency_max"] = df["q3_latency_max"] - df["q2_latency_max"]

    columns = [
        "batch_id",
        "phase",
        "preprocess_throughput",
        "q1_throughput",
        "q2_throughput",
        "q3_throughput",
        "preprocess_latency_mean",
        "q1_latency_mean",
        "q2_latency_mean",
        "q3_latency_mean"
    ]

    df.sort_values(by=["batch_id"], inplace=True)

    if os.path.exists(OUTPUT_FILE):
        os.remove(OUTPUT_FILE)
    df.to_csv(OUTPUT_FILE, index=False)
