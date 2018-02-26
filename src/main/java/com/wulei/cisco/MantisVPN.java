package com.wulei.cisco;

import java.io.File;
import java.io.IOException;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import com.wulei.utils.SwitchSSHUtils;
import com.wulei.utils.EmailSenderUtils;
import com.wulei.utils.PropertiesUtils;

public class MantisVPN {
	private static final Logger logger = Logger.getLogger(SwitchSSHUtils.class);
	private static String returnResult;
	private static String host  = PropertiesUtils.getString("asa.host");
	private static String account = PropertiesUtils.getString("asa.acount");
	private static String password = PropertiesUtils.getString("asa.password");
	private static String enpwd = PropertiesUtils.getString("asa.enablepwd");
	private static String mantisVpn = PropertiesUtils.getString("asa.mantisvpn");
	
	public static void main(String[] args) throws IOException, InterruptedException, EmailException {            
		SwitchSSHUtils base = new SwitchSSHUtils(host);  
        base.setAccount(account);  
        base.setPassword(password);  
        
        logger.info("\n"+"开始连接ASA");//支持中文
        base.connect();  
        base.getExpect();  
        base.login();  
        logger.info(base.write("en","Password:"));
        logger.info(base.write(enpwd,"#"));
        logger.info(base.write("terminal pager 0", "#"));
        Thread.sleep(3000);
        logger.info(base.write(mantisVpn, "#")); 
        returnResult = base.write(mantisVpn, "#");
        System.out.println("交换机返回："+returnResult);
        System.out.println("交换机返回包含IP："+returnResult.contains("208.91.114.30"));
        System.out.println("交换机返回包含异常："+returnResult.contains("返回异常"));
        if (returnResult.contains("返回异常")==false && returnResult.contains("208.91.114.30")==false) {
        	String path= "logs"+File.separator+"sshswitch.log";
        	EmailSenderUtils.sendEmailsWithAttachments(path,"Mantis VPN 连接断开！！","检测到Mantis VPN连接断开，请稍后登录Mantis。");
        	logger.info("邮件发送成功");
		}
        base.close();  
        logger.info("关闭连接");
    }  
}
