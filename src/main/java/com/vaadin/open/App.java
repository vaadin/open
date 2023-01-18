package com.vaadin.open;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public enum App {

    CHROME(() -> {
        List<String> options = new ArrayList<>();
        if (OSUtils.isWsl()) {
            options.add("/mnt/c/Program Files/Google/Chrome/Application/chrome.exe");
            options.add("/mnt/c/Program Files (x86)/Google/Chrome/Application/chrome.exe");
        } else if (OSUtils.isWindows()) {
            options.add("chrome");
        } else if (OSUtils.isMac()) {
            options.add("google chrome");
        } else if (OSUtils.isLinux()) {
            options.add("google-chrome");
            options.add("google-chrome-stable");
            options.add("chromium");
        }
        return options;
    }),

    FIREFOX(() -> {
        List<String> options = new ArrayList<>();
        if (OSUtils.isWsl()) {
            options.add("/mnt/c/Program Files/Mozilla Firefox/firefox.exe");
        } else if (OSUtils.isWindows()) {
            options.add("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
        } else if (OSUtils.isMac()) {
            options.add("firefox");
        } else if (OSUtils.isLinux()) {
            options.add("firefox");
        }
        return options;
    }),
    EDGE(() -> {
        List<String> options = new ArrayList<>();
        if (OSUtils.isWsl()) {
            options.add("/mnt/c/Program Files (x86)/Microsoft/Edge/Application/msedge.exe");
        } else if (OSUtils.isWindows()) {
            options.add("msedge");
        } else if (OSUtils.isMac()) {
            options.add("microsoft edge");
        } else if (OSUtils.isLinux()) {
            options.add("microsoft-edge");
            options.add("microsoft-edge-dev");
        }
        return options;
    }),

    SAFARI(() -> {
        List<String> options = new ArrayList<>();
        if (OSUtils.isMac()) {
            options.add("Safari");
        }
        return options;
    }),

    BRAVE(() -> {
        List<String> options = new ArrayList<>();
        if (OSUtils.isWindows()) {
            options.add("Brave");
        } else if (OSUtils.isMac()) {
            options.add("Brave Browser");
        } else if (OSUtils.isLinux()) {
            options.add("brave");
            options.add("brave-browser-stable");
            options.add("brave-browser-beta");
        }
        return options;
    }),

    OPERA(() -> {
        List<String> options = new ArrayList<>();
        if (OSUtils.isWindows()) {
            options.add("Opera");
        } else if (OSUtils.isMac()) {
            options.add("Opera");
        } else if (OSUtils.isLinux()) {
            options.add("opera");
        }
        return options;
    });

    private Supplier<List<String>> appSupplier;

    private App(Supplier<List<String>> appSupplier) {
        this.appSupplier = appSupplier;
    }

    List<String> getApp() {
        return appSupplier.get();
    }

}
