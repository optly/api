(ns api.domain.utils
  (:require
   [ring.swagger.json-schema :refer [describe field]]
   [schema.core :refer [defschema constrained Int Str]]))

(defn add-index
  [obj col]
  (let [col (if (coll? col)
              col
              [col])]
    (vary-meta
     obj update-in [:db :indices] #(conj % col))))

(defn add-indices
  [obj & cols]
  (reduce add-index obj cols))

(def add-indexes add-indices)

(defn non-negative-integer?
  [x]
  (and
   (>= x 0)
   (< x Integer/MAX_VALUE)))

(defschema NonNegInt
  "A non-negative-integer"
  (constrained Long non-negative-integer?))

(defschema ID
  (describe NonNegInt
            "A unique integer id"
            :db {:type :serial
                 :column-contraints [:primary-key]}))

(defn add-id
  [s]
  (->
   s
   (assoc :id ID)
   (add-index [:id])))

(defschema CreatedAt
  (describe
   org.joda.time.DateTime
   "The timestamp when the entity was created"))

(defschema UpdatedAt
  (describe
   org.joda.time.DateTime
   "The timestamp of the last update to the entity"))

(defn add-timestamps
  [s]
  (merge
   s
   {:created_at CreatedAt
    :updated_at UpdatedAt}))

(def db-string-defaults
  {:max-length 200})

(defn constrained-string
  [& kvs]
  (let [{max-length :max-length} (->>
                                  kvs
                                  (apply hash-map)
                                  (merge db-string-defaults))]
    (->
     Str
     (constrained #(<= (count %) max-length))
     (vary-meta
      assoc-in [:db :max-length] max-length))))
