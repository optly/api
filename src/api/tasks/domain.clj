(ns api.tasks.domain
  (:require
   [clojure.tools.trace :refer [trace]]
   [api.domain.utils
    :refer [add-timestamps add-id constrained-string]]
   [ring.swagger.json-schema :refer [describe]]
   [schema.core :refer [defschema Bool]]))

(defschema Task
  (->
   {:name (describe
           (constrained-string :max-length 512)
           "A descriptive name of the task"
           :example "Buy Groceries")
    :completed (describe
                Bool
                "A flag showing whether the task was completed or not")}
   add-id
   add-timestamps))
