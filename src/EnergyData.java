/**
 * Created by DevWork on 5/25/17.
 */
public class EnergyData {
    public static enum Direction {
        LEFT(-1),
        UP(0),
        RIGHT(1);
        public final int delta;
        private Direction(int delta) {
            this.delta = delta;
        }
    }
    public EnergyData(double rawValue) {
        this.rawValue = rawValue;
    }
    public double rawValue;
    public double num;
    public Direction direction = null;

    @Override
    public String toString() {
        return Double.toString(rawValue);
    }
}
