(ns config.migrate-config
  (:require
   [config.db :as db]
   [clojure.java.jdbc :as jdbc]
   [clojure.tools.trace :refer [trace]]
   [drift.builder :refer [timestamp-migration-number-generator]]))

(def db
  (db/db))

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
         DRIFT_MIGRATIONS
         ORDER BY VERSION DESC"))
    {:version 0})
   :version))

(defn update-version!
  [v]
  (jdbc/insert!
   db
   "DRIFT_MIGRATIONS" {:version v}))

(defn migrate-config
  []
  {:directory "/src/migrations"
   :init init!
   :current-version current-version!
   :update-version update-version!})
