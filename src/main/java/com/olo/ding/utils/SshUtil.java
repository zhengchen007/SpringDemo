package com.olo.ding.utils;

import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

public class SshUtil {
	
	public static Connection conn = null;
	public static boolean login(){
		//创建远程连接，默认连接端口为22，如果不使用默认，可以使用方法
		//new Connection(ip, port)创建对象
		conn = new Connection("192.168.2.85");        
		try {
			//连接远程服务器
			conn.connect();
			//使用用户名和密码登录
	        return conn.authenticateWithPassword("root", "oloCRM@2020");
		} catch (IOException e) {   
			System.err.printf("用户%s密码%s登录服务器%s失败！", "root", "oloCRM@2020", "192.168.2.85");
			e.printStackTrace();
	  }
	  return false;
	}
	
	/**
	  * 上传本地文件到服务器目录下
	  * @param conn Connection对象
	  * @param fileName 本地文件
	  * @param remotePath 服务器目录
	  */
	public static void putFile(Connection conn, String fileName, String remotePath){
		SCPClient sc = new SCPClient(conn);
		try {
			//将本地文件放到远程服务器指定目录下，默认的文件模式为 0600，即 rw，
			//如要更改模式，可调用方法 put(fileName, remotePath, mode),模式须是4位数字且以0开头
			sc.put(fileName, remotePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	  * 下载服务器文件到本地目录
	  * @param fileName 服务器文件
	  * @param localPath 本地目录
	  */
	public static void copyFile(Connection conn, String fileName,String localPath){
		SCPClient sc = new SCPClient(conn);
		try {
			sc.get(fileName, localPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
public static void main(String[] args) {
	String a = "/mnt/weaver/ecology/filesystem/202008/D/7bf5ea74-167c-4350-b35f-1415d6f7022d.zip";
	System.out.println(a.substring(a.lastIndexOf("/"), a.length()));
}

}
