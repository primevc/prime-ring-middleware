;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns ring.util)

;;; Helper functions.

(defn ->when
  "Helper for the -> macro.
  Returns: (`wrapper `handler) if `wrap? is truthy, else just `handler."
  [handler wrap? wrapper]
  {:pre [handler wrapper]}
  (if wrap? (wrapper handler) handler))


(defn https-request? [req]
  (or (= (:scheme req) :https)
      (= (get-in req [:headers "x-forwarded-proto"]) "https")))

