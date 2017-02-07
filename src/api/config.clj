(ns api.config
  (:require
   [clojure.java.io :refer [resource]]
   [clojure.edn :as edn]))

(def uber-version
  (some->
   (resource "project.clj")
   slurp
   edn/read-string
   (nth 2)))

(def version
  uber-version)
