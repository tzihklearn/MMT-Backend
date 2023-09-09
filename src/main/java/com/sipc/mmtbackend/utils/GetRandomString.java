package com.sipc.mmtbackend.utils;

import java.util.Random;


public class GetRandomString {
    public static String getRandomString2(int length){
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number = random.nextInt(3);
            long result;
            switch (number) {
                case 0:
                    result = Math.round(Math.random() * 25 + 65);
                    sb.append((char) result);
                    break;
                case 1:
                    result = Math.round(Math.random() * 25 + 97);
                    sb.append((char) result);
                    break;
                case 2:
                    sb.append(new Random().nextInt(10));
                    break;
            }
        }
        return sb.toString();
    }

    public static String getRandomString3(int length){
        Random random=new Random();
        StringBuffer sb= new StringBuffer();
        for(int i=0;i<length;i++) {
            long result;
            result = Math.round(Math.random() * 25 + 65);
            sb.append((char) result);
        }
        return sb.toString();
    }
}
