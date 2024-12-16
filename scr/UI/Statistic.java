package UI;

import DB.DBconnect;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Statistic extends Scene {
    private Connection connection;
    private BorderPane root;
    private Stage primaryStage;
    public Statistic(Stage primaryStage){
        super(new BorderPane(), 800, 800);
        root = (BorderPane) getRoot();

        this.primaryStage = primaryStage;

        DBconnect db = new DBconnect();
        this.connection = db.connect();

        int totalBorrowers = countTotalBorrowers();
        int totalBooksBorrowed = countTotalBooksBorrowed();
        int overdueLoans = countOverdueLoans();
        int unreturnedBooks = countUnreturnedBooks();
        int totalBooks = countTotalBooksInStock();


        root.setTop(
                layout2(
                        20,
                        layout3(20,
                                new Label("Số người mượn: " + totalBorrowers),
                                new Label("Tổng số sách: " + totalBooks),
                                new Label("Số phiếu mượn: " + totalBooksBorrowed)
                        ),
                        layout3(20,
                                new Label("Số sách hỏng: " + totalBooksBorrowed),
                                new Label("Số phiếu trả quá hạn: " + overdueLoans),
                                new Label("Số phiếu chưa trả sách: " + unreturnedBooks)
                        ),
                        30)
        );

        Button btnMenu = new Button("Menu");
        btnMenu.setOnMouseClicked(e -> {
            returnMenu();
        });


        setBtn(btnMenu);
        root.setBottom(btnMenu);


    }

    public HBox layout2(int height, Node node1, Node node2, int y) {
        HBox node = new HBox(height);
        node.setSpacing(200);
        node.getChildren().addAll(node1, node2);
        node.setTranslateY(y);
        node.setAlignment(Pos.CENTER);
        return node;
    }

    public VBox layout3(int height, Label label1, Label label2, Node node3) {
        VBox node = new VBox(height);
        node.getChildren().addAll(label1, label2, node3);
        node.setAlignment(Pos.CENTER);
        return node;
    }


    public void setBtn(Button btn) {
        btn.setPrefSize(80, 30);
        btn.setStyle("-fx-text-fill: black; -fx-font-size: 10px;");
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

    private int countTotalBorrowers() {
        String sql = "SELECT COUNT(DISTINCT r.ReaderID) AS TotalBorrowers FROM library.Loan l " +
                "JOIN library.LibraryCard lc ON l.CardID = lc.CardID " +
                "JOIN library.Reader r ON lc.CardID = r.cardID";

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



}
