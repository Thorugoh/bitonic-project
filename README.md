# Bitonic Sequence API

A simple Clojure API that generates a Bitonic Sequence of length N from a given range [l, r].

## Prerequisites

* Docker
* Docker Compose
* Leiningen (for local development/testing)

## Running the Application

1.  **Start the services (API + Redis):**
    ```sh
    docker-compose build
    docker-compose up
    ```
    The API will be available at `http://localhost:3000`.

2.  **Run Tests (Optional, for local development):**
    ```sh
    lein test
    ```

## API Usage

Make a GET request to the `/bitonic` endpoint with the query parameters `n`, `l`, and `r`.

### Example Request

```sh
curl "http://localhost:3000/bitonic?n=5&l=3&r=10"