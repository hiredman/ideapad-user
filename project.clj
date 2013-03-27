(defproject com.thelastcitadel/ideapad-user "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.cemerick/friend "0.1.3"]
                 [ring/ring-core "1.1.8"]
                 [compojure "1.1.5"]
                 [com.cemerick/friend "0.1.3"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [org.clojure/java.jdbc "0.2.3"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler com.thelastcitadel.ideapad-user/handler})
