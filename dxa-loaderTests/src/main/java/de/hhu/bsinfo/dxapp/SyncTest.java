package de.hhu.bsinfo.dxapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import de.hhu.bsinfo.dxram.app.Application;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import de.hhu.bsinfo.dxram.loader.LoaderService;
import de.hhu.bsinfo.dxutils.NodeID;

public class SyncTest extends Application {
    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "SyncTest";
    }

    @Override
    public void main(String[] p_args) {
        LoaderService loaderService = getService(LoaderService.class);
        BootService bootService = getService(BootService.class);

        List<Short> superPeers = bootService.getOnlineSuperpeerNodeIDs();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");

        loaderService.addJar(Paths.get("dxrest-1.jar"));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(String.format("syncTest-%s.txt",
                    format.format(new Date()))));

            for (int x = 0; 1000 > x; x++) {
                for (Short superPeer : superPeers) {
                    loaderService.flushSuperpeerTable(superPeer);
                }
                TimeUnit.MILLISECONDS.sleep(20);
                loaderService.addJar(Paths.get("dxrest-1.jar"));
                TimeUnit.MILLISECONDS.sleep(20);

                for (Short superPeer : superPeers) {
                    if (0.5 <= Math.random()) {
                        loaderService.flushSuperpeerTable(superPeer);
                    }
                }
                TimeUnit.MILLISECONDS.sleep(20);

                int i = -1;
                try {
                    loaderService.findClass("de.hhu.bsinfo.dxapp.rest.cmd.requests.AppRunRequest");
                    loaderService.cleanLoader();
                    i = 1;
                } catch (ClassNotFoundException e) {
                    i = 0;
                }

                writer.write(i + System.lineSeparator());
                writer.flush();
            }
            writer.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void signalShutdown() {

    }
}
