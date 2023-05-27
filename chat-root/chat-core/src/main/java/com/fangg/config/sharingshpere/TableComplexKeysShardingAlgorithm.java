package com.fangg.config.sharingshpere;

import java.util.Collection;

import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

public class TableComplexKeysShardingAlgorithm implements ComplexKeysShardingAlgorithm<Comparable<Long>> {

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<String> doSharding(Collection<String> tableNames, ComplexKeysShardingValue<Comparable<Long>> shardingValue) {
		// TODO Auto-generated method stub
		return null;
	}

}
