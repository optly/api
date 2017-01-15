(ns api.core
  (require
    [ring.adapter.jetty :as jetty])
  (:gen-class))

(defn app
  [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})
