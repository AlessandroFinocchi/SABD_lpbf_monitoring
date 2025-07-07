import os
import pandas as pd


INPUT_DIR = "../results/metrics/metrics_row"
OUTPUT_DIR = "../results/metrics/metrics_agg"
OUTPUT_NAME = "_agg.csv"
INPUT_PREFIX = "metrics_"

if __name__ == "__main__":
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    # List all immediate subdirectories in the root directory
    subdirs = [os.path.join(INPUT_DIR, d) for d in os.listdir(INPUT_DIR)
               if os.path.isdir(os.path.join(INPUT_DIR, d))]

    # Collect unique CSV filenames across all subdirectories
    csv_files = set()
    for sd in subdirs:
        for f in os.listdir(sd):
            if f.lower().endswith('.csv'):
                csv_files.add(f)

    # Define aggregation functions per column
    AGG_FUNCS = {
        ' final_throughput': 'mean',
        ' latency_min': 'min',
        ' latency_mean': 'mean',
        ' latency_p99': 'max',
        ' latency_max': 'max'
    }

    # Process each CSV file
    for filename in sorted(csv_files):
        summary_records = []
        for sd in subdirs:
            file_path = os.path.join(sd, filename)
            if os.path.exists(file_path):
                # Read CSV and drop run_id
                df = pd.read_csv(file_path)
                if 'run_id' in df.columns:
                    df = df.drop(columns=['run_id'])
                # Apply specified aggregation functions only on matching columns
                available_funcs = {col: func for col, func in AGG_FUNCS.items() if col in df.columns}
                if not available_funcs:
                    continue
                agg_values = df.agg(available_funcs)
                record = agg_values.to_dict()
                # Tag with subdirectory name
                record['subdir'] = os.path.basename(sd)
                summary_records.append(record)

        if summary_records:
            # Build DataFrame with subdir first
            agg_df = pd.DataFrame(summary_records)
            cols = ['subdir'] + [c for c in agg_df.columns if c != 'subdir']
            agg_df = agg_df[cols]
            # Save aggregated summary file
            out_path = os.path.join(OUTPUT_DIR, filename)
            agg_df.to_csv(out_path, index=False)
            print(f"Aggregated summary for {filename}: {len(summary_records)} entries -> {out_path}")
        else:
            print(f"No aggregatable data for {filename}")

