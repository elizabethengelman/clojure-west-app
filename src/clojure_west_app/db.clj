(ns clojure-west-app.db
  (:require [datomic.api :as d]
            [clojure-west-app.paws-data :as data]))

(def base-dev-uri "datomic:dev://localhost:4334")

(defn db-uri [db-name]
  (str base-dev-uri "/" db-name))

(defn connection [db-name]
  (d/connect (str base-dev-uri "/" db-name)))

(defn get-all-database-names []
  (d/get-database-names (db-uri "*")))

(defn create-database [db-name]
  (d/create-database (db-uri db-name)))

(defn delete-database [db-name]
  (d/delete-database (db-uri db-name)))

(def schema-map
  (into [] (concat (read-string (slurp "db/schema20170325142643521.edn"))
                   (read-string (slurp "db/schema20170326142642520.edn")))))

(defn transact [db-name txn-data]
  (d/transact (connection db-name) txn-data))

(defn transact-schema [db-name]
  (transact db-name schema-map))

(defn transact-seed-data [db-name]
  (transact db-name data/seed-data))
