package ws.hoyland.android.advx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;

public class Messenger implements Runnable {
	private Context context;
	private String server = "http://www.chenxjxc.com";
	private HttpURLConnection conn = null;
	private InputStreamReader isr = null;
	private String message = null;
	
	public Messenger(Context context) {
		this.context = context;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(true){
				conn = (HttpURLConnection) new URL(this.server + "/interface2.php").openConnection();
				conn.connect();
				isr = new InputStreamReader(conn.getInputStream(), "utf-8");
				String itf = new BufferedReader(isr, 1024*8).readLine().trim();
				String swt = "nffm=true";			
				conn.disconnect();
				System.out.println(itf);
				
				if (itf.startsWith(swt)) {
					System.out.println("T1");
					String param = itf.substring("nffm=true;".length());
					//String[] ps = params.split(";");
					if(message==null||!message.equals(param.substring("message=".length()))){
						System.out.println("T2");
						message = param.substring("message=".length());
						Intent activityIntent = new Intent(this.context, MessageActivity.class);
						activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						activityIntent.putExtra("info", message);
						this.context.startActivity(activityIntent);
						//this.context.get
						System.out.println("T3");
					}
				}
				System.out.println("T4");
				Thread.sleep(1000*60*3);
				System.out.println("T5");
			}
		}catch (Exception e) {
			e.printStackTrace();
			Intent activityIntent = new Intent(this.context, ExceptionActivity.class);
			activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activityIntent.putExtra("message", e.getMessage());
			this.context.startActivity(activityIntent);
		}
		
	}

}
