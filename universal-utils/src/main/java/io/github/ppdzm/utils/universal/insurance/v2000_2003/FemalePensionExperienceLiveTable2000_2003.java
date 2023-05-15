package io.github.ppdzm.utils.universal.insurance.v2000_2003;

import io.github.ppdzm.utils.universal.insurance.AbstractExperienceLiveTable;

import java.util.List;

/**
 * 女性养老金经验生命表（2000-20032版）
 */
public class FemalePensionExperienceLiveTable2000_2003 extends AbstractExperienceLiveTable {

    public FemalePensionExperienceLiveTable2000_2003(double i) {
        super(i);
    }

    @Override
    public List<Double> deadList() {
        // TODO
        return null;
    }

}
