
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;


public class myTest {

    public static void main(String[] args) {
        ArrayList<LatLng> a = new ArrayList<>();
        a.add(new LatLng(1,1));
//        Route route = new Route("123", a, "afternoon");
//        System.out.println(route.getPoints());
    }

    public static String getTimeOfDay(int timeOfDay, String Day) {
        if (timeOfDay >= 0 && timeOfDay < 12) {
            return Day + " morning run";
        } else if (timeOfDay >= 12 && timeOfDay <= 16) {
            return Day + " afternoon run";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            return Day + " evening run";
        } else {
            return Day + " night run";
        }
    }

    public static String getDay(int day) {
        switch (day) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return null;
        }
    }
}