(ns orbitdb.access-controllers
  (:require [goog.object :as gobj]
            ["orbit-db-access-controllers" :refer (AccessController) :as AccessControllers]))

(defonce access-controllers ^js AccessControllers)

(defn add-access-controller [^js controller]
  (.addAccessController access-controllers (clj->js {:AccessController controller})))

(defn- CustomAccessController [orbitdb options]
  (this-as this
    (.call AccessController this orbitdb options)))

(defn create-access-controller []

  (set! (.. CustomAccessController -prototype)
        (js/Object.create (.-prototype AccessController)))

  (set! (.. CustomAccessController -prototype -constructor)
      AccessController)

  (set! (.. CustomAccessController -type)
      "othertype")

  CustomAccessController)

#_(defn create-access-controller []
  (let [ac-obj #js {:type "othertype"}]

    (set! (.-prototype ac-obj) (.-prototype AccessController))

    (set! (.. ac-obj -prototype -canAppend)
          (fn [entry identity-provider]

            (js/console.log "@@@ canAppend" )

            (js/Promise.resolve false)))

    ac-obj))

(defn supported? [type]
  (.isSupported ^js AccessControllers type))
