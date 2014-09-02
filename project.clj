;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(defproject prime/ring-middleware "1.0.0-SNAPSHOT"
  :description      "Ring middleware and configuration shared between our different web apps."

  :dependencies [ [org.clojure/clojure "1.5.1"]
                  
                  [lib-noir "0.2.0"]
                  [containium "0.1.0-SNAPSHOT" :exclusions [boxure/clojure]]
                  [ring "1.2.2" :exclusions [ring/ring-jetty-adapter]]
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
