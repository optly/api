(ns api.core
  (:require
   [api.tasks.handlers :as tasks]
   [compojure.api.sweet :refer [api context]]
   [ring.adapter.jetty :as jetty])
  (:gen-class))

(def handler
  (api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Optly API"
                   :description "Optly API for task management"}
            :tags [{:name "tasks", :description "Tasks to be managed"}]}}}
   (context
     "/api"
     []
     tasks/handlers)))
