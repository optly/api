(ns api.db.utils
  (:require
   [clojure.string :refer [join]]
   [honeysql.core :as honeysql]
   [clojure.java.jdbc :as jdbc]))

(def updated-at
  [:updated_at "TIMESTAMP"])

(def created-at
  [:created_at "TIMESTAMP"])

(def bool
  "BOOL")

(def integer
  "INTEGER")

(def unique
  "UNIQUE")

(def serial
  "SERIAL")

(defn varchar
  [i]
  (str "VARCHAR(" i ")"))

(defn id
  [n]
  [n serial unique])

(defn create-index
  [table cols]
  (str
   "CREATE INDEX ON "
   (name table)
   "("
   (->>
    (map name cols)
    (join ", "))
   ")"))

(defn add-id
  [cols]
  (conj cols (id :id)))

(defn add-timestamps
  [cols]
  (->
   cols
   (conj updated-at created-at)))

(defn query
  [sql conn]
  (->>
   sql
   honeysql/format
   (jdbc/query conn)))

(defn insert
  [t conn tbl]
  (->
   conn
   (jdbc/insert! tbl t)
   first))

(defn merge-timestamps
  [t now]
  (assoc t :created_at now :updated_at now))

(defn delete-all
  [n conn]
  (->
   conn
   (jdbc/delete! n ["1=?" 1])))
