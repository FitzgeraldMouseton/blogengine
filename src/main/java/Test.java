import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws InterruptedException {

        Date date1 = new Date();
        Thread.sleep(3000);
        Date date2 = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        System.out.println(dateFormat.format(date1));
        System.out.println(date1.before(date2));
        System.out.println(date1.toString());
    }
}
