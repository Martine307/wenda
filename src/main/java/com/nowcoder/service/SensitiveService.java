package com.nowcoder.service;

import com.nowcoder.controller.IndexController;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.naming.spi.InitialContextFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();

        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }

    public void addWord(String lineTxt)  {
        TrieNode tempNode=rootNode;
        for(int i=0;i<lineTxt.length();i++){
            Character c=lineTxt.charAt(i);
            TrieNode node=tempNode.getSubNode(c);

            if(node==null){
                node=new TrieNode();
                tempNode.addSubNode(c,node);
            }

            tempNode=node;
            if(i==lineTxt.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    private class TrieNode{
        private  boolean end=false;
        private Map<Character,TrieNode> subNodes=new HashMap<>();

        public void addSubNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }

        TrieNode getSubNode(Character key){
            return  subNodes.get(key);
        }

        boolean isKeywordEnd(){return end;}
        void setKeywordEnd(boolean end){
            this.end=end;
        }
    }

    private  TrieNode rootNode=new TrieNode();
    /**
     * 过滤敏感词
     */
    private  boolean isSymbol(char c){
        int ic=(int )c;
        return  !CharUtils.isAsciiAlphanumeric(c)&&(ic < 0x2E80 || ic > 0x9FFF);
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        String replacement = "***";
        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootNode;
        int begin = 0; // 回滚数
        int position = 0; // 当前比较的位置

        while (position < text.length()) {
            char c = text.charAt(position);
            if(isSymbol(c)){
                if(tempNode==rootNode){
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);

            // 当前位置的匹配结束
            if (tempNode == null) {
                // 以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                // 跳到下一个字符开始测试
                position = begin + 1;
                begin = position;
                // 回到树初始节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词， 从begin到position的位置用replacement替换掉
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {
                ++position;
            }
        }

        result.append(text.substring(begin));

        return result.toString();
    }

    public static void main(String[] argv) {
        SensitiveService s = new SensitiveService();
        s.addWord("色情");
        s.addWord("赌博");
       System.out.print(s.filter("你好色"));
        System.out.println();
    }
}
