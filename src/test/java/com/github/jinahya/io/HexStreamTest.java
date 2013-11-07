/*
 * Copyright 2013 <a href="mailto:onacit@gmail.com">Jin Kwon</a>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.github.jinahya.io;


import com.github.jinahya.codec.HexCodecTests;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 */
public class HexStreamTest {


    /**
     * logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(HexStreamTest.class);


    @Test(enabled = true, invocationCount = 32)
    public static void writeRead_() throws IOException {

        final byte[] expected = HexCodecTests.decodedBytes();

        final ByteArrayOutputStream baos
            = new ByteArrayOutputStream(expected.length << 1);
        try (final HexEncodingStream hes = new HexEncodingStream(baos)) {
            hes.write(expected);
            hes.flush();
        }

        final byte[] encoded = baos.toByteArray();
        Assert.assertEquals(encoded.length, expected.length << 1);

        final ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
        try (final HexDecodingStream hds = new HexDecodingStream(bais)) {
            final byte[] actual = new byte[expected.length];
            for (int offset = 0; offset < actual.length;) {
                offset += hds.read(actual, offset, actual.length - offset);
            }
            Assert.assertEquals(actual, expected);
        }
    }


}

