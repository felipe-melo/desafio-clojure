(ns desafio.db
  (:require [datomic.api :as datomic]))

(def db-uri "datomic:dev://localhost:4334/desafio")

(defn abre-conexao! []
  (datomic/create-database db-uri)
  (datomic/connect db-uri))

(defn apaga-banco! []
  (datomic/delete-database db-uri))

(def schema [; Cartão de crédito
             {:db/ident :card/id
              :db/valueType :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity
              :db/doc "uuid do cartão"}
             {:db/ident :card/number
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "número do cartão"}
             {:db/ident :card/cvv
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "código de segurança do cartão"}
             {:db/ident :card/due-date
              :db/valueType :db.type/instant
              :db/cardinality :db.cardinality/one
              :db/doc "Data de validade do cartão"}
             {:db/ident :card/limit
              :db/valueType :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc "Limite do cartão"}

             ; Usuário
             {:db/ident :client/id
              :db/valueType :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity
              :db/doc "uuid do cliente"}
             {:db/ident :client/name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "nome do cliente"}
             {:db/ident :client/email
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "email do cliente"}
             {:db/ident :client/cpf
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "CPF do cliente"}
             {:db/ident :client/card
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/many
              :db/doc "cartão do cliente"}

             ; Purchase
             {:db/ident :purchase/id
              :db/valueType :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity
              :db/doc "uuid da compra"}
             {:db/ident :purchase/occuredAt
              :db/valueType :db.type/instant
              :db/cardinality :db.cardinality/one
              :db/doc "data da compra"}
             {:db/ident :purchase/value
              :db/valueType :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc "valor da compra"}
             {:db/ident :purchase/place
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "local da compra"}
             {:db/ident :purchase/card
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one
              :db/doc "cartão utilizado na compra"}
             {:db/ident :purchase/category
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one
              :db/doc "categoria da compra"}

             ; Category
             {:db/ident :category/id
              :db/valueType :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity
              :db/doc "uuid da categoria"}
             {:db/ident :category/name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "nome da categoria"}])

(defn cria-schema! [conn]
  (datomic/transact conn schema))

(defn link-card-to-user! [conn client card]
  (datomic/transact conn [[:db/add
                          [:client/id (:client/id client)]
                          :client/card [:card/id (:card/id card)]]]))

(defn link-category-to-purchase! [conn purchase category]
  (datomic/transact conn [[:db/add
                          [:purchase/id (:purchase/id purchase)]
                          :purchase/category [:category/id (:category/id category)]]]))

(defn link-card-to-purchase! [conn purchase card]
  (datomic/transact conn [[:db/add
                          [:purchase/id (:purchase/id purchase)]
                          :purchase/card [:card/id (:card/id card)]]]))

(defn create-client! [conn client card] 
  (datomic/transact conn [client])
  (datomic/transact conn [card])
  (link-card-to-user! conn client card))

(defn create-purchase! [conn purchase card category]
  (datomic/transact conn [purchase])
  (link-category-to-purchase! conn purchase category)
  (link-card-to-purchase! conn purchase card))

(defn create-categories! [conn categories]
  (datomic/transact conn categories))

(defn todas-as-compras [conn]
  (datomic/q '[:find (pull ?purchase [*])
               :where [?purchase :purchase/id]]
             (datomic/db conn)))

(defn count-clients-purchases [conn]
  (datomic/q '[:find (count ?purchase) (pull ?client [*])
               :where
               [?card :card/id]
               [?purchase :purchase/card ?card]
               [?client :client/card ?card]
               :keys count client]
             (datomic/db conn)))

(defn top-purchases-count-client [conn]
  (->>
   (count-clients-purchases conn)
   (apply max-key :count)
   ))

(defn top-purchases-value-client [conn]
  (->>
   (datomic/q '[:find ?value (pull ?client [*])
                :where
                [?card :card/id]
                [?purchase :purchase/card ?card]
                [?purchase :purchase/value ?value]
                [?client :client/card ?card]
                :keys value client]
              (datomic/db conn))
   (apply max-key :value)))
