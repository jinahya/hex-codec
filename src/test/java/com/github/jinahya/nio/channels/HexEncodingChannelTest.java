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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 */
public class HexEncodingChannelTest {


    /**
     * logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(HexEncodingChannelTest.class);


    @Test
    public void write_eagerChannelInitialization() throws IOException {

        final byte[] decodedBytes = HexCodecTests.decodedBytes();

        final ByteArrayOutputStream baos
            = new ByteArrayOutputStream(decodedBytes.length << 1);
        final WritableByteChannel wbc = Channels.newChannel(baos);

        try (final HexEncodingChannel hec = new HexEncodingChannel(wbc);) {
            final ByteBuffer src = ByteBuffer.wrap(decodedBytes);
            while (src.hasRemaining()) {
                hec.write(src);
            }
            Assert.assertEquals(baos.size(), decodedBytes.length << 1);
        }
    }


    @Test(enabled = true)
    public void write_lazyChannelInitialization() throws IOException {

        final byte[] decodedBytes = HexCodecTests.decodedBytes();

        final HexEncodingChannel hec = new HexEncodingChannel(null) {


            @Override
            public int write(final ByteBuffer src) throws IOException {

                if (channel == null) {
                    final ByteArrayOutputStream baos
                        = new ByteArrayOutputStream(decodedBytes.length << 1);
                    channel = Channels.newChannel(baos);
                }

                return super.write(src);
            }


        };

        try {
            final ByteBuffer src = ByteBuffer.wrap(decodedBytes);
            while (src.hasRemaining()) {
                hec.write(src);
            }
        } finally {
            hec.close();
        }
    }


}

