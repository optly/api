(ns api.users.domain
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
   [schema.core :refer [defschema Bool optional-key maybe]]))

(defschema ConfirmedAt
  (describe
   (maybe org.joda.time.DateTime)
   "The timestamp of when the email was confirmed"))

(defschema Password
  (describe
   (constrained-string :min-length 8 :max-length 1024)
   "The password of the user"
   :example "secure-password"))

(defschema PasswordConfirmation
  (describe
   (constrained-string :min-length 8 :max-length 1024)
   "The password confirmation of the user"
   :example "secure-password"))

(defschema User
  (->
   {:email (describe
            (constrained-string :max-length 1024)
            "the email of the user"
            :example "test-user@example.com")
    :confirmed_at ConfirmedAt}
   add-id
   add-timestamps))

(defschema UserCreateParams
  (->
   User
   remove-id
   remove-timestamps
   (dissoc :confirmed_at)
   (assoc :password Password)
   (assoc :password_confirmation PasswordConfirmation)))
