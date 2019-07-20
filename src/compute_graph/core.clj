(ns compute-graph.core)

(defn compute
  [graph input-map output-keys]
  (letfn [(aux [k]
            (if (contains? input-map k)
              (get input-map k)
              (if-some [step (get graph k)]
                (apply (:fn step)
                  (mapv aux
                    (:deps step)))
                (throw
                  (ex-info
                    (str "Key missing from graph or input: " (pr-str k))
                    {:key k
                     :graph graph})))))]
    (reduce
      (fn [m k]
        (assoc m k (aux k)))
      {}
      output-keys)))
