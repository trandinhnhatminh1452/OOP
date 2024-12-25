package Function;

import DB.DBconnect;
import code.Reader;
import javafx.collections.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class ReaderManagement extends BaseUI {
    public TableView<Reader> tableView;
    private ObservableList<Reader> readerList = FXCollections.observableArrayList();
    private Connection connection;
    private TextField searchFieldByID;
    private TextField searchFieldByName;
    private TextField searchFieldByAddress;
    private TextField searchFieldByPhone;
    private BorderPane root;

    public ReaderManagement(Stage primaryStage){
        super(primaryStage);
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

        root.setLeft(layout1(20, setting("ID", searchFieldByID), setting("Tên người mượn", searchFieldByName),
                    setting("Địa chỉ", searchFieldByAddress), setting("Số điện thoại", searchFieldByPhone), 100, 40));

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
        root.setRight(layout1(30, btnAdd, btnDelete,btnSearch, btnMenu, -100, 40));
        root.setBottom(createReaderTableView());

        DBconnect db = new DBconnect();
        this.connection = db.connect();

        fetchReadersFromDatabase();
    }

    public VBox layout1(int height, Node node1, Node node2, Node node3, Node node4, int x, int y) {
        VBox node = new VBox(height);
        node.getChildren().addAll(node1, node2, node3, node4);
        node.setTranslateX(x);
        node.setTranslateY(y);
        return node;
    }

    private void searchReaders() {
        String searchTermID = searchFieldByID.getText().toLowerCase().trim();
        String searchTermName = searchFieldByName.getText().toLowerCase().trim();
        String searchTermAddress = searchFieldByAddress.getText().toLowerCase().trim();
        String searchTermPhone = searchFieldByPhone.getText().toLowerCase().trim();

        ObservableList<Reader> filteredList = FXCollections.observableArrayList();

        for (Reader reader : readerList) {
            boolean matchesID = searchTermID.isEmpty() || reader.getReaderId().toLowerCase().equals(searchTermID);
            boolean matchesName = searchTermName.isEmpty() || reader.getName().toLowerCase().contains(searchTermName);
            boolean matchesAddress = searchTermAddress.isEmpty() || reader.getAddress().toLowerCase().contains(searchTermAddress);
            boolean matchesPhone = searchTermPhone.isEmpty() || reader.getPhoneNumber().toLowerCase().equals(searchTermPhone);

            if (matchesID && matchesName && matchesAddress && matchesPhone) {
                filteredList.add(reader);
            }
        }

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

    private void fetchReadersFromDatabase() {

        String sql = "SELECT readerid, readername, Address, phonenumber FROM library.reader";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Xóa dữ liệu cũ trong danh sách readerList
            readerList.clear();

            while (rs.next()) {
                String readerId = rs.getString("readerid");
                String readerName = rs.getString("readername");
                String address = rs.getString("address");
                String phoneNumber = rs.getString("phonenumber");

                Reader reader = new Reader(readerId, readerName, address, phoneNumber);

                readerList.add(reader);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

        readerList.add(reader);
        tableView.setItems(null);
        tableView.setItems(readerList);
    }

    private HBox createReaderTableView() {
        HBox readerTable = new HBox();
        readerTable.setTranslateY(-40);
        this.tableView = new TableView<>();
        tableView.prefWidthProperty().bind(readerTable.widthProperty().multiply(0.8));

        TableColumn<Reader, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("readerId"));
        colId.setStyle("-fx-alignment: center;");
        colId.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        TableColumn<Reader, String> colName = new TableColumn<>("Tên người mượn");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setStyle("-fx-alignment: center;");
        colName.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));

        TableColumn<Reader, String> colAddress = new TableColumn<>("Địa chỉ");
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colAddress.setStyle("-fx-alignment: center;");
        colAddress.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));

        TableColumn<Reader, String> colPhone = new TableColumn<>("Số điện thoại");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colPhone.setStyle("-fx-alignment: center;");
        colPhone.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));

        tableView.getColumns().addAll(colId, colName, colAddress, colPhone);

        readerTable.getChildren().add(tableView);
        readerTable.setAlignment(Pos.CENTER);
        return readerTable;
    }

}
