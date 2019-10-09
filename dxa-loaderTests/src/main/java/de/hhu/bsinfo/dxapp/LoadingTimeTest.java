package de.hhu.bsinfo.dxapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hhu.bsinfo.dxram.app.Application;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import de.hhu.bsinfo.dxram.loader.LoaderService;
import de.hhu.bsinfo.dxutils.NodeID;

public class LoadingTimeTest extends Application {
    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "LoadingTimeTest";
    }

    @Override
    public void main(final String[] p_args) {
        LoaderService loaderService = getService(LoaderService.class);
        loaderService.addJar(Paths.get("dxrest-1.jar"));

        long[] data = new long[5000];

        try {
            for (int i = 0; i < data.length; i++) {
                loaderService.cleanLoader();

                long start = System.nanoTime();
                loaderService.findClass("de.hhu.bsinfo.dxapp.rest.cmd.requests.AppRunRequest");
                long stop = System.nanoTime();

                data[i] = stop - start;
            }

            BootService bootService = getService(BootService.class);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            BufferedWriter writer = new BufferedWriter(new FileWriter(String.format("loadingTimes-%s-%s.txt",
                    format.format(new Date()), NodeID.toHexString(bootService.getNodeID()))));
            for (long da : data) {
                writer.write(da + System.lineSeparator());
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void signalShutdown() {

    }
}
