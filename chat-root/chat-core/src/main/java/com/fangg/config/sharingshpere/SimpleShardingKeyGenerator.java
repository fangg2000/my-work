package com.fangg.config.sharingshpere;

import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;

public class SimpleShardingKeyGenerator implements KeyGenerateAlgorithm{

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "CHAT";
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Comparable<?> generateKey() {
		return null;
	}

	
	
}
