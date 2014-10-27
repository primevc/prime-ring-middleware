;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns prime.middleware
  (:require
    [ring.middleware.params]
    [ring.middleware.resource]
    [ring.middleware.file-info]
    [ring.middleware.method-override]
    [ring.util :refer [->when]]
    [prime.session :as session :refer (wrap-sid-session wrap-sid-query-param)]
    [prone.middleware :as prone]
    [clojure.string :refer (lower-case)]))


(defn wrap
  "Wrap a handler with the base set of middlewares used by every web app that requires sessions.
  Option flags:
    :with-query-sid  Support session-IDs in the URL as query param: ?sid=..."
  [handler session-store & opts]
  (let [optset (apply hash-set opts)]
    (-> handler
      (ring.middleware.resource/wrap-resource "public")
      (ring.middleware.file-info/wrap-file-info)
      (ring.middleware.method-override/wrap-method-override)
      (wrap-sid-session session-store)
      (->when (optset :with-query-sid) wrap-sid-query-param)
      (ring.middleware.params/wrap-params))))


(defn wrap-prone
  [handler & [name]]
  (alter-var-root #'prone.middleware/get-application-name
                  (constantly (constantly (or name ""))))
  (let [vos? (try (import 'prime.vo.ValueObject) true (catch Exception ex false))]
    (when vos?
      (eval
       '(let [orig @#'prone.prep/prepare-for-serialization-1]
          (alter-var-root #'prone.prep/prepare-for-serialization-1
                          (constantly
                           (fn [val]
                             (cond
                              (instance? prime.vo.ValueObject val) (into {} (seq val))
                              (instance? org.bson.types.ObjectId val) (prime.types/to-String val)
                              :else (orig val)))))))))
  (prone/wrap-exceptions handler))


(defn wrap-redirect-trailing-slash
  "Returns a redirect response when a trailing slash is encountered in
  the URI, to the URI without the trailing slash."
  [handler]
  (let [uri-fn (fn [uri]
                 (when (and (.endsWith uri "/") (not (= "/" uri)))
                   (subs uri 0 (dec (count uri)))))]
    (fn [request]
      (if-let [without (uri-fn (:uri request))]
        (let [url (str (:context-path request)
                       without
                       (when-let [qs (:query-string request)]
                         (str "?" qs)))]
          {:status 302
           :headers {"Location" url}}))
      (handler request))))


(defn wrap-redirect-www
  "Returns a redirect response to the www-less counterpart, whenever a
  www. subdomain is encountered."
  [handler]
  (fn [request]
    (let [host (get-in request [:headers "host"])]
      (if (.startsWith (lower-case host) "www.")
        (let [url (str (subs host 4)
                       (:context-path request)
                       (:uri request)
                       (when-let [qs (:query-string request)]
                         (str "?" qs)))]
          {:status 301
           :headers {"Location" url}})
        (handler request)))))
