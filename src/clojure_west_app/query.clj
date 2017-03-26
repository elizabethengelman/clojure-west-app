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

(def user->animal ;not using this rulset in iteration 2b
  '[[(user->animal ?user-type ?g ?a)
     (user-group ?user-type ?g)
     (group-animal ?g ?a)]])

(def rules
  (concat user-type->group-rule
          group->animal-rule))

(defn second-user->animals [user db-value]
  (d/q '[:find ?a
         :in $ % ?user-type
         :where
         [?a :animal/type]
         (group->animal ?g ?a)
         (user-type->group ?user-type ?g)]
       db-value
       rules
       (:user-type user)))

;============================================
;Iteration 3: adding another data shape (special client)
;============================================

;(def user->animal
;  '[[(user->animal)
;     [?a :animal/type]
;     (user-group ?user-type)
;     (group-animal ?g)]])

  ;[[(special-user->animal ?user-email ?user-type)
  ;  [?u :user/email ?user-email]
  ;  [?u :user/assigned-animal ?a]]
  ; [(special-user->animal ?user-email ?user-type)
  ;  [?u :user/type ?user-type]
  ;  [?g :group/member ?u]
  ;  [?g :group/member ?a]
  ;  [?a :animal/name]]]
(defn special-user->animals [user db-value]
  (if (:user-email user)
    (d/q '[:find ?a
           :in $ ?user-email
           :where
           [?u :user/email ?user-email]
           [?u :user/assigned-animal ?a]]
         db-value
         (:user-email user))))

(defmulti third-user->animals (fn [user db-value] (:custom-query user)))
(defmethod third-user->animals :special-client [user db-value]
  (special-user->animals user db-value))
(defmethod third-user->animals :default [user db-value]
  (second-user->animals user db-value))
