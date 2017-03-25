(ns clojure-west-app.paws-data
  (:require [datomic.api :as d]))

(def users-and-groups
  [{:db/id (d/tempid :db.part/user -1)
    :user/type "public"}
   {:db/id (d/tempid :db.part/user -2)
    :user/type "employee"}
   {:group/name "Public Visiblity Group"
    :group/member (d/tempid :db.part/user -1)}
   {:group/name "Employee Visibility Group"
    :group/member  (d/tempid :db.part/user -2)}])
