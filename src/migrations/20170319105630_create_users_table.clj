(ns migrations.20170319105630-create-users-table
  (:require
   [api.db.utils :refer :all]
   [clojure.java.jdbc
    :refer [db-do-commands create-table-ddl drop-table-ddl]]
   [config.migrate-config :refer [db]]))

(def table-name :users)

(defn up
  "Migrates the database up to version 20170319105630."
  []
  (->>
   [(create-table-ddl
     table-name
     (->
      [[:email (varchar 512)]
       [:confirmed_at timestamp]
       [:encrypted_password (varchar 1024)]]
      add-id
      add-timestamps))

    (create-index table-name [:email])]

   (db-do-commands db))
  (println "migrations.20170319105630-create-users-table up..."))

(defn down
  "Migrates the database down from version 20170319105630."
  []
  (->>
   (drop-table-ddl table-name)
   (db-do-commands db))
  (println "migrations.20170319105630-create-users-table down..."))
