package com.ttsx.background.util;

public class SelectVariables {
    public static boolean selectStringNull(String s){
        if(s == null){
            return true;
        }
        return s.trim().length() == 0;
    }

    public static boolean selectStringNull(String...value){
        for (String s : value) {
            if(selectStringNull(s)){
                return true;
            }
        }
        return false;
    }


    public static boolean selectObjectNull(Object o){
        return (o == null);
    }

    public static boolean selectObjectNull(Object...value){
        for (Object o : value) {
            if(selectObjectNull(o)){
                return true;
            }
        }
        return false;
    }
}
