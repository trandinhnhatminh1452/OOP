module scr {
    requires javafx.controls;  // Dành cho các control cơ bản như Button, Label, TextField
    requires javafx.fxml;      // Dành cho việc sử dụng FXML để xây dựng giao diện
    requires javafx.graphics;  // Dành cho các chức năng vẽ giao diện
    requires java.base;        // Được tự động thêm, nhưng bạn có thể khai báo rõ ràng
    requires java.sql;
    opens code to javafx.base;
    exports GUI;
    exports Function;
    exports code;
}
