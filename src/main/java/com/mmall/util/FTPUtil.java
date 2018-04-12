package com.mmall.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-12 15:41
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FTPUtil {
    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;//FTP工具包
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");//ftp服务器的地址
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");//ftp服务器的用户名
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");//ftp服务器的密码

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    /**
     * 上传多文件
     *
     * @param fileList
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPass);
        log.info("开始连接ftp服务器");
        //img --> 文件服务器的桶
        boolean result = ftpUtil.uploadFile("img", fileList);
        log.info("开始连接ftp服务器,结束上传,上传结果:{}",result);
        return result;
    }


    /**
     * 上传多文件
     *
     * @param remotePath 远程路径，文件夹(linux 桶)
     * @param fileList   文件集合
     * @return
     * @throws IOException
     */
    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        //上传状态
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接FTP服务器
        if (connectServer(this.ip, this.port, this.user, this.pwd)) {
            try {
                //更改ftp会话的当前目录，切换文件夹（文件服务器的桶）
                ftpClient.changeWorkingDirectory(remotePath);
                //缓冲区
                ftpClient.setBufferSize(1024);
                //字符集
                ftpClient.setControlEncoding("UTF-8");
                //二进制文件类型，防止乱码
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //打开本地的被动模式（服务的被动端口范围）
                ftpClient.enterLocalPassiveMode();
                for (File fileItem : fileList) {
                    fis = new FileInputStream(fileItem);
                    //存储文件,输入流,通过输入流开始存储文件
                    //文件名 和 输入流
                    ftpClient.storeFile(fileItem.getName(), fis);
                }
            } catch (IOException e) {
                log.error("上传文件异常", e);
                uploaded = false;//上传状态
                e.printStackTrace();
            } finally {
                //释放资源，关闭流
                fis.close();
                //关闭文件服务器连接
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    /**
     * 连接FTP服务器
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @return
     */
    private boolean connectServer(String ip,int port,String user,String pwd){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip,port);//连接
            isSuccess = ftpClient.login(user,pwd);//登录
        } catch (IOException e) {
            log.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }
}
