(ns clojure-west-app.query
  (:require [datomic.api :as d]
   :require [clojure-west-app.db :as db]))

(def db-name "gene-and-louise")

(defn db-value []
  (d/db (db/connection db-name)))

(defn default-query [group-name]
  `[:find ?a
    :where
     [?a :animal/name]
     [?g :group/name ~group-name]
     [?g :group/member ?a]])

(defmulti user->animals (fn [user] (:custom-query user)))
(defmethod user->animals :special-client [user]
  "use the special-client query")
(defmethod user->animals :default [user]
  (d/q (default-query (:group-name user))
       (db-value)))

