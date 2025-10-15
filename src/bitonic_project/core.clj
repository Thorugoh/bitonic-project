; Namespace declaration and imports
(ns bitonic-project.core
  (:require [org.httpkit.server :as http-kit]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [taoensso.carmine :as car]
            [ring.middleware.params :refer [wrap-params]]
            [cheshire.core :as json])
  (:gen-class))

; Redis connections configuration
(def redis-conn {:pool {} :spec {:uri "redis://redis:6379"}})

; Macro to simplify Redis operations using the connection defined above
(defmacro wcar* [& body] `(car/wcar redis-conn ~@body))

(defn generate-bitonic
  [n l r]
  ; Check if it's possible to create a bitonic sequence of length n
  ; Maximum possible length is (r-l)*2 + 1
  (if (> n (+ (* (- r l) 2) 1))
    [-1] ; Return [-1] if impossible
    (let [initial-dq [(dec r)]
          ;  Build the decreasing part (including the peak)
          ; Process numbers from r down to l, adding them to the sequence
          after-decreasing (reduce (fn [dq i] 
                                     (if (< (count dq) n) 
                                       (conj dq i) ; Add to end if we need more elements
                                       (reduced dq))) ; Stop early if we have enough
                                   initial-dq 
                                   (range r (dec l) -1))
          ; Build the increasing part at the beginning
          ; Process numbers from r-2 down to l, adding them to the front
          final-dq (reduce (fn [dq i] 
                             (if (< (count dq) n) 
                               (into [i] dq) 
                               (reduced dq)))
                           after-decreasing 
                           (range (- r 2) (dec l) -1))]
      final-dq))) ; Return the completed bitonic sequence

(defn- respond
  [status body]
  {:status status
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/generate-string body)})

(defn- parse-long-safe [s]
  (try (Long/parseLong s) (catch NumberFormatException _ nil)))

(defn bitonic-handler
  [req]
  (let [; Extract query parameters from the request
        params (:query-params req)

        ; Safely parse string parameters to integers
        ; some-> ensures we only attempt parsing if the parameter exists
        n (some-> (get params "n") parse-long-safe)
        l (some-> (get params "l") parse-long-safe)
        r (some-> (get params "r") parse-long-safe)]

    ; Validate that all parameters exist and n > 2
    (if (and n l r (> n 2))
      (let [result (generate-bitonic n l r)
            redis-key (str "bitonic:" n ":" l ":" r)] ; Create cache key
        ; Store result in Redis with 1-hour expiration
        (wcar* (car/set redis-key (json/generate-string result) "EX" 3600))
        ; Return success response with the result
        (respond 200 {:n n :l l :r r :result result}))
      ; If invalid, return error response
      (respond 400 {:error "Invalid or missing parameters. Please provide integers for 'n', 'l', and 'r', with n > 2."}))))

; Define HTTP routes
(defroutes app-routes
  (GET "/bitonic" [] bitonic-handler)
  (route/not-found (respond 404 {:error "Not Found"})))

; Wrap routes with parameter parsing middleware
(def app (wrap-params app-routes))

(defn -main [& args]
  (let [port 3000]
    (println (str "🚀 Server starting on http://localhost:" port))
    ; Start the HTTP server using the configured app and port
    (http-kit/run-server app {:port port})))