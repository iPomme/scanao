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

public class HelloService extends QimessagingService {
    private com.aldebaran.qimessaging.Object memory = null;
    private com.aldebaran.qimessaging.Object tts = null;

    public HelloService(){
        try{
            Session session = new Session();
            Future<Void> futp = session.connect("tcp://localhost:9559");
            synchronized (futp) {
                futp.wait(1000);
            }
            memory = session.service("ALMemory");
            tts = session.service("ALTextToSpeech");
            memory.call("subscribeToEvent", "LeftBumperPressed", "hello", "event");
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public String greet(String name) {
        System.out.println("Method greet called");
        return "Hello, " + name;
    }

    public java.lang.Object event(java.lang.Object key, java.lang.Object values, java.lang.Object msg) {
        try{
            System.out.println("event: "+ key + "/" + values + "/" + msg);
            if(values.toString().equalsIgnoreCase("1")){
                tts.call("say", "Ouch !");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            return key;
        }
    }
}