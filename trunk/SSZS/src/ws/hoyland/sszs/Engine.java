package ws.hoyland.sszs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

/**
 * 核心引擎，UI以及其他线程观察此Engine
 * @author Administrator
 *
 */
public class Engine extends Observable {
	
	private static Engine instance;
	private List<String> accounts;
	private List<String> mails;
	private boolean login = false;
	private boolean cptType = true;
	private boolean running = false;
	private ThreadPoolExecutor pool;
	private int mindex = 0;
	private int mcount = 0;
	private Configuration configuration = Configuration.getInstance();
	private int recc = 0;//reconnect count
	private int frecc = 0;//finished
	
	private int atrecc; //config data
	
	private Engine(){
		
	}
	
	public static Engine getInstance(){
		if(instance==null){
			instance = new Engine();
		}
		return instance;
	}

	/**
	 * 消息处理机制
	 * @param type
	 * @param message
	 */
	public void fire(EngineMessage message){
		int type = message.getType();
		Object data = message.getData();
		EngineMessage msg = null;
		
		switch(type){
			case EngineMessageType.IM_CONFIG_UPDATED:
				//暂不做处理
				//this.setChanged();
				break;
			case EngineMessageType.IM_USERLOGIN:
				uulogin(data);
				break;
			case EngineMessageType.IM_UL_STATUS:
				msg = new EngineMessage();
				
				this.setChanged();
				if(data==null){
					msg.setType(EngineMessageType.OM_LOGINING);
					this.notifyObservers(msg);
				}else{
					Object[] objs = (Object[])data;
					Integer it = (Integer)objs[0];
					if(it>0){						
						login = true;
						msg.setType(EngineMessageType.OM_LOGINED);
						msg.setData(data);
						this.notifyObservers(msg);
					ready();
					}else{
						login = false;
						msg.setType(EngineMessageType.OM_LOGIN_ERROR);
						msg.setData(data);
						this.notifyObservers(msg);
						ready();
					}
				}
				break;
			case EngineMessageType.IM_LOAD_ACCOUNT:
				String path = (String)message.getData();
				
				try {
					msg = new EngineMessage();
					msg.setType(EngineMessageType.OM_CLEAR_ACC_TBL);
					
					this.setChanged();
					this.notifyObservers(msg);
					
					accounts = new ArrayList<String>();
					File ipf = new File(path);
					FileInputStream is = new FileInputStream(ipf);
					InputStreamReader isr = new InputStreamReader(
							is);
					BufferedReader reader = new BufferedReader(isr);
					String line = null;
					int i = 1;
					while ((line = reader.readLine()) != null) {
						if (!line.equals("")) {
							line = i + "----" + line;
							accounts.add(line);
							List<String> lns = new ArrayList<String>();
							lns.addAll(Arrays.asList(line.split("----")));
							lns.add("初始化");
//							if (lns.size() == 3) {
//								lns.add("0");
//								lns.add("初始化");
//								//line += "----0----初始化";
//							} else {
//								//line += "----初始化";
//								lns.add("初始化");
//							}
							
							String[] items = new String[lns.size()];
					        lns.toArray(items);
					        					        
					        msg = new EngineMessage();
					        msg.setType(EngineMessageType.OM_ADD_ACC_TBIT);
					        msg.setData(items);
					        
					        this.setChanged();
							this.notifyObservers(msg);							
						}
						i++;
					}

					reader.close();
					isr.close();
					is.close();
					
					if (accounts.size() > 0) {
						List<String> params = new ArrayList<String>();
						params.add(String.valueOf(accounts.size()));
						params.add(path);
						
						msg = new EngineMessage();
				        msg.setType(EngineMessageType.OM_ACCOUNT_LOADED);
				        msg.setData(params);

				        this.setChanged();
						this.notifyObservers(msg);
					}

					ready();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			case EngineMessageType.IM_LOAD_MAIL:
				path = (String)message.getData();
				
				try {
					msg = new EngineMessage();
					msg.setType(EngineMessageType.OM_CLEAR_MAIL_TBL);
					
					this.setChanged();
					this.notifyObservers(msg);
					
					mails = new ArrayList<String>();
					File ipf = new File(path);
					FileInputStream is = new FileInputStream(ipf);
					InputStreamReader isr = new InputStreamReader(
							is);
					BufferedReader reader = new BufferedReader(isr);
					String line = null;
					int i = 1;
					while ((line = reader.readLine()) != null) {
						if (!line.equals("")) {
							line = i + "----" + line;
							mails.add(line);

							List<String> lns = new ArrayList<String>();
							lns.addAll(Arrays.asList(line.split("----")));
							lns.add("0");
//							if (lns.size() == 3) {
//								lns.add("0");
//								lns.add("初始化");
//								//line += "----0----初始化";
//							} else {
//								//line += "----初始化";
//								lns.add("初始化");
//							}
							
							String[] items = new String[lns.size()];
					        lns.toArray(items);
					        					        
					        msg = new EngineMessage();
					        msg.setType(EngineMessageType.OM_ADD_MAIL_TBIT);
					        msg.setData(items);
					        
					        this.setChanged();
							this.notifyObservers(msg);							
						}
						i++;
					}

					reader.close();
					isr.close();
					is.close();
					
					if (mails.size() > 0) {
						List<String> params = new ArrayList<String>();
						params.add(String.valueOf(mails.size()));
						params.add(path);
						
						msg = new EngineMessage();
				        msg.setType(EngineMessageType.OM_MAIL_LOADED);
				        msg.setData(params);

				        this.setChanged();
						this.notifyObservers(msg);
					}

					ready();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			case EngineMessageType.IM_CAPTCHA_TYPE:
				cptType = (Boolean)message.getData();
				ready();				
				break;
			case EngineMessageType.IM_PROCESS:
				running = !running;
				
				msg = new EngineMessage();
				msg.setType(EngineMessageType.OM_RUNNING);
				msg.setData(running);
				this.setChanged();
				this.notifyObservers(msg);
				
				if(running){
					int tc = Integer.parseInt(Configuration.getInstance().getProperty("THREAD_COUNT"));
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

					for (int i = 0; i < accounts.size(); i++) {
						try {
							Task task = new Task(accounts.get(i));
							Engine.getInstance().addObserver(task);
							pool.execute(task);
						} catch (ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
							//System.out.println(i + ":" + accounts.get(i));
						}
					}
				}else{
					//停止情况下的处理
					shutdown();
				}
				break;
			case EngineMessageType.IM_IMAGE_DATA:				
				msg = new EngineMessage();
				msg.setType(EngineMessageType.OM_IMAGE_DATA);
				msg.setData(message.getData());
				this.setChanged();
				this.notifyObservers(msg);
				
				break;
			case EngineMessageType.IM_REQUIRE_MAIL:
				
				//Random rnd = new Random();
				if(mcount==Integer.parseInt(configuration.getProperty("EMAIL_TIMES"))){
					mcount = 0;
					mindex++;
				}
				
				String[] ms = null;
				
				if(mindex<mails.size()){
					ms = mails.get(mindex).split("----");
					mcount++;
				}
				
				msg = new EngineMessage();
				msg.setTid(message.getTid());
				msg.setType(EngineMessageType.OM_REQUIRE_MAIL);
				msg.setData(ms);
				this.setChanged();
				this.notifyObservers(msg);
				
				break;
			case EngineMessageType.IM_INFO:
				msg = new EngineMessage();
				msg.setTid(message.getTid());
				msg.setType(EngineMessageType.OM_INFO);
				msg.setData(message.getData());
				
				this.setChanged();
				this.notifyObservers(msg);
				break;
			case EngineMessageType.IM_NO_EMAILS:
				shutdown();				
				break;
			case EngineMessageType.IM_START: //IM_FINISH
				recc++;
				atrecc = Integer.parseInt(configuration.getProperty("AUTO_RECON"));
				if(atrecc!=0&&atrecc==recc){//重拨的触发条件
					recc = 0;
					
					msg = new EngineMessage();
					msg.setTid(-1); //所有task
					msg.setType(EngineMessageType.OM_RECONN);
					//msg.setData(message.getData());
					
					this.setChanged();
					this.notifyObservers(msg);					
				}
				break;
			case EngineMessageType.IM_FINISH:
				frecc++;
				
				atrecc = Integer.parseInt(configuration.getProperty("AUTO_RECON"));
				if(atrecc!=0&&atrecc==frecc){//执行重拨
					frecc = 0;
					
					Thread t = new Thread(new Runnable(){

						final String account = configuration.getProperty("ADSL_ACCOUNT");
						final String password = configuration.getProperty("ADSL_PASSWORD");
						
						@Override
						public void run() {
							System.err.println("正在重拨");
							
							String cut = "rasdial 宽带连接 /disconnect";
							String link = "rasdial 宽带连接 "
									+ account
									+ " "
									+ password;
							try{
								Thread.sleep(1000*Integer.parseInt(configuration.getProperty("RECON_DELAY")));
								
								String result = execute(cut);
								
								if (result
										.indexOf("没有连接") == -1) {
									result = execute(link);
								}
							}catch(Exception e){
								e.printStackTrace();
							}
								
							synchronized(ReconObject.getInstance()){
								try{
									ReconObject.getInstance().notifyAll();
								}catch(Exception e){
									e.printStackTrace();
								}
							}
							
							System.err.println("重拨结束");
						}
						
						private String execute(String cmd) throws Exception {
							Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
							StringBuilder result = new StringBuilder();
							BufferedReader br = new BufferedReader(new InputStreamReader(
									p.getInputStream(), "GB2312"));
							String line;
							while ((line = br.readLine()) != null) {
								result.append(line + "\n");
							}
							return result.toString();
						}
						
					});
					
					t.start();
					
				}
				break;
			default:
				break;
		}
	}

	private void shutdown() {
		if(pool!=null){
			//pool.shutdown();
			pool.shutdownNow();	
		}		
	}

	@SuppressWarnings("unchecked")
	private void uulogin(Object message){
		final List<String> msg = (List<String>)message;
		
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				EngineMessage message = new EngineMessage();
				message.setType(EngineMessageType.IM_UL_STATUS);
				message.setData(null);
				
				Engine.getInstance().fire(message);
				
				Object[] data = new Object[3];
				
				int userID = 0;
				int score = 0;
				DM.INSTANCE.uu_setSoftInfoA(94034, "0c324570e9914c20ad2fab51b50b3fdc");				
				userID = DM.INSTANCE.uu_loginA(msg.get(0), msg.get(1));
				
				if(userID>0){
					//save config
					configuration.put("R_PWD", msg.get(2));
					configuration.put("AUTO_LOGIN", msg.get(3));
					if("true".equals(msg.get(2))){
						configuration.put("T_PWD", msg.get(1));
					}
					configuration.put("T_ACC", msg.get(0));
					configuration.save();
					
					score = DM.INSTANCE.uu_getScoreA(msg.get(0), msg.get(1)); 
					
				}
				
				data[0] = new Integer(userID);
				data[1] = new Integer(score);
				data[2] = msg.get(0);
				
				message = new EngineMessage();
				message.setType(EngineMessageType.IM_UL_STATUS);
				message.setData(data);
				Engine.getInstance().fire(message);
			}			
		});
		
		t.start();
	}
	
	private void ready(){
		if(accounts!=null&&accounts.size()>0&&mails!=null&&mails.size()>0&&(!cptType||(cptType&&login))){
			EngineMessage msg = new EngineMessage();
	        msg.setType(EngineMessageType.OM_READY);
	        this.setChanged();
			this.notifyObservers(msg);
		}else{
			EngineMessage msg = new EngineMessage();
	        msg.setType(EngineMessageType.OM_UNREADY);
	        this.setChanged();
			this.notifyObservers(msg);
		}
	}
}
