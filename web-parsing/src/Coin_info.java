import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Coin_info {
    String title;
    String url;
    int price;
    String user_message="";
    Set<String> img_url=new HashSet<>();
    //String numenal;
    //String coin;
    String year="";

    String[] types = {"jpg", "jpeg", "png"};
    Pattern year_pattern = Pattern.compile("[0-2]\\d\\d\\d-[0-2]\\d\\d\\d|[0-2]\\d\\d\\d"); // шаблон для поиска года выпуска монеты
  //  Pattern price_pattern = Pattern.compile("\\d*");

    public void print() {
        System.out.println("Title: "+title);
        //System.out.print(" "+ numenal);
        //System.out.println(" "+coin);
        System.out.println(url);
        System.out.println(year);
        for (String str : img_url)
            System.out.println(str);
    }
}
