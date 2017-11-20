import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Coin_info {
    int id = 0;
    String title;
    String url;
    int price;
    String user_message = "";
    Set<String> img_url = new HashSet<String>();
    String year = "";
    String[] types = {"jpg", "jpeg", "png"};
    Pattern year_pattern = Pattern.compile("[0-2]\\d\\d\\d-[0-2]\\d\\d\\d|[0-2]\\d\\d\\d"); // шаблон для поиска года выпуска монеты

    public void print() {
        if (id != 0) {
            System.out.println("id = " + id);
            System.out.println("title : " + title);
            System.out.println("url = " + url);
            System.out.println("year = " + year);
            System.out.println("User_Message : " + user_message);
            for (String str : img_url)
                System.out.println("img_url = " + str);
        }
    }
}
