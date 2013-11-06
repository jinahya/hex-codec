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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 */
public class HexEncodingStreamTest {


    @Test
    public void write_() throws IOException {

        final byte[] decodedBytes = HexCodecTests.decodedBytes();

        final ByteArrayOutputStream baos
            = new ByteArrayOutputStream(decodedBytes.length * 2);

        try (final OutputStream hos = new HexEncodingStream(baos)) {
            for (final byte decodedByte : decodedBytes) {
                hos.write(decodedByte);
            }
            hos.flush();
        }

        final byte[] encodedBytes = baos.toByteArray();
        Assert.assertEquals(encodedBytes.length, decodedBytes.length * 2);
    }


}

