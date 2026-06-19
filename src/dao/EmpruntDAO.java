package dao;

import model.Emprunt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAO {

    public List<Emprunt> getAll() throws SQLException {
        List<Emprunt> list = new ArrayList<>();
        String sql = "SELECT e.*, l.titre FROM emprunt e JOIN livre l ON e.livre_id = l.id";
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void insert(Emprunt e) throws SQLException {
        String sql = "INSERT INTO emprunt (livre_id,emprunteur,date_emprunt,date_retour_prevue,statut,remarques) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, e.getLivreId());
            ps.setString(2, e.getEmprunteur());
            ps.setDate(3, Date.valueOf(e.getDateEmprunt()));
            ps.setDate(4, Date.valueOf(e.getDateRetourPrevue()));
            ps.setString(5, e.getStatut());
            ps.setString(6, e.getRemarques());
            ps.executeUpdate();
        }
    }

    public void update(Emprunt e) throws SQLException {
        String sql = "UPDATE emprunt SET livre_id=?,emprunteur=?,date_emprunt=?,date_retour_prevue=?,date_retour_reelle=?,statut=?,remarques=? WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, e.getLivreId());
            ps.setString(2, e.getEmprunteur());
            ps.setDate(3, Date.valueOf(e.getDateEmprunt()));
            ps.setDate(4, Date.valueOf(e.getDateRetourPrevue()));
            ps.setDate(5, e.getDateRetourReelle() != null ? Date.valueOf(e.getDateRetourReelle()) : null);
            ps.setString(6, e.getStatut());
            ps.setString(7, e.getRemarques());
            ps.setInt(8, e.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM emprunt WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int countEnCours() throws SQLException {
        ResultSet rs = Database.getConnection().createStatement()
                .executeQuery("SELECT COUNT(*) FROM emprunt WHERE statut='En cours'");
        return rs.next() ? rs.getInt(1) : 0;
    }

    public int countEnRetard() throws SQLException {
        ResultSet rs = Database.getConnection().createStatement()
                .executeQuery("SELECT COUNT(*) FROM emprunt WHERE statut='En retard' OR (statut='En cours' AND date_retour_prevue < CURDATE())");
        return rs.next() ? rs.getInt(1) : 0;
    }

    private Emprunt map(ResultSet rs) throws SQLException {
        Emprunt e = new Emprunt();
        e.setId(rs.getInt("id"));
        e.setLivreId(rs.getInt("livre_id"));
        e.setTitreLivre(rs.getString("titre"));
        e.setEmprunteur(rs.getString("emprunteur"));
        e.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
        e.setDateRetourPrevue(rs.getDate("date_retour_prevue").toLocalDate());
        Date dr = rs.getDate("date_retour_reelle");
        if (dr != null) e.setDateRetourReelle(dr.toLocalDate());
        e.setStatut(rs.getString("statut"));
        e.setRemarques(rs.getString("remarques"));
        return e;
    }
}