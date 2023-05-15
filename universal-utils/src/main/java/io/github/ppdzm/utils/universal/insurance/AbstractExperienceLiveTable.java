package io.github.ppdzm.utils.universal.insurance;

import io.github.ppdzm.utils.universal.base.Mathematics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractExperienceLiveTable implements ExperienceLiveTable {
    protected Double v;
    /**
     * 生命表
     */
    protected Map<Integer, Row> rows;
    protected int maxAge;

    public AbstractExperienceLiveTable(double i) {
        this.v = 1 / (1 + i);
        this.rows = fromDeadList(deadList());
        this.maxAge = this.rows.size() - 1;
    }

    public abstract List<Double> deadList();

    /**
     * 从死亡表推算出生命表
     *
     * @param deadList 从0岁开始，每1年的死亡人数
     * @return 生命表
     */
    public static Map<Integer, Row> fromDeadList(List<Double> deadList) {
        int maxAge = deadList.size() - 1;
        List<Row> temps = new ArrayList<>();
        for (int age = 0; age < deadList.size(); age++) {
            double survivalAtStart = deadList.subList(age, deadList.size()).stream().mapToDouble(Double::doubleValue).sum();
            double deadAtEnd = deadList.get(age);
            double deadRate = Math.round(deadAtEnd / survivalAtStart * 1e6) / 1e6;
            temps.add(new Row(age, survivalAtStart, deadAtEnd, 1 - deadRate, deadRate, -1));
        }
        Map<Integer, Row> result = new HashMap<>();
        for (int i = 0; i < temps.size(); i++) {
            Row r = temps.get(i);
            if (r.getX() == maxAge)
                result.put(r.getX(), new Row(r.getX(), r.getL(), r.getD(), r.getP(), r.getQ(), 0.5));
            else {
                double sumL = temps.stream().filter(t -> t.getX() >= r.getX()).mapToDouble(Row::getL).sum();
                double e = Math.round((sumL / r.getL() + 0.5) * 10) / 10.0;
                result.put(r.getX(), new Row(r.getX(), r.getL(), r.getD(), r.getP(), r.getQ(), e));
            }
        }
        return result;
    }

    public double a(int x, int n) {
        return (m(x) - m(x + n)) / d(x);
    }

    public double p(int x, int n) {
        return 1 - q(x, n);
    }

    public double q(int x, int n) {
        double sum = 0.0;
        for (int i = x; i < x + n; i++) {
            sum += rows.get(i).getD();
        }
        return Mathematics.round(sum / rows.get(x).getL(), 6);
    }

    public double q(int x, int n, int u) {
        double sum = 0.0;
        for (int i = x + n; i < x + n + u; i++) {
            sum += rows.get(i).getD();
        }
        return Mathematics.round(sum / rows.get(x).getL(), 6);
    }

    public double r(int x) {
        double sum = 0.0;
        for (int i = x; i <= maxAge; i++) {
            sum += m(i);
        }
        return sum;
    }

    public double m(int x) {
        double sum = 0.0;
        for (int i = x; i <= maxAge; i++) {
            sum += c(i);
        }
        return sum;
    }

    public double c(int x) {
        return Math.pow(v, x + 1) * rows.get(x).getD();
    }

    public double s(int x) {
        double sum = 0.0;
        for (int i = x; i <= maxAge; i++) {
            sum += n(i);
        }
        return sum;
    }

    public double n(int x) {
        double sum = 0.0;
        for (int i = x; i <= maxAge; i++) {
            sum += d(i);
        }
        return sum;
    }

    public double d(int x) {
        return Math.pow(v, x) * rows.get(x).getL();
    }

}
