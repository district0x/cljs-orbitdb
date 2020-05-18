(ns orbitdb.access-controllers
  (:require [goog.object :as gobj]
            ["orbit-db-access-controllers" :refer (AccessController) :as AccessControllers]))

(defonce access-controllers ^js AccessControllers)

(defn add-access-controller [^js controller]
  (.addAccessController access-controllers (clj->js {:AccessController controller})))

(defn create-access-controller []
  (let [ac-obj #js {:type "othertype"
                    #_:canAppend #_(fn [entry identity-provider]

                                     (prn "entry" entry)

                                     true)}]
    (set! (.-prototype ac-obj) (.-prototype AccessController))

    (set! (.. ac-obj -prototype -canAppend)
          (fn [entry identity-provider]

            (prn "entry" entry)

            true))


    #_    (gobj/extend
              (.-prototype ac-obj)
            (.-prototype AccessController)

            #js {:canAppend (fn [entry identity-provider]
                              (js/console.log "@@@ canappend" entry)
                              (js/Promise.resolve true))}

            #js {:grant (fn [access identity]
                          )}

            )

    ac-obj))
