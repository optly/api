(ns api.error.core
  (:require
   [clojure.algo.monads :refer :all]))

(defn error [x] {:error x})
(defn value [x] {:value x})

(def error-result identity)
(def error-zero {:value nil})
(defn error-bind
  [{:keys [error value] :as mv} f]
  (if (nil? error)
    (try
      (f value)
      (catch Exception e (error e)))
    mv))

(defmonad error-m
  [m-result error-result
   m-bind error-bind
   m-zero error-zero])

(defmacro doerr
  [steps expr]
  `(domonad error-m ~steps ~expr))

(defn binding-error
  [bindings]
  (->
   "resolve-error must only take 2 forms in let statement"
   (str " found " (count bindings) ": ")
   (str bindings)))

(defmacro let-error
  [bindings success error]
  (let [form (bindings 0)
        tst (bindings 1)]
    (if (> (count bindings) 2)
      (throw
       (IllegalArgumentException. (binding-error bindings))))
    `(let [temp# ~tst]
       (if (-> temp# :error nil?)
         (let [~form (-> temp# :value)]
           ~success)
         (let [~form (-> temp# :error)]
           ~error)))))
