(ns orbitdb.keyvalue)

(defn keyvalue [^js orbitdb-instance {:keys [name address opts]
                                      :or {opts {}}}]
  (.keyvalue orbitdb-instance (or address name) (clj->js opts)))

(defn set-key [^js keyvalue-instance k v]
  (.set keyvalue-instance k (clj->js v)))

(defn get-value [^js keyvalue-instance k]
  (js->clj (.get keyvalue-instance k) :keywordize-keys true))
