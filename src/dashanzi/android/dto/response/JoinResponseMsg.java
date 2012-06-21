package dashanzi.android.dto.response;

import java.util.List;

import dashanzi.android.dto.User;

public class JoinResponseMsg extends ResponseMsg {

	private String gid;
	private String uid;
	private List<User> users;
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
