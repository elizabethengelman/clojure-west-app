(defproject clojure-west-app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [hiccup "1.0.2"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [com.h2database/h2 "1.3.170"]
                 [com.datomic/datomic-pro "0.9.5561"]]
  :repositories  {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                    :creds :gpg}}
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler clojure-west-app.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
