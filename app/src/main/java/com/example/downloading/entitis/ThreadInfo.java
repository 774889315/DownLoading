package com.example.downloading.entitis;

/**
 * Created by Unreal Lover on 2017/10/2.
 */

public class ThreadInfo {
	private int id;
	private String url;
	private long start;
	private long end;
	private int finished;

	public ThreadInfo() {
		super();
	}


	public ThreadInfo(int id, String url, long start, long end, int finished) {
		super();
		this.id = id;
		this.url = url;
		this.start = start;
		this.end = end;
		this.finished = finished;
	}
	//存取线程id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	//存取下载地址
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	//存取线程开始下载的位置
	public long getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}
	//存取线程下载结束的位置
	public long getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	//存取线程下载进度
	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		return "ThreadInfo [id=" + id + ", url=" + url + ", start=" + start + ", end=" + end + ", finished=" + finished
				+ "]";
	}

}
