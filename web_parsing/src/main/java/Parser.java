import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Matcher;

public class Parser {
    private static Elements getElementsFromDocument(String url) throws SQLException, ClassNotFoundException {
        try {
            Document d = Jsoup.connect(url).timeout(10000).get();
            Elements ele = d.select("div#pagecontent").select("table.tablebg");
            System.out.println("Cтраница загружена");
            return ele;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + " : " + e.getMessage() + " Превышено время ожидания");
        }
        return null;
    }

    public static void parse(int limit) throws SQLException, ClassNotFoundException {
        int id = 0;
        for (int i = 0; i < limit * 50; i += 50) {
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
                    tmp.url = "https://coins.lave.ru/forum".concat(tmp.url.substring(1));//ссылка на страницу с монетой

                    //определение года выпуска монеты из названия
                    Matcher m = tmp.year_pattern.matcher(tmp.title);
                    while (m.find()) {
                        tmp.year = tmp.year.concat(tmp.title.substring(m.start(), m.end()) + ";");
                    }
                    tmp.title = tmp.title.replaceAll("[0-2]\\d\\d\\d-[0-2]\\d\\d\\d|[0-2]\\d\\d\\d|Оценка", "");
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
                        //поиск цен (запись сообщений в БД)
                        String tmp_user_message = element2.select("div.postbody").text();
                        tmp_user_message = tmp_user_message.replaceAll("\\n|\\(|\\)|(?U)[\\pP\\s]", " ");
                        tmp.user_message = tmp.user_message + tmp_user_message;
                    }
                    id++;
                    tmp.id = id;
                    tmp.print();
                    Connector.WriteDB(tmp);
                }
            }
        }
        Connector.SetInfo();
        System.out.println("Все страницы распарсены и " + Connector.i + " записей занесены в БД");
    }
}