package bluemix.rest.model;

import org.apache.commons.lang3.ObjectUtils;

public class Response {
	private Result result;
	private boolean success;
	private String status;
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return ObjectUtils.toString(result);
	}
	
	
}
