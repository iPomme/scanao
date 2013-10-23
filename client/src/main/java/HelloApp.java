/*
 * -----------------------------------------------------------------------------
 *  - ScaNao is an open-source enabling Nao's control from Scala code.            -
 *  - At the low level jNaoqi is used to bridge the C++ code with the JVM.        -
 *  -                                                                             -
 *  -  CreatedBy: Nicolas Jorand                                                  -
 *  -       Date: 22 Sep 2013                                                      -
 *  -                                                                            	-
 *  -       _______.  ______      ___      .__   __.      ___       ______       	-
 *  -      /       | /      |    /   \     |  \ |  |     /   \     /  __  \      	-
 *  -     |   (----`|  ,----'   /  ^  \    |   \|  |    /  ^  \   |  |  |  |     	-
 *  -      \   \    |  |       /  /_\  \   |  . `  |   /  /_\  \  |  |  |  |     	-
 *  -  .----)   |   |  `----. /  _____  \  |  |\   |  /  _____  \ |  `--'  |     	-
 *  -  |_______/     \______|/__/     \__\ |__| \__| /__/     \__\ \______/      	-
 *  -----------------------------------------------------------------------------
 */

import com.aldebaran.qimessaging.*;
import com.aldebaran.qimessaging.Object;
public class HelloApp
{
    public static void main(String[] args) throws Exception {
        Application app = new Application(args);
        String address = "tcp://0.0.0.0:9559";
        Session session = new Session();
        QimessagingService service = new HelloService();
        DynamicObjectBuilder objectBuilder = new DynamicObjectBuilder();
        objectBuilder.advertiseMethod("greet::s(s)", service, "Greet the caller");
        objectBuilder.advertiseMethod("event::m(mmm)", service, "events callback");
        Object object = objectBuilder.object();
        service.init(object);
        System.out.println("Connecting to: " + address);
        Future<Void> fut = session.connect(address);
        synchronized(fut) {
            fut.wait(1000);
        }
        System.out.println("Registering hello service");
        session.registerService("hello", objectBuilder.object());
        while(true) {
            Thread.sleep(1);
        }
    }
}