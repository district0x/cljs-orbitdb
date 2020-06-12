(ns orbitdb.keyvalue)

(defn keyvalue
  "Creates and opens a keyvalue database.
  Takes an OrbitDB instance and a map with:

  `:name` or `:address` whether you want to create a new or connect to an existing database.

  `:opts`: a map of options (see `orbitdb.core/create-database`)

  Returns a js/Promise resolving to the instance of the database."
  [^js orbitdb-instance {:keys [name address opts]
                         :or {opts {}}}]
  (.keyvalue orbitdb-instance (or address name) (clj->js opts)))

(defn set-key
  "Sets a value (map) at a given key (string).
  Returns a js/Promise that resolves to hash of the entry (string)."
  [^js keyvalue-instance k v & [opts]]
  (.set keyvalue-instance k (clj->js v) (clj->js (or opts {}))))

(defn get-value
  "Returns a map with the contents of an entry (map) stored at the key (string)"
  [^js keyvalue-instance k]
  (js->clj (.get keyvalue-instance k) :keywordize-keys true))
