(ns leihs.archive-audits.time
  (:refer-clojure :exclude [str keyword contains? iterate range min format zero? max])
  (:require
    [leihs.utils.core :refer [presence str keyword]]
    [java-time :refer :all]
    ))

(defn months-seq 
  ([]
   (months-seq (-> (local-date) (minus  (years 1)) format)))
  ([start-date]
   (months-seq start-date (-> (local-date)
                              (minus  (years 1))
                              format)))
  ([start-date end-date]
   (let [start-date (-> start-date local-date (adjust :first-day-of-month))
         end-date (-> end-date local-date (adjust :first-day-of-month))]

     (->> (iterate plus start-date (months 1))
          (take-while #(or (before? % end-date)
                           (= % end-date)))
          (map format)))))


;(months-seq)
;(months-seq "2015-01-01")
