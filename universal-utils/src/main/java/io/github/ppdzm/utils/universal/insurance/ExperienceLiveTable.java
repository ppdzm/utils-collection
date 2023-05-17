package io.github.ppdzm.utils.universal.insurance;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 经验生命表
 *
 * @author Created by Stuart Alex on 2017/3/29.
 */
public interface ExperienceLiveTable {

    double a(int x, int n);

    double p(int x, int n);

    double q(int x, int n);

    double q(int x, int n, int u);

    double r(int x);

    double m(int x);

    double c(int x);

    double s(int x);

    double n(int x);

    double d(int x);

    /**
     * 生命表的一行
     */
    @AllArgsConstructor
    @Data
    class Row {
        /**
         * 年龄
         */
        private int x;
        /**
         * 表示年龄 x 的年初存活人数，也称为生存人数或生存数
         */
        private double l;
        /**
         * 表示年龄 x 的年内死亡人数，也称为死亡数
         */
        private double d;
        /**
         * 表示年龄 x 的存活概率，即一个人在年龄 x 存活到下一年的概率
         */
        private double p;
        /**
         * 表示年龄 x 的死亡概率，即一个人在年龄 x 死亡的概率
         */
        private double q;
        /**
         * 表示年龄 x 的期望寿命，即一个人在年龄 x 时预期将再活多少年
         */
        private double e;
    }

}
