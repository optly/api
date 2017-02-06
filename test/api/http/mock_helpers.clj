(ns api.http.mock-helpers
  (:require
   [cemerick.url :refer [url]]
   [ring.mock.request :as mock]))

(defn e->url
  [e]
  (->
   "http://localhost"
   (url "api" (name e))
   str))

(defn api-get
  [e]
  (mock/request
   :get (e->url e)))
