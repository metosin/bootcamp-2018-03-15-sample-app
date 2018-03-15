(ns backend.components
  (:require [integrant.core :as ig]
            [backend.server :as server]
            [backend.routes :as routes]
            [backend.db :as db]))

(def components
  {::server/server {:host "localhost"
                    :port 3000
                    :path "/"
                    :handler (ig/ref ::routes/handler)}
   ::routes/handler {:ctx {:db (ig/ref ::db/db)}}
   ::db/db {:config {}}
   ::db/migrate {:db (ig/ref ::db/db)}})
