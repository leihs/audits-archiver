(ns leihs.archive-audits.main
  (:refer-clojure :exclude [str keyword])
  (:require
    [leihs.archive-audits.time :as time]
    [leihs.utils.core :refer [presence str keyword]]

    [clojure.tools.cli :as cli :refer [parse-opts]]
    [clojure.pprint :refer [pprint]]

    [clojure.tools.logging :as logging]
    [logbug.catcher :as catcher]
    [logbug.debug :as debug :refer [I>]]
    [logbug.thrown :as thrown]
    )
  (:gen-class)
  )

(def defaults
  {:LEIHS_HTTP_URL "http://localhost:3200"
   :START_DATE time/default-start-date
   :END_DATE time/default-end-date
   :PRTG_URL nil})

(defn env-or-default [kw & {:keys [parse-fn]
                            :or {parse-fn identity}}]
  (or (-> (System/getenv) (get (str kw) nil) presence)
      (get defaults kw nil)))

(def cli-options
  [["-d" "--dry-run" "Do download but do not delete"
    :default false]
   [nil "--start-date START-DATE" 
    (str "default: " (:START_DATE defaults))
    :default (:START_DATE defaults)
    :parse-fn identity]
   [nil "--end-date END-DATE" 
    (str "default: " (:END_DATE defaults))
    :default (:END_DATE defaults)
    :parse-fn identity]
   ["-h" "--help"]
   ["-l" "--leihs-http-url LEIHS_HTTP_URL"
    (str "default: " (:LEIHS_HTTP_URL defaults))
    :default (env-or-default :LEIHS_HTTP_URL)
    :parse-fn identity]
   ])

(defn run [options]
  (try 
    (println "running")
    (catch Throwable t
      (logging/error t)
      (System/exit -1))))


(defn main-usage [options-summary & more]
  (->> ["Leihs Archive-Audits"
        ""
        "usage:  [<opts>] [<args>]"
        ""
        "Options:"
        options-summary
        ""
        ""
        (when more
          ["-------------------------------------------------------------------"
           (with-out-str (pprint more))
           "-------------------------------------------------------------------"])]
       flatten (clojure.string/join \newline)))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args cli-options :in-order true)
        pass-on-args (->> [options (rest arguments)]
                          flatten (into []))]
    (cond
      (:help options) (println (main-usage summary {:args args :options options}))
      :else (run options)
      )))

;(-main "-h")


;#### debug ###################################################################
;(logging-config/set-logger! :level :debug)
;(logging-config/set-logger! :level :info)
;(debug/debug-ns 'cider-ci.utils.shutdown)
;(debug/debug-ns *ns*)
