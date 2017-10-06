package com.example.downloading.entitis;

import java.io.Serializable;

/**
 * Created by Unreal Lover on 2017/10/2.
 */

public class FileInfo implements Serializable {
	private int id;
	private String url;
	private String fileName;
	private long length;
	private long finished;
	private String util;
	private long Realong;
	private double rate;
	private String util2;

	public FileInfo() {
		super();
	}


	public FileInfo(int id, String url, String fileName, long length, long finished) {
		super();
		this.id = id;
		this.url = url;
		this.fileName = fileName;
		this.length = length;
		this.finished = finished;
	}
	//存取fileinfo的id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	//存取fileinfo的url
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	//存取file的文件名
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	//存储文件长度
	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}
	//存取完成情况
	public long getFinished() {
		return finished;
	}

	public void setFinished(long finished) {
		this.finished = finished;
	}
	//存取大小单位
	public void setUtil(String util){
		this.util = util;
	}

	public String getUtil(){
		return util;
	}
	//存取实际进度（换算后）
	public void setRealong(long Realong){
		this.Realong = Realong;
	}

	public long getRealong(){
		return Realong;
	}
	//存取下载速度
	public void setRate(double rate){
		this.rate = rate;
	}

	public double getRate(){
		return rate;
	}
	//存取下载速度的大小单位
	public void setUtil2(String util2){
		this.util2 = util2;
	}

	public String getUtil2(){
		return util2;
	}

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", url=" + url + ", fileName=" + fileName + ", length=" + length + ", finished="
				+ finished + "]";
	}

}
