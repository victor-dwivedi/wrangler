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

import io.cdap.wrangler.api.parser.Bool;
import io.cdap.wrangler.api.parser.BoolList;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.TextList;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.Token;
import io.cdap.wrangler.api.parser.TokenType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link TimeDuration}
 */
public class TimeDurationTest {

  @Test
  public void testTimeDurationParsing() {
    TimeDuration timeDuration = new TimeDuration("100ms");
    Assert.assertEquals(100 * 1_000_000, timeDuration.getNanoseconds());
    Assert.assertEquals(100.0, timeDuration.getNumericValue(), 0.001);
    Assert.assertEquals("ms", timeDuration.getUnit());
    Assert.assertEquals(TokenType.TIME_DURATION, timeDuration.type());
    Assert.assertEquals("100ms", timeDuration.value());
  }

  @Test
  public void testTimeDurationConversions() {
    TimeDuration timeDuration = new TimeDuration("2s");
    Assert.assertEquals(2 * 1_000_000_000, timeDuration.getNanoseconds());
    Assert.assertEquals(2.0, timeDuration.to("s"), 0.001);
    Assert.assertEquals(2000.0, timeDuration.to("ms"), 0.001);
    Assert.assertEquals(2 * 1_000_000_000, timeDuration.to("ns"), 0.001);
    Assert.assertEquals(0.0333333, timeDuration.to("m"), 0.0001);
  }
  
  @Test
  public void testDecimalValues() {
    TimeDuration timeDuration = new TimeDuration("1.5h");
    Assert.assertEquals(1.5 * 60 * 60 * 1_000_000_000, timeDuration.getNanoseconds(), 0.5);
    Assert.assertEquals(1.5, timeDuration.getNumericValue(), 0.001);
    Assert.assertEquals("h", timeDuration.getUnit());
  }
  
  @Test
  public void testAllUnits() {
    // Nanoseconds
    TimeDuration ns = new TimeDuration("1000ns");
    Assert.assertEquals(1000, ns.getNanoseconds());
    
    // Milliseconds
    TimeDuration ms = new TimeDuration("10ms");
    Assert.assertEquals(10 * 1_000_000, ms.getNanoseconds());
    
    // Seconds
    TimeDuration s = new TimeDuration("5s");
    Assert.assertEquals(5 * 1_000_000_000, s.getNanoseconds());
    
    // Minutes
    TimeDuration m = new TimeDuration("2m");
    Assert.assertEquals(2 * 60 * 1_000_000_000, m.getNanoseconds());
    
    // Hours
    TimeDuration h = new TimeDuration("1h");
    Assert.assertEquals(1 * 60 * 60 * 1_000_000_000, h.getNanoseconds());
    
    // Days
    TimeDuration d = new TimeDuration("0.5d");
    Assert.assertEquals(0.5 * 24 * 60 * 60 * 1_000_000_000, d.getNanoseconds(), 0.5);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidFormat() {
    new TimeDuration("10");  // Missing unit
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUnit() {
    new TimeDuration("10ys");  // Invalid unit
  }
} 



