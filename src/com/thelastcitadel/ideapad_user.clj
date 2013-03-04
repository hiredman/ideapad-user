(ns com.thelastcitadel.ideapad-user
  (:require [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET ANY]]
            [compojure.route :refer [not-found]]
            [cheshire.core :as json]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows]
            [cemerick.friend.credentials :as creds]
            [carica.core :refer [config]]))


(def user-users {"root" {:username "root"
                         :password (creds/hash-bcrypt "password")
                         :roles #{::authenticated}}})

(def users {"root" {:username "root"
                    :password (creds/hash-bcrypt "password")
                    :roles #{:com.thelastcitadel.ideapad/authenticated}}})

(defn user-lookup* [request]
  {:status 200
   :body (pr-str (get users (:username (:params request))))
   :headers {"Content-Type" "application/edn"}})

(def user-lookup (-> user-lookup*
                     (friend/wrap-authorize #{::authenticated})))

(defroutes app
  (ANY "/" request
       {:status 200
        :body ""})
  (ANY "/login" request
       {:status 200
        :body ""})
  (GET "/user/:username" request  user-lookup)
  (friend/logout
   (ANY "/logout" request (ring.util.response/redirect "/")))
  (not-found "wuh oh"))

(def handler (-> #'app
                 (friend/authenticate
                  {:credential-fn (partial creds/bcrypt-credential-fn user-users)
                   :workflows [(workflows/interactive-form)]})
                 site))