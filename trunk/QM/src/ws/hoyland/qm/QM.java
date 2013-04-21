package ws.hoyland.qm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class QM {

	protected Shell shlQqmail;
	private Table table;
	private Text text_1;
	private Option option;
	private Text text_2;
	private Link link;
	private Link link_4;
	private Label label_3;
	private Label label_4;
	private Link link_2;
	private Button button;
	private Label label_1;
	private Link link_3;
	private Button button_1;
	private Link link_1;
	private Label lblMaxOfMax;
	private Label label_10;
	private Label lblOfMax;
	private Label label_12;
	private Label label_13;
	private Label label_15;
	private Label lblNewLabel;
	private Text text;
	private boolean flag = false;
	private Button btnCheckButton;
	private Label label_6;
	private List<String> ns = null;
	private List<String> proxies = null;
	private int pc = 0;
	private Label label;
	private ThreadPoolExecutor pool;
	private TimerTask timerTask;
	private Task ctask;
//	private Map<Image, Task> list = new HashMap<Image, Task>();
//	private Image image = null;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			QM window = new QM();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlQqmail.open();
		shlQqmail.layout();
		while (!shlQqmail.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlQqmail = new Shell(Display.getDefault(), SWT.SHELL_TRIM ^ SWT.MAX
				^ SWT.RESIZE);
		shlQqmail.setSize(878, 589);
		shlQqmail.setText("QQMail");

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shlQqmail.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shlQqmail.setLocation(x, y);

		label = new Label(shlQqmail, SWT.NONE);
		label.setBounds(10, 10, 164, 17);
		label.setText("帐号列表 (共 0 条):");

		table = new Table(shlQqmail, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 33, 474, 320);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnId = new TableColumn(table, SWT.NONE);
		tblclmnId.setWidth(30);
		tblclmnId.setText("ID");

		TableColumn tblclmnAccount = new TableColumn(table, SWT.NONE);
		tblclmnAccount.setWidth(70);
		tblclmnAccount.setText("帐号");

		TableColumn tblclmnPassword = new TableColumn(table, SWT.NONE);
		tblclmnPassword.setWidth(70);
		tblclmnPassword.setText("密码");

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(70);
		tblclmnNewColumn.setText("索引");

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(70);
		tblclmnNewColumn_1.setText("状态");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(70);
		tblclmnNewColumn_2.setText("群数");

		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_3.setWidth(70);
		tblclmnNewColumn_3.setText("成功");

		link = new Link(shlQqmail, SWT.NONE);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDlg = new FileDialog(shlQqmail, SWT.OPEN);
				// fileDlg.setFilterExtensions(new String[]{"*.torrent"});
				fileDlg.setFilterPath(null);
				fileDlg.setText("选择帐号文件");
				String filePath = fileDlg.open();
				if (filePath != null) {
					try {
						ns = new ArrayList<String>();
						File ipf = new File(filePath);
						FileInputStream is = new FileInputStream(ipf);
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader reader = new BufferedReader(isr);
						String line = null;
						int i = 1;
						while ((line = reader.readLine()) != null) {
							line = line.trim();
							if (!line.equals("")) {
								ns.add(line);
								line = i + "----" + line;
								String[] items = line.split("----");
								if (items.length == 3) {
									line += "----0----初始化";
								} else {
									line += "----初始化";
								}
								items = line.split("----");
								TableItem tableItem = new TableItem(table,
										SWT.NONE);
								tableItem.setText(items);
							}
							i++;
							// System.out.println(line);
						}
						// pc = proxies.size();

						reader.close();
						isr.close();
						is.close();
						// System.out.println(ns.size());
						if (ns.size() > 0) {
							label.setText("帐号列表 (共 " + ns.size() + " 条):");
							if (!"".equals(text_2.getText())
									&& ((btnCheckButton.getSelection()
											&& proxies != null && proxies
											.size() > 0) || !btnCheckButton
											.getSelection())) {
								button_1.setEnabled(true);
							}
						}
						lblNewLabel.setText(filePath);

						reader.close();
						isr.close();
						is.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					// System.out.println("Path:"+filePath);
				}
			}
		});
		link.setBounds(447, 10, 36, 17);
		link.setText("<a>导入...</a>");

		lblNewLabel = new Label(shlQqmail, SWT.BORDER | SWT.WRAP);
		lblNewLabel.setBounds(180, 10, 254, 17);

		btnCheckButton = new Button(shlQqmail, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnCheckButton.getSelection()) {
					label_3.setEnabled(true);
					label_4.setEnabled(true);
					link_2.setEnabled(true);
					if (pc < 0) {
						button_1.setEnabled(false);
					}
				} else {
					label_3.setEnabled(false);
					label_4.setEnabled(false);
					link_2.setEnabled(false);
					if (ns != null && ns.size() > 0
							&& !"".equals(text_2.getText())) {
						button_1.setEnabled(true);
					}
				}
			}
		});
		btnCheckButton.setBounds(499, 290, 117, 17);
		btnCheckButton.setText("使用代理发送邮件");

		label_3 = new Label(shlQqmail, SWT.NONE);
		label_3.setEnabled(false);
		label_3.setText("导入代理:");
		label_3.setBounds(499, 313, 61, 17);

		label_4 = new Label(shlQqmail, SWT.BORDER | SWT.WRAP);
		label_4.setEnabled(false);
		label_4.setBounds(566, 313, 254, 17);

		link_2 = new Link(shlQqmail, 0);
		link_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDlg = new FileDialog(shlQqmail, SWT.OPEN);
				// fileDlg.setFilterExtensions(new String[]{"*.torrent"});
				fileDlg.setFilterPath(null);
				fileDlg.setText("选择代理文件");
				String filePath = fileDlg.open();
				if (filePath != null) {
					try {
						proxies = new ArrayList<String>();
						File ipf = new File(filePath);
						FileInputStream is = new FileInputStream(ipf);
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader reader = new BufferedReader(isr);
						String line = null;
						while ((line = reader.readLine()) != null) {
							line = line.trim();
							if (!line.equals("")) {
								proxies.add(line);
							}
							// System.out.println(line);
						}
						pc = proxies.size();

						reader.close();
						isr.close();
						is.close();
						// System.out.println(ns.size());
						if (pc > 0) {
							lblMaxOfMax.setText(proxies.size() + "/" + pc);
							if (ns != null && ns.size() > 0
									&& !"".equals(text_2.getText())) {
								button_1.setEnabled(true);
							}
						}
						label_4.setText(filePath);

						reader.close();
						isr.close();
						is.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					// System.out.println("Path:"+filePath);
				}
			}
		});
		link_2.setEnabled(false);
		link_2.setText("<a>导入...</a>");
		link_2.setBounds(826, 313, 36, 17);

		button = new Button(shlQqmail, SWT.CHECK);
		button.setSelection(true);
		button.setText("发送完删除");
		button.setBounds(499, 336, 81, 17);

		text_1 = new Text(shlQqmail, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL);
		text_1.setEditable(false);
		text_1.setBounds(499, 36, 363, 248);

		Group group = new Group(shlQqmail, SWT.NONE);
		group.setText("工作区");
		group.setBounds(373, 359, 489, 176);

		label_1 = new Label(group, SWT.CENTER);
		label_1.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_INFO_BACKGROUND));
		label_1.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND));
		label_1.setBounds(10, 103, 149, 73);

		text = new Text(group, SWT.BORDER | SWT.CENTER);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(text.getText().length()==4){
					ctask.setCaptcha(text.getText());
					label_1.getImage().notify();
				}
			}
		});
		text.setEnabled(false);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 18, SWT.NORMAL));
		text.setBounds(174, 103, 149, 73);

		button_1 = new Button(group, SWT.NONE);
		button_1.setEnabled(false);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ("开始".equals(button_1.getText())) {
					flag = true;
					link.setEnabled(false);
					link_4.setEnabled(false);
					link_1.setEnabled(false);
					btnCheckButton.setEnabled(false);
					button.setEnabled(false);

					lblMaxOfMax.setText("0");
					lblOfMax.setText("0");
					label_13.setText("0");
					label_10.setText("0");
					label_12.setText("0");
					label_15.setText("0");

					text.setEnabled(true);
					link_2.setEnabled(false);

					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							// 线程池 数据库连接池 可联系起来
							int corePoolSize = 3;// minPoolSize
							int maxPoolSize = 3;
							int maxTaskSize = 1024 * 10;// 缓冲队列
							long keepAliveTime = 0L;
							TimeUnit unit = TimeUnit.MILLISECONDS;
							// corePoolSize =
							// Integer.parseInt(spinner.getText());
							// maxPoolSize = Integer.parseInt(spinner.getText())
							// * 2;// 最大同时执行的线程
							// maxTaskSize = maxPoolSize;
							// System.out.println(maxTaskSize);
							// 任务队列
							BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
									maxTaskSize);
							// 饱和处理策略
							RejectedExecutionHandler handler = new AbortPolicy();
							// 创建线程池
							pool = new ThreadPoolExecutor(corePoolSize,
									maxPoolSize, keepAliveTime, unit,
									workQueue, handler);

							for (int i = 0; i < table.getItemCount(); i++) {
								// String[] qp = ns.get(i).split("----");
								try {
									// if (qp.length == 3) {
									Task task = new Task(pool, proxies, table
											.getItem(i), null, QM.this);
									pool.execute(task);
									// }
								} catch (ArrayIndexOutOfBoundsException exx) {
									System.out.println(i + ":" + ns.get(i));
								}
							}

							timerTask = new TimerTask() {
								@Override
								public void run() {
									Display.getDefault().asyncExec(
											new Runnable() {
												@Override
												public void run() {
													// 刷新
												}
											});
								}
							};
						}
					});

					button_1.setText("结束");
				} else {
					pool.shutdownNow();

					flag = false;
					link.setEnabled(true);
					link_4.setEnabled(true);
					link_1.setEnabled(true);
					btnCheckButton.setEnabled(true);
					button.setEnabled(true);

					text.setEnabled(false);
					if (btnCheckButton.getSelection()) {
						link_2.setEnabled(true);
					}

					button_1.setText("开始");
				}
			}
		});
		button_1.setText("开始");
		button_1.setBounds(340, 103, 149, 73);

		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setBounds(10, 37, 41, 17);
		lblNewLabel_1.setText("代理:");

		lblMaxOfMax = new Label(group, SWT.NONE);
		lblMaxOfMax.setAlignment(SWT.RIGHT);
		lblMaxOfMax.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lblMaxOfMax.setText("0/0");
		lblMaxOfMax.setBounds(63, 37, 96, 17);

		Label label_7 = new Label(group, SWT.NONE);
		label_7.setText("登录:");
		label_7.setBounds(174, 37, 41, 17);

		lblOfMax = new Label(group, SWT.NONE);
		lblOfMax.setAlignment(SWT.RIGHT);
		lblOfMax.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lblOfMax.setText("0:0");
		lblOfMax.setBounds(227, 37, 96, 17);

		Label label_9 = new Label(group, SWT.NONE);
		label_9.setText("成功:");
		label_9.setBounds(10, 60, 41, 17);

		label_10 = new Label(group, SWT.NONE);
		label_10.setText("0");
		label_10.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_10.setAlignment(SWT.RIGHT);
		label_10.setBounds(63, 60, 96, 17);

		Label label_11 = new Label(group, SWT.NONE);
		label_11.setText("失败:");
		label_11.setBounds(174, 60, 41, 17);

		label_12 = new Label(group, SWT.NONE);
		label_12.setText("0");
		label_12.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_12.setAlignment(SWT.RIGHT);
		label_12.setBounds(227, 60, 96, 17);

		Label label_8 = new Label(group, SWT.NONE);
		label_8.setText("群数:");
		label_8.setBounds(340, 37, 41, 17);

		label_13 = new Label(group, SWT.NONE);
		label_13.setText("0");
		label_13.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_13.setAlignment(SWT.RIGHT);
		label_13.setBounds(393, 37, 96, 17);

		Label label_14 = new Label(group, SWT.NONE);
		label_14.setText("保留:");
		label_14.setBounds(340, 60, 41, 17);

		label_15 = new Label(group, SWT.NONE);
		label_15.setText("0");
		label_15.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_15.setAlignment(SWT.RIGHT);
		label_15.setBounds(393, 60, 96, 17);

		link_1 = new Link(shlQqmail, SWT.NONE);
		link_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				option.show();
				// System.out.println(e.text);
			}
		});
		link_1.setBounds(809, 337, 53, 17);
		link_1.setText("<a>高级设置</a>");

		link_3 = new Link(shlQqmail, SWT.NONE);
		link_3.setToolTipText("http://www.hoyland.ws");
		link_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String cmd = "rundll32 url.dll, FileProtocolHandler "
						+ link_3.getToolTipText();
				// System.out.println(cmd);
				try {
					Runtime.getRuntime().exec(cmd);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		link_3.setBounds(10, 541, 852, 17);
		link_3.setText("<a>http://www.hoyland.ws</a>");

		Label label_2 = new Label(shlQqmail, SWT.NONE);
		label_2.setText("系统日志:");
		label_2.setBounds(499, 10, 363, 17);

		Label label_5 = new Label(shlQqmail, SWT.NONE);
		label_5.setText("模板文件:");
		label_5.setBounds(10, 360, 61, 17);

		label_6 = new Label(shlQqmail, SWT.BORDER | SWT.WRAP);
		label_6.setBounds(77, 359, 241, 17);

		link_4 = new Link(shlQqmail, 0);
		link_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDlg = new FileDialog(shlQqmail, SWT.OPEN);
				// fileDlg.setFilterExtensions(new String[]{"*.torrent"});
				fileDlg.setFilterPath(null);
				fileDlg.setText("选择模板文件");
				String filePath = fileDlg.open();
				if (filePath != null) {
					label_6.setText(filePath);
					try {
						File ipf = new File(filePath);
						FileInputStream is = new FileInputStream(ipf);
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader reader = new BufferedReader(isr);
						String line = null;
						StringBuffer sb = new StringBuffer();
						while ((line = reader.readLine()) != null) {
							// line = line.trim();
							sb.append(line + "\r\n");
						}

						text_2.setText(sb.toString());

						if (!"".equals(text_2.getText())) {
							if (ns != null
									&& ns.size() > 0
									&& ((btnCheckButton.getSelection()
											&& proxies != null && proxies
											.size() > 0) || !btnCheckButton
											.getSelection())) {
								button_1.setEnabled(true);
							}
						}

						reader.close();
						isr.close();
						is.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					// System.out.println("Path:"+filePath);
				}

			}
		});
		link_4.setText("<a>导入...</a>");
		link_4.setBounds(331, 360, 36, 17);

		option = new Option(shlQqmail, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		text_2 = new Text(shlQqmail, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL);
		text_2.setEditable(false);
		text_2.setBounds(10, 383, 357, 152);
	}

	public synchronized void showImage(Task task) {
		ctask = task;		
		label_1.setBackgroundImage(task.getImage());
		
		try{
			task.getImage().wait();
			task.notify();
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

//	public void help(Task task) {
//		DefaultHttpClient client = new DefaultHttpClient();
//		HttpGet get = null;
//		HttpResponse response = null;
//		HttpEntity entity = null;
//
//		try {
//			get = new HttpGet("http://vc.gtimg.com/" + task.getCaptchaUrl()
//					+ ".gif");
//			get.setHeader("Connection", "Keep-Alive");
//
//			response = client.execute(get);
//			entity = response.getEntity();
//
//			InputStream input = entity.getContent();
//			image = new Image(Display.getDefault(), input);
//			list.put(image, task);
//			EntityUtils.consume(entity);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			get.releaseConnection();
//		}
//		client.getConnectionManager().shutdown();
//	}
}