(ns clojudge.core)

(defprotocol Result
  "A validation result"
  (valid? [result] "Is the result valid?")
  (errors [result] "The errors associated with the result"))

(defn judge
  "Determines whether or not a given input is valid according to one or more validators"
  [input & validators]
  (let [results (map #(% input) validators)]
    {:valid? (every? valid? results)
     :errors (lazy-cat (map errors results))}))

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
    (errors [l] l))
