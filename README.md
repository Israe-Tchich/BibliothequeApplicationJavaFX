# 📚 Gestion de Bibliothèque - JavaFX

## 🎥 Vidéo de démonstration

[Voir la vidéo](https://drive.google.com/file/d/1fBWMm-Q7FdBtFi7F8G7bjbpor8iTvQ2i/view?usp=sharing)

## Rapport du projet 
https://docs.google.com/document/d/1oWSCEMsFbq2KetUs81x563PXsDUuPQnKxqTw8gLzKIc/edit?usp=sharing

Application de gestion d'une bibliothèque développée en JavaFX avec une base de données MySQL.

## 👥 Réalisé par
- [Imane Mesrar]
- [Israe Tchich]

**Module :** Développement Java IHM - GI3 ENSAO
**Encadrante :** Mme Douae EL HILA
**Année :** 2025/2026

## 📋 Description

Application permettant de gérer les livres et les emprunts d'une bibliothèque :
- Gestion des livres (ajout, modification, suppression)
- Gestion des emprunts liés aux livres
- Recherche et filtrage par catégorie / statut
- Statistiques (stock disponible, répartition par catégorie)
- Export des données en CSV

## 🛠️ Prérequis

- Java JDK 17 ou supérieur
- JavaFX SDK 25
- MySQL (via WAMP / phpMyAdmin)
- IntelliJ IDEA
- Connecteur MySQL JDBC (mysql-connector-j)

## ⚙️ Installation et lancement

1. **Cloner le projet** depuis GitHub

2. **Importer la base de données :**
   - Lancer WAMP
   - Ouvrir phpMyAdmin (`http://localhost/phpmyadmin`)
   - Créer une base de données nommée `bibliotheque`
   - Exécuter le script `init_db.sql` dans l'onglet SQL

3. **Ouvrir le projet dans IntelliJ IDEA**

4. **Configurer les librairies :**
   - File → Project Structure → Libraries
   - Ajouter tous les `.jar` du dossier `lib` de JavaFX SDK
   - Ajouter le `.jar` du connecteur MySQL (mysql-connector-j)
   - --module-path "chemin/vers/javafx-sdk-25/lib" --add-modules javafx.controls,javafx.fxml
  6. **Lancer l'application** en exécutant `application/Main.java`

## 🗄️ Base de données

- **Nom de la base :** `bibliotheque`
- **Tables :** `livre`, `emprunt`
- **Connexion configurée dans :** `dao/Database.java`
- **Identifiants par défaut :** utilisateur `root`, sans mot de passe (configuration WAMP par défaut)

## 🖥️ Fonctionnalités

- ✅ CRUD complet sur les livres (ajout, modification, suppression, affichage)
- ✅ CRUD complet sur les emprunts liés aux livres
- ✅ Recherche par titre / auteur / ISBN
- ✅ Filtrage par catégorie et par statut d'emprunt
- ✅ Statistiques visuelles (ProgressBar, ListView des catégories)
- ✅ Export CSV des livres et des emprunts
- ✅ Interface organisée en onglets (TabPane)

## 🎛️ Contrôles JavaFX utilisés

TextField, TextArea, Button, Label, CheckBox, ComboBox, ListView, TableView, DatePicker, Slider, Spinner, ProgressBar, Tooltip, MenuBar, Alert, Accordion, TitledPane, FileChooser, SplitPane, TabPane


## 📦 Structure du projet

\`\`\`
src/
├── application/    → Classe principale (Main.java)
├── model/          → Classes Livre, Emprunt
├── dao/            → Database, LivreDAO, EmpruntDAO
├── controller/      → MainController, LivreController, EmpruntController
└── view/           → Fichiers FXML + feuille de style CSS
\`\`\`

## 📄 Licence

Projet académique réalisé dans le cadre du module Développement Java IHM - ENSAO.
5. **Configurer les VM Options :**
   - Run → Edit Configurations
   - Dans VM Options, ajouter :
