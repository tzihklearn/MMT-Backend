package com.sipc.mmtbackend.utils;

import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import org.springframework.stereotype.Component;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.27
 */
@Component
public class ThreadLocalContextUtil {

    private static final ThreadLocal<BTokenSwapPo> THREAD_LOCAL_Context = new ThreadLocal<>();

    public static void setTHREAD_LOCAL_Context(BTokenSwapPo bTokenSwapPo) {
        THREAD_LOCAL_Context.set(bTokenSwapPo);
    }

    public static BTokenSwapPo getContext() {
        return THREAD_LOCAL_Context.get();
    }

    public static void remove() {
        THREAD_LOCAL_Context.remove();
    }

}
