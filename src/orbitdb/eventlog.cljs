(ns orbitdb.eventlog)

(defn eventlog [^js  orbitdb-instance {:keys [name address]}]
  (.eventlog orbitdb-instance (or name address)))

(defn add-event [^js  eventlog-instance data]
  (.add eventlog-instance (clj->js data)))

(defn get-event [^js eventlog-instance hash]
  (js->clj (.get eventlog-instance hash) :keywordize-keys true))

(defn iterator [^js eventlog-instance opts]
  (.iterator eventlog-instance (clj->js opts)))

(defn iterator-collect [^js iterator]
  (js->clj (.collect iterator) :keywordize-keys true))
