package top.yehonghan.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取登录用户id
 * @Author yehonghan
 * @2022/5/2 21:22
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
