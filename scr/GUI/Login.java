package GUI;

import DB.DBconnect;
import javafx.geometry.Pos;
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

public class Login extends BaseScene {
    private TextField txtUsername;
    private PasswordField txtPassword;

    public Login(Stage primaryStage) {
        super(primaryStage, 800, 800);
        setupUI();
    }
    @Override
    public void setupUI() {
        txtUsername = new TextField();
        txtUsername.setMaxWidth(200);
        txtPassword = new PasswordField();
        txtPassword.setMaxWidth(200);
        Button btnLogin = new Button("Đăng nhập");
        VBox form = new VBox(20, setting("Tài khoản", txtUsername), setting("Mật khẩu", txtPassword), btnLogin);
        form.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(form);
        this.setRoot(root);

        setBtnStyle(btnLogin, 200, 40);

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

    private VBox setting(String labelText, TextField field) {
        Label label = new Label(labelText);
        VBox vb = new VBox(5, label, field);
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

}
