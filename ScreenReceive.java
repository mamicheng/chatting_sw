package Code;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ScreenReceive extends Application {
    public void start(Stage primaryStage){
        primaryStage.initStyle(StageStyle.TRANSPARENT);//标题栏透明
        screenShare(primaryStage);
    }
    //接收图像
        public static void receive(ImageView imageView){
            String urlWay = "smb://192.168.43.230/test/test.png";
            String serverWay = "smb://192.168.43.230/test/";
            String localWay = "./ScreenImage";
            try {
                //UploadAndDownload hello = new UploadAndDownload();
                //if(hello.deleteHello(serverWay))
                    new UploadAndDownload().smbGet(urlWay, localWay, "","");
            }catch (Exception e){
                System.out.println("屏幕下载出错"+e);
            }
            Image image = new Image("file:./ScreenImage/test.png");
            imageView.setImage(image);
        }

    public static void screenShare(Stage stage){
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, 610,726);
        stage.setScene(scene);

        scene.setFill(null);

        Button button = new Button("停止共享");
        ImageView imageView = new ImageView();

        button.setPrefSize(80,30);button.setLayoutX(0);button.setLayoutY(0);
        button.setOnAction(event -> {
            stage.close();
        });

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    receive(imageView);
                }
            }
        };
        thread.start();

        imageView.setLayoutX(0);imageView.setLayoutY(40);


        root.getChildren().add(button);
        root.getChildren().add(imageView);

        stage.show();
    }
}
