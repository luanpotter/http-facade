package xyz.luan.facade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {

	@Test
	public void extractTest() {
		String text = "this is my id: 12342! don't lose it";
		String needle = Util.extract("[^\\d]*([\\d]*)[^\\d]*", text);
		assertEquals("12342", needle);
	}
}
