(ns ^:no-doc tests.orbitdb-tests
  (:require [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]
            [cljs.nodejs :as nodejs]
            [cljs.test :refer-macros [async deftest is]]
            [orbitdb.access-controllers :as access-controllers]
            [orbitdb.core :as orbitdb]
            [orbitdb.counter :as counter]
            [orbitdb.docstore :as docstore]
            [orbitdb.eventlog :as eventlog]
            [orbitdb.feed :as feed]
            [orbitdb.keyvalue :as keyvalue]))

(nodejs/enable-util-print!)

(defn rand-data []
  (let [[creature & _] (shuffle ["🐙" "🐷" "🐬" "🐞" "🐈" "🙉"  "🐸"  "🐓"])
        id (str (random-uuid))]
    (prn id creature)
    {:creature creature
     :id id}))

(defonce this-orbitdb (atom nil))

(deftest test-eventlog
  (async done
         (go
           (let [orbitdb-instance (<p! (orbitdb/create-instance {:ipfs-host "http://localhost:5001"}))
                 my-id (-> orbitdb-instance .-identity .-id)
                 db (<p! (-> (orbitdb/create-database orbitdb-instance {:name "creatures"
                                                                        :type :eventlog
                                                                        :opts {:accessController {:write [my-id]}
                                                                               :directory "./orbitdb/test.eventlog"
                                                                               :overwrite true
                                                                               :replicate false}})))
                 db-address (orbitdb/address db)
                 same-db (<p! (eventlog/eventlog orbitdb-instance {:address db-address}))
                 _ (<p! (eventlog/add-event db (rand-data) {:pin false}))
                 _ (<p! (eventlog/add-event db (rand-data) {:pin false}))
                 _ (<p! (eventlog/add-event db (rand-data) {:pin false}))
                 _ (<p! (eventlog/add-event db (rand-data) {:pin false}))
                 last-hash (<p! (eventlog/add-event db (rand-data)))
                 first-creature (eventlog/get-event db last-hash)
                 ;; iterator
                 all-creatures (-> (eventlog/iterator db {:limit -1}) eventlog/collect)
                 last-creature (loop [i 1
                                      creature first-creature]
                                 (if (= 4 i)
                                   creature
                                   (do
                                     (recur (inc i)
                                            (eventlog/get-event db (:next creature))))))]
             (is db)
             (is (= (.-root db-address) (.-root (orbitdb/address same-db))))
             (is (= (.-path db-address) (.-path (orbitdb/address same-db))))
             (is (= 5 (count all-creatures)))
             (is (= (-> all-creatures reverse last)
                    last-creature))
             (is (-> last-creature :payload :value :creature))
             (orbitdb/disconnect orbitdb-instance)
             (done)))))

(deftest test-access-controllers-allow
  (async done
         (go
           (let [my-controller (access-controllers/create-access-controller {:type "mytype"
                                                                             ;; NOTE: entity identity-provider
                                                                             ;; IdentityProvider https://github.com/orbitdb/orbit-db-identity-provider/blob/master/src/identity-provider-interface.js
                                                                             :can-append? (fn [_ _]
                                                                                            true)})
                 controllers (access-controllers/add-access-controller my-controller)
                 orbitdb-instance (<p! (orbitdb/create-instance {:ipfs-host "http://localhost:5001"
                                                                 :opts {:AccessControllers controllers}}))
                 db (<p! (-> (orbitdb/create-database orbitdb-instance {:name "kvstore"
                                                                        :type :keyvalue
                                                                        :opts {:accessController {:type "mytype"}
                                                                               :directory "./orbitdb/test.kvstore"
                                                                               :overwrite true
                                                                               :replicate false}})
                             (.catch (fn [error]
                                       (prn "ERROR" error)))))
                 _ (<p! (-> (keyvalue/set-key db "fu" {:fu "bar"})
                            (.then (fn [hash]
                                     hash))
                            (.catch (fn [error]
                                      error))))
                 val (keyvalue/get-value db "fu")]
             (is db)
             (is (access-controllers/supported? "mytype"))
             (is (= {:fu "bar"} val))
             (orbitdb/disconnect orbitdb-instance)
             (done)))))

