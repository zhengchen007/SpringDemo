package com.olo.ding.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author chenzheng
 *
 */

public class Pager {
	
	public static <T> List<T> getPage(List<T> list,String pageSize,String pageNum) {
		
		List<List<T>> sublists = splitList(list, Integer.parseInt(pageSize));
		
		if (sublists.size()==1) {
			return sublists.get(0);
		}else { 
			return sublists.get(Integer.parseInt(pageNum)-1);
		}
		
		
	}
	
	
	public static <T> List<List<T>> splitList(List<T> list, int blockSize) {
        List<List<T>> lists = new ArrayList<List<T>>();  
        if(blockSize == 1){
        	List<T> listTemp = new ArrayList<T>();
        	listTemp.add(list.get(0));
            lists.add(listTemp);
            return lists;
        }
        if (list != null && blockSize > 0) {  
            int listSize = list.size();  
            if(listSize<=blockSize){  
                lists.add(list);  
                return lists;  
            }  
            int batchSize = listSize / blockSize;  
            int remain = listSize % blockSize;  
            for (int i = 0; i < batchSize; i++) {  
                int fromIndex = i * blockSize;  
                int toIndex = fromIndex + blockSize;  
                lists.add(list.subList(fromIndex, toIndex));  
            }  
            if(remain>0){  
                lists.add(list.subList(listSize-remain, listSize));  
            }  
        }  
        return lists;  
    }
	
}
