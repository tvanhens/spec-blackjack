(ns blackjack.game
  (:require [blackjack.deck :as deck]
            [clojure.spec :as s]
            [clojure.spec.test :as test]
            [clojure.spec.gen :as gen]))

(defn init-game []
  {::deck        (deck/generate-deck)
   ::player-hand []
   ::dealer-hand []})

(s/def ::deck ::deck/cards)

(s/def ::player-hand ::deck/cards)

(s/def ::dealer-hand ::deck/cards)

(s/def ::dealer-hidden ::deck/cards)

(s/def ::game
  (s/and (s/keys :req [::deck
                       ::player-hand
                       ::dealer-hand]
                 :opt [::dealer-hidden])
         #(s/valid? ::deck/deck
                    (into []
                          (concat (::deck %)
                                  (::player-hand %)
                                  (::dealer-hidden %)
                                  (::dealer-hand %))))))

(s/fdef init-game :ret ::game)

(defn draw [deck n]
  (let [split (partition n deck)]
    (if (seq split)
      [(into [] (first split)) (into [] (apply concat (rest split)))]
      [deck []])))

(s/fdef draw
        :args (s/cat :draw ::deck/cards :count integer?)
        :ret  (s/cat :drawn ::deck/cards :draw ::deck)
        :fn   #(or (= (-> % :args :count) (-> % :ret :drawn count))
                   (= (-> % :args :draw count) (-> % :ret :deck count))))

(defn deal [{:keys [::deck] :as game}]
  (let [[player-hand deck]   (draw deck 2)
        [dealer-hand deck]   (draw deck 1)
        [dealer-hidden deck] (draw deck 1)]
    {::deck          deck
     ::player-hand   player-hand
     ::dealer-hand   dealer-hand
     ::dealer-hidden dealer-hidden}))

(s/fdef deal
        :args (s/cat :game ::game)
        :ret  ::game)

(defn hit [{:keys [::deck ::player-hand] :as game}]
  (let [[[card] deck] (draw deck 1)]
    (-> game
        (update ::player-hand conj card)
        (assoc ::deck deck))))

(s/fdef deal
  :args (s/cat :game ::game)
  :ret  ::game)

(test/instrument)
