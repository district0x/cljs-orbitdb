(ns orbitdb.feed
  (:require [orbitdb.signatures :as signatures]))

(defn feed
  "Creates and opens a feed database.
  Takes an OrbitDB instance and a map with:

  `:name` or `:address` whether you want to create a new or connect to an existing database.

  `:opts`: a map of options (see `orbitdb.core/create-database`)

  Returns a js/Promise resolving to the instance of the database."
  [^js orbitdb-instance {:keys [name address opts]
                         :or {opts {}}}]
  (.feed orbitdb-instance (or address name) (clj->js opts)))

(defn remove-event
  "Removes an item pointed to by the hash value.
  Returns a Promise that resolves to the hash of the deleted entry."
  [^js feed-instance hash]
  (.remove feed-instance hash))

(def add-event
  "Takes a OrbitDB feed database instance and a map with the data to store.
  Returns a js/Promise that resolves to the hash of the entry (string)."
  signatures/add-event)

(def get-event
  "Takes an OrbitDB feed database instance and a hash of an entry (string).
  Returns a map with the contents of that entry."
  signatures/get-event)

(def iterator
  "Returns an Array of Objects based on the map of options:

   `:gt` (string): Greater than, takes an item's hash.
   `:gte` (string): Greater than or equal to, takes an item's hash.
   `:lt` (string): Less than, takes an item's hash.
   `:lte` (string): Less than or equal to, takes an item's hash value.
   `:limit` (integer): Limiting the entries of result, defaults to 1, and -1 means all items.
   `:reverse` (boolean): If set to true will result in reversing the result."
  signatures/iterator)

(def collect
  "Takes an iterator as the argument and evaluates it, returns a vector of results."
  signatures/collect)
