package viewmodel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldRegex {

    private Pattern firstnameRegex;
    private Pattern lastNameRegex;
    private Pattern departmentRegex;
    private Pattern emailRegex;

    private Matcher fNameMatcher;
    private Matcher lNameMatcher;
    private Matcher departmentMatcher;
    private Matcher emailMatcher;

    public boolean FieldRegex(String fName, String lName, String department, String email) {
        firstnameRegex = Pattern.compile("[a-zA-Z]+");
        lastNameRegex = Pattern.compile("[a-zA-Z]+");
        departmentRegex = Pattern.compile("[a-zA-Z0-9\\s]+");

        emailRegex = Pattern.compile("^[A-Za-z0-9._%+-]+@farmingdale\\.edu$");

        fNameMatcher = firstnameRegex.matcher(fName);
        lNameMatcher = lastNameRegex.matcher(lName);
        departmentMatcher = departmentRegex.matcher(department);

        emailMatcher = emailRegex.matcher(email);

        if(fNameMatcher.matches() && lNameMatcher.matches() && departmentMatcher.matches()&& emailMatcher.matches()) {
            return true;
        }
        return false;
    }

    public boolean firstnameMatch(String fName) {
        fNameMatcher = firstnameRegex.matcher(fName);
        return fNameMatcher.matches();
    }

    public boolean lastNameMatch(String lName) {
        lNameMatcher = lastNameRegex.matcher(lName);
        return lNameMatcher.matches();
    }
    public boolean departmentMatch(String department) {
        departmentMatcher = departmentRegex.matcher(department);
        return departmentMatcher.matches();
    }

    public boolean emailMatch(String email) {
        emailMatcher = emailRegex.matcher(email);
        return emailMatcher.matches();
    }

}
