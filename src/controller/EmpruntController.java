package controller;

import dao.EmpruntDAO;
import dao.LivreDAO;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import model.Emprunt;
import model.Livre;
import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class EmpruntController {

    @FXML private ComboBox<Livre> cbLivre;
    @FXML private TextField tfEmprunteur, tfRechercheEmprunt;
    @FXML private DatePicker dpDateEmprunt, dpDateRetourPrevue, dpDateRetourReelle;
    @FXML private ComboBox<String> cbStatut, cbFiltreStatut;
    @FXML private TextArea taRemarques;
    @FXML private TableView<Emprunt> tableEmprunts;
    @FXML private TableColumn<Emprunt, Integer> colEmpId;
    @FXML private TableColumn<Emprunt, String> colEmpLivre, colEmpEmprunteur, colEmpStatut;
    @FXML private TableColumn<Emprunt, LocalDate> colEmpDateEmprunt, colEmpDateRetour;
    @FXML private Label lblEnCours, lblEnRetard;

    private EmpruntDAO empruntDAO = new EmpruntDAO();
    private LivreDAO livreDAO = new LivreDAO();
    private ObservableList<Emprunt> emprunts = FXCollections.observableArrayList();
    private Emprunt empruntSelectionne = null;

    @FXML
    public void initialize() {
        colEmpId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEmpLivre.setCellValueFactory(new PropertyValueFactory<>("titreLivre"));
        colEmpEmprunteur.setCellValueFactory(new PropertyValueFactory<>("emprunteur"));
        colEmpDateEmprunt.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));
        colEmpDateRetour.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));
        colEmpStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        cbStatut.setItems(FXCollections.observableArrayList("En cours", "Rendu", "En retard"));
        cbFiltreStatut.setItems(FXCollections.observableArrayList("Tous", "En cours", "Rendu", "En retard"));
        cbFiltreStatut.setValue("Tous");

        dpDateEmprunt.setValue(LocalDate.now());
        dpDateRetourPrevue.setValue(LocalDate.now().plusDays(14));

        tableEmprunts.getSelectionModel().selectedItemProperty().addListener((obs, o, e) -> {
            if (e != null) remplirFormulaire(e);
        });

        chargerLivres();
        chargerEmprunts();
    }

    private void chargerLivres() {
        try {
            cbLivre.setItems(FXCollections.observableArrayList(livreDAO.getAll()));
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void chargerEmprunts() {
        try {
            emprunts.setAll(empruntDAO.getAll());
            tableEmprunts.setItems(emprunts);
            majStats();
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    @FXML
    private void handleRecherche() {
        String kw = tfRechercheEmprunt.getText().toLowerCase();
        String statut = cbFiltreStatut.getValue();
        try {
            ObservableList<Emprunt> tous = FXCollections.observableArrayList(empruntDAO.getAll());
            emprunts.setAll(tous.filtered(e ->
                    (kw.isEmpty() || e.getEmprunteur().toLowerCase().contains(kw)) &&
                            (statut.equals("Tous") || e.getStatut().equals(statut))
            ));
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    @FXML
    private void handleAjouter() {
        if (!valider()) return;
        try {
            empruntDAO.insert(construire());
            chargerEmprunts();
            vider();
            showInfo("Emprunt ajouté !");
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    @FXML
    private void handleModifier() {
        if (empruntSelectionne == null) { showError("Sélectionnez un emprunt."); return; }
        if (!valider()) return;
        try {
            Emprunt e = construire();
            e.setId(empruntSelectionne.getId());
            empruntDAO.update(e);
            chargerEmprunts();
            vider();
            showInfo("Emprunt modifié !");
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    @FXML
    private void handleSupprimer() {
        if (empruntSelectionne == null) { showError("Sélectionnez un emprunt."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cet emprunt ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    empruntDAO.delete(empruntSelectionne.getId());
                    chargerEmprunts(); vider();
                } catch (SQLException e) { showError(e.getMessage()); }
            }
        });
    }

    @FXML
    private void handleVider() { vider(); }

    @FXML
    private void handleExportCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Exporter CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File f = fc.showSaveDialog(null);
        if (f == null) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            pw.println("ID,Livre,Emprunteur,Date Emprunt,Date Retour Prévue,Statut");
            for (Emprunt e : emprunts)
                pw.printf("%d,%s,%s,%s,%s,%s%n",
                        e.getId(), e.getTitreLivre(), e.getEmprunteur(),
                        e.getDateEmprunt(), e.getDateRetourPrevue(), e.getStatut());
            showInfo("Export CSV réussi !");
        } catch (IOException e) { showError(e.getMessage()); }
    }

    private Emprunt construire() {
        Emprunt e = new Emprunt();
        e.setLivreId(cbLivre.getValue().getId());
        e.setEmprunteur(tfEmprunteur.getText().trim());
        e.setDateEmprunt(dpDateEmprunt.getValue());
        e.setDateRetourPrevue(dpDateRetourPrevue.getValue());
        e.setDateRetourReelle(dpDateRetourReelle.getValue());
        e.setStatut(cbStatut.getValue());
        e.setRemarques(taRemarques.getText());
        return e;
    }

    private void remplirFormulaire(Emprunt e) {
        empruntSelectionne = e;
        tfEmprunteur.setText(e.getEmprunteur());
        dpDateEmprunt.setValue(e.getDateEmprunt());
        dpDateRetourPrevue.setValue(e.getDateRetourPrevue());
        dpDateRetourReelle.setValue(e.getDateRetourReelle());
        cbStatut.setValue(e.getStatut());
        taRemarques.setText(e.getRemarques());
        for (Livre l : cbLivre.getItems())
            if (l.getId() == e.getLivreId()) { cbLivre.setValue(l); break; }
    }

    private void vider() {
        empruntSelectionne = null;
        tfEmprunteur.clear(); taRemarques.clear();
        cbLivre.setValue(null); cbStatut.setValue(null);
        dpDateEmprunt.setValue(LocalDate.now());
        dpDateRetourPrevue.setValue(LocalDate.now().plusDays(14));
        dpDateRetourReelle.setValue(null);
        tableEmprunts.getSelectionModel().clearSelection();
    }

    private boolean valider() {
        if (cbLivre.getValue() == null) { showError("Choisissez un livre."); return false; }
        if (tfEmprunteur.getText().trim().isEmpty()) { showError("Entrez l'emprunteur."); return false; }
        if (dpDateEmprunt.getValue() == null) { showError("Entrez la date d'emprunt."); return false; }
        if (dpDateRetourPrevue.getValue() == null) { showError("Entrez la date de retour prévue."); return false; }
        return true;
    }

    private void majStats() {
        try {
            lblEnCours.setText("En cours : " + empruntDAO.countEnCours());
            lblEnRetard.setText("En retard : " + empruntDAO.countEnRetard());
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void showError(String msg) { new Alert(Alert.AlertType.ERROR, msg).show(); }
    private void showInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).show(); }
}