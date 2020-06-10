package com.jiin.admin.website.util;

import java.util.Random;

public class RandomUtil {
    public static String loadRandomAlphabetCode(int size){
        Random rand = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < size; i++){
            sb.append((char) ('A' + rand.nextInt('Z' - 'A' + 1)));
        }
        return sb.toString();
    }
}
