package jpkg.config;

import java.util.Scanner;

import org.junit.Test;

import static org.junit.Assert.*;
import jpkg.test.TestManager;

public class ConfigTest {
	public static void main(String[] args) {
		test();
		TestManager.runTests();
	}
	
	public static void test() {
		TestManager.addTest(ConfigTest.class);
	}
	
	@Test
	public void testConfig() {
		
		Config cfg = new Config();
		
		Scanner sc = new Scanner("hello=hi!\n"
				+ "foo = bar = foobar\t # This is a comment!\n"	// Test that only first equals sign is honored
				+ "# Comment only line\n"	// Test handling of comment only lines
				+ "\n"	// Test handling of empty lines
				+ "x = 1\n"
				+ "dubious and long config name containing spaces = phew! it worked!");
		cfg.populate(sc);
		
		assertEquals("hi!", cfg.getConfigFor("hello"));		
		assertEquals("bar = foobar", cfg.getConfigFor("foo"));
		assertEquals(1, cfg.getIntConfigFor("x"));
		assertEquals("phew! it worked!", cfg.getConfigFor("dubious and long config name containing spaces"));
	}
}
