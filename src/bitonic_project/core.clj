(ns bitonic-project.core
  (:require [org.httpkit.server :as http-kit]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [taoensso.carmine :as car]
            [ring.middleware.params :refer [wrap-params]]
            [cheshire.core :as json])
  (:gen-class))

(def redis-conn {:pool {} :spec {:uri "redis://redis:6379"}})
(defmacro wcar* [& body] `(car/wcar redis-conn ~@body))

(defn generate-bitonic
  [n l r]
  (if (> n (+ (* (- r l) 2) 1))
    [-1]
    (let [dq (atom [(dec r)])]
      (doseq [i (range r (dec l) -1)
              :while (< (count @dq) n)]
        (swap! dq conj i))
      (doseq [i (range (- r 2) (dec l) -1)
              :while (< (count @dq) n)]
        (swap! dq #(vec (concat [i] %))))
      @dq)))

(defn- respond
  [status body]
  {:status status
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/generate-string body)})

(defn- parse-long-safe [s]
  (try (Long/parseLong s) (catch NumberFormatException _ nil)))

(defn bitonic-handler
  [req]
  (let [params (:query-params req)
        n (some-> (get params "n") parse-long-safe)
        l (some-> (get params "l") parse-long-safe)
        r (some-> (get params "r") parse-long-safe)]
    (println (str "Received request with n=" n ", l=" l ", r=" r, " params=" params, " req=" req))

    (if (and n l r (> n 2))
      (let [result (generate-bitonic n l r)
            redis-key (str "bitonic:" n ":" l ":" r)]
        (wcar* (car/set redis-key (json/generate-string result) "EX" 3600))
        (respond 200 {:n n :l l :r r :result result}))
      (respond 400 {:error "Invalid or missing parameters. Please provide integers for 'n', 'l', and 'r', with n > 2."}))))

(defroutes app-routes
  (GET "/bitonic" [] bitonic-handler)
  (route/not-found (respond 404 {:error "Not Found"})))

(def app (wrap-params app-routes))

(defn -main [& args]
  (let [port 3000]
    (println (str "🚀 Server starting on http://localhost:" port))
    (http-kit/run-server app {:port port})))