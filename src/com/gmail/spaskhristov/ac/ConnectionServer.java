package com.gmail.spaskhristov.ac;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class ConnectionServer implements IConnectionServer {
	private String msg = "ConnectionServer: ";

	@Override
	public String sendMessage(String URL, String message) {
		String tempHome = "no connection";
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		SendMessageCallable sendMessageCallable = new SendMessageCallable(URL,
				message);
		Future<String> future = executorService.submit(sendMessageCallable);
		try {
			tempHome = future.get();
			future.cancel(true);
		} catch (InterruptedException e) {
			Log.i(msg, "Error InterruptedException : ");
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.i(msg, "Error ExecutionException : ");
			e.printStackTrace();
		}
		future = null;
		executorService.shutdown();
		executorService = null;
		sendMessageCallable = null;
		return tempHome;
	}

	class SendMessageCallable implements Callable<String> {
		private String url;
		private String message;
		private String valUsername = "a";
		private String valPassword = "s";
		private String valTemp;
		private String valMode;
		private String valFan;

		public SendMessageCallable(String url, String message) {
			this.url = url;
			this.message = message;
			this.parseData();
		}

		public SendMessageCallable(String url) {
			this.url = url;
		}

		private void parseData() {
			this.valTemp = this.message.substring(0, 2);
			this.valMode = this.message.substring(2, 3);
			this.valFan = this.message.substring(3);
		}

		@Override
		public String call() throws Exception {
			String retString = null;
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", this.valUsername));
				params.add(new BasicNameValuePair("password", this.valPassword));
				params.add(new BasicNameValuePair("temperature", this.valTemp));
				params.add(new BasicNameValuePair("mode", this.valMode));
				params.add(new BasicNameValuePair("fanSpeed", this.valFan));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
				int status = responsePOST.getStatusLine().getStatusCode();
				if (status == 200) {
					Log.i(msg, "Is connect: true");
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(
						responsePOST.getEntity().getContent()));
				String line = "";
				int i = 0;
				while ((line = br.readLine().trim()) != null) {
					i++;
					if (i == 20) {
						Log.i(msg, line.substring(19, 23));
						retString = line.substring(19, 23);
						break;
					}
				}
			} catch (Exception e) {
				Log.i(msg, "Error Exception : ");
				e.printStackTrace();
			}
			return retString;
		}
	}
}
