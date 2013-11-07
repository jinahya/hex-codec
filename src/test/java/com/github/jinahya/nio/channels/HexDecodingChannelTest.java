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
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 */
public class HexDecodingChannelTest {


    /**
     * logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(HexDecodingChannelTest.class);


    @Test
    public void read_eagerChannelInitialization() throws IOException {

        final byte[] encodedBytes = HexCodecTests.encodedBytes();

        final InputStream is = new ByteArrayInputStream(encodedBytes);
        final ReadableByteChannel rbc = Channels.newChannel(is);

        try (final HexDecodingChannel hdc = new HexDecodingChannel(rbc);) {
            final ByteBuffer dst = ByteBuffer.allocate(8192);
            int count = 0;
            for (int read; (read = hdc.read(dst)) != -1; count += read) {
                dst.position(0);
            }
            Assert.assertEquals(count, encodedBytes.length >> 1);
        }
    }


    @Test(enabled = true)
    public void read_lazyChannelInitialization() throws IOException {

        final byte[] encodedBytes = HexCodecTests.encodedBytes();

        final HexDecodingChannel hdc = new HexDecodingChannel(null) {


            @Override
            public int read(final ByteBuffer dst) throws IOException {

                if (channel == null) {
                    final InputStream is
                        = new ByteArrayInputStream(encodedBytes);
                    channel = Channels.newChannel(is);
                }

                return super.read(dst);
            }


        };

        try {
            final ByteBuffer dst = ByteBuffer.allocate(8192);
            int count = 0;
            for (int read; (read = hdc.read(dst)) != -1; count+= read) {
                dst.position(0);
            }
            Assert.assertEquals(count, encodedBytes.length >> 1);
        } finally {
            hdc.close();
        }
    }


}

