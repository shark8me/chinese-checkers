(ns chinese-checkers.alternatenumbering
  (:require [clojure.set :as set]
        [clojure.test :as t]
        [clojure.zip :as zip]
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

(defn ^{:dir :ne} ne [[x y]] [ (+ x xincr) (+ y yunit)] )
(defn ^{:dir :nw} nw [[x y]] [ (- x xincr) (+ y yunit)] )
(defn ^{:dir :se} se [[x y]] [ (+ x xincr) (- y yunit)] )
(defn ^{:dir :sw } sw [[x y]] [ (- x xincr) (- y yunit)] )
(defn ^{:dir :e} e [[x y]] [ (+ x xunit) y] )
(defn ^{:dir :w} w [[x y]] [ (- x xunit) y] )

(t/is (= [1 1] (ne [0 0])))
(t/is (= [-1 1] (nw [0 0])))
(t/is (= [-1 1] (w [1 1])))
(t/is (= [1 1] (e [-1 1])))
(t/is (= [0 0] (sw [1 1])))
(t/is (= [0 0] (se [-1 1])))

(def vecformat (map #(vector (% :x) (% :y)) sorted-board))
(def numformat (iterate inc 1))
(def vec-to-num-map (zipmap vecformat numformat ))
(def num-to-vec-map (zipmap numformat vecformat))

(defn dirn [src destn]
  "returns the direction, such that destn is e/w/n/s of src, if they are
immediate neighbours "
  (:dir (meta (first (filter #(= destn (% src)) [nw ne e se sw w])))))

(t/is (= :ne (dirn [0 0] [1 1])))
(t/is (= :sw (dirn [1 1] [0 0])))

(def all-dir (juxt nw ne e se sw w))
(def all-jumps
  (let [comp2 #(comp % %)]
  (apply juxt (map comp2 [nw ne e se sw w]))))

(defn nbrs1 [coord]
  (filter vec-to-num-map (all-dir coord)))



(t/is (= '([-1 1] [1 1] ) (nbrs1 [0 0]) ))
(t/is (= '([0 2] [2 2] [0 0] [-1 1]) 
       (nbrs1 [1 1])))

(defn nbr-cl [nbrs others]
  (filter (complement (set others)) nbrs))

(t/is (= '([-1 1]) (nbr-cl (nbrs1 [0 0]) [[1 1]])))

(map vec-to-num-map (all-jumps (num-to-vec-map 1)))

(defn jumps-and-bridges
  "returns a list that contains vectors. Each vector's first element is
  the jump, the last element is the bridge"
  [coord]
  (filter identity (map #(if (contains? vec-to-num-map %1) [%1 %2]) 
                        (all-jumps coord) (nbrs1 coord))))

(defn get-121-format [ & xs ]
  (for [i xs j i] (vec-to-num-map j)))

(t/is (= '([[-2 4] [-1 3]] [[2 4] [1 3]]) (jumps-and-bridges [0 2])))
(t/is (= '(40 29 42 30 19 18 6 9 4 8 15 16)
       (get-121-format (mapcat identity (jumps-and-bridges (num-to-vec-map 17))))))

(defn jumps
  [jmps others]
  (let [oset (set others)]
    (filter (fn[[x y]] (not (contains? oset x)))
            (filter (fn [[x y]] (oset y)) jmps))))



(t/is (= '(51 53 32 10 8 28)
       (get-121-format (map first (jumps (jumps-and-bridges (num-to-vec-map 30)) 
                                         (map num-to-vec-map [41 42 31 18 17 29]))))))

(t/is (= '(51 32 8)
       (get-121-format (map first (jumps (jumps-and-bridges (num-to-vec-map 30)) 
                                         (map num-to-vec-map [41 31 17 ]))))))

(t/is (= '(32)
       (get-121-format (map first (jumps (jumps-and-bridges (num-to-vec-map 30)) 
                                         (map num-to-vec-map [41 31 17 51 8]))))))

(defn mzipper [root]
  (zip/zipper
    #(:cren %) ;branch?
    #(:cren %) ;children
    (fn [node children]
      (with-meta (assoc node :cren (vec children)) (meta node)))
    root))

(defn make-zipper [src others]
  (mzipper {:self src  :others others}))

(defn root-self [loc]
  "returns the self value of the root node"
  (let [u (zip/up loc)]
    (if (nil? u)
      (:self (zip/node loc))
      (recur u))))

(defn addc [loc cfn]
  (do     
    (if (nil? (zip/node loc)) 
    loc
    (let [children (cfn loc)
          d-child (map #(assoc {} :self %1 :others %2 ) (map first children )
                       (map #(vec (set/difference (set (:others (zip/node loc))) 
                                                  (hash-set (last %)))) children))
          rs (root-self loc)
          f-child (filter #(not= rs (:self %)) d-child)] 
       ;(println "d-child" f-child "children" children)
       (if (empty? f-child) 
         (zip/next loc)
         (zip/edit loc (fn [{:as m}] (assoc m :cren (vec f-child)))))))))

(defn walk-and-grow [root growfn]
  (let [ifn (fn [loc]
              (if  (-> loc zip/branch? )                
                  (zip/next loc))
              (zip/next (addc loc growfn)))]
    (first (filter zip/end? (iterate ifn root)))))


(defn addc2 [loc]
  (let [nodel (zip/node loc)
        j1 (jumps-and-bridges (:self nodel))
        j2 (:others nodel)
        j3 (jumps j1 j2)]
    (do 
      ;(println "j1" j1 "j2" j2 "j3" j3)
    j3)))

(defn leafnodes [loc]
  (filter (complement zip/branch?) ;filter only non-branch nodes
          (take-while (complement zip/end?) ;take until the last node
                      (iterate zip/next loc))))

(defn gpath [n]
  "returns that path from the parent till the leaf"
  (conj (vec (map :self (zip/path n))) (-> n zip/node :self)))

(def t1 (make-zipper (num-to-vec-map 30)  (map num-to-vec-map [41 31 17 ])))

(defn n-jumps [src others]
  (for [i (->> (-> (make-zipper src others) 
                 (walk-and-grow  addc2) zip/root mzipper leafnodes  
                 ) (map (comp gpath)) 
            )] 
    (filter identity i))
  )

(defn- nlocal-jumps [src othr]
  (for [i (n-jumps (num-to-vec-map src)  (map num-to-vec-map othr))]
    (map vec-to-num-map i)))


(t/is (= '((30 51 71 53) (30 53)) (nlocal-jumps 30  [41 42 61 62])))

(t/is (= '((30 51 71)) (nlocal-jumps 30  [41 61 ])))
(t/is (= '((30 51 71) (30 53 73)) (nlocal-jumps 30  [41 61 42 63])))
  
(defrecord xnode [f121 nbrs jump-nbrs])

(defn makexnode [coord]
  (xnode. (vec-to-num-map coord) (partial nbr-cl (nbrs1 coord)) 
          (partial jumps (jumps-and-bridges coord))))

(t/is (= 1 (:f121 (makexnode [0 0]))))
(t/is (= '([1 1]) ((:nbrs (makexnode [0 0])) [[-1 1]])))
(t/is (= '([-2 2]) ((:jump-nbrs (makexnode [0 0])) [[-1 1]])))

(def boardmap 
  (zipmap xboard (map makexnode xboard)))

(def start-position
  (map num-to-vec-map (range 1 11)))

(defrecord Move [src walk-destns jump-destns])

(defn move [all i]
  (let [excepti (clojure.set/difference (set all) (set i))]
    (Move. i ((:nbrs (boardmap i)) excepti) ((:jump-nbrs (boardmap i)) excepti))))

(move [[0 0] [1 1] [-1 1]] [0 0])

(defn moves [all]
  (reduce conj #{} (mapcat (partial move all) all)))

;(map vec-to-num-map 
(moves start-position)






