(ns orbitdb.feed
  (:require [orbitdb.signatures :as signatures]))

(defn feed [^js orbitdb-instance {:keys [name opts]}]
  (.feed orbitdb-instance name (clj->js opts)))

(defn remove-event [^js feed-instance hash]
  (.remove feed-instance hash))

(def add-event signatures/add-event)

(def get-event signatures/get-event)

(def iterator signatures/iterator)

(def collect signatures/collect)
