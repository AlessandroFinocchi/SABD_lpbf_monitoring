# Use official Python 3.10 base image
FROM python:3.10-slim

# Set the working directory in the container
WORKDIR /app

# Copy requirements.txt into the container
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of your application code
COPY client_ref.py .

# Set the default command
CMD ["python3", "client_ref.py", "http://micro-challenger:8866"]
