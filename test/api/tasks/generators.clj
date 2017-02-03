(ns api.tasks.generators
  (:require
   [clj-time.core :as t]
   [clojure.string :refer [join]]
   [clojure.test.check.generators :as c-gen]
   [api.tasks.domain :refer [Task]]
   [api.domain.utils :refer [ID NonNegInt]]
   [schema.core :as s]
   [schema.experimental.generators :as s-gen]))

(def leaf-generators
  {org.joda.time.DateTime (c-gen/return (t/now))
   NonNegInt c-gen/pos-int
   String (->>
           20
           (c-gen/vector c-gen/char-alpha)
           (c-gen/fmap join))
   s/Bool c-gen/boolean})

(def task
  (s-gen/generator Task leaf-generators))
