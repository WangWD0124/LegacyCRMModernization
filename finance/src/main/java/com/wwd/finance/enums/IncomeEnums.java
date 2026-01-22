// finance/src/main/java/com/wwd/finance/enums/IncomeEnums.java
package com.wwd.finance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收入相关枚举
 */
public class IncomeEnums {

    /**
     * 结算状态枚举
     */
    @Getter
    @AllArgsConstructor
    public enum SettleStatus {
        YES("Y", "已结算"),
        NO("N", "未结算");

        private final String code;
        private final String desc;

        public static String getDescByCode(String code) {
            for (SettleStatus status : values()) {
                if (status.getCode().equals(code)) {
                    return status.getDesc();
                }
            }
            return code;
        }
    }

    /**
     * 货币类型枚举
     */
    @Getter
    @AllArgsConstructor
    public enum Currency {
        CNY("CNY", "人民币"),
        USD("USD", "美元"),
        EUR("EUR", "欧元"),
        GBP("GBP", "英镑"),
        JPY("JPY", "日元");

        private final String code;
        private final String desc;

        public static String getDescByCode(String code) {
            for (Currency currency : values()) {
                if (currency.getCode().equals(code)) {
                    return currency.getDesc();
                }
            }
            return code;
        }
    }

    /**
     * 收入来源类型枚举
     */
    @Getter
    @AllArgsConstructor
    public enum SourceType {
        SALARY("SALARY", "工资", false),
        BONUS("BONUS", "奖金", false),
        INVESTMENT("INVESTMENT", "投资收益", false),
        PART_TIME("PART_TIME", "兼职收入", true),
        FREELANCE("FREELANCE", "自由职业", true),
        GIFT("GIFT", "礼金红包", false),
        OTHER("OTHER", "其他收入", false);

        private final String code;
        private final String desc;
        private final boolean requiresWorkHours; // 是否需要工作时长

        public static String getDescByCode(String code) {
            for (SourceType type : values()) {
                if (type.getCode().equals(code)) {
                    return type.getDesc();
                }
            }
            return code;
        }

        public static boolean requiresWorkHours(String code) {
            for (SourceType type : values()) {
                if (type.getCode().equals(code)) {
                    return type.isRequiresWorkHours();
                }
            }
            return false;
        }
    }

    /**
     * 物品生命周期枚举
     */
    @Getter
    @AllArgsConstructor
    public enum ItemLifecycle {
        SHORT("SHORT", "短期(1年以内)"),
        MEDIUM("MEDIUM", "中期(1-3年)"),
        LONG("LONG", "长期(3-5年)"),
        VERY_LONG("VERY_LONG", "超长期(5年以上)");

        private final String code;
        private final String desc;

        public static String getDescByCode(String code) {
            for (ItemLifecycle lifecycle : values()) {
                if (lifecycle.getCode().equals(code)) {
                    return lifecycle.getDesc();
                }
            }
            return code;
        }
    }
}