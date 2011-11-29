(ns chinese-checkers.forest
   (:require [clojure.set :as set]
        [clojure.test :as t]
        [clojure.zip :as zip]
        [chinese-checkers.ziptools :as zt]
       ))

;forest: a list of trees, and each leafnode should be unique 
;each move generates a tree of future moves. A game will have a set of
;current moves. 
;As each move generates future moves, some of the advance moves may be duplicate 
;(in another tree).
;therefore the algo is

;for each move (which is a tree's root),
;generate the next level of leaf nodes.
;Remove/mark duplicates in the leaf nodes of all trees 
;repeat

(defn uniq-leaves[ xroot]
  (let [ shet #{}] 
    (reduce conj #{} 
            (map #(-> % zip/node :self ) 
                 (zt/leafnodes (zt/mzipper xroot))))))


(defn leaf-edit [[loc shet]]
  (do ;(println "le" (zip/node loc))
  (let [self (-> loc zip/node :self)
        pred (shet self ) 
        efn (fn [{:as m}] (assoc m :dupl true))
        newshet (if (not (zip/branch? loc)) (conj shet self) shet) ]
    [(zip/next (if (and (not (zip/branch? loc)) pred) 
                 (zip/edit loc efn)  
                 loc)) newshet ])))    
    


(defn leaf-walk-edit [editfn iterstart]
  "walks the tree, 
  and calls editfn on each node."
  (first (filter (fn [[x y]] (zip/end? x))
                 (iterate editfn iterstart))))

(def nroot {:self 20 :cren [ 
                            {:self 10 :cren [{:self 11} {:self 14}]} 
                            {:self 30 :cren [{:self 11} {:self 32}]}
                            ]})
(def nroot1 {:self 29 :cren [ 
                            {:self 10 :cren [{:self 12} {:self 14}]} 
                            {:self 30 :cren [{:self 16} {:self 17}]}
                            ]})
(def nroot2 {:self 50 :cren [ 
                            {:self 10 :cren [{:self 14} {:self 15}]} 
                            {:self 30 :cren [{:self 16} {:self 14}]}
                            {:self 31 :cren [{:self 16} {:self 15}]}
                            ]})

(def remove-duplicate-leaves (partial leaf-walk-edit leaf-edit))
  
;set contains only unique leaf node values.
(t/is (= #{32 14 11} 
         (last 
           (remove-duplicate-leaves [(zt/mzipper nroot) #{}]))))

(t/is (= #{14 15 16} 
         (last 
           (remove-duplicate-leaves [(zt/mzipper nroot2) #{14}]))))

;seq of trees
;(def for1 [nroot nroot1 nroot2])
    
(defn redfn [[lst-of-trees aset ] ith-tree]
  (let [ret (remove-duplicate-leaves [ith-tree aset])]  
    ;(println x aset )    
    [(conj lst-of-trees (first ret)) (last ret)]      
    ))

(t/is (= #{32 11 12 14 15 16 17}
         (last (reduce redfn [[] #{}] 
                       (map zt/mzipper [nroot nroot1 nroot2])))))
 