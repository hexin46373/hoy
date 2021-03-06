package ws.hoyland.qqid;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;

public class Option extends Dialog implements Observer {

	protected Object result;
	protected Shell shell;
	private Configuration configuration = Configuration.getInstance();
	

	private Spinner spinner_1;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Option(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
		Engine.getInstance().addObserver(this);
	}
	
	private void load(){
		//load & show
		try{
//			if(!flag){
//				InputStream is = Option.class.getResourceAsStream("/qm.ini");
//				this.configuration.load(is);
//				is.close();
//			}			
			if(this.configuration.size()>0){
//				combo_1.select(Integer.parseInt(this.configuration.getProperty("P1")));
//				combo_2.setItems(cities[combo_1.getSelectionIndex()]);
//				combo_2.select(Integer.parseInt(this.configuration.getProperty("C1")));
//				
//				combo_3.select(Integer.parseInt(this.configuration.getProperty("P2")));
//				combo_4.setItems(cities[combo_3.getSelectionIndex()]);
//				combo_4.select(Integer.parseInt(this.configuration.getProperty("C2")));
//				
//				combo_5.select(Integer.parseInt(this.configuration.getProperty("P3")));
//				combo_6.setItems(cities[combo_5.getSelectionIndex()]);
//				combo_6.select(Integer.parseInt(this.configuration.getProperty("C3")));
//				
//				spinner.setSelection(Integer.parseInt(this.configuration.getProperty("EMAIL_TIMES")));				
//
//				
//				text.setText(this.configuration.getProperty("ADSL_ACCOUNT"));
//				text_1.setText(this.configuration.getProperty("ADSL_PASSWORD"));
				
				spinner_1.setSelection(Integer.parseInt(this.configuration.getProperty("THREAD_COUNT")));
				
//				spinner_2.setSelection(Integer.parseInt(this.configuration.getProperty("AUTO_RECON")));
//				spinner_3.setSelection(Integer.parseInt(this.configuration.getProperty("RECON_DELAY")));
//				
//				spinner_4.setSelection(Integer.parseInt(this.configuration.getProperty("READ_TC")));
//				spinner_5.setSelection(Integer.parseInt(this.configuration.getProperty("MAIL_ITV")));
//				
//				if("true".equals(configuration.getProperty("AWCONN"))){
//					btnCheckButton.setSelection(true);
//				}else{
//					btnCheckButton.setSelection(false);
//				}

			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void save(){
//		this.configuration.put("EMAIL_TIMES", spinner.getText());
//		this.configuration.put("ADSL_ACCOUNT", text.getText());
//		this.configuration.put("ADSL_PASSWORD", text_1.getText());
		this.configuration.put("THREAD_COUNT", spinner_1.getText());
//		this.configuration.put("READ_TC", spinner_4.getText());
//		this.configuration.put("MAIL_ITV", spinner_5.getText());
//		
//		this.configuration.put("P1", String.valueOf(combo_1.getSelectionIndex()));
//		this.configuration.put("C1", String.valueOf(combo_2.getSelectionIndex()));
//		this.configuration.put("P2", String.valueOf(combo_3.getSelectionIndex()));
//		this.configuration.put("C2", String.valueOf(combo_4.getSelectionIndex()));
//		this.configuration.put("P3", String.valueOf(combo_5.getSelectionIndex()));
//		this.configuration.put("C3", String.valueOf(combo_6.getSelectionIndex()));
//		
//		this.configuration.put("AUTO_RECON", spinner_2.getText());
//		this.configuration.put("RECON_DELAY", spinner_3.getText());
//		this.configuration.put("AWCONN", String.valueOf(btnCheckButton.getSelection()));		
		
		this.configuration.save();
	}


	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		load();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

//	public void close(){
//		this.shell.setVisible(false);
//	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				Engine.getInstance().deleteObserver(Option.this);
			}
		});
//		shell.addShellListener(new ShellAdapter() {
//			@Override
//			public void shellClosed(ShellEvent e) {
//				shell.setVisible(false);
//				e.doit = false;
//			}
//		});
		
		shell.setSize(426, 280);
		shell.setText("设置");
		
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(-1, 2, 424, 215);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("常规");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite);
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setText("线程数量:");
		label_3.setBounds(10, 10, 61, 17);
		
		spinner_1 = new Spinner(composite, SWT.BORDER);
		spinner_1.setMinimum(1);
		spinner_1.setSelection(1);
		spinner_1.setBounds(77, 10, 47, 20);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//save configuration
				save();
				Option.this.shell.dispose();
			}
		});
		btnNewButton.setBounds(236, 221, 80, 27);
		btnNewButton.setText("确定(&O)");
		
		Button btnc = new Button(shell, SWT.NONE);
		btnc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Option.this.shell.dispose();
			}
		});
		btnc.setText("取消(&C)");
		btnc.setBounds(338, 221, 80, 27);
	}

	@Override
	public void update(Observable obj, Object arg) {
		// TODO Auto-generated method stub
		// 接收来自Engine的消息
	}
}
