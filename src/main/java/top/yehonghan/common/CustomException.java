package top.yehonghan.common;

/**
 * 自定义业务异常
 * @Author yehonghan
 * @2022/5/2 22:44
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
