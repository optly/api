(ns migrations.20170123213426-create-tasks-table
  (:require
   [clojure.java.jdbc :as jdbc]
   [config.migrate-config :refer [db]]))

(defn up
  "Create Tasks: Migrates the database up to version 20170123213426."
  []
  (jdbc/db-do-commands
   db
   ["CREATE TABLE TASKS
     (ID SERIAL PRIMARY KEY,
     VALUE TEXT)"])
  (println "migrations.20170123213426-create-tasks-table up..."))

(defn down
  "Drop Tasks: Migrates the database down from version 20170123213426."
  []
  (jdbc/db-do-commands
   db
   ["DROP TABLE TASKS"])
  (println "migrations.20170123213426-create-tasks-table down..."))
