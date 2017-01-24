(ns config.db
  (:require
   [environ.core :refer [env]]))

(defn production?
  []
  (= (env :clj-env) "production"))

(defn db-url
  []
  (env :jdbc-database-url))

(defn db
  []
  (if (production?)
    {:connection-uri (db-url)}
    {:dbtype "postgresql"
     :dbname "postgres"
     :host "localhost"
     :port 15432
     :user "postgres"
     :password "mysecretpassword"}))
