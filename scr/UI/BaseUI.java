package UI;

import DB.DBconnect;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class BaseUI extends Scene {
    protected Stage primaryStage;
    protected Connection connection;


    public BaseUI( Stage primaryStage) {
        super(new BorderPane(), 800, 800);
        this.primaryStage = primaryStage;
    }


    public VBox setting(String label1, Node label2) {
        Label lb1 = new Label(label1);
        VBox vb = new VBox(5, lb1, label2); // Khoảng cách giữa các thành phần là 5px
        return vb;
    }

    // Phương thức returnMenu để quay trở lại Menu chính
    public void returnMenu() {
        try {
            // Tạo một Scene mới cho Menu
            Scene menuScene = new Menu(primaryStage);

            // Cập nhật Scene của Stage
            primaryStage.setScene(menuScene);
        } catch (Exception e) {
            e.printStackTrace(); // In thông báo lỗi nếu xảy ra ngoại lệ
        }
    }

    public void setBtn(Button btn) {
        btn.setPrefSize(80, 30);
        btn.setStyle("-fx-text-fill: black; -fx-font-size: 10px;");
    }

    public HBox layout2(int height, Node node1, Node node2, int y) {
        HBox node = new HBox(height);
        node.setSpacing(200);
        node.getChildren().addAll(node1, node2);
        node.setTranslateY(y);
        node.setAlignment(Pos.CENTER);
        return node;
    }

    public VBox layout3(int height, Node node1, Node node2, Node node3) {
        VBox node = new VBox(height);
        node.getChildren().addAll(node1, node2, node3);
        node.setAlignment(Pos.CENTER);
        return node;
    }


}
