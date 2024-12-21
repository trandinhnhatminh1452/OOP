package code;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String publisher;
    private Category category;
    private int quantity ;

    public Book(String bookId,String title, String author, String publisher, int quantity, Category category) {
        this.title = title;
        this.bookId = bookId;
        this.author = author;
        this.publisher = publisher;
        this.quantity = quantity;
        this.category = category;
    }

    public Book(String bookId) {this.bookId = bookId;}
    public Book() {
    }
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "ID: "+getBookId()+" -Book: "+ getTitle()+" - "+getCategory()+" - "+getAuthor()+" - Publisher: "+getPublisher();
    }

}
