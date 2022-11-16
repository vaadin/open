package com.vaadin.open;

public class Options {

    private boolean wait = false;
    private boolean background = false;
    private boolean newInstance = false;
    private boolean allowNonzeroExitCode = false;

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public boolean isWait() {
        return wait;
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    public boolean isBackground() {
        return background;
    }

    public void setNewInstance(boolean newInstance) {
        this.newInstance = newInstance;
    }

    public boolean isNewInstance() {
        return newInstance;
    }

    public void setAllowNonzeroExitCode(boolean allowNonzeroExitCode) {
        this.allowNonzeroExitCode = allowNonzeroExitCode;
    }

    public boolean isAllowNonzeroExitCode() {
        return allowNonzeroExitCode;
    }

}
