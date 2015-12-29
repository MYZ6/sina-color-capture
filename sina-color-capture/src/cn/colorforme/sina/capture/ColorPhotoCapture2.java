package cn.colorforme.sina.capture;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ColorPhotoCapture2 {

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		List<Map<String, String>> colorLst = listColor(conn);
		System.out.println(colorLst.get(19));
		// subidArr = new String[] { "2014" };
		int colorCount = colorLst.size();
		int stepLength = 100;// 根据网络性能调整步长
		int phase = colorCount / stepLength;
		if (colorCount % stepLength != 0) {
			phase += 1;
		}
		System.out.println(colorCount);
		for (int i = 1; i < phase; i++) {
			System.out.println(i);
			int start = i * stepLength;
			int end = start + stepLength;
			if (end > colorCount) {
				end = colorCount;
			}
			List<Map<String, String>> sublist = colorLst.subList(start, end);

			List<JSONObject> phasePhotoLst = new ArrayList<>();
			for (int j = 0; j < sublist.size(); j++) {
				Map<String, String> colorMap = sublist.get(j);
				String subid = colorMap.get("subid");
				String colorid = colorMap.get("colorid");
				List<JSONObject> photoLst = listPhoto(httpclient, subid, colorid);
				phasePhotoLst.addAll(photoLst);
				if (j % 10 == 0) {
					System.out.println((start + j + 1) + "\t subid is : " + subid + "\t colorid is : " + colorid);
				}
			}
			System.out.println(phasePhotoLst.size());
			insertPhoto(conn, phasePhotoLst);// 早一些使用连接，避免连接断开（在外网时很重要）
			// break;
		}
		conn.close();
	}

	public static List<JSONObject> listPhoto(CloseableHttpClient httpclient, String subid, String colorid)
			throws ClientProtocolException, IOException {
		String requestUrl = "http://photo.auto.sina.com.cn/interface/v2/general/get_car_photo.php?pic_type=1&page=1&limit=10008&subid="
				+ subid + "&o_color=" + colorid;
		HttpPost httppost = new HttpPost(requestUrl);
		List<JSONObject> photoLst = new ArrayList<>();
		try {
			CloseableHttpResponse response = httpclient.execute(httppost);
			// Get hold of the response entity
			HttpEntity entity = response.getEntity();

			byte[] result = null;
			// If the response does not enclose an entity, there is no need
			// to bother about connection release
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = IOUtils.toByteArray(instream);
				instream.close();
			}
			response.close();

			String sResult = new String(result, "utf-8");
			sResult = StringEscapeUtils.unescapeJava(sResult);
			// System.out.println(sResult);
			JSONObject json = null;
			try {
				json = new JSONObject(sResult);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(sResult);
				String newResult = sResult.replace("\n", "");
				System.out.println(newResult);
				json = new JSONObject(newResult);
				// Pattern pattern = Pattern.compile("\n");
				// Matcher matcher = pattern.matcher(sResult);
				// while (matcher.find()) {
				// System.out.println(matcher.group(0));
				// }
				System.out.println(json);
			}
			int code = json.getJSONObject("result").getJSONObject("status").getInt("code");
			// if (!"0".equals(code)) {
			if (code != 0) {
				return photoLst;
			}
			JSONArray photoArr = json.getJSONObject("result").getJSONObject("data").getJSONArray("type_data");
			JSONArray imgLst = new JSONArray();
			if (photoArr.length() > 0) {
				imgLst = photoArr.getJSONObject(0).getJSONArray("img_list");
			}
			for (int i = 0; i < imgLst.length(); i++) {
				JSONObject imgJson = imgLst.getJSONObject(i);
				imgJson.put("subid", subid);
				imgJson.put("colorid", colorid);
				photoLst.add(imgJson);
			}
			return photoLst;
		} catch (ConnectException ex) {
			ex.printStackTrace();
		}
		return photoLst;
	}

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	// static final String DB_URL = "jdbc:mysql://192.168.1.9:3306/autocolor";
	static final String DB_URL = "jdbc:mysql://www.colorforme.cn:3306/test";

	// Database credentials
	static final String USER = "mjzc";
	static final String PASS = "mjzc";

	private static Integer insertPhoto(Connection conn, List<JSONObject> photoLst) throws JSONException, SQLException {
		String insertSql = "INSERT INTO t_color_photo (subid, colorid, carid, photoid, title, url) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (JSONObject photo : photoLst) {
			insPs.setString(1, photo.getString("subid"));
			insPs.setString(2, photo.getString("colorid"));
			insPs.setString(3, photo.getString("carid"));
			insPs.setString(4, photo.getString("photoid"));
			insPs.setString(5, photo.getString("title"));
			insPs.setString(6, photo.getString("img_340"));
			insPs.addBatch();
		}

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}

	private static List<Map<String, String>> listColor(Connection conn) throws JSONException, SQLException {
		String sql = "SELECT c.subid, c.colorid FROM t_car_subrand_color c";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<Map<String, String>> colorLst = new ArrayList<>();
		while (rs.next()) {
			String subid = rs.getString("subid");
			String colorid = rs.getString("colorid");
			Map<String, String> colorMap = new HashMap<>();
			colorMap.put("subid", subid);
			colorMap.put("colorid", colorid);
			colorLst.add(colorMap);
		}
		rs.close();
		st.close();

		return colorLst;
	}
}
