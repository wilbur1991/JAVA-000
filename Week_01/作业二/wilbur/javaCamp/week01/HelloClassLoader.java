/*******************************************************
 * Copyright (C) 2020 demo - All Rights Reserved
 *
 * This file is part of quiz.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-10-18
 * @Author jiangwenbo
 *
 *******************************************************/

package wilbur.javaCamp.week01;

import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HelloClassLoader extends ClassLoader {
    public static final String FILE_SUFFIX = ".xlass";

    public static void main(String[] args) {
        try {
            Class helloClass = new HelloClassLoader().findClass("Hello");
            Method helloMethod = helloClass.getDeclaredMethod("hello");
            Object helloObj = helloClass.newInstance();
            helloMethod.invoke(helloObj);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            RandomAccessFile classFile = new RandomAccessFile(name + FILE_SUFFIX, "r");
            byte[] bytes = new byte[(int) classFile.length()];
            classFile.read(bytes);

            byte[] finalBytes = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                finalBytes[i] = (byte) (255 - bytes[i]);
            }
            return defineClass(name, finalBytes, 0, finalBytes.length);
        } catch (Exception e) {
            //保留异常堆栈信息
            throw new ClassNotFoundException("load xlass file exception", e);
        }
    }
}
