import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;

public class Parser {
    private static Elements getElementsFromDocument(String url) {
        try {
            Document d = Jsoup.connect(url).timeout(10000).get();
            Elements ele = d.select("div#pagecontent").select("table.tablebg");
            return ele;
        } catch (Exception e) {
            System.out.println("Превышено время ожидания");
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        ArrayList<Coin_info> coin_array = new ArrayList<>(); // ArrayList монет


        //обход страниц раздела форума
        for (int i = 0; i < 550; i += 50) {
            String page_url = "https://coins.lave.ru/forum/viewforum.php?f=50&start=";
            page_url = page_url.concat(Integer.toString(i)); //ссылка на страницу раздела форума
                Elements ele = getElementsFromDocument(page_url);

                //обход страницы форума и поиск названий и ссылок на страницы с монетами, определение года выпуска монеты.
                for (Element element : ele.select("tbody tr td")) {
                    Coin_info tmp = new Coin_info();
                    tmp.url = element.select("a").attr("href");//ссылка на страницу
                    tmp.title = element.select("a").text();//название страницы
                    //проверка "является ли страница, страницой про монеты"
                    if (tmp.url != "" && !tmp.url.startsWith("./memberlist") && (tmp.title.contains("руб") || tmp.title.contains("копеек"))) {
                        /*if (tmp.title.contains("руб"))
                            tmp.coin = "рубль";
                        else
                            tmp.coin = "копейка";
                        tmp.numenal = tmp.title.substring(0, 2);*/
                        tmp.url = "https://coins.lave.ru/forum".concat(tmp.url.substring(1));//ссылка на страницу с монетой

                         //определение года выпуска монеты из названия
                        Matcher m = tmp.year_pattern.matcher(tmp.title);
                        while (m.find()) {
                            tmp.year = tmp.year.concat(tmp.title.substring(m.start(), m.end()) + ";");
                        }
                        //подключение к странице с монетой
                            Elements ele2 = getElementsFromDocument(tmp.url);
                            //парсинг страницы с монетой
                            for (Element element2 : ele2.select("tbody tr tbody tr td")) {
                                //поиск фотографий
                                String tmp_img = element2.select("a img").attr("src");
                                if ((Arrays.asList(tmp.types).contains(tmp_img.substring(tmp_img.lastIndexOf('.') + 1)))
                                        && !tmp_img.startsWith("https://coins.lave.ru/forum/small")) {
                                    tmp.img_url.add(tmp_img);
                                }
                                //поиск цен
                                String tmp_user_message=element2.select("div.postbody").text();
                              /*  Matcher m2 = tmp.price_pattern.matcher(tmp_user_message);
                                while (m2.find()) {
                                    tmp.user_message = tmp.user_message.concat(tmp_user_message.substring(m2.start(), m2.end()) + ";");
                                }*/
                                System.out.println(tmp.user_message);
                            }
                        tmp.print();
                        coin_array.add(tmp);
                    }
                }

        }
        System.out.println(coin_array.size());
    }
}
