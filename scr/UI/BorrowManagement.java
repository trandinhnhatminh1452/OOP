package UI;

import DB.DBconnect;
import code.Book;
import code.Borrow;
import code.Category;
import code.Reader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;


public class BorrowManagement extends BaseUI {
    public TableView<Borrow> tableView;
    private ObservableList<Borrow> borrowList = FXCollections.observableArrayList();
    private Stage primaryStage;
    private BorderPane root;
    private Connection connection;
    private TextField searchFieldByborrowId;
    private TextField searchFieldByborrowDate;
    private TextField searchFieldBydueDate;
    private TextField searchFieldByReaderID;
    private TextField searchFieldByBookID;
    private TextField searchFieldByreturnDate;
    public BorrowManagement(Stage primaryStage){
        super(primaryStage);
        root = (BorderPane) getRoot();
        this.primaryStage = primaryStage;

        searchFieldByborrowId = new TextField();
        searchFieldByborrowId.setPromptText("Tìm kiếm mã mượn...");

        searchFieldByReaderID = new TextField();
        searchFieldByReaderID.setPromptText("Tìm kiếm mã độc giả...");

        searchFieldByBookID = new TextField();
        searchFieldByBookID.setPromptText("Tìm kiếm mã sách...");

        searchFieldByborrowDate = new TextField();
        searchFieldByborrowDate.setPromptText("Tìm kiếm theo ngày mượn...");


        searchFieldBydueDate = new TextField();
        searchFieldBydueDate.setPromptText("Tìm kiếm theo hạn trả...");

        searchFieldByreturnDate = new TextField();
        searchFieldByreturnDate.setPromptText("Tìm kiếm theo ngày trả...");

        root.setTop(
                layout2(
                        20,
                        layout3(20,
                                setting("Mã Mượn", searchFieldByborrowId),
                                setting("Mã Người Mượn", searchFieldByReaderID),
                                setting("Mã Sách", searchFieldByBookID)
                        ),
                        layout3(20,
                                setting("Ngày Mượn", searchFieldByborrowDate),
                                setting("Hạn Trả", searchFieldBydueDate),
                                setting("Ngày trả", searchFieldByreturnDate)
                        ),
                        30)
        );

        Button btnSearch = new Button("Tìm Kiếm");
        btnSearch.setOnMouseClicked(e -> {
            searchBorrows();
        });

        Button btnAdd = new Button("Thêm");
        btnAdd.setOnMouseClicked(e -> {
            addBorrowToDatabase(createBorrow());
        });

        Button btnDelete = new Button("Xóa");
        btnDelete.setOnMouseClicked(e -> {
            deleteBorrows();
        });

        Button btnAddReturnDate = new Button("Cập nhật ngày trả");
        btnAddReturnDate.setOnMouseClicked(e -> {
            updateReturnDates();
            System.out.println(true);
        });

        Button btnMenu = new Button("Menu");
        btnMenu.setOnMouseClicked(e -> {
            returnMenu();
        });

        setBtn(btnAdd);
        setBtn(btnDelete);
        setBtn(btnSearch);
        setBtn(btnMenu);
        root.setCenter(layout(40, btnAdd, btnDelete, btnSearch,btnAddReturnDate, btnMenu));
        root.setBottom(createBorrowTableView());

        DBconnect db = new DBconnect();
        this.connection = db.connect();

        fetchBorrowDataFromDatabase();
    }


    public HBox layout(int height, Node node1, Node node2, Node node3, Node node4,Node node5) {
        HBox node = new HBox(height);
        node.getChildren().addAll(node1, node2, node3, node4, node5);
        node.setAlignment(Pos.CENTER);  // Set the alignment to center
        node.setTranslateY(20);
        return node;
    }



