(ns clojure-west-app.query
  (:require [datomic.api :as d]
   :require [clojure-west-app.db :as db]))

(def db-name "gene-and-louise")

(defn db-value []
  (d/db (db/connection db-name)))

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

(defn first-user->animals [user]
  (d/q '[:find ?a
         :in $ ?user-type
         :where [?u :user/type ?user-type]
         [?g :group/member ?u]
         [?g :group/member ?a]
         [?a :animal/name]]
       (db-value)
       (:user-type user)))

(defmulti user->animals (fn [user] (:custom-query user)))
(defmethod user->animals :special-client [user]
  "use the special-client query")
(defmethod user->animals :default [user]
  (d/q (default-query (:user-type user))
       (db-value)))
