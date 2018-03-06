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
 * SSH
 * @author Joyce.Luo 
 * @date 2016-8-8 锟斤拷锟斤拷09:12:51 
 * @version V3.0 
 * @since Tomcat6.0,Jdk1.6 
 * @copyright Copyright (c) 2016 
 */  
public class SwitchSSHUtils2 {  
    private static final Logger logger = Logger.getLogger(SwitchSSHUtils2.class);
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
      
    public SwitchSSHUtils2(String host) {  
        super();  
        this.host = host;  
    }  
  
    public SwitchSSHUtils2(String host, int port) {  
        this.host = host;  
        this.port = port;  
    }  
      
    public SwitchSSHUtils2(String host, int port, int timeout) {  
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
     * SSH锟斤拷锟斤拷 
     * @author Joyce.Luo 
     * @date 2016-8-8 锟斤拷锟斤拷10:13:16 
     * @version V3.0 
     * @since Tomcat6.0,Jdk1.6 
     * @copyright Copyright (c) 2016 
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
        	logger.info("SSH Connect Error --> {}"+ e.getMessage(), e);  
        }  
        return false;  
    }  
      
    /** 
     * 锟斤拷锟紼xpect锟斤拷锟襟，该讹拷锟矫匡拷锟斤拷锟斤拷SSH锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷 
     * @author Joyce.Luo 
     * @date 2016-8-16 锟斤拷锟斤拷10:17:03 
     * @version V3.0 
     * @since Tomcat6.0,Jdk1.6 
     * @copyright Copyright (c) 2016 
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
                    .withLineSeparator("\n")
                    //.withInputFilters(removeColors(), removeNonPrintable())  
                    //.withExceptionOnFailure()  
                    .build();  
            }  
        } catch (Exception e) {  
            logger.info("getExpect Error ---> {}"+e.getMessage(), e);  
        }  
    }  
      
    /** 
     * 锟角凤拷锟斤拷锟接碉拷录锟缴癸拷 
     * @return 锟角凤拷锟铰硷拷晒锟斤拷锟斤拷晒锟斤拷锟絫rue锟斤拷失锟杰ｏ拷false 
     * @author Joyce.Luo 
     * @date 2016-8-16 锟斤拷锟斤拷10:48:53 
     * @version V3.0 
     * @since Tomcat6.0,Jdk1.6 
     * @copyright Copyright (c) 2016 
     */  
    public boolean login(){  
        return true;  
    }  
      
    /** 
     * 锟斤拷锟秸端凤拷锟斤拷锟斤拷锟斤拷 
     * @author Joyce.Luo 
     * <span style="font-family:Arial, Helvetica, sans-serif;">@date 2016-8-16 锟斤拷锟斤拷10:48:53</span> 
     * @version V3.0 
     * @throws IOException  
     * @since Tomcat6.0,Jdk1.6 
     * @copyright Copyright (c) 2016 
     */  
    public void write(String value) throws IOException {  
        if (null == expect) {  
            return;  
        }  
        try {  
            expect.sendLine(value);  
        } catch (Exception e) {  
            logger.info("锟斤拷锟斤拷锟斤拷锟届常:write({}){}"+value+e.getMessage(), e);  
        }  
    }  
      
    /** 
     * 锟斤拷锟秸端凤拷锟斤拷锟斤拷锟斤拷 
     * @author Joyce.Luo 
     * @date 2016-8-16 锟斤拷锟斤拷10:25:29 
     * @version V3.0 
     * @since Tomcat6.0,Jdk1.6 
     * @copyright Copyright (c) 2016 
     */  
    public String write(String value, String sprompt) {  
        if (null == expect) {  
            return "锟斤拷锟轿拷锟�";  
        }  
        try {  
            expect.sendLine(value);  
            return expect.expect(regexp(sprompt)).getInput();  
        } catch (Exception e) {  
            logger.info("锟斤拷锟斤拷锟斤拷锟届常:write({}){}"+value+e.getMessage(), e);  
        }  
        return "commands 锟届常";  
    }  
      
    /** 
     * 锟斤拷取 
     * @author Joyce.Luo 
     * <span style="font-family:Arial, Helvetica, sans-serif;">@date 2016-8-16 锟斤拷锟斤拷10:48:53</span> 
     * @version V3.0 
     * @since Tomcat6.0,Jdk1.6 
     * @copyright Copyright (c) 2016 
     */  
    public String read() {  
        try {  
        } catch (Exception e) {  
            logger.info("鍛戒护閿欒{}"+ e.getMessage(), e);  
        }  
        return null;  
    }  
      
    /** 
     * 锟截憋拷锟斤拷锟斤拷 
     * @author Joyce.Luo 
     * @date 2016-8-8 锟斤拷锟斤拷10:30:10 
     * @version V3.0 
     * @since Tomcat6.0,Jdk1.6 
     * @copyright Copyright (c) 2016 
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
    }}