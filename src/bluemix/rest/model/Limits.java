package bluemix.rest.model;

public class Limits {
	private String timeout;
	private int memory;
	private int logs;
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	public int getMemory() {
		return memory;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	public int getLogs() {
		return logs;
	}
	public void setLogs(int logs) {
		this.logs = logs;
	}
	
}
