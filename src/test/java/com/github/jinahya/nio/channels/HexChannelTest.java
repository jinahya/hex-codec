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


package com.github.jinahya.nio.channels;


import com.github.jinahya.codec.HexCodecTests;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 */
public class HexChannelTest {


    @Test
    public void writeRead_() throws IOException {

        final byte[] expected = HexCodecTests.decodedBytes();

        final ByteArrayOutputStream baos
            = new ByteArrayOutputStream(expected.length << 1);
        try (final HexEncodingChannel hec
            = new HexEncodingChannel(Channels.newChannel(baos))) {
            for (final ByteBuffer src = ByteBuffer.wrap(expected);
                 src.hasRemaining(); hec.write(src)) {
            }
        }

        final ByteArrayInputStream bais
            = new ByteArrayInputStream(baos.toByteArray());
        try (final HexDecodingChannel hdc
            = new HexDecodingChannel(Channels.newChannel(bais))) {
            final byte[] actual = new byte[expected.length];
            for (final ByteBuffer dst = ByteBuffer.wrap(actual);
                 dst.hasRemaining(); hdc.read(dst)) {
            }
            Assert.assertEquals(actual, expected);
        }
    }


}

