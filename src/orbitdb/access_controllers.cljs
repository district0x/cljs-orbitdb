(ns orbitdb.access-controllers
  (:require [goog.object :as gobj]
            ["orbit-db-access-controllers" :refer (AccessController) :as AccessControllers]))

(defn add-access-controller [^js controller]
  (.addAccessController ^js AccessControllers (clj->js {:AccessController controller}))
  AccessControllers)

(defn create-access-controller [{:keys [:type :can-append?]}]
  (let [^js CustomAccessController (fn []
                                     (this-as this
                                       this))]
    (set! (.-prototype CustomAccessController) (js/Object.create (.-prototype AccessController)))
    (set! (.. CustomAccessController -prototype -constructor) CustomAccessController)
    (set! (.-type CustomAccessController) type)
    (set! (.-create CustomAccessController) (fn [orbitdb options]
                                              (new CustomAccessController)))
    (set! (.. CustomAccessController -prototype -grant) (fn [access identity]))
    (set! (.. CustomAccessController -prototype -save) (fn [] #js {}))
    (set! (.. CustomAccessController -prototype -canAppend) (fn [entity identity-provider]
                                                              (can-append? (js->clj entity :keywordize-keys true)
                                                                           (js->clj identity-provider :keywordize-keys true))))
    CustomAccessController))

(defn supported? [type]
  (.isSupported ^js AccessControllers type))
