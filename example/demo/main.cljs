(ns demo.main
  (:require
   ["preact/hooks" :as hooks]
   [precog.main :as precog :refer [html use-atom use-lens bind-handler]]))

(defn dtdd [{:keys [dt dd]}]
  (html [:<> [:dt dt] [:dd dd]]))

(defn UsesJsProps [props]
  (html [:h2 "wrapped: " (.-title props)]))
(precog/use-js-props UsesJsProps)

(defn has-children [{:keys [title children]}]
  (html [:div
         [:h2 title]
         children]))

(defn clicker []
  (let [clicks (use-atom 0)
        increment (bind-handler clicks nil inc)
        decrement (bind-handler clicks nil dec)]
    (html
     [:div
      [:div
       "clicked " @clicks " times: "
       (if (odd? @clicks) "odd" "even")
       ", "
       (case @clicks
         0 [:em "none"]
         1 [:em "try harder"]
         2 [:u "that's company"]
         [:strong "that's a crowd!"])]
      [:div
       [:button {:onClick increment} "increment"]
       [:button {:onClick decrement} "decrement"]]])))

(defn lens-input [{:keys [state]}]
  (let [input (use-lens state get :input "")
        update-input (bind-handler state #{:target} (fn [s {:keys [value]}] (assoc s :input value)))]
    (html
     [:div
      [:label "lens input"
       [:input {:type    "text"
                :value   input
                :onInput update-input}]
       " " (count input)]])))

(defn atom-input []
  (let [*input (use-atom "foo")
        update-input (bind-handler *input #{:target} (fn [_ {:keys [value]}] value))]
    (html
     [:label "atom input"
      [:input {:type    "text"
               :value   @*input
               :onInput update-input}]
      " "
      (cond
        (zero? (count @*input)) "empty"
        :default [:strong "some"])])))

(defn app [{:keys [state]}]
  (let [hello "hello"]
    (html
     [:div
      [:div hello]
      (when true
        [:strong "World!!"])
      [:<> [:div "one"] [:div "two"]]
      [UsesJsProps {:title "hello wrapped"}]
      [has-children {:title "hello children"}
       [:div "why hello papa"]
       [:div "yes hello"]]
      [:dl
       [dtdd {:dt "term"
              :dd "definition"}]
       [dtdd {:dt "term2"
              :dd "definiton2"}]]
      [clicker]
      [lens-input {:state state}]
      [atom-input]
      [:ul
       (for [x (range 10)]
         [:li x])]
      (let [greet "hello"
            thing "world"]
        (list
         [:div {:key "one"} greet]
         [:div {:key "two"} thing]))])))

(def app-ele (js/document.getElementById "app"))

(defonce state (atom {}))

(defn render []
  (js/console.log ::render)
  (precog/render (html [app {:state state}]) app-ele)
  (js/console.log ::render-done))

(defn main! []
  (js/console.log ::main!)
  (render))

(defn reload! []
  (js/console.log ::reload!)
  (render))
