;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(defproject prime/ring-middleware "1.0.0-SNAPSHOT"
  :description      "Ring middleware and configuration shared between our different web apps."

  :dependencies [ 
                  [lib-noir "0.2.0" :exclusions [ring cheshire org.clojure/clojure]]
                  [containium "0.1.0-SNAPSHOT" :exclusions [boxure clojurewerkz/elastisch com.sonian/elasticsearch-zookeeper http-kit org.apache.kafka/kafka_2.9.2 org.clojure/tools.reader org.elasticsearch/elasticsearch jline org.apache.httpcomponents/httpclient lein-light-nrepl info.sunng/ring-jetty9-adapter com.maitria/packthread com.draines/postal cc.qbits/alia boxure/clojure org.clojure/core.async org.apache.cassandra/cassandra-all]]
                  [ring/ring-core  "1.3.2" :exclusions [org.clojure/tools.reader org.clojure/clojure]]
                  [ring/ring-devel "1.3.2"]
                  [prone "0.4.0"]
                ]

  :pom-plugins [[com.theoryinpractise/clojure-maven-plugin "1.3.15"
                 {:extensions "true"
                  :configuration ([:sourceDirectories [:sourceDirectory "src"]]
                                  [:temporaryOutputDirectory "true"])
                  :executions [:execution
                               [:id "compile-clojure"]
                               [:phase "compile"]
                               [:goals [:goal "compile"]]]}]]
  :resource-paths   ["resources"]
  :jar-files        [["resources" ""]]
  :war-files        [["resources" ""]])
