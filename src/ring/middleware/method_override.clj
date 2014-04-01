;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns ring.middleware.method-override
  (:require [clojure.string :refer (lower-case)]))

(defn wrap-method-override
  "Ring middleware for method overriding (X-HTTP-Method-Override and
  _method query parameter). From github.com/myfreeweb/ringfinger."
  [handler]
  (fn [req]
    (let [rm (or (get-in req [:headers "x-http-method-override"])
                 (get-in req [:query-params "_method"]))]
      (handler (if rm (assoc req :request-method (keyword (lower-case rm))) req)))))
