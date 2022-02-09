package Code;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatWindow extends Application {
    static boolean isClicked = false;
    static  double mouse_x;//鼠标x轴坐标
    static  double mouse_y;//鼠标y坐标
    static  double scene_x_start;//截图开始时鼠标x轴坐标
    static  double scene_y_start;//截图开始时鼠标y坐标
    static  double scene_x_end;//截图结束时鼠标x轴坐标
    static  double scene_y_end;//截图结束时鼠标y坐标
    static int position_y = 10;

    static int UID = LogInPage.UID;
    static HBox cut;//全局使用边框

    static String friendIconURL;
    static String friendName;
    static String formerTime = "";//上一条对方信息发送时间
    static String existFile = ""; // 写入的文件条目

    static boolean isCreated = false;

    public static void cut(Stage primaryStage){
        primaryStage.setIconified(true);
        Stage cover = new Stage();

        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-background-color:#ffff5522");//透明黄色

        Scene scene = new Scene(pane);
        scene.setFill(Paint.valueOf("#ffffff00"));//透明

        cover.setScene(scene);
        cover.setFullScreen(true);//全屏
        cover.setFullScreenExitHint("");//去掉提示字
        cover.initStyle(StageStyle.TRANSPARENT);//去边框

        pane.setOnMousePressed(event -> {
            pane.getChildren().clear();//清空上一次截图的组件

            cut = new HBox();
            cut.setBackground(null);
            cut.setStyle("-fx-border-color: red");//框颜色为红
            //获取起始点坐标
            scene_x_start = event.getSceneX();
            scene_y_start = event.getSceneY();
            //起始点在框里显现
            AnchorPane.setLeftAnchor(cut,scene_x_start);
            AnchorPane.setTopAnchor(cut,scene_y_start);

            pane.getChildren().add(cut);
        });
        //设置全屏拖拽
        pane.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                pane.startFullDrag();
            }
        });
        //拖拽结束
        pane.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
                scene_x_end = event.getSceneX();
                scene_y_end = event.getSceneY();

                double width = scene_x_end - scene_x_start;
                double height = scene_y_end - scene_y_start;
                //鼠标往左上角拖拽时
                if(width<0){
                    width = -width;
                }
                if(height<0){
                    height = -height;
                }
                //提示截图信息
                Label positionInfo = new Label();
                positionInfo.setAlignment(Pos.CENTER);
                positionInfo.setText("宽度："+width+"; 高度："+height);
                positionInfo.setTextFill(Color.WHITE);
                positionInfo.setStyle("-fx-background-color:black;");
                positionInfo.setPrefSize(160,30);
                positionInfo.setLayoutX(scene_x_start);positionInfo.setLayoutY(scene_y_start-30);
                pane.getChildren().add(positionInfo);
                cut.setPrefSize(width,height);
            }
        });
        //显示截图完成询问
        pane.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
                Button btn = new Button("截图完成");
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        getScreenCut(cover,primaryStage);
                    }
                });
                cut.setAlignment(Pos.BOTTOM_RIGHT);
                cut.getChildren().add(btn);
            }
        });

        //esc关掉截屏界面，重新打开对话框
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ESCAPE){
                    cover.close();
                    primaryStage.setIconified(false);
                }
            }
        });

        cover.show();
    }
    //截图具体实现
    public static void getScreenCut(Stage cover, Stage primaryStage){
        cover.close();
        double width = scene_x_start - scene_x_end;
        double height = scene_y_start - scene_y_end;

        if(width<0){
            width = -width;
        }
        if(height<0){
            height = -height;
        }

        //机器人截图
        try {
            Robot robot = new Robot();
            Rectangle rec = new Rectangle((int) scene_x_start, (int) scene_y_start, (int) width, (int) height);
            BufferedImage bufferedImage = robot.createScreenCapture(rec);
            //awt转为fx
            WritableImage wi = SwingFXUtils.toFXImage(bufferedImage, null);
            //截图结束，弹出对话框
            primaryStage.setIconified(false);
            //复制到剪切板里
            Clipboard cb = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putImage(wi);
            cb.setContent(content);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void upload(AnchorPane textArea2, TextArea textArea, Statement sql){
        try{
            String userName = LogInPage.userName;
            ResultSet rs = sql.executeQuery("SELECT UserIcon FROM UserInfo WHERE UID = '"+UID+"';");
            rs.next();

            File dialogFileOwn = new File("./Dialog/"+userName+"/"
                    +friendName+"/"+"DialogOwn.txt");

            AnchorPane datePane = new AnchorPane()
                , ownDialogPane = new AnchorPane();

            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String currentDate = df.format(new Date());//获取当前时间

            Label dialog = new Label(textArea.getText())
               , time = new Label(currentDate);
            dialog.setFont(Font.font(16));
            dialog.setAlignment(Pos.CENTER_RIGHT);
            time.setAlignment(Pos.CENTER);//日期居中
            Image userIcon = new Image(rs.getString("UserIcon"));
            Circle iconCircle = new Circle();
            iconCircle.setFill(new ImagePattern(userIcon));

            Image image = new Image("/images/Dialog_own.png");
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            dialog.setTextFill(Color.WHITE);//气泡对话框

            datePane.setPrefSize(610,17);datePane.setLayoutX(0);datePane.setLayoutY(position_y);
            position_y+=17;
            ownDialogPane.setPrefSize(610,60);ownDialogPane.setLayoutX(0);ownDialogPane.setLayoutY(position_y);
            position_y+=60;

            time.setPrefSize(610,17);time.setLayoutX(0);time.setLayoutY(0);
            dialog.setPrefSize(530,50);dialog.setLayoutX(0);dialog.setLayoutY(0);
            iconCircle.setRadius(25);iconCircle.setLayoutX(580);iconCircle.setLayoutY(25);

            Text text = new Text(textArea.getText());
            text.setFont(Font.font(16));
            double textWidth = text.getBoundsInLocal().getWidth();

            imageView.setFitWidth(2*textWidth);imageView.setFitHeight(110);
            imageView.setLayoutX(545-1.5*textWidth);imageView.setLayoutY(dialog.getLayoutY()-15);


            //上传对话文本
            try {
                String urlWay = "smb://192.168.43.230/test/"+friendName+"/"+userName;

                FileOutputStream out = new FileOutputStream(dialogFileOwn,true);
                byte textDate[] = (currentDate+"\r\n").getBytes(StandardCharsets.UTF_8)
                   , textDialog[] = (textArea.getText()+"\r\n").getBytes(StandardCharsets.UTF_8);

                out.write(textDate);
                out.write(textDialog);
                out.close();

                new UploadAndDownload().smbPut(urlWay,dialogFileOwn.getPath(),userName,friendName);
            }catch(Exception e){
                System.out.println("文件写入失败");
            }

            datePane.getChildren().add(time);
            ownDialogPane.getChildren().add(imageView);
            ownDialogPane.getChildren().add(dialog);
            ownDialogPane.getChildren().add(iconCircle);
            textArea2.getChildren().add(datePane);
            textArea2.getChildren().add(ownDialogPane);
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("系统出错，请重新操作");
        }
    }
    //获取对方的文本文件
    public static ArrayList<String> receiveDialogFile(String userName){
        long count = 0;
        String urlWay = "smb://192.168.43.230/test/"+userName+"/"+friendName+"/Dialog.txt";
        //定义文件读取结果集
        ArrayList<String> result = new ArrayList<String>();
        RandomAccessFile fileRead = null;
        File dialogFileFriend = new File("./Dialog/"+userName+"/"
                +friendName+"/DialogFriend.txt")
           , dialogFileFriendPath = new File("./Dialog/"+userName+"/"
                +friendName);

        //从服务器获取对话文件
        new UploadAndDownload().smbGet(urlWay,dialogFileFriendPath.getPath(), userName, friendName);
        //获取朋友对话文本
        try {
            String lineString;
            //随机读取
            fileRead = new RandomAccessFile(dialogFileFriend,"r");
            long length = fileRead.length();//读取文件长度
            if(length == 0){
                return result;
            }else{
                long pos = length-1;
                while(pos>0){
                    pos--;
                    fileRead.seek(pos);//开始读取
                    //读到回车表示一行
                    if(fileRead.readByte() == '\n'){
                        lineString = fileRead.readLine();
                        result.add(lineString);
                        //行数统计
                        count++;
                        if(count == 2)
                            break;
                    }
                }
                if(pos == 0){
                    fileRead.seek(0);
                    result.add(fileRead.readLine());
                }
            }


        }catch(Exception e){
            System.out.println(e);
        }finally {
            if(fileRead != null){
                try{
                    fileRead.close();

                }catch (Exception e){
                    System.out.println("文件关闭失败");
                }
            }
        }
        return result;
    }

    //利用文本文档存储对话，数据库存储该文档
    public static void showFriendDialog(AnchorPane textArea2){
        try{
            String userName = LogInPage.userName;
            //获取最新的两行文本
            ArrayList<String> result = receiveDialogFile(userName);
            //最新对方信息发送时间
            if (result.size()>1) {
                String newTime = result.get(0);
                String newDialog = result.get(1);
                if (!formerTime.equals(newTime)) {
                    formerTime = newTime;
                    AnchorPane datePane = new AnchorPane(), friendDialogPane = new AnchorPane();
                    Label dialog = new Label(newDialog), time = new Label(newTime);
                    dialog.setFont(Font.font(16));
                    time.setAlignment(Pos.CENTER);//日期居中
                    dialog.setAlignment(Pos.CENTER_LEFT);

                    Image friendIcon = new Image(friendIconURL);
                    Circle iconCircle = new Circle();
                    iconCircle.setFill(new ImagePattern(friendIcon));
                    Image image = new Image("/images/Dialog_friend.png");
                    ImageView imageView = new ImageView();
                    imageView.setImage(image);
                    dialog.setTextFill(Color.WHITE);//气泡对话框

                    datePane.setPrefSize(610, 17);
                    datePane.setLayoutX(0);
                    datePane.setLayoutY(position_y);
                    position_y += 17;
                    friendDialogPane.setPrefSize(610, 60);
                    friendDialogPane.setLayoutX(0);
                    friendDialogPane.setLayoutY(position_y);
                    position_y += 60;

                    time.setPrefSize(610, 17);
                    time.setLayoutX(0);
                    time.setLayoutY(0);
                    dialog.setPrefSize(530, 50);
                    dialog.setLayoutX(75);
                    dialog.setLayoutY(0);
                    iconCircle.setRadius(25);
                    iconCircle.setLayoutX(30);
                    iconCircle.setLayoutY(25);

                    Text text = new Text(result.get(0));
                    text.setFont(Font.font(16));
                    double textWidth = text.getBoundsInLocal().getWidth();

                    imageView.setFitWidth(2 * textWidth);
                    imageView.setFitHeight(110);
                    imageView.setLayoutX(-75);
                    imageView.setLayoutY(dialog.getLayoutY() - 20);

                    datePane.getChildren().add(time);
                    friendDialogPane.getChildren().add(imageView);
                    friendDialogPane.getChildren().add(dialog);
                    friendDialogPane.getChildren().add(iconCircle);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            textArea2.getChildren().add(datePane);
                            textArea2.getChildren().add(friendDialogPane);
                        }
                    });
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("对话获取失败");
        }
    }

    public void start(Stage primaryStage){
        primaryStage.initStyle(StageStyle.TRANSPARENT);//标题栏透明
        chatting(primaryStage);
    }

    public static void chatting(Stage primaryStage){

    }
    //方法重载
    public static void chatting(Stage primaryStage, int loc){
        Statement sql = LogInPage.sql;
        try{
            ResultSet rs = sql.executeQuery("SELECT * FROM FriendList WHERE UID = '"+UID+"';");
            for(int i = 1;i<=loc;i++){
                rs.next();
            }
            AnchorPane root = new AnchorPane();
            Scene scene = new Scene(root, 610,726);
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(null);

            AnchorPane title = new AnchorPane()
                    , optionTitle = new AnchorPane()
                    , editDialog = new AnchorPane()
                    , textArea2 = new AnchorPane();
            ScrollPane showDialog = new ScrollPane(textArea2);
            editDialog.setStyle("-fx-background-color: white;");
            showDialog.setStyle("-fx-background-color: white;");
            showDialog.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            showDialog.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            SplitPane chatWindow = new SplitPane(showDialog,editDialog);
            chatWindow.setDividerPositions(0.7782);//设定分割线位置
            chatWindow.setOrientation(Orientation.VERTICAL);//水平分割
            optionTitle.setStyle("-fx-background-color: rgba(237,238,238);");

            friendName = rs.getString("FriendName");
            friendIconURL = rs.getString("FriendIcon");
            Label l1 = new Label(friendName);
            l1.setFont(Font.font(23));
            l1.setTextFill(Color.WHITE);
            l1.setAlignment(Pos.CENTER);

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

            Button button = new Button("send")
                    , b2 = new Button()
                    , b3 = new Button()
                    , b4 = new Button()
                    , b5 = new Button()
                    , b6 = new Button()
                    , b7 = new Button()
                    , b8 = new Button()
                    , b9 = new Button();
            button.setStyle("-fx-background-color: rgba(58,146,217);");
            button.setTextFill(Color.WHITE);
            b2.setId("b2");
            b2.setStyle("-fx-background-image:url(/images/CloseW.png);");
            b3.setId("b3");
            b3.setStyle("-fx-background-image:url(/images/Minimized.png);");
            b4.setId("b4");
            b4.setStyle("-fx-background-image:url(/images/Cut.png);");
            b5.setId("b2");
            b5.setStyle("-fx-background-image:url(/images/FileShare.png);");
            b6.setId("b3");
            b6.setStyle("-fx-background-image:url(/images/ScreenShare.png);");
            b7.setId("b2");
            b7.setStyle("-fx-background-image:url(/images/ScreenReceive.png);");
            b8.setId("b2");
            b8.setStyle("-fx-background-image:url(/images/FileDownload.png);");
            b9.setId("b2");
            b9.setStyle("-fx-background-image:url(/images/emoji.png);");

            TextArea textArea = new TextArea();
            textArea.setId("textArea");

            Image   img7 = new Image("/images/ChatTitleBG.jpg")
                    , img8 = new Image("/images/Cut.png");
            ImageView imv5 = new ImageView()
                    , imv6 = new ImageView();

            imv5.setImage(img7);
            imv6.setImage(img8);

            b2.setOnMouseEntered(event -> {
                b2.setStyle("-fx-background-image:url(/images/CloseBW.png);");
            });
            b2.setOnMouseExited(event -> {
                b2.setStyle("-fx-background-image:url(/images/CloseW.png);");
            });
            b2.setOnMouseClicked(event -> {
                primaryStage.close();
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

            b4.setOnMouseEntered(event -> {
                b4.setCursor(Cursor.HAND);
            });
            b4.setOnMouseExited(event -> {
                b4.setCursor(Cursor.DEFAULT);
            });
            b4.setOnMouseClicked(event -> {
                cut(primaryStage);
            });

            b7.setOnMouseEntered(event -> {
                b7.setCursor(Cursor.HAND);
                b7.setStyle("-fx-background-image:url(/images/ScreenRecieve_blue.png);");
            });
            b7.setOnMouseExited(event -> {
                b7.setCursor(Cursor.DEFAULT);
                b7.setStyle("-fx-background-image:url(/images/ScreenReceive.png);");
            });

            b7.setOnMouseClicked(event -> {
                //在这里实现桌面共享接收
                Stage stage = new Stage();
                ScreenReceive.screenShare(stage);
            });

            b6.setOnMouseEntered(event -> {
                b6.setCursor(Cursor.HAND);
                b6.setStyle("-fx-background-image:url(/images/ScreenShare_blue.png);");
            });
            b6.setOnMouseExited(event -> {
                b6.setCursor(Cursor.DEFAULT);
                b6.setStyle("-fx-background-image:url(/images/ScreenShare.png);");
            });

            b6.setOnMouseClicked(event -> {
                //在这里实现桌面共享
                Stage stage = new Stage();
                ScreenShare.screenShare(stage);
            });

            b5.setOnMouseEntered(event -> {
                b5.setCursor(Cursor.HAND);
                b5.setStyle("-fx-background-image:url(/images/FileShare_blue.png);");
            });
            b5.setOnMouseExited(event -> {
                b5.setCursor(Cursor.DEFAULT);
                b5.setStyle("-fx-background-image:url(/images/FileShare.png);");
            });

            b5.setOnMouseClicked(event -> {
                //服务器地址
                String serverWay = "smb://192.168.43.230/test/";
                //在这里实现文件互传
                UploadAndDownload server = new UploadAndDownload();
                //选文件窗口
                Stage chooseStage = new Stage();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("选择想要打开的文件");
                fileChooser.setInitialDirectory(new File("."));
                File result = fileChooser.showOpenDialog(chooseStage);
                //写入要上传的文件
                File showFileList = new File("./DownloadFile/FileList.txt");
                existFile = serverWay+result.getName()+"\r\n";
                try {
                    FileOutputStream out = new FileOutputStream(showFileList, false);
                    byte item[] = (existFile).getBytes(StandardCharsets.UTF_8);
                    out.write(item);
                    out.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(!result.getAbsolutePath().equals(null)){
                    //本地文件地址
                    String localWay = result.getAbsolutePath();
                    String listWay = showFileList.getAbsolutePath();
                    //传输至服务器,多线程
                    new Thread(() -> {
                        server.smbPut(serverWay, localWay, "", "");
                        server.smbPut(serverWay, listWay,"","");
                    }).start();
                }
            });

            b8.setOnMouseEntered(event -> {
                b8.setCursor(Cursor.HAND);
                b8.setStyle("-fx-background-image:url(/images/FileDownload_blue.png);");
            });
            b8.setOnMouseExited(event -> {
                b8.setCursor(Cursor.DEFAULT);
                b8.setStyle("-fx-background-image:url(/images/FileDownload.png);");
            });
            b8.setOnMouseClicked(event -> {
                //服务器内清单文本文件地址
                String serverListWay = "smb://192.168.43.230/test/FileList.txt";
                //首先下载文件清单文本
                UploadAndDownload check = new UploadAndDownload();
                check.smbGet(serverListWay,"./DownloadFile","","");
                //获取清单上最后一个文件地址
                File file = new File("./DownloadFile/FileList.txt");
                try {
                    RandomAccessFile read = new RandomAccessFile(file, "r");
                    String compareFileWay = read.readLine();
                    if(!existFile.equals(compareFileWay)){//出现新文件时则实行下载
                        check.smbGet(compareFileWay,"./DownloadFile","","");
                    }
                    read.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            //整活，未完成施工的表情包
            GridPane gridPane = new GridPane();
            AnchorPane emojiArea = new AnchorPane(gridPane);
            gridPane.setGridLinesVisible(true);
            gridPane.add(new ImageView(new Image("/images/unfinished.png")),0,0);
            gridPane.setPrefSize(200,200);
            emojiArea.setLayoutX(40);emojiArea.setLayoutY(289);emojiArea.setPrefSize(200,200);
            emojiArea.setVisible(false);
            root.getChildren().add(emojiArea);

            b9.setOnMouseEntered(event -> {
                b9.setStyle("-fx-background-color: rgba(34,129,174);");
            });
            b9.setOnMouseExited(event -> {
                b9.setStyle("-fx-background-color: rgba(58,146,217);");
            });
            b9.setOnMouseClicked(event -> {
                if(!isClicked){
                    emojiArea.setVisible(true);
                    isClicked = true;
                }else
                {
                    emojiArea.setVisible(false);
                    isClicked = false;
                }
            });
            //发送消息按钮
            button.setOnMouseEntered(event -> {
                button.setStyle("-fx-background-color: rgba(34,129,174);");
            });
            button.setOnMouseExited(event -> {
                button.setStyle("-fx-background-color: rgba(58,146,217);");
            });
            button.setOnMouseClicked(event -> {
                if(textArea.getText().equals("")){
                    //消息为空禁止发送
                }else {
                    upload(textArea2, textArea, sql);
                }
            });
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        showFriendDialog(textArea2);
                    }
                }
            });
            thread.start();

            title.setLayoutX(0);title.setLayoutY(0);title.setPrefSize(610,50);
            optionTitle.setLayoutX(0);optionTitle.setLayoutY(50);optionTitle.setPrefSize(610,50);
            chatWindow.setLayoutX(0);chatWindow.setLayoutY(100);chatWindow.setPrefSize(610,626);
            l1.setLayoutX(0); l1.setLayoutY(10); l1.setPrefSize(610,25);
            b2.setLayoutX(575); b2.setLayoutY(0); b2.setPrefSize(35,35);
            b3.setLayoutX(540); b3.setLayoutY(0); b3.setPrefSize(35,35);
            b6.setLayoutX(14); b6.setLayoutY(10); b6.setPrefSize(30,30);
            b7.setLayoutX(116); b7.setLayoutY(10); b7.setPrefSize(32,30);
            b5.setLayoutX(65); b5.setLayoutY(10); b5.setPrefSize(30,30);
            imv5.setLayoutX(0); imv5.setLayoutY(0); imv5.setFitWidth(610);imv5.setFitHeight(50);
            b4.setLayoutX(14); b4.setLayoutY(6); b4.setPrefSize(16,16);
            b9.setLayoutX(44); b9.setLayoutY(6); b9.setPrefSize(16,16);
            button.setLayoutX(530); button.setLayoutY(103); button.setPrefSize(70,25);
            textArea.setLayoutX(0);textArea.setLayoutY(27);textArea.setPrefSize(608,70);
            b8.setLayoutX(167); b8.setLayoutY(10); b8.setPrefSize(32,30);

            root.getChildren().add(title);
            root.getChildren().add(optionTitle);
            root.getChildren().add(chatWindow);
            title.getChildren().add(imv5);
            title.getChildren().add(l1);
            title.getChildren().add(b2);
            title.getChildren().add(b3);
            optionTitle.getChildren().add(b8);
            optionTitle.getChildren().add(b7);
            optionTitle.getChildren().add(b6);
            optionTitle.getChildren().add(b5);
            editDialog.getChildren().add(b4);
            editDialog.getChildren().add(b9);
            editDialog.getChildren().add(button);
            editDialog.getChildren().add(textArea);

            scene.getStylesheets().add("/CSS/BorderShadow.css");

            if(isCreated) {

                isCreated = false;
            }

            primaryStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}