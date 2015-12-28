package com.google.zxing.client.android.result;


public class Result<T> {

	String message;
	T data;

	public Result() {
		super();
	}

	public Result(String message) {
		this.message = message;
	}

	public Result(T data) {
		this.message = "SUCCESS";
		this.data = data;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
