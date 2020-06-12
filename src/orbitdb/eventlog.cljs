(ns orbitdb.eventlog
  (:require [orbitdb.signatures :as signatures]))

(defn eventlog
  "Creates and opens an eventlog database.
   Takes an OrbitDB instance and a map with:

   `:name` (string): the database name
   or
   `:address` (string): and IPFS address of an existing eventlog database,

   `:opts`: a map of options (see `create-database`)

  Returns a js/Promise resolving to the instance of the database."
  [^js orbitdb-instance {:keys [name address opts]
                         :or {opts {}}}]
  (.eventlog orbitdb-instance (or address name) (clj->js opts)))

(def add-event
  "`(add-event database-instance data & [opts])`

  Returns a js/Promise that resolves to the hash of the entry (string)."
  signatures/add-event)

(def get-event
  "`(get-event database-instance hash)`

  Takes an OrbitDB instance and a hash of the entry (string).
  Returns a map with the contents of that entry."
  signatures/get-event)

(def iterator
  "`(iterator database-instance opts)`

  Returns an Array of Objects based on the map of options:

   `:gt` (string): Greater than, takes an item's hash.

   `:gte` (string): Greater than or equal to, takes an item's hash.

   `:lt` (string): Less than, takes an item's hash.

   `:lte` (string): Less than or equal to, takes an item's hash value.

   `:limit` (integer): Limiting the entries of result, defaults to 1, and -1 means all items.

   `:reverse` (boolean): If set to true will result in reversing the result."
  signatures/iterator)

(def collect
  "`(collect iterator)`

  Takes an iterator as the argument and evaluates it, returns a vector of results."
  signatures/collect)
