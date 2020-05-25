(ns orbitdb.docstore
  (:require [orbitdb.signatures :as signatures]))

(defn docstore [^js orbitdb-instance {:keys [name address opts]
                                      :or {opts {}}}]
  (.docstore orbitdb-instance (or address name) (clj->js opts)))

(defn put-doc [^js docstore-instance doc]
  (.put docstore-instance (clj->js doc)))

(def get-doc signatures/get-event)

(defn query [^js docstore-instance predicate-fn]
  (js->clj (.query docstore-instance (fn [doc]
                                (predicate-fn (js->clj doc :keywordize-keys true))))
           :keywordize-keys true))
