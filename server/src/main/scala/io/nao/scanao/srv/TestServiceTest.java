package io.nao.scanao.srv;/*
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

public class TestServiceTest {

    public static void main(String[] args) {
        try{
        Application app = new Application(args);
        Session session = new Session();
        Future<Void> fut = session.connect("tcp://192.168.2.104:9559");
        synchronized(fut) {
            fut.wait(1000);
        }

        com.aldebaran.qimessaging.Object hello = null;
        hello = session.service(io.nao.scanao.msg.Constants.MODULE_TEST());
        String greeting = hello.<String>call("greet","Nicolas").get();
        System.out.println(greeting);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
