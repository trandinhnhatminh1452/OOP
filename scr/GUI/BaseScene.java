package GUI;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public abstract class BaseScene extends Scene {
    protected Stage primaryStage;

    public BaseScene(Stage primaryStage, double width, double height) {
        super(new javafx.scene.layout.Pane(), width, height);
        this.primaryStage = primaryStage;
    }
    public abstract void setupUI();

    public void setBtnStyle(Button btn, double width, double height) {
        btn.setPrefSize(width, height); // Điều chỉnh kích thước nút
        btn.setStyle(
                "-fx-background-color: linear-gradient(#87CEEB, white);" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0, 2, 2);" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 18px;" +
                        "-fx-padding: 10 20;"
        );
    }

}
