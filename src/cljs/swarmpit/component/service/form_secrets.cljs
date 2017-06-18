(ns swarmpit.component.service.form-secrets
  (:require [material.component :as comp]
            [swarmpit.component.state :as state]
            [rum.core :as rum]))

(enable-console-print!)

(def cursor [:page :service :wizard :secrets])

(def headers ["Name"])

(def undefined
  (comp/form-value "No secrets defined for the service."))

(defn- secret-id
  [secret-name secrets]
  (->> (filter #(= secret-name (:secretName %)) secrets)
       (first)
       :id))

(defn- form-secret [value index data]
  (comp/table-row-column
    {:name (str "form-secret-" index)
     :key  (str "form-secret-" index)}
    (comp/form-list-selectfield
      {:name     (str "form-secret-select-" index)
       :key      (str "form-secret-select-" index)
       :value    value
       :onChange (fn [_ _ v]
                   (state/update-item index :secretName v cursor)
                   (state/update-item index :id (secret-id v data) cursor))}
      (->> data
           (map #(comp/menu-item
                   {:name        (str "form-secret-item-" (:secretName %))
                    :key         (str "form-secret-item-" (:secretName %))
                    :value       (:secretName %)
                    :primaryText (:secretName %)}))))))

(defn- render-secrets
  [item index data]
  (let [{:keys [secretName]} item]
    [(form-secret secretName index data)]))

(defn- form-table
  [secrets data]
  (comp/form-table []
                   secrets
                   data
                   render-secrets
                   (fn [index] (state/remove-item index cursor))))

(defn add-item
  []
  (state/add-item {:secretName ""
                   :id         ""} cursor))

(rum/defc form-create < rum/reactive [data]
  (let [secrets (state/react cursor)]
    [:div
     (comp/form-add-btn "Expose secrets" add-item)
     (if (not (empty? secrets))
       (form-table secrets data))]))

(rum/defc form-update < rum/reactive [data]
  (let [secrets (state/react cursor)]
    (if (empty? secrets)
      undefined
      (form-table secrets data))))

(rum/defc form-view < rum/static [secrets]
  (if (empty? secrets)
    undefined
    (comp/form-info-table ["Name"] secrets identity "300px")))