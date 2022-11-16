package com.vaadin.open;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Open {

    private static String wslMountPoint = null;

    /**
     * Get the mount point for fixed drives in WSL.
     * 
     * @inner
     * @returns {string} The mount point.
     */
    static String getWslDrivesMountPoint() {
        // Default value for "root" param
        // according to https://docs.microsoft.com/en-us/windows/wsl/wsl-config
        if (wslMountPoint != null) {
            return wslMountPoint;
        }

        String defaultMountPoint = "/mnt/";

        String configFilePath = "/etc/wsl.conf";
        File configFile = new File(configFilePath);
        boolean isConfigFileExists = configFile.exists();

        if (!isConfigFileExists) {
            return defaultMountPoint;
        }

        try {
            String configContent = FileUtil.readFile(configFile);
            Pattern p = Pattern.compile("(?<!#.*)root\\s*=\\s*(?<mountPoint>.*)");
            Matcher matcher = p.matcher(configContent);
            if (!matcher.matches()) {
                return defaultMountPoint;
            }

            String mountPoint = matcher.group("mountPoint").trim();
            return mountPoint.endsWith("/") ? mountPoint : mountPoint + "/";
        } catch (IOException e) {
            e.printStackTrace();
            return defaultMountPoint;
        }

    }

    private static boolean doOpen(String target, String app, List<String> appArguments, Options options) {
        String command;
        List<String> cliArguments = new ArrayList<>();
        boolean consumeOutput = false;

        if (OSUtils.isMac()) {
            command = "open";
            if (options.isWait()) {
                cliArguments.add("--wait-apps");
            }
            if (options.isBackground()) {
                cliArguments.add("--background");
            }

            if (options.isNewInstance()) {
                cliArguments.add("--new");
            }

            if (app != null) {
                cliArguments.add("-a");
                cliArguments.add(app);
            }
        } else if (OSUtils.isWindows() || (OSUtils.isWsl() && !Docker.isDocker())) {
            boolean isWsl = OSUtils.isWsl();
            // Windows or WSL on windows
            String mountPoint = getWslDrivesMountPoint();
            if (isWsl) {
                command = mountPoint + "c/Windows/System32/WindowsPowerShell/v1.0/powershell.exe";
            } else {
                command = System.getenv("SYSTEMROOT") + "\\System32\\WindowsPowerShell\\v1.0\\powershell";
            }

            cliArguments.addAll(
                    Arrays.asList("-NoProfile", "-NonInteractive", "–ExecutionPolicy", "Bypass", "-EncodedCommand"));

            List<String> encodedArguments = new ArrayList<>();
            encodedArguments.add("Start");

            if (options.isWait()) {
                encodedArguments.add("-Wait");
            }

            if (app != null) {
                // Double quote with double quotes to ensure the inner quotes are passed
                // through.
                // Inner quotes are delimited for PowerShell interpretation with backticks.
                encodedArguments.add("\"`\"" + app + "`\"\"");
                encodedArguments.add("-ArgumentList");
                if (target != null) {
                    appArguments.add(0, target);
                }
            } else if (target != null) {
                encodedArguments.add("\"" + target + "\"");
            }

            if (!appArguments.isEmpty()) {
                String joinedArgs = appArguments.stream().map(arg -> "\"`\"" + arg + "`\"\"")
                        .collect(Collectors.joining(","));
                encodedArguments.add(joinedArgs);
            }
            // Using Base64-encoded command, accepted by PowerShell, to allow special
            // characters.
            target = Base64.getEncoder().encodeToString(
                    encodedArguments.stream().collect(Collectors.joining(" ")).getBytes(StandardCharsets.UTF_16LE));

        } else {
            if (app != null) {
                command = app;
            } else {
                // Use system xdg-open if we cannot use the included one
                command = "xdg-open";

                // Use bundled xdg-open
                InputStream bundledXdgOpen = Open.class.getResourceAsStream("xdg-open");
                try {
                    File open = File.createTempFile("xdg", "open");
                    open.setExecutable(true);
                    open.deleteOnExit();
                    try (FileOutputStream out = new FileOutputStream(open)) {
                        FileUtil.copy(bundledXdgOpen, out);
                        command = open.getAbsolutePath();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!appArguments.isEmpty()) {
                cliArguments.addAll(appArguments);
            }
            if (!options.isWait()) {
                // `xdg-open` will block the process unless stdio is ignored
                // and it"s detached from the parent even if it"s unref"d.
                consumeOutput = true;
            }
        }

        if (target != null) {
            cliArguments.add(target);
        }

        if (OSUtils.isMac() && appArguments.size() > 0) {
            cliArguments.add("--args");
            cliArguments.addAll(appArguments);
        }

        cliArguments.add(0, command);
        try {
            Process subprocess = new ProcessBuilder().command(cliArguments).start();
            if (options.isWait()) {
                try {
                    int exitCode = subprocess.waitFor();
                    if (!options.isAllowNonzeroExitCode() && exitCode > 0) {
                        return false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            } else {
                if (consumeOutput) {
                    FileUtil.read(subprocess.getInputStream());
                }

                // Give it a little time to realize the command might not be found
                for (int i = 0; i < 5; i++) {
                    if (!subprocess.isAlive()) {
                        if (subprocess.exitValue() != 0) {
                            return false;
                        }
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Opens the given URL.
     * 
     * @param target the URL to open
     * 
     * @return true if the URL was launched
     */
    public static boolean open(String target) {
        return open(target, new Options());
    }

    /**
     * Opens the given URL using the given options.
     * 
     * @param target  the URL to open
     * @param options the options to use
     * 
     * @return true if the URL was launched
     */
    public static boolean open(String target, Options options) {
        return doOpen(target, null, new ArrayList<>(), options);
    }

    /**
     * Opens the given URL in the given application.
     * 
     * @param target the URL to open
     * @param app    the application to use
     * 
     * @return true if the URL was launched
     */
    public static boolean open(String target, App app) {
        return open(target, app, new Options());
    }

    /**
     * Opens the given URL in the given application using the given options.
     * 
     * @param target  the URL to open
     * @param app     the application to use
     * @param options the options to use
     * 
     * @return true if the URL was launched
     */
    public static boolean open(String target, App app, Options options) {
        for (String name : app.getApp()) {
            if (doOpen(target, name, new ArrayList<>(), options)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Opens the given application using the given arguments and options.
     * 
     * @param name         the name of the application to open
     * @param appArguments the arguments to pass to the application
     * @param options      the options to use
     * 
     * @return true if the URL was launched
     */
    public static boolean openApp(String name, List<String> appArguments, Options options) {
        return doOpen(null, name, appArguments, options);
    }

    /**
     * Opens the given application using the given arguments and options.
     * 
     * @param app          the application to open
     * @param appArguments the arguments to pass to the application
     * @param options      the options to use
     * 
     * @return true if the URL was launched
     */
    public static boolean openApp(App app, List<String> appArguments, Options options) {
        for (String name : app.getApp()) {
            if (doOpen(null, name, appArguments, options)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            String appId = args[1].toUpperCase(Locale.ENGLISH);
            App app = App.valueOf(appId);
            open(args[0], app, new Options());
        } else {
            open(args[0], new Options());
        }
    }

}
