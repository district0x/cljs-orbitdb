(ns orbitdb.access-controllers
  (:require [goog.object :as gobj]
            ["orbit-db-access-controllers" :refer (AccessController) :as AccessControllers]))

(defonce access-controllers ^js AccessControllers)

(defn add-access-controller [^js controller]
  (.addAccessController access-controllers (clj->js {:AccessController controller })))

;; class OtherAccessController extends AccessController {

;;   static get type () { return 'othertype' } // Return the type for this controller

;;   async canAppend(entry, identityProvider) {
;;     // logic to determine if entry can be added, for example:
;;     if (entry.payload === "hello world" && entry.identity.id === identity.id && identityProvider.verifyIdentity(entry.identity))
;;       return true

;;     return false
;;   }
;;   async grant (access, identity) {} // Logic for granting access to identity
;; }

;; (prn "OrbitDBAccessController" OrbitDBAccessController)

(defn Foo1
  {:jsdoc ["@constructor"]}
  []
  (this-as this
    this))

;; (aset)

(gobj/extend Foo1
  #js {:type (fn []
               (this-as this
                 "othertype"
                 ))}
  )

(gobj/extend
  (.-prototype Foo1)
  (.-prototype AccessController)

  ;; #js {:type (fn []
  ;;             (this-as this
  ;;               "othertype"
  ;;               ))}

  #js {:canAppend (fn [entry identity-provider]
                    (js/console.log "@@@ canappend" entry)
                    (js/Promise.resolve true))}

  #js {:grant (fn [access identity]
                    )}

  )
