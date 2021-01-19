package com.olo.ding.utils;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;




/**
 *
 * @author wsearl
 */
public class NodeUtil<T> {


    /**
     *
     * @param nodeList
     * @param rootSuperId
     * @return
     */
    public static <T> List<T> toTreeList(List<T> nodeList, String rootSuperId)throws Exception {
        List<T> nodes = new ArrayList<T>();
        //String id = null;
        String superId = null;
        	  Map<String, T> map = getMapNode(nodeList).get(0);
        	  for (T node : nodeList) {
                  //id = node.getId();
                  nodes.addAll(forList(node, superId, map, rootSuperId));
              }
      
        return nodes;
    }


    /**
     *
     * @param all
     * @param checkedId
     * @param rootSuperId
     * @return
     */
    public List<T> toTreeList(List<T> all, String checkedId, String rootSuperId) {
        List<T> nodes = new ArrayList<T>();
        //String id = null;
        String superId = null;
        Map<String, T> map = getMapNode(all).get(0);
        try {
            for (T node : all) {
                //id = node.getId();
                if (checkedId.equals(node.getClass().getMethod("getId").invoke(node).toString())) {
                    Method setMethod = node.getClass().getMethod("setChecked");
                    setMethod.invoke(node, true);
                }
                nodes.addAll(forList(node, superId, map, rootSuperId));
            }
        } catch (Exception e) {
            throw new RuntimeException("NodeUtil ");
        }
        return nodes;
    }


    public static <T> List<T> forList(T node, String superId, Map<String, T> map, String rootSuperId) {
        List<T> nodes = new ArrayList<>();
        try {
        	if (StringUtils.isEmpty(node.getClass().getMethod("getPid").invoke(node).toString())||node.getClass().getMethod("getPid").invoke(node).toString() ==null) {
        		superId = "0";
			}else {
				superId = node.getClass().getMethod("getPid").invoke(node).toString();
			}
            if (map.containsKey(superId)) {
                Method method = map.get(superId).getClass().getMethod("getChildren");
                List<T> invoke = (List<T>) method.invoke(map.get(superId));
                invoke.add(node);
            } else {
                if (!"".equals(DealString.toString(rootSuperId)) && superId.equals(rootSuperId)) {
                    nodes.add(node);
                } else if ("".equals(DealString.toString(rootSuperId))) {
                    nodes.add(node);
                }
                //map.put(id, node);
            }
        } catch (Exception e) {
            throw new RuntimeException("NodeUtil ");
        }
        return nodes;
    }

    /**
     *
     * @param nodes
     * @return
     */
    private static <T> List<Map<String, T>> getMapNode(List<T> nodes) {
        Map<String, T> map = new HashMap<>();
        Map<String, T> superMap = new HashMap<>();
        List<Map<String, T>> list = new ArrayList<>();
        try {
            for (T node : nodes) {
                if (!map.containsKey(node.getClass().getMethod("getId").invoke(node).toString())) {
                    map.put(node.getClass().getMethod("getId").invoke(node).toString(), node);
                    superMap.put(node.getClass().getMethod("getPid").invoke(node).toString(), node);
                }
            }
        } catch (Exception e) {
           System.out.println(e);
        }
        list.add(map);
        list.add(superMap);
        return list;
    }
}
