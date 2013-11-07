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


import com.github.jinahya.codec.HexEncoder;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 */
public class HexEncodingStream extends FilterOutputStream {


    /**
     * logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(HexEncodingStream.class);


    public HexEncodingStream(final OutputStream out) {

        super(out);

        encoded = new byte[2];
    }


    @Override
    public void write(final int b) throws IOException {

        if (out == null) {
            throw new IllegalStateException("out is currently null");
        }

        HexEncoder.encodeSingle(b, encoded, 0);

        //LOGGER.trace("encoded[0]: {}", encoded[0]);
        //LOGGER.trace("encoded[1]: {}", encoded[1]);
        //assert encoded[0] > 0x00;
        //assert encoded[0] <= 0x0F;
        //assert encoded[1] > 0x00;
        //assert encoded[1] <= 0x0F;

        super.write(encoded[0]);
        super.write(encoded[1]);
    }


    @Override
    public void write(final byte[] b, final int off, final int len)
        throws IOException {

        for (int i = 0; i < len; i++) {
            write(b[off + i]);
        }
    }


    private transient final byte[] encoded;


}

