#!/usr/bin/env bash

# sudo rm -rf /home/$USER/cljdoc-docker
# mkdir -p /home/$USER/cljdoc-docker

# TODO : parse from defproject
version=0.0.9-SNAPSHOT

lein install

echo "---- cljdoc preview: ingesting"

docker run --rm \
       -v "$PWD:/cljs-orbitdb" \
       -v "$HOME/.m2:/root/.m2" \
       -v /home/$USER/cljdoc-docker:/app/data --entrypoint "clojure" \
       cljdoc/cljdoc -A:cli ingest -p cljs-orbitdb/cljs-orbitdb -v "$version" \
       --git /cljs-orbitdb \
       --rev "feature/cljdoc"

docker run --rm
       -v $PWD:/cljs-orbitdb \
       -v "$HOME/.m2:/root/.m2" \
       -v /tmp/cljdoc:/app/data \
       --entrypoint "clojure" \
       cljdoc/cljdoc -A:cli ingest -p metosin/muuntaja -v $version \
       --git /cljs-orbitdb

echo "---- cljdoc preview: starting server on port 8000"

docker run --rm -p 8000:8000 -v /home/$USER/cljdoc-docker:/app/data cljdoc/cljdoc
# http://localhost:8000/d/cljs-orbitdb/cljs-orbitdb/CURRENT
