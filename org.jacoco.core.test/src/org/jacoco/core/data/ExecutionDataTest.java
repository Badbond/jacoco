/*******************************************************************************
 * Copyright (c) 2009, 2021 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 *******************************************************************************/
package org.jacoco.core.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.math.BigInteger;

/**
 * Unit tests for {@link ExecutionData}.
 */
public class ExecutionDataTest {

	@Test
	public void testCreateEmpty() {
		final ExecutionData e = new ExecutionData(5, "Example", 3);
		assertEquals(5, e.getId());
		assertEquals("Example", e.getName());
		assertEquals(3, e.getProbes().length);
		assertEquals(BigInteger.ZERO, e.getProbes()[0]);
		assertEquals(BigInteger.ZERO, e.getProbes()[1]);
		assertEquals(BigInteger.ZERO, e.getProbes()[2]);
	}

	@Test
	public void testGetters() {
		final BigInteger[] data = new BigInteger[0];
		final ExecutionData e = new ExecutionData(5, "Example", data);
		assertEquals(5, e.getId());
		assertEquals("Example", e.getName());
		assertSame(data, e.getProbes());
	}

	@Test
	public void testReset() {
		final ExecutionData e = new ExecutionData(5, "Example",
				new BigInteger[] { BigInteger.ONE, BigInteger.ZERO,
						BigInteger.valueOf(2) });
		e.reset();
		assertEquals(BigInteger.ZERO, e.getProbes()[0]);
		assertEquals(BigInteger.ZERO, e.getProbes()[1]);
		assertEquals(BigInteger.ZERO, e.getProbes()[2]);
	}

	@Test
	public void testHasHits() {
		final BigInteger[] probes = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ZERO, BigInteger.ZERO };
		final ExecutionData e = new ExecutionData(5, "Example", probes);
		assertFalse(e.hasHits());
		probes[1] = BigInteger.ONE;
		assertTrue(e.hasHits());
	}

	@Test
	public void testHasHits_empty() {
		final BigInteger[] probes = new BigInteger[] {};
		final ExecutionData e = new ExecutionData(5, "Example", probes);
		assertFalse(e.hasHits());
	}

	@Test
	public void testMerge() {
		final ExecutionData a = new ExecutionData(5, "Example",
				new BigInteger[] { BigInteger.ZERO, BigInteger.ONE,
						BigInteger.ZERO, BigInteger.valueOf(2) });
		final ExecutionData b = new ExecutionData(5, "Example",
				new BigInteger[] { BigInteger.ZERO, BigInteger.ZERO,
						BigInteger.ONE, BigInteger.valueOf(2) });
		a.merge(b);

		// b is merged into a:
		assertEquals(BigInteger.ZERO, a.getProbes()[0]);
		assertEquals(BigInteger.ONE, a.getProbes()[1]);
		assertEquals(BigInteger.ONE, a.getProbes()[2]);
		assertEquals(BigInteger.valueOf(4), a.getProbes()[3]);

		// b must not be modified:
		assertEquals(BigInteger.ZERO, b.getProbes()[0]);
		assertEquals(BigInteger.ZERO, b.getProbes()[1]);
		assertEquals(BigInteger.ONE, b.getProbes()[2]);
		assertEquals(BigInteger.valueOf(2), b.getProbes()[3]);
	}

	@Test
	public void testMergeSubtract() {
		final ExecutionData a = new ExecutionData(5, "Example",
				new BigInteger[] { BigInteger.ZERO, BigInteger.ONE,
						BigInteger.ZERO, BigInteger.valueOf(2) });
		final ExecutionData b = new ExecutionData(5, "Example",
				new BigInteger[] { BigInteger.ZERO, BigInteger.ZERO,
						BigInteger.ONE, BigInteger.valueOf(2) });
		a.merge(b, false);

		// b is subtracted from a:
		assertEquals(BigInteger.ZERO, a.getProbes()[0]);
		assertEquals(BigInteger.ONE, a.getProbes()[1]);
		assertEquals(BigInteger.ZERO, a.getProbes()[2]);
		assertEquals(BigInteger.ZERO, a.getProbes()[3]);

		// b must not be modified:
		assertEquals(BigInteger.ZERO, b.getProbes()[0]);
		assertEquals(BigInteger.ZERO, b.getProbes()[1]);
		assertEquals(BigInteger.ONE, b.getProbes()[2]);
		assertEquals(BigInteger.valueOf(2), b.getProbes()[3]);
	}

	@Test
	public void testAssertCompatibility() {
		final ExecutionData a = new ExecutionData(5, "Example",
				new BigInteger[] { BigInteger.ONE });
		a.assertCompatibility(5, "Example", 1);
	}

	@Test(expected = IllegalStateException.class)
	public void testAssertCompatibilityNegative1() {
		final ExecutionData a = new ExecutionData(5, "Example",
				new BigInteger[] { BigInteger.ONE });
		a.assertCompatibility(55, "Example", 1);
	}

	@Test(expected = IllegalStateException.class)
	public void testAssertCompatibilityNegative2() {
		final ExecutionData a = new ExecutionData(5, "Example",
				new BigInteger[] { BigInteger.ONE });
		a.assertCompatibility(5, "Exxxample", 1);
	}

	@Test(expected = IllegalStateException.class)
	public void testAssertCompatibilityNegative3() {
		final ExecutionData a = new ExecutionData(5, "Example",
				new BigInteger[] { BigInteger.ONE });
		a.assertCompatibility(5, "Example", 3);
	}

	@Test
	public void testToString() {
		final ExecutionData a = new ExecutionData(Long.MAX_VALUE, "Example",
				new BigInteger[] { BigInteger.ONE });
		assertEquals("ExecutionData[name=Example, id=7fffffffffffffff]",
				a.toString());
	}

}
