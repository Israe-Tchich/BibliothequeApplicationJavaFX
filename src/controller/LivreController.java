package controller;

import dao.LivreDAO;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import model.Livre;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class LivreController {

    // --- Formulaire ---
    @FXML private TextField tfTitre, tfAuteur, tfIsbn;
    @FXML private TextArea taDescription;
    @FXML private ComboBox<String> cbCategorie;
    @FXML private CheckBox chkDisponible;
    @FXML private Spinner<Integer> spAnnee, spExemplaires;
    @FXML private Slider sliderExemplaires;
    @FXML private ProgressBar pbStock;
    @FXML private RadioButton rbDisponible, rbIndisponible;
    @FXML private ToggleGroup tgDisponible;

    // --- Recherche ---
    @FXML private TextField tfRecherche;
    @FXML private ComboBox<String> cbFiltreCategorie;

    // --- TableView ---
    @FXML private TableView<Livre> tableView;
    @FXML private TableColumn<Livre, Integer> colId;
    @FXML private TableColumn<Livre, String> colTitre, colAuteur, colCategorie, colIsbn;
    @FXML private TableColumn<Livre, Integer> colAnnee, colExemplaires;
    @FXML private TableColumn<Livre, Boolean> colDisponible;

    // --- ListView ---
    @FXML private ListView<String> listCategories;

    private LivreDAO livreDAO = new LivreDAO();
    private ObservableList<Livre> livres = FXCollections.observableArrayList();
    private Livre livreSelectionne = null;

    @FXML
    private void handleActualiser() {
        chargerLivres();
    }

    @FXML
    public void initialize() {
        // Colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));
        colExemplaires.setCellValueFactory(new PropertyValueFactory<>("nbExemplaires"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        // ComboBox catégories
        ObservableList<String> cats = FXCollections.observableArrayList(
                "Roman","Science-Fiction","Informatique","Histoire","Science","Autre");
        cbCategorie.setItems(cats);
        cbFiltreCategorie.setItems(FXCollections.observableArrayList("Toutes"));
        cbFiltreCategorie.getItems().addAll(cats);
        cbFiltreCategorie.setValue("Toutes");

        // Spinner année
        spAnnee.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1800, 2026, 2024));
        spExemplaires.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        // Slider → Spinner sync
        sliderExemplaires.setMin(1); sliderExemplaires.setMax(50);
        sliderExemplaires.valueProperty().addListener((obs, o, n) ->
                spExemplaires.getValueFactory().setValue(n.intValue()));
        spExemplaires.valueProperty().addListener((obs, o, n) ->
                sliderExemplaires.setValue(n));

        // Sélection dans la table
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, livre) -> {
            if (livre != null) remplirFormulaire(livre);
        });

        // Tooltips
        tfTitre.setTooltip(new Tooltip("Entrez le titre du livre"));
        tfIsbn.setTooltip(new Tooltip("Format : 978-X-XX-XXXXXX-X"));

        chargerLivres();
    }

    private void chargerLivres() {
        try {
            livres.setAll(livreDAO.getAll());
            tableView.setItems(livres);
            majListView();
            majProgressBar();
        } catch (SQLException e) {
            showError("Erreur chargement : " + e.getMessage());
        }
    }

    @FXML
    private void handleRecherche() {
        try {
            String kw = tfRecherche.getText().trim();
            String cat = cbFiltreCategorie.getValue();
            livres.setAll(livreDAO.search(kw, cat == null ? "Toutes" : cat));
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleAjouter() {
        if (!validerFormulaire()) return;
        try {
            Livre l = construireLivre();
            livreDAO.insert(l);
            chargerLivres();
            viderFormulaire();
            showInfo("Livre ajouté avec succès !");
        } catch (SQLException e) {
            showError("Erreur ajout : " + e.getMessage());
        }
    }

    @FXML
    private void handleModifier() {
        if (livreSelectionne == null) { showError("Sélectionnez un livre."); return; }
        if (!validerFormulaire()) return;
        try {
            Livre l = construireLivre();
            l.setId(livreSelectionne.getId());
            livreDAO.update(l);
            chargerLivres();
            viderFormulaire();
            showInfo("Livre modifié !");
        } catch (SQLException e) {
            showError("Erreur modification : " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimer() {
        if (livreSelectionne == null) { showError("Sélectionnez un livre."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer « " + livreSelectionne.getTitre() + " » ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    livreDAO.delete(livreSelectionne.getId());
                    chargerLivres(); viderFormulaire();
                } catch (SQLException e) { showError(e.getMessage()); }
            }
        });
    }

    @FXML
    private void handleExportCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Exporter CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File f = fc.showSaveDialog(null);
        if (f == null) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            pw.println("ID,Titre,Auteur,Categorie,ISBN,Annee,Disponible,Exemplaires");
            for (Livre l : livres)
                pw.printf("%d,%s,%s,%s,%s,%d,%b,%d%n",
                        l.getId(), l.getTitre(), l.getAuteur(), l.getCategorie(),
                        l.getIsbn(), l.getAnnee(), l.isDisponible(), l.getNbExemplaires());
            showInfo("Export CSV réussi !");
        } catch (IOException e) { showError(e.getMessage()); }
    }

    @FXML
    private void handleVider() { viderFormulaire(); }

    private Livre construireLivre() {
        Livre l = new Livre();
        l.setTitre(tfTitre.getText().trim());
        l.setAuteur(tfAuteur.getText().trim());
        l.setCategorie(cbCategorie.getValue());
        l.setIsbn(tfIsbn.getText().trim());
        l.setAnnee(spAnnee.getValue());
        l.setDisponible(chkDisponible.isSelected());
        l.setDescription(taDescription.getText().trim());
        l.setNbExemplaires(spExemplaires.getValue());
        return l;
    }

    private void remplirFormulaire(Livre l) {
        livreSelectionne = l;
        tfTitre.setText(l.getTitre());
        tfAuteur.setText(l.getAuteur());
        tfIsbn.setText(l.getIsbn());
        cbCategorie.setValue(l.getCategorie());
        spAnnee.getValueFactory().setValue(l.getAnnee());
        spExemplaires.getValueFactory().setValue(l.getNbExemplaires());
        sliderExemplaires.setValue(l.getNbExemplaires());
        chkDisponible.setSelected(l.isDisponible());
        taDescription.setText(l.getDescription());
    }

    private void viderFormulaire() {
        livreSelectionne = null;
        tfTitre.clear(); tfAuteur.clear(); tfIsbn.clear();
        taDescription.clear(); cbCategorie.setValue(null);
        spAnnee.getValueFactory().setValue(2024);
        spExemplaires.getValueFactory().setValue(1);
        chkDisponible.setSelected(true);
        tableView.getSelectionModel().clearSelection();
    }

    private boolean validerFormulaire() {
        if (tfTitre.getText().trim().isEmpty()) { showError("Le titre est obligatoire."); return false; }
        if (tfAuteur.getText().trim().isEmpty()) { showError("L'auteur est obligatoire."); return false; }
        if (cbCategorie.getValue() == null) { showError("Choisissez une catégorie."); return false; }
        return true;
    }

    private void majListView() {
        ObservableList<String> items = FXCollections.observableArrayList();
        livres.stream().map(Livre::getCategorie).distinct().forEach(c -> items.add(c + " (" +
                livres.stream().filter(l -> c.equals(l.getCategorie())).count() + ")"));
        listCategories.setItems(items);
    }

    private void majProgressBar() {
        try {
            int total = livreDAO.countTotal();
            int dispo = livreDAO.countDisponibles();
            pbStock.setProgress(total > 0 ? (double) dispo / total : 0);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).show();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
}