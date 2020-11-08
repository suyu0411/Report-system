package com.antra.report.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reverse {
    public static String reverse(String s){

        String puc = "[`\\\\~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%…&*（）——+|{}【】‘；：”“’。，、?-]";
        Boolean startWithWord = false;
        if (puc.indexOf(s.charAt(0))==-1){
            startWithWord = true;
        }

        String withoutPuc = s.replaceAll(puc," ");

        String[] wordArray = withoutPuc.split(" ");
        System.out.println(Arrays.asList(wordArray));
        List<Character> charList = new ArrayList<>();
        for (char c:s.toCharArray()){
            if (puc.indexOf(c) != -1){
                charList.add(c);
            }
        }
        System.out.println(charList);

        int wordArrayindex = wordArray.length - 1;
        String[] result = new String[charList.size()+wordArray.length];
        int start1 = 0;
        while (startWithWord == true && wordArrayindex >= 0){

            result[start1]  = wordArray[wordArrayindex];
            start1 += 2;
            wordArrayindex--;
        }
        int start2 = 1;
        while (startWithWord == false && wordArrayindex >= 0){

            result[start2]  = wordArray[wordArrayindex];
            start2 += 2;
            wordArrayindex--;
        }
        int charListIndex = 0;
        for (int i = 0; i < result.length; i++) {
            if (result[i] == null) {
                result[i] = String.valueOf(charList.get(charListIndex));
                charListIndex++;
            }
        }
        System.out.println(Arrays.asList(result));
        StringBuilder sb = new StringBuilder();
        for (String str:result){
            sb.append(str);
        }
        System.out.println("****:"+sb.toString());

        return sb.toString();
    }

    public static void main(String[] args) {
        String s = "Hello,wo-jiu-cao-le";
        reverse(s);

    }
}
