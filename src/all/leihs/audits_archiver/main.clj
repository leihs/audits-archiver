(ns leihs.audits-archiver.main
  (:refer-clojure :exclude [str keyword])
  (:require
    [leihs.audits-archiver.time :as time]
    [leihs.audits-archiver.fs :as fs]
    [leihs.utils.core :refer [presence str keyword]]

    [clj-http.client :as http-client]
    [clojure.tools.cli :as cli :refer [parse-opts]]
    [clojure.pprint :refer [pprint]]


    [clojure.tools.logging :as logging]
    [logbug.catcher :as catcher]
    [logbug.debug :as debug :refer [I>]]
    [logbug.thrown :as thrown]
    )
  
  (:gen-class)
  )

(thrown/reset-ns-filter-regex #".*leihs.*")


(def defaults
  {:LEIHS_HTTP_URL "http://localhost"
   :START_DATE time/default-start-date
   :END_DATE time/default-end-date
   :TARGET_DIR fs/default-target-dir
   :FILE_NAME_PREFIX "audits-before_"
   :PRTG_URL nil})

(defn env-or-default [kw & {:keys [parse-fn]
                            :or {parse-fn identity}}]
  (or (-> (System/getenv) (get (str kw) nil) presence)
      (get defaults kw nil)))

(def cli-options
  [["-d" "--dry-run" "Do download but do not delete"
    :default false]
   [nil "--start-date START_DATE" 
    (str "default: " (:START_DATE defaults))
    :default (env-or-default :START_DATE)
    :parse-fn identity]
   [nil "--end-date END_DATE" 
    (str "default: " (:END_DATE defaults))
    :default (env-or-default :END_DATE)
    :parse-fn identity]
   [nil "--leihs-token LEIHS_TOKEN"
    :default (env-or-default :LEIHS_TOKEN)
    :parse-fn identity]
   [nil "--file-name-prefix FILE_NAME_PREFIX"
    :default (env-or-default :FILE_NAME_PREFIX)
    :parse-fn identity]
   ["-h" "--help"]
   ["-l" "--leihs-http-url LEIHS_HTTP_URL"
    (str "default: " (:LEIHS_HTTP_URL defaults))
    :default (env-or-default :LEIHS_HTTP_URL)
    :parse-fn identity]
   ["-t" "--target-dir TARGET_DIR"
    (str "default: " (:TARGET_DIR defaults))
    :default (env-or-default :TARGET_DIR)
    :parse-fn identity]])

(defn run [{dry-run :dry-run :as options}]
  (try 
    (doseq [month-date (time/months-seq (:start-date options) (:end-date options))]
      (let [url (str (:leihs-http-url options) 
                     "/admin/system/database/audits/before/" month-date)]
        (logging/info "fetching " url)
        (let [ resp (http-client/get url 
                                     {:accept :json
                                      :basic-auth [(:leihs-token options) ""]})]
          (def ^:dynamic resp* resp)
          (fs/save (:body resp) month-date options)
          (if-not dry-run
            (do 
              (logging/info "deleting " url)
              (http-client/delete url 
                                  {:accept :json
                                   :basic-auth [(:leihs-token options) ""]}))
            (logging/info "skipping delete because of dry-run")))))
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
; local test token: 
;(-main "--leihs-token" "GTS4VVOK3R3KFK3WKUIYF56GAHDQCTRW" "--start-date" "2015-05-01" "--end-date" "2015-06-01" )


;#### debug ###################################################################
;(logging-config/set-logger! :level :debug)
;(logging-config/set-logger! :level :info)
;(debug/debug-ns 'cider-ci.utils.shutdown)
;(debug/debug-ns *ns*)
