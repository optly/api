(ns config.migrate-config
  (:require
   [config.db :refer [spec]]
   [clojure.java.jdbc :as jdbc]
   [clojure.tools.trace :refer [trace]]
   [drift.builder :refer [timestamp-migration-number-generator]]))

(def db (spec))

(defn init!
  [_]
  (jdbc/db-do-commands
   db
   ["CREATE TABLE IF NOT EXISTS
     DRIFT_MIGRATIONS (VERSION BIGINT)"]))

(defn current-version!
  []
  ((or
    (first
     (jdbc/query
      db
      "SELECT
         VERSION
         FROM
         DRIFT_MIGRATIONS"))
    {:version 0})
   :version))

(defn update-version!
  [v]
  (jdbc/delete!
   db
   "DRIFT_MIGRATIONS"
   ["1=?" 1])

  (jdbc/insert!
   db
   "DRIFT_MIGRATIONS" {:version v}))

(defn migrate-config
  []
  {:directory "/src/migrations"
   :init init!
   :current-version current-version!
   :update-version update-version!})
