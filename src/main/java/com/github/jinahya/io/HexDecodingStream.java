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


import com.github.jinahya.codec.HexDecoder;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 */
public class HexDecodingStream extends FilterInputStream {


    /**
     *
     * @param in the underlying input stream, or {@code null} if this instance
     * is to be created without an underlying stream.
     */
    public HexDecodingStream(final InputStream in) {

        super(in);

        input = new byte[2];
    }


    @Override
    public int read() throws IOException {

        if ((input[0] = (byte) super.read()) == -1) {
            return -1;
        }

        if ((input[1] = (byte) super.read()) == -1) {
            throw new IOException("unacceptable end of stream");
        }

        return HexDecoder.decodeSingle(input, 0);
    }


    private final byte[] input;


}

