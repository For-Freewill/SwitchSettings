package com.wulei.cisco;

import java.io.IOException;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import com.wulei.utils.SwitchSSHUtils;
import com.wulei.utils.PropertiesUtils;

public class ASASNMP {
	private static final Logger logger = Logger.getLogger(SwitchSSHUtils.class);
	private static String returnResult;
	private static String host  = PropertiesUtils.getString("asa.host");
	private static String account = PropertiesUtils.getString("asa.acount");
	private static String password = PropertiesUtils.getString("asa.password");
	private static String enpwd = PropertiesUtils.getString("asa.enablepwd");
	
	public static void main(String[] args) throws IOException, InterruptedException, EmailException {            
		SwitchSSHUtils base = new SwitchSSHUtils(host);  
        base.setAccount(account);  
        base.setPassword(password);  
        String IP=args[1];
        String asaNewsnmp = "snmp-server host inside "+IP+" community public";
        String asaCancelsnmp = "no snmp-server host inside "+IP+" community public";
        logger.info("\n"+"开始连接ASA");//支持中文
        base.connect();  
        base.getExpect();  
        base.login();  
        logger.info(base.write("en","Password:"));
        logger.info(base.write(enpwd,"#"));
        logger.info(base.write("terminal pager 0", "#"));
        logger.info(base.write("conf t", "#"));
        Thread.sleep(3000);
        System.out.println("第一个参数为："+args[0]);
        if (args[0].equals("n")) {
            logger.info(base.write(asaNewsnmp, "#")); 
            returnResult = base.write(asaNewsnmp, "#");
            System.out.println("交换机返回："+returnResult);
            System.out.println("命令执行成功");
		} else if (args[0].equals("c")) {
            logger.info(base.write(asaCancelsnmp, "#")); 
            returnResult = base.write(asaCancelsnmp, "#");	
            System.out.println("交换机返回："+returnResult);
            System.out.println("命令执行成功");
		}
        base.close();  
        logger.info("关闭连接");
    }  
}
