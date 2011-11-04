(ns chinese-checkers.ziptrial
  (:use [clojure.set :as set]
        [clojure.zip :as zip]
        [clojure.test]
       ))

(def v1 (zip/vector-zip [1 [2 3] [4 5]]))

(defn leafnodes [loc]
  (filter (complement zip/branch?) ;filter only non-branch nodes
          (take-while (complement zip/end?) ;take until the last node
                      (iterate zip/next loc))))

;all numbers are leaf nodes
(is (= '(1 2 3 4 5)  (map zip/node (leafnodes v1))))


(defn all [root]
  (lazy-seq (take-while #(do (println %) (not (zip/end? %)))  (iterate zip/next root))))

(defn editfn [x]
  (let [a (* x 10)]
    [(inc a) (dec a)]))

(def v2 (zip/vector-zip [[2 3] [4]]))


(defn inext [loc]
  "Adds children if loc is a leaf, else returns the next node"
  (cond (zip/end? loc) loc 
        (zip/branch? loc) (zip/next loc)
        :else (let [p (-> loc (zip/edit editfn))
                    r (-> p zip/right)]
                (if (-> r nil? not) r 
                  (let [nextnode  (-> p zip/up zip/right)]
                    (if (-> nextnode nil? not) (-> nextnode zip/next) (-> p zip/down zip/rightmost zip/next )))))))

(defn leaf-grown [v]
  "edits all the leaf nodes and returns after zipping to root"
  (zip/root (first (filter zip/end? (iterate inext v)))))

(is (= [[[21 19] [31 29]] [[41 39]]] (leaf-grown v2)))  
    

(def mroot {:self 20 :cren [ 
                            {:self 10 :cren [{:self 11} {:self 12}]} 
                            {:self 30 :cren [{:self 31} {:self 32}]}
                            ]})

(defn mzipper [root]
  (zip/zipper
    #(:cren %) ;branch?
    #(:cren %) ;children
    (fn [node children]
      (with-meta {:self (:self node) :cren (vec children)} (meta node)))
    root))

;only leaf nodes are listed, the parent nodes 20, 10 ,30 are not.    
(is (= '(11 12 31 32) (map :self (map zip/node  (leafnodes (mzipper mroot))))))

(defn gpath [n]
  (conj (vec (map :self (zip/path n))) (-> n zip/node :self)))

;path from root to leaf.
(is (= '([20 10 11] [20 10 12] [20 30 31] [20 30 32])
       (map (comp gpath) (leafnodes (mzipper mroot)))))

;the  added children now get listed as leaf nodes
(is (= '(91 92 12 31 32)
    (map :self (map zip/node 
                    (leafnodes (-> (mzipper mroot) zip/next zip/next 
                                 (zip/edit (fn[{:as m}] (assoc m :cren 
                                                     [ {:self 91} {:self 92}]))) zip/root mzipper))))))


(defn add-children [{x :self :as m}]
  (assoc m :cren [ {:self (inc x)} {:self (dec x)}]))

(defn edit-and-add [loc]
  (-> loc (zip/edit add-children)))
