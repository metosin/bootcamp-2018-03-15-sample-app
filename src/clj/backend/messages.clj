(ns backend.messages
  (:require [clojure.tools.logging :as log]
            [compojure.api.sweet :refer [context GET POST]]
            [ring.util.http-response :as resp]
            [schema.core :as s]
            [schema-tools.core :as st]
            [jdbc.core :as jdbc]
            [clj-uuid :as uuid])
  (:import (java.util UUID)))

;;
;; Message:
;;

(s/defschema Message {:id UUID
                      :message s/Str})

(s/defschema NewMessage (st/dissoc Message :id))

(def routes
  (context "/message" []
    (GET "/" []
      :components [db]
      :return [Message]
      (->> (with-open [conn (jdbc/connection db)]
             (jdbc/fetch conn "select * from app.messages"))
           (resp/ok)))

    (POST "/" []
      :body [new-message NewMessage]
      :components [db]
      :return [Message]
      (let [id (uuid/v1)
            message (assoc new-message :id id)]
        (-> (with-open [conn (jdbc/connection db)]
              (jdbc/atomic conn
                (jdbc/execute conn ["insert into app.messages (id, message) values (?, ?)" id (:message message)])
                (jdbc/fetch conn "select * from app.messages")))
            (resp/ok))))))
