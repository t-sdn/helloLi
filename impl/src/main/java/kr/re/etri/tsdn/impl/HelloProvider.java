/*
 * Copyright(c) 2016 ETRI All Rights Reserved and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package kr.re.etri.tsdn.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.NotificationListener;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.impl.rev141210.HelloRuntimeMXBean;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.HelloService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.HelloWorld;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloProvider implements BindingAwareProvider, AutoCloseable, HelloRuntimeMXBean {

	private static final Logger LOG = LoggerFactory.getLogger(HelloProvider.class);
	private DataBroker db;
    private NotificationProviderService notificationService;
    private ListenerRegistration<org.opendaylight.yangtools.yang.binding.NotificationListener> listenerRegistration;
	private BindingAwareBroker.RoutedRpcRegistration<HelloService> tsdnRoutedRpcReg;

	@Override
	public void onSessionInitiated(ProviderContext session) {


		//RPC wiring
		HelloWorldImpl helloWorldImpl = new HelloWorldImpl();
		this.tsdnRoutedRpcReg = session.addRoutedRpcImplementation(HelloService.class, helloWorldImpl);
		helloWorldImpl.setTsdnRoutedRpcReg(tsdnRoutedRpcReg);

		DogImpl dogImple = new DogImpl();
		this.tsdnRoutedRpcReg = session.addRoutedRpcImplementation(HelloService.class, dogImple);
		dogImple.setTsdnRoutedRpcReg(tsdnRoutedRpcReg);

//		session.addRpcImplementation(HelloService.class, helloWorldImpl);

		//Data Broker
		db = session.getSALService(DataBroker.class);
		helloWorldImpl.setDb(db);
		LOG.info("HelloProvider Session Initiated");
		
		 final InstanceIdentifier<HelloWorld> path = helloWorldImpl.HELLO_IID;
		 
		 //Data Broker Data Change Listener
	     final ListenerRegistration<DataChangeListener> dataChangeListenerRegistration = 
	                        db.registerDataChangeListener(LogicalDatastoreType.CONFIGURATION, 
	                                        path, helloWorldImpl, DataChangeScope.SUBTREE);
		//Notification publisher
		notificationService = session.getSALService(NotificationProviderService.class);
		helloWorldImpl.setNotificationService(notificationService);
		
		//Notification Listener
		HelloReceiver helloReceiver = new HelloReceiver();
		listenerRegistration = notificationService.registerNotificationListener(helloReceiver);
		
		
	}

	@Override
	public void close() throws Exception {
		LOG.info("HelloProvider Closed");
	}

	@Override
	public void updateSbNode(String id) {
		// TODO Auto-generated method stub
		LOG.info("HelloProvider updateSbNode called...");
	}

}
