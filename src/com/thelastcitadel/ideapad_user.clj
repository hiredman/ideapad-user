(ns com.thelastcitadel.ideapad-user
  (:require [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET ANY POST]]
            [compojure.route :refer [not-found]]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows]
            [cemerick.friend.credentials :as creds]
            [clojure.java.jdbc :as jdbc]
            [ring.util.response :refer [redirect]])
  (:import (java.util UUID)))


(def user-users {"root" {:username "root"
                         :password (creds/hash-bcrypt "password")
                         :roles #{::authenticated}}})

(defn users [username [url table]]
  (jdbc/with-connection url
    (jdbc/with-query-results results
      [(str "SELECT username,password FROM " table " WHERE username=?")
       username]
      (first (for [r results]
               (assoc r
                 :roles #{:com.thelastcitadel.ideapad/authenticated}))))))

(defn user-lookup* [request]
  {:status 200
   :body (pr-str (users (:username (:params request)) (::url request)))
   :headers {"Content-Type" "application/edn"}})

(def user-lookup (-> user-lookup*
                     (friend/wrap-authorize #{::authenticated})))

(def sites (atom {}))

(defroutes app
  (GET "/" request
       {:status 200
        :body ""})
  (POST "/" request
        (if-let [url (:jdbc-url (:params request))]
          (let [table (:table (:params request))
                id (str (UUID/randomUUID))]
            (swap! sites assoc id [url table])
            (redirect (str "/site/" id)))
          {:status 400
           :body "whoops"}))
  (GET "/site/:id" request
       {:status 200
        :body "configured"})
  (GET "/site/:id/:username" request
       (user-lookup (assoc request
                      ::url (get @sites (get (:params request) :id)))))
  (ANY "/login" request
       {:status 200
        :body ""})
  (friend/logout
   (ANY "/logout" request (ring.util.response/redirect "/")))
  (not-found "wuh oh"))

(def handler (-> #'app
                 (friend/authenticate
                  {:credential-fn (partial creds/bcrypt-credential-fn user-users)
                   :workflows [(workflows/interactive-form)]})
                 site))
