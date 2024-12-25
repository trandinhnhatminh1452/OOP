package GUI;

import Function.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Menu extends BaseScene {
    public Menu(Stage primaryStage) {
        super(primaryStage, 800, 800);
        setupUI();
    }

    @Override
    public void setupUI() {
        Label title = new Label("Hệ Thống Quản Lý Thư Viện");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button btn1 = new Button("Quản Lý Sách");
        Button btn2 = new Button("Quản Lý Người Mượn");
        Button btn3 = new Button("Quản lý Phiếu Mượn");
        Button btn4 = new Button("Thống Kê");

        VBox root = new VBox(20, title, btn1, btn2, btn3, btn4);
        root.setAlignment(Pos.CENTER);
        this.setRoot(root);

        setBtnStyle(btn1, 300, 80);
        setBtnStyle(btn2, 300, 80);
        setBtnStyle(btn3, 300, 80);
        setBtnStyle(btn4, 300, 80);


        btn1.setOnAction(e -> {
            primaryStage.setScene(new BookManagement(primaryStage));
            primaryStage.setTitle("Quản Lý Sách");
        });
        btn2.setOnAction(e -> {
            primaryStage.setScene(new ReaderManagement(primaryStage));
            primaryStage.setTitle("Quản Lý Người Mượn");
        });
        btn3.setOnAction(e -> {
            primaryStage.setScene(new BorrowManagement(primaryStage));
            primaryStage.setTitle("Quản Lý Phiếu Mượn");
        });
        btn4.setOnAction(e -> {
            primaryStage.setScene(new Statistic(primaryStage));
            primaryStage.setTitle("Thống Kê");
        });
    }

}
