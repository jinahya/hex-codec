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


import com.github.jinahya.codec.HexDecoder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 */
public class HexDecodingChannel implements ReadableByteChannel {


    /**
     * logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(HexDecodingChannel.class);


    public HexDecodingChannel(final ReadableByteChannel channel) {

        super();

        this.channel = channel;

        wrapped = new byte[2];
        wrapper = ByteBuffer.wrap(wrapped);
    }


    @Override
    public int read(final ByteBuffer dst) throws IOException {

        if (dst == null) {
            throw new NullPointerException("dst");
        }

        if (dst.remaining() == 0) {
            throw new IllegalArgumentException(
                "dst.remaining(" + dst.remaining() + ") == 0");
        }

        if (channel == null) {
            throw new IllegalStateException("channel is currently null");
        }

        final int position = dst.position();

        for (; dst.hasRemaining();) {
            // read full 2 nibbles
            wrapper.position(0);
            for (int read; wrapper.hasRemaining();) {
                read = channel.read(wrapper);
                if (read == -1) {
                    if (wrapper.position() == 0) {
                        final int count = dst.position() - position;
                        if (count == 0) {
                            return -1;
                        }
                        return count;
                    } else if (wrapper.position() == 1) {
                        throw new IOException("unacceptable end of channel");
                    }
                    break;
                }
            }
            assert wrapper.position() == 2;
            dst.put((byte) HexDecoder.decodeSingle(wrapped, 0));
        }

        return dst.position() - position;
    }


    @Override
    public boolean isOpen() {

        if (channel == null) {
            throw new IllegalStateException("channel is currently null");
        }

        return channel.isOpen();
    }


    @Override
    public void close() throws IOException {

        if (channel != null) {
            channel.close();
        }
    }


    protected ReadableByteChannel channel;


    private final transient byte[] wrapped;


    private final transient ByteBuffer wrapper;


}

