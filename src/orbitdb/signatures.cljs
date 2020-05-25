(ns orbitdb.signatures)

(defn add-event [^js database-instance data]
  (.add database-instance (clj->js data)))

(defn get-event [^js database-instance hash]
  (js->clj (.get database-instance hash) :keywordize-keys true))

(defn iterator [^js database-instance opts]
  (.iterator database-instance (clj->js opts)))

(defn collect [^js iterator]
  (js->clj (.collect iterator) :keywordize-keys true))
