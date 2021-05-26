package io.github.ppdzm.utils.universal.alert;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class UnsupportedAlerterException extends Exception {

    private static final long serialVersionUID = -2333039516580778007L;

    public UnsupportedAlerterException(String alerterType) {
        super("Unsupported Alerter type: " + alerterType);
    }

}
