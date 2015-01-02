;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns prime.session
  (:require
    [ring.util :refer (https-request?)]
    [ring.middleware.cookies :refer (cookies-request)]
    [noir.session :as session :refer (wrap-noir-session)]
    [containium.utils.session-store :refer (mk-session-store)]))

(defn session-store [systems]
  (mk-session-store (:session-store systems)))


(defn wrap-secure-noir-session [f, opts]
  (let [secure-session-handler
        (wrap-noir-session f (merge opts {:cookie-attrs {:secure true :http-only true}}))
        regular-session-handler
        (wrap-noir-session f (merge opts {:cookie-attrs {:http-only true}}))]
    (fn [req]
      (if (https-request? req)
        (secure-session-handler  req)
        (do (println "WARN: REQUESTED SESSION OVER HTTP")
            (regular-session-handler req))))))


(defn wrap-sid-query-param
  "Uses the ?sid=.. query parameter value if there is no sid cookie"
  ([handler]
    (wrap-sid-query-param handler "sid"))
  ([handler cookie-name]
    (let [path [:cookies cookie-name :value]]
      #(let [req (cookies-request %)]
        (handler (if (get-in req path) req
                  #_else (assoc-in req path (-> req :params (get cookie-name)))))))))


(defn wrap-sid-session [handler session-store]
    (-> handler
      (wrap-secure-noir-session
        (assoc (if session-store {:store session-store} {})
               :cookie-name "sid"))))
