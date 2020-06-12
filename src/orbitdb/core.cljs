(ns orbitdb.core
  (:require ["ipfs-http-client" :as IpfsClient]
            ["orbit-db" :as OrbitDB]))

(defn create-instance
  "Creates and returns an instance of OrbitDB
   Takes a map with:

   `:ipfs-host` (string): a URL of a running ipfs node.

   `:opts` is a map with following optional values:
     `:directory` (string): path to be used for the database files. Defaults to './orbitdb'.
     `:peerId` (string): Defaults to the base58 string of the ipfs peer id.
     `:keystore` (Keystore Instance) : By default creates own instance of Keystore, but a custom one can be passed here instead.
     `:cache` (Cache Instance) : By default creates own instance of Cache, but a custom one can be passed here instead.
     `:identity` (Identity Instance): By default creates own an instance of Identity, but a custom one can be passed here instead.
     `:offline` (boolean): Start the OrbitDB instance in offline mode. Databases are not be replicated when the instance is started in offline mode. If the OrbitDB instance was started offline mode and you want to start replicating databases, the OrbitDB instance needs to be re-created. Defaults to false.
     `:AccessControllers` (AccessControllers instance): Optional, custom controllers."
  [{:keys [ipfs-host opts]
    :or {opts {}}}]
  (.createInstance ^js OrbitDB (^js IpfsClient ipfs-host) (clj->js opts)))

(defn create-database
  "Creates and opens an OrbitDB database.
   Returns a js/Promise.
   Takes an OrbitDB instance and a map with:

  `name` (string) : the database name,

  `type` (keyword): a supported database type:
    - `:eventlog`
    - `:keyvalue`
    - `:feed`
    - `:docstore`
    - `:counter`

  `opts` is a map with:
    `:accessController`: a map with single keyword:

      `:write` (vector of strings): a whitelist of hex encoded public keys with write access to the database. You can specify a public database by passing ['*']

      or

      `:type` (string): a defined and registered AccessController type

    `:directory` (string): path to be used for the database files.

    `:overwrite` (boolean): Overwrite an existing database. Default: false.

    `:replicate` (boolean): Replicate the database with peers, requires IPFS PubSub. Default: true.

    `:meta`: a map with the manifest of the database. Default: nil."
  [^js orbitdb-instance {:keys [name type opts]}]
  (.create orbitdb-instance name (cljs.core/name type) (clj->js opts)))

(defn address
  "Takes an OrbitDB database instance created with `create-database`.
  Returns the IPFS address of that database."
  [^js database]
  (.-address database))

(defn disconnect
  "Closes databases, connections, pubsub and resets orbitdb state.
  Returns a js/Promise."
  [^js orbitdb]
  (.disconnect orbitdb))
