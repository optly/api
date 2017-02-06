(ns api.core
  (:require
   [api.tasks.handlers :as tasks]
   [compojure.api.sweet :refer [api context]]
   [ring.util.http-response :as response]
   [ring.adapter.jetty :as jetty])
  (:gen-class))

(defn custom-handler [^Exception e data request]
  (response/internal-server-error {:type "unknown-exception"
                                   :class (.getName (.getClass e))
                                   :message (.getMessage e)
                                   :data data}))

(def handler
  (api
   {:exceptions {:handlers {:compojure.api.exception/default custom-handler}}
    :swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Optly API"
                   :description "Optly API for task management"}
            :tags [{:name "tasks", :description "Tasks to be managed"}]}}}
   (context
     "/api"
     []
     tasks/handlers)))
