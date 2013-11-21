package ws.hoyland.sszs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Task implements Runnable, Observer {

	private String line;
	private boolean run = false;
	private boolean fb = false; // break flag;
	private boolean fc = false; // continue flag;
	private int idx = 0; // method index;

	// private boolean block = false;
	// private TaskObject obj = null;

	private DefaultHttpClient client;
	private HttpPost post = null;
	private HttpGet get = null;

	private HttpResponse response = null;
	private HttpEntity entity = null;
	private JSONObject json = null;
	// private HttpUriRequest request = null;
	private List<NameValuePair> nvps = null;

	private String sig = null;
	// private byte[] ib = null;
	// private byte[] image = null;

	private EngineMessage message = null;
	private int id = 0;
	private String account = null;
	protected String password = null;

	private ByteArrayOutputStream baos = null;
	private int codeID = -1;
	private String result;
	
	private String rc = null; //red code in mail

	protected String mid = null;
	private String mail = null;
	private String mpwd = null;

	private final String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";

	public Task(String line) {
		// TODO Auto-generated constructor stub
		String[] ls = line.split("----");
		this.id = Integer.parseInt(ls[0]);
		this.account = ls[1];
		this.password = ls[2];

		this.run = true;

		client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
	}

	@Override
	public void run() {
		// System.out.println(line);
		while (run) {
			if (fb) {
				break;
			}
			if (fc) {
				continue;
			}

			// if(block){
			// synchronized (obj.getBlock()) {
			// try {
			// obj.getBlock().wait();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			// block = false;
			// }

			process(idx);

			try {
				if (entity != null) {
					EntityUtils.consume(entity);
				}
				if (get != null) {
					get.releaseConnection();
				}
				if (post != null) {
					post.releaseConnection();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Engine.getInstance().deleteObserver(this);
	}

	private void process(int index) {
		switch (index) {
		case 0:
			try {
				get = new HttpGet(
						"http://captcha.qq.com/getsig?aid=523005413&uin=0&"
								+ Math.random());

				get.setHeader("User-Agent", UAG);
				get.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_index");
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				sig = line.substring(20, line.indexOf(";    "));
				// System.out.println(sig);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 1:
			try {
				get = new HttpGet("http://captcha.qq.com/getimgbysig?sig="
						+ URLEncoder.encode(this.sig, "UTF-8"));

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();

				DataInputStream in = new DataInputStream(entity.getContent());
				baos = new ByteArrayOutputStream();
				byte[] barray = new byte[1024];
				int size = -1;
				while ((size = in.read(barray)) != -1) {
					baos.write(barray, 0, size);
				}
				ByteArrayInputStream bais = new ByteArrayInputStream(
						baos.toByteArray());

				message = new EngineMessage();
				message.setType(EngineMessageType.IM_IMAGE_DATA);
				message.setData(bais);

				Engine.getInstance().fire(message);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 2:
			// 根据情况，阻塞或者提交验证码到UU
			try {
				byte[] by = baos.toByteArray();
				byte[] resultByte = new byte[30]; // 为识别结果申请内存空间
				codeID = DM.INSTANCE.uu_recognizeByCodeTypeAndBytesA(by,
						by.length, 1, resultByte); // 调用识别函数,resultBtye为识别结果
				result = new String(resultByte, "UTF-8").trim();

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 3:
			try {
				get = new HttpGet("http://aq.qq.com/cn2/appeal/appeal_index");

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();

				// line = EntityUtils.toString(entity);
				// System.out.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 4:
			try {
				get = new HttpGet(
						"http://aq.qq.com/cn2/appeal/appeal_check_assist_account?UserAccount="
								+ this.account);

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");
				get.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_index");

				response = client.execute(get);
				entity = response.getEntity();

				// line = EntityUtils.toString(entity);
				// System.out.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 5:
			try {
				get = new HttpGet(
						"http://aq.qq.com/cn2/ajax/check_verifycode?session_type=on_rand&verify_code="
								+ result + "&appid=523005413&CaptchaSig="
								+ URLEncoder.encode(this.sig, "UTF-8"));

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");
				get.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_index");

				response = client.execute(get);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				json = new JSONObject(line);

				// System.out.println(line);
				if ("0".equals(json.getString("Err"))) {
					idx += 2;
				} else {
					// 报错
					idx++;
				}
				// System.out.println(line);

			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 6:
			try {
				int reportErrorResult = DM.INSTANCE.uu_reportError(codeID);
				System.out.println(reportErrorResult);
				// TODO, send to UI
				idx = 0; // 重新开始
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 7:
			try {
				post = new HttpPost(
						"http://aq.qq.com/cn2/appeal/appeal_contact");

				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Accept", "text/html, */*");
				post.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_index");

				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("qqnum", this.account));
				nvps.add(new BasicNameValuePair("verifycode2", result));
				nvps.add(new BasicNameValuePair("CaptchaSig", this.sig));

				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				// line = EntityUtils.toString(entity);

				// System.out.println(line);

				// 发送消息，提示Engine，需要邮箱
				// obj = new TaskObject();
				EngineMessage message = new EngineMessage();
				message.setTid(this.id);
				message.setType(EngineMessageType.IM_REQUIRE_MAIL);
				// message.setData(obj);
				Engine.getInstance().fire(message);

				// block = true;

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 8:
			try {
				post = new HttpPost(
						"http://aq.qq.com/cn2/appeal/appeal_contact_confirm");

				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Accept", "text/html, */*");
				post.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_contact");

				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("txtLoginUin", this.account));
				nvps.add(new BasicNameValuePair("txtCtCheckBox", "0"));
				nvps.add(new BasicNameValuePair("txtName", Names.getInstance()
						.getName()));
				nvps.add(new BasicNameValuePair("txtAddress", ""));
				nvps.add(new BasicNameValuePair("txtIDCard", ""));
				nvps.add(new BasicNameValuePair("txtContactQQ", ""));
				nvps.add(new BasicNameValuePair("txtContactQQPW", ""));
				nvps.add(new BasicNameValuePair("txtContactQQPW2", ""));
				nvps.add(new BasicNameValuePair("radiobutton", "mail"));
				nvps.add(new BasicNameValuePair("txtContactEmail", this.mail));
				nvps.add(new BasicNameValuePair("txtContactMobile", "请填写您的常用手机"));

				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				// line = EntityUtils.toString(entity);

				// System.out.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 9: // 收邮件
			try {
				Thread.sleep(1000*5);
				
				Properties props = new Properties();
				props.setProperty("mail.store.protocol", "pop3");
				props.setProperty("mail.pop3.host", "pop3.163.com");
				Session session = Session.getDefaultInstance(props);
				Store store = session.getStore("pop3");
				store.connect(this.mail, this.mpwd);
				Folder folder = store.getFolder("INBOX");
				folder.open(Folder.READ_WRITE);

				// 全部邮件
				Message[] messages = folder.getMessages();
				
				//System.out.println(messages.length);
				for (int i = 0; i < messages.length; i++) {
					Message message = messages[i];
					// 删除邮件
					// message.setFlag(Flags.Flag.DELETED,true);
					if(message.getSubject().startsWith("QQ号码申诉联系方式确认")){
						message.setFlag(Flags.Flag.SEEN,true);	// 标记为已读
						String ssct = (String)message.getContent();
						rc = ssct.substring(ssct.indexOf("<b class=\"red\">")+15, ssct.indexOf("<b class=\"red\">")+23);
						
						System.out.println(rc);
						break;
					}					
				}
				folder.close(true);
				store.close();
				
				if(rc==null){
					idx = 9;
				}else{
					idx++;
				}
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 10:
			try {
				get = new HttpGet(
						"http://aq.qq.com/cn2/appeal/appeal_mail_code_verify?VerifyType=0&VerifyCode="
								+ this.rc);

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");
				get.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_contact_confirm");

				response = client.execute(get);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				json = new JSONObject(line);

				System.out.println(line);
				if ("1".equals(json.getString("ret_code"))) {
					// 验证成功
					System.out.println("验证成功");
				} else {
					// 报错, 重新开始
					System.out.println("验证失败");
					idx = 0;
				}
				// System.out.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 11:
			try {
				post = new HttpPost(
						"http://aq.qq.com/cn2/appeal/appeal_historyinfo_judge");

				post.setHeader("Connection", "keep-alive");
				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Accept", "text/html, */*");
				post.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_contact_confirm");
										
				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("txtBackToInfo", "1"));
				nvps.add(new BasicNameValuePair("txtEmail", this.mail));
				nvps.add(new BasicNameValuePair("txtUin", this.account));
				nvps.add(new BasicNameValuePair("txtBackFromFd", ""));
				nvps.add(new BasicNameValuePair("txtEmailVerifyCode", this.rc));
				nvps.add(new BasicNameValuePair("txtOldPW1", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW1", ""));
				nvps.add(new BasicNameValuePair("txtOldPW2", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW2", "13421829035"));
				nvps.add(new BasicNameValuePair("txtOldPW3", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW3", ""));
				nvps.add(new BasicNameValuePair("txtOldPW4", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW4", ""));
				nvps.add(new BasicNameValuePair("txtOldPW5", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW5", ""));
				nvps.add(new BasicNameValuePair("txtOldPW6", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW6", ""));
				
				nvps.add(new BasicNameValuePair("ddlLoginLocCountry1", "0"));
				nvps.add(new BasicNameValuePair("ddlLoginLocProvince1", "19"));
				nvps.add(new BasicNameValuePair("ddlLoginLocCity1", "21"));
				nvps.add(new BasicNameValuePair("txtLoginLocCountry1", "国家"));
				nvps.add(new BasicNameValuePair("txtLoginLocProvince1", "省份"));
				nvps.add(new BasicNameValuePair("txtLoginLocCity1", "城市"));
				
				nvps.add(new BasicNameValuePair("ddlLoginLocCountry2", "0"));
				nvps.add(new BasicNameValuePair("ddlLoginLocProvince2", "19"));
				nvps.add(new BasicNameValuePair("ddlLoginLocCity2", "21"));
				nvps.add(new BasicNameValuePair("txtLoginLocCountry2", "国家"));
				nvps.add(new BasicNameValuePair("txtLoginLocProvince2", "省份"));
				nvps.add(new BasicNameValuePair("txtLoginLocCity2", "城市"));
				
				nvps.add(new BasicNameValuePair("ddlLoginLocCountry3", "0"));
				nvps.add(new BasicNameValuePair("ddlLoginLocProvince3", "19"));
				nvps.add(new BasicNameValuePair("ddlLoginLocCity3", "21"));
				nvps.add(new BasicNameValuePair("txtLoginLocCountry3", "国家"));
				nvps.add(new BasicNameValuePair("txtLoginLocProvince3", "省份"));
				nvps.add(new BasicNameValuePair("txtLoginLocCity3", "城市"));
				
				nvps.add(new BasicNameValuePair("ddlLocYear4", ""));
				nvps.add(new BasicNameValuePair("ddlLoginLocCountry4", "0"));
				nvps.add(new BasicNameValuePair("ddlLoginLocProvince4", "-1"));
				nvps.add(new BasicNameValuePair("ddlLoginLocCity4", "-1"));
				nvps.add(new BasicNameValuePair("txtLoginLocCountry4", "国家"));
				nvps.add(new BasicNameValuePair("txtLoginLocProvince4", "省份"));
				nvps.add(new BasicNameValuePair("txtLoginLocCity4", "城市"));
				
				nvps.add(new BasicNameValuePair("ddlRegType", "0"));
				nvps.add(new BasicNameValuePair("ddlRegYear", ""));
				nvps.add(new BasicNameValuePair("ddlRegMonth", ""));
				nvps.add(new BasicNameValuePair("ddlRegCountry", "0"));
				nvps.add(new BasicNameValuePair("ddlRegProvince", "-1"));
				nvps.add(new BasicNameValuePair("ddlRegCity", "-1"));
				nvps.add(new BasicNameValuePair("txtRegCountry", "国家"));
				nvps.add(new BasicNameValuePair("txtRegProvince", "省份"));
				nvps.add(new BasicNameValuePair("txtRegCity", "城市"));
				nvps.add(new BasicNameValuePair("txtRegMobile", ""));
				nvps.add(new BasicNameValuePair("ddlRegPayMode", "0"));
				nvps.add(new BasicNameValuePair("txtRegPayAccount", ""));
				
				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);

//				System.out.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 12:
			try {
				get = new HttpGet(
						"http://aq.qq.com/cn2/appeal/appeal_mb2verify");

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();

//				line = EntityUtils.toString(entity);
//				System.out.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 13:
			try {
				post = new HttpPost(
						"http://aq.qq.com/cn2/appeal/appeal_invite_friend");

				post.setHeader("Connection", "keep-alive");
				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Accept", "text/html, */*");
				post.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_mb2verify");
						
				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("txtUserChoice", "2"));
				nvps.add(new BasicNameValuePair("txtOldDNAEmailSuffix", ""));
				nvps.add(new BasicNameValuePair("txtOldDNAAnswer3", ""));
				nvps.add(new BasicNameValuePair("txtOldDNAAnswer2", ""));
				nvps.add(new BasicNameValuePair("txtOldDNAAnswer1", ""));
				nvps.add(new BasicNameValuePair("txtBackToInfo", "1"));
				nvps.add(new BasicNameValuePair("txtBackFromFd", ""));
				nvps.add(new BasicNameValuePair("OldDNAMobile", ""));
				nvps.add(new BasicNameValuePair("OldDNAEmail", ""));
				nvps.add(new BasicNameValuePair("OldDNACertCardID", ""));
				
				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				// line = EntityUtils.toString(entity);

				// System.out.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 14:
			try {
				post = new HttpPost(
						"http://aq.qq.com/cn2/appeal/appeal_end");

				post.setHeader("Connection", "keep-alive");
				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Accept", "text/html, */*");
				post.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_invite_friend");
					
				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("txtPcMgr", "1"));
				nvps.add(new BasicNameValuePair("txtUserPPSType", "1"));
				nvps.add(new BasicNameValuePair("txtBackFromFd", "1"));
				nvps.add(new BasicNameValuePair("txtBackToInfo", "1"));
				nvps.add(new BasicNameValuePair("usernum", this.account));
				nvps.add(new BasicNameValuePair("FriendQQNum1", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum2", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum3", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum4", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum5", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum6", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum7", ""));

				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);

				System.out.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Observable obj, Object arg) {
		final EngineMessage msg = (EngineMessage) arg;

		if (msg.getTid() == this.id) {
			int type = msg.getType();

			switch (type) {
			case EngineMessageType.OM_REQUIRE_MAIL:
				String[] ms = (String[]) msg.getData();
				System.out.println(ms[0] + "/" + ms[1] + "/" + ms[2]);
				this.mid = ms[0];
				this.mail = ms[1];
				this.mpwd = ms[2];
				break;
			default:
				break;
			}
		}
	}
}