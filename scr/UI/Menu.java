package UI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Menu extends Scene {

    public Menu(Stage primaryStage) {
        super(new VBox(20),800,800);
        Button btn1 = new Button("Quản Lý Sách");
        Button btn2 = new Button("Quản Lý Người Mượn");
        Button btn3 = new Button("Quản lý Phiếu Mượn");
        Button btn4 = new Button("Thống Kê");

        VBox root = (VBox)getRoot();
        root.getChildren().addAll(btn1,btn2,btn3,btn4);
        root.setAlignment(Pos.CENTER);
        setBtn(btn1);
        setBtn(btn2);
        setBtn(btn3);
        setBtn(btn4);

        btn1.setOnMouseClicked(e->{
            primaryStage.setScene(new BookManagement(primaryStage));
        });

        btn2.setOnMouseClicked(e->{
            primaryStage.setScene(new ReaderManagement(primaryStage));
        });

        btn3.setOnMouseClicked(e->{
            primaryStage.setScene(new BorrowManagement(primaryStage));
        });

        btn4.setOnMouseClicked(e->{
            primaryStage.setScene(new Statistic(primaryStage));
        });
    }

    public void setBtn(Button btn){
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
