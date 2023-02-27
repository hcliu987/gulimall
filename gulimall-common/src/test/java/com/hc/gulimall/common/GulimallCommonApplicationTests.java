package com.hc.gulimall.common;


import javax.validation.constraints.Pattern;
import java.io.File;

class GulimallCommonApplicationTests {
    public static void main(String[] args) {
        File[] files = new File("/Users/liuhaicheng/Downloads/").listFiles();
        for (File f : files) {
            if (!f.isDirectory()&&f.getName().length()>20) {
                System.out.println(f.getName());
                File file = new File("/Users/liuhaicheng/Downloads/" + f.getName().replace(f.getName().substring(4, 12), ""));
                f.renameTo(file);
            }

        }
    }
}

