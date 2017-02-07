(ns api.tasks.handlers
  (:require
   [cemerick.url :refer [url]]
   [clojure.tools.trace :refer [trace]]
   [clj-time.core :refer [now]]
   [api.domain.utils :refer [ID]]
   [api.tasks.domain :refer [Task CreateParams PatchParams]]
   [api.tasks.db :refer [select! insert! delete! update!]]
   [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
   [ring.util.http-response
    :refer [ok created no-content]]
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

(defn patch-h
  [params id]
  (fn
    [req]
    (update! id params)
    (no-content)))

(defn delete-h
  [id]
  (fn
    [req]
    (delete! id)
    (no-content)))

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
      (post-h params))

    (context
      "/:id"
      []
      :path-params [id :- ID]

      (PATCH
        "/"
        []
        :body [params PatchParams]
        :responses {status/created {:schema Task
                                    :description "Edit a task with a set of values in patch"}}
        (patch-h params id))

      (DELETE
        "/"
        []
        :responses {status/no-content {:description "The task was deleted"}}
        (delete-h id)))))
