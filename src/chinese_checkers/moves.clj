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
  (vec (for [i (empty-nbrs [mypos otherpos])]
    [mypos i])))


(deftest movetest 
  (is (= [[1 2] [1 3]] (onestepmove [1 [4 5]])))
  (is (= [[1 3]]  (onestepmove [1 [2 4]])))
  (is (not (seq? (onestepmove [1 [2 3 4 5 6 7]]))))
  )


(defn multistepmove
  [[mypos otherpos]]
  (let [src (peek mypos)
        full-nbrs (set/intersection (set otherpos) (set (nbrs src)))
        valid-dirs (remove nil? (map #(nbr-dir % src) full-nbrs))
        opposite-directions (map board/opposite-dir valid-dirs)
        valid-destns (remove nil? (map #((board/board %1) %2) full-nbrs opposite-directions))
        open-positions (remove #(contains? (set otherpos) %) valid-destns) 
        ;remove positions where pawns exist
        ]
    (do
      ;(println mypos otherpos full-nbrs valid-dirs opposite-directions valid-destns open-positions)
    (map #(conj mypos %) open-positions)
    )))

(is (= '([1 4] [1 6]) (multistepmove [[1] [2 3]])))
(is (empty? (multistepmove [[1] [4 6]])))    
(is (= '([1 2 7] [1 2 9]) 
       (multistepmove [[1 2] [4 5]])))
(is (empty? (multistepmove [[1] [2 3 4 6]])))

(defn mms 
  [[moveseq otherpos]]
  (do 
    ;(println moveseq otherpos)
  (let [unfiltered (mapcat #(multistepmove [% otherpos]) moveseq)  
         result    (filter #(= (count %) (count (set %))) unfiltered)
         ;remove loops
         ]
        ;(println unfiltered )
        ;(println result)
        (vector (into [] result) otherpos)
  )))

(is (= 
      [[[2 7 27] [2 9 31]] [4 5 15 18]]
      (mms [[[2 7] [2 9]] [4 5 15 18]])))
      
(mms [[[2]] [1 3 4 5 6 7 9 10]])
(defn jumpmoves 
  [[mypos otherpos]]   
  (let [
        ;res (take-while #(seq (first %) )(iterate mms  [[ [mypos]] otherpos]))
        res (take-while #(seq (first %) )(iterate mms  [[ [mypos]] otherpos]))
        
        ]
    (do
      (println "jmp" res)
    (first (last res))
  )))

(is (=
      [[2 7 27] [2 9 31]]
      (jumpmoves [2 [4 5 15 18]])))

(jumpmoves [2 [1  3 4 5 6 7 9 10 ]])
  
(defn allmoves 
  [input]
  (let [jmoves (jumpmoves input) 
        smoves (onestepmove input)]
    (do
      (println jmoves smoves)
  (concat (if (seq? jmoves) jmoves [])
          (if (seq? smoves) smoves [])))))

(allmoves [1 [2 3 7 9 10 ]])

(defn allpawnsexcept [i mine others]
  (vec (concat (remove #(= i %) mine) others)))

(is (= [2 3 4 5 6 7 8 9 10 16 17 18]
       (allpawnsexcept 1 [1 2 3 4 5 6 7 8 9 10] [16 17 18])))

(defn moves 
  [mypawns pawns]
  (for [i  mypawns]
    (do
    (println i (allmoves [i (allpawnsexcept i mypawns pawns)]))
    )))

(moves [1 2 3 4 5 6 7 8 9 10] [16 17 18])
  