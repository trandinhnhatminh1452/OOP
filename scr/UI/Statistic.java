package UI;

import DB.DBconnect;
import code.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;

public class Statistic extends BaseUI {
    private TableView<Borrow> tableView;
    private ObservableList<Borrow> borrowList1 = FXCollections.observableArrayList();
    private ObservableList<Borrow> borrowList2 = FXCollections.observableArrayList();
    private ObservableList<Borrow> borrowList3 = FXCollections.observableArrayList();
    private Connection connection;
    private BorderPane root;

    public Statistic(Stage primaryStage) {
        super(primaryStage);
        root = (BorderPane) getRoot();
        this.primaryStage = primaryStage;

        DBconnect db = new DBconnect();
        this.connection = db.connect();

        int totalBorrowers = countTotalBorrowers();
        int totalBooksBorrowed = countTotalBooksBorrowed();
        int overdueLoans = countOverdueLoans();
        int unreturnedBooks = countUnreturnedBooks();
        int totalBooks = countTotalBooksInStock();
        int damaged = countDamagedBooks();

        root.setTop(
                layout2(
                        20,
                        layout3(20,
                                new Label("Số người mượn: " + totalBorrowers),
                                new Label("Tổng số sách: " + totalBooks),
                                new Label("Số phiếu mượn: " + totalBooksBorrowed)
                        ),
                        layout3(20,
                                new Label("Số sách hỏng: " + damaged),
                                new Label("Số phiếu trả quá hạn: " + overdueLoans),
                                new Label("Số phiếu chưa trả sách: " + unreturnedBooks)
                        ),
                        30)
        );

        Button btnOverdue = new Button("Trả quá hạn");
        btnOverdue.setOnMouseClicked(e -> fetchOverdueLoanDataFromDatabase());

        Button btnUnreturn = new Button("Chưa Trả");
        btnUnreturn.setOnMouseClicked(e -> fetchUnreturnedBooksDataFromDatabase());

        Button btnDamaged = new Button("Tình trạng");
        btnDamaged.setOnMouseClicked(e -> fetchDamagedBooksDataFromDatabase());

        Button btnMenu = new Button("Menu");
        btnMenu.setOnMouseClicked(e -> returnMenu());

        setBtn(btnOverdue);
        setBtn(btnUnreturn);
        setBtn(btnDamaged);
        setBtn(btnMenu);
        root.setCenter(layout1(30, btnOverdue, btnUnreturn, btnDamaged, btnMenu));
        root.setBottom(new VBox());
    }

    public HBox layout1(int height, Node node1, Node node2, Node node3, Node node4) {
        HBox node = new HBox(height);
        node.getChildren().addAll(node1, node2, node3, node4);
        node.setAlignment(Pos.CENTER);
        node.setTranslateY(20);
        return node;
    }

