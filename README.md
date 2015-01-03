thread_priority_setting_on_android
==================================
Study two types of thread priority setting method on android: Thread.setPriority(), android.os.Process.setThreadPriority().

It is clearly shown that the later one is more effective to control the priority, as the performance of the thread with the highest priority can be 450 times better than with lowest priority. While with the Java style priority setting, just less than 6 times difference between the highest and the lowest.