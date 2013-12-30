package ws.hoyland.qqol;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import ws.hoyland.util.Configuration;
import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;
import ws.hoyland.util.EngineMessage;


/**
 * 登录完成之后，就放到这里来，这里同时要到Heart处登记
 * @author Administrator
 *
 */
public class SocketLand {
	private static SocketLand instance; 
	private List<SSClient> clients = null;
	private ThreadPoolExecutor pool = null;
	
	private SocketLand(){
		this.clients = new ArrayList<SSClient>();
		// new pool
		int tc = 20000; //支持20000个号码
		int corePoolSize = tc;// minPoolSize
		int maxPoolSize = tc;
		int maxTaskSize = (1024 + 512) * 100 * 40;// 缓冲队列
		long keepAliveTime = 0L;
		TimeUnit unit = TimeUnit.MILLISECONDS;

		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
				maxTaskSize);
		RejectedExecutionHandler handler = new AbortPolicy();// 饱和处理策略
		
		// 创建线程池
		pool = new ThreadPoolExecutor(corePoolSize,
				maxPoolSize, keepAliveTime, unit,
				workQueue, handler);
	}
	
	public static SocketLand getInstance( ){
		if(instance==null){
			instance = new SocketLand();
		}
		return instance;
	}

	public void add(SSClient client){
		synchronized(clients){
			clients.add(client);
		}
		//pool.add
		pool.execute(new MonitorTask(client));
	}
	
	public List<SSClient> getClients(){
		return this.clients;
	}
}

//监视，并做处理；自动回复和0017（挤线等的处理）
class MonitorTask implements Runnable{
	private SSClient client;
	private byte[] buffer;
	private byte[] content;
	private byte[] decrypt;
	private byte[] encrypt;
	
	private byte[] buf;
	
	private short seq = 0x1123;
	private int status = 1;
	private boolean relogin = false;
	
	private DatagramPacket dpIn = null;
	private DatagramPacket dpOut = null;
	private ByteArrayOutputStream bsofplain = null;
	private ByteArrayOutputStream baos = null;
	
	private boolean run = true;
	
	private static DateFormat format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static byte[] msgs = null;
	private static Crypter crypter = new Crypter();;
	private static Random rnd = new Random();
	private static int max = 0xFFFF;
	private static int port = 8000;
	private static String tflag = "4D53470000000000";

