import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String command;
        Connector.Connect();//подключение к БД
        Connector.CreateDB();// создание таблиц, если их нет
        do {
            System.out.println("Введите одну из команд (clear,show stats,show info,parse,exit)");
            Scanner in = new Scanner(System.in);
            command = in.nextLine();
            if (command.equals("clear")) {
                Connector.DeleteTables();//удаление таблиц
                Connector.CreateDB();// создание таблиц пустых таблиц
            }
            if (command.equals("show stats"))
                Connector.ReadDB();// вывод данных из БД
            if (command.equals("show info"))
                Connector.GetInfo();// вывод количество записей добавленных за последнюю сессию
            if (command.equals("parse")) {
                int limit = 0;
                do {
                    System.out.println("Введите количество страниц раздела для парсинга");
                    try {
                        Scanner in2 = new Scanner(System.in);
                        limit = in2.nextInt();
                        if (limit > 0)
                            Parser.parse(limit); //парсинг
                    } catch (Exception e) {
                        System.err.println("Введите число типа int");
                    }
                } while (limit <= 0);
            }
        } while (!command.equals("exit"));
    }
}
