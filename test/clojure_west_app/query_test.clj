(ns clojure-west-app.query-test
  (:require [clojure.test :refer :all]
            [datomic.api :as d]
            [clojure-west-app.db :as db]
            [clojure-west-app.query :refer :all]))

(def test-db "test")

(defn paws-example-fixture [fn]
  (db/create-database test-db)
  (db/transact-schema test-db)
  (db/transact-seed-data test-db)
  (fn)
  (db/delete-database test-db))

(defn test-db-value []
  (d/db (db/connection test-db)))

(use-fixtures :once paws-example-fixture)

(deftest iteration-one
  "Path from user to animals is in one query"
  (testing "user->animals"
    (testing "when it is an employee user"
      (is (= (first-user->animals {:user-type "employee"} (test-db-value))
             #{[17592186045420] [17592186045421]})))
    (testing "when it is a public user"
      (is (= (first-user->animals {:user-type "public"} (test-db-value))
             #{[17592186045420]})))))

(deftest iteration-two
  "Refactoring queries into two smaller, more composable queries"
  (testing "user->animals"
    (testing "when it is an employee user"
      (is (= (second-user->animals {:user-type "employee"} (test-db-value))
             #{[17592186045420] [17592186045421]})))
    (testing "when it is a public user"
      (is (= (second-user->animals {:user-type "public"} (test-db-value))
             #{[17592186045420]})))))

;(deftest iteration-three
;  "A association directly from user to animal"
;  (testing "user->animals"
;    (testing "when it is an employee user"
;      (is (= (third-user->animals {:user-type "employee"}) #{[17592186045420] [17592186045421]})))
;    (testing "when it is a public user"
;      (is (= (third-user->animals {:user-type "public"}) #{[17592186045420]})))))
