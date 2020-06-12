(ns orbitdb.counter)

(defn counter
  "Creates and opens a counter database.
  Takes an OrbitDB instance and a map with:

  `:name` or `:address` whether you want to create a new or connect to an existing database.

  `:opts`: a map of options (see `orbitdb.core/create-database`)

  Returns a js/Promise resolving to the instance of the database."
  [^js orbitdb-instance {:keys [name address opts]
                         :or {opts {}}}]
  (.counter orbitdb-instance (or address name) (clj->js opts)))

(defn value
  "Returns a number."
  [^js counter-instance]
  (.-value counter-instance))

(defn increase
  "Increases the number by a value (optional, defaults to 1)"
  [^js counter-instance & [value]]
  (.inc counter-instance (or value 1)))
