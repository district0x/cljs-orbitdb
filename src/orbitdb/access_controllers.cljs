(ns orbitdb.access-controllers
  (:require ["orbit-db-access-controllers"
             :as
             AccessControllers
             :refer
             [AccessController]]))

(defn add-access-controller
  "Add a custom AccessController to the AccessControllers (a mutable singleton).
  Returns the updated AccessControllers instance that can be passed passing to OrbitDB.

  *NOTE:*
  The AccessController object is not directly exposed by this library, `add-access-controller` is the only function which returns it."
  [^js controller]
  (.addAccessController ^js AccessControllers (clj->js {:AccessController controller}))
  AccessControllers)

(defn create-access-controller
  "Creates and returns a custom AccessController object.
  Takes a map with:

  `:type` (string): name which uniqely identifies the controller.

  `:can-append`: a function with a followinf signature `fn [entity identity-provider]`"
  [{:keys [:type :can-append?]}]
  (let [^js CustomAccessController (fn []
                                     (this-as this
                                       this))]
    (set! (.-prototype CustomAccessController) (js/Object.create (.-prototype AccessController)))
    (set! (.. CustomAccessController -prototype -constructor) CustomAccessController)
    (set! (.-type CustomAccessController) type)
    (set! (.-create CustomAccessController) (fn
                                              ;; NOTE : args orbitdb options
                                              [_ _]
                                              (new CustomAccessController)))
    (set! (.. CustomAccessController -prototype -grant) (fn
                                                          ;; NOTE : args access identity
                                                          [_ _]))
    (set! (.. CustomAccessController -prototype -save) (fn [] #js {}))
    (set! (.. CustomAccessController -prototype -canAppend) (fn [entity identity-provider]
                                                              (can-append? (js->clj entity :keywordize-keys true)
                                                                           (js->clj identity-provider :keywordize-keys true))))
    CustomAccessController))

(defn supported?
  "Returns `true` when AccessController identified as `type` is added to the AccessControllers object."
  [type]
  (.isSupported ^js AccessControllers type))
