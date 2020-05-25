(ns orbitdb.counter)

(defn counter [^js orbitdb-instance {:keys [name address opts]
                                  :or {opts {}}}]
  (.counter orbitdb-instance (or address name) (clj->js opts)))

(defn value [^js counter-instance]
  (.-value counter-instance))

(defn increase [^js counter-instance]
  (.inc counter-instance))
