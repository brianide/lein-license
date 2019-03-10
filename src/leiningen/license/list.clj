(ns leiningen.license.list
  (:require [leiningen.core.main :as main]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :refer [starts-with? lower-case]]))

;; ## Query

(def ^:dynamic *api-url*
  "https://api.github.com/licenses")

(defn query-licenses!
  []
  (-> (slurp *api-url* :encoding "UTF-8")
      (json/parse-string keyword)
      (as-> lic (map (juxt :name :key :url) lic))))

;; ## License List

(defn list-licenses
  []
  (let [home (or (some-> (System/getenv "LEIN_HOME") io/file)
                 (io/file (System/getProperty "user.home") ".lein"))
        cache-file (io/file home ".licenses.edn")
        delta (when (.isFile cache-file)
                (- (System/currentTimeMillis) (.lastModified cache-file)))]
    (or (try
          (when (some-> delta (< 3600000))
            (binding [*read-eval* false]
              (when-let [result (seq (read-string (slurp cache-file :encoding "UTF-8")))]
                result)))
          (catch Exception _
            (main/warn "could not read local license cache.")))
        (let [licenses (query-licenses!)]
          (try
            (spit cache-file (pr-str licenses))
            (catch Exception _
              (main/warn "could not update local license cache.")))
          licenses))))

(defn match-licenses
  [^String prefix]
  (if (seq prefix)
    (let [prefix (lower-case prefix)
          pred (fn [[name key]] (or (starts-with? (lower-case name) prefix)
                                    (starts-with? (lower-case key) prefix)))]
      (filter pred (list-licenses)))
    (list-licenses)))
