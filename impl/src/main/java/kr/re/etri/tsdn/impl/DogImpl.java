package kr.re.etri.tsdn.impl;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.cats.*;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import java.util.concurrent.Future;

/**
 * Created by user on 2017. 3. 2..
 */
public class DogImpl implements HelloService {
    private BindingAwareBroker.RoutedRpcRegistration<HelloService> tsdnRoutedRpcReg;

    public void setTsdnRoutedRpcReg(BindingAwareBroker.RoutedRpcRegistration<HelloService> tsdnRoutedRpcReg) {
        this.tsdnRoutedRpcReg = tsdnRoutedRpcReg;
        InstanceIdentifier<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.cats.Cat> path = InstanceIdentifier
                .create(Cats.class)
                .child(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.cats.Cat.class, new CatKey("dog"));

        tsdnRoutedRpcReg.registerPath(CatContext.class, path);
        //{"hello:input":{"name-ref":"/hello:cats/hello:cat[hello:name='dog']"}}
    }

    @Override
    public Future<RpcResult<AnimalWriteOutput>> animalWrite(AnimalWriteInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<HelloWorldOutput>> helloWorld(HelloWorldInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<HelloWorldReadOutput>> helloWorldRead(HelloWorldReadInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<HelloWorldWriteOutput>> helloWorldWrite(HelloWorldWriteInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<MakeSoundOutput>> makeSound(MakeSoundInput input) {


        MakeSoundOutputBuilder	makeSoundOutputBuilder = new MakeSoundOutputBuilder();

        makeSoundOutputBuilder.setResult("dog is good.");

        return RpcResultBuilder.success(makeSoundOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<Void>> noinputOutput() {
        return null;
    }
}
