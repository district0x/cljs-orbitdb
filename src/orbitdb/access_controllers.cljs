(ns orbitdb.access-controllers
  (:require [goog.object :as gobj]
            ["orbit-db-access-controllers" :refer (AccessController OrbitDBAccessController) :as AccessControllers]))

(defn add-access-controller [^js controller]
  (.addAccessController ^js AccessControllers (clj->js {:AccessController controller}))
  AccessControllers)

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

  (set! (.. CustomAccessController -create)
              (fn []))

  (set! (.. CustomAccessController -prototype -canAppend)
        (fn [entry identity-provider]

          (js/console.log "@@@ canAppend" )

          (js/Promise.resolve false)))

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
