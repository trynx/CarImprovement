package nicodo.com.myemail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Util {

    public static final String SHEET_ID = "1xEp2dYuKZP0WCoxwG3VY2d4QkuwGOajJ9djNHvYItXg";
    public static final String SHEET_ID_COSTS = "1hlZ2zjjc5cgyhkUhAKoqt9ydQALqQWkIuRnHlYF_r0Q";

    public static String getCurrentTime(){

        return  new SimpleDateFormat("dd/MM/yyyy HH:mm",
                Locale.getDefault()).format(new Date());
    }

    // Change from numeric month to hebrew, as some devices could have different languages
    public static String getHebrewMonth(String value){
        switch (value){
            case "01":
                return "ינואר";
            case "02":
                return "פברואר";
            case "03":
                return "מרץ";
            case "04":
                return "אפריל";
            case "05":
                return "מאי";
            case "06":
                return "יוני";
            case "07":
                return "יולי";
            case "08":
                return "אוגוסט";
            case "09":
                return "ספטמבר";
            case "10":
                return "אוקטובר";
            case "11":
                return "נובמבר";
            case "12":
                return "דצמבר";
        }
        return "";
    }

}
