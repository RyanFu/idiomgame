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
import dashanzi.android.activity.IExceptionHandler;
import dashanzi.android.activity.IMessageHandler;
import dashanzi.android.dto.IMessage;
import dashanzi.android.service.ReceiverService;
import dashanzi.android.service.SenderService;
import dashanzi.android.util.Json2BeansUtil;

public class IdiomGameApp extends Application {
	private IMessageHandler currentActivity;
	// private NetworkService networkService;
	private SenderService senderService;
	private ReceiverService receiverService;
	private MyReceiver receiver;
	private ServiceConnection scSender;
	private ServiceConnection scReceiver;
	private boolean aboutThreadIsInterrupt = false;

	private String serverIp = "127.0.0.1";
	private int serverPort = 12345;

	// add TODO
	private String lastRegisterName;
	private int[] femaleHeaderIdArray = { R.drawable.g000, R.drawable.g001,
			R.drawable.g002, R.drawable.g003, R.drawable.g004, R.drawable.g005,
			R.drawable.g006, R.drawable.g007, R.drawable.g008 };

	private int[] manHeaderIdArray = { R.drawable.b000, R.drawable.b001,
			R.drawable.b002, R.drawable.b003, R.drawable.b004, R.drawable.b005,
			R.drawable.b006, R.drawable.b007, R.drawable.b008 };

	public void onCreate(Bundle savedInstanceState) {

	}

	// ------------- public methods ----------------------------
	/**
	 * used by all activities
	 */
	public void sendMessage(IMessage msg) {
		if (senderService == null) {
			exceptionCaught(-2, "");
			Log.e("==APP==", "networkService == NULL");
		} else {
			senderService.sendMessage(msg);
		}
	}

	public boolean doConnect(String ip, int port) {
		this.serverIp = ip;
		this.serverPort = port;

		if (doBindService()) {
			doRegisterReceiver();
			if (senderService.connect(ip, port)) {
				return true;
			} else {
				doUnregisterReceiver();
			}
		}

		return false;
	}

	public void doDisconnect() {
		// stop receiving before close connection!
		if (receiverService != null) {
			receiverService.stopReceiving();
			unbindService(scReceiver);
			receiverService = null;
		}
		if (senderService != null) {
			senderService.disconnect();
			unbindService(scSender);
			senderService = null;
		}
	}

	// ------------- private methods ----------------------------
	private void onMessageReceived(String s) throws JSONException {
		if (s.trim().length() == 0) {
			Log.i("==APP==", "message length=0");
			return;
		}
		IMessage msg = Json2BeansUtil.getMessageFromJsonStr(s);
		currentActivity.onMesssageReceived(msg);
	}

	private boolean doBindService() {
		Intent itSender = new Intent(this, SenderService.class);
		Intent itReceiver = new Intent(this, ReceiverService.class);
		int i;

		scSender = new ServiceConnection() {

			public void onServiceDisconnected(ComponentName name) {
				senderService = null;
			}

			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.i("==APP==", "sender service connected");
				senderService = ((SenderService.MyBinder) service).getService();

				// senderService.connect(serverIp, serverPort);
				// System.out.println("service=" + networkService);

				// IdiomGameApp.this.handler.handle();
				// networkService.test();
			}
		};

		scReceiver = new ServiceConnection() {

			public void onServiceDisconnected(ComponentName name) {
				receiverService = null;
			}

			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.i("==APP==", "receiver service connected");
				receiverService = ((ReceiverService.MyBinder) service)
						.getService();

				// networkService.connect(serverIp, serverPort);

				// IdiomGameApp.this.handler.handle();
			}
		};

		if (!bindService(itSender, scSender, Context.BIND_AUTO_CREATE)) {
			unbindService(scSender);
			return false;
		}
		if (!bindService(itReceiver, scReceiver, Context.BIND_AUTO_CREATE)) {
			unbindService(scReceiver);
			return false;
		}

		for (i = 0; i < 3; i++) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (senderService != null && receiverService != null) {
				break;
			}
		}

		if (i == 3) {

			// network error
			Log.e("==APP==", "scSender=" + senderService + ", scReceiver="
					+ receiverService);

			unbindService(scSender);
			Log.e("==APP==", "sender service unbinded");

			unbindService(scReceiver);
			Log.e("==APP==", "receiver service unbinded");

			exceptionCaught(-1, "");
			return false;
		}

		Log.i("==APP==", "sender service binded");
		Log.i("==APP==", "receiver service binded");
		return true;
	}

	private void doRegisterReceiver() {
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.test");
		IdiomGameApp.this.registerReceiver(receiver, filter);
		Log.i("==APP==", "receiver registered");
	}

	private void doUnregisterReceiver() {
		IdiomGameApp.this.unregisterReceiver(receiver);
		Log.i("==APP==", "receiver unregistered");
	}

	private void exceptionCaught(int code, String reason) {
		((IExceptionHandler) currentActivity).exceptionCatch();
	}

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("==APP==", "message received");
			Bundle bundle = intent.getExtras();
			String strStatus = bundle.getString("status");
			if (strStatus.equals("ok")) {
				String strMsg = bundle.getString("msg");
				try {
					onMessageReceived(strMsg);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else if (strStatus.equals("error")) {
				String strCode = bundle.getString("code");
				if (strCode.equals("1")) {// connection error
					// disconnect();
					exceptionCaught(1, "");
					// ((IExceptionHandler) currentActivity).exceptionCatch();
				} else if (strCode.equals("2")) {// send message error
					// disconnect();
					exceptionCaught(2, "");
					// ((IExceptionHandler) currentActivity).exceptionCatch();
				}
			}
		}

		public MyReceiver() {
			// System.out.println("MyReceiver");
		}

	}

	// ------------- setters and getters ----------------------------

	public String getServerIp() {
		return serverIp;
	}

	public boolean isAboutThreadIsInterrupt() {
		return aboutThreadIsInterrupt;
	}

	public synchronized void setAboutThreadIsInterrupt(
			boolean aboutThreadIsInterrupt) {
		this.aboutThreadIsInterrupt = aboutThreadIsInterrupt;
	}

	public IMessageHandler getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(IMessageHandler currentActivity) {
		this.currentActivity = currentActivity;
	}


	public int getServerPort() {
		return serverPort;
	}


	public String getLastRegisterName() {
		return lastRegisterName;
	}

	public void setLastRegisterName(String lastRegisterName) {
		this.lastRegisterName = lastRegisterName;
	}

	public int[] getFemaleHeaderIdArray() {
		return femaleHeaderIdArray;
	}

	public void setFemaleHeaderIdArray(int[] femaleHeaderIdArray) {
		this.femaleHeaderIdArray = femaleHeaderIdArray;
	}

	public int[] getManHeaderIdArray() {
		return manHeaderIdArray;
	}

	public void setManHeaderIdArray(int[] manHeaderIdArray) {
		this.manHeaderIdArray = manHeaderIdArray;
	}
}
