(ns api.users.handlers-test
  (:require
   [clojure.tools.trace :refer [trace]]
   [com.gfredericks.test.chuck :as chuck]
   [com.gfredericks.test.chuck.clojure-test :refer [checking]]
   [clj-time.coerce :refer [from-string]]
   [api.core :refer [handler]]
   [buddy.hashers :as hashers]
   [api.users.db :as db]
   [api.db.utils :as db-utils]
   [config.db :refer [connection]]
   [api.users.generators :as gen]
   [api.http.mock-helpers :refer
    [api-get api-post api-delete api-patch with-body]]
   [ring.util.http-predicates
    :refer [ok? created? no-content? not-found?]]
   [cheshire.core :refer [parse-string]]
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

(defn read-user-json
  [body]
  (->
   body
   (parse-string true)
   update-ts))

(def table-name :users)

(deftest create-api-users-handler-test
  (checking "creates a user" (chuck/times 5)
            [params (no-shrink gen/create-params)]
            (db-utils/delete-all table-name (connection))
            (let [response (->
                            (api-post table-name)
                            (with-body params)
                            handler)
                  {:keys [email
                          confirmed_at
                          encrypted_password]} (-> (db/select!) first)
                  body (slurp (response :body))
                  json (read-user-json body)]
              (is (created? response))
              (is (= (params :email) email))
              (is (nil? confirmed_at))
              (is (hashers/check (params :password) encrypted_password)))))
