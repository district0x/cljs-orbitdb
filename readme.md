# cljs-orbitdb

[![Build status](https://circleci.com/gh/district0x/cljs-orbitdb/tree/master.svg?style=shield)](https://circleci.com/gh/district0x/cljs-orbitdb/tree/master)
[![Clojars Project](https://img.shields.io/clojars/v/cljs-orbitdb.svg)](https://clojars.org/cljs-orbitdb)
[![cljdoc badge](https://cljdoc.org/badge/cljs-orbitdb/cljs-orbitdb)](https://cljdoc.org/d/cljs-orbitdb/cljs-orbitdb/CURRENT)

ClojureScript library for [OrbitDB](https://github.com/orbitdb/orbit-db/blob/master/API.md).

## Setup

OrbitDB requires a running IPFS node, compatible with IPFS version > 5.x.x and with the EXPERIMENTAL pub-sub featured turned on an.

Here's how you can run an ipfs node using a [docker](https://www.docker.com/) container:

```bash
docker run --name ipfs-daemon -v /home/$USER/ipfs-docker:/data/ipfs -p 8080:8080 -p 5001:5001 ipfs/go-ipfs:latest daemon --enable-pubsub-experiment
```

Alternatively you can use [docker-compose](https://docs.docker.com/compose/):

```yaml
version: "3"
services:

  ipfs-daemon:
    image: ipfs/go-ipfs:v0.5.1
    command: ["daemon", "--enable-pubsub-experiment"]
    container_name: ipfs-daemon
    volumes:
      - /home/$USER/ipfs-docker:/data/ipfs
    ports:
      - 8080:8080
      - 5001:5001
```

This will expose nodes ports 5001 and 8080 to the hosts.

## Create an OrbitDB instance

Use `create-instance` function from `orbitdb.core` namespace:

```clojure
(create-instance {:ipfs-host "http://localhost:5001"})
```

This funciton returns a js/Promise which evaluates to an OrbitDB object representing the instance.

## Create a database

First, choose the data model you want to use. The available data models are:

- *keyvalue*: A key-value store.
- *eventlog*: Immutable (append only) database.
- *feed*: a mutable log database.
- *docstore*: a documents database. Useful for structured data.
- *counter*: Useful for storing count data.

You can use a general-purpose `create-database` function from the `orbitdb.core` namespace or a function from a namespace which corresponds to that specific database.

Here's how you would create an *eventlog* database instance:

```clojure
(create-database orbitdb-instance {:name "my-eventlog"
                                   :type :eventlog
                                   :opts {:directory "./orbitdb/my.eventlog"}})
```

Alternatively, using the `eventlog` function in `orbitdb.eventlog` namespace:

```clojure
(eventlog orbitdb-instance {:name "my-eventlog"
                            :opts {:directory "./orbitdb/my.eventlog"}})
```

Here is how you can persist some data using `add-event` function in `orbitdb.eventlog` namespace:

```clojure
(add-event eventlog-instance {:fu "bar"})
```

This function returns a js/Promise which evaluates to the hash of the persisted entry.
Other database types have corresponding functions in their respective namespaces.

*Note*

_OrbitDB does not automatically pin content to IPFS. To pin the entry, pass the optional `{ pin: true }` in the options:_

```clojure
(add-event eventlog-instance {:fu "bar"} {:pin true})
```

## Acces Controllers

You can specify the peers that have write-access to a database or write a custom access controller.

*Note*

_OrbitDB currently supports only dynamically adding write-access, meaning that write-access cannot be revoked once added_

If you simply want to specify a whitelist for write access, you can do it by passing a vector of of public keys when creating the database.
This example gives write priviledges to the node's public key (your key) and to another peer identified by the public key starting with `042c...`:

```clojure
(create-database orbitdb-instance {:name "creatures"
                                   :type :eventlog
                                   :opts {:accessController {:write [(-> ^js orbitdb-instance .-identity .-id)
                                                                     042c07044e7ea51a489c02854db5e09f0191690dc59db0afd95328c9db614a2976e088cab7c86d7e48183191258fc59dc699653508ce25bf0369d67f33d5d77839]}}})
```

If you want to create a database with a public access you can do so by passing a wildcard:

```clojure
(eventlog orbitdb-instance {:name "my-eventlog"
                            :opts {:accessController {:write ["*"]}}})
```

You can also create a custom access controller, which regulates who and what can be written to your database, with a predicate function with two arguments:
- `entry`: the persisted entry
- `identity-provider`: an instance of the [IdentityProvider](https://github.com/orbitdb/orbit-db-identity-provider)

The functions for creating and managing custom access controllers are in the `orbitdb.access-controllers` namespace.
Here is how you can create an access controller:

```clojure
(def my-controller (create-access-controller {:type "mytype"
                                              :can-append? (fn [entry identity-provider]
                                                             (and (= "bar" (:fu entry))
                                                                  (= (-> ^js orbitdb-instance .-identity .-id) (-> ^js identity-provider .-id))))}))
```

Next you need to add it:

```clojure
(def access-controllers (add-access-controller my-controller))
```

The `add-access-controller` function returns the instance of AccessControllers object, which is a singleton. You need to pass it in the options map when creating an instance of the OrbitDB:

```clojure
orbitdb-instance (orbitdb/create-instance {:ipfs-host "http://localhost:5001"
                                           :opts {:AccessControllers access-controllers}})
```

To use the controller with a database, specify it when creating that database:

```clojure
(create-database orbitdb-instance {:name "creatures"
                                   :type :eventlog
                                   :opts {:accessController {:type ["my-type"]}}})
```
