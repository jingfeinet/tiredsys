package com.sicau.tiredsys.service;

import org.apache.commons.lang.CharUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by zhong  on 2019/5/14 15:03
 */
@Service
public class WordFilterService implements InitializingBean {
    // 默认敏感词替换符
    String replaceWord = "***";
    private TrieNode rootNode = new TrieNode();

    // 在类路径下面读取敏感词过滤文件，构建前缀树
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream iStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("FilterWords.txt");
            InputStreamReader reader = new InputStreamReader(iStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String word;
            while ((word = bufferedReader.readLine()) != null) {
                addWord(word);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 略过特殊字符和空格
    private boolean isSpecialSymbol(char c) {
        int ic = (int) c;
        // 东亚文字的范围0x2E80-0x9FFF，可以去除大部分特殊字符以及跳过所有的空格
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    // 添加节点
    public void addWord(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }
        Character character;
        TrieNode temp = rootNode;
        for (int i = 0; i < word.length(); i++) {
            character = word.charAt(i);
            if (temp.getNextNodes(character) == null) {
                temp.addNode(character, new TrieNode());
                temp = temp.getNextNodes(character);
            } else {
                temp = temp.getNextNodes(character);
            }
            if (i == word.length() - 1) {
                temp.setStatus(true);
            }
        }
    }

    // 过滤文本
    public String filter(String text) {
        if (text.isEmpty())
            return text;
        StringBuffer sb = new StringBuffer();
        TrieNode temp = rootNode;
        int start = 0;
        int end = 0;
        Character word;
        while (end < text.length()) {
            word = text.charAt(end);
            // 如果是特殊字符就跳过，同时end指针向后移
            if (isSpecialSymbol(word)) {
                end++;
                continue;
            }
            temp = temp.getNextNodes(word);
            if (temp == null) {
                sb.append(text.charAt(start));
                start++;
                end = start;
                temp = rootNode;
            } else if (temp.isEnd()) {
                end++;
                start = end;
                sb.append(replaceWord);
                temp = rootNode;
            } else {
                end++;
            }
            // 避免既没有到达end条件，又一直匹配着屏蔽词到text的最后。比如屏蔽词中有“testa”，在匹配“test”的时候会直接被吞掉。
            if (end == text.length())
                for (int j = start; j < end; j++)
                    sb.append(text.charAt(j));
        }
        return sb.toString();
    }

    // 前缀树：使用一个敏感词结束符+一个能够通过字符寻找出后面所有的子节点的hash表构成
    private class TrieNode {
        HashMap<Character, TrieNode> nextNodes = new HashMap<Character, TrieNode>();
        private boolean end = false;

        public void setStatus(boolean end) {
            this.end = end;
        }

        public TrieNode getNextNodes(Character key) {
            return nextNodes.get(key);
        }

        public boolean isEnd() {
            return this.end;
        }

        public void addNode(Character key, TrieNode nextNodes) {
            this.nextNodes.put(key, nextNodes);
        }
    }
}
