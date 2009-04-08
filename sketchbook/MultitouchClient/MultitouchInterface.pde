

public class MultitouchInterface {
    private RectButton mZoomInButton;
    private RectButton mZoomOutButton;
    private RectButton mCreateButton;
    private RectButton mConnectButton;
    private PApplet mParent;

    public MultitouchInterface(PApplet parent) {
        mParent = parent;
        mParent.registerDraw(this);
        mParent.registerMouseEvent(this);

    }

}
