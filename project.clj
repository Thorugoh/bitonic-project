(defproject bitonic-project "0.1.0-SNAPSHOT"
  :description "API to generate Bitonic Sequences"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [http-kit "2.7.0"]
                 [compojure "1.7.0"]
                 [com.taoensso/carmine "3.2.0"]
                 [cheshire "5.12.0"]]
  :main ^:skip-aot bitonic-project.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})