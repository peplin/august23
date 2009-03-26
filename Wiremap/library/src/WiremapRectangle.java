//package wiremap;

import processing.core.*;

public class WiremapRectangle extends WiremapPositionedShape {
    protected int mWidth;
    protected int mHeight;
    protected int mDepth;
    protected int mBorderHeight;
    protected color mBorderColor;

    private int[][] mCornerPositions = new int[8][3];

    /**
     * z >= 0, z <= mDepthThickness
     */
    public WiremapRectangle(Wiremap map, int x, int y, int z, color baseColor,
            int width, int height, int depth, int borderHeight,
            color borderColor) {
        super(map, x, y, z, baseColor);
        setSize(width, height, depth);
        setBorderHeight(borderHeight);
        setBorderColor(borderColor);
        initializeCorners();
    }

    private void initializeCorners() {
        mCornerPositions[0][0] = -mWidth + mX;
        mCornerPositions[0][1] = -mHeight + mY;
        mCornerPositions[0][2] = -mDepth + mZ;

        mCornerPositions[1][0] = -mWidth + mX;
        mCornerPositions[1][1] = -mHeight + mY;
        mCornerPositions[1][2] = mDepth + mZ;

        mCornerPositions[2][0] = -mWidth + mX;
        mCornerPositions[2][1] = mHeight + mY;
        mCornerPositions[2][2] = -mDepth + mZ;

        mCornerPositions[3][0] = -mWidth + mX;
        mCornerPositions[3][1] = mHeight + mY;
        mCornerPositions[3][2] = mDepth + mZ;

        mCornerPositions[4][0] = mWidth + mX;
        mCornerPositions[4][1] = -mHeight + mY;
        mCornerPositions[4][2] = -mDepth + mZ;

        mCornerPositions[5][0] = mWidth + mX;
        mCornerPositions[5][1] = -mHeight + mY;
        mCornerPositions[5][2] = mDepth + mZ;

        mCornerPositions[6][0] = mWidth + mX;
        mCornerPositions[6][1] = mHeight + mY;
        mCornerPositions[6][2] = -mDepth + mZ;

        mCornerPositions[7][0] = mWidth + mX;
        mCornerPositions[7][1] = mHeight + mY;
        mCornerPositions[7][2] = mDepth + mZ;
    }

    private int findBottomCorner() {
        for (int i = 0; i < 8; i++) {
            int counter = 0;
            for(int j = 0; j < 8; j++) {
                if(mCornerPositions[i][1] > mCornerPositions[j][1]) {
                    break;
                } else {
                    counter++;
                }
            }
            if (counter == 8) {
                return i;
            }
        }
        return 0;
    }

