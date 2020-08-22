(ns maze.play-test
  (:require [clojure.test :refer :all]
            [maze.play :as play]
            [maze.gen :as gen]))

(deftest run-game
  (let [maze (gen/maze-gen)
        maze-fn (fn [n-view e-view s-view w-view path] :e)
        result (play/play-maze maze maze-fn 0 5000 [])]
    (is (= :failure (first result)))))
