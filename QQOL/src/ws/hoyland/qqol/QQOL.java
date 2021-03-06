package ws.hoyland.qqol;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ws.hoyland.util.Configuration;
import ws.hoyland.util.EngineMessage;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;

public class QQOL implements Observer{

	protected Shell shlSszs;
	
	private Table table;
	private Text text_2;
	private Option option;
	private Label status;
	private Label label;
	private Label label_1;
	private Button button_2;
	private Text text_1;
	private Text text_3;
	private Button btnDenglu;
	private Label label_2;
	private Label label_3;
	private Button button_1;
	private Button button_3;
	private Label label_4;
	private Group group_1;
	private Label lblNewLabel;
	private Label label_9;
	private Button button;
	private Label label_6;
	private Combo combo;
	private int first = -1;
	private int last = -1;
	private int mfirst = -1;
	private boolean nsa = false;//need select all
	
	private int lasttid = -1;
	
	private Clipboard clipBoard = new Clipboard(Display.getDefault());
	private Transfer textTransfer = TextTransfer.getInstance();
	private MenuItem mntmc_1;
	private MenuItem mntml;
	private MenuItem mntmNewItem;
	private TableColumn tableColumn_3;
	private TableColumn tableColumn_5;
	private TableColumn tblclmnNewColumn;
	private TableColumn tblclmnNewColumn_1;
	private TableColumn tblclmnNewColumn_2;
	private Menu menu;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			QQOL window = new QQOL();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public QQOL(){
		Engine.getInstance().addObserver(this);
	}
	
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		display.addFilter(SWT.KeyDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				if ((event.stateMask & SWT.CTRL) != 0&&event.keyCode==116) { //CTRL+T
					//not need here
                    // new Tool(shlSszs, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL).open();
                }				
			}             
        });
		
		createContents();
		load();
		shlSszs.open();

		EngineMessage message = new EngineMessage();
		message.setType(EngineMessageType.IM_CAPTCHA_TYPE);
		message.setData(combo.getSelectionIndex());
		Engine.getInstance().fire(message);
		
		shlSszs.layout();
		while (!shlSszs.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void load(){
		Configuration configuration = Configuration.getInstance();
		if(configuration.size()>0){
			text_1.setText(configuration.getProperty("T_ACC"));
			combo.select(Integer.parseInt(configuration.getProperty("CPT_TYPE")));
			
			if("true".equals(configuration.getProperty("R_PWD"))){
				button_1.setSelection(true);
				text_3.setText(configuration.getProperty("T_PWD"));
			}
			if("true".equals(configuration.getProperty("AUTO_LOGIN"))){
				button_3.setSelection(true);
				login();
			}
		}
		
	}
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {		
		shlSszs = new Shell(Display.getDefault(), SWT.SHELL_TRIM ^ SWT.MAX
				^ SWT.RESIZE);
		shlSszs.setImage(SWTResourceManager.getImage(QQOL.class, "/ws/hoyland/qqol/icon.png"));
		
		shlSszs.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				Engine.getInstance().deleteObserver(QQOL.this);
				
				e.doit = false;
				
				Thread t = new Thread(new Runnable(){

					@Override
					public void run() {
						EngineMessage message = new EngineMessage();
						message.setType(EngineMessageType.IM_EXIT);
						Engine.getInstance().fire(message);
					}
					
				});
				t.start();
				//System.exit(0);
			}
		});
		shlSszs.setSize(713, 499);
		shlSszs.setText("QQ在线");

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shlSszs.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shlSszs.setLocation(x, y);
		
		status = new Label(shlSszs, SWT.BORDER);
		status.setBounds(0, 450, 706, 20);
		
		label = new Label(shlSszs, SWT.NONE);
		label.setText("帐号列表:");
		label.setBounds(0, 1, 60, 17);
		
		label_1 = new Label(shlSszs, SWT.BORDER | SWT.WRAP);
		label_1.setBounds(66, 1, 555, 17);
		
		Link link = new Link(shlSszs, 0);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog fileDlg = new FileDialog(shlSszs, SWT.OPEN);
				fileDlg.setFilterPath(null);
				fileDlg.setText("选择帐号文件");
				String filePath = fileDlg.open();
				if(filePath!=null){
					EngineMessage message = new EngineMessage();
					message.setType(EngineMessageType.IM_LOAD_ACCOUNT);
					message.setData(filePath);
					Engine.getInstance().fire(message);
				}
			}
		});
		link.setText("<a>导入...</a>");
		link.setBounds(627, 1, 36, 17);
		
		table = new Table(shlSszs, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] tis = table.getSelection();
				if(tis.length==0){
					mntmc_1.setEnabled(false);
					mntml.setEnabled(false);
					mntmNewItem.setEnabled(false);
				}else{
					//ready();
					if(button_2.getEnabled()){
						mntml.setEnabled(true);
						mntmNewItem.setEnabled(true);
					}
					mntmc_1.setEnabled(true);
				}
			}
		});
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(0, 24, 706, 238);
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(30);
		tableColumn.setText("ID");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("帐号");
		
		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(100);
		tableColumn_2.setText("密码");
		
		tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(90);
		tblclmnNewColumn.setText("昵称");
		
		tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(36);
		tblclmnNewColumn_1.setText("等级");
		
		tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(36);
		tblclmnNewColumn_2.setText("天数");
		
		TableColumn tableColumn_4 = new TableColumn(table, SWT.NONE);
		tableColumn_4.setWidth(164);
		tableColumn_4.setText("状态");
		
		Menu menu_1 = new Menu(table);
		table.setMenu(menu_1);
		
		mntml = new MenuItem(menu_1, SWT.NONE);
		mntml.setEnabled(false);
		mntml.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] tis = table.getSelection();
				if(tis.length==0){
					return;
				}
				first = table.indexOf(tis[0]);
				last = first;
				
				Event ex = new Event();
				ex.widget = button_2;
				ex.data = "1";
				//主动触发button点击事件				
				button_2.notifyListeners(SWT.Selection, ex);
