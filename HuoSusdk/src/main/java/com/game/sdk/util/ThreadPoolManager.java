package com.game.sdk.util;

import android.annotation.SuppressLint;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ling
 * @date 2016/6/7
 */
@SuppressLint("NewApi")
public class ThreadPoolManager {

	private static ThreadPoolManager tpm;
	private ThreadPoolExecutor service;

	private ThreadPoolManager() {
		// 返回java虚拟机可用处理器的数目
		int num = Runtime.getRuntime().availableProcessors();
//		service = Executors.newFixedThreadPool(num * 2);
		service = new ThreadPoolExecutor(1, 4, 2, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
	}

	public static ThreadPoolManager getInstance() {
		if (null == tpm) {
			tpm = new ThreadPoolManager();
		}
		return tpm;
	}

	public void addTask(Runnable task) {
		service.submit(task);
	}
}
