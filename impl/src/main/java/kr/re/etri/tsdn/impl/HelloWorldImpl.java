package kr.re.etri.tsdn.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.Cat;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.animal.Fish;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.animal.FishBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.cats.*;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Futures;

public class HelloWorldImpl implements HelloService, DataChangeListener {

	private static final Logger LOG = LoggerFactory.getLogger(HelloWorldImpl.class);
	public static final InstanceIdentifier<HelloWorld> HELLO_IID = InstanceIdentifier.builder(HelloWorld.class).build();
	public static final InstanceIdentifier<Organism> ORGANISM_IID = InstanceIdentifier.builder(Organism.class).build();
	private DataBroker db;
	private NotificationProviderService notificationService;
	private long helloCounter = 100L;

	public void setTsdnRoutedRpcReg(BindingAwareBroker.RoutedRpcRegistration<HelloService> tsdnRoutedRpcReg) {
		this.tsdnRoutedRpcReg = tsdnRoutedRpcReg;

		InstanceIdentifier<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.cats.Cat> path = InstanceIdentifier
				.create(Cats.class)
				.child(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.cats.Cat.class, new CatKey("cat"));

		tsdnRoutedRpcReg.registerPath(CatContext.class, path);
		//{"hello:input":{"name-ref":"/hello:cats/hello:cat[hello:name='cat']"}}
	}

	private BindingAwareBroker.RoutedRpcRegistration<HelloService> tsdnRoutedRpcReg;

	@Override
	public Future<RpcResult<HelloWorldOutput>> helloWorld(HelloWorldInput input) {
		// TODO Auto-generated method stub

		HelloWorldOutputBuilder helloBuilder = new HelloWorldOutputBuilder();
		helloBuilder.setGreating("Greetings " + input.getStrin() + "!");

		return RpcResultBuilder.success(helloBuilder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<Void>> noinputOutput() {
		// TODO Auto-generated method stub
		LOG.info("noinputOutput is called.");
		return Futures.immediateFuture( RpcResultBuilder.<Void> success().build() );
	}

	@Override
	public Future<RpcResult<HelloWorldReadOutput>> helloWorldRead(HelloWorldReadInput input) {
		final ReadWriteTransaction tx = db.newReadWriteTransaction();

		Future<Optional<HelloWorld>> readFuture = 
				tx.read(LogicalDatastoreType.OPERATIONAL, HELLO_IID);

		HelloWorldReadOutputBuilder helloReadBuilder = new HelloWorldReadOutputBuilder();
		try {
			helloReadBuilder.setStrout(input.getStrin() + ", " + readFuture.get().get().getValue());
		} catch (InterruptedException | ExecutionException e) {
			LOG.warn("[labry]Exception: ", e);
			e.printStackTrace();
		}

		//            LOG.info("[labry]helloReadBuilder.build: " + helloReadBuilder.build());


		tx.put(LogicalDatastoreType.CONFIGURATION, HELLO_IID, 
				new HelloWorldBuilder().setCounter(++helloCounter).build());
		try {
			tx.submit().get();
		} catch (InterruptedException | ExecutionException e) {
			LOG.warn("[labry]Exception: ", e);
			e.printStackTrace();
		}
		LOG.info("[labry]helloCount(read): " + helloCounter);

		return RpcResultBuilder.success(helloReadBuilder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<HelloWorldWriteOutput>> helloWorldWrite(HelloWorldWriteInput input) {

		final ReadWriteTransaction tx = db.newReadWriteTransaction();

		tx.put(LogicalDatastoreType.OPERATIONAL, HELLO_IID, new HelloWorldBuilder().setValue(input.getStrin()).build());

		tx.put(LogicalDatastoreType.CONFIGURATION, HELLO_IID, 
				new HelloWorldBuilder().setCounter(++helloCounter).build());
		try {
			tx.submit().get();
		} catch (InterruptedException | ExecutionException e) {
			LOG.warn("[labry]Exception: ", e);
			e.printStackTrace();
		}
		LOG.info("[labry]helloCount(write): " + helloCounter);

		HelloWorldWriteOutputBuilder helloWriteBuilder = new HelloWorldWriteOutputBuilder();
		helloWriteBuilder.setStrout(input.getStrin());

		return RpcResultBuilder.success(helloWriteBuilder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<MakeSoundOutput>> makeSound(MakeSoundInput input) {

		MakeSoundOutputBuilder	makeSoundOutputBuilder = new MakeSoundOutputBuilder();

		makeSoundOutputBuilder.setResult("cat is good.");

		return RpcResultBuilder.success(makeSoundOutputBuilder.build()).buildFuture();
	}

	public void setDb(DataBroker db) {
		// TODO Auto-generated method stub
		this.db = db;
	}

	public void setNotificationService(NotificationProviderService notificationService) {
		// TODO Auto-generated method stub
		this.notificationService = notificationService;
	}

	@Override
	public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> arg0) {

		DataObject dataObject = arg0.getUpdatedSubtree();
		
		if (dataObject instanceof HelloWorld) {
			HelloWorld helloWorld = (HelloWorld) dataObject;
			Long helloCount = helloWorld.getCounter();
			if (helloCount != null) {
				//                                    this.helloCounter = helloCount;
				LOG.info("[labry]onDataChanged - HelloWorldImpl: " + helloCounter);
			}

			if((helloCount % 10L) == 0L) {
				MultipleOfTens multipleOfTensNotification = new MultipleOfTensBuilder().build();
				notificationService.publish(multipleOfTensNotification);
			}
		}

	}

	@Override
	public Future<RpcResult<AnimalWriteOutput>> animalWrite(
			AnimalWriteInput input) {
		// TODO Auto-generated method stub
		final ReadWriteTransaction tx = db.newReadWriteTransaction();

		List<Fish> list = new LinkedList<>();
		FishBuilder fishBuilder = new FishBuilder();
		fishBuilder.setFishId(input.getId());
		fishBuilder.setFishName(input.getStrin());
		list.add(fishBuilder.build());
		
		tx.merge(LogicalDatastoreType.OPERATIONAL, ORGANISM_IID, new OrganismBuilder().setFish(list).build());
		
		try {
			tx.submit().get();
		} catch (InterruptedException | ExecutionException e) {
			LOG.warn("[labry]Exception: ", e);
			e.printStackTrace();
		}
		LOG.info("[labry]animalWrite(write): {} ", input.getStrin());

		AnimalWriteOutputBuilder animalWriteOutputBuilder = new AnimalWriteOutputBuilder();
		animalWriteOutputBuilder.setStrout(input.getStrin());

		return RpcResultBuilder.success(animalWriteOutputBuilder.build()).buildFuture();
	}


}
