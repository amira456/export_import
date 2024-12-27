package Model;

import DAO.HolidayDAOImpl;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HolidayModel {
    private final HolidayDAOImpl dao;
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public HolidayModel(HolidayDAOImpl dao) {
        this.dao = dao;
    }

    // Ajouter un nouveau congé
    public boolean add(String employe, Type type, String startDate, String endDate) {
        if (!isValidDateRange(startDate, endDate)) {
            return false; // Invalid date range, returning false
        }

        // Convertir les noms d'employé en IDs correspondants
        int employeId = dao.getEmployeIdByName(employe);

        if (employeId == -1) {
            return false; // Si aucun ID correspondant trouvé, retour false
        }

        if (isStartBeforeToday(startDate)) {
            System.err.println("La date de début doit venir après aujourd'hui.");
            return false;
        }

        // Vérification des chevauchements
        if (hasHolidayOverlap(employeId, startDate, endDate)) {
            System.err.println("L'employé a déjà un congé pendant cette période.");
            return false;
        }

        // Vérifier la balance de congé
        int holidayDays = calculateHolidayDuration(startDate, endDate);
        if (!hasSufficientHolidayBalance(employeId, holidayDays)) {
            System.err.println("Le nombre de jours de congé est insuffisant.");
            return false;
        }

        Holiday holiday = new Holiday(employeId, type, startDate, endDate);
        try {
            dao.add(holiday); // Appeler la méthode DAO pour ajouter un congé
            return true;
        } catch (Exception e) {
            logError("Erreur lors de l'ajout du congé", e);
            return false;
        }
    }

    // Vérifier si la date de début est avant aujourd'hui
    private boolean isStartBeforeToday(String startDate) {
        LocalDate start = LocalDate.parse(startDate);
        return start.isBefore(LocalDate.now());
    }

    // Vérifier les chevauchements de congés
    private boolean hasHolidayOverlap(int employeId, String startDate, String endDate) {
        List<Holiday> employeeHolidays = dao.getHolidaysByEmployeeId(employeId);
        LocalDate newStart = LocalDate.parse(startDate);
        LocalDate newEnd = LocalDate.parse(endDate);

        for (Holiday holiday : employeeHolidays) {
            LocalDate currentStart = LocalDate.parse(holiday.getStartDate());
            LocalDate currentEnd = LocalDate.parse(holiday.getEndDate());

            if ((newStart.isBefore(currentEnd) && newEnd.isAfter(currentStart))) {
                return true; // Il y a un chevauchement
            }
        }
        return false; // Pas de chevauchement
    }

    // Vérifier si l'employé a un solde de congé suffisant
    private boolean hasSufficientHolidayBalance(int employeId, int holidayDays) {
        // Récupérer la balance de congé de l'employé à partir de la table Employee
        int currentBalance = dao.getHolidayBalance(employeId);

        // Vérifier si la balance de congé est suffisante
        if (currentBalance >= holidayDays) {
            return true;
        } else {
            System.err.println("Solde insuffisant. L'employé n'a que " + currentBalance + " jours de congé.");
            return false;
        }
    }

    // Calculer le nombre de jours entre deux dates
    private int calculateHolidayDuration(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            long start = sdf.parse(startDate).getTime();
            long end = sdf.parse(endDate).getTime();
            long diffInMillis = end - start;
            return (int) (diffInMillis / (1000 * 60 * 60 * 24)) + 1; // Inclure le premier et le dernier jour
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
    // Mettre à jour un congé existant
    public boolean update(int holidayId, String employe, Type type, String startDate, String endDate) {
        if (!isValidDateRange(startDate, endDate)) {
            return false; // Invalid date range, returning false
        }

        int employeId = dao.getEmployeIdByName(employe);
        if (employeId == -1) {
            return false; // Si aucun ID correspondant trouvé, retour false
        }

        if (isStartBeforeToday(startDate)) {
            System.err.println("La date de début doit venir après aujourd'hui.");
            return false;
        }

        if (hasHolidayOverlap(employeId, startDate, endDate)) {
            System.err.println("L'employé a déjà un congé pendant cette période.");
            return false;
        }

        int holidayDays = calculateHolidayDuration(startDate, endDate);
        if (!hasSufficientHolidayBalance(employeId, holidayDays)) {
            System.err.println("Le nombre de jours de congé est insuffisant.");
            return false;
        }

        Holiday holiday = new Holiday(holidayId, employeId, type, startDate, endDate);
        try {
            dao.update(holiday); // Appeler la méthode DAO pour mettre à jour le congé
            return true;
        } catch (Exception e) {
            logError("Erreur lors de la mise à jour du congé", e);
            return false;
        }
    }

    // Supprimer un congé
    public boolean delete(int holidayId) {
        try {
            dao.delete(holidayId); // Appeler la méthode DAO pour supprimer le congé
            return true;
        } catch (Exception e) {
            logError("Erreur lors de la suppression du congé", e);
            return false;
        }
    }

    // Récupérer tous les congés sous forme de tableau pour l'affichage
    public Object[][] getAllHolidaysAsTableData() {
        List<Holiday> holidayList = dao.getAll();
        Object[][] tab = new Object[holidayList.size()][5]; // 5 colonnes : id, employé, type, début, fin

        for (int i = 0; i < holidayList.size(); i++) {
            Holiday holiday = holidayList.get(i);
            tab[i][0] = holiday.getId();
            String employeName = dao.getNameEmployeById(holiday.getEmployeId());
            tab[i][1] = employeName;
            tab[i][2] = holiday.getType();
            tab[i][3] = holiday.getFormattedStartDate(DATE_FORMAT);
            tab[i][4] = holiday.getFormattedEndDate(DATE_FORMAT);
        }

        return tab;
    }

    // Méthode pour journaliser les erreurs
    private void logError(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }
    private boolean isValidDateRange(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Le format des dates utilisé
        try {
            // Convertir les chaînes de dates en objets Date
            java.util.Date start = sdf.parse(startDate);
            java.util.Date end = sdf.parse(endDate);

            // Vérifier si la date de fin est après la date de début
            return !start.after(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false; // Retourner false si les dates sont invalides ou la conversion échoue
    }

    // HolidayModel.java
    public List<String> getEmployes() {
        try {
            return dao.getAllEmployeNames();
        } catch (Exception e) {
            logError("Erreur lors de la récupération des employés", e);
            return Collections.emptyList(); // Retourne une liste vide en cas d'erreur
        }
    }

}