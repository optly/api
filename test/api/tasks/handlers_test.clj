(ns api.tasks.handlers-test
  (:require
   [clojure.tools.trace :refer [trace]]
   [clj-time.coerce :refer [from-string]]
   [api.core :refer [handler]]
   [api.tasks.db :as db]
   [api.db.utils :as db-utils]
   [config.db :refer [connection]]
   [api.tasks.generators :as gen]
   [api.http.mock-helpers :refer
    [api-get api-post api-delete api-patch with-body]]
   [ring.util.http-predicates
    :refer [ok? created? no-content?]]
   [cheshire.core :refer [parse-string]]
   [clojure.test :refer [deftest testing is]]
   [clojure.test.check.properties :refer [for-all]]
   [clojure.test.check.clojure-test :refer [defspec]]))

(defn read-task-json
  [body]
  (->
   body
   (parse-string true)
   (update :created_at from-string)
   (update :updated_at from-string)))

(defn read-tasks-json
  [body]
  (->>
   (parse-string body true)
   (map
    #(->
      %
      (update :created_at from-string)
      (update :updated_at from-string)))))

(defn remove-updated-at
  [ts]
  (map #(dissoc % :updated_at) ts))

(defspec get-api-tasks-handler-test
  20
  (for-all [t gen/task]
           (db-utils/delete-all :tasks (connection))
           (let [task (db/insert! t)
                 response (->
                           (api-get :tasks)
                           handler)
                 body (slurp (response :body))
                 json (read-tasks-json body)]
             (is (true? (ok? response)))
             (is (= [task] json)))))

(defspec create-api-tasks-handler-test
  20
  (for-all [params gen/create-params]
           (db-utils/delete-all :tasks (connection))
           (let [response (->
                           (api-post :tasks)
                           (with-body params)
                           handler)
                 task (-> (db/select!) first)
                 body (slurp (response :body))
                 json (read-task-json body)]
             (is (true? (created? response)))
             (is (= task json)))))

(defspec delete-api-tasks-handler-test
  20
  (for-all [t gen/task]
           (db-utils/delete-all :tasks (connection))
           (let [{id :id :as task} (db/insert! t)
                 response (->
                           (api-delete :tasks id)
                           handler)]
             (is (true? (no-content? response)))
             (is (= [] (db/select!))))))

(defspec delete-api-tasks-handler-test
  20
  (for-all [t gen/task
            params gen/patch-params]
           (db-utils/delete-all :tasks (connection))
           (let [{id :id :as task} (db/insert! t)
                 response (->
                           (api-patch :tasks id)
                           (with-body params)
                           handler)
                 expected (remove-updated-at [(merge task params)])
                 actual (remove-updated-at (db/select!))]
             (is (true? (no-content? response)))
             (is (= expected actual)))))
