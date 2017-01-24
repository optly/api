(defproject api "0.1.0-SNAPSHOT"
  :min-lein-version "2.0.0"
  :description "Optly API"
  :url "https://github.com/optly/api"
  :license {:name "Eclipse Public License"
            :url "https://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljfmt "0.5.6"]
            [lein-ring "0.10.0"]
            [lein-codox "0.10.2"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.5.1"]
                 [ring/ring-jetty-adapter "1.5.1"]
                 [environ "1.0.3"]]
  :ring {:handler api.core/app}
  :main ^:skip-aot api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