    private void fetchBorrowDataFromDatabase() {
        String sql = """
            SELECT
              l.LoanID, r.ReaderID, b.BookID,
              l.LoanDate, ld.DueDate, ld.ReturnDate
            FROM
              library.Loan l
            JOIN
              library.LibraryCard lc ON l.CardID = lc.CardID
            JOIN
              library.Reader r ON r.CardID = lc.CardID
            JOIN
              library.LoanDetail ld ON l.LoanID = ld.LoanID
            JOIN
              library.Book b ON ld.BookID = b.BookID;
    """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Xóa danh sách cũ
            borrowList.clear();

            while (rs.next()) {
                // Lấy các giá trị từ kết quả truy vấn
                String loanId = rs.getString("LoanID");
                String bookId = rs.getString("BookID");
                LocalDate loanDate = rs.getDate("LoanDate").toLocalDate();
                LocalDate dueDate = rs.getDate("DueDate").toLocalDate();
                LocalDate returnDate = rs.getDate("ReturnDate") != null ? rs.getDate("ReturnDate").toLocalDate() : null;
                String readerId = rs.getString("ReaderID");

                // Tạo đối tượng Reader
                Reader reader = new Reader(readerId); // Chỉ cần ID của người mượn

                // Tạo đối tượng Book
                Book book = new Book(bookId); // Chỉ cần ID của sách

                // Tạo đối tượng Borrow
                Borrow borrow = new Borrow(loanId, loanDate, dueDate, returnDate, book, reader);

                // Thêm vào danh sách borrowList
                borrowList.add(borrow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Cập nhật TableView
        tableView.setItems(borrowList);
    }

    private void searchBorrows() {
        String searchTermLoanID = searchFieldByborrowId.getText().trim();
        String searchTermReaderID = searchFieldByReaderID.getText().trim();
        String searchTermBookID = searchFieldByBookID.getText().trim();
        String searchTermLoanDate = searchFieldByborrowDate.getText().trim();
        String searchTermDueDate = searchFieldBydueDate.getText().trim();
        String searchTermReturnDate = searchFieldByreturnDate.getText().trim();

        // Lọc danh sách mượn dựa trên tất cả các điều kiện tìm kiếm
        ObservableList<Borrow> filteredList = FXCollections.observableArrayList();

        for (Borrow borrow : borrowList) {
            boolean matchesLoanID = searchTermLoanID.isEmpty() || borrow.getBorrowId().toLowerCase().equals(searchTermLoanID.toLowerCase());
            boolean matchesReaderID = searchTermReaderID.isEmpty() || borrow.getReaderId().toLowerCase().equals(searchTermReaderID.toLowerCase());
            boolean matchesBookID = searchTermBookID.isEmpty() || borrow.getBookId().toLowerCase().equals(searchTermBookID.toLowerCase());
            boolean matchesLoanDate = searchTermLoanDate.isEmpty() || borrow.getBorrowDate().toString().equals(searchTermLoanDate);
            boolean matchesDueDate = searchTermDueDate.isEmpty() || borrow.getDueDate().toString().equals(searchTermDueDate);
            boolean matchesReturnDate = searchTermReturnDate.isEmpty() || (borrow.getReturnDate() != null && borrow.getReturnDate().toString().equals(searchTermReturnDate));

            // Chỉ thêm mượn vào filteredList nếu nó khớp với tất cả các điều kiện tìm kiếm
            if (matchesLoanID && matchesReaderID && matchesBookID && matchesLoanDate && matchesDueDate && matchesReturnDate) {
                filteredList.add(borrow);
            }
        }

        // Cập nhật TableView với danh sách đã lọc
        tableView.setItems(filteredList);
    }

    private Borrow createBorrow() {
        String loanID =  searchFieldByborrowId.getText().trim();
        String readerID = searchFieldByReaderID.getText().trim();
        String bookID = searchFieldByBookID.getText().trim();
        LocalDate loanDate = LocalDate.parse(searchFieldByborrowDate.getText().trim()); // Đảm bảo rằng ngày đúng định dạng
        LocalDate dueDate = LocalDate.parse(searchFieldBydueDate.getText().trim());
        LocalDate returnDate = searchFieldByreturnDate.getText().trim().isEmpty() ? null : LocalDate.parse(searchFieldByreturnDate.getText().trim());

        // Lấy thông tin sách và bạn đọc từ ID
        Book book = new Book(bookID);
        Reader reader = new Reader(readerID);

        // Tạo đối tượng Borrow
        Borrow borrow = new Borrow(loanID, loanDate, dueDate, returnDate, book, reader);

        return borrow;
    }

    private void updateReturnDates() {
        // Lấy giá trị ngày trả từ TextField
        String returnDateText = searchFieldByreturnDate.getText().trim();

        if (returnDateText.isEmpty()) {
            System.out.println("Ngày trả không hợp lệ");
            return;
        }

        // Chỉ tạo Borrow nếu đã có LoanID (borrowId) từ người dùng
        String borrowId = searchFieldByborrowId.getText().trim();
        if (borrowId.isEmpty()) {
            System.out.println("Mã mượn không hợp lệ");
            return;
        }

        Borrow borrow = new Borrow();
        borrow.setBorrowId(borrowId);  // Đảm bảo thiết lập borrowId

        // Cập nhật ngày trả vào cơ sở dữ liệu
        String updateReturnDateSql = """
    UPDATE library.LoanDetail
    SET ReturnDate = ?
    WHERE LoanID = ? AND ReturnDate IS NULL;
    """;

        try (PreparedStatement pstmt = connection.prepareStatement(updateReturnDateSql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(returnDateText)); // Set ngày trả
            pstmt.setString(2, borrow.getBorrowId()); // Set LoanID từ đối tượng Borrow
            // Thực thi câu lệnh cập nhật và kiểm tra số dòng bị ảnh hưởng
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi cập nhật ngày trả: " + e.getMessage());
        }

        // Cập nhật lại TableView
        tableView.setItems(null);
        tableView.setItems(borrowList);
        fetchBorrowDataFromDatabase();
    }


    private void addBorrowToDatabase(Borrow borrow) {
        String cardId = null;

        Random random = new Random();
        int staffIdNumber = random.nextInt(10) + 1;  // Tạo StaffID ngẫu nhiên từ 1 đến 10
        String staffId = String.valueOf(staffIdNumber);

        // Truy vấn để lấy CardID từ ReaderID thông qua JOIN giữa LibraryCard và Reader
        String checkCardQuery = """
        SELECT lc.CardID
        FROM library.LibraryCard lc
        JOIN library.Reader r ON lc.CardID = r.CardID
        WHERE r.ReaderID = ?;  
    """;

        try (PreparedStatement checkCardStmt = connection.prepareStatement(checkCardQuery)) {
            // Thực hiện truy vấn với ReaderID
            checkCardStmt.setString(1, borrow.getReader().getReaderId());  // Sử dụng ReaderID từ đối tượng Borrow

            try (ResultSet rs = checkCardStmt.executeQuery()) {
                if (rs.next()) {
                    cardId = rs.getString("CardID");  // Lấy CardID từ kết quả truy vấn
                } else {
                    System.out.println("Error: Không tìm thấy CardID cho ReaderID: " + borrow.getReader().getReaderId());
                    return;  // Dừng nếu không tìm thấy CardID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;  // Nếu có lỗi truy vấn, dừng lại
        }

        // Nếu CardID tồn tại, tiếp tục chèn vào bảng Loan và LoanDetail
        if (cardId != null) {
            String sql = """
        INSERT INTO library.Loan (LoanID, CardID, StaffID, LoanDate)
        VALUES (?, ?, ?, ?);

        INSERT INTO library.LoanDetail (LoanID, BookID, DueDate, ReturnDate)
        VALUES (?, ?, ?, ?);
        """;

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                // Lưu vào bảng Loan
                stmt.setString(1, borrow.getBorrowId());
                stmt.setString(2, cardId);
                stmt.setString(3, staffId);
                stmt.setDate(4, java.sql.Date.valueOf(borrow.getBorrowDate()));

                // Lưu vào bảng LoanDetail
                stmt.setString(5, borrow.getBorrowId());  // LoanID (khớp với bảng Loan)
                stmt.setString(6, borrow.getBook().getBookId());  // BookID từ đối tượng Book
                stmt.setDate(7, java.sql.Date.valueOf(borrow.getDueDate()));  // DueDate (Hạn trả)
                stmt.setDate(8, borrow.getReturnDate() != null ? java.sql.Date.valueOf(borrow.getReturnDate()) : null);  // ReturnDate (Ngày trả), có thể null

                // Thực hiện lệnh SQL
                stmt.executeUpdate();
                System.out.println("Borrow record added for LoanID: " + borrow.getBorrowId());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Thêm vào danh sách borrowList và cập nhật TableView
            borrowList.add(borrow);
            tableView.setItems(null);
            tableView.setItems(borrowList);
        }
    }



    private void deleteBorrows() {
        String searchTermLoanID = searchFieldByborrowId.getText().trim();
        String searchTermReaderID = searchFieldByReaderID.getText().trim();
        String searchTermBookID = searchFieldByBookID.getText().trim();
        String searchTermLoanDate = searchFieldByborrowDate.getText().trim();
        String searchTermDueDate = searchFieldBydueDate.getText().trim();
        String searchTermReturnDate = searchFieldByreturnDate.getText().trim();

        // Điều kiện xóa trong danh sách
        borrowList.removeIf(borrow ->
                (searchTermLoanID.isEmpty() || borrow.getBorrowId().equalsIgnoreCase(searchTermLoanID)) &&
                        (searchTermReaderID.isEmpty() || borrow.getReaderId().equalsIgnoreCase(searchTermReaderID)) &&
                        (searchTermBookID.isEmpty() || borrow.getBookId().equalsIgnoreCase(searchTermBookID)) &&
                        (searchTermLoanDate.isEmpty() || borrow.getBorrowDate().toString().equals(searchTermLoanDate)) &&
                        (searchTermDueDate.isEmpty() || borrow.getDueDate().toString().equals(searchTermDueDate)) &&
                        (searchTermReturnDate.isEmpty() || (borrow.getReturnDate() != null && borrow.getReturnDate().toString().equals(searchTermReturnDate)))
        );

        // Xóa trong cơ sở dữ liệu
        String sql = "DELETE FROM library.Loan WHERE " +
                "(? = '' OR LoanID = ?) AND " +
                "(? = '' OR ReaderID = ?) AND " +
                "(? = '' OR BookID = ?) AND " +
                "(? = '' OR LoanDate = ?) AND " +
                "(? = '' OR DueDate = ?) AND " +
                "(? = '' OR ReturnDate = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, searchTermLoanID);
            stmt.setString(2, searchTermLoanID);
            stmt.setString(3, searchTermReaderID);
            stmt.setString(4, searchTermReaderID);
            stmt.setString(5, searchTermBookID);
            stmt.setString(6, searchTermBookID);
            stmt.setString(7, searchTermLoanDate);
            stmt.setString(8, searchTermLoanDate);
            stmt.setString(9, searchTermDueDate);
            stmt.setString(10, searchTermDueDate);
            stmt.setString(11, searchTermReturnDate);
            stmt.setString(12, searchTermReturnDate);

            int rowsDeleted = stmt.executeUpdate();
            System.out.println("Số bản ghi đã xóa: " + rowsDeleted);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Làm mới TableView
        tableView.setItems(null);
        tableView.setItems(borrowList);
    }


    private HBox createBorrowTableView() {
        HBox borrowTable = new HBox();
        borrowTable.setTranslateY(-40);
        this.tableView = new TableView<>(); // Sửa kiểu TableView thành Borrow
        tableView.prefWidthProperty().bind(borrowTable.widthProperty().multiply(0.9));

        // Tạo cột Mã mượn
        TableColumn<Borrow, String> colBorrowId = new TableColumn<>("Mã mượn");
        colBorrowId.setCellValueFactory(new PropertyValueFactory<>("borrowId")); // Thuộc tính "borrowId" trong lớp Borrow
        colBorrowId.setStyle("-fx-alignment: center;");
        colBorrowId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        // Tạo cột Mã người mượn
        TableColumn<Borrow, String> colReaderId = new TableColumn<>("Mã người mượn");
        colReaderId.setCellValueFactory(new PropertyValueFactory<>("readerId")); // Thuộc tính "readerId" trong lớp Borrow
        colReaderId.setStyle("-fx-alignment: center;");
        colReaderId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        // Tạo cột Mã sách
        TableColumn<Borrow, String> colBookId = new TableColumn<>("Mã sách");
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId")); // Thuộc tính "bookId" trong lớp Borrow
        colBookId.setStyle("-fx-alignment: center;");
        colBookId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        // Tạo cột Ngày mượn
        TableColumn<Borrow, String> colBorrowDate = new TableColumn<>("Ngày mượn");
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate")); // Thuộc tính "borrowDate" trong lớp Borrow
        colBorrowDate.setStyle("-fx-alignment: center;");
        colBorrowDate.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        // Tạo cột Hạn trả
        TableColumn<Borrow, String> colReturnDueDate = new TableColumn<>("Hạn trả");
        colReturnDueDate.setCellValueFactory(new PropertyValueFactory<>("returnDueDate")); // Thuộc tính "returnDueDate" trong lớp Borrow
        colReturnDueDate.setStyle("-fx-alignment: center;");
        colReturnDueDate.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        // Tạo cột Ngày trả
        TableColumn<Borrow, String> colReturnDate = new TableColumn<>("Ngày trả");
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate")); // Thuộc tính "returnDate" trong lớp Borrow
        colReturnDate.setStyle("-fx-alignment: center;");
        colReturnDate.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        // Thêm tất cả các cột vào TableView
        tableView.getColumns().addAll(colBorrowId, colReaderId, colBookId, colBorrowDate, colReturnDueDate, colReturnDate);

        // Trả về TableView
        borrowTable.getChildren().add(tableView);
        borrowTable.setAlignment(Pos.CENTER);
        return borrowTable;
    }

}
