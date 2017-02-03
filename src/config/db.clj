(ns config.db
  (:import
   com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require
   [clojure.tools.trace :refer [trace]]
   [environ.core :refer [env]]))

(defn production?
  []
  (= (env :clj-env) "production"))

(defn url
  []
  (env :jdbc-database-url))

(defn spec
  []
  (if (production?)
    {:connection-uri (url)}
    {:dbtype "postgresql"
     :dbname "postgres"
     :host "localhost"
     :port 15432
     :user "postgres"
     :password "mysecretpassword"}))

(defn spec->jdbc-url
  [spec]
  (str
   "jdbc:"
   (:dbtype spec)
   "://"
   (:host spec)
   ":"
   (:port spec)
   "/"
   (:dbname spec)))

(defn pool
  [spec]
  (if (production?)
    {:datasource (doto (ComboPooledDataSource.)
                   (.setDriverClass "org.postgresql.Driver")
                   (.setJdbcUrl (spec :connection-uri))
                   (.setMaxIdleTimeExcessConnections (* 30 60))
                   (.setMaxIdleTime (* 3 60 60)))}
    {:datasource (doto (ComboPooledDataSource.)
                   (.setDriverClass "org.postgresql.Driver")
                   (.setJdbcUrl (spec->jdbc-url spec))
                   (.setUser (:user spec))
                   (.setPassword (:password spec))
                   (.setMaxIdleTimeExcessConnections (* 30 60))
                   (.setMaxIdleTime (* 3 60 60)))}))

(def pooled
  (->
   (spec)
   pool
   delay))

(defn connection [] @pooled)
