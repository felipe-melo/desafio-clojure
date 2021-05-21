(ns desafio.desafio2
  (:use clojure.pprint)
  (:require [desafio.db :as db])
  (:require [desafio.models :as models]))
(use 'clojure.instant)

(def conn (db/abre-conexao!))
(db/cria-schema! conn)

; ---------------- cria clientes com cartões ----------------
(def client1 (models/new-client "Felipe Melo" "11111111111" "felipe@nubank.com.br"))
(def card1 (models/new-card "1111111111111111" "123" (read-instant-date "2022-12-30T10:22:22") 1000M))

(db/create-client! conn client1 card1)

(def client2 (models/new-client "Bianca Abreu" "22222222222" "bianca@nubank.com.br"))
(def card2 (models/new-card "2222222222222222" "321" (read-instant-date "2024-06-18T10:22:22") 2000M))

(db/create-client! conn client2 card2)

(def client3 (models/new-client "Marisa Silva" "33333333333" "marisa@nubank.com.br"))
(def card3 (models/new-card "3333333333333333" "231" (read-instant-date "2025-04-30T10:22:22") 3500M))

(db/create-client! conn client3 card3)

(def client4 (models/new-client "James" "44444444444" "james@nubank.com.br"))
(def card4 (models/new-card "4444444444444444" "234" (read-instant-date "2025-04-30T10:22:22") 35000M))

(db/create-client! conn client4 card4)
; ---------------- cria clientes com cartões ----------------

; --------------------- cria categogiras --------------------
(def category1 (models/new-category "mercado"))
(def category2 (models/new-category "serviço"))
(def category3 (models/new-category "eletrônico"))

(db/create-categories! conn [category1 category2 category3])
; --------------------- cria categogiras --------------------

; -------------------- criação de compras -------------------
(def purchase1 (models/new-purchase (read-instant-date "2021-05-20T10:22:22") 240M "Rio de Janeiro"))
(def purchase2 (models/new-purchase (read-instant-date "2021-05-20T10:22:22") 7240M "Rio de Janeiro"))
(def purchase3 (models/new-purchase (read-instant-date "2021-05-20T10:22:22") 240M "Rio de Janeiro"))
(def purchase4 (models/new-purchase (read-instant-date "2021-05-20T10:22:22") 540M "Rio de Janeiro"))
(def purchase5 (models/new-purchase (read-instant-date "2021-05-20T10:22:22") 540M "Rio de Janeiro"))
(def purchase6 (models/new-purchase (read-instant-date "2021-05-20T10:22:22") 540M "Rio de Janeiro"))

(db/create-purchase! conn purchase1 card1 category1)
(db/create-purchase! conn purchase2 card2 category2)
(db/create-purchase! conn purchase3 card3 category3)
(db/create-purchase! conn purchase4 card1 category2)
(db/create-purchase! conn purchase5 card3 category2)
(db/create-purchase! conn purchase6 card3 category2)
; -------------------- criação de compras -------------------

(pprint (db/todas-as-compras conn))
(pprint (db/top-purchases-count-client conn))
(pprint (db/top-purchases-value-client conn))

(db/apaga-banco!)
