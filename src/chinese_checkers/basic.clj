(ns chinese-checkers.basic
  (:use [chinese-checkers.board :as board]
        [chinese-checkers.moves2 :as moves]
        [clojure.set :as set]
        [clojure.test]
       ))

;a basic player

(def mypawns (atom (vec (range 1 11))))

(def src (vec (range 1 11)))
(def destn (vec (range 112 122)))

(defn next-move 
  [others]
  nil)

(defn choose-move
  ([moves curpos others] nil )
  ([moves curpos] (choose-move moves curpos [])))   

(defn apply-move
  "takes the move and returns list of new positions"
  [move curpos]
  (conj (remove #(= % (first move)) curpos) (peek move)))

(is (= '(17 1 2 3 5 6 7 8 9 10)
       (apply-move [4 17] src)))

(defn size-sorted-moves
  "moves sorted by number of moves, max last"
  [curpos]
  (sort-by count (moves/moves curpos [])))




(defn add-child-moves 
  [curpos]
  [curpos (vec (moves/moves curpos []))]) 

(defn node 
  [{:keys [parent move] :as m}]
  (assoc m :makenode (fn [] (vec (moves/moves parent [])))))

((:makenode (node {:parent @mypawns :move "move"})))

(map #(add-child-moves (apply-move % @mypawns)) (second (add-child-moves @mypawns)))
(min-max @mypawns)

