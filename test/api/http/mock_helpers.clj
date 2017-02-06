(ns api.http.mock-helpers
  (:require
   [cheshire.core :refer [generate-string]]
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

(defn api-post
  [e]
  (->
   (mock/request
    :post (e->url e))
   (mock/content-type "application/json")))

(defn with-body
  [req body]
  (->>
   body
   generate-string
   (mock/body req)))
