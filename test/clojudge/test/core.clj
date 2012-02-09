(ns clojudge.test.core
  (:use [clojudge.core])
  (:use [clojure.test]))

(defn simple-judge [n]
  (even? n))

(defn error-message-judge [n]
  (if (even? n) [] ["Number is not even"]))

(defn map-judge [n]
  (if (even? n) {:result true} {:result false :errors ["Number is not even"]}))

(defn reified-result-judge [n]
  (if (even? n) 
    (reify Result
      (valid? [] true)
      (errors [] []))
    (reify Result
      (valid? [] false)
      (errors [] ["Number is not even"]))))

(deftest test-simple-judge
  (doseq [j [simple-judge error-message-judge map-judge]]
    (is (true? (valid? (judge 0 j))))
    (is (= [] (errors (judge 0 j))))
    (is (false? (valid? (judge 1 j))))
    (if (not (= simple-judge j))
      (is (= ["Number is not even"] (errors (judge 1 j))))))
