(ns frontend.dev
  (:require [reagent.core :as r]
            [reagent-dev-tools.state-tree :as dev-state]
            [reagent-dev-tools.core :as dev-tools]
            [frontend.state :as state]))

(defn init! []
  (js/console.log "Initialising dev tools...")
  (when js/goog.DEBUG
    (dev-state/register-state-atom "App state" state/state)
    (r/render [dev-tools/dev-tool {}] (js/document.getElementById "dev"))))