	static{
		try{
			msgs = Configuration.getInstance().getProperty("LEFT_MSG").getBytes("UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static byte[][] bs = new byte[][]{
		new byte[]{0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2},
		new byte[]{0x03},
		new byte[]{
				0x00, 0x00, 0x00, 0x0D, 0x00, 0x01, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x01,
				0x34, 0x4B
		},
		new byte[]{
				0x00, 0x0B
		},
		new byte[]{
				//0x52,(byte)0xB9,0x0E,(byte)0x94,  //时间
				0x02,0x37,  //图标？
				0x00,0x00,0x00,
				0x00,  //是否有字体
				0x01,  //分片数
				0x00,  //分片索引
				0x00,0x00, //消息ID
				0x02,  //02,0xauto,0xreply,,0x01,0xnormal
				0x4D,0x53,0x47,0x00,0x00,0x00,0x00,0x00,  //fix
		},
		new byte[]{		
				0x00,
				0x00,0x00,0x00,  //字体颜色
				0x0A,  //字体大小
				0x00,  //其他属性
				(byte)0x86,0x02,  //字符集
				0x00,0x06,  //字体名称长度
				(byte)0xE5,(byte)0xAE,(byte)0x8B,(byte)0xE4,(byte)0xBD,(byte)0x93,  //宋体
				0x00,0x00,0x01, 
				0x00,(byte)(msgs.length+3),  //消息快总长度
				0x01,  //消息快编号
				0x00,(byte)msgs.length,  //消息快大小
		},		
	};	
	
	public MonitorTask(SSClient client){
		this.client = client;
		this.status = Integer.parseInt(Configuration.getInstance().getProperty("LOGIN_TYPE"));
		this.seq = (short)rnd.nextInt(max);
	}
	
	@Override
	public void run() {
		try{
			while(run){
				//00CE接收消息
				//--------------------------------------------------
				buffer = new byte[1024];
				dpIn = new DatagramPacket(buffer, buffer.length);
				try{
					client.getDs().receive(dpIn);
				}catch(SocketException e){
					//e.printStackTrace();
					System.err.println(e.getMessage());
					if("socket closed".equals(e.getMessage())){
						return;
					}
				}
				buffer = Util.pack(buffer);
//				System.out.println("P1:"+buffer.length);
//				System.out.println(Converts.bytesToHexString(buffer));
				
				//最后活动
				infoact();
				byte[] header = Util.slice(buffer, 3, 2);
				byte[] rh = Util.slice(buffer, 0, 11);
				System.out.println("RECV["+Converts.bytesToHexString(header)+"]:"+buffer.length);
				//System.out.println(Converts.bytesToHexString(buffer));
				boolean nmsg = false;//是否是新消息
				if(header[0]==0x00&&header[1]==(byte)0xCE){
					content = Util.slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, client.getSessionKey());
					//System.out.println("00CE[RECV]:"+decrypt.length);
					//System.out.println(Converts.bytesToHexString(decrypt));
					
					//判断时间
					String rbof00CE = Converts.bytesToHexString(decrypt);
					byte[] cetime = Util.slice(decrypt, rbof00CE.indexOf(tflag)/2+8, 0x04);
					long lcetime = Long.parseLong(Converts.bytesToHexString(cetime), 16)*1000;
					//System.out.println("P5:"+(System.currentTimeMillis()-lcetime));
					if(System.currentTimeMillis()-lcetime<=1000*60){//一分钟内							
						nmsg = true;
					}else{
						nmsg = false;
					}
					//XYZ
					//System.out.println("P3:"+lcetime);
					
					//00CE的反馈
					bsofplain = new ByteArrayOutputStream();
					bsofplain.write(Util.slice(decrypt, 0, 0x010));
					encrypt = crypter.encrypt(bsofplain.toByteArray(), client.getSessionKey());
										
					baos = new ByteArrayOutputStream();
					baos.write(rh);
					baos.write(bs[0]);
					baos.write(encrypt);
					baos.write(bs[1]);
					
					buf = baos.toByteArray();
					
					//System.out.println("00CE[SEND]["+Converts.bytesToHexString(client.getSessionKey())+"]");
					//System.out.println(Converts.bytesToHexString(baos.toByteArray()));
					
					dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(client.getIp()), port);
					client.getDs().send(dpOut);
					
					//0319 ? 01C0?
					//表示已经读取本消息 //TODO
					//----------------------------------------------
					
					//00CD回复消息
					//----------------------------------------------
					//消息处理 0319 已读
					if((status==1||status==2)&&nmsg&&("true".equals(Configuration.getInstance().getProperty("AUTO_REPLY")))){ //需要自动回复
						seq++;
						
						byte[] time = Converts.hexStringToByte(Long.toHexString(System.currentTimeMillis()/1000).toUpperCase());
												
						bsofplain = new ByteArrayOutputStream();
						bsofplain.write(Util.slice(decrypt, 4, 0x04));
						bsofplain.write(Util.slice(decrypt, 0, 0x04));
						bsofplain.write(bs[2]);
						bsofplain.write(Util.slice(decrypt, 4, 0x04));
						bsofplain.write(Util.slice(decrypt, 0, 0x04));
	
						byte[] tn = Util.slice(decrypt, 0, 0x04);
						byte[] msghd = new byte[20];
						for(int i=0;i<4;i++){ 
							msghd[i] = tn[i];
						}
						for(int i=0;i<client.getSessionKey().length;i++){
							msghd[i+4] = client.getSessionKey()[i];
						}
						bsofplain.write(Converts.MD5Encode(msghd));
						bsofplain.write(bs[3]);
						bsofplain.write(Converts.hexStringToByte(Integer.toHexString(seq+0x1111).toUpperCase()));
											bsofplain.write(time);
						bsofplain.write(bs[4]);
						bsofplain.write(time);
								//0x52,(byte)0xB9,0x0E,(byte)0x94,  //发送时间
								//(byte)0xBE,(byte)0x97,0x4A,(byte)0x92,  //随机？
						bsofplain.write(Util.genKey(4));
						bsofplain.write(bs[5]);
//						bsofplain.write(new byte[]{	
//								0x00,(byte)msgs.length,  //消息快大小
//								//0x61,0x73,0x64,0x66  //消息
//								//(byte)0xE4,(byte)0xBD,(byte)0xA0,(byte)0xE5,(byte)0xA5,(byte)0xBD,(byte)0xEF,(byte)0xBC,(byte)0x8C,(byte)0xE6,(byte)0x88,(byte)0x91,(byte)0xE7,(byte)0x8E,(byte)0xB0,(byte)0xE5,(byte)0x9C,(byte)0xA8,(byte)0xE4,(byte)0xB8,(byte)0x8D,(byte)0xE5,(byte)0x9C,(byte)0xA8,0x21
//						});
						bsofplain.write(msgs);
						
						encrypt = crypter.encrypt(bsofplain.toByteArray(), client.getSessionKey());
											
						baos = new ByteArrayOutputStream();
						baos.write(new byte[]{
								0x02, 0x34, 0x4B, 0x00, (byte)0xCD
						});
						baos.write(Converts.hexStringToByte(Integer.toHexString(seq).toUpperCase()));
						baos.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(client.getAccount())).toUpperCase()));
						baos.write(bs[0]);
						baos.write(encrypt);
						baos.write(bs[1]);
						
						buf = baos.toByteArray();
						
//						System.out.println("00CD["+Converts.bytesToHexString(client.getSessionKey())+"]");
//						System.out.println(Converts.bytesToHexString(baos.toByteArray()));
						
						dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(client.getIp()), port);
						client.getDs().send(dpOut);
						
						//IN: not need
						/**
						buffer = new byte[1024];
						dpIn = new DatagramPacket(buffer, buffer.length);
										
						ds.receive(dpIn);
						
						buffer = pack(buffer);
						System.out.println(buffer.length);
						System.out.println(Converts.bytesToHexString(buffer));
						**/
					}
				}else if(header[0]==0x00&&header[1]==(byte)0x62){ //0017, 0062的处理
					//已经离线					
					quit();//相关处理
				}else if(header[0]==0x00&&header[1]==(byte)0x58){ //收到心跳了
					//已经离线					
					//quit();//相关处理
					client.setHeart(true);
				}else if(header[0]==0x00&&header[1]==(byte)0x17&&buffer.length==231&&!relogin){
					relogin = true;//不再处理0017
					content = Util.slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, client.getSessionKey());
					if(decrypt==null){
						System.err.println("0017 decrypt null");
						System.err.println(Converts.bytesToHexString(buffer));
						System.err.println(Converts.bytesToHexString(content));
						System.err.println(Converts.bytesToHexString(client.getSessionKey()));
					}
