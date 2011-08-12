package com.nearinfinity.blur.manager.writer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nearinfinity.blur.index.WalIndexWriter;

public class BlurIndexCommiter {
    
    private static final Log LOG = LogFactory.getLog(BlurIndexCommiter.class);

    private Thread _commitDaemon;
    private Map<String,WalIndexWriter> _writers = new ConcurrentHashMap<String, WalIndexWriter>();
    private AtomicBoolean _running = new AtomicBoolean();
    private long _delay = TimeUnit.MINUTES.toMillis(5);
    
    public void init() {
        _running.set(true);
        _commitDaemon = new Thread(new Runnable() {
            @Override
            public void run() {
                while (_running.get()) {
                    commit();
                    try {
                        Thread.sleep(_delay);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });
        _commitDaemon.setDaemon(true);
        _commitDaemon.setName("Commit Thread");
        _commitDaemon.start();
    }

    public void commit() {
        for (String name : _writers.keySet()) {
            WalIndexWriter writer = _writers.get(name);
            if (writer != null) {
                commitIndex(name, writer);
            }
        }
    }

    private void commitIndex(String name, WalIndexWriter writer) {
//        synchronized (writer) {
            LOG.info("Commiting writer for [" + name + "]");
            try {
                writer.commitAndRollWal();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
//        }
    }

    public void addWriter(String name, WalIndexWriter writer) {
        _writers.put(name, writer);
    }

    public void close() {
        _running.set(false);
        _commitDaemon.interrupt();
        commit();
        try {
            _commitDaemon.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(String name) {
        WalIndexWriter writer = _writers.remove(name);
        commitIndex(name, writer);
    }

}
