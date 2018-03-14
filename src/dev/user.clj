(ns user
  (:require [integrant.repl :as igr :refer [clear reset-all]]
            [integrant.repl.state :as state]))

(igr/set-prep!
  (fn []
    (require 'backend.components)
    (deref (resolve 'backend.components/components))))

(def reset igr/reset)
(def start igr/init)
(def stop igr/halt)
(def system state/system)
