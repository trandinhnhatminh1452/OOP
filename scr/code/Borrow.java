package code;

import java.time.LocalDate;

public class Borrow {
    private String borrowId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Book book;
    private Reader reader;
    private String condition;

    // Constructor with condition
    public Borrow(String borrowId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, Book book, Reader reader, String condition) {
        this.borrowId = borrowId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.book = book;
        this.reader = reader;
        this.condition = condition;
    }

    public Borrow(String borrowId, LocalDate borrowDate, LocalDate dueDate, Book book, Reader reader) {
        this.borrowId = borrowId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.book = book;
        this.reader = reader;
    }

    public Borrow() {
    }

    public String getCondition() {return condition;}

    public void setCondition(String condition) {this.condition = condition;}

    public String getReaderId() {return reader.getReaderId();}

    public String getBookId() {return book.getBookId();}
    public LocalDate getReturnDueDate() {return returnDate != null ? returnDate : dueDate;}

    public String getBorrowId() {return borrowId;}
    public void setBorrowId(String borrowId) {this.borrowId = borrowId;}

    public LocalDate getBorrowDate() {return borrowDate;}
    public void setBorrowDate(LocalDate borrowDate) {this.borrowDate = borrowDate;}

    public LocalDate getDueDate() {return dueDate;}
    public void setDueDate(LocalDate dueDate) {this.dueDate = dueDate;}

    public LocalDate getReturnDate() {return returnDate;}
    public void setReturnDate(LocalDate returnDate) {this.returnDate = returnDate;}

    public Book getBook() {return book;}
    public void setBook(Book book) {this.book = book;}

    public Reader getReader() {return reader;}
    public void setReader(Reader reader) {this.reader = reader;}

    @Override
    public String toString() {
        return "BorrowID: "+getBorrowId()+" From: "+getBorrowDate()+" to: "+getDueDate();
    }

}