(ns clojudge.test.core
  (:use [clojudge.core])
  (:use [clojure.test]))

(defn simple-judge [n]
  (even? n))

(defn error-message-judge [n]
  (if (even? n) [] ["Number is not even"]))

(defn map-judge [n]
  (if (even? n) {:valid? true} {:valid? false :errors ["Number is not even"]}))

(defn reified-result-judge [n]
  (if (even? n) 
    (reify Result
      (valid? [_] true)
      (errors [_] []))
    (reify Result
      (valid? [_] false)
      (errors [_] ["Number is not even"]))))

(def jury [simple-judge error-message-judge])

(deftest test-single-judge
  (doseq [j [simple-judge error-message-judge map-judge jury]]
    (is (true? (valid? (judge j 0))) (str "failure to mark valid: " j))
    (is (empty? (errors (judge j 0))) (str "failure to get empty errors: " j))
    (is (false? (valid? (judge j 1))) (str "failure to mark invalid: " j))
    (if (not (= simple-judge j))
      (is (= ["Number is not even"] (errors (judge j 1))) (str "failure to get errors: " j)))))

(deftest test-conflicting-judges
  (is (true? (valid? (judge [simple-judge (constantly true)] 0))))
  (is (false? (valid? (judge [simple-judge (constantly true)] 1))))
  (is (= #{"Number is not even" "All shall fail"} 
         (set (errors (judge [error-message-judge (constantly ["All shall fail"])] 1))))))


