(ns orbitdb.core
  (:require ["ipfs-http-client" :as IpfsClient]
            ["orbit-db" :as OrbitDB]))

(defn create-instance
  "Creates and returns an instance of OrbitDB
  opts is a map with following optional values"
  [{:keys [ipfs-host opts]
    :or {opts {}}}]
  (.createInstance ^js OrbitDB (^js IpfsClient ipfs-host) (clj->js opts)))

(defn create-database
  "Creates and opens an OrbitDB database.
  `name` (string) : the database name,
  `type` (keyword): a supported database type
  `opts`: a kv map of options"
  [^js orbitdb-instance {:keys [name type opts]}]
  (.create orbitdb-instance name (cljs.core/name type) (clj->js opts)))

(defn address [database]
  (.-address ^js database))

(defn disconnect [orbitdb]
  (.disconnect ^js orbitdb))
