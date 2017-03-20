(ns api.users.handlers
  (:require
   [cemerick.url :refer [url]]
   [clj-time.core :refer [now]]
   [api.users.domain
    :as domain
    :refer [User UserCreateParams UserSigninParams]]
   [api.users.db :refer [insert!] :as db]
   [api.jwt.core :as jwt]
   [buddy.hashers :as hashers]
   [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
   [ring.util.http-response
    :refer [ok created
            no-content not-found
            unauthorized bad-request]]
   [ring.util.http-status :as status]))

(defn ->url
  [{id :id} {origin :origin}]
  (->
   "http://localhost"
   (url "api" "tasks" id)
   str))

(defn create-params->user
  [{:keys [password email]}]
  {:email email
   :confirmed_at nil
   :encrypted_password (hashers/derive password)})

(defn signin-token
  [id]
  {:token (jwt/sign {:id id})})

(defn post-h
  [params]
  (fn
    [req]
    (let [errors (domain/validate-create-params params)
          result (->
                  params
                  create-params->user
                  insert!)]
      (if (nil? errors)
        (created
         (->url result req)
         (dissoc result :encrypted_password))
        (bad-request errors)))))

(defn signin-post-h
  [{:keys [email password]}]
  (fn
    [req]
    (let [{:keys [id encrypted_password]} (db/find-by! :email email)]
      (cond
        (nil? encrypted_password) (unauthorized)
        (hashers/check password encrypted_password) (ok (signin-token id))
        :else (unauthorized)))))

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
      (post-h params))

    (POST
      "/signin"
      []
      :body [params UserSigninParams]
      :responses {status/ok {:schema {:token String}
                             :description "Sign in and retrieve JWT token"}}
      (signin-post-h params))))
