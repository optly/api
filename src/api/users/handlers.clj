(ns api.users.handlers
  (:require
   [cemerick.url :refer [url]]
   [clj-time.core :refer [now]]
   [api.domain.utils :refer [ID]]
   [api.users.domain :refer [User UserCreateParams]]
   [api.users.db :refer [insert!]]
   [buddy.hashers :as hashers]
   [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
   [ring.util.http-response
    :refer [ok created no-content not-found]]
   [ring.util.http-status :as status]))

(defn create-params->user
  [{:keys [password email]}]
  {:email email
   :confirmed_at nil
   :encrypted_password (hashers/derive password)})

(defn ->url
  [{id :id}]
  (->
   "http://localhost"
   (url "api" "users" id)
   str))

(defn post-h
  [params]
  (fn
    [req]
    (let [result (->
                  params
                  create-params->user
                  insert!)]
      (created
       (->url result)
       (dissoc result :encrypted_password)))))

(def handlers
  (context
    "/users"
    []
    :tags ["users"]

    (POST
      "/"
      []
      :body [params UserCreateParams]
      :responses {status/created {:schema User
                                  :description "Create a user"}}
      (post-h params))))
