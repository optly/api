(ns api.users.db
  (:require
   [clojure.tools.trace :refer [trace]]
   [clj-time.coerce :refer [to-timestamp from-sql-time]]
   [config.db :refer [connection]]
   [api.error.core :refer [value error]]
   [api.db.utils
    :refer [query insert delete merge-timestamps] :as db-utils]
   [api.tasks.domain :refer [Task]]
   [clj-time.core :as t]
   [clojure.java.jdbc :as jdbc]
   [honeysql.helpers :as h]))

(def table-name :users)

(defn ->schema
  [db]
  (->
   db
   (dissoc :password :password-confirmation)
   (update :created_at from-sql-time)
   (update :updated_at from-sql-time)))

(defn select!
  []
  (->>
   (->
    (h/select :*)
    (h/from table-name)
    (query (connection)))
   (map ->schema)))

(defn insert!
  [t]
  (jdbc/with-db-transaction [conn (connection)]
    (let [now (->
               (t/now)
               to-timestamp)]
      (->
       t
       (merge-timestamps now)
       (insert conn table-name)
       ->schema))))