//				for(int i=0;i<tis.length;i++){
//					
//					sb.append(tis[i].getText(0)+"----"+tis[i].getText(1)+"----"+tis[i].getText(2)+"----"+tis[i].getText(3)+"\r\n");					
//					//System.out.println("OK");
//				}
				//TTT
			}
		});
		mntml.setText("只执行选定行(&L)");
		
		mntmNewItem = new MenuItem(menu_1, SWT.NONE);
		mntmNewItem.setEnabled(false);
		mntmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] tis = table.getSelection();
				if(tis.length==0){
					return;
				}
				first = table.indexOf(tis[0]);
				last = table.getItemCount()-1;
				
				Event ex = new Event();
				ex.widget = button_2;
				ex.data = "2";
				//主动触发button点击事件				
				button_2.notifyListeners(SWT.Selection, ex);
			}
		});
		mntmNewItem.setText("从选定行开始执行(&S)");
		
		MenuItem menuItem_1 = new MenuItem(menu_1, SWT.SEPARATOR);
		menuItem_1.setText("-");
		
		mntmc_1 = new MenuItem(menu_1, SWT.NONE);
		mntmc_1.setEnabled(false);
		mntmc_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] tis = table.getSelection();
				if(tis.length==0){
					return;
				}
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<tis.length;i++){
					sb.append(tis[i].getText(0)+"----"+tis[i].getText(1)+"----"+tis[i].getText(2)+"----"+tis[i].getText(3)+"\r\n");					
					//System.out.println("OK");
				}
				clipBoard.clearContents();
				clipBoard.setContents(new String[]{sb.toString()}, new Transfer[]{textTransfer});
				//clipBoard.clearContents();
				//clipBoard.dispose();
			}
		});
		mntmc_1.setText("复制(&C)");
		
		tableColumn_3 = new TableColumn(table, SWT.NONE);
		tableColumn_3.setWidth(132);
		tableColumn_3.setText("最后活动");
		
		tableColumn_5 = new TableColumn(table, SWT.NONE);
		tableColumn_5.setWidth(70);
		tableColumn_5.setText("在线时长");
		
		Group group = new Group(shlSszs, SWT.NONE);
		group.setText("工作区");
		group.setBounds(217, 268, 489, 176);
		
		label_4 = new Label(group, SWT.BORDER | SWT.SHADOW_NONE | SWT.CENTER);
		label_4.setForeground(SWTResourceManager.getColor(0, 0, 0));
		label_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		label_4.setBounds(10, 103, 149, 73);
		
		text_2 = new Text(group, SWT.CENTER);
		text_2.setFont(SWTResourceManager.getFont("微软雅黑", 18, SWT.NORMAL));
		text_2.setEnabled(false);
		text_2.setBackground(SWTResourceManager.getColor(255, 255, 255));
		text_2.setBounds(200, 124, 96, 34);
		
		button_2 = new Button(group, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if(first==-1){
					first = 0;
				}
				if(last==-1){
					last = table.getItemCount() - 1;
				}
				
				Thread t = new Thread(new Runnable(){

					@Override
					public void run() {
						//System.out.println("EEF");
						
						Integer[] flidx = new Integer[4];
						flidx[0] = first;
						flidx[1] = last;
						flidx[2] = mfirst;
						if(e.data==null){
							flidx[3] = 0;
						}else{
							flidx[3] = Integer.parseInt(e.data.toString());
						}
						EngineMessage message = new EngineMessage();
						message.setType(EngineMessageType.IM_PROCESS);
						message.setData(flidx);
						Engine.getInstance().fire(message);
					}
					
				});
				
				t.start();
			}
		});
		button_2.setText("开始");
		button_2.setEnabled(false);
		button_2.setBounds(340, 103, 149, 73);
		
		Label lblAb = new Label(group, SWT.NONE);
		lblAb.setText("XA:");
		lblAb.setBounds(10, 37, 41, 17);
		
		Label label_8 = new Label(group, SWT.NONE);
		label_8.setText("0/0");
		label_8.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_8.setAlignment(SWT.RIGHT);
		label_8.setBounds(63, 37, 96, 17);
		
		Label lblXb = new Label(group, SWT.NONE);
		lblXb.setText("YA:");
		lblXb.setBounds(174, 37, 41, 17);
		
		Label label_10 = new Label(group, SWT.NONE);
		label_10.setText("0:0");
		label_10.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_10.setAlignment(SWT.RIGHT);
		label_10.setBounds(227, 37, 96, 17);
		
		Label lblYa = new Label(group, SWT.NONE);
		lblYa.setText("XB:");
		lblYa.setBounds(10, 60, 41, 17);
		
		Label label_12 = new Label(group, SWT.NONE);
		label_12.setText("0");
		label_12.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_12.setAlignment(SWT.RIGHT);
		label_12.setBounds(63, 60, 96, 17);
		
		Label lblYb = new Label(group, SWT.NONE);
		lblYb.setText("YB:");
		lblYb.setBounds(174, 60, 41, 17);
		
		Label label_14 = new Label(group, SWT.NONE);
		label_14.setText("0");
		label_14.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_14.setAlignment(SWT.RIGHT);
		label_14.setBounds(227, 60, 96, 17);
		
		Label lblXc = new Label(group, SWT.NONE);
		lblXc.setText("ZA:");
		lblXc.setBounds(340, 37, 41, 17);
		
		Label label_16 = new Label(group, SWT.NONE);
		label_16.setText("0");
		label_16.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_16.setAlignment(SWT.RIGHT);
		label_16.setBounds(386, 37, 96, 17);
		
		Label lblYc = new Label(group, SWT.NONE);
		lblYc.setText("ZB:");
		lblYc.setBounds(340, 60, 41, 17);
		
		Label label_18 = new Label(group, SWT.NONE);
		label_18.setText("0");
		label_18.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_18.setAlignment(SWT.RIGHT);
		label_18.setBounds(386, 60, 96, 17);
		
		Label label_19 = new Label(group, SWT.BORDER | SWT.SHADOW_NONE | SWT.CENTER);
		label_19.setForeground(SWTResourceManager.getColor(0, 0, 0));
		label_19.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		label_19.setBounds(174, 103, 149, 73);
		
		group_1 = new Group(shlSszs, SWT.NONE);
		group_1.setText("识别方式");
		group_1.setBounds(0, 268, 213, 176);
		
		label_2 = new Label(group_1, SWT.NONE);
		label_2.setText("帐号:");
		label_2.setBounds(10, 46, 43, 17);
		
		label_3 = new Label(group_1, SWT.NONE);
		label_3.setText("密码:");
		label_3.setBounds(10, 70, 43, 17);
		
		button_1 = new Button(group_1, SWT.CHECK);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!button_1.getSelection()){
					button_3.setSelection(false);
				}
			}
		});
		button_1.setText("记住密码");
		button_1.setBounds(10, 92, 69, 17);
		
		button_3 = new Button(group_1, SWT.CHECK);
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(button_3.getSelection()){
					button_1.setSelection(true);
				}
			}
		});
		button_3.setText("自动登录");
		button_3.setBounds(135, 92, 69, 17);

		text_1 = new Text(group_1, SWT.BORDER);
		text_1.setBounds(59, 44, 139, 20);
		
		text_3 = new Text(group_1, SWT.BORDER | SWT.PASSWORD);
		text_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(nsa){
					text_3.selectAll();
					nsa = false;
				}
			}
		});
		text_3.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				//System.out.println("EXF");
				nsa = true;
				text_3.selectAll();
			}
		});
