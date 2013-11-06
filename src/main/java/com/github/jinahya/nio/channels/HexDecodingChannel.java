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


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 */
public class HexDecodingChannel implements ReadableByteChannel {


    public HexDecodingChannel(final ReadableByteChannel channel) {

        super();

        this.channel = channel;

        bytes = new byte[2];
        buffer = ByteBuffer.wrap(bytes);
    }


    @Override
    public int read(final ByteBuffer dst) throws IOException {

        if (dst == null) {
            throw new NullPointerException("dst");
        }

        if (channel == null) {
            throw new IllegalStateException("channel is currently null");
        }

        final int position = dst.position();

        while (dst.hasRemaining()) {
            buffer.position(0);
            for (int read; buffer.hasRemaining();) {
                read = channel.read(buffer);
                if (read == -1) {
                    break;
                }
            }
            if (buffer.position() == 0) {
                break;
            } else if (buffer.position() == 1) {
                throw new IOException("unacceptable end of channel");
            } else {
                dst.put((byte) HexDecoder.decodeSingle(bytes, 0));
            }
        }

        return dst.position() - position;

//        if (dst.remaining() == 0) {
//            return 0;
//        } else if (dst.remaining() == 1) {
//            for (buffer.reset(); buffer.hasRemaining();) {
//                final int read = channel.read(buffer);
//                if (read == -1) {
//                    if (buffer.position() == 0) {
//                        return -1;
//                    }
//                    throw new IOException("unacceptable end of channel");
//                }
//            }
//            assert buffer.remaining() == 0;
//            dst.put((byte) HexDecoder.decodeSingle(bytes, 0));
//            return 1;
//        }
//        assert dst.remaining() >= 2;
//
//        final int limit = dst.limit();
//        if ((dst.remaining() & 1) == 1) { // odd number of remaining
//            dst.limit(limit - 1);
//        }
//        assert (dst.remaining() & 1) == 0;
//
//        int count = 0;
//        do {
//            final int read = channel.read(dst);
//            if (read == -1) {
//                if (count == 0) {
//                    return -1;
//                }
//                if ((count & 1) == 1) {
//                    throw new IOException("unacceptable end of channel");
//                }
//            }
//            count += read;
//            if ((count & 1) == 0) {
//                break;
//            }
//        } while ((count & 1) == 1);
//
//        int index = dst.position() - count;
//        for (int i = index; i < dst.position(); i += 2) {
//            bytes[0] = dst.get(i);
//            bytes[0] = dst.get(i + 1);
//            dst.put(index++, (byte) HexDecoder.decodeSingle(bytes, 0));
//        }
//        dst.position(index);
//
//        dst.limit(limit); // restore
//
//        return index;
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


    private transient byte[] bytes;


    private transient ByteBuffer buffer;


}

