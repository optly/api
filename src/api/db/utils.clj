(ns api.db.utils
  (:require
   [clojure.string :refer [join]]
   [honeysql.core :as honeysql]
   [clojure.java.jdbc :as jdbc]))

(def null
  "NULL")

(def not-null
  "NOT NULL")

(def bool
  "BOOL")

(def integer
  "INTEGER")

(def unique
  "UNIQUE")

(def serial
  "SERIAL")

(def timestamp
  "TIMESTAMP")

(defn varchar
  [i]
  (str "VARCHAR(" i ")"))

(defn id
  [n]
  [n serial unique not-null])

(def updated-at
  [:updated_at timestamp not-null])

(def created-at
  [:created_at timestamp not-null])

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

(defn set-not-null
  [tbl col]
  (str
   "ALTER TABLE "
   (name tbl)
   " ALTER COLUMN "
   (name col)
   " SET "
   not-null))

(defn set-null
  [tbl col]
  (str
   "ALTER TABLE "
   (name tbl)
   " ALTER COLUMN "
   (name col)
   " DROP "
   not-null))

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

(defn delete
  [id conn tbl]
  (->
   conn
   (jdbc/delete! tbl ["id = ?" id])))

(defn merge-timestamps
  [t now]
  (assoc t :created_at now :updated_at now))

(defn delete-all
  [n conn]
  (->
   conn
   (jdbc/delete! n ["1=?" 1])))

(defn update
  [t conn id tbl]
  (->
   conn
   (jdbc/update! tbl t ["id = ?" id])))
