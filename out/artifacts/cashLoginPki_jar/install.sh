#!/bin/bash

# Get the path to the JAR file
jar_file_path="cashLoginPki.jar"
root="cashLogin"

# Check if the directory exists
if [ ! -d "$HOME/$root" ]; then
  # The directory does not exist, so create it
  mkdir "$HOME/$root"
  echo "Root directory created successfully."
else
  # The directory already exists
  echo "Root directory is already created"
fi

cp "$jar_file_path" "$HOME/$root"
sudo cp "$root" "/usr/local/bin/"

sudo chmod +x "/usr/local/bin/$root"

# Reload the PATH environment variable
source ~/.bashrc
