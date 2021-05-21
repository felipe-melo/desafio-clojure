(ns desafio.desafio1)
(require '[clj-time.core :as t])

(defn cards
  []
  [{:number "1111111111111111"
    :cvv 123
    :due-date (t/date-time 2022 12 30)
    :limit 1000}
   {:number "2222222222222222"
    :cvv 321
    :due-date (t/date-time 2024 06 18)
    :limit 1500}
   {:number "3333333333333333"
    :cvv 231
    :due-date (t/date-time 2025 04 30)
    :limit 2500}])

(defn clients
  []
  [{:name "Felipe Melo"
    :cpf "11111111111"
    :email "felipe.bezerra@nubank.com.br"
    :card (get cards 0)}
   {:name "Bianca Abreu"
    :cpf "22222222222"
    :email "bianca.abreu@nubank.com.br"
    :card (get cards 1)}
   {:name "Lionel Messi"
    :cpf "33333333333"
    :email "lionel.messi@nubank.com.br"
    :card (get cards 2)}])

(defn purchases
  []
  [{:date (t/date-time 2021 05 10)
    :value 50.64
    :place "mercado"
    :category "food"
    :card (get cards 0)}
   {:date (t/date-time 2021 05 11)
    :value 234.9
    :place "steam"
    :category "entreteniment"
    :card (get cards 0)}
   {:date (t/date-time 2021 05 06)
    :value 4.9
    :place "net shoes"
    :category "sport"
    :card (get cards 1)}
   {:date (t/date-time 2021 04 25)
    :value 42.94
    :place "net shoes"
    :category "sport"
    :card (get cards 2)}])

(defn filter-purchase
  [key, value]
  (filter #(= value (key %)) (purchases)))

(defn filter-client
  [key, value]
  (filter #(= value (key %)) (clients)))

(defn values-by-category
  [category]
  (let [filtered-values (filter-purchase :category category)]
    (reduce + (map :value filtered-values))))

(defn month-invoice
  [cpf]
  (let [client (filter-client :cpf cpf)
        client-purchases (filter-purchase #(-> % :card :number) (:number (:card client)))]
    (->>
     client-purchases
     (filter #(= (t/month (:date %)) (t/month (t/now))))
     (map :value)
     (reduce +))))

(println (values-by-category "sport"))
(println (filter-purchase :value 4.9))
(println (filter-purchase :place "net shoes"))

(month-invoice "11111111111")
