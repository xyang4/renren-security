package io.renren.common.enums;

import lombok.Getter;

/**
 * 用户枚举
 */
public class UserEntityEnum {

    /**
     * 用户类型：porter搬运工、mer商户、agent1级代理商、agent2级代理商、agent3级代理商、clerk接单员
     */
    @Getter
    public static enum UserType{
        PORTER("porter","搬运工"),MER("mer","商户")
        ,AGENT1("agent1","1级代理商"),AGENT2("agent2","2级代理商"),AGENT3("agent3","3级代理商")
        ,CLERK("clerk","接单员");
        private String value; private String name;
        UserType(String value,String name){this.value = value;this.name = name;}
    }

    /**
     * 状态：-1停用 0-待审核 1-审核通过，启用 10-黑户 11-异常
     */
    @Getter
    public static enum Status{
        STOP(-1,"停用"),INIT(0,"待审核"),VALID(1,"审核通过，启用")
        ,BLACK(10,"黑户"),INVALID(11,"黑户");

        private int value; private String name;
        Status(int value,String name){this.value = value;this.name = name;}
    }



}
