package cn.colorforme.sina.capture;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CategoryCapture {
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		String requestUrl = "http://photo.auto.sina.com.cn/interface/v2/general/mul_opt_search.php";
		HttpPost httppost = new HttpPost(requestUrl);

		CloseableHttpClient httpclient = HttpClients.createDefault();
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
		httpclient.close();

		String sResult = new String(result, "utf-8");
		sResult = StringEscapeUtils.unescapeJava(sResult);
		// System.out.println(sResult);
		JSONObject json = new JSONObject(sResult);
		JSONObject brandTree = json.getJSONObject("result").getJSONObject("data").getJSONObject("tree");

		List<JSONObject> categoryLst = new ArrayList<>();
		List<JSONObject> brandLst = new ArrayList<>();
		List<JSONObject> subrandLst = new ArrayList<>();
		for (Object key : brandTree.keySet()) {
			// System.out.println(key);
			JSONArray categoryArr = brandTree.getJSONArray((String) key);
			for (int i = 0; i < categoryArr.length(); i++) {
				JSONObject cjson = (JSONObject) categoryArr.get(i);
				JSONObject category = new JSONObject();
				String cid = cjson.getString("cid");
				category.put("cid", cid);
				category.put("cname", cjson.getString("cname"));
				category.put("ename", cjson.getString("ename"));
				category.put("chinese", cjson.getString("chinese"));
				category.put("key_letter", cjson.getString("key_letter"));
				categoryLst.add(category);

				JSONObject joBrand = cjson.getJSONObject("brand_list");
				for (Object bkey : joBrand.keySet()) {
					JSONObject bjson = joBrand.getJSONObject((String) bkey);
					JSONObject brand = new JSONObject();
					String bid = bjson.getString("bid");
					brand.put("cid", cid);
					brand.put("bid", bid);
					brand.put("cname", bjson.getString("cname"));
					brand.put("ename", bjson.getString("ename"));
					brand.put("chinese", bjson.getString("chinese"));
					brandLst.add(brand);

					JSONArray subArr = bjson.getJSONArray("sub_list");
					for (int j = 0; j < subArr.length(); j++) {
						JSONObject sjson = (JSONObject) subArr.get(j);
						JSONObject subrand = new JSONObject();
						String subid = sjson.getString("subid");
						subrand.put("cid", cid);
						subrand.put("bid", bid);
						subrand.put("subid", subid);
						subrand.put("cname", sjson.getString("cname"));
						subrand.put("ename", sjson.getString("ename"));
						subrand.put("chinese", sjson.getString("chinese"));
						subrandLst.add(subrand);
					}
				}
			}
		}
		System.out.println(categoryLst);
		System.out.println(brandLst);
		System.out.println(subrandLst);

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

		insertCategory(conn, categoryLst);
		insertBrand(conn, brandLst);
		insertSubrand(conn, subrandLst);
		conn.close();
	}

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://192.168.1.9:3306/autocolor";

	// Database credentials
	static final String USER = "mjzc";
	static final String PASS = "mjzc";

	private static Integer insertCategory(Connection conn, List<JSONObject> cateLst)
			throws JSONException, SQLException {
		String insertSql = "INSERT INTO t_car_category (cid, key_letter, cname, ename, chinese) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (JSONObject cate : cateLst) {
			insPs.setString(1, cate.getString("cid"));
			insPs.setString(2, cate.getString("key_letter"));
			insPs.setString(3, cate.getString("cname"));
			insPs.setString(4, cate.getString("ename"));
			insPs.setString(5, cate.getString("chinese"));
			insPs.addBatch();
		}

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}

	private static Integer insertBrand(Connection conn, List<JSONObject> brandLst) throws JSONException, SQLException {
		String insertSql = "INSERT INTO t_car_brand (cid, bid, cname, ename, chinese) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (JSONObject brand : brandLst) {
			insPs.setString(1, brand.getString("cid"));
			insPs.setString(2, brand.getString("bid"));
			insPs.setString(3, brand.getString("cname"));
			insPs.setString(4, brand.getString("ename"));
			insPs.setString(5, brand.getString("chinese"));
			insPs.addBatch();
		}

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}

	private static Integer insertSubrand(Connection conn, List<JSONObject> subrandLst)
			throws JSONException, SQLException {
		String insertSql = "INSERT INTO t_car_subrand (cid, bid, subid, cname, ename, chinese) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (JSONObject subrand : subrandLst) {
			insPs.setString(1, subrand.getString("cid"));
			insPs.setString(2, subrand.getString("bid"));
			insPs.setString(3, subrand.getString("subid"));
			insPs.setString(4, subrand.getString("cname"));
			insPs.setString(5, subrand.getString("ename"));
			insPs.setString(6, subrand.getString("chinese"));
			insPs.addBatch();
		}

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}
}
