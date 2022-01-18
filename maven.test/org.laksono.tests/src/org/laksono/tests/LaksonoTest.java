package org.laksono.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.laksono.data.DataFactory;
import org.laksono.data.DataModel;

public class LaksonoTest {
	
	static DataModel data;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		data = DataFactory.create(10);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		data = null;
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertNotNull(data);
		assertNull(data.getParent());
	}

}
