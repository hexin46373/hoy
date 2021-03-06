package net.buxland.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
//import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class Client {

	public Client() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String url = "http://localhost/fx/service";
		HttpClient httpclient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		CookieStore cookieStore = new BasicCookieStore();

		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		String line = null;
		StringBuffer content = new StringBuffer();

		HttpResponse response = null;
		HttpEntity entity = null;
		HttpPost httpPost = null;

		List<NameValuePair> nameValuePairs = null;

		try {
			// httpGet = new HttpGet(url1);
			// response = httpclient.execute(httpGet, localContext);
			// entity = response.getEntity();
			// System.out.println("");
			// System.out.println(url1);
			// System.out.println("----------------------------------------");
			// System.out.println(response.getStatusLine());
			// if (entity != null) {
			// // System.out.println("Response content length: " +
			// entity.getContentLength());
			// // InputStream is = entity.getContent();
			// // InputStreamReader isr = new InputStreamReader(is);
			// // BufferedReader in = new BufferedReader(isr);
			// // while ((line = in.readLine()) != null) {
			// // //content.append(line);
			// // // System.out.println(line);
			// // }
			// entity.consumeContent();
			// }

			httpPost = new HttpPost(url);
			nameValuePairs = new ArrayList<NameValuePair>(1);
			 nameValuePairs.add(new BasicNameValuePair("SESSIONID", "CA6B5DDDAB51CF7298B45FF84DA6162F"));
			// nameValuePairs.add(new BasicNameValuePair("lieuDeTravail",
			// "sel"));
			// nameValuePairs.add(new BasicNameValuePair("distance", "10"));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			response = httpclient.execute(httpPost, localContext);
			entity = response.getEntity();
			System.out.println(url);
			System.out.println(response.getStatusLine());

			if (entity != null) {
				System.out.println("Response content length: "
						+ entity.getContentLength());
				System.out.println("----------------------------------------");
				InputStream is = entity.getContent();
				InputStreamReader isr = new InputStreamReader(is);

				BufferedReader in = new BufferedReader(isr);
				while ((line = in.readLine()) != null) {
					content.append(line);
					System.out.println(line);
				}
				entity.consumeContent();
				System.out.println();
			}

			httpclient.getConnectionManager().shutdown();

			// URL u = new URL(url);
			// InputStream in = u.openStream();
			// int n = -1;
			// byte[] b = new byte[1024];
			// // 从服务端读取数据并打印
			// // while((n=in.read())!=-1){
			// // System.out.print(n);
			// // }
			//			
			// while ((n = in.read(b)) != -1) {
			// String s = new String(b, 0, n, "UTF-8");
			// System.out.print(s);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