//		text_3.addFocusListener(new FocusAdapter() {
//			@Override
//			public void focusGained(FocusEvent e) {
//				System.out.println("EXF");
//				text_3.selectAll();				
//			}
//		});
		text_3.setBounds(59, 68, 139, 20);		
		
		btnDenglu = new Button(group_1, SWT.NONE);
		btnDenglu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				login();
			}
		});
		btnDenglu.setText("登录");
		btnDenglu.setBounds(41, 118, 129, 48);
		
		Link link_1 = new Link(group_1, 0);
		link_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				option = new Option(shlSszs, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				option.open();
			}
		});
		link_1.setBounds(174, 19, 24, 17);
		link_1.setText("<a>设置</a>");
		
		button = new Button(group_1, SWT.NONE);
		button.setBounds(41, 118, 129, 48);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				button_1.setVisible(true);
				button_3.setVisible(true);
				text_1.setVisible(true);
				text_3.setVisible(true);
				btnDenglu.setVisible(true);
				//btnUu.setVisible(true);
				combo.setEnabled(true);
				label_3.setVisible(true);

				lblNewLabel.setVisible(false);
				label_9.setVisible(false);
				button.setVisible(false);
				label_6.setVisible(true);
			}
		});
		button.setText("切换帐号");
		
		lblNewLabel = new Label(group_1, SWT.NONE);
		lblNewLabel.setBounds(59, 46, 139, 17);
		
		label_9 = new Label(group_1, SWT.NONE);
		label_9.setBounds(59, 70, 139, 17);
		
		label_6 = new Label(group_1, SWT.NONE);
		label_6.setText("题分:");
		label_6.setBounds(10, 70, 43, 17);
		
		combo = new Combo(group_1, SWT.NONE);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(combo.getSelectionIndex()!=2){
					label_2.setEnabled(true);
					label_3.setEnabled(true);
					text_3.setEnabled(true);
					text_1.setEnabled(true);
					button_1.setEnabled(true);
					button_3.setEnabled(true);
					btnDenglu.setEnabled(true);
				}else{
					label_2.setEnabled(false);
					label_3.setEnabled(false);
					text_3.setEnabled(false);
					text_1.setEnabled(false);
					button_1.setEnabled(false);
					button_3.setEnabled(false);
					btnDenglu.setEnabled(false);					
				}
				
				EngineMessage message = new EngineMessage();
				message.setType(EngineMessageType.IM_CAPTCHA_TYPE);
				message.setData(combo.getSelectionIndex());
				Engine.getInstance().fire(message);
			}
		});
		combo.setItems(new String[] {"云打码", "悠悠云", "手动输入"});
		combo.setBounds(10, 15, 97, 23);
		combo.select(0);
		
		Link link_2 = new Link(shlSszs, SWT.NONE);
		link_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(e.button==1){
					//menu.
					menu.setVisible(true);
					return;
				}else {
					menu.setVisible(false);
					return;
				}
				
				
				//System.out.println("OK:"+e.button);
			}
		});
		link_2.setBounds(669, 1, 37, 17);
		link_2.setText("<a>导出...</a>");
		
		menu = new Menu(link_2);
		link_2.setMenu(menu);
		
		MenuItem mntmNewItem_1 = new MenuItem(menu, SWT.NONE);
		mntmNewItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog fileDlg = new FileDialog(shlSszs, SWT.SAVE);
				//fileDlg.setFilterPath(null);
				fileDlg.setFilterExtensions(new String[] { "*.txt", "*.*" });
				fileDlg.setText("保存帐号[密码正确]");
				String filePath = fileDlg.open();
				if(filePath!=null){
					EngineMessage message = new EngineMessage();
					message.setType(EngineMessageType.IM_EXPORT);
					message.setData("0|"+filePath);
					Engine.getInstance().fire(message);
				}
			}
		});
		mntmNewItem_1.setText("密码正确帐号...");
		
		MenuItem mntmNewItem_2 = new MenuItem(menu, SWT.NONE);
		mntmNewItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog fileDlg = new FileDialog(shlSszs, SWT.SAVE);
				//fileDlg.setFilterPath(null);
				fileDlg.setFilterExtensions(new String[] { "*.txt", "*.*" });
				fileDlg.setText("保存帐号[密码错误]");
				String filePath = fileDlg.open();
				if(filePath!=null){
					EngineMessage message = new EngineMessage();
					message.setType(EngineMessageType.IM_EXPORT);
					message.setData("1|"+filePath);
					Engine.getInstance().fire(message);
				}
			}
		});
		mntmNewItem_2.setText("密码错误帐号...");
		
		MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog fileDlg = new FileDialog(shlSszs, SWT.SAVE);
				//fileDlg.setFilterPath(null);
				fileDlg.setFilterExtensions(new String[] { "*.txt", "*.*" });
				fileDlg.setText("保存帐号[需要密保]");
				String filePath = fileDlg.open();
				if(filePath!=null){
					EngineMessage message = new EngineMessage();
					message.setType(EngineMessageType.IM_EXPORT);
					message.setData("2|"+filePath);
					Engine.getInstance().fire(message);
				}
			}
		});
		menuItem.setText("需密保帐号...");
		
		MenuItem menuItem_2 = new MenuItem(menu, SWT.NONE);
		menuItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog fileDlg = new FileDialog(shlSszs, SWT.SAVE);
				//fileDlg.setFilterPath(null);
				fileDlg.setFilterExtensions(new String[] { "*.txt", "*.*" });
				fileDlg.setText("保存帐号[冻结帐号]");
				String filePath = fileDlg.open();
				if(filePath!=null){
					EngineMessage message = new EngineMessage();
					message.setType(EngineMessageType.IM_EXPORT);
					message.setData("3|"+filePath);
					Engine.getInstance().fire(message);
				}
			}
		});
		menuItem_2.setText("冻结帐号...");
	}

	private void login() {
		List<String> params = new ArrayList<String>();
		params.add(text_1.getText());
		params.add(text_3.getText());
		params.add(String.valueOf(button_1.getSelection()));
		params.add(String.valueOf(button_3.getSelection()));
		params.add(String.valueOf(combo.getSelectionIndex()));
		
		EngineMessage message = new EngineMessage();
		message.setType(EngineMessageType.IM_USERLOGIN);
		message.setData(params);
		
		Engine.getInstance().fire(message);		
	}

	@Override
	public void update(Observable obj, Object arg) {
		//接收来自Engine的消息
		final EngineMessage msg = (EngineMessage) arg;
		int type = msg.getType();
		
		switch(type){
			case EngineMessageType.OM_LOGINING:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						btnDenglu.setEnabled(false);
						status.setText("正在登录");
					}				
				});
				break;
			case EngineMessageType.OM_LOGINED:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						Object[] objs = (Object[])msg.getData();
						
						lblNewLabel.setText((String)objs[2]);
						label_9.setText(String.valueOf(((Integer)objs[1]).intValue()));
						
						btnDenglu.setEnabled(true);
						lblNewLabel.setVisible(true);
						label_9.setVisible(true);
						button.setVisible(true);
						label_6.setVisible(true);
						
						//btnUu.setVisible(false);
						combo.setEnabled(false);
						button_1.setVisible(false);
						button_3.setVisible(false);
						text_1.setVisible(false);
						text_3.setVisible(false);
						label_3.setVisible(false);
						btnDenglu.setVisible(false);
						status.setText("登录成功: ID="+String.valueOf(((Integer)objs[0]).intValue()));
					}				
				});
				break;
			case EngineMessageType.OM_LOGIN_ERROR:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						Object[] objs = (Object[])msg.getData();
						btnDenglu.setEnabled(true);
						status.setText("登录失败: ERR="+String.valueOf(((Integer)objs[0]).intValue()));
					}				
				});
				break;
			case EngineMessageType.OM_CLEAR_ACC_TBL:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						table.removeAll();
					}				
				});
				break;
			case EngineMessageType.OM_ADD_ACC_TBIT:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						String[] ts = (String[])msg.getData();
											
						TableItem tableItem = new TableItem(
								table, SWT.NONE);
						tableItem.setText(ts);
						table.setSelection(tableItem);
					}
				});
				break;
			case EngineMessageType.OM_ACCOUNT_LOADED:
				Display.getDefault().asyncExec(new Runnable() {
					@SuppressWarnings("unchecked")
					@Override
					public void run() {
						List<String> ls = (List<String>)msg.getData();
//						label.setText("帐号列表 (共 " + ls.get(0)
//								+ " 条):");
						label_1.setText(ls.get(1));						
						table.setSelection(0);
					}
				});
				break;
			case EngineMessageType.OM_COMPLETE:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
