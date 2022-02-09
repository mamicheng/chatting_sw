package Code;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jcifs.smb.SmbFile;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
public class ScreenShare extends Application {
    static ImageView show;
    static  double scene_x_start;//截图开始时鼠标x轴坐标
    static  double scene_y_start;//截图开始时鼠标y坐标
    static  double scene_x_end;//截图结束时鼠标x轴坐标
    static  double scene_y_end;//截图结束时鼠标y坐标
    static HBox cut;//全局使用边框
    public void start(Stage primaryStage){
        primaryStage.initStyle(StageStyle.TRANSPARENT);//标题栏透明
        screenShare(primaryStage);
    }
    public static void screenShare(Stage stage){
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, 610,726);
        stage.setScene(scene);

        scene.setFill(null);

        Button button = new Button("停止共享");
        show = new ImageView();

        button.setPrefSize(80,30);button.setLayoutX(0);button.setLayoutY(0);
        button.setOnAction(event -> {
            stage.close();
        });
        show.setLayoutX(0);show.setLayoutY(40);

        cut(stage);

        root.getChildren().add(button);
        root.getChildren().add(show);

        stage.show();
    }
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
                        sendScreenCut(primaryStage,show);
                        cover.close();
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
    public static void sendScreenCut(Stage primaryStage, ImageView show){
        String urlWay = "smb://192.168.43.230/test";
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
            //截图结束，弹出对话框
            primaryStage.setIconified(false);
            show.setFitWidth(width);show.setFitHeight(height);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while(true) {
                        BufferedImage bufferedImage = robot.createScreenCapture(rec);
                        //awt转为fx
                        WritableImage wi = SwingFXUtils.toFXImage(bufferedImage, null);
                        try {
                            SmbFile netFile = new SmbFile(urlWay+"hello.txt");
                            //if(!netFile.exists()){
                                //netFile.createNewFile();//打招呼
                                try {
                                    //创建png文件
                                    UploadAndDownload hello = new UploadAndDownload();
                                    File file = new File("./ScreenImage/test.png");
                                   // boolean flag = hello.putHello("smb://192.168.43.230/test");//打招呼
                                   // System.out.println(flag);
                                  //  if(flag) {
                                        ImageIO.write(bufferedImage, "png", file);
                                        hello.smbPut(urlWay, file.getAbsolutePath(), "", "");
                                   // }
                                }catch (IOException ioe){
                                    System.out.println(ioe);
                                }
                                show.setImage(wi);
                           // }
                        }catch (Exception e){

                        }
                    }
                }
            };
            thread.start();

        }catch (Exception e){
            System.out.println(e);
        }
    }
}
