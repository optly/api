(ns api.core
  (:require
   [clojure.tools.trace :refer [trace]]
   [api.config :refer [version init!]]
   [api.tasks.handlers :as tasks]
   [api.users.handlers :as users]
   [compojure.api.sweet :refer [api context]]
   [ring.logger :refer [wrap-with-logger]]
   [ring.util.http-response :as response]
   [ring.adapter.jetty :as jetty])
  (:gen-class))

(defn custom-handler [^Exception e data request]
  (response/internal-server-error {:type "unknown-exception"
                                   :class (.getName (.getClass e))
                                   :message (.getMessage e)
                                   :data data
                                   :stack (map str (.getStackTrace e))}))

(def handler
  (->
   (api
    {:exceptions {:handlers {:compojure.api.exception/default custom-handler}}
     :swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Optly API"
                    :description "Optly API for task management"
                    :version version}
             :tags [{:name "tasks" :description "Tasks to be managed"}
                    {:name "users" :description "Users of the API"}]}}}
    (context
      "/api"
      []
      tasks/handlers
      users/handlers))
   init!))
