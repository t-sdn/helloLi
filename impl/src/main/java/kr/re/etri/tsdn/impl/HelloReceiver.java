package kr.re.etri.tsdn.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.HelloListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.MultipleOfTens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloReceiver implements HelloListener {
	
    private static final Logger LOG = LoggerFactory.getLogger(HelloReceiver.class);
	@Override
	public void onMultipleOfTens(MultipleOfTens notification) {
		// TODO Auto-generated method stub
		int a = 10;
				
		++a;
		++a;
		
		LOG.info("on Multiple of Tens.");
	}

}
