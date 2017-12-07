package com.github.taniqng.eventbus.api;

import java.io.Serializable;

public abstract class DisEvent<T> implements Serializable {

	private static final long serialVersionUID = 8866021050288260207L;
	
	/**
	 * 事件编码
	 */
	private String eventCode;
	
	/**
	 * 事件流水
	 */
	private String eventFlow;
	
	/**
	 * 源IP
	 */
	private String sourceIp;
	
	/**
	 * 源appId
	 */
	private String appId;
	
	/**
	 * event数据
	 */
	private T data;

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getEventFlow() {
		return eventFlow;
	}

	public void setEventFlow(String eventFlow) {
		this.eventFlow = eventFlow;
	}
}
