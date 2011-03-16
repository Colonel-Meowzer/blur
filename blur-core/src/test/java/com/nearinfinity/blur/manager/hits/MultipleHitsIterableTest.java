/*
 * Copyright (C) 2011 Near Infinity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nearinfinity.blur.manager.hits;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.nearinfinity.blur.thrift.generated.BlurResult;

public class MultipleHitsIterableTest {
    
    @Test
    public void testMultipleHitsIterable() {
        BlurResultIterableMultiple iterable = new BlurResultIterableMultiple();
        iterable.addHitsIterable(newHitsIterable(0,0.1,3,2,9,10,2));
        iterable.addHitsIterable(newHitsIterable(7,2,9,1,34,53,12));
        iterable.addHitsIterable(newHitsIterable(4,3));
        iterable.addHitsIterable(newHitsIterable(7,2,34,132));
        iterable.addHitsIterable(newHitsIterable());
        
        for (BlurResult hit : iterable) {
            System.out.println(hit);
        }
    }

    private BlurResultIterable newHitsIterable(double... ds) {
        List<BlurResult> hits = new ArrayList<BlurResult>();
        for (double d : ds) {
            hits.add(new BlurResult(UUID.randomUUID().toString() + "-" + Double.toString(d),d,null));
        }
        return new BlurResultIterableSimple(UUID.randomUUID().toString(), hits);
    }

}
