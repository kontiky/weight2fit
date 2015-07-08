package org.weight2fit.application.ui;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.FileOptionHandler;
import org.weight2fit.application.ui.shared.UiUtils;
import org.weight2fit.domain.FitFields;
import org.weight2fit.domain.FitParams;

import java.io.File;
import java.security.CodeSource;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Common logic for all <code>UiFitParamsSupplier</code> impls.
 *
 * @author Andriy Kryvtsun
 */
// TODO remove CLI arg4j @Option from this common class
public abstract class AbstractUiFitParamsSupplier implements UiFitParamsSupplier {

    private static final Logger LOG = Logger.getLogger(AbstractUiFitParamsSupplier.class.getName());

    protected FitParams params;

    @Option(name = "-o", aliases = { "--out" }, usage = "Output FIT file name", handler = FileOptionHandler.class)
    protected File out;

    @Override
    public File getFile() {
        return out;
    }

    // TODO move to domain validation
    protected void completeParams() {
        // if timestamp isn't set use current date
        if (!params.hasValue(FitFields.TIMESTAMP)) {
            params.setValue(FitFields.TIMESTAMP, new Date());
        }
        // if file name isn't set use timestamp for it
        if (out == null) {
            Date timestamp = params.getValue(FitFields.TIMESTAMP);
            out = UiUtils.createDefaultFile(timestamp);
        }
    }

    protected String getVersion() {
        String version = "<last version>";

        try {
            CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
            String path = codeSource.getLocation().toURI().getPath();

            if (path.endsWith(".jar") || path.endsWith(".exe")) {
                JarFile jarFile = new JarFile(path);
                Attributes attrs = jarFile.getManifest().getMainAttributes();
                version = attrs.getValue("Version");
            }
        } catch (Exception e) {
            LOG.log(Level.FINE, "an exception was thrown", e);
        }

        return version;
    }
}