package com.huantek.vein.util;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SearchJmDNS extends Thread{


    private static JmDNS jmDNS;

    static {
        try {
            jmDNS = JmDNS.create();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class SampleListener implements ServiceListener, ServiceTypeListener {

        //serviceAdded 方法会自动返回搜索到服务的相关信息，比如一般都会带有ip地址、端口、serviceInfo信息，这些信息一般都是搜索到的注册的设备的信息，用这个方法就可以拿到设备的所有信息
        //有的时候这个信息ServiceInfo的值是null，这个几率不是很大，但是和手机的硬件设备有点关系（htc e1搜索不是很乐观、ASUS me302c和三星GT-I8160不错），看情况通常很少
        @Override
        public void serviceAdded(ServiceEvent serviceEvent) {
            //System.out.println("Service add:"+serviceEvent.getName());
            //System.out.println(serviceEvent.getDNS());
        }

        //移除某一个服务
        @Override
        public void serviceRemoved(ServiceEvent serviceEvent) {
            //System.out.println("Service removed : " + serviceEvent.getName() + "." + serviceEvent.getType());
        }


        @Override
        public void serviceResolved(ServiceEvent serviceEvent) {
            //System.out.println("Service Resolved:"+serviceEvent.getInfo());
        }

        //搜索局域网设备的type，该方法和ServiceAdded很像，但是这个方法不到serviceInfo，event的serviceInfo是为null
        @Override
        public void serviceTypeAdded(ServiceEvent serviceEvent) {
            //System.out.println(" serviceTypeAdded: " + serviceEvent.getInfo() );
        }

        @Override
        public void subTypeForServiceTypeAdded(ServiceEvent serviceEvent) {

        }
    }


    @Override
    public void run(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {

                    String type = "_http._tcp.local.";
                    //事实证明，执行到这个方法时会去回调（该监听的相关方法）serviceAdded、serviceRemove、serviceresolved
                    //jmdns.addServiceTypeListener(new SampleListener());
                    //执行到这个方法的时候，会去回调（该监听的相关方法）serviceTypeAdded、subTypeForServiceTypeAdded
                    jmDNS.addServiceListener(type, new SampleListener());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },5000,30000);
    }
}
