package ru.codeportfolio;

import ru.codeportfolio.db.InitializatorDB;

public class Main {
    public static void main(String[] args){
        // принять запрос
        // обработать запрос, провалидировать
        // составить и отправить запрос в бд
        // получить ответ из бд
        // направить ответ в сервис отправки ответов на веб
        // отправить на сервлет

        InitializatorDB.execute();


    }
}
