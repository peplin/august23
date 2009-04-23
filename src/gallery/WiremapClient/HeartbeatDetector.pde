/**
 ** Heartbeat Detector (analog input peak detection)
 **
 ** by Christopher Peplin (chris.peplin@rhubarbtech.com)
 ** for August 23, 1966 (GROCS Project Group)
 ** University of Michigan, 2009
 **
 ** http://august231966.com
 ** http://www.dc.umich.edu/grocs
 **
 ** Copyright 2009 Christopher Peplin 
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at 
 ** http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 ** See the License for the specific language governing permissions and
 ** limitations under the License. 
 */

import processing.serial.*;

/**
 * The Heartbeat Detector class is a tool to read analog data in over a serial
 * connection (probably an Arduino), average the input and detect peaks. For the
 * August 23, 1966 installation, the peaks correspond to heart beats. <br>
 * <br>
 * 
 * From these peaks, a heart rate frequency can be determined and this is used
 * by the Wiremap Client to update the star oscillation simulation.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
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

        for (int i = 0; i < values.length; i++) {
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
                            if(mGraph[mCurrentIndex] / (float) AVERAGE_COUNT < mMinimumValue) {
                                mMinimumValue =
                                        mGraph[mCurrentIndex]
                                                / (float) AVERAGE_COUNT;
                            }
                            float valueAverage =
                                    mGraph[mCurrentIndex] / AVERAGE_COUNT;
                            if(mCurrentIndex >= 10
                                    && valueAverage < mGraph[(mCurrentIndex - 10)
                                            % width]
                                            / (float) AVERAGE_COUNT && !mUpbeat) {
                                if(!mUpbeat) {
                                    beat();
                                    mUpbeat = true;
                                }
                            } else if(mCurrentIndex >= 10
                                    && valueAverage > mGraph[(mCurrentIndex - 10)
                                            % width]
                                            / (float) AVERAGE_COUNT) {
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
        mCurrentRate = (mCurrentRate + ((now - mLastBeatTime) / 1000.0)) / 2.0;
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
