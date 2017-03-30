(ns clojure-west-app.query
  (:require [datomic.api :as d]
            [clojure-west-app.db :as db]))

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

(def special-user->animal-rule
  '[[(user->animal ?user-type ?g ?a ?user-email)
     [?u :user/email ?user-email]
     [?u :user/assigned-animal ?a]]])

(def default-user->animal-rule
  '[[(user->animal ?user-type ?g ?a ?user-email)
     [?u :user/type ?user-type]
     [?g :group/member ?u]
     [?g :group/member ?a]
     [?a :animal/name]]])

(defn special-user->animals [user db-value]
  (d/q '[:find ?a
         :in $ % ?user-email ?user-type
         :where (user->animal ?user-type ?g ?a ?user-email)]
       db-value
       (concat default-user->animal-rule special-user->animal-rule)
       (:user-email user "no-email")
       (:user-type user)))

(defmulti user->animals3 (fn [user db-value] (:custom-query user)))
(defmethod user->animals3 :purrticular-customer [user db-value]
  (special-user->animals user db-value))
(defmethod user->animals3 :default [user db-value]
  (user->animals2 user db-value))




