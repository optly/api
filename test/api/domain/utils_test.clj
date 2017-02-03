(ns api.domain.utils-test
  (:require
   [api.domain.utils :refer :all]
   [clojure.test :refer [deftest testing is]]
   [clojure.test.check.clojure-test :refer [defspec]]))

(deftest add-index-test
  (testing "adds an index keyword to :db :indices"
    (is
     (=
      {:db {:indices [[:name]]}}
      (-> {} (add-index :name) meta))))

  (testing "it does not overwrite the previous values"
    (let [obj (with-meta {} {:db {:indices [[:subject]]}})]
      (is
       (=
        {:db {:indices [[:subject]
                        [:name]]}}
        (->
         obj (add-index [:name]) meta))))))

(deftest add-indices-test
  (testing "adds an index keyword to :db :indices"
    (is
     (=
      {:db {:indices [[:subject :label] [:name]]}}
      (-> {} (add-indices [:name] [:subject :label]) meta)))))

(deftest add-timestamps-test
  (testing "adds CreatedAt and UpdatedAt to hash"
    (is
     (=
      {:created_at CreatedAt
       :updated_at UpdatedAt}
      (add-timestamps {})))))
