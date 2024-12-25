package UI;

import DB.DBconnect;
import code.Book;
import code.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import java.sql.*;
import java.time.Year;

public class BookManagement extends BaseUI {
    public TableView<Book> tableView;
    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private Connection connection;
    private TextField searchFieldByID;
    private TextField searchFieldByName;
    private TextField searchFieldByAuthor;
    private TextField searchFieldByCategory;
    private TextField searchFieldByQuantity;
    private TextField searchFieldByPublisher;
    private BorderPane root;
    public BookManagement(Stage primaryStage) {
        super(primaryStage);
        root = (BorderPane) getRoot();
        this.primaryStage = primaryStage;

        searchFieldByID = new TextField();
        searchFieldByID.setPromptText("Tìm kiếm theo mã sách...");

        searchFieldByName = new TextField();
        searchFieldByName.setPromptText("Tìm kiếm theo tên sách...");


        searchFieldByAuthor = new TextField();
        searchFieldByAuthor.setPromptText("Tìm kiếm theo tác giả...");

        searchFieldByCategory = new TextField();
        searchFieldByCategory.setPromptText("Tìm kiếm theo thể loại...");

        searchFieldByQuantity = new TextField();
        searchFieldByQuantity.setPromptText("Tìm kiếm theo số lượng...");

        searchFieldByPublisher = new TextField();
        searchFieldByPublisher.setPromptText("Tìm kiếm theo NXB...");

        root.setTop(
                layout2(
                        20,
                        layout3(20,
                                setting("Mã Sách", searchFieldByID),
                                setting("Tên sách", searchFieldByName),
                                setting("Tác giả", searchFieldByAuthor)
                        ),
                        layout3(20,
                                setting("Nhà xuất bản", searchFieldByPublisher),
                                setting("Số lượng", searchFieldByQuantity),
                                setting("Thể loại", searchFieldByCategory)
                        ),
                        30)
        );

        Button btnSearch = new Button("Tìm Kiếm");
        btnSearch.setOnMouseClicked(e -> {
            searchBooks();
        });

        Button btnAdd = new Button("Thêm");
        btnAdd.setOnMouseClicked(e -> {
            addBookToDatabase(creatBook());
        });

        Button btnDelete = new Button("Xóa");
        btnDelete.setOnMouseClicked(e -> {
            deleteBooks();
        });

        Button btnMenu = new Button("Menu");
        btnMenu.setOnMouseClicked(e -> {
            returnMenu();
        });

        setBtn(btnAdd);
        setBtn(btnDelete);
        setBtn(btnSearch);
        setBtn(btnMenu);
        root.setCenter(layout1(30, btnAdd, btnDelete,btnSearch, btnMenu));
        root.setBottom(createTableView());

        // Open database connection
        DBconnect db = new DBconnect();
        this.connection = db.connect();
        // Fetch books from the database
        fetchBooksFromDatabase();

    }

    public HBox layout1(int height, Node node1, Node node2, Node node3, Node node4) {
        HBox node = new HBox(height);
        node.getChildren().addAll(node1, node2, node3, node4);
        node.setAlignment(Pos.CENTER);  // Set the alignment to center
        node.setTranslateY(20);
        return node;
    }

