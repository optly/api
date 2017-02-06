(ns api.tasks.handlers
  (:require
   [cemerick.url :refer [url]]
   [clojure.tools.trace :refer [trace]]
   [clj-time.core :refer [now]]
   [api.tasks.domain :refer [Task CreateParams]]
   [api.tasks.db :refer [select! insert!]]
   [compojure.api.sweet :refer [context GET POST]]
   [ring.util.http-response :refer [ok created]]
   [ring.util.http-status :as status]))

(defn ->url
  [{id :id}]
  (->
   "http://localhost"
   (url "api" "task" id)
   str))

(defn get-h
  [req]
  (ok (select!)))

(defn post-h
  [params]
  (fn
    [req]
    (let [result (insert! params)]
      (created (->url result) result))))

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
      get-h)

    (POST
      "/"
      []
      :body [params CreateParams]
      :responses {status/created {:schema Task
                                  :description "Create a task in the last position in the list"}}
      (post-h params))))
