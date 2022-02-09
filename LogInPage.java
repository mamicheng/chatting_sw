package Code;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.stage.StageStyle;

import java.sql.*;

public class LogInPage extends Application {
    static  double mouse_x;//鼠标x轴坐标
    static  double mouse_y;//鼠标y坐标
    static int UID;
    static Statement sql;
    static String userName;
    public static boolean compareInfo(TextField tf, PasswordField pwf){
        String username = tf.getText()
             , password = pwf.getText();
        try{
            ResultSet rs = sql.executeQuery("SELECT *from UserInfo WHERE UserName='" + username + "' AND Password = '" + password + "';");

            if(rs.next()){
                UID = rs.getInt("UID");
                userName = rs.getString("UserName");
                return true;
            }
        }catch (SQLException e) {
            System.out.println(e);
            System.out.println("系统出错，请重新操作");
        }
        return false;
    }

    public void start(Stage primaryStage) {
        try {
        Class.forName("com.hxtt.sql.access.AccessDriver");
    } catch (ClassNotFoundException e) {
        System.out.println("加载桥连接器驱动失败！");
    }

        // 连接到数据库
        try {
            Connection con = DriverManager.getConnection("jdbc:Access://./QQ.mdb", "", "");
            // 向数据库发送SQL语句
            sql = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            primaryStage.initStyle(StageStyle.TRANSPARENT);//标题栏透明
            logIn(primaryStage);
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("系统出错，请重新操作");
        }

    }

    public static void logIn(Stage primaryStage) {
        try{
            AnchorPane root = new AnchorPane();//根节点
            Scene scene = new Scene(root,430,356);
            primaryStage.setScene(scene);//窗体显示样式设定
            primaryStage.setResizable(false);//设置窗口不可改变

            ImageCursor cursor = new ImageCursor(new Image("/images/haoye.png"));//好耶

            root.setOnMousePressed(event -> {
                mouse_x = event.getScreenX()-primaryStage.getX();
                mouse_y = event.getScreenY()-primaryStage.getY();
                root.setCursor(cursor);
            });//获取当前鼠标坐标
            root.setOnMouseDragged(event -> {
                primaryStage.setX(event.getScreenX()-mouse_x);
                primaryStage.setY(event.getScreenY()-mouse_y);
                root.setCursor(Cursor.DEFAULT);
            });//拖拽事件产生后改变当前窗体坐标

            Label l1 = new Label("注册账号")
                , l2 = new Label("账号不存在或密码错误");
            l2.setTextFill(Color.RED);
            l2.setVisible(false);
            TextField tf = new TextField();
            tf.setPromptText("用户名");
            PasswordField pwf = new PasswordField();
            pwf.setPromptText("密码");
            Button b1 = new Button("登录")
                 , b2 = new Button()
                 , b3 = new Button();
            b2.setId("b2");
            b3.setId("b3");

            Image img3 = new Image("/images/LogInBG.gif")
                , img4 = new Image("/images/User.png")
                , img5 = new Image("/images/Key.png");
            ImageView imv3 = new ImageView()
                    , imv4 = new ImageView()
                    , imv5 = new ImageView();
            imv3.setImage(img3);
            imv4.setImage(img4);
            imv5.setImage(img5);

            b1.setTextFill(Color.WHITE);
            b1.setStyle("-fx-background-color: rgba(7,189,253);");
            b2.setStyle("-fx-background-image:url(/images/CloseW.png);");
            b3.setStyle("-fx-background-image:url(/images/Minimized.png);");

            b1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    boolean isLogIn = compareInfo(tf,pwf);
                    if(isLogIn) {
                        l2.setVisible(false);
                        primaryStage.close();
                        Platform.setImplicitExit(false);
                        UserList.userList(primaryStage);
                    }
                    else{
                        l2.setVisible(true);
                    }
                }
            });
            b1.setOnMouseEntered(event -> {
                b1.setCursor(Cursor.HAND);
                b1.setStyle("-fx-background-color: rgba(33,200,253);");
            });
            b1.setOnMouseExited(event -> {
                b1.setCursor(Cursor.DEFAULT);
                b1.setStyle("-fx-background-color: rgba(7,189,253);");
            });


            b2.setOnMouseEntered(event -> {
                b2.setStyle("-fx-background-image:url(/images/CloseBW.png);");
            });
            b2.setOnMouseExited(event -> {
                b2.setStyle("-fx-background-image:url(/images/CloseW.png);");
            });
            b2.setOnMouseClicked(event -> {
                primaryStage.close();
                System.exit(0);
            });

            b3.setOnMouseEntered(event -> {
                b3.setStyle("-fx-background-image:url(/images/MinimizedG.png);");
            });
            b3.setOnMouseExited(event -> {
                b3.setStyle("-fx-background-image:url(/images/Minimized.png);");
            });
            b3.setOnMouseClicked(event -> {
                primaryStage.setIconified(true);
            });

            l1.setOnMouseEntered(event -> {
                l1.setTextFill(Color.GRAY);
                l1.setCursor(Cursor.HAND);
            });
            l1.setOnMouseExited(event -> {
                l1.setTextFill(Color.BLACK);
                l1.setCursor(Cursor.DEFAULT);
            });
            l1.setOnMouseClicked(event -> {
                primaryStage.close();
                Platform.setImplicitExit(false);
                RegisterPage.register(primaryStage);
            });

            l1.setLayoutX(14); l1.setLayoutY(324); l1.setPrefSize(56,17);
            l2.setLayoutX(147); l2.setLayoutY(263); l2.setPrefSize(137,17);
            imv4.setLayoutX(90); imv4.setLayoutY(170);
            imv5.setLayoutX(90); imv5.setLayoutY(222);
            b3.setLayoutX(370); b2.setLayoutY(0); b2.setPrefSize(30,30);
            b2.setLayoutX(400); b3.setLayoutY(0); b3.setPrefSize(30,30);
            imv3.setLayoutX(0); imv3.setLayoutY(0); imv3.setFitWidth(430); imv3.setFitHeight(128);
            tf.setLayoutX(115); tf.setLayoutY(164); tf.setPrefSize(201,28);
            pwf.setLayoutX(115); pwf.setLayoutY(216); pwf.setPrefSize(201,28);
            b1.setLayoutX(96); b1.setLayoutY(291); b1.setPrefSize(238,33);

            root.getChildren().add(l1);
            root.getChildren().add(l2);
            root.getChildren().add(imv4);
            root.getChildren().add(imv5);
            root.getChildren().add(imv3);
            root.getChildren().add(b2);
            root.getChildren().add(b3);
            root.getChildren().add(tf);
            root.getChildren().add(pwf);
            root.getChildren().add(b1);
            //添加回车作为登录键
            b1.setDefaultButton(true);

            scene.getStylesheets().add("/CSS/BorderShadow.css");

            primaryStage.show();//显示
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