    private void fetchDamagedBooksDataFromDatabase() {
        String sql = """
                SELECT b.bookid AS BookID, b.bookname AS BookName, a.authorname AS AuthorName, p.publishername AS PublisherName,ld.isdamaged AS IsDamaged
                FROM  library.book b
                JOIN  library.loandetail ld ON b.bookid = ld.bookid
                JOIN  library.author a ON a.authorid = b.authorid
                JOIN  library.publisher p ON p.publisherid = b.publisherid
                WHERE  ld.isdamaged = TRUE
                ORDER BY b.bookid ASC
                """;


        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            borrowList1.clear();

            while (rs.next()) {
                String bookId = rs.getString("BookID");
                String bookName = rs.getString("BookName");
                String authorName = rs.getString("AuthorName");
                String publisherName = rs.getString("PublisherName");
                String isDamaged = rs.getBoolean("IsDamaged") ? "Có" : "Không";

                Book book = new Book();
                book.setBookId(bookId);
                book.setTitle(bookName);
                book.setAuthor(authorName);
                book.setPublisher(publisherName);

                Borrow borrow = new Borrow(null, null, null, null, book, null, isDamaged);
                borrowList1.add(borrow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        showTableDamaged(borrowList1);
    }

    private void fetchOverdueLoanDataFromDatabase() {
        String sql = """
                SELECT b.BookID,b.BookName,r.ReaderID,r.ReaderName,l.LoanDate,ld.DueDate,ld.ReturnDate
                FROM library.Loan l
                JOIN library.LibraryCard lc ON l.CardID = lc.CardID
                JOIN library.Reader r ON r.CardID = lc.CardID
                JOIN library.LoanDetail ld ON l.LoanID = ld.LoanID
                JOIN library.Book b ON ld.BookID = b.BookID
                where ld.ReturnDate>ld.DueDate
                """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            borrowList2.clear();

            while (rs.next()) {
                String bookId = rs.getString("BookID");
                String bookName = rs.getString("BookName");
                String readerId = rs.getString("ReaderID");
                String readerName = rs.getString("ReaderName");
                LocalDate loanDate = rs.getDate("LoanDate").toLocalDate();
                LocalDate dueDate = rs.getDate("DueDate").toLocalDate();
                LocalDate returnDate = rs.getDate("ReturnDate").toLocalDate();

                Book book = new Book();
                book.setBookId(bookId);
                book.setTitle(bookName);

                Reader reader = new Reader();
                reader.setReaderId(readerId);
                reader.setName(readerName);

                Borrow borrow = new Borrow();
                borrow.setBook(book);
                borrow.setReader(reader);
                borrow.setBorrowDate(loanDate);
                borrow.setDueDate(dueDate);
                borrow.setReturnDate(returnDate);

                borrowList2.add(borrow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        showTableUnreturnAndOverDue(borrowList2);
    }

    private void fetchUnreturnedBooksDataFromDatabase() {
        String sql = """
            SELECT b.BookID,b.BookName, r.ReaderID,r.ReaderName,l.LoanDate,ld.DueDate
            FROM library.Loan l
            JOIN library.LibraryCard lc ON l.CardID = lc.CardID
            JOIN library.Reader r ON r.CardID = lc.CardID
            JOIN library.LoanDetail ld ON l.LoanID = ld.LoanID
            JOIN library.Book b ON ld.BookID = b.BookID
            WHERE ld.ReturnDate IS NULL
            """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            borrowList3.clear();

            while (rs.next()) {
                String bookId = rs.getString("BookID");
                String bookName = rs.getString("BookName");
                String readerId = rs.getString("ReaderID");
                String readerName = rs.getString("ReaderName");
                LocalDate loanDate = rs.getDate("LoanDate").toLocalDate();
                LocalDate dueDate = rs.getDate("DueDate").toLocalDate();

                // Create Book object
                Book book = new Book();
                book.setBookId(bookId);
                book.setTitle(bookName);

                // Check if Reader data is not null, otherwise set default or skip
                Reader reader = new Reader();
                if (readerId != null && readerName != null) {
                    reader.setReaderId(readerId);
                    reader.setName(readerName);
                } else {
                    reader.setReaderId("Unknown");
                    reader.setName("Unknown");
                }

                // Create Borrow object
                Borrow borrow = new Borrow();
                borrow.setBook(book);
                borrow.setReader(reader); // Ensure the reader is never null
                borrow.setBorrowDate(loanDate);
                borrow.setDueDate(dueDate);

                borrowList3.add(borrow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        showTableUnreturnAndOverDue(borrowList3);
    }

    private void showTableUnreturnAndOverDue(ObservableList<Borrow> borrowList) {
        tableView = new TableView<>();
        tableView.setItems(borrowList);
        tableView.prefWidthProperty().bind(tableView.widthProperty().multiply(1));

        TableColumn<Borrow, String> colBookId = new TableColumn<>("Mã sách");
        colBookId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getBookId()));
        colBookId.setStyle("-fx-alignment: center;");
        colBookId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        TableColumn<Borrow, String> colBookName = new TableColumn<>("Tên sách");
        colBookName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getTitle()));
        colBookName.setStyle("-fx-alignment: center;");
        colBookName.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));

        TableColumn<Borrow, String> colReaderId = new TableColumn<>("Mã người mượn");
        colReaderId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReader().getReaderId()));
        colReaderId.setStyle("-fx-alignment: center;");
        colReaderId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        TableColumn<Borrow, String> colReaderName = new TableColumn<>("Tên người mượn");
        colReaderName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReader().getName()));
        colReaderName.setStyle("-fx-alignment: center;");
        colReaderName.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        TableColumn<Borrow, LocalDate> colLoanDate = new TableColumn<>("Ngày mượn");
        colLoanDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colLoanDate.setStyle("-fx-alignment: center;");
        colLoanDate.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        TableColumn<Borrow, LocalDate> colDueDate = new TableColumn<>("Hạn trả");
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colDueDate.setStyle("-fx-alignment: center;");
        colDueDate.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        TableColumn<Borrow, LocalDate> colReturnDate = new TableColumn<>("Ngày trả");
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colReturnDate.setStyle("-fx-alignment: center;");
        colReturnDate.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        tableView.getColumns().clear();
        tableView.getColumns().addAll(colBookId, colBookName, colReaderId, colReaderName, colLoanDate, colDueDate, colReturnDate);

        root.setBottom(new VBox(tableView));
    }

    private void showTableDamaged(ObservableList<Borrow> borrowList) {
        tableView = new TableView<>();
        tableView.setItems(borrowList);
        tableView.prefWidthProperty().bind(tableView.widthProperty().multiply(1));

        TableColumn<Borrow, String> colBookId = new TableColumn<>("Mã sách");
        colBookId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getBookId()));
        colBookId.setStyle("-fx-alignment: center;");
        colBookId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        TableColumn<Borrow, String> colBookName = new TableColumn<>("Tên sách");
        colBookName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getTitle()));
        colBookName.setStyle("-fx-alignment: center;");
        colBookName.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        TableColumn<Borrow, String> colAuthorName = new TableColumn<>("Tên Tác Giả");
        colAuthorName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getAuthor()));
        colAuthorName.setStyle("-fx-alignment: center;");
        colAuthorName.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        TableColumn<Borrow, String> colPublisher = new TableColumn<>("Nhà Sản Xuất");
        colPublisher.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getPublisher()));
        colPublisher.setStyle("-fx-alignment: center;");
        colPublisher.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        TableColumn<Borrow, String> colDamaged = new TableColumn<>("Tình Trạng Hư Hỏng");
        colDamaged.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCondition()));
        colDamaged.setStyle("-fx-alignment: center;");
        colDamaged.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));


        tableView.getColumns().clear();
        tableView.getColumns().addAll(colBookId, colBookName, colAuthorName, colPublisher, colDamaged);

        root.setBottom(new VBox(tableView));
    }

    private int countTotalBorrowers() {
        String sql = """
                SELECT COUNT(DISTINCT r.ReaderID) AS TotalBorrowers FROM library.Loan l 
                JOIN library.LibraryCard lc ON l.CardID = lc.CardID 
                JOIN library.Reader r ON lc.CardID = r.cardID
                """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("TotalBorrowers");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countTotalBooksBorrowed() {
        String sql = "SELECT COUNT(ld.BookID) AS TotalBooksBorrowed FROM library.LoanDetail ld";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("TotalBooksBorrowed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countOverdueLoans() {
        String sql = "SELECT COUNT(*) AS OverdueLoans FROM library.LoanDetail \n" +
                "WHERE DueDate < ReturnDate ";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("OverdueLoans");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countUnreturnedBooks() {
        String sql = "SELECT COUNT(*) AS UnreturnedBooks FROM library.LoanDetail WHERE ReturnDate IS NULL";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("UnreturnedBooks");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countTotalBooksInStock() {
        String sql = "SELECT SUM(Quantity) AS TotalBooks FROM library.Book";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("TotalBooks");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countDamagedBooks() {
        String sql = "SELECT COUNT(*) AS DamagedBooks FROM library.loanDetail WHERE isDamaged = True";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("DamagedBooks");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
