package org.weight2fit.application.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.spi.FileOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.OptionHandler;
import org.weight2fit.domain.FitException;
import org.weight2fit.domain.FitFields;
import org.weight2fit.domain.FitParams;
import org.weight2fit.domain.FitParamsSupplier;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import static org.weight2fit.domain.shared.Utils.checkNotNull;

/**
 * FIT parameters reader from command line.
 *
 * @author Andiry Kryvtsun
 */
public class CmdLineParamsSupplier implements FitParamsSupplier {

    private final String[] args;
    private final CmdLineParser parser;

    @Option(name = "-o", aliases = { "--out" }, usage = "Output FIT file", required = true, handler = FileOptionHandler.class)
    private File out;

    private final FitParams params = new FitParams();

    public CmdLineParamsSupplier(String... args) {
        this.args = checkNotNull(args);

        parser = new CmdLineParser(this);
        addOptions();
        sortOptions();
    }

    private void addOptions() {
        addOption(FitFields.TIMESTAMP, createTimestampOption());
        addOption(FitFields.WEIGHT, createWeightOption());
        addOption(FitFields.BODY_FAT, createBodyFatOption());
        addOption(FitFields.BODY_WATER, createBodyWaterOption());
        addOption(FitFields.VISCERAL_FAT, createVisceralFatOption());
        addOption(FitFields.MUSCLE_MASS, createMuscleMassOption());
        addOption(FitFields.PHYSIQUE_RATING, createPhysiqueRatingOption());
        addOption(FitFields.BONE_MASS, createBoneMassOption());
        addOption(FitFields.DCI, createDailyCalorieIntakeOption());
        addOption(FitFields.METABOLIC_AGE, createMetabolicAgeOption());
    }

    // put "out" option at the end of options list
    // to beautify usage example view
    private void sortOptions() {
        Collections.sort(parser.getOptions(), new Comparator<OptionHandler>() {
            @Override
            public int compare(OptionHandler o1, OptionHandler o2) {
                return o1 instanceof FileOptionHandler
                        ? 1
                        : o2 instanceof FileOptionHandler ? -1 : 0;
            }
        });
    }

    private Option createTimestampOption() {
        return CmdLineOption.Builder.create()
                .name("t").longName("timestamp")
                .required()
                .description("Timestamp of measurement")
                .metaVar(DateOptionHandler.DATE_PATTERN)
                .handler(DateOptionHandler.class)
                .build();
    }

    private Option createWeightOption() {
        return CmdLineOption.Builder.create()
                .name("w").longName("weight")
                .required()
                .description("Weight of the body in kg")
                .build();
    }

    private Option createBodyFatOption() {
        return CmdLineOption.Builder.create()
                .name("bf").longName("bodyFat")
                .description("Fat of the body in %")
                .build();
    }

    private Option createBodyWaterOption() {
        return CmdLineOption.Builder.create()
                .name("bw").longName("bodyWater")
                .description("Water of the body in %")
                .build();
    }

    private Option createVisceralFatOption() {
        return CmdLineOption.Builder.create()
                .name("vf").longName("visceralFat")
                .description("Visceral fat")
                .metaVar("index")
                .handler(IntOptionHandler.class)
                .build();
    }

    private Option createMuscleMassOption() {
        return CmdLineOption.Builder.create()
                .name("mm").longName("muscleMass")
                .description("Muscle mass of the body in kg")
                .build();
    }

    private Option createPhysiqueRatingOption() {
        return CmdLineOption.Builder.create()
                .name("pr").longName("physiqueRating")
                .description("Physique rating")
                .metaVar("index")
                .handler(IntOptionHandler.class)
                .build();
    }

    private Option createBoneMassOption() {
        return CmdLineOption.Builder.create()
                .name("bm").longName("boneMass")
                .description("Bone mass of the body in kg")
                .build();
    }

    private Option createDailyCalorieIntakeOption() {
        return CmdLineOption.Builder.create()
                .name("dci").longName("dailyCalorieIntake")
                .description("Daily calorie intake in kcal/day")
                .handler(IntOptionHandler.class)
                .build();
    }

    private Option createMetabolicAgeOption() {
        return CmdLineOption.Builder.create()
                .name("ma").longName("metabolicAge")
                .description("Metabolic age in years")
                .handler(IntOptionHandler.class)
                .build();
    }

    private void addOption(FitFields field, Option option) {
        FitFieldSetter setter = new FitFieldSetter(params, field);
        parser.addOption(setter, option);
    }

    @Override
    public FitParams get() throws FitException {
        try {
            parser.parseArgument(args);
            return params;
        }
        catch (CmdLineException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
            System.out.println("Usage: weight2fit" + parser.printExample(OptionHandlerFilter.REQUIRED));
            parser.printUsage(System.out);

            throw new FitException("Error during FitParams creation", e);
        }
    }

    public File getFile() {
        return out;
    }
}
