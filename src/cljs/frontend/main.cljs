(ns frontend.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [frontend.dev :as dev]
            [frontend.state :as state]))

;;
;; Utils:
;;

(defn prevent-default [e]
  (.preventDefault e)
  e)

;;
;; New message form:
;;

(defn submit! [e]
  (prevent-default e)
  (let [message (-> @state/state :message)]
    (swap! state/state dissoc :message)
    (go
      (->> (http/post "/api/message" {:edn-params {:message message}})
           <!
           :body
           (swap! state/state assoc :messages)))))

(defn set-message [e]
  (->> e
       prevent-default
       .-target
       .-value
       (swap! state/state assoc :message)))

(defn message-form [{:keys [message]}]
  [:form.message-form {:on-submit submit!}
   [:div "Say something:"]
   [:input {:value message
            :on-change set-message}]
   [:button {:on-click submit!} "Send"]])

;;
;; Messages view:
;;

(defn messages-view [{:keys [messages]}]
  (when messages
    [:div.messages
     [:ul
      (for [{:keys [id message]} messages]
        [:li {:key id} message])]]))

;;
;; Main view:
;;

(defn main-view []
  (let [state @state/state]
    [:div.main-view
     [:h1 "Messages"]
     [message-form state]
     [messages-view state]]))

;;
;; Main
;;

(defn init! []
  (js/console.log "Initialising frontend...")
  (go
    (->> (http/get "/api/message" {:accept "application/edn"})
         <!
         :body
         (swap! state/state assoc :messages))
    (r/render [main-view] (js/document.getElementById "app"))
    (dev/init!)))

(init!)
