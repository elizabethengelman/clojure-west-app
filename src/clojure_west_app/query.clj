(ns clojure-west-app.query
  (:require [datomic.api :as d]
            [clojure-west-app.db :as db]))

(def db-name "gene-and-louise")

(defn current-db-value []
  (d/db (db/connection db-name)))
;============================================
;Iteration 1: all in one query
;============================================
(defn user->animals1 [user db-value]
    (d/q '[:find ?a
           :in $ ?user-type
           :where [?u :user/type ?user-type]
           [?g :group/member ?u]
           [?g :group/member ?a]
           [?a :animal/name]]
         db-value
         (:user-type user)))
;============================================
;Iteration 2: breaking it into 2 separate queries
;============================================
;(defn user->group [user db-value]
;  (d/q '[:find ?g
;         :in $ ?user-type
;         :where
;         [?u :user/type ?user-type]
;         [?g :group/member ?u]]
;       db-value
;       (:user-type user)))
;
;(defn groups->animals [groups db-value]
;  (d/q '[:find ?a
;         :in $ [?group ...]
;         :where
;         [?group :group/member ?a]
;         [?a :animal/name]]
;       db-value
;       groups))
;
;(defn second-user->animals [user db-value]
;  (let [groups (flatten (into [] (user->group user db-value)))]
;    (groups->animals groups db-value)))

;============================================
;Iteration 2B: using rules in favor of a new query call for each piece
;============================================
(def user-type->group-rule
  '[[(user-type->group ?user-type ?g)
     [?u :user/type ?user-type]
     [?g :group/member ?u]]])

(def group->animal-rule
  '[[(group->animal ?g ?a)
     [?g :group/member ?a]
     [?a :animal/name]]])

(def rule-set
  (concat user-type->group-rule
          group->animal-rule))

(defn user->animals2 [user db-value]
  (d/q '[:find ?a
         :in $ % ?user-type
         :where (user-type->group ?user-type ?g)
         (group->animal ?g ?a)]
       db-value
       rule-set
       (:user-type user)))

;============================================
;Iteration 3: adding another data shape (special client)
;============================================

(def special-user->animal-rule
  '[[(user->animal ?user-type ?g ?a ?user-email)
     [?u :user/email ?user-email]
     [?g :group/member]
     [?u :user/assigned-animal ?a]
     [?a :animal/name]]])

(def default-user->animal-rule
  '[[(user->animal ?user-type ?g ?a ?user-email)
     [?u :user/type ?user-type]
     [?g :group/member ?u]
     [?g :group/member ?a]
     [?a :animal/name]]])

(defn user->animals [user db-value rules]
  (d/q '[:find ?a
         :in $ % ?user-email ?user-type
         :where (user->animal ?user-type ?g ?a ?user-email)]
       db-value
       rules
       (:user-email user "no-email")
       (:user-type user)))

(defmulti user->animals3 (fn [user db-value] (:custom-query user)))
(defmethod user->animals3 :special-client [user db-value]
  (user->animals user db-value
    (concat default-user->animal-rule special-user->animal-rule)))
(defmethod user->animals3 :default [user db-value]
  (user->animals user db-value default-user->animal-rule))
