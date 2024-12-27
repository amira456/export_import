package DAO;

import Model.Holiday;
import Model.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HolidayDAOImpl {

    private static connexion conn;

    public HolidayDAOImpl() {
        conn = new connexion();
    }

    // Ajouter un congé
    public void add(Holiday holiday) {
        String sql = "INSERT INTO holiday (employeId, type, startdate, enddate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            // Ajouter le congé dans la base de données
            stmt.setInt(1, holiday.getEmployeId());
            stmt.setString(2, holiday.getType().name());
            stmt.setString(3, holiday.getStartDate());
            stmt.setString(4, holiday.getEndDate());
            stmt.executeUpdate();

            // Calculer la durée du congé
            int duration = calculateHolidayDuration(holiday.getStartDate(), holiday.getEndDate());

            // Mettre à jour la balance
            updateHolidayBalanceAfterAddition(holiday.getEmployeId(), duration);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mettre à jour un congé
    public void update(Holiday holiday) {
        String sql = "UPDATE holiday SET employeId = ?, type = ?, startdate = ?, enddate = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            stmt.setInt(1, holiday.getEmployeId());
            stmt.setString(2, holiday.getType().name());
            stmt.setString(3, holiday.getStartDate());
            stmt.setString(4, holiday.getEndDate());
            stmt.setInt(5, holiday.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer un congé
    public void delete(int id) {
        String sql = "DELETE FROM holiday WHERE id = ?";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer tous les congés pour un employé
    public List<Holiday> getByEmployeeId(int employeId) {
        List<Holiday> holidays = new ArrayList<>();
        String sql = "SELECT * FROM holiday WHERE employeId = ?";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            stmt.setInt(1, employeId);
            try (ResultSet rslt = stmt.executeQuery()) {
                while (rslt.next()) {
                    holidays.add(new Holiday(
                            rslt.getInt("id"),
                            rslt.getInt("employeId"),
                            Type.valueOf(rslt.getString("type")),
                            rslt.getString("startdate"),
                            rslt.getString("enddate")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidays;
    }

    // Récupérer un congé par ID
    public Holiday getById(int id) {
        Holiday holiday = null;
        String sql = "SELECT * FROM holiday WHERE id = ?";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rslt = stmt.executeQuery()) {
                if (rslt.next()) {
                    holiday = new Holiday(
                            rslt.getInt("id"),
                            rslt.getInt("employeId"),
                            Type.valueOf(rslt.getString("type")),
                            rslt.getString("startdate"),
                            rslt.getString("enddate")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holiday;
    }

    // Récupérer tous les congés
    public List<Holiday> getAll() {
        List<Holiday> holidays = new ArrayList<>();
        String sql = "SELECT * FROM holiday";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql);
             ResultSet rslt = stmt.executeQuery()) {
            while (rslt.next()) {
                holidays.add(new Holiday(
                        rslt.getInt("id"),
                        rslt.getInt("employeId"),
                        Type.valueOf(rslt.getString("type")),
                        rslt.getString("startdate"),
                        rslt.getString("enddate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidays;
    }

    // Récupérer le nom de l'employé par son ID
    public String getNameEmployeById(int employeId) {
        String sql = "SELECT nom FROM Employee WHERE id = ?";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            stmt.setInt(1, employeId);
            try (ResultSet rslt = stmt.executeQuery()) {
                if (rslt.next()) {
                    return rslt.getString("nom");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Récupérer l'ID de l'employé par son nom
    public int getEmployeIdByName(String nom) {
        String sql = "SELECT id FROM Employee WHERE nom = ?";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            stmt.setString(1, nom);
            try (ResultSet rslt = stmt.executeQuery()) {
                if (rslt.next()) {
                    return rslt.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<String> getEmployes() {
        return fetchSingleColumn("SELECT nom FROM Employee", "nom");
    }

    public List<String> getAllEmployeNames() {
        return fetchSingleColumn("SELECT nom FROM Employee", "nom");
    }

    private List<String> fetchSingleColumn(String sql, String columnLabel) {
        List<String> results = new ArrayList<>();
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(rs.getString(columnLabel));
            }
        } catch (SQLException e) {
            logError(e, "Error fetching data for column: " + columnLabel);
        }
        return results;
    }

    private void logError(Exception e, String message) {
        System.err.println(message);
        e.printStackTrace();
    }

    // Récupérer la balance de congé pour un employé
    public int getHolidayBalance(int employeId) {
        int balance = 25;
        String sql = "SELECT used_balance FROM Employee WHERE id = ?";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            stmt.setInt(1, employeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    balance = rs.getInt("used_balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    // Mettre à jour la balance de congé après l'ajout d'un congé
    private void updateHolidayBalanceAfterAddition(int employeId, int duration) {
        int currentBalance = getHolidayBalance(employeId);
        int newBalance = currentBalance - duration;
        if (newBalance < 0) {
            throw new IllegalArgumentException("Insufficient holiday balance.");
        }
        updateHolidayBalance(employeId, newBalance);
    }

    // Mettre à jour la balance de congé dans la base de données
    public void updateHolidayBalance(int employeId, int newBalance) {
        String sql = "UPDATE Employee SET used_balance = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            stmt.setInt(1, newBalance);
            stmt.setInt(2, employeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Calculer la durée d'un congé en jours
    private int calculateHolidayDuration(String startDate, String endDate) {
        try {
            java.util.Date start = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            java.util.Date end = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(endDate);
            long diff = end.getTime() - start.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24)) + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public List<Holiday> getHolidaysByEmployeeId(int employeId) {
        List<Holiday> holidays = new ArrayList<>();
        String sql = "SELECT * FROM holiday WHERE employeId = ?";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            stmt.setInt(1, employeId); // Ajouter l'identifiant de l'employé dans la requête
            try (ResultSet rslt = stmt.executeQuery()) {
                while (rslt.next()) {
                    holidays.add(new Holiday(
                            rslt.getInt("id"),
                            rslt.getInt("employeId"),
                            Type.valueOf(rslt.getString("type")), // Convertir en Type (enum)
                            rslt.getString("startdate"),
                            rslt.getString("enddate")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidays;
    }
}
