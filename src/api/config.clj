(ns api.config
  (:require
   [ring.logger :refer :all]
   [environ.core :refer [env]]
   [clojure.java.io :refer [resource]]
   [clojure.edn :as edn]))

(def cljenv (env :cljenv "dev"))

(defn init!
  [app]
  (case cljenv
    "test" app
    (wrap-with-logger app)))

(def uber-version
  (some->
   (resource "project.clj")
   slurp
   edn/read-string
   (nth 2)))

(def version (str cljenv "-" uber-version))

(def jwt-secret
  (env :jwt-secret "jwt-secret"))
