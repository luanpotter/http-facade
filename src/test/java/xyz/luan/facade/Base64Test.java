package xyz.luan.facade;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Base64Test {

	@Test
	public void encodeTest() {
		String expected = "amF2YSBoYXMgbm8gYmFzZTY0";
		String result = Base64.encode("java has no base64".getBytes());
		assertEquals(expected, result);
	}

	@Test
	public void decodeTest() {
		String expected = "progress is slow";
		String result = new String(Base64.decode("cHJvZ3Jlc3MgaXMgc2xvdw=="));
		assertEquals(expected, result);
	}

	@Test
	public void doubleTest() {
		String expected = "will it hold up?";
		String result = new String(Base64.decode(Base64.encode(expected.getBytes())));
		assertEquals(expected, result);
	}
}
