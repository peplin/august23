public class StarLink { 
    private Star mFirst;
    private Star mSecond;

    public StarLink(Star first, Star second) {
        mFirst = first;
        mSecond = second;
    }

    public StarLink(Star first) {
        mFirst = first;
        mSecond = null;
    }

    public Star getFirst() {
        return mFirst;
    }

    public Star getSecond() {
        return mSecond;
    }

    public void setFirst(Star first) {
        mFirst = first;
    }

    public void setSecond(Star second) {
        mSecond = second;
    }
}
