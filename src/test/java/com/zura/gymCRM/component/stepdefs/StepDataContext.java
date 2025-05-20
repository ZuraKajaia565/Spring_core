package com.zura.gymCRM.component.stepdefs;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component
@Scope("cucumber-glue")
public class StepDataContext {
    private int responseStatus;
    private Exception lastException;
    private MvcResult mvcResult;

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Exception getLastException() {
        return lastException;
    }

    public void setLastException(Exception lastException) {
        this.lastException = lastException;
    }

    public MvcResult getMvcResult() {
        return mvcResult;
    }

    public void setMvcResult(MvcResult mvcResult) {
        this.mvcResult = mvcResult;
    }
}