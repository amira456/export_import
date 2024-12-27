package Model;

// Classe Employe
public class Employe {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private double salaire;
    private Role role;
    private Poste poste;
    private int balance = 25;

    // Constructeur avec tous les champs
    public Employe(int id, String nom, String prenom, String email, String telephone, double salaire, Role role, Poste poste, int balance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.salaire = salaire;
        this.role = role;
        this.poste = poste;
        this.balance = balance;
    }

    // Constructeur sans "id" (par exemple pour l'ajout d'un nouvel employé avant l'attribution d'un ID)
    public Employe(String nom, String prenom, String email, String telephone, double salaire, Role role, Poste poste, int balance) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.salaire = salaire;
        this.role = role;
        this.poste = poste;
        this.balance = balance;
    }

    public Employe(String employeeName) {
    }

    // Getter pour l'ID
    public int getId() {
        return id;
    }

    // Getter et setter pour "nom"
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    // Getter et setter pour "prenom"
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    // Getter et setter pour "email"
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter et setter pour "telephone"
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    // Getter et setter pour "salaire"
    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    // Getter et setter pour "role"
    public String getRole() {
        return  role.name() ; // Vérifie si "role" n'est pas null avant d'appeler name().
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Getter et setter pour "poste"
    public String getPoste() {
        return poste != null ? poste.name() : null; // Vérifie si "poste" n'est pas null avant d'appeler name().
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    // Getter et setter pour "balance"
    public int getBalance() {
        return balance;
    }


    public void setBalance(int newBalance) {
        if (this.balance > 0) {
            this.balance= balance - newBalance;
        }
    }
}
