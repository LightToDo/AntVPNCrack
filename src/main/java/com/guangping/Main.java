package com.guangping;

import com.guangping.android.AndroidConstructor;
import com.guangping.encrypt.DigestAes;
import com.guangping.invite.InviteTask;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.*;

import static com.guangping.android.AndroidConstructor.generateOauthId;

/**
 * @author GuangPing Lin
 * @date 2021/2/21 15:40
 */
public class Main {
    private static final Instant ACTIVITY = Instant.parse("2021-02-28T00:00:00.00Z");

    private static ExecutorService executorService;

    static {
        int i = Runtime.getRuntime().availableProcessors();
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();
        executorService = new ThreadPoolExecutor(i, 2 * i, 5, TimeUnit.MINUTES,
                blockingQueue);
    }

    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        while (Instant.now().isBefore(ACTIVITY)) {
            if (i > 0 && i % 11 == 0) {
                TimeUnit.MILLISECONDS.sleep(InviteTask.COUNT * 4200 * 10);
            }

            String oauthId = generateOauthId();

            InviteTask inviteTask = new InviteTask(oauthId);

            executorService.execute(inviteTask);
            i++;
        }
    }
}