    private void searchBooks() {
        String searchTermID = searchFieldByID.getText().toLowerCase().trim();
        String searchTermName = searchFieldByName.getText().toLowerCase().trim();
        String searchTermAuthor = searchFieldByAuthor.getText().toLowerCase().trim();
        String searchTermCategory = searchFieldByCategory.getText().toLowerCase().trim();
        String searchTermQuantity = searchFieldByQuantity.getText().trim();
        String searchTermPublisher = searchFieldByPublisher.getText().toLowerCase().trim();

        // Lọc danh sách sách dựa trên tất cả các điều kiện tìm kiếm
        ObservableList<Book> filteredList = FXCollections.observableArrayList();

        for (Book book : bookList) {
            boolean matchesID = searchTermID.isEmpty() || book.getBookId().toLowerCase().equals(searchTermID);
            boolean matchesName = searchTermName.isEmpty() || book.getTitle().toLowerCase().contains(searchTermName);
            boolean matchesAuthor = searchTermAuthor.isEmpty() || book.getAuthor().toLowerCase().contains(searchTermAuthor);
            boolean matchesCategory = searchTermCategory.isEmpty() || book.getCategory().getCategoryName().toLowerCase().equals(searchTermCategory);
            boolean matchesQuantity = searchTermQuantity.isEmpty() || String.valueOf(book.getQuantity()).equals(searchTermQuantity);
            boolean matchesPublisher = searchTermPublisher.isEmpty() || book.getPublisher().toLowerCase().contains(searchTermPublisher);

            // Chỉ thêm sách vào filteredList nếu nó khớp với tất cả các điều kiện tìm kiếm
            if (matchesID && matchesName && matchesAuthor && matchesCategory && matchesQuantity && matchesPublisher) {
                filteredList.add(book);
            }
        }

        // Cập nhật TableView với danh sách đã lọc
        tableView.setItems(filteredList);
    }


    private Book creatBook(){
        String searchTermID = searchFieldByID.getText().trim();
        String searchTermName = searchFieldByName.getText().trim();
        String searchTermAuthor = searchFieldByAuthor.getText().trim();
        String searchTermCategory = searchFieldByCategory.getText().trim();
        Category category = new Category("", searchTermCategory);
        return new Book(searchTermID,searchTermName,searchTermAuthor,"100",100,category);
    }


