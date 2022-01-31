(ns maze.core
  (:require [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [maze.www :refer [app]]))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty app {:port port :join? false})))
