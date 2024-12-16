package Aims;

import java.time.LocalDate;
import java.util.List;
import code.*;
import Management.LibraryManagement;

public class Main {
    public static void main(String[] args) {
    LibraryManagement library = new LibraryManagement();

    // 1. Khởi tạo dữ liệu mẫu
    Category fiction = new Category("C1", "Fiction");
    Category horror = new Category("C2", "Horror");

    Book book1 = new Book("B1", "Harry Potter", "J.K. Rowling", "Bloomsbury", 5, fiction);
    Book book2 = new Book("B2", "The Shining", "Stephen King", "Doubleday", 3, horror);

    library.getBooks().add(book1);
    library.getBooks().add(book2);

    Reader reader1 = new Reader("R1", "Alice", "0123456789", "123 Wonderland");
    Reader reader2 = new Reader("R2", "Bob","0987654321", "456 Nowhere");

    library.getReaders().add(reader1);
    library.getReaders().add(reader2);

    // Kiểm tra dữ liệu đã được thêm
    System.out.println("Books: " + library.getBooks());
    System.out.println("Readers: " + library.getReaders());

    // 2. Thực thi các chức năng
    System.out.println("\nBorrowing books:");
    library.borrowBook("B1", "R1");
    library.borrowBook("B2", "R2");
    library.borrowBook("B3", "R1"); // Sách không tồn tại

    System.out.println("\nSearching for books:");
    List<Book> searchResults = library.searchBooks("Harry", "title");
    for (Book book : searchResults) {
        System.out.println(book);
    }

    System.out.println("\nReturning books:");
    library.returnBook("BR1", false);
    library.returnBook("BR2", true);
    library.returnBook("BR3", false);

    System.out.println("\nGenerating statistics:");
    library.generateStatistics();
}

}