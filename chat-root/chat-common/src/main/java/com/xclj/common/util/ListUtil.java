package com.xclj.common.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ListUtils;

public class ListUtil extends ListUtils{

    /** 
     * 排除包含名字 (先替换后排除，保证顺序不变)
     * */
    public static void removeIncludeNameList(List<String> nameList, Set<String> nameSet) {
        //排除包含名字
        Set<String> removeSet = new HashSet<String>();
        List<String> cacheNList = new ArrayList<String>();
        String newN = null;
        
        if (nameList != null) {
            for (String nameA : nameList) {
                for (String nameB : nameList) {
                    if (nameB.equals(nameA) == false && nameA.contains(nameB)) {
                        removeSet.add(nameB);
                    }
                }
            }
            if (removeSet.isEmpty() == false) {
                for (String valiN : nameList) {
                    newN = valiN;
                    if (removeSet.contains(valiN)) {
                        for (String valiNameB : nameList) {
                            if (valiNameB.equals(valiN) == false && valiNameB.contains(valiN)) {
                                newN = valiNameB;
                                break;
                            }
                        }
                    }
                    cacheNList.add(newN);
                }
                nameList.clear();
                for (String cacheN : cacheNList) {
                    if (nameList.contains(cacheN) == false) {
                        nameList.add(cacheN);
                    }
                }
            }
        }
        
        if (nameSet != null) {
            removeSet.clear();
            for (String valiNameA : nameSet) {
                for (String valiNameB : nameSet) {
                    if (valiNameB.equals(valiNameA) == false && valiNameA.contains(valiNameB)) {
                        removeSet.add(valiNameB);
                    }
                }
            }
            if (removeSet.isEmpty() == false) {
                cacheNList.clear();
                for (String valiN : nameSet) {
                    newN = valiN;
                    if (removeSet.contains(valiN)) {
                        for (String valiNameB : nameSet) {
                            if (valiNameB.equals(valiN) == false && valiNameB.contains(valiN)) {
                                newN = valiNameB;
                                break;
                            }
                        }
                    }
                    cacheNList.add(newN);
                }
                nameSet.clear();
                for (String cacheN : cacheNList) {
                    nameSet.add(cacheN);
                }
            }
        }
    }
    
}
