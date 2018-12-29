package nicodo.com.myemail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Util {

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

    // Get the current year -> the sheet depend on it
    public static String getSheetId(String value){
        switch (value){
            case "2018":
                return "1xEp2dYuKZP0WCoxwG3VY2d4QkuwGOajJ9djNHvYItXg";
            case "2019":
                return "1GGh468FK4ptnlHFF5IAoKd74QJqEY7gURpIrDVjfQxQ";
            case "2020":
                return "17BFqaWXruvSuhy5QIw7t8OLlbO6Wvc1MgOgOzepPPrc";
            case "2021":
                return "17tmcpB4du2QceYGOTW4lUfHZ8arhweUmNe478-m4hVs";
            case "2022":
                return "1VpKhEdFwf2A3u9r4AtoYWNkY91fpM4fYa90kz9ezIHc";
            case "2023":
                return "1oDI359hehT7O3ets5qAjeSMoiuQO1jbT9gzchMvCBrc";
            case "2024":
                return "1yG23xZe2HJQAjZQfM8nSYdjY3OcUgrDTBNf6-EDH5hA";
            case "2025":
                return "1TcZsOzCRlMV9yLlr37h7vmDZn_TdztNLK2JIigwr_pE";
        }
        return "";
    }

    // Get the current year -> the sheet depend on it
    public static String getSheetIdCosts(String value){
        switch (value){
            case "2018":
                return "1hlZ2zjjc5cgyhkUhAKoqt9ydQALqQWkIuRnHlYF_r0Q";
            case "2019":
                return "1MZCp8QE--EagwQl-sYXBbau3gz2aQuHOo17eFkczhIs";
            case "2020":
                return "15LG9NMA_vam5gQ-LOlZbdovX6NAXwXiHmW1akSWmzIA";
            case "2021":
                return "1E1mpMrrPZukltB5vOPOeCNSY3AUPlq6cmQ9MIm9FkyQ";
            case "2022":
                return "1elVcoxR0oqjTg5TeiBzByaSfdHoIvlFbDcaOXryG3ig";
            case "2023":
                return "1hUvZ50FEIIcuNTj2KsAJ-qb-aH9pqeynwFyK5FaEH0o";
            case "2024":
                return "1szNUL4_f-YGYzsE4xV3tx6egSHYRXAvoQBha6i3Vxi8";
            case "2025":
                return "1tnVF8Mj_C_ktT8TZafJNUvlYMroQr5w77Pfs0CaxR4U";
        }
        return "";
    }
}
