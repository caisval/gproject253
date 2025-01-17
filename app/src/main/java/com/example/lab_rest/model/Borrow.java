package com.example.lab_rest.model;

public class Borrow {

    private int id;

    private int book_id;
    private Book book;

    private int user_id;
    private User user;

    private String borrow_date;
    private String return_date;

    public Borrow(int id, int book_id, Book book, int user_id, User user, String borrow_date, String return_date) {
        this.id = id;
        this.book_id = book_id;
        this.book = book;
        this.user_id = user_id;
        this.user = user;
        this.borrow_date = borrow_date;
        this.return_date = return_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBorrow_date() {
        return borrow_date;
    }

    public void setBorrow_date(String borrow_date) {
        this.borrow_date = borrow_date;
    }

    public String getReturn_date() {
        return return_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    @Override
    public String toString() {
        return "Borrow{" +
                "id=" + id +
                ", book_id=" + book_id +
                ", book=" + book +
                ", user_id=" + user_id +
                ", user=" + user +
                ", borrowDate='" + borrow_date + '\'' +
                ", returnDate='" + return_date + '\'' +
                '}';
    }
}

