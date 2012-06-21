package dashanzi.android;

public class Constants {

	public static class PlayerList {
		public static final String HEADER_IMAGE = "image";
		public static final String NAME = "name";
		public static final String SCORE = "score";
	}

	public static class HouseList {
		public static final String HEADER_IMAGE = "image";
		public static final String HOUSE_NUM = "housenum";
		public static final String HOUSE_REST_PLACE_NUM = "restnum";
	}

	public static class Response {
		public static final String SUCCESS = "OK";
		public static final String FAILED = "FAIL";
	}

	public static class JSON {
		public static final String HEADER = "header";
		public static final String BODY = "body";
	}

	public static class JSON_REQ_HEADER {
		public static final String TYPE = "type";
		public static final String UID = "uid";
		public static final String GID = "gid";
	}

	public static class JSON_RES_HEADER {
		public static final String TYPE = "type";
		public static final String STATUS = "uid";
		public static final String GID = "gid";
	}

	public static class Type {

		public static final String LOGIN_REQ = "LOGIN_REQ";
		public static final String JOIN_REQ = "JOIN_REQ";
		public static final String JOIN_RESP = "JOIN_RESP";
		public static final String REFRESH_REQ = "REFRESH_REQ";
	}

}
