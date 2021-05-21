(ns desafio.models)

(defn uuid [] (java.util.UUID/randomUUID))

(defn new-card
  [number cvv due-date limit]
   {:card/id (uuid)
    :card/number number
    :card/cvv cvv
    :card/due-date due-date
    :card/limit limit})

(defn new-client
  [nome cpf email]
   {:client/id (uuid)
    :client/name nome
    :client/cpf cpf
    :client/email email})

(defn new-category
  [nome]
  {:category/id (uuid)
   :category/name nome})

(defn new-purchase
  [occuredAt value place]
  {:purchase/id (uuid)
   :purchase/occuredAt occuredAt
   :purchase/value value
   :purchase/place place})
