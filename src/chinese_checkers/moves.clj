(ns chinese-checkers.moves
  (:use [chinese-checkers.board :as board]
        [clojure.set :as set]
        [clojure.test]
       ))

(defn nbrs [pos]
  (vals (board/board pos)))

(defn empty-nbrs 
  [[mypos others]]
  (set/difference (set (nbrs mypos))
                          (set others)))

(is (= #{3} (empty-nbrs [1 [2 4]])))

(defn occupied-nbrs
  [[mypos others]]
  (set/intersection (set (nbrs mypos))
                          (set others)))

(is (= #{2} (occupied-nbrs [1 [2 4]])))

(defn nbr-dir
  "gets the direction of the destn neighbour from the
  src. Both src and destn must be neighbours" 
  [src dest]
  (ffirst (filter #(= dest (second %)) (board/board src))))
  
(is (= :w (nbr-dir 10 9)))

(defn onestepmove
  [[mypos otherpos]]
  (empty-nbrs [mypos otherpos]))

(deftest movetest 
  (is (= #{2 3} (onestepmove [1 [4 5]])))
  (is (= #{3}  (onestepmove [1 [2 4]])))
  )

(defn multistepmove
  [[mypos otherpos]]
  (let [src (peek mypos)
        full-nbrs (set/intersection (set otherpos) (set (nbrs src)))
        valid-dirs (remove nil? (map #(nbr-dir % src) full-nbrs))
        opposite-directions (map board/opposite-dir valid-dirs)
        valid-destns (map #((board/board %1) %2) full-nbrs opposite-directions) 
        ]
    (map #(conj mypos %) valid-destns)
    ))

(is (= '([1 4] [1 6]) (multistepmove [[1] [2 3]])))
(is (empty? (multistepmove [[1] [4 6]])))    
(is (= '([1 2 7] [1 2 9]) 
       (multistepmove [[1 2] [4 5]])))


(defn mms 
  [[moveseq otherpos]]
  (do 
  (let [unfiltered (mapcat #(multistepmove [% otherpos]) moveseq)  
         result    (filter #(= (count %) (count (set %))) unfiltered)
         
         ]
        (println unfiltered )
        (println result)
        (vector (into [] result) otherpos)
  )))

(is (= 
      [[[2 7 27] [2 9 31]] [4 5 15 18]]
      (mms [[[2 7] [2 9]] [4 5 15 18]])))
      
(defn moves 
  [[mypos otherpos]]
  (do 
    (println [[ [mypos]] otherpos]))  
  (first (last (take-while #(seq (first %) )(iterate mms  [[ [mypos]] otherpos]))))
  )

(is (=
      [[2 7 27] [2 9 31]]
      (moves [2 [4 5 15 18]])))

(moves [1 [2 3 7  9 10 ]])
  
  
  