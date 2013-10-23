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

import com.aldebaran.qimessaging.Application;
import com.aldebaran.qimessaging.Future;
import com.aldebaran.qimessaging.Session;

import java.util.ArrayList;

public class TestModuleTest {

    public static void main(String[] args) {
        try {
            Application app = new Application(args);
            Session session = new Session();
            Future<Void> fut = session.connect("tcp://192.168.2.104:9559");
            synchronized (fut) {
                fut.wait(1000);
            }

            com.aldebaran.qimessaging.Object mem = null;
            mem = session.service("ALMemory");
            ArrayList help = mem.<ArrayList>call("getEventList").get();
            mem.call("subscribeToEvent", "ALChestButton/SimpleClickOccurred", "SNTest", "event");
            mem.call("subscribeToEvent", "LeftBumperPressed", "SNTest", "event");
            mem.call("subscribeToEvent", "ALTextToSpeech/TextStarted", "SNTest", "event");
            mem.call("subscribeToEvent", "ALTextToSpeech/CurrentWord", "SNTest", "event");
            System.out.println(help);
            while(true) Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
