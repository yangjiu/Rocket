package cn.hikyson.rocket.task;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by kysonchao on 2017/12/28.
 */
@RunWith(RobolectricTestRunner.class)
public class TaskScheduerTest {
    @Test
    public void schedule1() throws Exception {
        final Executor executor = Executors.newCachedThreadPool();
        final CountDownLatch downLatch = new CountDownLatch(2);
        final long[] taskStartTime = {0, 0};
        final long[] taskEndTime = {0, 0};
        LaunchTask task0 = TaskFactory.create("task0", 1000, new ArrayList<String>(), executor, new TestTaskCallback() {
            @Override
            public void taskStart() {
                taskStartTime[0] = System.nanoTime();
            }

            @Override
            public void taskEnd() {
                taskEndTime[0] = System.nanoTime();
                downLatch.countDown();
            }
        });
        LaunchTask task1 = TaskFactory.create("task1", 1000, Collections.singletonList("task0"), executor, new TestTaskCallback() {
            @Override
            public void taskStart() {
                taskStartTime[1] = System.nanoTime();
            }

            @Override
            public void taskEnd() {
                taskEndTime[1] = System.nanoTime();
                downLatch.countDown();
            }
        });
        new TaskScheduer(Arrays.<LaunchTask>asList(task0, task1)).schedule(null, 0, null);
        downLatch.await();
        //1 依赖 0
        Assert.assertTrue(taskStartTime[1] > taskEndTime[0]);
    }


    @Test
    public void schedule2() throws Exception {
        final Executor executor = Executors.newCachedThreadPool();
        final CountDownLatch downLatch = new CountDownLatch(3);
        final long[] taskStartTime = {0, 0, 0};
        final long[] taskEndTime = {0, 0, 0};
        LaunchTask task0 = TaskFactory.create("task0", 1000, new ArrayList<String>(), executor, new TestTaskCallback() {
            @Override
            public void taskStart() {
                taskStartTime[0] = System.nanoTime();
            }

            @Override
            public void taskEnd() {
                taskEndTime[0] = System.nanoTime();
                downLatch.countDown();
            }
        });
        LaunchTask task1 = TaskFactory.create("task1", 1000, Collections.singletonList("task0"), executor, new TestTaskCallback() {
            @Override
            public void taskStart() {
                taskStartTime[1] = System.nanoTime();
            }

            @Override
            public void taskEnd() {
                taskEndTime[1] = System.nanoTime();
                downLatch.countDown();
            }
        });
        LaunchTask task2 = TaskFactory.create("task2", 1000, Collections.singletonList("task0"), executor, new TestTaskCallback() {
            @Override
            public void taskStart() {
                taskStartTime[2] = System.nanoTime();
            }

            @Override
            public void taskEnd() {
                taskEndTime[2] = System.nanoTime();
                downLatch.countDown();
            }
        });
        new TaskScheduer(Arrays.asList(task0, task1, task2)).schedule(null, 0, null);
        downLatch.await();
        //1 依赖 0 ,2 依赖 0
        Assert.assertTrue(taskEndTime[0] < taskStartTime[1]);
        Assert.assertTrue(taskEndTime[0] < taskStartTime[2]);
    }

    @Test
    public void schedule3() throws Exception {
        final Executor executor = Executors.newCachedThreadPool();
        final CountDownLatch downLatch = new CountDownLatch(3);
        final long[] taskStartTime = {0, 0, 0};
        final long[] taskEndTime = {0, 0, 0};
        LaunchTask task0 = TaskFactory.create("task0", 1000, new ArrayList<String>(), executor, new TestTaskCallback() {
            @Override
            public void taskStart() {
                taskStartTime[0] = System.nanoTime();
            }

            @Override
            public void taskEnd() {
                taskEndTime[0] = System.nanoTime();
                downLatch.countDown();
            }
        });
        LaunchTask task1 = TaskFactory.create("task1", 1000, Collections.singletonList("task0"), executor, new TestTaskCallback() {
            @Override
            public void taskStart() {
                taskStartTime[1] = System.nanoTime();
            }

            @Override
            public void taskEnd() {
                taskEndTime[1] = System.nanoTime();
                downLatch.countDown();
            }
        });
        LaunchTask task2 = TaskFactory.create("task2", 1000, Arrays.asList("task0", "task1"), executor, new TestTaskCallback() {
            @Override
            public void taskStart() {
                taskStartTime[2] = System.nanoTime();
            }

            @Override
            public void taskEnd() {
                taskEndTime[2] = System.nanoTime();
                downLatch.countDown();
            }
        });
        new TaskScheduer(Arrays.asList(task2, task1, task0)).schedule(null, 0, null);
        downLatch.await();
        //1 依赖 0 ,2 依赖 1 ,2 依赖0
        Assert.assertTrue(taskEndTime[0] < taskStartTime[1]);
        Assert.assertTrue(taskEndTime[1] < taskStartTime[2]);
    }


    @Test
    public void schedule4() throws Exception {
        final Executor executor = Executors.newCachedThreadPool();
        LaunchTask task0 = TaskFactory.create("task0", 1000, Collections.singletonList("task2"), executor, new TestTaskCallback() {
            @Override
            public void taskStart() {
            }

            @Override
            public void taskEnd() {
            }
        });
        LaunchTask task1 = TaskFactory.create("task1", 1000, Collections.singletonList("task0"), executor, new TestTaskCallback() {
            @Override
            public void taskStart() {
            }

            @Override
            public void taskEnd() {
            }
        });
        LaunchTask task2 = TaskFactory.create("task2", 1000, Collections.singletonList("task1"), executor, new TestTaskCallback() {
            @Override
            public void taskStart() {
            }

            @Override
            public void taskEnd() {
            }
        });
        try {
            new TaskScheduer(Arrays.asList(task2, task1, task0)).schedule(null, 0, null);
        } catch (Throwable throwable) {
            Assert.assertTrue(throwable instanceof IllegalStateException);
            Assert.assertTrue(throwable.getLocalizedMessage().equals("Exists a cycle in the graph"));
            return;
        }
        Assert.assertTrue(false);
    }


    @Test
    public void schedule5() throws Exception {

    }

}