package cn.colorforme.sina.capture;

import java.io.IOException;
import java.io.InputStream;
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

public class CarColorCapture {

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

		List<Map<String, String>> carLst = listCar(conn);
		System.out.println(carLst.get(19));
		Map<String, String> testMap = carLst.get(19);
		// carLst = new ArrayList<Map<String, String>>();
		// carLst.add(testMap);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		List<JSONObject> allColorLst = new ArrayList<>();
		int count = 0;
		// for (String subid : subidArr) {
		for (int i = 0; i < carLst.size(); i++) {
			Map<String, String> carMap = carLst.get(i);
			String subid = carMap.get("subid");
			String carid = carMap.get("carid");
			List<JSONObject> colorLst = listColor(httpclient, subid, carid);
			allColorLst.addAll(colorLst);
			System.out.println(count++ + "\t subid is : " + subid + "\t carid is : " + carid);
			// break;
		}
		int total = allColorLst.size();
		System.out.println(total);

		int phase = total / 3000;
		if (total % 3000 != 0) {
			phase += 1;
		}
		for (int i = 0; i < phase; i++) {
			int start = i * 3000;
			int end = start + 3000;
			if (end > total) {
				end = total;
			}
			List<JSONObject> sublist = allColorLst.subList(start, end);
			insertPhoto(conn, sublist);
			System.out.println(sublist.size());
		}
		conn.close();
	}

	public static List<JSONObject> listColor(CloseableHttpClient httpclient, String subid, String carid)
			throws ClientProtocolException, IOException {
		String requestUrl = "http://photo.auto.sina.com.cn/interface/v2/general/get_car_photo.php?pic_type=1&page=1&limit=10008&subid="
				+ subid + "&carid=" + carid;
		HttpPost httppost = new HttpPost(requestUrl);

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
		List<JSONObject> colorLst = new ArrayList<>();
		int code = json.getJSONObject("result").getJSONObject("status").getInt("code");
		// if (!"0".equals(code)) {
		if (code != 0) {
			return colorLst;
		}
		JSONObject optObj = json.getJSONObject("result").getJSONObject("data").getJSONObject("opt_data");
		Object colorObj = optObj.get("o_color");

		// System.out.println(optObj);
		// System.out.println(colorObj);
		// System.out.println(colorObj instanceof String);
		// System.out.println("".equals(colorObj));

		if ("".equals(colorObj) || colorObj == null) {
			return colorLst;
		}
		JSONObject colorJObj = optObj.getJSONObject("o_color");

		JSONArray hasPicArr = colorJObj.getJSONArray("has_pic");
		for (int i = 0; i < hasPicArr.length(); i++) {
			JSONObject cjson = (JSONObject) hasPicArr.get(i);
			JSONObject color = new JSONObject();
			color.put("subid", subid);
			color.put("carid", carid);
			color.put("has_pic", 1);
			color.put("colorid", cjson.getString("colorid"));
			color.put("colorname", cjson.getString("colorname"));
			color.put("colorcode", cjson.getString("colorcode"));
			colorLst.add(color);
		}
		JSONArray noPicArr = colorJObj.getJSONArray("no_pic");
		for (int i = 0; i < noPicArr.length(); i++) {
			JSONObject cjson = (JSONObject) noPicArr.get(i);
			JSONObject color = new JSONObject();
			color.put("subid", subid);
			color.put("carid", carid);
			color.put("has_pic", 0);
			color.put("colorid", cjson.getString("colorid"));
			color.put("colorname", cjson.getString("colorname"));
			color.put("colorcode", cjson.getString("colorcode"));
			colorLst.add(color);
		}
		return colorLst;
	}

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://192.168.1.9:3306/autocolor";

	// Database credentials
	static final String USER = "mjzc";
	static final String PASS = "mjzc";

	private static Integer insertPhoto(Connection conn, List<JSONObject> colorLst) throws JSONException, SQLException {
		String insertSql = "INSERT INTO t_car_color (subid, carid, has_pic, colorid, colorname, colorcode) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (JSONObject color : colorLst) {
			insPs.setString(1, color.getString("subid"));
			insPs.setString(2, color.getString("carid"));
			insPs.setInt(3, color.getInt("has_pic"));
			insPs.setString(4, color.getString("colorid"));
			insPs.setString(5, color.getString("colorname"));
			insPs.setString(6, color.getString("colorcode"));
			insPs.addBatch();
		}

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}

	private static List<Map<String, String>> listCar(Connection conn) throws JSONException, SQLException {
		String sql = "SELECT c.subid, c.carid FROM t_car c";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<Map<String, String>> carLst = new ArrayList<>();
		while (rs.next()) {
			String subid = rs.getString("subid");
			String carid = rs.getString("carid");
			Map<String, String> carMap = new HashMap<>();
			carMap.put("subid", subid);
			carMap.put("carid", carid);
			carLst.add(carMap);
		}
		rs.close();
		st.close();

		return carLst;
	}
}