    private void drawWire(int wire) {
        boolean gt1 = false;
        boolean gt3 = false;
        boolean gt5 = false;
        float bottomY = 0.0, topY = 0.0;
        int[] clock = new int[8];
        switch(findBottomCorner()) {
        case 0:
            clock[0] = 0;
            clock[1] = 1;
            clock[2] = 3;
            clock[3] = 2;
            clock[4] = 6;
            clock[5] = 4;
            clock[6] = 5;
            clock[7] = 7;
            break;
        case 1:
            clock[0] = 1;
            clock[1] = 0;
            clock[2] = 4;
            clock[3] = 5;
            clock[4] = 7;
            clock[5] = 3;
            clock[6] = 2;
            clock[7] = 6;
            break;
        case 2:
            clock[0] = 2;
            clock[1] = 0;
            clock[2] = 1;
            clock[3] = 3;
            clock[4] = 7;
            clock[5] = 6;
            clock[6] = 4;
            clock[7] = 5;
            break;
        case 3:
            clock[0] = 3;
            clock[1] = 1;
            clock[2] = 5;
            clock[3] = 7;
            clock[4] = 6;
            clock[5] = 2;
            clock[6] = 0;
            clock[7] = 4;
            break;
        case 4:
            clock[0] = 4;
            clock[1] = 0;
            clock[2] = 2;
            clock[3] = 6;
            clock[4] = 7;
            clock[5] = 5;
            clock[6] = 1;
            clock[7] = 3;
            break;
        case 5:
            clock[0] = 5;
            clock[1] = 1;
            clock[2] = 0;
            clock[3] = 4;
            clock[4] = 6;
            clock[5] = 7;
            clock[6] = 3;
            clock[7] = 2;
            break;
        case 6:
            clock[0] = 6;
            clock[1] = 2;
            clock[2] = 3;
            clock[3] = 7;
            clock[4] = 5;
            clock[5] = 4;
            clock[6] = 0;
            clock[7] = 1;
            break;
        case 7:
            clock[0] = 7;
            clock[1] = 3;
            clock[2] = 1;
            clock[3] = 5;
            clock[4] = 4;
            clock[5] = 6;
            clock[6] = 2;
            clock[7] = 0;
            break;
        }

        if (mCornerPositions[clock[0]][2] > mCornerPositions[clock[1]][2]) {
            gt1 = true;
        }
        if (mCornerPositions[clock[0]][2] > mCornerPositions[clock[3]][2]) {
            gt3 = true;
        }
        if (mCornerPositions[clock[0]][2] > mCornerPositions[clock[5]][2]) {
            gt5 = true;
        }

        if (gt1 && gt3) {
            if(lessThanX(wire, clock[2], clock[3], clock[4], clock[5]) && greaterThanX(wire, clock[5], clock[6], clock[1], clock[2])) {
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[3]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[3]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[3]][2]) + mCornerPositions[clock[3]][0]) &&
                    (mMap.getWireX(wire) > ((mCornerPositions[clock[5]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[5]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                        bottomY = findTopY(wire, clock[0], clock[3], clock[5]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[5]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[5]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[5]][2]) + mCornerPositions[clock[5]][0]) &&
                    (mMap.getWireX(wire) < ((mCornerPositions[clock[1]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[1]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                        bottomY = findTopY(wire, clock[0], clock[1], clock[5]);
                } else {
                    bottomY = findTopY(wire, clock[0], clock[1], clock[3]);
                }
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[4]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[4]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[4]][2]) + mCornerPositions[clock[4]][0]) &&
                    (mMap.getWireX(wire) > ((mCornerPositions[clock[2]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[2]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                        topY = findTopY(wire, clock[7], clock[2], clock[4]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[6]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[6]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[6]][2]) + mCornerPositions[clock[6]][0]) &&
                    (mMap.getWireX(wire) < ((mCornerPositions[clock[2]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[2]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                        topY = findTopY(wire, clock[7], clock[2], clock[6]);
                } else {
                    topY = findTopY(wire, clock[7], clock[4], clock[6]);
                }
            }
        } else if (gt1 && gt5) {
            if(lessThanX(wire, clock[6], clock[1], clock[2], clock[3]) && greaterThanX(wire, clock[3], clock[4], clock[5], clock[6])) {
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[3]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[3]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[3]][2]) + mCornerPositions[clock[3]][0]) &&
                (mMap.getWireX(wire) > ((mCornerPositions[clock[1]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[1]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                    bottomY = findTopY(wire, clock[0], clock[1], clock[3]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[5]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[5]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[5]][2]) + mCornerPositions[clock[5]][0]) &&
                (mMap.getWireX(wire) < ((mCornerPositions[clock[3]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[3]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                    bottomY = findTopY(wire, clock[0], clock[3], clock[5]);
                } else {
                    bottomY = findTopY(wire, clock[0], clock[1], clock[5]);
                }
                
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[6]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[6]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[6]][2]) + mCornerPositions[clock[6]][0]) &&
                (mMap.getWireX(wire) > ((mCornerPositions[clock[2]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[2]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                    topY = findTopY(wire, clock[7], clock[2], clock[6]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[6]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[6]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[6]][2]) + mCornerPositions[clock[6]][0]) &&
                (mMap.getWireX(wire) < ((mCornerPositions[clock[4]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[4]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                    topY = findTopY(wire, clock[7], clock[4], clock[6]);
                } else {
                    topY = findTopY(wire, clock[7], clock[2], clock[4]);
                }
            }
        } else if (gt3 && gt5) {
            if(lessThanX(wire, clock[4], clock[5], clock[6], clock[1]) && greaterThanX(wire, clock[1], clock[2], clock[3], clock[4])) {
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[5]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[5]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[5]][2]) + mCornerPositions[clock[5]][0]) &&
                (mMap.getWireX(wire) > ((mCornerPositions[clock[1]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[1]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                    bottomY = findTopY(wire, clock[0], clock[1], clock[5]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[1]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[1]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[1]][2]) + mCornerPositions[clock[1]][0]) &&
                (mMap.getWireX(wire) < ((mCornerPositions[clock[3]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[3]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                    bottomY = findTopY(wire, clock[0], clock[1], clock[3]);
                } else {
                    bottomY = findTopY(wire, clock[0], clock[3], clock[5]);
                }
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[4]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[4]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[4]][2]) + mCornerPositions[clock[4]][0]) &&
                (mMap.getWireX(wire) > ((mCornerPositions[clock[6]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[6]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                    topY = findTopY(wire, clock[7], clock[4], clock[6]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[4]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[4]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[4]][2]) + mCornerPositions[clock[4]][0]) &&
                (mMap.getWireX(wire) < ((mCornerPositions[clock[2]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[2]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                    topY = findTopY(wire, clock[7], clock[2], clock[4]);
                } else {
                    topY = findTopY(wire, clock[7], clock[2], clock[6]);
                }
            }
        } else if (!gt1 && !gt3) {
            if(lessThanX(wire, clock[5], clock[6], clock[1], clock[2]) && greaterThanX(wire, clock[2], clock[3], clock[4], clock[5])) {
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[5]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[5]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[5]][2]) + mCornerPositions[clock[5]][0]) &&
                    (mMap.getWireX(wire) > ((mCornerPositions[clock[1]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[1]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                        bottomY = findTopY(wire, clock[0], clock[1], clock[5]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[5]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[5]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[5]][2]) + mCornerPositions[clock[5]][0]) &&
                    (mMap.getWireX(wire) < ((mCornerPositions[clock[3]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[3]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                        bottomY = findTopY(wire, clock[0], clock[3], clock[5]);
                } else {
                    bottomY = findTopY(wire, clock[0], clock[1], clock[3]);
                }
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[6]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[6]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[6]][2]) + mCornerPositions[clock[6]][0]) &&
                    (mMap.getWireX(wire) > ((mCornerPositions[clock[2]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[2]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                        topY = findTopY(wire, clock[7], clock[2], clock[6]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[2]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[2]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[2]][2]) + mCornerPositions[clock[2]][0]) &&
                    (mMap.getWireX(wire) < ((mCornerPositions[clock[4]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[4]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                        topY = findTopY(wire, clock[7], clock[2], clock[4]);
                } else {
                    topY = findTopY(wire, clock[7], clock[4], clock[6]);
                }  
            }
        } else if (!gt1 && !gt5) {
            if(lessThanX(wire, clock[3], clock[4], clock[5], clock[6]) && greaterThanX(wire, clock[6], clock[1], clock[2], clock[3])) {
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[3]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[3]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[3]][2]) + mCornerPositions[clock[3]][0]) &&
                    (mMap.getWireX(wire) > ((mCornerPositions[clock[5]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[5]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                        bottomY = findTopY(wire, clock[0], clock[3], clock[5]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[1]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[1]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[1]][2]) + mCornerPositions[clock[1]][0]) &&
                    (mMap.getWireX(wire) < ((mCornerPositions[clock[3]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[3]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                        bottomY = findTopY(wire, clock[0], clock[1], clock[3]);
                } else {
                    bottomY = findTopY(wire, clock[0], clock[1], clock[5]);
                }
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[4]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[4]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[4]][2]) + mCornerPositions[clock[4]][0]) &&
                    (mMap.getWireX(wire) > ((mCornerPositions[clock[6]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[6]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                        topY = findTopY(wire, clock[7], clock[4], clock[6]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[6]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[6]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[6]][2]) + mCornerPositions[clock[6]][0]) &&
                    (mMap.getWireX(wire) < ((mCornerPositions[clock[2]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[2]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                        topY = findTopY(wire, clock[7], clock[2], clock[6]);
                } else {
                    topY = findTopY(wire, clock[7], clock[2], clock[4]);
                }
            }
        } else if (!gt3 && !gt5) {
            if(lessThanX(wire, clock[1], clock[2], clock[3], clock[4]) && greaterThanX(wire, clock[4], clock[5], clock[6], clock[1])) {
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[3]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[3]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[3]][2]) + mCornerPositions[clock[3]][0]) &&
                    (mMap.getWireX(wire) > ((mCornerPositions[clock[1]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[1]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                        bottomY = findTopY(wire, clock[0], clock[1], clock[3]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[0]][0] - mCornerPositions[clock[5]][0]) / (mCornerPositions[clock[0]][2] - mCornerPositions[clock[5]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[5]][2]) + mCornerPositions[clock[5]][0]) &&
                    (mMap.getWireX(wire) < ((mCornerPositions[clock[1]][0] - mCornerPositions[clock[0]][0]) / (mCornerPositions[clock[1]][2] - mCornerPositions[clock[0]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[0]][2]) + mCornerPositions[clock[0]][0])) {
                        bottomY = findTopY(wire, clock[0], clock[1], clock[5]);
                } else {
                    bottomY = findTopY(wire, clock[0], clock[3], clock[5]);
                }
                
                if ((mMap.getWireX(wire) > ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[4]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[4]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[4]][2]) + mCornerPositions[clock[4]][0]) &&
                    (mMap.getWireX(wire) > ((mCornerPositions[clock[2]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[2]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                        topY = findTopY(wire, clock[7], clock[2], clock[4]);
                } else if ((mMap.getWireX(wire) < ((mCornerPositions[clock[7]][0] - mCornerPositions[clock[6]][0]) / (mCornerPositions[clock[7]][2] - mCornerPositions[clock[6]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[6]][2]) + mCornerPositions[clock[6]][0]) &&
                    (mMap.getWireX(wire) < ((mCornerPositions[clock[4]][0] - mCornerPositions[clock[7]][0]) / (mCornerPositions[clock[4]][2] - mCornerPositions[clock[7]][2])) * (mMap.getWireZ(wire) - mCornerPositions[clock[7]][2]) + mCornerPositions[clock[7]][0])) {
                        topY = findTopY(wire, clock[7], clock[4], clock[6]);
                } else {
                    topY = findTopY(wire, clock[7], clock[2], clock[6]);
                } 
            }
        }
        if (bottomY != 0 && topY != 0) {
            drawSliver(wire, bottomY, topY);
        }
    }

    private boolean lessThanX(int wire, int clocka, int clockb, int clockc,
            int clockd) {
        float ax = mCornerPositions[clocka][0];
        float az = mCornerPositions[clocka][2];
        float bx = mCornerPositions[clockb][0];
        float bz = mCornerPositions[clockb][2];
        float cx = mCornerPositions[clockc][0];
        float cz = mCornerPositions[clockc][2];
        float dx = mCornerPositions[clockd][0];
        float dz = mCornerPositions[clockd][2];

        if (mMap.getWireX(wire) < ((bx - ax) / (bz - az))
                * (mMap.getWireZ(wire) - az) + ax) {
            if (mMap.getWireX(wire) < ((cx - bx) / (cz - bz))
                    * (mMap.getWireZ(wire) - bz) + bx) {
                if (mMap.getWireX(wire) < ((dx - cx) / (dz - cz))
                        * (mMap.getWireZ(wire) - cz) + cx) {
                    return true;
                }
            }
        }
        return false;
    }  

    private boolean greaterThanX(int wire, int clocka, int clockb, int clockc,
            int clockd) {
        float ax = mCornerPositions[clocka][0];
        float az = mCornerPositions[clocka][2];
        float bx = mCornerPositions[clockb][0];
        float bz = mCornerPositions[clockb][2];
        float cx = mCornerPositions[clockc][0];
        float cz = mCornerPositions[clockc][2];
        float dx = mCornerPositions[clockd][0];
        float dz = mCornerPositions[clockd][2];

        if (mMap.getWireX(wire) > ((bx - ax) / (bz - az))
                * (mMap.getWireZ(wire) - az) + ax) {
            if (mMap.getWireX(wire) > ((cx - bx) / (cz - bz))
                    * (mMap.getWireZ(wire) - bz) + bx) {
                if (mMap.getWireX(wire) > ((dx - cx) / (dz - cz))
                        * (mMap.getWireZ(wire) - cz) + cx) {
                    return true;
                }
            }
        }
        return false;
    }  

    private float findTopY(int wire, int point1, int point2, int point3) {
        float px = mCornerPositions[point1][0];
        float py = mCornerPositions[point1][1];
        float pz = mCornerPositions[point1][2];

        float qx = mCornerPositions[point2][0];
        float qy = mCornerPositions[point2][1];
        float qz = mCornerPositions[point2][2];

        float rx = mCornerPositions[point3][0];
        float ry = mCornerPositions[point3][1];
        float rz = mCornerPositions[point3][2];

        float a1 = (qx - px);
        float a2 = (qy - py);
        float a3 = (qz - pz);

        float b1 = (rx - px);
        float b2 = (ry - py);
        float b3 = (rz - pz);

        float i_var = (a2 * b3) - (b2 * a3);
        float j_var = -((a1 * b3) - (b1 * a3));
        float k_var = (a1 * b2) - (b1 * a2);
        float stand_const = ((i_var*(-px)) + (j_var*(-py)) + (k_var*(-pz)));

        return -(i_var *  mMap.getWireX(wire) + k_var * mMap.getWireZ(wire) + stand_const) / j_var;
    }

    private void drawSliver(int wire, float bottomY, float topY) {
        bottomY = bottomY * mMap.getDepth()
                / mMap.getWireZ(wire);
        topY = topY * mMap.getDepth()
                / mMap.getWireZ(wire);

        WiremapSliver sliver = new WiremapSliver(mMap, wire, (int)topY,
            mBaseColor, (int)(topY - bottomY),
            mBorderHeight, mBorderColor);
        sliver.display();
    }

    private void translateX(float step) {
        for (int i = 0; i < 8; i ++) {
            mCornerPositions[i][0] += step;
        }
        mX += step;
    }

    private void translateY(float step) {
        for (int i = 0; i < 8; i ++) {
            mCornerPositions[i][1] += step;
        }
        mY += step;
    }

    private void translateZ(float step) {
        for (int i = 0; i < 8; i ++) {
            mCornerPositions[i][2] += step;
        }
        mZ += step;
    }

    public void display() {
        pushMatrix();
        for(int i = 0; i < mMap.getWireCount(); i++) {
            drawWire(i);
        }
        popMatrix();
    }

    public void pitch(float rad) {
        for(int i = 0; i < 8; i++) {
            float z_dist = mCornerPositions[i][2] - mZ;
            float y_dist = mCornerPositions[i][1] - mY;    
            float hyp = sqrt(sq(z_dist) + sq(y_dist));
            float theta = atan2(z_dist, y_dist);
            theta = (theta + (rad));
            mCornerPositions[i][1] = (int)(mY + hyp * cos(theta));
            mCornerPositions[i][2] = (int)(mZ + hyp * sin(theta));
        }
    }

    public void yaw(float rad) {
        for(int i = 0; i < 8; i++) {
            float x_dist = mCornerPositions[i][0] - mX;
            float z_dist = mCornerPositions[i][2] - mZ;
            float hyp = sqrt(sq(x_dist) + sq(z_dist));
            float theta = atan2(x_dist, z_dist);
            theta = (theta + (rad));
            mCornerPositions[i][2] = (int)(mZ + hyp * cos(theta));
            mCornerPositions[i][0] = (int)(mX + hyp * sin(theta));
        }
    }

    public void roll(float rad) {
        for(int i = 0; i < 8; i++) {
            float x_dist = mCornerPositions[i][0] - mX;
            float y_dist = mCornerPositions[i][1] - mY;
            float hyp = sqrt(sq(x_dist) + sq(y_dist));
            float theta = atan2(y_dist, x_dist);
            theta = (theta + (rad));
            mCornerPositions[i][0] = (int)(mX + hyp * cos(theta));
            mCornerPositions[i][1] = (int)(mY + hyp * sin(theta));
        }
    }

    public void setSize(int width, int height, int depth) {
        mWidth = width;
        mHeight = height;
        mDepth = depth;
        initializeCorners();
    }

    public void setPosition(int x, int y, int z) {
        super.setPosition(x, y, z);
    }

    public void setBorderHeight(int height) {
        mBorderHeight = height;
    }

    public void setBorderColor(color borderColor) {
        mBorderColor = borderColor;
    }
}
