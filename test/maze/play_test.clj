(ns maze.play-test
  (:require [clojure.test :refer :all]
            [maze.play :as play]
            [maze.gen :as gen]
            [maze.draw :as draw]))

(def the-maze (atom []))

(def right-wall
  (fn [n-view e-view s-view w-view path]
    (let [available-moves (->>
                            (map (fn [[dist _ _] move] [dist move])
                                 [n-view e-view s-view w-view]
                                 [:n :e :s :w])
                            (filter (fn [[dist _]] (pos? dist)))
                            (map second)
                            set)
          preferred-moves {:e [:s :e :n :w] :s [:w :s :e :n] :w [:n :w :s :e] :n [:e :n :w :s]}
          last-move (last path)
          move (cond
                 (= 1 (count available-moves)) (first available-moves) ; only one available, so choose that
                 (empty? path) :s                           ; assume more then one move available, :e or :s so choose :s
                 :else (some available-moves (preferred-moves last-move)))]

      ;; (draw/draw-maze @the-maze path)
      ;; (println "path" path "available-moves" available-moves "preferred" (preferred-moves last-move) "move" move)
      move))
  )

(def go-east
  (fn [n-view e-view s-view w-view path] :e)
  )

(def go-south
  (fn [n-view e-view s-view w-view path] :s)
  )

(deftest single-move
  (let [maze (gen/maze-gen)
        maze-fn (fn [n-view e-view s-view w-view path] :e)
        result (play/play-maze maze maze-fn 0 5000 [])]
    (is (= :failure (first result)))))

(defn simplify [path]
  (loop [current-path path]
    (let [new-path (reduce (fn [path move]
                             (if (some #{[(last path) move]}
                                       [[:s :n] [:w :e] [:n :s] [:e :w]])
                               (subvec path 0 (dec (count path)))
                               (conj path move)))
                           [(first path)]
                           (subvec path 1))]
      (if (= new-path current-path)
        current-path
        (recur new-path)))))


(deftest right-wall-succeeds
  (let [maze (gen/maze-gen)
        _ (reset! the-maze maze)
        _ (draw/draw-maze maze)
        [result path] (play/play-maze maze right-wall 0 5000 [])]
    (println path)
    (println "simplified" (simplify path))
    (is (= :success result))))
