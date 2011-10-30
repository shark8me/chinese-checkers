(ns chinese-checkers.trees
  (:use [chinese-checkers.board :as board]
        [chinese-checkers.moves2 :as moves]
        [clojure.set :as set]
        [clojure.test]
       ))

(defrecord node
  [curpos from])

(defn spawn2 
  [node]
  (let [without-head (fn[[y & z]] (sort (conj (vec (filter #(not= % y) (:curpos node))) (last z))))
        makenode (fn [move] (node. (without-head move) 
                                   (conj (:from node) move)))]
    (lazy-seq 
      (pmap makenode
           (moves/moves (:curpos node))))))

(is (= 2 (count (spawn2 (node. [1 ] [])))))

(defn p-spawn 
  [[:as nodes]]
  (let [xset (atom #{})
        ifnotpresent (fn[x] (if (contains? @xset x) false (swap! xset conj x) ))
        ]    
    (lazy-seq (filter #(ifnotpresent (:curpos %)) (apply concat (pmap spawn2 nodes))))))  

    

(is (= 5 (count (p-spawn [(node. [1] []) (node. [2] [])]))))

(def start (vec (range  1 11)))

(defn dfs [plies ]
  (last (take plies (iterate p-spawn (list (node. start []))))))

;(dfs 4)

(is (= 15939 (last (map #(time (count (dfs %))) (range 1 6)))))

;scoring- assigning a score to each move.

(defn vicinity-score 
  [x] (case x 1 5
       2 4
       3 3
       0))

(defn no-of-nbrs [i all]
  (let [nbrs (filter #(not= i %)  all)
        nbrlst (vals (board/board i))
        nbrcount (count (clojure.set/difference (set nbrlst) (set nbrs)))
        score (fn [x] (case x
                        1 3
                        2 5
                        3 2
                        0)) ]
    (do
      (println i nbrlst nbrcount)
    
    )))

(defn nscore [all]
  (let 
  (map 
  )))

(def all '(1 2 3 4 5 6 7 9 30 41))
(map #(no-of-nbrs % all) all)

(defn nbrs-of [dirs i]
  (set (mapcat #(vals (select-keys (board/board %) dirs)) i)))

(defn nbrs-up [i]
  ((partial nbrs-of [:ne :nw ]) i))

(defn nbrs-down [i]
  ((partial nbrs-of [:se :sw ]) i))

(defn nbrs-updown [i]
  ((partial nbrs-of [:se :sw :ne :nw]) i))

(is (= #{8 9 29 30} (nbrs-updown [17])))

(defn star-nbrs [i]
  (let [one (fn[x] (last (take 3 (iterate x [i]))))]
    (into (one nbrs-up ) (one nbrs-down ))))

(is (= #{4 5 6 40 41 42} (star-nbrs 17)))

(defn star-score [all]
  )



  


