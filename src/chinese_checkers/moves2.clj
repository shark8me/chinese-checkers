(ns chinese-checkers.moves2
  (:use [chinese-checkers.board :as board]
        [clojure.set :as set]
        [clojure.test]
       ))

(defn onestep
  ([i] (onestep i []))
  ([i others] 
    (for [j (remove #(contains? (set others) %) (vals (board/board i)))]
      [i j])))

(is (= '([1 3] [1 2]) (onestep 1)))
(is (= '([1 3]) (onestep 1 [2])))
(is (empty? (onestep 1 [2 3])))

(defn nbrs-at
  [[dir i]] [dir (dir (board/board i))])

(is (= [:ne 3] (nbrs-at [:ne 1])))

(defn jump-nbr
  "neighbours a pawn can jump to"
  [dir i]
  (let [nbrs (filter #(not (nil? %)) 
                     (map last (take 3 (iterate nbrs-at [dir i]))))]
      (if (= 3 (count nbrs))
        nbrs [])))
  

(is (= '(1 3 6) (jump-nbr :ne 1)))
(is (empty? (jump-nbr :se 1)))

(defn jump-nbrs
  "returns a list of single jumps i can jump to, given others pawns"
  [i others]
  (let [probables (filter seq? (map #(jump-nbr % i) board/directions))
        oset (set others)
        ; the bridge pawn exists and the final destn is empty.
        ffn (fn[[a x y]] (and (contains? oset x) (not (contains? oset y))))]
    (for [i (filter ffn probables)] [(first i) (last i)])))
  
(is (= '([1 6] [1 4]) (jump-nbrs 1 [2 3])))
(is (= '([1 4]) (jump-nbrs 1 [2 ])))
(is (empty? (jump-nbrs 1 [2 4])))
  

(defn add-jumps2
  [jumps others]
  (do
      ;(println jumps)
  (let [noloops (fn [x] (= (count x) (count (set x))))
        newjumps (mapcat #(for [i (jump-nbrs (peek %) others)] (into (vec (butlast %)) i)) jumps)] 
    (filter noloops newjumps))))

(is (= '([1 6 19] [1 4 15]) (add-jumps2 [[1 6] [1 4]] [2 3 7 10])))
(is (empty? (add-jumps2 [[1 6] [1 4]] [2 3 30 31])))
(is (= '([1 6] [1 4]) (add-jumps2 [[1]] [2 3])))
(is (= '([1 6 19]) (add-jumps2 [[1 6] [1 4]] [2 3 10 30 31])))

(defn add-jumps-closure [others]
  (fn [jumps] (add-jumps2 jumps others)))

(defn multi-step
  [i others]
  (let [jclosure (add-jumps-closure others)]
    (mapcat #(into [] % ) (rest (take-while #(not (empty? %)) (iterate jclosure [[i]]))))))
  
(is (= '([1 6] [1 4] [1 6 19] [1 6 17] [1 4 15] [1 6 19 44] [1 6 17 42] [1 6 19 44 65])
        (multi-step 1 [2 3 7 9 10 30 32 55])))
(is (= '([1 6] [1 4]) (multi-step 1 [2 3 ])))
(is (empty? (multi-step 1 [ 4 5 ])))


(defn move
  [i others]
  (concat (onestep i others) (multi-step i others)))

(is (empty? (move 1 [2 3 4 5 6])))
(is (= '([1 3] [1 2]) (move 1 [])))
(is (= '([1 2] [1 6]) (move 1 [3 4])))
(is (= '([1 6] [1 4]) (move 1 [2 3 ])))

(defn moves
  ([mine] (moves mine []))
  ([mine others]
    (let [excepti (fn[x] (into (remove #(= x %) mine) others))]
    (mapcat #(move % (excepti %)) mine))
    ))

(is (= '([1 4] [1 4 15] [2 4] [2 9] [3 10] [3 8] [5 9] [5 8] [5 4] [6 10] [6 9] [6 4] [6 4 15] [7 16] [7 15] [7 4] [7 8])
       (moves [1 2 3 5 6 7])))