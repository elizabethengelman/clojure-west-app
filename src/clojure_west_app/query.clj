(ns clojure-west-app.query
  (:require [datomic.api :as d]
            [clojure-west-app.db :as db]))

(def db-name "gene-and-louise")

(defn db-value []
  (d/db (db/connection db-name)))
;============================================
;Iteration 1
;============================================
(defn first-user->animals [user]
  (d/q '[:find ?a
         :in $ ?user-type
         :where [?u :user/type ?user-type]
         [?g :group/member ?u]
         [?g :group/member ?a]
         [?a :animal/name]]
       (db-value)
       (:user-type user)))
;============================================
;Iteration 2
;============================================
(defn user->group [user]
  (d/q '[:find ?g
         :in $ ?user-type
         :where
         [?u :user/type ?user-type]
         [?g :group/member ?u]]
       (db-value)
       (:user-type user)))

(defn groups->animals [groups]
  (d/q '[:find ?a
         :in $ [?group ...]
         :where
         [?group :group/member ?a]
         [?a :animal/name]]
       (db-value)
       groups))

(defn second-user->animals [user]
  (let [groups (flatten (into [] (user->group user)))]
    (groups->animals groups)))
