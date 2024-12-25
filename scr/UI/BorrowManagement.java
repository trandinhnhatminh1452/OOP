package UI;

import DB.DBconnect;
import code.*;
import javafx.collections.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
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
    private ComboBox<String> isDamagedComboBox;
    public BorrowManagement(Stage primaryStage){
        super(primaryStage);
        root = (BorderPane) getRoot();
        this.primaryStage = primaryStage;

        isDamagedComboBox = new ComboBox<>();
        isDamagedComboBox.getItems().addAll("Không", "Có");
        isDamagedComboBox.setValue("Không");

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
                        layout4(20,
                                setting("Ngày Mượn", searchFieldByborrowDate),
                                setting("Hạn Trả", searchFieldBydueDate),
                                setting("Ngày trả", searchFieldByreturnDate),
                                setting("Tình trạng",isDamagedComboBox)
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
            updateIsDamaged();
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

    public VBox layout4(int height, Node node1, Node node2, Node node3,Node node4) {
        VBox node = new VBox(height);
        node.getChildren().addAll(node1, node2, node3,node4);
        node.setAlignment(Pos.CENTER);
        return node;
    }

    private void fetchBorrowDataFromDatabase() {
        String sql = """
            SELECT l.LoanID, r.ReaderID, b.BookID,l.LoanDate, ld.DueDate, ld.ReturnDate,ld.isdamaged
            FROM library.Loan l
            JOIN library.LibraryCard lc ON l.CardID = lc.CardID
            JOIN library.Reader r ON r.CardID = lc.CardID
            JOIN library.LoanDetail ld ON l.LoanID = ld.LoanID
            JOIN library.Book b ON ld.BookID = b.BookID
            """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            borrowList.clear();
            while (rs.next()) {
                String loanId = rs.getString("LoanID");
                String bookId = rs.getString("BookID");
                LocalDate loanDate = rs.getDate("LoanDate").toLocalDate();
                LocalDate dueDate = rs.getDate("DueDate").toLocalDate();
                LocalDate returnDate = rs.getDate("ReturnDate") != null ? rs.getDate("ReturnDate").toLocalDate() : null;
                String readerId = rs.getString("ReaderID");
                String isdamaged = rs.getBoolean("isdamaged") ? "Có" : "Không";

                Reader reader = new Reader(readerId);
                Book book = new Book(bookId);
                Borrow borrow = new Borrow(loanId, loanDate, dueDate, returnDate, book, reader,isdamaged);

                borrowList.add(borrow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        tableView.setItems(filteredList);
    }

    private Borrow createBorrow() {
        String loanID =  searchFieldByborrowId.getText().trim();
        String readerID = searchFieldByReaderID.getText().trim();
        String bookID = searchFieldByBookID.getText().trim();
        LocalDate loanDate = LocalDate.parse(searchFieldByborrowDate.getText().trim()); // Đảm bảo rằng ngày đúng định dạng
        LocalDate dueDate = LocalDate.parse(searchFieldBydueDate.getText().trim());
        LocalDate returnDate = searchFieldByreturnDate.getText().trim().isEmpty() ? null : LocalDate.parse(searchFieldByreturnDate.getText().trim());
        String isDamaged = isDamagedComboBox.getValue().trim();

        // Lấy thông tin sách và bạn đọc từ ID
        Book book = new Book(bookID);
        Reader reader = new Reader(readerID);

        // Tạo đối tượng Borrow
        Borrow borrow = new Borrow(loanID, loanDate, dueDate, returnDate, book, reader,isDamaged);

        return borrow;
    }

    private void updateIsDamaged() {
        String isDamagedText = isDamagedComboBox.getValue();

        if (isDamagedText == null || isDamagedText.isEmpty()) {
            System.out.println("Thông tin hư hỏng không hợp lệ");
            return;
        }

        boolean isDamaged = isDamagedText.equals("Có");

        String borrowId = searchFieldByborrowId.getText().trim();
        if (borrowId.isEmpty()) {
            System.out.println("Mã mượn không hợp lệ");
            return;
        }

        String updateIsDamagedSql = """
        UPDATE library.LoanDetail
        SET IsDamaged = ?
        WHERE LoanID = ?;
    """;

        try (PreparedStatement pstmt = connection.prepareStatement(updateIsDamagedSql)) {
            pstmt.setBoolean(1, isDamaged);
            pstmt.setString(2, borrowId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cập nhật tình trạng hư hỏng thành công.");
            } else {
                System.out.println("Không thể cập nhật.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi cập nhật IsDamaged: " + e.getMessage());
        }

        fetchBorrowDataFromDatabase();
    }

    private void updateReturnDates() {
        String returnDateText = searchFieldByreturnDate.getText().trim();

        if (returnDateText.isEmpty()) {
            System.out.println("Ngày trả không hợp lệ");
            return;
        }

        String borrowId = searchFieldByborrowId.getText().trim();
        if (borrowId.isEmpty()) {
            System.out.println("Mã mượn không hợp lệ");
            return;
        }

        String updateReturnDateSql = """
        UPDATE library.LoanDetail
        SET ReturnDate = ?
        WHERE LoanID = ? AND ReturnDate IS NULL;
    """;

        try (PreparedStatement pstmt = connection.prepareStatement(updateReturnDateSql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(returnDateText));
            pstmt.setString(2, borrowId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cập nhật ngày trả thành công.");
            } else {
                System.out.println("Không tìm thấy bản ghi để cập nhật.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi cập nhật ngày trả: " + e.getMessage());
        }

        fetchBorrowDataFromDatabase();
    }

    private void addBorrowToDatabase(Borrow borrow) {
        String cardId = null;

        Random random = new Random();
        int staffIdNumber = random.nextInt(10) + 1;  // Tạo StaffID ngẫu nhiên từ 1 đến 10
        String staffId = String.valueOf(staffIdNumber);

        String checkCardQuery = """
        SELECT lc.CardID
        FROM library.LibraryCard lc
        JOIN library.Reader r ON lc.CardID = r.CardID
        WHERE r.ReaderID = ?;  
    """;

        try (PreparedStatement checkCardStmt = connection.prepareStatement(checkCardQuery)) {
            checkCardStmt.setString(1, borrow.getReader().getReaderId());

            try (ResultSet rs = checkCardStmt.executeQuery()) {
                if (rs.next()) {
                    cardId = rs.getString("CardID");
                } else {
                    System.out.println("Error: Không tìm thấy CardID cho ReaderID: " + borrow.getReader().getReaderId());
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
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
                stmt.setString(1, borrow.getBorrowId());
                stmt.setString(2, cardId);
                stmt.setString(3, staffId);
                stmt.setDate(4, java.sql.Date.valueOf(borrow.getBorrowDate()));

                stmt.setString(5, borrow.getBorrowId());  // LoanID (khớp với bảng Loan)
                stmt.setString(6, borrow.getBook().getBookId());  // BookID từ đối tượng Book
                stmt.setDate(7, java.sql.Date.valueOf(borrow.getDueDate()));  // DueDate (Hạn trả)
                stmt.setDate(8, borrow.getReturnDate() != null ? java.sql.Date.valueOf(borrow.getReturnDate()) : null);

                stmt.executeUpdate();
                System.out.println("Borrow record added for LoanID: " + borrow.getBorrowId());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            borrowList.add(borrow);
            tableView.setItems(null);
            tableView.setItems(borrowList);
        }
    }

    private void deleteBorrows() {
        String searchTermLoanID = searchFieldByborrowId.getText().trim();

        if (searchTermLoanID.isEmpty()) {
            System.out.println("Vui lòng nhập mã mượn để xóa.");
            return;
        }

        String sql = "DELETE FROM library.Loan WHERE LoanID = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, searchTermLoanID);

            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Xóa thành công LoanID: " + searchTermLoanID);

                borrowList.removeIf(borrow -> borrow.getBorrowId().equalsIgnoreCase(searchTermLoanID));

                tableView.setItems(FXCollections.observableArrayList(borrowList));
                tableView.refresh();
            } else {
                System.out.println("Không tìm thấy LoanID để xóa.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi xóa bản ghi: " + e.getMessage());
        }
    }


    private HBox createBorrowTableView() {
        HBox borrowTable = new HBox();
        borrowTable.setTranslateY(-10);
        this.tableView = new TableView<>();
        tableView.prefWidthProperty().bind(borrowTable.widthProperty().multiply(1));

        TableColumn<Borrow, String> colBorrowId = new TableColumn<>("Mã mượn");
        colBorrowId.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        colBorrowId.setStyle("-fx-alignment: center;");
        colBorrowId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        TableColumn<Borrow, String> colReaderId = new TableColumn<>("Mã người mượn");
        colReaderId.setCellValueFactory(new PropertyValueFactory<>("readerId"));
        colReaderId.setStyle("-fx-alignment: center;");
        colReaderId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        TableColumn<Borrow, String> colBookId = new TableColumn<>("Mã sách");
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBookId.setStyle("-fx-alignment: center;");
        colBookId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        TableColumn<Borrow, String> colBorrowDate = new TableColumn<>("Ngày mượn");
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colBorrowDate.setStyle("-fx-alignment: center;");
        colBorrowDate.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        TableColumn<Borrow, String> colReturnDueDate = new TableColumn<>("Hạn trả");
        colReturnDueDate.setCellValueFactory(new PropertyValueFactory<>("returnDueDate"));
        colReturnDueDate.setStyle("-fx-alignment: center;");
        colReturnDueDate.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        TableColumn<Borrow, String> colReturnDate = new TableColumn<>("Ngày trả");
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colReturnDate.setStyle("-fx-alignment: center;");
        colReturnDate.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        TableColumn<Borrow, String> colIsDamaged = new TableColumn<>("Tình trạng hư hỏng");
        colIsDamaged.setCellValueFactory(new PropertyValueFactory<>("condition"));
        colIsDamaged.setStyle("-fx-alignment: center;");
        colIsDamaged.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        tableView.getColumns().addAll(colBorrowId, colReaderId, colBookId, colBorrowDate, colReturnDueDate, colReturnDate,colIsDamaged);

        borrowTable.getChildren().add(tableView);
        borrowTable.setAlignment(Pos.CENTER);
        return borrowTable;
    }

}