package cn.enjoy.order.listener;

import cn.enjoy.order.utils.LoadBalance;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VULCAN on 2018/7/28.
 */


public class InitListenerZkClient implements ServletContextListener {

    private  static final String BASE_SERVICES = "/services";
    private static final String  SERVICE_NAME="/products";

//    private ZooKeeper zooKeeper;
    private ZkClient zkc;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            zkc = new ZkClient("59.110.139.17:2181", 3000);
            zkc.subscribeChildChanges(BASE_SERVICES + SERVICE_NAME, new IZkChildListener() {
                @Override
                public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                    System.out.println("parentPath: " + parentPath+"   ;currentChilds: " + currentChilds);
                    updateServiceList();
                }
            });
//             zooKeeper = new ZooKeeper("localhost:2181",5000,(watchedEvent)->{
//                if(watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged  && watchedEvent.getPath().equals(BASE_SERVICES+SERVICE_NAME)) {
//                    updateServiceList();
//                }
//            });

            updateServiceList();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateServiceList() {
       try{
//           List<String> children = zooKeeper.getChildren(BASE_SERVICES  + SERVICE_NAME, true);
           List<String> children = zkc.getChildren(BASE_SERVICES  + SERVICE_NAME);
           List<String> newServerList = new ArrayList<String>();
           for(String subNode:children) {
               System.out.println("subNode-host:"+subNode);
//               byte[] data = zooKeeper.getData(BASE_SERVICES  + SERVICE_NAME + "/" + subNode, false, null);
               String host = zkc.readData(BASE_SERVICES  + SERVICE_NAME + "/" + subNode);
//               String host = new String(data, "utf-8");
               System.out.println("监控到变化-host:"+host);
               newServerList.add(host);
           }
           LoadBalance.SERVICE_LIST = newServerList;
       }catch (Exception e) {
           e.printStackTrace();
       }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
