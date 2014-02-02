(ns app
  (:require [reagent.core :as reagent :refer [atom]]))

(def TWO_PI (* Math/PI 2))
(def HALF_PI (/ Math/PI 2))

(defn p [f center radius deg]
    (+ center (* radius (f (- (* TWO_PI deg) HALF_PI)))))

(defn position [center radius deg]
  {:x (p Math/cos center radius deg)
   :y (p Math/sin center radius deg)})

(defn Hand [props]
  (let [center (:center props)
        {x :x y :y} (position center (:length props) (:time props))]
    [:line {:x1 center :y1 center :x2 x :y2 y :stroke "black" :strokeWidth (:width props)}]))


(defn Face [props]
  [:g
   (for [i (range 60)]
     (let [{x :x y :y} (position (:center props) (:length props) (/ i 60))
           radius (if (= 0 (mod i 5)) 2 1)]
       [:circle {:cx x :cy y :r radius :fill "black"}]))])

(defn get-time[]
  (let [time (js/Date.)
        minutes (/ (.getMinutes time) 60)]
    {
     :hours (+ (/ (mod (.getHours time) 12) 12) (* (/ 1 12) minutes))
     :minutes minutes
     :seconds (/ (.getSeconds time) 60)}))

(defn Clock [props]
  (let [time (atom ((:f props)))
        size (:size props)
        center (/ size 2)]
    (fn []
      (js/setTimeout #(swap! time (:f props)) 1000)
      [:svg {:width size :height size}
       [Hand {:time (:hours @time)   :width 3 :length (* size .25) :center center}]
       [Hand {:time (:minutes @time) :width 2 :length (* size .4)  :center center}]
       [Hand {:time (:seconds @time) :width 1 :length (* size .4)  :center center}]
       [Face {:length (* size .4)  :center center}]])))


(reagent/render-component [Clock {:size 220 :f get-time} ]
  (.-body js/document))
