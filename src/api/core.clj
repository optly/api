(ns api.core
  (require
    [ring.adapter.jetty :as jetty])
  (:gen-class))

(defn app
  [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Welcome to Optly beta.  Optly will help you <strong>optimize</strong> your time with a cutting edge todo list manager. Don't be a loser use our app! "})
