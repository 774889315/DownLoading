package com.example.downloading.db;

import java.util.List;

import com.example.downloading.entitis.ThreadInfo;

public interface ThreadDAO {

 void insertThread(ThreadInfo info);

	public void deleteThread(String url);

	public void updateThread(String url, int thread_id, int finished);

	public List<ThreadInfo> queryThreads(String url);

	public boolean isExists(String url, int threadId);
}
