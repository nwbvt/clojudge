(ns clojudge.core)

(defprotocol Judge
  "Something that can be used as a judge"
  (judge [j input] "Determine whether or not the input is valid"))

(defprotocol Result
  "A validation result"
  (valid? [result] "Is the result valid?")
  (errors [result] "The errors associated with the result"))

(extend-type clojure.lang.IFn
  Judge
    (judge [f input] (f input)))

(extend-type java.lang.Boolean
  Result   
  ;; booleans can serve as results, they just cannot have error messages
    (valid? [b] b)
    (errors [_] []))

(extend-type java.util.Map
  Result
  ;; Maps are results as long as they have :valid? and :errors keys
    (valid? [m] (:valid? m))
    (errors [m] (:errors m)))

(extend-type java.util.List
  Result
  ;; If it is a result, a list simply contains a list of errors
  ;; An empty list is valid
    (valid? [l] (empty? l))
    (errors [l] l)
  Judge
  ;; Lists serve as judges by evaluating the input against each member
    (judge [l input] (make-judgement input l)))

(defn- make-judgement
  "Determines whether or not a given input is valid according to a list of validators"
  [input validators]
  (let [results (map #(% input) validators)]
    {:valid? (every? valid? results)
     :errors (reduce into (map errors results))}))
