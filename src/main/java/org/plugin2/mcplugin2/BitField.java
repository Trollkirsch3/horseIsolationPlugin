package org.plugin2.mcplugin2;

public class BitField {
    static final int[] DX = { 2, 2, -2, -2, 1, 1, -1, -1 };
    static final int[] DY = {-1, 1,  1, -1, 2,-2,  2, -2 };
    static long[] buildKnightMasks() {
        long[] masks = new long[64];

        for (int pos = 0; pos < 64; pos++) {
            int x = pos % 8;//rest 8 = 0 also geht es wieder zum anfang
            int y = pos / 8;//alle 8 x y +1

            long mask = 0L;

            for (int i = 0; i < 8; i++) {
                int nx = x + DX[i];
                int ny = y + DY[i];

                if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {//nicht ausserhalb der map
                    mask |= bit(bitLoc(nx, ny));//mask wird kombiniert
                }
            }

            masks[pos] = mask;
        }

        return masks;
    }
    static final long [] KNIGHT_MASKS = buildKnightMasks();
    static int bitLoc(int x, int y) {
        return y * 8 + x;
    }
    static long bit(int x, int y) {
        return 1L << bitLoc(x, y); //1 wird bitLoc mal verschoben
    }
    static long bit(int index) {
        return 1L << index;
    }
    static int [] getCoords(int pos){
        int [] coords = {pos %8, pos/8};
        return coords;
    }

    static long getPossibleMoves(long f, int playerPos){
        return KNIGHT_MASKS[playerPos] & ~ f; //und gate mit dem feld umgedreht: 1->0 0->1
    }
    //out array wird befüllt und die länge zurückgegeben
    public static int bitMovesToArray(long possibleMoves, int[] out) {
        int count = 0;
        long moves = possibleMoves;

        while (moves != 0) {
            long moveBit = moves & -moves;
            int pos = Long.numberOfTrailingZeros(moveBit);

            out[count++] = pos;

            moves &= moves - 1;
        }

        return count;
    }
    static boolean isFree(long field, int pos) {
        return (field & bit(pos)) == 0;
    }
}
