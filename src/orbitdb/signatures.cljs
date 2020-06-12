(ns ^:no-doc orbitdb.signatures)

(defn add-event [^js database-instance data & [opts]]
  (.add database-instance (clj->js data) (clj->js (or opts {}))))

(defn get-event [^js database-instance hash]
  (js->clj (.get database-instance hash) :keywordize-keys true))

(defn iterator [^js database-instance opts]
  (.iterator database-instance (clj->js opts)))

(defn collect [^js iterator]
  (js->clj (.collect iterator) :keywordize-keys true))
