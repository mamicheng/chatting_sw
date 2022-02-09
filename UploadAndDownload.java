package Code;

import java.io.*;
import java.io.IOException;
import jcifs.smb.*;

public class UploadAndDownload {
    public void smbGet(String remoteUrl, String localDir, String userName, String friendName)
    {
        InputStream in = null;
        OutputStream out = null;
        try
        {
            //初始化文件对象
            SmbFile remoteFile = new SmbFile(remoteUrl);
            //连接服务器
            remoteFile.connect();
            if (remoteFile == null)
            {
                System.out.println("共享文件不存在");
                return;
            }
            String fileName = remoteFile.getName();
            File localFile;
            if(userName.equals("")) {
                localFile = new File(localDir + File.separator + fileName);//截图或其他文件
            }
            else{
                localFile = new File(localDir+"/"+"DialogFriend.txt");//文本文件
            }
            in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));

            byte[] buffer = new byte[1024];

            while (in.read(buffer) != -1)
            {
                out.write(buffer);
                //缓存
                buffer = new byte[1024];
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void smbPut(String remoteUrl, String localFilePath, String userName, String friendName)
    {
        InputStream in = null;
        OutputStream out = null;
        try
        {
            File localFile = new File(localFilePath);
            //获取本地文件名
            String fileName = localFile.getName();
            //////////////上传到指定地址
            SmbFile remoteFile;
            if(userName.equals("")) {
                remoteFile = new SmbFile(remoteUrl + "/" + fileName);
            }
            else{
                remoteFile = new SmbFile(remoteUrl + "/" +"Dialog.txt");
            }

            in = new BufferedInputStream(new FileInputStream(localFile));
            out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
            byte[] buffer = new byte[1024];

            while (in.read(buffer) != -1)
            {
                out.write(buffer);
                buffer = new byte[1024];
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
/*
    public boolean putHello(String urlWay){
        try {
            SmbFile netFile = new SmbFile(urlWay + "/hello.txt");
            if(netFile.exists())
                return false;
            //netFile.connect();
            netFile.createNewFile();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteHello(String urlWay){
        try {
            SmbFile netFile = new SmbFile(urlWay + "hello.txt");
            if(netFile.exists()) {
                //netFile.connect();
                netFile.delete();
                return true;
            }
        }
        catch (Exception e){
            return false;
        }
        return false;
    }
    */
    //public void main(String args[]) {
        /*UploadAndDownload test = new UploadAndDownload();
        // smb:域名;用户名:密码@目的IP/文件夹/文件名.xxx
        // test.smbGet("smb://szpcg;jiang.t:xxx@192.168.193.13/Jake/test.txt",
        //"c://") ;

        //本地文档地址
        String localWay = "f://practice1/Work/";  //文件地址

        /////////服务器地址
        String urlWay = "smb://192.168.43.230/test/";

        Scanner sc = new Scanner(System.in);
        String fileName = sc.nextLine();
        //test.smbPut(urlWay,localWay);

        // test.smbPut(urlWay, localWay+fileName);

        test.smbGet(urlWay+fileName, localWay);

        System.out.println("success");

        //用户名密码不能有强字符，也就是不能有特殊字符，否则会被作为分断处理
        // test.smbGet("smb://CHINA;xieruilin:123456Xrl@10.70.36.121/project/report/网上问题智能分析助手使用文档.doc",
        //"c://Temp/");*/
        //String urlWay = "smb://192.168.43.230/test/";
        //if(deleteHello(urlWay)){
        //    System.out.println("1111");
        //}
    //}
}

