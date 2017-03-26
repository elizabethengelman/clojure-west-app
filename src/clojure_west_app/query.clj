(ns clojure-west-app.query
  (:require [datomic.api :as d]
            [clojure-west-app.db :as db]))

(def db-name "gene-and-louise")

(defn current-db-value []
  (d/db (db/connection db-name)))
;============================================
;Iteration 1: all in one query
;============================================
(defn first-user->animals
  ([user db-value]
    (d/q '[:find ?a
           :in $ ?user-type
           :where [?u :user/type ?user-type]
           [?g :group/member ?u]
           [?g :group/member ?a]
           [?a :animal/name]]
         db-value
         (:user-type user))))
;============================================
;Iteration 2: breaking it into 2 composable queries
;============================================
(defn user->group [user db-value]
  (d/q '[:find ?g
         :in $ ?user-type
         :where
         [?u :user/type ?user-type]
         [?g :group/member ?u]]
       db-value
       (:user-type user)))

(defn groups->animals [groups db-value]
  (d/q '[:find ?a
         :in $ [?group ...]
         :where
         [?group :group/member ?a]
         [?a :animal/name]]
       db-value
       groups))

(defn second-user->animals [user db-value]
  (let [groups (flatten (into [] (user->group user db-value)))]
    (groups->animals groups db-value)))

;============================================
;Iteration 3: adding another data shape (special client)
;============================================

;(defmulti user->animals (fn [user] (:custom-query user)))
;(defmethod user->animals :special-client [user]
;  "use the special-client query")
;(defmethod user->animals :default [user]
;  (d/q (default-query (:user-type user))
;       (db-value)))
