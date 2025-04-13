/*
 * Copyright © 2023 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TokenType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link ByteSize}
 */
public class ByteSizeTest {

  @Test
  public void testByteSizeParsing() {
    ByteSize byteSize = new ByteSize("10KB");
    Assert.assertEquals(10 * 1024, byteSize.getBytes());
    Assert.assertEquals(10.0, byteSize.getNumericValue(), 0.001);
    Assert.assertEquals("KB", byteSize.getUnit());
    Assert.assertEquals(TokenType.BYTE_SIZE, byteSize.type());
    Assert.assertEquals("10KB", byteSize.value());
  }

  @Test
  public void testByteSizeConversions() {
    ByteSize byteSize = new ByteSize("2MB");
    Assert.assertEquals(2 * 1024 * 1024, byteSize.getBytes());
    Assert.assertEquals(2.0, byteSize.to("MB"), 0.001);
    Assert.assertEquals(2048.0, byteSize.to("KB"), 0.001);
    Assert.assertEquals(2 * 1024 * 1024, byteSize.to("B"), 0.001);
    Assert.assertEquals(0.001953125, byteSize.to("GB"), 0.0000001);
  }
  
  @Test
  public void testDecimalValues() {
    ByteSize byteSize = new ByteSize("1.5GB");
    Assert.assertEquals(1.5 * 1024 * 1024 * 1024, byteSize.getBytes(), 0.5);
    Assert.assertEquals(1.5, byteSize.getNumericValue(), 0.001);
    Assert.assertEquals("GB", byteSize.getUnit());
  }
  
  @Test
  public void testCaseInsensitivity() {
    ByteSize byteSize1 = new ByteSize("5mb");
    ByteSize byteSize2 = new ByteSize("5MB");
    Assert.assertEquals(byteSize1.getBytes(), byteSize2.getBytes());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidFormat() {
    new ByteSize("10");  // Missing unit
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUnit() {
    new ByteSize("10XB");  // Invalid unit
  }
} 


