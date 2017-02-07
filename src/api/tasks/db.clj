(ns api.tasks.db
  (:require
   [clojure.tools.trace :refer [trace]]
   [clj-time.coerce :refer [to-timestamp from-sql-time]]
   [config.db :refer [connection]]
   [api.db.utils
    :refer [query insert delete merge-timestamps]]
   [api.tasks.domain :refer [Task]]
   [clj-time.core :as t]
   [clojure.java.jdbc :as jdbc]
   [honeysql.helpers :as h]))

(defn ->schema
  [db]
  (->
   db
   (dissoc :position)
   (update :created_at from-sql-time)
   (update :updated_at from-sql-time)))

(defn select!
  []
  (->>
   (->
    (h/select :*)
    (h/from :tasks)
    (h/order-by [:position :asc])
    (query (connection)))
   (map ->schema)))

(defn max-position
  [conn]
  (let [result (->
                (h/select :position)
                (h/from :tasks)
                (h/order-by [:position :desc])
                (h/limit 1)
                (query conn)
                first)]
    (if (nil? result)
      0
      (result :position))))

(defn insert!
  [t]
  (jdbc/with-db-transaction [conn (connection)]
    (let [now (->
               (t/now)
               to-timestamp)]
      (->
       t
       (merge-timestamps now)
       (assoc :position (-> conn max-position inc))
       (insert conn :tasks)
       ->schema))))

(defn delete!
  [id]
  (delete id (connection) :tasks))
