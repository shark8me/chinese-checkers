;(add-classpath "file:///c:/home/clojure-1.0/")
;(import 'board)
(def currentPos 
	;current positions of pawns, should be 10 in number
	(range 1 11)
)

(defn nbrList [source]
	;returns list of neighbours for 'source'
	(vals (board source)) 
)

(defn emptyFilter [positions occupied]
	;returns all items in 'positions' that are not in 'occupied'
	(clojure.set/difference (set positions) (set occupied))
)

(defn occupiedFilter [positions occupied]
	;returns all items in 'positions' that are in 'occupied'
	(clojure.set/intersection (set positions) (set occupied))
)

(defn loopedRoute? [source route]
	(contains? (set route) source)
)

(defn getNbrDirection [source destn]
	((clojure.set/map-invert (board source)) destn)
)

(getNbrDirection 1 3)

(defn constrainedNbrListFn [ constraint]
	(fn [source] (set (vals (select-keys (board source) constraint) )))
)
(defn constrainedNbrList [ source constraint]
	((constrainedNbrListFn constraint) source)
)


(defn getNbrInDirection [source direction]
	;returns the neighbour in a given direction
	((board source) direction)
)

(def forwardConstrained (constrainedNbrListFn '(:ne :nw)))
(def backwardConstrained (constrainedNbrListFn '(:se :sw)))
(def exceptBackwardConstrained (constrainedNbrListFn '(:ne :nw :w :e)))


(defn getJumpsFrom [source occupied nbrFn]
	(let [ occupiedNbrs (occupiedFilter (nbrFn source) occupied)	
		occuNbrDir (map #(getNbrDirection source %) occupiedNbrs) 
		occuMap (filter #(not (nil? %)) (map #(getNbrInDirection %1 %2) occupiedNbrs occuNbrDir))
		res (emptyFilter occuMap occupied)  ]
		(do 
			;(println " getJumpsFrom source " source " occuMap " occuMap)
			res
		)
	)	
)

(getJumpsFrom 1 '(2 3 8 10) forwardConstrained)


(defn srcDestnMatch? [route src destn]
	(and (== (first route) src) (== (last route) destn))
)

(defn removeDuplicateRoutes [routes]
	(let [ corpus (filter #(>= (count %) 3) routes)
		amap (set (map #(list (first %) (last %)) corpus)) 
		myMap {} ] 
		(concat (filter #(< (count %) 3) routes)
			(for [i amap] 
				(let [ item (myMap i) 
					matchingRoutes (sort #(> (count %1) (count %2)) 
						(filter #(srcDestnMatch? % (first i) (last i)) corpus)) ]
					;matchingRoutes
					(first matchingRoutes)
				)
			)
		)
	)
)

(removeDuplicateRoutes '((1 4 13) (1 4 11) (1 6 13) (1 6 15) (2 3) (4 5)))

(defn not-nil? [arg ]
	(not (nil? arg))
)
(defn getJumpSequences [ rout occu nFn ]
	(loop [routes rout
		occupied occu
		nbrFn nFn] 
		(let [ jumpInput (map #(seq (getJumpsFrom (first %) occupied nbrFn)) routes)  
		jumps (filter not-nil? jumpInput) 
			;some routes have no further jumps, save them and append later
			noFurtherJumps (filter not-nil? (map #(if (nil? %2) %1 nil) routes jumpInput))
			knit1  (map #(list %1 %2) routes jumps)
			knit2 (mapcat #(for [ j (last %)] (conj (first %) j)) knit1 )
			pass (removeDuplicateRoutes knit2) 
			passOn (if (empty? noFurtherJumps) pass (concat noFurtherJumps pass))]
			(do 
				(println "routes " routes "jumpInput " jumpInput " jumps " jumps "knit 1" knit1 "knit 2" knit2 "no further jumps " noFurtherJumps "pass " passOn)
			(if (empty? jumps)
				routes
				(recur passOn	
					occupied
					nbrFn)
					
			))
		)
	)
)
(getJumpSequences '((2)) '(4 16 40 41 61 62 82 105) forwardConstrained)
;answer- ((50 29 7 2) (70 52 29 7 2) (113 50 29 7 2))
;(getJumpSequences '((4 1) (6 1)) '(2 3 7 8 9 10) forwardConstrained)
;(getJumpSequences '((1)) '(2 3 8 10) forwardConstrained)


(forwardConstrained 5)

(defn getMoves 
	([source occupiedPlaces] (getMoves source occupiedPlaces nbrList))
	([source occupiedPlaces nbrFn ] 
	;returns the possible moves from the 'source' position
	;output format is map where key is destination, and value is a list containing
	;all routes to destn.
	;(getMoves 9 '(2 3))
	;{9 #{5 6 8 10 13 14}}

	;there are 2 sets of possible moves
	; 1 > the immediate neighbours that are empty.
	; 2 > jump moves.
	(let [emptyNbrSet (emptyFilter (nbrFn source) occupiedPlaces)
			jumpSeqWithSource(set (getJumpSequences (list (list source)) occupiedPlaces nbrFn))
			;remove jumpsequences that only contain the source]
			jumpSequences (filter #(> (count %) 1 ) jumpSeqWithSource) ]	
		(do 
			;(println " src " source " occupied " occupiedPlaces "emptyNbrs " emptyNbrSet " jumpSeq " jumpSequences )
			(let [result (clojure.set/union emptyNbrSet jumpSequences) ]
				(list source (if (set? result) result (set result)))
			)
			;output format :
			;(getMoves 9 '(2 3))
			;{9 #{5 6 8 10 13 14}}
			;
		)
	)
	)
)

(getMoves 1 '(2 7 5) forwardConstrained)

(getMoves 13 '(9) ))
(defn getAllMoves 
	([myPawns occupiedPlaces] (getAllMoves myPawns nbrList))
	([myPawns occupiedPlaces nbrFn]
		(let [ allOccupied (clojure.set/union myPawns occupiedPlaces ) 
				aMinus (for [ i myPawns] (filter #(not= % i) myPawns))
				each (map #(getMoves %1 (concat occupiedPlaces %2) nbrFn) myPawns aMinus) 
				;remove results for positions with no possible moves ]
				possibleMoves (filter #(not (empty? (last %))) each)]
			(do 
				;(println "allOccu " allOccupied " possible " possibleMoves )
				(zipmap (map first possibleMoves) (mapcat rest possibleMoves)) 
			)
		)
	)
)

;(getAllMoves '(1 2 5))
(getAllMoves '(1 2 3 4 5 6 7 8 9 10) '(11 12 13) forwardConstrained)

;-----------------------------
;section related to gameplay
;-----------------------------

(defn relativeCentre [pawns]
	;returns the pawn which is the relative center of the group of pawns
	;the input pawn list is meant to be the 'goal'.
	;the returned pawn is one that has the most neighbours 1 move away. 	
	(let [ allNbrs (map nbrList pawns) 
		validNbrs (map #(clojure.set/intersection (set %)  (set pawns)) allNbrs)
		nMap (zipmap validNbrs pawns)
		]	
		(do 
			;(println "all " allNbrs " valid " validNbrs )
		(nMap (reduce #(if (> (count %1 ) (count %2)) %1 %2) validNbrs))
		)
	)
)

(def destnPos (range 16 26))
(relativeCentre destnPos)
(relativeCentre currentPos)



(defn shortestPath [source destn constrainedDirection path]
	;returns the route from source to destn, 
	;if constrained direction is provided, the search set is reduced.
	(let [nextNbrs  (constrainedNbrList source constrainedDirection) ]
	(do (println "desnt " destn " nextNbr " nextNbrs )
	(if (contains? nextNbrs destn)
		path
	;	(mapcat #(recur % destn constrainedDirection (conj path %)) nextNbrs)	
		(recur (first nextNbrs) destn constrainedDirection (conj path (first nextNbrs))) 
	)
	)
	)	
)

(shortestPath 1 5 '(:ne :nw) '(1))

(defn findNonCohesivePawns [myPawns occupied]
	;presumes that myPawns are not a part of the occupied set
	(let [combinedOccu (clojure.set/union myPawns occupied) 
		nbSet (map #(set (nbrList %)) myPawns) 
		nonEmptyNbr (map 
				#(clojure.set/intersection %1 (set combinedOccu)) 
				nbSet)
		]
		(do 
		;(println combinedOccu "- " nbSet " - " "- " nonEmptyNbr )	
			(filter 
				#(not (nil? %)) 
				(map #(if (empty? %1 ) %2 nil) nonEmptyNbr myPawns)
			)
		)
	)
)
(findNonCohesivePawns '(1 2 3 4 5) '(6))
(defn isCohesive? [myPawns occupied]
	"cohesive means that all pawns should be within one move 
	or jump of each other"
	(empty? (findNonCohesivePawns myPawns occupied))
)

(defn boardEntry [ start end ne se ]
	(map 	
		(fn[i j] (do
				(println j "{ :e " (inc j) " :w " (dec j) " :nw " 
					(dec (+ i ne)) " :ne " (+ i ne) " } ")
				))
	(range (- end start)) (range start (inc end)))
)

(def movedb #{((1)) ((2)) ((3)) ((4)) ((5)) ((6)) ((7)) ((8)) ((9)) ((10))})

(defn getCurPos [ moves ]
	(map #(last (last %)) moves)
)

(getCurPos movedb)
(defn addMove [ move moveStore ]
	(let [ inplace (map #(if (= (first move) (last (last %)))
							;append 
							(concat % (list move))
							%
							) moveStore)]
		(do 
			;(println inplace)
			inplace	
		)
	)		
)
(def startTriangle #(1 2 3 4 5 6 7 8 9 10))
(addMove '(1 2) movedb)

(defn sortByDistance [ st lvl]
	(loop [ start st 
			level lvl ]
		(let [nextLine (distinct (mapcat forwardConstrained start)) 
			newLevel (conj level start) ]
			(do 
				;(println "start " start " nextLine " nextLine)
				(if (empty? nextLine)
					level	
					(recur nextLine newLevel)
				)	
			)
		)
	)
)

;THIS list does not include the side triangles. 
(def distanceFromBase (sortByDistance '(1) []))


(defn getPawnSortedByDistanceToBase [curPos]
	;returns the sorted list of pawns, first item is nearest to base
	(filter #(not (empty? %))(map #(clojure.set/intersection (set %) (set curPos)) distanceFromBase))
)

(defn getFarthestPawn [curPos]
	(first (getPawnSortedByDistanceToBase curPos))
)

(getFarthestPawn '(17 40 60 93 7 31))