//						Object[] objs = (Object[])msg.getData();
//						btnDenglu.setEnabled(true);
						status.setText("登录完成");
					}				
				});
				break;
//			case EngineMessageType.OM_CLEAR_MAIL_TBL:
//				Display.getDefault().asyncExec(new Runnable() {
//					@Override
//					public void run() {
//						table_1.removeAll();
//					}				
//				});
//				break;
//			case EngineMessageType.OM_ADD_MAIL_TBIT:
//				Display.getDefault().asyncExec(new Runnable() {
//					@Override
//					public void run() {
//						TableItem tableItem = new TableItem(
//								table_1, SWT.NONE);
//						tableItem.setText((String[])msg.getData());
//						table_1.setSelection(tableItem);
//					}
//				});
//				break;
//			case EngineMessageType.OM_MAIL_LOADED:
//				Display.getDefault().asyncExec(new Runnable() {
//					@SuppressWarnings("unchecked")
//					@Override
//					public void run() {
//						List<String> ls = (List<String>)msg.getData();
////						label.setText("帐号列表 (共 " + ls.get(0)
////								+ " 条):");
//						label_5.setText(ls.get(1));
//						table_1.setSelection(0);
//					}
//				});
//				break;
			case EngineMessageType.OM_READY:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						button_2.setEnabled(true);
					}				
				});
				break;
			case EngineMessageType.OM_UNREADY:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						button_2.setEnabled(false);
					}				
				});
				break;
			case EngineMessageType.OM_RUNNING:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						if((Boolean)msg.getData()){
							status.setText("正在登录...");
							button_2.setText("结束");
							//button_4.setEnabled(true);
						}else{
							first = -1;
							last = -1;
							mfirst = -1;
							lasttid = -1;
							status.setText("运行结束");
							button_2.setText("开始");
							//button_4.setEnabled(false);
						}
					}				
				});
				break;
			case EngineMessageType.OM_IMAGE_DATA:		
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						label_4.setImage(new Image(Display.getDefault(), (InputStream)msg.getData()));
					}
				});
				break;
			case EngineMessageType.OM_INFO:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						String[] msgs = ((String)msg.getData()).split("\\|");
						table.getItem(msg.getTid()-1).setText(6, msgs[1]);
						//System.err.println(msgs[0]);
						/**
						if("false".equals(msgs[0])){
							table.setSelection(msg.getTid()-1);
						}**/
						if(msg.getTid()>lasttid){//最新的才会跳至
							table.setSelection(msg.getTid()-1);
							lasttid = msg.getTid();
						}						
					}
				});
				break;
			case EngineMessageType.OM_INFOACT:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {				
						//System.out.println(msg.getTid()-1);
						table.getItem(msg.getTid()-1).setText(7, (String)msg.getData());
						//table.setSelection(msg.getTid()-1);
					}
				});
				break;
			case EngineMessageType.OM_PROFILE:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						String[] ld = ((String)msg.getData()).split(":");
						table.getItem(msg.getTid()-1).setText(4, ld[0]);
						table.getItem(msg.getTid()-1).setText(5, ld[1]);
						//table.setSelection(msg.getTid()-1);
					}
				});
				break;				
			case EngineMessageType.OM_BEAT://心跳包
				//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				Display.getDefault().asyncExec(new Runnable() {
					private Long time = null;
					private Integer oltime = null;
					private int hr = 0;
					private int mt = 0;
					private int sec = 0;
					@Override
					public void run() {
						time = (Long)msg.getData();
//						oltime = oltime/1000;
//						int hr = oltime / 3600;
//						int mt = (oltime - hr*3600) / 60;
//						int sec = oltime - hr*3600 - mt*60;
						//table.getS
						//table.getItem(1).
						for(int i=0;i<table.getItemCount();i++){
							//if(!"".equals(table.getItem(i).getText(5))){
							if(table.getItem(i).getData()!=null){
								oltime = (int)(time - (Long)table.getItem(i).getData());
								oltime = oltime/1000;
								hr = oltime / 3600;
								mt = (oltime - hr*3600) / 60;
								sec = oltime - hr*3600 - mt*60;
								table.getItem(i).setText(8, (hr<10?("0"+hr):hr)+":"+(mt<10?("0"+mt):mt)+":"+(sec<10?("0"+sec):sec));
							}
						}
						//table.setSelection(msg.getTid()-1);
					}
				});
				break;
			case EngineMessageType.OM_NICK:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {					
						table.getItem(msg.getTid()-1).setText(3, (String)msg.getData());
						//table.getItem(msg.getTid()-1).setData(System.currentTimeMillis());
						//table.setSelection(msg.getTid()-1);
					}
				});
				break;
			case EngineMessageType.OM_OLTIME://在线时间计时
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {					
						//table.getItem(msg.getTid()-1).setText(3, (String)msg.getData());
						table.getItem(msg.getTid()-1).setData(System.currentTimeMillis());
						//table.setSelection(msg.getTid()-1);
					}
				});
				break;
			case EngineMessageType.OM_TF:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {					
						table.getItem(msg.getTid()-1).setData(null);
						//table.setSelection(msg.getTid()-1);
					}
				});
				break;
//			case EngineMessageType.OM_REQUIRE_MAIL:
//				Display.getDefault().asyncExec(new Runnable() {
//					@Override
//					public void run() {
//						int mid = Integer.parseInt(((String[])msg.getData())[0]);
//						int mc = Integer.parseInt(table_1.getItem(mid-1).getText(3))+1;
//						table_1.getItem(mid-1).setText(3, String.valueOf(mc));
//						table_1.setSelection(mid-1);
//					}
//				});
//				break;
			default:
				break;
		}
	}
}
