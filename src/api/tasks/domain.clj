(ns api.tasks.domain
  (:require
   [clojure.tools.trace :refer [trace]]
   [api.domain.utils
    :refer [add-timestamps
            remove-timestamps
            add-id
            remove-id
            constrained-string]]
   [ring.swagger.json-schema :refer [describe]]
   [clojure.set :refer [rename-keys]]
   [schema.core :refer [defschema Bool optional-key]]))

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

(defschema CreateParams
  (->
   Task
   remove-id
   remove-timestamps
   (dissoc :position)))

(defschema PatchParams
  (->
   CreateParams
   (rename-keys
    {:name (optional-key :name)
     :completed (optional-key :completed)})))
