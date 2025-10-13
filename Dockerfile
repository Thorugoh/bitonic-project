FROM clojure:lein-2.11.2

# Set the working directory
WORKDIR /usr/src/app

# Copy the project file and download dependencies
COPY project.clj .
RUN lein deps

# Copy the rest of the application code
COPY . .

# Expose the port the app runs on
EXPOSE 3000