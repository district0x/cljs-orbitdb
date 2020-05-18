(ns orbitdb.keyvalue)

(defn keyvalue [^js orbitdb-instance {:keys [name opts]}]
  (.keyvalue orbitdb-instance name (clj->js opts)))

(defn set-key [^js eventlog-instance k v]
  (.set eventlog-instance k (clj->js v)))

(defn get-value [^js eventlog-instance k]
  (js->clj (.get eventlog-instance k) :keywordize-keys true))
