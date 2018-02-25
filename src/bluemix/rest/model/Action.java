package bluemix.rest.model;

import java.util.List;

public class Action {

	
	private String namespace;
	private String name;
	private Exec exec;
	private String version;
	private List<Annotation> annotations;
	private long updated;
	private Limits limits;
	private boolean publish;
	
	public boolean isPublish() {
		return publish;
	}
	public void setPublish(boolean publish) {
		this.publish = publish;
	}
	public Limits getLimits() {
		return limits;
	}
	public void setLimits(Limits limits) {
		this.limits = limits;
	}
	public long getUpdated() {
		return updated;
	}
	public void setUpdated(long updated) {
		this.updated = updated;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Annotation> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}
	public Exec getExec() {
		return exec;
	}
	public void setExec(Exec exec) {
		this.exec = exec;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	

}
