(ns clojudge.core)

(defprotocol Judge
  "Something that can be used as a judge"
  (judge [j input] "Determine whether or not the input is valid"))

(defprotocol Result
  "A validation result"
  (valid? [result] "Is the result valid?")
  (errors [result] "The errors associated with the result"))

(defn make-judgement
  "Determines whether or not a given input is valid according to a list of validators"
  [input validators]
  (let [results (map #(% input) validators)]
    {:valid? (every? valid? results)
     :errors (reduce into (map errors results))}))

(extend-type clojure.lang.IFn
  Judge
    (judge [f input] (f input)))

(extend-type java.lang.Boolean
  Result
    (valid? [b] b)
    (errors [_] []))

(extend-type java.util.Map
  Result
    (valid? [m] (:valid? m))
    (errors [m] (:errors m)))

(extend-type java.util.List
  Result
    (valid? [l] (empty? l))
    (errors [l] l)
  Judge
    (judge [l input] (make-judgement input l)))
