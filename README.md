# clojure-west-app

FIXME

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen


```
(require '[clojure-west-app.db :as db])
(require '[clojure-west-app.query :as query])
(require '[clojure-west-app.paws-data :as data])

(db/delete-database "gene-and-louise")
(db/create-database "gene-and-louise")

(db/transact-schema "gene-and-louise")
(db/transact "gene-and-louise" data/seed-data)
```
