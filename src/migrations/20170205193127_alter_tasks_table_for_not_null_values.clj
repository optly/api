(ns migrations.20170205193127-alter-tasks-table-for-not-null-values
  (:require
   [api.db.utils :refer :all]
   [clojure.java.jdbc
    :refer [db-do-commands create-table-ddl drop-table-ddl]]
   [config.migrate-config :refer [db]]))

(defn up
  "Migrates the database up to version 20170205193127."
  []
  (->>
   [(set-not-null :tasks :name)
    (set-not-null :tasks :completed)
    (set-not-null :tasks :position)]

   (db-do-commands db))
  (println "migrations.20170205193127-alter-tasks-table-for-not-null-values up..."))

(defn down
  "Migrates the database down from version 20170205193127."
  []
  (->>
   [(set-null :tasks :name)
    (set-null :tasks :completed)
    (set-null :tasks :position)]

   (db-do-commands db))
  (println "migrations.20170205193127-alter-tasks-table-for-not-null-values down..."))
