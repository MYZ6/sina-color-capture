package cn.colorforme.hosts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class GetHosts {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String requestUrl = "http://www.360kb.com/kb/2_122.html";
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
		// System.out.println(sResult);

		Pattern pattern = Pattern.compile("#google hosts [0-9]+.*#google hosts [0-9]+ end", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(sResult);
		String newHosts = null;
		while (matcher.find()) {
			newHosts = matcher.group(0);
			// System.out.println(hosts);
			break;
		}

		String hostsPath = "C:/Windows/System32/drivers/etc/hosts";
		File hostsFile = new File(hostsPath);
		String oriContents = FileUtils.readFileToString(hostsFile, "UTF-8");
		// System.out.println(oriContents);

		Matcher matcher2 = pattern.matcher(oriContents);
		if (matcher2.find()) {
			String hosts = matcher.group(0);
			oriContents = oriContents.replace(hosts, newHosts);
		} else {
			oriContents += "\r\n\r\n" + newHosts + "\r\n\r\n";
		}
		System.out.println(oriContents);
		//
		// file_put_contents($hostsFile, $hosts);
		FileUtils.write(hostsFile, oriContents, "UTF-8");
	}
}
