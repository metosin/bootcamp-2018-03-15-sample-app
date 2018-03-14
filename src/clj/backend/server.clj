(ns backend.server
  (:require [integrant.core :as ig]
            [immutant.web :as immutant]))

;;
;; Web server:
;;

(defmethod ig/init-key ::server [_ {:keys [handler] :as opts}]
  (immutant/run handler (dissoc opts :handler)))

(defmethod ig/halt-key! ::server [_ server]
  (immutant/stop server))
