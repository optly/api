(ns api.jwt.core
  (:require
   [clojure.tools.trace :refer [trace]]
   [api.config :as config]
   [buddy.sign.jwt :as jwt]))

(defn sign
  [claims]
  (jwt/sign claims config/jwt-secret))

(defn unsign
  [text]
  (jwt/unsign text config/jwt-secret))
