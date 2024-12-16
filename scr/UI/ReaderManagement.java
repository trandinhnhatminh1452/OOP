package UI;

import DB.DBconnect;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class ReaderManagement extends Scene {
    public TableView<Reader> tableView;
    private ObservableList<Reader> readerList = FXCollections.observableArrayList();
    private Connection connection;
    private TextField searchFieldByID;
    private TextField searchFieldByName;
    private TextField searchFieldByAddress;
    private TextField searchFieldByPhone;
    private BorderPane root;
    private Stage primaryStage;

    public ReaderManagement(Stage primaryStage){
        super(new BorderPane(), 800, 800);
        root = (BorderPane) getRoot();

        this.primaryStage = primaryStage;

        searchFieldByID = new TextField();
        searchFieldByID.setPromptText("Tìm kiếm mã độc giả...");

        searchFieldByName = new TextField();
        searchFieldByName.setPromptText("Tìm kiếm tên độc giả...");


        searchFieldByAddress = new TextField();
        searchFieldByAddress.setPromptText("Tìm kiếm theo địa chỉ...");

        searchFieldByPhone = new TextField();
        searchFieldByPhone.setPromptText("Tìm kiếm theo sdt...");

        root.setLeft(layout(20, setting("ID", searchFieldByID), setting("Tên người mượn", searchFieldByName), setting("Địa chỉ", searchFieldByAddress), setting("Số điện thoại", searchFieldByPhone), 100, 40));

        Button btnSearch = new Button("Tìm Kiếm");
        btnSearch.setOnMouseClicked(e -> {
            searchReaders();
        });

        Button btnAdd = new Button("Thêm");
        btnAdd.setOnMouseClicked(e -> {
            addReaderToDatabase(createReader());
        });

        Button btnDelete = new Button("Xóa");
        btnDelete.setOnMouseClicked(e -> {
            deleteReaders();
        });

        Button btnMenu = new Button("Menu");
        btnMenu.setOnMouseClicked(e -> {
            returnMenu();
        });

        setBtn(btnAdd);
        setBtn(btnDelete);
        setBtn(btnSearch);
        setBtn(btnMenu);
        root.setRight(layout(30, btnAdd, btnDelete,btnSearch, btnMenu, -100, 40));
        root.setBottom(createReaderTableView());

        DBconnect db = new DBconnect();
        this.connection = db.connect();

        fetchReadersFromDatabase();
    }

    private void searchReaders() {
        String searchTermID = searchFieldByID.getText().toLowerCase().trim();
        String searchTermName = searchFieldByName.getText().toLowerCase().trim();
        String searchTermAddress = searchFieldByAddress.getText().toLowerCase().trim();
        String searchTermPhone = searchFieldByPhone.getText().toLowerCase().trim();

        // Lọc danh sách sách dựa trên tất cả các điều kiện tìm kiếm
        ObservableList<Reader> filteredList = FXCollections.observableArrayList();

        for (Reader reader : readerList) {
            boolean matchesID = searchTermID.isEmpty() || reader.getReaderId().toLowerCase().equals(searchTermID);
            boolean matchesName = searchTermName.isEmpty() || reader.getName().toLowerCase().contains(searchTermName);
            boolean matchesAuthor = searchTermAddress.isEmpty() || reader.getAddress().toLowerCase().contains(searchTermAddress);
            boolean matchesCategory = searchTermPhone.isEmpty() || reader.getPhoneNumber().toLowerCase().equals(searchTermPhone);

            // Chỉ thêm sách vào filteredList nếu nó khớp với tất cả các điều kiện tìm kiếm
            if (matchesID && matchesName && matchesAuthor && matchesCategory) {
                filteredList.add(reader);
            }
        }

        // Cập nhật TableView với danh sách đã lọc
        tableView.setItems(filteredList);
    }

    private void deleteReaders() {
        String searchTermID = searchFieldByID.getText().trim();       // Lấy giá trị ID từ trường tìm kiếm
        String searchTermName = searchFieldByName.getText().trim();   // Lấy giá trị tên từ trường tìm kiếm
        String searchTermAddress = searchFieldByAddress.getText().trim(); // Lấy giá trị địa chỉ từ trường tìm kiếm
        String searchTermPhone = searchFieldByPhone.getText().trim(); // Lấy giá trị số điện thoại từ trường tìm kiếm

        // Điều kiện xóa trong danh sách độc giả
        readerList.removeIf(reader ->
                (searchTermID.isEmpty() || reader.getReaderId().equalsIgnoreCase(searchTermID)) &&
                        (searchTermName.isEmpty() || reader.getName().equalsIgnoreCase(searchTermName)) &&
                        (searchTermAddress.isEmpty() || reader.getAddress().equalsIgnoreCase(searchTermAddress)) &&
                        (searchTermPhone.isEmpty() || reader.getPhoneNumber().equalsIgnoreCase(searchTermPhone))
        );

        // Xóa trong cơ sở dữ liệu
        String sql = "DELETE FROM library.reader WHERE " +
                "(? = '' OR readerid = ?) AND " +
                "(? = '' OR readername = ?) AND " +
                "(? = '' OR Address = ?) AND " +
                "(? = '' OR phonenumber = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, searchTermID);
            stmt.setString(2, searchTermID);
            stmt.setString(3, searchTermName);
            stmt.setString(4, searchTermName);
            stmt.setString(5, searchTermAddress);
            stmt.setString(6, searchTermAddress);
            stmt.setString(7, searchTermPhone);
            stmt.setString(8, searchTermPhone);

            int rowsDeleted = stmt.executeUpdate();
            System.out.println("Số bản ghi đã xóa: " + rowsDeleted);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Làm mới TableView
        tableView.setItems(null);
        tableView.setItems(readerList);
    }


    public VBox setting(String label1, Node label2) {
        Label lb1 = new Label(label1);
        VBox vb = new VBox(0, lb1, label2);
        return vb;
    }

    public VBox layout(int height, Node node1, Node node2, Node node3, Node node4, int x, int y) {
        VBox node = new VBox(height);
        node.getChildren().addAll(node1, node2, node3, node4);
        node.setTranslateX(x);
        node.setTranslateY(y);
        return node;
    }



    public void setBtn(Button btn) {
        btn.setPrefSize(80, 30);
        btn.setStyle("-fx-text-fill: black; -fx-font-size: 10px;");
    }

    private void fetchReadersFromDatabase() {
        // Câu lệnh SQL để lấy thông tin độc giả từ cơ sở dữ liệu
        String sql = "SELECT readerid, readername, Address, phonenumber FROM library.reader";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Xóa dữ liệu cũ trong danh sách readerList
            readerList.clear();

            // Lặp qua từng dòng dữ liệu từ kết quả truy vấn
            while (rs.next()) {
                // Lấy dữ liệu từ mỗi cột
                String readerId = rs.getString("readerid");
                String readerName = rs.getString("readername");
                String address = rs.getString("address");
                String phoneNumber = rs.getString("phonenumber");

                // Tạo đối tượng Reader từ dữ liệu
                Reader reader = new Reader(readerId, readerName, address, phoneNumber);

                // Thêm đối tượng Reader vào danh sách readerList
                readerList.add(reader);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Cập nhật TableView với danh sách mới
        tableView.setItems(readerList);
    }

    private Reader createReader() {
        String searchTermID = searchFieldByID.getText().trim();       // Lấy giá trị ID từ trường tìm kiếm
        String searchTermName = searchFieldByName.getText().trim();   // Lấy giá trị tên từ trường tìm kiếm
        String searchTermAddress = searchFieldByAddress.getText().trim(); // Lấy giá trị địa chỉ từ trường tìm kiếm
        String searchTermPhone = searchFieldByPhone.getText().trim(); // Lấy giá trị số điện thoại từ trường tìm kiếm

        // Tạo đối tượng Reader mới với các thông tin nhập vào
        return new Reader(searchTermID, searchTermName, searchTermAddress, searchTermPhone);
    }


    private void addReaderToDatabase(Reader reader) {
        String sql = "INSERT INTO library.reader(readerid, readername, Address, phonenumber) " +
                "VALUES (?, ?, ?, ?);";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Set giá trị cho các tham số
            stmt.setString(1, reader.getReaderId());
            stmt.setString(2, reader.getName());
            stmt.setString(3, reader.getAddress());
            stmt.setString(4, reader.getPhoneNumber());

            // Thực thi câu lệnh INSERT
            stmt.executeUpdate();
            System.out.println("Reader added: " + reader.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Thêm đối tượng Reader vào danh sách readerList
        readerList.add(reader);

        // Làm mới TableView
        tableView.setItems(null);
        tableView.setItems(readerList);
    }



    public void returnMenu() {
        try {
            // Tạo một Scene mới cho Demo
            Scene demoScene = new Menu(primaryStage);

            // Cập nhật Scene của Stage
            primaryStage.setScene(demoScene);
        } catch (Exception e) {
            e.printStackTrace(); // In thông báo lỗi nếu xảy ra ngoại lệ
        }
    }

    private HBox createReaderTableView() {
        HBox readerTable = new HBox();
        readerTable.setTranslateY(-40);
        this.tableView = new TableView<>();
        tableView.prefWidthProperty().bind(readerTable.widthProperty().multiply(0.8));

        // Tạo cột ID
        TableColumn<Reader, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("readerId")); // Thuộc tính "id" trong lớp Reader
        colId.setStyle("-fx-alignment: center;");
        colId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        // Tạo cột Tên người mượn
        TableColumn<Reader, String> colName = new TableColumn<>("Tên người mượn");
        colName.setCellValueFactory(new PropertyValueFactory<>("name")); // Thuộc tính "name" trong lớp Reader
        colName.setStyle("-fx-alignment: center;");
        colName.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));

        // Tạo cột Địa chỉ
        TableColumn<Reader, String> colAddress = new TableColumn<>("Địa chỉ");
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address")); // Thuộc tính "address" trong lớp Reader
        colAddress.setStyle("-fx-alignment: center;");
        colAddress.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));

        // Tạo cột Số điện thoại
        TableColumn<Reader, String> colPhone = new TableColumn<>("Số điện thoại");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber")); // Thuộc tính "phone" trong lớp Reader
        colPhone.setStyle("-fx-alignment: center;");
        colPhone.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));

        // Thêm tất cả các cột vào TableView
        tableView.getColumns().addAll(colId, colName, colAddress, colPhone);

        // Trả về TableView
        readerTable.getChildren().add(tableView);
        readerTable.setAlignment(Pos.CENTER);
        return readerTable;
    }

}
