package bluemix.rest;

public class UpdateAction extends CreateAction {

	
	/**
	 * Constructor for Action1.
	 */
	public UpdateAction() {
		super();
	}

	@Override
	protected String isOverwrite() {
		return "true";
	}
}

