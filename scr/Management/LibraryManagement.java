package Management;

import code.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryManagement {
    private List<Book> books;
    private List<Reader> readers;
    private List<Borrow> borrows;

    public LibraryManagement() {
        this.books = new ArrayList<>();
        this.readers = new ArrayList<>();
        this.borrows = new ArrayList<>();
    }

    // Getter cho danh sách
    public List<Book> getBooks() { return books; }
    public List<Reader> getReaders() { return readers; }
    public List<Borrow> getBorrows() { return borrows; }

    // 1. Phương thức mượn sách
    public boolean borrowBook(String bookId, String readerId) {
        Book book = findBookById(bookId);
        Reader reader = findReaderById(readerId);

        if (book == null) {
            System.out.println("Book not found!");
            return false;
        }
        if (reader == null) {
            System.out.println("Reader not found!");
            return false;
        }
        if (book.getQuantity() <= 0) {
            System.out.println("No books available for borrowing!");
            return false;
        }

        // Giảm số lượng sách và tạo bản ghi mượn
        book.setQuantity(book.getQuantity() - 1);
        Borrow borrow = new Borrow("BR" + (borrows.size() + 1), LocalDate.now(),
                LocalDate.now().plusDays(7), book, reader);
        borrows.add(borrow);
        reader.addBorrow(borrow);

        System.out.println("Borrow successfully created: " + borrow.getBorrowId());
        return true;
    }

    // 2. Phương thức trả sách
    public boolean returnBook(String borrowId, boolean isDamaged) {
        Borrow borrow = findBorrowById(borrowId);

        if (borrow == null) {
            System.out.println("Borrow record not found!");
            return false;
        }

        borrow.setReturnDate(LocalDate.now());
        Book book = borrow.getBook();

        if (!isDamaged) {
            book.setQuantity(book.getQuantity() + 1);
            System.out.println("Book returned successfully!");
        } else {
            System.out.println("Book is damaged and requires attention!");
        }

        return true;
    }

    // 3. Phương thức tìm kiếm sách
    public List<Book> searchBooks(String keyword, String criteria) {
        List<Book> results = new ArrayList<>();

        for (Book book : books) {
            if ((criteria.equalsIgnoreCase("title") && book.getTitle().toLowerCase().contains(keyword.toLowerCase())) ||
                (criteria.equalsIgnoreCase("author") && book.getAuthor().toLowerCase().contains(keyword.toLowerCase())) ||
                (criteria.equalsIgnoreCase("category") && book.getCategory().getCategoryName().toLowerCase().contains(keyword.toLowerCase()))) {
                results.add(book);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No books found for the given criteria!");
        }

        return results;
    }

    // 4. Phương thức thống kê
    public void generateStatistics() {
        int totalBorrowed = borrows.size();
        int damagedBooks = 0;
        int lostBooks = 0;

        for (Borrow borrow : borrows) {
            if (borrow.getReturnDate() == null) {
                lostBooks++;
            } else if (borrow.getBook().getQuantity() < 0) {
                damagedBooks++;
            }
        }

        System.out.println("Statistics:");
        System.out.println("Total books borrowed: " + totalBorrowed);
        System.out.println("Total lost books: " + lostBooks);
        System.out.println("Total damaged books: " + damagedBooks);
    }

    // 5. Các phương thức bổ trợ
    public Book findBookById(String bookId) {
        for (Book book : books) {
            if (book.getBookId().equals(bookId)) {
                return book;
            }
        }
        return null;
    }

    public Reader findReaderById(String readerId) {
        for (Reader reader : readers) {
            if (reader.getReaderId().equals(readerId)) {
                return reader;
            }
        }
        return null;
    }

    private Borrow findBorrowById(String borrowId) {
        for (Borrow borrow : borrows) {
            if (borrow.getBorrowId().equals(borrowId)) {
                return borrow;
            }
        }
        return null;
    }
}
