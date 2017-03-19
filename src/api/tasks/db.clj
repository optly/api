(ns api.tasks.db
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
    (query (connection)))
   (map ->schema)))

(defn find!
  [id]
  (let [r (->>
           (->
            (h/select :*)
            (h/from :tasks)
            (h/where [:= :id id])
            (query (connection)))
           (map ->schema)
           first)]
    (if (nil? r)
      (error (str "Task: " id " not found"))
      (value r))))

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

(defn update!
  [id t]
  (let [now (->
             (t/now)
             to-timestamp)]
    (->
     t
     (assoc :updated_at now)
     (db-utils/update (connection) id :tasks))))
