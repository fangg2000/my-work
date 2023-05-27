package com.xclj.common.util;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class SplitWordUtil {

    /*public static void main(String[] args) {
        System.out.println("---------解释开始---------");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        
//      String path = "D:/fangg/book/逆天邪神.txt";
    String path = "D:/fangg/book/剑来.txt";
//      String path = "D:/fangg/book/元尊.txt";
//      String path = "D:/fangg/book/牧神记.txt";
//      String path = "D:/fangg/book/永夜君王.txt";
//      String path = "D:/fangg/book/大主宰.txt";
//      String path = "D:/fangg/book/校花的贴身高手.txt";
//      String path = "D:/fangg/book/仙逆.txt";
//      String path = "D:/fangg/book/苍龙混世.txt";
//      String path = "D:/fangg/book/jy/神雕侠侣.txt";
//      String path = "D:/fangg/book/jy/a.txt";
//      String path = "D:/fangg/book/jy/天龙八部.txt";
//      String path = "D:/fangg/book/jy/白马啸西风.txt";
//  String path = "D:/fangg/book/jy/碧血剑.txt";
//  String path = "D:/fangg/book/jy/飞狐外传.txt";
//  String path = "D:/fangg/book/jy/连城诀.txt";
//      String path = "D:/fangg/book/jy/鹿鼎记.txt";
//  String path = "D:/fangg/book/jy/三十三剑客图.txt";
//      String path = "D:/fangg/book/jy/射雕英雄传.txt";
//      String path = "D:/fangg/book/jy/书剑恩仇录.txt";
//      String path = "D:/fangg/book/jy/侠客行.txt";
//      String path = "D:/fangg/book/jy/笑傲江湖.txt";
//  String path = "D:/fangg/book/jy/雪山飞狐.txt";
//      String path = "D:/fangg/book/jy/倚天屠龙记.txt";
//      String path = "D:/fangg/book/jy/鸳鸯刀.txt";
//      String path = "D:/fangg/book/jy/越女剑.txt";
//  String path = "D:/fangg/book/gulong/霸王枪.txt";
//  String path = "D:/fangg/book/gulong/白玉雕龙.txt";
//  String path = "D:/fangg/book/gulong/白玉老虎.txt";
//  String path = "D:/fangg/book/gulong/碧血洗银枪.txt";
//  String path = "D:/fangg/book/gulong/碧玉刀.txt";
//  String path = "D:/fangg/book/gulong/边城刀声.txt";
//  String path = "D:/fangg/book/gulong/边城浪子.txt";
//  String path = "D:/fangg/book/gulong/蝙蝠传奇.txt";
//  String path = "D:/fangg/book/gulong/彩环曲.txt";
//  String path = "D:/fangg/book/gulong/残金缺玉.txt";
//  String path = "D:/fangg/book/gulong/苍穹神剑.txt";
//  String path = "D:/fangg/book/gulong/大地飞鹰.txt";
//      String path = "D:/fangg/book/gulong/大旗英雄传.txt";
//  String path = "D:/fangg/book/gulong/大人物.txt";
//  String path = "D:/fangg/book/gulong/大沙漠.txt";
//  String path = "D:/fangg/book/gulong/多情环.txt";
//      String path = "D:/fangg/book/gulong/多情剑客无情剑.txt";
//  String path = "D:/fangg/book/gulong/飞刀，又见飞刀.txt";
//  String path = "D:/fangg/book/gulong/风铃中的刀声.txt";
//  String path = "D:/fangg/book/gulong/凤舞九天.txt";
//  String path = "D:/fangg/book/gulong/孤星传.txt";
//  String path = "D:/fangg/book/gulong/护花铃.txt";
//  String path = "D:/fangg/book/gulong/画眉鸟.txt";
//  String path = "D:/fangg/book/gulong/欢乐英雄.txt";
//      String path = "D:/fangg/book/gulong/浣花洗剑录.txt";
//  String path = "D:/fangg/book/gulong/火并萧十一郎.txt";
//  String path = "D:/fangg/book/gulong/剑·花·烟雨江南.txt";
//  String path = "D:/fangg/book/gulong/剑毒梅香.txt";
//  String path = "D:/fangg/book/gulong/剑客行.txt";
//  String path = "D:/fangg/book/gulong/剑气书香.txt";
//  String path = "D:/fangg/book/gulong/剑神一笑.txt";
//      String path = "D:/fangg/book/gulong/剑玄录.txt";
//  String path = "D:/fangg/book/gulong/借尸还魂.txt";
//  String path = "D:/fangg/book/gulong/九月鹰飞.txt";
//  String path = "D:/fangg/book/gulong/绝不低头.txt";
//      String path = "D:/fangg/book/gulong/绝代双骄.txt";
//  String path = "D:/fangg/book/gulong/孔雀翎.txt";
//  String path = "D:/fangg/book/gulong/离别钩.txt";
//  String path = "D:/fangg/book/gulong/猎鹰·赌局.txt";
//      String path = "D:/fangg/book/gulong/流星·蝴蝶·剑.txt";
//      String path = "D:/fangg/book/gulong/陆小凤传奇.txt";
//      String path = "D:/fangg/book/gulong/名剑风流.txt";
//  String path = "D:/fangg/book/gulong/那一剑的风情.txt";
//  String path = "D:/fangg/book/gulong/怒剑狂花.txt";
//  String path = "D:/fangg/book/gulong/飘香剑雨.txt";
//  String path = "D:/fangg/book/gulong/七杀手.txt";
//  String path = "D:/fangg/book/gulong/七星龙王.txt";
//      String path = "D:/fangg/book/gulong/情人箭.txt";
//  String path = "D:/fangg/book/gulong/拳头.txt";
//  String path = "D:/fangg/book/gulong/三少爷的剑.txt";
//      String path = "D:/fangg/book/gulong/神君别传.txt";
//  String path = "D:/fangg/book/gulong/失魂引.txt";
//  String path = "D:/fangg/book/gulong/桃花传奇.txt";
//  String path = "D:/fangg/book/gulong/天涯·明月·刀.txt";
//  String path = "D:/fangg/book/gulong/午夜兰花.txt";
//      String path = "D:/fangg/book/gulong/武林外史.txt";
//  String path = "D:/fangg/book/gulong/湘妃剑.txt";
//      String path = "D:/fangg/book/gulong/萧十一郎.txt";
//      String path = "D:/fangg/book/gulong/新月传奇.txt";
//      String path = "D:/fangg/book/gulong/血海飘香.txt";
//  String path = "D:/fangg/book/gulong/血鹦鹉.txt";
//      String path = "D:/fangg/book/gulong/英雄无泪.txt";
//      String path = "D:/fangg/book/gulong/游侠录.txt";
//      String path = "D:/fangg/book/gulong/圆月弯刀.txt";
//      String path = "D:/fangg/book/gulong/月异星邪.txt";
//      String path = "D:/fangg/book/gulong/长生剑.txt";
//      String path = "D:/fangg/book/七界传说.txt";
//      String path = "D:/fangg/book/福尔摩斯.txt";
//      String path = "D:/fangg/book/鬼吹灯.txt";
//      String path = "D:/fangg/book/猎艳江湖梦.txt";
//      String path = "D:/fangg/book/极品家丁.txt";
//      String path = "D:/fangg/book/都市奇缘.txt";
//      String path = "D:/fangg/book/九州缥缈录.txt";
//      String path = "D:/fangg/book/三国演义.txt";
//      String path = "D:/fangg/book/诛仙.txt";
//      String path = "D:/fangg/book/凡人修仙传.txt";
//      String path = "D:/fangg/book/红楼梦.txt";
//      String path = "D:/fangg/book/魔道祖师.txt";
//      String path = "D:/fangg/book/雪中悍刀行.txt";
//      String path = "D:/fangg/book/寻秦记.txt";
//      String path = "D:/fangg/book/斗破苍穹.txt";
        
        long time = System.currentTimeMillis();

        
        // 判断文本编码
        String enCode = FileUtil.checkEncode(path);
        System.out.println("编码：" + enCode);
        if (enCode == null) {
            System.out.println("编码格式不支持");
            return;
        }


        //线程池
        ExecutorService executor = new ThreadPoolExecutor(100, 300, 10, TimeUnit.SECONDS, 
                new LinkedBlockingDeque<>(500));
        
        
        try {
            boolean nextFlag = true;
            int numRow = 0;
            List<String> listStr = null;
            TreeMap<Integer, List<String>> rowTreeMap = new TreeMap<>();

//            while (nextFlag) {
//                listStr = FileUtil.readFile(path, numRow, (numRow + 10000), enCode);
//                // 每次读取1000行
//                numRow += 10000;
//
//                if (listStr.size() > 0) {
//                    // 分页记录全部的行，每页1000行
//                    rowTreeMap.put(numRow, listStr);
//                } else {
//                    System.out.println(numRow + "行没有数据");
//                    nextFlag = false;
//                    break;
//                }
//            }
//            
//            //保存的文本内容
//            Set<String> wordSet = new HashSet<>();
//            String wordStr = FileUtil.fileRead(path, TypeConstant.FILE_TYPE_WORD);
//            JSONArray wordArr = JSONArray.parseArray(wordStr);
//            
//            System.out.println("");
//            System.out.println("词语数量：" + wordArr.size());
//            System.out.println("");
//            
//            for (Object object : wordArr) {
//                wordSet.add(String.valueOf(object));
//            }
//            
//            rowExplain(rowTreeMap, wordSet);
            
//            String txt = "";
//            Set<String> wordSet = new HashSet<>();
//            splitWord(txt, wordSet);
//            FileUtil.ageFileWrite("D:/fangg/book/word_data/word_data_new.json", JSONArray.toJSONString(wordSet));
            
//            if (wordSet.size() > wordArr.size()) {
//                FileUtil.ageFileWrite("D:/fangg/book/word_data/word_data.json", JSONArray.toJSONString(wordSet));
//            }
            
            System.out.println("");
            System.out.println("");
            System.out.println("");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
//        
//        //写入文件
//        FileUtil.ageFileWrite(path, ageMap);
        
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("---------解释结束---------");
        System.out.println("用时：" + (System.currentTimeMillis()-time));
    }*/
    
    private static void rowExplain(TreeMap<Integer, List<String>> rowTreeMap, Set<String> wordSet) {
        List<String> listStr = null;
        String txt = null;
        
        for (Entry<Integer, List<String>> entry : rowTreeMap.entrySet()) {
            listStr = entry.getValue();
            for (int i = 0,size=listStr.size(); i < size; i++) {
                //每行文本
                txt = listStr.get(i);
                //分词
                splitWord(txt, wordSet);
            }
        }
    }

    /**
     * 分词处理
     */
    private static void splitWord(String txt, Set<String> wordSet) {       
        //排除汉字外其它字符
        String [] temp = txt.split("[\u4e00-\u9fa5]");
        String wordStr = txt;
        for (String flag : temp) {
            wordStr = wordStr.replaceAll(flag, "");
        }
        
        char[] chars = wordStr.toCharArray();
        for (char c : chars) {
            wordSet.add(String.valueOf(c));
        }
    }

}
