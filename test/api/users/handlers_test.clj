(ns api.users.handlers-test
  (:require
   [com.gfredericks.test.chuck :as chuck]
   [com.gfredericks.test.chuck.clojure-test :refer [checking]]
   [clj-time.coerce :refer [from-string]]
   [buddy.hashers :as hashers]
   [config.db :refer [connection]]
   [api.core :refer [handler]]
   [api.users.db :as db]
   [api.users.domain :as domain]
   [api.db.utils :as db-utils]
   [api.users.generators :as gen]
   [api.jwt.core :as jwt]
   [api.http.mock-helpers :refer
    [api-get api-post api-delete api-patch with-body]]
   [ring.util.http-predicates
    :refer [ok? created?
            no-content? not-found?
            unauthorized? bad-request?]]
   [cheshire.core :refer [parse-string]]
   [clojure.tools.trace :refer [trace]]
   [clojure.test :refer [deftest testing is]]
   [clojure.test.check.generators :refer [no-shrink]]
   [clojure.test.check.properties :refer [for-all]]
   [clojure.test.check.clojure-test :refer [defspec]]))

(defn update-ts
  [json]
  (->
   json
   (update :created_at from-string)
   (update :updated_at from-string)))

(defn parse-json
  [s]
  (cond
    (= String (class s)) (parse-string s true)
    :else (parse-string (slurp s) true)))

(defn read-user-json
  [body]
  (->
   body
   parse-json
   update-ts))

(def table-name :users)

(defn create-user!
  [params]
  (->
   (api-post table-name)
   (with-body params)
   handler))

(defn response->user-json
  [resp]
  (->
   resp
   :body
   slurp
   read-user-json))

(def bad-password-confirmation
  {:errors {:password_confirmation domain/password-confirmation-does-not-match-msg}})

(deftest create-api-users-handler-test
  (checking "creates a user" (chuck/times 5)
            [params (no-shrink gen/create-params)]
            (db-utils/delete-all table-name (connection))
            (let [response (create-user! params)
                  json (response->user-json response)
                  {:keys [id
                          email
                          confirmed_at
                          encrypted_password]} (-> (db/select!) first)]
              (is (created? response))
              (is (= (json :id) id))
              (is (= (params :email) (json :email) email))
              (is (nil? confirmed_at))
              (is (hashers/check (params :password) encrypted_password))))

  (checking "returns a bad request if password confirmation does not match" (chuck/times 5)
            [params (no-shrink gen/create-params)]
            (db-utils/delete-all table-name (connection))
            (let [response (create-user! (assoc params :password_confirmation "bad password"))
                  json (-> response :body parse-json)]
              (is (bad-request? response))
              (is (= bad-password-confirmation json)))))

(deftest api-users-signin-handler-test
  (checking "signin returns a JWT for the user" (chuck/times 5)
            [params (no-shrink gen/create-params)]
            (db-utils/delete-all table-name (connection))
            (let [{id :id} (-> params create-user! response->user-json)
                  signin-params (select-keys params [:email :password])
                  response (->
                            (api-post table-name :signin)
                            (with-body signin-params)
                            handler)
                  body (slurp (response :body))

                  {:keys [token]} (parse-json body)]
              (is (ok? response))
              (is (= {:id id} (jwt/unsign token)))))

  (checking "using a bad password does not work" (chuck/times 5)
            [params (no-shrink gen/create-params)]
            (db-utils/delete-all table-name (connection))
            (let [{user-id :id} (create-user! params)
                  signin-params (->
                                 (select-keys params [:email])
                                 (assoc :password "wrong-password-124"))
                  response (->
                            (api-post table-name :signin)
                            (with-body signin-params)
                            handler)]
              (is (unauthorized? response)))))
