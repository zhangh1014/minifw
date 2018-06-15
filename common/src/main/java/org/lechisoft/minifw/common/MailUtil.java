package org.lechisoft.minifw.common;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

public class MailUtil {

    private static String account; //登录用户名
    private static String password; //登录密码
    private static String host; //服务器地址（邮件服务器）
    private static String port; //端口
    private static String protocol; //协议

    static {
        URL url = MailUtil.class.getResource("/mail.properties");
        if (null == url) {
            MiniLogger.error("属性文件/mail.properties不存在");
        } else {
            File file = new File(url.getPath());
            Properties prop = new Properties();
            try {
                MiniLogger.debug("正在读取属性文件：/mail.properties");
                InputStream is = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                prop.load(isr);
                isr.close();
                is.close();

                account = prop.getProperty("account");
                password = prop.getProperty("password");
                host = prop.getProperty("host");
                port = prop.getProperty("port");
                protocol = prop.getProperty("protocol");

                MiniLogger.debug("读取属性文件成功");
            } catch (IOException e) {
                MiniLogger.error("读取属性文件发生异常");
            }
        }
    }

    public static void send(String to, String subject, String content, String fileStr) throws MessagingException {

        Properties prop = new Properties();
        prop.setProperty("mail.transport.protocol", protocol); // 协议
        prop.setProperty("mail.smtp.host", host); // 服务器
        prop.setProperty("mail.smtp.port", port); // 端口
        prop.setProperty("mail.smtp.auth", "true"); // 使用smtp身份验证

        // 使用SSL，企业邮箱必需！
        // 开启安全协议
        MailSSLSocketFactory sf;
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.socketFactory", sf);
        } catch (GeneralSecurityException e) {
            MiniLogger.error(e.getMessage());
            return;
        }

        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, password);
            }
        });
        session.setDebug(true);

        MimeMessage mimeMessage = new MimeMessage(session);

        mimeMessage.setFrom(new InternetAddress(account)); // 发件人
        // mimeMessage.setFrom(new InternetAddress(account,"Lechisoft")); // 可以设置发件人的别名
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); // 收件人
        mimeMessage.setSubject(subject); // 主题
        mimeMessage.setSentDate(new Date()); // 时间
        Multipart mp = new MimeMultipart(); // 容器类，可以包含多个MimeBodyPart对象
        MimeBodyPart body = new MimeBodyPart(); // MimeBodyPart可以包装文本，图片，附件
        body.setContent(content, "text/html; charset=UTF-8"); // HTML正文
        mp.addBodyPart(body);

        // 添加附件
        if (!"".equals(fileStr)) {
            body = new MimeBodyPart();
            try {
                body.attachFile(fileStr);
                mp.addBodyPart(body);
            } catch (IOException e) {
                MiniLogger.error(e.getMessage());
            }
        }

        mimeMessage.setContent(mp); // 设置邮件内容
        //仅仅发送文本
        //mimeMessage.setText(content);
        mimeMessage.saveChanges();
        Transport.send(mimeMessage);
    }
}