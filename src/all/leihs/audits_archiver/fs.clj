(ns leihs.audits-archiver.fs
  (:refer-clojure :exclude [str keyword resolve])
  (:require
    [leihs.utils.core :refer [presence str keyword]]

    [nio2.core :refer :all :exclude [path]]

    [clojure.tools.logging :as logging]
    [logbug.catcher :as catcher]
    [logbug.debug :as debug :refer [I>]]
    [logbug.thrown :as thrown]
    )
  (:import
    [java.io File]
    [java.nio.file Files Path Paths]))


(defn path [s & args] 
  (Paths/get s (into-array (if (empty? args) [""] args))))

(def default-target-dir (-> (path "." "audits-archive") absolute normalize str))
    
(defn save [data before-date {target-dir :target-dir 
                              file-name-prefix :file-name-prefix
                              dry-run :dry-run}] 
  (let [target-dir-path (-> target-dir path absolute normalize)
        file-path (-> (path target-dir (str file-name-prefix before-date ".json"))
                      absolute normalize)]

    (when-not (-> target-dir-path exists?) 
      (create-dir target-dir-path))
    (when-not (-> target-dir-path dir?)
      (throw (ex-info "The target-dir is not a directory!"
                      {:target-dir target-dir})))
    (when (exists? file-path)
      (throw (ex-info "The file-path already exists!"
                      {:file-path file-path})))
    (logging/info "writing " (str file-path))
    (if-not dry-run
      (spit (.toString file-path) data)
      (logging/info "skipping actual write because of dry-run")
      )))

;(write-lines (path "foo.txt")

;(write-lines (path "foo.txt") (char-array "foo"))

;#### debug ###################################################################
;(logging-config/set-logger! :level :debug)
;(logging-config/set-logger! :level :info)
;(debug/debug-ns 'cider-ci.utils.shutdown)
(debug/debug-ns *ns*)
