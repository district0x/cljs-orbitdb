(defproject cljs-orbitdb "0.0.9-SNAPSHOT"
  :description "Clojurescript library for interacting with OrbitDB"
  :url "https://github.com/"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [;;[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.439"]]
  ;; :plugins [[lein-codox "0.10.7"]]
  ;; :codox {:source-paths "src/orbitdb"
  ;;         :language :clojurescript
  ;;         :namespaces [orbitdb.core
  ;;                      orbitdb.eventlog
  ;;                      orbitdb.keyvalue
  ;;                      orbitdb.access_controllers
  ;;                      orbitdb.counter
  ;;                      orbitdb.docstore
  ;;                      orbitdb.feed]}
  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]]

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["deploy"]])
