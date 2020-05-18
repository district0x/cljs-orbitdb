(ns tests.orbitdb-tests
  (:require [cljs.test :refer-macros [async deftest is use-fixtures]]
            [cljs.nodejs :as nodejs]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]
            [orbitdb.core :as orbitdb]
            [orbitdb.eventlog :as eventlog]
            [orbitdb.access-controllers :as access-controllers]

            ["CustomController" :as custom-controller :default BasicController]
            ))

(nodejs/enable-util-print!)

(defn rand-data []
  (let [[creature & _] (shuffle ["🐙" "🐷" "🐬" "🐞" "🐈" "🙉"  "🐸"  "🐓"])
        id (str (random-uuid))]
    (prn id creature)
    {:creature creature
     :id id}))

(defonce this-orbitdb (atom nil))

#_(use-fixtures :once
  {:before (fn [] (go
                    (reset! this-orbitdb (orbitdb/create-instance {:ipfs-host "http://localhost:5001"}))))
   :after (fn [])})

(deftest test-eventlog
  (async done
         (go
           (let [orbitdb-instance (<p! (orbitdb/create-instance {:ipfs-host "http://localhost:5001"}))
                 my-id (-> orbitdb-instance .-identity .-id)
                 db (<p! (orbitdb/create orbitdb-instance "creatures" :eventlog {:accessController {:write [my-id]}
                                                                                 :directory "/home/filip/orbitdb/test.eventlog"
                                                                                 :overwrite true
                                                                                 :replicate false}))
                 db-address (orbitdb/address db)
                 same-db (<p! (eventlog/eventlog orbitdb-instance {:address db-address}))
                 _ (<p! (eventlog/add-event db (rand-data)))
                 _ (<p! (eventlog/add-event db (rand-data)))
                 _ (<p! (eventlog/add-event db (rand-data)))
                 _ (<p! (eventlog/add-event db (rand-data)))
                 last-hash (<p! (eventlog/add-event db (rand-data)))
                 first-creature (eventlog/get-event db last-hash)
                 ;; iterator
                 all-creatures (-> (eventlog/iterator db {:limit -1}) eventlog/iterator-collect)
                 last-creature (loop [i 1
                                      creature first-creature]
                                 (if (= 4 i)
                                   creature
                                   (recur (inc i)
                                          (eventlog/get-event db (:next creature)))))
                 ]

             (is (= (.-root db-address) (.-root (orbitdb/address same-db))))
             (is (= (.-path db-address) (.-path (orbitdb/address same-db))))
             (is (= 5 (count all-creatures)))
             (is (= (-> all-creatures reverse last)
                    last-creature))
             (is (-> last-creature :payload :value :creature))
             (orbitdb/disconnect orbitdb-instance)
             (done)))))

(deftest test-access-controllers
  (async done
         (go
           (let [controllers access-controllers/access-controllers
                 ;; controller (new access-controllers/Foo1)
                 _ (access-controllers/add-access-controller BasicController)

                 ]

             (prn BasicController)

             (prn custom-controller)

             ;; (prn (js-keys controller))

             ;; (prn (js-keys (-> access-controllers/Foo1 #_.-prototype #_.-constructor)))

             ;; (orbitdb/disconnect orbitdb-instance)
             (done)))))
