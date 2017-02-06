(ns api.tasks.handlers-test
  (:require
   [clojure.tools.trace :refer [trace]]
   [clj-time.coerce :refer [from-string]]
   [api.core :refer [handler]]
   [api.tasks.db :as db]
   [api.db.utils :as db-utils]
   [config.db :refer [connection]]
   [api.tasks.generators :as gen]
   [api.http.mock-helpers :refer [api-get]]
   [ring.util.http-predicates :refer [ok?]]
   [cheshire.core :refer [parse-string]]
   [clojure.test :refer [deftest testing is]]
   [clojure.test.check.properties :refer [for-all]]
   [clojure.test.check.clojure-test :refer [defspec]]))

(defn read-json
  [body]
  (->>
   (parse-string body true)
   (map
    #(->
      %
      (update :created_at from-string)
      (update :updated_at from-string)))))

(defspec get-api-tasks-handler-test
  20
  (for-all [t gen/task]
           (db-utils/delete-all :tasks (connection))
           (let [task (db/insert! t)
                 response (->
                           (api-get :tasks)
                           handler)
                 body (slurp (response :body))
                 json (read-json body)]
             (is (true? (ok? response)))
             (is (= [task] json)))))
