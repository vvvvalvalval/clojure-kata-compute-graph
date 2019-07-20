(ns compute-graph.core)

(defn compute
  [graph input-map output-keys]
  (let [cache-atom (atom {})]
    (letfn [(aux [k]
              (let [cache @cache-atom]
                (if (contains? cache k)
                  (get cache k)
                  (let [ret
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
                                 :graph graph}))))]
                    (swap! cache-atom assoc k ret)
                    ret))))]
      (reduce
        (fn [m k]
          (assoc m k (aux k)))
        {}
        output-keys))))
