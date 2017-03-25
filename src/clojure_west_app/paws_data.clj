(ns clojure-west-app.paws-data
  (:require [datomic.api :as d]))

(def users-and-groups
  [{:db/id (d/tempid :db.part/user -1)
    :user/type "public"}
   {:db/id (d/tempid :db.part/user -3)
    :animal/type "cat"
    :animal/name "Gene"}
   {:group/name "Public Visibility Group"
    :group/member [(d/tempid :db.part/user -1)
                   (d/tempid :db.part/user -3)]}


   {:db/id (d/tempid :db.part/user -2)
    :user/type "employee"}
   {:db/id (d/tempid :db.part/user -4)
    :animal/type "cat"
    :animal/name "Louise"}
   {:group/name "Employee Visibility Group"
    :group/member [(d/tempid :db.part/user -2)
                   (d/tempid :db.part/user -4)]}])
