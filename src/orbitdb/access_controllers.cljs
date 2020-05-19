(ns orbitdb.access-controllers
  (:require [goog.object :as gobj]
            ["orbit-db-access-controllers" :refer (AccessController) :as AccessControllers]))

(defn add-access-controller [^js controller]
  (.addAccessController ^js AccessControllers (clj->js {:AccessController controller}))
  AccessControllers)


(defn CustomAccessController
  {:jsdoc ["@constructor"]}
  []
  (this-as this
    ;; (.call AccessController this orbitdb options)
    this))

(defn create-access-controller []
  (let []

;; (prn )

    (set! (.-prototype CustomAccessController) (js/Object.create (.-prototype AccessController)))

    (set! (.. CustomAccessController -prototype -constructor) CustomAccessController)

    (set! (.-type CustomAccessController) "othertype")

    (set! (.-create CustomAccessController) (fn [orbitdb options]
                                              (prn "create")
                                              (new CustomAccessController)))

    (set! (.. CustomAccessController -prototype -grant) (fn [access identity]))

    (set! (.. CustomAccessController -prototype -save) (fn [] #js {}))

    (set! (.. CustomAccessController -prototype -canAppend) (fn [entity identity-provider]
                                                              (prn "can append?")
                                                              true))

    CustomAccessController))





















#_(defn- CustomAccessController
  {:jsdoc ["@constructor"]}
  [orbitdb options]
  (this-as this
    (.call AccessController this orbitdb options)
    this))

#_(defn create-access-controller []
  (gobj/extend
      ;; inheritance
      (.-prototype CustomAccessController)
      (.-prototype AccessController)

    ;; methods
    #js {:canAppend (fn [entry identity-provider]
                      (this-as this
                        ;; this is CustomAccessController instance
                        true))})

    ;; static properties
    (set! (.. CustomAccessController -type) "othertype")

    (set! (.. CustomAccessController -create) (fn [db options]
                                                (new CustomAccessController db (clj->js {}))))
  CustomAccessController)

#_(defn create-access-controller []
  (let [type "othertype"
        ac-obj #js {:create (fn [db options]
                              (js/Promise.resolve true))
                    :type type}]

    (set! (.-prototype ac-obj) (.-prototype AccessController))

    (set! (.. CustomAccessController -prototype -type)
          (fn [] type))

    (set! (.. CustomAccessController -prototype -canAppend)
          (fn [entry identity-provider]
            (js/Promise.resolve true)))

    ac-obj))

#_(defn create-access-controller []

  (set! (.. CustomAccessController -prototype)
        (js/Object.create (.-prototype AccessController)))

  (set! (.. CustomAccessController -prototype -constructor)
        AccessController)

  (set! (.. CustomAccessController -type)
        "othertype")

  (set! (.. CustomAccessController -create)
              (fn [orbitdb, options]))

  (set! (.. CustomAccessController -prototype -canAppend)
        (fn [entry identity-provider]

          (js/console.log "@@@ canAppend" )

          (js/Promise.resolve false)))

  CustomAccessController)

(defn supported? [type]
  (.isSupported ^js AccessControllers type))
