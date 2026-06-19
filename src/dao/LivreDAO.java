package dao;

import model.Livre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {

    public List<Livre> getAll() throws SQLException {
        List<Livre> list = new ArrayList<>();
        String sql = "SELECT * FROM livre";
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Livre> search(String keyword, String categorie) throws SQLException {
        List<Livre> list = new ArrayList<>();
        String sql = "SELECT * FROM livre WHERE (titre LIKE ? OR auteur LIKE ? OR isbn LIKE ?)"
                + (categorie.equals("Toutes") ? "" : " AND categorie = ?");
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            if (!categorie.equals("Toutes")) ps.setString(4, categorie);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void insert(Livre l) throws SQLException {
        String sql = "INSERT INTO livre (titre,auteur,categorie,isbn,annee,disponible,description,nb_exemplaires) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, l.getTitre()); ps.setString(2, l.getAuteur());
            ps.setString(3, l.getCategorie()); ps.setString(4, l.getIsbn());
            ps.setInt(5, l.getAnnee()); ps.setBoolean(6, l.isDisponible());
            ps.setString(7, l.getDescription()); ps.setInt(8, l.getNbExemplaires());
            ps.executeUpdate();
        }
    }

    public void update(Livre l) throws SQLException {
        String sql = "UPDATE livre SET titre=?,auteur=?,categorie=?,isbn=?,annee=?,disponible=?,description=?,nb_exemplaires=? WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, l.getTitre()); ps.setString(2, l.getAuteur());
            ps.setString(3, l.getCategorie()); ps.setString(4, l.getIsbn());
            ps.setInt(5, l.getAnnee()); ps.setBoolean(6, l.isDisponible());
            ps.setString(7, l.getDescription()); ps.setInt(8, l.getNbExemplaires());
            ps.setInt(9, l.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM livre WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int countTotal() throws SQLException {
        ResultSet rs = Database.getConnection().createStatement()
                .executeQuery("SELECT COUNT(*) FROM livre");
        return rs.next() ? rs.getInt(1) : 0;
    }

    public int countDisponibles() throws SQLException {
        ResultSet rs = Database.getConnection().createStatement()
                .executeQuery("SELECT COUNT(*) FROM livre WHERE disponible=true");
        return rs.next() ? rs.getInt(1) : 0;
    }

    private Livre map(ResultSet rs) throws SQLException {
        Livre l = new Livre();
        l.setId(rs.getInt("id"));
        l.setTitre(rs.getString("titre"));
        l.setAuteur(rs.getString("auteur"));
        l.setCategorie(rs.getString("categorie"));
        l.setIsbn(rs.getString("isbn"));
        l.setAnnee(rs.getInt("annee"));
        l.setDisponible(rs.getBoolean("disponible"));
        l.setDescription(rs.getString("description"));
        l.setNbExemplaires(rs.getInt("nb_exemplaires"));
        return l;
    }
}