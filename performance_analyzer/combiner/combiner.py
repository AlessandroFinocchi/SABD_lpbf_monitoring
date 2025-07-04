import os
import pandas as pd

INPUT_DIR = "input/"
OUTPUT_FILE = "all.csv"
entries = {}


def find_csv_files(directory):
    csv_files = []
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".csv"):
                csv_files.append(os.path.join(root, file))
    return csv_files


def import_file(input):
    print(input)
    tm = input.split("/")[1].split("_")[1]
    parallel = input.split("/")[1].split("_")[2]
    mode = input.split("/")[1].split("_")[3]

    phase = input.split("/")[2].split("_")[0]

    input_file = open(input, "r")
    for run in input_file.readlines()[1:]:
        run_id = int(run.split(", ")[0])
        key = (tm, parallel, mode, run_id)

        entry = entries.get(key)
        if entry is None:
            entries[key] = {}
            entries[key]["run_id"] = run_id
            entries[key]["tm"] = tm
            entries[key]["parallel"] = parallel
            entries[key]["mode"] = mode

        entries[key][phase + "_throughput"] = float(run.split(", ")[1])
        entries[key][phase + "_latency_min"] = float(run.split(", ")[2])
        entries[key][phase + "_latency_mean"] = float(run.split(", ")[3])
        entries[key][phase + "_latency_p99"] = float(run.split(", ")[4])
        entries[key][phase + "_latency_max"] = float(run.split(", ")[5])


if __name__ == "__main__":

    input_files = find_csv_files(INPUT_DIR)
    for input in input_files:
        import_file(input)

    df = pd.DataFrame(entries.values())

    df["key"] = df["tm"] + "_" + df["parallel"] + "_" + df["mode"]
    df.sort_values(by=["run_id", "key", "tm", "parallel", "mode"], inplace=True)

    df["q1_only_latency_min"] = df["q1_latency_min"] - df["preprocess_latency_min"]
    df["q1_only_latency_mean"] = df["q1_latency_mean"] - df["preprocess_latency_mean"]
    df["q1_only_latency_p99"] = df["q1_latency_p99"] - df["preprocess_latency_p99"]
    df["q1_only_latency_max"] = df["q1_latency_max"] - df["preprocess_latency_max"]

    df["q2_only_latency_min"] = df["q2_latency_min"] - df["q1_latency_min"]
    df["q2_only_latency_mean"] = df["q2_latency_mean"] - df["q1_latency_mean"]
    df["q2_only_latency_p99"] = df["q2_latency_p99"] - df["q1_latency_p99"]
    df["q2_only_latency_max"] = df["q2_latency_max"] - df["q1_latency_max"]

    df["q3_only_latency_min"] = df["q3_latency_min"] - df["q2_latency_min"]
    df["q3_only_latency_mean"] = df["q3_latency_mean"] - df["q2_latency_mean"]
    df["q3_only_latency_p99"] = df["q3_latency_p99"] - df["q2_latency_p99"]
    df["q3_only_latency_max"] = df["q3_latency_max"] - df["q2_latency_max"]

    columns = [
        "run_id",
        "key",
        "tm",
        "parallel",
        "mode",
        "preprocess_throughput",
        "q1_throughput",
        "q2_throughput",
        "q3_throughput",
        "challenger_throughput",
        "preprocess_latency_min",
        "preprocess_latency_mean",
        "preprocess_latency_p99",
        "preprocess_latency_max",
        "q1_latency_min",
        "q1_latency_mean",
        "q1_latency_p99",
        "q1_latency_max",
        "q2_latency_min",
        "q2_latency_mean",
        "q2_latency_p99",
        "q2_latency_max",
        "q3_latency_min",
        "q3_latency_mean",
        "q3_latency_p99",
        "q3_latency_max",
        "challenger_latency_min",
        "challenger_latency_mean",
        "challenger_latency_p99",
        "challenger_latency_max",
        "q1_only_latency_min",
        "q1_only_latency_mean",
        "q1_only_latency_p99",
        "q1_only_latency_max",
        "q2_only_latency_min",
        "q2_only_latency_mean",
        "q2_only_latency_p99",
        "q2_only_latency_max",
        "q3_only_latency_min",
        "q3_only_latency_mean",
        "q3_only_latency_p99",
        "q3_only_latency_max",
    ]

    if os.path.exists(OUTPUT_FILE):
        os.remove(OUTPUT_FILE)
    df.to_csv(OUTPUT_FILE, index=False, columns=columns)
