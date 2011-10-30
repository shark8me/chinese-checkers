(ns chinese-checkers.alternatenumbering
  (:use [clojure.set :as set]
        [clojure.test]
       ))

;an x,y notation to denote positions.

(def yunit 1)
(def xunit 2)
(def xincr 1)
(def start {:x 0 :y 0})
(def top {:x 0 :y (* yunit 16)})

(defn next-row [[f :as fres] changefn]
  (let [newy (changefn (:y f) yunit)
        xstart (- (:x f) xincr)]
    (do
      ;(println newy xstart)
  (map #(hash-map :x % :y newy ) (range xstart (inc (count fres)) xunit)))))
  
(defn increasing-y [f] 
  (next-row f +))
(defn make-triangle [direction startnode]  
  (flatten (take 13 (iterate direction [startnode]))))

(make-triangle increasing-y start)

(defn decreasing-y [f] 
  (next-row f -))

(defrecord node [x y])

(def board (clojure.set/union (set (make-triangle decreasing-y top)) 
                   (set (make-triangle increasing-y start))))

(def sorted-board 
  (lazy-seq (mapcat #(sort-by :x %) (partition-by :y (sort-by :y board)))))
(def xboard (set (map (juxt :x :y) board)))

(defn nbrs [[x y] ]
  (let [posfn (fn[ xseq yval]
                (set (map vector xseq (repeat yval))))
        north (posfn [(+ xincr x) (- x xincr)] (+ y yunit))                         
        east-west (posfn [(+ xunit x) (- x xunit)] y)                         
        south (posfn  [(+ xincr x) (- x xincr)] (- y yunit))]
    (clojure.set/intersection xboard 
                              (reduce into [north east-west south]))))

(is (= #{[1 1] [-1 1]} (nbrs [0 0]) ))
(is (= #{[2 2] [0 0] [-1 1] [0 2]} 
       (nbrs [1 1])))

(defn ^{:dir :ne} ne [[x y]] [ (+ x xincr) (+ y yunit)] )
(defn ^{:dir :nw} nw [[x y]] [ (- x xincr) (+ y yunit)] )
(defn ^{:dir :se} se [[x y]] [ (+ x xincr) (- y yunit)] )
(defn ^{:dir :sw } sw [[x y]] [ (- x xincr) (- y yunit)] )
(defn ^{:dir :e} e [[x y]] [ (+ x xunit) y] )
(defn ^{:dir :w} w [[x y]] [ (- x xunit) y] )

(is (= [1 1] (ne [0 0])))
(is (= [-1 1] (nw [0 0])))
(is (= [-1 1] (w [1 1])))
(is (= [1 1] (e [-1 1])))
(is (= [0 0] (sw [1 1])))
(is (= [0 0] (se [-1 1])))

(defn dirn [src destn]
  "returns the direction, such that destn is e/w/n/s of src, if they are
immediate neighbours "
  (:dir (meta (first (filter #(= destn (% src)) [nw ne e se sw w])))))

(is (= :ne (dirn [0 0] [1 1])))
(is (= :sw (dirn [1 1] [0 0])))

(def all-dir (juxt nw ne e se sw w))
(def all-jumps
  (let [comp2 #(comp % %)]
  (apply juxt (map comp2 [nw ne e se sw w]))))

(defn nbrs1 [coord]
  (filter vec-to-num-map (all-dir coord)))

(is (= '([1 1] [-1 1]) (nbrs1 [0 0]) ))
(is (= '([2 2] [0 0] [-1 1] [0 2]) 
       (nbrs1 [1 1])))
(map vec-to-num-map (all-jumps (num-to-vec-map 1)))

(defn jumps-and-bridges
  "returns a list that contains vectors. Each vector's first element is
  the jump, the last element is the bridge"
  [coord]
  (filter identity (map #(if (contains? vec-to-num-map %1) [%1 %2]) 
       (all-jumps coord) (nbrs1 coord))))

(is (= '([[-2 4] [-1 3]] [[2 4] [1 3]]) (jumps-and-bridges [0 2])))

(is (= '(40 29 42 30 19 18 6 9 4 8 15 16)
       (for [i (jumps-and-bridges (num-to-vec-map 17)) j i] (vec-to-num-map j))))

(defrecord 

(filter altmap (nbrs1 [0 0]))
(first board)
(sort-by :x (sort-by :y board))

(def vecformat (map #(vector (% :x) (% :y)) sorted-board))
(def numformat (iterate inc 1))
(def vec-to-num-map (zipmap vecformat numformat ))
(def num-to-vec-map (zipmap numformat vecformat))

(defn jump-nbrs 
  [[x y]]
  (clojure.set/intersection xboard (hash-set 
    [(+ (* 2 xunit) x) y] [(- x (* 2 xunit)) y]; east-west
    [(- x xunit) (- y (* 2 yunit))] [(+ x xunit) (- y (* 2 yunit))];south
    [(- x xunit) (+ y (* 2 yunit))] [(+ x xunit) (+ y (* 2 yunit))])
  ))


(is (='(6 4) (map altmap (jump-nbrs [0 0]))))
(is (='(62 41 39 81 58 79) (map altmap (jump-nbrs [-2 8]))))
(comment
  node should contain
  x & y
  neighbours (with directions)
  absolute numbering
  
  )


        





