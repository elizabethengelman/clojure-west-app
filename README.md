# clojure-west-app

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Description
This application goes through a series of 3 refactors/changes to the
user->animal query:
1. a simple implementation to get from the user to all of the animals
   that they are able to see
2. refactoring the previous query
  a. the first refactor splits it into 2 smaller queries where the
results of on can be used as an input for the other
  b. the second refactor uses query rules instead
3. adds another client into the mix, so now, for certain users they will
   be able to have an association directly to the animal, which still
   respecting the previous path

## Creating the dev db
```
(require '[clojure-west-app.db :as db])
(require '[clojure-west-app.query :as query])
(require '[clojure-west-app.paws-data :as data])

(db/delete-database "gene-and-louise")
(db/create-database "gene-and-louise")

(db/transact-schema "gene-and-louise")
(db/transact "gene-and-louise" data/seed-data)
```

## Creating the test db
start your transactor: `bin/transactor config/dev-transactor.properties`
run `cat test-db-setup.clj | lein repl`


## Running the specs
after creating the test db (above), run `lein test`

## To start the console
` bin/console -p 8080 dev datomic:dev://localhost:4334/`
