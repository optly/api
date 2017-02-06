(ns migrations.20170123213426-create-tasks-table
  (:require
   [api.db.utils :refer :all]
   [clojure.java.jdbc
    :refer [db-do-commands create-table-ddl drop-table-ddl]]
   [config.migrate-config :refer [db]]))

(defn up
  "Create Tasks: Migrates the database up to version 20170123213426."
  []
  (->>
   [(create-table-ddl
     :tasks
     (->
      [[:name (varchar 512)]
       [:completed bool]
       [:position integer unique]]
      add-id
      add-timestamps))

    (create-index
     :tasks [:position])]

   (db-do-commands db))
  (println "migrations.20170123213426-create-tasks-table up..."))

(defn down
  "Drop Tasks: Migrates the database down from version 20170123213426."
  []
  (->>
   (drop-table-ddl :tasks)
   (db-do-commands db))
  (println "migrations.20170123213426-create-tasks-table down..."))
