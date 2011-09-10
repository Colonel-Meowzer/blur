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

package com.nearinfinity.blur.manager.clusterstatus;

import java.util.ArrayList;
import java.util.List;

import com.nearinfinity.blur.thrift.generated.TableDescriptor;

public abstract class ClusterStatus {

    public abstract List<String> getOnlineShardServers(String cluster);

    public abstract List<String> getControllerServerList();

    public abstract List<String> getShardServerList(String cluster);
    
    public abstract List<String> getClusterList();

    public abstract TableDescriptor getTableDescriptor(String table);

    public abstract List<String> getTableList();

    public abstract String getCluster(String table);

    public List<String> getOfflineShardServers(String cluster) {
        List<String> shardServerList = new ArrayList<String>(getShardServerList(cluster));
        shardServerList.removeAll(getOnlineShardServers(cluster));
        return shardServerList;
    }

}