//					System.err.println("0017[RECV]:"+decrypt.length);
//					System.err.println(Converts.bytesToHexString(decrypt));
					
					if(decrypt!=null&&decrypt[0]==0x00&&decrypt[1]==0x00){ //00 00 27 10
						//被挤掉下线
						info("被挤线，等待重新登录");
						//0017的反馈
						//------------------
						bsofplain = new ByteArrayOutputStream();
						bsofplain.write(Util.slice(decrypt, 0, 0x010));
						encrypt = crypter.encrypt(bsofplain.toByteArray(), client.getSessionKey());
											
						baos = new ByteArrayOutputStream();
						baos.write(rh);
						baos.write(bs[0]);
						baos.write(encrypt);
						baos.write(bs[1]);
						
						buf = baos.toByteArray();
						
//						System.out.println("0017[SEND]["+Converts.bytesToHexString(client.getSessionKey())+"]");
//						System.out.println(Converts.bytesToHexString(baos.toByteArray()));
						
						dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(client.getIp()), port);
						client.getDs().send(dpOut);
						
						//相关处理
						quit();						
						//IM_RELOGIN
						new Thread(new Runnable(){
							@Override
							public void run() {
								try{
									Thread.sleep(1000*60*Integer.parseInt(Configuration.getInstance().getProperty("EX_ITV")));
								}catch(Exception e){
									e.printStackTrace();
								}
								EngineMessage message = new EngineMessage();
								message.setTid(MonitorTask.this.client.getId());
								message.setType(EngineMessageType.IM_RELOGIN);
																
								Engine.getInstance().fire(message);
							}						
						}).start();						
					}//其他情况不做处理					
				}else{
					//			
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	private void quit(){
		//删除此client，在clients中
		synchronized(SocketLand.getInstance().getClients()){
			SocketLand.getInstance().getClients().remove(this.client);
		}
		//关闭socket
		try{
			client.getDs().close();
		}catch(Exception e){
			e.printStackTrace();
		}
		//终止当前线程
		run = false;
		//通知引擎任务完成
		EngineMessage message = new EngineMessage();
		message.setTid(client.getId());
		message.setType(EngineMessageType.IM_TF);
		//message.setData(info);
		Engine.getInstance().fire(message);
	}
	
	private void info(String info){
		EngineMessage message = new EngineMessage();
		message.setTid(client.getId());
		message.setType(EngineMessageType.IM_INFO);
		message.setData(info);

		//DateFormat format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String tm = format.format(new Date());
//		
		System.err.println("["+client.getAccount()+"]"+info+"("+tm+")");
		Engine.getInstance().fire(message);
	}
	
	private void infoact(){		
		EngineMessage message = new EngineMessage();
		message.setTid(client.getId());
		message.setType(EngineMessageType.IM_INFOACT);
		message.setData(format.format(new Date()));

		
//		System.err.println("["+this.account+"]ACT("+tm+")");
		Engine.getInstance().fire(message);
	}
	
}