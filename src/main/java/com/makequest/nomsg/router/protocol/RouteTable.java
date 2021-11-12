package com.makequest.nomsg.router.protocol;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Singleton or Router 안쪽에서 관리.
 */
@Slf4j
public class RouteTable {
    private final Map<String, String> routeHostMap = new HashMap<>();
    private final Map<String, List<String>> hostRouteMap = new HashMap<>();
    private final Object lock = new Object();

    public void addTable(String hid, String rid){
        synchronized (this.lock){
            routeHostMap.put(rid, hid);
            List<String> routes = hostRouteMap.get(hid);
            if (routes == null){
                routes = new LinkedList<>();
                routes.add(rid);
                hostRouteMap.put(hid, routes);
            } else {
              routes.add(rid);
            }
        }
    }

    public void removeTable(String hid, String rid){
        synchronized (this.lock){
            routeHostMap.remove(rid);
            List<String> routes = hostRouteMap.get(hid);
            if (routes == null){
                log.error(String.format("Route already gone H(%s) R(%s)", hid, rid));
            } else {
                routes.remove(rid);
            }
        }
    }

    public List<String> getRouteByHost(String hid){
        List<String> routes = hostRouteMap.get(hid);
        if (routes == null){
            return new LinkedList<>();
        }
        return new LinkedList<>(routes);
    }

    public String getHostByRoute(String route){
        return routeHostMap.get(route);
    }
}
