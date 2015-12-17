package cn.colorforme.sina.capture;

import java.util.ArrayList;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		int total = 57000;
		System.out.println(total);
		List<Integer> testLst = new ArrayList<>();
		for (int i = 0; i < total; i++) {
			testLst.add(i + 1);
		}

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
			List<Integer> sublist = testLst.subList(start, end);
			System.out.println(sublist.size());
		}
	}
}
