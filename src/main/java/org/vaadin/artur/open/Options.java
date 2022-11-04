package org.vaadin.artur.open;

public class Options {

    private boolean wait = false;
    private boolean background = false;
    private boolean newInstance = false;
    private boolean allowNonzeroExitCode = false;

    public boolean isWait() {
        return wait;
    }

    public boolean isBackground() {
        return background;
    }

    public boolean isNewInstance() {
        return newInstance;
    }

    public boolean isAllowNonzeroExitCode() {
        return allowNonzeroExitCode;
    }

}
