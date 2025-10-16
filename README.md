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

## Architecture & Libraries

### Core Dependencies

This project uses the following Clojure libraries:

- **org.httpkit.server**: High-performance, async HTTP server for Clojure
- **compojure**: Routing library for Ring-based web applications with clean DSL
- **taoensso.carmine**: Redis client for Clojure
- **ring.middleware.params**: Middleware to automatically parse query parameters
- **cheshire**: Fast JSON encoding/decoding library for Clojure

### System Architecture


- **API Layer**: HTTP-kit server handling incoming requests
- **Business Logic**: Bitonic sequence generation algorithm
- **Caching Layer**: Redis for storing computed results (1-hour TTL)

## API Documentation

### Base URL
```
http://localhost:3000
```

### Endpoints

#### GET /bitonic
Generates a bitonic sequence of specified length using integers from given range.

**Query Parameters:**
- `n` (required): Integer > 2, length of desired bitonic sequence
- `l` (required): Integer, lower bound of range (inclusive)
- `r` (required): Integer, upper bound of range (exclusive)

**Response Format:** `application/json`

**Success Response (200 OK):**
```json
{
  "n": number,     // requested sequence length
  "l": number,     // lower bound used
  "r": number,     // upper bound used  
  "result": number[] // bitonic sequence or [-1] if impossible
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Error description"
}
```

### API Examples

**Valid Request:**
```sh
curl "http://localhost:3000/bitonic?n=5&l=1&r=4"
```
```json
{"n":5,"l":1,"r":4,"result":[1,2,3,2,1]}
```

**Impossible Sequence:**
```sh
curl "http://localhost:3000/bitonic?n=10&l=1&r=3"
```
```json
{"n":10,"l":1,"r":3,"result":[-1]}
```

**Invalid Parameters:**
```sh
curl "http://localhost:3000/bitonic?n=2&l=1&r=4"
```
```json
{"error":"Invalid or missing parameters. Please provide integers for 'n', 'l', and 'r', with n > 2."}
```

## Algorithm Details

### Bitonic Sequence Generation
A bitonic sequence is one that first increases then decreases (or vice versa).

**Algorithm:**
1. Creates peak at position (r-1)
2. Builds decreasing part from peak rightward
3. Builds increasing part from peak leftward
4. Returns [-1] if sequence length exceeds maximum possible: (r-l)*2 + 1

**Time Complexity:** O(n)  
**Space Complexity:** O(n)

## Error Handling

The API handles the following error cases:

1. **Missing Parameters**: Returns 400 if any required parameter is missing
2. **Invalid Types**: Returns 400 if parameters cannot be parsed as integers
3. **Invalid Values**: Returns 400 if n ≤ 2
4. **Unknown Routes**: Returns 404 for any path other than `/bitonic`