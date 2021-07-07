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
package org.jacoco.core.internal.data;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link CompactDataInput} and {@link CompactDataOutput}. The
 * tests don't care about the written binary format, they just verify symmetry.
 */
public class CompactDataInputOutputTest {

	private CompactDataOutput out;

	private CompactDataInput in;

	@Before
	public void setup() throws IOException {
		PipedOutputStream pipe = new PipedOutputStream();
		out = new CompactDataOutput(pipe);
		in = new CompactDataInput(new PipedInputStream(pipe));
	}

	@Test
	public void testVarInt0x00000000() throws IOException {
		testVarInt(0x00000000);
	}

	@Test
	public void testVarInt0x0000007F() throws IOException {
		testVarInt(0x0000007F);
	}

	@Test
	public void testVarInt0x00000080() throws IOException {
		testVarInt(0x00000080);
	}

	@Test
	public void testVarInt0x00000100() throws IOException {
		testVarInt(0x00000100);
	}

	@Test
	public void testVarInt0x12345678() throws IOException {
		testVarInt(0x12345678);
	}

	@Test
	public void testVarIntMinus1() throws IOException {
		testVarInt(-1);
	}

	@Test
	public void testVarIntMinValue() throws IOException {
		testVarInt(Integer.MIN_VALUE);
	}

	@Test
	public void testVarIntMaxValue() throws IOException {
		testVarInt(Integer.MAX_VALUE);
	}

	private void testVarInt(int value) throws IOException {
		out.writeVarInt(value);
		out.close();
		assertEquals(Long.valueOf(value), Long.valueOf(in.readVarInt()));
		assertEquals(Integer.valueOf(-1), Integer.valueOf(in.read()));
	}

	@Test
	public void testPackedBooleanEmpty() throws IOException {
		testPackedBoolean();
	}

	@Test
	public void testPackedBoolean3() throws IOException {
		testPackedBoolean(false, false, true);
	}

	@Test
	public void testPackedBoolean8() throws IOException {
		testPackedBoolean(true, false, true, false, false, true, false, true);
	}

	@Test
	public void testPackedBoolean9() throws IOException {
		testPackedBoolean(true, true, false, true, false, false, true, false,
				true);
	}

	private void testPackedBoolean(boolean... values) throws IOException {
		out.writeBooleanArray(values);
		out.close();
		final boolean[] actual = in.readBooleanArray();
		for (int i = 0; i < values.length; i++) {
			assertEquals("Index " + i, Boolean.valueOf(values[i]),
					Boolean.valueOf(actual[i]));
		}
	}

	@Test
	public void testBigIntArrayEmpty() throws IOException {
		testBigIntArray();
	}

	@Test
	public void testBigIntArray0x08() throws IOException {
		testBigIntArray(BigInteger.valueOf(8));
	}

	@Test
	public void testIntArray() throws IOException {
		testBigIntArray(BigInteger.ZERO, BigInteger.ONE,
				BigInteger.valueOf(300), BigInteger.valueOf(Integer.MAX_VALUE),
				BigInteger.valueOf(Long.MAX_VALUE),
				BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.TEN));
	}

	private void testBigIntArray(BigInteger... values) throws IOException {
		out.writeBigIntegerArray(values);
		out.close();
		final BigInteger[] actual = in.readBigIntegerArray();
		for (int i = 0; i < values.length; i++) {
			assertEquals("Index " + i, values[i], actual[i]);
		}
	}

}
