package UI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Menu extends Scene {

    public Menu(Stage primaryStage) {
        super(new VBox(20),800,800);
        Label title = new Label("Hệ Thống Quản Lý Thư Viện");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Button btn1 = new Button("Quản Lý Sách");
        Button btn2 = new Button("Quản Lý Người Mượn");
        Button btn3 = new Button("Quản lý Phiếu Mượn");
        Button btn4 = new Button("Thống Kê");

        VBox root = (VBox)getRoot();
        root.getChildren().addAll(title,btn1,btn2,btn3,btn4);
        root.setAlignment(Pos.CENTER);
        setBtnMenu(btn1);
        setBtnMenu(btn2);
        setBtnMenu(btn3);
        setBtnMenu(btn4);

        btn1.setOnMouseClicked(e->{
            primaryStage.setScene(new BookManagement(primaryStage));
            primaryStage.setTitle("Quản lý sách");
        });

        btn2.setOnMouseClicked(e->{
            primaryStage.setScene(new ReaderManagement(primaryStage));
            primaryStage.setTitle("Quản lý người mượn");
        });

        btn3.setOnMouseClicked(e->{
            primaryStage.setScene(new BorrowManagement(primaryStage));
            primaryStage.setTitle("Quản lý phiếu mượn");
        });

        btn4.setOnMouseClicked(e->{
            primaryStage.setScene(new Statistic(primaryStage));
            primaryStage.setTitle("Thống kê");
        });
    }

    public void setBtnMenu(Button btn){
        btn.setPrefSize(300,80);
        btn.setStyle(
                "-fx-background-color: linear-gradient(#87CEEB, white);" + // Gradient nền
                        "-fx-background-radius: 10;" +                             // Góc bo tròn
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0, 2, 2);" + // Hiệu ứng đổ bóng
                        "-fx-text-fill: black;" +                                  // Màu chữ
                        "-fx-font-size: 18px;" +                                   // Kích thước chữ
                        "-fx-padding: 10 20;"                                      // Khoảng cách padding
        );
    }
}
