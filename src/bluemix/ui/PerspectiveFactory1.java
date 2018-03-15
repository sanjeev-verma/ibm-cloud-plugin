package bluemix.ui;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.internal.registry.StickyViewDescriptor;

public class PerspectiveFactory1 implements IPerspectiveFactory {

    public void createInitialLayout(IPageLayout layout) {
        layout.createFolder("left", IPageLayout.LEFT, 0.2f, IPageLayout.ID_EDITOR_AREA);
        layout.createFolder("right", IPageLayout.RIGHT, 0.8f, IPageLayout.ID_EDITOR_AREA);
        layout.createFolder("bottom", IPageLayout.BOTTOM, 0.8f, IPageLayout.ID_EDITOR_AREA);
        layout.createFolder("xyz", IPageLayout.BOTTOM, 0.8f, IPageLayout.ID_PROJECT_EXPLORER);;
//        layout.createFolder("top", IPageLayout.TOP, 0.6f, IPageLayout.ID_EDITOR_AREA);;
    }

}