    private void fetchBooksFromDatabase() {
        String sql = "SELECT b.bookid, b.bookname,b.quantity, b.authorid, a.authorname, b.publisherid, p.publishername, b.publishedyear, " +
                "c.categoryid, c.categoryname " +
                "FROM library.book b " +
                "JOIN library.category c ON b.categoryid = c.categoryid " +
                "JOIN library.author a ON b.authorid = a.authorid " +
                "JOIN library.publisher p ON b.publisherid = p.publisherid";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String bookId = rs.getString("bookid");
                String bookName = rs.getString("bookname");
                String authorName = rs.getString("authorname"); // Lấy tên tác giả
                String publisherName = rs.getString("publishername"); // Lấy tên nhà xuất bản
                int quantity = rs.getInt("quantity");
                String categoryId = rs.getString("categoryid");
                String categoryName = rs.getString("categoryname");

                Category category = new Category(categoryId, categoryName);
                Book book = new Book(bookId, bookName, authorName, publisherName, quantity, category);


                addBookData(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        root.setBottom(createTableView());
    }

    public void addBookData(Book book) {
        bookList.add(book);
    }


    private void addBookToDatabase(Book book) {
        int currentYear = Year.now().getValue();
        String checkAuthorSql = "SELECT authorid FROM library.author WHERE authorname = ?";
        String insertAuthorSql = "INSERT INTO library.author (authorname) VALUES (?)";
        String insertBookSql = "INSERT INTO library.book (bookid, bookname, categoryid, authorid, publisherid, quantity,publishedyear) " +
                "VALUES (?, ?, (SELECT categoryid FROM library.category WHERE categoryname = ?), ?, ?, ?, ?)";

        try {
            // 1. Kiểm tra xem tác giả đã tồn tại chưa
            String authorId = null;
            try (PreparedStatement checkAuthorStmt = connection.prepareStatement(checkAuthorSql)) {
                checkAuthorStmt.setString(1, book.getAuthor());
                ResultSet rs = checkAuthorStmt.executeQuery();

                if (rs.next()) {
                    // Tác giả đã tồn tại, lấy authorid
                    authorId = rs.getString("authorid");
                } else {
                    // Tác giả chưa tồn tại, thêm tác giả mới và lấy authorid
                    try (PreparedStatement insertAuthorStmt = connection.prepareStatement(insertAuthorSql, Statement.RETURN_GENERATED_KEYS)) {
                        insertAuthorStmt.setString(1, book.getAuthor());
                        insertAuthorStmt.executeUpdate();

                        // Lấy ID của tác giả mới
                        ResultSet generatedKeys = insertAuthorStmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            authorId = generatedKeys.getString(1);
                        }
                    }
                }
            }

            // 2. Thêm sách vào bảng book
            try (PreparedStatement insertBookStmt = connection.prepareStatement(insertBookSql)) {
                insertBookStmt.setString(1, book.getBookId());
                insertBookStmt.setString(2, book.getTitle());
                insertBookStmt.setString(3, book.getCategory().getCategoryName());
                insertBookStmt.setString(4, authorId);
                insertBookStmt.setString(5, book.getPublisher());
                insertBookStmt.setInt(6, book.getQuantity());
                insertBookStmt.setInt(7, currentYear);
                insertBookStmt.executeUpdate();
                System.out.println("Book added: " + book.getTitle());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Cập nhật danh sách sách trong TableView
        bookList.add(book);
        tableView.setItems(null);
        tableView.setItems(bookList);
    }

    private void deleteBooks() {
        String searchTermID = searchFieldByID.getText().trim();
        String searchTermName = searchFieldByName.getText().trim();
        String searchTermAuthor = searchFieldByAuthor.getText().trim();
        String searchTermCategory = searchFieldByCategory.getText().trim();

        //Xóa trong bookList
        bookList.removeIf(book ->
                (searchTermID.isEmpty() || book.getBookId().equalsIgnoreCase(searchTermID)) &&
                        (searchTermName.isEmpty() || book.getTitle().equalsIgnoreCase(searchTermName)) &&
                        (searchTermAuthor.isEmpty() || book.getAuthor().equalsIgnoreCase(searchTermAuthor)) &&
                        (searchTermCategory.isEmpty() || book.getCategory().getCategoryName().equalsIgnoreCase(searchTermCategory))
        );

        // Xóa trong cơ sở dữ liệu
        String sql = "DELETE FROM library.book WHERE " +
                "(? = '' OR bookid = ?) AND " +
                "(? = '' OR bookname = ?) AND " +
                "(? = '' OR authorid = (SELECT authorid FROM library.author WHERE authorname = ?)) AND " +
                "(? = '' OR categoryid = (SELECT categoryid FROM library.category WHERE categoryname = ?))";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, searchTermID);
            stmt.setString(2, searchTermID);
            stmt.setString(3, searchTermName);
            stmt.setString(4, searchTermName);
            stmt.setString(5, searchTermAuthor);
            stmt.setString(6, searchTermAuthor);
            stmt.setString(7, searchTermCategory);
            stmt.setString(8, searchTermCategory);

            int rowsDeleted = stmt.executeUpdate();
            System.out.println("Số bản ghi đã xóa: " + rowsDeleted);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Cập nhật TableView
        tableView.setItems(null);
        tableView.setItems(bookList);
    }


    private HBox createTableView() {
        HBox hbox2 = new HBox();
        hbox2.setTranslateY(-40);
        this.tableView = new TableView<>();
        tableView.prefWidthProperty().bind(hbox2.widthProperty().multiply(0.8));

        TableColumn<Book, String> colBookId = new TableColumn<>("Mã sách");
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBookId.setStyle("-fx-alignment: center;");
        colBookId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        TableColumn<Book, String> colTitle = new TableColumn<>("Tên sách");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTitle.setStyle("-fx-alignment: center;");
        colTitle.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        TableColumn<Book, String> colAuthor = new TableColumn<>("Tác giả");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colAuthor.setStyle("-fx-alignment: center;");
        colAuthor.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        TableColumn<Book, String> colPublisher = new TableColumn<>("Nhà xuất bản");
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colPublisher.setStyle("-fx-alignment: center;");
        colPublisher.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        TableColumn<Book, Integer> colQuantity = new TableColumn<>("Số lượng");
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQuantity.setStyle("-fx-alignment: center;");
        colQuantity.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        TableColumn<Book, String> colCategory = new TableColumn<>("Thể loại");
        colCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().getCategoryName()));
        colCategory.setStyle("-fx-alignment: center;");
        colCategory.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        tableView.getColumns().addAll(colBookId, colTitle, colAuthor, colPublisher, colQuantity, colCategory);

        tableView.setItems(bookList);

        hbox2.getChildren().add(tableView);
        hbox2.setAlignment(Pos.CENTER);
        return hbox2;
    }
}
