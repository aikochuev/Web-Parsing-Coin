import java.sql.*;


public class Connector {
    public static Connection connect;
    public static Statement statement;
    public static int i=0;

    // подключение к базе данных
    public static void Connect() throws ClassNotFoundException, SQLException {
        try {
            connect = null;
            Class.forName("org.sqlite.JDBC");
            connect = DriverManager.getConnection("jdbc:sqlite:coin_db.sdb");
            statement = connect.createStatement();
            System.out.println("БД подключена");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + " : " + e.getMessage() + " Ошибка подключения к БД");
        }
    }

    // Создание таблицы
    public static void CreateDB() throws ClassNotFoundException, SQLException {
        try {
            statement.execute("CREATE TABLE if not exists 'Coins' ('id' INTEGER PRIMARY KEY AUTOINCREMENT,'title' text,'url' text,'year' text,'userMessage' text)");
            statement.execute("CREATE TABLE if not exists 'Img' ('id' INTEGER PRIMARY KEY AUTOINCREMENT,'id_page' INTEGER,'imgUrl' text,FOREIGN KEY(id_page) REFERENCES 'Coins'(id));");
            statement.execute("CREATE TABLE if not exists 'Info' ('id' INTEGER PRIMARY KEY AUTOINCREMENT,'LastAdd' INTEGER)");
            System.out.println("Таблицы созданы или уже существуют");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + " : " + e.getMessage() + " Ошибка создания таблиц");
        }
    }

    public static void SetInfo() throws SQLException {
        statement.execute("INSERT INTO 'Info' ('LastAdd') VALUES('"+i+"')");
    }
    public static void GetInfo() throws SQLException {
        ResultSet resultSet=statement.executeQuery("SELECT * FROM Info ORDER BY id DESC LIMIT 1");
        System.out.println(resultSet.getInt("LastAdd"));
        resultSet.close();
    }
    // Заполнение таблицы
    public static void WriteDB(Coin_info coin) throws SQLException {
        ResultSet resSetWrite = statement.executeQuery("SELECT url FROM Coins WHERE url='" + coin.url + "';");
        if (!resSetWrite.next()) {
            statement.execute("INSERT INTO 'Coins' ('title','url', 'year','userMessage') VALUES ('" + coin.title + "','" + coin.url + "','" + coin.year + "','"+coin.user_message+"');");
            for (String imgURL : coin.img_url)
                statement.execute("INSERT INTO 'Img' ('id_page', 'imgUrl') VALUES ((SELECT id FROM Coins ORDER BY id DESC LIMIT 1),'" + imgURL + "');");
            i++;
            System.out.println("Записано в БД");
        }
        resSetWrite.close();
    }

    // Удаление таблиц
    public static void DeleteTables() throws SQLException {
        statement.execute("DROP TABLE IF EXISTS 'Coins'");
        statement.execute("DROP TABLE IF EXISTS 'Img'");
        statement.execute("DROP TABLE IF EXISTS 'Info'");
        System.out.println("База данных очищена");
    }

    // Вывод таблицы
    public static void ReadDB() throws ClassNotFoundException, SQLException {
        ResultSet resSet = statement.executeQuery("SELECT c.id as id, c.title as title, c.url as url," +
                " c.year as year,c.userMessage as mess, i.id_page as id_p, i.imgUrl as img FROM Coins c JOIN Img i ON c.id=i.id_page");
        int current_id = 0;
        Coin_info tmp = new Coin_info();
        while (resSet.next()) {
            if (resSet.getInt("id") != current_id) {
                tmp.print();
                tmp = new Coin_info();
                tmp.id = resSet.getInt("id");
                tmp.title = resSet.getString("title");
                tmp.url = resSet.getString("url");
                tmp.year = resSet.getString("year");
                tmp.user_message=resSet.getString("mess");
                current_id = tmp.id;
            }
            tmp.img_url.add(resSet.getString("img"));
        }
        tmp.print();
        resSet.close();
        System.out.println("Записей выведено = "+current_id);
    }
}
