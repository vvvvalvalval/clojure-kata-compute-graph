(ns compute-graph.core)

(defn compute
  [graph input-map output-keys]
  (let [VISITING (Object.)]
    (letfn [(ensure-key-in-acc [acc k]
              (if (contains? acc k)
                (if (identical? VISITING (get acc k))
                  (throw (ex-info
                           (str "Dependency cycle detected around key: " (pr-str k))
                           {:key k :graph graph}))
                  acc)
                (if (contains? input-map k)
                  (assoc acc
                    k
                    (get input-map k))
                  (if-some [step (get graph k)]
                    (let [acc1 (assoc acc k VISITING)
                          deps-ks (:deps step)
                          acc-with-deps (reduce ensure-key-in-acc acc1 deps-ks)
                          deps-values (mapv acc-with-deps deps-ks)
                          v (apply (:fn step) deps-values)]
                      (assoc acc-with-deps k v))
                    (throw
                      (ex-info
                        (str "Key missing from graph or input: " (pr-str k))
                        {:key k
                         :graph graph}))))))]
      (select-keys
        (reduce ensure-key-in-acc {} output-keys)
        output-keys))))
