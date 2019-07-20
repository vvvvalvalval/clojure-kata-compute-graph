(ns compute-graph.core-test
  (:require [clojure.test :refer :all]
            [compute-graph.core :as cg]))


(def stats-graph
  "A Compute Graph for computing statistics on a series of numbers."
  {:mystats/cardinality
   {:deps [:mystats/xs]
    :doc "The size of the input series."
    :fn (fn [xs]
          (count xs))}
   :mystats/average
   {:deps [:mystats/sum :mystats/cardinality]
    :fn (fn [sum n]
          (/ sum n))}
   :mystats/stddev
   {:deps [:mystats/variance]
    :doc "The standard deviation of the series."
    :fn (fn [variance]
          (Math/sqrt variance))}
   :mystats/sum
   {:deps [:mystats/xs]
    :doc "The sum of the input series."
    :fn (fn [xs]
          (apply + xs))}
   :mystats/squares
   {:deps [:mystats/xs]
    :fn (fn [xs]
          (mapv #(* % %) xs))}
   :mystats/sum-of-squares
   {:deps [:mystats/squares]
    :fn (fn [x2s]
          (apply + x2s))}
   :mystats/variance
   {:deps [:mystats/sum-of-squares :mystats/cardinality :mystats/average]
    :fn (fn [sum2 n avg]
          (-
            (/ sum2 n)
            (* avg avg)))}})

(deftest stats-example
  (testing "Nominal case. Computes the requested outputs and returns them in a map, transitively computing the intermediary steps as needed."
    (is
      (=
        (let [;; the source nodes for our computations
              inputs-map {:mystats/xs (range 10)}
              ;; the results we want to see in the output
              output-keys [:mystats/cardinality :mystats/average :mystats/variance]]
          (cg/compute stats-graph
            inputs-map
            output-keys))
        {:mystats/cardinality 10,
         :mystats/average 9/2,
         :mystats/variance 33/4})))
  (testing "Can substitute intermediary results instead of providing the input; useful e.g for caching."
    (is (=
          (cg/compute stats-graph
            {:mystats/cardinality 10
             :mystats/sum 45}
            [:mystats/cardinality :mystats/average])
          {:mystats/cardinality 10,
           :mystats/average 9/2})))
  (testing "Will throw an error if some required input key is missing"
    (is (= :did-throw
          (try
            (cg/compute stats-graph
              {}
              [:mystats/cardinality :mystats/average :mystats/variance])
            :should-have-thrown
            (catch Throwable _ex
              :did-throw))))))

