(ns maze.www
  (:use compojure.core hiccup.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [hiccup
             [page :as html]
             [form :as form]]
            [maze.controller :as ctrl]
            [clojure.pprint :as pp]
            [ring.util
             [response :as rr]
             [codec :as rc]])
  (:import [java.io StringWriter]))

(defn head [] 
  [:head [:title "Maze Challenge"] (html/include-css "style.css")])

(defn submit-html []
  (html/html5 (head)
              [:body
               [:h1 "Submit your maze solver"]
               [:p "Paste in the text of your maze solver, in the form of an anonymous function literal. The function must take 5 arguments" [:pre "[n-view e-view s-view w-view path]"]]
               (form/form-to [:post "/upload"]
                             (form/label "name" "name")
                             (form/text-field {:id "fnname" :class "forminput"} "name")
                             [:br]
                             (form/label "code" "code")
                             (form/text-area {:id "fnarea" :class "forminput"} "solver")
                             [:br]
                             (form/submit-button {:id "sub"} "Upload"))]))

(defn score-percentage [{w :w l :l d :d}]
  (let [total (+ w l d)
        percentage (if (zero? total)
                     0.0
                     (/ w total 0.01))]
    {:w w :l l :d d :p percentage}))

(defn collate-scores [solvers]
  (->>
   (dosync (into [] (map (fn [[name {score :score}]] [name @score]) solvers)))
   (map #(update-in % [1] score-percentage))
   (sort (fn [[_ {p1 :p}] [_ {p2 :p}]] (Double/compare p2 p1)))
   (map #(update-in % [1 :p] (partial format "%.2f")))))

(defn results-html []
  (html/html5 (head)
              [:body
               [:h1 "Amazing Dojo - Current Scores"]
               [:div
                [:table
                 [:thead
                  [:tr#head
                   [:th] [:th.name "Name"] [:th.score "Won"] [:th.score "Lost"] [:th.score "Drawn"] [:th.score "Percentage"]]]
                 [:tbody
                  (for [[solver {w :w l :l d :d p :p}] (collate-scores @ctrl/solvers)]
                    [:tr
                     [:td.delete [:img {:src "del.png"
                                        :onclick (str "javascript: if (confirm('Remove solver: " solver " ?')) window.location='/delete/" (rc/url-encode solver) "';")}]]
                     [:td.name solver]
                     [:td.score w]
                     [:td.score l]
                     [:td.score d]
                     [:td.score p]])]]]
               [:div [:a#upload {:href "submit"} "Upload a solver"]]]))

(defn submit-response [[code name submission exception]]
  (html/html5 (head)
              [:body
               (condp = code
                 :success (html [:p "Successfully uploaded function " name]
                                [:pre (with-out-str (pp/pprint submission))])
                 :eval-error (html [:p "Could not evaluate the function " name]
                                   [:p (.getMessage exception)]
                                   [:pre (with-out-str (pp/pprint submission))])
                 :read-error (html [:p "Could not read the text for the functon " name]
                                   [:p (.getMessage exception)]
                                   [:pre submission])
                 :test-error (html [:p "Failure when testing function " name]
                                   [:p (.getMessage exception)]
                                   [:pre (with-out-str (pp/pprint submission))])
                 :name-clash (html [:p "The name " name " is already taken."]
                                   [:pre (with-out-str (pp/pprint submission))]))
               [:a {:href "/"} "Back"]]))

(defn control-page []
  (html/html5 (head)
              [:body
               [:a {:href "/"} "Back to the game"]
               [:p "The game is currently " (if (ctrl/is-running?) "running." "not running.")]
               (if (ctrl/is-running?)
                 [:a {:href "/control/stop"} "Pause the game"]
                 [:a {:href "/control/start"} "Start the game"])]))

(defroutes main-routes
  (GET "/" _ (results-html))
  (GET "/submit" _ (submit-html))
  (GET "/delete/:solver" [solver]
    (ctrl/delete-solver solver)
    (rr/redirect "/"))
  (POST "/upload" [solver name] (submit-response (ctrl/process-solver name solver)))
  (GET "/control" _ (control-page))
  (GET "/control/:action" [action]
    (ctrl/start-stop (keyword action))
    (rr/redirect "/control"))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (handler/site main-routes))


;; This file is part of Amazing Dojo.

;; Amazing Dojo is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; Amazing Dojo is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with Amazing Dojo. If not, see <http://www.gnu.org/licenses/>.
