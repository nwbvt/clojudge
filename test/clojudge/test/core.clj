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

(deftest test-single-judge
  (doseq [j [simple-judge error-message-judge map-judge]]
    (is (true? (valid? (judge 0 j))) (str "failure to mark valid: " j))
    (is (empty? (errors (judge 0 j))) (str "failure to get empty errors: " j))
    (is (false? (valid? (judge 1 j))) (str "failure to mark invalid: " j))
    (if (not (= simple-judge j))
      (is (= ["Number is not even"] (errors (judge 1 j))) (str "failure to get errors: " j)))))

(deftest test-multiple-judges
  (is (true? (valid? (judge 0 simple-judge (constantly true)))))
  (is (false? (valid? (judge 1 simple-judge (constantly true)))))
  (is (= #{"Number is not even" "All shall fail"} 
         (set (errors (judge 1 error-message-judge (constantly ["All shall fail"])))))))