(deftest test-access-controllers-deny
  (async done
         (go
           (let [my-controller (access-controllers/create-access-controller {:type "mytype"
                                                                             :can-append? (fn [_ _]
                                                                                            false)})
                 controllers (access-controllers/add-access-controller my-controller)
                 orbitdb-instance (<p! (orbitdb/create-instance {:ipfs-host "http://localhost:5001"
                                                                 :opts {:AccessControllers controllers}}))
                 db (<p! (-> (orbitdb/create-database orbitdb-instance {:name "kvstore"
                                                                        :type :keyvalue
                                                                        :opts {:accessController {:type "mytype"}
                                                                               :directory "./orbitdb/test.kvstore"
                                                                               :overwrite true
                                                                               :replicate false}})
                             (.catch (fn [error]
                                       (prn "ERROR" error)))))
                 response (<p! (-> (keyvalue/set-key db "fu" "bar")
                                   (.catch (fn [error]
                                             error))))]
             (is db)
             (is (access-controllers/supported? "mytype"))
             (is (instance? js/Error response))
             (orbitdb/disconnect orbitdb-instance)
             (done)))))

(deftest test-feedstore
  (async done
         (go
           (let [orbitdb-instance (<p! (orbitdb/create-instance {:ipfs-host "http://localhost:5001"}))
                 db (<p! (orbitdb/create-database orbitdb-instance {:name "posts"
                                                                    :type :feed
                                                                    :opts {:directory "./orbitdb/test.feedstore"
                                                                           :overwrite true
                                                                           :replicate false}}))
                 _ (<p! (feed/add-event db {:title "Hello" :content "World"}))
                 hash2 (<p! (feed/add-event db {:title "Fu" :content "Bar"}))
                 _ (<p! (feed/add-event db {:title "Foo" :content "Bar"}))
                 {{:keys [value]} :payload} (feed/get-event db hash2)
                 _ (<p! (feed/remove-event db hash2))
                 posts (map #(-> % :payload :value)
                            (-> (feed/iterator db {:limit -1}) eventlog/collect))]
             (is db)
             (is (= {:title "Fu" :content "Bar"} value))
             (is (= 2 (count posts)))
             (is (= #{{:content "World", :title "Hello"} {:content "Bar", :title "Foo"}} (set posts)))
             (orbitdb/disconnect orbitdb-instance)
             (done)))))

(deftest test-docstore
  (async done
         (go
           (let [orbitdb-instance (<p! (orbitdb/create-instance {:ipfs-host "http://localhost:5001"}))
                 db (<p! (orbitdb/create-database orbitdb-instance {:name "docs"
                                                                    :type :docstore
                                                                    :opts {:directory "./orbitdb/test.docstore"
                                                                           :overwrite true
                                                                           :replicate false}}))
                 id (str (random-uuid))
                 _ (<p! (docstore/put-doc db {:_id id :doc "bar"  :views 10}))
                 _ (<p! (docstore/put-doc db {:_id (str (random-uuid)) :doc "fu"  :views 1}))
                 _ (<p! (docstore/put-doc db {:_id (str (random-uuid)) :doc "foo"  :views 5}))
                 bar2-id (str (random-uuid))
                 _ (<p! (docstore/put-doc db {:_id bar2-id :doc "bar" :views 6}))
                 _ (<p! (docstore/del-doc db bar2-id))
                 bar (docstore/get-doc db id)
                 all (docstore/get-doc db "")
                 >5 (docstore/query db (fn [doc] (< 5 (:views doc))))]
             (is db)
             (is (= "bar" (-> bar first :doc)))
             (is (= 3 (count all)))
             (is (= 1 (count >5)))
             (orbitdb/disconnect orbitdb-instance)
             (done)))))

(deftest test-counter
  (async done
         (go
           (let [orbitdb-instance (<p! (orbitdb/create-instance {:ipfs-host "http://localhost:5001"}))
                 db (<p! (orbitdb/create-database orbitdb-instance {:name "play_count"
                                                                    :type :counter
                                                                    :opts {:directory "./orbitdb/test.counter"
                                                                           :overwrite true
                                                                           :replicate false}}))
                 count-before (counter/value db)
                 _ (<p! (counter/increase db))
                 count-after (counter/value db)]
             (is db)
             (is (= 0 count-before))
             (is (= 1 count-after))
             (orbitdb/disconnect orbitdb-instance)
             (done)))))
