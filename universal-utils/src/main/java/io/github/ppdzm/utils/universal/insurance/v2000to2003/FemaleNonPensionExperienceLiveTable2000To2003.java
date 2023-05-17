package io.github.ppdzm.utils.universal.insurance.v2000to2003;

import io.github.ppdzm.utils.universal.insurance.AbstractExperienceLiveTable;

import java.util.List;

/**
 * 女性非养老金经验生命表（2000-20032版）
 * @author Created by Stuart Alex on 2017/3/29.
 */
public class FemaleNonPensionExperienceLiveTable2000To2003 extends AbstractExperienceLiveTable {

    public FemaleNonPensionExperienceLiveTable2000To2003(double i) {
        super(i);
    }

    @Override
    public List<Double> deadList() {
        // TODO
        return null;
    }

}
