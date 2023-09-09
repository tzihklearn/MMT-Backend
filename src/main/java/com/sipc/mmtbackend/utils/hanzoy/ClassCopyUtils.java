package com.sipc.mmtbackend.utils.hanzoy;

import java.lang.reflect.Field;

public class ClassCopyUtils {
        public ClassCopyUtils() {
        }

        public static void ClassCopy(Object dest, Object src) {
            Class<?> srcClass = src.getClass();
            Field[] srcClassDeclaredFields = srcClass.getDeclaredFields();
            Class<?> destClass = dest.getClass();
            Field[] destClassDeclaredFields = destClass.getDeclaredFields();
            int var7 = destClassDeclaredFields.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Field destClassDeclaredField = destClassDeclaredFields[var8];
                destClassDeclaredField.setAccessible(true);
                String name = destClassDeclaredField.getName();
                if (destClassDeclaredField.isAnnotationPresent(CopyProperty.class)) {
                    name = destClassDeclaredField.getAnnotation(CopyProperty.class).value();
                }

                int var12 = srcClassDeclaredFields.length;

                for(int var13 = 0; var13 < var12; ++var13) {
                    Field srcClassDeclaredField = srcClassDeclaredFields[var13];
                    srcClassDeclaredField.setAccessible(true);
                    if (srcClassDeclaredField.getName().equals(name)) {
                        try {
                            destClassDeclaredField.set(dest, srcClassDeclaredField.get(src));
                        } catch (IllegalAccessException var16) {
                            var16.printStackTrace();
                        }
                    }
                }
            }

        }
}
