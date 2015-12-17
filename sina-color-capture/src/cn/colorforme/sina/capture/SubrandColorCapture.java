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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubrandColorCapture {

	static String[] subidArr = new String[] { "5", "6", "9", "10", "11", "13", "25", "40", "42", "44", "46", "54", "55",
			"58", "59", "60", "61", "64", "66", "72", "78", "85", "86", "87", "88", "93", "100", "105", "109", "110",
			"111", "116", "119", "124", "129", "136", "139", "145", "153", "154", "159", "160", "162", "167", "168",
			"172", "180", "181", "182", "185", "188", "190", "191", "193", "198", "201", "214", "215", "216", "219",
			"222", "226", "227", "228", "229", "234", "236", "237", "238", "239", "245", "250", "253", "256", "257",
			"260", "264", "265", "266", "267", "268", "273", "277", "282", "286", "297", "300", "301", "304", "305",
			"306", "316", "319", "320", "321", "322", "324", "325", "326", "330", "332", "334", "335", "336", "337",
			"340", "341", "350", "351", "353", "354", "355", "356", "357", "370", "371", "372", "373", "377", "379",
			"380", "381", "383", "388", "389", "390", "395", "403", "406", "409", "410", "411", "412", "415", "416",
			"417", "418", "419", "421", "422", "425", "428", "429", "439", "441", "443", "446", "447", "450", "452",
			"456", "457", "459", "464", "465", "466", "473", "474", "482", "489", "491", "492", "495", "499", "501",
			"502", "506", "507", "509", "514", "518", "523", "524", "528", "529", "530", "534", "535", "539", "541",
			"545", "546", "547", "548", "549", "550", "551", "552", "553", "555", "556", "557", "558", "560", "561",
			"562", "563", "567", "568", "572", "573", "576", "578", "579", "583", "584", "585", "587", "588", "589",
			"595", "596", "597", "601", "602", "605", "608", "610", "612", "615", "620", "621", "622", "626", "628",
			"629", "630", "632", "633", "634", "635", "638", "641", "643", "646", "648", "649", "651", "655", "656",
			"657", "659", "660", "661", "664", "666", "667", "668", "669", "671", "672", "676", "677", "678", "680",
			"683", "688", "689", "691", "693", "694", "695", "696", "698", "701", "702", "705", "706", "707", "710",
			"714", "716", "718", "720", "721", "722", "723", "724", "728", "729", "730", "731", "732", "734", "735",
			"736", "738", "739", "740", "741", "744", "745", "747", "750", "752", "755", "756", "757", "758", "760",
			"761", "764", "765", "766", "768", "769", "772", "775", "776", "778", "782", "783", "785", "791", "792",
			"793", "794", "795", "797", "798", "799", "800", "801", "804", "805", "807", "809", "812", "813", "814",
			"815", "820", "825", "828", "829", "830", "832", "834", "835", "836", "837", "838", "839", "840", "841",
			"847", "848", "857", "858", "859", "860", "862", "864", "865", "868", "873", "876", "877", "880", "881",
			"882", "885", "887", "888", "890", "891", "892", "894", "896", "897", "898", "902", "903", "904", "905",
			"906", "911", "914", "916", "917", "918", "919", "923", "925", "927", "930", "931", "932", "933", "934",
			"935", "938", "942", "943", "944", "945", "946", "953", "956", "958", "959", "960", "965", "969", "973",
			"979", "981", "987", "994", "1007", "1015", "1024", "1026", "1028", "1030", "1056", "1069", "1070", "1071",
			"1073", "1084", "1088", "1090", "1092", "1095", "1097", "1099", "1100", "1121", "1132", "1147", "1149",
			"1152", "1154", "1156", "1158", "1159", "1160", "1162", "1163", "1166", "1167", "1168", "1170", "1173",
			"1180", "1185", "1210", "1217", "1221", "1233", "1243", "1253", "1256", "1258", "1260", "1267", "1268",
			"1269", "1270", "1278", "1279", "1280", "1281", "1282", "1287", "1314", "1322", "1327", "1329", "1350",
			"1353", "1359", "1370", "1376", "1380", "1383", "1384", "1386", "1387", "1393", "1397", "1402", "1410",
			"1413", "1414", "1417", "1418", "1421", "1423", "1425", "1426", "1427", "1430", "1432", "1434", "1435",
			"1436", "1441", "1442", "1446", "1448", "1449", "1453", "1455", "1462", "1463", "1466", "1473", "1487",
			"1489", "1494", "1495", "1496", "1497", "1500", "1503", "1508", "1514", "1522", "1523", "1524", "1525",
			"1526", "1528", "1529", "1530", "1531", "1532", "1533", "1548", "1549", "1550", "1551", "1553", "1554",
			"1556", "1559", "1560", "1561", "1562", "1563", "1564", "1569", "1571", "1572", "1573", "1574", "1581",
			"1582", "1583", "1585", "1586", "1588", "1589", "1590", "1591", "1593", "1594", "1595", "1596", "1597",
			"1600", "1601", "1602", "1605", "1606", "1607", "1608", "1613", "1614", "1618", "1619", "1623", "1625",
			"1626", "1628", "1635", "1639", "1650", "1651", "1652", "1653", "1654", "1655", "1656", "1658", "1660",
			"1661", "1665", "1666", "1667", "1668", "1669", "1670", "1671", "1673", "1676", "1677", "1678", "1680",
			"1681", "1690", "1691", "1692", "1695", "1696", "1698", "1699", "1708", "1709", "1711", "1712", "1713",
			"1714", "1715", "1717", "1719", "1722", "1723", "1724", "1725", "1726", "1727", "1728", "1730", "1733",
			"1737", "1739", "1741", "1745", "1747", "1748", "1749", "1751", "1752", "1753", "1754", "1755", "1756",
			"1757", "1765", "1766", "1767", "1768", "1769", "1770", "1773", "1775", "1776", "1777", "1778", "1779",
			"1782", "1783", "1784", "1785", "1786", "1787", "1788", "1789", "1790", "1791", "1798", "1802", "1808",
			"1810", "1811", "1813", "1815", "1816", "1817", "1818", "1819", "1820", "1821", "1822", "1823", "1825",
			"1826", "1827", "1828", "1831", "1832", "1835", "1841", "1843", "1844", "1846", "1847", "1848", "1849",
			"1850", "1851", "1852", "1855", "1857", "1858", "1859", "1863", "1864", "1866", "1867", "1868", "1878",
			"1879", "1880", "1881", "1882", "1883", "1885", "1886", "1887", "1889", "1890", "1894", "1895", "1908",
			"1919", "1920", "1921", "1922", "1923", "1925", "1926", "1927", "1929", "1930", "1932", "1933", "1934",
			"1935", "1936", "1938", "1939", "1942", "1946", "1949", "1951", "1953", "1956", "1957", "1958", "1959",
			"1960", "1961", "1963", "1970", "1972", "1977", "1980", "1983", "1988", "1989", "1993", "1994", "1999",
			"2009", "2010", "2012", "2013", "2014", "2015", "2017", "2019", "2021", "2022", "2024", "2026", "2027",
			"2028", "2029", "2030", "2031", "2032", "2033", "2034", "2039", "2041", "2043", "2045", "2048", "2049",
			"2050", "2051", "2054", "2055", "2056", "2058", "2063", "2065", "2067", "2069", "2070", "2071", "2072",
			"2073", "2074", "2075", "2076", "2077", "2078", "2079", "2081", "2082", "2083", "2084", "2086", "2088",
			"2089", "2090", "2091", "2092", "2093", "2094", "2102", "2108", "2110", "2112", "2113", "2115", "2118",
			"2119", "2120", "2122", "2124", "2128", "2130", "2132", "2134", "2136", "2140", "2142", "2143", "2145",
			"2146", "2147", "2149", "2150", "2151", "2152", "2153", "2154", "2155", "2156", "2158", "2159", "2160",
			"2161", "2162", "2163", "2164", "2165", "2168", "2169", "2170", "2171", "2175", "2176", "2179", "2180",
			"2181", "2184", "2186", "2188", "2189", "2191", "2194", "2196", "2197", "2199", "2200", "2202", "2215",
			"2222", "2241", "2255", "2257", "2265", "2271", "2272", "2275", "2277", "2278", "2280", "2282", "2284",
			"2286", "2289", "2291", "2292", "2293", "2294", "2295", "2296", "2297", "2298", "2299", "2303", "2310",
			"2311", "2314", "2315", "2317", "2321", "2322", "2324", "2326", "2328", "2329", "2331", "2337", "2373" };

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		List<JSONObject> allColorLst = new ArrayList<>();
		System.out.println(subidArr[825]);
		// subidArr = new String[] { "2124" };
		int count = 0;
		// for (String subid : subidArr) {
		for (int i = 0; i < subidArr.length; i++) {
			String subid = subidArr[i];
			List<JSONObject> colorLst = listColor(httpclient, subid);
			allColorLst.addAll(colorLst);
			System.out.println(count++ + "\t subid is : " + subid);
			// break;
		}
		System.out.println(allColorLst.size());

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

		insertColor(conn, allColorLst);
		conn.close();
	}

	public static List<JSONObject> listColor(CloseableHttpClient httpclient, String subid)
			throws ClientProtocolException, IOException {
		String requestUrl = "http://photo.auto.sina.com.cn/interface/v2/general/get_car_photo.php?subid=" + subid;
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

		JSONObject optObj = json.getJSONObject("result").getJSONObject("data").getJSONObject("opt_data");
		Object colorObj = optObj.get("o_color");

		// System.out.println(optObj);
		// System.out.println(colorObj);
		// System.out.println(colorObj instanceof String);
		// System.out.println("".equals(colorObj));

		List<JSONObject> colorLst = new ArrayList<>();
		if ("".equals(colorObj) || colorObj == null) {
			return colorLst;
		}
		JSONObject colorJObj = optObj.getJSONObject("o_color");

		JSONArray hasPicArr = colorJObj.getJSONArray("has_pic");
		for (int i = 0; i < hasPicArr.length(); i++) {
			JSONObject cjson = (JSONObject) hasPicArr.get(i);
			JSONObject color = new JSONObject();
			color.put("subid", subid);
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

	private static Integer insertColor(Connection conn, List<JSONObject> colorLst) throws JSONException, SQLException {
		String insertSql = "INSERT INTO  t_car_subrand_color (subid, has_pic, colorid, colorname, colorcode) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (JSONObject color : colorLst) {
			insPs.setString(1, color.getString("subid"));
			insPs.setInt(2, color.getInt("has_pic"));
			insPs.setString(3, color.getString("colorid"));
			insPs.setString(4, color.getString("colorname"));
			insPs.setString(5, color.getString("colorcode"));
			insPs.addBatch();
		}

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}
}
