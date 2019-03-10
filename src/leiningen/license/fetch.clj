(ns leiningen.license.fetch
  (:require [leiningen.license.list :refer [match-licenses]]
            [leiningen.core.main :as main]
            [clojure.string :as string]
            [clojure.set :refer [rename-keys]]
            [cheshire.core :as json]))

;; ## URLs

(def ^:dynamic *raw-prefix*
  "https://api.github.com/licenses/")

;; ## License I/O

(defn fetch-license!
  "Fetch license data from the Github API."
  [license-key & [seen]]
  (if-not (contains? seen license-key)
    (or (try
          (some-> (str *raw-prefix* license-key)
                  (slurp :encoding "UTF-8")
                  (json/parse-string keyword)
                  (rename-keys {:name     :title
                                :html_url :source
                                :body     :text}))
          (catch Exception _))
        (if-let [matches (map second (match-licenses (name license-key)))]
          (if (next matches)
            {:error (str "No such License. Did you mean: "
                         (string/join ", " matches)
                         "?")}
            (fetch-license! (first matches) (conj (set seen) license-key)))
          {:error "No such License."}))
    {:error "Could not load License."}))
