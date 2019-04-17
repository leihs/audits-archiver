(defproject leihs_audits-archiver "0.1.0-SNAPSHOT"
  :description "Download, store and remove legacy-audits from leihs"
  :url "https://github.com/leihs/audits-archiver"
  :license {:name "WTFPL"
            :url "http://www.wtfpl.net/txt/copying/"}
  :dependencies [
                 [cheshire "5.8.0"]
                 [clj-http "3.9.0"]
                 [clojure.java-time "0.3.2"]
                 [environ "1.1.0"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail javax.jms/jms com.sun.jdmk/jmxtools com.sun.jmx/jmxri]]
                 [logbug "4.2.2"]
                 [nio2 "0.2.1"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.3.7"]
                 [org.slf4j/slf4j-log4j12 "1.7.25"]
                 ]


  ; jdk 9 needs ["--add-modules" "java.xml.bind"]
  :jvm-opts #=(eval (if (re-matches #"^9\..*" (System/getProperty "java.version"))
                      ["--add-modules" "java.xml.bind"]
                      []))

  :javac-options ["-target" "1.8" "-source" "1.8"  "-Xlint:-options"]

  :main leihs.audits-archiver.main

  :resource-paths ["resources/all"]

  :source-paths ["src/all"] 

  :test-paths ["src/test"]

  :plugins [[lein-environ "1.1.0"]]

  :profiles {:dev {:env {:dev true}
                   :resource-paths ["resources/dev"]}
             :test {:env {:test true}
                    :resource-paths ["resources/test"]
                    :source-paths ["src/test"]}
             :uberjar {:aot :all
                       :resource-paths ["resources/prod"]
                       :uberjar-name "../leihs_audits-archiver.jar"}})
