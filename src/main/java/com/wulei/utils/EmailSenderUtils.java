package com.wulei.utils;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class EmailSenderUtils {
	
	public static void main(String[] args) {
		try {
			//sendMsg();
			 String path= "D:\\test1.xlsx";
			sendEmailsWithAttachments(path,"测试结果","请查收");
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMsg() throws EmailException {
		HtmlEmail mail = new HtmlEmail();
		String hostname  = PropertiesUtils.getString("mail.host");
		String password = PropertiesUtils.getString("mail.password");
		String username = PropertiesUtils.getString("mail.username");
	    String[] toList = PropertiesUtils.getStringArray("mail.touser");
	    String subject = PropertiesUtils.getString("mail.title");
		mail.setHostName(hostname); // 邮件服务器域名
		//mail.setSmtpPort(smtpPort); // 邮件服务器smtp协议端口
		mail.setAuthentication(username, password); // 邮箱账户
		mail.setCharset("UTF-8"); // 邮件的字符集

		//mail.setSSLOnConnect(true); // 是否启用SSL
		//mail.setSslSmtpPort(sslSmtpPort); // 若启用，设置smtp协议的SSL端口号
 
		mail.setFrom(username); // 发件人地址
		for (String to : toList) {
			mail.addTo(to); // 收件人地址，可以设置多个
		}
		
		mail.setSubject(subject); // 邮件主题  
		mail.setHtmlMsg("test"); // 邮件正文  
		String rString = mail.send(); // 发送邮件  
		System.out.println(rString);
	}
	
	 public static void sendEmailsWithAttachments(String filepath,String title, String context) throws EmailException {
	        // Create the attachment
		  
	        EmailAttachment attachment = new EmailAttachment();
	        attachment.setPath(filepath);
	        attachment.setDisposition(EmailAttachment.ATTACHMENT);
	        attachment.setDescription("系统日志");
	        attachment.setName("sshswitch.log");
	        
//	        EmailAttachment attachment2 = new EmailAttachment();
//	        attachment2.setPath(filepath);
//	        attachment2.setDisposition(EmailAttachment.ATTACHMENT);
//	        attachment2.setDescription("测试结果");
//	        attachment2.setName("excel2.xlsx");

	        String hostname  = PropertiesUtils.getString("mail.host");
			String password = PropertiesUtils.getString("mail.password");
			String username = PropertiesUtils.getString("mail.username");
		    String[] toList = PropertiesUtils.getStringArray("mail.touser");
		    
	        // Create the email message
		    HtmlEmail email = new HtmlEmail();
	        email.setHostName(hostname); // 邮件服务器域名
			//mail.setSmtpPort(smtpPort); // 邮件服务器smtp协议端口
	        email.setAuthentication(username, password); // 邮箱账户
	        email.setCharset("UTF-8"); // 邮件的字符集

			//mail.setSSLOnConnect(true); // 是否启用SSL
			//mail.setSslSmtpPort(sslSmtpPort); // 若启用，设置smtp协议的SSL端口号
	        email.setSubject(title);
	        email.setFrom(username); // 发件人地址
	        email.setHtmlMsg(context);
	        
	        for (String to : toList) {
	        	email.addTo(to); // 收件人地址，可以设置多个
			}
	        
	        // add the attachment
	        email.attach(attachment);
	   
	        //email.attach(attachment2);

	        // send the email
	        email.send();
	    }

}
