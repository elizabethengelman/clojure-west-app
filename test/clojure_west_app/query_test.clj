(ns clojure-west-app.query-test
  (:require [clojure.test :refer :all]
            [datomic.api :as d]
            [clojure-west-app.db :as db]
            [clojure-west-app.query :refer :all]))

(def test-db "test")

(defn paws-example-fixture [fn]
  (db/delete-database test-db)
  (db/create-database test-db)
  (db/transact-schema test-db)
  (db/transact-seed-data test-db)
  (fn)
  (db/delete-database test-db)
  )

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

(def user-to-animal-tx
  [{:db/id (d/tempid :db.part/user -1)
    :animal/type "dog"
    :animal/name "Dottie"}

   {:db/id (d/tempid :db.part/user -2)
    :user/type "employee"
    :user/email "dotties_employee@email.com"
    :user/assigned-animal (d/tempid :db.part/user -1)}])

(deftest iteration-three
  (db/transact test-db user-to-animal-tx)
  "A association directly from user to animal"
  (let [db (test-db-value)]
    (testing "user->animals for default clients"
      (testing "when it is an employee user"
        (is (= (third-user->animals {:user-type "employee"} db) #{[17592186045420] [17592186045421]})))
      (testing "when it is a public user"
        (is (= (third-user->animals {:user-type "public"} db) #{[17592186045420]}))))

    (testing "user->animals for Special Client1"
      (testing "when it is an employee user"
        (is (= (third-user->animals {:user-type "employee"
                                     :user-email "dotties_employee@email.com"
                                     :custom-query :special-client} db) #{[17592186045426] [17592186045420] [17592186045421]})))
      (testing "when it is a public user"
        (is (= (third-user->animals {:user-type "public"
                                     :custom-query :special-client} db) #{[17592186045420]}))))))
