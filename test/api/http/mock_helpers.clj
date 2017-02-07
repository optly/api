(ns api.http.mock-helpers
  (:require
   [cheshire.core :refer [generate-string]]
   [cemerick.url :refer [url]]
   [ring.mock.request :as mock]))

(defn coerce->name
  [v]
  (if (keyword? v)
    (name v)
    (str v)))

(defn es->url
  [es]
  (->
   url
   (apply
    "http://localhost"
    "api"
    (map coerce->name es))
   str))

(defn api-get
  [& es]
  (mock/request
   :get (es->url es)))

(defn api-post
  [& es]
  (->
   (mock/request
    :post (es->url es))
   (mock/content-type "application/json")))

(defn api-delete
  [& es]
  (mock/request
   :delete (es->url es)))

(defn api-patch
  [& es]
  (->
   (mock/request :patch (es->url es))
   (mock/content-type "application/json")))

(defn with-body
  [req body]
  (->>
   body
   generate-string
   (mock/body req)))
