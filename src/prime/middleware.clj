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
    [prone.middleware :as prone]))


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
