(defproject api "0.1.0-SNAPSHOT"
  :min-lein-version "2.0.0"
  :description "Optly API"
  :url "https://github.com/optly/api"
  :license {:name "Eclipse Public License"
            :url "https://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljfmt "0.5.6"]
            [lein-ring "0.10.0"]
            [lein-codox "0.10.2"]
            [drift "1.5.3"]]
  :dependencies [[org.clojure/tools.trace "0.7.9"]
                 [org.postgresql/postgresql "9.4.1212"]
                 [org.clojure/java.jdbc "0.7.0-alpha1"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.2"]
                 [org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.5.1"]
                 [ring/ring-jetty-adapter "1.5.1"]
                 [environ "1.0.3"]]
  :ring {:handler api.core/app}
  :main ^:skip-aot api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
