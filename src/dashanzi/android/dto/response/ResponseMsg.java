package dashanzi.android.dto.response;

import dashanzi.android.dto.IMessage;

public class ResponseMsg implements IMessage {

	private String type;
	private String status;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ResponseMsg [type=" + type + ", status=" + status + "]";
	}
	
}
