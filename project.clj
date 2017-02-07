(defproject api "0.1.0"
  :min-lein-version "2.6.1"
  :description "Optly API"
  :url "https://github.com/optly/api"
  :license {:name "Eclipse Public License"
            :url "https://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljfmt "0.5.6"]
            [lein-ring "0.10.0"]
            [lein-codox "0.10.2"]
            [drift "1.5.3"]]
  :dependencies [[clj-time "0.13.0"]
                 [metosin/compojure-api "1.1.10"]
                 [drift "1.5.3"]
                 [honeysql "0.8.2"]
                 [com.cemerick/url "0.1.1"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.2"]
                 [org.clojure/tools.trace "0.7.9"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.postgresql/postgresql "9.4.1212.jre7"]
                 [org.clojure/java.jdbc "0.7.0-alpha1"]
                 [org.clojure/clojure "1.8.0"]
                 [cheshire "5.7.0"]
                 [prismatic/schema-generators "0.1.0"]
                 [ring-logger "0.7.7"]
                 [ring/ring-mock "0.3.0"]
                 [ring/ring-core "1.5.1"]
                 [ring/ring-jetty-adapter "1.5.1"]
                 [environ "1.1.0"]]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]
  :ring {:handler api.core/handler}
  :main ^:skip-aot api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :resource-paths ["swagger-ui"]}
             :test {:dependencies [[ring/ring-mock "0.3.0"]]}
             :dev {:dependencies [[ring/ring-mock "0.3.0"]]}})
