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
      (valid? [_] true)
      (errors [_] []))
    (reify Result
      (valid? [_] false)
      (errors [_] ["Number is not even"]))))

(deftest test-simple-judge
  (doseq [j [simple-judge error-message-judge map-judge]]
    (is (true? (valid? (judge 0 j))))
    (is (= [] (errors (judge 0 j))))
    (is (false? (valid? (judge 1 j))))
    (if (not (= simple-judge j))
      (is (= ["Number is not even"] (errors (judge 1 j)))))))

(deftest test-multiple-judges
  (is (true? (valid? (judge 0 simple-judge (constantly true)))))
  (is (false? (valid? (judge 1 simple-judge (constantly true)))))
  (is (= #{"Number is not even" "All shall fail"} 
         (set (errors (judge 1 error-message-judge (constantly ["All shall fail"])))))))
