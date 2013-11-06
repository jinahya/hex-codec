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


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 */
public class HexEncodingStream extends FilterOutputStream {


    public HexEncodingStream(final OutputStream out) {

        super(out);

        output = new byte[2];
    }


    @Override
    public void write(final int b) throws IOException {

        HexEncoder.encodeSingle(b, output, 0);
        
        super.write(output[0]);
        super.write(output[1]);
    }


    private final byte[] output;


}

