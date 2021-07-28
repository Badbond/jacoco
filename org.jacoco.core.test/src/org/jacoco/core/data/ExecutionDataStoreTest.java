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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link ExecutionDataStore}.
 */
public class ExecutionDataStoreTest implements IExecutionDataVisitor {

	private ExecutionDataStore store;

	private Map<Long, ExecutionData> dataOutput;

	@Before
	public void setup() {
		store = new ExecutionDataStore();
		dataOutput = new HashMap<Long, ExecutionData>();
	}

	@Test
	public void testEmpty() {
		assertNull(store.get(123));
		assertFalse(store.contains("org/jacoco/example/Foo"));
		store.accept(this);
		assertEquals(Collections.emptyMap(), dataOutput);
	}

	@Test
	public void testPut() {
		final BigInteger[] probes = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ZERO, BigInteger.ONE };
		store.put(new ExecutionData(1000, "Sample", probes));
		final ExecutionData data = store.get(1000);
		assertSame(probes, data.getProbes());
		assertTrue(store.contains("Sample"));
		store.accept(this);
		assertEquals(Collections.singletonMap(Long.valueOf(1000), data),
				dataOutput);
	}

	@Test
	public void testReentrantAccept() {
		final BigInteger[] probes = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ZERO, BigInteger.ONE };
		store.put(new ExecutionData(1000, "Sample0", probes));
		store.put(new ExecutionData(1001, "Sample1", probes));
		store.accept(new IExecutionDataVisitor() {
			public void visitClassExecution(ExecutionData data) {
				store.put(new ExecutionData(1002, "Sample2", probes));
				ExecutionDataStoreTest.this.visitClassExecution(data);
			}
		});
		assertEquals(2, dataOutput.size());
	}

	@Test
	public void testGetContents() {
		final BigInteger[] probes = new BigInteger[] {};
		final ExecutionData a = new ExecutionData(1000, "A", probes);
		store.put(a);
		final ExecutionData aa = new ExecutionData(1000, "A", probes);
		store.put(aa);
		final ExecutionData b = new ExecutionData(1001, "B", probes);
		store.put(b);
		final Set<ExecutionData> actual = new HashSet<ExecutionData>(
				store.getContents());
		final Set<ExecutionData> expected = new HashSet<ExecutionData>(
				Arrays.asList(a, b));
		assertEquals(expected, actual);
	}

	@Test
	public void testGetWithoutCreate() {
		final ExecutionData data = new ExecutionData(1000, "Sample",
				new BigInteger[] {});
		store.put(data);
		assertSame(data, store.get(1000));
	}

	@Test
	public void testGetWithCreate() {
		final Long id = Long.valueOf(1000);
		final ExecutionData data = store.get(id, "Sample", 3);
		assertEquals(1000, data.getId());
		assertEquals("Sample", data.getName());
		assertEquals(3, data.getProbes().length);
		assertEquals(BigInteger.ZERO, data.getProbes()[0]);
		assertEquals(BigInteger.ZERO, data.getProbes()[1]);
		assertEquals(BigInteger.ZERO, data.getProbes()[2]);
		assertSame(data, store.get(id, "Sample", 3));
		assertTrue(store.contains("Sample"));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetNegative1() {
		final BigInteger[] data = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ZERO, BigInteger.ONE };
		store.put(new ExecutionData(1000, "Sample", data));
		store.get(Long.valueOf(1000), "Other", 3);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetNegative2() {
		final BigInteger[] data = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ZERO, BigInteger.ONE };
		store.put(new ExecutionData(1000, "Sample", data));
		store.get(Long.valueOf(1000), "Sample", 4);
	}

	@Test(expected = IllegalStateException.class)
	public void testPutNegative() {
		final BigInteger[] data = new BigInteger[0];
		store.put(new ExecutionData(1000, "Sample1", data));
		store.put(new ExecutionData(1000, "Sample2", data));
	}

	@Test
	public void testMerge() {
		final BigInteger[] data1 = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ONE, BigInteger.ZERO, BigInteger.valueOf(2) };
		store.visitClassExecution(new ExecutionData(1000, "Sample", data1));
		final BigInteger[] data2 = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ONE, BigInteger.valueOf(2), BigInteger.ZERO };
		store.visitClassExecution(new ExecutionData(1000, "Sample", data2));

		final BigInteger[] result = store.get(1000).getProbes();
		assertEquals(BigInteger.ZERO, result[0]);
		assertEquals(BigInteger.valueOf(2), result[1]);
		assertEquals(BigInteger.valueOf(2), result[2]);
		assertEquals(BigInteger.valueOf(2), result[3]);
	}

	@Test(expected = IllegalStateException.class)
	public void testMergeNegative() {
		final BigInteger[] data1 = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ZERO };
		store.visitClassExecution(new ExecutionData(1000, "Sample", data1));
		final BigInteger[] data2 = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ZERO, BigInteger.ZERO };
		store.visitClassExecution(new ExecutionData(1000, "Sample", data2));
	}

	@Test
	public void testSubtract() {
		final BigInteger[] data1 = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ONE, BigInteger.ZERO, BigInteger.valueOf(2) };
		store.put(new ExecutionData(1000, "Sample", data1));
		final BigInteger[] data2 = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ZERO, BigInteger.ONE, BigInteger.valueOf(2) };
		store.subtract(new ExecutionData(1000, "Sample", data2));

		final BigInteger[] result = store.get(1000).getProbes();
		assertEquals(BigInteger.ZERO, result[0]);
		assertEquals(BigInteger.ONE, result[1]);
		assertEquals(BigInteger.ZERO, result[2]);
		assertEquals(BigInteger.ZERO, result[3]);
	}

	@Test
	public void testSubtractOtherId() {
		final BigInteger[] data1 = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ONE };
		store.put(new ExecutionData(1000, "Sample1", data1));
		final BigInteger[] data2 = new BigInteger[] { BigInteger.ONE,
				BigInteger.ONE };
		store.subtract(new ExecutionData(2000, "Sample2", data2));

		final BigInteger[] result = store.get(1000).getProbes();
		assertEquals(BigInteger.ZERO, result[0]);
		assertEquals(BigInteger.ONE, result[1]);

		assertNull(store.get(2000));
	}

	@Test
	public void testSubtractStore() {
		final BigInteger[] data1 = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ONE, BigInteger.ZERO, BigInteger.valueOf(2) };
		store.put(new ExecutionData(1000, "Sample", data1));

		final ExecutionDataStore store2 = new ExecutionDataStore();
		final BigInteger[] data2 = new BigInteger[] { BigInteger.ZERO,
				BigInteger.ZERO, BigInteger.ONE, BigInteger.valueOf(2) };
		store2.put(new ExecutionData(1000, "Sample", data2));

		store.subtract(store2);

		final BigInteger[] result = store.get(1000).getProbes();
		assertEquals(BigInteger.ZERO, result[0]);
		assertEquals(BigInteger.ONE, result[1]);
		assertEquals(BigInteger.ZERO, result[2]);
		assertEquals(BigInteger.ZERO, result[3]);
	}

	@Test
	public void testReset()
			throws InstantiationException, IllegalAccessException {
		final BigInteger[] data1 = new BigInteger[] { BigInteger.ONE,
				BigInteger.valueOf(2), BigInteger.ZERO };
		store.put(new ExecutionData(1000, "Sample", data1));
		store.reset();
		final BigInteger[] data2 = store.get(1000).getProbes();
		assertNotNull(data2);
		assertEquals(BigInteger.ZERO, data2[0]);
		assertEquals(BigInteger.ZERO, data2[1]);
		assertEquals(BigInteger.ZERO, data2[2]);
	}

	// === IExecutionDataOutput ===

	public void visitClassExecution(final ExecutionData data) {
		dataOutput.put(Long.valueOf(data.getId()), data);
	}

}
