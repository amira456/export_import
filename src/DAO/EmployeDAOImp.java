package DAO;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.Employe;
import Model.Poste;
import Model.Role;

public class EmployeDAOImp implements GeniriqueDAOI<Employe>,DataImportExport<Employe> {
    private static connexion conn;

    public EmployeDAOImp() {
        conn = new connexion();
    }

    @Override
    public void add(Employe emp) {
        String sql = "INSERT INTO Employee (nom, prenom, email, phone, salaire, role, poste, used_balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connexion.getConnexion().prepareStatement(sql)) {
            stmt.setString(1, emp.getNom());
            stmt.setString(2, emp.getPrenom());
            stmt.setString(3, emp.getEmail());
            stmt.setString(4, emp.getTelephone());
            stmt.setDouble(5, emp.getSalaire());
            stmt.setString(6, emp.getRole()); // Convertir en String si Role est une énumération
            stmt.setString(7, emp.getPoste()); // Convertir en String si Poste est une énumération
            stmt.setInt(8, emp.getBalance()); // Ajout de la balance
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Employe emp) {
        String sql = "UPDATE Employee SET nom = ?, prenom = ?, email = ?, phone = ?, salaire = ?, role = ?, poste = ?, used_balance = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            stmt.setString(1, emp.getNom());
            stmt.setString(2, emp.getPrenom());
            stmt.setString(3, emp.getEmail());
            stmt.setString(4, emp.getTelephone());
            stmt.setDouble(5, emp.getSalaire());
            stmt.setString(6, emp.getRole());
            stmt.setString(7, emp.getPoste());
            stmt.setInt(8, emp.getBalance()); // Ajout de la balance
            stmt.setInt(9, emp.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Employee WHERE id = ?";
        try (PreparedStatement stmt = conn.getConnexion().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Employe> getAll() {
        List<Employe> emp = new ArrayList<>();
        String sql = "SELECT * FROM Employee";
        try (PreparedStatement stmt = connexion.getConnexion().prepareStatement(sql);
             ResultSet rslt = stmt.executeQuery()) {
            while (rslt.next()) {
                // Récupération et conversion en majuscules pour le rôle
                String roleString = rslt.getString("role");
                Role role = null;
                try {
                    role = Role.valueOf(roleString.toUpperCase());  // Convertir en majuscules
                } catch (IllegalArgumentException e) {
                    System.err.println(" ");
                    role = Role.EMPLOYEE;  // Rôle par défaut en cas d'erreur
                }

                // Récupération et conversion en majuscules pour le poste
                String posteString = rslt.getString("poste");
                Poste poste = null;
                try {
                    poste = Poste.valueOf(posteString.toUpperCase());  // Convertir en majuscules
                } catch (IllegalArgumentException e) {
                    System.err.println("");
                    poste = Poste.DEFAULT;  // Poste par défaut en cas d'erreur
                }

                // Ajout de l'employé à la liste
                emp.add(new Employe(
                        rslt.getInt("id"),
                        rslt.getString("nom"),
                        rslt.getString("prenom"),
                        rslt.getString("email"),
                        rslt.getString("phone"),
                        rslt.getDouble("salaire"),
                        role,
                        poste,
                        rslt.getInt("used_balance") // Récupération de la balance
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emp;
    }
    @Override
    public void importData(String filePath) {
        String sql = "INSERT INTO Employee (nom, prenom, email, phone, salaire, role, poste, used_balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath)); PreparedStatement stmt = connexion.getConnexion().prepareStatement(sql)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data =line.split(",");
                if (data.length ==8){
                    stmt.setString(1,data[0].trim());
                    stmt.setString(2,data[1].trim());
                    stmt.setString(3,data[2].trim());
                    stmt.setString(4,data[3].trim());
                    stmt.setDouble(5,Double.parseDouble(data[4].trim()) );
                    stmt.setString(6,data[5].trim());
                    stmt.setString(7,data[6].trim());
                    stmt.setInt(8,Integer.parseInt(data[7].trim()));
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            System.out.println("Employees imported successfully !");

        }catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void exportData (String fileName,List<Employe> data) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("nom,prenom,email,phone,salaire,role,poste,solde");
            writer.newLine();
            for (Employe employe : data) {
                String line = String.format("%s,%s,%s,%s,%.2f,%s,%s,%d",
                        employe.getNom(),
                        employe.getPrenom(),
                        employe.getEmail(),
                        employe.getTelephone(),
                        employe.getSalaire(),
                        employe.getRole(),
                        employe.getPoste(),
                        employe.getBalance()

                );

                writer.write(line);
                writer.newLine();
            }
        }
    }


}
