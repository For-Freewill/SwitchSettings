package com.wulei.cisco;

import java.io.IOException;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import com.wulei.utils.SwitchSSHUtils;
import com.wulei.utils.PropertiesUtils;

public class ASANetFlow {
	private static final Logger logger = Logger.getLogger(SwitchSSHUtils.class);
	private static String returnResult;
	private static String host  = PropertiesUtils.getString("asa.host");
	private static String account = PropertiesUtils.getString("asa.acount");
	private static String password = PropertiesUtils.getString("asa.password");
	private static String enpwd = PropertiesUtils.getString("asa.enablepwd");
	
	public static void run(String IP,String mode) {
        String netflowdevice = "flow-export destination inside "+IP+" 2055";
        String[] netflowststrategy = new String[] {"class-map netflow","match access-list EC","policy-map netflow","class netflow"};
        String addnetflow = "flow-export event-type all destination  "+IP;
        String cancelnetflow = "no flow-export event-type all destination  "+IP;
        
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
        logger.info(base.write("conf t", "#"));
        logger.info(base.write("show run | grep flow-export", "#"));
        returnResult = base.write("show run | grep flow-export", "#");
        if (returnResult.contains(IP)) {
        	System.out.println("NetFlow 已经设置");
		} 
        if (returnResult.contains(IP)==false&&mode.equals("new")) {
    		logger.info(base.write(netflowdevice, "#"));
    		String maxmum=base.write(netflowdevice, "#");
    		if (maxmum.contains("ERROR: A maximum of 5 flow-export destinations can be configured")) {
				System.out.println("系统最大支持向5个设备发送netflow，请先删除一个设备后重试");
				return;
			}
    		logger.info(base.write(netflowststrategy[0], "#"));
    		logger.info(base.write(netflowststrategy[1], "#"));
    		logger.info(base.write(netflowststrategy[2], "#"));
    		logger.info(base.write(netflowststrategy[3], "#"));
			logger.info(base.write(addnetflow, "#"));
		}else if (returnResult.contains(IP)==true&&mode.equals("cancel")) {
			logger.info(base.write(netflowststrategy[0], "#"));
			logger.info(base.write(netflowststrategy[1], "#"));
			logger.info(base.write(netflowststrategy[2], "#"));
			logger.info(base.write(netflowststrategy[3], "#"));
			logger.info(base.write(cancelnetflow, "#"));
			logger.info(base.write("no "+netflowdevice, "#"));
		}
        base.close();  
        System.out.println("设置成功");
        logger.info("关闭连接");
	}
	public static void main(String[] args) throws IOException, InterruptedException, EmailException {            
		String IP=args[1];
		String mode=args[0];
//		String IP="10.30.2.95";
//		String mode="cancel";
		run(IP,mode);

    }  
}
