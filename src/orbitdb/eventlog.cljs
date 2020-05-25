(ns orbitdb.eventlog
  (:require [orbitdb.signatures :as signatures]))

(defn eventlog [^js orbitdb-instance {:keys [name address opts]
                                      :or {opts {}}}]
  (.eventlog orbitdb-instance (or address name) (clj->js opts)))

(def add-event signatures/add-event)

(def get-event signatures/get-event)

(def iterator signatures/iterator)

(def collect signatures/collect)
