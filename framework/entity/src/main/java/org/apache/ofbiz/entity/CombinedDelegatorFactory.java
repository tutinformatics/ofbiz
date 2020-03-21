package org.apache.ofbiz.entity;

import org.apache.ofbiz.base.util.Debug;

public class CombinedDelegatorFactory extends DelegatorFactory {

    public static final String module = CombinedDelegatorFactory.class.getName();

    // TODO: add option to specify second delegator elegantly
    @Override
    public Delegator getInstance(String delegatorName) {
        if (Debug.infoOn()) {
            Debug.logInfo("Creating new delegator [" + delegatorName + "] (" + Thread.currentThread().getName() + ")", module);
        }
        try {
            return new CombinedDelegator(delegatorName);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error creating delegator: " + e.getMessage(), module);
            return null;
        }
    }
}
