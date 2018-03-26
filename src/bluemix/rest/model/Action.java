package bluemix.rest.model;

import java.util.ArrayList;
import java.util.List;

public class Action {

	
	private String namespace;
	private String name;
	private Exec exec;
	private String version;
	private List<Annotation> annotations = new ArrayList<>();
	private long updated;
	private Limits limits;
//	private boolean publish;
	
	private List<Activation> activations = new ArrayList<>();
	
//	public boolean isPublish() {
//		return publish;
//	}
//	public void setPublish(boolean publish) {
//		this.publish = publish;
//	}
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
	
	public List<Activation> getActivations() {
		return activations;
	}
	public void setActivations(List<Activation> activations) {
		this.activations = activations;
	}
	
	public String getExtension(){
		if(exec.getKind().contains("nodejs"))
			return ".js";
		else{
			return ".java";
		}
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	
	

}
