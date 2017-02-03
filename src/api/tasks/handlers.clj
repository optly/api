(ns api.tasks.handlers
  (:require
   [clj-time.core :refer [now]]
   [api.tasks.domain :refer [Task]]
   [api.tasks.db :refer [select!]]
   [compojure.api.sweet :refer [context GET]]
   [ring.util.http-response :refer [ok]]
   [ring.util.http-status :as status]))

(defn get-h
  [req]
  (ok (select!)))

(def handlers
  (context
    "/tasks"
    []
    :tags ["tasks"]
    (GET
      "/"
      []
      :responses {status/ok {:schema [Task]
                             :description "The list of all tasks"}}
      get-h)))
