package UI;

import DB.DBconnect;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends Scene {
    private TextField txtUsername;
    private PasswordField txtPassword;
    private BorderPane root;

    public Login(Stage primaryStage) {

        super(new BorderPane(), 800, 800);
        root = (BorderPane) getRoot(); // Lấy root từ Scene

        txtUsername = new TextField();
        txtUsername.setMaxWidth(200);

        txtPassword = new PasswordField();
        txtPassword.setMaxWidth(200);

        Button btnLogin = new Button("Đăng nhập");

        root.setCenter(layout(20, setting("Tài khoản", txtUsername), setting("Mật khẩu", txtPassword), btnLogin));

        setBtnStyle(btnLogin);

        btnLogin.setOnAction(e -> {
            if (authenticate(txtUsername.getText(), txtPassword.getText())) {
                primaryStage.setScene(new Menu(primaryStage));
            } else {
                Label lblError = new Label("Sai tài khoản hoặc mật khẩu. Vui lòng thử lại.");
                VBox errorBox = new VBox(lblError);
                errorBox.setAlignment(Pos.CENTER);
                root.setBottom(errorBox);
            }
        });
    }

    public VBox layout(int height, Node node1, Node node2, Node node3) {
        VBox node = new VBox(height);
        node.getChildren().addAll(node1, node2, node3);
        node.setAlignment(Pos.CENTER);
        return node;
    }

    public VBox setting(String label1, Node label2) {
        Label lb1 = new Label(label1);
        VBox vb = new VBox(5, lb1, label2);
        vb.setAlignment(Pos.CENTER);
        return vb;
    }

    private boolean authenticate(String username, String password) {
        String query = "SELECT * FROM library.account WHERE Username = ? AND Password = ?";
        try (Connection connection = DBconnect.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


    // Hàm đặt style cho nút
    private void setBtnStyle(Button btn) {
        btn.setPrefSize(200, 40);
        btn.setStyle(
                "-fx-background-color: linear-gradient(#87CEEB, white);" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0, 2, 2);" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 10 20;"
        );
    }
}
