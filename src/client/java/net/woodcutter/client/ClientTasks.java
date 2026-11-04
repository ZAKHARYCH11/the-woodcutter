package net.woodcutter.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public final class ClientTasks {
    private static final List<BooleanSupplier> TASKS = new ArrayList<>();

    private ClientTasks() {}

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> TASKS.removeIf(BooleanSupplier::getAsBoolean));
    }

    public static void add(BooleanSupplier task) {
        TASKS.add(task);
    }
}
