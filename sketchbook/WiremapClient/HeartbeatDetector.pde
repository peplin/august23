import processing.serial.*;

public class HeartbeatDetector {
    private final int AVERAGE_COUNT = 10;
    private final float BIG_AVERAGE_COUNT = 100;
    private float mGraph[];
    private int mCurrentIndex = 0;
    private int mCurrentAverageIndex = 0;
    private int mLastLevel = 1023;
    private int mLastLevelCount = 0;
    private float mBigAverage = 0;
    private float mBigAverageIndex = 0;
    private float mMinimumValue = 1023;
    private int mLastAverage = 0;
    private boolean mUpbeat = false;
    private float mCurrentRate;
    private float mLastBeatTime;
    private Serial mPort;
    private PApplet mParent;

    public HeartbeatDetector(PApplet parent) {
        mParent = parent;
        mParent.registerDraw(this);
        mGraph = new float[width];
        mPort = new Serial(mParent, Serial.list()[0], 9600); 
    }

    public void draw() {
        String value = "";
        if(mPort.available() > 0) {
            value = trim(mPort.readString());
        }
        String[] values = value.split(" ");

        for(int i = 0; i < values.length; i++) {
            if(values[i] != null && values[i] != "") {
                try {
                    int y = Integer.parseInt(values[i]);
                    if(y > 100) { 
                        // serial in is often split, creating weird outliers
                        mGraph[mCurrentIndex] += y;
                        mCurrentAverageIndex++;
                        mBigAverage += y;
                        mBigAverageIndex++;
                        if(mCurrentAverageIndex == AVERAGE_COUNT) {
                            if(mGraph[mCurrentIndex] / (float)AVERAGE_COUNT < mMinimumValue) {
                                mMinimumValue = mGraph[mCurrentIndex] / (float)AVERAGE_COUNT;
                            }
                            float valueAverage = mGraph[mCurrentIndex] / AVERAGE_COUNT;
                            if(mCurrentIndex >= 10 && valueAverage < mGraph[(mCurrentIndex - 10) % width] 
                                    / (float)AVERAGE_COUNT && !mUpbeat) {
                                if(!mUpbeat) {
                                    beat();
                                    mUpbeat = true;
                                }
                            } else if(mCurrentIndex >= 10 && valueAverage
                                    > mGraph[(mCurrentIndex - 10) % width]
                                    / (float)AVERAGE_COUNT) {
                                mUpbeat = false; 
                            }
                            mCurrentIndex = (mCurrentIndex + 1) % (width - 1);
                            mGraph[mCurrentIndex] = 0;
                            mCurrentAverageIndex = 0;
                        }

                        if(mBigAverageIndex == BIG_AVERAGE_COUNT) {
                            mBigAverage = 0;
                            mBigAverageIndex = 0;
                        }

                    }
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private void beat() {
        float now = millis();
        mCurrentRate = (mCurrentRate + 
                ((now - mLastBeatTime) / 1000.0)) / 2.0;
        mLastBeatTime = now;
    }

    public float getCurrentRate() {
        return mCurrentRate;
    }

    public void resetAverages() {
        mBigAverage = 0;
        mBigAverageIndex = 0;
    }
}
