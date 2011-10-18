(ns chinese-checkers.trees
  (:use [chinese-checkers.board :as board]
        [chinese-checkers.moves2 :as moves]
        [clojure.set :as set]
        [clojure.test]
       ))

(defrecord node
  [curpos from])

(defn spawn 
  ([node]
    (let [mo (moves/moves (:curpos node)) 
        newpos (map #(conj (vec (clojure.set/difference (set (:curpos node)) #{ (% 0)})) (peek %))
                    mo)
        res (map #(node. %1 (conj (:from node) %2)) newpos mo)  ]
      res)))

(is (= 2 (count (spawn (node. [1 ] [])))))


(defn n-spawn [[ :as nodes]]
  (let [all (mapcat spawn nodes)
    ret (vals (zipmap (map #(sort (:curpos %)) all) all))]
    ;(println (count all) (count ret))
    ret))

(is (= 6 (count (n-spawn [(node. [1] []) (node. [2] [])]))))

(count (n-spawn [(node. [1 2 3 4] [])]))
(count (last (take 6 (iterate n-spawn (list (node. [1 2 3 4] []))))))

