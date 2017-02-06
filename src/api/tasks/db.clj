(ns api.tasks.db
  (:require
   [clj-time.coerce :refer [to-timestamp from-sql-time]]
   [config.db :refer [connection]]
   [api.db.utils :refer [query insert merge-timestamps]]
   [api.tasks.domain :refer [Task]]
   [clj-time.core :as t]
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

(defn insert!
  [t]
  (let [now (->
             (t/now)
             to-timestamp)]
    (->
     t
     (merge-timestamps now)
     (insert (connection) :tasks)
     ->schema)))
