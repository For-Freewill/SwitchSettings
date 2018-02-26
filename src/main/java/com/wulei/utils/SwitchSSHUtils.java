package com.wulei.utils;

import static net.sf.expectit.matcher.Matchers.regexp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;

import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;  


/**
 * SSH基类
 * @author Lei.Wu
 * @date 2018-1-8 上午09:12:51
 * @version V1.0
 * @since ,Jdk1.8
 * @copyright Copyright (c) 2018
 */
public class SwitchSSHUtils {
	private static final Logger logger = Logger.getLogger(SwitchSSHUtils.class);
	
	private SshClient client;
	private ClientSession session;
	private ClientChannel channel;
	private Expect expect;
	
	public String account;
	public String password;
	public String enablepassword;
	public String host;
	public int port = 22;
	public int timeout = 3000;
	
	public SwitchSSHUtils(String host) {
		super();
		this.host = host;
	}

	public SwitchSSHUtils(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public SwitchSSHUtils(String host, int port, int timeout) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEnablepassword() {
		return enablepassword;
	}

	public void setEnablepassword(String enablepassword) {
		this.enablepassword = enablepassword;
	}
	
	/**
	 * SSH连接
	 * @author Lei.Wu
	 * @date 2018-1-8 上午10:13:16
	 * @version V1.0
	 * @since ,Jdk1.8
	 * @copyright Copyright (c) 2018
	 */
	public boolean connect(){
		client = SshClient.setUpDefaultClient();
		try {
			client.start();
			ConnectFuture cf = client.connect(account, host, port);
			if(cf.awaitUninterruptibly(timeout, TimeUnit.MILLISECONDS) && cf.isConnected()){
				session = cf.getSession();
				session.addPasswordIdentity(password);
				return session.auth().awaitUninterruptibly(timeout, TimeUnit.MILLISECONDS);
			}
		} catch (Exception e) {
			logger.error("SSH Connect Error --> {}"+e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * 获得Expect对象，该对用可以往SSH发送命令请求
	 * @author Lei.Wu
	 * @date 2018-1-16 上午10:17:03
	 * @version V1.0
	 * @since ,Jdk1.8
	 * @copyright Copyright (c) 2018
	 */
	public void getExpect(){
		try {
			if (null == channel || channel.isClosed() || channel.isClosing()) {
				channel = session.createShellChannel();
				channel.open().awaitUninterruptibly();
			}
			if (channel.isOpen()) {
				expect = new ExpectBuilder()
					.withOutput(channel.getInvertedIn())
					.withInputs(channel.getInvertedOut(), channel.getInvertedErr())
					//.withInputFilters(removeColors(), removeNonPrintable())
					.withExceptionOnFailure()
					.build();
			}
		} catch (Exception e) {
			logger.info("getExpect Error ---> {}"+e.getMessage(), e);
		}
	}
	
	/**
	 * 是否连接登录成功
	 * @return 是否登录成功；成功：true，失败：false
	 * @author Lei.Wu
	 * @date 2018-1-16 上午10:48:53
	 * @version V1.0
	 * @since ,Jdk1.8
	 * @copyright Copyright (c) 2018
	 */
	public boolean login(){
		return true;
	}
	
	/**
	 * 向终端发送命令
	 * @author Lei.Wu
	 * <span style="font-family: Arial, Helvetica, sans-serif;">@date 2018-1-16 上午10:48:53</span>
	 * @version V1.0
	 * @throws IOException 
	 * @since ,Jdk1.8
	 * @copyright Copyright (c) 2018
	 */
	public void write(String value) throws IOException {
		if (null == expect) {
			return;
		}
		try {
			expect.sendLine(value);
		} catch (Exception e) {
			logger.info("发命令异常:write({}){}"+value+e.getMessage(), e);
		}
	}
	
	/**
	 * 向终端发送命令
	 * @author Lei.Wu
	 * @date 2018-1-16 上午10:25:29
	 * @version V1.0
	 * @since ,Jdk1.8
	 * @copyright Copyright (c) 2018
	 */
	public String write(String value, String sprompt) {
		if (null == expect) {
			return "交换机返回异常";
		}
		try {
			expect.sendLine(value);
			return expect.expect(regexp(sprompt)).getInput();
		} catch (Exception e) {
			logger.info("交换机返回异常:write({}){}"+value+e.getMessage(), e);
		}
		return "交换机返回异常";
	}
	
	/**
	 * 读取
	 * @author Lei.Wu
	 * <span style="font-family: Arial, Helvetica, sans-serif;">@date 2018-1-16 上午10:48:53</span>
	 * @version V1.0
	 * @since ,Jdk1.8
	 * @copyright Copyright (c) 2018
	 */
	public String read() {
		try {
		} catch (Exception e) {
			logger.info("读取回显信息异常！{}"+e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 关闭连接
	 * @author Lei.Wu
	 * @date 2018-1-8 上午10:30:10
	 * @version V1.0
	 * @since Jdk1.8
	 * @copyright Copyright (c) 2018
	 */
	public void close(){
		try {
			if (null != expect) {
				expect.close();
			}
			if (channel.isOpen() || !channel.isClosing()) {
				channel.close(true);
			}
			if (null != session && session.isOpen()) {
				session.close(true);
			}
			if (null != client && client.isOpen()) {
				client.close(true);
			}
		} catch (Exception e) {
			logger.info("Close Error ---> {}"+e.getMessage(), e);
		}
	}
	
}
