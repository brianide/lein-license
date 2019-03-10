(defproject org.clojars.brianide/lein-license "0.1.10-SNAPSHOT"
  :description "Project-Level License Management."
  :url "https://github.com/brianide/lein-license"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :key "mit"
            :year 2015}
  :dependencies [^:source-dep [rewrite-clj "0.6.1" :exclusions [org.clojure/clojure]]
                 ^:source-dep [cheshire "5.8.0" :exclusions [org.clojure/clojure]]]
  :eval-in-leiningen true
  :pedantic? :abort)
