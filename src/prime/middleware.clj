;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns prime.middleware
  (:require
    [ring.middleware.resource]
    [ring.middleware.file-info]
    [ring.middleware.method-override]
    [ring.util :refer [->when]]
    [prime.session :as session :refer (wrap-sid-session wrap-sid-query-param)]))


(defn wrap
  "Wrap a handler with the base set of middlewares used by every web app that requires sessions.
  Option flags:
    :with-query-sid  Support session-IDs in the URL as query param: ?sid=..."
  [handler session-store & opts]
  (let [opts (hash-set opts)]
    (-> handler
      (ring.middleware.resource/wrap-resource "public")
      (ring.middleware.file-info/wrap-file-info)
      (ring.middleware.method-override/wrap-method-override)
      (->when (opts :with-query-sid) wrap-sid-query-param)
      (wrap-sid-session session-store))))
