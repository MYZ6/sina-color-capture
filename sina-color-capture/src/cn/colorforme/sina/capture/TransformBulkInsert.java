package cn.colorforme.sina.capture;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class TransformBulkInsert {

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		File file = new File("d:/t_color_photo.sql");
		List<String> lines = FileUtils.readLines(file, "utf-8");
		List<String> newLst = new ArrayList<>();
		String newLine = "INSERT INTO `t_color_photo` VALUES ";
		for (int i = 0; i < lines.size(); i++) {
			String line = (String) lines.get(i);
			// System.out.println(line);
			// System.out.println(line.substring(35));
			newLine += line.substring(35, line.length() - 1);
			if (i % 3000 == 0) {
				newLine += ";";
				newLst.add(newLine);
				// System.out.println(newLine);
				System.out.println(i);
				newLine = "INSERT INTO `t_color_photo` VALUES ";
			} else {
				newLine += ", ";
			}
			if (i == 300) {
				// break;
			}
		}
		System.out.println(newLst);

		File newFile = new File("d:/t_color_photo2.sql");
		FileUtils.writeLines(newFile, newLst);
	}
}
