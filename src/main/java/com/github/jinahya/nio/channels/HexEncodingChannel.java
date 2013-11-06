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


import com.github.jinahya.codec.HexEncoder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 */
public class HexEncodingChannel implements WritableByteChannel {


    public HexEncodingChannel(final WritableByteChannel channel) {

        super();

        this.channel = channel;

        bytes = new byte[2];
        buffer = ByteBuffer.wrap(bytes);
    }


    @Override
    public int write(final ByteBuffer src) throws IOException {

        if (src == null) {
            throw new NullPointerException("src");
        }

        if (channel == null) {
            throw new IllegalStateException("channel is currently null");
        }

        final int position = src.position();

        while (src.hasRemaining()) {
            HexEncoder.encodeSingle(src.get() & 0xFF, bytes, 0);
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            buffer.position(0);
        }

        return src.position() - position;
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


    protected WritableByteChannel channel;


    private final byte[] bytes;


    private final ByteBuffer buffer;


}

