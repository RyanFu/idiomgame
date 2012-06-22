package dashanzi.android;

import org.json.JSONException;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import dashanzi.android.activity.IConnectHandler;
import dashanzi.android.activity.IMessageHandler;
import dashanzi.android.dto.IMessage;
import dashanzi.android.service.NetworkService;
import dashanzi.android.util.Json2BeansUtil;

public class IdiomGameApp extends Application {
	private IMessageHandler currentActivity;
	private NetworkService networkService;

	private String serverIp = "127.0.0.1";
	private int serverPort = 12345;
	private IConnectHandler handler;

	public void onCreate(Bundle savedInstanceState) {

	}

	// ------------- public methods ----------------------------
	/**
	 * used by all activities
	 */
	public void sendMessage(IMessage msg) {
		if (networkService == null) {
			Log.e("sendMessage", "networkService == NULL");
		} else {
			networkService.sendMessage(msg);
		}
	}

	/**
	 * used by all welcome activity
	 */
	public void connect(IConnectHandler handler) {
		this.handler = handler;
		initService();
		// connnectService();
	}

	public void disconnect() {
		networkService.disconnect();
	}

	// ------------- private methods ----------------------------
	private void onMessageReceived(String s) throws JSONException {
		IMessage msg = Json2BeansUtil.getMessageFromJsonStr(s);
		currentActivity.onMesssageReceived(msg);
	}

	private void initService() {
		// 1. init service
		Intent intent = new Intent(this, NetworkService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
		Log.i("ttt", "Dispatcher() service binded");

		// 2. reg receiver
		MyReceiver receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.test");
		IdiomGameApp.this.registerReceiver(receiver, filter);

	}

	private ServiceConnection connection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			networkService = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			System.out.println("onServiceConnected");
			networkService = ((NetworkService.MyBinder) service).getService();

			networkService.connect(serverIp, serverPort);
			System.out.println("service=" + networkService);

			Log.i("iiiii", "before handle");
			IdiomGameApp.this.handler.handle();
			// networkService.test();
		}
	};

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("NNNN", "MyReceiver onMessageReceived");
			Bundle bundle = intent.getExtras();
			String strMsg = bundle.getString("msg");
			try {
				onMessageReceived(strMsg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public MyReceiver() {
			System.out.println("MyReceiver");
		}

	}

	// ------------- setters and getters ----------------------------

	public String getServerIp() {
		return serverIp;
	}

	public IMessageHandler getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(IMessageHandler currentActivity) {
		this.currentActivity = currentActivity;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

}
