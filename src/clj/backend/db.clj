(ns backend.db
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [hikari-cp.core :as hikari]
            [jdbc.core :as jdbc]
            [clj-uuid :as uuid])
  (:import (org.flywaydb.core Flyway)))

;;
;; DB connection pool:
;;

(def default-config {:database-name "bootcamp"
                     :username "bootcamp"
                     :password "bootcamp"
                     :server-name "localhost"
                     :port-number 5432
                     :connection-timeout 3000
                     :idle-timeout 600000
                     :max-lifetime 1800000
                     :minimum-idle 1
                     :maximum-pool-size 20
                     :adapter "postgresql"})

(defmethod ig/init-key ::db [_ {:keys [config]}]
  (hikari/make-datasource (merge default-config config)))

(defmethod ig/halt-key! ::db [_ db]
  (hikari/close-datasource db))

(comment
  (with-open [db (hikari/make-datasource default-config)]
    (jdbc/fetch db "select 42 as \"answer\"")))

;;
;; Migrations:
;;

(def +schemas+ ["public"])
(def +locations+ ["classpath:db/migrations"])

(defn ^Flyway make-flyway [db]
  (doto (Flyway.)
    (.setDataSource db)
    (.setSchemas (into-array String +schemas+))
    (.setLocations (into-array String +locations+))))

(defn migrate [^Flyway flyway]
  (.migrate flyway)
  flyway)

(defn clean-db [^Flyway flyway]
  (.clean flyway)
  flyway)

(defmethod ig/init-key ::migrate [_ {:keys [db]}]
  (-> db
      (make-flyway)
      (migrate)))

(comment
  (with-open [ds (hikari/make-datasource default-config)]
    (-> ds
        (make-flyway)
        (clean-db)
        (migrate))))

(comment
  (with-open [db (hikari/make-datasource default-config)]
    (jdbc/fetch db "select * from messages"))

  (with-open [db (hikari/make-datasource default-config)
              conn (jdbc/connection db)]
    (jdbc/atomic conn
      (jdbc/execute conn ["insert into messages (id, message) values (?, ?)" (uuid/v1) "Heeloz"])))

  (with-open [db (hikari/make-datasource default-config)
              conn (jdbc/connection db)]
    (jdbc/atomic conn
      (jdbc/execute conn ["delete from messages"]))))
