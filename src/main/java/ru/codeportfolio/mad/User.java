package ru.codeportfolio.mad;


public class User {
    private final int id;
    private final String name;
    private final String email;

    // Конструктор
    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
