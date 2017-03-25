# clojure-west-app

FIXME

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2017 FIXME


-started a ring application, just in case i wanted to create a web app.
it may only an endpoint to query for now

-not sure if i should set this up with the peer library (which is how i
did it before) or, if using the client library would be easier/a better
fit

-got the datomic starter, registered, downloaded it
- copied /Users/elizabethengelman/datomic/datomic-pro-0.9.5561/config/samples/dev-transactor-template.properties
and added my license key

these are run from the directory where datomic is: /Users/elizabethengelman/datomic/datomic-pro-0.9.5561

http://docs.datomic.com/dev-setup.html

(require '[datomic.api :as d])
(def db-uri "datomic:dev://localhost:4334/hello")
(d/create-database db-uri)

for some reason, i am having trouble doing this from my own repl in my
clojure-west-app project
(it seems to work when running the commands from the bin/repl in the
base datomic directory)
  -- when i have the version in project.clj as  [com.datomic/datomic-free
"0.9.5561"]] i can run start the repl, but i get this error: IllegalArgumentExceptionInfo :db.error/unsupported-protocol Unsupported protocol :dev  datomic.error/arg (error.clj:57)
  -- however, when i change the version to [com.datomic/datomic-pro "0.9.5561"]] i can't even start the repl i get this issue: Could not find artifact com.datomic:datomic-pro:jar:0.9.5561 in central (https://repo1.maven.org/maven2/)
Could not find artifact com.datomic:datomic-pro:jar:0.9.5561 in clojars (https://clojars.org/repo/)
This could be due to a typo in :dependencies or network issues.

do i need to authenticate, because this is a "pro" version?
----------------

then it says: Once you've created the database, you can interact with it via the Datomic Peer or Client Libraries.

so, this is where i would need to decide if i am going to use the peer
or client?

integrating the peer library: http://docs.datomic.com/integrating-peer-lib.html

peer server: http://docs.datomic.com/peer-server.html

-----------------
CLIENT
using a client, http://docs.datomic.com/dev-setup.html#client
you have to have a db created, start a transactor, and then start the
peer server:

bin/run -m datomic.peer-server -h localhost -p 8998 -a myaccesskey,mysecret -d hello,datomic:dev://localhost:4334/hello

then import the datomic client library and clojure core asyn in a repl
that includes the Datomic Client library - how do i include this in my
own repl/project? for now, i'm just using bin/repl

In addition to using the included REPL, you can include the Datomic
Client Library in your Clojure project....http://docs.datomic.com/project-setup.html


-------------------
PEER LIBRARY
this guy says "in order to use the peer library as a project dependency,
it's necessary to configure Maven (pom.xml) or Leiningen (project.clj)
to use my.datomic.com login credentials."

https://github.com/jamesmartin/datomic-tutorial
but i haven't been able to find that anywhere in the datomic docs

though there is mention of credentials in my account... but it wasn't
super clear what to do with them... 
https://github.com/technomancy/leiningen/blob/master/doc/DEPLOY.md#authentication
    https://gpgtools.org/
https://www.gnupg.org/

now the repl works!

to reload a ns in the repl without stopping/starting the repl:
(use '[clojure-west-app.db :as db] :reload)
or (use '[clojure-west-app.db :as db] :reload-all)

------------------------
-to run the transactor: bin/transactor config/dev-transactor.properties
this starts the dev transactor process on port 4334
base URI: datomic:dev://localhost:4334/<DB-NAME>

-to start the console:
bin/console -p 8080 dev datomic:dev://localhost:4334/

-to interact with the db in the repl
(require '[clojure-west-app.db :as db])
