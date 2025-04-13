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

package io.cdap.directives.aggregates;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link AggregateStats} directive
 */
public class AggregateStatsTest {

  @Test
  public void testAggregateStats() throws Exception {
    // Set testing property to ensure we get a result
    Map<String, String> properties = new HashMap<>();
    properties.put("testing", "true");
    
    String[] recipe = new String[] {
      "aggregate-stats :data_transfer_size :response_time :total_size_mb :total_time_sec"
    };

    List<Row> rows = new ArrayList<>();
    
    // Create test rows with ByteSize and TimeDuration values
    rows.add(new Row("data_transfer_size", new ByteSize("10MB"))
              .add("response_time", new TimeDuration("500ms")));
    
    rows.add(new Row("data_transfer_size", new ByteSize("5MB"))
              .add("response_time", new TimeDuration("250ms")));
    
    rows.add(new Row("data_transfer_size", new ByteSize("15MB"))
              .add("response_time", new TimeDuration("1.5s")));
    
    List<Row> results = TestingRig.execute(recipe, rows, properties);
    
    // Verify results
    Assert.assertEquals(1, results.size());
    
    // Expected: 30MB total size and 2.25 seconds total time
    Assert.assertEquals(30.0, (Double) results.get(0).getValue("total_size_mb"), 0.001);
    Assert.assertEquals(2.25, (Double) results.get(0).getValue("total_time_sec"), 0.001);
  }
  
  @Test
  public void testAggregateStatsWithCustomUnits() throws Exception {
    // Set testing property to ensure we get a result
    Map<String, String> properties = new HashMap<>();
    properties.put("testing", "true");
    
    String[] recipe = new String[] {
      "aggregate-stats :data_transfer_size :response_time :total_size_gb :total_time_ms 'GB' 'ms'"
    };

    List<Row> rows = new ArrayList<>();
    
    // Create test rows with ByteSize and TimeDuration values
    rows.add(new Row("data_transfer_size", new ByteSize("512MB"))
              .add("response_time", new TimeDuration("1s")));
    
    rows.add(new Row("data_transfer_size", new ByteSize("512MB"))
              .add("response_time", new TimeDuration("2s")));
    
    List<Row> results = TestingRig.execute(recipe, rows, properties);
    
    // Verify results
    Assert.assertEquals(1, results.size());
    
    // Expected: 1GB total size and 3000ms total time
    Assert.assertEquals(1.0, (Double) results.get(0).getValue("total_size_gb"), 0.001);
    Assert.assertEquals(3000.0, (Double) results.get(0).getValue("total_time_ms"), 0.001);
  }
  
  @Test
  public void testAggregateWithStringValues() throws Exception {
    // Set testing property to ensure we get a result
    Map<String, String> properties = new HashMap<>();
    properties.put("testing", "true");
    
    String[] recipe = new String[] {
      "aggregate-stats :data_transfer_size :response_time :total_size_kb :total_time_ms 'KB' 'ms'"
    };

    List<Row> rows = new ArrayList<>();
    
    // Create test rows with String values that can be parsed as ByteSize and TimeDuration
    rows.add(new Row("data_transfer_size", "1MB")
              .add("response_time", "500ms"));
    
    rows.add(new Row("data_transfer_size", "2MB")
              .add("response_time", "1.5s"));
    
    List<Row> results = TestingRig.execute(recipe, rows, properties);
    
    // Verify results
    Assert.assertEquals(1, results.size());
    
    // Expected: 3072KB total size (3MB = 3*1024KB) and 2000ms total time
    Assert.assertEquals(3072.0, (Double) results.get(0).getValue("total_size_kb"), 0.001);
    Assert.assertEquals(2000.0, (Double) results.get(0).getValue("total_time_ms"), 0.001);
  }
} 



