(ns orbitdb.docstore
  (:require [orbitdb.signatures :as signatures]))

(defn docstore
  "Creates and opens a docstore database.
  Takes an OrbitDB instance and a map with:

  `:name` or `:address` whether you want to create a new or connect to an existing database.

  `:opts`: a map of options (see `orbitdb.core/create-database`)

  Returns a js/Promise resolving to the instance of the database."
  [^js orbitdb-instance {:keys [name address opts]
                         :or {opts {}}}]
  (.docstore orbitdb-instance (or address name) (clj->js opts)))

(defn put-doc
  "Returns a Promise that resolves to the hash of the entry (string)."
  [^js docstore-instance doc & [opts]]
  (.put docstore-instance (clj->js doc) (clj->js (or opts {}))))

(def get-doc
  "`(get-doc database-instance data & [opts])`

  Takes an OrbitDB docstore database instance and a hash of an entry (string).
  Returns a map with the contents of that entry."
  signatures/get-event)

(defn del-doc
  "Removes an item pointed to by the hash value.
  Returns a Promise that resolves to the hash of the entry as a String."
  [^js docstore-instance hash]
  (.del docstore-instance hash))

(defn query
  "Returns a vector of results based on a predicate function with a following signature: `fn [doc]`"
  [^js docstore-instance predicate-fn]
  (js->clj (.query docstore-instance (fn [doc]
                                       (predicate-fn (js->clj doc :keywordize-keys true))))
           :keywordize-keys true))
