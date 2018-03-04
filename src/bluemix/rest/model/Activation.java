package bluemix.rest.model;

public class Activation {

	 private transient Action action;
	 private int statusCode;
     private long duration;
     private String activationId;
     private long end;
     private long start;
     private String[] logs;
     private Response response;
    
     public Response getResponse() {
		return response;
	}
     public void setResponse(Response response) {
		this.response = response;
	}
     
     public Action getAction() {
 		return action;
 	}
 	public void setAction(Action action) {
 		this.action = action;
 	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getActivationId() {
		return activationId;
	}
	public void setActivationId(String activationId) {
		this.activationId = activationId;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public String[] getLogs() {
		return logs;
	}
	public void setLogs(String[] logs) {
		this.logs = logs;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((activationId == null) ? 0 : activationId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Activation other = (Activation) obj;
		if (activationId == null) {
			if (other.activationId != null)
				return false;
		} else if (!activationId.equals(other.activationId))
			return false;
		return true;
	}
	
	
	
}
