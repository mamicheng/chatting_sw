package Code;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.sql.Statement;


public class RegisterPage extends Application {
    static  double mouse_x;//鼠标x轴坐标
    static  double mouse_y;//鼠标y坐标
    private static boolean isOpen1 = false;
    private static boolean isOpen2 = false;

    public static void addUser(TextField tf1, TextField tf2, PasswordField pwf2, Statement sql){
        try {
            sql.executeUpdate("INSERT INTO UserInfo (UserName, Password, TrueName, UserIcon) VALUES('"
                    + tf1.getText() + "','" + pwf2.getText() + "','" + tf2.getText()+"','" +"null" + "');");
        }catch (SQLException E) {
            System.out.println(E);
            System.out.println("系统出错，请重新操作");
        }
    }

    private static boolean compare(PasswordField pwf1, PasswordField pwf2){
        if(pwf1.getText().equals(pwf2.getText())){
            return true;
        }
        return false;
    }

    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        register(primaryStage);
    }

    public static void register(Stage primaryStage){
        Statement sql = LogInPage.sql;
        try{
            AnchorPane root = new AnchorPane();
            Scene scene = new Scene(root, 540,490);
            //设置背景透明
            primaryStage.setScene(scene);
            scene.setFill(null);
            root.setStyle("-fx-background-color: rgba(250,206,253,0.7);");

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

            Label l1= new Label("昵称：")
                , l2= new Label("密码：")
                , l3= new Label("密码强度")
                , l4= new Label("")
                , l5= new Label("确认密码：")
                , l6= new Label("真实姓名：")
                , l7= new Label()
                , l8= new Label("注册成功，欢迎加入，赶紧去登陆吧");
            l1.setFont(Font.font(20));
            l2.setFont(Font.font(20));
            l3.setFont(Font.font(12));
            l4.setFont(Font.font(12));
            l5.setFont(Font.font(20));
            l6.setFont(Font.font(20));
            l8.setVisible(false);

            TextField tf1 = new TextField()
                    , tf2 = new TextField()
                    , tf3 = new TextField()
                    , tf4 = new TextField();
            tf3.setVisible(false);
            tf4.setVisible(false);

            tf1.setPromptText("请输入想要的昵称");
            tf2.setPromptText("请输入你的真实姓名");

            PasswordField pwf1 = new PasswordField()
                        , pwf2 = new PasswordField();
            pwf1.setPromptText("请输入字符串长度超过8的密码");
            pwf2.setPromptText("请再次输入密码以确认");

            Button b1 = new Button("注册")
                 , b2 = new Button()
                 , b3 = new Button();
            b2.setId("b2");
            b3.setId("b3");
            b1.setFont(Font.font(20));

            b2.setStyle("-fx-background-image:url(/images/CloseW.png);");
            b3.setStyle("-fx-background-image:url(/images/Minimized.png);");

            ProgressBar pgb = new ProgressBar();
            pgb.setId("bar");
            pgb.setProgress(0);

            Image img5 = new Image("/images/OpenEye.png")
                , img6 = new Image("/images/CloseEye.png");
            ImageView imv3 = new ImageView()
                    , imv4 = new ImageView();
            imv3.setImage(img6);
            imv4.setImage(img6);

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

            imv3.setOnMouseClicked(event -> {
                if(isOpen1){
                    imv3.setImage(img6);
                    tf3.setVisible(false);
                    pwf1.setVisible(true);
                    isOpen1 = false;
                }else {
                    imv3.setImage(img5);
                    tf3.setText(pwf1.getText());
                    tf3.setVisible(true);
                    pwf1.setVisible(false);
                    isOpen1 = true;
                }
            });//密码可视按钮监听
            imv3.setOnMouseEntered(event -> {
                imv3.setCursor(Cursor.HAND);
            });
            imv3.setOnMouseExited(event -> {
                imv3.setCursor(Cursor.DEFAULT);
            });

            imv4.setOnMouseClicked(event -> {
                if(isOpen2){
                    imv4.setImage(img6);
                    tf4.setVisible(false);
                    pwf2.setVisible(true);
                    isOpen2 = false;
                }else {
                    imv4.setImage(img5);
                    tf4.setText(pwf2.getText());
                    tf4.setVisible(true);
                    pwf2.setVisible(false);
                    isOpen2 = true;
                }
            });//密码可视按钮监听
            imv4.setOnMouseEntered(event -> {
                imv4.setCursor(Cursor.HAND);
            });
            imv4.setOnMouseExited(event -> {
                imv4.setCursor(Cursor.DEFAULT);
            });

            pwf1.setOnKeyTyped(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    int wordString = pwf1.getText().length();
                    if (wordString<=8){
                        pgb.setStyle("-fx-accent:red");
                        pgb.setProgress(0.33);
                        l4.setText("弱");
                    }
                    else if(wordString>8&&wordString<=10){
                        pgb.setStyle("-fx-accent:yellow");
                        pgb.setProgress(0.66);
                        l4.setText("中");
                    }
                    else if(wordString == 0){
                        pgb.setProgress(0);
                        l4.setText("");
                    }
                    else{
                        pgb.setStyle("-fx-accent:green");
                        pgb.setProgress(1);
                        l4.setText("强");
                    }
                }
            });

            b1.setOnMouseClicked(event -> {
                boolean confirm = compare(pwf1,pwf2);
                if (confirm){
                    addUser(tf1,tf2,pwf2,sql);//录入数据库
                    l8.setVisible(true);
                    primaryStage.close();
                    Platform.setImplicitExit(true);
                    LogInPage.logIn(primaryStage);
                }
                else{
                    l7.setTextFill(Color.RED);
                    l7.setText("确认密码与设定的密码不匹配");
                }
            });

            l1.setLayoutX(66); l1.setLayoutY(58); l1.setPrefSize(60,27);
            l2.setLayoutX(66); l2.setLayoutY(138); l2.setPrefSize(60,27);
            l3.setLayoutX(126); l3.setLayoutY(184); l3.setPrefSize(60,27);
            l4.setLayoutX(345); l4.setLayoutY(205); l4.setPrefSize(60,27);
            l5.setLayoutX(26); l5.setLayoutY(257); l5.setPrefSize(100,27);
            l6.setLayoutX(26); l6.setLayoutY(337); l6.setPrefSize(100,27);
            l7.setLayoutX(124); l7.setLayoutY(300); l7.setPrefSize(165,17);
            l8.setLayoutX(171); l8.setLayoutY(406); l8.setPrefSize(198,17);
            imv3.setLayoutX(455); imv3.setLayoutY(135); imv3.setFitWidth(32); imv3.setFitHeight(32);
            imv4.setLayoutX(455); imv4.setLayoutY(255); imv4.setFitWidth(32); imv4.setFitHeight(32);
            tf1.setLayoutX(126); tf1.setLayoutY(46); tf1.setPrefSize(363,44);
            tf2.setLayoutX(126); tf2.setLayoutY(328); tf2.setPrefSize(363,44);
            tf3.setLayoutX(126); tf3.setLayoutY(126); tf3.setPrefSize(363,44);
            tf4.setLayoutX(126); tf4.setLayoutY(248); tf4.setPrefSize(363,44);
            pwf1.setLayoutX(126); pwf1.setLayoutY(126); pwf1.setPrefSize(363,44);
            pwf2.setLayoutX(126); pwf2.setLayoutY(248); pwf2.setPrefSize(363,44);
            b1.setLayoutX(209); b1.setLayoutY(418); b1.setPrefSize(125,44);
            pgb.setLayoutX(124); pgb.setLayoutY(210); pgb.setPrefSize(205,18);
            b3.setLayoutX(480); b3.setLayoutY(0); b3.setPrefSize(30,30);
            b2.setLayoutX(510); b2.setLayoutY(0); b2.setPrefSize(30,30);

            root.getChildren().add(tf1);
            root.getChildren().add(tf2);
            root.getChildren().add(tf3);
            root.getChildren().add(tf4);
            root.getChildren().add(pwf1);
            root.getChildren().add(pwf2);
            root.getChildren().add(l1);
            root.getChildren().add(l2);
            root.getChildren().add(l3);
            root.getChildren().add(l4);
            root.getChildren().add(l5);
            root.getChildren().add(l6);
            root.getChildren().add(l7);
            root.getChildren().add(l8);
            root.getChildren().add(imv3);
            root.getChildren().add(imv4);
            root.getChildren().add(b1);
            root.getChildren().add(pgb);
            root.getChildren().add(b2);
            root.getChildren().add(b3);

            scene.getStylesheets().add("/CSS/BorderShadow.css");

            primaryStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
