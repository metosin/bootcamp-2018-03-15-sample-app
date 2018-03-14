(ns backend.routes
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.core :as hiccup]
            [hiccup.page :as page]
            [compojure.api.middleware :as mw]
            [backend.messages :as messages]))

;;
;; Index page
;;

(def index
  (-> (hiccup/html
        (page/html5
          [:head
           [:title "Bootcamp 2018-03-15 sample app"]
           [:meta {:charset "utf-8"}]
           [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
           [:meta {:name "viewport" :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
           (page/include-css "/css/style.css")]
          [:body
           [:div#app
            [:div.loading
             [:h1 "Loading..."]]]
           [:div#dev]
           (page/include-js "/js/main.js")]))
      (resp/ok)
      (resp/content-type "text/html; charset=utf-8")))

;;
;; API handler:
;;

(def api-handler
  (api
    {:swagger {:ui "/swagger"
               :spec "/swagger.json"
               :data {:info {:title "Bootcamp 2018-03-15", :description "Sample app API"}}}}

    (undocumented
      (GET "/" []
        index))

    (context "/api" []
      messages/routes)))

;;
;; HTTP request handler:
;;

(def site-config (assoc-in site-defaults [:security :anti-forgery] false))

(defmethod ig/init-key ::handler [_ {:keys [ctx]}]
  (some-fn (-> #'api-handler
               (mw/wrap-components ctx)
               (wrap-defaults site-config))
           (constantly (resp/not-found "Say wha?"))))
