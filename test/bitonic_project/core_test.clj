(ns bitonic-project.core-test
  (:require [clojure.test :refer :all]
            [bitonic-project.core :refer [generate-bitonic]]))

(deftest generate-bitonic-test
  (testing "Example 1 from prompt"
    (is (= [9 10 9 8 7] (generate-bitonic 5 3 10))))

  (testing "Example 2 from prompt (fully symmetric sequence)"
    (is (= [2 3 4 5 4 3 2] (generate-bitonic 7 2 5))))

  (testing "Impossible sequence: n is too large for the range"
    (is (= [-1] (generate-bitonic 8 2 5))))
    
  (testing "Impossible sequence: n exceeds max possible length"
    (is (= [-1] (generate-bitonic 6 8 10))))

  (testing "A possible short sequence"
    (is (= [9 10 9] (generate-bitonic 3 8 10))))
    
  (testing "A possible asymmetric sequence"
    (is (= [8 9 10 9 8 7] (generate-bitonic 6 7 10)))))