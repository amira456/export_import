package Model;

// Classe Holiday
public class Holiday {
    private int id;
    private int employeId;  // Clé étrangère vers l'Employé
    private Type type;
    private String startDate;
    private String endDate;

    // Constructeur avec tous les champs
    public Holiday(int id, int employeId, Type type, String startDate, String endDate) {
        this.id = id;
        this.employeId = employeId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Constructeur sans "id" (par exemple pour l'ajout d'un nouveau congé)
    public Holiday(int employeId, Type type, String startDate, String endDate) {
        this.employeId = employeId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getter et setter pour "id"
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter et setter pour "employeId"
    public int getEmployeId() {
        return employeId;
    }

    public void setEmployeId(int employeId) {
        this.employeId = employeId;
    }

    // Getter et setter pour "type"
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    // Getter et setter pour "startDate"
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    // Getter et setter pour "endDate"
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public String getFormattedStartDate(String dateFormat) {
        return startDate;
    }

    public String getFormattedEndDate(String dateFormat) {
        return endDate != null ? endDate : "Non retourné";
    }


}
