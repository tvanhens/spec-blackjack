(ns blackjack.deck
  (:require [clojure.spec :as s]
            [clojure.spec.test :as test]))

(def suits #{:hearts :diamonds :clubs :spades})

(def ranks (into #{:ace :king :queen :jack} (range 2 11)))

(def cards (for [rank ranks suit suits] [rank suit]))

(s/def ::card (s/cat :rank ranks :suit suits))

(s/def ::cards (s/coll-of ::card))

(s/def ::deck (s/and ::cards
                #(= 52 (count %))
                #(= % (dedupe %))))

(defn generate-deck [] (shuffle cards))
