package com.n8yn8.abma;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class UtilsTest {

	@Test
	public void getStartOfDay() {

		long dateStart = Utils.getStartOfDay(1554508800000L);
		Assert.assertEquals(1554508800000L, dateStart);

		long dateStart2 = Utils.getStartOfDay(1554508800001L);
		Assert.assertEquals(1554508800000L, dateStart2);

		long dateMillis = Utils.getStartOfDay(1554552000000L);
		Assert.assertEquals(1554508800000L, dateMillis);
	}
}