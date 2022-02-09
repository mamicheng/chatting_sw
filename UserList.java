package Code;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class UserList extends Application {
    static  double mouse_x;//鼠标x轴坐标
    static  double mouse_y;//鼠标y坐标
    static int UID = LogInPage.UID;
    static double friend_id;

    public static void fileMaker(String userName, Statement sql){
        try {
            ResultSet rs = sql.executeQuery("SELECT * FROM FriendList WHERE UID = '"+UID+"';");

            String path = "./Dialog/" + userName;
            File folder_user = new File(path);
            if (!folder_user.exists() && !folder_user.isDirectory()) {
                folder_user.mkdirs();//给新用户创建对话文件夹
            }

            ArrayList<String> friendList = new ArrayList<String>();
            while(rs.next()){
                friendList.add(rs.getString("FriendName"));
            }

            int length = friendList.size();

            while(length>0){
                String friendName = friendList.get(length-1);
                File dialogFileOwn = new File("./Dialog/"+userName+"/"
                        +friendName+"/"+"DialogOwn.txt")
                   , dialogFileFriend = new File("./Dialog/"+userName+"/"
                        +friendName+"/"+"DialogFriend.txt")
                   , folder_friend = new File("./Dialog/"+userName+"/"
                        +friendName);

                if (!folder_friend.exists() && !folder_friend.isDirectory()) {
                    folder_friend.mkdirs();//对应朋友的对话文件夹不存在则创造一个文件夹
                }

                if(!dialogFileOwn.exists()){
                    dialogFileOwn.createNewFile();//对应朋友的对话文本文件不存在则创造一个文件
                    sql.executeUpdate("UPDATE FriendList SET DialogOwn = '"+dialogFileOwn.getPath()
                            +"' WHERE UID = '"+UID+"' AND FriendName = '"
                            +friendName+"';");
                }

                if(!dialogFileFriend.exists()){
                    dialogFileFriend.createNewFile();//对应朋友的对话文本文件不存在则创造一个文件
                    sql.executeUpdate("UPDATE FriendList SET DialogFriend = '"+dialogFileOwn.getPath()
                            +"' WHERE UID = '"+UID+"' AND FriendName = '"
                            +friendName+"';");
                }
                length--;
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void showList(VBox listArea, Statement sql){
        int position_x = 0, position_y = 0;
        try{
            ResultSet rs = sql.executeQuery("SELECT *from FriendList WHERE UID='" + UID + "';");

            listArea.setStyle("-fx-background-color:white;");

            listArea.setOnMouseClicked(event -> {
                    friend_id=event.getY()/60+1;
                    int loc = (int)friend_id;
            });
            while(rs.next()) {
                AnchorPane friend = new AnchorPane();
                Circle friendIcon = new Circle();
                Label friendName = new Label(rs.getString("FriendName"))
                    , friendID = new Label("ID:"+rs.getString("FriendID"));
                Image icon = new Image(rs.getString("FriendIcon"));
                friendIcon.setFill(new ImagePattern(icon));//头像
                friendName.setFont(Font.font(16));
                friendID.setTextFill(Color.rgb(208,207,209));

                friend.setStyle("-fx-background-color:white;");
                friend.setOnMouseEntered(event -> {
                    friend.setStyle("-fx-background-color:rgba(242,242,242);");
                });
                friend.setOnMouseExited(event -> {
                    friend.setStyle("-fx-background-color:white;");
                });//区域hover效果
                friend.setOnMouseClicked(event -> {
                    if(event.getClickCount()==2){
                        Stage secondStage = new Stage();
                        ChatWindow.chatting(secondStage,(int)friend_id);
                    }
                });//双击事件


                friend.setLayoutX(position_x);friend.setLayoutY(position_y);friend.setPrefSize(360,60);
                friendName.setLayoutX(64);friendName.setLayoutY(5);friendName.setPrefSize(80,30);
                friendID.setLayoutX(64);friendID.setLayoutY(29);friendID.setPrefSize(66,30);
                friendIcon.setLayoutX(33);friendIcon.setLayoutY(29);friendIcon.setRadius(19);

                friend.getChildren().add(friendName);
                friend.getChildren().add(friendIcon);
                friend.getChildren().add(friendID);
                listArea.getChildren().add(friend);
            }

        }catch (SQLException e) {
            System.out.println(e);
            System.out.println("系统出错，请重新操作");
        }
    }
    public void start(Stage primaryStage){
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        userList(primaryStage);
    }
    public static void userList(Stage primaryStage){
        Statement sql = LogInPage.sql;
        String user_name = LogInPage.userName;
        try{
            ResultSet rs = sql.executeQuery("SELECT * FROM UserInfo WHERE UID = '"+UID+"';");
            rs.next();//指针下移
            AnchorPane root = new AnchorPane()
                     , titlePane = new AnchorPane()
                     , searchPane = new AnchorPane();
            VBox listArea = new VBox();
            ScrollPane listPane = new ScrollPane(listArea);
            SplitPane splitPane =new SplitPane(searchPane,listPane);
            Scene scene = new Scene(root, 360,590);
            primaryStage.setResizable(false);//设置窗口不可改变
            primaryStage.setAlwaysOnTop(true);//设置窗口悬浮
            listPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            listPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            splitPane.setDividerPositions(0.087);
            splitPane.setOrientation(Orientation.VERTICAL);//水平分割

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

            Button b1 = new Button()
                 , b2 = new Button();
            b1.setId("b2");
            b2.setId("b3");

            Label selfID = new Label("ID:"+String.valueOf(UID))
                , userName = new Label(rs.getString("UserName"));
            userName.setFont(Font.font(20));
            userName.setTextFill(Color.WHITE);

            TextField tf = new TextField();
            String url = rs.getString("UserIcon");
            if(url.equals("null")){
                url = "./images/UserIcon/DefaultIcon.png";
            }

            Image img = new Image("/images/UserListBG.gif")
                , picture = new Image(url);
            ImageView imv = new ImageView();
            imv.setImage(img);

            Circle selfIcon = new Circle();
            selfIcon.setFill(new ImagePattern(picture));

            b1.setStyle("-fx-background-image:url(/images/CloseW.png);");
            b2.setStyle("-fx-background-image:url(/images/Minimized.png);");

            b1.setOnMouseEntered(event -> {
                b1.setStyle("-fx-background-image:url(/images/CloseBW.png);");
            });
            b1.setOnMouseExited(event -> {
                b1.setStyle("-fx-background-image:url(/images/CloseW.png);");
            });
            b1.setOnMouseClicked(event -> {
                primaryStage.close();
                System.exit(0);
            });

            b2.setOnMouseEntered(event -> {
                b2.setStyle("-fx-background-image:url(/images/MinimizedG.png);");
            });
            b2.setOnMouseExited(event -> {
                b2.setStyle("-fx-background-image:url(/images/Minimized.png);");
            });
            b2.setOnMouseClicked(event -> {
                primaryStage.setIconified(true);
            });

            showList(listArea,sql);
            fileMaker(user_name, sql);

            titlePane.setLayoutX(0);titlePane.setLayoutY(0);titlePane.setPrefSize(361,150);
            splitPane.setLayoutX(0);splitPane.setLayoutY(107);splitPane.setPrefSize(365,472);
            searchPane.setLayoutX(0);searchPane.setLayoutY(0);searchPane.setPrefSize(362,30);
            listPane.setLayoutX(0);listPane.setLayoutY(0);
            listArea.setLayoutX(0);listArea.setLayoutY(0);listArea.setPrefSize(360,415);
            imv.setLayoutX(0);imv.setLayoutY(0);imv.setFitWidth(361);imv.setFitHeight(200);
            b1.setLayoutX(328);b1.setLayoutY(-1);b1.setPrefSize(35,35);
            b2.setLayoutX(293);b2.setLayoutY(-1);b2.setPrefSize(35,35);
            tf.setLayoutX(0);tf.setLayoutY(0);tf.setPrefSize(363,40);
            selfIcon.setLayoutX(40);selfIcon.setLayoutY(47);selfIcon.setRadius(25);
            selfID.setLayoutX(85);selfID.setLayoutY(53);selfID.setPrefSize(35,17);
            userName.setLayoutX(91);userName.setLayoutY(17);userName.setPrefSize(106,30);

            root.getChildren().add(titlePane);
            root.getChildren().add(splitPane);
            titlePane.getChildren().add(imv);
            titlePane.getChildren().add(b1);
            titlePane.getChildren().add(b2);
            titlePane.getChildren().add(selfIcon);
            titlePane.getChildren().add(userName);
            titlePane.getChildren().add(selfID);
            searchPane.getChildren().add(tf);

            scene.getStylesheets().add("/CSS/BorderShadow.css");
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
