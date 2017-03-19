(ns api.tasks.handlers
  (:require
   [cemerick.url :refer [url]]
   [clojure.tools.trace :refer [trace]]
   [clj-time.core :refer [now]]
   [api.error.core :refer [let-error]]
   [api.domain.utils :refer [ID]]
   [api.tasks.domain :refer [Task TaskCreateParams TaskPatchParams]]
   [api.tasks.db :refer [select! insert! delete! update! find!]]
   [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
   [ring.util.http-response
    :refer [ok created no-content not-found]]
   [ring.util.http-status :as status]))

(defn ->url
  [{id :id} {origin :origin}]
  (->
   "http://localhost"
   (url "api" "tasks" id)
   str))

(defn get-all-h
  [req]
  (ok (select!)))

(defn get-h
  [id]
  (fn
    [req]
    (let-error [x (find! id)]
               (ok x)
               (not-found))))

(defn post-h
  [params]
  (fn
    [req]
    (let [result (insert! params)]
      (created (->url result req) result))))

(defn patch-h
  [params id]
  (fn
    [req]
    (let-error [_ (update! id params)]
               (no-content)
               (not-found))))

(defn delete-h
  [id]
  (fn
    [req]
    (let-error [_ (delete! id)]
               (no-content)
               (not-found))))

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
      get-all-h)

    (POST
      "/"
      []
      :body [params TaskCreateParams]
      :responses {status/created {:schema Task
                                  :description "Create a task in the last position in the list"}}
      (post-h params))

    (context
      "/:id"
      []
      :path-params [id :- ID]

      (GET
        "/"
        []
        :responses {status/ok {:schema Task
                               :description "The details of a task"}
                    status/not-found {:description "Task to be fetched does not exist"}}
        (get-h id))

      (PATCH
        "/"
        []
        :body [params TaskPatchParams]
        :responses {status/created {:description "Edit a task with a set of values in patch"}
                    status/not-found {:description "Task to be updated does not exist"}}
        (patch-h params id))

      (DELETE
        "/"
        []
        :responses {status/no-content {:description "Delete a task"}
                    status/not-found {:description "Task to be deleted does not exist"}}
        (delete-h id)))))
